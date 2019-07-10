package com.mosaiker.winserzuul.filter;

import com.alibaba.fastjson.JSONObject;
import com.mosaiker.winserzuul.service.OAuthService;
import com.mosaiker.winserzuul.utils.Utils;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
    * /user/login,/user
    * */
    @Value("${noAuth}")
    String noAuth;

    /*
    * 判断是否需要认证
    * 默认需要认证，把不需要认证的特殊情况写出来
    * */
    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String requestUrl = request.getRequestURL().toString();
        String pathUrl = requestUrl.substring(requestUrl.indexOf(port)+port.length());
        ctx.set("pathUrl", pathUrl);
        System.out.println("pathUrl:  " + pathUrl);
        List<String> noAuthPaths = Arrays.asList(noAuth.split(","));
        System.out.println("noAuthPaths:  " + noAuthPaths);
        for (String noAuthPath : noAuthPaths) {
            System.out.println("noAuthPath:  " + noAuthPath);
            if (pathUrl.startsWith(noAuthPath)) {
                return false;
            }
        }
        System.out.println("true");
        return true;
    }

    /*
     * e.g. /user/updateInfo:USER,SUPERUSER;/user/login:;/admin:ADMIN;
     * */
    @Value("${pathRole}")
    String pathRoleString;

    @Value("${server.port}")
    String port;

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String token = request.getHeader("Authorization");
        String uId = request.getHeader("uId");
        String pathUrl = ctx.get("pathUrl").toString();
        JSONObject result = new JSONObject();
        //  验证header信息是否不全
        if (token == null || uId == null) {
            ctx.setSendZuulResponse(false);//不需要进行路由，也就是不会调用api服务提供者
            ctx.setResponseStatusCode(401);
            ctx.set("isOK",false);//可以把一些值放到ctx中，便于后面的filter获取使用
            //返回内容给客户端
            result.put("message", "auth fail: no token or no uId");
            ctx.setResponseBody(result.toJSONString());// 返回错误内容
            return null;
        }
        ctx.set("uId", uId);
        JSONObject param = new JSONObject();
        param.put("token", token);
        param.put("uId", Long.parseLong(uId));
        //  对各路径的Auth身份规则进行定义
        List<String> roles = new ArrayList<>();
        Map<String, List<String>> pathRoleMap = Utils.parsePathAndRole(pathRoleString);
        //roles为空，表示通配，所有身份都可以（包括被禁用）
        for (String path : pathRoleMap.keySet()) {
            if (pathUrl.startsWith(path)) {
                roles = pathRoleMap.get(path);
                break;
            }
        }
        param.put("roles", roles);

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



