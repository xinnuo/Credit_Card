package com.ruanmeng.credit_card

import android.os.Bundle
import android.view.View
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.makeramen.roundedimageview.RoundedImageView
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.CommonModel
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.NumberHelper
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter

class PlanDownActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plan_down)
        init_title("计划列表")

        swipe_refresh.isRefreshing = true
        getData()
    }

    override fun init_title() {
        super.init_title()
        swipe_refresh.refresh { getData() }
        recycle_list.load_Linear(baseContext, swipe_refresh)

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_plan_list) { data, injector ->
                    injector.text(R.id.item_plan_name, data.repaymentType)
                            .text(R.id.item_plan_price, "￥${NumberHelper.fmtMicrometer(data.repaymentSum)}")
                            .text(R.id.item_plan_type,
                                    "信用卡" + when (data.type) {
                                        "0" -> "还款"
                                        "1" -> "消费"
                                        else -> ""
                                    })
                            .text(R.id.item_plan_num, "尾号(${data.cardNo.substring(data.cardNo.length - 4)})")
                            .text(R.id.item_plan_time, if (data.status == "1") data.repaymentTime else (data.repaymentDay + ":00"))
                            .text(R.id.item_plan_status, when (data.status) {
                                "-1" -> "未还款"
                                "0" -> "还款失败"
                                "1" -> "已还款"
                                else -> ""
                            })

                            .with<RoundedImageView>(R.id.item_plan_img) { view ->
                                when (data.repaymentType) {
                                    "还款消费" -> view.setImageResource(R.mipmap.group01)
                                    "快速还款" -> view.setImageResource(R.mipmap.group02)
                                    "智能还款" -> view.setImageResource(R.mipmap.group02)
                                    "余额还款" -> view.setImageResource(R.mipmap.group03)
                                    "消费计划" -> view.setImageResource(R.mipmap.group04)
                                    "提现" -> view.setImageResource(R.mipmap.group05)
                                    "充值" -> view.setImageResource(R.mipmap.group06)
                                }
                            }

                            .visibility(R.id.item_plan_divider1, if (list.indexOf(data) == list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_plan_divider2, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)

                            .clicked(R.id.item_plan) { }
                }
                .attachTo(recycle_list)
    }

    override fun getData() {
        OkGo.post<CommonModel>(BaseHttp.repayment_list)
                .tag(this@PlanDownActivity)
                .headers("token", getString("token"))
                .params("repaymentplanId", intent.getStringExtra("repaymentplanId"))
                .execute(object : JacksonDialogCallback<CommonModel>(baseContext, CommonModel::class.java) {

                    override fun onSuccess(response: Response<CommonModel>) {
                        list.apply {
                            clear()
                            addItems(response.body().repayments)
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
