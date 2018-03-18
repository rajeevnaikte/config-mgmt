package com.rajeevn.configmgmt.common;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

import java.security.Principal;
import java.util.List;

public abstract class AuthUtil
{
    public static String getRole()
    {
        return ((SimpleGrantedAuthority) (
                (List) SecurityContextHolder.getContext().getAuthentication().getAuthorities())
                .get(0)).getAuthority().split("_")[1].toLowerCase();
    }

    public static String getUserName()
    {
        return ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
    }
}
