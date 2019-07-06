package com.mosaiker.winserzuul.filter;

import com.alibaba.fastjson.JSONObject;
import com.mosaiker.winserzuul.service.OAuthService;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Component
public class OAuthFilter extends ZuulFilter {

    @Autowired
    private OAuthService oAuthService;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    /*
    * 判断是否需要认证
    * 默认需要认证，把不需要认证的特殊情况写出来
    * */
    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String requestUrl = request.getRequestURL().toString();
        String pathUrl = requestUrl.substring(requestUrl.indexOf("7120")+4);
        ctx.set("pathUrl", pathUrl);
        if (pathUrl.startsWith("/user")) {
            String secondPath = pathUrl.substring(10);
            if (secondPath.startsWith("/sendCode")||secondPath.startsWith("/signup")||secondPath.startsWith("/login")) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String token = request.getHeader("Authorization");
        String  uId = request.getHeader("uId");
        String pathUrl = ctx.get("pathUrl").toString();
        JSONObject result = new JSONObject();
        if (token == null || uId == null) {
            ctx.setSendZuulResponse(false);//不需要进行路由，也就是不会调用api服务提供者
            ctx.setResponseStatusCode(401);
            ctx.set("isOK",false);//可以把一些值放到ctx中，便于后面的filter获取使用
            //返回内容给客户端
            result.put("message", "auth fail: no token or no uId");
            ctx.setResponseBody(result.toJSONString());// 返回错误内容
            return null;
        }
        JSONObject param = new JSONObject();
        param.put("token", token);
        param.put("uId", Long.parseLong(uId));
        //  对各路径的Auth身份规则进行定义
        List<String> roles = new ArrayList<>();
        if (pathUrl.startsWith("/user/user/updateInfo")) {
            param.put("roles", roles);  //roles为空，表示通配，所有身份都可以（包括被禁用）
        } else if (pathUrl.startsWith("/hean/hean")) {
            roles.add("USER");
            roles.add("SUPERUSER");
            param.put("roles", roles);
        } else if (pathUrl.startsWith("/admin/admin")) {
            roles.add("ADMIN");
            param.put("roles", roles);
        } else {
            //未知网址。。。
        }
        //  认证身份
        JSONObject oAuthResult = oAuthService.authenticate(param);
        if (oAuthResult.getString("message").equals("ok")) {
            //  认证成功
            ctx.setSendZuulResponse(true);//会进行路由，也就是会调用api服务提供者
            ctx.setResponseStatusCode(200);
            ctx.set("isOK", true);//可以把一些值放到ctx中，便于后面的filter获取使用
        } else {
            ctx.setSendZuulResponse(false);//不需要进行路由，也就是不会调用api服务提供者
            ctx.setResponseStatusCode(401);
            ctx.set("isOK", false);//可以把一些值放到ctx中，便于后面的filter获取使用
            ctx.setResponseBody(oAuthResult.toJSONString());
        }
        return null;
    }
}


