package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
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
import com.google.firebase.auth.FirebaseAuth;

public class forgetpassword extends AppCompatActivity {

    private Button passeditBtn;
    private TextView PassEmail;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private  String Email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgetpassword);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        firebaseAuth=FirebaseAuth.getInstance();
        passeditBtn=findViewById(R.id.passeditbutton);
        PassEmail=findViewById(R.id.passEmaileditText);
        progressDialog=new ProgressDialog(this);

        passeditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Email=PassEmail.getText().toString();
                if(!Email.isEmpty())
                {
                    ResetPass();
                }
                else {
                    PassEmail.setError("Email field can't be empty");
                }
            }
        });
    }

    private void ResetPass() {
        progressDialog.show();
        firebaseAuth.sendPasswordResetEmail(Email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                progressDialog.dismiss();
                Toast.makeText(forgetpassword.this,"Reset Password Link has been sent to your email ",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(forgetpassword.this, login.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(forgetpassword.this,"Reset Password Link could not be sent to your email ",Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}