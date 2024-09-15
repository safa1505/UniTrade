package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;



public class Product_Details extends AppCompatActivity {

     ImageView detailImage;
     Button backbutton;
     TextView detail_ProductName, detail_ProductDescription,detail_ProductPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

       detailImage= findViewById(R.id.detail_Image);
        detail_ProductName = findViewById(R.id.detail_ProductName);
        detail_ProductDescription = findViewById(R.id.detail_productDescription);
        detail_ProductPrice = findViewById(R.id.detail_productPrice);
        backbutton=findViewById(R.id.BackButton);

        Intent intent=getIntent();
        if(intent!=null){

            detail_ProductName.setText(intent.getStringExtra("Product Name"));
            detail_ProductDescription.setText(intent.getStringExtra("Product Description"));
            detail_ProductPrice.setText(intent.getStringExtra("Product Price"));
            Glide.with(this).load(intent.getStringExtra("Image")).into(detailImage);
        }

        Linkify.addLinks(detail_ProductDescription,Linkify.EMAIL_ADDRESSES);
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}

