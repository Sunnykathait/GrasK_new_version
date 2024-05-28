package com.example.graskupdated;

import java.util.ArrayList;

public class ConfessionAsPost {
    String userName , userStudentID, userConfession , showIdentity , userConfessionDocumentID , Date;
    ArrayList<String> comments;

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getUserConfessionDocumentID() {
        return userConfessionDocumentID;
    }

    public void setUserConfessionDocumentID(String userConfessionDocumentID) {
        this.userConfessionDocumentID = userConfessionDocumentID;
    }

    public ArrayList<String> getComments() {
        return comments;
    }

    public void setComments(ArrayList<String> comments) {
        this.comments = comments;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserStudentID() {
        return userStudentID;
    }

    public void setUserStudentID(String userStudentID) {
        this.userStudentID = userStudentID;
    }

    public String getUserConfession() {
        return userConfession;
    }

    public void setUserConfession(String userConfession) {
        this.userConfession = userConfession;
    }

    public String getShowIdentity() {
        return showIdentity;
    }

    public void setShowIdentity(String showIdentity) {
        this.showIdentity = showIdentity;
    }

    public ConfessionAsPost() {
    }

    @Override
    public String toString() {
        return "ConfessionAsPost{" +
                "userName='" + userName + '\'' +
                ", userStudentID='" + userStudentID + '\'' +
                ", userConfession='" + userConfession + '\'' +
                ", showIdentity='" + showIdentity + '\'' +
                ", comments=" + comments +
                '}';
    }

    public ConfessionAsPost(String userName, String userStudentID, String userConfession,
                            String showIdentity, ArrayList<String> commentList,String userConfessionDocumentID, String Date) {
        this.userName = userName;
        this.userStudentID = userStudentID;
        this.userConfession = userConfession;
        this.showIdentity = showIdentity;
        this.comments = commentList;
        this.userConfessionDocumentID = userConfessionDocumentID;
        this.Date = Date;
    }
}
