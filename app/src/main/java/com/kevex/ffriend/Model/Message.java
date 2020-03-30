package com.kevex.ffriend.Model;

import java.util.Date;

/**
 * @author kevin
 * Message represent one message that will be used in chats
 * TODO: make finish the todos below
 */
public class Message {
    String sender,message;
    Date sentmessage;

    /**
     * Firestore require empty cons
     * do no delete
     */
    public Message() {

    }

    public Message(String sender, String message, Date sentmessage) {
        this.sender = sender;
        this.message = message;
        this.sentmessage = sentmessage;
    }

    // Setter

    /**
     * TODO: check if sender is empty or null - this is not allowed and will need to throw
     * @param sender
     */
    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     * TODO: make sure not empty
     * @param sentmessage - the date that the message is sent
     */
    public void setSentmessage(Date sentmessage) {
        this.sentmessage = sentmessage;
    }

    /**
     *  TODO: check if message is empty or null and maximum chars is exceed - this is not allowed and will need to throw
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    // Getter

    public String getSender() {
        return sender;
    }

    public Date getSentmessage() {
        return sentmessage;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Message{" +
                "sender='" + sender + '\'' +
                ", message='" + message + '\'' +
                ", sentmessage=" + sentmessage +
                '}';
    }
}
