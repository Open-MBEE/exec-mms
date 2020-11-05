package org.openmbee.mms.core.utils;

import org.springframework.security.core.Authentication;

import java.util.HashSet;
import java.util.Set;

public class AuthenticationUtils {

    public static Set<String> getGroups(Authentication auth) {
        Set<String> res = new HashSet<>();
        auth.getAuthorities().forEach(ga ->res.add(ga.getAuthority()));
        return res;
    }

    public static boolean hasGroup(Authentication auth, String group){
        if(group == null)
            return false;

        return auth.getAuthorities().stream().anyMatch(v -> group.equals(v.getAuthority()));
    }
}
