package com.ronda.googleplay.global;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Process;

import com.ronda.googleplay.BuildConfig;

import org.xutils.x;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/11/24
 * Version: v1.0
 *
 * 自定义Application
 */

public class MyApplication extends Application {

    private static Context mContext;
    private static int mMainThreadId;
    private static Handler mHandler;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = getApplicationContext();

        mMainThreadId = Process.myTid();// Tid 表示 Thread id, 而 Pid 表示进程id

        mHandler = new Handler();

        init();
    }

    private void init() {
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG); // 是否输出debug日志, 开启debug会影响性能.
    }

    public static Context getContext() {
        return mContext;
    }

    public static int getMainThreadId() {
        return mMainThreadId;
    }

    public static Handler getHandler() {
        return mHandler;
    }
}
