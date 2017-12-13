package com.ruanmeng.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

/**
 * 项目名称：Credit_Card
 * 创建人：小卷毛
 * 创建时间：2017-12-06 17:02
 */
public class TabFragmentAdapter extends FragmentPagerAdapter {

    private final List<String> titles;
    private List<Fragment> fragments;

    public TabFragmentAdapter(FragmentManager fm, List<String> titles, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
        this.titles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return titles.size();
    }

    // 配置标题的方法
    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

    // 动态添加删除的方法
    @Override
    public long getItemId(int position) {
        super.getItemId(position);
        if (fragments != null) {
            if (position < fragments.size()) {
                return fragments.get(position).hashCode();
            }
        }
        return super.getItemId(position);
    }

}
