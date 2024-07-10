package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {

    private Button updateBtn,BackBtn;
    private EditText Name, Pass,ID,Email;
    private ProgressDialog progressDialog;
    String userid;
    FirebaseAuth fauth;
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
        Email=findViewById(R.id.email);
        Pass=findViewById(R.id.password);
        updateBtn = findViewById(R.id.UpdateProfilebtn);
        BackBtn =findViewById(R.id.BackButton);

        fauth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        BackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputdata();
            }
        });
    }

    private String Updatedname, Updatedpass;

    private void inputdata() {
        Updatedname = Name.getText().toString().trim();
       Updatedpass= Pass.getText().toString().trim();
        UpdateProfile();
    }

    private void UpdateProfile() {

                    userid = fauth.getCurrentUser().getUid();
                    DocumentReference documentReference = fstore.collection("user").document(userid);
                    HashMap<String, Object> user = new HashMap<>();
                    user.put("newname", "" + Updatedname);
                    user.put("newphone", "" + Updatedpass);

                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("TAG", "user profile updated for" + userid);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("TAG", "user profile  can't be updated for" + userid);
                        }
                    });

                    startActivity(new Intent(getApplicationContext(), Navigation_Activity.class));
                    finish();
                }
            }




