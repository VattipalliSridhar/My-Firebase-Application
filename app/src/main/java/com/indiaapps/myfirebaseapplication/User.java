package com.indiaapps.myfirebaseapplication;

/**
 * Created by Sridhar on 7/24/2017.
 */

public class User
{
    private String user_name;
    private String user_email;
    private long createdAt_time;
    private String user_pic_url;



    public User(String user_name, String user_email, long createdAt_time, String user_pic_url)
    {
        this.user_name = user_name;
        this.user_email = user_email;
        this.createdAt_time = createdAt_time;
        this.user_pic_url = user_pic_url;
    }

    public User()
    {
    }

    public String getUser_name() {
        return user_name;
    }

    public String getUser_email() {
        return user_email;
    }

    public long getCreatedAt_time() {
        return createdAt_time;
    }
    public String getUser_pic_url() {
        return user_pic_url;
    }
}
