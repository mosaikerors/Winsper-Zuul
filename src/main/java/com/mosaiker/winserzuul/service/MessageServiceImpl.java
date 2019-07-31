package com.mosaiker.winserzuul.service;

import com.mosaiker.winserzuul.entity.Message;
import com.mosaiker.winserzuul.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    private MessageRepository messageRepository;

    @Override
    public List<Message> findMessagesByReceiverUId(Long uId) {
        return messageRepository.findAllByReceiverUId(uId);
    }

    @Override
    public List<Message> findUnreadMessagesByReceiverUId(Long uId) {
        return messageRepository.findAllByReceiverUIdAndHasReadFalse(uId);
    }

    @Override
    public List<Message> findMessagesByReceiverUIdAndType(Long uId, int type) {
        return messageRepository.findAllByReceiverUIdAndType(uId, type);
    }

    @Override
    public List<Message> findUnreadMessagesByReceiverUIdAndType(Long uId, int type) {
        return messageRepository.findAllByReceiverUIdAndTypeAndHasReadFalse(uId, type);
    }

    @Override
    public void readMessagesByReceiverUIdAndType(Long uId, int type) {
        List<Message> messages = messageRepository.findAllByReceiverUIdAndTypeAndHasReadFalse(uId, type);
        for (Message message : messages) {
            message.setHasRead(true);
        }
        messageRepository.saveAll(messages);
    }

    @Override
    public void readMessageByMId(String mId) {
//        Message message = messageRepository.findByMId(mId);
        Message message = messageRepository.findById(mId).get();
        message.setHasRead(true);
        messageRepository.save(message);
    }

    @Override
    public void readMessagesByReceiverUId(Long uId) {
        List<Message> messages = messageRepository.findAllByReceiverUIdAndHasReadFalse(uId);
        for (Message message : messages) {
            message.setHasRead(true);
        }
        messageRepository.saveAll(messages);
    }

    @Override
    public void deleteMessagesByReceiverUIdAndType(Long uId, int type) {
        List<Message> messages = messageRepository.findAllByReceiverUIdAndType(uId, type);
        messageRepository.deleteAll(messages);
    }

    @Override
    public void deleteMessagesByReceiverUId(Long uId) {
        List<Message> messages = messageRepository.findAllByReceiverUId(uId);
        messageRepository.deleteAll(messages);
    }

    @Override
    public void deleteMessageByMId(String mId) {
//        Message message = messageRepository.findByMId(mId);
        Message message = messageRepository.findById(mId).get();
        messageRepository.delete(message);
    }

    @Override
    public void addNewMessage(Message myMessage) {
        messageRepository.save(myMessage);
    }
}
