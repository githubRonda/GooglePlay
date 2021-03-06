package com.ronda.googleplay.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.ronda.googleplay.R;
import com.ronda.googleplay.manager.ThreadPool;
import com.ronda.googleplay.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/11/26
 * Version: v1.0
 *
 * 待优化:
 * 1. 可自定义ViewHolder. Adapter 和 ViewHolder 耦合度太高, 可以提供一个默认的ViewHolder,但也要暴露一个自定义ViewHolder的接口
 */

public abstract class CommonAdapter<T> extends BaseAdapter {

    //注意: 此处必须要从0开始写, 否则AbsListView中就会报ArrayIndexOutOfBoundsException(数组越界异常)
    private static final int TYPE_MORE = 0;// 加载更多类型
    private static final int TYPE_NORMAL = 1;// 正常布局类型. 这里 TYPE_MORE 的值最好为0, 而 TYPE_NORMAL 的值最好为1, 便于子类复写 getInnerType() 方法时直接使用 super.getInnerType(position)+1;

    // 加载更多的几种状态
    public static final int STATE_MORE_MORE = 1; //可以加载更多
    public static final int STATE_MORE_ERROR = 2; //加载更多失败
    public static final int STATE_MORE_NONE = 3; //没有更多数据

    private boolean hasMore = true; // 标记是否有加载更多的Item, 默认有加载更多的item

    private int mLoadMoreState;

    protected Context mContext;
    protected List<T> mDatas;
    protected LayoutInflater mInflater; //这个其实没有用上，
    protected int layoutId;
    protected int[] layoutIds;
    private ViewHolder LoadMoreItemHolder;


    /**
     * 这里要注意第三个形参 layoutId 的含义, 是一个不定形参
     * 一般情况下:只需要传入一个layoutId 表示 TYPE_NORMAL 类型的item即可;
     * 但若是子类需要除了 TYPE_MORE 和 TYPE_NORMAL 之外的其他item类型,则需要传入包括 TYPE_MORE 的在内的多个item的布局
     * 另外,子类若是多布局类型,则 {@link CommonAdapter#getItemViewType(int)} 返回的值必须是连续的.否则就会角标越界.
     * 所以建议子类在复写 getInnerType(int) 方法增加新的item类型时, 建议使用 super.getInnerType(position) + 1 的这种形式
     * layoutId[0] --> super.getInnerType(position) 的返回值 即 TYPE_NORMAL
     * layoutId[1] --> super.getInnerType(position) + 1
     * layoutId[2] --> super.getInnerType(position) + 2
     * ...
     */
    public CommonAdapter(Context context, List<T> datas, int... layoutId) {
        mContext = context;
        mDatas = (datas != null ? datas : new ArrayList<T>());
        this.layoutIds = layoutId;
        mInflater = LayoutInflater.from(context);

        mLoadMoreState = hasMore ? STATE_MORE_MORE : STATE_MORE_NONE;
    }

    @Override
    public int getViewTypeCount() {
        return 2; //两种布局类型: 普通ItemView, 底部加载更多ItemView
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getCount() - 1) {
            return TYPE_MORE;
        } else {
            return getInnerType(position);
        }
    }

    /**
     * 之所以抽取这个方法是为了子类可以重写此方法来更改返回的布局类型.
     * 有时候, ListView中可能不止两种类型的ItemView
     */
    public int getInnerType(int position) {
        return TYPE_NORMAL;// 默认就是普通类型
    }


    @Override
    public int getCount() {
        if (mDatas == null) {
            return 0;
        }
        return mDatas.size() + 1; // 多出了一个底部加载更多的条目
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        if (getItemViewType(position) == TYPE_NORMAL) {
//            ViewHolder holder = ViewHolder.getViewHolder(mContext, convertView, parent, layoutId, position);
//
//            convert(holder, getItem(position));
//
//            return holder.getConvertView();
//        }
        if (getItemViewType(position) != TYPE_MORE) {
            ViewHolder holder = ViewHolder.getViewHolder(mContext, convertView, parent, layoutIds[getItemViewType(position) - 1], position);
            convert(holder, getItem(position), position);
            return holder.getConvertView();
        } else {
            LoadMoreItemHolder = ViewHolder.getViewHolder(mContext, convertView, parent, R.layout.item_load_more, position);

            Log.e("Liu", "hasMore: " + hasMore + ", LoadMoreItemHolder: " + LoadMoreItemHolder);
            // TODO: 2017/11/27/0027 更新UI, 显示或隐藏, 并且加载更多数据
            updateLoadMoreViewState(mLoadMoreState);

            if (mLoadMoreState == STATE_MORE_MORE) {
                // 请求加载更多数据
                loadMore();
            }

            return LoadMoreItemHolder.getConvertView();
        }
    }

    public void updateLoadMoreViewState(int state) {
        if (LoadMoreItemHolder == null) {
            return;
        }

        switch (state) {
            case STATE_MORE_MORE:
                mLoadMoreState = STATE_MORE_MORE;
                // 显示加载更多
                LoadMoreItemHolder.setVisibility(R.id.ll_load_more, true);
                LoadMoreItemHolder.setVisibility(R.id.tv_load_error, false);
                break;
            case STATE_MORE_ERROR:
                mLoadMoreState = STATE_MORE_ERROR;
                // 显示加载失败的布局
                LoadMoreItemHolder.setVisibility(R.id.ll_load_more, false);
                LoadMoreItemHolder.setVisibility(R.id.tv_load_error, true);
                break;
            case STATE_MORE_NONE:
                mLoadMoreState = STATE_MORE_NONE;
                // 隐藏加载更多
                Log.e("Liu", "隐藏加载更多");
                LoadMoreItemHolder.setVisibility(R.id.ll_load_more, false);
                LoadMoreItemHolder.setVisibility(R.id.tv_load_error, false);
                break;
        }
    }

    private boolean isLoadingMore = false;// 标记是否正在加载更多

    private void loadMore() {

        if (isLoadingMore) {
            return;
        }

        isLoadingMore = true;
//        new Thread() {
//            @Override
//            public void run() {
//
//                final List<T> moreData = onLoadMore();
//
//                UIUtils.runOnUIThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (moreData == null) {
//                            updateLoadMoreViewState(STATE_MORE_ERROR);
//                        } else {
//                            // 后台定义的每一页有20条数据(实测有些页面有21条数据), 如果返回的数据小于20条, 就认为到了最后一页了
//                            // 其实也存在刚好这个页面有20条,然后之后就没有数据了. 对于这种情况用户无非就是用户再滑动加载一次,发现下一页数据为0,也是小于20的
//                            if (moreData.size() < 20) {
//                                updateLoadMoreViewState(STATE_MORE_NONE);
//                                Toast.makeText(UIUtils.getContext(), "没有更多数据了", Toast.LENGTH_SHORT).show();
//                            } else {
//                                // 还有更多数据
//                                updateLoadMoreViewState(STATE_MORE_MORE);
//                            }
//
//                            mDatas.addAll(moreData);// 将更多数据追加到当前集合中
//                            notifyDataSetChanged(); // 刷新界面
//                        }
//                    }
//                });
//
//                isLoadingMore = false;
//            }
//        }.start();

        // 使用线程池来改造
        ThreadPool.getThreadPool().execute(new Runnable() {
            @Override
            public void run() {

                final List<T> moreData = onLoadMore();

                UIUtils.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (moreData == null) {
                            updateLoadMoreViewState(STATE_MORE_ERROR);
                        } else {
                            // 后台定义的每一页有20条数据(实测有些页面有21条数据), 如果返回的数据小于20条, 就认为到了最后一页了
                            // 其实也存在刚好这个页面有20条,然后之后就没有数据了. 对于这种情况用户无非就是用户再滑动加载一次,发现下一页数据为0,也是小于20的
                            if (moreData.size() < 20) {
                                updateLoadMoreViewState(STATE_MORE_NONE);
                                Toast.makeText(UIUtils.getContext(), "没有更多数据了", Toast.LENGTH_SHORT).show();
                            } else {
                                // 还有更多数据
                                updateLoadMoreViewState(STATE_MORE_MORE);
                            }

                            mDatas.addAll(moreData);// 将更多数据追加到当前集合中
                            notifyDataSetChanged(); // 刷新界面
                        }
                    }
                });

                isLoadingMore = false;
            }
        });
    }

    /**
     * 获取Adapter中数据部分的条目的大小
     *
     * @return
     */
    public int getDataSize() {
        if (mDatas != null) {
            return mDatas.size();
        } else {
            return 0;
        }
    }

    /**
     * 设置是否有加载更多的ItemView
     *
     * @param hasMore
     */
    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
        mLoadMoreState = hasMore ? STATE_MORE_MORE : STATE_MORE_NONE;
    }


    /**
     * 加载更多数据, 必须由子类实现
     * 子线程中执行
     *
     * @return
     */
    public abstract List<T> onLoadMore();

    /**
     * 控件赋值
     *
     * @param holder
     * @param bean
     */
    public abstract void convert(ViewHolder holder, T bean, int position);
}
