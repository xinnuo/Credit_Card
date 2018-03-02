package com.ruanmeng.credit_card

import android.os.Bundle
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.Tools
import kotlinx.android.synthetic.main.activity_credit_code.*

class CreditCodeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credit_code)
        init_title("授权登录")

        getData()
    }

    override fun init_title() {
        super.init_title()
        val json = intent.getStringExtra("qrcode")
        credit_qrcode.setImageBitmap(Tools.strToBitmap(json))
    }

    override fun getData() {
        OkGo.post<String>(BaseHttp.get_bill_qqcredit)
                .tag(this@CreditCodeActivity)
                .headers("token", getString("token"))
                .params("btoken", intent.getStringExtra("token"))
                .params("cardId", intent.getStringExtra("cardId"))
                .execute(object : StringDialogCallback(baseContext) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                    }

                })
    }
}
