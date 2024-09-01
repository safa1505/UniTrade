package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.bumptech.glide.Glide;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
public class MyPostsAdapter extends RecyclerView.Adapter<MyPostsAdapter.ProductViewHolder> {
    private List<Product> ProductList;
    private Context context;

    private DatabaseReference databaseReference;

    public MyPostsAdapter(List<Product> ProductList, Context context) {

        if (ProductList == null) {
            this.ProductList = new ArrayList<>();
        } else {
            this.ProductList = ProductList;
        }
        this.context = context;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.my_posts_item, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyPostsAdapter.ProductViewHolder holder, int position) {
        Product product = ProductList.get(position);
        Glide.with(context).load(ProductList.get(position).getProductImage()).into(holder.My_recImage);
        holder.My_recName.setText(product.getProductName());
        holder.My_recPrice.setText(product.getProductPrice());

        holder.product_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Product_Update.class);
                intent.putExtra("Image", ProductList.get(holder.getAdapterPosition()).getProductImage());
                intent.putExtra("Product Name", ProductList.get(holder.getAdapterPosition()).getProductName());
                intent.putExtra("Product Description", ProductList.get(holder.getAdapterPosition()).getProductDescription());
                intent.putExtra("Product Price ", ProductList.get(holder.getAdapterPosition()).getProductPrice());

                context.startActivity(intent);
            }
        });
        holder.product_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                String name = product.getProductName();
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser user = mAuth.getCurrentUser();


                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle("Delete Product Parmanently ?")
                        .setMessage("Are you sure you want to delete?")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                databaseReference = FirebaseDatabase.getInstance().getReference("Products");
                                if (databaseReference != null) {
                                    Query query = databaseReference.orderByChild("productName").equalTo(name);
                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                dataSnapshot.getRef().removeValue();
                                                Product product = dataSnapshot.getValue(Product.class);
                                                if (product != null) {
                                                    ProductList.remove(product);
                                                    notifyItemRemoved(position);
                                                    Log.d("Deletion", "Product deleted");
                                                }
                                            }
                                            dialog.dismiss();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Log.e("Deletion", "Error deleting product: " + error.getMessage());
                                            dialog.dismiss();
                                        }
                                    });
                                } else {
                                    Log.e("Deletion", "databaseReference is null");
                                }
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create().show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return ProductList.size();
    }

    class ProductViewHolder extends ViewHolder {
        TextView My_recName, My_recPrice;
        ImageView My_recImage;
        Button product_update, product_delete;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            My_recName = itemView.findViewById(R.id.my_product_name);
            My_recPrice = itemView.findViewById(R.id.my_product_price);
            My_recImage = itemView.findViewById(R.id.myProduct_imageView);
            product_update = itemView.findViewById(R.id.My_product_update);
            product_delete = itemView.findViewById(R.id.My_product_delete);
        }
    }
}



