package ru.geekbrains.dropbox.modules.authorization.dao;

import org.springframework.security.core.GrantedAuthority;

public enum  UserRole implements GrantedAuthority {
    USER, ADMIN;


    @Override
    public String getAuthority() {
        return name();
    }
}
