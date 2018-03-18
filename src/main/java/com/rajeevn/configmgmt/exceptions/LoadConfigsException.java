package com.rajeevn.configmgmt.exceptions;

import static java.text.MessageFormat.format;

public class LoadConfigsException extends RuntimeException
{
    private static final String message = "Unable to load configurations for environment {0}";

    public LoadConfigsException(String env)
    {
        super(format(message, env));
    }
}
