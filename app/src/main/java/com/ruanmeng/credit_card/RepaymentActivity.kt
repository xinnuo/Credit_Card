package com.ruanmeng.credit_card

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.makeramen.roundedimageview.RoundedImageView
import com.ruanmeng.base.*
import com.ruanmeng.model.CardData
import com.ruanmeng.model.CommonModel
import com.ruanmeng.model.RefreshMessageEvent
import com.ruanmeng.share.BaseHttp
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

    override fun init_title() {
        super.init_title()
        swipe_refresh.refresh { getData() }
        recycle_list.load_Linear(baseContext)

        mAdapter = SlimAdapter.create()
                .register<CardData>(R.layout.item_bankcard_list) { data, injector ->
                    injector.background(R.id.item_bankcard, R.drawable.rec_bg_blue)
                            .text(R.id.item_bankcard_hint, "新增信用卡")
                            .text(R.id.item_bankcard_name, data.bank + "信用卡")
                            .text(R.id.item_bankcard_tail,
                                    if (data.tel.isEmpty()) "0000" else data.tel.substring(data.tel.length - 4))
                            .text(R.id.item_bankcard_num,
                                    if (data.creditcard.isEmpty()) "0000" else data.creditcard.substring(data.creditcard.length - 4))
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
                                }
                            }

                            .clicked(R.id.item_bankcard) {
                                val intent = Intent(baseContext, CreditDetailActivity::class.java)
                                intent.putExtra("creditcardId", data.creditcardId)
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
            "删除信用卡" -> {
                swipe_refresh.isRefreshing = true
                getData()
            }
        }
    }
}
