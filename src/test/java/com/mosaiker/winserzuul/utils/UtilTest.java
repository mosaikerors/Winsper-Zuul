package com.mosaiker.winserzuul.utils;

import org.junit.Test;

import java.util.List;
import java.util.Map;


public class UtilTest {

    @Test
    public void parsePathAndRole() {
        String pathRoleString = "user/updateInfo:USER,SUPERUSER;user/login:;admin:ADMIN;";
        Map<String, List<String>> pathRoleMap = Utils.parsePathAndRole(pathRoleString);
        System.out.println(pathRoleMap);

    }
}