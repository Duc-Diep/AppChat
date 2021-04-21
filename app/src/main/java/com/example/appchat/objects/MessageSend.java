package com.example.appchat.objects;

public class MessageSend {
    String idSender,idReceiver,content,isSeen;

    public MessageSend(String idSender, String idReceiver, String content, String isSeen) {
        this.idSender = idSender;
        this.idReceiver = idReceiver;
        this.content = content;
        this.isSeen = isSeen;
    }

    public MessageSend() {
    }

    public String getIdSender() {
        return idSender;
    }

    public void setIdSender(String idSender) {
        this.idSender = idSender;
    }

    public String getIdReceiver() {
        return idReceiver;
    }

    public void setIdReceiver(String idReceiver) {
        this.idReceiver = idReceiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIsSeen() {
        return isSeen;
    }

    public void setIsSeen(String isSeen) {
        this.isSeen = isSeen;
    }
}
