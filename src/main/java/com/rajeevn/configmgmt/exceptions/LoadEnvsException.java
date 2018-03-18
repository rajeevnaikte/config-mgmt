package com.rajeevn.configmgmt.exceptions;

public class LoadEnvsException extends RuntimeException
{
    private static final String message = "Unable to get environments list and data";

    public LoadEnvsException()
    {
        super(message);
    }
}
