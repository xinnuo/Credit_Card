package com.ruanmeng.credit_card

import android.os.Bundle
import android.view.View
import com.ruanmeng.base.BaseActivity
import kotlinx.android.synthetic.main.activity_plan_back.*
import kotlinx.android.synthetic.main.layout_title_left.*

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
            R.id.plan_type_11 -> { }
            R.id.plan_date_11 -> { }
            R.id.plan_plus -> { }
            R.id.plan_add -> { }
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
