package com.ruanmeng.credit_card

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.base.startActivity
import com.ruanmeng.utils.Tools
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        init_title("设置")
    }

    override fun init_title() {
        super.init_title()
        setting_hotline.setRightString(getString("platform"))
        setting_version.setRightString("v" + Tools.getVersion(baseContext))

        setting_password.setOnClickListener { startActivity(SettingPasswordActivity::class.java) }

        setting_hotline.setOnClickListener {
            if (getString("platform").isNotEmpty()) {
                AlertDialog.Builder(baseContext)
                        .setTitle("客服热线")
                        .setMessage("拨打客服热线电话 " + getString("platform"))
                        .setPositiveButton("呼叫") { dialog, _ ->
                            dialog.dismiss()

                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + getString("platform")))
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }
                        .setNegativeButton("取消") { dialog, _ -> dialog.dismiss() }
                        .create()
                        .show()
            }
        }

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

        setting_quit.setOnClickListener {
            AlertDialog.Builder(baseContext)
                    .setTitle("退出登录")
                    .setMessage("确定要退出当前账号吗？")
                    .setPositiveButton("退出") { dialog, _ ->
                        dialog.dismiss()

                        val intent = Intent(baseContext, LoginActivity::class.java)
                        intent.putExtra("offLine", true)
                        startActivity(intent)
                    }
                    .setNegativeButton("取消") { dialog, _ -> dialog.dismiss() }
                    .create()
                    .show()
        }
    }
}
