package com.ruanmeng.credit_card

import android.os.Bundle
import android.view.View
import com.ruanmeng.base.BaseActivity
import kotlinx.android.synthetic.main.layout_title_left.*

class SavingsDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_savings_detail)
        setToolbarVisibility(false)
        init_title()
    }

    override fun init_title() {
        left_nav_title.text = "储蓄卡"
        left_nav_right.visibility = View.VISIBLE
    }
}
