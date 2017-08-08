package com.indiaapps.myfirebaseapplication;

import java.io.Serializable;

/**
 * Created by Sridhar on 2017/02/05.
 */

public class Comment {

    private String commentId;
    private long timeCreated;




    private String comment;
    private String username;
    private String userimage;

    public Comment() {
    }

    public Comment(String commentId, long timeCreated, String comment,String username, String userimage) {


        this.commentId = commentId;
        this.timeCreated = timeCreated;
        this.comment = comment;
        this.username = username;
        this.userimage = userimage;
    }



    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public long getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserimage() {
        return userimage;
    }

    public void setUserimage(String userimage) {
        this.userimage = userimage;
    }
}
