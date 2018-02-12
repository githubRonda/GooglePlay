package com.ronda.googleplay.ui.adapter;

import android.content.Context;
import android.os.Process;
import android.view.View;

/**
 * Created by Ronda on 2018/1/7.
 * <p>
 * ViewHolderWrapper 主要是针对普通意义上的View的封装, 而非ListView中的ItemView;
 * 一般来说: 更适合用于把一个相对复杂界面封装到ViewHolder 中以减轻Activity的复杂度. 或者也可以把一个复杂界面拆分成相对独立的各个模块的界面, 每个子模块都各自封装到ViewHolder中
 *
 * 一个ViewHolder 的作用无非就是封装持有一系列相关的View. 具体的:1. 创建整个View; 2. 设置数据; 3. 获取这个View
 */

public abstract class ViewHolderWrapper<T> {
    protected Context mContext;
    protected ViewHolder mViewHolder;


    public ViewHolderWrapper(Context context) {
        this.mContext = context;

        View view = onCreateView(context);

        mViewHolder = new ViewHolder(view);

    }

    public abstract View onCreateView(Context context);

    public abstract void setViewData(T data);


    public ViewHolder getViewHolder() {
        return mViewHolder;
    }

    public View getRootView(){
        return mViewHolder.getConvertView();
    }


}
