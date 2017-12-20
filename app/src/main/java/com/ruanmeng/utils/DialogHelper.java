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
import com.weigan.loopview.OnItemSelectedListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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

    public static void showDateDialog(
            final Context context,
            final int minYearValue,
            final int maxYearValue,
            final int count,
            final String title,
            final boolean isCurrentDate,
            final boolean isLimited,
            final DateAllCallBack callback) {

        BottomBaseDialog dialog = new BottomBaseDialog(context) {

            private LoopView loop_year, loop_month, loop_day, loop_hour, loop_minute;

            @Override
            public View onCreateView() {
                View view = View.inflate(context, R.layout.dialog_select_time, null);

                TextView tv_title = view.findViewById(R.id.tv_dialog_select_title);
                TextView tv_cancel = view.findViewById(R.id.tv_dialog_select_cancle);
                TextView tv_ok = view.findViewById(R.id.tv_dialog_select_ok);
                loop_year = view.findViewById(R.id.lv_dialog_select_year);
                loop_month = view.findViewById(R.id.lv_dialog_select_month);
                loop_day = view.findViewById(R.id.lv_dialog_select_day);
                loop_hour = view.findViewById(R.id.lv_dialog_select_hour);
                loop_minute = view.findViewById(R.id.lv_dialog_select_minute);

                tv_title.setText(title);
                loop_year.setTextSize(15f);
                loop_month.setTextSize(15f);
                loop_day.setTextSize(15f);
                loop_hour.setTextSize(15f);
                loop_minute.setTextSize(15f);
                loop_year.setNotLoop();
                loop_month.setNotLoop();
                loop_day.setNotLoop();
                loop_hour.setNotLoop();
                loop_minute.setNotLoop();

                switch (count) {
                    case 1:
                        loop_month.setVisibility(View.GONE);
                        loop_day.setVisibility(View.GONE);
                        loop_hour.setVisibility(View.GONE);
                        loop_minute.setVisibility(View.GONE);
                        break;
                    case 2:
                        loop_day.setVisibility(View.GONE);
                        loop_hour.setVisibility(View.GONE);
                        loop_minute.setVisibility(View.GONE);
                        break;
                    case 3:
                        loop_hour.setVisibility(View.GONE);
                        loop_minute.setVisibility(View.GONE);
                        break;
                    case 4:
                        loop_minute.setVisibility(View.GONE);
                        break;
                }

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

                        int year = loop_year.getSelectedItem() + minYearValue;
                        int month = loop_month.getSelectedItem() + 1;
                        int day = loop_day.getSelectedItem() + 1;
                        int hour = loop_hour.getSelectedItem();
                        int minute = loop_minute.getSelectedItem();

                        Calendar calendar = Calendar.getInstance();
                        int year_now = calendar.get(Calendar.YEAR);
                        int month_now = calendar.get(Calendar.MONTH);
                        int day_now = calendar.get(Calendar.DAY_OF_MONTH);

                        if (isLimited && year == year_now) {
                            if (month < month_now + 1) month = month_now + 1;
                            if (month == month_now + 1 && day < day_now) day = day_now;
                        }

                        String date_new;
                        switch (count) {
                            case 1:
                                date_new = year + "年";
                                break;
                            case 2:
                                date_new = year + "-" + month;
                                if (month < 10) date_new = year + "-0" + month;
                                break;
                            case 3:
                                date_new = year + "-" + month + "-" + day;
                                if (month < 10 && day < 10)
                                    date_new = year + "-0" + month + "-0" + day;
                                if (month < 10 && day >= 10)
                                    date_new = year + "-0" + month + "-" + day;
                                if (month >= 10 && day < 10)
                                    date_new = year + "-" + month + "-0" + day;
                                break;
                            case 4:
                                date_new = year + "-" + month + "-" + day + " " + hour + "时";
                                if (month < 10 && day < 10)
                                    date_new = year + "-0" + month + "-0" + day + " " + hour + "时";
                                if (month < 10 && day >= 10)
                                    date_new = year + "-0" + month + "-" + day + " " + hour + "时";
                                if (month >= 10 && day < 10)
                                    date_new = year + "-" + month + "-0" + day + " " + hour + "时";
                                break;
                            default:
                                date_new = year + "-" + month + "-" + day;
                                if (month < 10 && day < 10)
                                    date_new = year + "-0" + month + "-0" + day;
                                if (month < 10 && day >= 10)
                                    date_new = year + "-0" + month + "-" + day;
                                if (month >= 10 && day < 10)
                                    date_new = year + "-" + month + "-0" + day;

                                if (hour < 10 && minute < 10)
                                    date_new += " 0" + hour + ":0" + minute;
                                if (hour < 10 && minute >= 10)
                                    date_new += " 0" + hour + ":" + minute;
                                if (hour >= 10 && minute < 10)
                                    date_new += " " + hour + ":0" + minute;
                                if (hour >= 10 && minute >= 10)
                                    date_new += " " + hour + ":" + minute;
                                break;
                        }

                        callback.doWork(year, month, day, hour, minute, date_new);
                    }
                });

                return view;
            }

            @Override
            public void setUiBeforShow() {
                loop_year.setItems(dateToList(minYearValue, maxYearValue, "%d年"));
                loop_month.setItems(dateToList(1, 12, "%d月"));
                loop_day.setItems(dateToList(1, 31, "%d日"));
                loop_hour.setItems(dateToList(0, 23, "%d时"));
                loop_minute.setItems(dateToList(0, 59, "%d分"));

                if (isCurrentDate) {
                    loop_year.setInitPosition(Calendar.getInstance().get(Calendar.YEAR) - minYearValue);
                    loop_month.setInitPosition(Calendar.getInstance().get(Calendar.MONTH));
                    loop_day.setInitPosition(Calendar.getInstance().get(Calendar.DAY_OF_MONTH) - 1);
                }

                String[] months_big = {"1", "3", "5", "7", "8", "10", "12"};
                String[] months_little = {"4", "6", "9", "11"};
                final List<String> list_big = Arrays.asList(months_big);
                final List<String> list_little = Arrays.asList(months_little);

                loop_month.setListener(new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(int index) {
                        int month_num = loop_month.getSelectedItem() + 1;
                        // 判断大小月及是否闰年,用来确定"日"的数据
                        if (list_big.contains(String.valueOf(month_num))) {
                            loop_day.setItems(dateToList(1, 31, "%d日"));
                        } else if (list_little.contains(String.valueOf(month_num))) {
                            loop_day.setItems(dateToList(1, 30, "%d日"));
                            if (loop_day.getSelectedItem() == 30) loop_day.setCurrentPosition(29);
                        } else {
                            if (((loop_year.getSelectedItem() + minYearValue) % 4 == 0
                                    && (loop_year.getSelectedItem() + minYearValue) % 100 != 0)
                                    || (loop_year.getSelectedItem() + minYearValue) % 400 == 0) {
                                loop_day.setItems(dateToList(1, 29, "%d日"));
                                if (loop_day.getSelectedItem() >= 29)
                                    loop_day.setCurrentPosition(28);
                            } else {
                                loop_day.setItems(dateToList(1, 28, "%d日"));
                                if (loop_day.getSelectedItem() >= 28)
                                    loop_day.setCurrentPosition(27);
                            }
                        }
                    }
                });

                loop_year.setListener(new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(int index) {
                        int year_num = loop_year.getSelectedItem() + minYearValue;
                        // 判断大小月及是否闰年,用来确定"日"的数据
                        if (list_big.contains(String.valueOf(loop_month.getSelectedItem() + 1))) {
                            loop_day.setItems(dateToList(1, 31, "%d日"));
                        } else if (list_little.contains(String.valueOf(loop_month.getSelectedItem() + 1))) {
                            loop_day.setItems(dateToList(1, 30, "%d日"));
                        } else {
                            if ((year_num % 4 == 0 && year_num % 100 != 0)
                                    || year_num % 400 == 0)
                                loop_day.setItems(dateToList(1, 29, "%d日"));
                            else {
                                loop_day.setItems(dateToList(1, 28, "%d日"));
                                if (loop_day.getSelectedItem() == 28)
                                    loop_day.setCurrentPosition(27);
                            }
                        }
                    }
                });
            }

        };

        dialog.show();
    }

    private static List<String> dateToList(int minValue, int maxValue, String format) {
        List<String> items = new ArrayList<>();

        for (int i = 0; i < maxValue - minValue + 1; i++) {
            int value = minValue + i;
            items.add(format != null ? String.format(format, value) : Integer.toString(value));
        }

        return items;
    }

    public interface HintCallBack {
        void doWork();
    }

    public interface ItemCallBack {
        void doWork(int position, String name);
    }

    public interface DateAllCallBack {
        void doWork(int year, int month, int day, int hour, int minute, String date);
    }
}
