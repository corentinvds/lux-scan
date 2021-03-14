package com.corentinvds.luxscan;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    static final int REQUEST_PICTURE_CAPTURE = 1;
    private ImageView imageView;
    private TextView filenamesView;
    private String pictureFilePath;
    private TextView outputDirectoryView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.picture);
        imageView.setVisibility(View.GONE);
        filenamesView = findViewById(R.id.filenames);
        outputDirectoryView = findViewById(R.id.outputDirectory);
        Button captureButton = findViewById(R.id.capture);

        captureButton.setOnClickListener(view -> captureImage());

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            captureButton.setEnabled(false);
        }

        updateFilenames();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PICTURE_CAPTURE && resultCode == RESULT_OK) {
            onImageCaptured();
        }
    }

    private void updateFilenames() {
        outputDirectoryView.setText("Directory: ");
        filenamesView.setText("");

        File outputDir = null;
        try {
            outputDir = getOutputDir();
            outputDirectoryView.setText("Directory: " + outputDir.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Cannot get output directory: " + e, Toast.LENGTH_LONG).show();
            return;
        }
        if (outputDir.isDirectory()) {
            StringBuilder sb = new StringBuilder();
            File[] files = outputDir.listFiles();
            Arrays.sort(files);
            for (int i = files.length - 1; i >= 0; i--) {
                sb.append(" - ").append(files[i].getName()).append("\n");
            }
            filenamesView.setText(sb.toString());
        }
    }

    private void captureImage() {
        File pictureFile;
        try {
            pictureFile = getPictureFile();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this,
                    "Photo file can't be created, please try again: " + e,
                    Toast.LENGTH_LONG).show();
            return;
        }
        this.pictureFilePath = pictureFile.getAbsolutePath();
        Uri pictureFileUri = Utility.getUri(this, pictureFile);
        Camera.requestCameraCapture(this, pictureFileUri, REQUEST_PICTURE_CAPTURE);
    }

    private void onImageCaptured() {
        File imgFile = new File(pictureFilePath);
        if (imgFile.exists()) {
            Gallery.addImageToGallery(this, imgFile);
            imageView.setImageURI(Uri.fromFile(imgFile));
            imageView.setVisibility(View.VISIBLE);
            updateFilenames();
        }
    }

    private File getOutputDir() throws IOException {
        // File directory = this.getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (directory == null) {
            throw new IOException("Storage no available, cannot get app documents directory");
        }
        return new File(directory, "LuxScan");
    }

    private File getPictureFile() throws IOException {
        Utility.verifyStoragePermissions(this);
        File outputDir = getOutputDir();
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            throw new IOException("Cannot create directory " + outputDir);
        }
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault()).format(new Date());
        File pictureFile = new File(outputDir, "LuxScan_" + timeStamp + ".jpg");

        if (pictureFile.exists()) {
//            File image = File.createTempFile(pictureFile.geName(), ".jpg", outputDir);
            throw new IOException("The file " + pictureFile + " already exists");
        }
        return pictureFile;
    }
}