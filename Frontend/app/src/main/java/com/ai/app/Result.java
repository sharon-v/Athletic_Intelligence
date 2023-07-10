package com.ai.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;



public class Result extends AppCompatActivity {
    private TextView textViewAccuracy;
    private TextView textViewCounter;
    private TextView textViewUsername;
    TextView textViewElapsedTime;
    String fightKey;
    Button buttonExport;

    FirebaseAuth auth;
    FirebaseUser user;
    String username;
    Bitmap finalImageBitmap;

    double normalAccuracy;
    boolean fightTraining = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        buttonExport = findViewById(R.id.buttonExport);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        String email = user.getEmail();
        username = email.substring(0, email.indexOf("@"));

        buttonExport.setVisibility(View.INVISIBLE);
        // Check if the Intent has extra data
        if (getIntent().hasExtra("fightKey")) {
            // Handle the case where Intent contains extra data
            fightKey = getIntent().getStringExtra("fightKey");
            // Do something with the extra data
            // ...
            fightTraining = true;
//            Toast.makeText(this, "Extra data received: " + fightKey, Toast.LENGTH_SHORT).show();
        } else {
            // Handle the case where Intent does not contain extra data
//            Toast.makeText(this, "No extra data received. Using default values.", Toast.LENGTH_SHORT).show();
        }

        // Retrieve the elapsed time from the intent
        long elapsedTime = getIntent().getLongExtra("elapsedTime", 0);

        // Display the elapsed time (you can format it as needed)
        String formattedTime = formatTime(elapsedTime);
        textViewElapsedTime = findViewById(R.id.textViewTrainingTime);
        textViewElapsedTime.setText(formattedTime);
        textViewUsername = findViewById(R.id.textViewUsername);
        textViewUsername.setText(username);

        // Find the TextView by its ID
        textViewAccuracy = findViewById(R.id.textViewAccuracy);
        textViewCounter = findViewById(R.id.textViewCounter);
        calculateNormalAccuracy();
        updateCounter();
        Bitmap imageBitmap = null;
        List<Integer> intList;
        if(fightTraining) {
            buttonExport.setVisibility(View.VISIBLE);
            updateFight();
            updateWinner();

        }

        AchievementTime();
        AchievementAccuracy();
        AchievementCount();

        Bitmap finalImageBitmap = imageBitmap;
        buttonExport.setOnClickListener(v ->
                showFightAward()
        );

    }

    private byte[] generateImage(Bitmap image,String WinnerName,String LoserName) {
        // Create a copy of the existing image to draw on
        Bitmap bitmap = image.copy(Bitmap.Config.ARGB_8888, true);

        // Create a canvas from the bitmap
        Canvas canvas = new Canvas(bitmap);

        // Create a paint object for drawing the player name
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#FF9933")); // Set text color to #FF9933 (orange)
        paint.setTextSize(300); // Set text size to 113
        paint.setAntiAlias(true); // Enable anti-aliasing for smoother text

        // Set the custom font (Norwester)
        Typeface typeface = Typeface.createFromAsset(this.getAssets(), "Norwester 400.otf"); // Replace "fonts/Norwester.ttf" with the path to your Norwester font file
        paint.setTypeface(typeface);

        String playersName = WinnerName + " VS "+ LoserName;
        // Calculate the position to draw the name (centered horizontally, 100 points from the top)
        float textWidth = paint.measureText(playersName);
        float x = (bitmap.getWidth() - textWidth) / 2;
        float y = 1700 ;

        // Draw the player name onto the canvas
        canvas.drawText(playersName, x, y, paint);

        // Resize the bitmap to reduce its size
        int targetWidth = 800; // Adjust the target width as needed
        int targetHeight = (int) (targetWidth * ((float) bitmap.getHeight() / bitmap.getWidth()));
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true);

        // Compress the resized bitmap to reduce its file size
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos); // Adjust the compression quality and format as needed
        byte[] imageData = baos.toByteArray();

        // You can now upload the imageData to Firebase Storage or use it as needed
        return imageData;
    }

    // Method to export the image (example implementation, adjust it based on your requirements)
    private void exportImage(Bitmap imageBitmap) {
        // Add your implementation here to export the image to social media or any other desired action
        // For example, you can use the ShareCompat Intent to share the image:
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/png");
        Uri imageUri = getBitmapUri(imageBitmap);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        startActivity(Intent.createChooser(shareIntent, "Share Image"));
    }
    private Uri getBitmapUri(Bitmap imageBitmap) {
        File file = new File(this.getExternalCacheDir(), "image.png");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return FileProvider.getUriForFile(this, this.getPackageName() + ".provider", file);
    }
    public void AchievementAccuracy()
    {
//        Double accuracy = getAccuracyValue();

        if (normalAccuracy > 90) {
            DrawOnImage drawing = new DrawOnImage();
            String outputImName = username + "Accuracy";
            byte[] editedPhoto = drawing.generateImage(getApplicationContext(), username, BitmapFactory.decodeResource(getResources(), R.drawable.accurancy), outputImName);
            updateAchievements(editedPhoto , "achievement-accuracy", username);
//            retrieveAchievementData("achievement-acurenccy");
        }
    }
    public void AchievementFight(String userName)
    {
            DrawOnImage drawing = new DrawOnImage();
            String outputImName = username + "Accuracy";
            byte[] editedPhoto = drawing.generateImage(getApplicationContext(), username, BitmapFactory.decodeResource(getResources(), R.drawable.firstfight), outputImName);
            updateAchievements(editedPhoto , "achievement-fight",userName);
    }

    public void AchievementTime()
    {
        long elapsedTime = getIntent().getLongExtra("elapsedTime", 0);
        String formattedTime = formatTime(elapsedTime);

        if (elapsedTime > TimeUnit.MINUTES.toMillis(1))  {
            DrawOnImage drawing = new DrawOnImage();
            String outputImName = username + "Time";
            byte[] editedPhoto = drawing.generateImage(getApplicationContext(), username, BitmapFactory.decodeResource(getResources(), R.drawable.time), outputImName);
            updateAchievements(editedPhoto ,"achievement-time",username);
//            retrieveAchievementData("achievement-time");
        }
    }
    public void AchievementCount()
    {
        int counter = getCounterValue();

        if (counter > 20) {
            DrawOnImage drawing = new DrawOnImage();
            String outputImName = username + "Count";
            byte[] editedPhoto = drawing.generateImage(getApplicationContext(), username, BitmapFactory.decodeResource(getResources(), R.drawable.squat), outputImName);
            updateAchievements(editedPhoto , "achievement-count" , username);
//            retrieveAchievementData("achievement-count");
        }
    }
    private int getCounterValue() {
        String counterText = textViewCounter.getText().toString();
        String[] parts = counterText.split(":");
        if (parts.length > 1) {
            String counterValue = parts[1].trim();
            return Integer.parseInt(counterValue);
        }
        return 0;
    }
    private Double getAccuracyValue() {
        String accuracyText = textViewAccuracy.getText().toString();

        return Double.parseDouble(accuracyText.substring(0, accuracyText.indexOf("%")));

    }

    private void updateAchievements(byte[] data ,String achievementType, String userName) {

//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        editedPhoto.compress(Bitmap.CompressFormat.PNG,100,baos);
//        byte[] data = baos.toByteArray();
//        String base64Data = Base64.encodeToString(data, Base64.DEFAULT);
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userName);
        List<Integer> intList = new ArrayList<>();
        for (byte b : data) {
            intList.add((int) b);
        }
        Achievement a = new Achievement(intList,achievementType);
        userRef.child(achievementType).setValue(a);
// avital

    }
    private Bitmap convertByteArrayToBitmap(List<Integer> byteArray) {
        byte[] data = new byte[byteArray.size()];
        for (int i = 0; i < byteArray.size(); i++) {
            data[i] = byteArray.get(i).byteValue();
        }
        return BitmapFactory.decodeByteArray(data, 0, data.length);
    }
    private void retrieveAchievementData(String achievementType) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(username).child(achievementType);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Achievements data exists, retrieve the achievement
                    Achievement achievement = dataSnapshot.getValue(Achievement.class);
                    if (achievement != null) {
                        // Convert the achievement data back to a bitmap
                        Bitmap bitmap = convertByteArrayToBitmap(achievement.getData());
                        if (bitmap != null) {
                            // Do something with the bitmap
                            // ...
                            ImagePreviewDialog dialog = ImagePreviewDialog.newInstance(bitmap);
                            dialog.show(getSupportFragmentManager(), "image_preview_dialog");
                        } else {
                            Toast.makeText(Result.this, "Failed to convert achievement data to bitmap", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    // No achievements data
                    Toast.makeText(Result.this, "No achievements data found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occurred
            }
        });
    }

    private void updateWinner() {
        DatabaseReference fightRef = FirebaseDatabase.getInstance().getReference().child("Fights").child(fightKey);

        fightRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Fight fight = dataSnapshot.getValue(Fight.class);

                    if (!fight.getScore1().equals("") && !fight.getScore2().equals("")) {
                        // Compare the scores and determine the winner
                        double score1 = Double.parseDouble(fight.getScore1());
                        double score2 = Double.parseDouble(fight.getScore2());


                        if (score1 > score2) {
                            fightRef.child("winner").setValue(fight.getName1());
                            updateWinnerStatus(fight.getName1(), fight.getName1());
                            updateWinnerStatus(fight.getName2(), fight.getName1());
                            AchievementFight(fight.getName1());
                        } else if (score2 > score1) {
                            fightRef.child("winner").setValue(fight.getName2());
                            updateWinnerStatus(fight.getName1(), fight.getName2());
                            updateWinnerStatus(fight.getName2(), fight.getName2());
                            AchievementFight(fight.getName2());
                        } else {
                            fightRef.child("winner").setValue("Tie");
                            updateWinnerStatus(fight.getName1(), "Tie");
                            updateWinnerStatus(fight.getName2(), "Tie");
                        }
                        updateWinnerEnmy(fight.getName1(),fight.getName2());
                        updateWinnerEnmy(fight.getName2(),fight.getName1());
                        buttonExport.setVisibility(View.VISIBLE);
                        fightRef.removeValue();
                    }
                } else {
                    // Fight does not exist
                    Toast.makeText(Result.this, "Fight not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential errors
                Toast.makeText(Result.this, "Failed to update the winner.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateWinnerStatus(String username, String winner) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(username);

        userRef.child("fights").child(fightKey).child("winner").setValue(winner);
    }
    private void showFightAward() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(username);
        DatabaseReference fightRef = userRef.child("fights").child(fightKey);

        ValueEventListener fightListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String winner = dataSnapshot.child("winner").getValue(String.class);
                    String enmy = dataSnapshot.child("Enmy").getValue(String.class);
                    if (winner == null) {
                        // Winner is not set, handle the logic here
                        Toast.makeText(Result.this, "Winner is not set", Toast.LENGTH_SHORT).show();
                    }
                    else if (winner.equals(username))
                    {
                        byte[] imageBitmapData = generateImage(BitmapFactory.decodeResource(getResources(), R.drawable.winner),username,enmy);
                        ArrayList<Integer> intList = new ArrayList<>();
                        for (byte b : imageBitmapData) {
                            intList.add((int) b);
                        }
                        finalImageBitmap = convertByteArrayToBitmap(intList);
                        ImagePreviewDialog dialog = ImagePreviewDialog.newInstance(finalImageBitmap);
                        dialog.show(getSupportFragmentManager(), "image_preview_dialog");
//                        exportImage(finalImageBitmap);
//
                        userRef.child("win").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    int win = dataSnapshot.getValue(Integer.class);
                                    userRef.child("win").setValue(win + 1);
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Handle error if necessary
                            }
                        });
                    }
                    else if (winner.equals("Tie"))
                    {
                        byte[] imageBitmapData = generateImage(BitmapFactory.decodeResource(getResources(), R.drawable.tie),username,enmy);
                        ArrayList<Integer> intList = new ArrayList<>();
                        for (byte b : imageBitmapData) {
                            intList.add((int) b);
                        }
                        finalImageBitmap = convertByteArrayToBitmap(intList);
                        ImagePreviewDialog dialog = ImagePreviewDialog.newInstance(finalImageBitmap);
                        dialog.show(getSupportFragmentManager(), "image_preview_dialog");
//                        exportImage(finalImageBitmap);
                    }
                    else
                    {
                        byte[] imageBitmapData = generateImage(BitmapFactory.decodeResource(getResources(), R.drawable.loser),username,enmy);
                        ArrayList<Integer> intList = new ArrayList<>();
                        for (byte b : imageBitmapData) {
                            intList.add((int) b);
                        }
                        finalImageBitmap = convertByteArrayToBitmap(intList);
                        ImagePreviewDialog dialog = ImagePreviewDialog.newInstance(finalImageBitmap);
                        dialog.show(getSupportFragmentManager(), "image_preview_dialog");
//                        exportImage(finalImageBitmap);
//                        int lost = userRef.child("lost").getValue(Integer.class);
//                        userRef.child("lost").setValue(lost+1);
                        userRef.child("lost").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    int lost = dataSnapshot.getValue(Integer.class);
                                    userRef.child("lost").setValue(lost + 1);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                // Handle error if necessary
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error if necessary
            }
        };

        fightRef.addListenerForSingleValueEvent(fightListener);
    }

    private void updateWinnerEnmy(String username, String enmy) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(username);

        userRef.child("fights").child(fightKey).child("Enmy").setValue(enmy);
    }


    private void updateFight() {
        DatabaseReference fightRef = FirebaseDatabase.getInstance().getReference().child("Fights").child(fightKey);

        fightRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Fight fight = dataSnapshot.getValue(Fight.class);

                    // Check if the current user is in the fight
                    if (fight.getName1().equals(username)) {
                        // Update the score for name1
                        String newScore = String.valueOf((int) (normalAccuracy));
                        fightRef.child("score1").setValue(newScore);

                        // Update the user's fight information
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(username);
                        DatabaseReference fightsRef = userRef.child("fights").child(fightKey);
                        fightsRef.child("score").setValue(newScore);
//                        fightsRef.child("enmy").setValue(fight.getName2());

                    } else if (fight.getName2().equals(username)) {
                        // Update the score for name2
                        String newScore = String.valueOf((int) (normalAccuracy));
                        fightRef.child("score2").setValue(newScore);

                        // Update the user's fight information
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(username);
                        DatabaseReference fightsRef = userRef.child("fights").child(fightKey);
                        fightsRef.child("score").setValue(newScore);
//                        fightsRef.child("enmy").setValue(fight.getName1());

                    } else {
                        // The current user is not in the fight
                        Toast.makeText(Result.this, "You are not a participant in this fight.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Fight does not exist
                    Toast.makeText(Result.this, "Fight not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle potential errors
                Toast.makeText(Result.this, "Failed to update the fight.", Toast.LENGTH_SHORT).show();
            }
        });
    }




    // Helper method to format the elapsed time (e.g., convert milliseconds to minutes and seconds)
    private String formatTime(long milliseconds) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)
                - TimeUnit.MINUTES.toSeconds(minutes);
        long millis = milliseconds
                - TimeUnit.MINUTES.toMillis(minutes)
                - TimeUnit.SECONDS.toMillis(seconds);

        return String.format(Locale.getDefault(), "%02d:%02d:%03d", minutes, seconds, millis);
    }
    private void updateCounter() {
        // Get the counter value from SquatActivity.Counter
        int counter = SquatActivity.counterFromServer;

        // Update the textViewCounter with the new counter value
        textViewCounter.setText("Counter: " + counter);
    }


    public double calculateNormalAccuracy() {
        int total = SquatActivity.Normal + SquatActivity.UnevenBack + SquatActivity.FeetTooNarrow + SquatActivity.ButtockTooHigh + SquatActivity.KneesTooWide + SquatActivity.KneesInward;
        if (total > 0) {
            normalAccuracy = (double) (SquatActivity.Normal * 100) / total;
            // Accuracy Update
            String accuracyText = "Accuracy: " + normalAccuracy + "%";
            textViewAccuracy.setText(accuracyText);
            return normalAccuracy;
        } else {
            return 0;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        Intent intent;
        switch (item.getItemId()) {
            case R.id.app_info:
                intent = new Intent(this, LegalTabsActivity.class);
                startActivity(intent);
                return true;
            case R.id.home:
                intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.user_guide:
                // Handle the menu item click
//                Toast.makeText(this, "Menu Item user guide Clicked", Toast.LENGTH_SHORT).show();
                intent = new Intent(getApplicationContext(), GuideActivity.class);
                startActivity(intent);
                return true;
            case R.id.user_profile:
//                Toast.makeText(this, "Menu Item user profile Clicked", Toast.LENGTH_SHORT).show();
                intent = new Intent(getApplicationContext(), UserProfileActivity.class);
                startActivity(intent);
                return true;
            case R.id.achievements_page:
                intent = new Intent(getApplicationContext(), AchievementsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



}