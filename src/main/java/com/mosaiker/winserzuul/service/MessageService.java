package com.mosaiker.winserzuul.service;

import com.mosaiker.winserzuul.entity.Message;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MessageService {
    List<Message> findMessagesByReceiverUId(Long uId);

    List<Message> findUnreadMessagesByReceiverUId(Long uId);

    List<Message> findMessagesByReceiverUIdAndType(Long uId, int type);

    List<Message> findUnreadMessagesByReceiverUIdAndType(Long uId, int type);

    void readMessagesByReceiverUIdAndType(Long uId, int type);

    void readMessageByMId(String mId);

    void readMessagesByReceiverUId(Long uId);

    void deleteMessagesByReceiverUIdAndType(Long uId, int type);

    void deleteMessagesByReceiverUId(Long uId);

    void deleteMessageByMId(String mId);

    void addNewMessage(Message message);
}
