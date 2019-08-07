package com.mosaiker.winserzuul.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
//import javax.persistence.GeneratedValue;


@Document(collection = "message")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
//    @GeneratedValue
    @Id
    private String mId;
    private int type;
    private Long receiverUId;
    private Long senderUId;
    private String senderUsername;
    private String hId;
    private String text;
    private boolean hasRead;

    public void setHasRead(boolean hasRead) {
        this.hasRead = hasRead;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Long getReceiverUId() {
        return receiverUId;
    }

    public void setReceiverUId(Long receiverUId) {
        this.receiverUId = receiverUId;
    }

    public Long getSenderUId() {
        return senderUId;
    }

    public void setSenderUId(Long senderUId) {
        this.senderUId = senderUId;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public String gethId() {
        return hId;
    }

    public void sethId(String hId) {
        this.hId = hId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
