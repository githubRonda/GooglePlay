package com.ronda.googleplay.ui.view.holder;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.widget.ImageView;

import com.ronda.googleplay.R;
import com.ronda.googleplay.http.HttpHelper;
import com.ronda.googleplay.http.bean.AppInfo;
import com.ronda.googleplay.ui.adapter.ViewHolder;
import com.ronda.googleplay.ui.adapter.ViewHolderWrapper;
import com.ronda.googleplay.utils.UIUtils;

import org.xutils.x;


/**
 * Created by Ronda on 2018/1/7.
 */

public class DetailAppInfoHolder extends ViewHolderWrapper<AppInfo> {

    public DetailAppInfoHolder(Context context) {
        super(context);
    }

    @Override
    public View onCreateView(Context context) {
        View rooView = UIUtils.inflate(R.layout.layout_detail_appinfo);

        return rooView;
    }

    @Override
    public void setViewData(AppInfo data) {

        ViewHolder holder = getViewHolder();

        holder.setText(R.id.tv_name, data.getName());
        holder.setRating(R.id.rb_star, data.getStars());
        holder.setText(R.id.tv_download_num, "下载量:" + data.getDownloadNum());
        holder.setText(R.id.tv_version, "版本:" + data.getVersion());
        holder.setText(R.id.tv_date, data.getDate());
        holder.setText(R.id.tv_size, Formatter.formatFileSize(UIUtils.getContext(), data.getSize()));

        x.image().bind(((ImageView) holder.getView(R.id.iv_icon)), HttpHelper.URL + "image?name=" + data.getIconUrl());
    }
}
