package com.date.maskeddates.models;

/**
 * Created by sonback123456 on 6/17/2018.
 */

public class Online {
    int idx = 1;
    String status = "";
    String time = "";
    String user = "";

    public Online(){

    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getIdx() {
        return idx;
    }

    public String getStatus() {
        return status;
    }

    public String getTime() {
        return time;
    }

    public String getUser() {
        return user;
    }
}
