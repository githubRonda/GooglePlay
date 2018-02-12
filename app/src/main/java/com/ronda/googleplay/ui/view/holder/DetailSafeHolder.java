package com.ronda.googleplay.ui.view.holder;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ronda.googleplay.R;
import com.ronda.googleplay.http.HttpHelper;
import com.ronda.googleplay.http.bean.AppInfo;
import com.ronda.googleplay.ui.adapter.ViewHolder;
import com.ronda.googleplay.ui.adapter.ViewHolderWrapper;
import com.ronda.googleplay.utils.UIUtils;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;

/**
 * Created by Ronda on 2018/1/7.
 */

public class DetailSafeHolder extends ViewHolderWrapper<AppInfo> {

    private ImageView[] mSafeIcons = new ImageView[4];// 安全标识图片
    private ImageView[] mDesIcons = new ImageView[4];// 安全描述图片
    private TextView[] mSafeDes = new TextView[4];// 安全描述文字
    private LinearLayout[] mSafeDesBar = new LinearLayout[4]; // 安全描述条目(图片+文字)

    private LinearLayout llDesRoot;
    private ImageView ivArrow;

    private int mDesHeight;
    private LinearLayout.LayoutParams mParams;
    private ImageOptions options;


    public DetailSafeHolder(Context context) {
        super(context);
    }

    @Override
    public View onCreateView(Context context) {
        View rootView = UIUtils.inflate(R.layout.layout_detail_safeinfo);

        options = new ImageOptions.Builder()
                .setLoadingDrawable(new ColorDrawable(Color.TRANSPARENT))
                .build();
        return rootView;
    }

    @Override
    public void setViewData(AppInfo data) {

        ViewHolder holder = getViewHolder();
        mSafeIcons[0] = holder.getView(R.id.iv_safe1);
        mSafeIcons[1] = holder.getView(R.id.iv_safe2);
        mSafeIcons[2] = holder.getView(R.id.iv_safe3);
        mSafeIcons[3] = holder.getView(R.id.iv_safe4);

        mDesIcons[0] = holder.getView(R.id.iv_des1);
        mDesIcons[1] = holder.getView(R.id.iv_des2);
        mDesIcons[2] = holder.getView(R.id.iv_des3);
        mDesIcons[3] = holder.getView(R.id.iv_des4);

        mSafeDes[0] = holder.getView(R.id.tv_des1);
        mSafeDes[1] = holder.getView(R.id.tv_des2);
        mSafeDes[2] = holder.getView(R.id.tv_des3);
        mSafeDes[3] = holder.getView(R.id.tv_des4);

        mSafeDesBar[0] = holder.getView(R.id.ll_des1);
        mSafeDesBar[1] = holder.getView(R.id.ll_des2);
        mSafeDesBar[2] = holder.getView(R.id.ll_des3);
        mSafeDesBar[3] = holder.getView(R.id.ll_des4);

        ivArrow = holder.getView(R.id.iv_arrow);

        llDesRoot = holder.getView(R.id.ll_des_root);

        // 获取安全描述的完整高度
        llDesRoot.measure(0, 0); //View.MeasureSpec.UNSPECIFIED
        mDesHeight = llDesRoot.getMeasuredHeight();

        Log.d("Liu", "mDesHeight: " + mDesHeight);

        // 初始时,高度为0
        mParams = (LinearLayout.LayoutParams) llDesRoot.getLayoutParams();
        mParams.height = 0;
        llDesRoot.setLayoutParams(mParams);


        List<AppInfo.SafeBean> safeList = data.getSafe();
        // 因为布局中就只设置了4个标签
        for (int i = 0; i < 4; i++) {

            if (i < safeList.size()) {
                mSafeIcons[i].setVisibility(View.VISIBLE);

                mSafeDesBar[i].setVisibility(View.VISIBLE);
                x.image().bind(mSafeIcons[i], HttpHelper.URL + "image?name=" + safeList.get(i).getSafeUrl(), options);
                x.image().bind(mDesIcons[i], HttpHelper.URL + "image?name=" + safeList.get(i).getSafeDesUrl());
                mSafeDes[i].setText(safeList.get(i).getSafeDes());
            } else {
                // 剩下不应该显示的图片
                mSafeIcons[i].setVisibility(View.GONE);

                // 隐藏多余的描述条目
                mSafeDesBar[i].setVisibility(View.GONE);
            }
        }

        // 当点击头部的View时, 收缩/展开内容
        holder.setOnClickListener(R.id.rl_des_root, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });
    }

    private boolean isOpen = false;// 标记安全描述开关状态,默认关

    // 打开或者关闭安全描述信息
    // 导入jar包: nineoldandroids-2.4.0.jar  谷歌提供兼容API11之前的版本使用属性动画.
    // 属性动画: ObjectAnimator, ValueAnimator 感觉就相当于一个取值器
    private void toggle() {
        ValueAnimator animator = null;
        if (isOpen) {
            // 关闭
            isOpen = false;
            // 属性动画
            animator = ValueAnimator.ofInt(mDesHeight, 0);
        } else {
            // 开启
            isOpen = true;
            animator = ValueAnimator.ofInt(0, mDesHeight);
        }

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                // 获取最新的高度值
                Integer height = (Integer) animation.getAnimatedValue();

                Log.d("Liu", "当前的 height: " + height);

                // 重新修改布局高度
                mParams.height = height;
                llDesRoot.setLayoutParams(mParams);
            }
        });

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // 动画结束的事件. 更新小箭头的方向
                if (isOpen) {
                    ivArrow.setImageResource(R.drawable.arrow_up);
                } else {
                    ivArrow.setImageResource(R.drawable.arrow_down);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        animator.setDuration(200); //200ms
        animator.start();// 启动动画
    }
}
