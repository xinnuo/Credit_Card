package com.ruanmeng.credit_card

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.base.toast
import com.ruanmeng.model.CommonData
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.DialogHelper
import com.ruanmeng.utils.TimeHelper
import com.ruanmeng.view.FullyGridLayoutManager
import kotlinx.android.synthetic.main.activity_plan_back.*
import kotlinx.android.synthetic.main.layout_title_left.*
import net.idik.lib.slimadapter.SlimAdapter
import org.json.JSONObject
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

class PlanBackActivity : BaseActivity() {

    private val list = ArrayList<Any>()
    private val list_title = ArrayList<Any>()
    private var mRate = 1.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plan_back)
        setToolbarVisibility(false)
        init_title()

        getData()
    }

    override fun init_title() {
        left_nav_title.text = "新增还款计划"

        plan_submit.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
        plan_submit.isClickable = false

        plan_total.addTextChangedListener(this@PlanBackActivity)
    }

    @Suppress("DEPRECATION")
    @SuppressLint("SetTextI18n", "InflateParams")
    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.plan_type_11 -> {
                DialogHelper.showItemDialog(
                        baseContext,
                        "选择还款类型",
                        Arrays.asList("快速还款", "余额还款", "还款消费")) { position, name ->
                    plan_type.text = name
                }
            }
            R.id.plan_date_11 -> {
                val view = LayoutInflater.from(baseContext).inflate(R.layout.dialog_select_day, null) as View
                val tv_cancel = view.findViewById(R.id.tv_dialog_select_cancle) as TextView
                val tv_ok = view.findViewById(R.id.tv_dialog_select_ok) as TextView
                val month_first = view.findViewById(R.id.tv_dialog_month1) as TextView
                val month_second = view.findViewById(R.id.tv_dialog_month2) as TextView
                val list_first = view.findViewById(R.id.tab_dialog_select_day1) as RecyclerView
                val list_second = view.findViewById(R.id.tab_dialog_select_day2) as RecyclerView
                val dialog = BottomSheetDialog(baseContext)

                val date_now = TimeHelper.getInstance().stringDateShort                     //今天
                val date_tom = TimeHelper.getInstance().getNextDay(date_now, 1)        //明天
                val date_end = TimeHelper.getInstance().defaultDay                          //本月最后一天
                val date_month = TimeHelper.getInstance().nextMonthFirst                    //下月第一天
                val date_next = TimeHelper.getInstance().getAfterMonth(date_now, 1) //下月的今天

                month_first.text = date_now.substring(0, 7).replace("-", "年") + "月"
                month_second.text = date_next.substring(0, 7).replace("-", "年") + "月"

                val item_first = ArrayList<CommonData>()
                val item_second = ArrayList<CommonData>()
                val days_first = TimeHelper.getInstance().getDays(date_now, date_end)
                val days_second = TimeHelper.getInstance().getDays(date_end, date_next)
                val start_fist = TimeHelper.getInstance().getDayOfDate(date_tom)
                val start_second = TimeHelper.getInstance().getDayOfDate(date_month)

                if (days_first > 0) {
                    (0 until days_first).mapTo(item_first) {
                        CommonData().apply {
                            title = (start_fist + it).toString()
                            content = TimeHelper.getInstance().getNextDay(date_tom, it.toInt())
                            isChecked = list_title.contains((start_fist + it).toString())
                        }
                    }
                } else {
                    month_first.visibility = View.GONE
                    list_first.visibility = View.GONE
                }

                if (days_second > 1) {
                    (0 until days_second).mapTo(item_second) {
                        CommonData().apply {
                            title = (start_second + it).toString()
                            content = TimeHelper.getInstance().getNextDay(date_month, it.toInt())
                            isChecked = list_title.contains((start_second + it).toString())
                        }
                    }
                } else {
                    month_second.visibility = View.GONE
                    list_second.visibility = View.GONE
                }

                list_first.apply {
                    layoutManager = FullyGridLayoutManager(baseContext, 7)
                    adapter = SlimAdapter.create()
                            .register<CommonData>(R.layout.item_tab_list) { data, injector ->
                                injector.text(R.id.item_tab, data.title)
                                        .with<TextView>(R.id.item_tab) { view ->
                                            view.setTextColor(resources.getColor(if (data.isChecked) R.color.colorAccent else R.color.black_dark))
                                            view.setBackgroundColor(resources.getColor(if (data.isChecked) R.color.lighter else R.color.white))
                                        }
                                        .visibility(R.id.item_tab_left,
                                                if (item_first.indexOf(data) % 7 == 0) View.VISIBLE else View.INVISIBLE)
                                        .visibility(R.id.item_tab_top,
                                                if (item_first.indexOf(data) > 6) View.INVISIBLE else View.VISIBLE)
                                        .clicked(R.id.item_tab) {
                                            data.isChecked = !data.isChecked
                                            (adapter as SlimAdapter).updateData(item_first).notifyDataSetChanged()
                                        }
                            }
                            .attachTo(this)
                }
                (list_first.adapter as SlimAdapter).updateData(item_first).notifyDataSetChanged()

                list_second.apply {
                    layoutManager = FullyGridLayoutManager(baseContext, 7)
                    adapter = SlimAdapter.create()
                            .register<CommonData>(R.layout.item_tab_list) { data, injector ->
                                injector.text(R.id.item_tab, data.title)
                                        .with<TextView>(R.id.item_tab) { view ->
                                            view.setTextColor(resources.getColor(if (data.isChecked) R.color.colorAccent else R.color.black_dark))
                                            view.setBackgroundColor(resources.getColor(if (data.isChecked) R.color.lighter else R.color.white))
                                        }
                                        .visibility(R.id.item_tab_left,
                                                if (item_second.indexOf(data) % 7 == 0) View.VISIBLE else View.INVISIBLE)
                                        .visibility(R.id.item_tab_top,
                                                if (item_second.indexOf(data) > 6) View.INVISIBLE else View.VISIBLE)
                                        .clicked(R.id.item_tab) {
                                            data.isChecked = !data.isChecked
                                            (adapter as SlimAdapter).updateData(item_second).notifyDataSetChanged()
                                        }
                            }
                            .attachTo(this)
                }
                (list_second.adapter as SlimAdapter).updateData(item_second).notifyDataSetChanged()

                tv_cancel.setOnClickListener { dialog.dismiss() }
                tv_ok.setOnClickListener {
                    dialog.dismiss()

                    list_title.clear()
                    list.clear()
                    item_first.filter { it.isChecked }.forEach {
                        list_title.add(it.title)
                        list.add(it.content)
                    }
                    item_second.filter { it.isChecked }.forEach {
                        list_title.add(it.title)
                        list.add(it.content)
                    }
                    plan_date.text = list_title.toString()
                }

                dialog.setContentView(view)
                dialog.show()
            }
            R.id.plan_plus -> {
                val count = plan_count.text.toString().toInt()
                if (count > 1) plan_count.text = (count - 1).toString()
            }
            R.id.plan_add -> {
                val count = plan_count.text.toString().toInt()
                plan_count.text = (count + 1).toString()
            }
            R.id.plan_submit -> {
                if (plan_type.text.isEmpty()) {
                    toast("请选择还款类型")
                    return
                }

                if (list.isEmpty()) {
                    toast("请选择还款日期")
                    return
                }

                val repaymentDay = list.toString()
                        .replace("[", "")
                        .replace("]", "")
                        .replace(" ", "")

                OkGo.post<String>(BaseHttp.add_repaymentplan)
                        .tag(this@PlanBackActivity)
                        .headers("token", getString("token"))
                        .params("creditcardId", intent.getStringExtra("creditcardId"))
                        .params("repaymentType", plan_type.text.toString())
                        .params("repaymentSum", plan_total.text.toString())
                        .params("repaymentDay", repaymentDay)
                        .params("repaymentNum", plan_count.text.toString())
                        .params("cardType", "1")
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                toast(msg)
                                ActivityStack.getScreenManager().popActivities(this@PlanBackActivity::class.java)
                            }

                        })
            }
            R.id.plan_preview -> {
                if (plan_type.text.isEmpty()) {
                    toast("请选择还款类型")
                    return
                }

                if (list.isEmpty()) {
                    toast("请选择还款日期")
                    return
                }

                val repaymentDay = list.toString()
                        .replace("[", "")
                        .replace("]", "")
                        .replace(" ", "")

                intent.setClass(baseContext, PlanPreviewActivity::class.java)
                intent.putExtra("repaymentType", plan_type.text.toString())
                intent.putExtra("repaymentSum", plan_total.text.toString())
                intent.putExtra("repaymentDay", repaymentDay)
                intent.putExtra("repaymentNum", plan_count.text.toString())
                startActivity(intent)
            }
        }
    }

    override fun getData() {
        OkGo.post<String>(BaseHttp.base_rate_data)
                .tag(this@PlanBackActivity)
                .execute(object : StringDialogCallback(baseContext, false) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        mRate = JSONObject(response.body())
                                .getJSONObject("pingTai")
                                .getString("consumeRate")
                                .toDouble()
                    }

                })
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (plan_total.text.isNotBlank()) {
            plan_submit.setBackgroundResource(R.drawable.rec_bg_blue)
            plan_submit.isClickable = true
        } else {
            plan_submit.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
            plan_submit.isClickable = false
        }

        if (s.isNotEmpty()) {
            if ("." == s.toString()) {
                plan_total.setText("0.")
                plan_total.setSelection(plan_total.text.length) //设置光标的位置
            } else {
                plan_fee.setRightString(DecimalFormat("########0.0#####").format(s.toString().toDouble() * mRate))
            }
        }
    }

    override fun afterTextChanged(s: Editable) {
        val temp = s.toString()
        val posDot = temp.indexOf(".")
        if (posDot < 0) {
            if (temp.length > 7) s.delete(7, 8)
        } else {
            if (temp.length - posDot - 1 > 2) s.delete(posDot + 3, posDot + 4)
        }
    }
}
