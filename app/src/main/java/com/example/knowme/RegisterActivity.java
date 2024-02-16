package com.example.knowme;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class RegisterActivity extends AppCompatActivity {

    private EditText edtUser, edtPass, edtName, edtAge, edtStrengths;
    private Button btnUploader, btnRegister;
    private Uri selectedImageUri;
    private StorageReference storageReference;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize your views
        edtUser = findViewById(R.id.edtUser);
        edtPass = findViewById(R.id.edtPass);
        edtName = findViewById(R.id.edtName);
        edtAge = findViewById(R.id.edtAge);
        edtStrengths = findViewById(R.id.edtStrengths);
        btnUploader = findViewById(R.id.btnUploader);
        btnRegister = findViewById(R.id.btnRegister);

        // Initialize Firebase Storage
        storageReference = FirebaseStorage.getInstance().getReference().child("profile_images");

        // Set OnClickListener for the Upload Photo button
        btnUploader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        // Set OnClickListener for the Register button
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });
    }

    // Method to open the gallery and select an image
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
    }

    // Handle the result from opening the gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            // Load the selected image into the ImageView
        }
    }

    // Method to handle user registration
    private void registerUser() {
        // Get user input
        String username = edtUser.getText().toString().trim();
        String password = edtPass.getText().toString().trim();
        String name = edtName.getText().toString().trim();
        String age = edtAge.getText().toString().trim();
        String strengths = edtStrengths.getText().toString().trim();

        // Check if any field is empty
        if (username.isEmpty() || password.isEmpty() || name.isEmpty() || age.isEmpty() || strengths.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a User object with the entered information
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        User newUser = new User(username, password, name, age, strengths, selectedImageUri.toString());

        // Check if an image is selected
        if (selectedImageUri != null) {
            // Create a StorageReference with a unique name (in this case, the username)
            StorageReference imageRef = storageReference.child(username + ".jpg");

            // Upload the image to Firebase Storage
            imageRef.putFile(selectedImageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Image uploaded successfully, now get the download URL
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            // Save the image download URL to Firebase Database
                            newUser.setPhoto(uri.toString());
                            mDatabase.child(username).setValue(newUser);
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Handle errors during image upload
                        Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                    });
        } else {
            // If no image is selected, save the user without an image URL
            mDatabase.child(username).setValue(newUser);
        }

        // Display a toast message indicating successful registration
        Toast.makeText(this, "User registered successfully", Toast.LENGTH_SHORT).show();

        // Navigate back to SignActivity (replace SignActivity with your actual sign-in activity)
        Intent intent = new Intent(RegisterActivity.this, SignActivity.class);
        startActivity(intent);
        finish();
    }
}
