package com.mosaiker.winserzuul.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.RequestBody;


public interface OAuthService {
  JSONObject authenticate(@RequestBody JSONObject request);
}
