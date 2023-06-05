package com.project.segicr.Models;

import java.io.Serializable;

public class User implements Serializable {
    private String name, stdId, token, email, password, uid;

    public User(String name, String stdId, String email, String password, String uid) {
        this.name = name;
        this.stdId = stdId;
        this.email = email;
        this.password = password;
        this.uid = uid;
    }

    public User(String name, String stdId) {
        this.name = name;
        this.stdId = stdId;
    }

    public User(String name, String stdId, String token) {
        this.name = name;
        this.stdId = stdId;
        this.token = token;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStdId() {
        return stdId;
    }

    public void setStdId(String stdId) {
        this.stdId = stdId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
