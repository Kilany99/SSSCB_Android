package com.example.ssscb_android;

import com.google.gson.annotations.SerializedName;
public class LoginResponse {


    @SerializedName("UserId")
    public String UserId;

    @SerializedName("Password")
    public String Password;
    @SerializedName("ResponseCode")
    public String ResponseCode;
    @SerializedName("ResponseMessage")
    public String ResponseMessage;

    public LoginResponse(String UserId, String Password) {
        this.UserId = UserId;
        this.Password = Password;
        this.ResponseCode = "";
        this.ResponseMessage = "" ;
    }

    public String getUserId() {
        return UserId;
    }




    public String getResponseCode() {
        return ResponseCode;
    }

    public String getResponseMessage() {
        return ResponseMessage;
    }
}
