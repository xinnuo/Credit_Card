package com.ruanmeng.credit_card

import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.utils.DialogHelper
import kotlinx.android.synthetic.main.activity_plan_back.*
import kotlinx.android.synthetic.main.layout_title_left.*
import java.util.*

class PlanBackActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plan_back)
        setToolbarVisibility(false)
        init_title()
    }

    override fun init_title() {
        left_nav_title.text = "新增还款计划"

        plan_submit.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
        plan_submit.isClickable = false

        plan_type.addTextChangedListener(this@PlanBackActivity)
        plan_total.addTextChangedListener(this@PlanBackActivity)
        plan_date.addTextChangedListener(this@PlanBackActivity)
    }

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
                val day_first = view.findViewById(R.id.tab_dialog_select_day) as RecyclerView
                val dialog = BottomSheetDialog(baseContext)

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
            R.id.plan_submit -> { }
            R.id.plan_preview -> { }
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (plan_type.text.isNotBlank()
                && plan_total.text.isNotBlank()
                && plan_date.text.isNotBlank()) {
            plan_submit.setBackgroundResource(R.drawable.rec_bg_blue)
            plan_submit.isClickable = true
        } else {
            plan_submit.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
            plan_submit.isClickable = false
        }
    }
}
