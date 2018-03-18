package com.rajeevn.configmgmt.dao;

import com.rajeevn.configmgmt.exceptions.LoadConfigsException;
import com.rajeevn.configmgmt.exceptions.LoadEnvsException;
import com.rajeevn.configmgmt.exceptions.LoadRolesException;
import com.rajeevn.configmgmt.exceptions.SaveConfigsException;
import com.rajeevn.configmgmt.exceptions.SaveRoleException;
import com.rajeevn.configmgmt.model.Config;
import com.rajeevn.configmgmt.model.RolePermissions;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Collections.unmodifiableMap;
import static java.util.Optional.ofNullable;

public abstract class AbstractConfigsRepository implements ConfigsRepository
{
    private Map<String, Map<String, Config>> envConfigs = new ConcurrentHashMap<>();
    private Map<String, RolePermissions> rolePermissions = new ConcurrentHashMap<>();
    private String ROLE_ADMIN = "admin";

    @PostConstruct
    protected void init() throws Exception
    {
        loadEnvs();
        loadRoles();
        if (!rolePermissions.containsKey(ROLE_ADMIN))
            saveRole(ROLE_ADMIN, new RolePermissions());
    }

    @PreDestroy
    protected abstract void releaseResources();

    @Override
    synchronized public void loadEnvs() throws LoadEnvsException
    {
        try
        {
            loadEnvsFromDataStore().forEach(this::loadEnv);
        } catch (Exception e)
        {
            e.printStackTrace();
            throw new LoadEnvsException();
        }
    }

    protected abstract Set<String> loadEnvsFromDataStore();

    @Override
    synchronized public void loadEnv(String env) throws LoadEnvsException
    {
        try
        {
            loadEnvFromDataStore(env);
            loadConfigs(env);
        } catch (Exception e)
        {
            e.printStackTrace();
            throw new LoadEnvsException();
        }
    }

    protected abstract void loadEnvFromDataStore(String env);

    @Override
    synchronized public void loadRoles() throws LoadRolesException
    {
        try
        {
            rolePermissions.clear();
            rolePermissions.putAll(loadRolesFromDataStore());
        } catch (Exception e)
        {
            e.printStackTrace();
            throw new LoadRolesException();
        }
    }

    protected abstract Map<String, RolePermissions> loadRolesFromDataStore();

    @Override
    synchronized public void saveRole(String role, RolePermissions permissions) throws SaveRoleException
    {
        try
        {
            saveRoleToDataStore(role, permissions);
            rolePermissions.put(role, permissions);
        } catch (Exception e)
        {
            e.printStackTrace();
            throw new SaveRoleException(role);
        }
    }

    protected abstract void saveRoleToDataStore(String role, RolePermissions permissions);

    @Override
    public Map<String, RolePermissions> getRoles()
    {
        return unmodifiableMap(rolePermissions);
    }

    @Override
    synchronized public void loadConfigs(String env) throws LoadConfigsException
    {
        try
        {
            envConfigs.put(env, loadConfigsFromDataStore(env));
        } catch (Exception e)
        {
            e.printStackTrace();
            throw new LoadConfigsException(env);
        }
    }

    protected abstract Map<String, Config> loadConfigsFromDataStore(String env);

    @Override
    public Set<String> getEnvs()
    {
        return envConfigs.keySet();
    }

    @Override
    public Map<String, Config> getConfigs(String env)
    {
        return envConfigs.get(env);
    }

    @Override
    synchronized public void saveConfigs(String env, Map<String, String> keyValues, String comment)
    {
        try
        {
            Map<String, Config> configs = envConfigs.get(env);
            Map<String, Map<String, String>> locKeyValMap = new HashMap<>();
            keyValues.forEach((k, v) ->
            {
                Config conf = configs.get(k);
                if (conf == null)
                    return;
                conf.getLocations().forEach(loc ->
                {
                    Map<String, String> keyValMap = ofNullable(locKeyValMap.get(loc)).orElseGet(HashMap::new);
                    String key = k;
                    if (conf.getAppended() != null) {
                        key = k.substring(conf.getAppended().length());
                    }
                    keyValMap.put(key, v);
                    locKeyValMap.put(loc, keyValMap);
                });
            });
            saveConfigsToDataStore(env, locKeyValMap, comment);
            keyValues.forEach((k, v) ->
            {
                Config conf = configs.get(k);
                if (conf == null)
                    return;
                conf.setValue(v);
            });
        } catch (Exception e)
        {
            e.printStackTrace();
            throw new SaveConfigsException(env, keyValues);
        }
    }

    protected abstract void saveConfigsToDataStore(String env, Map<String, Map<String, String>> locKeyValMap, String comment);
}




