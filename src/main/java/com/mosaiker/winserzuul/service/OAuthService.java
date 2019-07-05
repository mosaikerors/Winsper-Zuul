package com.mosaiker.winserzuul.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Primary  // 因为引入 fallback 类，出现两个同类 Bean，所以不加 @Primary 会导致 @Autowired 有红色波浪线（尽管不影响运行）
@FeignClient(value = "user-service", path = "/user", fallback = OAuthServiceHystric.class)
public interface OAuthService {
  @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
  JSONObject authenticate(@RequestBody JSONObject request);
}
