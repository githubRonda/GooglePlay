package com.ronda.googleplay.ui.view;

import java.util.ArrayList;
import java.util.List;

import com.ronda.googleplay.utils.UIUtils;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class MyFlowLayout extends ViewGroup {

    private int mUsedWidth;
    private int mHorizontalSpacing = UIUtils.dip2px(6);
    private int mVerticalSpacing = UIUtils.dip2px(8);

    private List<Line> mLineList = new ArrayList<>();

    private Line mLine;

    private static final int MAX_LINE = 100;// 最大行数是100行

    public MyFlowLayout(Context context) {
        super(context);
    }

    public MyFlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        // 有效宽度和高度
        int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        int height = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);


        // 测量子控件宽度进行比较
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width, (widthMode == MeasureSpec.EXACTLY) ? MeasureSpec.AT_MOST : widthMode);
            int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, (heightMode == MeasureSpec.EXACTLY) ? MeasureSpec.AT_MOST : heightMode);

            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);

            int measuredWidth = child.getMeasuredWidth();


            if (mLine == null) {
                mLine = new Line();
                mUsedWidth = 0;
            }

            mUsedWidth += measuredWidth;


            // 判断边界
            if (mUsedWidth <= width) {
                mLine.addView(child);

                mUsedWidth += mHorizontalSpacing;

                // 再次判断边界
                if (mUsedWidth >= width) {
                    if (!newLine()) {
                        break;
                    }
                }
            } else { // 超出边界(两种具体情况)
                if (mLine.getChildCount() == 0) {// 一行中的第一个VIew就超出了边界

                    mLine.addView(child);
                    if (!newLine()) {
                        break;
                    }
                } else {
                    if (!newLine()) {
                        break;
                    }
                    mLine.addView(child);
                    mUsedWidth += child.getMeasuredWidth() + mHorizontalSpacing;
                }
            }
        }



        if (mLine != null && mLine.getChildCount() != 0 && !mLineList.contains(mLine)) {
            mLineList.add(mLine);
        }

        int totalWidth = MeasureSpec.getSize(widthMeasureSpec);

        int totalHeight = 0;
        for (int i = 0; i < mLineList.size(); i++) {
            totalHeight += mLineList.get(i).mHeight;
        }

        totalHeight += mVerticalSpacing * (mLineList.size() - 1);

        totalHeight+= getPaddingTop()  + getPaddingBottom();

        // 根据最新的宽高来测量整体布局的大小
        setMeasuredDimension(totalWidth, totalHeight);
        setMeasuredDimension(totalWidth, resolveSize(totalHeight, heightMeasureSpec));

    }

    private boolean newLine() {
        mLineList.add(mLine);
        if (mLineList != null || mLineList.size() < MAX_LINE) {
            mLine = new Line();
            mUsedWidth = 0;
            return true;
        }
        return false;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int leftOffset = l + getPaddingLeft();
        int topOffset = t + getPaddingTop();

        for (int i = 0; i < mLineList.size(); i++) {

            Line line = mLineList.get(i);
            line.layout(leftOffset, topOffset);

            topOffset += line.mHeight + mVerticalSpacing;
        }
    }


    class Line {
        private int mHeight;
        private int mTotalWidth;
        private List<View> mChildViewList = new ArrayList<>();


        public void addView(View view) {
            mChildViewList.add(view);
            mTotalWidth += view.getMeasuredWidth();
            mHeight = (mHeight < view.getMeasuredHeight()) ? view.getMeasuredHeight() : mHeight; // 取最大的高度
        }

        public int getChildCount() {
            return mChildViewList.size();
        }

        public void layout(int l, int t) {
            int left = l;
            int top = t;

            int childCount = getChildCount();

            int validWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();

            int surplusWidth = validWidth - mTotalWidth - mHorizontalSpacing * (childCount - 1);

            if (surplusWidth > 0) {
                // 有剩余空间, 需要平分
                int space = (int) (surplusWidth * 1.0 / childCount + 0.5);

                for (int i = 0; i < childCount; i++) {
                    View childView = mChildViewList.get(i);
                    int measuredWidth = childView.getMeasuredWidth();
                    int measuredHeight = childView.getMeasuredHeight();

                    measuredWidth += space;

                    Log.e("Liu", "measuredWidth: " + measuredWidth+", measuredHeight: "+measuredHeight);

                    if (space > 0) {
                        // 需重测子view
                        int widthMeasureSpec = MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.EXACTLY);
                        int heightMeasureSpec = MeasureSpec.makeMeasureSpec(measuredHeight, MeasureSpec.EXACTLY);

                        childView.measure(widthMeasureSpec, heightMeasureSpec);
                    }

                    int topOffset = (int) ((mHeight - measuredHeight) / 2.0 + 0.5);
                    if (topOffset < 0) {
                        topOffset = 0;
                    }

                    childView.layout(left, top+ topOffset, left + measuredWidth, top + topOffset + measuredHeight);

                    Log.w("Liu", " top + topOffset + measuredHeight: "+ (top + topOffset + measuredHeight));
                    left += measuredWidth + mHorizontalSpacing;
                }

            } else {
                if (childCount == 1) {
                    View view = mChildViewList.get(0);
                    view.layout(left, top, left + view.getMeasuredWidth(), top + view.getMeasuredHeight());
                }
            }

//            int left = l;
//            int top = t;
//            int count = getChildCount();
//            // 总宽度
//            int layoutWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
//            // 剩余的宽度，是除了View和间隙的剩余空间
//            int surplusWidth = layoutWidth - mTotalWidth - mHorizontalSpacing * (count - 1);
//            if (surplusWidth >= 0) {// 有剩余空间
//
//                // 采用float类型数据计算后四舍五入能减少int类型计算带来的误差
//                int splitSpacing = (int) (1.0 * surplusWidth / count + 0.5); // 平均每个空间分配剩余空间的大小
//
//                // 重新测量子控件
//                for (int i = 0; i < count; i++) {
//                    final View childView = mChildViewList.get(i);
//                    int childWidth = childView.getMeasuredWidth();
//                    int childHeight = childView.getMeasuredHeight();
//
//                    // 把剩余空间平均到每个View上
//                    childWidth = childWidth + splitSpacing;
//                    childView.getLayoutParams().width = childWidth;// 这句可以不要, 下面本来就是使用新的childWidth的测量的
//                    if (splitSpacing > 0) {// View的长度改变了，需要重新measure
//                        int widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY);
//                        int heightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY);
//                        // 从新测量控件
//                        childView.measure(widthMeasureSpec, heightMeasureSpec);
//                    }
//
//                    // 当一行中的各个子View 高度不一时, 要计算top偏移量进行居中显示子View
//                    // 计算出每个View的顶点，是由最高的View和该View高度的差值除以2
//                    int topOffset = (int) ((mHeight - childHeight) / 2.0 + 0.5); // (行高 - 子view 的高度) / 2
//                    if (topOffset < 0) {
//                        topOffset = 0;
//                    }
//
//                    // 布局View
//                    childView.layout(left, top + topOffset, left + childWidth, top + topOffset + childHeight);
//                    left += childWidth + mHorizontalSpacing; // 为下一个View的left赋值
//                }
//            } else {
//                // 这个控件很长, 占满整行
//                if (count == 1) {
//                    View view = mChildViewList.get(0);
//                    view.layout(left, top, left + view.getMeasuredWidth(), top + view.getMeasuredHeight());
//                } else {
//                    // 走到这里来，应该是代码出问题了，目前按照逻辑来看，是不可能走到这一步
//                }
//            }


        }
    }
}
