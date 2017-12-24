package com.ronda.googleplay.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.ronda.googleplay.R;
import com.ronda.googleplay.utils.UIUtils;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/11/26
 * Version: v1.0
 * <p>
 * 根据当前状态来显示不同页面的自定义控件
 * <p>
 * - 未加载 - 加载中 - 加载失败 - 数据为空 - 加载成功
 */

public abstract class LoadingPage extends FrameLayout {

    private static final int STATE_UNDO = 0; //未加载
    private static final int STATE_LOADING = 1; //加载中
    private static final int STATE_LOAD_ERROR = 2; //加载失败
    private static final int STATE_LOAD_EMPTY = 3; //数据为空
    private static final int STATE_LOAD_SUCCESS = 4; //加载成功

    private int mCurrentState = STATE_UNDO;
    private View mLoadingPage;
    private View mLoadErrorPage;
    private View mLoadEmptyPage;
    private View mLoadSuccessPage;

    public LoadingPage(Context context) {
        super(context);

        initView();
    }

    public LoadingPage(Context context, AttributeSet attrs) {
        super(context, attrs);

        initView();
    }

    public LoadingPage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView();
    }

    private void initView() {
        // 初始化加载中的布局
        if (mLoadingPage == null) {
            mLoadingPage = UIUtils.inflate(R.layout.page_loading);
            addView(mLoadingPage);
        }

        // 初始化加载失败布局
        if (mLoadErrorPage == null) {
            mLoadErrorPage = UIUtils.inflate(R.layout.page_load_error);
            mLoadErrorPage.findViewById(R.id.btn_retry).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //重新加载网络数据
                    loadData();
                }
            });

            addView(mLoadErrorPage);
        }

        // 初始化数据为空布局
        if (mLoadEmptyPage == null) {
            mLoadEmptyPage = UIUtils.inflate(R.layout.page_load_empty);
            addView(mLoadEmptyPage);
        }

        showRightPage(); // 显示正确的page界面
    }

    /**
     * 根据当前的状态显示对应的pager布局
     */
    private void showRightPage() {
        mLoadingPage.setVisibility((mCurrentState == STATE_UNDO || mCurrentState == STATE_LOADING) ? View.VISIBLE : View.GONE);
        mLoadErrorPage.setVisibility(mCurrentState == STATE_LOAD_ERROR ? View.VISIBLE : View.GONE);
        mLoadEmptyPage.setVisibility(mCurrentState == STATE_LOAD_EMPTY ? View.VISIBLE : View.GONE);

        // TODO: 2017/11/26/0026 其实其他几个页面的加载逻辑也可以改成如下形式. 这样可以提高性能.
        // todo: 但由于其他几个页面中的布局相当简单, 不像加载成功的布局比较复杂, 所以性能几乎提高不了什么
        // 当成功布局为空,并且当前状态为成功,才初始化成功的布局
        if (mLoadSuccessPage == null && mCurrentState == STATE_LOAD_SUCCESS) {
            mLoadSuccessPage = onCreateSuccessView();
            if (mLoadSuccessPage != null) { // 当mLoadSuccessPage不为空的时候,才添加至界面中
                addView(mLoadSuccessPage);
            }
        }
        //显示策略
        if (mLoadSuccessPage != null) {
            mLoadSuccessPage.setVisibility(mCurrentState == STATE_LOAD_SUCCESS ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 异步加载数据
     */
    public void loadData() {
        if (mCurrentState != STATE_LOADING) { // 如果当前没有加载, 就开始加载数据
            mCurrentState = STATE_LOADING;

            new Thread() {
                @Override
                public void run() {

                    final ResultState resultState = onLoad();
                    //主线程中更新UI
                    UIUtils.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            if (resultState != null) {
                                mCurrentState = resultState.getState();// 网络加载结束后,更新网络状态

                                // 根据最新的状态来刷新页面
                                showRightPage();
                            }
                        }
                    });
                }
            }.start();
        }
    }

    //因为每个Fragment中加载成功的界面是不同的,所以需要各自来实现,所以变成一个抽象方法, 由使用者来实现
    public abstract View onCreateSuccessView();

    //加载网络数据, 返回值表示请求网络结束后的状态. 每个Fragment中的请求逻辑都不同, 所以由各自实现
    protected abstract ResultState onLoad();

    public enum ResultState {
        STATE_SUCCESS(STATE_LOAD_SUCCESS),
        STATE_EMPTY(STATE_LOAD_EMPTY),
        STATE_ERROR(STATE_LOAD_ERROR);

        private int state;

        private ResultState(int state) {
            this.state = state;
        }

        public int getState() {
            return state;
        }
    }
}
