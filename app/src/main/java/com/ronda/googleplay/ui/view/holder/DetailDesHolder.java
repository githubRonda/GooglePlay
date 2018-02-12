package com.ronda.googleplay.ui.view.holder;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ronda.googleplay.R;
import com.ronda.googleplay.http.bean.AppInfo;
import com.ronda.googleplay.ui.adapter.ViewHolder;
import com.ronda.googleplay.ui.adapter.ViewHolderWrapper;
import com.ronda.googleplay.utils.UIUtils;

/**
 * Created by Ronda on 2018/1/11.
 * <p>
 * 当描述的文本高度超过7行时, 只显示7行的高度(即设置layout_height为7行的高度), 点击展开之后才完全显示; 若是高度不超过7行,则显示全部
 */

public class DetailDesHolder extends ViewHolderWrapper<AppInfo> {

    private TextView tvDes;
    private TextView tvAuthor;
    private ImageView ivArrow;
    private RelativeLayout rlToggle;
    private LinearLayout.LayoutParams mParams;

    public DetailDesHolder(Context context) {
        super(context);
    }

    @Override
    public View onCreateView(Context context) {
        return UIUtils.inflate(R.layout.layout_detail_desinfo);
    }

    @Override
    public void setViewData(AppInfo data) {

        ViewHolder holder = getViewHolder();

        tvDes = holder.getView(R.id.tv_detail_des);
        tvAuthor = holder.getView(R.id.tv_detail_author);
        ivArrow = holder.getView(R.id.iv_arrow);
        rlToggle = holder.getView(R.id.rl_detail_toggle);

        rlToggle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                toggle();
            }
        });


        tvDes.setText(data.getDes());
        tvAuthor.setText(data.getAuthor());




        // 放在消息队列中运行, 解决当只有三行描述时也是7行高度的bug.
        // 本质上就是保证: getShortHeight()中的 tvDes.getMeasuredWidth() 是当 tvDes 绘制出来后调用
        tvDes.post(new Runnable() {
            @Override
            public void run() {
                // 设置默认展示7行的高度
                mParams = (LinearLayout.LayoutParams) tvDes.getLayoutParams();
                mParams.height = getShortHeight();
                Log.d("Liu", "mParams.height: " + getShortHeight()); //236,,, 104
                tvDes.setLayoutParams(mParams);
            }
        });
    }


    private boolean isOpen = false;

    private void toggle() {

        int shortHeight = getShortHeight();
        int longHeight = getLongHeight();


        Log.e("Liu","shortHeight: "+shortHeight);

        // 只有当文本大于七行的时候, 才有展开和收起的动画. 不足7行时, 直接返回
        if (longHeight <= shortHeight) {
            return;
        }

        ValueAnimator animator = null;

        isOpen = !isOpen;
        if (isOpen) {
            // 打开
            animator = ValueAnimator.ofInt(shortHeight, longHeight);
        } else {
            // 关闭
            animator = ValueAnimator.ofInt(longHeight, shortHeight);
        }

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int height = (int) animation.getAnimatedValue();
                mParams.height = height;
                tvDes.setLayoutParams(mParams);
            }
        });

        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                // 因为Android很多函数都是基于消息队列来同步,addView完之后，不等于马上就会显示，而是在队列中等待处理，所以添加到队列中,可以保证按次序来执行, 不会出现fullScroll()在addView()前面执行的情况
                // 一般来说,上述情况几乎很少出现. 当然这样做的目的就是为了运行更加安全和稳定
                tvDes.post(new Runnable() {
                    @Override
                    public void run() {
                        ScrollView scrollView = getScrollView();
                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });

                // 更新箭头
                ivArrow.setImageResource(isOpen ? R.drawable.arrow_up : R.drawable.arrow_down);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        animator.setDuration(200);
        animator.start();
    }


    /**
     * 获取7行的TextView的高度
     */
    private int getShortHeight() {

        // 模拟一个TextView, 属性以及内容和实际的TextView一模一样,且设置最大行数为7行, 计算该虚拟TextView的高度, 从而知道TVDES在展示7行时应该多高

        // 获取tvDes测量的宽度. 这里我们预想是要获取tvDes显示出来时的真实的宽度. 但这里也有一个坑:getShortHeight() 在 setViewData() 中也有调用,但此时界面并没有绘制出来, 所以此时使用getMeasuredWidth()获取的值为0
        // 但是如果我们先使用 tvDes.measure(0, 0); 测量的话, 然后 getMeasuredWidth()值为 1680, getMeasuredHeight()值为38, 结果很奇怪, 其实这个就是tvDes在一个无限大的屏幕中单行显示的结果.
        // 原因就是: 测量的规则为 MeasureSpec.UNSPECIFIED(即值为0) 即按照tvDes 想要的大小来测量, 没有任何限制. 所以tvDes的测量宽度并没有被限制为不超过屏幕的宽度, 所以tvDes在真正绘制出来时就得需要二次甚至多次测量
        int width = tvDes.getMeasuredWidth(); // 这个是在点击事件中调用的, 界面已经完整的绘制出来了, 所以使用 tvDes.getWidth() 也是可以的

//        Log.d("Liu", "width: "+width); // width: 736
//        Log.e("Liu", "width: "+tvDes.getWidth()); // width: 736

        TextView textView = new TextView(UIUtils.getContext());
        textView.setText(tvDes.getText());// 设置文字一样
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);// 文字大小一致
        // 最大行数为7行.
        // 注意, 这里设置了最大行数为7行, 若是内容不足7行时, 显示出来的理论上就是真实的行数. 但是若是直接在 setViewData()中调用的话(即布局文件刚加载完成后立即调用),无论内容多少,获取到的永远就是7行的高度
        // 所以: 在 setViewData() 中把 getShortHeight() 放在了消息队列中, 按次序来执行
        textView.setMaxLines(7);

        // todo: 2018/1/12  这里一定不能直接使用 measure(0,0) 来测量, 因为我们并没有给该textView 设置 layout 相关的属性, 所以无法测量
        // 解决方法就是: 直接手动指定 MeasureSpec 参数, 这个参数中包含了大小和规则. layout_width 和 layout_height 最终就是解析成适当的 MeasureSpec 参数, 然后传递给 setMeasuredDimension() 方法中
//        textView.measure(0, 0);
//        int measuredHeight = textView.getMeasuredHeight();
//        Log.e("Liu", "measuredHeight: "+measuredHeight); // measuredHeight: 38
//        Log.e("Liu", "Height: "+tvDes.getHeight()); // Height: 236. 7行时的高度

        // 宽不变, 确定值, match_parent.
        // 注意, 这里一不小心就会掉入坑中: 这里的width的值会影响到getShortHeight()最终的测量结果, 若是tvDes没有绘制出来,此时width的值就为0, 就相当于TextView内容是单列排列,而最终getMeasuredHeight()的值就是TextView7行的高度(除非内容就只有几个字,连7行都没达到)
        int widthMeasuredSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int heightMeasuredSpec = View.MeasureSpec.makeMeasureSpec(2000, View.MeasureSpec.AT_MOST);// 高度包裹内容, wrap_content;当包裹内容时,参1表示尺寸最大值,暂写2000, 也可以是屏幕高度

        // 开始测量
        textView.measure(widthMeasuredSpec, heightMeasuredSpec);

        Log.e("Liu", "measuredHeight: " + textView.getMeasuredHeight());  //measuredHeight: 236. 发现和实际的TextView的7行时的高度一样

        return textView.getMeasuredHeight();
    }


    /**
     * 获取完整的TextView的高度. 过程和 getShortHeight() 很类似, 仅仅是把 setMaxLines(7); 去掉即可.
     */
    private int getLongHeight() {

        // 模拟一个TextView, 属性以及内容和实际的TextView一模一样,且设置最大行数为7行, 计算该虚拟TextView的高度, 从而知道TVDES在展示7行时应该多高
        int width = tvDes.getMeasuredWidth(); // 这个是在点击事件中调用的, 界面已经完整的绘制出来了, 所以使用 tvDes.getWidth() 也是可以的

        TextView textView = new TextView(UIUtils.getContext());
        textView.setText(tvDes.getText());// 设置文字一样
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);// 文字大小一致
        //textView.setMaxLines(7);// 最大行数为7行

        int widthMeasuredSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);// 宽不变, 确定值, match_parent
        int heightMeasuredSpec = View.MeasureSpec.makeMeasureSpec(2000, View.MeasureSpec.AT_MOST);// 高度包裹内容, wrap_content;当包裹内容时,参1表示尺寸最大值,暂写2000, 也可以是屏幕高度

        // 开始测量
        textView.measure(widthMeasuredSpec, heightMeasuredSpec);

        Log.e("Liu", "measuredHeight: " + textView.getMeasuredHeight());  //measuredHeight: 236. 发现和实际的TextView的7行时的高度一样

        return textView.getMeasuredHeight();
    }


    /**
     * 获取父控件 ScrollView
     * 因为当前布局仅仅是 ScrollView 中的一部分, 所以不能通过findViewById()的方式获取ScrollView.
     * 解决方法:  一层一层向上查找, 直到找到有一个父控件为ScrollView为止
     * 注意:一定要保证父控件或祖宗控件有ScrollView,否则死循环
     *
     * @return
     */
    private ScrollView getScrollView() {

        View parent = (View) tvDes.getParent();
        while (!(parent instanceof ScrollView)) {
            parent = (View) parent.getParent();
        }
        return (ScrollView) parent;
    }


}
