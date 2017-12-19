package com.ruanmeng.credit_card

import android.os.Bundle
import android.support.design.widget.TabLayout
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
import com.ruanmeng.utils.Tools
import kotlinx.android.synthetic.main.activity_plan.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter

class PlanActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plan)
        setToolbarVisibility(false)
        init_title()
    }

    override fun init_title() {
        empty_hint.text = "暂无相关计划信息！"

        plan_tab.apply {
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

                override fun onTabReselected(tab: TabLayout.Tab) {}
                override fun onTabUnselected(tab: TabLayout.Tab) {}

                override fun onTabSelected(tab: TabLayout.Tab) {
                    mPosition = 1 - tab.position
                    OkGo.getInstance().cancelTag(this@PlanActivity)

                    window.decorView.postDelayed({ runOnUiThread { updateList() } }, 300)
                }

            })

            addTab(this.newTab().setText("还款计划"), true)
            addTab(this.newTab().setText("消费计划"), false)

            post { Tools.setIndicator(this, 50, 50) }
        }

        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_plan_list) { data, injector ->
                    injector.text(R.id.item_plan_name, data.repaymentType)
                            .text(R.id.item_plan_price, "￥${NumberHelper.fmtMicrometer(data.repaymentSum)}")
                            .text(R.id.item_plan_type, if (data.cardType == "0") "储蓄卡" else "信用卡" + (if (mPosition == 0) "消费" else "还款"))
                            .text(R.id.item_plan_num, "尾号(${data.cardNo.substring(data.cardNo.length - 4)})")
                            .text(R.id.item_plan_time, data.repaymentTime)
                            .text(R.id.item_plan_status, if (data.status == "1") "已还款" else "未还款")

                            .with<RoundedImageView>(R.id.item_plan_img) { view ->
                                when (data.repaymentType) {
                                    "还款消费" -> view.setImageResource(R.mipmap.group01)
                                    "快速还款" -> view.setImageResource(R.mipmap.group02)
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

    override fun getData(pindex: Int) {
        OkGo.post<CommonModel>(BaseHttp.repayment_data)
                .tag(this@PlanActivity)
                .headers("token", getString("token"))
                .params("type", mPosition)
                .params("page", pindex)
                .execute(object : JacksonDialogCallback<CommonModel>(baseContext, CommonModel::class.java) {

                    override fun onSuccess(response: Response<CommonModel>) {
                        list.apply {
                            if (pindex == 1) {
                                clear()
                                pageNum = pindex
                            }
                            addItems(response.body().repayments)
                            if (count(response.body().repayments) > 0) pageNum++
                        }

                        mAdapter.updateData(list).notifyDataSetChanged()
                    }

                    override fun onFinish() {
                        super.onFinish()
                        swipe_refresh.isRefreshing = false
                        isLoadingMore = false

                        empty_view.visibility = if (list.size == 0) View.VISIBLE else View.GONE
                    }

                })
    }

    fun updateList() {
        swipe_refresh.isRefreshing = true
        if (list.size > 0) {
            list.clear()
            mAdapter.updateData(list).notifyDataSetChanged()
        }
        getData(mPosition)
    }
}
