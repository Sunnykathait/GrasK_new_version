package com.example.graskupdated;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Transportation extends AppCompatActivity {

    TextView searchPlace_btn;

    ImageView search_img;
    EditText edt_search;
    ListView showPlaces;

    private GestureDetector gestureDetector;

    FirebaseFirestore firebaseFirestore;

    ArrayList<String> places_array;

    ArrayAdapter<String> adapter;

    FirebaseOptions options = new FirebaseOptions.Builder()
            .setApplicationId("1:524990813073:android:249281a55f7fcf1d110030")
            .setApiKey("AIzaSyBzVntWSjVxukck9nJSPOKpq6zSObCQvhw")
            .setProjectId("grabus-18f1c")
            .setDatabaseUrl("https://console.firebase.google.com/u/1/project/grabus-18f1c/firestore/databases/-default-/data/~2FBusNumber~2F1")
            .build();


    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_transportation);

        gestureDetector = new GestureDetector(this, new SwipeGestureListener());

        searchPlace_btn = findViewById(R.id.searchByplace_btn);

        search_img = findViewById(R.id.btn_search);
        edt_search = findViewById(R.id.edttxt_searchByBus);

        places_array = new ArrayList<>();
        FirebaseApp busDatabaseApp;

        RecyclerView recyclerView = findViewById(R.id.RV_ShowLocation);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        try {
            busDatabaseApp = FirebaseApp.getInstance("busDatabase");
        }catch (Exception e){
            busDatabaseApp = null;
        }

        if (busDatabaseApp == null) {
            FirebaseApp.initializeApp(this.getApplicationContext(), options, "busDatabase");
        }

        FirebaseApp secondApp = FirebaseApp.getInstance("busDatabase");

        if (secondApp == null) {
            Toast.makeText(getApplicationContext(), "first time", Toast.LENGTH_SHORT).show();
        } else {
            FirebaseFirestore db = FirebaseFirestore.getInstance(secondApp);
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance(secondApp);


        search_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edt_search.getText().toString().trim().length() == 0){
                    edt_search.setError("No bus number selected");
                    return;
                }else{
                    edt_search.setError(null);

                    DocumentReference documentReference = db.collection("BusNumber").document(edt_search.getText().toString());
                    documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if(documentSnapshot.exists()){
                                    places_array = (ArrayList<String>) documentSnapshot.get("Places");
                                    LocationAdapterClass adapter = new LocationAdapterClass(getApplicationContext(), places_array);
                                    recyclerView.setAdapter(adapter);
                                }else{
                                    Toast.makeText(getApplicationContext() , "No bus found with this number",Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                Toast.makeText(getApplicationContext() , "Failed with " + task.getException(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
    }

    private class SwipeGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float diffX = e2.getX() - e1.getX();
            float diffY = e2.getY() - e1.getY();

            if (diffX < 0 && Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                startActivity(new Intent(Transportation.this, PlacesActivity.class));
                finish();
            }

            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

}