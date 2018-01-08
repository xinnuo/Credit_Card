package com.ruanmeng.credit_card

import android.os.Bundle
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.CommonModel
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.CommonUtil
import kotlinx.android.synthetic.main.activity_real.*
import java.util.ArrayList

class RealActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_real)
        init_title("实名信息")

        getData()
    }

    override fun init_title() {
        super.init_title()
        real_name.text = getString("real_name")
        real_gender.text = when (getString("real_sex")) {
            "0" -> "女"
            else -> "男"
        }
        real_num.text = CommonUtil.idCardReplaceWithStar(getString("real_IDCard"))
    }

    override fun getData() {
        OkGo.post<CommonModel>(BaseHttp.depositcard_data)
                .tag(this@RealActivity)
                .headers("token", getString("token"))
                .execute(object : JacksonDialogCallback<CommonModel>(baseContext, CommonModel::class.java) {

                    override fun onSuccess(response: Response<CommonModel>) {

                        val list = ArrayList<CommonData>()
                        list.addItems(response.body().depositcards)

                        if (list.isEmpty()) {
                            dialog("温馨提示", "您还未绑定储蓄卡，是否现在去添加储蓄卡？") {
                                positiveButton("去添加") { startActivity(BankcardActivity::class.java) }
                                show()
                            }
                        }
                    }

                })
    }
}
