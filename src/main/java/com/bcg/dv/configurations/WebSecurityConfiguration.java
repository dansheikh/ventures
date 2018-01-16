package com.bcg.dv.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Value("${security.saml2.metadata-url}")
  private String metadataUrl;

  @Value("${server.port}")
  private String port;

  @Value("${server.ssl.key-alias}")
  private String keyAlias;

  @Value("${server.ssl.key-store}")
  private String keyStorePath;

  @Value("${server.ssl.key-store-password}")
  private String keyStorePassword;

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.authorizeRequests().antMatchers("/saml*").permitAll().anyRequest().authenticated();
  }
}
