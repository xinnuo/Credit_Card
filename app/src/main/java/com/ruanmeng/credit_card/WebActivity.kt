package com.ruanmeng.credit_card

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.*
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.activity_web.*
import kotlinx.android.synthetic.main.layout_title_left.*
import org.json.JSONObject

class WebActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)
        setToolbarVisibility(false)
        init_title()

        when (intent.getStringExtra("title")) {
            "信用卡攻略" -> { }
            "用户协议" -> { }
            "关于我们" -> { }
            "服务协议" -> { }
            "详情" -> { }
            "消息详情" -> {
                OkGo.post<String>(BaseHttp.msgInfo_details)
                        .tag(this@WebActivity)
                        .params("msgReciveId", intent.getStringExtra("msgReciveId"))
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                val str = "<!doctype html><html>\n" +
                                        "<meta charset=\"utf-8\">" +
                                        "<style type=\"text/css\">" +
                                        "body{ padding:0; margin:0; }\n" +
                                        ".view_h1{ width:95%; margin:0 auto; display:block; overflow:hidden;  font-size:1.1em; color:#333; padding:0.5em 0; line-height:1.0em; }\n" +
                                        ".view_time{ width:95%; margin:0 auto; display:block; overflow:hidden; font-size:0.8em; color:#999; }\n" +
                                        ".con{ width:95%; margin:0 auto; color:#fff; color:#666; padding:0.5em 0; overflow:hidden; display:block; font-size:0.92em; line-height:1.8em; }\n" +
                                        ".con h1,h2,h3,h4,h5,h6{ font-size:1em;}\n " +
                                        "img{ width:auto; max-width: 100% !important; height:auto !important; margin:0 auto; display:block; }\n" +
                                        "*{ max-width:100% !important; }\n" +
                                        "</style>\n" +
                                        "<body style=\"padding:0; margin:0; \">" +
                                        "<div class=\"view_h1\">" +
                                        JSONObject(response.body()).getString("title") +
                                        "</div>" +
                                        "<div class=\"view_time\" style=\"border-bottom:1px solid #ededed; padding-bottom:5px;\">" +
                                        JSONObject(response.body()).getString("sendDate") +
                                        "</div>" +
                                        "<div class=\"con\">" +
                                        JSONObject(response.body()).getString("content") +
                                        "</div>" +
                                        "</body>" +
                                        "</html>"

                                wv_web.loadDataWithBaseURL(BaseHttp.baseImg, str, "text/html", "utf-8", "")
                            }

                        })
            }
            "收款支付" -> wv_web.loadUrl(intent.getStringExtra("url"))
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

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                    view.loadUrl(url)
                    return true
                }
            }
        }
    }
}
