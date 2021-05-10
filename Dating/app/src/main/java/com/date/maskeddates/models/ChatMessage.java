package com.date.maskeddates.models;

/**
 * Created by LGH419 on 10/5/2018.
 */

public class ChatMessage {
    int idx = 1;
    String key = "";
    String text = "";
    String datetime = "";
    String read = "";

    public ChatMessage(){

    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public void setRead(String read) {
        this.read = read;
    }

    public int getIdx() {
        return idx;
    }

    public String getText() {
        return text;
    }

    public String getDatetime() {
        return datetime;
    }

    public String getRead() {
        return read;
    }
}
