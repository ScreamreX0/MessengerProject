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
        ConversationsHelper.ConversationType conversationType;

        public Conversation(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public ConversationsHelper.ConversationType getConversationType() {
            return conversationType;
        }

        public void setConversationType(ConversationsHelper.ConversationType conversationType) {
            this.conversationType = conversationType;
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
        boolean isSelected = false;

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

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }

        public void setText(String text) {
            this.text = text;
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
    public static class Contact {
        String phoneNumber;
        String name;
        boolean isSelected = false;

        public Contact(String phoneNumber, String name) {
            this.phoneNumber = phoneNumber;
            this.name = name;
        }

        public Contact(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public String getName() {
            return name;
        }

        public boolean isSelected() {
            return this.isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }
    }
}
