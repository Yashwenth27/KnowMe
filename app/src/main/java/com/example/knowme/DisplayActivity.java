package com.example.knowme;

import static android.content.ContentValues.TAG;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class DisplayActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private TextView nametag, usernametag, agetag, strengthtag;
    private ImageView profileImageView;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    public String useruser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        // Initialize Firebase Storage
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        // Initialize TextViews and ImageView
        nametag = findViewById(R.id.nametag);
        usernametag = findViewById(R.id.usernametag);
        agetag = findViewById(R.id.agetag);
        strengthtag = findViewById(R.id.strengthtag);
        profileImageView = findViewById(R.id.profile);

        // Retrieve username from the intent
        String userName = getIntent().getStringExtra("userName");

        // Fetch user details from Firebase and update UI
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(userName);
        mDatabase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    DataSnapshot dataSnapshot = task.getResult();
                    if (dataSnapshot.exists()) {
                        // Update UI with user details
                        nametag.setText(dataSnapshot.child("name").getValue(String.class));
                        usernametag.setText("@" + userName);
                        useruser = userName;
                        agetag.setText("Age: " + dataSnapshot.child("age").getValue(String.class));
                        strengthtag.setText(dataSnapshot.child("strengths").getValue(String.class));

                        // Fetch the image URL from Firebase Storage
                        String profileImageUri = dataSnapshot.child("photo").getValue(String.class);
                        loadImageFromFirebaseStorage(profileImageUri);
                    }
                }
            }
        });
    }

    private void loadImageFromFirebaseStorage(String imageUrl) {
        if (imageUrl != null) {
            // Create a storage reference from the URL
            StorageReference imageRef = storageRef.child(imageUrl);
            //Toast.makeText(DisplayActivity.this, String.valueOf(imageRef), Toast.LENGTH_SHORT).show();
            Log.d(TAG,String.valueOf(imageRef));
            //gs://knowme-cc8dc.appspot.com/https%3A/firebasestorage.googleapis.com/v0/b/knowme-cc8dc.appspot.com/o/profile_images%252FDCS.jpg%3Falt%3Dmedia%26token%3Dc65a8bc3-cd92-4069-beb1-54cac8919a79
            String step1 = String.valueOf(imageRef).replace("gs://knowme-cc8dc.appspot.com/", "");
            String step2 = step1.replace("%2F", "/");
            String step3 = step2.replace("%3A", ":");
            String step4 = step3.replace("%26", "&");
            String convertedUrl = String.valueOf(imageRef).replace("gs://knowme-cc8dc.appspot.com/", "").replace("%3A", ":").replace("%252F", "/").replace("%3F", "?").replace("%3D", "=").replace("%26", "&");

            // Load the image into the ImageView using Picasso
            //Toast.makeText(DisplayActivity.this, String.valueOf(step4), Toast.LENGTH_SHORT).show();
            Log.d(TAG,String.valueOf("https://firebasestorage.googleapis.com/v0/b/knowme-cc8dc.appspot.com/o/profile_images/"+useruser+".jpg?alt=media&token=c65a8bc3-cd92-4069-beb1-54cac8919a79"));
            Picasso.get().load(String.valueOf("https://firebasestorage.googleapis.com/v0/b/knowme-cc8dc.appspot.com/o/profile_images%2F"+useruser+".jpg?alt=media&token=c65a8bc3-cd92-4069-beb1-54cac8919a79")).into(profileImageView);
            //Picasso.get().load(String.valueOf("https://firebasestorage.googleapis.com/v0/b/knowme-cc8dc.appspot.com/o/profile_images%2FDCS.jpg?alt=media&token=c65a8bc3-cd92-4069-beb1-54cac8919a79")).into(profileImageView);
        } else {
            // Handle the case when the image URL is null
            Toast.makeText(DisplayActivity.this, "No profile image found", Toast.LENGTH_SHORT).show();
        }
    }
}
