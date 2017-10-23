package com.zhuchi.android.dragview.slider;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.zhuchi.android.dragview.R;

@SuppressLint({ "ClickableViewAccessibility", "NewApi" })
public class DragView extends RelativeLayout implements OnSeekBarChangeListener, OnTouchListener {
	private static final int LEVEL_MAX = 10000;
	private Handler mMainHandler;
	private LayoutInflater mInflater;

	private TextSeekBar mSeekBar;
	private ImageView mLoadingIv;

	private Drawable mSucceedDrawable;
	private Drawable mFailedDrawable;
	private LayerDrawable mLoadingDrawable;
	private Drawable mThumb;

	private Context mContext;
	private ValueAnimator mValueAnimator;
	private BackAnimatorUpdateListener mUpdateListener;
	
	private int mOldProgress;
	private boolean mIsFromStartPoint = true;
	private boolean mIsValidating = false;
	private boolean mIsSucceed = false;
	private long mResetDelay = 1000;
	private long mResetDuration = 500;

	private Slider mSlider;
	private ClipDrawable mNormalProgressDrawable;
	private ClipDrawable mFailedProgressDrawable;
	private boolean mHas2End;

	@SuppressWarnings("deprecation")
	public DragView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		mContext = context;
		mMainHandler = new Handler(context.getMainLooper());
		mInflater = LayoutInflater.from(context);
		mSlider = new Slider(context);
		initView();
		bindListener();

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SldValLayout);
		LayerDrawable drawable = (LayerDrawable) a.getDrawable(R.styleable.SldValLayout_progressDrawable);
		if (drawable == null) {
			drawable = (LayerDrawable) context.getResources().getDrawable(R.drawable.seekbar_bg);
		}
		mNormalProgressDrawable = (ClipDrawable) drawable.findDrawableByLayerId(android.R.id.progress);
		mFailedProgressDrawable = (ClipDrawable) a.getDrawable(R.styleable.SldValLayout_failedProgressDrawable);
		if (mFailedProgressDrawable == null) {
			mFailedProgressDrawable = (ClipDrawable) context.getResources().getDrawable(R.drawable.seekbar_failed_bg);
		}
		mSeekBar.setProgressDrawable(drawable.mutate());

		mThumb = a.getDrawable(R.styleable.SldValLayout_thumb);
		if (mThumb == null) {
			mThumb = context.getResources().getDrawable(R.drawable.slider_thumb);
		}
		setThumb(mThumb, 0);
		mSucceedDrawable = a.getDrawable(R.styleable.SldValLayout_succeedDrawable);
		if (mSucceedDrawable == null) {
			mSucceedDrawable = context.getResources().getDrawable(R.drawable.slider_succeed);
		}
		mFailedDrawable = a.getDrawable(R.styleable.SldValLayout_failedDrawable);
		if (mFailedDrawable == null) {
			mFailedDrawable = context.getResources().getDrawable(R.drawable.slider_failed);
		}
		mLoadingDrawable = (LayerDrawable) a.getDrawable(R.styleable.SldValLayout_loadingDrawable);
		if (mLoadingDrawable == null) {
			mLoadingDrawable = (LayerDrawable) context.getResources().getDrawable(R.drawable.slider_loading);//ResourceUtil.getAnimId(context, "pgs_validation_loading") getDrawableId
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			mLoadingIv.setBackground(mLoadingDrawable);
		} else {  
			mLoadingIv.setBackgroundDrawable(mLoadingDrawable);
		}  
		
		String text = a.getString(R.styleable.SldValLayout_text);
		if (TextUtils.isEmpty(text)) {
			text = context.getResources().getString(R.string.sv_hint_init);
		}
		mSeekBar.setHintText(text);

		a.recycle();
	}

	public Slider getPGSSlider() {
		mSlider.mSlideView = this;

		return mSlider;
	}

	private void bindListener() {
		mSeekBar.setOnSeekBarChangeListener(this);
		mSeekBar.setOnTouchListener(this);
	}

	private void initView() {
		mInflater.inflate(R.layout.layout_slide_validation, this, true);
		mSeekBar = (TextSeekBar) findViewById(R.id.pgs_slider_sb);
		mLoadingIv = (ImageView) findViewById(R.id.pgs_slider_iv);

		mValueAnimator = ValueAnimator.ofInt(0, 1).setDuration(mResetDuration);
		mUpdateListener = new BackAnimatorUpdateListener();
		mValueAnimator.addUpdateListener(mUpdateListener);
	}

	public DragView(Context context) {
		this(context, null);
	}

	public DragView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	void setResetDelay(long resetDelay) {
		this.mResetDelay = resetDelay;
	}

	void setResetDuration(long resetDuration) {
		this.mResetDuration = resetDuration;
		if (mValueAnimator != null) {
			mValueAnimator.setDuration(mResetDuration);
		}
	}

	TextSeekBar getSeekBar() {
		return mSeekBar;
	}

	private void setThumb(Drawable drawable, int thumbOffset) {
		mSeekBar.setThumb(drawable);
		mSeekBar.setThumbOffset(thumbOffset);
	}

	public void onDestroy() {
		if (mValueAnimator != null) {
			mValueAnimator.removeAllListeners();
		}
		if (mSlider != null) {
			mSlider.onDestroy();
		}
		if (mMainHandler != null) {
			mMainHandler.removeCallbacksAndMessages(null);
			mMainHandler = null;
		}
		mUpdateListener = null;
		mContext = null;
		mFailedProgressDrawable = null;
		mNormalProgressDrawable = null;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// getParent().requestDisallowInterceptTouchEvent(true);
		return super.onTouchEvent(event);
	}

	public void successValidation() {
		mIsSucceed  = true;
		mSeekBar.setHintText(mContext.getString(R.string.sv_hint_succeed));
		if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN_MR1) {
			setThumb(mSucceedDrawable, 0);
		} else {
			setThumb(mSucceedDrawable, mSucceedDrawable.getIntrinsicWidth());
		}
		hideLoadingAnim();
	}

	public void failedValidation() {
		mSeekBar.setHintText(mContext.getString(R.string.sv_hint_failed));
		if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN_MR1) {
			setThumb(mFailedDrawable, 0);
		} else {
			setThumb(mFailedDrawable, mFailedDrawable.getIntrinsicWidth());
		}
		hideLoadingAnim();
		setFailedProgress(LEVEL_MAX);
		resetState(mSeekBar.getMax());
	}

	private void showLoadingAnim() {
		mLoadingIv.setVisibility(VISIBLE);
	}

	private void hideLoadingAnim() {
		mLoadingIv.setVisibility(GONE);
	}

	private void setFailedProgress(int level) {
		LayerDrawable progressDrawable = (LayerDrawable) mSeekBar.getProgressDrawable();
		if (mFailedProgressDrawable != null) {
			mFailedProgressDrawable.setLevel(level);
			progressDrawable.setDrawableByLayerId(android.R.id.progress,mFailedProgressDrawable);
		}
	}

	private void resetState(final int progress) {
		mIsValidating = false;
		if (mMainHandler != null) {
			mMainHandler.postDelayed(new Runnable() {

				@Override
				public void run() {
					resetThumb(progress);
					mSeekBar.setHintText(R.string.sv_hint_init);
					setThumb(mThumb, 0);
				}
			}, mResetDelay);
		}
	}
	
	private void resetThumb(int progress) {
		if (mUpdateListener != null) {
			mUpdateListener.setProgress(progress);
		}
		if (!mValueAnimator.isRunning()) {
			mValueAnimator.start();
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		int realWidth = seekBar.getWidth() - seekBar.getPaddingLeft() - seekBar.getPaddingRight();
		int thumbWidth = mThumb.getIntrinsicWidth();
		float percent = thumbWidth / (float)realWidth;
		float offestProgress = seekBar.getMax() * percent;
		
		if (fromUser
				&& (progress > mOldProgress + offestProgress || progress < mOldProgress - offestProgress)) {
			mSeekBar.setProgress(mOldProgress);
			return;
		}
		if (mIsFromStartPoint && fromUser) {
			mIsFromStartPoint = false;
			mSlider.slideStart();
		}
		if (fromUser && progress == seekBar.getMax() && !mIsValidating ) {
			mIsValidating = true;
			mHas2End = true;
			setThumb(new ShapeDrawable(), 0);
			showLoadingAnim();

			mSeekBar.setHintText(mContext.getString(R.string.sv_hint_validating));
			mSeekBar.setEnabled(false);
			if (mMainHandler != null) {
				mMainHandler.postDelayed(new Runnable() {
					@Override
					public void run() {
						mSlider.slideFinish();
					}
				},500);
			}
		}
		mOldProgress = progress;
	}
	
	@Override
	protected Parcelable onSaveInstanceState() {
		Bundle bundle = new Bundle();
		Parcelable superState = super.onSaveInstanceState();
		bundle.putBoolean("isValidating", mIsValidating);
		bundle.putBoolean("isEnabled", mSeekBar.isEnabled());
		bundle.putBoolean("isFromStartPoint", mIsFromStartPoint);
		bundle.putBoolean("isSucceed", mIsSucceed);
		bundle.putParcelable("superState", superState);
		bundle.putInt("Visibility", mLoadingIv.getVisibility());
		bundle.putBoolean("isRunning", mValueAnimator.isRunning());
		bundle.putInt("progress", mSeekBar.getProgress());
		bundle.putBoolean("has2End",mHas2End);
		
		return bundle;
	}
	
	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		Bundle bundle = (Bundle)state;
		mIsValidating = bundle.getBoolean("isValidating");
		mIsFromStartPoint = bundle.getBoolean("isFromStartPoint");
		mIsSucceed = bundle.getBoolean("isSucceed");
		mSeekBar.setEnabled(bundle.getBoolean("isEnabled"));
		mLoadingIv.setVisibility(bundle.getInt("Visibility"));
		mHas2End = bundle.getBoolean("has2End");
		int progress = bundle.getInt("progress");

		if (progress > 0 && !mIsSucceed && !mIsValidating) {
			resetState(progress);
		}

		if (mIsSucceed) {
			setThumb(mSucceedDrawable, 0);
		} else if (mHas2End && !mIsValidating) {
			float percent = progress/(float)mSeekBar.getMax();
			int level = (int) (LEVEL_MAX * percent);
			setFailedProgress(level);
		}

		super.onRestoreInstanceState(bundle.getParcelable("superState"));
	}
	
	
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(final SeekBar seekBar) {
		if (!mIsValidating) {
			resetThumb(seekBar.getProgress());
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_MOVE:
		}
		return false;
	}
	
	private final class BackAnimatorUpdateListener implements ValueAnimator.AnimatorUpdateListener {
		private int progress;

		public void setProgress(int progress) {
			this.progress = progress;
		}

		@Override
		public void onAnimationUpdate(ValueAnimator animation) {
			// 计算动画完成的百分比
			float percent = animation.getAnimatedFraction();
			int delay = (int) (progress * percent);
			mSeekBar.setProgress(progress-delay);

			if (delay == progress) {
				mHas2End = false;
				mIsFromStartPoint = true;
				mSeekBar.setEnabled(true);
				setNormalProgress(0);
			}
		}
	}

	private void setNormalProgress(int level) {
		LayerDrawable progressDrawable = (LayerDrawable) mSeekBar.getProgressDrawable();
		if (mNormalProgressDrawable != null) {
			mNormalProgressDrawable.setLevel(level);
			progressDrawable.setDrawableByLayerId(android.R.id.progress,mNormalProgressDrawable);
		}
	}

}
