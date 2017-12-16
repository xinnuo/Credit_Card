package com.ruanmeng.credit_card

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.startActivity
import com.ruanmeng.base.toast
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.CommonUtil
import kotlinx.android.synthetic.main.activity_register.*
import org.json.JSONObject

class RegisterActivity : BaseActivity() {

    private var time_count: Int = 90
    private lateinit var thread: Runnable
    private var YZM: String = ""
    private var mTel: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        init_title("注册")
    }

    override fun init_title() {
        super.init_title()
        bt_sign.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
        bt_sign.isClickable = false

        et_name.addTextChangedListener(this@RegisterActivity)
        et_yzm.addTextChangedListener(this@RegisterActivity)
        et_pwd.addTextChangedListener(this@RegisterActivity)
        register_agree.setOnCheckedChangeListener(this@RegisterActivity)
    }

    @SuppressLint("SetTextI18n")
    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.register_xieyi -> {
                val intent = Intent(baseContext, WebActivity::class.java)
                intent.putExtra("title", "服务协议")
                startActivity(intent)
            }
            R.id.register_qrcode -> startActivity(ScanActivity::class.java)
            R.id.bt_yzm -> {
                if (et_name.text.isBlank()) {
                    et_name.requestFocus()
                    toast("请输入手机号")
                    return
                }

                if (!CommonUtil.isMobileNumber(et_name.text.toString())) {
                    et_name.requestFocus()
                    et_name.setText("")
                    toast("手机号码格式错误，请重新输入")
                    return
                }

                thread = Runnable {
                    bt_yzm.text = "${time_count}秒后重发"
                    if (time_count > 0) {
                        bt_yzm.postDelayed(thread, 1000)
                        time_count--
                    } else {
                        bt_yzm.text = "发送验证码"
                        bt_yzm.isClickable = true
                        bt_yzm.setBackgroundResource(R.drawable.rec_bg_ova_orange)
                        time_count = 90
                    }
                }

                OkGo.post<String>(BaseHttp.identify_get)
                        .tag(this@RegisterActivity)
                        .params("mobile", et_name.text.trim().toString())
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                YZM = JSONObject(response.body()).getString("object")
                                mTel = et_name.text.trim().toString()
                                if (BuildConfig.LOG_DEBUG) et_yzm.setText(YZM)

                                bt_yzm.isClickable = false
                                bt_yzm.setBackgroundResource(R.drawable.rec_bg_ova_d0d0d0)
                                time_count = 90
                                bt_yzm.post(thread)
                            }

                        })
            }
            R.id.bt_sign -> {
                if (!CommonUtil.isMobileNumber(et_name.text.toString())) {
                    et_name.requestFocus()
                    et_name.setText("")
                    toast("手机号码格式错误，请重新输入")
                    return
                }

                if (et_name.text.toString() != mTel) {
                    toast("手机号码不匹配，请重新获取验证码")
                    return
                }

                if (et_yzm.text.trim().toString() != YZM) {
                    et_yzm.requestFocus()
                    et_yzm.setText("")
                    toast("验证码错误，请重新输入")
                    return
                }

                if (et_pwd.text.length < 6) {
                    toast("新密码长度不少于6位")
                    return
                }

                OkGo.post<String>(BaseHttp.register_sub)
                        .tag(this@RegisterActivity)
                        .params("mobile", et_name.text.toString())
                        .params("smscode", et_yzm.text.toString())
                        .params("password", et_pwd.text.toString())
                        .params("nickName", et_name.text.toString())
                        .params("loginType", "mobile")
                        .execute(object : StringDialogCallback(this@RegisterActivity) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                toast(msg)
                                ActivityStack.getScreenManager().popActivities(this@RegisterActivity::class.java)
                            }

                        })
            }
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (et_name.text.isNotBlank()
                && et_yzm.text.isNotBlank()
                && et_pwd.text.isNotBlank()
                && register_agree.isChecked) {
            bt_sign.setBackgroundResource(R.drawable.rec_bg_blue)
            bt_sign.isClickable = true
        } else {
            bt_sign.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
            bt_sign.isClickable = false
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (isChecked
                && et_name.text.isNotBlank()
                && et_yzm.text.isNotBlank()
                && et_pwd.text.isNotBlank()) {
            bt_sign.setBackgroundResource(R.drawable.rec_bg_blue)
            bt_sign.isClickable = true
        } else {
            bt_sign.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
            bt_sign.isClickable = false
        }
    }
}
