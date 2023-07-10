package com.ai.app;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.AsyncTask;
import android.util.Log;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.camera.core.CameraInfo;
import androidx.camera.view.PreviewView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;


import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.media.MediaPlayer;

public class SquatActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    private final String url = "http://10.100.102.109:5002";
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET};

    private class StartActivityTask extends AsyncTask<Class<?>, Void, Void> {
        @Override
        protected Void doInBackground(Class<?>... classes) {
            Intent intent = new Intent(getApplicationContext(), classes[0]);
            startActivity(intent);
            return null;
        }
    }

    private ProcessCameraProvider cameraProvider;
    private Camera camera;
    private ExecutorService cameraExecutor;
    private ImageCapture imageCapture;
    private OkHttpClient okHttpClient;
    private Button captureButton;
    private Button stopCaptureButton;

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;

    ImageView click_image_id;
    static int counterFromServer = 0;

    static int Normal = 0;
    static int UnevenBack = 0;
    static int FeetTooNarrow = 0;
    static int ButtockTooHigh = 0;
    static int KneesTooWide = 0;
    static int KneesInward = 0;
    static int Counter = 0;

    // timer
    private boolean isTimerRunning = false;
    private long startTime;
    public long elapsedTime;

    String fightKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_squat);
//        sendEmptyPostRequest();

        // Check if the Intent has extra data
        if (getIntent().hasExtra("fightKey")) {
            fightKey = getIntent().getStringExtra("fightKey");
            // Get the child count under the fightKey
            DatabaseReference fightRef = FirebaseDatabase.getInstance().getReference().child("Fights").child(fightKey);
            fightRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Fight fight = dataSnapshot.getValue(Fight.class);
                        Counter = Integer.parseInt(fight.getAmount());
                    } else {
                        // Fight does not exist
                        Toast.makeText(SquatActivity.this, "Fight not found.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle potential errors
//                    Toast.makeText(SquatActivity.this, "Failed to retrieve child count.", Toast.LENGTH_SHORT).show();
                }

            });
//            Toast.makeText(this, "Extra data received: " + fightKey, Toast.LENGTH_SHORT).show();
        } else {
            Counter = Integer.parseInt(getIntent().getStringExtra("counter"));
            // Handle the case where Intent does not contain extra data
//            Toast.makeText(this, "No extra data received. Using default values.", Toast.LENGTH_SHORT).show();
        }
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        cameraExecutor = Executors.newSingleThreadExecutor();
        okHttpClient = new OkHttpClient();
        captureButton = findViewById(R.id.captureButton);
        stopCaptureButton = findViewById(R.id.stopCaptureButton);

        surfaceView = findViewById(R.id.surface_view);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        click_image_id = findViewById(R.id.click_image);
        stopCaptureButton.setVisibility(View.INVISIBLE);
        stopCaptureButton.setOnClickListener(v -> stopCamera());
    }

    private void stopCamera() {
        if (cameraProvider != null) {
            cameraProvider.unbindAll();
            cameraProvider = null;
            camera = null;

            // Stop the timer and calculate the elapsed time
            if (isTimerRunning) {
                elapsedTime = System.currentTimeMillis() - startTime;
                isTimerRunning = false;
            }

            // send empty POST request to server
            sendEmptyPostRequest();

            if (fightKey != null) {
                Intent intent = new Intent(getApplicationContext(), Result.class);
                intent.putExtra("elapsedTime", elapsedTime);
                intent.putExtra("fightKey", fightKey);
                intent.putExtra("count", counterFromServer);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(getApplicationContext(), Result.class);
                intent.putExtra("elapsedTime", elapsedTime);
                intent.putExtra("count", counterFromServer);
                startActivity(intent);
                finish();
            }
        }
    }

    private void sendEmptyPostRequest() {
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(null, new byte[0]);
        Request request = new Request.Builder()
                .url(url + "/init-counter") // Replace with your server URL
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // Handle the response if needed
            }
        });
    }


    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        Intent intent;
        switch (item.getItemId()) {
            case R.id.app_info:
                new StartActivityTask().execute(LegalTabsActivity.class);
                finish();
                return true;
            case R.id.user_guide:
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void startCamera() {
        PreviewView previewView = findViewById(R.id.previewView);

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider, previewView);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private boolean hasFrontCamera() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                Integer lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (lensFacing != null && lensFacing == CameraCharacteristics.LENS_FACING_FRONT) {
                    return true;
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void bindPreview(ProcessCameraProvider cameraProvider, PreviewView previewView) {
        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = null;
        if (hasFrontCamera()) {
            cameraSelector = new CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                    .build();
        } else {
            cameraSelector = new CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build();
        }


        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        imageCapture = new ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();

        camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
        previewView.setAlpha(0);
        captureButton.setOnClickListener(v -> captureImage());
    }

    private void captureImage() {
        // start the timer
        if (!isTimerRunning) {
            startTime = System.currentTimeMillis();
            isTimerRunning = true;
        }
        // create a file with a unique name
        stopCaptureButton.setVisibility(View.VISIBLE);
        captureButton.setVisibility(View.INVISIBLE);
        File outputDirectory = getOutputDirectory();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String currentTime = sdf.format(new Date());
        String fileName = "IMG_" + currentTime + ".jpg";
        File outputFile = new File(outputDirectory, fileName);

        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(outputFile).build();
        Log.d("captureImage", outputFile.getAbsolutePath());

        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {

                Bitmap photo = BitmapFactory.decodeFile(outputFile.getAbsolutePath());

                sendImageToServer(photo, outputFile.getAbsolutePath());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                captureImage();

//                // check if image deleted
//                File imageFile = new File(outputFile.getAbsolutePath());
//                boolean deleted = imageFile.delete();
//                if (deleted) {
//                    Log.d("sendImageToServer", "Image deleted successfully");
//                } else {
//                    Log.d("sendImageToServer", "Failed to delete image");
//                }
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                // Error occurred while capturing image
                exception.printStackTrace();
            }
        });
    }

    private File getOutputDirectory() {
        // Get the appropriate directory for storing the images.
        File mediaDir = getExternalMediaDirs()[0];
        File outputDirectory = new File(mediaDir, "Camera");

        if (!outputDirectory.exists()) {
            if (!outputDirectory.mkdirs()) {
                return null;
            }
        }
        return outputDirectory;
    }

    private void sendImageToServer(Bitmap photo, String imagePath) {

        // Create a mutable copy of the original bitmap
        Bitmap mutableBitmap = photo.copy(Bitmap.Config.ARGB_8888, true);

        // Rotate the bitmap pixels
        Matrix matrix = new Matrix();
        matrix.postRotate(360); // Rotate by 360 degrees (no rotation)
        Bitmap rotatedBitmap = Bitmap.createBitmap(mutableBitmap, 0, 0, mutableBitmap.getWidth(), mutableBitmap.getHeight(), matrix, true);

        // Convert the rotated bitmap to byte array
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        OkHttpClient client = new OkHttpClient();

        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", "image.png", RequestBody.create(MediaType.parse("image/png"), byteArray));

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addPart(imagePart)
                .build();

        Request request = new Request.Builder()
                .url(url + "/analyze-image")
                .post(requestBody)
                .build();

//        File imageFile = new File(imagePath);
//        boolean deleted = imageFile.delete();
//        if (deleted) {
//            Log.d("sendImageToServer", "Image deleted successfully");
//        } else {
//            Log.d("sendImageToServer", "Failed to delete image");
//        }

        /* this is how the callback get handled */
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                // Read data on the worker thread
                final String responseData = response.body().string();

                runOnUiThread(() -> {
                    // Extract the text from the response data
                    String label = "";

                    try {

                        JSONObject jsonResponse = new JSONObject(responseData);
                        JSONArray landmarksArray;
                        String imageData = jsonResponse.getString("image_data");

                        // decode the Base64 encoded image data
                        byte[] decodedImageData = Base64.getDecoder().decode(imageData);

                        // create a Bitmap from the decoded image data
                        Bitmap imageBitmap = BitmapFactory.decodeByteArray(decodedImageData, 0, decodedImageData.length);

                        // update the image view with the modified bitmap
                        click_image_id.setImageBitmap(imageBitmap);

                        // extract the values from the JSON object
                        label = jsonResponse.getString("label");
                        if (label.equals("Body not detected")) {
                            Log.d("Image", "Body not detected");
                            landmarksArray = new JSONArray();
                        } else {

                            countTheLabel(label);
                            counterFromServer = jsonResponse.getInt("counter");
                            landmarksArray = jsonResponse.getJSONArray("landmarks");
                        }

//                        // Draw the circles at the landmark positions
//                        for (int i = 0; i < landmarksArray.length(); i++) {
//                            if (label.equals("Uneven back") && (i == 12 || i == 11 || i == 24 || i == 23)) {
//
//                                // Play sound here
//                                MediaPlayer mediaPlayer = MediaPlayer.create(SquatActivity.this, R.raw.normal_label_sound);
//                                mediaPlayer.start();
//                            }
//                            if (label.equals("Feet too narrow") && (i <= 32 && i >= 27)) {
//
//                                MediaPlayer mediaPlayer = MediaPlayer.create(SquatActivity.this, R.raw.label_sound);
//                                mediaPlayer.start();
//                            }
//                            if (label.equals("Buttock too high") && (i <= 24 && i >= 23)) {
//
//                                MediaPlayer mediaPlayer = MediaPlayer.create(SquatActivity.this, R.raw.label_sound);
//                                mediaPlayer.start();
//                            }
//                            if ((label.equals("Knees too wide") || label.equals("Knees inward")) && (i <= 26 && i >= 23)) {
//
//                                // Play sound here
//                                MediaPlayer mediaPlayer = MediaPlayer.create(SquatActivity.this, R.raw.label_sound);
//                                mediaPlayer.start();
//                            }
//                        }

                        if (Counter <= counterFromServer) {
                            sendEmptyPostRequest();
                            stopCamera();
                            // send to the sever init the conter

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }


//    private void drawLandmarksOnBitmap(JSONArray landmarksArray, Bitmap bitmap) {
//        try {
//            // Create a new canvas from the bitmap
//            Canvas canvas = new Canvas(bitmap);
//
//            // Create a paint object for drawing the landmarks
//            Paint landmarkPaint = new Paint();
//            landmarkPaint.setColor(Color.RED);
//            landmarkPaint.setStyle(Paint.Style.FILL);
//            landmarkPaint.setAntiAlias(true);
//
//            // Iterate over the landmarks array and draw each landmark on the canvas
//            for (int i = 0; i < landmarksArray.length(); i++) {
//                JSONObject landmarkObj = landmarksArray.getJSONObject(i);
//
//                // Extract the landmark coordinates
//                float x = (float) landmarkObj.getDouble("x");
//                float y = (float) landmarkObj.getDouble("y");
//
//                // Draw the landmark on the canvas
//                canvas.drawCircle(x, y, 5, landmarkPaint); // Example: draw a circle at the landmark position
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, "Permissions not granted.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        } else {
            startCamera();
        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }

    public void countTheLabel(String label) {
        switch (label) {
            case "Normal":
                Normal++;
                return;
            case "Uneven back":
                UnevenBack++;
                return;
            case "Feet too narrow":
                FeetTooNarrow++;
                return;
            case "Buttock too high":
                ButtockTooHigh++;
                return;
            case "Knees too wide":
                KneesTooWide++;
                return;
            case "Knees inward":
                KneesInward++;
                return;
        }
    }
}