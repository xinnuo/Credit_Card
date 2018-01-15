package com.ruanmeng.credit_card

import android.content.Intent
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
import com.ruanmeng.utils.AnimationHelper
import com.ruanmeng.utils.BankCardUtil
import com.ruanmeng.utils.NumberHelper
import com.ruanmeng.utils.PopupWindowUtils
import kotlinx.android.synthetic.main.activity_bill.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter

class BillActivity : BaseActivity() {

    private val list = ArrayList<Any>()
    private var mPayType = ""
    private var mPosBill = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bill)
        setToolbarVisibility(false)
        init_title()

        swipe_refresh.isRefreshing = true
        getData(pageNum)
    }

    override fun init_title() {
        empty_hint.text = "暂无相关账单信息！"

        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_first_list) { data, injector ->
                    injector.text(R.id.item_first_name, data.payType)
                            .text(R.id.item_first_price, "￥${NumberHelper.fmtMicrometer(data.paySum)}")
                            .text(R.id.item_first_type,
                                    if (data.cardType == "0") "储蓄卡" else "信用卡" + when (data.type) {
                                        "0" -> "还款"
                                        "1" -> "消费"
                                        "2" -> "提现"
                                        "3" -> "充值"
                                        "4" -> "收款"
                                        else -> ""
                                    })
                            .text(R.id.item_first_num, "尾号(${data.cardNo.substring(data.cardNo.length - 4)})")
                            .text(R.id.item_first_rate,
                                    when (data.type) {
                                        "0" -> "手续费 ￥${if (data.rate.isEmpty()) "0.00" else String.format("%.2f", data.rate.toDouble())}"
                                        else -> "费率 ${if (data.rate.isEmpty()) "0.0" else (data.rate.toDouble() * 100).toString()}%"
                                    })
                            .text(R.id.item_first_time, data.payTime)
                            .text(R.id.item_first_status, when (data.status) {
                                "1" -> "交易成功"
                                "-2" -> "交易中"
                                else -> "交易失败"
                            })

                            .visibility(R.id.item_first_divider1, if (list.indexOf(data) == list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_first_divider2, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)

                            .with<RoundedImageView>(R.id.item_first_img) { view ->
                                when (BankCardUtil(data.cardNo).bankName) {
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

                            .clicked(R.id.item_first) {
                                val intent = Intent(baseContext, BillDetailActivity::class.java)
                                intent.putExtra("payRecordId", data.payRecordId)
                                startActivity(intent)
                            }
                }
                .attachTo(recycle_list)
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.bill_header -> {
                AnimationHelper.startRotateAnimator(bill_arrow, 0f, 180f)

                PopupWindowUtils.showDatePopWindow(
                        baseContext,
                        bill_divider,
                        mPosBill,
                        listOf("全部账单", "智能还款", "消费计划", "提现", "充值", "收款"),
                        object : PopupWindowUtils.PopupWindowCallBack {

                            override fun doWork(position: Int, name: String) {
                                bill_hint.text = name
                                mPayType = when (position) {
                                    0 -> ""
                                    else -> (position - 1).toString()
                                }
                                mPosBill = position

                                updateList()
                            }

                            override fun onDismiss() = AnimationHelper.startRotateAnimator(bill_arrow, 180f, 0f)

                        })
            }
        }
    }

    override fun getData(pindex: Int) {
        OkGo.post<CommonModel>(BaseHttp.payrecord_data)
                .tag(this@BillActivity)
                .headers("token", getString("token"))
                .params("page", pindex)
                .params("payType", mPayType)
                .execute(object : JacksonDialogCallback<CommonModel>(baseContext, CommonModel::class.java) {

                    override fun onSuccess(response: Response<CommonModel>) {
                        list.apply {
                            if (pindex == 1) {
                                clear()
                                pageNum = pindex
                            }
                            addItems(response.body().payrecords)
                            if (count(response.body().payrecords) > 0) pageNum++
                        }

                        mAdapter.updateData(list).notifyDataSetChanged()
                    }

                    override fun onFinish() {
                        super.onFinish()
                        swipe_refresh.isRefreshing = false
                        isLoadingMore = false

                        empty_view.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                    }

                })
    }

    fun updateList() {
        swipe_refresh.isRefreshing = true
        if (list.size > 0) {
            list.clear()
            mAdapter.updateData(list).notifyDataSetChanged()
        }
        pageNum = 1
        getData(pageNum)
    }
}
