package com.ronda.googleplay.utils;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;

/**
 * Created by Ronda on 2018/1/1.
 */

public class DrawableUtils {

    /**
     * 排行模块中的随机背景色, 有两个特点: 1. 背景形状为圆角矩形; 2. 背景色随机.
     * 思路: 若是使用xml的话, 则只能实现圆角矩形背景, 但是背景色是固定死的. 所以只能使用java代码来动态生成
     */
    // 获取一个shape对象
    public static GradientDrawable getGradientDrawable(int color, int radius) {
        // xml中定义的shape标签 对应GradientDrawable类
        GradientDrawable shape = new GradientDrawable();

        shape.setShape(GradientDrawable.RECTANGLE);// 矩形
        shape.setCornerRadius(radius);// 圆角半径
        shape.setColor(color);//颜色

        return shape;
    }

    /**
     * 获取selector 对象(即:StateListDrawable对象)
     */
    public static StateListDrawable getSelector(Drawable nomal, Drawable press) {

        StateListDrawable selector = new StateListDrawable();
        selector.addState(new int[]{android.R.attr.state_pressed}, press); // 按下时的图片. 第一个参数可以是 state_pressed,  state_enable等多个属性的数组.
        selector.addState(new int[]{}, nomal); // 默认情况下的图片, 第一个参数不建议写成null,可能会有空指针异常

        return selector;
    }

    /**
     * 获取 selector , 重载方法
     */
    public static StateListDrawable getSelector(int nomalColor, int pressColor, int radius) {
        GradientDrawable shapNormal = DrawableUtils.getGradientDrawable(nomalColor, radius);
        GradientDrawable shapePress = DrawableUtils.getGradientDrawable(pressColor, radius);
        StateListDrawable selector = DrawableUtils.getSelector(shapNormal, shapePress);
        return selector;
    }
}
