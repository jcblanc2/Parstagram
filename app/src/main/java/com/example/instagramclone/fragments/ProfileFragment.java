package com.example.instagramclone.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.instagramclone.activities.LoginActivity;
import com.example.instagramclone.R;
import com.example.instagramclone.adapters.ImageGridAdapter;
import com.example.instagramclone.models.Post;
import com.example.instagramclone.models.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment{

    public static final String TAG = "ProfileFragment";
    private Button btnLogout, btnEditProfile;
    private ImageView ivProfile;
    private TextView tvUsername;
    private RecyclerView rvImagePost;
    protected List<ParseFile> postList;
    protected ImageGridAdapter adapter;
    private SwipeRefreshLayout swipeContainer;
    private static ParseUser currentUser;
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    public static final int GALLERY_IMAGE_ACTIVITY_REQUEST_CODE = 3;
    private File photoFile;
    public String photoFileName = "photo.jpg";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        currentUser = ParseUser.getCurrentUser();

        btnLogout = view.findViewById(R.id.btnLogout);
        ivProfile = view.findViewById(R.id.ivProfile);
        tvUsername = view.findViewById(R.id.tvUsername);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        rvImagePost = view.findViewById(R.id.rvImagePost);
        swipeContainer = view.findViewById(R.id.swipeContainer);
        postList = new ArrayList<>();

        tvUsername.setText(currentUser.getUsername());
        Glide.with(getContext()).load(currentUser.getParseFile("image_profile").getUrl()).transform(new RoundedCorners(100)).into(ivProfile);

        //Create the adapter
        adapter = new ImageGridAdapter(getContext(), postList);

        //Set the adapter on the recyclerView
        rvImagePost.setAdapter(adapter);

        // First param is number of columns and second param is orientation i.e Vertical or Horizontal
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        // Attach the layout manager to the recycler view
        rvImagePost.setLayoutManager(gridLayoutManager);
        queryPost();

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

        // click to logout
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
            }
        });


        // click to add ad photo profile
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                open();
            }
        });
    }

    // query to get all post of the current user
    protected void queryPost() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_USER, currentUser);
        query.addDescendingOrder(Post.KEY_CREATED_KEY);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                List<ParseFile> listImages = new ArrayList<>();
                if (e != null){
                    Log.e(TAG, "Issue with getting Posts", e);
                    Toast.makeText(getContext(), "Issue with getting Posts", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (Post post:posts){
                    listImages.add(post.getImage());
                }
                // Remember to CLEAR OUT old items before appending in the new ones
                // ...the data has come back, add new items to your adapter...
                // Now we call setRefreshing(false) to signal refresh has finished
                adapter.clear();
                adapter.addAll(listImages);
                swipeContainer.setRefreshing(false);
            }
        });
    }


    // open the dialog (user can take photo or choose one from his gallery)
    public void open(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
        alertDialogBuilder.setMessage("Edit photo profile");

                alertDialogBuilder.setPositiveButton("Take Photo",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                launchCamera();
                            }
                        });

                alertDialogBuilder.setNegativeButton("Choose Photo",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {openGallery();}
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    // method to open the gallery
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);


            startActivityForResult(intent, GALLERY_IMAGE_ACTIVITY_REQUEST_CODE);

    }


    // method to launch the camera
    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    // method to save a post
    private void changePhoto(File photoFile) {
        User user = (User) currentUser;
        user.setProfileImage(new ParseFile(photoFile));

        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(getContext(), "Error while changing", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (photoFile == null || ivProfile.getDrawable() == null){
                    Toast.makeText(getContext(), "There is no image", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(getContext(), "Image change as successful!!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                changePhoto(photoFile);
            } else { // Result was a failure
                Toast.makeText(getContext(), "Error taking picture", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == GALLERY_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                changePhoto(photoFile);
            } else {
                Toast.makeText(getContext(), "Error taking picture", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
