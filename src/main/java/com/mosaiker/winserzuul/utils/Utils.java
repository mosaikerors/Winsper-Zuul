package com.mosaiker.winserzuul.utils;

import com.alibaba.fastjson.JSONObject;
import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
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

    public static String getUId(HttpServletRequest request) {
        String requestUId = "";
        String method = request.getMethod();
        if ("GET".equals(method) || "DELETE".equals(method)) {
            requestUId = request.getParameter("uId");

        } else if ("POST".equals(method) || "PUT".equals(method)) {
            try {
                //  获取请求的输入流
                InputStream in = request.getInputStream();
                String body = StreamUtils.copyToString(in, Charset.forName("UTF-8"));
                // 如果body为空初始化为空json
                if (StringUtils.isBlank(body)) {
                    body = "{}";
                }
                //  转化成json
                JSONObject json = JSONObject.parseObject(body);
                requestUId = json.getString("uId");
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        return requestUId;
    }
}
