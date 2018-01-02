package com.ronda.googleplay.ui.fragment.tabs;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ronda.googleplay.R;
import com.ronda.googleplay.http.HttpHelper;
import com.ronda.googleplay.http.bean.CategoryInfo;
import com.ronda.googleplay.ui.adapter.CommonAdapter;
import com.ronda.googleplay.ui.adapter.ViewHolder;
import com.ronda.googleplay.ui.fragment.base.BaseFragment;
import com.ronda.googleplay.ui.view.LoadingPage;
import com.ronda.googleplay.ui.view.MyListView;
import com.ronda.googleplay.utils.UIUtils;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/11/25
 * Version: v1.0
 * <p>
 * 分类
 * 这个界面感觉使用ListView实现适用性不强, 应该使用GridView等
 */

public class CategoryFragment extends BaseFragment {

    private List<CategoryInfo.InfosBean> mData;

    @Override
    public View onCreateSuccessView() {
        MyListView listView = new MyListView(UIUtils.getContext());

        CategoryAdapter categoryAdapter = new CategoryAdapter(UIUtils.getContext());
        categoryAdapter.setHasMore(false);

        listView.setAdapter(categoryAdapter);

        return listView;
    }

    @Override
    public LoadingPage.ResultState onLoad() {

        HashMap<String, Object> params = new HashMap<>();
        params.put("index", 0);
        String result = HttpHelper.get("category", params);
        List<CategoryInfo> tmpData = new Gson().fromJson(result, new TypeToken<List<CategoryInfo>>() {
        }.getType());


        // 由于本页面使用的是ListView, 所以必须要对数据进行再处理,把标题和具体数据放到同一级中
        if (tmpData != null) {
            mData = new ArrayList<>();
            for (int i = 0; i < tmpData.size(); i++) {
                CategoryInfo.InfosBean titleBean = new CategoryInfo.InfosBean();
                titleBean.setTitle(tmpData.get(i).getTitle());

                mData.add(titleBean);
                for (CategoryInfo.InfosBean infosBean : tmpData.get(i).getInfos()) {
                    mData.add(infosBean);
                }
            }
        }
        return checkRequestResult(mData);
    }

    class CategoryAdapter extends CommonAdapter<CategoryInfo.InfosBean> implements View.OnClickListener {

        public CategoryAdapter(Context context) {
            super(context, mData, R.layout.item_category, R.layout.item_category_title); // 注意:这里的布局文件的顺序不要写反了, TYPE_NOMAL是第一个, 新增的在后面
        }

        @Override
        public List onLoadMore() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return super.getViewTypeCount() + 1; // 3 即两种普通item, 和加载更多
        }

        @Override
        public int getInnerType(int position) {
            // 判断是标题item类型还是普通item类型
            CategoryInfo.InfosBean infosBean = mData.get(position);
            if (infosBean.isTitle()) { // 标题类型的item
                return super.getInnerType(position) + 1;// 在原来默认的基础上加1, 即值为2, 表示标题类型的Item
            } else { // 普通类型的item
                return super.getInnerType(position);
            }
        }

        @Override
        public void convert(ViewHolder holder, CategoryInfo.InfosBean bean, int position) {

            if (getInnerType(position) == super.getInnerType(position)) { // 普通类型的item

                holder.setText(R.id.tv_name1, bean.getName1());
                holder.setText(R.id.tv_name2, bean.getName2());
                holder.setText(R.id.tv_name3, bean.getName3());

                x.image().bind(((ImageView) holder.getView(R.id.iv_icon1)), HttpHelper.URL + "image?name=" + bean.getUrl1());
                x.image().bind(((ImageView) holder.getView(R.id.iv_icon2)), HttpHelper.URL + "image?name=" + bean.getUrl2());
                x.image().bind(((ImageView) holder.getView(R.id.iv_icon3)), HttpHelper.URL + "image?name=" + bean.getUrl3());

                holder.setOnClickListener(R.id.ll_grid1, this);
                holder.setOnClickListener(R.id.ll_grid2, this);
                holder.setOnClickListener(R.id.ll_grid3, this);

            } else if (getInnerType(position) == super.getInnerType(position) + 1) { // 标题类型的item

                holder.setText(R.id.tv_title, bean.getTitle());
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ll_grid1:
                    String text1 = ((TextView) v.findViewById(R.id.tv_name1)).getText().toString();
                    Toast.makeText(mContext, text1, Toast.LENGTH_SHORT).show();
                    break;
                case R.id.ll_grid2:
                    String text2 = ((TextView) v.findViewById(R.id.tv_name1)).getText().toString();
                    Toast.makeText(mContext, text2, Toast.LENGTH_SHORT).show();
                    break;
                case R.id.ll_grid3:
                    String text3 = ((TextView) v.findViewById(R.id.tv_name1)).getText().toString();
                    Toast.makeText(mContext, text3, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
