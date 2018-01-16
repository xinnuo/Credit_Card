package com.ruanmeng.credit_card

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.makeramen.roundedimageview.RoundedImageView
import com.ruanmeng.base.*
import com.ruanmeng.model.CardData
import com.ruanmeng.model.CommonModel
import com.ruanmeng.model.RefreshMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.TimeHelper
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class RepaymentActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repayment)
        init_title("智能还款")

        EventBus.getDefault().register(this@RepaymentActivity)

        swipe_refresh.isRefreshing = true
        getData()
    }

    @SuppressLint("SetTextI18n")
    override fun init_title() {
        super.init_title()
        empty_hint.text = "暂无相关信用卡信息！"

        swipe_refresh.refresh { getData() }
        recycle_list.load_Linear(baseContext, swipe_refresh)

        mAdapter = SlimAdapter.create()
                .register<CardData>(R.layout.item_creditcard_list) { data, injector ->
                    injector.text(R.id.item_creditcard_hint, "新增信用卡")
                            .text(R.id.item_creditcard_name,
                                    "${data.bank}信用卡(尾号${when {
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
                                }
                            }

                            .clicked(R.id.item_creditcard) {
                                val intent = Intent(baseContext, CreditDetailActivity::class.java)
                                intent.putExtra("creditcardId", data.creditcardId)
                                intent.putExtra("isPay", true)
                                startActivity(intent)
                            }
                }
                .attachTo(recycle_list)
    }

    override fun getData() {
        OkGo.post<CommonModel>(BaseHttp.creditcard_data)
                .tag(this@RepaymentActivity)
                .headers("token", getString("token"))
                .execute(object : JacksonDialogCallback<CommonModel>(baseContext, CommonModel::class.java) {

                    override fun onSuccess(response: Response<CommonModel>) {
                        list.apply {
                            clear()
                            addItems(response.body().creditcards)
                        }

                        mAdapter.updateData(list).notifyDataSetChanged()
                    }

                    override fun onFinish() {
                        super.onFinish()
                        swipe_refresh.isRefreshing = false

                        empty_view.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                    }

                })
    }

    override fun finish() {
        EventBus.getDefault().unregister(this@RepaymentActivity)
        super.finish()
    }

    @Subscribe
    fun onMessageEvent(event: RefreshMessageEvent) {
        when (event.name) {
            "删除信用卡", "更新信用卡" -> {
                swipe_refresh.isRefreshing = true
                getData()
            }
        }
    }
}
