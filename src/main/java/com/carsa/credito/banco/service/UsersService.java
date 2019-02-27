package com.carsa.credito.banco.service;


import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.carsa.credito.banco.domain.CarsaUserDetails;

/**
 * Created by esteban on 06/08/16.
 */
@Service
public class UsersService {

    public static final String AUTHORIZATION = "Authorization";
    public static final String STORE_CODE = "storeCode";
    public static final String CODE = "code";

    @Autowired
    private RestTemplate template;

    @Value("${sso.resources.userCarsaInfoUri}")
    private String userCarsaInfoUri;

    private static final Logger LOGGER = LoggerFactory.getLogger(UsersService.class);

    /**
     * This method is used for getting user details
     * @param user
     * @return user details
     */
    public Optional<CarsaUserDetails> getUserDetails(OAuth2Authentication user){
        LOGGER.info("Getting user details of the logged user");
        OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) user.getDetails();
        CarsaUserDetails userDetails = (CarsaUserDetails) details.getDecodedDetails();
        Optional<CarsaUserDetails> carsaUser = Optional.ofNullable(userDetails);
        LOGGER.info("User Details successfully gotten");
        return carsaUser;
    }

    /**
     * This method returns the user's seller code.
     * @param user
     * @return
     */
    public String getUserInitials(OAuth2Authentication user){
        LOGGER.info("Getting user's initials");
        Optional<CarsaUserDetails> userDetails = getUserDetails(user);
        String initials = userDetails.get().getInitials();
        LOGGER.info("User's initials successfully gotten");
        return initials;
    }

    /**
     * This method returns the store code of the seller
     * @param token
     * @return the store code of the seller
     */
    public Optional<String> getStore(String token) {
        LOGGER.info("calling services which brings the store code of the user");
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> result = template.exchange(userCarsaInfoUri, HttpMethod.GET, entity, Map.class);
        Optional<String> store = Optional.ofNullable(result.getBody().get(STORE_CODE).toString());
        LOGGER.info("Store code successfully gotten");
        return store;
    }
    
    /**
     * This method returns the store code of the seller
     * @param token
     * @return the store code of the seller
     */
    public Optional<String> getCode(String token) {
        LOGGER.info("calling services which brings the code of the user");
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTHORIZATION, token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> result = template.exchange(userCarsaInfoUri, HttpMethod.GET, entity, Map.class);
        Optional<String> legajo = Optional.ofNullable(result.getBody().get(CODE).toString());
        LOGGER.info("Store code successfully gotten");
        return legajo;
    }
}