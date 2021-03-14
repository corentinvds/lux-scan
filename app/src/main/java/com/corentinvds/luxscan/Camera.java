package com.corentinvds.luxscan;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

public class Camera {
    public static void requestCameraCapture(Activity activity, Uri pictureFileUri, int requestCode) {
        if (activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_FINISH_ON_COMPLETION, true);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, pictureFileUri);
            if (cameraIntent.resolveActivity(activity.getPackageManager()) != null) {
                activity.startActivityForResult(cameraIntent, requestCode);
            } else {
                Toast.makeText(activity, "No camera app found", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(activity, "Camera not available", Toast.LENGTH_LONG).show();
        }
    }
}
