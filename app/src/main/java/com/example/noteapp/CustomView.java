package com.example.noteapp;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.Random;

public class CustomView extends View {

    private Paint textPaint;
    private int textColor;
    private boolean isColorChanging = false;

    public CustomView(Context context) {
        super(context);
        init(null);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(@Nullable AttributeSet set) {
        // Initialize Paint for text
        textPaint = new Paint();
        textPaint.setTextSize(100 * getResources().getDisplayMetrics().scaledDensity); // Tăng cỡ chữ lên 120
        textPaint.setAntiAlias(true);

        startColorChangeAnimation();
    }

    private void startColorChangeAnimation() {
        ObjectAnimator colorAnimator = ObjectAnimator.ofObject(
                this,
                "textColor",
                new ArgbEvaluator(),
                generateRandomColor(),
                generateRandomColor()
        );

        colorAnimator.setDuration(1000);
        colorAnimator.setRepeatMode(ObjectAnimator.REVERSE);
        colorAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        colorAnimator.start();
    }

    private int generateRandomColor() {
        Random random = new Random();
        return Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        String text = "HUTECH";
        float x = 0;
        float y = textPaint.getTextSize();

        // Set the current text color
        textPaint.setColor(textColor);

        // Draw the text
        canvas.drawText(text, x, y, textPaint);
    }
}
