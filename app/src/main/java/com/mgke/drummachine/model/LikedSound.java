package com.mgke.drummachine.model;

public class LikedSound {
    private String id;
    private String userid;
    private String soundid;

    public LikedSound() {
    }

    public LikedSound(String id, String userid, String soundid) {
        this.id = id;
        this.userid = userid;
        this.soundid = soundid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getSoundid() {
        return soundid;
    }

    public void setSoundid(String soundid) {
        this.soundid = soundid;
    }
}


