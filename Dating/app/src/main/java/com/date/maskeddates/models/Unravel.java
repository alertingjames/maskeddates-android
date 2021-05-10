package com.date.maskeddates.models;

/**
 * Created by sonback123456 on 6/13/2018.
 */

public class Unravel {
    int idx = 1;
    int me_id = 1;
    int user_id = 1;
    String unraveled = "";
    String entered_datetime = "";
    String exited_datetime = "";
    String active = "";

    public Unravel(){

    }

    public void setEntered_datetime(String entered_datetime) {
        this.entered_datetime = entered_datetime;
    }

    public void setExited_datetime(String exited_datetime) {
        this.exited_datetime = exited_datetime;
    }

    public String getEntered_datetime() {
        return entered_datetime;
    }

    public String getExited_datetime() {
        return exited_datetime;
    }

    public void setUnraveled(String unraveled) {
        this.unraveled = unraveled;
    }

    public String getUnraveled() {
        return unraveled;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public void setMe_id(int me_id) {
        this.me_id = me_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public int getIdx() {
        return idx;
    }

    public int getMe_id() {
        return me_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getActive() {
        return active;
    }
}
