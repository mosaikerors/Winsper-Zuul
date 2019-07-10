package com.mosaiker.winserzuul.utils;

import java.util.*;

public class Utils {
    public static Map<String, List<String>> parsePathAndRole(String pathRoleString) {
        Map<String, List<String>> pathRoleMap = new HashMap<>();
        List<String> roles;
        int beginIndex = 0;
        int endIndex;
        while (beginIndex < pathRoleString.length()) {
            endIndex = pathRoleString.indexOf(':', beginIndex);
            String path = pathRoleString.substring(beginIndex, endIndex);
            beginIndex = endIndex + 1;
            endIndex = pathRoleString.indexOf(';', beginIndex);
            String rolesString = pathRoleString.substring(beginIndex, endIndex);
            roles = Arrays.asList(rolesString.split(","));
            pathRoleMap.put(path, roles);
            beginIndex = endIndex + 1;
        }
        return pathRoleMap;
    }

    public static String getFullSecret(String password, int status, String commonSecret) {
        if (status > 0) {
            return password + commonSecret;
        } else {
            return commonSecret + password;
        }
    }
}
