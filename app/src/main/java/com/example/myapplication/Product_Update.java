package com.example.myapplication;


import static android.text.TextUtils.isEmpty;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Product_Update extends AppCompatActivity {
    private EditText productNameEditText, productDescriptionEditText, productPriceEditText;
    private ImageView productImageView;
    private Button updateProductButton,backbutton;
    private ProgressDialog progressDialog;
    private StorageReference storageReference;
    private DatabaseReference productRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_update);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        productRef=FirebaseDatabase.getInstance().getReference("Products");
        storageReference = FirebaseStorage.getInstance().getReference().child("Product Images");
        productNameEditText = findViewById(R.id.productnameupdate);
        productDescriptionEditText = findViewById(R.id.productdescriptionupdate);
        productPriceEditText = findViewById(R.id.productpriceupdate);
        updateProductButton = findViewById(R.id.updateProductbtn);
        productImageView = findViewById(R.id.cameraimageViewUpdate);
        backbutton = findViewById(R.id.backButton);
        progressDialog = new ProgressDialog(this);

        Intent intent=getIntent();
        if(intent!=null){
            productNameEditText.setText(intent.getStringExtra("Product Name"));
            productDescriptionEditText.setText(intent.getStringExtra("Product Description"));
            productPriceEditText.setText(intent.getStringExtra("Product price"));
            Glide.with(this).load(intent.getStringExtra("Product Image")).into(productImageView);

            String productId=intent.getStringExtra("Product ID");
            if(productId!=null) {
                productRef = FirebaseDatabase.getInstance().getReference("Products").child(productId);
            }
        }

            updateProductButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.show();
                   updateProduct();
                }
            });

            backbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

    private void updateProduct() {
            String updatedName = productNameEditText.getText().toString().trim();
            String updatedDescription = productDescriptionEditText.getText().toString().trim();
            String updatedPrice = productPriceEditText.getText().toString().trim();

            if (!updatedName.isEmpty() && !updatedDescription.isEmpty() && !updatedPrice.isEmpty())  {
                Map<String, Object> productData = new HashMap<>();
                productData.put("productName", updatedName);
                productData.put("productDescription", updatedDescription);
                productData.put("productPrice", updatedPrice);

                productRef.updateChildren(productData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Product Updated", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Product_Update.this, Navigation_Activity.class));
                    finish();
                }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Product Updated", Toast.LENGTH_SHORT).show();
            }
        });
                }
            }

    }




