package com.example.speakaid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class FidgetSpinnerView extends View {
    private float angle = 0;
    private float velocity = 0;
    private long lastTime = 0;
    private float lastTouchX, lastTouchY;
    private final float friction = 0.99f; // Higher friction for a more premium feel

    private Paint paintBody, paintBearing, paintAccent;
    private RectF rect;

    public FidgetSpinnerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paintBody = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBody.setColor(Color.parseColor("#3F51B5")); // Indigo Blue
        
        paintAccent = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintAccent.setColor(Color.parseColor("#FF4081")); // Pink Accent
        
        paintBearing = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBearing.setColor(Color.parseColor("#90A4AE")); // Silver/Steel
        
        rect = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radius = Math.min(getWidth(), getHeight()) / 4;

        canvas.save();
        canvas.rotate(angle, centerX, centerY);

        // Draw the 3 arms of the spinner
        for (int i = 0; i < 3; i++) {
            canvas.save();
            canvas.rotate(i * 120, centerX, centerY);
            
            // Draw Arm Body
            paintBody.setColor(Color.parseColor("#3F51B5"));
            canvas.drawCircle(centerX, centerY - radius * 1.5f, radius, paintBody);
            
            // Connect arm to center
            paintBody.setStrokeWidth(radius * 1.2f);
            canvas.drawLine(centerX, centerY, centerX, centerY - radius * 1.5f, paintBody);
            
            // Draw Finger Hole/Accent on arm
            paintAccent.setColor(Color.parseColor("#FF4081"));
            canvas.drawCircle(centerX, centerY - radius * 1.5f, radius * 0.6f, paintAccent);
            
            canvas.restore();
        }

        // Draw Center Bearing
        paintBearing.setColor(Color.parseColor("#CFD8DC"));
        canvas.drawCircle(centerX, centerY, radius * 0.8f, paintBearing);
        paintBearing.setColor(Color.parseColor("#455A64"));
        canvas.drawCircle(centerX, centerY, radius * 0.4f, paintBearing);

        canvas.restore();

        // Physics
        if (Math.abs(velocity) > 0.05f) {
            angle += velocity;
            velocity *= friction;
            invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX() - getWidth() / 2f;
        float y = event.getY() - getHeight() / 2f;
        long time = System.currentTimeMillis();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                velocity = 0;
                break;
            case MotionEvent.ACTION_MOVE:
                float currentAngle = (float) Math.toDegrees(Math.atan2(y, x));
                float prevAngle = (float) Math.toDegrees(Math.atan2(lastTouchY, lastTouchX));
                float deltaAngle = currentAngle - prevAngle;
                
                if (deltaAngle > 180) deltaAngle -= 360;
                if (deltaAngle < -180) deltaAngle += 360;

                angle += deltaAngle;
                if (time != lastTime) {
                    // Calculate spin force
                    velocity = (deltaAngle / (time - lastTime)) * 10;
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                // Let it spin!
                invalidate();
                break;
        }
        lastTouchX = x;
        lastTouchY = y;
        lastTime = time;
        return true;
    }
}
