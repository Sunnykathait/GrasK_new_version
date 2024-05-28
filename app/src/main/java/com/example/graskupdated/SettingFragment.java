package com.example.graskupdated;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SettingFragment extends Fragment {

    private TextView txt_userNameSetting , txt_userStudentIDSetting , txt_userPhoneNumberSetting, txt_logout, txt_moreOptions;
    FirebaseFirestore firebaseFirestore;
    ArrayList<String> documentID_array = new ArrayList<>();

    RecyclerView recyclerView_yourPost;
    YourPostUserSettingAdapter userShownPostAdapter;


    public SettingFragment() {
        // Required empty public constructor
    }


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);

        // id initialized
        txt_userNameSetting = rootView.findViewById(R.id.txt_userNameSetting);
        txt_userStudentIDSetting = rootView.findViewById(R.id.txt_userStudentIDSetting);
        txt_userPhoneNumberSetting = rootView.findViewById(R.id.txt_userPhoneNumberSetting);
        txt_moreOptions = rootView.findViewById(R.id.txt_moreSettingBTN);
        recyclerView_yourPost = rootView.findViewById(R.id.RV_yourPost);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView_yourPost.setLayoutManager(layoutManager);

        // setting user basic info
        txt_userNameSetting.setText(SharedPreferenceClass.getString(getContext(),"StudentName","Student name"));
        txt_userStudentIDSetting.setText(SharedPreferenceClass.getString(getContext(),"StudentID","Student name"));
        txt_userPhoneNumberSetting.setText(SharedPreferenceClass.getString(getContext(),"StudentPhoneNumber","Student name"));

        firebaseFirestore = FirebaseFirestore.getInstance();

        txt_moreOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MoreOptionsActivity.class);
                startActivity(intent);
                requireActivity().overridePendingTransition(R.anim.slide_right, R.anim.slide_left);
            }
        });

        DocumentReference docRef = firebaseFirestore.collection("StudentInfo").document(SharedPreferenceClass.getString(getContext(),"StudentID",null));
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        documentID_array = (ArrayList<String>) document.get("UserPost");

                        if(documentID_array == null || documentID_array.size() == 0){
                            return;
                        }

                        FirebaseFirestore _firebaseFirestore1 = FirebaseFirestore.getInstance();
                        Query query = _firebaseFirestore1.collection("Query")
                                .whereIn(FieldPath.documentId(), documentID_array);

                        query.get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                    List<ConfessionAsPost> _confessionAsPostcommentsList1 = new ArrayList<>();
                                    for(QueryDocumentSnapshot documentSnapshot1 : queryDocumentSnapshots){
                                        ConfessionAsPost comment_ = documentSnapshot1.toObject(ConfessionAsPost.class);
                                        _confessionAsPostcommentsList1.add(comment_);
                                    }
                                     userShownPostAdapter= new YourPostUserSettingAdapter(getContext(), (ArrayList<ConfessionAsPost>) _confessionAsPostcommentsList1, getActivity());
                                    recyclerView_yourPost.setAdapter(userShownPostAdapter);
                                }).addOnFailureListener(e -> {
                                    // Handle failure
                                    Toast.makeText(getContext(),"error in retrievein the comments from the collection ",Toast.LENGTH_SHORT).show();
                                });

                    }
                }
            }
        });


        return rootView;
    }
}