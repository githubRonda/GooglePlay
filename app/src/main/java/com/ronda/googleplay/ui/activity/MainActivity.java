package com.ronda.googleplay.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ronda.googleplay.R;
import com.ronda.googleplay.ui.fragment.FragmentFactory;
import com.ronda.googleplay.ui.fragment.base.BaseFragment;
import com.ronda.googleplay.ui.view.PagerTab;
import com.ronda.googleplay.utils.UIUtils;

public class MainActivity extends BaseActivity {

    private PagerTab mPagerTab;
    private ViewPager mViewPager;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("Liu", "savedInstanceState: " + savedInstanceState + ", " + this.getClass().getSimpleName() + ", " + this);

        initView();
    }

    private void initView() {
        mPagerTab = (PagerTab) findViewById(R.id.pager_tab);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        mAdapter = new MyAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);

        mPagerTab.setViewPager(mViewPager);// 将指针和viewpager绑定在一起, 必须要在ViewPager初始化且设置Adapter之后才可以

        mPagerTab.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            // 在第一次进来的时候, 该方法也会被回调
            @Override
            public void onPageSelected(int position) {
                BaseFragment fragment = FragmentFactory.getFragment(position);
                // 开始加载数据
                fragment.loadData();

                Log.d("TAG", "position: " + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    /**
     * FragmentPagerAdapter是PagerAdapter的子类, 如果viewpager的页面是fragment的话,就继承此类
     */
    class MyAdapter extends FragmentPagerAdapter {

        private final String[] mTabTitles;

        public MyAdapter(FragmentManager fm) {
            super(fm);

            mTabTitles = UIUtils.getStringArray(R.array.tab_names);// 加载页面标题数组
        }

        // 返回页签标题
        @Override
        public CharSequence getPageTitle(int position) {
            return mTabTitles[position];
        }

        // 返回当前页面位置的fragment对象
        @Override
        public Fragment getItem(int position) {
            return FragmentFactory.getFragment(position);
        }

        @Override
        public int getCount() {
            return mTabTitles.length;
        }
    }
}
