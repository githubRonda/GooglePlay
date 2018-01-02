package com.ronda.googleplay.ui.fragment.tabs;

import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ronda.googleplay.R;
import com.ronda.googleplay.http.HttpHelper;
import com.ronda.googleplay.http.bean.SubjectBean;
import com.ronda.googleplay.ui.adapter.CommonAdapter;
import com.ronda.googleplay.ui.adapter.ViewHolder;
import com.ronda.googleplay.ui.fragment.base.BaseFragment;
import com.ronda.googleplay.ui.view.LoadingPage;
import com.ronda.googleplay.ui.view.MyListView;
import com.ronda.googleplay.utils.UIUtils;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/11/25
 * Version: v1.0
 * <p>
 * 专题
 */

public class SubjectFragment extends BaseFragment {

    private List<SubjectBean> mData;

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
        String result = HttpHelper.get("subject", params);
        mData = new Gson().fromJson(result, new TypeToken<List<SubjectBean>>() {
        }.getType());

        return checkRequestResult(mData);
    }

    class MyAdapter extends CommonAdapter<SubjectBean> {

        private final ImageOptions imageOptions;

        public MyAdapter() {
            super(UIUtils.getContext(), mData, R.layout.item_subject);

            imageOptions = new ImageOptions.Builder()
                    .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                    .setLoadingDrawableId(R.drawable.selector_item_click) // 设置加载中的图片(直接给ImageView用setImageResource()设置一个默认图片是不管用的), 避免图片加载过程中出现白板, 体验不好.
                    .setFailureDrawableId(R.drawable.selector_item_click)
                    .build();
        }

        @Override
        public List<SubjectBean> onLoadMore() {

            Map<String, Object> params = new HashMap<>();
            params.put("index", getDataSize());
            String result = HttpHelper.get("subject", params);

            List<SubjectBean> moreData = new Gson().fromJson(result, new TypeToken<List<SubjectBean>>() {
            }.getType());

            return moreData;
        }

        @Override
        public void convert(ViewHolder holder, SubjectBean bean, int position) {
            holder.setText(R.id.tv_des, bean.getDes());

            x.image().bind(((ImageView) holder.getView(R.id.iv_icon)), HttpHelper.URL + "image?name=" + bean.getUrl(),imageOptions );
        }
    }
}
