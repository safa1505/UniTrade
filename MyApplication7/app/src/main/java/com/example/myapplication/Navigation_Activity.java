package com.example.myapplication;

import static java.util.Locale.filter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Navigation_Activity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;
    private FirebaseAuth firebaseAuth;

    ImageView imageMenu;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    List<Product> ProductList;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseFirestore firebaseFirestore;
    private ValueEventListener valueEventListener;
    private SearchView searchView;

    private DatabaseReference productsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_navigation);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.nav_drawer), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        progressDialog = new ProgressDialog(this);
        toolbar = findViewById(R.id.toolbar);
        searchView = findViewById(R.id.searchView);
        drawerLayout = findViewById(R.id.nav_drawer);
        navigationView = findViewById(R.id.nav_view);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
        }
        setSupportActionBar(toolbar);

        Window window = getWindow();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(true);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(Navigation_Activity.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        AlertDialog.Builder builder = new AlertDialog.Builder(Navigation_Activity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        ProductList=new ArrayList<>();

        ProductAdapter productAdapter = new ProductAdapter( ProductList, Navigation_Activity.this);
        recyclerView.setAdapter(productAdapter);


        databaseReference = FirebaseDatabase.getInstance().getReference("Products");
        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ProductList.clear();
                for (DataSnapshot itemsnapshot : snapshot.getChildren()) {
                    try {
                        Product product = itemsnapshot.getValue(Product.class);
                        if (product != null) {
                            ProductList.add(product);
                        }
                    } catch (Exception e) {
                        Log.e("ProductAdapter", "Error converting product data: " + e.getMessage());
                    }
                }
                productAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                dialog.dismiss();
            }
        });


        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();
                if (id == R.id.myprofile) {
                    startActivity(new Intent(getApplicationContext(), EditProfile.class));
                }
                else if (id == R.id.myposts) {
                    startActivity(new Intent(getApplicationContext(), NewAd.class));
                } else if (id == R.id.Wishlist) {
                    Toast.makeText(Navigation_Activity.this, "Facebook", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.logout) {
                    logout();
                } else if (id == R.id.delete_account) {
                    Delete_Account();
                }
                return false;
            }
        });
    }

    private void logout(){
        FirebaseAuth.getInstance().signOut();
        Intent intent=new Intent(Navigation_Activity.this, register.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity(intent);
    }

    private  void Delete_Account() {
        if (FirebaseAuth.getInstance().getCurrentUser().delete().isSuccessful()) {
            Toast.makeText(Navigation_Activity.this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(Navigation_Activity.this, register.class);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(Navigation_Activity.this, "Couldn't delete account", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    }


