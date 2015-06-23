package ca.tobymurray.fingerpainting.fingerpainting;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Toby on 23/06/2015.
 */
public class DrawingView extends View {
    private Path m_drawPath;
    private Paint m_drawPaint;
    private Paint m_canvasPaint;
    private int m_paintColor = 0xFF660000;
    private Canvas m_drawCanvas;
    private Bitmap m_canvasBitmap;
    private float m_brushSize;
    private float m_lastBrushSize;
    private boolean m_erase = false;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setUpDrawing();
    }

    private void setUpDrawing() {
        m_brushSize = getResources().getInteger(R.integer.medium_size);
        m_lastBrushSize = m_brushSize;

        m_drawPath = new Path();
        m_drawPaint = new Paint();
        m_drawPaint.setColor(m_paintColor);
        m_drawPaint.setAntiAlias(true);
        m_drawPaint.setStrokeWidth(m_brushSize);
        m_drawPaint.setStyle(Paint.Style.STROKE);
        m_drawPaint.setStrokeJoin(Paint.Join.ROUND);
        m_drawPaint.setStrokeCap(Paint.Cap.ROUND);

        m_canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    public void setBrushSize(float newSize) {
        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newSize, getResources().getDisplayMetrics());
        m_brushSize = pixelAmount;
        m_drawPaint.setStrokeWidth(m_brushSize);
    }

    public void setColor(String newColor) {
        invalidate();
        m_paintColor = Color.parseColor(newColor);
        m_drawPaint.setColor(m_paintColor);
    }

    public float getLastBrushSize() {
        return m_lastBrushSize;
    }

    public void setLastBrushSize(float m_lastBrushSize) {
        this.m_lastBrushSize = m_lastBrushSize;
    }

    public void setErase(boolean isErasing) {
        m_erase = isErasing;
        if (isErasing) {
            m_drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }
        else {
            m_drawPaint.setXfermode(null);
        }
    }

    public void startNew() {
        m_drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        m_canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        m_drawCanvas = new Canvas(m_canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(m_canvasBitmap, 0, 0, m_canvasPaint);
        canvas.drawPath(m_drawPath, m_drawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                m_drawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                m_drawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                m_drawCanvas.drawPath(m_drawPath, m_drawPaint);
                m_drawPath.reset();
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }
}
