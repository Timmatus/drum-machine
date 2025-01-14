package com.mgke.drummachine.model;

import com.google.firebase.Timestamp;

public class Comment {
    private String id;
    private String soundId;
    private String userId;
    private String content;
    private Timestamp timestamp;

    public Comment() {} // Для Firestore

    public Comment(String id, String soundId, String userId, String content, Timestamp timestamp) {
        this.id = id;
        this.soundId = soundId;
        this.userId = userId;
        this.content = content;
        this.timestamp = timestamp;
    }

    // Геттеры и сеттеры
    public String getSoundId() { return soundId; }
    public void setSoundId(String soundId) { this.soundId = soundId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
