package com.capsulestudio.schoolmanagement.Model;

/**
 * Created by Shuvo on 2/28/2018.
 */

public class FeeType {
    private int fee_id;
    private String fee_type;

    public FeeType() {
    }

    public FeeType(int fee_id, String fee_type) {
        this.fee_id = fee_id;
        this.fee_type = fee_type;
    }

    public int getFee_id() {
        return fee_id;
    }

    public void setFee_id(int fee_id) {
        this.fee_id = fee_id;
    }

    public String getFee_type() {
        return fee_type;
    }

    public void setFee_type(String fee_type) {
        this.fee_type = fee_type;
    }
}
