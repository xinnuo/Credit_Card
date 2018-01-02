package com.ruanmeng.credit_card

import android.content.Intent
import android.os.Bundle
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.lzy.okgo.utils.OkLogger
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.base.toast
import com.ruanmeng.model.RefreshMessageEvent
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.activity_bank_code.*
import org.greenrobot.eventbus.EventBus

class BankCodeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank_code)
        init_title("短信验证码")
    }

    override fun init_title() {
        super.init_title()
        code_sure.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
        code_sure.isClickable = false

        et_code.addTextChangedListener(this@BankCodeActivity)

        code_sure.setOnClickListener {
            if (et_code.text.length < 6) {
                toast("请输入6位数字短信验证码")
                return@setOnClickListener
            }

            OkGo.post<String>(BaseHttp.depositcard_add_sub)
                    .tag(this@BankCodeActivity)
                    .headers("token", getString("token"))
                    .params("smsCode", et_code.text.toString())
                    .execute(object : StringDialogCallback(baseContext) {
                        /*{
                            "msg": "提交成功，请等待审核",
                            "msgcode": 100
                        }*/
                        override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                            OkLogger.i(msg)

                            if (intent.getBooleanExtra("isChanged", false)) {
                                EventBus.getDefault().post(RefreshMessageEvent("更换银行卡"))

                                val intent = Intent(baseContext, BankDoneActivity::class.java)
                                intent.putExtra("title", "更换银行卡")
                                intent.putExtra("hint", "更换银行卡成功！")
                                startActivity(intent)
                            } else {
                                EventBus.getDefault().post(RefreshMessageEvent("新增储蓄卡"))

                                val intent = Intent(baseContext, BankDoneActivity::class.java)
                                intent.putExtra("title", "新增储蓄卡")
                                intent.putExtra("hint", "新增储蓄卡成功！")
                                startActivity(intent)
                            }
                        }

                    })
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (et_code.text.isNotBlank()) {
            code_sure.setBackgroundResource(R.drawable.rec_bg_blue)
            code_sure.isClickable = true
        } else {
            code_sure.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
            code_sure.isClickable = false
        }
    }
}
