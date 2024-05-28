package com.example.graskupdated;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Comments> commentList;
    private Context context;

    public CommentAdapter(Context context, List<Comments> postList) {
        this.context = context;
        this.commentList = postList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_layout, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Comments post = commentList.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {

        private ImageView profileImageView;
        private TextView CommenternameTextView, contentTextView;
        private ImageView commentImageView, forwardImageView, reportImageView;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            CommenternameTextView = itemView.findViewById(R.id.commenteruserName);
            contentTextView = itemView.findViewById(R.id.commenterTextView);
            commentImageView = itemView.findViewById(R.id.commenterPI);

        }

        public void bind(Comments post) {
            CommenternameTextView.setText(post.getUserName());
            contentTextView.setText(post.getUserComment());
            // Optionally set click listeners for buttons here
        }
    }
}