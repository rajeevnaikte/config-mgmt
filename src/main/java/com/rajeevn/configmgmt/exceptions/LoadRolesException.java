package com.rajeevn.configmgmt.exceptions;

public class LoadRolesException extends RuntimeException
{
    private static final String message = "Unable to roles list and its permission details.";

    public LoadRolesException()
    {
        super(message);
    }
}
