package com.ruanmeng.credit_card

import android.os.Bundle
import com.ruanmeng.base.BaseActivity
import kotlinx.android.synthetic.main.layout_title_left.*

class PlanPayActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plan_pay)
        setToolbarVisibility(false)
        init_title()
    }

    override fun init_title() {
        left_nav_title.text = "新增消费计划"
    }
}
