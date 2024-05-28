package com.example.graskupdated;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PlacesActivity extends AppCompatActivity {

    TextView searchBusPage;

    ImageView search_img;
    EditText edt_search;
    ListView showPlaces , showBuses;

    ArrayList<String> bus_array , places_name;;

    ArrayAdapter<String> adapter , adapter2;

    private GestureDetector gestureDetector;

    FirebaseOptions options = new FirebaseOptions.Builder()
            .setApplicationId("1:524990813073:android:249281a55f7fcf1d110030")
            .setApiKey("AIzaSyBzVntWSjVxukck9nJSPOKpq6zSObCQvhw")
            .setProjectId("grabus-18f1c")
            .setDatabaseUrl("https://console.firebase.google.com/u/1/project/grabus-18f1c/firestore/databases/-default-/data/~2FBusNumber~2F1")
            .build();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_places);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        gestureDetector = new GestureDetector(this, new PlacesActivity.SwipeGestureListener());

        FirebaseApp busDatabaseApp;

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

        searchBusPage = findViewById(R.id.searchByBusNumber_btn);
        search_img = findViewById(R.id.btn_search_2);
        edt_search = findViewById(R.id.edttxt_searchByPlace);
        showPlaces = findViewById(R.id.showBus_lstview1);
        showBuses = findViewById(R.id.showBus_lstview2);

        bus_array = new ArrayList<String>();

        DocumentReference documentReference = db.collection("Places").document("placeName");
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        places_name = (ArrayList<String>) documentSnapshot.get("places");
                        liskPlaces();
                    }
                }
            }
        });

        showPlaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(),"working",Toast.LENGTH_SHORT).show();
                String placesItemAtPosition = (String) (showPlaces.getItemAtPosition(i));
                edt_search.setText(placesItemAtPosition);
            }
        });

        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                PlacesActivity.this.adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                bus_array.clear();
            }
        });

        search_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Query query = db.collection("BusNumber");
                query = query.whereArrayContains("Places",edt_search.getText().toString());
                query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null){
                            return;
                        }
                        if(value != null){
                            List<DocumentSnapshot> snapshots = value.getDocuments();
                            bus_array.clear();
                            for (DocumentSnapshot document : snapshots){
                                bus_array.add(document.getId());
                            }
                        }
//                        adapter2.notifyDataSetChanged();
                        adapter2 = new ArrayAdapter<String>(getApplicationContext() , android.R.layout.simple_list_item_1 , bus_array);
                        showBuses.setAdapter(adapter2);
//                        adapter2.notifyDataSetChanged();
                    }
                });
            }
        });


        searchBusPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext() , MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initBuses(ArrayList<String> bus_array) {
        ArrayList<String> temp = bus_array;
        bus_array.clear();
        bus_array = temp;
    }

    private void liskPlaces() {
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, (List<String>) places_name);
        showPlaces.setAdapter(adapter);
    }

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

            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        // Swipe from left to right
                        // Perform activity transition to the previous activity
                        Intent intent = new Intent(getApplicationContext() , Transportation.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }

            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext() , Transportation.class);
        startActivity(intent);
        finish();
    }
}