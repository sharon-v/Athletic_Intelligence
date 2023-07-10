package com.ai.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FightMenuActivity extends AppCompatActivity {
    FirebaseAuth auth;
    private DatabaseReference fightRef;


    @Override
    public void onBackPressed() {
        // call super to execute the default back button behavior
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fight_menu);
        auth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        String fightKey = intent.getStringExtra("fightKey");

        // Construct the DatabaseReference using the key value
        fightRef = FirebaseDatabase.getInstance().getReference().child("Fights").child(fightKey);
        // Retrieve the fight details from the DatabaseReference
        fightRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Get the fight details from the dataSnapshot
                    String name1 = dataSnapshot.child("name1").getValue(String.class);
                    String name2 = dataSnapshot.child("name2").getValue(String.class);
                    String exercise = dataSnapshot.child("exercise").getValue(String.class);
                    String amount = dataSnapshot.child("amount").getValue(String.class);

                    // Display the fight details in your UI
                    TextView textViewFightDetails = findViewById(R.id.textViewFightDetails);
                    textViewFightDetails.setText("Fight Details:\n" +
                            "Name 1: " + name1 + "\n" +
                            "Name 2: " + name2 + "\n" +
                            "Exercise: " + exercise + "\n" +
                            "Amount: " + amount);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential errors
            }
        });
        Intent intent2 = new Intent(this, SquatActivity.class);
        intent2.putExtra("fightKey", fightKey);
        startActivity(intent2);

    }

    /* menu code */
    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
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