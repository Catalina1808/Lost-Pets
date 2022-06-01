package com.example.lostmypet.models;

public class Comment {
    private String userID;
    private String username;
    private String announcementID;
    private String commentID;
    private String message;
    private String date;

    public Comment(){}

    public Comment(String userID, String username, String announcementID, String message, String date) {
        this.userID = userID;
        this.username = username;
        this.announcementID = announcementID;
        this.message = message;
        this.date = date;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAnnouncementID() {
        return announcementID;
    }

    public void setAnnouncementID(String announcementID) {
        this.announcementID = announcementID;
    }

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
