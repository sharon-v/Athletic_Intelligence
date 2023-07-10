package com.ai.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserProfileActivity extends AppCompatActivity {

    private TextView textView;
    private FirebaseUser user;
    private FirebaseAuth auth;
    private Button deleteButton;
    private class StartActivityTask extends AsyncTask<Class<?>, Void, Void> {
        @Override
        protected Void doInBackground(Class<?>... classes) {
            Intent intent = new Intent(getApplicationContext(), classes[0]);
            startActivity(intent);
            return null;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        textView = findViewById(R.id.user_name);
        deleteButton = findViewById(R.id.delete_user);

        if (user == null) {
            new StartActivityTask().execute(Login.class);
            finish();
        } else {
            String email = user.getEmail();
            String username = email.substring(0, email.indexOf("@"));
            textView.setText(username);
        }

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });
    }
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete User");
        builder.setMessage("Are you sure you want to permanently delete your account?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteCurrentUser(); // call the deleteCurrentUser() method to handle the deletion
            }
        });
        builder.setNegativeButton("No", null);
        builder.show();
    }

    // inside UserProfileActivity
    private void deleteCurrentUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Account");
        builder.setMessage("Enter your password to delete your account:");

        // set up the input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        // set up the buttons
        builder.setPositiveButton("Delete", (dialog, which) -> {
            String password = input.getText().toString().trim();

            // check if the password is empty
            if (TextUtils.isEmpty(password)) {
                Toast.makeText(UserProfileActivity.this, "Please enter your password", Toast.LENGTH_SHORT).show();
                return;
            }

            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);

            // re-authenticate the user
            user.reauthenticate(credential)
                    .addOnCompleteListener(reauthTask -> {
                        if (reauthTask.isSuccessful()) {
                            // user has been re-authenticated, proceed with deleting the account
                            user.delete()
                                    .addOnCompleteListener(deleteTask -> {
                                        if (deleteTask.isSuccessful()) {
                                            // account deleted successfully
                                            Toast.makeText(UserProfileActivity.this, "User deleted successfully", Toast.LENGTH_SHORT).show();
                                            // log out the user and redirect to the main page
                                            auth.signOut();
                                            new StartActivityTask().execute(MainActivity.class);
                                            finish();
                                        } else {
                                            // failed to delete the account
                                            Toast.makeText(UserProfileActivity.this, "Failed to delete user", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            // re-authentication failed
                            Toast.makeText(UserProfileActivity.this, "Incorrect password. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }



    private void deleteCurrentUserFromAuth() {
        // Delete the user from Firebase Authentication
        user.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // user deletion from Firebase Authentication is successful
                        // sign out the user and redirect to the main page
                        auth.signOut();
                        new StartActivityTask().execute(MainActivity.class);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // failed to delete the user from Firebase Authentication
                        Toast.makeText(UserProfileActivity.this, "Failed to delete user from authentication", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.app_info:
                new StartActivityTask().execute(LegalTabsActivity.class);
                finish();
                return true;
            case R.id.home:
                new StartActivityTask().execute(MainActivity.class);
                finish();
                return true;
            case R.id.user_guide:
                new StartActivityTask().execute(GuideActivity.class);
                finish();
                return true;
            case R.id.achievements_page:
                new StartActivityTask().execute(AchievementsActivity.class);
                finish();
                return true;
            case R.id.user_profile:
                new StartActivityTask().execute(UserProfileActivity.class);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
