package com.ruanmeng.credit_card

import android.annotation.SuppressLint
import android.os.Bundle
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.GlideApp
import com.ruanmeng.base.getString
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.Tools
import kotlinx.android.synthetic.main.activity_share.*
import org.json.JSONObject

class ShareActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)
        init_title("分享赚钱")

        getData()
    }

    @SuppressLint("SetTextI18n")
    override fun init_title() {
        super.init_title()

        share_hint.text = "我是${getString("nickName")}"
        GlideApp.with(baseContext)
                .load(BaseHttp.baseImg + getString("userhead"))
                .placeholder(R.mipmap.default_user)
                .error(R.mipmap.default_user)
                .dontAnimate()
                .into(share_img)
    }

    override fun getData() {
        OkGo.post<String>(BaseHttp.get_userQrcode)
                .tag(this@ShareActivity)
                .headers("token", getString("token"))
                .execute(object : StringDialogCallback(baseContext) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        val qrcode = JSONObject(response.body()).getString("qrcode")
                        share_qrcode.setImageBitmap(Tools.strToBitmap(qrcode))
                    }

                })
    }
}
