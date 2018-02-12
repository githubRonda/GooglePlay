package com.ronda.googleplay.ui.fragment.tabs;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ronda.googleplay.http.HttpHelper;
import com.ronda.googleplay.ui.fragment.base.BaseFragment;
import com.ronda.googleplay.ui.view.FlowLayout;
import com.ronda.googleplay.ui.view.LoadingPage;
import com.ronda.googleplay.ui.view.MyFlowLayout;
import com.ronda.googleplay.utils.DrawableUtils;
import com.ronda.googleplay.utils.UIUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static android.R.attr.padding;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/11/25
 * Version: v1.0
 * <p>
 * 排行
 */

public class HotFragment extends BaseFragment {

    private List<String> mData;

    @Override
    public View onCreateSuccessView() {
        ScrollView scrollView = new ScrollView(UIUtils.getContext());
        FlowLayout flowLayout = new FlowLayout(UIUtils.getContext());
        int padding = UIUtils.dip2px(10);
        flowLayout.setPadding(padding, padding, padding, padding); //内边距
        flowLayout.setHorizontalSpacing(UIUtils.dip2px(6)); // 水平间距
        flowLayout.setVerticalSpacing(UIUtils.dip2px(8)); // 垂直间距
//        MyFlowLayout flowLayout = new MyFlowLayout(UIUtils.getContext());

        for (String s : mData) {
            final TextView textView = new TextView(UIUtils.getContext());
            textView.setText(s);

            textView.setTextColor(Color.WHITE);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18); //18sp
            textView.setPadding(padding, padding, padding, padding);
            textView.setGravity(Gravity.CENTER);

            // 生成随机色
            Random random = new Random();
            int r = 30 + random.nextInt(200);
            int g = 30 + random.nextInt(200);
            int b = 30 + random.nextInt(200);

            // 按下后偏白的颜色
            int color = 0xffcecece;

            StateListDrawable selector = DrawableUtils.getSelector(Color.rgb(r, g, b), color, UIUtils.dip2px(6));

            textView.setClickable(true);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(UIUtils.getContext(), textView.getText().toString(), Toast.LENGTH_SHORT).show();
                }
            });

            textView.setBackgroundDrawable(selector);

            flowLayout.addView(textView);
        }
        scrollView.addView(flowLayout);
        return scrollView;
    }

    @Override
    public LoadingPage.ResultState onLoad() {

        HashMap<String, Object> params = new HashMap<>();
        params.put("index", 0);
        String result = HttpHelper.get("hot", params);

        mData = new Gson().fromJson(result, new TypeToken<List<String>>() {
        }.getType());

        return checkRequestResult(mData);
    }
}
