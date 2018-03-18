package com.rajeevn.configmgmt.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Profile("dev")
@Configuration("ConfigMgmtSecurity")
public class WebSecurityConfig extends WebSecurityConfigurerAdapter
{
    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        http.headers().defaultsDisabled().cacheControl();
        http
                .authorizeRequests()
                .antMatchers("/*.js").permitAll()
                .antMatchers("/admin/**").hasRole("admin")
                .anyRequest().fullyAuthenticated()
                .and()
                .httpBasic();
        http.cors();
        http.csrf().disable();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception
    {
        auth
                .inMemoryAuthentication()
                .withUser("rajeevn").password("{noop}root").roles("admin")
                .and()
                .withUser("user").password("{noop}root").roles("user");
    }
}