package com.example.instagramclone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.instagramclone.models.Post;
import com.parse.ParseFile;

import org.parceler.Parcels;

public class DetailActivity extends AppCompatActivity {

    protected ImageView ivProfileImage, ivPost;
    TextView tvUsername, tvDescription, tvDate;
    ImageButton imgBtnSettings, imgBtnHeart, imgBtnComment, imgBtnSend, imgBtnSave;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Post post = Parcels.unwrap(getIntent().getParcelableExtra("post"));

        ivProfileImage = findViewById(R.id.ivProfileImage);
        ivPost = findViewById(R.id.ivPost);
        tvUsername = findViewById(R.id.tvUsername);
        tvDate = findViewById(R.id.tvDate);
        tvDescription = findViewById(R.id.tvDescription);
        imgBtnSettings = findViewById(R.id.imgBtnSettings);
        imgBtnHeart = findViewById(R.id.imgBtnHeart);
        imgBtnComment = findViewById(R.id.imgBtnComment);
        imgBtnSend = findViewById(R.id.imgBtnSend);
        imgBtnSave = findViewById(R.id.imgBtnSave);


        Glide.with(context).load(R.drawable.clay).transform(new RoundedCorners(100)).into(ivProfileImage);
        tvUsername.setText(post.getUser().getUsername());
        tvDescription.setText(post.getDescription());
        tvDate.setText(post.getCreatedAt().toString());

        ParseFile image = post.getImage();
        if(image != null){
            Glide.with(context).load(image.getUrl()).into(ivPost);
        }

    }


}