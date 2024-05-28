package com.example.graskupdated;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class YourPostUserSettingAdapter extends RecyclerView.Adapter<YourPostUserSettingAdapter.ViewHolder> {

    private Context context;
    private ArrayList<ConfessionAsPost> dataList;
    private FragmentActivity activity;

    public YourPostUserSettingAdapter(Context context, ArrayList<ConfessionAsPost> dataList, FragmentActivity activity) {
        this.context = context;
        this.dataList = dataList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_in_user_setting_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ConfessionAsPost data = dataList.get(position);
        holder.textView.setText(data.userConfession);
        holder.txt_moreSettingPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                // Set the dialog title
                builder.setTitle("Post Options");

                // Set the dialog message
                builder.setMessage("Choose an action:");

                // Add the "Edit" option
                builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferenceClass.saveString(context.getApplicationContext(), "_documentToBeEditied", data.getUserConfessionDocumentID());
                        SharedPreferenceClass.saveString(context.getApplicationContext(), "_PostToBeEdited", data.getUserConfession());
                        SharedPreferenceClass.saveString(context.getApplicationContext(), "_identity", data.getShowIdentity());

                        openFragment(R.id.navigation_post);
                    }
                });


                builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("Query").document(data.getUserConfessionDocumentID())
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(context, "Post Deleted", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "Couldn't delete the post for right now, please try " +
                                                "again later", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });

                // Create and show the dialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView, txt_moreSettingPost;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.txt_userPostInUserSetting);
            txt_moreSettingPost = itemView.findViewById(R.id.txt_morePostSettings);
        }
    }

    private void openFragment(int itemID) {
        Fragment fragment = new CreateFragment();
        activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, fragment)
                .commit();

        BottomNavigationView bottomNavigationView = activity.findViewById(R.id.bottom_nav);
        bottomNavigationView.setSelectedItemId(itemID);

    }
}
