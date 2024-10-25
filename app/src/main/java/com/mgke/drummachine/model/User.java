package com.mgke.drummachine.model;

public class User {
    public String id;
    public String username;
    public String password;
    public String mail;

    public User() {
    }

    public User(String id, String username, String password, String mail) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.mail = mail;
    }
}
