package com.ronda.googleplay.ui.fragment.tabs;

import android.view.View;

import com.ronda.googleplay.ui.fragment.base.BaseFragment;
import com.ronda.googleplay.ui.view.LoadingPage;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/11/25
 * Version: v1.0
 *
 * 排行
 */

public class HotFragment extends BaseFragment {
    @Override
    public View onCreateSuccessView() {
        return null;
    }

    @Override
    public LoadingPage.ResultState onLoad() {
        return LoadingPage.ResultState.STATE_SUCCESS;
    }
}
