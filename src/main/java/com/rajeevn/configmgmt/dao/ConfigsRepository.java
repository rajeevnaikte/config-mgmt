package com.rajeevn.configmgmt.dao;

import com.rajeevn.configmgmt.exceptions.LoadConfigsException;
import com.rajeevn.configmgmt.exceptions.LoadEnvsException;
import com.rajeevn.configmgmt.exceptions.LoadRolesException;
import com.rajeevn.configmgmt.exceptions.SaveRoleException;
import com.rajeevn.configmgmt.model.Config;
import com.rajeevn.configmgmt.model.RolePermissions;

import java.util.Map;
import java.util.Set;

public interface ConfigsRepository
{
    void loadEnvs() throws LoadEnvsException;

    void loadEnv(String env) throws LoadEnvsException;

    void loadRoles() throws LoadRolesException;

    void loadConfigs(String env) throws LoadConfigsException;

    void saveRole(String role, RolePermissions permissions) throws SaveRoleException;

    Map<String, RolePermissions> getRoles();

    Set<String> getEnvs();

    Map<String, Config> getConfigs(String env);

    void saveConfigs(String env, Map<String, String> keyValues, String comment);

}
