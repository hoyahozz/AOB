package com.dongyang.android.aob.Introduction.Model;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("success")
    private boolean success;
    @SerializedName("id")
    private String id;
    @SerializedName("pw")
    private String pw;
    @SerializedName("image")
    private int image;
    @SerializedName("name")
    private String name;
    @SerializedName("number")
    private String number;
    @SerializedName("email")
    private String email;
    @SerializedName("sos")
    private String sos;

    public User(boolean success, String id, String pw, int image, String name, String number, String email, String sos) {
        this.success = success;
        this.id = id;
        this.pw = pw;
        this.image = image;
        this.name = name;
        this.number = number;
        this.email = email;
        this.sos = sos;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSos() {
        return sos;
    }

    public void setSos(String sos) {
        this.sos = sos;
    }
}
