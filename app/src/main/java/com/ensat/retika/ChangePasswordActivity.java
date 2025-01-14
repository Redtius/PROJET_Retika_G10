package com.ensat.retika;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Activity for changing the user's password.
 * This activity allows the user to update their password by entering a new password
 * and confirming it. The new password is updated in Firebase Authentication.
 */
public class ChangePasswordActivity extends AppCompatActivity {

    private EditText inputNewPassword, inputConfirmPassword;
    private Button buttonUpdatePassword;

    private FirebaseAuth mAuth;

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
        setContentView(R.layout.activity_change_password);

        // Initialize UI components
        inputNewPassword = findViewById(R.id.input_new_password);
        inputConfirmPassword = findViewById(R.id.input_confirm_password);
        buttonUpdatePassword = findViewById(R.id.button_update_password);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Set click listener for the update password button
        buttonUpdatePassword.setOnClickListener(v -> updatePassword());
    }

    /**
     * Updates the user's password in Firebase Authentication.
     * This method validates the input fields, checks if the new password and confirm password
     * match, and then updates the password for the currently logged-in user.
     * Displays appropriate Toast messages for success or failure.
     */
    private void updatePassword() {
        String newPassword = inputNewPassword.getText().toString().trim();
        String confirmPassword = inputConfirmPassword.getText().toString().trim();

        // Validate input fields
        if (TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if passwords match
        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the current user
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Update the password in Firebase Authentication
            user.updatePassword(newPassword)
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(this, "Mot de passe mis à jour avec succès", Toast.LENGTH_SHORT).show()
                    )
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Échec de la mise à jour : " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        } else {
            // Handle case where the user is not logged in
            Toast.makeText(this, "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
        }
    }
}