package com.mosaiker.winserzuul.filter;

import com.alibaba.fastjson.JSONObject;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

@Component
public class AvoidAttackFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    /*
     * /user/login,/user
     * */
    @Value("${avoidAttack}")
    String avoidAttack;

    /*
     * 判断是否需要认证
     * 默认需要认证，把不需要认证的特殊情况写出来
     * */
    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        System.out.println("isOK:" + ctx.getBoolean("isOK"));
        if (!ctx.getBoolean("isOK")) {
            return false;
        }
        String pathUrl = ctx.get("pathUrl").toString();
        List<String> avoidAttackPaths = Arrays.asList(avoidAttack.split(","));
        System.out.println("avoidAttackPaths:  " + avoidAttackPaths);
        for (String avoidAttackPath : avoidAttackPaths) {
            System.out.println("avoidAttackPath:  " + avoidAttackPath);
            if (pathUrl.startsWith(avoidAttackPath)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object run() {
        //  验证参数中的uId和header中的uId一致（如果有的话），防止狡猾的用户用自己的token改别人的信息
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
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
        JSONObject result = new JSONObject();
        if (!requestUId.equals(ctx.get("uId").toString())) {
            ctx.setSendZuulResponse(false);//不需要进行路由，也就是不会调用api服务提供者
            ctx.setResponseStatusCode(401);
            ctx.set("isOK", false);//可以把一些值放到ctx中，便于后面的filter获取使用
            //返回内容给客户端
            result.put("message", "Wenn du lange in einen Abgrund blickst,blickt der Abgrund auch dich hinein.");
            ctx.setResponseBody(result.toJSONString());// 返回错误内容
        }
        return null;
    }
}
