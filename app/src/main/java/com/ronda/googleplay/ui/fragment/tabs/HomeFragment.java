package com.ronda.googleplay.ui.fragment.tabs;

import android.content.Intent;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.gson.reflect.TypeToken;
import com.ronda.googleplay.R;
import com.ronda.googleplay.http.HttpHelper;
import com.ronda.googleplay.http.bean.AppInfo;
import com.ronda.googleplay.http.gson.GsonUtil;
import com.ronda.googleplay.manager.DownloadManager;
import com.ronda.googleplay.ui.activity.HomeDetailActivity;
import com.ronda.googleplay.ui.adapter.CommonAdapter;
import com.ronda.googleplay.ui.adapter.ViewHolder;
import com.ronda.googleplay.ui.adapter2.BaseHolder;
import com.ronda.googleplay.ui.adapter2.MyBaseAdapter;
import com.ronda.googleplay.ui.fragment.base.BaseFragment;
import com.ronda.googleplay.ui.view.ProgressArc;
import com.ronda.googleplay.ui.view.holder.HomeHeaderHolder;
import com.ronda.googleplay.ui.view.LoadingPage;
import com.ronda.googleplay.ui.view.MyListView;
import com.ronda.googleplay.utils.UIUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.x;

import java.util.ArrayList;
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

    private List<AppInfo> mData;
    private List<String> mPicUris;



    //如果数据加载成功,就会回调此方法
    @Override
    public View onCreateSuccessView() {
        MyListView listView = new MyListView(UIUtils.getContext());


        // 给ListView添加头布局展示轮播图
        if (mPicUris != null && mPicUris.size() > 0) {
            // 设置轮播条数据
            HomeHeaderHolder holder = new HomeHeaderHolder(UIUtils.getContext());
            listView.addHeaderView(holder.getRootView());
            holder.setViewData(mPicUris);
        }

        listView.setAdapter(new HomeAdapter(mData));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(UIUtils.getContext(), HomeDetailActivity.class);
                intent.putExtra("packageName", mData.get(position - 1).getPackageName());// 因为有一个头布局,所以要-1
                startActivity(intent);
            }
        });
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

        if (TextUtils.isEmpty(result)){
            return LoadingPage.ResultState.STATE_ERROR;
        }

        try {
            JSONObject rootJsonObj = new JSONObject(result);
            JSONArray picture = rootJsonObj.getJSONArray("picture");
            mPicUris = new ArrayList<>();
            for (int i = 0; i < picture.length(); i++) {
                //mPicUris.add(picture.getJSONArray(i).toString()); //或者
                mPicUris.add(picture.getString(i));
            }

            JSONArray list = rootJsonObj.getJSONArray("list");
            mData = GsonUtil.getGson().fromJson(list.toString(), new TypeToken<List<AppInfo>>() {
            }.getType());
            return checkRequestResult(mData);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return LoadingPage.ResultState.STATE_ERROR;

//        AppInfo appInfoBean = GsonUtil.getGson().fromJson(result, AppInfo.class);
//        if (appInfoBean == null) {
//            return LoadingPage.ResultState.STATE_ERROR;
//        } else {
//            mData = appInfoBean.getList();
//            mPicUris = appInfoBean.getPicture();
//            return checkRequestResult(mData);
//        }
    }


    /**
     * 之所以使用 MyBaseAdapter 是因为 ItemView 有一个下载环形进度的更新,但 CommonAdapter 不支持自定义 ViewHolder.
     * 首页ItemView中的环形进度条的更新逻辑和详情页中水平进度条的更新逻辑相同, 而且需要注册观察者监听进度和状态变化. 并且
     * 对于每一个ItemView都需要注册一个自己的观察者, 所以只能注册在ViewHolder中.
     *
     * 注意:即使ListView的复用机制,导致观察者也复用,不能够实时更新之前的ItemView中的进度, 但是这并不影响. 因为当重新显示之前的ItemView时,进度条的状态就会同步起来.
     * 这也是View的绘制技巧, 对于未显示的View,可以不用更新其数据的变化, 只用当其真正显示出来的时候,才初始化数据,并进行过程数据监听
     */
    class HomeAdapter extends MyBaseAdapter<AppInfo> {

        public HomeAdapter(List<AppInfo> data) {
            super(data);
        }

        @Override
        public BaseHolder<AppInfo> getHolder(int position) {
            return new HomeHolder();
        }

        // 此方法在子线程调用
        @Override
        public List<AppInfo> onLoadMore() {

            HashMap<String, Object> params = new HashMap<>();
            params.put("index", mData.size()); // 在当前获取的数据的个数的基础上再加载下一页数据
            String result = HttpHelper.get("home", params);

            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray list = jsonObject.getJSONArray("list");
                List<AppInfo> appInfoList = GsonUtil.getGson().fromJson(list.toString(), new TypeToken<List<AppInfo>>(){}.getType());

                return appInfoList;

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    /*class HomeAdapter extends CommonAdapter<AppInfo>  {

        private DownloadManager mDm;
        private int mProgress = 0;
        private int mCurrentState = DownloadManager.STATE_UNDO;

        public HomeAdapter() {
            super(UIUtils.getContext(), mData, R.layout.item_home);
        }

        @Override
        public List<AppInfo> onLoadMore() {

            Log.e("Liu", "onLoadMore --> getDataSize: " + getDataSize());

            HashMap<String, Object> params = new HashMap<>();
            params.put("index", getDataSize()); // 在当前获取的数据的个数的基础上再加载下一页数据
            String result = HttpHelper.get("home", params);

            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray list = jsonObject.getJSONArray("list");
                List<AppInfo> appInfoList = GsonUtil.getGson().fromJson(list.toString(), new TypeToken<List<AppInfo>>(){}.getType());

                return appInfoList;

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;

//            AppInfo appInfoBean = GsonUtil.getGson().fromJson(result, AppInfo.class);
//            if (appInfoBean == null) {
//                return null;
//            }
//            return appInfoBean.getList();
        }

        @Override
        public void convert(ViewHolder holder, final AppInfo bean, int position) {
            //holder.setText(R.id.tv, bean.getName());

            //holder.setImageResource()
            holder.setText(R.id.tv_name, bean.getName());
            holder.setRating(R.id.rb_star, bean.getStars());
            holder.setText(R.id.tv_size, Formatter.formatFileSize(UIUtils.getContext(), bean.getSize()));
            holder.setText(R.id.tv_des, bean.getDes());
            x.image().bind(((ImageView) holder.getView(R.id.iv_icon)), HttpHelper.URL + "image?name=" + bean.getIconUrl());

            FrameLayout flProgress = holder.getView(R.id.fl_progress);
            ProgressArc pbProgress = new ProgressArc(UIUtils.getContext());
            // 设置圆形进度条直径
            pbProgress.setArcDiameter(UIUtils.dip2px(26));
            // 设置进度条颜色
            pbProgress.setProgressColor(UIUtils.getColor(R.color.progress));
            // 设置进度条宽高布局参数
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    UIUtils.dip2px(27), UIUtils.dip2px(27));
            flProgress.addView(pbProgress,params);

            flProgress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 根据当前状态来决定下一步操作
                    if (mCurrentState == DownloadManager.STATE_UNDO
                            || mCurrentState == DownloadManager.STATE_PAUSE
                            || mCurrentState == DownloadManager.STATE_ERROR){
                        mDm.download(bean); // 开始下载
                    }
                    else if (mCurrentState == DownloadManager.STATE_WAITING
                            || mCurrentState == DownloadManager.STATE_DOWNLOADING){
                        mDm.pause(bean);
                    }
                    else if (mCurrentState == DownloadManager.STATE_SUCCESS){
                        mDm.install(bean);
                    }
                }
            });
            holder.getView(R.id.tv_download);
        }
    }*/
}
