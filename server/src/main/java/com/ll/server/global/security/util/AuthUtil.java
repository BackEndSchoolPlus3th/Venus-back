package com.ll.server.global.security.util;

import com.ll.server.global.response.enums.ReturnCode;
import com.ll.server.global.response.exception.CustomException;
import com.ll.server.global.security.custom.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;

public class AuthUtil {

    public static String getCurrentMemberEmail(){
        return getUserDetails().getUsername();
    }

    public static String getCurrentMemberNickname(){
        return getUserDetails().getMember().getNickname();
    }

    public static Collection<? extends GrantedAuthority> getAuth(){
        return getUserDetails().getAuthorities();
    }

    public static Long getCurrentMemberId(){
        return getUserDetails().getMember().getId();
    }

    private static CustomUserDetails getUserDetails(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null) throw new CustomException(ReturnCode.INTERNAL_ERROR);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        if(userDetails == null) throw new CustomException(ReturnCode.INTERNAL_ERROR);

        return userDetails;
    }
}
