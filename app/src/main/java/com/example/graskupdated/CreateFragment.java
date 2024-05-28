package com.example.graskupdated;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateFragment extends Fragment {

    Switch btn_switch;
    TextInputEditText textView_conf;
    Boolean swt_bool = true;
    TextView txt_hereTOedit;
    Button btn_postConfession;

    FirebaseFirestore firebaseFirestore;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_create, container, false);

        btn_switch = rootView.findViewById(R.id.swtch_showIdentity);
        btn_postConfession = rootView.findViewById(R.id.btn_postConfession);
        textView_conf = rootView.findViewById(R.id.edt_confessionTxt);
        txt_hereTOedit = rootView.findViewById(R.id.txt_haveAConfession);

        String userStudentID = SharedPreferenceClass.getString(getContext(),"StudentID",null);
        String _documentToBeEditiedID = SharedPreferenceClass.getString(getContext(),"_documentToBeEditied","none");

        if(!_documentToBeEditiedID.equals("none")){

            btn_postConfession.setText("Edit");

            txt_hereTOedit.setText("EDIT YOUR ");
            String ToBeSet = SharedPreferenceClass.getString(getContext(),"_PostToBeEdited","none");
            String identiyToBeShown = SharedPreferenceClass.getString(getContext(),"_anonToBeEdited","none");

            textView_conf.setText(ToBeSet);
            if(identiyToBeShown.equals("show")){
                btn_switch.setChecked(true);
            }else{
                btn_switch.setChecked(false);
            }

        }

        btn_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    swt_bool = true;
                    btn_switch.setText("Reveal my identity"); // Change text when switch is checked (true)
                } else {
                    swt_bool = false;
                    btn_switch.setText("Do not reveal my identity"); // Change text when switch is unchecked (false)
                }
            }
        });


        btn_postConfession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!btn_postConfession.getText().equals("Edit")){
                    Date currentDate = new Date();

                    // Create a SimpleDateFormat instance with the desired format
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

                    // Format the date
                    String formattedDate = sdf.format(currentDate);

                    if(textView_conf.getText().toString().trim().isEmpty()){
                        Toast.makeText(getContext(),"Confession not specified",Toast.LENGTH_SHORT).show();
                    }else{
                        String userName = SharedPreferenceClass.getString(getContext(),"StudentName",null);
                        String userConfession = textView_conf.getText().toString();
                        String userIdentity;
                        ArrayList<String> commentList = new ArrayList<>();
                        if(swt_bool){
                            userIdentity = "show";
                        }else{
                            userIdentity = "Don't show";
                        }

                        firebaseFirestore = FirebaseFirestore.getInstance();
                        firebaseFirestore.collection("Query").add(new ConfessionAsPost(userName,userStudentID,userConfession,userIdentity,commentList,null,formattedDate))
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        String documentID = documentReference.getId();
                                        ConfessionAsPost confession = new ConfessionAsPost(userName, userStudentID, userConfession, userIdentity, commentList, documentID,formattedDate);
                                        firebaseFirestore.collection("Query")
                                                .document(documentID)
                                                .set(confession)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(getContext(), "Posted", Toast.LENGTH_SHORT).show();
                                                        FirebaseFirestore firebaseFirestore_updateArray = FirebaseFirestore.getInstance();
                                                        firebaseFirestore_updateArray.collection("StudentInfo")
                                                                .document(userStudentID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                                    @Override
                                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                        if(documentSnapshot.exists()){
                                                                            List<String> currentArray  = (List<String>) documentSnapshot.get("UserPost");
                                                                            currentArray.add(documentID);
                                                                            firebaseFirestore_updateArray.collection("StudentInfo").document(userStudentID).update("UserPost",currentArray);
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // Handle failure
                                                    }
                                                });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });
                    }
                }else{
                    firebaseFirestore = FirebaseFirestore.getInstance();
                    updateDocumentField("Query",SharedPreferenceClass.getString(getContext(),"_documentToBeEditied","none"));
                    SharedPreferenceClass.saveString(getContext(), "_documentToBeEditied", "none");

                }
            }
        });

        return rootView;
    }

    private void updateDocumentField(String collectionName, String documentId) {
        // Data to update
        Map<String, Object> updates = new HashMap<>();
        updates.put("userConfession", textView_conf.getText().toString());
        String identity;
        if(btn_switch.getText().equals("Reveal my identity")){
            identity = "show";
        }else{
            identity = "Don't show";
        }

        updates.put("showIdentity",identity);

        // Update the specific field
        firebaseFirestore.collection(collectionName).document(documentId)
                .update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Your post has been edited", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Error while editing the post, please try again later ", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void openFragment() {
        Fragment fragment = new QueryFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .commit();

    }

}