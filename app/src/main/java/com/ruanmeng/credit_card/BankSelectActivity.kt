package com.ruanmeng.credit_card

import android.os.Bundle
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.load_Linear
import com.ruanmeng.model.RefreshMessageEvent
import com.ruanmeng.utils.ActivityStack
import kotlinx.android.synthetic.main.activity_bank_select.*
import net.idik.lib.slimadapter.SlimAdapter
import org.greenrobot.eventbus.EventBus

class BankSelectActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank_select)
        init_title("选择银行")

        val list = resources.getStringArray(R.array.bank_list).asList()
        mAdapter.updateData(list).notifyDataSetChanged()
    }

    override fun init_title() {
        super.init_title()
        bank_list.load_Linear(baseContext)

        mAdapter = SlimAdapter.create()
                .register<String>(R.layout.item_bank_select_list) { data, injector ->
                    injector.text(R.id.item_bank_name, data)

                            .clicked(R.id.item_bank) {
                                EventBus.getDefault().post(RefreshMessageEvent("选择银行", data))
                                ActivityStack.getScreenManager().popActivities(this@BankSelectActivity::class.java)
                            }
                }
                .attachTo(bank_list)
    }
}
