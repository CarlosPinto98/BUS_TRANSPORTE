package com.unimag.security.service;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import com.unimag.entities.User;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class UserDetailsImpl implements UserDetails {

    private final Long id;
    private final String name;
    private final String email;
    @JsonIgnore
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Long id, String name, String email, String password, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(
                "ROLE_USER"));
        return new UserDetailsImpl(user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPasswordHash(),
                authorities);
    }


    @Override
    public String getUsername() {
        return email;
    }




}
