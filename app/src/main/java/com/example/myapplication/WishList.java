package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WishList extends AppCompatActivity {
    private RecyclerView wishlistRecyclerView;
    private WishlistAdapter wishlistAdapter;
    private List<Product> wishlist;
    private DatabaseReference wishlistRef,childRef,productRef;
    private Product product;
    String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_wish_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        wishlistRecyclerView = findViewById(R.id.WishlistrecyclerView);
        wishlistRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        wishlistRecyclerView.setLayoutManager(linearLayoutManager);

        wishlist = new ArrayList<>();
        wishlistAdapter = new WishlistAdapter(this, wishlist);
        wishlistRecyclerView.setAdapter(wishlistAdapter);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            currentUserID = currentUser.getUid();
            wishlistRef = FirebaseDatabase.getInstance().getReference("Wishlist").child(currentUserID);

            loadWishlistOrderedByName();
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadWishlistOrderedByName() {

        wishlistRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                wishlist.clear();
                for (DataSnapshot productSnapshot : dataSnapshot.getChildren()) {
                     product = productSnapshot.getValue(Product.class);
                    if (product != null && product.getProductName()!=null) {
                        wishlist.add(product);
                    }
                }
                wishlistAdapter. notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Failed to load wishlist", Toast.LENGTH_SHORT).show();
            }
        });
    }
}