package com.tgb.gsvnbackend.lib;

import com.tgb.gsvnbackend.exc.NotFoundException;

import java.security.Principal;

public class Oath2UtilsConverter {
    public static String getUserId(Principal principal) {
        if (principal != null) {
            return principal.getName();
        }
        throw new NotFoundException("User not authenticated.");
    }
}
