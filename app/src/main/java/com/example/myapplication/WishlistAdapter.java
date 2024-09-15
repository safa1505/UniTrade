package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;
import java.util.ArrayList;
import java.util.List;


public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.ViewHolder> {
    private List<Product> wishlist;
    private Context context;


    public WishlistAdapter(Context context, List<Product> wishlist) {
        if(wishlist!=null) {
            this.wishlist = wishlist;
        }
        else {
           this. wishlist=new ArrayList<>();
        }
            this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.wishlist_rec_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = wishlist.get(position);
        Glide.with(context).load(product.getProductImage()).into(holder.WishlistproductImage);
        holder.WishlistproductName.setText(product.getProductName());
        holder.WishlistproductPrice.setText(product.getProductPrice());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Product_Details.class);
                intent.putExtra("Image",product.getProductImage());
                intent.putExtra("Product Name", product.getProductName());
                intent.putExtra("Product Price", product.getProductPrice());
                intent.putExtra("Product Description", product.getProductDescription());

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return wishlist.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView WishlistproductName, WishlistproductPrice;
        ImageView WishlistproductImage;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            WishlistproductName = itemView.findViewById(R.id.wishlistrec_Name);
            WishlistproductPrice = itemView.findViewById(R.id.wishlistrec_Price);
            WishlistproductImage = itemView.findViewById(R.id.wishlistrec_imageView);
            cardView=itemView.findViewById(R.id.wishlist_rec_item);
        }
    }

}