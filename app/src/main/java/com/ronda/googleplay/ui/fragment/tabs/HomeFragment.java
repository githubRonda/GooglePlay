package com.ronda.googleplay.ui.fragment.tabs;

import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.ronda.googleplay.R;
import com.ronda.googleplay.http.HttpHelper;
import com.ronda.googleplay.http.bean.AppInfoBean;
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

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/11/25
 * Version: v1.0
 * <p>
 * 首页
 */

public class HomeFragment extends BaseFragment {

    private List<AppInfoBean.ListBean> mData;

    //如果数据加载成功,就会回调此方法
    @Override
    public View onCreateSuccessView() {
        MyListView listView = new MyListView(UIUtils.getContext());

        listView.setAdapter(new HomeAdapter());


        return listView;
    }

    // 运行在子线程,可以直接执行耗时网络操作
    @Override
    public LoadingPage.ResultState onLoad() {
        //请求网络, 常用方法: HttpClient, HttpUrlConnection, XUtils

        // 其实每次按返回键退出程序重新进来时, 原有的data数据会保留, Activity虽然会重新创建一个,但是Fragment会恢复, 所以原来的数据会保留

        HashMap<String, Object> params = new HashMap<>();
        params.put("index", 0); // 服务器固定每页返回20或21条数据, index 表示偏移量
        String result = HttpHelper.get("home", params);

        AppInfoBean appInfoBean = GsonUtil.getGson().fromJson(result, AppInfoBean.class);
        mData = appInfoBean.getList();

        return checkRequestResult(mData);
    }

    class HomeAdapter extends CommonAdapter<AppInfoBean.ListBean> {

        public HomeAdapter() {
            super(UIUtils.getContext(), mData, R.layout.item_home);
        }

        @Override
        public List<AppInfoBean.ListBean> onLoadMore() {

            Log.e("Liu", "onLoadMore --> getDataSize: " + getDataSize());

            HashMap<String, Object> params = new HashMap<>();
            params.put("index", getDataSize()); // 在当前获取的数据的个数的基础上再加载下一页数据
            String result = HttpHelper.get("home", params);
            AppInfoBean appInfoBean = GsonUtil.getGson().fromJson(result, AppInfoBean.class);
            return appInfoBean.getList();
        }

        @Override
        public void convert(ViewHolder holder, AppInfoBean.ListBean bean) {
            //holder.setText(R.id.tv, bean.getName());

            //holder.setImageResource()
            holder.setText(R.id.tv_name, bean.getName());
            holder.setRating(R.id.rb_star, bean.getStars());
            holder.setText(R.id.tv_size, Formatter.formatFileSize(UIUtils.getContext(), bean.getSize()));
            holder.setText(R.id.tv_des, bean.getDes());
            x.image().bind(((ImageView) holder.getView(R.id.iv_icon)), HttpHelper.URL + "image?name=" + bean.getIconUrl());
        }
    }
}
