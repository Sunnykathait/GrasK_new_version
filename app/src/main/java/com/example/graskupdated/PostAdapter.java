package com.example.graskupdated;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<ConfessionAsPost> ConfessionList;
    private Context context;
    private OnCommentClickListener commentClickListener;

    public PostAdapter(Context context, List<ConfessionAsPost> postList) {
        this.context = context;
        this.ConfessionList = postList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_layout, parent, false);
        return new PostViewHolder(view);
    }

    public void setOnCommentClickListener(OnCommentClickListener listener){
        this.commentClickListener = listener;
    }


    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ConfessionAsPost post = ConfessionList.get(position);
        holder.bind(post);
        holder.reportImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "This post has been reported to the admin, he will look at this.",Toast.LENGTH_SHORT).show();
            }
        });
        holder.commentImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(commentClickListener != null){
                    SharedPreferenceClass.saveString(v.getContext(), "userNameToBeShownForTheComment",holder.usernameTextView.getText().toString());
                    SharedPreferenceClass.saveString(v.getContext(),"userCommentToBeShownFroTheComment",holder.contentTextView.getText().toString());
                    SharedPreferenceClass.saveString(v.getContext(),"userPostDocumentID",post.userConfessionDocumentID);
                    commentClickListener.onCommentClick(position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return ConfessionList.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {

        private ImageView profileImageView;
        private TextView usernameTextView, contentTextView, date;
        private ImageView commentImageView, forwardImageView, reportImageView;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.profileImageView);
            usernameTextView = itemView.findViewById(R.id.usernameTextView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
            date = itemView.findViewById(R.id.txt_date);
            commentImageView = itemView.findViewById(R.id.commentImageView);
            forwardImageView = itemView.findViewById(R.id.forwardImageView);
            reportImageView = itemView.findViewById(R.id.reportImageView);

        }

        public void bind(ConfessionAsPost post) {
            date.setText(post.getDate());
            if(post.getShowIdentity().equals("Don't show")){
                usernameTextView.setText("Anonymous");
            }else{
                usernameTextView.setText(post.getUserName());
            }
            contentTextView.setText(post.getUserConfession());
            // Optionally set click listeners for buttons here
        }
    }
}