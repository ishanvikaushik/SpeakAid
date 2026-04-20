package com.example.speakaid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import androidx.core.content.ContextCompat;

public class FidgetSpinnerView extends View {
    private Drawable spinnerDrawable;
    private float angle = 0;
    private float velocity = 0;
    private long lastTime = 0;
    private float lastTouchX, lastTouchY;
    private final float friction = 0.98f;

    public FidgetSpinnerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        spinnerDrawable = ContextCompat.getDrawable(context, R.drawable.ic_spinner);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int size = Math.min(getWidth(), getHeight()) - 100;

        canvas.save();
        canvas.rotate(angle, centerX, centerY);
        spinnerDrawable.setBounds(centerX - size / 2, centerY - size / 2, centerX + size / 2, centerY + size / 2);
        spinnerDrawable.draw(canvas);
        canvas.restore();

        if (Math.abs(velocity) > 0.1f) {
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
                
                // Handle wrap around
                if (deltaAngle > 180) deltaAngle -= 360;
                if (deltaAngle < -180) deltaAngle += 360;

                angle += deltaAngle;
                if (time != lastTime) {
                    velocity = deltaAngle / (time - lastTime) * 16;
                }
                invalidate();
                break;
        }
        lastTouchX = x;
        lastTouchY = y;
        lastTime = time;
        return true;
    }
}
