package com.example.graskupdated;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CommentFragment extends Fragment {

    private TextView textView_userName, textView_userPost;
    private RecyclerView recyclerView_comments;
    private CommentAdapter commentAdapter;

    private EditText edt_comment;
    private ImageView imageView_post;
    private TextView textView_NO_comment;

    FirebaseFirestore firebaseFirestore, firebaseFirestore_fetchComments;

    public CommentFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comment, container, false);

        textView_userName = view.findViewById(R.id.userName);
        textView_userPost = view.findViewById(R.id.userPost);

        edt_comment = view.findViewById(R.id.edt_comment);
        imageView_post = view.findViewById(R.id.btn_postComment);

        recyclerView_comments = view.findViewById(R.id.RV_comments);
        textView_NO_comment = view.findViewById(R.id.txt_noComments);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore_fetchComments = FirebaseFirestore.getInstance();

        String _userName = SharedPreferenceClass.getString(view.getContext(),"userNameToBeShownForTheComment","Username");
        String _userPost = SharedPreferenceClass.getString(view.getContext(),"userCommentToBeShownFroTheComment","Userpost");
        String _userDocumentConID = SharedPreferenceClass.getString(view.getContext(),"userPostDocumentID","userPostID");

        textView_userName.setText(_userName);
        textView_userPost.setText(_userPost);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView_comments.setLayoutManager(layoutManager);

       firebaseFirestore_fetchComments.collection("Query")
               .document(_userDocumentConID)
               .get()
               .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                   @Override
                   public void onSuccess(DocumentSnapshot documentSnapshot) {
                       if (documentSnapshot.exists()) {
                           // Document exists, retrieve the array field
                           List<String> yourArray = (List<String>) documentSnapshot.get("comments");
                           if (yourArray != null) {
                               if(yourArray.isEmpty()){
                                   textView_NO_comment.setVisibility(View.VISIBLE);
                                   recyclerView_comments.setVisibility(View.GONE);
                               }else{
                                   textView_NO_comment.setVisibility(View.GONE);
                                   recyclerView_comments.setVisibility(View.VISIBLE);

                                   FirebaseFirestore firebaseFirestore1 = FirebaseFirestore.getInstance();
                                   Query query = firebaseFirestore1.collection("Comment")
                                           .whereIn(FieldPath.documentId(), yourArray);

                                   query.get()
                                           .addOnSuccessListener(queryDocumentSnapshots -> {
                                               List<Comments> commentsList1 = new ArrayList<>();
                                               for(QueryDocumentSnapshot documentSnapshot1 : queryDocumentSnapshots){
                                                   Comments comment_ = documentSnapshot1.toObject(Comments.class);
                                                   commentsList1.add(comment_);
                                               }
                                               commentAdapter = new CommentAdapter(view.getContext(), commentsList1);
                                               recyclerView_comments.setAdapter(commentAdapter);
                                           }).addOnFailureListener(e -> {
                                               // Handle failure
                                               Toast.makeText(getContext(),"error in retrievein the comments from the collection ",Toast.LENGTH_SHORT).show();
                                           });
                               }



                           } else {
                               Toast.makeText(getContext(),"Array is null",Toast.LENGTH_SHORT).show();
                               // Handle case where array is null
                               Log.d("ArrayItem", "Array is null");
                           }

                       } else {
                           // Document does not exist
                           Log.d("Document", "Document does not exist");
                       }
                   }
               });


        imageView_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = SharedPreferenceClass.getString(getContext(),"StudentName","dev darshan");
                if(edt_comment.getText().toString().trim().isEmpty()){
                    Toast.makeText(getContext(),"Comment cannot be empty",Toast.LENGTH_SHORT).show();
                }else{
                    Comments comments = new Comments(userName,edt_comment.getText().toString(),String.valueOf(0));
                    firebaseFirestore.collection("Comment").add(comments).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            FirebaseFirestore firebaseFirestore1 = FirebaseFirestore.getInstance();
                            Map<String,Object> updateMap = new HashMap<>();
                            updateMap.put("comments", FieldValue.arrayUnion(documentReference.getId().toString()));
                            firebaseFirestore1.collection("Query").document(_userDocumentConID)
                                            .update(updateMap)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Toast.makeText(getContext(),"Comment added ....",Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(),"Error occurred !!!!!!!!!!....",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


        return view;
    }

}