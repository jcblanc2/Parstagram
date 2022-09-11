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
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.instagramclone.activities.CommentActivity;
import com.example.instagramclone.activities.DetailActivity;
import com.example.instagramclone.activities.MainActivity;
import com.example.instagramclone.R;
import com.example.instagramclone.fragments.ProfileFragment;
import com.example.instagramclone.helpers.TimeFormatter;
import com.example.instagramclone.models.Post;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.json.JSONException;
import org.parceler.Parcels;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{

    public static final String TAG = "PostAdapter";
    public static Context context;
    List<Post> posts;
    public static List<String> listUserLike;

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
        try {
            holder.bind(post);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        TextView tvUsername, tvDescription, tvDate, tvLike;
        ImageButton imgBtnSettings, imgBtnHeart, imgBtnComment, imgBtnSend, imgBtnSave;
        RelativeLayout container1, containerProfileUsername;
        public static int like;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            ivPost = itemView.findViewById(R.id.ivPost);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvLike = itemView.findViewById(R.id.tvLike);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            imgBtnSettings = itemView.findViewById(R.id.imgBtnSettings);
            imgBtnHeart = itemView.findViewById(R.id.imgBtnHeart);
            imgBtnComment = itemView.findViewById(R.id.imgBtnComment);
            imgBtnSend = itemView.findViewById(R.id.imgBtnSend);
            imgBtnSave = itemView.findViewById(R.id.imgBtnSave);
            container1 = itemView.findViewById(R.id.container1);
            containerProfileUsername = itemView.findViewById(R.id.containerProfileUsername);
        }

        public void bind(Post post) throws JSONException {
            ParseUser currentUser = ParseUser.getCurrentUser();

            Glide.with(context).load(currentUser.getParseFile("image_profile").getUrl()).transform(new RoundedCorners(100)).into(ivProfileImage);
            tvUsername.setText(post.getUser().getUsername());
            tvDescription.setText(post.getDescription());
            tvDate.setText(TimeFormatter.getTimeStamp(post.getCreatedAt().toString()));
//            tvLike.setText(String.valueOf(post.getNumberLike()));

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

            containerProfileUsername.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.fragmentPlaceholder, new ProfileFragment()).commit();
                    MainActivity.bottom_navigation.setSelectedItemId(R.id.action_account);
                }
            });

            imgBtnComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, CommentActivity.class);
                    i.putExtra("post", Parcels.wrap(post));
                    context.startActivity(i);
                }
            });

//            imgBtnHeart.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    like = post.getNumberLike();
//
//                    try {
//                        listUserLike = Post.fromJsonArray(post.getListLike());
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    Log.i(TAG, currentUser.getObjectId());
//
//
//                    if (listUserLike.contains(currentUser.getObjectId())){
//                        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.cards_heart_outline);
//                        imgBtnHeart.setImageDrawable(drawable);
//
//                        --like;
//                    }else {
//                        ++like;
//
//                        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.red_heart);
//                        imgBtnHeart.setImageDrawable(drawable);
//                    }
//
//                    tvLike.setText(String.valueOf(like));
//                    saveLike(post, like, currentUser);
//                }
//            });
        }

        // method to save a like
//        private void saveLike(Post post, int like, ParseUser currentUser) {
//            post.setNumberLike(like);
//            post.setListLike(currentUser);
//
//            post.saveInBackground(new SaveCallback() {
//                @Override
//                public void done(ParseException e) {
//                    if (e != null){
//                        Log.e(TAG, "Error while saving", e);
//                        Toast.makeText(context, "Error while saving", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                    Toast.makeText(context, "Liked", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }


    }
}
