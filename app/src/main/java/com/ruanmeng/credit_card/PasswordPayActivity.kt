package com.ruanmeng.credit_card

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.base.putString
import com.ruanmeng.base.toast
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.CommonUtil
import kotlinx.android.synthetic.main.activity_password_pay.*
import org.json.JSONObject

class PasswordPayActivity : BaseActivity() {

    private var time_count: Int = 180
    private lateinit var thread: Runnable
    private var YZM: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_pay)
        init_title("修改支付密码")
    }

    @SuppressLint("SetTextI18n")
    override fun init_title() {
        super.init_title()
        if (getString("isPayPwd") != "1") tvTitle.text = "设置支付密码"
        password_hint.text = "验证码将发送至${CommonUtil.phoneReplaceWithStar(getString("mobile"))}"

        bt_ok.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
        bt_ok.isClickable = false

        et_yzm.addTextChangedListener(this@PasswordPayActivity)
        et_pay.addTextChangedListener(this@PasswordPayActivity)
        et_confirm.addTextChangedListener(this@PasswordPayActivity)
    }

    @SuppressLint("SetTextI18n")
    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.bt_yzm -> {
                thread = Runnable {
                    bt_yzm.text = "${time_count}秒后重发"
                    if (time_count > 0) {
                        bt_yzm.postDelayed(thread, 1000)
                        time_count--
                    } else {
                        bt_yzm.text = "发送验证码"
                        bt_yzm.isClickable = true
                        bt_yzm.setBackgroundResource(R.drawable.rec_bg_ova_orange)
                        time_count = 180
                    }
                }

                OkGo.post<String>(BaseHttp.identify_othercode)
                        .tag(this@PasswordPayActivity)
                        .headers("token", getString("token"))
                        .params("mobile", getString("mobile"))
                        .params("type", 1)
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                YZM = JSONObject(response.body()).getString("object")
                                if (BuildConfig.LOG_DEBUG) et_yzm.setText(YZM)

                                bt_yzm.isClickable = false
                                bt_yzm.setBackgroundResource(R.drawable.rec_bg_ova_d0d0d0)
                                time_count = 180
                                bt_yzm.post(thread)
                            }

                        })
            }
            R.id.bt_ok -> {
                if (et_yzm.text.toString() != YZM) {
                    toast("验证码错误，请重新输入")
                    return
                }

                if (et_pay.text.length != 6 || et_confirm.text.length != 6) {
                    toast("密码长度应为6位")
                    return
                }

                if (et_pay.text.toString() != et_confirm.text.toString()) {
                    toast("密码输入不一致，请重新输入")
                    return
                }

                OkGo.post<String>(if (getString("isPayPwd") != "1") BaseHttp.paypawd_add_sub else BaseHttp.paypawd_change_mobile)
                        .tag(this@PasswordPayActivity)
                        .headers("token", getString("token"))
                        .params("mobile", getString("mobile"))
                        .params("password", et_pay.text.toString())
                        .params("smsCode", et_yzm.text.toString())
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                toast(msg)
                                putString("isPayPwd", "1")
                                ActivityStack.getScreenManager().popActivities(this@PasswordPayActivity::class.java)
                            }

                        })
            }
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (et_yzm.text.isNotBlank()
                && et_pay.text.isNotBlank()
                && et_confirm.text.isNotBlank()) {
            bt_ok.setBackgroundResource(R.drawable.rec_bg_blue)
            bt_ok.isClickable = true
        } else {
            bt_ok.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
            bt_ok.isClickable = false
        }
    }
}
