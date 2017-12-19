package com.ruanmeng.credit_card

import android.os.Bundle
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
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

    override fun onStart() {
        super.onStart()
        password_pay.setLeftString(if (getString("isPayPwd") != "1") "设置支付密码" else "修改支付密码")
    }

    override fun init_title() {
        super.init_title()
        left_nav_title.text = "密码设置"

        password_login.setOnClickListener { startActivity(PasswordLoginActivity::class.java) }
        password_pay.setOnClickListener { startActivity(PasswordPayActivity::class.java) }
    }
}
