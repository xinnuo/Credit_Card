package com.ruanmeng.credit_card

import android.os.Bundle
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.startActivity
import kotlinx.android.synthetic.main.activity_setting_password.*
import kotlinx.android.synthetic.main.layout_title_left.*

class SettingPasswordActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_password)
        setToolbarVisibility(false)
        init_title()
    }

    override fun init_title() {
        super.init_title()
        left_nav_title.text = "密码设置"

        password_login.setOnClickListener { startActivity(PasswordLoginActivity::class.java) }
        password_pay.setOnClickListener { startActivity(PasswordPayActivity::class.java) }
    }
}
