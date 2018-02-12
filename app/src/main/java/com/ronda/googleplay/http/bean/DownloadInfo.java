package com.ronda.googleplay.http.bean;

import android.os.Environment;

import com.ronda.googleplay.manager.DownloadManager;

import java.io.File;

/**
 * Created by Ronda on 2018/2/8.
 */

public class DownloadInfo {
    public String id;
    public String name;
    public String downloadUrl;
    public String packageName;
    public long size;

    public long currentDownloadBytes;// 当前下载位置
    public int currentState;// 当前下载状态
    public String path;// 下载到本地文件的路径

    public static final String GOOGLE_MARKET = "GOOGLE_MARKET";// sdcard根目录文件夹名称
    public static final String DONWLOAD = "download";// 子文件夹名称, 存放下载的文件


    //获取下载进度(0-1)
    public float getProgressRatio() {
        if (size == 0) {
            return 0;
        }
        return currentDownloadBytes / (float) size;
    }

    public String getFilePath() {
        StringBuilder sb = new StringBuilder();
        String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        sb.append(sdcardPath);
        sb.append(File.separator);
        sb.append(GOOGLE_MARKET);
        sb.append(File.separator);
        sb.append(DONWLOAD);

        String dir = sb.toString();
        if (createDir(dir)) {
            // 文件夹存在或者已经创建完成
            return dir + File.separator + name + ".apk";
        }
        return null;
    }

    private boolean createDir(String dir) {

        File dirFile = new File(dir);
        // 文件夹不存在或者不是一个文件夹
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return dirFile.mkdirs();
        }
        return true;
    }

    public static DownloadInfo getFromAppInfo(AppInfo appInfo){

        DownloadInfo downloadInfo = new DownloadInfo();
        downloadInfo.id = appInfo.getId();
        downloadInfo.name = appInfo.getName();
        downloadInfo.packageName = appInfo.getPackageName();
        downloadInfo.downloadUrl =appInfo.getDownloadUrl();
        downloadInfo.size = appInfo.getSize();

        downloadInfo.currentState = DownloadManager.STATE_UNDO;
        downloadInfo.path = downloadInfo.getFilePath(); // 自己给自己赋值

        return downloadInfo;
    }
}
