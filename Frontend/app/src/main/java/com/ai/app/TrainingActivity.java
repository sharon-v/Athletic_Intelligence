package com.ai.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class TrainingActivity extends AppCompatActivity {
    FirebaseAuth auth;
    Button button_squat;
    Button button_situp;

    EditText amountEditText;
    private class StartActivityTask extends AsyncTask<Class<?>, Void, Void> {
        @Override
        protected Void doInBackground(Class<?>... classes) {
            Intent intent = new Intent(getApplicationContext(), classes[0]);
            startActivity(intent);
            return null;
        }
    }
    @Override
    public void onBackPressed() {
        // call super to execute the default back button behavior
        super.onBackPressed();
        // finish the current activity to go back to the previous one
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
                // Handle the menu item click
                new StartActivityTask().execute(GuideActivity.class);
                finish();
                return true;
            case R.id.user_profile:
                new StartActivityTask().execute(UserProfileActivity.class);
                finish();
                return true;
            case R.id.achievements_page:
                new StartActivityTask().execute(AchievementsActivity.class);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);
        auth = FirebaseAuth.getInstance();
        button_squat = findViewById(R.id.squat);
        amountEditText = findViewById(R.id.amountEditText);

        button_squat.setOnClickListener(v -> {
            String amount = amountEditText.getText().toString();
            Intent intent = new Intent(getApplicationContext(), SquatActivity.class);
            intent.putExtra("counter", amount);
            startActivity(intent);
        });
        button_situp = findViewById(R.id.SitUp);
        button_situp.setOnClickListener(v -> {
            Toast.makeText(this, "Sorry , will be updated later", Toast.LENGTH_SHORT).show();
        });

    }
}