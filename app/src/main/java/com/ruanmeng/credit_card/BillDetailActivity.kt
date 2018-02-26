package com.ruanmeng.credit_card

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.model.CountMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.CommonUtil
import com.ruanmeng.utils.TimeHelper
import kotlinx.android.synthetic.main.activity_bill_detail.*
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject

class BillDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bill_detail)
        init_title("交易详情")

        getData()
    }

    override fun init_title() {
        super.init_title()

        bill_retry.setOnClickListener { getRetryData() }
    }

    override fun getData() {
        OkGo.post<String>(BaseHttp.payrecord_detils)
                .tag(this@BillDetailActivity)
                .params("payRecordId", intent.getStringExtra("payRecordId"))
                .execute(object : StringDialogCallback(baseContext) {

                    @SuppressLint("SetTextI18n")
                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                        val data = JSONObject(response.body()).getJSONObject("payrecord")

                        val cardType = data.getString("cardType")
                        val cardNo = data.getString("cardNo")
                        val realSum = data.getString("realSum")
                        val businessSum = if (data.isNull("businessSum")) "0.00" else data.getString("businessSum")
                        val payTime = data.getString("payTime")
                        val status = when (data.getString("status")) {
                            "1" -> "交易成功"
                            "-2" -> "交易中"
                            else -> "交易失败"
                        }

                        bill_id.setRightString(data.getString("payRecordId"))
                        bill_type.setRightString(data.getString("payType"))
                        bill_card.setRightString(CommonUtil.bankCardReplaceHeaderWithStar(cardNo))
                        bill_pay.setRightString(String.format("%.2f", realSum.toDouble()))
                        bill_back.setRightString(String.format("%.2f", realSum.toDouble() - businessSum.toDouble()))
                        bill_fee.setRightString(String.format("%.2f", businessSum.toDouble()))
                        bill_time.setRightString(payTime)
                        bill_status.setRightString(status)

                        val nowTime = TimeHelper.getInstance().stringDateShort
                        if ((cardType == "0" || cardType == "1")
                                && payTime.contains(nowTime)
                                && status == "交易失败")
                            bill_retry.visibility = View.VISIBLE
                    }

                })
    }

    private fun getRetryData() {
        OkGo.post<String>(BaseHttp.hand_exec_plan)
                .tag(this@BillDetailActivity)
                .headers("token", getString("token"))
                .params("repaymentId", intent.getStringExtra("payRecordId"))
                .execute(object : StringDialogCallback(baseContext) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        getData()
                        EventBus.getDefault().post(CountMessageEvent("手动计划", ""))
                    }

                })
    }
}
