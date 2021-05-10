package com.date.maskeddates.models;

/**
 * Created by sonback123456 on 6/17/2018.
 */

public class Membership {
    int _idx = 0;
    float amount = 0.0f;
    int validity = 0;
    String product_id = "";

    public Membership(){

    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void set_idx(int _idx) {
        this._idx = _idx;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public void setValidity(int validity) {
        this.validity = validity;
    }

    public int get_idx() {
        return _idx;
    }

    public float getAmount() {
        return amount;
    }

    public int getValidity() {
        return validity;
    }
}
