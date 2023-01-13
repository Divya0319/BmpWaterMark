package io.fastturtle.BmpWaterMark;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import androidx.annotation.ColorRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

/**
 * @Author: Divya Gupta
 * @Date: 08-Jan-23
 */

public class WaterMarkProvider {
    private final Context context;

    // required parameters
    private Bitmap waterMarkedBitmap;
    private final String waterMarkText;

    // optional parameters
    private int alpha;
    private int textSize;
    private final double rotationAngle;
    private final @ColorRes
    int color;
    private final int xCoordinate;
    private int yCoordinate;

    public WaterMarkProvider(Builder builder) {
        this.context = builder.context;
        this.waterMarkedBitmap = builder.bitmap;
        this.waterMarkText = builder.waterMarkText;
        this.alpha = builder.alpha;
        this.color = builder.color;
        this.rotationAngle = builder.rotationAngle;
        this.textSize = builder.textSize;
        this.xCoordinate = builder.xCoordinate;
        this.yCoordinate = builder.yCoordinate;
    }

    public String getWaterMarkText() {
        return waterMarkText;
    }

    public int getAlpha() {
        return alpha;
    }

    public int getTextSize() {
        return textSize;
    }

    public double getRotationAngle() {
        return rotationAngle;
    }

    public int getColor() {
        return color;
    }

    public int getxCoordinate() {
        return xCoordinate;
    }

    public int getyCoordinate() {
        return yCoordinate;
    }

    public Bitmap getWaterMarkedBitmap() {
        waterMarkedBitmap = createScaledBitmapWithScreenWidth(waterMarkedBitmap);
        int w = waterMarkedBitmap.getWidth();
        int h = waterMarkedBitmap.getHeight();

        Canvas canvas = new Canvas(waterMarkedBitmap);
        canvas.drawBitmap(waterMarkedBitmap, 0f, 0f, null);

        double heightByWidth = h / (double) w;
        double angleOfRotation = Math.toDegrees(Math.atan(heightByWidth));

        double diagonalInPixels = Math.sqrt((h * h) + (w * w));

        Paint paint = new Paint();

        paint.setColor(ContextCompat.getColor(context, color));

        // Range of Unicode - 0x0900 to 0x097F
        boolean isHindi = false;
        for (char c : waterMarkText.toCharArray()) {
            if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.DEVANAGARI) {
                isHindi = true;
                break;
            }
        }

        Log.d("Hindi?", isHindi + "");
        if (textSize == -1) {
            /*
             * Follows binary search to find the text size which fits bitmap diagonal properly
             */
            float hi = (float) diagonalInPixels;
            float lo = 2;
            final float threshold = 0.5f;
            while ((hi - lo) > threshold) {
                float size = (hi + lo) / 2;
                paint.setTextSize(size);
                if (paint.measureText(waterMarkText) >= diagonalInPixels)
                    hi = size; // too big
                else
                    lo = size; // too small
            }
            // Use lo so that we undershoot rather than overshoot

            if (isHindi) {
                textSize = (int) (lo - (0.15 * lo));
            } else {
                textSize = (int) (lo - (0.11 * lo));
            }
            paint.setTextSize(textSize);
        } else {
            paint.setTextSize(sp2px(textSize));
        }

        paint.setAntiAlias(true);
        paint.setUnderlineText(false);
        if (alpha <= 0) {
            alpha = 5;
        } else if (alpha > 255) {
            alpha = 255;
        }
        paint.setAlpha(alpha);
        Log.d("Bitmap height", h + "");

        if (rotationAngle == -1.0D) {

            if (isHindi) {
                yCoordinate = 120;
            } else {
                yCoordinate = 60;
            }
            canvas.rotate((float) angleOfRotation, xCoordinate, yCoordinate);
            canvas.drawText(waterMarkText, xCoordinate, yCoordinate, paint);
            canvas.rotate(-(float) (angleOfRotation), xCoordinate, yCoordinate);

        } else {
            if (isHindi) {
                yCoordinate = 120;
            } else {
                yCoordinate = 60;
            }
            canvas.rotate((float) rotationAngle, xCoordinate, yCoordinate);
            canvas.drawText(waterMarkText, xCoordinate, yCoordinate, paint);
            canvas.rotate(-((float) rotationAngle), xCoordinate, yCoordinate);
        }

        return waterMarkedBitmap;
    }

    /*
        Generates scaled down version of bitmap provided, with width of bitmap equal to screen size, and
        height generated based on screen width (maintaining aspect ratio of bitmap)
     */
    public Bitmap createScaledBitmapWithScreenWidth(Bitmap bmp) {
        int[] screenDims = getScreenWidthAndHeight();
        float newHeight = (bmp.getHeight() / (float) bmp.getWidth()) * (float) screenDims[0];

        return Bitmap.createScaledBitmap(bmp, screenDims[0], (int) newHeight, false);
    }

    public int[] getScreenWidthAndHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((AppCompatActivity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return new int[]{displayMetrics.widthPixels, displayMetrics.heightPixels};
    }

    public float sp2px(float sizeInSp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                sizeInSp, context.getResources().getDisplayMetrics());
    }

    public int pixelsToDp(float px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static class Builder {
        private final Context context;
        private final Bitmap bitmap;
        private final String waterMarkText;

        private int alpha = 50;
        private int textSize = -1;
        private double rotationAngle = -1.0D;
        private @ColorRes
        int color = R.color.red_for_watermark;
        private int xCoordinate = 0;
        private int yCoordinate;

        public Builder(Context context, Bitmap bitmap, String waterMarkText) {
            this.context = context;
            this.bitmap = bitmap;
            this.waterMarkText = waterMarkText;
        }

        public Builder setAlpha(int alpha) {
            this.alpha = alpha;
            return this;
        }

        public Builder setTextSize(int textSize) {
            this.textSize = textSize;
            return this;
        }

        public Builder setRotationAngle(double rotationAngle) {
            this.rotationAngle = rotationAngle;
            return this;
        }

        public Builder setColor(int color) {
            this.color = color;
            return this;
        }

        public Builder setxCoordinate(int xCoordinate) {
            this.xCoordinate = xCoordinate;
            return this;
        }

        public Builder setyCoordinate(int yCoordinate) {
            this.yCoordinate = yCoordinate;
            return this;
        }

        public WaterMarkProvider build() {
            return new WaterMarkProvider(this);
        }

    }
}
