package com.ruanmeng.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.flyco.animation.BaseAnimatorSet;
import com.flyco.animation.BounceEnter.BounceTopEnter;
import com.flyco.animation.SlideExit.SlideBottomExit;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.widget.MaterialDialog;
import com.flyco.dialog.widget.base.BottomBaseDialog;
import com.maning.mndialoglibrary.MProgressDialog;
import com.ruanmeng.credit_card.R;
import com.weigan.loopview.LoopView;

import java.util.List;

/**
 * 项目名称：Credit_Card
 * 创建人：小卷毛
 * 创建时间：2017-12-05 10:50
 */

public class DialogHelper {

    @SuppressLint("StaticFieldLeak")
    private static MProgressDialog mMProgressDialog;

    private DialogHelper() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static void showDialog(Context context) {
        mMProgressDialog = new MProgressDialog.Builder(context)
                .setCancelable(true)
                .isCanceledOnTouchOutside(false)
                .setDimAmount(0.5f)
                .build();

        mMProgressDialog.show();
    }

    public static void dismissDialog() {
        if (mMProgressDialog != null && mMProgressDialog.isShowing())
            mMProgressDialog.dismiss();
    }

    public static void showDialog(
            final Context context,
            final String title,
            final String content,
            final String btnText) {
        showDialog(context, title, content, btnText, null);
    }

    public static void showDialog(
            final Context context,
            final String title,
            final String content,
            final String btnText,
            final HintCallBack msgCallBack) {
        final MaterialDialog materialDialog = new MaterialDialog(context);
        materialDialog.content(content)
                .title(title)
                .btnText(btnText)
                .btnNum(1)
                .btnTextColor(context.getResources().getColor(R.color.colorAccent))
                .showAnim(new BounceTopEnter())
                .show();
        materialDialog.setOnBtnClickL(
                new OnBtnClickL() { //left btn click listener
                    @Override
                    public void onBtnClick() {
                        materialDialog.dismiss();
                        if (msgCallBack != null) msgCallBack.doWork();
                    }
                }
        );
    }

    public static void showDialog(
            Context context,
            String title,
            String content,
            String left,
            String right,
            HintCallBack msgCallBack) {
        showDialog(context, title, content, left, right, true, msgCallBack);
    }

    public static void showDialog(
            Context context,
            String title,
            String content,
            String left,
            String right,
            BaseAnimatorSet dismissAnim,
            final HintCallBack msgCallBack) {
        showDialog(context, title, content, left, right, new BounceTopEnter(), dismissAnim, true, msgCallBack);
    }

    public static void showDialog(
            final Context context,
            final String title,
            final String content,
            final String left,
            final String right,
            boolean isOutDismiss,
            final HintCallBack msgCallBack) {
        showDialog(context, title, content, left, right, new BounceTopEnter(), new SlideBottomExit(), isOutDismiss, msgCallBack);
    }

    public static void showDialog(
            Context context,
            String title,
            String content,
            String left,
            String right,
            BaseAnimatorSet showAnim,
            BaseAnimatorSet dismissAnim,
            boolean isOutDismiss,
            final HintCallBack msgCallBack) {
        final MaterialDialog dialog = new MaterialDialog(context);
        dialog.content(content)
                .title(title)
                .contentTextColor(context.getResources().getColor(R.color.black))
                .btnText(left, right)
                .btnTextColor(
                        context.getResources().getColor(R.color.black),
                        context.getResources().getColor(R.color.colorAccent))
                .showAnim(showAnim)
                .dismissAnim(dismissAnim)
                .show();
        dialog.setCanceledOnTouchOutside(isOutDismiss);
        dialog.setOnBtnClickL(
                new OnBtnClickL() {//left btn click listener
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                    }
                },
                new OnBtnClickL() {//right btn click listener
                    @Override
                    public void onBtnClick() {
                        dialog.dismiss();
                        msgCallBack.doWork();
                    }
                }
        );
    }

    public static void showItemDialog(
            final Context context,
            final String title,
            final List<String> items,
            final ItemCallBack callBack) {

        BottomBaseDialog dialog = new BottomBaseDialog(context) {

            private LoopView loopView;

            @Override
            public View onCreateView() {
                View view = View.inflate(context, R.layout.dialog_select_item, null);

                TextView tv_title = view.findViewById(R.id.tv_dialog_select_title);
                TextView tv_cancel = view.findViewById(R.id.tv_dialog_select_cancle);
                TextView tv_ok = view.findViewById(R.id.tv_dialog_select_ok);
                loopView = view.findViewById(R.id.lv_dialog_select_loop);

                tv_title.setText(title);
                loopView.setTextSize(14f);
                loopView.setDividerColor(context.getResources().getColor(R.color.divider));
                loopView.setNotLoop();

                tv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });

                tv_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();

                        callBack.doWork(loopView.getSelectedItem(), items.get(loopView.getSelectedItem()));
                    }
                });

                return view;
            }

            @Override
            public void setUiBeforShow() {
                loopView.setItems(items);
            }

        };

        dialog.show();
    }

    public interface HintCallBack {
        void doWork();
    }

    public interface ItemCallBack {
        void doWork(int position, String name);
    }

}
