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
    WaterMarkProvider.Builder watermarkBuilder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btPickImage = findViewById(R.id.bt_pick_image);
        ivPickedImage = findViewById(R.id.pickedImage);

        pickImageFromGalleryForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        if (result.getData() != null) {
                            Uri selectedImageUri = result.getData().getData();
                            if (selectedImageUri != null) {
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

        watermarkBuilder = new WaterMarkProvider.Builder(this, bitmap, getString(R.string.hindi_text));

        WaterMarkProvider wmp = watermarkBuilder
                .setColor(io.fastturtle.BmpWaterMark.R.color.red_for_watermark)
                .setAlpha(50)
                .setxCoordinate(0)
                .setyCoordinate(120)
                .setTextSize(78)
                .setRotationAngle(45)
                .build();
        bitmap = wmp.getWaterMarkedBitmap();
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
