package com.fastturtlePractice.BmpWaterMarkTesting.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;

import com.fastturtlePractice.BmpWaterMarkTesting.R;
import com.fastturtlePractice.BmpWaterMarkTesting.helperClasses.BitmapCompressionTask;

import io.fastturtle.BmpWaterMark.WaterMarkProvider;


/**
 * @Author: Divya Gupta
 * @Date: 20-Dec-22
 */
public class MainActivity extends AppCompatActivity implements BitmapCompressionTask.BitmapCompressedListener {

    AppCompatButton btPickImage;
    AppCompatImageView ivPickedImage;
    ActivityResultLauncher<Intent> pickImageFromGalleryForResult;
    BitmapCompressionTask bitmapCompressionTask;
    WaterMarkProvider wmp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btPickImage = findViewById(R.id.bt_pick_image);
        ivPickedImage = findViewById(R.id.pickedImage);

        wmp = new WaterMarkProvider(this);

        pickImageFromGalleryForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        if (result.getData() != null) {
                            Uri selectedImageUri = result.getData().getData();
                            if(selectedImageUri != null) {
                                bitmapCompressionTask = new BitmapCompressionTask(MainActivity.this, MainActivity.this);
                                bitmapCompressionTask.execute(selectedImageUri);
                            }

                        }
                    }
                });

        btPickImage.setOnClickListener(v -> openImageChooser());

    }

    @Override
    public void onBitmapCompressed(Bitmap bitmap) {

        bitmap = wmp.generateWaterMarkedBitmap(bitmap, "This is a watermark", R.color.blue_200, 50);
        ivPickedImage.setImageBitmap(bitmap);
    }

    void openImageChooser() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
        String[] mimeTypes = {"image/jpeg", "image/png"};
        galleryIntent.setType("image/jpeg|image/png");
        galleryIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        pickImageFromGalleryForResult.launch(galleryIntent);
    }
}
