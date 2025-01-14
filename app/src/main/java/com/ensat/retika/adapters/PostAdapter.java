/**
 * Adapter for displaying a list of posts in a RecyclerView.
 * Handles different layouts and interactions like likes, comments, and follows.
 */
package com.ensat.retika.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ensat.retika.R;
import com.ensat.retika.models.Comment;
import com.ensat.retika.models.Post;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private final List<Post> postList;
    private final int layoutResourceId;
    private final String currentUserId;
    private final FirebaseFirestore db;
    private final Map<String, String> usernameCache;
    private final Context context;

    /**
     * Constructor for the PostAdapter.
     *
     * @param postList         List of posts to display.
     * @param layoutResourceId Layout resource ID for the post item.
     * @param currentUserId    ID of the currently logged-in user.
     * @param context          Context for accessing resources.
     */
    public PostAdapter(List<Post> postList, int layoutResourceId, String currentUserId, Context context) {
        this.postList = postList;
        this.layoutResourceId = layoutResourceId;
        this.currentUserId = currentUserId;
        this.db = FirebaseFirestore.getInstance();
        this.usernameCache = new HashMap<>();
        this.context = context;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResourceId, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);

        // Load post image
        Glide.with(context)
                .load(post.getImageUrl())
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.imageView);

        if (layoutResourceId == R.layout.item_post) {
            fetchAndSetUsername(holder.textUsername, post.getUserId());

            if (holder.textCaption != null) {
                holder.textCaption.setVisibility(View.VISIBLE);
                holder.textCaption.setText(post.getCaption());
            }

            if (holder.buttonLike != null) {
                handleLikeButton(holder.buttonLike, post);
            }

            if (holder.buttonFollow != null) {
                handleFollowButton(holder.buttonFollow, post.getUserId());
            }

            if (holder.buttonComment != null) {
                holder.buttonComment.setVisibility(View.VISIBLE);
                holder.buttonComment.setOnClickListener(v -> openCommentDialog(post));
            }

            if (holder.recyclerViewComments != null) {
                bindComments(holder.recyclerViewComments, post);
            }
        } else if (layoutResourceId == R.layout.item_post_grid) {
            // Handle grid layout visibility
            if (holder.textUsername != null) holder.textUsername.setVisibility(View.GONE);
            if (holder.textCaption != null) holder.textCaption.setVisibility(View.GONE);
            if (holder.buttonLike != null) holder.buttonLike.setVisibility(View.GONE);
            if (holder.buttonComment != null) holder.buttonComment.setVisibility(View.GONE);
            if (holder.buttonFollow != null) holder.buttonFollow.setVisibility(View.GONE);
            if (holder.recyclerViewComments != null) holder.recyclerViewComments.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    /**
     * Fetches and sets the username for a given user ID.
     *
     * @param textUsername TextView to display the username.
     * @param userId       ID of the user.
     */
    private void fetchAndSetUsername(TextView textUsername, String userId) {
        if (usernameCache.containsKey(userId)) {
            textUsername.setText(usernameCache.get(userId));
        } else {
            db.collection("users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String username = documentSnapshot.getString("username");
                            usernameCache.put(userId, username);
                            textUsername.setText(username);
                        } else {
                            textUsername.setText("Unknown User");
                        }
                    })
                    .addOnFailureListener(e -> textUsername.setText("Error Loading User"));
        }
    }

    /**
     * Handles the like button functionality for a post.
     *
     * @param buttonLike Button to toggle likes.
     * @param post       Post object being liked/unliked.
     */
    private void handleLikeButton(Button buttonLike, Post post) {
        if (post.getLikedBy() == null) {
            post.setLikedBy(new ArrayList<>());
        }

        if (post.getLikedBy().contains(currentUserId)) {
            buttonLike.setText("Unlike");
        } else {
            buttonLike.setText("Like");
        }

        buttonLike.setOnClickListener(v -> {
            List<String> likedBy = post.getLikedBy();
            if (likedBy.contains(currentUserId)) {
                likedBy.remove(currentUserId);
                buttonLike.setText("Like");
            } else {
                likedBy.add(currentUserId);
                buttonLike.setText("Unlike");
            }

            if (post.getPostId() != null) {
                db.collection("posts").document(post.getPostId())
                        .update("likedBy", likedBy)
                        .addOnFailureListener(e -> e.printStackTrace());
            }
        });
    }

    /**
     * Handles the follow button functionality for a user.
     *
     * @param buttonFollow Button to toggle follow status.
     * @param userId       ID of the user being followed/unfollowed.
     */
    private void handleFollowButton(Button buttonFollow, String userId) {
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> followers = (List<String>) documentSnapshot.get("followers");
                        if (followers != null && followers.contains(currentUserId)) {
                            buttonFollow.setText("Following");
                        } else {
                            buttonFollow.setText("Follow");
                        }
                    }
                });

        buttonFollow.setOnClickListener(v -> {
            db.collection("users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            List<String> followers = (List<String>) documentSnapshot.get("followers");
                            if (followers == null) followers = new ArrayList<>();
                            if (followers.contains(currentUserId)) {
                                followers.remove(currentUserId);
                                buttonFollow.setText("Follow");
                            } else {
                                followers.add(currentUserId);
                                buttonFollow.setText("Following");
                            }

                            db.collection("users").document(userId)
                                    .update("followers", followers)
                                    .addOnFailureListener(e -> {
                                        // Handle failure
                                    });
                        }
                    });
        });
    }

    /**
     * Opens a dialog to add a comment to a post.
     *
     * @param post Post object to add the comment to.
     */
    private void openCommentDialog(Post post) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        builder.setTitle("Add a Comment");

        final android.widget.EditText input = new android.widget.EditText(context);
        input.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Post", (dialog, which) -> {
            String commentText = input.getText().toString().trim();
            if (!android.text.TextUtils.isEmpty(commentText)) {
                addCommentToPost(post, commentText);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    /**
     * Adds a comment to a post and updates it in Firestore.
     *
     * @param post        Post object to add the comment to.
     * @param commentText Text of the comment.
     */
    private void addCommentToPost(Post post, String commentText) {
        String commentId = db.collection("comments").document().getId();
        Comment comment = new Comment(commentId, currentUserId, commentText, System.currentTimeMillis());

        if (post.getComments() == null) {
            post.setComments(new ArrayList<>());
        }
        post.getComments().add(comment);

        db.collection("posts").document(post.getPostId())
                .update("comments", post.getComments())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Comment added!", Toast.LENGTH_SHORT).show();
                    notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to add comment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * Binds the comments for a post to a RecyclerView.
     *
     * @param recyclerViewComments RecyclerView to display comments.
     * @param post                 Post object containing the comments.
     */
    private void bindComments(RecyclerView recyclerViewComments, Post post) {
        if (post.getComments() != null && !post.getComments().isEmpty()) {
            CommentAdapter commentAdapter = new CommentAdapter(post.getComments(), context);
            recyclerViewComments.setAdapter(commentAdapter);
            recyclerViewComments.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerViewComments.setAdapter(null);
        }
    }

    /**
     * ViewHolder class for holding post item views.
     */
    public static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textCaption, textUsername;
        Button buttonLike, buttonFollow, buttonComment;
        RecyclerView recyclerViewComments;

        /**
         * Constructor for PostViewHolder.
         *
         * @param itemView View representing a single post item.
         */
        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.post_image);
            textCaption = itemView.findViewById(R.id.text_caption);
            textUsername = itemView.findViewById(R.id.text_username);
            buttonLike = itemView.findViewById(R.id.button_like);
            buttonComment = itemView.findViewById(R.id.button_comment);
            buttonFollow = itemView.findViewById(R.id.button_follow);
            recyclerViewComments = itemView.findViewById(R.id.recycler_view_comments);
        }
    }
}