package com.ronda.googleplay.http.bean;

import java.util.List;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/12/03
 * Version: v1.0
 *
 * 详情页和首页有很多字段都是相同的,最好封装到一个类中,这样在实现下载时,两个界面传入对象也比较方便
 * 其实甚至是可以把 DownloadInfo 合并到这个类中
 */

public class AppInfo {
    /**
     * id : 1641339
     * name : 中华万年历日历
     * packageName : cn.etouch.ecalendar
     * iconUrl : app/cn.etouch.ecalendar/icon.jpg
     * stars : 4.5
     * downloadNum : 1000万+
     * version : 4.5.1
     * date : 2014-06-12
     * size : 5098427
     * downloadUrl : app/cn.etouch.ecalendar/cn.etouch.ecalendar.apk
     * des :
     * author : 随身移动
     * screen : ["app/cn.etouch.ecalendar/screen0.jpg"]
     * safe : [{"safeUrl":"app/cn.etouch.ecalendar/safeIcon0.jpg","safeDesUrl":"app/cn.etouch.ecalendar/safeDesUrl0.jpg","safeDes":"已通过安智市场安全检测，请放心使用","safeDesColor":0}]
     */

    private String id;
    private String name;
    private String packageName;
    private String iconUrl;
    private float stars;
    private long size;
    private String downloadUrl;
    private String des;

    //补充字段, 供应用详情页使用
    private String downloadNum;
    private String version;
    private String date;
    private String author;
    private List<String> screen;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public float getStars() {
        return stars;
    }

    public void setStars(float stars) {
        this.stars = stars;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getDownloadNum() {
        return downloadNum;
    }

    public void setDownloadNum(String downloadNum) {
        this.downloadNum = downloadNum;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public List<String> getScreen() {
        return screen;
    }

    public void setScreen(List<String> screen) {
        this.screen = screen;
    }

    public List<SafeBean> getSafe() {
        return safe;
    }

    public void setSafe(List<SafeBean> safe) {
        this.safe = safe;
    }

    private List<SafeBean> safe;


    public static class SafeBean {
        /**
         * safeUrl : app/cn.etouch.ecalendar/safeIcon0.jpg
         * safeDesUrl : app/cn.etouch.ecalendar/safeDesUrl0.jpg
         * safeDes : 已通过安智市场安全检测，请放心使用
         * safeDesColor : 0
         */

        private String safeUrl;
        private String safeDesUrl;
        private String safeDes;
        private int safeDesColor;

        public String getSafeUrl() {
            return safeUrl;
        }

        public void setSafeUrl(String safeUrl) {
            this.safeUrl = safeUrl;
        }

        public String getSafeDesUrl() {
            return safeDesUrl;
        }

        public void setSafeDesUrl(String safeDesUrl) {
            this.safeDesUrl = safeDesUrl;
        }

        public String getSafeDes() {
            return safeDes;
        }

        public void setSafeDes(String safeDes) {
            this.safeDes = safeDes;
        }

        public int getSafeDesColor() {
            return safeDesColor;
        }

        public void setSafeDesColor(int safeDesColor) {
            this.safeDesColor = safeDesColor;
        }
    }

//    private List<String> picture;
//    /**
//     * id : 1525490
//     * name : 有缘网
//     * packageName : com.youyuan.yyhl
//     * iconUrl : app/com.youyuan.yyhl/icon.jpg
//     * stars : 4
//     * size : 3876203
//     * downloadUrl : app/com.youyuan.yyhl/com.youyuan.yyhl.apk
//     * des : 产品介绍：有缘是时下最受大众单身男女亲睐的婚恋交友软件。有缘网专注于通过轻松、
//     */
//
//    private List<ListBean> list;
//
//    public List<String> getPicture() {
//        return picture;
//    }
//
//    public void setPicture(List<String> picture) {
//        this.picture = picture;
//    }
//
//    public List<ListBean> getList() {
//        return list;
//    }
//
//    public void setList(List<ListBean> list) {
//        this.list = list;
//    }
//
//    public static class ListBean {
//        private int id;
//        private String name;
//        private String packageName;
//        private String iconUrl;
//        private float stars;
//        private long size;
//        private String downloadUrl;
//        private String des;
//
//        public int getId() {
//            return id;
//        }
//
//        public void setId(int id) {
//            this.id = id;
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        public void setName(String name) {
//            this.name = name;
//        }
//
//        public String getPackageName() {
//            return packageName;
//        }
//
//        public void setPackageName(String packageName) {
//            this.packageName = packageName;
//        }
//
//        public String getIconUrl() {
//            return iconUrl;
//        }
//
//        public void setIconUrl(String iconUrl) {
//            this.iconUrl = iconUrl;
//        }
//
//        public float getStars() {
//            return stars;
//        }
//
//        public void setStars(float stars) {
//            this.stars = stars;
//        }
//
//        public long getSize() {
//            return size;
//        }
//
//        public void setSize(long size) {
//            this.size = size;
//        }
//
//        public String getDownloadUrl() {
//            return downloadUrl;
//        }
//
//        public void setDownloadUrl(String downloadUrl) {
//            this.downloadUrl = downloadUrl;
//        }
//
//        public String getDes() {
//            return des;
//        }
//
//        public void setDes(String des) {
//            this.des = des;
//        }
//    }
}
