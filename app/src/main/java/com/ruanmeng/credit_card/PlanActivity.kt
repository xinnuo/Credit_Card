package com.ruanmeng.credit_card

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.lzy.extend.StringDialogCallback
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.makeramen.roundedimageview.RoundedImageView
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.CommonModel
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.BankCardUtil
import com.ruanmeng.utils.DialogHelper
import com.ruanmeng.utils.NumberHelper
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter

class PlanActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plan)
        init_title("全部计划")

        swipe_refresh.isRefreshing = true
        getData(pageNum)
    }

    override fun init_title() {
        super.init_title()
        empty_hint.text = "暂无相关计划信息！"

        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_plan_up_list) { data, injector ->
                    injector.text(R.id.item_plan_name, data.repaymentType)
                            .text(R.id.item_plan_price, "￥${NumberHelper.fmtMicrometer(data.repaymentSum)}")
                            .text(R.id.item_plan_type, "信用卡还款")
                            .text(R.id.item_plan_num, "尾号(${data.cardNo.substring(data.cardNo.length - 4)})")
                            .text(
                                    R.id.item_plan_time,
                                    data.repaymentDay
                                            .split(",")
                                            .toString()
                                            .replace("[", "")
                                            .replace("]", ""))

                            .with<RoundedImageView>(R.id.item_plan_img) { view ->
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
                                    "浦发银行" -> view.setImageResource(R.mipmap.bank14)
                                    "广发银行" -> view.setImageResource(R.mipmap.bank15)
                                    "邮政储蓄" -> view.setImageResource(R.mipmap.bank16)
                                }
                            }

                            .visibility(R.id.item_plan_divider1, if (list.indexOf(data) == list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_plan_divider2, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_plan_delete, when {
                                data.sfdelete.isEmpty() -> View.VISIBLE
                                data.sfdelete.toInt() > 0 -> View.GONE
                                else -> View.VISIBLE
                            })

                            .clicked(R.id.item_plan_delete) {
                                DialogHelper.showDialog(
                                        baseContext,
                                        "温馨提示",
                                        "确定要删除该计划吗？",
                                        "取消",
                                        "删除",
                                        null) {
                                    OkGo.post<String>(BaseHttp.remove_repaymentplan)
                                            .tag(this@PlanActivity)
                                            .headers("token", getString("token"))
                                            .params("repaymentplanId", data.repaymentplanId)
                                            .execute(object : StringDialogCallback(baseContext) {

                                                override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                                    toast(msg)
                                                    list.remove(data)
                                                    mAdapter.updateData(list).notifyDataSetChanged()

                                                    empty_view.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                                                }

                                            })
                                }
                            }

                            .clicked(R.id.item_plan) {
                                val intent = Intent(baseContext, PlanDownActivity::class.java)
                                intent.putExtra("repaymentplanId", data.repaymentplanId)
                                intent.putExtra("maxSum", data.maxSum)
                                intent.putExtra("sumRateSum", data.sumRateSum)
                                startActivity(intent)
                            }
                }
                .attachTo(recycle_list)
    }

    override fun getData(pindex: Int) {
        OkGo.post<CommonModel>(BaseHttp.repaymentplan_data)
                .tag(this@PlanActivity)
                .headers("token", getString("token"))
                .params("creditcardId", intent.getStringExtra("creditcardId"))
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

                        mAdapter.updateData(list)
                    }

                    override fun onFinish() {
                        super.onFinish()
                        swipe_refresh.isRefreshing = false
                        isLoadingMore = false

                        empty_view.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                    }

                })
    }
}
