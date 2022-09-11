package com.example.instagramclone.models;

import android.annotation.SuppressLint;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

@Parcel(analyze = User.class)
@ParseClassName("_User")
public class User extends ParseUser {
    public static final String KEY_PROFILE_IMAGE = "image_profile";

    public User() {}

    public ParseFile getProfileImage() {return getParseFile(KEY_PROFILE_IMAGE);}
    public void setProfileImage(ParseFile parseFile) {put(KEY_PROFILE_IMAGE, parseFile);}

}