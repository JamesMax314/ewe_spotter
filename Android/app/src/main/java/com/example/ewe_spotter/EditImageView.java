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
                timeRunnable runnableX = new timeRunnable(timeRunnable.X_POS,
                        100, xPos, 0, 100);
                new Thread(runnableX).start();
                timeRunnable runnableY = new timeRunnable(timeRunnable.Y_POS,
                        100, yPos, 0, 100);
                new Thread(runnableY).start();
                timeRunnable runnableXFar = new timeRunnable(timeRunnable.X_POS,
                        100, xPos, -(bitmapWidth-width), 100);
                new Thread(runnableXFar).start();
                timeRunnable runnableYFar = new timeRunnable(timeRunnable.Y_POS,
                        100, yPos, -(bitmapHeight-height), 100);
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
        public static final int X_POS = 0;
        public static final int Y_POS = 1;
        public static final int SCALE = 2;
        long period;
        int value;
        float current;
        float desired;
        int samples;
        long startTime;
        long lastTime;
        long interval;
        float amplitude;
        timeRunnable(int value, long period, float current, float desired, int samples){
            this.value = value;
            this.period = period;
            this.current = current;
            this.desired = desired;
            this.samples = samples;
            interval = period/samples;
            amplitude = current - desired;
        }

        private float f(long time){
            long deltaTime = time - startTime;
            return (float) ((float) amplitude * Math.cos(deltaTime*2*Math.PI/(4*period)));
        }

        @Override
        public void run() {
            startTime = System.currentTimeMillis();
            lastTime = startTime;
            switch (getValue()){
                case X_POS:
                    while (xPos < desired){
                        long time = System.currentTimeMillis();
                        if ((time - lastTime) > interval){
                            lastTime = time;
                            xPos = f(time);

                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    updateFrame();
                                }
                            });
                        }
                    }
                case Y_POS:
                    while (yPos < desired){
                        long time = System.currentTimeMillis();
                        if ((time - lastTime) > interval){
                            lastTime = time;
                            yPos = f(time);

                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    updateFrame();
                                }
                            });
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
