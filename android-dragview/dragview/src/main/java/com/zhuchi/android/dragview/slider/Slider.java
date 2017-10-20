package com.zhuchi.android.dragview.slider;

import android.content.Context;

/**
 * Created by chi.zhu on 2017/6/6.
 */

public class Slider {

    DragView mSlideView;

    private OnSlideListener mOnSlideListener;


    public Slider(Context context) {
    }

    void slideStart() {
        if (mOnSlideListener != null) {
            mOnSlideListener.onSlideStart();
        }
    }

    void slideFinish() {
        if (mOnSlideListener != null) {
            mOnSlideListener.onSlideFinish();
        }
    }

    void onDestroy() {
        removeListener();
        mSlideView = null;
    }

    private void removeListener() {
        mOnSlideListener = null;
    }

    public void setSliderOption(SliderOption option) {
        if (option != null) {
            mSlideView.setResetDelay(option.resetDelay);
            mSlideView.setResetDuration(option.resetDuration);
            mSlideView.getSeekBar().setCoveredColor(option.hintCoveredColor);
            mSlideView.getSeekBar().setNotCoveredColor(option.hintColor);
            mSlideView.getSeekBar().setHintSize(option.hintSize);
            mSlideView.getSeekBar().setNeedCover(option.isNeedCover);
        }
    }

    public void setOnSlideListener(OnSlideListener mOnSlideListener) {
        this.mOnSlideListener = mOnSlideListener;
    }

    public interface OnSlideListener {
        void onSlideStart();

        void onSlideFinish();
    }

}
