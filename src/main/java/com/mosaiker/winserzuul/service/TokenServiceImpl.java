package com.mosaiker.winserzuul.service;

import com.alibaba.fastjson.JSONObject;
import com.mosaiker.winserzuul.entity.User;
import com.mosaiker.winserzuul.repository.UserRepository;
import com.mosaiker.winserzuul.utils.Utils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TokenServiceImpl implements TokenService {

    @Autowired
    private UserRepository userRepository;

    static final String COMMON_SECRET = "MosA1kER5738h";            //JWT密码
    static final String TOKEN_PREFIX = "Bearer ";        //Token前缀

    @Override
    public JSONObject parseToken(String token, Long uId) {
        JSONObject result = new JSONObject();
        User user = userRepository.findUserByUId(uId);
        if (user == null) {
            result.put("message", "用户id不存在");
            return result;
        }
        String secret = Utils.getFullSecret(user.getPassword(), user.getStatus(), COMMON_SECRET);
        // 解析 Token
        try {
            Claims claims = Jwts.parser()
                    // 验签
                    .setSigningKey(secret)
                    // 去掉 Bearer
                    .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                    .getBody();
            String role = claims.get("authorities").toString();
            result.put("uId", uId);
            result.put("role", role);
            result.put("message", "ok");
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            result.put("message", "token已过期");
        } catch (io.jsonwebtoken.SignatureException e) {
            result.put("message", "token无效");
        }
        return result;
    }

    @Override
    public boolean verifyTokenRoleIs(String token, Long uId, String role) {
        // 解析 Token
        JSONObject userInfo = parseToken(token, uId);
        if (!userInfo.getString("message").equals("ok")) {
            //token已过期
            return false;
        }
        // 要求的身份和 token 中含有的身份信息匹配，返回 true
        return role.equals(userInfo.get("role"));
    }

    @Override
    public boolean verifyTokenRoleHave(String token, Long uId, List<String> roleArray) {
        // 解析 Token
        JSONObject userInfo = parseToken(token, uId);
        if (!userInfo.getString("message").equals("ok")) {
            //token已过期
            System.out.println("token expire");
            return false;
        }
        // 要求的身份和 token 中含有的身份信息匹配，返回 true
        if (roleArray.size() <= 0) {
            return true;
        }
        for (String role : roleArray) {
            if (role.equals(userInfo.get("role"))) {
                return true;
            }
        }
        return false;
    }
}