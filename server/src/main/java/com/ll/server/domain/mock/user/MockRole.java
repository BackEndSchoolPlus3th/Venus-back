package com.ll.server.domain.mock.user;

public enum MockRole {
    USER("user"),PUBLISHER("publisher"),ADMIN("admin");

    private final String role;

    private MockRole(String role){
        this.role=role;
    }

    public String getRole() {
        return role;
    }
}
