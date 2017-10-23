package com.zhuchi.android.dragview.slider;

import android.graphics.Color;

/**
 * Created by chi.zhu on 2017/6/20.
 */

public class SliderOption {
    public final long resetDelay;
    public final long resetDuration;
    public final int hintColor;
    public final int hintCoveredColor;
    public final float hintSize;
    public final boolean isNeedCover;

    public SliderOption(long resetDelay, long resetDuration, int hintColor, int hintCoveredColor, float hintSize, boolean isNeedCover) {
        this.resetDelay = resetDelay;
        this.resetDuration = resetDuration;
        this.hintColor = hintColor;
        this.hintCoveredColor = hintCoveredColor;
        this.hintSize = hintSize;
        this.isNeedCover = isNeedCover;
    }

    public static final class Builder {
        private long resetDelay = 1000;
        private long resetDuration = 500;
        private int hintColor = Color.parseColor("#999999");
        private int hintCoveredColor = Color.parseColor("#999999");
        private float hintSize = 16;
        private boolean isNeedCover = false;

        public Builder() {
        }

        public Builder delay(long resetDelay) {
            this.resetDelay = resetDelay;
            return this;
        }

        public Builder duration(long resetDuration) {
            this.resetDuration = resetDuration;
            return this;
        }

        public Builder hintCoveredColor(int hintCoveredColor) {
            this.hintCoveredColor = hintCoveredColor;
            return this;
        }

        public Builder hintColor(int hintColor) {
            this.hintColor = hintColor;
            return this;
        }

        public Builder hintSize(float hintSize) {
            this.hintSize = hintSize;
            return this;
        }

        public Builder isNeedCover(boolean isNeedCover) {
            this.isNeedCover = isNeedCover;
            return this;
        }

        public SliderOption build() {
            return new SliderOption(this.resetDelay,this.resetDuration,this.hintColor,this.hintCoveredColor, hintSize, isNeedCover);
        }
    }
}
