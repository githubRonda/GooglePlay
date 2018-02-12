package com.ronda.googleplay.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;

import com.google.gson.Gson;
import com.ronda.googleplay.R;
import com.ronda.googleplay.http.HttpHelper;
import com.ronda.googleplay.http.bean.AppInfo;
import com.ronda.googleplay.ui.view.LoadingPage;
import com.ronda.googleplay.ui.view.holder.DetailAppInfoHolder;
import com.ronda.googleplay.ui.view.holder.DetailDesHolder;
import com.ronda.googleplay.ui.view.holder.DetailDownloadHolder;
import com.ronda.googleplay.ui.view.holder.DetailPicsHolder;
import com.ronda.googleplay.ui.view.holder.DetailSafeHolder;
import com.ronda.googleplay.utils.UIUtils;

import java.util.HashMap;

/**
 * Created by Ronda on 2018/1/7.
 * <p>
 * 首页中的应用详情页
 */

public class HomeDetailActivity extends BaseActivity {

    private LoadingPage mLoadingPage;
    private String mPackageName;

    private AppInfo mData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initActionBar();

        mLoadingPage = new LoadingPage(UIUtils.getContext()) {

            @Override
            public View onCreateSuccessView() {
                // 初始化成功的布局
                View rootView = UIUtils.inflate(R.layout.page_home_detail);

                // 初始化应用信息模块
                FrameLayout flDetailAppInfo = (FrameLayout) rootView.findViewById(R.id.fl_detail_appinfo);
                // 动态给帧布局填充页面
                DetailAppInfoHolder appInfoHolder = new DetailAppInfoHolder(UIUtils.getContext());
                appInfoHolder.setViewData(mData);
                flDetailAppInfo.addView(appInfoHolder.getRootView());

                // 初始化应用安全模块
                FrameLayout flDetailSafe = (FrameLayout) rootView.findViewById(R.id.fl_detail_safe);
                DetailSafeHolder safeHolder = new DetailSafeHolder(UIUtils.getContext());
                safeHolder.setViewData(mData);
                flDetailSafe.addView(safeHolder.getRootView());

                // 初始化应用截图模块
                HorizontalScrollView hsvDetailPics = (HorizontalScrollView) rootView.findViewById(R.id.hsv_detail_pics);
                DetailPicsHolder picsHolder = new DetailPicsHolder(UIUtils.getContext());
                picsHolder.setViewData(mData);
                hsvDetailPics.addView(picsHolder.getRootView());

                // 初始化应用详情模块
                FrameLayout flDetailDes = (FrameLayout) rootView.findViewById(R.id.fl_detail_des);
                DetailDesHolder desHolder = new DetailDesHolder(UIUtils.getContext());
                desHolder.setViewData(mData);
                flDetailDes.addView(desHolder.getRootView());

                // 初始化下载模块
                FrameLayout flDetailDownload = (FrameLayout) rootView.findViewById(R.id.fl_detail_download);
                DetailDownloadHolder downloadHolder = new DetailDownloadHolder(UIUtils.getContext());
                downloadHolder.setViewData(mData);
                flDetailDownload.addView(downloadHolder.getRootView());

                return rootView;
            }

            @Override
            protected ResultState onLoad() {

                HashMap<String, Object> params = new HashMap<>();
                params.put("packageName", mPackageName);
                String result = HttpHelper.get("detail", params);

                if (TextUtils.isEmpty(result)) {
                    return ResultState.STATE_ERROR;
                } else {
                    mData = new Gson().fromJson(result, AppInfo.class);
                    return ResultState.STATE_SUCCESS;
                }
            }
        };

        // 获取从HomeFragment传递过来的包名
        mPackageName = getIntent().getStringExtra("packageName");

        // 开始加载网络数据
        mLoadingPage.loadData();

        setContentView(mLoadingPage);
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
