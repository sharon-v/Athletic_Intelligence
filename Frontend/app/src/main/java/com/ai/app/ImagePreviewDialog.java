package com.ai.app;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImagePreviewDialog extends DialogFragment {

    private static final String ARG_IMAGE_BITMAP = "image_bitmap";

    public static ImagePreviewDialog newInstance(Bitmap imageBitmap) {
        ImagePreviewDialog fragment = new ImagePreviewDialog();
        Bundle args = new Bundle();
        args.putParcelable(ARG_IMAGE_BITMAP, imageBitmap);
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_image_preview, null);
        ImageView imageViewPreview = dialogView.findViewById(R.id.imageViewPreview);

        // Get the image bitmap from the arguments
        Bitmap imageBitmap = getArguments().getParcelable(ARG_IMAGE_BITMAP);

        // Set the image bitmap to the ImageView
        imageViewPreview.setImageBitmap(imageBitmap);

        // Add an export button to the dialog
        builder.setPositiveButton("Export", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Perform the export action here, e.g., share the image on social media
                exportImage(imageBitmap);
            }
        });

        builder.setView(dialogView);

        Dialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
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

    // Helper method to get the Uri of the bitmap
    private Uri getBitmapUri(Bitmap imageBitmap) {
        File file = new File(getActivity().getExternalCacheDir(), "image.png");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".provider", file);
    }
}
