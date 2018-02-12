package com.ronda.googleplay.ui.view.holder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.ronda.googleplay.R;
import com.ronda.googleplay.http.HttpHelper;
import com.ronda.googleplay.http.bean.AppInfo;
import com.ronda.googleplay.ui.adapter.ViewHolder;
import com.ronda.googleplay.ui.adapter.ViewHolderWrapper;
import com.ronda.googleplay.utils.UIUtils;

import org.xutils.x;

import java.util.List;

/**
 * Created by Ronda on 2018/1/8.
 */

public class DetailPicsHolder extends ViewHolderWrapper<AppInfo> {

    private ImageView[] ivPics;

    public DetailPicsHolder(Context context) {
        super(context);
    }

    @Override
    public View onCreateView(Context context) {
        return UIUtils.inflate(R.layout.layout_detail_pics);
    }

    @Override
    public void setViewData(AppInfo data) {

        ViewHolder holder = getViewHolder();

        ivPics = new ImageView[5];
        ivPics[0] = holder.getView(R.id.iv_pic1);
        ivPics[1] = holder.getView(R.id.iv_pic2);
        ivPics[2] = holder.getView(R.id.iv_pic3);
        ivPics[3] = holder.getView(R.id.iv_pic4);
        ivPics[4] = holder.getView(R.id.iv_pic5);


        List<String> screenUriList = data.getScreen();
        for (int i = 0; i < ivPics.length; i++) {


            // 跳转至新的Activity进行全屏显示
//            final int pos = i;
//            ivPics[i].setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    //跳转activity, activity展示viewpager
//                    //将集合通过intent传递过去, 当前点击的位置i也可以传过去
//                    Intent intent = new Intent();
//                    intent.putExtra("list", screenUriList);
//                    intent.putExtra("position", pos);
//                }
//            });


            if (i < screenUriList.size()) {
                ivPics[i].setVisibility(View.VISIBLE);
                x.image().bind(ivPics[i], HttpHelper.URL + "image?name=" + screenUriList.get(i));
            } else {
                ivPics[i].setVisibility(View.GONE);
            }
        }

    }
}
