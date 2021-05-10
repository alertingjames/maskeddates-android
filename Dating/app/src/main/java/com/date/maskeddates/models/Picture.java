package com.date.maskeddates.models;

/**
 * Created by sonback123456 on 8/7/2018.
 */

public class Picture {
    int idx = 0;
    int member_id = 0;
    String picture_url = "";

    public Picture(){

    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public void setMember_id(int member_id) {
        this.member_id = member_id;
    }

    public void setPicture_url(String picture_url) {
        this.picture_url = picture_url;
    }

    public int getIdx() {
        return idx;
    }

    public int getMember_id() {
        return member_id;
    }

    public String getPicture_url() {
        return picture_url;
    }
}
