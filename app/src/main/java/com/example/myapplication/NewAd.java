package com.example.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.result.ActivityResultCallback;

public class NewAd extends AppCompatActivity {

    public ImageView galleryimage;
    private Button addnewproductbutton,BackBtn;
    private String Pdescription, Pname,Pprice;
    private Uri imageUri;
    private EditText inputproductname, inputproductdescription, inputproductprice;
    private String productRandKey, downloadImageURl, formattedDate, formattedTime;
    private StorageReference ProductImagestorageReference;
    private ProgressDialog progressDialog;
    private  FirebaseFirestore fstore,db;
    private FirebaseAuth fauth;
    private String  userID,productID;
    private FirebaseUser currentuser;
    DatabaseReference productRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_ad);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ProductImagestorageReference = FirebaseStorage.getInstance().getReference().child("Product Images");

        productRef=FirebaseDatabase.getInstance().getReference("Products");
        db=FirebaseFirestore.getInstance();
        fstore = FirebaseFirestore.getInstance();
        fauth=FirebaseAuth.getInstance();
        galleryimage = findViewById(R.id.cameraimageView);
        inputproductname = findViewById(R.id.productnameadd);
        inputproductdescription = findViewById(R.id.productdescription);
        inputproductprice = findViewById(R.id.productprice);
        BackBtn = findViewById(R.id.backButton);
        addnewproductbutton=findViewById(R.id.addproductbtn);
        progressDialog = new ProgressDialog(this);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            imageUri = data.getData();
                            galleryimage.setImageURI(imageUri);
                        } else {
                            Toast.makeText(NewAd.this, "No Image selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        galleryimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                activityResultLauncher.launch(galleryIntent);
            }
        });

        addnewproductbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri != null) {
                    uploadPhoto();
                }
            }
        });

        BackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
}

    private void validateproductData() {
        Pdescription = inputproductdescription.getText().toString().trim();
        Pname = inputproductname.getText().toString().trim();
        Pprice =inputproductprice.getText().toString().trim();

        currentuser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentuser != null)
        {
            userID= currentuser.getUid();
        }
        productID=productRef.push().getKey();

        if (imageUri== null) {
            Toast.makeText(this, "Product Image is Mandatory", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(Pdescription)) {
            Toast.makeText(this, "Product description is Mandatory", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(Pname)) {
            Toast.makeText(this, "Product Name is Mandatory", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(Pprice)) {
            Toast.makeText(this, "Product price is Mandatory", Toast.LENGTH_SHORT).show();
        }
        else {
            Product product=new Product(Pname,Pprice,Pdescription,downloadImageURl,userID,productID);
            productRef.child(productID).setValue(product).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        Toast.makeText(NewAd.this, "Product is added Successfully", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(NewAd.this, Navigation_Activity.class);
                        startActivity(intent);
                    } else {
                        progressDialog.dismiss();
                        String message = task.getException().toString();
                        Toast.makeText(NewAd.this, "Product couldn't be added: " + message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    private void uploadPhoto() {

        progressDialog.setTitle("Adding New Product");
        progressDialog.setMessage("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss a", Locale.getDefault());

        formattedDate = dateFormat.format(calendar.getTime());
        formattedTime = timeFormat.format(calendar.getTime());

        productRandKey = formattedTime + formattedDate;

        StorageReference filepath = ProductImagestorageReference.child(imageUri.getLastPathSegment() + productRandKey + ".jpg");

        final UploadTask uploadTask = filepath.putFile(imageUri);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.getMessage();
                Toast.makeText(NewAd.this, "Error uploading image: " + message, Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(NewAd.this, "Image uploaded successfully!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                    filepath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> uriTask) {
                            if (uriTask.isSuccessful()) {
                                downloadImageURl = uriTask.getResult().toString();
                                Toast.makeText(NewAd.this, "Getting product image URL successfully", Toast.LENGTH_SHORT).show();
                                validateproductData();
                            } else {
                                Exception exception = uriTask.getException();
                                String message = exception.getMessage();
                                Toast.makeText(NewAd.this, "Error getting image URL: " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Exception exception = task.getException();
                    String message = exception.getMessage();
                    Toast.makeText(NewAd.this, "Image upload failed: " + message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}







