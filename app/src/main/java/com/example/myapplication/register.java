package com.example.myapplication;

import static android.os.Build.ID;
import static com.example.myapplication.R.id.password;
import static com.example.myapplication.R.id.registerbtn;
import static com.example.myapplication.R.id.studentid;
import static com.example.myapplication.R.id.studentname;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ktx.Firebase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class register extends AppCompatActivity {


    private EditText signinname, signinid, signinemail, signinpass;
    private Button registerBtn;
    private TextView loginBtn;
    private ProgressDialog progressDialog;

    FirebaseFirestore fstore;
    FirebaseUser fuser;
    String userid;
    FirebaseAuth fauth;
    FirebaseDatabase firebaseDatabase;


    @Override
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

        if(fauth.getCurrentUser()!=null)
        {
            Intent intent=new Intent(register.this, Navigation_Activity.class);
            startActivity(intent);
            finish();
        }

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

        String emailpattern = "^[a-z]{3}_[0-9]{10}@lus\\.ac\\.bd$";
        //String emailpattern="^[a-zA-Z0-9. _%+-]+@[a-zA-Z0-9. -]+\\\\. [a-zA-Z]{2,}$";

        if (Email.isEmpty()) {
            signinemail.setError("Email is required");
        } else if (!Email.matches(emailpattern)) {
            signinemail.setError("Invalid Email");
        } else if (Password.isEmpty()) {
            signinpass.setError("Password is required");
        } else if (Password.length() < 6) {
            signinpass.setError("Password must be >= 6 characters");
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
                          Toast.makeText(getApplicationContext(), "User account is created", Toast.LENGTH_SHORT).show();
                          if(fauth.getCurrentUser().isEmailVerified()){
                              Toast.makeText(getApplicationContext(), "Verification is successful", Toast.LENGTH_SHORT).show();
                              startActivity(new Intent(getApplicationContext(), Navigation_Activity.class));
                          }
                          else {
                              Toast.makeText(getApplicationContext(), "Please verify your email", Toast.LENGTH_SHORT).show();
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