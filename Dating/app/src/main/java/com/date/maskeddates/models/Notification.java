package com.date.maskeddates.models;

import com.firebase.client.Firebase;

/**
 * Created by LGH419 on 10/6/2018.
 */

public class Notification {
    int idx = 1;
    String message = "";
    int sender_id = 1;
    String sender_name = "";
    String sender_photo = "";
    String date_time = "";
    String key = "";
    Firebase firebase = null;

    public Notification(){

    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSender_id(int sender_id) {
        this.sender_id = sender_id;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public void setSender_photo(String sender_photo) {
        this.sender_photo = sender_photo;
    }

    public void setDate_time(String date_time) {
        this.date_time = date_time;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setFirebase(Firebase firebase) {
        this.firebase = firebase;
    }

    public int getIdx() {
        return idx;
    }

    public String getMessage() {
        return message;
    }

    public int getSender_id() {
        return sender_id;
    }

    public String getSender_name() {
        return sender_name;
    }

    public String getSender_photo() {
        return sender_photo;
    }

    public String getDate_time() {
        return date_time;
    }

    public String getKey() {
        return key;
    }

    public Firebase getFirebase() {
        return firebase;
    }
}
