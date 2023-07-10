package com.ai.app;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword;
    Button buttonReg;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView;
    TextView wrongPass;

    private class StartActivityTask extends AsyncTask<Class<?>, Void, Void> {
        @Override
        protected Void doInBackground(Class<?>... classes) {
            Intent intent = new Intent(getApplicationContext(), classes[0]);
            startActivity(intent);
            return null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            new StartActivityTask().execute(MainActivity.class);

            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.app_info:
//                intent = new Intent(this, LegalTabsActivity.class);
//                startActivity(intent);
                new StartActivityTask().execute(LegalTabsActivity.class);

                return true;
            case R.id.home:
//                intent = new Intent(getApplicationContext(), MainActivity.class);
//                startActivity(intent);
                new StartActivityTask().execute(MainActivity.class);

                finish();
                return true;

            case R.id.user_guide:
                // Handle the menu item click
//                Toast.makeText(this, "Menu Item user guide Clicked", Toast.LENGTH_SHORT).show();
//                intent = new Intent(getApplicationContext(), GuideActivity.class);
//                startActivity(intent);
                new StartActivityTask().execute(GuideActivity.class);

                return true;
            case R.id.achievements_page:
//                intent = new Intent(getApplicationContext(), AchievementsActivity.class);
//                startActivity(intent);
                new StartActivityTask().execute(AchievementsActivity.class);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem userProfileItem = menu.findItem(R.id.user_profile);
        MenuItem userAchivmentsItem = menu.findItem(R.id.achievements_page);
        if (mAuth.getCurrentUser() == null) {
            userProfileItem.setVisible(false);
            userAchivmentsItem.setVisible(false);

        }
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonReg = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progressBar);
        wrongPass = findViewById(R.id.wrongPass);
        textView = findViewById(R.id.loginNow);
        textView.setOnClickListener(v -> {
//            Intent intent = new Intent(getApplicationContext(), Login.class);
//            startActivity(intent);
            new StartActivityTask().execute(Login.class);

        });


        buttonReg.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            wrongPass.setVisibility(View.GONE);
            String email , password;
            email = String.valueOf(editTextEmail.getText());
            password = String.valueOf(editTextPassword.getText());

            // checking if the email and password are empty
            if(TextUtils.isEmpty(email))
            {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(Register.this, "⚠ Enter email", Toast.LENGTH_SHORT).show();
                return;
            }
            if(TextUtils.isEmpty(password))
            {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(Register.this, "⚠ Enter password", Toast.LENGTH_SHORT).show();
                return;
            }
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(Register.this, "Account created!",
                                    Toast.LENGTH_SHORT).show();

                            // TODO:  method to add an awards array to user in DB
                            // Add user to the database
                            FirebaseUser user = mAuth.getCurrentUser();
                            addUserToDatabase(user);
//                            Intent intent = new Intent(getApplicationContext(), Login.class);
//                            startActivity(intent);
                            new StartActivityTask().execute(Login.class);

                        } else {
                            // If sign in fails, display a message to the user.
                            progressBar.setVisibility(View.GONE);
                            wrongPass.setVisibility(View.VISIBLE);
                            Toast.makeText(Register.this, "⛒ Authentication failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
    private void addUserToDatabase(FirebaseUser user) {
        String email = user.getEmail();
        String userID = user.getUid();
        String username = email.substring(0, email.indexOf("@"));

        // Create a new User object
        User newUser = new User(userID, email,null,null);

        // Add the user to the database
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
        usersRef.child(username).setValue(newUser)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "User added to the database");
                        // Add a node for fights
                        DatabaseReference fightsRef = usersRef.child(username).child("fights");
                        fightsRef.setValue(null)
                                .addOnCompleteListener(fightsTask -> {
                                    if (fightsTask.isSuccessful()) {
                                        Log.d(TAG, "Fights node added for the user");
                                    } else {
                                        Log.e(TAG, "Failed to add fights node for the user");
                                    }
                                });

                        // Add a node for achievements
                        DatabaseReference achievementsRef = usersRef.child(username).child("achievements");
                        achievementsRef.setValue(null)
                                .addOnCompleteListener(achievementsTask -> {
                                    if (achievementsTask.isSuccessful()) {
                                        Log.d(TAG, "Achievements node added for the user");
                                    } else {
                                        Log.e(TAG, "Failed to add achievements node for the user");
                                    }
                                });
                    } else {
                        Log.e(TAG, "Failed to add user to the database");
                    }
                });
    }


}