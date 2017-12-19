package com.ruanmeng.base;

import android.content.Context;
import android.view.View;

import com.flyco.dialog.widget.base.BottomBaseDialog;

/**
 * 项目名称：Credit_Card
 * 创建人：小卷毛
 * 创建时间：2017-12-19 17:44
 */

public abstract class BottomDialog extends BottomBaseDialog {

    public BottomDialog(Context context, View animateView) {
        super(context, animateView);
    }

    public BottomDialog(Context context) {
        super(context);
    }
}
