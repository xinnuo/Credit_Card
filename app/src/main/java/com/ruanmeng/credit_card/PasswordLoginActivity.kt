package com.ruanmeng.credit_card

import android.os.Bundle
import android.view.View
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.base.toast
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import kotlinx.android.synthetic.main.activity_password_login.*

class PasswordLoginActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_login)
        init_title("修改登录密码")
    }

    override fun init_title() {
        super.init_title()
        bt_ok.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
        bt_ok.isClickable = false

        et_now.addTextChangedListener(this@PasswordLoginActivity)
        et_new.addTextChangedListener(this@PasswordLoginActivity)
        et_confirm.addTextChangedListener(this@PasswordLoginActivity)
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.bt_ok -> {
                if (et_now.text.length < 6 || et_new.text.length < 6 || et_confirm.text.length < 6) {
                    toast("密码长度应不少于6位")
                    return
                }

                if (et_new.text.toString() != et_confirm.text.toString()) {
                    toast("密码输入不一致，请重新输入")
                    return
                }

                OkGo.post<String>(BaseHttp.password_change_sub)
                        .tag(this@PasswordLoginActivity)
                        .headers("token", getString("token"))
                        .params("oldPwd", et_now.text.toString())
                        .params("newPwd", et_new.text.toString())
                        .params("confirmPwd", et_confirm.text.toString())
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                toast(msg)
                                ActivityStack.getScreenManager().popActivities(this@PasswordLoginActivity::class.java)
                            }

                        })
            }
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (et_now.text.isNotBlank()
                && et_new.text.isNotBlank()
                && et_confirm.text.isNotBlank()) {
            bt_ok.setBackgroundResource(R.drawable.rec_bg_blue)
            bt_ok.isClickable = true
        } else {
            bt_ok.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
            bt_ok.isClickable = false
        }
    }
}
