package com.ruanmeng.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseFragment
import com.ruanmeng.base.getString
import com.ruanmeng.credit_card.R
import com.ruanmeng.model.CountMessageEvent
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.fragment_main_second.*
import kotlinx.android.synthetic.main.layout_title_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.json.JSONObject

class MainSecondFragment : BaseFragment() {

    //调用这个方法切换时不会释放掉Fragment
    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (this.view != null)
            this.view!!.visibility = if (menuVisible) View.VISIBLE else View.GONE
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main_second, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init_title()

        EventBus.getDefault().register(this@MainSecondFragment)

        getData()
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun init_title() {
        main_title.text = "发现"
        main_right.visibility = View.VISIBLE

        val count = getString("msgCount", "0").toInt()
        main_hint.visibility = if (count > 0) View.VISIBLE else View.INVISIBLE
        second_update.visibility = View.GONE

        second_web.apply {
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

    override fun getData() {
        OkGo.post<String>(BaseHttp.help_center)
                .tag(this@MainSecondFragment)
                .params("htmlKey", "hysj")
                .execute(object : StringDialogCallback(activity) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        val str = "<!doctype html><html>\n" +
                                "<meta charset=\"utf-8\">" +
                                "<style type=\"text/css\">" +
                                "body{ padding:0; margin:0; }\n" +
                                ".con{ width:95%; margin:0 auto; color:#fff; color:#666; padding:0.5em 0; overflow:hidden; display:block; font-size:0.92em; line-height:1.8em; }\n" +
                                ".con h1,h2,h3,h4,h5,h6{ font-size:1em; }\n " +
                                "img{ width:auto; max-width: 100% !important; height:auto !important; margin:0 auto; display:block; }\n" +
                                "*{ max-width:100% !important; }\n" +
                                "</style>\n" +
                                "<body style=\"padding:0; margin:0; \">" +
                                "<div class=\"con\">" +
                                JSONObject(response.body()).getString("help") +
                                "</div>" +
                                "</body>" +
                                "</html>"

                        second_web.loadDataWithBaseURL(BaseHttp.baseImg, str, "text/html", "utf-8", "")
                    }

                })
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this@MainSecondFragment)
        super.onDestroy()
    }

    @Subscribe
    fun onMessageEvent(event: CountMessageEvent) {
        when (event.name) {
            "未读消息" -> {
                val count = event.count.toInt()
                main_hint.visibility = if (count > 0) View.VISIBLE else View.INVISIBLE
            }
        }
    }
}
