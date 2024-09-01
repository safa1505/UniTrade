package com.example.myapplication;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;



public class Product_Details extends AppCompatActivity {

     ImageView detailImage;
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


        Bundle bundle=getIntent().getExtras();
        if(bundle!=null)
        {
            detail_ProductName.setText(bundle.getString("Product Name"));
            detail_ProductDescription.setText(bundle.getString("Product Description"));
            detail_ProductPrice.setText(bundle.getString("Product Price"));
            Glide.with(this).load(bundle.getString("Image")).into(detailImage);
        }
    }
}

