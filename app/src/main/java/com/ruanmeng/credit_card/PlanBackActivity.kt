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

        plan_type.setOnClickListener {  }
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.plan_date_11 -> { }
            R.id.plan_plus -> { }
            R.id.plan_add -> { }
            R.id.plan_submit -> { }
            R.id.plan_preview -> { }
        }
    }
}
