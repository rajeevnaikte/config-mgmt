package com.rajeevn.configmgmt.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rajeevn.common.util.CollectionsUtil.toUnmodifiableOrEmptyIfNull;
import static java.util.Collections.EMPTY_LIST;

public class RolePermissions
{
    List<String> readOnlyKeysPrefix = EMPTY_LIST;
    List<String> writeKeysPrefix = EMPTY_LIST;
    List<String> notPermittedKeysPrefix = EMPTY_LIST;
    List<String> readOnlyEnvs = EMPTY_LIST;
    List<String> writeEnvs = EMPTY_LIST;

    public RolePermissions()
    {
    }

    public void setReadOnlyKeysPrefix(List<String> readOnlyKeysPrefix)
    {
        this.readOnlyKeysPrefix = toUnmodifiableOrEmptyIfNull(readOnlyKeysPrefix);
    }

    public void setWriteKeysPrefix(List<String> writeKeysPrefix)
    {
        this.writeKeysPrefix = toUnmodifiableOrEmptyIfNull(writeKeysPrefix);
    }

    public void setNotPermittedKeysPrefix(List<String> notPermittedKeysPrefix)
    {
        this.notPermittedKeysPrefix = toUnmodifiableOrEmptyIfNull(notPermittedKeysPrefix);
    }

    public void setReadOnlyEnvs(List<String> readOnlyEnvs)
    {
        this.readOnlyEnvs = toUnmodifiableOrEmptyIfNull(readOnlyEnvs);
    }

    public void setWriteEnvs(List<String> writeEnvs)
    {
        this.writeEnvs = toUnmodifiableOrEmptyIfNull(writeEnvs);
    }

    public List<String> getReadOnlyKeysPrefix()
    {
        return readOnlyKeysPrefix;
    }

    public List<String> getWriteKeysPrefix()
    {
        return writeKeysPrefix;
    }

    public List<String> getNotPermittedKeysPrefix()
    {
        return notPermittedKeysPrefix;
    }

    public List<String> getReadOnlyEnvs()
    {
        return readOnlyEnvs;
    }

    public List<String> getWriteEnvs()
    {
        return writeEnvs;
    }

    @JsonIgnore
    public boolean isReadOnlyEnv(String env)
    {
        return readOnlyEnvs.contains(env) || (readOnlyEnvs.isEmpty() && !writeEnvs.contains(env));
    }

    @JsonIgnore
    public boolean isNotPermittedKey(String key)
    {
        if (notPermittedKeysPrefix.isEmpty())
            return false;
        return notPermittedKeysPrefix.stream()
                .anyMatch(s -> key.startsWith(s));
    }

    @JsonIgnore
    public boolean isReadOnlyKey(String key)
    {
        if (readOnlyKeysPrefix.isEmpty() && writeKeysPrefix.isEmpty())
            return true;
        if (writeKeysPrefix.isEmpty())
            return readOnlyKeysPrefix.stream().anyMatch(s -> key.startsWith(s));
        return writeKeysPrefix.stream().noneMatch(s -> key.startsWith(s));
    }
}
