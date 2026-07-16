package com.project.chitti.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.project.chitti.entity.Users;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails extends User {

    private final Long id;

    public CustomUserDetails(Long id, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password != null ? password : "", authorities); 
        this.id = id;
    }

    public CustomUserDetails(Users user) {
        super(
            user.getEmail(),
            user.getPassword() != null ? user.getPassword() : "",
            List.of(new SimpleGrantedAuthority(user.getRole())) 
        );
        this.id = user.getId();
    }

    public Long getId() {
        return id;
    }
}