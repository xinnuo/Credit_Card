package com.ruanmeng.credit_card

import android.os.Bundle
import android.view.View
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.toast
import com.ruanmeng.utils.CommonUtil
import kotlinx.android.synthetic.main.activity_email.*

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
                if (!CommonUtil.isEmail(email_account.text.toString())) {
                    email_account.requestFocus()
                    email_account.setText("")
                    toast("邮箱格式错误，请重新输入")
                    return
                }
            }
        }
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
