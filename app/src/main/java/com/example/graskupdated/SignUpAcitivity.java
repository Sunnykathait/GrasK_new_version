package com.example.graskupdated;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SignUpAcitivity extends AppCompatActivity {

    Button btn_singUp, btn_wrongINFO, btn_fetchINFO, btn_goBack, btn_confirm, btn_otp;
    TextView txt_studenNAME, txt_studentCOURSE, txt_studentYEARSEM;
    EditText edt_studentID, edt_studentPhoneNumber, edt_otp;
    FirebaseFirestore firebaseFirestore;

    LinearLayout LL_info, LL_link;

    FirebaseAuth mAuth;

    String returnedPhoneNumber = "-1";

    String _StudenId;

    private String verificationId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up_acitivity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();

        btn_fetchINFO = findViewById(R.id.btn_FI);
        btn_singUp = findViewById(R.id.btn_YTIM);
        btn_wrongINFO = findViewById(R.id.btn_WI);
        btn_confirm = findViewById(R.id.btn_confirm);
        btn_goBack = findViewById(R.id.btn_goBack);
        btn_otp = findViewById(R.id.btn_confOTP);

        txt_studenNAME = findViewById(R.id.txt_studentName);
        txt_studentYEARSEM = findViewById(R.id.txt_yearSEM);
        txt_studentCOURSE = findViewById(R.id.txt_Course);

        edt_studentID = findViewById(R.id.edt_StudentID);
        edt_studentPhoneNumber = findViewById(R.id.edt_StudentNumber);
        edt_otp = findViewById(R.id.edt_otp);

        LL_info = findViewById(R.id.LL_info);
        LL_link = findViewById(R.id.LL_linkNumber);

        btn_fetchINFO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FetchInfo(edt_studentID.getText().toString());
                _StudenId = edt_studentID.getText().toString();
                btn_singUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LL_link.setVisibility(View.VISIBLE);
                        LL_info.setVisibility(View.GONE);
                    }
                });
            }
        });

        btn_goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LL_info.setVisibility(View.VISIBLE);
                LL_link.setVisibility(View.GONE);
            }
        });

        firebaseFirestore = FirebaseFirestore.getInstance();

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = edt_studentPhoneNumber.getText().toString();
                String signedPhoneNumber = getPhoneNumberFromFirebase(_StudenId);
                if (signedPhoneNumber.equals(phoneNumber)) {
                    ConfirmStudentID(_StudenId);
                }
            }
        });

        btn_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyCode(edt_otp.getText().toString());
            }
        });
    }

    String getPhoneNumberFromFirebase(String StudentID){
        firebaseFirestore = FirebaseFirestore.getInstance();
        Task<DocumentSnapshot> documentReference = firebaseFirestore.collection("StudentInfo")
                .document(StudentID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            returnedPhoneNumber = documentSnapshot.getString("PhoneNumber");
                            SharedPreferenceClass.saveString(getApplicationContext(),"StudentPhoneNumber",returnedPhoneNumber);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Some error occurred while processing , please tyr again later",Toast.LENGTH_SHORT
                        ).show();
                    }
                });

        return returnedPhoneNumber;
    }

    void ConfirmStudentID(String StudentID){
        DocumentReference docRef = firebaseFirestore.collection("StudentInfo").document(StudentID);
        docRef.update("Confirmed",true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        edt_studentPhoneNumber.setFocusable(false);
                        edt_studentPhoneNumber.setFocusableInTouchMode(false);
                        edt_studentPhoneNumber.setCursorVisible(false);
                        edt_studentPhoneNumber.setInputType(InputType.TYPE_NULL);

                        edt_otp.setVisibility(View.VISIBLE);
                        sendVerificationCode(edt_studentPhoneNumber.getText().toString());
                    }
                });
    }

    void FetchInfo(String studentID){
        Task<DocumentSnapshot> documentReference = firebaseFirestore.collection("StudentInfo")
                .document(studentID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){

                            // retrieving information from firebase
                            String studentName = documentSnapshot.getString("SName");
                            String studentCourse = documentSnapshot.getString("Course");
                            String studentYearSem = documentSnapshot.getString("YearSem");


                            // Saving information in SharedPreference
                            SharedPreferenceClass.saveString(getApplicationContext(),"StudentName",studentName);
                            SharedPreferenceClass.saveString(getApplicationContext(),"Course",studentCourse);
                            SharedPreferenceClass.saveString(getApplicationContext(),"YearSem",studentYearSem);
                            SharedPreferenceClass.saveString(getApplicationContext(),"StudentID",studentID);

                            txt_studenNAME.setText(studentName);
                            txt_studentCOURSE.setText(studentCourse);
                            txt_studentYEARSEM.setText(studentYearSem);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Some error occurred while processing , please tyr again later",Toast.LENGTH_SHORT
                                ).show();
                    }
                });
    }

    private void sendVerificationCode(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91"+phoneNumber,
                60,
                TimeUnit.SECONDS,
                this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        // Auto-retrieval of OTP completed by Google Play services.
                        // You can directly call signInWithCredential(phoneAuthCredential) here.
                        Log.d(TAG, "onVerificationCompleted:" + phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        Log.w(TAG, "onVerificationFailed", e);
                        Toast.makeText(getApplicationContext(), "Verification failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        verificationId = s;
                        Toast.makeText(getApplicationContext(), "OTP sent", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Verification successful
                            Toast.makeText(getApplicationContext(), "Verification successful", Toast.LENGTH_SHORT).show();

                            Toast.makeText(getApplicationContext(), "Signing you in ....... ", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            SharedPreferenceClass.saveString(getApplicationContext(),"userLoggedIn","yes");
                            startActivity(intent);
                            finish();
                        } else {
                            // Verification failed
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(getApplicationContext(), "Verification failed, invalid code entered", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Verification failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}