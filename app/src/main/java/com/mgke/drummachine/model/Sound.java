package com.mgke.drummachine.model;

public class Sound {
    public String id;
    public String userID;
    public String soundName;
    public String soundUrl;

    public Sound() {
    }

    public Sound(String id, String soundUrl, String soundName, String userID) {
        this.id = id;
        this.soundUrl = soundUrl;
        this.soundName = soundName;
        this.userID = userID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSoundUrl() {
        return soundUrl;
    }

    public void setSoundUrl(String soundUrl) {
        this.soundUrl = soundUrl;
    }

    public String getSoundName() {
        return soundName;
    }

    public void setSoundName(String soundName) {
        this.soundName = soundName;
    }
}
