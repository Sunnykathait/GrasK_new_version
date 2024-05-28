package com.example.graskupdated;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class ReportRagging extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;

    private LinearLayout LL_done, LL_rag;

    TextInputEditText edt_RagDesc;
    ImageView img_Proof;
    Button btn_submit;

    private FirebaseStorage storage;

    FirebaseFirestore firebaseFirestore;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_report_ragging);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        edt_RagDesc = findViewById(R.id.edt_ragging_description);
        img_Proof = findViewById(R.id.img_upload_proof);
        btn_submit = findViewById(R.id.btn_submitProof);

        LL_rag = findViewById(R.id.LL_rag);
        LL_done = findViewById(R.id.LL_done);

        LL_rag.setVisibility(View.VISIBLE);

        storage = FirebaseStorage.getInstance();

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edt_RagDesc.getText().toString().toString().isEmpty()){
                    edt_RagDesc.setError("Please enter a description");
                    Toast.makeText(getApplicationContext(),"Ragging description cannot be empty",Toast.LENGTH_SHORT).show();
                    return;
                } else{
                    String desc = edt_RagDesc.getText().toString().trim();
                    firebaseFirestore = FirebaseFirestore.getInstance();

                    DocumentReference documentReference = firebaseFirestore.collection("Ragging").document();
                    Bitmap bitmap = ((BitmapDrawable) img_Proof.getDrawable()).getBitmap();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();

                    StorageReference storageRef = storage.getReference();
                    String imageName = System.currentTimeMillis() + ".jpg";
                    StorageReference imagesRef = storageRef.child("images/" + imageName);

                    UploadTask uploadTask = imagesRef.putBytes(data);
                    uploadTask.addOnFailureListener(exception -> {
                        // Handle unsuccessful uploads
                        Log.e("Upload", "Upload failed", exception);
                        Toast.makeText(getApplicationContext(), "Upload failed", Toast.LENGTH_SHORT).show();
                    }).addOnSuccessListener(taskSnapshot -> {
                        // Task completed successfully
                        imagesRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String downloadUrl = uri.toString();
                            Log.d("Upload", "Upload successful, URL: " + downloadUrl);
                            Map<String, Object> _ragPost = new HashMap<>();
                            _ragPost.put("description", desc);
                            _ragPost.put("imageURL", downloadUrl);
                            documentReference.set(_ragPost).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(getApplicationContext(),"Your post successfully uploaded and will be reviewed by the admin",Toast.LENGTH_SHORT).show();
                                }
                            });

                            LL_rag.setVisibility(View.GONE);
                            LL_done.setVisibility(View.VISIBLE);

                        });
                    });

                }
            }
        });

        img_Proof.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE);
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                img_Proof.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void goBack(View view) {
        Intent intent = new Intent(getApplicationContext(), MoreOptionsActivity.class);
        startActivity(intent);
        finish();
    }
}