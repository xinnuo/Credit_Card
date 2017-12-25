package com.ruanmeng.credit_card

import android.os.Bundle
import android.view.View
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.base.startActivity
import com.ruanmeng.base.toast
import com.ruanmeng.model.RefreshMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.CommonUtil
import kotlinx.android.synthetic.main.activity_recommend.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class RecommendActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommend)
        init_title("绑定推荐人")

        EventBus.getDefault().register(this@RecommendActivity)
    }

    override fun init_title() {
        super.init_title()
        bt_ok.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
        bt_ok.isClickable = false

        et_code.addTextChangedListener(this@RecommendActivity)
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.rec_qrcode -> startActivity(ScanActivity::class.java)
            R.id.bt_ok -> {
                if (!CommonUtil.isMobileNumber(et_code.text.toString())) {
                    et_code.requestFocus()
                    et_code.setText("")
                    toast("手机号码格式错误，请重新输入")
                    return
                }

                OkGo.post<String>(BaseHttp.add_recommenduser)
                        .tag(this@RecommendActivity)
                        .headers("token", getString("token"))
                        .params("mobile", et_code.text.toString())
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                toast(msg)
                                EventBus.getDefault().post(RefreshMessageEvent("绑定推荐人", et_code.text.toString()))
                                ActivityStack.getScreenManager().popActivities(this@RecommendActivity::class.java)
                            }

                        })
            }
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (et_code.text.isNotBlank()) {
            bt_ok.setBackgroundResource(R.drawable.rec_bg_blue)
            bt_ok.isClickable = true
        } else {
            bt_ok.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
            bt_ok.isClickable = false
        }
    }

    override fun finish() {
        super.finish()
        EventBus.getDefault().unregister(this@RecommendActivity)
    }

    @Subscribe
    fun onMessageEvent(event: RefreshMessageEvent) {
        when (event.name) {
            "扫描二维码" -> {
                val code = event.id
                if (CommonUtil.isMobileNumber(code.substring(0, 11))) {
                    et_code.setText(code)
                    et_code.setSelection(et_code.text.length)
                }
            }
        }
    }
}
