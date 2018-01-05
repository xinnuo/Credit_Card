package com.ruanmeng.credit_card

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.view.View
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.base.toast
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.DialogHelper
import kotlinx.android.synthetic.main.activity_plan_pay.*
import kotlinx.android.synthetic.main.layout_title_left.*
import java.util.*

class PlanPayActivity : BaseActivity() {

    private var creditcardId = ""
    private var creditcard = ""
    private var bank = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plan_pay)
        setToolbarVisibility(false)
        init_title()
    }

    @SuppressLint("SetTextI18n")
    override fun init_title() {
        left_nav_title.text = "新增消费计划"

        creditcardId = intent.getStringExtra("creditcardId")
        creditcard = intent.getStringExtra("creditcard")
        bank = intent.getStringExtra("bank")

        plan_bank.text = bank + "(尾号${creditcard.substring(creditcard.length - 4)})"

        plan_submit.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
        plan_submit.isClickable = false

        plan_count.addTextChangedListener(this@PlanPayActivity)
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.plan_date_11 -> {
                val year_now = Calendar.getInstance().get(Calendar.YEAR)

                DialogHelper.showDateDialog(this@PlanPayActivity,
                        year_now,
                        year_now + 2,
                        3,
                        "选择还款日期",
                        true,
                        true, { _, _, _, _, _, date ->
                    plan_date.text = date
                })
            }
            R.id.plan_submit -> {
                if (plan_date.text.isEmpty()) {
                    toast("请选择还款日期")
                    return
                }

                OkGo.post<String>(BaseHttp.add_consumeplan)
                        .tag(this@PlanPayActivity)
                        .isMultipart(true)
                        .headers("token", getString("token"))
                        .params("creditcardId", creditcardId)
                        .params("repaymentType", "消费计划")
                        .params("repaymentSum", plan_count.text.toString())
                        .params("repaymentDay", plan_date.text.toString())
                        .params("cardType", "1")
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                toast(msg)
                                ActivityStack.getScreenManager().popActivities(this@PlanPayActivity::class.java)
                            }

                        })
            }
        }
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (plan_count.text.isNotBlank()) {
            plan_submit.setBackgroundResource(R.drawable.rec_bg_blue)
            plan_submit.isClickable = true
        } else {
            plan_submit.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
            plan_submit.isClickable = false
        }

        if (s.isNotEmpty()) {
            if ("." == s.toString()) {
                plan_count.setText("0.")
                plan_count.setSelection(plan_count.text.length) //设置光标的位置
            }
        }
    }

    override fun afterTextChanged(s: Editable) {
        val temp = s.toString()
        val posDot = temp.indexOf(".")
        if (posDot < 0) {
            if (temp.length > 7) s.delete(7, 8)
        } else {
            if (temp.length - posDot - 1 > 2) s.delete(posDot + 3, posDot + 4)
        }
    }
}
