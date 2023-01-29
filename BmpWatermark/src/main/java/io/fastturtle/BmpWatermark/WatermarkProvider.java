package io.fastturtle.BmpWatermark;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import androidx.annotation.ColorRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.lang.reflect.Field;

/**
 * Generates watermark over a bitmap. The placement, color, size, and transparency of watermark can be customised.
 */

public class WatermarkProvider {
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
    private int xCoordinate;
    private int yCoordinate;

    /**
     * @param builder builder instance containing context, bitmap and watermark text
     */
    public WatermarkProvider(Builder builder) {
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

    /**
     * @return text used for watermarking
     */
    public String getWaterMarkText() {
        return waterMarkText;
    }

    /**
     * @return transparency level of watermark (0-255)
     */
    public int getAlpha() {
        return alpha;
    }

    /**
     * @return size of watermark text as a integer value
     */
    public int getTextSize() {
        return textSize;
    }

    /**
     * @return angle of rotation of watermark text
     */
    public double getRotationAngle() {
        return rotationAngle;
    }

    /**
     * @return id of color used for watermark
     */
    public int getColor() {
        return color;
    }

    /**
     * @return x-coordinate of top-left of watermark
     */
    public int getxCoordinate() {
        return xCoordinate;
    }

    /**
     * @return y-coordinate of top-left of watermark
     */
    public int getyCoordinate() {
        return yCoordinate;
    }

    /**
     * @return resulting watermarked bitmap, after all customisations being applied
     */
    public Bitmap getWaterMarkedBitmap() {
        waterMarkedBitmap = createScaledBitmapWithScreenWidth(waterMarkedBitmap);
        int w = waterMarkedBitmap.getWidth();
        int h = waterMarkedBitmap.getHeight();

        float aspectRatio = h / (float) w;

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

        int[] screenDim = getScreenWidthAndHeight();

        if (rotationAngle == -1.0D) {

            if (yCoordinate == -1)
                yCoordinate = (int) ((100 / 2296f) * screenDim[1]);
            if (xCoordinate == -1)
                xCoordinate = 0;

            if (aspectRatio >= 0.45 && aspectRatio <= 0.90) {
                canvas.rotate((float) (angleOfRotation - (0.1 * angleOfRotation)), xCoordinate, yCoordinate);
                canvas.drawText(waterMarkText, xCoordinate, yCoordinate, paint);
                canvas.rotate(-(float) (angleOfRotation - (0.1 * angleOfRotation)), xCoordinate, yCoordinate);
            } else {
                canvas.rotate((float) (angleOfRotation + (0.04 * angleOfRotation)), xCoordinate, yCoordinate);
                canvas.drawText(waterMarkText, xCoordinate, yCoordinate, paint);
                canvas.rotate(-(float) (angleOfRotation + (0.04 * angleOfRotation)), xCoordinate, yCoordinate);
            }

        } else {
            if (yCoordinate == -1)
                yCoordinate = (int) ((100 / 2296f) * screenDim[1]);
            if (xCoordinate == -1)
                xCoordinate = 0;

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

    /**
     * @param bmp the bitmap to be scaled down
     * @return scaled down version of bitmap passed
     */
    public Bitmap createScaledBitmapWithScreenWidth(Bitmap bmp) {
        int[] screenDims = getScreenWidthAndHeight();
        float newHeight = (bmp.getHeight() / (float) bmp.getWidth()) * (float) screenDims[0];

        return Bitmap.createScaledBitmap(bmp, screenDims[0], (int) newHeight, false);
    }

    /**
     * @return an array with first element being device screen width, and second element being
     * device screen height
     */
    public int[] getScreenWidthAndHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((AppCompatActivity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return new int[]{displayMetrics.widthPixels, displayMetrics.heightPixels};
    }

    /**
     * @param sizeInSp size passed as sp, to be converted to pixels
     * @return size converted to pixel
     */
    public float sp2px(float sizeInSp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                sizeInSp, context.getResources().getDisplayMetrics());
    }

    /**
     * @param px size passed as pixel, to be converted to dp
     * @return size converted to dp
     */
    public int pixelsToDp(float px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    /**
     * Builder class which builds watermark step-by-step
     */

    public static class Builder {
        private final Context context;
        private final Bitmap bitmap;
        private final String waterMarkText;

        private int alpha = 50;
        private int textSize = -1;
        private double rotationAngle = -1.0D;
        private @ColorRes
        int color = R.color.red_for_watermark;
        private int xCoordinate = -1;
        private int yCoordinate = -1;

        /**
         *
         * @param context used for accessing bitmap helper classes, and resource classes
         * @param bitmap bitmap to be watermarked
         * @param waterMarkText text to be shown for watermark
         */
        public Builder(Context context, Bitmap bitmap, String waterMarkText) {
            this.context = context;
            this.bitmap = bitmap;
            this.waterMarkText = waterMarkText;
        }
		/**
         *
         * @param alpha sets a transparency level for watermark text. Value must be between 0 to 255, 0 being invisible, 255 being fully visible.
         */
        public Builder setAlpha(int alpha) {
            this.alpha = alpha;
            return this;
        }
		/**
         *
         * @param textSize sets a custom watermark text size. Value must be in sp to work properly.
         */
        public Builder setTextSize(int textSize) {
            this.textSize = textSize;
            return this;
        }

		/**
         *
         * @param rotationAngle sets a custom angle of rotation for watermark text. Value must be in degrees to work properly.
         */
        public Builder setRotationAngle(double rotationAngle) {
            this.rotationAngle = rotationAngle;
            return this;
        }

        /**
         *
         * @param color sets a custom color for watermark text. Must be a color reference from colors.xml
         */
        public Builder setColor(int color) {
            this.color = color;
            return this;
        }

        /**
         *
         * @param xCoordinate sets a custom starting (top-left) x-position for watermark text.
         */
        public Builder setxCoordinate(int xCoordinate) {
            this.xCoordinate = xCoordinate;
            return this;
        }

        /**
         *
         * @param yCoordinate sets a custom starting (top-left) y-position for watermark text.
         */
        public Builder setyCoordinate(int yCoordinate) {
            this.yCoordinate = yCoordinate;
            return this;
        }

        /**
         *
         * @return instance of WatermarkProvider which contains watermarked bitmap
         */
        public WatermarkProvider build() {
            return new WatermarkProvider(this);
        }

    }
}
