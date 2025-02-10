package com.ll.server.global.security.util;

import com.ll.server.global.security.custom.CustomUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthUtil {

    public static String getCurrentMemberEmail(){
        return getUserDetails()!=null?getUserDetails().getUsername():null;
    }

    public static String getCurrentMemberNickname(){
        return getUserDetails()!=null?getUserDetails().getMember().getNickname():null;
    }

    public static Long getCurrentMemberId(){
        return getUserDetails()!=null?getUserDetails().getMember().getId():null;
    }

    private static CustomUserDetails getUserDetails(){
        return (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
