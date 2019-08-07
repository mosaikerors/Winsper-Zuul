package com.mosaiker.winserzuul.service;

import com.alibaba.fastjson.JSONObject;
import com.mosaiker.winserzuul.entity.Message;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketHandler extends AbstractWebSocketHandler {
    @Autowired
    private MessageService messageService;
    private static WebSocketHandler webSocketHandler;

    @PostConstruct
    public void init() {
        webSocketHandler =this;
        webSocketHandler.messageService = this.messageService;
    }

    /**
     *  存储sessionId和webSocketSession
     *  需要注意的是，webSocketSession没有提供无参构造，不能进行序列化，也就不能通过redis存储
     *  在分布式系统中，要想别的办法实现webSocketSession共享
     */
    private static Map<String, WebSocketSession> sessionMap = new ConcurrentHashMap<>();
    private static Map<String, String> userMap = new ConcurrentHashMap<>();

    /**
     * webSocket连接创建后调用
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        // 获取参数
        String uId = String.valueOf(session.getAttributes().get("senderUId"));
        userMap.put(uId, session.getId());
        sessionMap.put(session.getId(), session);
        System.out.println("userMap:" + userMap);
        System.out.println("sessionMap:" + sessionMap);
    }

    /**
     * 接收到消息会调用
     */
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        if (message instanceof TextMessage) {
            Map<String, Object> attributes = session.getAttributes();
            Long senderUId = Long.parseLong(attributes.get("senderUId").toString());
            JSONObject requestBody = JSONObject.parseObject(((TextMessage) message).getPayload());
            System.out.println(requestBody);
            Long receiverUId = requestBody.getLong("receiverUId");
            int type = requestBody.getIntValue("type");
            String senderUsername = requestBody.getString("senderUsername");
            String text = requestBody.getString("text");
            String hId = requestBody.getString("hId");
            Message myMessage = new Message();
            JSONObject result = new JSONObject();
            result.put("type", type);
            result.put("senderUId", senderUId);
            myMessage.setType(type);
            myMessage.setReceiverUId(receiverUId);
            myMessage.setSenderUId(senderUId);
            myMessage.setHasRead(false);
            switch (type) {
                case 1:
                    result.put("senderUsername", senderUsername);
                    myMessage.setSenderUsername(senderUsername);
                    break;
                case 2:
                case 3:
                    result.put("hId", hId);
                    result.put("senderUsername", senderUsername);
                    myMessage.setSenderUsername(senderUsername);
                    myMessage.sethId(hId);
                    break;
                case 4:
                    result.put("hId", hId);
                    result.put("senderUsername", senderUsername);
                    result.put("text", text);
                    myMessage.setSenderUsername(senderUsername);
                    myMessage.sethId(hId);
                    myMessage.setText(text);
                    break;
                case 5:
                    result.put("hId", hId);
                    myMessage.sethId(hId);
                    break;
                default:
                    break;
            }
            result.put("hasRead", false);
            sendMessage(receiverUId.toString(), result.toJSONString());
            // 存到数据库持久化
            System.out.println(myMessage.toString());
            System.out.println(result.toJSONString());
            webSocketHandler.messageService.addNewMessage(myMessage);
//        } else if (message instanceof BinaryMessage) {
//            System.out.println("binary");
//        } else if (message instanceof PongMessage) {
//            System.out.println("pong");
        } else {
            System.out.println("Unexpected WebSocket message type: " + message);
        }
    }

    /**
     * 连接出错会调用
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        sessionMap.remove(session.getId());
    }

    /**
     * 连接关闭会调用
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessionMap.remove(session.getId());
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 后端发送消息
     */
    public void sendMessage(String uId, String message) {
        try {
//            System.out.println("sendMessage Invoked");
            String sessionId = userMap.get(uId);
            WebSocketSession session = sessionMap.get(sessionId);
//            System.out.println(uId);
//            System.out.println(sessionId);
//            System.out.println(message);
            session.sendMessage(new TextMessage(message));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
