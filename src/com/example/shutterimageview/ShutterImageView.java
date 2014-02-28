package com.example.shutterimageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

public class ShutterImageView extends ImageView {

    private RectF bounds;

    private RectF boundsOri;

    private Bitmap bitmap;

    private Paint imagePaint;

    private PorterDuffXfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);

    public ShutterImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public ShutterImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ShutterImageView(Context context) {
        super(context);
        init();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);

    }

    private void init() {
        imagePaint = new Paint();
        imagePaint.setFilterBitmap(false);
        imagePaint.setAntiAlias(true);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getDrawable() != null) {
            int width = getLayoutParams().width;
            int height = getLayoutParams().height;

            if (bitmap == null) {
                bitmap = ((BitmapDrawable) getDrawable()).getBitmap();
                bitmap = getCroppedBitmap(bitmap);
                anim();
            }

            if (bounds == null) {
                bounds = new RectF(0f, 0f, (float) width, (float) height);
                boundsOri = new RectF(0f, 0f, (float) width, (float) height);
                bounds.offsetTo(0, -bounds.height());
            }

            int sc = canvas.saveLayer(bounds, imagePaint, Canvas.ALL_SAVE_FLAG);
            canvas.drawCircle(boundsOri.centerX(), boundsOri.centerY(), boundsOri.width() / 2,
                    imagePaint);

            imagePaint.setXfermode(xfermode);
            canvas.drawBitmap(bitmap, null, bounds, imagePaint);
            imagePaint.setXfermode(null);
            canvas.restoreToCount(sc);

        }
    }

    Animation animation = new Animation() {

        protected void applyTransformation(float interpolatedTime,
                android.view.animation.Transformation t) {

            if (bounds != null) {
                bounds.offsetTo(0, -bounds.height() * (1 - interpolatedTime));
                invalidate();
            }

            if (interpolatedTime == 1) {}
        };
    };

    public void anim() {

        setAnimation(animation);
        animation.setDuration(1000);
        animation.setInterpolator(new OvershootInterpolator());

        animation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                if (bounds != null) {
                    bounds.offsetTo(0, -bounds.height());
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }
        });
        animation.start();
        invalidate();
    }

    private Bitmap getCroppedBitmap(Bitmap bmp) {

        Bitmap output = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        final Rect rect = new Rect(0, 0, bmp.getWidth(), bmp.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true); //防止抖动，效果会好一些
        //        paint.setMaskFilter(new BlurMaskFilter(5, Blur.NORMAL));

        int sc = canvas
                .saveLayer(0, 0, bmp.getWidth(), bmp.getHeight(), null, Canvas.ALL_SAVE_FLAG);
        canvas.drawCircle(bmp.getWidth() / 2, bmp.getHeight() / 2, bmp.getWidth() / 2, paint);
        paint.setXfermode(xfermode);
        canvas.drawBitmap(bmp, null, rect, paint);
        imagePaint.setXfermode(null);
        canvas.restoreToCount(sc);
        return output;

    }

}
