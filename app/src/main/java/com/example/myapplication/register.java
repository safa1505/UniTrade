package com.example.myapplication;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class register extends AppCompatActivity {


    private EditText signinname, signinid, signinemail, signinpass;
    private Button registerBtn;
    private TextView loginBtn;
    private ProgressDialog progressDialog;
    FirebaseFirestore fstore;
    FirebaseUser fuser;
    String userid;
    FirebaseAuth fauth;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        signinname = findViewById(R.id.studentname);
        signinid = findViewById(R.id.studentid);
        signinemail = findViewById(R.id.email);
        signinpass = findViewById(R.id.password);
        registerBtn = findViewById(R.id.registerbtn);
        loginBtn = findViewById(R.id.createtext);
        progressDialog = new ProgressDialog(this);


        fauth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), login.class));
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAccount();
            }
        });
    }

    private void CreateAccount() {

        final String Student_Name = signinname.getText().toString().trim();
        final String ID = signinid.getText().toString().trim();
        final String Email = signinemail.getText().toString().trim();
        String Password = signinpass.getText().toString().trim();

        String emailpattern = "^(cse|bba|ce|eee)_[0-9]{10}@lus.ac.bd$";

        String passpattern="^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9]).{8,}$";


        if (Email.isEmpty()) {
            signinemail.setError("Email is required");
        } else if (!Email.matches(emailpattern)) {
            signinemail.setError("Invalid Email");
        } else if (Password.isEmpty()) {
            signinpass.setError("Password is required");
        } else if (!Password.matches(passpattern)) {
            signinpass.setError("Password must be >= 8 characters containing at least one" +
                    " lowercase letter," + " one uppercase letter, and one digit");
        } else {
            progressDialog.setTitle("Create Account");
            progressDialog.setMessage("Please wait for a moment");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            validatestudentID(Student_Name, ID, Email, Password);
        }
    }

    private void validatestudentID(String Student_Name, String ID, String Email, String Password) {
        fauth.createUserWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    fauth = FirebaseAuth.getInstance();
                    fuser = fauth.getCurrentUser();
                    if ((fuser != null)) {
                        fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getApplicationContext(), "Verification link has been sent to your email. Please verify your email...", Toast.LENGTH_SHORT).show();
                           startActivity(new Intent(getApplicationContext(), login.class));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "Couldn't send verification email", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    userid=fauth.getCurrentUser().getUid();
                    DocumentReference documentReference=fstore.collection("Users").document(userid);
                    HashMap<String, Object> userdataMap = new HashMap<>();
                    userdataMap.put("Name", Student_Name);
                    userdataMap.put("ID", ID);
                    userdataMap.put("Email", Email);
                    userdataMap.put("Password", Password);

                    documentReference.set(userdataMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            if(!fauth.getCurrentUser().isEmailVerified()){
                                Toast.makeText(getApplicationContext(), "Please verify your email", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Verification is successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), Navigation_Activity.class));
                                finish();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(register.this, "Failed to save user data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("register", "Error writing user data", task.getException());
                        }
                    });
                }
                else {
                    Exception e = task.getException();
                    if (e instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(register.this, "Email already in use", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.w("register", "createUserWithEmailAndPassword failed", e);
                    }
                }
            }
        });
    }
}