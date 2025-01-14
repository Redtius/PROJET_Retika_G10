package com.ensat.retika.models;

public class Notification {
    private String notificationId;
    private String userId;
    private String senderId;
    private String type;
    private String relatedPostId;
    private long timestamp;

    public Notification() {}

    // Constructor
    public Notification(String notificationId, String userId, String senderId, String type, String relatedPostId, long timestamp) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.senderId = senderId;
        this.type = type;
        this.relatedPostId = relatedPostId;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRelatedPostId() {
        return relatedPostId;
    }

    public void setRelatedPostId(String relatedPostId) {
        this.relatedPostId = relatedPostId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
