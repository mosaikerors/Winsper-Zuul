package com.mosaiker.winserzuul.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

public class WebSocketInterceptor implements HandshakeInterceptor {
    @Value("${server.port}")
    String port;

    /**
     * handler处理前调用,attributes属性最终在WebSocketSession里,可能通过webSocketSession.getAttributes().get(key值)获得
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        System.out.println("beforeHandshake invoked");
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest serverHttpRequest = (ServletServerHttpRequest) request;
            // 获取请求路径携带的参数
            String senderUId = serverHttpRequest.getServletRequest().getParameter("senderUId");
            attributes.put("senderUId", senderUId);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        System.out.println("afterHandshake invoked");
    }
}