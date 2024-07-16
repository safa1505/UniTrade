package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class My_Posts extends AppCompatActivity {

    private RecyclerView recyclerView;
    List<Product> ProductList;
    private MyPostsAdapter adapter;
    private DatabaseReference databaseReference;
    private ImageView new_product_image;
    FirebaseFirestore db;
    private FirebaseUser currentUser;
     private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_posts);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();

        new_product_image = findViewById(R.id.new_product_image);
        recyclerView = findViewById(R.id.MyrecyclerView);
        recyclerView.setHasFixedSize(true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(My_Posts.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

       AlertDialog.Builder builder = new AlertDialog.Builder(My_Posts.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();



        new_product_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NewAd.class));
            }
        });


        ProductList = new ArrayList<>();
        adapter = new MyPostsAdapter(ProductList, My_Posts.this);
        recyclerView.setAdapter(adapter);

         mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String currentUserID = currentUser.getUid();
            db.collection("Users").document(currentUserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    String userId = document.getString("userId");
                                    databaseReference = FirebaseDatabase.getInstance().getReference("Products");

                                    Query query = databaseReference.orderByChild("userID").equalTo(userId);

                                    query.addValueEventListener(new ValueEventListener() {
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            ProductList.clear();
                                            for (DataSnapshot itemsnapshot : snapshot.getChildren()) {
                                                try {
                                                    Product product = itemsnapshot.getValue(Product.class);
                                                    if (product != null && product.getUserId().equals(currentUserID)) {
                                                        ProductList.add(product);
                                                    }
                                                } catch (Exception e) {
                                                    Log.e("ProductAdapter", "Error converting product data: " + e.getMessage());
                                                }
                                                adapter.notifyDataSetChanged();
                                                dialog.dismiss();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                            dialog.dismiss();
                                        };
                                    });
                                } else {
                                    Log.d("Firestore", "No user document found");
                                }
                            } else {
                                Log.w("Firestore", "Error getting user document:", task.getException());
                            }
                        }
                    });
        }
    }
}