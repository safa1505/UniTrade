package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {
    List<Product> ProductList;
    Context context;
    String currentuserID;
    DatabaseReference wishlistRef, productRef, childRef;

    public ProductAdapter(List<Product> ProductList, Context context) {
        if (ProductList == null) {
            this.ProductList = new ArrayList<>();
        } else {
            this.ProductList = ProductList;
        }
        this.context = context;

        FirebaseAuth mauth = FirebaseAuth.getInstance();
        FirebaseUser currentuser = mauth.getCurrentUser();
        if (currentuser != null) {
            currentuserID = currentuser.getUid();
            wishlistRef = FirebaseDatabase.getInstance().getReference("Wishlist");
            childRef = wishlistRef.child(currentuserID);
        } else {
            currentuserID = null;
            wishlistRef = null;
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycle_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.MyViewHolder holder, int position) {
        Product product = ProductList.get(position);

        if (product == null || product.getProductName() == null) {
            Log.e("ProductAdapter", "Invalid product data: " + product);
            return;
        }
        Glide.with(context).load(product.getProductImage()).into(holder.recImage);
        holder.recName.setText(product.getProductName());
        holder.recPrice.setText(product.getProductPrice());


        holder.seedetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, Product_Details.class);
                intent.putExtra("Image", product.getProductImage());
                intent.putExtra("Product Name", product.getProductName());
                intent.putExtra("Product Description", product.getProductDescription());
                intent.putExtra("Product Price", (product.getProductPrice()));
                context.startActivity(intent);
            }
        });


        if (childRef != null) {
            DatabaseReference productRef = childRef.child(product.getProductName());

            productRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        holder.favIcon.setImageResource(R.drawable.baseline_favorite_24);
                    } else {
                        holder.favIcon.setImageResource(R.drawable.baseline_favorite_border_24);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, "Error retrieving wishlist", Toast.LENGTH_SHORT).show();
                }
            });

            holder.favIcon.setOnClickListener(v -> {
                int Position=holder.getAdapterPosition();
                productRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            productRef.removeValue().addOnSuccessListener(aVoid -> {
                                holder.favIcon.setImageResource(R.drawable.baseline_favorite_border_24);
                                Toast.makeText(context, "Removed from wishlist", Toast.LENGTH_SHORT).show();
                                notifyItemRemoved(Position);
                            }).addOnFailureListener(e -> {
                                Toast.makeText(context, "Error removing from wishlist", Toast.LENGTH_SHORT).show();
                            });
                        } else {
                            DatabaseReference wishlistRef = FirebaseDatabase.getInstance().getReference().child("Wishlist").child(currentuserID).child(product.getProductName());
                            wishlistRef.setValue(product)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            holder.favIcon.setImageResource(R.drawable.baseline_favorite_24);
                                            Toast.makeText(context, "Added to wishlist", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(context, "Error adding to wishlist: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            e.printStackTrace();
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }
        holder.favIcon.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return ProductList.size();
    }

    public void searchProductList(ArrayList<Product> searchList) {
        ProductList = searchList;
        notifyDataSetChanged();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView recName, recPrice,seedetails;
        ImageView recImage, favIcon;
        CardView recCard;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            recName = itemView.findViewById(R.id.rec_Name);
            recPrice = itemView.findViewById(R.id.rec_Price);
            recImage = itemView.findViewById(R.id.rec_imageView);
            recCard = itemView.findViewById(R.id.recycle_Item);
            seedetails=itemView.findViewById(R.id.seeDetailsTextView);
            favIcon = itemView.findViewById(R.id.fav_icon);
        }
    }
}