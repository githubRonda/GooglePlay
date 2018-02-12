package com.ronda.googleplay.ui.view.holder;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.ronda.googleplay.R;
import com.ronda.googleplay.http.HttpHelper;
import com.ronda.googleplay.ui.adapter.ViewHolder;
import com.ronda.googleplay.ui.adapter.ViewHolderWrapper;
import com.ronda.googleplay.utils.UIUtils;

import org.xutils.x;

import java.util.List;

/**
 * Created by Ronda on 2018/1/2.
 *
 * 首页的轮播图
 */

public class HomeHeaderHolder extends ViewHolderWrapper<List<String>> {

    private ViewPager mViewPager;
    private List<String> mData;
    private LinearLayout mLlContainer;
    private int mLastPosition; // 记录ViewPager中item的位置, 便于设置上一个indicator的背景

    public HomeHeaderHolder(Context context) {
        super(context);
    }

    @Override
    public View onCreateView(Context context) {

        // 创建根布局, 相对布局
        RelativeLayout rlRoot = new RelativeLayout(UIUtils.getContext());
        // 初始化布局参数, 根布局上层控件是listview, 所以要使用listview定义的LayoutParams
        AbsListView.LayoutParams layoutParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UIUtils.dip2px(150));
        rlRoot.setLayoutParams(layoutParams);

        // ViewPager
        mViewPager = new ViewPager(UIUtils.getContext());
        RelativeLayout.LayoutParams vpParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //mViewPager.setLayoutParams(vpParams);
        rlRoot.addView(mViewPager, vpParams); // 添加View的同时设置LayoutParams

        // 初始化指示器
        mLlContainer = new LinearLayout(UIUtils.getContext());

        RelativeLayout.LayoutParams llParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // 设置内边距
        int padding = UIUtils.dip2px(10);
        mLlContainer.setPadding(padding, padding, padding, padding);

        // 添加RelativeLayout的规则, 设置展示位置
        llParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM); // 底部对齐
        llParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT); // 右对齐

        rlRoot.addView(mLlContainer, llParams);
        return rlRoot;
    }

    public void setViewData(List<String> data) {
        mData = data;
        mViewPager.setAdapter(new HomeHeaderAdapter());
        mViewPager.setCurrentItem(mData.size() * 10000); // 让其处于中间的某个显示第一个Item的位置, 便于向前滑动

        new AutoSlideTask().start();


        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        for (int i = 0; i < mData.size(); i++) {
            ImageView pointer = new ImageView(UIUtils.getContext());

            if (i == 0) {// 第一个默认选中
                pointer.setBackgroundResource(R.drawable.indicator_selected);
                layoutParams.leftMargin = 0;
            } else {
                pointer.setBackgroundResource(R.drawable.indicator_normal);
                layoutParams.leftMargin = UIUtils.dip2px(4);
            }

            mLlContainer.addView(pointer, layoutParams);
        }

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                position = position % mData.size();

                // 上个点变为不选中
                ImageView lastPointer = (ImageView) mLlContainer.getChildAt(mLastPosition);
                lastPointer.setBackgroundResource(R.drawable.indicator_normal);
                // 当前点被选中
                ImageView curPointer = (ImageView) mLlContainer.getChildAt(position);
                curPointer.setBackgroundResource(R.drawable.indicator_selected);
                mLastPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    class AutoSlideTask implements Runnable {

        public void start() {
            // 移除之前发送的所有消息, 避免多次调用start()方法时,消息累计重复发送
            UIUtils.getHandler().removeCallbacksAndMessages(null);

            UIUtils.getHandler().postDelayed(this, 3000);
        }

        @Override
        public void run() {

            int currentItem = mViewPager.getCurrentItem();
            currentItem++;
            mViewPager.setCurrentItem(currentItem); // 不会越界, 即使越界内部也会处理

            // 继续发延时3秒消息, 实现内循环
            UIUtils.getHandler().postDelayed(this, 3000);
        }
    }

    class HomeHeaderAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            //return mData.size();
            return Integer.MAX_VALUE; // 模拟无限滑动
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            position = position % mData.size(); // 取余, 防止角标越界

            ImageView imageView = new ImageView(UIUtils.getContext());
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);

            x.image().bind(imageView, HttpHelper.URL + "image?name=" + mData.get(position));
            container.addView(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
