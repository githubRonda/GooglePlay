package com.ronda.googleplay.manager;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.ronda.googleplay.http.HttpHelper;
import com.ronda.googleplay.http.bean.AppInfo;
import com.ronda.googleplay.http.bean.DownloadInfo;
import com.ronda.googleplay.utils.IOUtils;
import com.ronda.googleplay.utils.UIUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Ronda on 2018/2/8.
 */

public class DownloadManager {


    // 下载状态: 未下载, 等待下载, 正在下载, 暂停下载, 下载失败, 下载成功
    public static final int STATE_UNDO = 1;
    public static final int STATE_WAITING = 2;
    public static final int STATE_DOWNLOADING = 3;
    public static final int STATE_PAUSE = 4;
    public static final int STATE_ERROR = 5;
    public static final int STATE_SUCCESS = 6;

    // 观察者集合
    private List<DownloadObserver> mObserverList = new ArrayList<>();

    public static DownloadManager sManager = new DownloadManager();


    // 下载对象的集合
    //private HashMap<String, DownloadInfo> mDownloadInfoMap = new HashMap<>();
    private ConcurrentHashMap<String, DownloadInfo> mDownloadInfoMap = new ConcurrentHashMap<>(); // 使用ConcurrentHashMap,多线程频繁操作时,避免出错
    //下载任务的集合
    //private HashMap<String, DownloadTask> mDownloadTaskMap = new HashMap<>();
    private ConcurrentHashMap<String, DownloadTask> mDownloadTaskMap = new ConcurrentHashMap<>();



    private DownloadManager() {
    }

    public static DownloadManager getInstance() {
        return sManager;
    }

    // 注册观察者
    public void registerObserver(DownloadObserver observer) {
        if (observer != null && !mObserverList.contains(observer)) {
            mObserverList.add(observer);
        }
    }

    // 注销观察者
    public void unregisterObserver(DownloadObserver observer) {
        if (observer != null && mObserverList.contains(observer)) {
            mObserverList.remove(observer);
        }
    }

    //通知下载状态发生变化
    public void notifyAllDownloadStateChanged(DownloadInfo downloadInfo) {
        for (DownloadObserver observer : mObserverList) {
            observer.onDownloadStateChanged(downloadInfo);
        }
    }

    //通知下载进度发生变化
    public void notifyAllDownloadProgressChanged(DownloadInfo downloadInfo) {
        for (DownloadObserver observer : mObserverList) {
            observer.onDownloadProgressChanged(downloadInfo);
        }
    }

    // 根据应用信息返回下载对象
    public DownloadInfo getDownloadInfo(AppInfo info) {
        return mDownloadInfoMap.get(info.getId());
    }

    //声明观察者的接口
    public interface DownloadObserver {
        // 下载状态发生改变
        public void onDownloadStateChanged(DownloadInfo info);

        // 下载进度发生改变
        public void onDownloadProgressChanged(DownloadInfo info);
    }


    // 开始下载
    public synchronized void download(AppInfo info) {
        // 如果对象是第一次下载, 需要创建一个新的DownloadInfo对象,从头下载
        // 如果之前下载过, 要接着下载,实现断点续传
        DownloadInfo downloadInfo = mDownloadInfoMap.get(info.getId());
        if (downloadInfo == null) {
            downloadInfo = DownloadInfo.getFromAppInfo(info);

            // 将下载对象放入集合中
            mDownloadInfoMap.put(info.getId(), downloadInfo);
        }

        downloadInfo.currentState = STATE_WAITING;
        notifyAllDownloadStateChanged(downloadInfo);
        Log.d("Liu", downloadInfo.name + "等待下载啦~");


        DownloadTask task = new DownloadTask(downloadInfo);
        ThreadPool.getThreadPool().execute(task);

        // 将下载任务放入集合中
        mDownloadTaskMap.put(downloadInfo.id, task);
    }

    // 暂停下载
    public synchronized void pause(AppInfo info) {
        // 取出下载对象
        DownloadInfo downloadInfo = mDownloadInfoMap.get(info.getId());

        if (downloadInfo != null) {
            // 只有在正在下载和等待下载时才需要暂停
            if (downloadInfo.currentState == STATE_WAITING || downloadInfo.currentState == STATE_DOWNLOADING) {

                DownloadTask task = mDownloadTaskMap.get(info.getId());

                if (task != null) {
                    // 移除下载任务, 如果任务还没开始,正在等待, 可以通过此方法移除
                    // 如果任务已经开始运行, 需要在下载循环中的run方法里面进行中断
                    ThreadPool.getThreadPool().cancel(task);
                }

                // 将下载状态切换为暂停
                downloadInfo.currentState = STATE_PAUSE;
                notifyAllDownloadStateChanged(downloadInfo);
            }
        }

    }

    // 安装
    public synchronized void install(AppInfo info) {
        DownloadInfo downloadInfo = mDownloadInfoMap.get(info.getId());
        if (downloadInfo != null) {
            // 跳到系统的安装页面进行安装
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.parse("file://" + downloadInfo.path), "application/vnd.android.package-archive");
            UIUtils.getContext().startActivity(intent);
        }
    }

    // 下载任务对象
    class DownloadTask implements Runnable {
        private DownloadInfo downloadInfo;

        public DownloadTask(DownloadInfo downloadInfo) {
            this.downloadInfo = downloadInfo;
        }

        @Override
        public void run() {

            Log.d("Liu", "开始下载" + downloadInfo.name + "APP了");
            // 状态切换为正在下载
            downloadInfo.currentState = STATE_DOWNLOADING;
            notifyAllDownloadStateChanged(downloadInfo);

            File file = new File(downloadInfo.path);

            HttpHelper.HttpResult httpResult;

            if (!file.exists() || file.length() != downloadInfo.currentDownloadBytes
                    || downloadInfo.currentDownloadBytes == 0) {

                // 删除无效文件, 从头开始下载
                file.delete(); // 文件如果不存在也是可以删除的, 只不过没有效果而已
                downloadInfo.currentDownloadBytes = 0;// 当前下载位置置为0

                // 从头开始下载
                httpResult = HttpHelper.download(HttpHelper.URL + "download?name=" + downloadInfo.downloadUrl);
            } else {
                // 断点续传
                // range 表示请求服务器从文件的哪个位置开始返回数据
                httpResult = HttpHelper.download(HttpHelper.URL + "download?name=" + downloadInfo.downloadUrl
                        + "&range=" + file.length());
            }

            if (httpResult != null && httpResult.getInputStream() != null) {
                InputStream in = httpResult.getInputStream();
                FileOutputStream out = null;

                try {
                    out = new FileOutputStream(file, true);// 要在原有文件基础上追加数据

                    int len = 0;
                    byte[] buf = new byte[1024];

                    // 只有状态是正在下载, 才继续轮询. 解决下载过程中中途暂停的问题
                    while ((len = in.read(buf)) != -1 && downloadInfo.currentState == STATE_DOWNLOADING) {
                        out.write(buf, 0, len);
                        out.flush(); // 把剩余数据刷入本地

                        // 更新下载进度
                        downloadInfo.currentDownloadBytes += len;
                        notifyAllDownloadProgressChanged(downloadInfo);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    IOUtils.close(out);
                    IOUtils.close(in);
                }

                // 文件下载结束
                if (file.length() == downloadInfo.size){
                    // 文件完整, 表示下载成功
                    downloadInfo.currentState = STATE_SUCCESS;
                    notifyAllDownloadStateChanged(downloadInfo);
                }
                else if (downloadInfo.currentState == STATE_PAUSE){
                    // 中途暂停 (这个其实在pause方法中也通知过一次)
                    notifyAllDownloadStateChanged(downloadInfo);
                }
                else{
                    // 下载失败
                    file.delete();// 删除无效文件
                    downloadInfo.currentState = STATE_ERROR;
                    downloadInfo.currentDownloadBytes = 0;
                    notifyAllDownloadStateChanged(downloadInfo);
                }
            }else{
                // 网络异常
                file.delete();// 删除无效文件
                downloadInfo.currentDownloadBytes = 0;
                downloadInfo.currentState= STATE_ERROR;
                notifyAllDownloadStateChanged(downloadInfo);
            }


            // 下载任务已经结束, 不管下载成功,失败还是暂停, 都需要从当前任务集合中移除
            // 不能删除 mDownloadInfoMap 集合中对应的元素, 原因:断点续传时需要找到当前下载的进度
            mDownloadTaskMap.remove(downloadInfo.id);

        }
    }

}
