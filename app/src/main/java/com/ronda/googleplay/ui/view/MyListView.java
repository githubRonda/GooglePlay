package com.ronda.googleplay.ui.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/12/10
 * Version: v1.0
 */

public class MyListView extends ListView {
    public MyListView(Context context) {
        super(context);
        init();
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }



    public MyListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        this.setSelector(new ColorDrawable()); //系统默认的点击效果, 设置为透明
        this.setDivider(null); //去掉默认分割线
        this.setCacheColorHint(Color.TRANSPARENT); //有时滑动ListView会变成黑色, 这里设置为透明
    }

}
