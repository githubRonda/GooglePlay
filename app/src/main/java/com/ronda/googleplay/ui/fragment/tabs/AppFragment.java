package com.ronda.googleplay.ui.fragment.tabs;

import android.text.format.Formatter;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.reflect.TypeToken;
import com.ronda.googleplay.R;
import com.ronda.googleplay.http.HttpHelper;
import com.ronda.googleplay.http.bean.AppInfo;
import com.ronda.googleplay.http.gson.GsonUtil;
import com.ronda.googleplay.ui.adapter.CommonAdapter;
import com.ronda.googleplay.ui.adapter.ViewHolder;
import com.ronda.googleplay.ui.fragment.base.BaseFragment;
import com.ronda.googleplay.ui.view.LoadingPage;
import com.ronda.googleplay.ui.view.MyListView;
import com.ronda.googleplay.utils.UIUtils;

import org.xutils.x;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/11/25
 * Version: v1.0
 *
 * 应用
 */

public class AppFragment extends BaseFragment {

    private List<AppInfo> mData;

    @Override
    public View onCreateSuccessView() {

        MyListView listView = new MyListView(UIUtils.getContext());
        listView.setAdapter(new MyAdapter());

        return listView;
    }

    @Override
    public LoadingPage.ResultState onLoad() {

        Map<String, Object> params = new HashMap<>();
        params.put("index", 0);
        String result = HttpHelper.get("app", params);
        List<AppInfo> mData = GsonUtil.getGson().fromJson(result, new TypeToken<List<AppInfo>>() {
        }.getType());

        return checkRequestResult(mData);
    }

    class MyAdapter extends CommonAdapter<AppInfo>{

        public MyAdapter() {
            super(UIUtils.getContext(), mData, R.layout.item_home);
        }

        @Override
        public List<AppInfo> onLoadMore() {
            Map<String, Object> params = new HashMap<>();
            params.put("index", getDataSize());
            String result = HttpHelper.get("app", params);
            List<AppInfo> moreData = GsonUtil.getGson().fromJson(result, new TypeToken<List<AppInfo>>() {
            }.getType());

            return moreData;
        }

        @Override
        public void convert(ViewHolder holder, AppInfo bean, int position) {
            holder.setText(R.id.tv_name, bean.getName());
            holder.setRating(R.id.rb_star, bean.getStars());
            holder.setText(R.id.tv_size, Formatter.formatFileSize(UIUtils.getContext(), bean.getSize()));
            holder.setText(R.id.tv_des, bean.getDes());
            x.image().bind(((ImageView) holder.getView(R.id.iv_icon)), HttpHelper.URL + "image?name=" + bean.getIconUrl());
        }
    }
}
