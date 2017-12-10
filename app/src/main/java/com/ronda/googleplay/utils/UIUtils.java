package com.ronda.googleplay.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Process;
import android.view.View;

import com.ronda.googleplay.global.MyApplication;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/11/24
 * Version: v1.0
 * <p>
 * 工具类, 专门处理UI相关的逻辑
 */

public class UIUtils {


    public static Context getContext() {
        return MyApplication.getContext();
    }

    public static int getMainThreadId() {
        return MyApplication.getMainThreadId();
    }

    public static Handler getHandler() {
        return MyApplication.getHandler();
    }


    /**
     * 根据id获取字符串资源
     */
    public static String getString(int id) {
        return getContext().getResources().getString(id);
    }

    /**
     * 根据id获取字符串数组资源
     */
    public static String[] getStringArray(int id) {
        return getContext().getResources().getStringArray(id);
    }

    /**
     * 根据id获取图片资源
     */
    public static Drawable getDrawable(int id) {
        return getContext().getResources().getDrawable(id);
    }

    /**
     * 根据id获取颜色资源
     */
    public static int getColor(int id) {
        return getContext().getResources().getColor(id);
    }

    /**
     * 根据id获取颜色状态选择器
     */
    public static ColorStateList getColorStateList(int id) {
        return getContext().getResources().getColorStateList(id);
    }

    /**
     * 根据id获取尺寸
     */
    public static int getDimen(int id) {
        return getContext().getResources().getDimensionPixelSize(id);
    }

    /**
     * dp转px
     */
    public static int dip2px(float dip) {

        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5);
    }

    /**
     * px转dp
     */
    public static float px2dip(int px) {
        float density = getContext().getResources().getDisplayMetrics().density;

        return px / density;
    }

    /**
     * 加载布局文件
     */
    public static View inflate(int layoutId) {
        return View.inflate(getContext(), layoutId, null);
    }


    /**
     * 判断当前是否是主线程环境
     */
    public static boolean isRunOnUIThread() {

        // Thread.currentThread().getId(); // 这种方法也可以获取当前线程的id. 这个是属于java中的, 而 Process是属于android中的
        return Process.myTid() == MyApplication.getMainThreadId();
    }


    /**
     * 保证当前操作运行在UI主线程
     */
    public static void runOnUIThread(Runnable runnable) {
        if (isRunOnUIThread()) {
            //如果是主线程就直接执行run()方法
            runnable.run();
        } else {
            // 如果是子线程, 借助handler让其运行在主线程
            getHandler().post(runnable);
        }
    }
}
