package com.ronda.googleplay.ui.fragment.tabs;

import android.security.keystore.KeyGenParameterSpec;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ronda.googleplay.http.HttpHelper;
import com.ronda.googleplay.ui.fragment.base.BaseFragment;
import com.ronda.googleplay.ui.view.LoadingPage;
import com.ronda.googleplay.ui.view.fly.StellarMap;
import com.ronda.googleplay.utils.UIUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/11/25
 * Version: v1.0
 * <p>
 * 推荐
 */

public class RecommendFragment extends BaseFragment {

    private List<String> mData;

    @Override
    public View onCreateSuccessView() {

        StellarMap stellarMap = new StellarMap(UIUtils.getContext());
        stellarMap.setAdapter(new RecommendAdapter());

        // 随机方式, 将控件划分为9行6列的的格子, 然后在格子中随机展示
        stellarMap.setRegularity(6, 9);

        // 设置内边距10dp
        int padding = UIUtils.dip2px(10);
        stellarMap.setPadding(padding, padding, padding, padding);

        // 设置默认页面, 第一组数据, 有动画, 必须要在setAdapter之后才有效
        stellarMap.setGroup(0, true);


        return stellarMap;
    }

    @Override
    public LoadingPage.ResultState onLoad() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("index", 0);
        String recommend = HttpHelper.get("recommend", params);

        mData = new Gson().fromJson(recommend, new TypeToken<List<String>>() {
        }.getType());

        System.out.println(mData);

        return checkRequestResult(mData);
    }


    class RecommendAdapter implements StellarMap.Adapter {

        // 把数据分为多少组
        @Override
        public int getGroupCount() {
            return 2;
        }

        // 返回某组的item个数
        @Override
        public int getCount(int group) {
            return 16;
        }

        // 初始化布局
        @Override
        public View getView(int group, int position, View convertView) {

            TextView textView = new TextView(UIUtils.getContext());
            String keyword = mData.get(position);
            textView.setText(keyword);
            System.out.println(keyword);

            return textView;
        }

        // 返回下一组的组id
        @Override
        public int getNextGroupOnZoom(int group, boolean isZoomIn) {
            System.out.println("isZoomIn: " + isZoomIn);
            return 0;
        }
    }
}
