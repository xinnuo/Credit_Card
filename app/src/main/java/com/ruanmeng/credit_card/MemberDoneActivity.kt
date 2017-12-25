package com.ruanmeng.credit_card

import android.annotation.SuppressLint
import android.os.Bundle
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.utils.ActivityStack
import kotlinx.android.synthetic.main.activity_member_done.*
import kotlinx.android.synthetic.main.layout_title_left.*

class MemberDoneActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member_done)
        setToolbarVisibility(false)
        init_title()
    }

    @SuppressLint("SetTextI18n")
    override fun init_title() {
        left_nav_title.text = "升级会员"
        member_hint.text = "您已成功升级至${intent.getStringExtra("levelName")}"

        member_done.setOnClickListener {
            ActivityStack.getScreenManager().popActivities(this@MemberDoneActivity::class.java)
        }
    }
}
