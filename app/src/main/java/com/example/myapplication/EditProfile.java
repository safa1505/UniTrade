package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;

public class EditProfile extends AppCompatActivity {

    private Button updateBtn, BackBtn;
    private EditText Name, Pass, ID, Email;
    private ProgressDialog progressDialog;

    String userid;
    FirebaseAuth fauth;
    FirebaseUser user;
    FirebaseFirestore fstore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        Name = findViewById(R.id.studentname);
        ID = findViewById(R.id.studentid);
        Email = findViewById(R.id.email);
        updateBtn = findViewById(R.id.UpdateProfilebtn);
        BackBtn = findViewById(R.id.BackButton);


        fauth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();
        user = fauth.getCurrentUser();
        userid = user.getUid();

        BackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   UpdateProfile();
            }
        });
    }

    private void UpdateProfile() {

        String uID=fauth.getCurrentUser().getUid();
        String updatedName=Name.getText().toString().trim();
        String updatedID=ID.getText().toString().trim();
        String updatedEmail =Email.getText().toString().trim();

        if(updatedName.isEmpty()|| updatedID.isEmpty()|| updatedEmail.isEmpty())
        {
            Toast.makeText(this,"Please fill all fields",Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating profile...");
        progressDialog.show();

        HashMap<String, Object> useredited = new HashMap<>();
        useredited.put("Name",updatedName);
        useredited.put("ID", updatedID);
        useredited.put("Email", updatedEmail);
        fstore.collection("Users").document(uID)
                .set(useredited, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toast.makeText(EditProfile.this, "Profile is Updated", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), User_Profile.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(EditProfile.this, "Couldn't edit profile", Toast.LENGTH_SHORT).show();
                            }
                            });

                fauth.getCurrentUser().updateEmail(updatedEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(EditProfile.this,"Successfully Updated Email",Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProfile.this,"Error in updating email",Toast.LENGTH_SHORT).show();
                    }
                });

            }

    }





