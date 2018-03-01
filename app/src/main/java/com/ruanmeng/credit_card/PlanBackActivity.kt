package com.ruanmeng.credit_card

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v7.app.AlertDialog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.lzy.extend.StringDialogCallback
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.addItems
import com.ruanmeng.base.getString
import com.ruanmeng.base.toast
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.CommonModel
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.DialogHelper
import com.ruanmeng.utils.TimeHelper
import com.ruanmeng.view.FullyGridLayoutManager
import kotlinx.android.synthetic.main.activity_plan_back.*
import kotlinx.android.synthetic.main.layout_title_left.*
import net.idik.lib.slimadapter.SlimAdapter
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

class PlanBackActivity : BaseActivity() {

    private val list = ArrayList<Any>()
    private val list_title = ArrayList<Any>()
    private var list_item = ArrayList<Any>()
    private var list_days = ArrayList<String>()
    private var billDay = 0
    private var repaymentDay = 0
    private var mRate = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plan_back)
        setToolbarVisibility(false)
        init_title()

        getData()
    }

    override fun init_title() {
        left_nav_title.text = "新增还款计划"

        billDay = intent.getStringExtra("billDay").toInt()
        repaymentDay = intent.getStringExtra("repaymentDay").toInt()
        @Suppress("UNCHECKED_CAST")
        list_days = intent.getSerializableExtra("days") as ArrayList<String>

        plan_submit.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
        plan_submit.isClickable = false

        plan_total.addTextChangedListener(this@PlanBackActivity)
        plan_count.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {
                    if (s.toString().toInt() > 0 && plan_total.text.isNotEmpty())
                        calculatedValue(plan_total.text.toString().toDouble())
                }
            }
        })

        thread { list_item.addAll(getDateList()) }

        if (list_days.size < 24) {
            plan_check4.visibility = View.GONE
            plan_divider3.visibility = View.GONE
        }
        if (list_days.size < 18) {
            plan_check3.visibility = View.GONE
            plan_divider2.visibility = View.GONE
        }
        if (list_days.size < 12) {
            plan_check2.visibility = View.GONE
            plan_divider1.visibility = View.GONE
        }
        if (list_days.size < 6) plan_num_11.collapse()

        plan_group.setOnCheckedChangeListener { _, checkedId ->
            if (!plan_check1.isChecked
                    && !plan_check2.isChecked
                    && !plan_check3.isChecked
                    && !plan_check4.isChecked) return@setOnCheckedChangeListener

            val count = when (checkedId) {
                R.id.plan_check1 -> 6
                R.id.plan_check2 -> 12
                R.id.plan_check3 -> 18
                R.id.plan_check4 -> 24
                else -> -1
            }

            list_title.clear()
            list.clear()

            (0 until count).mapTo(list) { list_days[it] }
            (0 until count).mapTo(list_title) { TimeHelper.getInstance().getDayOfDate(list_days[it]) }
            plan_date.text = list_title.toString()
            if (list.isNotEmpty()) {
                plan_count.setText((list.size).toString())
                plan_count.setSelection(plan_count.text.length)
            }
        }
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
                        Arrays.asList(/*"快速还款", "余额还款", */"智能还款")) { _, name ->
                    plan_type.text = name
                }
            }
            R.id.plan_date_11 -> selectDate()
            R.id.plan_plus -> {
                val count = plan_count.text.toString().toInt()
                if (count > 1) {
                    plan_count.setText((count - 1).toString())
                    plan_count.setSelection(plan_count.text.length)
                    if (plan_total.text.isNotEmpty())
                        calculatedValue(plan_total.text.toString().toDouble())
                }
            }
            R.id.plan_add -> {
                val count = plan_count.text.toString().toInt()
                plan_count.setText((count + 1).toString())
                plan_count.setSelection(plan_count.text.length)
                if (plan_total.text.isNotEmpty())
                    calculatedValue(plan_total.text.toString().toDouble())
            }
            R.id.plan_submit -> {
                if (list.isEmpty()) {
                    toast("请选择还款日期")
                    return
                }

                if (plan_count.text.isEmpty()) {
                    toast("请输入还款笔数")
                    return
                }

                if (plan_count.text.toString().toInt() < list.size) {
                    toast("还款笔数不能小于还款天数")
                    return
                }

                if (plan_count.text.toString().toDouble() / list.size > 2) {
                    toast("每天最多还款不超过两笔")
                    return
                }

                AlertDialog.Builder(baseContext)
                        .setTitle("温馨提示")
                        .setMessage("信用卡余额应不低于${plan_save.text}元(保证金)，确定要提交吗？")
                        .setPositiveButton("确定") { _, _ ->
                            val repaymentDay = list.toString()
                                    .replace("[", "")
                                    .replace("]", "")
                                    .replace(" ", "")

                            OkGo.post<CommonModel>(BaseHttp.add_repaymentplan)
                                    .tag(this@PlanBackActivity)
                                    .isMultipart(true)
                                    .headers("token", getString("token"))
                                    .params("creditcardId", intent.getStringExtra("creditcardId"))
                                    .params("repaymentSum", plan_total.text.toString())
                                    .params("repaymentDay", repaymentDay)
                                    .params("repaymentNum", plan_count.text.toString())
                                    .params("cardType", "1")
                                    .execute(object : JacksonDialogCallback<CommonModel>(baseContext, CommonModel::class.java) {
                                        override fun onSuccess(response: Response<CommonModel>) {

                                            toast(response.body().msg)
                                            if (response.body().msgcode == "100") {
                                                val list = ArrayList<CommonData>()
                                                list.addItems(response.body().`object`)
                                                val intent = Intent(baseContext, PlanPreviewActivity::class.java)
                                                intent.putExtra("data", list)
                                                intent.putExtra("maxSum", response.body().maxSum)
                                                intent.putExtra("sumRateSum", response.body().sumRateSum)
                                                startActivity(intent)

                                                ActivityStack.getScreenManager().popActivities(this@PlanBackActivity::class.java)
                                            }
                                        }

                                    })
                        }
                        .setNegativeButton("取消") { _, _ -> }
                        .create()
                        .show()
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
                .execute(object : StringDialogCallback(baseContext) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        mRate = JSONObject(response.body())
                                .getJSONObject("pingTai")
                                .getString("consumeRate")
                                .toDouble()
                    }

                })
    }

    @Suppress("DEPRECATION")
    @SuppressLint("InflateParams")
    private fun selectDate() {
        val view = LayoutInflater.from(baseContext).inflate(R.layout.dialog_select_date, null) as View
        val tv_cancel = view.findViewById(R.id.tv_dialog_select_cancle) as TextView
        val tv_ok = view.findViewById(R.id.tv_dialog_select_ok) as TextView
        val recycle_list = view.findViewById(R.id.dialog_select_list) as RecyclerView
        val dialog = BottomSheetDialog(baseContext)

        list_item.filter { it is CommonData /*&& it.isChecked*/ }.forEach {
            it as CommonData
            it.isChecked = list.contains(it.content)
        }

        recycle_list.apply {
            layoutManager = GridLayoutManager(baseContext, 7)

            (layoutManager as GridLayoutManager).spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int = when {
                    (adapter as SlimAdapter).getItem(position) is String -> 7
                    else -> 1
                }
            }

            adapter = SlimAdapter.create()
                    .register<String>(R.layout.item_date_list) { data, injector ->
                        injector.text(R.id.item_date_title, data)
                    }
                    .register<CommonData>(R.layout.item_tab_list) { data, injector ->
                        injector.text(R.id.item_tab, data.title)
                                .with<TextView>(R.id.item_tab) { view ->
                                    view.setTextColor(resources.getColor(if (data.isChecked) R.color.colorAccent else R.color.black_dark))
                                    view.setBackgroundColor(resources.getColor(if (data.isChecked) R.color.lighter else R.color.white))
                                }
                                .visibility(R.id.item_pay,
                                        if (data.title.toInt() == repaymentDay) View.VISIBLE else View.INVISIBLE)
                                .visibility(R.id.item_bill,
                                        if (data.title.toInt() == billDay) View.VISIBLE else View.INVISIBLE)
                                .visibility(R.id.item_tab_left,
                                        if (data.position % 7 == 0) View.VISIBLE else View.INVISIBLE)
                                .visibility(R.id.item_tab_top,
                                        if (data.position > 6) View.INVISIBLE else View.VISIBLE)
                                .clicked(R.id.item_tab) {
                                    data.isChecked = !data.isChecked
                                    (adapter as SlimAdapter).notifyItemChanged(list_item.indexOf(data), 0)
                                }
                    }
                    .attachTo(this)
        }
        (recycle_list.adapter as SlimAdapter).updateData(list_item)

        tv_cancel.setOnClickListener { dialog.dismiss() }
        tv_ok.setOnClickListener {
            dialog.dismiss()

            list_title.clear()
            list.clear()
            plan_group.check(-1)
            list_item.filter { it is CommonData && it.isChecked }.forEach {
                it as CommonData
                list_title.add(it.title)
                list.add(it.content)
            }
            plan_date.text = list_title.toString()
            if (list.isNotEmpty()) {
                plan_count.setText((list.size).toString())
                plan_count.setSelection(plan_count.text.length)
            }
        }

        dialog.setContentView(view)
        dialog.show()
    }

    @Suppress("DEPRECATION")
    @SuppressLint("InflateParams", "SetTextI18n")
    private fun selectDateOne() {
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
        val date_end = TimeHelper.getInstance().dayOfMonthEnd                       //本月最后一天
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
                                .visibility(R.id.item_pay,
                                        if (data.title.toInt() == repaymentDay) View.VISIBLE else View.INVISIBLE)
                                .visibility(R.id.item_bill,
                                        if (data.title.toInt() == billDay) View.VISIBLE else View.INVISIBLE)
                                .visibility(R.id.item_tab_left,
                                        if (item_first.indexOf(data) % 7 == 0) View.VISIBLE else View.INVISIBLE)
                                .visibility(R.id.item_tab_top,
                                        if (item_first.indexOf(data) > 6) View.INVISIBLE else View.VISIBLE)
                                .clicked(R.id.item_tab) {
                                    data.isChecked = !data.isChecked
                                    (adapter as SlimAdapter).notifyDataSetChanged()
                                }
                    }
                    .attachTo(this)
        }
        (list_first.adapter as SlimAdapter).updateData(item_first)

        list_second.apply {
            layoutManager = FullyGridLayoutManager(baseContext, 7)
            adapter = SlimAdapter.create()
                    .register<CommonData>(R.layout.item_tab_list) { data, injector ->
                        injector.text(R.id.item_tab, data.title)
                                .with<TextView>(R.id.item_tab) { view ->
                                    view.setTextColor(resources.getColor(if (data.isChecked) R.color.colorAccent else R.color.black_dark))
                                    view.setBackgroundColor(resources.getColor(if (data.isChecked) R.color.lighter else R.color.white))
                                }
                                .visibility(R.id.item_pay,
                                        if (data.title.toInt() == repaymentDay) View.VISIBLE else View.INVISIBLE)
                                .visibility(R.id.item_bill,
                                        if (data.title.toInt() == billDay) View.VISIBLE else View.INVISIBLE)
                                .visibility(R.id.item_tab_left,
                                        if (item_second.indexOf(data) % 7 == 0) View.VISIBLE else View.INVISIBLE)
                                .visibility(R.id.item_tab_top,
                                        if (item_second.indexOf(data) > 6) View.INVISIBLE else View.VISIBLE)
                                .clicked(R.id.item_tab) {
                                    data.isChecked = !data.isChecked
                                    (adapter as SlimAdapter).notifyDataSetChanged()
                                }
                    }
                    .attachTo(this)
        }
        (list_second.adapter as SlimAdapter).updateData(item_second)

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
            if (list.isNotEmpty()) {
                plan_count.setText((list.size).toString())
                plan_count.setSelection(plan_count.text.length)
            }
        }

        dialog.setContentView(view)
        dialog.show()
    }

    private fun getDateList(): ArrayList<Any> {
        val date_now = TimeHelper.getInstance().stringDateShort                     //今天
        val date_tom = TimeHelper.getInstance().getNextDay(date_now, 1)        //明天

        val first_start = TimeHelper.getInstance().getAnyMonthFirst(1)     //下月第一天
        val second_start = TimeHelper.getInstance().getAnyMonthFirst(2)    //下下月第一天
        val third_start = TimeHelper.getInstance().getAnyMonthFirst(3)     //下下下月第一天

        val now_end = TimeHelper.getInstance().dayOfMonthEnd                        //本月最后一天
        val first_end = TimeHelper.getInstance().getAnyMonthEnd(1)         //下月最后一天
        val second_end = TimeHelper.getInstance().getAnyMonthEnd(2)        //下下月最后一天
        val third_end = TimeHelper.getInstance().getAfterMonth(date_now, 3) //下下下月的今天

        val days_now = TimeHelper.getInstance().getDays(date_now, now_end)
        val days_first = TimeHelper.getInstance().getDays(now_end, first_end)
        val days_second = TimeHelper.getInstance().getDays(first_end, second_end)
        val days_third = TimeHelper.getInstance().getDays(second_end, third_end)
        val start_now = TimeHelper.getInstance().getDayOfDate(date_tom)
        val dates = ArrayList<Any>()

        if (days_now > 0) {
            dates.add(date_now.substring(0, 7).replace("-", "年") + "月")

            (0 until days_now).mapTo(dates) {
                CommonData().apply {
                    title = (start_now + it).toString()
                    content = TimeHelper.getInstance().getNextDay(date_tom, it.toInt())
                    position = it.toInt()
                    isChecked = list.contains(content)
                }
            }
        }

        if (days_first > 1) {
            dates.add(first_end.substring(0, 7).replace("-", "年") + "月")

            (0 until days_first).mapTo(dates) {
                CommonData().apply {
                    title = (it + 1).toString()
                    content = TimeHelper.getInstance().getNextDay(first_start, it.toInt())
                    position = it.toInt()
                    isChecked = list.contains(content)
                }
            }
        }

        if (days_second > 1) {
            dates.add(second_end.substring(0, 7).replace("-", "年") + "月")

            (0 until days_second).mapTo(dates) {
                CommonData().apply {
                    title = (it + 1).toString()
                    content = TimeHelper.getInstance().getNextDay(second_start, it.toInt())
                    position = it.toInt()
                    isChecked = list.contains(content)
                }
            }
        }

        if (days_third > 1) {
            dates.add(third_end.substring(0, 7).replace("-", "年") + "月")

            (0 until days_third).mapTo(dates) {
                CommonData().apply {
                    title = (it + 1).toString()
                    content = TimeHelper.getInstance().getNextDay(third_start, it.toInt())
                    position = it.toInt()
                    isChecked = list.contains(content)
                }
            }
        }

        dates.add("")
        return dates
    }

    @SuppressLint("SetTextI18n")
    private fun calculatedValue(value: Double) {
        val value_count = plan_count.text.toString().toInt()

        if (value_count < 1) return

        val value_max = when {
            value < 1000 -> value / value_count + value * 0.1
            else -> value / value_count + 100
        }
        val value_fee = (value + value_count * 2) * mRate + value_count
        val value_bond = value_max + value_fee

        plan_save.text = "￥${String.format("%.2f", value_bond)}"
        plan_fee.text = "￥${String.format("%.2f", value_fee)}"
    }

    @SuppressLint("SetTextI18n")
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
                val input = s.toString().toDouble()
                calculatedValue(input)
            }
        } else {
            plan_save.text = "￥0.00"
            plan_fee.text = "￥0.00"
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
