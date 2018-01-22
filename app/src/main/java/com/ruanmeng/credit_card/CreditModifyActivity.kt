package com.ruanmeng.credit_card

import android.os.Bundle
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.base.toast
import com.ruanmeng.model.RefreshMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import kotlinx.android.synthetic.main.activity_credit_modify.*
import org.greenrobot.eventbus.EventBus
import android.text.InputFilter


class CreditModifyActivity : BaseActivity() {

    private lateinit var title: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credit_modify)
        title = intent.getStringExtra("title")
        init_title(title)
    }

    override fun init_title() {
        super.init_title()
        when (title) {
            "修改账单日" -> {
                modify_hint.text = "账单日"
                modify_day.hint = "请输入账单日(两位日期数字)"
            }
            "修改还款日" -> {
                modify_hint.text = "还款日"
                modify_day.hint = "请输入还款日(两位日期数字)"
            }
            "修改额度" -> {
                modify_hint.text = "额度"
                modify_day.hint = "请输入信用卡额度(元)"
                modify_day.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(7))
            }
        }
        modify_sure.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
        modify_sure.isClickable = false

        modify_day.addTextChangedListener(this@CreditModifyActivity)

        modify_day.setText(intent.getStringExtra("day"))
        modify_day.setSelection(modify_day.text.length)

        modify_sure.setOnClickListener {
            val day = modify_day.text.trim().toString().toInt()

            if (day == intent.getStringExtra("day").toInt()) {
                ActivityStack.getScreenManager().popActivities(this@CreditModifyActivity::class.java)
                return@setOnClickListener
            }

            when (title) {
                "修改账单日", "修改还款日" -> if (day < 0 || day > 31) {
                    toast("请输入正确的${modify_hint.text}（两位数字）")
                    return@setOnClickListener
                }
                "修改额度" -> if (day < 0) {
                    toast("请输入正确的信用卡额度")
                    return@setOnClickListener
                }
            }

            OkGo.post<String>(BaseHttp.creditcard_edit_sub)
                    .apply {
                        tag(this@CreditModifyActivity)
                        headers("token", getString("token"))
                        params("creditcardId", intent.getStringExtra("creditcardId"))
                        when (title) {
                            "修改账单日" -> params("billDay", day)
                            "修改还款日" -> params("repaymentDay", day)
                            "修改额度" -> params("quota", day)
                        }
                    }
                    .execute(object : StringDialogCallback(baseContext) {
                        /*{
                            "msg": "信用卡更新成功",
                            "msgcode": 100
                        }*/
                        override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                            toast(msg)

                            EventBus.getDefault().post(RefreshMessageEvent(title, day.toString()))
                            EventBus.getDefault().post(RefreshMessageEvent("更新信用卡"))

                            ActivityStack.getScreenManager().popActivities(this@CreditModifyActivity::class.java)
                        }

                    })
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (modify_day.text.isNotBlank()) {
            modify_sure.setBackgroundResource(R.drawable.rec_bg_blue)
            modify_sure.isClickable = true
        } else {
            modify_sure.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
            modify_sure.isClickable = false
        }
    }
}
