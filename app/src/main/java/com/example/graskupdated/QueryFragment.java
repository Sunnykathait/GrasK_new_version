package com.example.graskupdated;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;


public class QueryFragment extends Fragment {

    RecyclerView recyclerView, recyclerView_FAQ;
    private PostAdapter postAdapter;
    private List<ConfessionAsPost> postList = new ArrayList<>();

    private ArrayList<ArrayList<String>> faqArrayList = new ArrayList<>();

    private TextView txt_faqBTN, txt_queryBTN;

    private LottieAnimationView lottieAnimationView_faq;
    private int lastVisitedRecylerView = R.id.RV_faq;

    FirebaseFirestore firebaseFirestore;

    public QueryFragment() {
        firebaseFirestore = FirebaseFirestore.getInstance();

//         initializing the array with the dummy data

        faqArrayList.add(new ArrayList<String>() {{
            add("What is the admission process for this university?");
            add("The admission process for our university involves submitting an application form, providing transcripts and test scores, and participating in an interview.");
        }});

        faqArrayList.add(new ArrayList<String>() {{
            add("What are the tuition and fees for this university?");
            add("Tuition and fees for our university vary depending on the program of study and the student's residency status. Please visit our website for more information.");
        }});

        faqArrayList.add(new ArrayList<String>() {{
            add("What types of financial aid are available to students?");
            add("Our university offers a variety of financial aid options, including scholarships, grants, loans, and work-study programs. Please visit our website for more information.");
        }});

        faqArrayList.add(new ArrayList<String>() {{
            add("What are the housing options for students?");
            add("Our university offers a variety of on-campus housing options, including traditional dorms, apartments, and suites. We also have a number of off-campus housing options available.");
        }});

        faqArrayList.add(new ArrayList<String>() {{
            add("What are the dining options on campus?");
            add("Our university has a variety of dining options on campus, including a dining hall, a food court, and a number of restaurants and cafes.");
        }});
    }


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_query, container, false);

        String whichLayoutToShow = SharedPreferenceClass.getString(getContext(),"WhichLayoutToShow","FAQ");


        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView_FAQ = rootView.findViewById(R.id.RV_faq);

        txt_faqBTN = rootView.findViewById(R.id.tv_FAQ);
        txt_queryBTN = rootView.findViewById(R.id.tv_query);

        lottieAnimationView_faq = rootView.findViewById(R.id.LF_animation);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView_FAQ.setLayoutManager(layoutManager2);

        FAQsAdapter adapter = new FAQsAdapter(faqArrayList);

        recyclerView_FAQ.setAdapter(adapter);

        if(whichLayoutToShow.equals("FAQ")){
            txt_faqBTN.setTextColor(Color.WHITE);
            txt_faqBTN.setBackgroundResource(R.drawable.bg_topbar);
            lottieAnimationView_faq.setVisibility(View.VISIBLE);

            txt_queryBTN.setTextColor(Color.BLACK);
            txt_queryBTN.setBackgroundResource(R.drawable.bg_topbar_white);

            recyclerView_FAQ.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }else{
            lottieAnimationView_faq.setVisibility(View.GONE);

            txt_queryBTN.setTextColor(Color.WHITE);
            txt_queryBTN.setBackgroundResource(R.drawable.bg_topbar);

            recyclerView.setVisibility(View.VISIBLE);
            recyclerView_FAQ.setVisibility(View.GONE);
        }


        txt_faqBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferenceClass.saveString(getContext(),"WhichLayoutToShow","FAQ");
                txt_faqBTN.setTextColor(Color.WHITE);
                txt_faqBTN.setBackgroundResource(R.drawable.bg_topbar);
                lottieAnimationView_faq.setVisibility(View.VISIBLE);

                txt_queryBTN.setTextColor(Color.BLACK);
                txt_queryBTN.setBackgroundResource(R.drawable.bg_topbar_white);

                recyclerView_FAQ.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);

            }
        });

        txt_queryBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferenceClass.saveString(getContext(),"WhichLayoutToShow","QUERY");
                txt_faqBTN.setTextColor(Color.BLACK);
                txt_faqBTN.setBackgroundResource(R.drawable.bg_topbar_white);
                lottieAnimationView_faq.setVisibility(View.GONE);

                txt_queryBTN.setTextColor(Color.WHITE);
                txt_queryBTN.setBackgroundResource(R.drawable.bg_topbar);

                recyclerView.setVisibility(View.VISIBLE);
                recyclerView_FAQ.setVisibility(View.GONE);

            }
        });

        firebaseFirestore.collection("Query")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    postList.clear();
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                        ConfessionAsPost document =  documentSnapshot.toObject(ConfessionAsPost.class);
                        document.setUserConfessionDocumentID(documentSnapshot.getId().toString());
                        postList.add(document);
                    }

                    postAdapter = new PostAdapter(rootView.getContext(), postList);
                    recyclerView.setAdapter(postAdapter);
                    postAdapter.setOnCommentClickListener(new OnCommentClickListener() {
                        @Override
                        public void onCommentClick(int position) {
                            lastVisitedRecylerView = R.id.recyclerView;

                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            transaction.replace(R.id.frameLayout, new CommentFragment());
                            transaction.addToBackStack(null);
                            transaction.commit();
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(),"Some error occurred ....",Toast.LENGTH_SHORT).show();
                });


        return rootView;
    }

}