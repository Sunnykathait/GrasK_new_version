package com.example.graskupdated;

public class Comments {
    String userName , userComment , userLikes;

    public Comments(String userName, String userComment, String userLikes) {
        this.userName = userName;
        this.userComment = userComment;
        this.userLikes = userLikes;
    }

    public Comments() {
    }

    public String getUserName() {
        return userName;
    }

    @Override
    public String toString() {
        return "Comments{" +
                "userName='" + userName + '\'' +
                ", userComment='" + userComment + '\'' +
                ", userLikes='" + userLikes + '\'' +
                '}';
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserComment() {
        return userComment;
    }

    public void setUserComment(String userComment) {
        this.userComment = userComment;
    }

    public String getUserLikes() {
        return userLikes;
    }

    public void setUserLikes(String userLikes) {
        this.userLikes = userLikes;
    }
}
