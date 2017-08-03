package com.indiaapps.myfirebaseapplication;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by 2136 on 8/3/2017.
 */

public class PostListAdapter extends ArrayAdapter<Post>
{
    private Activity context;
    private int resource;
    private List<Post> postValuesList;
    public PostListAdapter(WelcomeActivity welcomeActivity, int post_row, List<Post> postList)
    {
        super(welcomeActivity, post_row, postList);
        this.context = welcomeActivity;
        this.resource = post_row;
        postValuesList = postList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View v = inflater.inflate(resource, null);
        TextView post_title=(TextView)v.findViewById(R.id.post_rev_title);
        TextView post_desc=(TextView)v.findViewById(R.id.post_rev_desc);
        ImageView imageView=(ImageView)v.findViewById(R.id.post_rev_img);
        imageView.getLayoutParams().height=(WelcomeActivity.screenWidth*50)/100;
        imageView.getLayoutParams().width=(WelcomeActivity.screenWidth*50)/100;


        post_title.setText(postValuesList.get(position).getTitle());
        post_desc.setText(postValuesList.get(position).getDesc());
        Glide.with(context).load(postValuesList.get(position).getImage()).into(imageView);

        return v;
    }
}
