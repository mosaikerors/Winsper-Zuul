package com.mosaiker.winserzuul.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class OAuthServiceHystric implements OAuthService {

    @Override
    public JSONObject authenticate(JSONObject request) {
        JSONObject result = new JSONObject();
        result.put("message", /*"在认证时服务器发生故障，请稍后重试"*/"fail when authentication");
        return result;
    }

}
