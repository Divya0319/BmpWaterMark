package io.fastturtle.BmpWaterMark;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
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
    private Context ctx;


    /*
        Provide context to constructor once
     */
    public WaterMarkProvider(Context ctx) {
        this.ctx = ctx;
    }


    /*
        Generates Watermarked bitmap with custom watermark text, custom color, and custom transparency.
        Watermark starts by default from left top-most corner of the bitmap, and rotated
        towards principal diagonal of Bitmap(from top-left to bottom-right of bitmap)
    */
    public Bitmap generateWaterMarkedBitmap(Bitmap src, String watermarkText, @ColorRes int color, int alpha) {

        src = createScaledBitmapWithScreenWidth(src);
        int w = src.getWidth();
        int h = src.getHeight();

        Canvas canvas = new Canvas(src);
        canvas.drawBitmap(src, 0f, 0f, null);

        double heightByWidth = h / (double) w;
        double angle = Math.toDegrees(Math.atan(heightByWidth));

        double diagonalInPixels = Math.sqrt((h * h) + (w * w));
        Log.d("Angle", angle + "");
//        float sizeInsp;

//        if (angle > 49) {
//            sizeInsp = 110f;
//        } else if (angle >= 44) {
//            sizeInsp = 94f;
//        } else if (angle >= 38) {
//            sizeInsp = 86f;
//        } else if (angle >= 35) {
//            sizeInsp = 78f;
//        } else {
//            sizeInsp = 68f;
//        }
//
//        // Convert the dips to pixels
//        int sizeInPixels = sp2px(sizeInsp);

        Paint paint = new Paint();
        paint.setColor(ContextCompat.getColor(ctx, color));

        float hi = (float) diagonalInPixels;
        float lo = 2;
        final float threshold = 0.5f;
        while ((hi - lo) > threshold) {
            float size = (hi + lo) / 2;
            paint.setTextSize(size);
            if (paint.measureText(watermarkText) >= diagonalInPixels)
                hi = size; // too big
            else
                lo = size; // too small
        }
        // Use lo so that we undershoot rather than overshoot

        paint.setTextSize((float) (lo - (0.11 * lo)));

        paint.setAntiAlias(true);
        paint.setAlpha(alpha);
        paint.setUnderlineText(false);
        canvas.rotate((float) (angle), 0, 60);
        canvas.drawText(watermarkText, 0, 60, paint);
        canvas.rotate(-(float) (angle), 0, 60);

        return src;
    }

    /*
        Generates Watermarked bitmap with custom watermark text, and custom transparency.
        Watermark generated with default red color, and starts by default from left top-most corner of the bitmap,
        and rotated towards principal diagonal
        (uses trigonometric function 'arctan()' to calculate the rotation angle)
        of Bitmap(from top-left to bottom-right of bitmap)
     */
    public Bitmap generateWaterMarkedBitmap(Bitmap src, String watermarkText, int alpha) {

        src = createScaledBitmapWithScreenWidth(src);
        int w = src.getWidth();
        int h = src.getHeight();

        Canvas canvas = new Canvas(src);
        canvas.drawBitmap(src, 0f, 0f, null);

        double heightByWidth = h / (double) w;
        double angle = Math.toDegrees(Math.atan(heightByWidth));
        Log.d("Angle", angle + "");
        float sizeInsp;

        if (angle > 49) {
            sizeInsp = 110f;
        } else if (angle >= 44) {
            sizeInsp = 94f;
        } else if (angle >= 38) {
            sizeInsp = 86f;
        } else if (angle >= 35) {
            sizeInsp = 78f;
        } else {
            sizeInsp = 68f;
        }

        // Convert the dips to pixels
        int sizeInPixels = sp2px(sizeInsp);

        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#DA1414"));
        paint.setTextSize(sizeInPixels);
        paint.setAntiAlias(true);
        paint.setAlpha(alpha);
        paint.setUnderlineText(false);
        canvas.rotate((float) (angle), 10, 150);
        canvas.drawText(watermarkText, 10, 150, paint);
        canvas.rotate(-(float) (angle), 10, 150);

        return src;
    }

    /*
        Generates Watermarked bitmap with custom watermark text, custom text size, custom color,
        and custom transparency.
        Uses default angle calculation and default start coordinates as mentioned above.
     */
    public Bitmap generateWaterMarkedBitmap(Bitmap src, String watermarkText, float textSizeInSp, @ColorRes int color, int alpha) {

        src = createScaledBitmapWithScreenWidth(src);
        int w = src.getWidth();
        int h = src.getHeight();

        Canvas canvas = new Canvas(src);
        canvas.drawBitmap(src, 0f, 0f, null);

        double heightByWidth = h / (double) w;
        double angle = Math.toDegrees(Math.atan(heightByWidth));
        Log.d("Angle", angle + "");

        // Convert the dips to pixels
        int sizeInPixels = sp2px(textSizeInSp);

        Paint paint = new Paint();
        paint.setColor(ContextCompat.getColor(ctx, color));
        paint.setTextSize(sizeInPixels);
        paint.setAntiAlias(true);
        paint.setAlpha(alpha);
        paint.setUnderlineText(false);
        canvas.rotate((float) (angle), 10, 150);
        canvas.drawText(watermarkText, 10, 150, paint);
        canvas.rotate(-(float) (angle), 10, 150);

        return src;
    }

    /*
            Generates Watermarked bitmap with custom watermark text, custom text size, custom rotation angle,
            custom color, and custom transparency.
            Uses default angle calculation and default start coordinates as mentioned above.
     */

    public Bitmap generateWaterMarkedBitmap(Bitmap src, String watermarkText, double angle, @ColorRes int color, int alpha) {

        src = createScaledBitmapWithScreenWidth(src);

        Canvas canvas = new Canvas(src);
        canvas.drawBitmap(src, 0f, 0f, null);


        Log.d("Angle", angle + "");
        float sizeInsp;

        if (angle > 49) {
            sizeInsp = 110f;
        } else if (angle >= 44) {
            sizeInsp = 94f;
        } else if (angle >= 38) {
            sizeInsp = 86f;
        } else if (angle >= 35) {
            sizeInsp = 78f;
        } else {
            sizeInsp = 68f;
        }

        // Convert the dips to pixels
        int sizeInPixels = sp2px(sizeInsp);

        Paint paint = new Paint();
        paint.setColor(ContextCompat.getColor(ctx, color));
        paint.setTextSize(sizeInPixels);
        paint.setAntiAlias(true);
        paint.setAlpha(alpha);
        paint.setUnderlineText(false);
        canvas.rotate((float) (angle), 10, 150);
        canvas.drawText(watermarkText, 10, 150, paint);
        canvas.rotate(-(float) (angle), 10, 150);

        return src;
    }

    /*
        Generates Watermarked bitmap with custom watermark text, custom text size, custom rotation angle,
        custom color, and custom transparency.
        Uses default angle calculation and default start coordinates as mentioned above.
     */

    public Bitmap generateWaterMarkedBitmap(Bitmap src, String watermarkText, float textSizeInSp, double angle, @ColorRes int color, int alpha) {

        src = createScaledBitmapWithScreenWidth(src);

        Canvas canvas = new Canvas(src);
        canvas.drawBitmap(src, 0f, 0f, null);

        // Convert the dips to pixels
        int sizeInPixels = sp2px(textSizeInSp);

        Paint paint = new Paint();
        paint.setColor(ContextCompat.getColor(ctx, color));
        paint.setTextSize(sizeInPixels);
        paint.setAntiAlias(true);
        paint.setAlpha(alpha);
        paint.setUnderlineText(false);
        canvas.rotate((float) (angle), 10, 150);
        canvas.drawText(watermarkText, 10, 150, paint);
        canvas.rotate(-(float) (angle), 10, 150);

        return src;
    }

    /*
        Generates Watermarked bitmap with custom watermark text, custom text size, custom color,
        and custom transparency.
        But start x,y coordinates can also be customised here(values are in pixels)
     */
    public Bitmap generateWaterMarkedBitmap(Bitmap src, String watermarkText, float textSizeInSp, double angle, @ColorRes int color, int alpha, int px, int py) {

        src = createScaledBitmapWithScreenWidth(src);

        Canvas canvas = new Canvas(src);
        canvas.drawBitmap(src, 0f, 0f, null);

        // Convert the dips to pixels
        int sizeInPixels = sp2px(textSizeInSp);

        Paint paint = new Paint();
        paint.setColor(ContextCompat.getColor(ctx, color));
        paint.setTextSize(sizeInPixels);
        paint.setAntiAlias(true);
        paint.setAlpha(alpha);
        paint.setUnderlineText(false);
        canvas.rotate((float) (angle), px, py);
        canvas.drawText(watermarkText, px, py, paint);
        canvas.rotate(-(float) (angle), px, py);

        return src;
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
        ((AppCompatActivity) ctx).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return new int[]{displayMetrics.widthPixels, displayMetrics.heightPixels};
    }

    public int sp2px(float sizeInDp) {
        DisplayMetrics displayMetrics = ctx.getResources().getDisplayMetrics();
        return Math.round(sizeInDp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public int pixelsToDp(float px) {
        DisplayMetrics displayMetrics = ctx.getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
