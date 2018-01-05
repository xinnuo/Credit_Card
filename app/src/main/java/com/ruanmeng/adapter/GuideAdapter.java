/**
 * created by 小卷毛, 2018/01/04
 * Copyright (c) 2018, 416143467@qq.com All Rights Reserved.
 * #                                                   #
 * #                       _oo0oo_                     #
 * #                      o8888888o                    #
 * #                      88" . "88                    #
 * #                      (| -_- |)                    #
 * #                      0\  =  /0                    #
 * #                    ___/`---'\___                  #
 * #                  .' \\|     |# '.                 #
 * #                 / \\|||  :  |||# \                #
 * #                / _||||| -:- |||||- \              #
 * #               |   | \\\  -  #/ |   |              #
 * #               | \_|  ''\---/''  |_/ |             #
 * #               \  .-\__  '-'  ___/-. /             #
 * #             ___'. .'  /--.--\  `. .'___           #
 * #          ."" '<  `.___\_<|>_/___.' >' "".         #
 * #         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       #
 * #         \  \ `_.   \_ __\ /__ _/   .-` /  /       #
 * #     =====`-.____`.___ \_____/___.-`___.-'=====    #
 * #                       `=---='                     #
 * #     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   #
 * #                                                   #
 * #               佛祖保佑         永无BUG             #
 * #                                                   #
 */
package com.ruanmeng.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jude.rollviewpager.adapter.StaticPagerAdapter;
import com.ruanmeng.base.GlideApp;
import com.ruanmeng.credit_card.R;
import com.ruanmeng.share.BaseHttp;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：Credit_Card
 * 创建人：小卷毛
 * 创建时间：2018-01-04 16:28
 */
public class GuideAdapter extends StaticPagerAdapter {

    private Context context;
    private List<String> imgs = new ArrayList<>();

    public void setImgs(List<String> imgs) {
        this.imgs = imgs;
        notifyDataSetChanged();
    }

    public GuideAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View getView(ViewGroup container, int position) {
        View view = View.inflate(context, R.layout.item_banner_img, null);
        ImageView iv_img = view.findViewById(R.id.iv_banner_img);

        GlideApp.with(context)
                .load(BaseHttp.baseImg + imgs.get(position))
                .dontAnimate()
                .into(iv_img);
        return view;
    }

    @Override
    public int getCount() {
        return imgs.size();
    }
}
