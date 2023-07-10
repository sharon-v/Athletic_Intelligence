package com.ai.app;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

//import com.chaquo.python.PyObject;
//import com.chaquo.python.Python;
//import com.chaquo.python.android.AndroidPlatform;
//import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

//import com.ai.app.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    Button button_logout;
    Button button_training;
    Button button_fight;
    TextView textView;
    FirebaseUser user;


    @Override
    public void onStart() {
        super.onStart();
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
                return true;
            case R.id.user_guide:
                // Handle the menu item click
                new StartActivityTask().execute(GuideActivity.class);
                return true;
            case R.id.home:
                Toast.makeText(this, "You are already on the Home Page", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.user_profile:
                new StartActivityTask().execute(UserProfileActivity.class);
                return true;
            case R.id.achievements_page:
                new StartActivityTask().execute(AchievementsActivity.class);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auth = FirebaseAuth.getInstance();
        button_logout = findViewById(R.id.logout);
        button_training = findViewById(R.id.training);
        button_fight = findViewById(R.id.fight);
        textView = findViewById(R.id.user_details);
        user = auth.getCurrentUser();
        if (user == null) {
            new StartActivityTask().execute(Login.class);
            finish();
        } else {
            String email = user.getEmail();
            String username = email.substring(0, email.indexOf("@"));
            String text = String.format("Hello %s", username);
            textView.setText(text);

        }
        button_training.setOnClickListener(v -> {
            new StartActivityTask().execute(TrainingActivity.class);
        });
        button_logout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            new StartActivityTask().execute(Login.class);
            finish();
        });
        button_fight.setOnClickListener(v -> {
            new StartActivityTask().execute(FightActivity.class);
        });

    }
}