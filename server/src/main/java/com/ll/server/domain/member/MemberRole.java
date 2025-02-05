package com.ll.server.domain.member;


public enum MemberRole {

    MEMBER("member"),PUBLISHER("publisher"),ADMIN("admin");
    private final String role;
    private MemberRole(String role){
        this.role=role;
    }
    public String getRole() {
        return role;
    }
}
