package com.ai.app;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.database.collection.LLRBNode;


public class Login extends AppCompatActivity {
    TextInputEditText editTextEmail, editTextPassword;
    Button buttonLogin;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView;
    TextView forgotPassword ;
    boolean user;


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), Register.class);
            startActivity(intent);
            finish();
        }
        else {
            user = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        forgotPassword = findViewById(R.id.forgot_password);
        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.registerNow);

        textView.setOnClickListener(view -> {
//            Intent intent = new Intent(getApplicationContext(), Register.class);
//            startActivity(intent);
            new StartActivityTask().execute(Register.class);

        });
        forgotPassword.setOnClickListener(v -> startActivity(new Intent(Login.this, ResetPasswordActivity.class)));

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String email , password;
                email = String.valueOf(editTextEmail.getText());
                password = String.valueOf(editTextPassword.getText());

                // checking if the email and password are empty
                if(TextUtils.isEmpty(email))
                {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Login.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(password))
                {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Login.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(Login.this, "Logging Successful", Toast.LENGTH_SHORT).show();
//                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                                    startActivity(intent);
                                    new StartActivityTask().execute(MainActivity.class);
                                    finish();
                                } else {
                                    progressBar.setVisibility(View.GONE);
                                    if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                        // Invalid email address
                                        Toast.makeText(Login.this, "⚠ Invalid email address", Toast.LENGTH_LONG).show();
                                    } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                        // Invalid password
                                        Toast.makeText(Login.this, "⚠ Invalid password", Toast.LENGTH_LONG).show();
                                    } else {
                                        // Other authentication failure
                                        Toast.makeText(Login.this, "⛒ Authentication failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });

            }
        });
    }
    private class StartActivityTask extends AsyncTask<Class<?>, Void, Void> {
        @Override
        protected Void doInBackground(Class<?>... classes) {
            Intent intent = new Intent(getApplicationContext(), classes[0]);
            startActivity(intent);
            return null;
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        Intent intent;
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

}
