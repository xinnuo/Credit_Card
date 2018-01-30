package com.ruanmeng.credit_card

import android.content.Intent
import android.os.Bundle
import com.ruanmeng.base.BaseActivity
import kotlinx.android.synthetic.main.activity_online_card.*

class OnlineCardActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_online_card)
        init_title("在线办卡")
    }

    override fun init_title() {
        super.init_title()
        online_51.setOnClickListener {
            val intent = Intent(baseContext, WebActivity::class.java)
            intent.putExtra("title", "在线办卡")
            intent.putExtra("url", "http://partner.51credit.com/xzf/")
            startActivity(intent)
        }
        online_hui.setOnClickListener {
            val intent = Intent(baseContext, WebActivity::class.java)
            intent.putExtra("title", "在线办卡")
            intent.putExtra("url", "http://liuliang.sfmyw.cn/index.php?m=&c=Index&a=card&uid=MLTsgwwiMgDcO0O0O")
            startActivity(intent)
        }
    }
}
