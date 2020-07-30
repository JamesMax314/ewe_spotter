package com.example.ewe_spotter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.graphics.Bitmap;

import androidx.core.content.ContextCompat;


public class EditImageView extends View {
    private static final String TAG = "EditImage";

    private Handler mainHandler = new Handler();

    public static int RIGHT = 1;
    public static int LEFT = 2;

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
    private Bitmap imgNewBitmap;

    private int mActivePointerId;
    private float dragBeginX;
    private float dragBeginY;

    double expScale = 1e-2;

    float scaleFactor = 1.f;
    float scale = 1.f;
    float focusX;
    float focusY;
    float xScale;
    float yScale;

    float scaleXPos;
    float scaleYPos;

    float bitmapWidth;
    float bitmapHeight;

    float tmpBmpWidth;
    float tmpBmpHeight;

    boolean runAnimation;
    boolean scaling = false;

    private int angle;
    boolean rotating = false;

    private ScaleGestureDetector detector;


    public EditImageView(Context context) {
        super(context);
    }

    public EditImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        dpi = context.getResources().getDisplayMetrics().density;
        detector = new ScaleGestureDetector(context, new scaleListener());
        setFocusable(true);
    }

    public void setImageBitmap(Bitmap bitmap){
        imgBitmap = bitmap;
        imgNewBitmap = bitmap;
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        left = 0;
        width = getWidth();
        top = 0;
        height = getHeight();
        bitmapWidth = imgBitmap.getWidth() * scale;
        bitmapHeight = imgBitmap.getHeight() * scale;
//        if (Math.cos(Math.PI * angle / 180) < 0.1){
//            float temp = bitmapHeight;
//            bitmapHeight = bitmapWidth;
//            bitmapWidth = temp;
//        }

        drawClippedRectangle(canvas);
        drawBox(canvas);
    }

    private void drawClippedRectangle(Canvas canvas) {
        // Set the boundaries of the clipping rectangle for whole picture.
        Resources res = getResources();
        // TODO: 25/07/20 add a variable for orientation and manipulate displayed image accordingly
        float angleRad = (float) (angle * Math.PI / 180);
        Matrix rotMatrix = new Matrix();
        if (rotating) {
            rotMatrix.postScale(scale, scale);
            rotMatrix.postTranslate((xPos + xScale), (yPos + yScale));
            rotMatrix.postRotate(angle, width/2, height/2);
        } else {
            rotMatrix.postScale(scale, scale);
            rotMatrix.postRotate(angle);

//        rotMatrix.postTranslate(width, height);
            // TODO: 29/07/20 add a unction to return the correct xPos and yPos and bmpWidth and Height
            rotMatrix.postTranslate((xPos + xScale), (yPos + yScale));

//        canvas.clipRect(left, top,
//                right, bottom);

            if (angle % 360 == 90) {
                rotMatrix.postTranslate(bitmapHeight, 0);
                tmpBmpHeight = bitmapWidth;
                tmpBmpWidth = bitmapHeight;
            } else if (angle % 360 == 180) {
                rotMatrix.postTranslate(bitmapWidth, bitmapHeight);
            } else if (angle % 360 == 270) {
                rotMatrix.postTranslate(0, bitmapWidth);
                tmpBmpHeight = bitmapWidth;
                tmpBmpWidth = bitmapHeight;
            } else {
                tmpBmpHeight = bitmapHeight;
                tmpBmpWidth = bitmapWidth;
            }
        }
        // TODO: 28/07/20 use matrix multiplication to change the bitmap
        if (imgBitmap != null) {
            canvas.drawBitmap(imgBitmap, rotMatrix, null);
//            Bitmap imgBitmapNew =
//                    Bitmap.createScaledBitmap(imgBitmap, (int) (bitmapWidth),
//                            (int) (bitmapHeight),
//                            false);
//            Bitmap rotatedBitmap = Bitmap.createBitmap(imgBitmapNew, 0, 0,
//                    (int) (bitmapWidth), (int) (bitmapHeight), rotMatrix, true);
//            canvas.drawBitmap(rotatedBitmap, xPos + xScale, yPos + yScale, null);
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

    public void rotate(int direction){
        if (direction == RIGHT){
            rotRunnable runnableYFar = new rotRunnable(angle , angle + 90, 1000, 100);
            new Thread(runnableYFar).start();
//            angle += 90;
        } else if (direction == LEFT){
            rotRunnable runnableYFar = new rotRunnable(angle , angle - 90, 1000, 100);
            new Thread(runnableYFar).start();
//            angle -= 90;
        }
        invalidate();
    }

    public void reset(){
        angle = 0;
        scale = 1;
        xPos = -1;
        yPos = -1;
        invalidate();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getActionMasked();
        detector.onTouchEvent(ev);
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                runAnimation = false;
                xVel = 0;
                yVel = 0;
                wVel = 0;
                hVel = 0;

//                numPointers = ev.getPointerCount();

                final int pointerIndex = ev.getActionIndex();
                final float x = ev.getX(pointerIndex);
                final float y = ev.getY(pointerIndex);

                // Remember where we started (for dragging)
                dragBeginX = x;
                dragBeginY = y;
                // Save the ID of this pointer (for dragging)
                mActivePointerId = ev.getPointerId(0);
                break;
            }
            case MotionEvent.ACTION_MOVE:
                float x;
                float y;
                if (scaling){
                    x = scaleXPos;
                    y = scaleYPos;
                } else {
                    final int pointerIndex =
                            ev.findPointerIndex(mActivePointerId);
                    x = ev.getX(pointerIndex);
                    y = ev.getY(pointerIndex);
                }
                double expDecayX = 1;
                double expDecayY = 1;

                // TODO: 23/07/20 change width and height to bitmap width and height
                if (xPos > 0){
                    expDecayX = Math.exp(-Math.abs(xPos)*expScale);
                } else if (xPos < -(tmpBmpWidth-width)){
                    expDecayX = Math.exp(-Math.abs(xPos + (tmpBmpWidth-width))*expScale);
                }
                if (yPos > 0){
                    expDecayY = Math.exp(-Math.abs(yPos)*expScale);
                } else if (yPos < -(tmpBmpHeight-height)){
                    expDecayY = Math.exp(-Math.abs(yPos + (tmpBmpHeight-height))*expScale);
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

        // Rotate Bitmap
        Matrix rotMatrix = new Matrix();
        rotMatrix.postRotate(angle);
        Bitmap rotatedBitmap = Bitmap.createBitmap(imgBitmapNew, 0, 0,
                (int) (bitmapWidth), (int) (bitmapHeight), rotMatrix, true);

        // Crop bitmap
        imgBitmapNew = Bitmap.createBitmap(rotatedBitmap, (int) -(xPos+xScale),
                (int) -(yPos+yScale), (int) (width), (int) (height));
        return imgBitmapNew;
    }

    private class scaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        //        private PointF viewportFocus = new PointF();
        private float lastSpan;
        private float lastScale;

        @Override
        public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
            lastSpan = scaleGestureDetector.getCurrentSpan();
            focusX = scaleGestureDetector.getFocusX();
            focusY = scaleGestureDetector.getFocusY();
            dragBeginX = focusX;
            dragBeginY = focusY;
            lastScale = 1;
            scaling = true;
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            float currentSpan = scaleGestureDetector.getCurrentSpan();
            scale = scaleFactor*(currentSpan/lastSpan);
            focusX = scaleGestureDetector.getFocusX();
            focusY = scaleGestureDetector.getFocusY();
            scaleXPos = focusX;
            scaleYPos = focusY;

            xScale = scale; //  (focusX - scale * focusX);
            yScale = scale; //  (focusY - scale * focusY);
            float relScale = scale / lastScale;
            xPos -= (-xPos + focusX) * relScale + xPos - focusX;
            yPos -= (-yPos + focusY) * relScale + yPos - focusY;
            lastScale = scale;
            invalidate();
//            lastSpan = currentSpan;
            return true;
        }
        @Override
        public void onScaleEnd(ScaleGestureDetector scaleGestureDetector){
            scaling = false;
        }
    }

    class rotRunnable implements Runnable{
        int oldAngle;
        int newAngle;
        int samples;
        long startTime;
        long lastTime;
        long interval;

        rotRunnable(int oldAngle, int newAngle, long period, int samples){
            this.oldAngle = oldAngle;
            this.newAngle = newAngle;
            this.samples = samples;
            interval = period/samples;
        }

        @Override
        public void run() {
            rotating = true;
            startTime = System.currentTimeMillis();
            lastTime = startTime;
            long time = System.currentTimeMillis();
            for (int i = 0; i <= samples; i++) {
                while ((time - lastTime) < interval){
                    time = System.currentTimeMillis();
                }
                lastTime = time;
                angle = oldAngle + i*(newAngle - oldAngle) / samples;
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateFrame();
                    }
                });
            }
            rotating = false;
        }
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

            boolean stop = false;
            while (runAnimation) {
                long time = System.currentTimeMillis();
                if ((time - lastTime) > interval) {
                    stop = true;
                    if (tmpBmpWidth < width) {
                        float wAcc = f(tmpBmpWidth, width);
                        wVel += wAcc * interval;
                        float newBitmapWidth = tmpBmpWidth + wVel * interval;
                        scale = scale * newBitmapWidth / tmpBmpWidth;
                        stop = false;
                    }
                    if (xPos > 0) {
                        float xAcc = f(xPos, 0);
                        xVel += xAcc * interval;
                        xPos += xVel * interval;
                        stop = false;
                    }
                    if (xPos < -(tmpBmpWidth - width)) {
                        float xAcc = f(xPos, -(tmpBmpWidth - width));
                        xVel += xAcc * interval;
                        xPos += xVel * interval;
                        stop = false;
                    }

                    if (tmpBmpHeight < height) {
                        float hAcc = f(tmpBmpHeight, height);
                        hVel += hAcc * interval;
                        float newBitmapHeight = tmpBmpHeight + hVel * interval;
                        scale = scale * newBitmapHeight / tmpBmpHeight;
                        stop = false;
                    }
                    if (yPos > 0) {
                        float yAcc = f(yPos, 0);
                        yVel += yAcc * interval;
                        yPos += yVel * interval;
                        stop = false;
                    }
                    if (yPos < -(tmpBmpHeight - height)) {
                        float yAcc = f(yPos, -(tmpBmpHeight - height));
                        yVel += yAcc * interval;
                        yPos += yVel * interval;
                        stop = false;
                    }

                    if (stop){
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
