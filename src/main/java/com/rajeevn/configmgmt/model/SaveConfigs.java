package com.rajeevn.configmgmt.model;

import io.swagger.annotations.ApiModelProperty;

import java.util.Map;

public class SaveConfigs
{
    @ApiModelProperty(required = true)
    private String comment = "";
    @ApiModelProperty(required = true)
    private Map<String, String> keyVals;

    public String getComment()
    {
        return comment;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }

    public Map<String, String> getKeyVals()
    {
        return keyVals;
    }

    public void setKeyVals(Map<String, String> keyVals)
    {
        this.keyVals = keyVals;
    }
}
