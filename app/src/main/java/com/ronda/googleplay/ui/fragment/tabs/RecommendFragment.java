package com.ronda.googleplay.ui.fragment.tabs;

import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ronda.googleplay.http.HttpHelper;
import com.ronda.googleplay.ui.fragment.base.BaseFragment;
import com.ronda.googleplay.ui.view.LoadingPage;
import com.ronda.googleplay.ui.view.fly.ShakeListener;
import com.ronda.googleplay.ui.view.fly.StellarMap;
import com.ronda.googleplay.utils.UIUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

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

        // StellarMap: Stellar: 星球的意思. 由内向外滑动 和 由外向内滑动的方式是不一样的.
        final StellarMap stellarMap = new StellarMap(UIUtils.getContext());
        stellarMap.setAdapter(new RecommendAdapter());

        // 随机方式, 将控件划分为9行6列的的格子, 然后在格子中随机展示
        stellarMap.setRegularity(6, 9);

        // 设置内边距10dp
        int padding = UIUtils.dip2px(10);
        stellarMap.setPadding(padding, padding, padding, padding);

        // 设置默认页面, 第一组数据, 有动画, 必须要在setAdapter之后才有效
        stellarMap.setGroup(0, true);


        // 晃动手机调到下一页数据(需要真机测试)
        ShakeListener shakeListener = new ShakeListener(UIUtils.getContext());
        shakeListener.setOnShakeListener(new ShakeListener.OnShakeListener() {
            @Override
            public void onShake() {
                stellarMap.zoomIn(); // 向内滑动, 调到下一页数据
            }
        });

        return stellarMap;
    }

    @Override
    public LoadingPage.ResultState onLoad() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("index", 0);
        String recommend = HttpHelper.get("recommend", params);

        mData = new Gson().fromJson(recommend, new TypeToken<List<String>>() {
        }.getType());

        return checkRequestResult(mData);
    }


    class RecommendAdapter implements StellarMap.Adapter {

        // 把数据分为多少组(查看服务器数据可知一共有33条数据,这里分成2组比较合适)
        @Override
        public int getGroupCount() {
            return 2;
        }

        // 返回某组的item个数
        @Override
        public int getCount(int group) {
            // 先平均给每组分配item的个数, 然后把最后的余数添加到最后一页, 保证数据的完整性
            int count = mData.size() / getGroupCount();
            if (group == getGroupCount() - 1) {
                count = count + mData.size() % getGroupCount();
            }
            return count;
        }

        // 初始化布局
        @Override
        public View getView(int group, int position, View convertView) {

            // 对于每一组数据而言, position 都是从0开始计数, 所以需要将前面几组数据的个数加起来, 才能确定当前组中数据的正确位置
            position += group * getCount(group - 1); // 除了最后一组外, 其他所有组的数据的个数一定是一样的. 所以这里就相当于 前面组的个数 * 每组的数量

            TextView textView = new TextView(UIUtils.getContext());
            final String keyword = mData.get(position);
            textView.setText(keyword);

            Random random = new Random();
            // 文字大小随机 (16~25)
            int size = 16 + random.nextInt(10);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);

            // 文字颜色随机 (30~230)
            int r = 30 + random.nextInt(200);
            int g = 30 + random.nextInt(200);
            int b = 30 + random.nextInt(200);
            textView.setTextColor(Color.rgb(r, g, b));

            // 给TextView 设置点击事件
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(UIUtils.getContext(),keyword, Toast.LENGTH_SHORT).show();
                }
            });

            return textView;
        }

        // 返回下一组的组id
        @Override
        public int getNextGroupOnZoom(int group, boolean isZoomIn) {
            if (isZoomIn) {
                // 向外滑 isZoomIn = true,  加载下一页数据
                group = (group - 1 + getGroupCount()) % getGroupCount(); // -1 % 2 = -1. 所以这里要加上 getGroupCount() 避免变成负数
            } else {
                // 向里滑动 isZoomIn = false
                group = (group + 1) % getGroupCount();
            }
            return group;
        }
    }
}
