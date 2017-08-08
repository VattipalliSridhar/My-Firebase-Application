package com.indiaapps.myfirebaseapplication;

/**
 * Created by Sridhar on 8/2/2017.
 */

public class Post
{
    private String title;
    private String desc;
    private String image;
    private String username;
    private String userimage;
    private long posttime;




    public Post()
    {

    }

    public Post(String title, String desc, String image, String username, String userimage, long posttime) {
        this.title = title;
        this.desc = desc;
        this.image = image;
        this.username = username;
        this.userimage = userimage;
        this.posttime = posttime;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public long getPosttime() {
        return posttime;
    }

    public void setPosttime(long posttime) {
        this.posttime = posttime;
    }
}
