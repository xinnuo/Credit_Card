package com.ruanmeng.credit_card

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.CompoundButton
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.CommonUtil
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject

class LoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        transparentStatusBar(false)

        init_title()
    }

    override fun init_title() {
        bt_login.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
        bt_login.isClickable = false

        et_name.addTextChangedListener(this)
        et_pwd.addTextChangedListener(this)
        cb_pwd.setOnCheckedChangeListener(this)

        if (getString("mobile").isNotEmpty()) {
            et_name.setText(getString("mobile"))
            et_name.setSelection(et_name.text.length)
        }

        if (intent.getBooleanExtra("offLine", false)) {
            if (intent.getBooleanExtra("isToast", false))
                toast("当前账户在其他设备登录")

            clearData()

            ActivityStack.getScreenManager().popAllActivityExcept(this@LoginActivity::class.java)
        }
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.bt_login -> {
                if (!CommonUtil.isMobileNumber(et_name.text.toString())) {
                    et_name.requestFocus()
                    et_name.setText("")
                    toast("手机号码格式错误，请重新输入")
                    return
                }
                if (et_pwd.text.length < 6) {
                    et_pwd.requestFocus()
                    toast("密码长度不少于6位")
                    return
                }

                OkGo.post<String>(BaseHttp.login_sub)
                        .tag(this@LoginActivity)
                        .params("accountName", et_name.text.trim().toString())
                        .params("password", et_pwd.text.trim().toString())
                        .params("loginType", "mobile")
                        .execute(object : StringDialogCallback(this@LoginActivity) {
                            /*{
                                "msg": "登录成功",
                                "msgcode": 100,
                                "object": {
                                    "cusTel": "",
                                    "isPass": 0,
                                    "mobile": "17603870563",
                                    "msgsNum": 28,
                                    "rongtoken": "Q4DZc5aadc9rp8f7PWRFNc6IEHMWJRjuP/Un2Bh7R8OALyAioDNc8UX8RXn+TR8MVsC9MnbwkUndHZG64866M6nQ9l89IT/nN2JWgpjVAAH9/snaemskGR3HEfdi28dVDHGbu4/6NK8=",
                                    "token": "31743A18B53842298BC9DDF861651658"
                                }
                            }*/
                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                val obj = JSONObject(response.body()).getJSONObject("object")

                                putBoolean("isLogin", true)
                                putString("mobile", obj.getString("mobile"))
                                putString("msgsNum", obj.getString("msgsNum"))
                                putString("rongtoken", if (obj.isNull("rongtoken")) "" else obj.getString("rongtoken"))
                                putString("token", obj.getString("token"))

                                startActivity(MainActivity::class.java)
                                ActivityStack.getScreenManager().popActivities(this@LoginActivity::class.java)
                            }

                        })
            }
            R.id.tv_sign -> startActivity(RegisterActivity::class.java)
            R.id.tv_forget -> startActivity(ForgetActivity::class.java)
        }
    }

    private fun clearData() {
        putBoolean("isLogin", false)
        putString("token", "")
        putString("rongToken", "")

        putString("balanceSum", "0.00")
        putString("profitSum", "0.00")
        putString("withdrawSum", "0.00")
        putString("levelName", "")
        putString("platform", "")

        putString("age", "")
        putString("isPass", "")
        putString("nickName", "")
        putString("sex", "")
        putString("userhead", "")

        //清除通知栏消息
        // RongCloudContext.getInstance().clearNotificationMessage()
        // RongPushClient.clearAllPushNotifications(applicationContext)
        // RongIM.getInstance().logout()
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (et_name.text.isNotBlank()
                && et_pwd.text.isNotBlank()) {
            bt_login.setBackgroundResource(R.drawable.rec_bg_blue)
            bt_login.isClickable = true
        } else {
            bt_login.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
            bt_login.isClickable = false
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (isChecked) {
            et_pwd.transformationMethod = HideReturnsTransformationMethod.getInstance()
            et_pwd.setSelection(et_pwd.text.length)
        } else {
            et_pwd.transformationMethod = PasswordTransformationMethod.getInstance()
            et_pwd.setSelection(et_pwd.text.length)
        }
    }
}
