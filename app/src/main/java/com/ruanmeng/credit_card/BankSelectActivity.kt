package com.ruanmeng.credit_card

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.load_Linear
import com.ruanmeng.model.RefreshMessageEvent
import com.ruanmeng.utils.ActivityStack
import kotlinx.android.synthetic.main.activity_bank_select.*
import net.idik.lib.slimadapter.SlimAdapter
import org.greenrobot.eventbus.EventBus

class BankSelectActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank_select)
        init_title("选择银行")

        when (intent.getStringExtra("title")) {
            "储蓄卡" -> list.addAll(resources.getStringArray(R.array.bank_saving).asList())
            "信用卡" -> list.addAll(resources.getStringArray(R.array.bank_credit).asList())
            else -> list.addAll(resources.getStringArray(R.array.bank_list).asList())
        }
        mAdapter.updateData(list)
    }

    override fun init_title() {
        super.init_title()
        bank_list.load_Linear(baseContext)

        mAdapter = SlimAdapter.create()
                .register<String>(R.layout.item_bank_select_list) { data, injector ->
                    injector.text(R.id.item_bank_name, data)
                            .visibility(R.id.item_bank_divider1, if (list.indexOf(data) == list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_bank_divider2, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)

                            .with<ImageView>(R.id.item_bank_img, { view ->
                                when (data) {
                                    "工商银行" -> view.setImageResource(R.mipmap.bank01)
                                    "农业银行" -> view.setImageResource(R.mipmap.bank02)
                                    "招商银行" -> view.setImageResource(R.mipmap.bank03)
                                    "建设银行" -> view.setImageResource(R.mipmap.bank04)
                                    "交通银行" -> view.setImageResource(R.mipmap.bank05)
                                    "中信银行" -> view.setImageResource(R.mipmap.bank06)
                                    "光大银行" -> view.setImageResource(R.mipmap.bank07)
                                    "北京银行" -> view.setImageResource(R.mipmap.bank08)
                                    "平安银行" -> view.setImageResource(R.mipmap.bank09)
                                    "中国银行" -> view.setImageResource(R.mipmap.bank10)
                                    "兴业银行" -> view.setImageResource(R.mipmap.bank11)
                                    "民生银行" -> view.setImageResource(R.mipmap.bank12)
                                    "华夏银行" -> view.setImageResource(R.mipmap.bank13)
                                    "浦发银行" -> view.setImageResource(R.mipmap.bank14)
                                    "广发银行" -> view.setImageResource(R.mipmap.bank15)
                                    "邮政储蓄" -> view.setImageResource(R.mipmap.bank16)
                                }
                            })

                            .clicked(R.id.item_bank) {
                                EventBus.getDefault().post(RefreshMessageEvent("选择银行", data))
                                ActivityStack.getScreenManager().popActivities(this@BankSelectActivity::class.java)
                            }
                }
                .attachTo(bank_list)
    }
}
