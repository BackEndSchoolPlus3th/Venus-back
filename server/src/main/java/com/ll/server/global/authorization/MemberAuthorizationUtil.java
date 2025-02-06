//package com.ll.server.global.authorization;
//
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//
//public class MemberAuthorizationUtil {
//
//    private MemberAuthorizationUtil() {
//        throw new AssertionError();
//    }
//    public static Long getLoginMemberId() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
//
//        return userDetails.getMemberId();
//    }
//}
