package com.ai.app;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LegalTabsActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
//        setContentView(R.layout.activity_app_info_and_disclaimer);
        setContentView(R.layout.activity_legal_tabs);

        TabHost tabHost = findViewById(android.R.id.tabhost);
        tabHost.setup();

        // Create the "Info" tab
        TabHost.TabSpec tabInfo = tabHost.newTabSpec("Info");
        tabInfo.setIndicator("Info & Contact");
        tabInfo.setContent(R.id.Info);
        tabHost.addTab(tabInfo);

        // Create the "Disclaimer" tab
        TabHost.TabSpec tabDisclaimer = tabHost.newTabSpec("Policy");
        tabDisclaimer.setIndicator("Policy & Terms");
        tabDisclaimer.setContent(R.id.Policy);
        tabHost.addTab(tabDisclaimer);

        tabHost.setCurrentTab(0); // Set the default tab
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.app_info:
                new StartActivityTask().execute(LegalTabsActivity.class);
                finish();
                return true;
            case R.id.user_guide:
                // Handle the menu item click
                new StartActivityTask().execute(GuideActivity.class);
                finish();
                return true;
            case R.id.home:
                new StartActivityTask().execute(MainActivity.class);
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
    private class StartActivityTask extends AsyncTask<Class<?>, Void, Void> {
        @Override
        protected Void doInBackground(Class<?>... classes) {
            Intent intent = new Intent(getApplicationContext(), classes[0]);
            startActivity(intent);
            return null;
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

