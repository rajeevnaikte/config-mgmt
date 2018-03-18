package com.rajeevn.configmgmt.common;

import static com.rajeevn.configmgmt.common.AuthUtil.getUserName;

public abstract class Util
{
    public static String versionComment(String comment)
    {
        return "By " + getUserName() + ": " + comment;
    }
}
