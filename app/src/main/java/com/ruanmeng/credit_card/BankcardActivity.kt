package com.ruanmeng.credit_card

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.view.View
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.CardData
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.CommonModel
import com.ruanmeng.model.RefreshMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.DialogHelper
import com.ruanmeng.utils.Tools
import kotlinx.android.synthetic.main.activity_bankcard.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class BankcardActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bankcard)
        init_title("我的银行卡")

        EventBus.getDefault().register(this@BankcardActivity)
    }

    override fun init_title() {
        super.init_title()

        agency_tab.apply {
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

                override fun onTabReselected(tab: TabLayout.Tab) {}
                override fun onTabUnselected(tab: TabLayout.Tab) {}

                override fun onTabSelected(tab: TabLayout.Tab) {
                    mPosition = tab.position
                    OkGo.getInstance().cancelTag(this@BankcardActivity)

                    window.decorView.postDelayed({ runOnUiThread { updateList() } }, 300)
                }

            })

            addTab(this.newTab().setText("储蓄卡"), true)
            addTab(this.newTab().setText("信用卡"), false)

            post { Tools.setIndicator(this, 50, 50) }
        }

        swipe_refresh.refresh { getData(mPosition) }
        recycle_list.load_Linear(baseContext)

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_bankcard_list) { data, injector ->
                    injector.background(R.id.item_bankcard, R.drawable.rec_bg_ca566b)
                            .text(R.id.item_bankcard_hint, "新增储蓄卡")
                            .text(R.id.item_bankcard_name, data.bank)
                            .text(R.id.item_bankcard_tail,
                                    if (data.tel.isEmpty()) "0000" else data.tel.substring(data.tel.length - 4))
                            .text(R.id.item_bankcard_num,
                                    if (data.depositcard.isEmpty()) "0000" else data.depositcard.substring(data.depositcard.length - 4))
                            .visibility(R.id.item_bankcard, if (data.isChecked) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_bankcard_add, if (!data.isChecked) View.GONE else View.VISIBLE)

                            .clicked(R.id.item_bankcard) {
                                val intent = Intent(baseContext, SavingsDetailActivity::class.java)
                                intent.putExtra("data", data)
                                startActivity(intent)
                            }

                            .clicked(R.id.item_bankcard_add) {
                                when (getString("isPass")) {
                                    "-1" -> {
                                        DialogHelper.showDialog(
                                                baseContext,
                                                "温馨提示",
                                                "实名认证审核失败，是否现在去重新认证？",
                                                "取消",
                                                "去认证") {

                                            startActivity(RealNameActivity::class.java)
                                            ActivityStack.getScreenManager().popActivities(this@BankcardActivity::class.java)
                                        }
                                    }
                                    "0" -> toast("实名认证信息正在审核中")
                                    "1" -> startActivity(SavingsCardActivity::class.java)
                                    else -> {
                                        DialogHelper.showDialog(
                                                baseContext,
                                                "温馨提示",
                                                "未进行实名认证，暂无法添加储蓄卡，是否现在去认证？",
                                                "取消",
                                                "去认证") {

                                            startActivity(RealNameActivity::class.java)
                                            ActivityStack.getScreenManager().popActivities(this@BankcardActivity::class.java)
                                        }
                                    }
                                }
                            }
                }
                .register<CardData>(R.layout.item_bankcard_list) { data, injector ->
                    injector.background(R.id.item_bankcard, R.drawable.rec_bg_414b80)
                            .text(R.id.item_bankcard_hint, "新增信用卡")
                            .text(R.id.item_bankcard_name, data.bank)
                            .text(R.id.item_bankcard_tail,
                                    if (data.tel.isEmpty()) "0000" else data.tel.substring(data.tel.length - 4))
                            .text(R.id.item_bankcard_num,
                                    if (data.creditcard.isEmpty()) "0000" else data.creditcard.substring(data.creditcard.length - 4))
                            .visibility(R.id.item_bankcard, if (data.isChecked) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_bankcard_add, if (!data.isChecked) View.GONE else View.VISIBLE)

                            .clicked(R.id.item_bankcard) {
                                val intent = Intent(baseContext, CreditDetailActivity::class.java)
                                intent.putExtra("creditcardId", data.creditcardId)
                                startActivity(intent)
                            }

                            .clicked(R.id.item_bankcard_add) {
                                when (getString("isPass")) {
                                    "-1" -> {
                                        DialogHelper.showDialog(
                                                baseContext,
                                                "温馨提示",
                                                "实名认证审核失败，是否现在去重新认证？",
                                                "取消",
                                                "去认证") {

                                            startActivity(RealNameActivity::class.java)
                                            ActivityStack.getScreenManager().popActivities(this@BankcardActivity::class.java)
                                        }
                                    }
                                    "0" -> toast("实名认证信息正在审核中")
                                    "1" -> startActivity(CreditCardActivity::class.java)
                                    else -> {
                                        DialogHelper.showDialog(
                                                baseContext,
                                                "温馨提示",
                                                "未进行实名认证，暂无法添加信用卡，是否现在去认证？",
                                                "取消",
                                                "去认证") {

                                            startActivity(RealNameActivity::class.java)
                                            ActivityStack.getScreenManager().popActivities(this@BankcardActivity::class.java)
                                        }
                                    }
                                }
                            }
                }
                .attachTo(recycle_list)
    }

    override fun getData(pindex: Int) {
        when (pindex) {
            0 -> {
                OkGo.post<CommonModel>(BaseHttp.depositcard_data)
                        .tag(this@BankcardActivity)
                        .headers("token", getString("token"))
                        .execute(object : JacksonDialogCallback<CommonModel>(baseContext, CommonModel::class.java) {

                            override fun onSuccess(response: Response<CommonModel>) {
                                list.apply {
                                    clear()
                                    addItems(response.body().depositcards)

                                    if (isEmpty()) add(CommonData().apply { isChecked = true })
                                }

                                mAdapter.updateData(list).notifyDataSetChanged()
                            }

                            override fun onFinish() {
                                super.onFinish()
                                swipe_refresh.isRefreshing = false
                            }

                        })
            }
            1 -> {
                OkGo.post<CommonModel>(BaseHttp.creditcard_data)
                        .tag(this@BankcardActivity)
                        .headers("token", getString("token"))
                        .execute(object : JacksonDialogCallback<CommonModel>(baseContext, CommonModel::class.java) {

                            override fun onSuccess(response: Response<CommonModel>) {
                                list.apply {
                                    clear()
                                    addItems(response.body().creditcards)

                                    add(CardData().apply { isChecked = true })
                                }

                                mAdapter.updateData(list).notifyDataSetChanged()
                            }

                            override fun onFinish() {
                                super.onFinish()
                                swipe_refresh.isRefreshing = false
                            }

                        })
            }
        }
    }

    fun updateList() {
        swipe_refresh.isRefreshing = true
        if (list.size > 0) {
            list.clear()
            mAdapter.updateData(list).notifyDataSetChanged()
        }
        getData(mPosition)
    }

    override fun onBackPressed() {
        EventBus.getDefault().unregister(this@BankcardActivity)
        super.onBackPressed()
    }

    @Subscribe
    fun onMessageEvent(event: RefreshMessageEvent) {
        when (event.name) {
            "新增储蓄卡" -> {
                swipe_refresh.isRefreshing = true
                getData(mPosition)
            }
            "新增信用卡" -> {
                swipe_refresh.isRefreshing = true
                getData(mPosition)
            }
            "更换银行卡" -> {
                swipe_refresh.isRefreshing = true
                getData(mPosition)
            }
        }
    }
}
