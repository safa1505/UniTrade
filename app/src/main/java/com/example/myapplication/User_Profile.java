package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class User_Profile extends AppCompatActivity {

    private TextView profilename,profileemail,profileID;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ImageView editIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


       profilename = findViewById(R.id.user_profile_name);
       profileemail = findViewById(R.id.user_profile_email);
       profileID=findViewById(R.id.user_profile_id);
       editIcon=findViewById(R.id.edit_profile_icon);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        String userId = mAuth.getCurrentUser().getUid();
        fetchUserData(userId);


        editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),EditProfile.class));
                finish();
            }
        });

    }

    private void fetchUserData(String userId) {
        DocumentReference docRef = db.collection("Users").document(userId);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String name = document.getString("Name");
                    String email = document.getString("Email");
                    String ID=document.getString("ID");

                    profilename.setText(name);
                    profileemail.setText(email);
                    profileID.setText(ID);

                } else {
                    Toast.makeText(User_Profile.this, "User data not found!", Toast.LENGTH_SHORT).show();
                        }
            } else {
                Toast.makeText(User_Profile.this, "Error fetching data!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}