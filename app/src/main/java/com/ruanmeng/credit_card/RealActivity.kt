package com.ruanmeng.credit_card

import android.os.Bundle
import com.ruanmeng.base.BaseActivity

class RealActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_real)
        init_title("实名信息")
    }
}
