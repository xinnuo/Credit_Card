package com.ruanmeng.credit_card

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.base.startActivity
import com.ruanmeng.base.toast
import com.ruanmeng.model.RefreshMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.BankcardHelper
import com.ruanmeng.utils.CommonUtil
import kotlinx.android.synthetic.main.activity_savings_card.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.json.JSONObject

class SavingsCardActivity : BaseActivity() {

    private var time_count: Int = 90
    private lateinit var thread: Runnable
    private var YZM: String = ""
    private var mTel: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_savings_card)
        init_title("新增储蓄卡")

        EventBus.getDefault().register(this@SavingsCardActivity)
    }

    override fun init_title() {
        super.init_title()
        card_sure.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
        card_sure.isClickable = false

        saving_name.addTextChangedListener(this@SavingsCardActivity)
        saving_num.addTextChangedListener(this@SavingsCardActivity)
        et_card.addTextChangedListener(this@SavingsCardActivity)
        card_bank.addTextChangedListener(this@SavingsCardActivity)
        et_phone.addTextChangedListener(this@SavingsCardActivity)
        et_yzm.addTextChangedListener(this@SavingsCardActivity)

        saving_name.text = getString("real_name")
        saving_num.text = getString("real_IDCard")
    }

    @SuppressLint("SetTextI18n")
    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.card_bank_ll -> startActivity(BankSelectActivity::class.java)
            R.id.bt_yzm -> {
                if (et_phone.text.isBlank()) {
                    et_phone.requestFocus()
                    toast("请输入预留手机号")
                    return
                }

                if (!CommonUtil.isMobileNumber(et_phone.text.toString())) {
                    et_phone.requestFocus()
                    et_phone.setText("")
                    toast("手机号码格式错误，请重新输入")
                    return
                }

                thread = Runnable {
                    bt_yzm.text = "${time_count}秒后重发"
                    if (time_count > 0) {
                        bt_yzm.postDelayed(thread, 1000)
                        time_count--
                    } else {
                        bt_yzm.text = "发送验证码"
                        bt_yzm.isClickable = true
                        bt_yzm.setBackgroundResource(R.drawable.rec_bg_ova_orange)
                        time_count = 90
                    }
                }

                OkGo.post<String>(BaseHttp.identify_othercode)
                        .tag(this@SavingsCardActivity)
                        .headers("token", getString("token"))
                        .params("mobile", et_phone.text.trim().toString())
                        .params("type", 2)
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                YZM = JSONObject(response.body()).getString("object")
                                mTel = et_phone.text.trim().toString()
                                if (BuildConfig.LOG_DEBUG) et_yzm.setText(YZM)

                                bt_yzm.isClickable = false
                                bt_yzm.setBackgroundResource(R.drawable.rec_bg_ova_d0d0d0)
                                time_count = 90
                                bt_yzm.post(thread)
                            }

                        })
            }
            R.id.card_sure -> {
                if (!BankcardHelper.checkBankCard(et_card.rawText)) {
                    toast("请输入正确的储蓄卡卡号")
                    return
                }

                if (!CommonUtil.isMobileNumber(et_phone.text.trim().toString())) {
                    toast("请输入正确的手机号码")
                    return
                }

                if (et_phone.text.trim().toString() != mTel) {
                    toast("手机号码不匹配，请重新获取验证码")
                    return
                }

                if (et_yzm.text.trim().toString() != YZM) {
                    toast("验证码错误，请重新输入")
                    return
                }

                OkGo.post<String>(BaseHttp.depositcard_add_sub)
                        .tag(this@SavingsCardActivity)
                        .isMultipart(true)
                        .headers("token", getString("token"))
                        .params("name", getString("real_name"))
                        .params("identityCard", getString("real_IDCard"))
                        .params("depositcard", et_card.rawText)
                        .params("bank", card_bank.text.toString())
                        .params("tel", mTel)
                        .params("smsCode", YZM)
                        .params("type", 0)
                        .execute(object : StringDialogCallback(baseContext) {
                            /*{
                                "msg": "提交成功，请等待审核",
                                "msgcode": 100
                            }*/
                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                toast(msg)

                                EventBus.getDefault().post(RefreshMessageEvent("新增储蓄卡"))
                                ActivityStack.getScreenManager().popActivities(this@SavingsCardActivity::class.java)
                            }

                        })
            }
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (saving_name.text.isNotBlank()
                && saving_num.text.isNotBlank()
                && et_card.text.isNotBlank()
                && card_bank.text.isNotBlank()
                && et_phone.text.isNotBlank()
                && et_yzm.text.isNotBlank()) {
            card_sure.setBackgroundResource(R.drawable.rec_bg_blue)
            card_sure.isClickable = true
        } else {
            card_sure.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
            card_sure.isClickable = false
        }
    }

    override fun onBackPressed() {
        EventBus.getDefault().unregister(this@SavingsCardActivity)
        super.onBackPressed()
    }

    @Subscribe
    fun onMessageEvent(event: RefreshMessageEvent) {
        when (event.name) {
            "选择银行" -> card_bank.text = if (event.id == "平安银行") "深圳发展银行" else event.id
        }
    }
}
