package com.rajeevn.configmgmt.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

import static java.util.Optional.ofNullable;

public class Config
{
    @ApiModelProperty(required = true)
    private volatile Object value;
    private List<String> locations;
    private boolean isReadOnly = false;

    private String appended;

    public Object getValue()
    {
        return value;
    }

    public void setValue(Object value)
    {
        this.value = value;
    }

    public void addLocation(String location)
    {
        locations = ofNullable(locations).orElseGet(ArrayList::new);
        locations.add(location);
    }

    @JsonIgnore
    public List<String> getLocations()
    {
        return locations;
    }

    public boolean isReadOnly()
    {
        return isReadOnly;
    }

    public void setReadOnly(boolean readOnly)
    {
        isReadOnly = readOnly;
    }

    @JsonIgnore
    public String getAppended()
    {
        return appended;
    }

    public void setAppended(String appended)
    {
        this.appended = appended;
    }
}
