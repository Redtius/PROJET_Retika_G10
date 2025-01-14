package com.ensat.retika;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Activity for user login.
 * This activity allows users to log in using their email and password.
 * If the login is successful, the user is redirected to the main activity.
 * If the user does not have an account, they can navigate to the signup activity.
 */
public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth; // Firebase Authentication instance
    private EditText emailInput, passwordInput; // Input fields for email and password
    private Button loginButton; // Button to trigger login
    private TextView signupText; // TextView to navigate to the signup activity

    /**
     * Called when the activity is starting. This is where most initialization should go:
     * calling setContentView(int) to inflate the activity's UI, initializing fields, and
     * setting up event listeners.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously
     *                           being shut down, this Bundle contains the data it most
     *                           recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI components
        emailInput = findViewById(R.id.input_email);
        passwordInput = findViewById(R.id.input_password);
        loginButton = findViewById(R.id.button_login);
        signupText = findViewById(R.id.text_signup);

        // Set click listener for the login button
        loginButton.setOnClickListener(view -> loginUser());

        // Set click listener for the signup text
        signupText.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Handles the user login process.
     * This method validates the email and password input fields, then attempts to log in
     * the user using Firebase Authentication. If the login is successful, the user is
     * redirected to the main activity. If the login fails, an error message is displayed.
     */
    private void loginUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // Validate input fields
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter email and password.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Attempt to log in with Firebase Authentication
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Login successful
                        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish(); // Close the login activity
                    } else {
                        // Login failed
                        Toast.makeText(this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}