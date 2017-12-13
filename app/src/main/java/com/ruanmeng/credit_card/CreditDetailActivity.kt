package com.ruanmeng.credit_card

import android.os.Bundle
import android.view.View
import com.ruanmeng.base.BaseActivity
import kotlinx.android.synthetic.main.layout_title_left.*

class CreditDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credit_detail)
        setToolbarVisibility(false)
        init_title()
    }

    override fun init_title() {
        left_nav_title.text = "信用卡"
        left_nav_right.visibility = View.VISIBLE
    }
}
