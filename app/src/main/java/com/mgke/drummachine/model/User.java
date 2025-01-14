package com.mgke.drummachine.model;

public class User {
    public String id;
    public String username;
    public String password;
    public String mail;
    public String avatarUrl;

    public User() {
    }

    public User(String id, String username, String password, String mail, String avatarUrl) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.mail = mail;
        this.avatarUrl = avatarUrl;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
