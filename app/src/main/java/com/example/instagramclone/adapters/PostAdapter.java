package com.example.instagramclone.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.instagramclone.DetailActivity;
import com.example.instagramclone.R;
import com.example.instagramclone.models.Post;
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{

    public static final String TAG = "PostAdapter";
    public static Context context;
    List<Post> posts;

    public PostAdapter(Context context1, List<Post> posts) {
        this.context = context1;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = posts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    // Method to clean all elements of the recycler
    public void clear(){
        posts.clear();
        notifyDataSetChanged();
    }

    // Method to add a list of Posts -- change to type used
    public void addAll(List<Post> postList){
        posts.addAll(postList);
        notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        protected ImageView ivProfileImage, ivPost;
        TextView tvUsername, tvDescription, tvDate;
        ImageButton imgBtnSettings, imgBtnHeart, imgBtnComment, imgBtnSend, imgBtnSave;
        RelativeLayout container1;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            ivPost = itemView.findViewById(R.id.ivPost);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            imgBtnSettings = itemView.findViewById(R.id.imgBtnSettings);
            imgBtnHeart = itemView.findViewById(R.id.imgBtnHeart);
            imgBtnComment = itemView.findViewById(R.id.imgBtnComment);
            imgBtnSend = itemView.findViewById(R.id.imgBtnSend);
            imgBtnSave = itemView.findViewById(R.id.imgBtnSave);
            container1 = itemView.findViewById(R.id.container1);

        }

        public void bind(Post post){
            Glide.with(context).load(R.drawable.clay).transform(new RoundedCorners(100)).into(ivProfileImage);
            tvUsername.setText(post.getUser().getUsername());
            tvDescription.setText(post.getDescription());
            tvDate.setText(post.getCreatedAt().toString());

            ParseFile image = post.getImage();
            if(image != null){
                Glide.with(context).load(image.getUrl()).into(ivPost);
            }


            container1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, DetailActivity.class);
                    i.putExtra("post", Parcels.wrap(post));
                    context.startActivity(i);
                }
            });
        }


    }
}
