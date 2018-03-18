package com.rajeevn.configmgmt.exceptions;

import java.util.Map;

import static java.text.MessageFormat.format;

public class SaveConfigsException extends RuntimeException
{
    private static final String message = "Failed to add or update configs {1} - for environment {0}";

    public SaveConfigsException(String env, Map<String, String> keyValues)
    {
        super(format(message, env, keyValues));
    }
}
