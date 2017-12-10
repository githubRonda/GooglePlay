package com.ronda.googleplay.ui.fragment;

import com.ronda.googleplay.ui.fragment.base.BaseFragment;
import com.ronda.googleplay.ui.fragment.tabs.AppFragment;
import com.ronda.googleplay.ui.fragment.tabs.CategoryFragment;
import com.ronda.googleplay.ui.fragment.tabs.GameFragment;
import com.ronda.googleplay.ui.fragment.tabs.HomeFragment;
import com.ronda.googleplay.ui.fragment.tabs.HotFragment;
import com.ronda.googleplay.ui.fragment.tabs.RecommendFragment;
import com.ronda.googleplay.ui.fragment.tabs.SubjectFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/11/25
 * Version: v1.0
 * <p>
 * 生产fragment的工厂
 */

public class FragmentFactory {

    private static Map<Integer, BaseFragment> map = new HashMap<>();

    public static BaseFragment getFragment(int pos) {

        // 先从集合中取, 如果没有,才创建对象, 提高性能
        BaseFragment baseFragment = map.get(pos);

        if (baseFragment == null) {
            switch (pos) {
                case 0:
                    baseFragment = new HomeFragment();
                    break;
                case 1:
                    baseFragment = new AppFragment();
                    break;
                case 2:
                    baseFragment = new GameFragment();
                    break;
                case 3:
                    baseFragment = new SubjectFragment();
                    break;
                case 4:
                    baseFragment = new RecommendFragment();
                    break;
                case 5:
                    baseFragment = new CategoryFragment();
                    break;
                case 6:
                    baseFragment = new HotFragment();
                    break;
            }

            map.put(pos, baseFragment);
        }
        return baseFragment;
    }

}
