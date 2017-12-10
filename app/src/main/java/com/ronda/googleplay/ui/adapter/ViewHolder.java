package com.ronda.googleplay.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Process;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/11/26
 * Version: v1.0
 * <p>
 * 通用ViewHolder
 * 装载器, 持有者, 包装者, 封装者
 * <p>
 * 每一个Item，都有一个ViewHolder对象
 * ViewHolder中属性:
 * 当前Item的布局View --> mConverView
 * 当前Item里的所有子控件 --> mViews 集合
 * 当前Item的position --> mPosition
 * 对外提供的功能：
 * getConvertView() 获取 ViewHolder中关联的 itemView
 * getView(int viewId) 根据itemView中子view的id，获取对应的View对象
 * setXXX() 各种set 方法，给子View赋值或添加事件
 */
public class ViewHolder {

    private SparseArray<View> mViews;//用于装载Item中的所有子view控件
    private int mPosition;  //当前Item的position
    private View mConvertView; //当前Item的布局View，并且和当前的ViewHolder是关联在一起的：setTag(holder)

    public ViewHolder(Context context, ViewGroup parent, int layoutId, int position) {
        mPosition = position;
        mViews = new SparseArray<View>();
        mConvertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        mConvertView.setTag(this); //Tag既可以用于标识一个View，并且这个层级结构中不一定是唯一的，还可以用户存储数据。setTag(Object)参数是Object类型
    }


    public static ViewHolder getViewHolder(Context context, View convertView, ViewGroup parent, int layoutId, int position) {
        if (convertView == null) {
            return new ViewHolder(context, parent, layoutId, position);
        } else {
            ViewHolder holder = (ViewHolder) convertView.getTag();
            holder.mPosition = position;//因为是复用的itemView 和 ViewHolder对象，所以要给其成员重新赋值，但是 mConvertView 不需要重新赋值，因为每一个ViewHolder的 mConvertView 都是相同的（都是Item的布局View）
            return holder;
        }
    }


    /**
     * 通过viewId获取ItemView中的子控件
     * 先从mViews这个集合中取，若没有则通过 mConvertView.findViewById() 获取，然后存入集合; 以便以后再次获取
     *
     * @param viewId
     * @return View
     */
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);

        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }

        return (T) view;
    }

    public View getConvertView() {
        return mConvertView;
    }


    public int getItemPosition() {
        return mPosition;
    }

    public void updatePosition(int position) {
        mPosition = position;
    }


    /****以下为辅助方法*****/

    /**
     * 设置ItemView中指定的TextView 的值
     *
     * @param viewId
     * @param text
     * @return
     */
    public ViewHolder setText(int viewId, String text) {
        TextView view = getView(viewId);
        view.setText(text);
        return this;
    }

    /**
     * 设置Item中指定的ImageView 的图片
     *
     * @param viewId
     * @param resId
     * @return
     */
    public ViewHolder setImageResource(int viewId, int resId) {
        ImageView view = getView(viewId);
        view.setImageResource(resId);
        return this;
    }

    /**
     * 设置Item中指定的ImageView 的图片
     *
     * @param viewId
     * @param bm
     * @return
     */
    public ViewHolder setImageBitmap(int viewId, Bitmap bm) {
        ImageView view = getView(viewId);
        view.setImageBitmap(bm);
        return this;
    }

    public ViewHolder setProgress(int viewId, int progress){
        ProgressBar view = getView(viewId);
        view.setProgress(progress);
        return this;
    }

    public ViewHolder setRating(int viewId, float rating){
        RatingBar view = getView(viewId);
        view.setRating(rating);
        return this;
    }

    /**
     * 设置ImageView 图片
     *
     * @param viewId
     * @param url
     * @return
     */
    public ViewHolder setImageURL(int viewId, String url) {
        ImageView view = getView(viewId);
        // 图片下载方法，暂时保留; 可以通过Volley，Picasso等加载图片
        //ImageLoader.getInstance();
        return this;
    }

    public ViewHolder setVisibility(int viewId, int visibility) {
        View view = getView(viewId);
        view.setVisibility(visibility);
        return this;
    }

    public ViewHolder setVisibility(int viewId, boolean isVisible) {
        View view = getView(viewId);
        view.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        return this;
    }

    /******关于事件的*********/

    /**
     * 给Item中指定的子View控件设置点击事件
     *
     * @param viewId
     * @param listener
     * @return
     */
    public ViewHolder setOnClickListener(int viewId, View.OnClickListener listener) {
        View view = getView(viewId);
        view.setOnClickListener(listener);
        return this;
    }
}