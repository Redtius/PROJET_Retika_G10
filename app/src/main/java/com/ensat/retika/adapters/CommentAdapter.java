/**
 * Adapter for displaying a list of comments in a RecyclerView.
 */
package com.ensat.retika.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ensat.retika.R;
import com.ensat.retika.models.Comment;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private final List<Comment> commentList;
    private final Context context;

    /**
     * Constructor for the CommentAdapter.
     *
     * @param commentList List of comments to display.
     * @param context     Context for accessing resources.
     */
    public CommentAdapter(List<Comment> commentList, Context context) {
        this.commentList = commentList;
        this.context = context;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.textComment.setText(comment.getCommentText());
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    /**
     * ViewHolder class for holding comment item views.
     */
    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView textComment;

        /**
         * Constructor for CommentViewHolder.
         *
         * @param itemView View representing a single comment item.
         */
        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            textComment = itemView.findViewById(R.id.text_comment);
        }
    }
}
