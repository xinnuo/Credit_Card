package com.ruanmeng.credit_card

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.CompoundButton
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.base.startActivity
import com.ruanmeng.base.toast
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.CommonUtil
import kotlinx.android.synthetic.main.activity_login.*

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

                startActivity(MainActivity::class.java)
                ActivityStack.getScreenManager().popActivities(this@LoginActivity::class.java)
            }
            R.id.tv_sign -> startActivity(RegisterActivity::class.java)
            R.id.tv_forget -> startActivity(ForgetActivity::class.java)
        }
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
