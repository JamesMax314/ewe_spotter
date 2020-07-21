package com.example.ewe_spotter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.graphics.Bitmap;

import androidx.core.content.ContextCompat;
import androidx.core.view.MotionEventCompat;


public class EditImageView extends View {
    private static final String TAG = "EditImage";

    float dpi;

    private float left;
    private float right;
    private float top;
    private float bottom;
    float xPos = 0;
    float yPos = 0;
    private Bitmap imgBitmap;

    private int mActivePointerId;
    private float dragBeginX;
    private float dragBeginY;

    float scaleFactor = 1.f;
    float scale = 1.f;
    float focusX;
    float focusY;
    float xScale;
    float yScale;


    public EditImageView(Context context) {
        super(context);
    }

    public EditImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        dpi = context.getResources().getDisplayMetrics().density;
        setFocusable(true);
    }

    public void setImageBitmap(Bitmap bitmap){
        imgBitmap = bitmap;
    }

    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        drawClippedRectangle(canvas);
        drawBox(canvas);
    }

    private void drawClippedRectangle(Canvas canvas) {
        // Set the boundaries of the clipping rectangle for whole picture.
        Resources res = getResources();
        float width = res.getDimension(R.dimen.thumbnail);
        float height = res.getDimension(R.dimen.thumbnail);
        left = 0;
        right = width;
        top = 0;
        bottom = height;
        canvas.clipRect(left, top,
                right, bottom);
        if (imgBitmap != null) {
            Bitmap imgBitmapNew =
                    Bitmap.createScaledBitmap(imgBitmap, (int) (imgBitmap.getWidth() * scale),
                            (int) (imgBitmap.getHeight() * scale),
                            false);
            canvas.drawBitmap(imgBitmapNew, xPos + xScale, yPos + yScale, null);
        }
    }

    private void drawBox(Canvas canvas){
        Resources res = getResources();
        float width = res.getDimension(R.dimen.thumbnail);
        float height = res.getDimension(R.dimen.thumbnail);
        Paint p = new Paint();
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(res.getDimension(R.dimen.line_width));
        p.setColor(ContextCompat.getColor(getContext(), R.color.white));
        canvas.drawRect(0, 0, width, height, p);
    }

    class scaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
//        private PointF viewportFocus = new PointF();
        private float lastSpan;

        @Override
        public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
            lastSpan = scaleGestureDetector.getCurrentSpan();
            focusX = scaleGestureDetector.getFocusX();
            focusY = scaleGestureDetector.getFocusY();
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {

            scale = scaleFactor*(scaleGestureDetector.getCurrentSpan()/lastSpan);
//            Log.i(TAG, String.valueOf(span));

            xScale =  (focusX - scale * focusX);
            yScale = (focusY - scale * focusY);

            Log.i(TAG, String.valueOf(dpi));
            return true;
        }
    }

    ScaleGestureDetector detector =
            new ScaleGestureDetector(EditImageView.this.getContext(), new scaleListener());

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        detector.onTouchEvent(ev);
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                final int pointerIndex = MotionEventCompat.getActionIndex(ev);
                final float x = MotionEventCompat.getX(ev, pointerIndex);
                final float y = MotionEventCompat.getY(ev, pointerIndex);

                // Remember where we started (for dragging)
                dragBeginX = x;
                dragBeginY = y;
                // Save the ID of this pointer (for dragging)
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                break;
            }
            case MotionEvent.ACTION_MOVE:
                final int pointerIndex =
                        MotionEventCompat.findPointerIndex(ev, mActivePointerId);
                final float x = MotionEventCompat.getX(ev, pointerIndex);
                final float y = MotionEventCompat.getY(ev, pointerIndex);

                // Calculate the distance moved
                final float dx = x - dragBeginX;
                final float dy = y - dragBeginY;

                yPos += dy;
                xPos += dx;

                dragBeginX = x;
                dragBeginY = y;

                invalidate();
                break;
        }
        return true;
    }

    public Bitmap outputImage(){
        Resources res = getResources();
        float width = res.getDimension(R.dimen.thumbnail);
        float height = res.getDimension(R.dimen.thumbnail);
        Bitmap imgBitmapNew =
                Bitmap.createScaledBitmap(imgBitmap, (int) (imgBitmap.getWidth()*scale),
                        (int) (imgBitmap.getHeight()*scale), false);
        imgBitmapNew = Bitmap.createBitmap(imgBitmapNew, (int) (xPos+xScale),
                (int) (yPos+yScale), (int) (width), (int) (height));
        return imgBitmapNew;
    }

    public float getScale(){
        return scale;
    }


    public float getxPos(){
        return xPos;
    }

    public float getyPos(){
        return yPos;
    }

    public float getxScale(){
        return xScale;
    }

    public float getyScale(){
        return yScale;
    }
}
