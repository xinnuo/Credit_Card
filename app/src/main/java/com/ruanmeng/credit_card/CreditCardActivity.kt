package com.ruanmeng.credit_card

import android.os.Bundle
import com.ruanmeng.base.BaseActivity

class CreditCardActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credit_card)
        init_title("新增信用卡")
    }
}
