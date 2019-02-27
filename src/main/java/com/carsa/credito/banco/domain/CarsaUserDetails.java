package com.carsa.credito.banco.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * Created by esteban on 26/07/16.
 */
public class CarsaUserDetails implements UserDetails {

    private String username;
    private String initials;
    private Collection<? extends GrantedAuthority> grantedAuthorities;

    public CarsaUserDetails(){}

    public CarsaUserDetails(String username, String initials, Collection<? extends GrantedAuthority> authorities){
        this.username = username;
        this.initials = initials;
        this.grantedAuthorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getInitials(){
        return initials;
    }

}