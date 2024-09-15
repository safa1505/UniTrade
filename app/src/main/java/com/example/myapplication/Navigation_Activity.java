package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class Navigation_Activity extends AppCompatActivity {

    TextView navUsername,navUseremail;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    List<Product> ProductList;
    private DatabaseReference databaseReference;

    private ValueEventListener valueEventListener;
    private SearchView searchView;
    private ProductAdapter productAdapter;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DocumentReference documentReference;

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


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


        toolbar = findViewById(R.id.customtoolbar);
        searchView = findViewById(R.id.searchView);
        drawerLayout = findViewById(R.id.nav_drawer);
        navigationView = findViewById(R.id.nav_view);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toggle = new ActionBarDrawerToggle(this, drawerLayout,toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        mAuth = FirebaseAuth.getInstance();

        searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(Navigation_Activity.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        AlertDialog.Builder builder = new AlertDialog.Builder(Navigation_Activity.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        ProductList = new ArrayList<>();
        productAdapter = new ProductAdapter(ProductList, Navigation_Activity.this);
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
                        Toast.makeText(getApplicationContext(), "Couldn't add product", Toast.LENGTH_SHORT).show();
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

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchlist(newText);
                return true;
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();
                if (id == R.id.myprofile) {
                    startActivity(new Intent(getApplicationContext(), User_Profile.class));
                } else if (id == R.id.myposts) {
                    startActivity(new Intent(getApplicationContext(), My_Posts.class));
                } else if (id == R.id.Wishlist) {
                    startActivity(new Intent(getApplicationContext(), WishList.class));
                } else if (id == R.id.logout) {
                    logout();
                } else if (id == R.id.delete_account) {
                    Delete_Account();
                } else if (id==R.id.share) {
                    final String appname=getPackageName();
                    Intent intent=new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT,"Check this App\n"+"https://play.google.com/store/apps/details?id"+appname );
                    intent.setType("text/plain");
                    startActivity(Intent.createChooser(intent,"Share this App"));
                }
                else if (id==R.id.changePass)
                {
                    startActivity(new Intent(getApplicationContext(), forgetpassword.class));
                }

                return false;
            }
        });

        if (currentUser != null) {
            String userID = currentUser.getUid();
            documentReference = FirebaseFirestore.getInstance().collection("Users").document(userID);
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            DocumentSnapshot document = task.getResult();
                            String name = document.getString("Name");
                            String email = document.getString("Email");

                            updateNavigationHeader(name, email);
                        } else {
                            Log.d("Firestore", "No user document found");
                        }
                    } else {
                        Log.w("Firestore", "Error getting user document:", task.getException());
                    }
                }
            });
        } else {
            Log.d("Auth", "No user currently signed in");
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                else {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(Navigation_Activity.this);
                    alertDialog.setTitle("Exit App ?");
                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishAffinity();
                        }
                    });
                    alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertDialog.create().show();
                }
            }
        });
    }

    public void searchlist(String text) {
        ArrayList<Product> searchList = new ArrayList<>();
        for (Product product : ProductList) {
            if (product != null && product.getProductName() != null) {
                String productName = product.getProductName().toLowerCase();
                String searchText = text.toLowerCase();
                if (productName.contains(searchText)) {
                    searchList.add(product);
                }
            }
        }
        productAdapter.searchProductList(searchList);
    }

    private void updateNavigationHeader(String name,String email) {

        View headerView = navigationView.getHeaderView(0);
        if (headerView != null) {
            navUsername = headerView.findViewById(R.id.profile_name);
            navUseremail = headerView.findViewById(R.id.profile_email);

            navUsername.setText(name);
            navUseremail.setText(email);
        }
    }

    private void logout(){
        FirebaseAuth.getInstance().signOut();
        Intent intent=new Intent(Navigation_Activity.this, register.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
        startActivity(intent);
    }

    private  void Delete_Account() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (builder!= null) {
            builder.setTitle("Delete account Parmanently ?").setMessage("Are you sure ?")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(Navigation_Activity.this, "Account deleted", Toast.LENGTH_SHORT).show();
                                    if (mAuth != null){
                                        mAuth.signOut();
                                    startActivity(new Intent(getApplicationContext(), register.class));
                                    finish();
                                }
                            }
                            });
                        }
                    }).setNegativeButton("Cancel", null).create().show();
        }
        else {
            Toast.makeText(Navigation_Activity.this, "Account couldn't be deleted", Toast.LENGTH_SHORT).show();
        }
    }

    }
