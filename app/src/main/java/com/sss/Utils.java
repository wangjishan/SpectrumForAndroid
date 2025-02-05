package com.sss;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.TypedValue;

import java.lang.ref.WeakReference;

public class Utils {
    /**
     * dp转px
     */
    public static int dp2px(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, Resources.getSystem().getDisplayMetrics());
    }

    /**
     * sp转px
     */
    public static int sp2px(float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, Resources.getSystem().getDisplayMetrics());
    }



    public static Bitmap getScaleBitmap(int r, int res, Resources resources) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        BitmapFactory.decodeResource(resources, res, opts);
        int width = opts.outWidth;
        int height = opts.outHeight;
        float scaleWidth = 0.f, scaleHeight = 0.f;
        if (width > r || height > r) {
            // 缩放
            scaleWidth = ((float) width) / r;
            scaleHeight = ((float) height) / r;
        }
        opts.inJustDecodeBounds = false;
        float scale = Math.max(scaleWidth, scaleHeight);
        opts.inSampleSize = (int) scale;
        opts.inDither = true;
        WeakReference<Bitmap> weak = new WeakReference<>(BitmapFactory.decodeResource(resources, res, opts));
        return Bitmap.createScaledBitmap(weak.get(), r, r, true);
    }

    /**
     * 获取圆形bitmap
     */
    public static Bitmap getCirleBitmap(int r,Bitmap bmp,Paint paint) {
        WeakReference<Bitmap> weak = new WeakReference<>(Bitmap.createBitmap(r, r, Bitmap.Config.ARGB_8888));
        Canvas canvas = new Canvas(weak.get());
        Path path = new Path();
        path.addCircle(r / 2, r / 2, r / 2, Path.Direction.CW);
        canvas.clipPath(path);
        canvas.drawBitmap(bmp, 0, 0, paint);
        return weak.get();
    }
}
