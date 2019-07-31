package com.mosaiker.winserzuul.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
//import javax.persistence.GeneratedValue;


@Document
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

    public Message(int type, Long receiverUId, Long senderUId, String senderUsername, String hId, String text) {
        this.type = type;
        this.receiverUId = receiverUId;
        this.senderUId = senderUId;
        this.senderUsername = senderUsername;
        this.hId = hId;
        this.text = text;
        this.hasRead = false;
    }
}
