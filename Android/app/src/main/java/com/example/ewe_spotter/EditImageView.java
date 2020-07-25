package com.example.ewe_spotter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
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

    private Handler mainHandler = new Handler();

    float dpi;

    private float left;
    private float width;
    private float top;
    private float height;
    float xPos = 0;
    float yPos = 0;
    float xVel = 0;
    float yVel = 0;
    float wVel = 0;
    float hVel = 0;
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

    float bitmapWidth;
    float bitmapHeight;

    boolean runAnimation;


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
        left = 0;
        width = getWidth();
        top = 0;
        height = getHeight();
    }

    private void drawClippedRectangle(Canvas canvas) {
        // Set the boundaries of the clipping rectangle for whole picture.
        Resources res = getResources();

        bitmapWidth = imgBitmap.getWidth() * scale;
        bitmapHeight = imgBitmap.getHeight() * scale;
//        canvas.clipRect(left, top,
//                right, bottom);
        if (imgBitmap != null) {
            Bitmap imgBitmapNew =
                    Bitmap.createScaledBitmap(imgBitmap, (int) (bitmapWidth),
                            (int) (bitmapHeight),
                            false);
            canvas.drawBitmap(imgBitmapNew, xPos + xScale, yPos + yScale, null);
        }
    }

    private void drawBox(Canvas canvas){
        Resources res = getResources();
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
                runAnimation = false;
                xVel = 0;
                yVel = 0;
                wVel = 0;
                hVel = 0;

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
                double expDecayX = 1;
                double expDecayY = 1;
                double expScale = 1e-2;

                // TODO: 23/07/20 change width and height to bitmap width and height
                if (xPos > 0){
                    expDecayX = Math.exp(-Math.abs(xPos)*expScale);
                } else if (xPos < -(bitmapWidth-width)){
                    expDecayX = Math.exp(-Math.abs(xPos + (bitmapWidth-width))*expScale);
                }
                if (yPos > 0){
                    expDecayY = Math.exp(-Math.abs(yPos)*expScale);
                } else if (yPos < -(bitmapHeight-height)){
                    expDecayY = Math.exp(-Math.abs(yPos + (bitmapHeight-height))*expScale);
                }

                // Calculate the distance moved
                final float dx = x - dragBeginX;
                final float dy = y - dragBeginY;

                yPos += dy*expDecayY;
                xPos += dx*expDecayX;

                dragBeginX = x;
                dragBeginY = y;

                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                timeRunnable runnableYFar = new timeRunnable(1000 , 100);
                new Thread(runnableYFar).start();
        }
        return true;
    }

    public void updateFrame(){
        invalidate();
    }

    public Bitmap outputImage(){
        // Scale bitmap
        Bitmap imgBitmapNew =
                Bitmap.createScaledBitmap(imgBitmap, (int) (imgBitmap.getWidth()*scale),
                        (int) (imgBitmap.getHeight()*scale), false);
        // Crop bitmap
        imgBitmapNew = Bitmap.createBitmap(imgBitmapNew, (int) -(xPos+xScale),
                (int) -(yPos+yScale), (int) (width), (int) (height));
        return imgBitmapNew;
    }

    // TODO: 23/07/20 create new thread for animation timing
    class timeRunnable implements Runnable{
        int value;
        long startTime;
        long lastTime;
        long interval;
        float constant;

        timeRunnable(long period, int samples){
            runAnimation = true;
            interval = period/samples;
            constant = (float) Math.pow((4*2*Math.PI/period), 2);
        }

        private float f(float current, float natural){
            return -constant*(current-natural);
        }

        @Override
        public void run() {
            startTime = System.currentTimeMillis();
            lastTime = startTime;
            boolean stopX = false;
            boolean stopY = false;
            while (runAnimation) {
                long time = System.currentTimeMillis();
                if ((time - lastTime) > interval) {
                    if (bitmapWidth < width) {
                        float wAcc = f(bitmapWidth, width);
                        wVel += wAcc * interval;
                        bitmapWidth += wVel * interval;
                    } else if (xPos > 0) {
                        float xAcc = f(xPos, 0);
                        xVel += xAcc * interval;
                        xPos += xVel * interval;
                    } else if (xPos < -(bitmapWidth - width)) {
                        float xAcc = f(xPos, -(bitmapWidth - width));
                        xVel += xAcc * interval;
                        xPos += xVel * interval;
                    } else {
                        stopX = true;
                    }

                    if (bitmapHeight < height) {
                        float hAcc = f(bitmapHeight, height);
                        hVel += hAcc * interval;
                        bitmapHeight += hVel * interval;
                    } else if (yPos > 0) {
                        float yAcc = f(yPos, 0);
                        yVel += yAcc * interval;
                        yPos += yVel * interval;
                    } else if (yPos < -(bitmapHeight - height)) {
                        float yAcc = f(yPos, -(bitmapHeight - height));
                        yVel += yAcc * interval;
                        yPos += yVel * interval;
                    } else {
                        stopY = true;
                    }

                    if (stopX && stopY){
                        runAnimation = false;
                    } else {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                updateFrame();
                            }
                        });
                        lastTime = time;
                    }
                }
            }
        }

        public int getValue() {
            return value;
        }
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
