package com.ai.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class GuideActivity extends AppCompatActivity {
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_guide);
    }



    /* menu code */
    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */

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