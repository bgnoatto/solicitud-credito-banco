package com.carsa.credito.banco.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;
import org.springframework.security.oauth2.provider.error.OAuth2AuthenticationEntryPoint;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by esteban on 11/07/16.
 */

@EnableResourceServer
@Configuration
public class OAuth2ResourceServerConfig extends ResourceServerConfigurerAdapter {

  @Value("${sso.auth.checkTokenUri}")
  private String tokenEndpointUrl;

  private TokenExtractor tokenExtractor = new BearerTokenExtractor();

  @Override
  public void configure(HttpSecurity http) throws Exception {

    http.addFilterAfter(new OncePerRequestFilter() {
      @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response, FilterChain filterChain)
              throws ServletException, IOException {

      if (tokenExtractor.extract(request) == null) {
          List<GrantedAuthority> auths = new ArrayList<GrantedAuthority>();
          auths.add(new SimpleGrantedAuthority("anon"));
          SecurityContextHolder.getContext().setAuthentication(new AnonymousAuthenticationToken("anon", "anon", auths));
      }
      filterChain.doFilter(request, response);
    }
  }, AbstractPreAuthenticatedProcessingFilter.class);

    http.csrf().disable();
    http
            .authorizeRequests()
            .antMatchers("/**/creditobanco/**")
            .permitAll()
            .anyRequest()
            .authenticated();

  }

  @Bean
  public RemoteTokenServices remoteTokenServices() {
    CustomRemoteTokenServices remoteTokenServices = new CustomRemoteTokenServices();
    remoteTokenServices.setCheckTokenEndpointUrl(tokenEndpointUrl);
    remoteTokenServices.setClientId("web");
    remoteTokenServices.setClientSecret("web");
    return remoteTokenServices;

  }

  @Value("${sso.uri}")
  private String oauthHost;

  @Override
  public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
    OAuth2AuthenticationEntryPoint ep = new OAuth2AuthenticationEntryPoint();
    ep.setExceptionRenderer(new CustomDefaultOAuth2ExceptionRenderer(oauthHost));
    resources.authenticationEntryPoint(ep);
  }

}
