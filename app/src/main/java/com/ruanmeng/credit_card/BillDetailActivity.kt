package com.ruanmeng.credit_card

import android.annotation.SuppressLint
import android.os.Bundle
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.CommonUtil
import kotlinx.android.synthetic.main.activity_bill_detail.*
import org.json.JSONObject

class BillDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bill_detail)
        init_title("交易详情")

        getData()
    }

    override fun getData() {
        OkGo.post<String>(BaseHttp.payrecord_detils)
                .tag(this@BillDetailActivity)
                .params("payRecordId", intent.getStringExtra("payRecordId"))
                .execute(object : StringDialogCallback(baseContext) {

                    @SuppressLint("SetTextI18n")
                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                        val data = JSONObject(response.body()).getJSONObject("payrecord")

                        val cardNo = data.getString("cardNo")
                        val realSum = data.getString("realSum")
                        val businessSum = data.getString("businessSum")
                        val status = data.getString("status")

                        bill_id.setRightString(data.getString("payRecordId"))
                        bill_type.setRightString(data.getString("payType"))
                        bill_card.setRightString(CommonUtil.bankCardReplaceHeaderWithStar(cardNo))
                        bill_pay.setRightString(String.format("%.2f", realSum.toDouble()))
                        bill_back.setRightString(String.format("%.2f", realSum.toDouble() - businessSum.toDouble()))
                        bill_fee.setRightString(String.format("%.2f", businessSum.toDouble()))
                        bill_time.setRightString(data.getString("payTime"))
                        bill_status.setRightString(when (status) {
                            "1" -> "交易成功"
                            "-2" -> "交易中"
                            else -> "交易失败"
                        })
                    }

                })
    }
}
