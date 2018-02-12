package com.ronda.googleplay.ui.view.holder;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.ronda.googleplay.R;
import com.ronda.googleplay.http.bean.AppInfo;
import com.ronda.googleplay.http.bean.DownloadInfo;
import com.ronda.googleplay.manager.DownloadManager;
import com.ronda.googleplay.ui.adapter.ViewHolderWrapper;
import com.ronda.googleplay.ui.view.ProgressHorizontal;
import com.ronda.googleplay.utils.UIUtils;

/**
 * Created by Ronda on 2018/1/22.
 * <p>
 * 底部下载模块
 */

public class DetailDownloadHolder extends ViewHolderWrapper<AppInfo>
        implements View.OnClickListener, DownloadManager.DownloadObserver {

    private Button btnFav, btnShare, btnDownload;
    private FrameLayout flProgress;
    private ProgressHorizontal progressView;
    private DownloadManager mDm;

    private AppInfo mAppInfo;

    private int mCurrentState;
    private float mProgress;

    public DetailDownloadHolder(Context context) {
        super(context);
    }

    @Override
    public View onCreateView(Context context) {
        View view = UIUtils.inflate(R.layout.layout_detail_download);

        btnFav = (Button) view.findViewById(R.id.btn_fav);
        btnShare = (Button) view.findViewById(R.id.btn_share);
        btnDownload = (Button) view.findViewById(R.id.btn_download);
        flProgress = (FrameLayout) view.findViewById(R.id.fl_progress);

        btnDownload.setOnClickListener(this);
        flProgress.setOnClickListener(this);

        // 初始化自定义进度条
        progressView = new ProgressHorizontal(UIUtils.getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        progressView.setLayoutParams(params);
        progressView.setProgressBackgroundResource(R.drawable.progress_bg);// 进度条背景图片
        progressView.setProgressResource(R.drawable.progress_normal);// 进度条图片
        progressView.setProgressTextColor(Color.WHITE);// 进度文字颜色
        progressView.setProgressTextSize(UIUtils.dip2px(18));// 进度文字大小

        // 给帧布局添加自定义进度条
        flProgress.addView(progressView);

        mDm = DownloadManager.getInstance();
        mDm.registerObserver(this);// 注册观察者, 监听状态和进度变化

        return view;
    }

    @Override
    public void setViewData(AppInfo data) {
        mAppInfo = data;

        // 判断当前应用是否下载过
        DownloadInfo downloadInfo = mDm.getDownloadInfo(data);

        if (downloadInfo != null){
            // 之前下载过
            mCurrentState = downloadInfo.currentState;
            mProgress = downloadInfo.getProgressRatio();
        }else{
            // 没有下载过
            mCurrentState = DownloadManager.STATE_UNDO;
            mProgress = 0f;
        }

        refreshUI(mCurrentState, mProgress);
    }

    // 根据当前的下载进度和状态来更新界面
    private void refreshUI(int currentState, float progress) {
        mCurrentState = currentState;
        mProgress = progress;

        switch (currentState){
            case DownloadManager.STATE_UNDO: // 未下载
                flProgress.setVisibility(View.GONE);
                btnDownload.setVisibility(View.VISIBLE);
                btnDownload.setText("下载");
                break;
            case DownloadManager.STATE_WAITING: // 等待下载
                flProgress.setVisibility(View.GONE);
                btnDownload.setVisibility(View.VISIBLE);
                btnDownload.setText("等待中...");
                break;
            case DownloadManager.STATE_DOWNLOADING:// 正在下载
                btnDownload.setVisibility(View.GONE);
                flProgress.setVisibility(View.VISIBLE);
                progressView.setCenterText("");
                progressView.setProgress(mProgress);// 设置下载进度
                break;
            case DownloadManager.STATE_PAUSE: // 下载暂停
                btnDownload.setVisibility(View.GONE);
                flProgress.setVisibility(View.VISIBLE);
                progressView.setCenterText("暂停");
                progressView.setProgress(mProgress);
                break;
            case DownloadManager.STATE_ERROR://下载失败
                flProgress.setVisibility(View.GONE);
                btnDownload.setVisibility(View.VISIBLE);
                btnDownload.setText("下载失败");
                break;
            case DownloadManager.STATE_SUCCESS://下载成功
                flProgress.setVisibility(View.GONE);
                btnDownload.setVisibility(View.VISIBLE);
                btnDownload.setText("安装");
                break;
        }

    }


    // 主线程更新ui
    // 因为 DownloadInfo 是从 DownloadManager 缓存传递过来的, 所以一个 APP 只对应一个 DownloadInfo
    // 所以主线程中的消息队列中会创建大量的 Runnable 对象, 但他们执行却都是同一个最新的任务. 因为 DownloadInfo 地址没有改变,但是其成员却在不断改变
    // 所以虽然 Runnable 会排队执行有延迟,但是其更新内容却没有延迟
    // 优化: 把Runnable提升为成员变量, 进行一次为空判断, 让Runnable只创建一次
    Runnable runnable;
//    private void refreshUIOnMainThread(final int currentState, final float progress) { // 不能使用这种方式
    private void refreshUIOnMainThread(final DownloadInfo info) {
        // 判断下载对象是否是当前应用
        if (mAppInfo.getId().equals(info.id)) {
            if (runnable ==null){
                runnable = new Runnable() {
                    @Override
                    public void run() {

                        refreshUI(info.currentState, info.getProgressRatio());
                    }
                };
            }
            UIUtils.runOnUIThread(runnable);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_download:
            case R.id.fl_progress:
                if (mCurrentState == DownloadManager.STATE_UNDO
                        || mCurrentState == DownloadManager.STATE_ERROR
                        || mCurrentState == DownloadManager.STATE_PAUSE){
                    mDm.download(mAppInfo); // 开始下载
                }else if (mCurrentState==DownloadManager.STATE_DOWNLOADING
                        || mCurrentState == DownloadManager.STATE_WAITING){
                    mDm.pause(mAppInfo); // 暂停下载
                }
                else if (mCurrentState == DownloadManager.STATE_SUCCESS){
                    mDm.install(mAppInfo); // 开始安装
                }
                break;
        }
    }

    //=================DownloadManager.DownloadObserver===============
    @Override
    public void onDownloadStateChanged(DownloadInfo info) {
        refreshUIOnMainThread(info);
    }

    @Override
    public void onDownloadProgressChanged(DownloadInfo info) {
        refreshUIOnMainThread(info);
    }
}
