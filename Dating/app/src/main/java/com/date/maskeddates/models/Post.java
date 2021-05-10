package com.date.maskeddates.models;

/**
 * Created by sonback123456 on 6/10/2018.
 */

public class Post {
    int _idx = 0;
    int _userID = 0;
    String _photo = "";
    String _text="";
    String _datetime="";

    public Post(){

    }

    public void set_datetime(String _datetime) {
        this._datetime = _datetime;
    }

    public String get_datetime() {
        return _datetime;
    }

    public void set_idx(int _idx) {
        this._idx = _idx;
    }

    public void set_userID(int _userID) {
        this._userID = _userID;
    }

    public void set_photo(String _photos) {
        this._photo = _photos;
    }

    public void set_text(String _texts) {
        this._text = _texts;
    }

    public int get_idx() {
        return _idx;
    }

    public int get_userID() {
        return _userID;
    }

    public String get_photo() {
        return _photo;
    }

    public String get_text() {
        return _text;
    }
}
