package com.ruanmeng.credit_card

import android.os.Bundle
import android.text.InputFilter
import android.view.View
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.base.toast
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.NameLengthFilter
import kotlinx.android.synthetic.main.activity_feedback.*

class FeedbackActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)
        init_title("意见反馈")
    }

    override fun init_title() {
        super.init_title()
        feedback_submit.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
        feedback_submit.isClickable = false

        feedback_content.filters = arrayOf<InputFilter>(NameLengthFilter(300))
        feedback_content.addTextChangedListener(this@FeedbackActivity)
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when(v.id) {
            R.id.feedback_submit ->
                OkGo.post<String>(BaseHttp.consult_sub)
                        .tag(this@FeedbackActivity)
                        .isMultipart(true)
                        .headers("token", getString("token"))
                        .params("content", feedback_content.text.trim().toString())
                        .execute(object : StringDialogCallback(baseContext) {
                            /*{
                                "msg": "用户问题反馈添加成功",
                                "msgcode": 100
                            }*/
                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                toast(msg)
                                ActivityStack.getScreenManager().popActivities(this@FeedbackActivity::class.java)
                            }

                        })
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (feedback_content.text.isNotBlank()) {
            feedback_submit.setBackgroundResource(R.drawable.rec_bg_blue)
            feedback_submit.isClickable = true
        } else {
            feedback_submit.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
            feedback_submit.isClickable = false
        }
    }
}
