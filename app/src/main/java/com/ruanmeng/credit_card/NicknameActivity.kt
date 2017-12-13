package com.ruanmeng.credit_card

import android.os.Bundle
import com.ruanmeng.base.BaseActivity

class NicknameActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nickname)
        init_title("修改名称")
    }
}
