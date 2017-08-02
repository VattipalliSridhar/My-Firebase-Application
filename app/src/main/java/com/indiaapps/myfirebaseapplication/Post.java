package com.indiaapps.myfirebaseapplication;

/**
 * Created by Sridhar on 8/2/2017.
 */

public class Post
{
    private String title,desc,image;

    public Post()
    {

    }

    public Post(String title, String desc, String image) {
        this.title = title;
        this.desc = desc;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }


    public String getDesc() {
        return desc;
    }


    public String getImage() {
        return image;
    }


}
