package com.mosaiker.winserzuul.service;

import com.alibaba.fastjson.JSONObject;
import com.mosaiker.winserzuul.entity.User;
import com.mosaiker.winserzuul.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OAuthServiceImpl implements OAuthService {

    @Autowired
    private TokenService tokenService;

    /*
     * {"uId":10000,"token":"efwfsef.fefesf.efsefsef","roles":["USER","SUPERUSER"]}
     * roles可以为空，表示通配，只要有有效的token就能访问，被禁用的也可以：
     * {"uId":10000,"token":"efwfsef.fefesf.efsefsef","roles":[]}
     * */
    @Override
    public JSONObject authenticate(JSONObject request) {
        JSONObject result = new JSONObject();
        String token = request.getString("token");
        List<String> roleArray = request.getJSONArray("roles").toJavaList(String.class);
        if (token==null||!tokenService.verifyTokenRoleHave(token, request.getLong("uId"), roleArray)) {
            result.put("rescode", 2);  //抱歉，你没有这个权限
            return result;
        }
        result.put("rescode", 0);
        return result;
    }

}
