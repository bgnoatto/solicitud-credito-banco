package com.carsa.credito.banco.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.web.client.RestTemplate;

import com.carsa.credito.banco.domain.CarsaUserDetails;

/**
 * Created by esteban on 05/08/16.
 */
public class CustomRemoteTokenServices extends RemoteTokenServices {

    @Value("${sso.resources.userInfoUri}")
    private String userEndpoint;

    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public OAuth2Authentication loadAuthentication(String accessToken) throws AuthenticationException, InvalidTokenException {

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-Type", "application/json");
        HttpEntity<?> entity = new HttpEntity<Map<String, ?>>(headers);

        OAuth2Authentication auth = super.loadAuthentication(accessToken);

        ResponseEntity user = restTemplate.exchange(userEndpoint, HttpMethod.GET, entity, Object.class);
        HashMap<String , ?> body = (HashMap<String, ?>) user.getBody();
        HashMap<String, ?> principal = (HashMap<String, ?>) body.get("principal");

       if(principal.get("initials") != null) {
            CarsaUserDetails details = new CarsaUserDetails(body.get("name").toString(), principal.get("initials").toString(), (List<GrantedAuthority>) body.get("authorities"));
            auth.setDetails(details);
        }
        return auth;
    }

}