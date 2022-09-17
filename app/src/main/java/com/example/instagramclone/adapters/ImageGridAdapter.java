package com.example.instagramclone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.instagramclone.R;
import com.parse.ParseFile;
import java.util.List;

public class ImageGridAdapter extends BaseAdapter {

    public static final String TAG = "ImageGridAdapter";
    public static Context context;
    List<ParseFile> posts;
    LayoutInflater inflater;

    public ImageGridAdapter(Context context1, List<ParseFile> posts) {
        this.context = context1;
        this.posts = posts;
    }


    @Override
    public int getCount() {
        return posts.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (inflater == null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (view == null){
            view = inflater.inflate(R.layout.grid_item,null);
        }

        ImageView ivPostImage = view.findViewById(R.id.ivPostImage);
        ParseFile image = posts.get(i);
        Glide.with(context).load(image.getUrl()).into(ivPostImage);

        return view;
    }
}
