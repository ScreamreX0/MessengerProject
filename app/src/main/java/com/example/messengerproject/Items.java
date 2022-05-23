package com.example.messengerproject;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Items {
    public static class Conversation {
        String id;
        String name;
        ArrayList<DialogMember> members;
        ArrayList<Message> messages;

        public Conversation(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public Conversation(String id, String name, ArrayList<DialogMember> members, ArrayList<Message> messages) {
            this.id = id;
            this.name = name;
            this.members = members;
            this.messages = messages;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public ArrayList<DialogMember> getMembers() {
            return members;
        }

        public ArrayList<Message> getMessages() {
            return messages;
        }
    }
    public static class DialogMember {
        String phoneNumber;

        public DialogMember(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }
    }
    public static class Message {
        String name;
        String senderPhone;
        String messageTime;
        MessagesHelper.MessageType type;
        String text;
        URI image;

        public Message(String name,
                       String senderPhone,
                       String messageTime,
                       MessagesHelper.MessageType type,
                       String text) {
            this.name = name;
            this.senderPhone = senderPhone;
            this.messageTime = messageTime;
            this.type = type;
            this.text = text;
        }

        public Message(String name,
                       String senderPhone,
                       String messageTime,
                       MessagesHelper.MessageType type,
                       URI uri) {
            this.name = name;
            this.senderPhone = senderPhone;
            this.messageTime = messageTime;
            this.type = type;
            this.image = uri;
        }

        public String getText() {
            return text;
        }

        public URI getImage() {
            return image;
        }

        public String getName() {
            return name;
        }

        public String getSenderPhone() {
            return senderPhone;
        }

        public String getMessageTime() {
            return messageTime;
        }

        public MessagesHelper.MessageType getType() {
            return type;
        }
    }
}
