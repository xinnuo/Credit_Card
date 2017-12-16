package com.ruanmeng.credit_card

import android.os.Bundle
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.utils.CommonUtil
import kotlinx.android.synthetic.main.activity_real.*

class RealActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_real)
        init_title("实名信息")
    }

    override fun init_title() {
        super.init_title()
        real_name.text = CommonUtil.nameReplaceWithStar(getString("real_name"))
        real_gender.text = when (getString("real_sex")) {
            "0" -> "女"
            else -> "男"
        }
        real_num.text = CommonUtil.idCardReplaceWithStar(getString("real_IDCard"))
    }
}
