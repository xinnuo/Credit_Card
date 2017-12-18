package com.ruanmeng.credit_card

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebSettings
import com.ruanmeng.base.BaseActivity
import kotlinx.android.synthetic.main.activity_web.*
import kotlinx.android.synthetic.main.layout_title_left.*

class WebActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)
        setToolbarVisibility(false)
        init_title()

        when (intent.getStringExtra("title")) {
            "信用卡攻略" -> {}
            "用户协议" -> {}
            "关于我们" -> {}
            "服务协议" -> {}
            "详情" -> {}
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun init_title() {
        left_nav_title.text = intent.getStringExtra("title")

        wv_web.apply {
            //支持javascript
            settings.javaScriptEnabled = true
            // 设置可以支持缩放
            settings.setSupportZoom(true)
            // 自适应屏幕
            settings.loadWithOverviewMode = true
            settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
            isHorizontalScrollBarEnabled = false

            // 设置出现缩放工具
            settings.builtInZoomControls = true
            settings.displayZoomControls = false
        }
    }
}
