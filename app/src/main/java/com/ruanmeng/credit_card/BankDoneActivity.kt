package com.ruanmeng.credit_card

import android.os.Bundle
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.utils.ActivityStack
import kotlinx.android.synthetic.main.activity_bank_done.*

class BankDoneActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank_done)
        init_title(intent.getStringExtra("title"))
    }

    override fun init_title() {
        super.init_title()
        bank_hint.text = intent.getStringExtra("hint")

        bank_done.setOnClickListener {
            when (intent.getStringExtra("title")) {
                "新增储蓄卡" -> ActivityStack.getScreenManager().popActivities(
                        this@BankDoneActivity::class.java,
                        SavingsCardActivity::class.java)
                "新增信用卡" -> ActivityStack.getScreenManager().popActivities(
                        this@BankDoneActivity::class.java,
                        CreditCardActivity::class.java)
                "更换银行卡" -> ActivityStack.getScreenManager().popActivities(
                        this@BankDoneActivity::class.java,
                        SavingsCardActivity::class.java,
                        SavingsDetailActivity::class.java)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        when (intent.getStringExtra("title")) {
            "新增储蓄卡" -> ActivityStack.getScreenManager().popActivities(
                    this@BankDoneActivity::class.java,
                    SavingsCardActivity::class.java)
            "新增信用卡" -> ActivityStack.getScreenManager().popActivities(
                    this@BankDoneActivity::class.java,
                    CreditCardActivity::class.java)
            "更换银行卡" -> ActivityStack.getScreenManager().popActivities(
                    this@BankDoneActivity::class.java,
                    SavingsCardActivity::class.java,
                    SavingsDetailActivity::class.java)
        }
    }
}
