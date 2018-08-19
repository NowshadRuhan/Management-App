package com.capsulestudio.schoolmanagement.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Shuvo on 2/27/2018.
 */

public class Result {

    @SerializedName("error")
    private Boolean error;

    @SerializedName("message")
    private String message;

    @SerializedName("user")
    private UserLogin user;

    public Result(Boolean error, String message, UserLogin user) {
        this.error = error;
        this.message = message;
        this.user = user;
    }

    public Boolean getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public UserLogin getUser() {
        return user;
    }

}
