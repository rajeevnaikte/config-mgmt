package com.rajeevn.configmgmt.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.requireNonNull;

public class RoleEnv
{
    private String role;
    private String env;
    private boolean readOnly = false;

    private Map<String, Config> configs = new HashMap<>();

    public RoleEnv(String role, String env, boolean readOnly)
    {
        this.role = requireNonNull(role);
        this.env = requireNonNull(env);
        this.readOnly = readOnly;
    }

    public String getRole()
    {
        return role;
    }

    public String getEnv()
    {
        return env;
    }

    public boolean isReadOnly()
    {
        return readOnly;
    }

    public Map<String, Config> getConfigs()
    {
        return configs;
    }

    @Override
    @JsonIgnore
    public int hashCode()
    {
        return role.hashCode() + env.hashCode();
    }

    @Override
    @JsonIgnore
    public boolean equals(Object obj)
    {
        RoleEnv roleEnv = (RoleEnv) obj;
        return (role.equals(roleEnv.getRole()) && env.equals(roleEnv.getEnv()));
    }
}
