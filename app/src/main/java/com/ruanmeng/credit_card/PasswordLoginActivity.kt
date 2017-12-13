package com.ruanmeng.credit_card

import android.os.Bundle
import com.ruanmeng.base.BaseActivity

class PasswordLoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_login)
        init_title("修改登录密码")
    }
}
