package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<MyViewHolder> {
    private List<Product> ProductList;
    private Context context;

    public ProductAdapter(List<Product> ProductList, Context context) {
        if (ProductList == null) {
            this.ProductList = new ArrayList<>();
        } else {
            this.ProductList = ProductList;
        }
        this.context = context;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycle_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Product product = ProductList.get(position);
        Glide.with(context).load(ProductList.get(position).getProductImage()).into(holder.recImage);
        holder.recName.setText(product.getProductName());
        holder.recPrice.setText(product.getProductPrice());

        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Product_Details.class);
                intent.putExtra("Image", ProductList.get(holder.getAdapterPosition()).getProductImage());
                intent.putExtra("Product Name", ProductList.get(holder.getAdapterPosition()).getProductName());
                intent.putExtra("Product Description", ProductList.get(holder.getAdapterPosition()).getProductDescription());
                intent.putExtra("Product Price ", ProductList.get(holder.getAdapterPosition()).getProductPrice());

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ProductList.size();
    }
}
     class MyViewHolder extends RecyclerView.ViewHolder {
          TextView recName,recPrice;
         ImageView recImage;
        CardView recCard;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            recName = itemView.findViewById(R.id.rec_Name);
            recPrice = itemView.findViewById(R.id.rec_Price);
            recImage= itemView.findViewById(R.id.rec_imageView);
            recCard=itemView.findViewById(R.id.recycle_Item);
        }
    }



