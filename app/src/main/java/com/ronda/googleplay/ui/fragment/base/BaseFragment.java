package com.ronda.googleplay.ui.fragment.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ronda.googleplay.ui.view.LoadingPage;
import com.ronda.googleplay.utils.UIUtils;

import java.util.List;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/11/25
 * Version: v1.0
 */

public abstract class BaseFragment extends Fragment {

    private LoadingPage mLoadingPage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d("Liu", "onCreateView: " + this.getClass().getSimpleName() + ", " + this);

        // 注意:此处一定要调用BaseFragment的onCreateSuccessView, 否则栈溢出
        mLoadingPage = new LoadingPage(UIUtils.getContext()) {
            @Override
            public View onCreateSuccessView() {
                // 注意:此处一定要调用BaseFragment的onCreateSuccessView, 否则栈溢出
                return BaseFragment.this.onCreateSuccessView();
            }

            @Override
            protected ResultState onLoad() {
                return BaseFragment.this.onLoad();
            }
        };
        return mLoadingPage;
    }

    // 加载成功的布局,交由派生类来实现这个方法, 因为每个界面都不一样
    public abstract View onCreateSuccessView();

    // 加载后台数据的方法,交由派生类来实现这个方法, 因为每个界面逻辑都不一样
    public abstract LoadingPage.ResultState onLoad();


    // 公开给MainActivity的方法
    public void loadData() {
        if (mLoadingPage != null) {
            mLoadingPage.loadData();
        }
    }


    /**
     * 根据请求结果, 进行判断, 返回对应的状态值
     * @param obj
     * @return
     */
    public LoadingPage.ResultState checkRequestResult(Object obj){
        if (obj != null){

            if (obj instanceof List){ //判断是否是集合
                List list = ((List) obj);
                if (list.isEmpty()){
                    return LoadingPage.ResultState.STATE_EMPTY;
                }
                else{
                    return LoadingPage.ResultState.STATE_SUCCESS;
                }
            }

        }
        return LoadingPage.ResultState.STATE_ERROR;
    }

}
