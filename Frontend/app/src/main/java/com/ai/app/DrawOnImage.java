package com.ai.app;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DrawOnImage {

    /* creates new image and returns local path */
    public byte[] generateImage(Context context, String playerName, Bitmap image, String outputName) {
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
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "Norwester 400.otf"); // Replace "fonts/Norwester.ttf" with the path to your Norwester font file
        paint.setTypeface(typeface);

        // Calculate the position to draw the name (centered horizontally, 100 points from the top)
        float textWidth = paint.measureText(playerName);
        float x = (bitmap.getWidth() - textWidth) / 2;
        float y = 1700 ;

        // Draw the player name onto the canvas
        canvas.drawText(playerName, x, y, paint);

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


    // get file path for drawable image
    public String getDrawableFilePath(Context context, String imageName) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier(imageName, "drawable", context.getPackageName());
        if (resourceId == 0) {
            // Image resource not found
            return null;
        }

        String filePath;
        try {
            InputStream inputStream = resources.openRawResource(resourceId);
            File cacheDir = context.getCacheDir();
            File cacheFile = new File(cacheDir, imageName + ".png");
            FileOutputStream outputStream = new FileOutputStream(cacheFile);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outputStream.close();

            filePath = cacheFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return filePath;
    }

    // find out if path OK
    public boolean isImagePathValid(String imagePath) {
        File file = new File(imagePath);

        // Check if the file exists
        if (!file.exists()) {
            return false;
        }

        // Check if the file is an image file
        String extension = getFileExtension(file);
        if (extension != null) {
            String lowercaseExtension = extension.toLowerCase();
            return lowercaseExtension.equals("jpg") ||
                    lowercaseExtension.equals("jpeg") ||
                    lowercaseExtension.equals("png") ||
                    lowercaseExtension.equals("gif") ||
                    lowercaseExtension.equals("bmp");
        }

        return false;
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastDotIndex = name.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < name.length() - 1) {
            return name.substring(lastDotIndex + 1);
        }
        return null;
    }
}
