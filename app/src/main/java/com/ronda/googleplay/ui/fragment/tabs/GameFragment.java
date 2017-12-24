package com.ronda.googleplay.ui.fragment.tabs;

import android.view.View;
import android.widget.TextView;

import com.ronda.googleplay.ui.fragment.base.BaseFragment;
import com.ronda.googleplay.ui.view.LoadingPage;
import com.ronda.googleplay.utils.UIUtils;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/11/25
 * Version: v1.0
 *
 * 游戏
 */

public class GameFragment extends BaseFragment {
    @Override
    public View onCreateSuccessView() {
        TextView textView = new TextView(UIUtils.getContext());
        textView.setText("GameFragment");
        return textView;
    }

    @Override
    public LoadingPage.ResultState onLoad() {
        return LoadingPage.ResultState.STATE_SUCCESS;
    }
}
