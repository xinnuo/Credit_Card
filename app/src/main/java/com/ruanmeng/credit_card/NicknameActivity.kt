package com.ruanmeng.credit_card

import android.net.Uri
import android.os.Bundle
import android.text.InputFilter
import android.text.TextUtils
import android.view.View
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.base.putString
import com.ruanmeng.base.toast
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.NameLengthFilter
import io.rong.imkit.RongIM
import io.rong.imlib.model.UserInfo
import kotlinx.android.synthetic.main.activity_nickname.*

class NicknameActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nickname)
        init_title("修改名称")
    }

    override fun init_title() {
        super.init_title()
        bt_ok.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
        bt_ok.isClickable = false

        et_name.filters = arrayOf<InputFilter>(NameLengthFilter(12))
        et_name.addTextChangedListener(this@NicknameActivity)

        if (getString("nickName").isNotEmpty()) {
            et_name.setText(getString("nickName"))
            et_name.setSelection(et_name.text.length)
        }
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when(v.id) {
            R.id.bt_ok -> {
                if (et_name.text.trim().toString() == getString("nickName")) {
                    toast("未做任何修改")
                    return
                }

                OkGo.post<String>(BaseHttp.nickName_change_sub)
                        .tag(this@NicknameActivity)
                        .isMultipart(true)
                        .headers("token", getString("token"))
                        .params("nickName", et_name.text.trim().toString())
                        .execute(object : StringDialogCallback(baseContext) {
                            /*{
                                "msg": "更新成功",
                                "msgcode": 100
                            }*/
                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                toast(msg)
                                putString("nickName", et_name.text.toString())

                                RongIM.getInstance().refreshUserInfoCache(UserInfo(
                                        getString("token"),
                                        getString("nickName"),
                                        Uri.parse(BaseHttp.baseImg + getString("userhead"))))

                                ActivityStack.getScreenManager().popActivities(this@NicknameActivity::class.java)
                            }

                        })
            }
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (!TextUtils.isEmpty(et_name.text.toString())) {
            bt_ok.setBackgroundResource(R.drawable.rec_bg_blue)
            bt_ok.isClickable = true
        } else {
            bt_ok.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
            bt_ok.isClickable = false
        }
    }
}
