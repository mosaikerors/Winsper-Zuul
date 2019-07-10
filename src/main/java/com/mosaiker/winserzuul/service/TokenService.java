package com.mosaiker.winserzuul.service;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

public interface TokenService {

    JSONObject parseToken(String token, Long uId);

    /*
    * 你可以大胆放心地使用这两个函数来认证
    * 并且你可以相信这两个函数不仅验证role，
    * 还验证uId和token匹不匹配
    * */
    boolean verifyTokenRoleIs(String token, Long uId, String role);

    boolean verifyTokenRoleHave(String token, Long uId, List<String> roleArray);
}