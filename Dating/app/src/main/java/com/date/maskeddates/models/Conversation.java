package com.date.maskeddates.models;

/**
 * Created by sonback123456 on 6/12/2018.
 */

public class Conversation {
    int idx = 1;
    int user_id = 1;
    int sender_id = 1;
    String sender_name = "";
    String sender_email = "";
    String sender_photo = "";
    String notitext = "";
    String notitime = "";
    String enteredtime = "";
    String exitedtime = "";
    int unraveled = 0;
    String read = "";
    String status = "";
    String option = "";
    int active = 0;

    public Conversation(){

    }

    public void setNotitime(String notitime) {
        this.notitime = notitime;
    }

    public void setEnteredtime(String enteredtime) {
        this.enteredtime = enteredtime;
    }

    public void setExitedtime(String exitedtime) {
        this.exitedtime = exitedtime;
    }

    public void setUnraveled(int unraveled) {
        this.unraveled = unraveled;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public String getNotitime() {
        return notitime;
    }

    public String getEnteredtime() {
        return enteredtime;
    }

    public String getExitedtime() {
        return exitedtime;
    }

    public int getUnraveled() {
        return unraveled;
    }

    public String getOption() {
        return option;
    }

    public int getActive() {
        return active;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public String getRead() {
        return read;
    }

    public void setRead(String read) {
        this.read = read;
    }

    public void setSender_id(int sender_id) {
        this.sender_id = sender_id;
    }

    public int getSender_id() {
        return sender_id;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setSender_name(String sender_name) {
        this.sender_name = sender_name;
    }

    public void setSender_email(String sender_email) {
        this.sender_email = sender_email;
    }

    public void setSender_photo(String sender_photo) {
        this.sender_photo = sender_photo;
    }

    public void setNotitext(String notitext) {
        this.notitext = notitext;
    }

    public int getIdx() {
        return idx;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getSender_name() {
        return sender_name;
    }

    public String getSender_email() {
        return sender_email;
    }

    public String getSender_photo() {
        return sender_photo;
    }

    public String getNotitext() {
        return notitext;
    }

}
