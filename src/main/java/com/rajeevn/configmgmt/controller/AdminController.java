package com.rajeevn.configmgmt.controller;

import com.rajeevn.configmgmt.dao.ConfigsRepository;
import com.rajeevn.configmgmt.model.RolePermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/admin")
public class AdminController
{
    @Autowired
    private ConfigsRepository configsRepository;

    @GetMapping("/load/envs")
    public boolean loadEnvs()
    {
        configsRepository.loadEnvs();
        return true;
    }

    @GetMapping("/load/env/{env}")
    public boolean loadEnv(@PathVariable String env)
    {
        configsRepository.loadEnv(env);
        return true;
    }

    @GetMapping("/load/roles")
    public boolean loadRoles()
    {
        configsRepository.loadRoles();
        return true;
    }

    @GetMapping("/load/configs/{env}")
    public boolean loadConfigs(@PathVariable String env)
    {
        configsRepository.loadConfigs(env);
        return true;
    }

    @PostMapping("/save/role/{role}")
    public boolean saveRole(@PathVariable String role, @RequestBody RolePermissions perm)
    {
        configsRepository.saveRole(role, perm);
        return true;
    }

    @GetMapping("/get/roles")
    public Map<String, RolePermissions> getRoles()
    {
        return configsRepository.getRoles();
    }

    @GetMapping("/get/all/environments")
    public Set<String> getAllEnvironments()
    {
        return configsRepository.getEnvs();
    }
}
