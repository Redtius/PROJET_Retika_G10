/**
 * Fragment class for the Profile screen.
 * Displays the user's profile information, including followers, likes, and posts.
 */
package com.ensat.retika.ui.other;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ensat.retika.ChangePasswordActivity;
import com.ensat.retika.LoginActivity;
import com.ensat.retika.R;
import com.ensat.retika.adapters.PostAdapter;
import com.ensat.retika.models.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    private RecyclerView recyclerViewPosts; // RecyclerView for displaying user's posts
    private TextView textFollowers, textLikes, textUsername;
    private ImageView profileImage;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private List<Post> postList;
    private PostAdapter postAdapter;
    private String currentUserId;

    /**
     * Called when the fragment is created.
     * Initializes Firebase authentication and database instances.
     *
     * @param savedInstanceState Saved state of the fragment.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        setHasOptionsMenu(true); // Enable options menu
    }

    /**
     * Creates and returns the view hierarchy for the fragment.
     *
     * @param inflater           LayoutInflater to inflate views in the fragment.
     * @param container          Parent view that the fragment's UI should attach to.
     * @param savedInstanceState Saved state of the fragment.
     * @return Root view of the fragment.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        // Toolbar setup
        Toolbar toolbar = root.findViewById(R.id.toolbar);
        toolbar.setOnMenuItemClickListener(this::onMenuItemClick);

        // Initialize views
        textFollowers = root.findViewById(R.id.text_followers_count);
        textLikes = root.findViewById(R.id.text_likes_count);
        textUsername = root.findViewById(R.id.text_username);
        profileImage = root.findViewById(R.id.profile_image);
        recyclerViewPosts = root.findViewById(R.id.recycler_view_posts);

        // Retrieve current user ID
        if (mAuth.getCurrentUser() != null) {
            currentUserId = mAuth.getCurrentUser().getUid();
        } else {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return root; // Exit if no user is logged in
        }

        // Configure RecyclerView
        recyclerViewPosts.setLayoutManager(new GridLayoutManager(requireContext(), 3)); // 3 columns
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(postList, R.layout.item_post_grid, currentUserId, requireContext());
        recyclerViewPosts.setAdapter(postAdapter);

        // Load user profile and posts
        loadUserProfile();
        loadUserPosts();

        return root;
    }

    /**
     * Inflates the options menu in the toolbar.
     *
     * @param menu     Menu instance to inflate.
     * @param inflater MenuInflater to inflate the menu layout.
     */
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.profile_menu, menu); // Inflate the menu here
        super.onCreateOptionsMenu(menu, inflater);
    }

    /**
     * Loads the user's profile information from Firestore.
     * Updates username, followers, profile picture, and likes.
     */
    private void loadUserProfile() {
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        textUsername.setText(documentSnapshot.getString("username"));

                        // Ensure followers are correctly retrieved
                        List<String> followers = (List<String>) documentSnapshot.get("followers");
                        long followersCount = (followers != null) ? followers.size() : 0;
                        textFollowers.setText(String.valueOf(followersCount));

                        // Load profile image using Glide
                        String profilePictureUrl = documentSnapshot.getString("profilePictureUrl");
                        if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
                            Glide.with(requireContext())
                                    .load(profilePictureUrl)
                                    .placeholder(R.drawable.ic_baseline_person_24) // Default placeholder
                                    .into(profileImage);
                        }

                        // Calculate likes dynamically
                        calculateTotalLikes(userId);
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle Firestore retrieval errors
                    textUsername.setText("Error");
                    textFollowers.setText("0");
                    textLikes.setText("0");
                });
    }

    /**
     * Calculates the total number of likes for the user's posts.
     * Updates the likes count in the UI.
     *
     * @param userId ID of the current user.
     */
    private void calculateTotalLikes(String userId) {
        db.collection("posts").whereEqualTo("userId", userId).get()
                .addOnSuccessListener(querySnapshot -> {
                    int totalLikes = 0;
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Post post = doc.toObject(Post.class);
                        if (post.getLikedBy() != null) {
                            totalLikes += post.getLikedBy().size();
                        }
                    }
                    textLikes.setText(String.valueOf(totalLikes));
                })
                .addOnFailureListener(e -> {
                    textLikes.setText("0");
                });
    }

    /**
     * Loads the user's posts from Firestore and displays them in the RecyclerView.
     */
    private void loadUserPosts() {
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("posts").whereEqualTo("userId", userId).get()
                .addOnSuccessListener(querySnapshot -> {
                    postList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        postList.add(doc.toObject(Post.class));
                    }
                    postAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                });
    }

    /**
     * Handles menu item clicks in the toolbar.
     *
     * @param item Selected menu item.
     * @return True if the item was handled, false otherwise.
     */
    private boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.action_change_password) {
            // Launch Change Password Activity
            startActivity(new Intent(requireContext(), ChangePasswordActivity.class));
            return true;
        } else if (item.getItemId() == R.id.action_logout) {
            // Log out the user
            mAuth.signOut();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return true;
        }
        return false;
    }
}
