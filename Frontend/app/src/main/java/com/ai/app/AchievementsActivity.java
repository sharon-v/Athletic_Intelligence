package com.ai.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.TooltipCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class AchievementsActivity extends AppCompatActivity {

    ProgressBar progressBar;

    private FirebaseAuth auth;
    private FirebaseUser user;
    private String email;
    private String username;
    private ImageView countAward;
    private ImageView accuracyAward;
    private  ImageView timeAward;
    private  ImageView fightAward;
    Bitmap bitmapCount;
    Bitmap bitmapAccuracy;
    Bitmap bitmapTime;
    Bitmap bitmapFight;

    private ImageView imageView;
    private ImageView imageView1;
    private ImageView imageView2;
    private ImageView imageView3;
    private ImageView imageView4;
    private ImageView imageView5;
    private ImageView imageView6;
    private ImageView imageView7;
    private ImageView imageView8;
    private ImageView imageView9;
    private ImageView imageView10;
    private ImageView imageView11;
    private ImageView imageView12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);
        progressBar = findViewById(R.id.progressBar);

        imageView = findViewById(R.id.imageView);
        imageView1 = findViewById(R.id.imageView);
        imageView2 = findViewById(R.id.imageView2);
        imageView3 = findViewById(R.id.imageView3);
        imageView4 = findViewById(R.id.imageView4);
        imageView5 = findViewById(R.id.imageView5);
        imageView6 = findViewById(R.id.imageView6);
        imageView7 = findViewById(R.id.imageView7);
        imageView8 = findViewById(R.id.imageView8);
        imageView9 = findViewById(R.id.imageView9);
        imageView10 = findViewById(R.id.imageView10);
        imageView11 = findViewById(R.id.imageView11);
        imageView12 = findViewById(R.id.imageView12);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        countAward = findViewById(R.id.countAward);
        accuracyAward = findViewById(R.id.accuracyAward);
        timeAward = findViewById(R.id.timeAward);
        fightAward = findViewById(R.id.fightAward);


        if (user != null) { // check for current user, and get username
            email = user.getEmail();
            assert email != null;
            username = email.substring(0, email.indexOf("@"));
        }
        updateAchievements("achievement-time");
        updateAchievements("achievement-count");
        updateAchievements("achievement-accuracy");
        updateAchievements("achievement-fight");
        countAward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrieveAchievementData(bitmapCount);
                Toast.makeText(AchievementsActivity.this, "Achieved by doing 20 squats", Toast.LENGTH_SHORT).show();
            }
        });

        accuracyAward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrieveAchievementData(bitmapAccuracy);
                Toast.makeText(AchievementsActivity.this, "Achieved by getting over 90% accuracy score", Toast.LENGTH_SHORT).show();
            }
        });

        timeAward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrieveAchievementData(bitmapTime);
                Toast.makeText(AchievementsActivity.this, "Achieved by doing a 10 minute exercise", Toast.LENGTH_SHORT).show();
            }
        });

        fightAward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrieveAchievementData(bitmapFight);
                Toast.makeText(AchievementsActivity.this, "Achieved by winning your 1st fight ♛", Toast.LENGTH_SHORT).show();
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AchievementsActivity.this, "Coming Soon", Toast.LENGTH_SHORT).show();
            }
        });
        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AchievementsActivity.this, "Coming Soon", Toast.LENGTH_SHORT).show();
            }
        });
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AchievementsActivity.this, "Coming Soon", Toast.LENGTH_SHORT).show();
            }
        });

        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AchievementsActivity.this, "Coming Soon", Toast.LENGTH_SHORT).show();
            }
        });

        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AchievementsActivity.this, "Coming Soon", Toast.LENGTH_SHORT).show();
            }
        });

        imageView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AchievementsActivity.this, "Coming Soon", Toast.LENGTH_SHORT).show();
            }
        });

        imageView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AchievementsActivity.this, "Coming Soon", Toast.LENGTH_SHORT).show();
            }
        });

        imageView7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AchievementsActivity.this, "Coming Soon", Toast.LENGTH_SHORT).show();
            }
        });

        imageView8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AchievementsActivity.this, "Coming Soon", Toast.LENGTH_SHORT).show();
            }
        });

        imageView9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AchievementsActivity.this, "Coming Soon", Toast.LENGTH_SHORT).show();
            }
        });

        imageView10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AchievementsActivity.this, "Coming Soon", Toast.LENGTH_SHORT).show();
            }
        });

        imageView11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AchievementsActivity.this, "Coming Soon", Toast.LENGTH_SHORT).show();
            }
        });

        imageView12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AchievementsActivity.this, "Coming Soon", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void retrieveAchievementData(Bitmap bitmap)
    {
        if(bitmap == null)
            return;
        ImagePreviewDialog dialog = ImagePreviewDialog.newInstance(bitmap);
        dialog.show(getSupportFragmentManager(), "image_preview_dialog");
    }

    private Bitmap convertByteArrayToBitmap(List<Integer> byteArray) {
        byte[] data = new byte[byteArray.size()];
        for (int i = 0; i < byteArray.size(); i++) {
            data[i] = byteArray.get(i).byteValue();
        }
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }

    private void updateAchievements(String achievementType) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(username).child(achievementType);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // achievements data exists, retrieve the achievement
                    Achievement achievement = dataSnapshot.getValue(Achievement.class);
                    if (achievement != null) {
                        // convert the achievement data back to a bitmap
                        Bitmap bitmap = convertByteArrayToBitmap(achievement.getData());

                        if (bitmap != null) {
                            switch (achievementType)
                            {
                                case "achievement-count":
                                    countAward.setImageResource(R.drawable.a4);
                                    bitmapCount = bitmap;
                                    break;
                                case "achievement-time":
                                    timeAward.setImageResource(R.drawable.a2);
                                    bitmapTime = bitmap;
                                    break;
                                case "achievement-fight":
                                    fightAward.setImageResource(R.drawable.a1);
                                    bitmapFight = bitmap;
                                    break;
                                case "achievement-accuracy":
                                    accuracyAward.setImageResource(R.drawable.a3);
                                    bitmapAccuracy = bitmap;
                                    break;
                            }
                            progressBar.setVisibility(View.GONE);

                        } else {
                            Toast.makeText(AchievementsActivity.this, "Failed to convert achievement data to bitmap", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    // no achievements data
//                    Toast.makeText(AchievementsActivity.this, "No achievements data found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occurred
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