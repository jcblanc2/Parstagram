package com.example.instagramclone.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.instagramclone.R;
import com.example.instagramclone.adapters.PostAdapter;
import com.example.instagramclone.helpers.EndlessRecyclerViewScrollListener;
import com.example.instagramclone.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import java.util.ArrayList;
import java.util.List;

public class PostFragment extends Fragment {

    public static final String TAG = "PostFragment";
    protected RecyclerView rvPost;
    protected List<Post> postList;
    protected PostAdapter adapter;
    private SwipeRefreshLayout swipeContainer;
    private Context context;
    private static ParseQuery<Post> query;
    private EndlessRecyclerViewScrollListener scrollListener;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_post, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvPost = view.findViewById(R.id.rvPost);
        swipeContainer = view.findViewById(R.id.swipeContainer);
        postList = new ArrayList<>();

        //Create the adapter
        adapter = new PostAdapter(getContext(), postList);

        //Set the adapter on the recyclerView
        rvPost.setAdapter(adapter);

        // Set The layout manager on the recyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        rvPost.setLayoutManager(layoutManager);

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "Fetching new data!! (setOnRefreshListener)");
                queryPost();
            }
        });


        scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Log.i(TAG, "Load more data "+page);
                loadMoreData();
            }
        };

//      Adds the scroll listener to RecyclerView
        rvPost.addOnScrollListener(scrollListener);

        queryPost();
    }

    private void loadMoreData() {
        query = ParseQuery.getQuery(Post.class);

        query.include(Post.KEY_USER);
        query.setLimit(20);

        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null){
                    Log.e(TAG, "Issue with getting Posts", e);
                    Toast.makeText(getContext(), "Issue with getting Posts", Toast.LENGTH_SHORT).show();
                    return;
                }

                adapter.addAll(posts);
            }
        });
    }

    protected void queryPost() {
        query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.setLimit(20);
        query.addDescendingOrder(Post.KEY_CREATED_KEY);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                if (e != null){
                    Log.e(TAG, "Issue with getting Posts", e);
                    Toast.makeText(getContext(), "Issue with getting Posts", Toast.LENGTH_SHORT).show();
                    return;
                }

                // CLEAR OUT old items before appending in the new ones
                adapter.clear();
                // ...the data has come back, add new items to your adapter...
                adapter.addAll(posts);
                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);
            }
        });
    }

}