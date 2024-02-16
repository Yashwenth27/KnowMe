package com.example.knowme;

import android.net.Uri;

public class User {
    public String username;
    public String password;
    public String name,age,strengths;

    public User(String username, String password, String name, String age, String strengths, String photo) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.age = age;
        this.strengths = strengths;
        this.photo = photo;
    }

    public String photo;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
    public void setPhoto(String photo) {
        this.photo = photo;
    }

    // Getter for the photo URL
    public String getPhoto() {
        return photo;
    }
}
