package com.luv2code.springboot.restaurant.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;
import java.util.stream.Collectors;

public class KeycloakRealmRoleConverter implements Converter<Map<String, Object>, Collection<GrantedAuthority>> {
    @Override
    public Collection<GrantedAuthority> convert(Map<String, Object> realmAccess) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        if (realmAccess != null) {
            List<String> roles = (List<String>) realmAccess.get("roles");
            for (String role : roles) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase())); // Ensure to prefix with "ROLE_"
            }
            System.out.println("Assigned Roles: " + roles);
        }
        return authorities;
    }
}

