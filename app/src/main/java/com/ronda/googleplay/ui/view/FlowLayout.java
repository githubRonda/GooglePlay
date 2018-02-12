package com.ronda.googleplay.ui.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.ronda.googleplay.utils.UIUtils;

import static android.R.attr.topOffset;


/**
 * 行情况:
 * 1. 当添加某一个子View时, 宽度刚好
 * 2. 当添加某一个子View时, 宽度+水平间隔, 刚好超过了父控件的宽度
 * 2. 当添加某一个子View时, 宽度刚好超过了父控件的宽度
 * 3. 当一行刚开始添加第一个view时, 宽度就超过了父控件的宽度
 */
public class FlowLayout extends ViewGroup {
    public static final int DEFAULT_SPACING = 20;
    /**
     * 横向间隔
     */
    private int mHorizontalSpacing = DEFAULT_SPACING;
    /**
     * 纵向间隔
     */
    private int mVerticalSpacing = DEFAULT_SPACING;
    /**
     * 是否需要布局，只用于第一次
     */
    boolean mNeedLayout = true;
    /**
     * 当前行已用的宽度，由子View宽度加上横向间隔
     */
    private int mUsedWidth = 0;
    /**
     * 代表每一行的集合
     */
    private final List<Line> mLines = new ArrayList<Line>();
    private Line mLine = null;
    /**
     * 最大的行数
     */
    private int mMaxLinesCount = Integer.MAX_VALUE;

    public FlowLayout(Context context) {
        super(context);
    }

    public void setHorizontalSpacing(int spacing) {
        if (mHorizontalSpacing != spacing) {
            mHorizontalSpacing = spacing;
            requestLayoutInner();
        }
    }

    public void setVerticalSpacing(int spacing) {
        if (mVerticalSpacing != spacing) {
            mVerticalSpacing = spacing;
            requestLayoutInner();
        }
    }

    public void setMaxLines(int count) {
        if (mMaxLinesCount != count) {
            mMaxLinesCount = count;
            requestLayoutInner();
        }
    }

    private void requestLayoutInner() {
        UIUtils.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                requestLayout();
            }
        });
    }

    /**
     * Measure 完整过程: measure() --> onMeasure() --> setMeasuredDimension()
     * Layout 完整过程: [layout() 若布局发生改变, 或者得到更新请求] --> onLayout() --> layout()
     *
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 有效宽度
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec) - getPaddingRight() - getPaddingLeft();
        // 有效高度
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();

        // 获取宽高模式
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        restoreLine();// 还原数据，以便重新记录

        // 获取所有子控件数量
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            // 若父控件是精确模式, 则子控件为至多模式, 否则和父控件模式一样
            int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(sizeWidth, modeWidth == MeasureSpec.EXACTLY ? MeasureSpec.AT_MOST : modeWidth);
            int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(sizeHeight, modeHeight == MeasureSpec.EXACTLY ? MeasureSpec.AT_MOST : modeHeight);

            // 测量child
            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);

            if (mLine == null) {
                mLine = new Line();
                mUsedWidth = 0;
            }
            // 获取子控件的测量宽度
            int childWidth = child.getMeasuredWidth();
            mUsedWidth += childWidth;// 增加使用的宽度

            if (mUsedWidth <= sizeWidth) {// 使用宽度小于总宽度，该child属于这一行。
                mLine.addView(child);// 添加child
                mUsedWidth += mHorizontalSpacing;// 加上间隔
                if (mUsedWidth >= sizeWidth) {// 加上间隔后如果大于等于总宽度，需要换行
                    if (!newLine()) {
                        break; // 若创建行达到最大行数, 则结束循环, 不再添加
                    }
                }
            } else {// 使用宽度大于总宽度。需要换行
                if (mLine.getViewCount() == 0) {// 如果这行一个child都没有，即使占用长度超过了总长度，也要加上去，保证每行都有至少有一个child
                    mLine.addView(child);// 添加child
                    if (!newLine()) {// 换行
                        break;
                    }
                } else {// 如果该行有数据了，就直接换行
                    if (!newLine()) {// 换行
                        break;
                    }
                    // 在新的一行，不管是否超过长度，先加上去，因为这一行一个child都没有，所以必须满足每行至少有一个child
                    mLine.addView(child);
                    mUsedWidth += childWidth + mHorizontalSpacing;
                }
            }
        }

        // 保存最后一行的行对象
        if (mLine != null && mLine.getViewCount() > 0 && !mLines.contains(mLine)) {
            // 由于前面采用判断长度是否超过最大宽度来决定是否换行，则最后一行可能因为还没达到最大宽度，所以需要验证后加入集合中
            mLines.add(mLine);
        }

        int totalWidth = MeasureSpec.getSize(widthMeasureSpec);// 该ViewGroup的整体宽度
        int totalHeight = 0;
        final int linesCount = mLines.size();
        for (int i = 0; i < linesCount; i++) {// 加上所有行的高度
            totalHeight += mLines.get(i).mHeight;
        }
        totalHeight += mVerticalSpacing * (linesCount - 1);// 加上所有间隔的高度
        totalHeight += getPaddingTop() + getPaddingBottom();// 加上padding
        // 设置布局的宽高，宽度直接采用父view传递过来的最大宽度，而不用考虑子view是否填满宽度，因为该布局的特性就是填满一行后，再换行
        // 高度根据设置的模式来决定采用所有子View的高度之和还是采用父view传递过来的高度
        setMeasuredDimension(totalWidth, resolveSize(totalHeight, heightMeasureSpec));
    }

    /* resolveSize()方法的源码在较低版本上的源码如下:(高版本的源码稍微复杂一点, 但是无论高低版本其神不变)
    //这个方法的主要作用就是根据你提供的大小和模式，返回你想要的大小值，这个里面根据传入模式的不同来做相应的处理。
    参数size：View期望的大小
    参数measureSpec：父View的measureSpec限制
    public static int resolveSize(int size, int measureSpec) {
         int result = size;
         int specMode = MeasureSpec.getMode(measureSpec);
         int specSize =  MeasureSpec.getSize(measureSpec);
         switch (specMode) {
         case MeasureSpec.UNSPECIFIED:
             result = size;
             break;
         case MeasureSpec.AT_MOST:
             result = Math.min(size, specSize);
             break;
         case MeasureSpec.EXACTLY:
             result = specSize;
             break;
         }
         return result;
     }
     */

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (!mNeedLayout || changed) {// 没有发生改变就不重新布局
            mNeedLayout = false;
            int left = getPaddingLeft();// 获取最初的左上点
            int top = getPaddingTop();
            final int linesCount = mLines.size();
            for (int i = 0; i < linesCount; i++) {
                final Line oneLine = mLines.get(i);
                oneLine.layoutView(left, top);// 布局每一行
                top += oneLine.mHeight + mVerticalSpacing;// 为下一行的top赋值
            }
        }
    }

    /**
     * 还原所有数据
     */
    private void restoreLine() {
        mLines.clear();
        mLine = new Line();
        mUsedWidth = 0;
    }

    /**
     * 新增加一行
     */
    private boolean newLine() {
        mLines.add(mLine);
        if (mLines.size() < mMaxLinesCount) {
            mLine = new Line();
            mUsedWidth = 0;
            return true;
        }
        return false;
    }

    // ==========================================================================
    // Inner/Nested Classes
    // ==========================================================================

    /**
     * 代表着一行，封装了一行所占高度，该行子View的集合，以及所有View的宽度总和
     */
    class Line {
        int mWidth = 0;// 该行中所有的子View累加的宽度
        int mHeight = 0;// 该行中所有的子View中高度最高的那个子View的高度
        List<View> views = new ArrayList<View>();

        public void addView(View view) {// 往该行中添加一个
            views.add(view);
            mWidth += view.getMeasuredWidth();
            int childHeight = view.getMeasuredHeight();
            mHeight = mHeight < childHeight ? childHeight : mHeight;// 高度等于一行中最高的View
        }

        public int getViewCount() {
            return views.size();
        }

        // 子控件位置设置(最终就是调用子View的 view.layout() 方法来摆放各个子View)
        public void layoutView(int l, int t) {// 布局
            int left = l;
            int top = t;
            int count = getViewCount();
            // 总宽度
            int layoutWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
            // 剩余的宽度，是除了View和间隙的剩余空间
            int surplusWidth = layoutWidth - mWidth - mHorizontalSpacing * (count - 1);
            if (surplusWidth >= 0) {// 有剩余空间

                // 采用float类型数据计算后四舍五入能减少int类型计算带来的误差
                int splitSpacing = (int) (1.0 * surplusWidth / count + 0.5); // 平均每个空间分配剩余空间的大小

                // 重新测量子控件
                for (int i = 0; i < count; i++) {
                    final View childView = views.get(i);
                    int childWidth = childView.getMeasuredWidth();
                    int childHeight = childView.getMeasuredHeight();

                    // 把剩余空间平均到每个View上
                    childWidth = childWidth + splitSpacing;
                    childView.getLayoutParams().width = childWidth;// 这句可以不要, 下面本来就是使用新的childWidth的测量的
                    if (splitSpacing > 0) {// View的长度改变了，需要重新measure
                        int widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY);
                        int heightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY);
                        // 从新测量控件
                        childView.measure(widthMeasureSpec, heightMeasureSpec);
                    }

                    // 当一行中的各个子View 高度不一时, 要计算top偏移量进行居中显示子View
                    // 计算出每个View的顶点，是由最高的View和该View高度的差值除以2
                    int topOffset = (int) ((mHeight - childHeight) / 2.0 + 0.5); // (行高 - 子view 的高度) / 2
                    if (topOffset < 0) {
                        topOffset = 0;
                    }

                    // 布局View
                    childView.layout(left, top + topOffset, left + childWidth, top + topOffset + childHeight);
                    left += childWidth + mHorizontalSpacing; // 为下一个View的left赋值
                }
            } else {
                // 这个控件很长, 占满整行
                if (count == 1) {
                    View view = views.get(0);
                    view.layout(left, top, left + view.getMeasuredWidth(), top + view.getMeasuredHeight());
                } else {
                    // 走到这里来，应该是代码出问题了，目前按照逻辑来看，是不可能走到这一步
                }
            }
        }
    }
}
