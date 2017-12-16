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
import com.ruanmeng.utils.DialogHelper
import kotlinx.android.synthetic.main.activity_credit_card.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.json.JSONObject

class CreditCardActivity : BaseActivity() {

    private var time_count: Int = 90
    private lateinit var thread: Runnable
    private var YZM: String = ""
    private var mTel: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credit_card)
        init_title("新增信用卡")

        EventBus.getDefault().register(this@CreditCardActivity)
    }

    override fun init_title() {
        super.init_title()
        card_sure.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
        card_sure.isClickable = false

        credit_name.addTextChangedListener(this@CreditCardActivity)
        credit_num.addTextChangedListener(this@CreditCardActivity)
        et_card.addTextChangedListener(this@CreditCardActivity)
        et_back.addTextChangedListener(this@CreditCardActivity)
        et_date.addTextChangedListener(this@CreditCardActivity)
        card_bank.addTextChangedListener(this@CreditCardActivity)
        et_limit.addTextChangedListener(this@CreditCardActivity)
        et_bill.addTextChangedListener(this@CreditCardActivity)
        et_pay.addTextChangedListener(this@CreditCardActivity)
        et_phone.addTextChangedListener(this@CreditCardActivity)
        et_yzm.addTextChangedListener(this@CreditCardActivity)

        credit_name.text = getString("real_name")
        credit_num.text = getString("real_IDCard")
    }

    @SuppressLint("SetTextI18n")
    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.card_bank_ll -> startActivity(BankSelectActivity::class.java)
            R.id.back_hint -> {
                DialogHelper.showDialog(
                        baseContext,
                        "温馨提示",
                        "Card Vеrification Number，卡确认码/安全码。这是银行卡用于非直接场合，如网络支付等下交易使用的。可以识别银行卡交易的在场性。一般可见于银行卡背面的签名条一串数列的末三位。对于银联组织的银联标准卡使用的称为CVN2，万事达卡称为CVC2，VISA卡使用的称为CVV2，AE运通卡则称为CSC2。签名栏你要刮开涂层",
                        "确定")
            }
            R.id.date_hint -> {
                DialogHelper.showDialog(
                        baseContext,
                        "温馨提示",
                        "信用卡的有效期以 月月/年年 表示 10/12 有效为2012年10月31日到期2.银行信用卡有效期一般为3年 个别银行为5年3.有效期到期后 银行将根据以往客户使用信用卡的情况 决定是否让客户继续使用信用卡",
                        "确定")
            }
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
                        .tag(this@CreditCardActivity)
                        .headers("token", getString("token"))
                        .params("mobile", et_phone.text.trim().toString())
                        .params("type", 3)
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
                    toast("请输入正确的信用卡卡号")
                    return
                }

                if (et_back.text.trim().length < 3) {
                    toast("请输入正确的CVN2码（三位数字）")
                    return
                }

                if (et_date.text.trim().length < 5) {
                    toast("请输入正确的有效期（格式：月月/年年）")
                    return
                }

                val day_bill = et_bill.text.trim().toString().toInt()
                if (day_bill < 0 || day_bill > 31) {
                    toast("请输入正确的账单日（两位数字）")
                    return
                }

                val day_pay = et_pay.text.trim().toString().toInt()
                if (day_pay < 0 || day_pay > 31) {
                    toast("请输入正确的还款日（两位数字）")
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

                OkGo.post<String>(BaseHttp.creditcard_add_sub)
                        .tag(this@CreditCardActivity)
                        .isMultipart(true)
                        .headers("token", getString("token"))
                        .params("name", getString("real_name"))
                        .params("identityCard", getString("real_IDCard"))
                        .params("creditcard", et_card.rawText)
                        .params("cvn2", et_back.text.trim().toString())
                        .params("invalidDate", et_date.text.trim().toString())
                        .params("bank", card_bank.text.toString())
                        .params("quota", et_limit.text.trim().toString())
                        .params("billDay", et_bill.text.trim().toString())
                        .params("repaymentDay", et_pay.text.trim().toString())
                        .params("tel", mTel)
                        .params("smsCode", YZM)
                        .execute(object : StringDialogCallback(baseContext) {
                            /*{
                                "msg": "提交成功，请等待审核",
                                "msgcode": 100
                            }*/
                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                toast(msg)

                                EventBus.getDefault().post(RefreshMessageEvent("新增信用卡"))
                                ActivityStack.getScreenManager().popActivities(this@CreditCardActivity::class.java)
                            }

                        })
            }
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (credit_name.text.isNotBlank()
                && credit_num.text.isNotBlank()
                && et_card.text.isNotBlank()
                && et_back.text.isNotBlank()
                && et_date.text.isNotBlank()
                && card_bank.text.isNotBlank()
                && et_limit.text.isNotBlank()
                && et_bill.text.isNotBlank()
                && et_pay.text.isNotBlank()
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
        EventBus.getDefault().unregister(this@CreditCardActivity)
        super.onBackPressed()
    }

    @Subscribe
    fun onMessageEvent(event: RefreshMessageEvent) {
        when (event.name) {
            "选择银行" -> card_bank.text = if (event.id == "平安银行") "深圳发展银行" else event.id
        }
    }
}
