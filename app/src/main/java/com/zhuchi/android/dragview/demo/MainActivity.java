package com.zhuchi.android.dragview.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.zhuchi.android.dragview.slider.DragView;
import com.zhuchi.android.dragview.slider.Slider;
import com.zhuchi.android.dragview.slider.SliderOption;

public class MainActivity extends AppCompatActivity {

    private DragView mDragView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.zhuchi.android.dragview.R.layout.activity_main);

        mDragView = (DragView) findViewById(R.id.dragView);

        Slider slider = mDragView.getPGSSlider();
        //设置滑块相关参数
        SliderOption option = new SliderOption.Builder()
                .delay(1000)//滑块滑动到底回退延时
                .duration(500)//滑块回退动画持续时间
                .hintColor(getResources().getColor(R.color.colorPrimary))//提示文字颜色
                .isNeedCover(true)//是否需要覆盖提示文字
                .hintCoveredColor(getResources().getColor(R.color.colorAccent))//提示文字被进度覆盖时颜色
                .hintSize(16)//提示文字大小
                .build();
        slider.setSliderOption(option);

        slider.setOnSlideListener(new Slider.OnSlideListener() {
            @Override
            public void onSlideStart() {
                Toast.makeText(MainActivity.this,"开始滑动",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSlideFinish() {
                Toast.makeText(MainActivity.this,"滑动结束",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
