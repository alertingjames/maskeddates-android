package com.date.maskeddates.models;

/**
 * Created by sonback123456 on 6/4/2018.
 */

public class Questionnaire {
    int _idx = 0;
    String _text = "";
    String _answer = "";
    String _isactive = "";

    public Questionnaire(){

    }

    public void set_answer(String _answer) {
        this._answer = _answer;
    }

    public String get_answer() {
        return _answer;
    }

    public void set_text(String _text) {
        this._text = _text;
    }

    public void set_isactive(String _isactive) {
        this._isactive = _isactive;
    }

    public String get_isactive() {
        return _isactive;
    }

    public int get_idx() {
        return _idx;
    }

    public String get_text() {
        return _text;
    }

    public void set_idx(int _idx) {
        this._idx = _idx;
    }
}
