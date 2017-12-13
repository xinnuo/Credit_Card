package com.ruanmeng.credit_card

import android.content.Intent
import android.os.Bundle
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.startActivity
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        init_title("设置")
    }

    override fun init_title() {
        super.init_title()

        setting_password.setOnClickListener { startActivity(SettingPasswordActivity::class.java) }

        setting_deal.setOnClickListener {
            val intent = Intent(baseContext, WebActivity::class.java)
            intent.putExtra("title", "用户协议")
            startActivity(intent)
        }

        setting_about.setOnClickListener {
            val intent = Intent(baseContext, WebActivity::class.java)
            intent.putExtra("title", "关于我们")
            startActivity(intent)
        }
    }
}
