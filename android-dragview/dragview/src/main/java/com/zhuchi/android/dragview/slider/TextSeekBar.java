package com.zhuchi.android.dragview.slider;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.widget.SeekBar;

import com.zhuchi.android.dragview.util.DisplayUtil;

/**
 * Created by chi.zhu on 2017/6/19.
 */

public class TextSeekBar extends SeekBar {
    private Context mContext;
    private Paint mPaint;
    private Paint mClearPaint;
    private String mHintText;

    private int notCoveredColor = Color.parseColor("#999999");
    private int coveredColor = Color.parseColor("#999999");
    private float hintSize = 16;
    private boolean isNeedCover = false;
    private Drawable mThumb;

    void setHintSize(float hintSize) {
        this.hintSize = hintSize;
        mPaint.setTextSize(DisplayUtil.sp2px(mContext, this.hintSize));
    }

    void setNotCoveredColor(int notCoveredColor) {
        this.notCoveredColor = notCoveredColor;
        mPaint.setColor(notCoveredColor);
    }

    void setNeedCover(boolean needCover) {
        this.isNeedCover = needCover;
    }

    void setCoveredColor(int coveredColor) {
        this.coveredColor = coveredColor;
    }

    void setHintText(String mHintText) {
        this.mHintText = mHintText;
    }

    void setHintText(int resid) {
        this.mHintText = getContext().getResources().getText(resid).toString();
    }

    public TextSeekBar(Context context) {
        this(context,null);
    }

    public TextSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.seekBarStyle);
    }

    public TextSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TextSeekBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        mContext = context;
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setTextAlign(Paint.Align.LEFT);
        mPaint.setTextSize(DisplayUtil.sp2px(mContext, hintSize));
        mPaint.setTypeface(Typeface.MONOSPACE);
        mPaint.setColor(notCoveredColor);

        mClearPaint = new Paint();
        mClearPaint.setColor(coveredColor);
        mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    @Override
    public void setThumb(Drawable thumb) {
        super.setThumb(thumb);
        if (thumb != null) {
            mThumb = thumb;
        }
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawText(canvas,mHintText);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        Parcelable superState = super.onSaveInstanceState();
        bundle.putParcelable("superState", superState);
        bundle.putString("hintText", mHintText);

        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle) state;
        mHintText = bundle.getString("hintText");

        super.onRestoreInstanceState(bundle.getParcelable("superState"));
    }

    private void drawText(Canvas canvas, String text) {
        Rect textRect = new Rect();
        mPaint.getTextBounds(text, 0, text.length(), textRect);

        float textX = (getWidth() / 2) - textRect.centerX();
        float textY = (getHeight() / 2) - textRect.centerY();

        Bitmap bufferBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas bufferCanvas = new Canvas(bufferBitmap);
        bufferCanvas.drawText(text, textX, textY, mPaint);

        float percent = getProgress() / (float)getMax();
        float left = 0;
        float right = getWidth() * percent + mThumb.getIntrinsicWidth() * (1 - percent);
        if (!isNeedCover) {
            left = getWidth() * percent - mThumb.getIntrinsicWidth() * percent;
        }

        RectF rectF = new RectF(left, 0, right, getHeight());
        if (percent == 1) {
            mClearPaint.setColor(coveredColor);
            mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        }
        else {
            mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }
        bufferCanvas.drawRect(rectF, mClearPaint);
        canvas.drawBitmap(bufferBitmap, 0, 0, null);

        if (!bufferBitmap.isRecycled()) {
            bufferBitmap.recycle();
        }
    }
}
