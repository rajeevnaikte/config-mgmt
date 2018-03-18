package com.rajeevn.configmgmt.exceptions;

import static java.text.MessageFormat.format;

public class SaveRoleException extends RuntimeException
{
    private static final String message = "Failed to add or update role {0}";

    public SaveRoleException(String role)
    {
        super(format(message, role));
    }
}
