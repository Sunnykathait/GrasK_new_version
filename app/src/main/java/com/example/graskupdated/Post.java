package com.example.graskupdated;

public class Post {
    private int profileImage;
    private String username;
    private String content;
    private String Date;

    public Post(int profileImage, String username, String content, String Date) {
        this.profileImage = profileImage;
        this.username = username;
        this.content = content;
        this.Date = Date;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public int getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(int profileImage) {
        this.profileImage = profileImage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
