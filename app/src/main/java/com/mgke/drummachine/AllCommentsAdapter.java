package com.mgke.drummachine;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mgke.drummachine.model.Comment;

import java.util.List;

public class AllCommentsAdapter extends RecyclerView.Adapter<AllCommentsAdapter.CommentViewHolder> {

    private List<Comment> comments;
    private final OnDeleteClickListener onDeleteClickListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(String commentId);
    }

    public AllCommentsAdapter(List<Comment> comments, OnDeleteClickListener onDeleteClickListener) {
        this.comments = comments;
        this.onDeleteClickListener = onDeleteClickListener;
    }

    public void updateComments(List<Comment> comments) {
        this.comments = comments;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_all_comments, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);

        holder.commentText.setText(comment.getContent());
        holder.deleteButton.setOnClickListener(v -> onDeleteClickListener.onDeleteClick(comment.getId()));
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView commentText;
        Button deleteButton;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            commentText = itemView.findViewById(R.id.comment_text);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }
}
