package com.ai.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ktx.Firebase;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Random;


public class FightActivity extends AppCompatActivity {
//    FirebaseAuth mAuth;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    Button generateCode;
    Button start;
    TextView textView_generateCode;
    TextInputEditText code;
    int lost = 0;
    int win = 0;
    FirebaseAuth auth;
    FirebaseUser user;
    String username;
    DatabaseReference fightRef;
    TextView lostTextview;
    TextView wonTextView;

    Spinner exerciseSpinner;
    EditText amountEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fight);
//        mAuth = FirebaseAuth.getInstance();
        generateCode = findViewById(R.id.generateCode);
        start = findViewById(R.id.start);
        textView_generateCode = findViewById(R.id.textView_generateCode);
        code =findViewById(R.id.code);
        lostTextview = findViewById(R.id.lostTextView);
        wonTextView = findViewById(R.id.wonTextView);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        String email = user.getEmail();
        username = email.substring(0, email.indexOf("@"));
        addUserFightSum();
        exerciseSpinner = findViewById(R.id.exerciseSpinner);
        amountEditText = findViewById(R.id.amountEditText);


        generateCode.setOnClickListener(v -> generateCode());
        start.setOnClickListener(v -> generateCodeExists());
    }

    private void addUserFightSum() {
        DatabaseReference userRef = db.getReference("Users").child(username);
        userRef.child("lost").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    lost = dataSnapshot.getValue(Integer.class);
                    lostTextview.setText("lost: " + String.valueOf(lost));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error if necessary
            }
        });

        userRef.child("win").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    win = dataSnapshot.getValue(Integer.class);
                    wonTextView.setText("won: " + String.valueOf(win));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error if necessary
            }
        });
    }

    private void generateCodeExists()
    {
        DatabaseReference fightsRef = db.getReference("Fights");
        String generateCode = String.valueOf(code.getText());
        fightRef = fightsRef.child(generateCode);
//        Query exists = fightRef.orderByKey().equalTo(generateCode);
        ValueEventListener ValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Fight fight = dataSnapshot.getValue(Fight.class);

                    System.out.println("Fight found: " + fight.getName1() + " vs " + fight.getName2());
                    writeUserToFight(fight , fightRef ,generateCode);
//                    intent.putExtra("exercise", selectedExercise);
//                    intent.putExtra("amount", amount);
                } else {
                    System.out.println("Fight not found.");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential errors
            }
        };
        fightRef.addListenerForSingleValueEvent(ValueEventListener);
    }

    private void generateCode()
    {
        fightRef = db.getReference().child("Fights");
        Fight fight = new Fight();
        String fightKey = fightRef.push().getKey();
        fightRef.child(fightKey).setValue(fight);

        // Get the values from the spinner and edit text
        String exercise = exerciseSpinner.getSelectedItem().toString();
        String amount = amountEditText.getText().toString();

        // Set default values if exercise or amount is empty
        if (exercise.isEmpty()) {
            exercise = "Squat"; // Default exercise value
            Toast.makeText(this, "No exercise selected. Defaulting to Squat", Toast.LENGTH_SHORT).show();
        }
        if (amount.isEmpty()) {
            amount = "5"; // Default amount value
            Toast.makeText(this, "No amount entered. Defaulting to 5", Toast.LENGTH_SHORT).show();
        }

        // Add children for amount and exercise
        fightRef.child(fightKey).child("amount").setValue(amount);
        fightRef.child(fightKey).child("exercise").setValue(exercise);
        fightRef.child(fightKey).child("winner").setValue("");

        textView_generateCode.setText(fightKey);
    }
    private void writeUserToFight(Fight fight , DatabaseReference fightRef , String generateCode)
    {
        // Check if the fight has ended
        if ((!fight.getName1().equals("") && !fight.getScore1().equals("")) && (!fight.getName1().equals("") && !fight.getScore1().equals(""))) {
            // Add the fight information to each user's array of fights
            addUserFight(fight.getName1(), generateCode);
            addUserFight(fight.getName2(), generateCode);
//            fightRef.removeValue();
//            Intent intent = new Intent(this, FightMenuActivity.class);
//            intent.putExtra("fightKey", generateCode);
//            startActivity(intent);
        }
        else if (fight.getName1().equals(username) || fight.getName2().equals(username))
        {
                Toast.makeText(this, "You are already a competitor in the competition", Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(this, FightMenuActivity.class);
//                intent.putExtra("fightKey", generateCode);
//                startActivity(intent);
        }
        else if (fight.getName2().equals(""))
        {
            fight.setName2(username);
            Toast.makeText(this, "You entered the competition, good luck!", Toast.LENGTH_SHORT).show();
            fightRef.child("name2").setValue(username);
            Intent intent = new Intent(this, FightMenuActivity.class);
            intent.putExtra("fightKey", generateCode);
            startActivity(intent);
        }
        else  if (fight.getName1().equals(""))
        {
        fight.setName1(username);
        Toast.makeText(this, "You entered the competition, good luck!", Toast.LENGTH_SHORT).show();
        fightRef.child("name1").setValue(username);
        Intent intent = new Intent(this, FightMenuActivity.class);
        intent.putExtra("fightKey", generateCode);
        startActivity(intent);
        }
        else
            Toast.makeText(this, "There are already two competitors in the competition", Toast.LENGTH_LONG).show();
    }
    private void addUserFight(String username, String generateCode) {
        DatabaseReference userRef = db.getReference("Users").child(username).child("fights");
        Query query = userRef.orderByChild("fightKey").equalTo(generateCode);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    DatabaseReference fightRef = db.getReference("Fights").child(generateCode);
                    fightRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot fightSnapshot) {
                            if (fightSnapshot.exists()) {
                                Fight fight = fightSnapshot.getValue(Fight.class);
                                String score = "";

                                if (fight.getName1().equals(username)) {
                                    score = fight.getScore1();
                                } else if (fight.getName2().equals(username)) {
                                    score = fight.getScore2();
                                }

                                if (!score.isEmpty()) {
                                    String userFightKey = userRef.push().getKey();

                                    // Create a map to hold the fight information
                                    Map<String, Object> fightInfo = new HashMap<>();
                                    fightInfo.put("fightKey", generateCode);
                                    fightInfo.put("score", score);

                                    // Add the fight information to the user's array of fights
                                    userRef.child(userFightKey).setValue(fightInfo);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            // Handle potential errors
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential errors
            }
        });
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

