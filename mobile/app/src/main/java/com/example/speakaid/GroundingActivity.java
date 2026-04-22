package com.example.speakaid;
import com.bumptech.glide.Glide;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class GroundingActivity extends AppCompatActivity {

    private View breathingCircle;
    private TextView txtBreatheState;
    private ImageButton btnSoundToggle;
    private MediaPlayer mediaPlayer;
    private boolean isPlaying = false;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeHelper.applyTheme(this);
        setContentView(R.layout.activity_grounding);

        breathingCircle = findViewById(R.id.breathingCircle);
        txtBreatheState = findViewById(R.id.txtBreatheState);
        btnSoundToggle = findViewById(R.id.btnSoundToggle);
        ImageButton btnBack = findViewById(R.id.btnBack);


        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        btnBack.setOnClickListener(v -> finish());
        btnSoundToggle.setOnClickListener(v -> toggleSound());

        startBreathingAnimation();
    }

    private void startBreathingAnimation() {
        // Create Inhale animation (Scale Up)
        PropertyValuesHolder scaleXUp = PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0f, 2.0f);
        PropertyValuesHolder scaleYUp = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0f, 2.0f);
        ObjectAnimator inhale = ObjectAnimator.ofPropertyValuesHolder(breathingCircle, scaleXUp, scaleYUp);
        inhale.setDuration(4000); // 4 seconds inhale
        inhale.setInterpolator(new AccelerateDecelerateInterpolator());
        inhale.addUpdateListener(animation -> {
            txtBreatheState.setText("INHALE");
            // Gentle vibration during inhale start
            if (animation.getAnimatedFraction() < 0.1 && vibrator != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(50);
                }
            }
        });

        // Create Exhale animation (Scale Down)
        PropertyValuesHolder scaleXDown = PropertyValuesHolder.ofFloat(View.SCALE_X, 2.0f, 1.0f);
        PropertyValuesHolder scaleYDown = PropertyValuesHolder.ofFloat(View.SCALE_Y, 2.0f, 1.0f);
        ObjectAnimator exhale = ObjectAnimator.ofPropertyValuesHolder(breathingCircle, scaleXDown, scaleYDown);
        exhale.setDuration(6000); // 6 seconds exhale (calming)
        exhale.setInterpolator(new AccelerateDecelerateInterpolator());
        exhale.addUpdateListener(animation -> txtBreatheState.setText("EXHALE"));

        // Loop the sequence
        AnimatorSet breatheCycle = new AnimatorSet();
        breatheCycle.playSequentially(inhale, exhale);
        breatheCycle.addListener(new Animator.AnimatorListener() {
            @Override public void onAnimationEnd(Animator animation) { breatheCycle.start(); }
            @Override public void onAnimationStart(Animator animation) {}
            @Override public void onAnimationCancel(Animator animation) {}
            @Override public void onAnimationRepeat(Animator animation) {}
        });
        breatheCycle.start();
    }

    private void toggleSound() {
        if (isPlaying) {
            stopSound();
        } else {
            playSound();
        }
        isPlaying = !isPlaying;
    }

    private void playSound() {
        // If it's already playing or initialized, stop it first to prevent duplicates
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        // Try to load
        int resId = getResources().getIdentifier("rain_bg", "raw", getPackageName());
        if (resId != 0) {
            mediaPlayer = MediaPlayer.create(this, resId);
            if (mediaPlayer != null) {
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
                btnSoundToggle.setImageResource(android.R.drawable.ic_lock_silent_mode);
                android.util.Log.d("AudioDebug", "Started playing sound.");
            }
        } else {
            android.util.Log.e("AudioDebug", "Could not find resource.");
        }
    }

    private void stopSound() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
            mediaPlayer = null;
            btnSoundToggle.setImageResource(android.R.drawable.ic_lock_silent_mode_off);
            android.util.Log.d("AudioDebug", "Stopped playing sound.");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}
