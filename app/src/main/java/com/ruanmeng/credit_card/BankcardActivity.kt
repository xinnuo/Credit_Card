package com.ruanmeng.credit_card

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.view.View
import android.widget.TextView
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.makeramen.roundedimageview.RoundedImageView
import com.ruanmeng.base.*
import com.ruanmeng.model.CardData
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.CommonModel
import com.ruanmeng.model.RefreshMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.DialogHelper
import com.ruanmeng.utils.TimeHelper
import com.ruanmeng.utils.Tools
import kotlinx.android.synthetic.main.activity_bankcard.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class BankcardActivity : BaseActivity() {

    private val list = ArrayList<Any>()
    private val list_depositcard = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bankcard)
        init_title("我的银行卡")

        EventBus.getDefault().register(this@BankcardActivity)
    }

    @SuppressLint("SetTextI18n")
    override fun init_title() {
        super.init_title()

        backcard_tab.apply {
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
        recycle_list.load_Linear(baseContext, swipe_refresh)

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_bankcard_list) { data, injector ->
                    injector.background(R.id.item_bankcard, R.drawable.rec_bg_ca566b)
                            .text(R.id.item_bankcard_hint, "新增储蓄卡")
                            .text(R.id.item_bankcard_name, data.bank + "储蓄卡")
                            .text(R.id.item_bankcard_tail,
                                    if (data.tel.isEmpty()) "0000" else data.tel.substring(data.tel.length - 4))
                            .text(R.id.item_bankcard_num,
                                    if (data.depositcard.isEmpty()) "0000" else data.depositcard.substring(data.depositcard.length - 4))
                            .visibility(R.id.item_bankcard, if (data.isChecked) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_bankcard_add, if (!data.isChecked) View.GONE else View.VISIBLE)

                            .with<RoundedImageView>(R.id.item_bankcard_img) { view ->
                                when (data.bank) {
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
                            }

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
                .register<CardData>(R.layout.item_creditcard_list) { data, injector ->
                    injector.text(R.id.item_creditcard_hint, "新增信用卡")
                            .text(R.id.item_creditcard_name,
                                    "${data.bank}(尾号${when {
                                        data.creditcard.isEmpty() -> "0000"
                                        else -> data.creditcard.substring(data.creditcard.length - 4)
                                    }})")
                            .text(R.id.item_creditcard_bill, "账单日：${data.billDay.toInt()}日")
                            .text(R.id.item_creditcard_pay, "还款日：${data.repaymentDay.toInt()}日")
                            .text(R.id.item_creditcard_tail,
                                    if (data.tel.isEmpty()) "0000" else data.tel.substring(data.tel.length - 4))
                            .visibility(R.id.item_creditcard, if (data.isChecked) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_creditcard_add, if (!data.isChecked) View.GONE else View.VISIBLE)

                            .with<TextView>(R.id.item_creditcard_day) { view ->
                                val date_now = TimeHelper.getInstance().stringDateShort                     //当天
                                val date_day = TimeHelper.getInstance().getDayOfMonth(data.repaymentDay)    //本月指定日期
                                val date_next = TimeHelper.getInstance().getAfterMonth(date_day, 1) //下月指定日期
                                val days_now = TimeHelper.getInstance().getDays(date_now, date_day)         //本月相隔天数
                                val days_next = TimeHelper.getInstance().getDays(date_now, date_next)       //下月相隔天数

                                view.text = "距离还款日还有${when {
                                    days_now < 0 -> days_next
                                    else -> days_now
                                }}天"
                            }

                            .with<RoundedImageView>(R.id.item_creditcard_img) { view ->
                                when (data.bank) {
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
                            }

                            .clicked(R.id.item_creditcard) {
                                val intent = Intent(baseContext, CreditDetailActivity::class.java)
                                intent.putExtra("creditcardId", data.creditcardId)
                                startActivity(intent)
                            }

                            .clicked(R.id.item_creditcard_add) {
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
                                    "1" -> {
                                        if (list_depositcard.isEmpty()) {
                                            toast("您还未绑定储蓄卡，请先添加储蓄卡")
                                            return@clicked
                                        }

                                        startActivity(CreditCardActivity::class.java)
                                    }
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

                                list_depositcard.addItems(response.body().depositcards)

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

    override fun finish() {
        EventBus.getDefault().unregister(this@BankcardActivity)
        super.finish()
    }

    @Subscribe
    fun onMessageEvent(event: RefreshMessageEvent) {
        when (event.name) {
            "新增储蓄卡", "新增信用卡", "更换银行卡", "删除信用卡", "更新信用卡" -> {
                swipe_refresh.isRefreshing = true
                getData(mPosition)
            }
        }
    }
}
