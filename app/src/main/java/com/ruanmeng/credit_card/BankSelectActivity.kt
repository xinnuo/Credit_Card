package com.ruanmeng.credit_card

import android.os.Bundle
import com.ruanmeng.base.BaseActivity

class BankSelectActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank_select)
        init_title("选择银行")
    }
}
