package com.kevex.ffriend.Model;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author kevin
 * Chat is an object to represent a out going conversation of two people
 * Inside it there is a messages array where all messages that are sent are inside it
 * TODO: make finish the todos below
 */
public class Chat {

    ArrayList<Message> messages;
    Date startconversation;

    /**
     * empty constructor required for Firestore - dont delete
     */
    public Chat(){

    }

    public Chat(ArrayList<Message> messages, Date startconversation) {
        this.messages = messages;
        this.startconversation = startconversation;
    }

    // Setter

    /**
     * TODO: make sure to check later on if setting more message is allowed/exceed the max messages
     * @param messages
     */
    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    /**
     * TODO: make sure it is not empty
     * @param startconversation
     */
    public void setStartconversation(Date startconversation) {
        this.startconversation = startconversation;
    }

    // Getter

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public Date getStartconversation() {
        return startconversation;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "messages=" + messages +
                ", startconversation=" + startconversation +
                '}';
    }
}
