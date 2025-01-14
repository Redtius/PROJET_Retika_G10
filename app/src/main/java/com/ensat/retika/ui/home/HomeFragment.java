/**
 * Fragment class for the Home screen.
 * Displays a list of posts and supports pagination.
 */
package com.ensat.retika.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ensat.retika.R;
import com.ensat.retika.adapters.PostAdapter;
import com.ensat.retika.models.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerViewPosts; // RecyclerView for displaying posts
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private List<Post> postList;
    private PostAdapter postAdapter;
    private DocumentSnapshot lastVisiblePost; // Tracks the last visible post for pagination
    private boolean isLoading = false; // Indicates whether posts are currently loading
    private String currentUserId; // ID of the current user

    /**
     * Called when the fragment is created.
     * Initializes Firebase Firestore and Auth instances.
     *
     * @param savedInstanceState Saved state of the fragment.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
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
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        // Retrieve current user ID
        if (mAuth.getCurrentUser() != null) {
            currentUserId = mAuth.getCurrentUser().getUid();
        } else {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return root; // Exit if no user is logged in
        }

        // Initialize RecyclerView
        recyclerViewPosts = root.findViewById(R.id.recycler_view_posts);
        recyclerViewPosts.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Initialize post list and adapter
        postList = new ArrayList<>();
        postAdapter = new PostAdapter(postList, R.layout.item_post, currentUserId, requireContext());
        recyclerViewPosts.setAdapter(postAdapter);

        // Load posts
        loadPosts();

        // Pagination: Load more posts when scrolled to the bottom
        recyclerViewPosts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!recyclerView.canScrollVertically(1) && !isLoading) { // Check if at bottom
                    loadPosts();
                }
            }
        });

        return root;
    }

    /**
     * Loads posts from Firestore and updates the RecyclerView.
     * Handles pagination using the last visible post.
     */
    private void loadPosts() {
        if (lastVisiblePost == null) {
            postList.clear(); // Clear the list only when loading fresh posts
            postAdapter.notifyDataSetChanged(); // Notify adapter of changes
        }

        isLoading = true;

        Query query = db.collection("posts").orderBy("timestamp", Query.Direction.DESCENDING).limit(5);
        if (lastVisiblePost != null) {
            query = query.startAfter(lastVisiblePost);
        }

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                lastVisiblePost = queryDocumentSnapshots.getDocuments().get(queryDocumentSnapshots.size() - 1);

                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                    Post post = doc.toObject(Post.class);
                    if (post != null) {
                        post.setPostId(doc.getId()); // Set the postId using the document ID
                        postList.add(post);
                    }
                }
                postAdapter.notifyDataSetChanged();
            }
            isLoading = false;
        }).addOnFailureListener(e -> {
            Toast.makeText(requireContext(), "Failed to load posts: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            isLoading = false;
        });
    }

    /**
     * Called when the fragment becomes visible.
     * Resets pagination and reloads posts.
     */
    @Override
    public void onResume() {
        super.onResume();
        lastVisiblePost = null;
        loadPosts();
    }
}
