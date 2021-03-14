package com.corentinvds.luxscan;

import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;

public class Gallery {
    public static void addImageToGallery(Context context, File image) {
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    image.getAbsolutePath(), image.getName(), null);
            context.sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Utility.getUri(context, image)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(context, "Cannot add image to gallery: " + e, Toast.LENGTH_LONG).show();
        }
    }
}
