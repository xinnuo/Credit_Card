package com.ruanmeng.credit_card

import android.os.Bundle
import android.view.View
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.base.toast
import com.ruanmeng.model.RefreshMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import kotlinx.android.synthetic.main.activity_email.*
import org.greenrobot.eventbus.EventBus
import java.util.regex.Pattern

class EmailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email)
        init_title(intent.getStringExtra("title"))
    }

    override fun init_title() {
        super.init_title()
        email_sure.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
        email_sure.isClickable = false

        email_account.addTextChangedListener(this@EmailActivity)
        email_pwd.addTextChangedListener(this@EmailActivity)
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.email_sure -> {
                if (!isEmail(email_account.text.toString())) {
                    email_account.requestFocus()
                    email_account.setText("")
                    toast("邮箱格式错误，请重新输入")
                    return
                }

                when (intent.getStringExtra("title")) {
                    "绑定邮箱" -> {
                        OkGo.post<String>(BaseHttp.mail_add_sub)
                                .apply {
                                    tag(this@EmailActivity)
                                    headers("token", getString("token"))
                                    params("creditcardId", intent.getStringExtra("creditcardId"))
                                    params("mailUser", email_account.text.toString())
                                    params("mailPassword", email_pwd.text.toString())
                                }
                                .execute(object : StringDialogCallback(baseContext) {
                                    /*{
                                        "msg": "添加成功",
                                        "msgcode": 100
                                    }*/
                                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                        toast(msg)
                                        EventBus.getDefault().post(RefreshMessageEvent("绑定邮箱", email_account.text.toString()))
                                        ActivityStack.getScreenManager().popActivities(this@EmailActivity::class.java)
                                    }

                                })
                    }
                    "更换邮箱" -> {
                        OkGo.post<String>(BaseHttp.mail_update_sub)
                                .apply {
                                    tag(this@EmailActivity)
                                    headers("token", getString("token"))
                                    params("mailId", intent.getStringExtra("mailId"))
                                    params("mailUser", email_account.text.toString())
                                    params("mailPassword", email_pwd.text.toString())
                                }
                                .execute(object : StringDialogCallback(baseContext) {
                                    /*{
                                        "msg": "更新成功",
                                        "msgcode": 100
                                    }*/
                                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                        toast(msg)
                                        EventBus.getDefault().post(RefreshMessageEvent("更换邮箱", email_account.text.toString()))
                                        ActivityStack.getScreenManager().popActivities(this@EmailActivity::class.java)
                                    }

                                })
                    }
                }
            }
        }
    }

    private fun isEmail(strEmail: String): Boolean {
        val strPattern = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@(163|126|sina|qq|139)\\.com"
        val p = Pattern.compile(strPattern)
        val m = p.matcher(strEmail)
        return m.matches()
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (email_account.text.isNotBlank()
                && email_pwd.text.isNotBlank()) {
            email_sure.setBackgroundResource(R.drawable.rec_bg_blue)
            email_sure.isClickable = true
        } else {
            email_sure.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
            email_sure.isClickable = false
        }
    }
}
