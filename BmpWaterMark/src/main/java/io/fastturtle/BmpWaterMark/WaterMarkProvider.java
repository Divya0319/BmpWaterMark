package io.fastturtle.BmpWaterMark;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

/**
 * @Author: Divya Gupta
 * @Date: 08-Jan-23
 */

@SuppressWarnings("ConstantConditions")
public class WaterMarkProvider {
    private Context ctx;


    /*
        Provide context to constructor once
     */
    public WaterMarkProvider(Context ctx) {
        this.ctx = ctx;
    }

    /**
     * @param src           - Bitmap to be watermarked
     * @param watermarkText - text to be used for watermarking(should not be too small in character count)
     * @return - watermarked bitmap
     */
    public Bitmap generateWaterMarkedBitmap(@NonNull Bitmap src, @NonNull String watermarkText) {

        src = createScaledBitmapWithScreenWidth(src);
        int w = src.getWidth();
        int h = src.getHeight();

        Canvas canvas = new Canvas(src);
        canvas.drawBitmap(src, 0f, 0f, null);

        double heightByWidth = h / (double) w;
        double angle = Math.toDegrees(Math.atan(heightByWidth));

        double diagonalInPixels = Math.sqrt((h * h) + (w * w));

        Paint paint = new Paint();
        paint.setColor(ContextCompat.getColor(ctx, R.color.red_for_watermark));

        /*
         * Follows binary search to find the text size which fits bitmap diagonal properly
         */
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

        String hindiText = ctx.getString(R.string.vastu);
        if (watermarkText.contains(hindiText)) {
            paint.setTextSize((float) (lo - (0.15 * lo)));
        } else {
            paint.setTextSize((float) (lo - (0.11 * lo)));
        }


        paint.setAntiAlias(true);
        paint.setAlpha(50);
        paint.setUnderlineText(false);
        if (watermarkText.contains(hindiText)) {
            canvas.rotate((float) (angle), 0, 120);
            canvas.drawText(watermarkText, 0, 120, paint);
            canvas.rotate(-(float) (angle), 0, 120);
        } else {
            canvas.rotate((float) (angle), 0, 60);
            canvas.drawText(watermarkText, 0, 60, paint);
            canvas.rotate(-(float) (angle), 0, 60);
        }

        return src;
    }

    /**
     * @param src           - Bitmap to be watermarked
     * @param watermarkText - text to be used for watermarking(should not be too small in character count)
     * @param textSizeInSp  - font size to be set for watermark (can be passed null for default behaviour)
     * @param angle         - rotation angle for watermark(can be passed null for default behaviour)
     * @param color         - color for watermark(must be a color defined in resources)
     * @param alpha         - transparency for watermark(must be between 0-255, 0 being 100% transparent(invisible), and 255 means 0% transparent(fully visible))
     * @param px            - x coordinate for starting of watermark text(if passed null, uses default position)
     * @param py            - y coordinate for starting of watermark text(if passed null, uses default position)
     * @return - watermarked bitmap
     */
    public Bitmap generateWaterMarkedBitmap(@NonNull Bitmap src, @NonNull String watermarkText,
                                            @Nullable Integer textSizeInSp, @Nullable Double angle,
                                            @Nullable @ColorRes Integer color, @Nullable Integer alpha,
                                            @Nullable Integer px, @Nullable Integer py) {

        src = createScaledBitmapWithScreenWidth(src);
        int w = src.getWidth();
        int h = src.getHeight();

        Canvas canvas = new Canvas(src);
        canvas.drawBitmap(src, 0f, 0f, null);

        double heightByWidth = h / (double) w;
        double angleOfRotation = Math.toDegrees(Math.atan(heightByWidth));

        double diagonalInPixels = Math.sqrt((h * h) + (w * w));

        Paint paint = new Paint();
        if (color == null) {
            paint.setColor(ContextCompat.getColor(ctx, R.color.red_for_watermark));
        } else {
            paint.setColor(ContextCompat.getColor(ctx, color));
        }
        String hindiText = ctx.getString(R.string.vastu);

        if (textSizeInSp == null) {
            /*
             * Follows binary search to find the text size which fits bitmap diagonal properly
             */
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

            if (watermarkText.contains(hindiText)) {
                paint.setTextSize((float) (lo - (0.15 * lo)));
            } else {
                paint.setTextSize((float) (lo - (0.11 * lo)));
            }
        } else {
            paint.setTextSize(sp2px(textSizeInSp));
        }


        paint.setAntiAlias(true);
        paint.setUnderlineText(false);
        if (alpha == null) {
            paint.setAlpha(50);
        } else {
            paint.setAlpha(alpha);
        }

        if (angle == null) {
            if (watermarkText.contains(hindiText)) {
                if (px == null && py == null) {
                    canvas.rotate((float) angleOfRotation, 0, 120);
                    canvas.drawText(watermarkText, 0, 120, paint);
                    canvas.rotate(-((float) angleOfRotation), 0, 120);
                } else {
                    canvas.rotate((float) angleOfRotation, px, py);
                    canvas.drawText(watermarkText, px, py, paint);
                    canvas.rotate(-((float) angleOfRotation), px, py);
                }
            } else {
                if (px == null && py == null) {
                    canvas.rotate((float) angleOfRotation, 0, 60);
                    canvas.drawText(watermarkText, 0, 60, paint);
                    canvas.rotate(-((float) angleOfRotation), 0, 60);
                } else {
                    canvas.rotate((float) angleOfRotation, px, py);
                    canvas.drawText(watermarkText, px, py, paint);
                    canvas.rotate(-((float) angleOfRotation), px, py);
                }

            }
        } else {
            if (watermarkText.contains(hindiText)) {
                if (px == null && py == null) {
                    canvas.rotate(angle.floatValue(), 0, 120);
                    canvas.drawText(watermarkText, 0, 120, paint);
                    canvas.rotate(-(angle.floatValue()), 0, 120);
                } else {
                    canvas.rotate(angle.floatValue(), px, py);
                    canvas.drawText(watermarkText, px, py, paint);
                    canvas.rotate(-(angle.floatValue()), px, py);
                }
            } else {
                if (px == null && py == null) {
                    canvas.rotate(angle.floatValue(), 0, 60);
                    canvas.drawText(watermarkText, 0, 60, paint);
                    canvas.rotate(-(angle.floatValue()), 0, 60);
                } else {
                    canvas.rotate(angle.floatValue(), px, py);
                    canvas.drawText(watermarkText, px, py, paint);
                    canvas.rotate(-(angle.floatValue()), px, py);
                }
            }
        }

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

    public float sp2px(float sizeInSp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                sizeInSp, ctx.getResources().getDisplayMetrics());
    }

    public int pixelsToDp(float px) {
        DisplayMetrics displayMetrics = ctx.getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
