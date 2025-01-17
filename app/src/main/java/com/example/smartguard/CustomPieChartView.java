package com.example.smartguard;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.Map;

public class CustomPieChartView extends View {

    private Paint paint;
    private RectF rectF;
    private Map<String, Float> data;
    private int[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.MAGENTA};

    public CustomPieChartView(Context context) {
        super(context);
        init();
    }

    public CustomPieChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        rectF = new RectF();
    }

    public void setData(Map<String, Float> data) {
        this.data = data;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (data == null || data.isEmpty()) {
            return;
        }

        rectF.set(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
        float total = 0;
        for (float value : data.values()) {
            total += value;
        }

        float startAngle = 0;
        int colorIndex = 0;
        for (Map.Entry<String, Float> entry : data.entrySet()) {
            float sweepAngle = (entry.getValue() / total) * 360;
            paint.setColor(colors[colorIndex % colors.length]);
            canvas.drawArc(rectF, startAngle, sweepAngle, true, paint);
            startAngle += sweepAngle;
            colorIndex++;
        }
    }
}