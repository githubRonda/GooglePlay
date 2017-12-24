package com.ronda.googleplay.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.RadioButton;

import com.ronda.googleplay.R;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/12/12
 * Version: v1.0
 */

public class RatioLayout extends FrameLayout {

    private float ratio;

    public RatioLayout(Context context) {
        super(context);
    }

    public RatioLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    public RatioLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        /**
         * 有两种方法获取自定义属性 ratio 的值
         */
        // 方法一:
        //float ratio = attrs.getAttributeFloatValue("http://schemas.android.com/apk/res-auto", "ratio", -1f);

        // 方法二:
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RatioLayout); //R.styleable.RatioLayout 指attrs.xml中定义的属性集数组
        //R.styleable.RatioLayout_ratio 对应属性的索引值
        ratio = ta.getFloat(R.styleable.RatioLayout_ratio, -1);
        ta.recycle();

        Log.d("Liu", "ratio == " + ratio);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 1. 获取宽度
        // 2. 根据宽度和比例ratio, 计算控件的高度
        // 3. 重新测量控件

        // MeasureSpec.AT_MOST; 至多模式, 控件有多大显示多大, wrap_content
        // MeasureSpec.EXACTLY; 确定模式, 类似宽高写死成dip, match_parent
        // MeasureSpec.UNSPECIFIED; 未指定模式.

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        //Log.d("Liu", "widthMeasureSpec: " + widthMeasureSpec + ", widthMode: " + widthMode + ", width: " + width);

        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        // 宽度确定, 高度不确定, ratio合法, 才计算高度值
        if (widthMode == MeasureSpec.EXACTLY && heightMode != MeasureSpec.EXACTLY && ratio > 0) {

            // 图片宽度 = 控件宽度 - 左侧内边距 - 右侧内边距
            int contentWidth = width - getPaddingLeft() - getPaddingRight();

            // 图片高度 = 图片宽度/宽高比例
            int contentHeight = (int) (contentWidth / ratio + 0.5f);

            // 控件高度 = 图片高度 + 上侧内边距 + 下侧内边距
            height = contentHeight + getPaddingTop() + getPaddingBottom();

            // 根据最新的高度来重新生成heightMeasureSpec(高度模式是确定模式)
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        }

        // 按照最新的高度测量控件
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
