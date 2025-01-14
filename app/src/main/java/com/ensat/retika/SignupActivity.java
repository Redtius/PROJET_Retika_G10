package com.ensat.retika;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ensat.retika.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;
/**
 * SignupActivity is an Android activity that allows users to sign up for the application.
 * It provides functionality for users to input their email, password, and username, and optionally upload a profile photo.
 * The activity integrates with Firebase Authentication, Firestore, and Firebase Storage to handle user registration,
 * data storage, and profile photo uploads.
 *
 * <p>
 * The activity includes the following features:
 * <ul>
 *     <li>User registration using email and password via Firebase Authentication.</li>
 *     <li>Optional profile photo upload to Firebase Storage.</li>
 *     <li>Storing user data (username, email, profile photo URL) in Firestore.</li>
 *     <li>Validation of required fields (email and password).</li>
 * </ul>
 * </p>
 *
 * <p>
 * The activity uses the following Firebase services:
 * <ul>
 *     <li>{@link FirebaseAuth} for user authentication.</li>
 *     <li>{@link FirebaseFirestore} for storing user data.</li>
 *     <li>{@link FirebaseStorage} for storing profile photos.</li>
 * </ul>
 * </p>
 *
 * @author Reda Mountassir
 * @version 1.0
 * @see AppCompatActivity
 * @see FirebaseAuth
 * @see FirebaseFirestore
 * @see FirebaseStorage
 */
public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth mAuth; // Firebase Authentication instance
    private FirebaseFirestore db; // Firestore database instance
    private StorageReference storageReference; // Firebase Storage reference for profile photos

    private EditText inputEmail; // EditText for user email input
    private EditText inputPassword; // EditText for user password input
    private EditText inputUsername; // EditText for user username input
    private Button buttonUploadPhoto; // Button to trigger photo upload
    private Button buttonSignUp; // Button to trigger user sign-up
    private Uri photoUri; // URI of the selected profile photo

    private static final int PICK_IMAGE_REQUEST = 1; // Request code for image selection

    /**
     * Called when the activity is first created. Initializes the activity layout, Firebase services,
     * and sets up event listeners for the UI components.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied in onSaveInstanceState(Bundle).
     *                           Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase services
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("profile_photos");

        // Find views
        inputEmail = findViewById(R.id.input_email);
        inputPassword = findViewById(R.id.input_password);
        inputUsername = findViewById(R.id.input_name); // Assuming "input_name" is now used for username
        buttonUploadPhoto = findViewById(R.id.button_upload_photo);
        buttonSignUp = findViewById(R.id.button_signup);

        // Photo upload button
        buttonUploadPhoto.setOnClickListener(v -> openFileChooser());

        // Sign-up button
        buttonSignUp.setOnClickListener(v -> signUpUser());
    }

    /**
     * Opens a file chooser to allow the user to select a profile photo.
     * The selected photo is stored in the {@link #photoUri} variable.
     */
    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    /**
     * Handles the result of the file chooser activity. If an image is successfully selected,
     * the URI of the image is stored in {@link #photoUri}.
     *
     * @param requestCode The integer request code originally supplied to startActivityForResult(),
     *                    allowing you to identify who this result came from.
     * @param resultCode The integer result code returned by the child activity through its setResult().
     * @param data An Intent, which can return result data to the caller (various data can be attached to Intent "extras").
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            photoUri = data.getData();
            Toast.makeText(this, "Photo selected!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handles the user sign-up process. Validates the input fields and creates a new user
     * using Firebase Authentication. If successful, the user's data is saved to Firestore.
     */
    private void signUpUser() {
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        String username = inputUsername.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email and password are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create the user with email and password
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    saveUserData(user.getUid(), username, email);
                }
            } else {
                Toast.makeText(SignupActivity.this, "Sign up failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Saves the user's data to Firestore. If a profile photo was selected, it is uploaded to Firebase Storage,
     * and the download URL is saved along with the user's data.
     *
     * @param userId The unique ID of the user generated by Firebase Authentication.
     * @param username The username entered by the user.
     * @param email The email entered by the user.
     */
    private void saveUserData(String userId, String username, String email) {
        final AtomicReference<String> profilePictureUrl = new AtomicReference<>(null); // Use AtomicReference

        // If photo selected, upload it
        if (photoUri != null) {
            StorageReference fileRef = storageReference.child(userId + ".jpg");
            fileRef.putFile(photoUri).addOnSuccessListener(taskSnapshot ->
                    fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        profilePictureUrl.set(uri.toString());
                        saveUserToFirestore(userId, username, email, profilePictureUrl.get());
                    })
            ).addOnFailureListener(e ->
                    Toast.makeText(SignupActivity.this, "Failed to upload photo: " + e.getMessage(), Toast.LENGTH_SHORT).show()
            );
        } else {
            // Save without a profile photo
            saveUserToFirestore(userId, username, email, profilePictureUrl.get());
        }
    }

    /**
     * Saves the user's data to Firestore. This includes the user's ID, username, email, and profile photo URL.
     *
     * @param userId The unique ID of the user generated by Firebase Authentication.
     * @param username The username entered by the user.
     * @param email The email entered by the user.
     * @param profilePictureUrl The URL of the user's profile photo, or null if no photo was uploaded.
     */
    private void saveUserToFirestore(String userId, String username, String email, String profilePictureUrl) {
        // Create a User object
        User user = new User(userId, username, email, profilePictureUrl, 0);
        user.setFollowers(new ArrayList<>());
        user.setFollowing(new ArrayList<>());

        // Save to Firestore
        db.collection("users").document(userId).set(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(SignupActivity.this, "Sign up successful!", Toast.LENGTH_SHORT).show();
                finish(); // Close the activity
            } else {
                Toast.makeText(SignupActivity.this, "Failed to save user: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}