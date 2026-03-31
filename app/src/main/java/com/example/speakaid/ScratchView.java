package com.example.speakaid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class ScratchView extends View {

    private Bitmap scratchBitmap;
    private Canvas scratchCanvas;
    private Paint scratchPaint;
    private Path scratchPath;
    private Paint transparentPaint;

    public ScratchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        scratchPath = new Path();
        
        scratchPaint = new Paint();
        scratchPaint.setColor(Color.parseColor("#B0BEC5")); // Soothing gray top layer
        
        transparentPaint = new Paint();
        transparentPaint.setAntiAlias(true);
        transparentPaint.setDither(true);
        transparentPaint.setStyle(Paint.Style.STROKE);
        transparentPaint.setStrokeJoin(Paint.Join.ROUND);
        transparentPaint.setStrokeCap(Paint.Cap.ROUND);
        transparentPaint.setStrokeWidth(100f); // Wide scratch path
        transparentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        scratchBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        scratchCanvas = new Canvas(scratchBitmap);
        scratchCanvas.drawColor(Color.parseColor("#B0BEC5")); // Fill top layer
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Base layer is the background set in XML (the soothing image)
        canvas.drawBitmap(scratchBitmap, 0, 0, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                scratchPath.moveTo(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                scratchPath.lineTo(x, y);
                scratchCanvas.drawPath(scratchPath, transparentPaint);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                // No action needed
                break;
        }
        return true;
    }
    
    public void reset() {
        if (scratchCanvas != null) {
            scratchPath.reset();
            scratchCanvas.drawColor(Color.parseColor("#B0BEC5"), PorterDuff.Mode.SRC);
            invalidate();
        }
    }
}
