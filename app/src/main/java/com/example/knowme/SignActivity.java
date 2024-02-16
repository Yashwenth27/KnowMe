package com.example.knowme;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignActivity extends AppCompatActivity {
    public String userName;
    String passWord;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        Button signbtn = findViewById(R.id.btnSign);
        signbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userName = ((EditText) findViewById(R.id.edtUser)).getText().toString();
                passWord = ((EditText) findViewById(R.id.edtPass)).getText().toString();
                authenticateAndNavigate(userName, passWord);
            }
        });
        TextView newbie = findViewById(R.id.textView);
        newbie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignActivity.this, RegisterActivity.class));
            }
        });
    }

    private void authenticateAndNavigate(final String userName, final String passWord) {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        mDatabase.child(userName).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    DataSnapshot dataSnapshot = task.getResult();
                    if (dataSnapshot.exists()) {
                        // User exists, check password
                        String storedPassword = dataSnapshot.child("password").getValue(String.class);
                        if (passWord.equals(storedPassword)) {
                            // Password matches, start DisplayActivity
                            Intent intent = new Intent(SignActivity.this, DisplayActivity.class);
                            intent.putExtra("userName", userName);
                            startActivity(intent);
                        } else {
                            Toast.makeText(SignActivity.this,"Incorrect Password",Toast.LENGTH_SHORT).show();
                            // Password doesn't match, show error or start RegisterActivity
                            startActivity(new Intent(SignActivity.this, SignActivity.class));
                        }
                    } else {
                        // User doesn't exist, show error or start RegisterActivity
                        Toast.makeText(SignActivity.this,"User Not existing",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignActivity.this, RegisterActivity.class));
                    }
                }
            }
        });
    }
}
