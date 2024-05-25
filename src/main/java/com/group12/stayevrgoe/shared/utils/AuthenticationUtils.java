package com.group12.stayevrgoe.shared.utils;

import com.group12.stayevrgoe.user.entity.MyUserDetails;
import com.group12.stayevrgoe.user.entity.User;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author anhvn
 */
@UtilityClass
public class AuthenticationUtils {
    public static User getCurrentUser() {
        MyUserDetails myUserDetails = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return myUserDetails.getUser();
    }
}
