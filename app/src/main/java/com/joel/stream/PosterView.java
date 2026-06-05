package com.joel.stream;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.text.TextPaint;
import android.view.View;

final class PosterView extends View {
    private final MediaItem item;
    private final Paint bg = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint overlay = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final TextPaint titlePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private final TextPaint metaPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private final RectF rect = new RectF();

    PosterView(Context context, MediaItem item) {
        super(context);
        this.item = item;
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        titlePaint.setColor(0xffffffff);
        titlePaint.setFakeBoldText(true);
        titlePaint.setTextSize(dp(18));
        metaPaint.setColor(0xd9ffffff);
        metaPaint.setTextSize(dp(12));
        overlay.setColor(0x66000000);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        rect.set(0, 0, getWidth(), getHeight());
        bg.setShader(new LinearGradient(0, 0, getWidth(), getHeight(), item.colorA, item.colorB, Shader.TileMode.CLAMP));
        canvas.drawRoundRect(rect, dp(12), dp(12), bg);
        canvas.drawRoundRect(rect, dp(12), dp(12), overlay);

        Paint play = new Paint(Paint.ANTI_ALIAS_FLAG);
        play.setColor(0x30ffffff);
        canvas.drawCircle(getWidth() / 2f, getHeight() / 2f - dp(18), dp(42), play);
        play.setColor(0xffffffff);
        android.graphics.Path tri = new android.graphics.Path();
        tri.moveTo(getWidth() / 2f - dp(12), getHeight() / 2f - dp(42));
        tri.lineTo(getWidth() / 2f - dp(12), getHeight() / 2f + dp(6));
        tri.lineTo(getWidth() / 2f + dp(26), getHeight() / 2f - dp(18));
        tri.close();
        canvas.drawPath(tri, play);

        drawMultiline(canvas, item.title, titlePaint, dp(14), getHeight() - dp(58), getWidth() - dp(28), 2);
        canvas.drawText(item.category + "  " + item.duration, dp(14), getHeight() - dp(18), metaPaint);
    }

    private void drawMultiline(Canvas canvas, String text, TextPaint paint, float x, float y, float maxWidth, int maxLines) {
        String[] words = text.split(" ");
        String line = "";
        int lines = 0;
        for (String word : words) {
            String next = line.length() == 0 ? word : line + " " + word;
            if (paint.measureText(next) > maxWidth && line.length() > 0) {
                canvas.drawText(line, x, y + lines * dp(21), paint);
                lines++;
                line = word;
                if (lines >= maxLines - 1) break;
            } else {
                line = next;
            }
        }
        if (line.length() > 0 && lines < maxLines) {
            canvas.drawText(line, x, y + lines * dp(21), paint);
        }
    }

    private float dp(float value) {
        return value * getResources().getDisplayMetrics().density;
    }
}
