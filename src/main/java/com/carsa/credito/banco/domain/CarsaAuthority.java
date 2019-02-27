package com.carsa.credito.banco.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/**
 * Created by esteban on 24/08/16.
 */
public class CarsaAuthority implements GrantedAuthority {

    private SimpleGrantedAuthority authority;
    private String ROLE_FORMAT = "ROLE_%s";

    public CarsaAuthority(){}

    public CarsaAuthority(String role){
        this.authority = new SimpleGrantedAuthority(role);
    }

    @Override
    public String getAuthority() {
        return String.format(ROLE_FORMAT, authority.getAuthority());
    }
}
