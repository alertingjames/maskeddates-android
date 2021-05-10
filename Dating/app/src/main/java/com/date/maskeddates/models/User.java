package com.date.maskeddates.models;

/**
 * Created by sonback123456 on 6/3/2018.
 */

public class User {
    int _idx = 0;
    String _firstName = "";
    String _lastName = "";
    String _name="";
    String _email = "";
    String _password = "";
    String _fbPhoto = "";
    String _photoUrl = "";
    String _gender = "";
    String _birthday = "";
    String _age = "";
    String _phone = "";
    String _address = "";
    String _lat = "";
    String _lng = "";
    String _answers = "";
    String _answers2 = "";

    String _premium = "";
    String _photo_unlock = "";
    String _photos = "";
    String _selfie_approved = "";

    String _lastlogin = "";
    int _actives = 0;
    String _phone_imei = "";

    public User(){

    }

    public void set_phone_imei(String _phone_imei) {
        this._phone_imei = _phone_imei;
    }

    public String get_phone_imei() {
        return _phone_imei;
    }

    public void set_selfie_approved(String _selfie_approved) {
        this._selfie_approved = _selfie_approved;
    }

    public String get_selfie_approved() {
        return _selfie_approved;
    }

    public void set_lastlogin(String _lastlogin) {
        this._lastlogin = _lastlogin;
    }

    public void set_actives(int _actives) {
        this._actives = _actives;
    }

    public String get_lastlogin() {
        return _lastlogin;
    }

    public int get_actives() {
        return _actives;
    }

    public void set_photo_unlock(String _photo_unlock) {
        this._photo_unlock = _photo_unlock;
    }

    public String get_photo_unlock() {
        return _photo_unlock;
    }

    public void set_premium(String _premium) {
        this._premium = _premium;
    }

    public void set_photos(String _photos) {
        this._photos = _photos;
    }

    public String get_premium() {
        return _premium;
    }

    public String get_photos() {
        return _photos;
    }

    public void set_answers2(String _answers2) {
        this._answers2 = _answers2;
    }

    public String get_answers2() {
        return _answers2;
    }

    public void set_fbPhoto(String _fbPhoto) {
        this._fbPhoto = _fbPhoto;
    }

    public String get_fbPhoto() {
        return _fbPhoto;
    }

    public void set_answers(String _answers) {
        this._answers = _answers;
    }

    public String get_answers() {
        return _answers;
    }

    public String get_age() {
        return _age;
    }

    public void set_age(String _age) {
        this._age = _age;
    }

    public void set_address(String _address) {
        this._address = _address;
    }

    public void set_lat(String _lat) {
        this._lat = _lat;
    }

    public void set_lng(String _lng) {
        this._lng = _lng;
    }

    public String get_address() {
        return _address;
    }

    public String get_lat() {
        return _lat;
    }

    public String get_lng() {
        return _lng;
    }

    public void set_idx(int _idx) {
        this._idx = _idx;
    }

    public void set_firstName(String _firstName) {
        this._firstName = _firstName;
    }

    public void set_lastName(String _lastName) {
        this._lastName = _lastName;
    }

    public void set_name(String _name) {
        this._name = _name;
    }

    public void set_email(String _email) {
        this._email = _email;
    }

    public void set_password(String _password) {
        this._password = _password;
    }

    public void set_photoUrl(String _photoUrl) {
        this._photoUrl = _photoUrl;
    }

    public void set_gender(String _gender) {
        this._gender = _gender;
    }

    public void set_birthday(String _birthday) {
        this._birthday = _birthday;
    }

    public void set_phone(String _phone) {
        this._phone = _phone;
    }

    public int get_idx() {
        return _idx;
    }

    public String get_firstName() {
        return _firstName;
    }

    public String get_lastName() {
        return _lastName;
    }

    public String get_name() {
        return _name;
    }

    public String get_email() {
        return _email;
    }

    public String get_password() {
        return _password;
    }

    public String get_photoUrl() {
        return _photoUrl;
    }

    public String get_gender() {
        return _gender;
    }

    public String get_birthday() {
        return _birthday;
    }

    public String get_phone() {
        return _phone;
    }
}
