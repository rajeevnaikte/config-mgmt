package com.rajeevn.configmgmt.controller;

import com.rajeevn.configmgmt.dao.ConfigsRepository;
import com.rajeevn.configmgmt.model.Config;
import com.rajeevn.configmgmt.model.RoleEnv;
import com.rajeevn.configmgmt.model.RolePermissions;
import com.rajeevn.configmgmt.model.SaveConfigs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.rajeevn.configmgmt.common.AuthUtil.getRole;

@RestController
@RequestMapping("/rest")
public class ConfigMgmtController
{
    @Autowired
    private ConfigsRepository configsRepository;

    @GetMapping("/get/environments")
    public Set<String> getEnvironments()
    {
        RolePermissions perm = configsRepository.getRoles().get(getRole());
        Set<String> envs = configsRepository.getEnvs();
        if (perm.getReadOnlyEnvs().isEmpty())
            return envs;

        return Stream.concat(perm.getReadOnlyEnvs().stream(), perm.getWriteEnvs().stream())
                .filter(env -> envs.contains(env))
                .collect(Collectors.toSet());
    }

    @GetMapping("/get/configurations/{env}")
    public RoleEnv getConfigurations(@PathVariable String env)
    {
        String role = getRole();
        RolePermissions perm = configsRepository.getRoles().get(role);
        RoleEnv roleEnv = new RoleEnv(role, env, perm.isReadOnlyEnv(env));
        configsRepository.getConfigs(env)
                .forEach((key, config) ->
                {
                    if (perm.isNotPermittedKey(key))
                        return;
                    Config conf = new Config();
                    conf.setValue(config.getValue());
                    conf.setReadOnly(roleEnv.isReadOnly() || perm.isReadOnlyKey(key));
                    roleEnv.getConfigs().put(key, conf);
                });
        return roleEnv;
    }

    @PostMapping("/save/configurations/{env}")
    public boolean saveConfigurations(@PathVariable String env, @RequestBody SaveConfigs saveConfigs)
    {
        String role = getRole();
        RolePermissions perm = configsRepository.getRoles().get(role);
        if (perm.isReadOnlyEnv(env))
            throw new RuntimeException("Environment is read only for you!");
        if (saveConfigs.getKeyVals().keySet().stream()
                .anyMatch(key -> perm.isNotPermittedKey(key) || perm.isReadOnlyKey(key)))
            throw new RuntimeException("These configurations are read only for you!");
        configsRepository.saveConfigs(env, saveConfigs.getKeyVals(), saveConfigs.getComment());
        return true;
    }

    @GetMapping("/get/user/role")
    public String getUserRole()
    {
        return getRole();
    }
}
