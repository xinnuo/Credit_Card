package com.ruanmeng.credit_card

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.lzy.extend.StringDialogCallback
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.lzy.okgo.utils.OkLogger
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.CommonModel
import com.ruanmeng.model.RefreshMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.BankcardHelper
import com.ruanmeng.utils.CommonUtil
import com.ruanmeng.utils.DialogHelper
import com.weigan.loopview.LoopView
import kotlinx.android.synthetic.main.activity_credit_card.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.json.JSONObject
import java.util.ArrayList

class CreditCardActivity : BaseActivity() {

    private var time_count: Int = 180
    private lateinit var thread: Runnable
    private var YZM: String = ""
    private var mTel: String = ""

    private var list_province = ArrayList<CommonData>()
    private var list_city = ArrayList<CommonData>()
    private var bankProvince = ""
    private var bankCity = ""

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
        et_bank.addTextChangedListener(this@CreditCardActivity)
        card_city.addTextChangedListener(this@CreditCardActivity)

        credit_name.text = getString("real_name")
        credit_num.text = CommonUtil.idCardReplaceWithStar(getString("real_IDCard"))
    }

    @SuppressLint("SetTextI18n")
    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.card_bank_ll -> {
                val intent = Intent(baseContext, BankSelectActivity::class.java)
                intent.putExtra("title", "信用卡")
                startActivity(intent)
            }
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
                        time_count = 180
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
                                time_count = 180
                                bt_yzm.post(thread)
                            }

                        })
            }
            R.id.card_city_ll -> {
                showLoadingDialog()

                getProvince(object : ResultCallBack {

                    override fun doWork() {
                        cancelLoadingDialog()

                        DialogHelper.showAddressDialog(
                                baseContext,
                                list_province,
                                list_city,
                                object : DialogHelper.AddressCallBack {

                                    override fun doWork(pos_province: Int, pos_city: Int) {
                                        bankProvince = list_province[pos_province].areaName
                                        bankCity = list_city[pos_city].areaName

                                        if (bankCity.contains(bankProvince)) card_city.text = bankCity
                                        else card_city.text = bankProvince + bankCity
                                    }

                                    override fun getCities(loopView: LoopView, pos: Int) {
                                        getCity(list_province[pos].areaId, object : ResultCallBack {

                                            override fun doWork() {
                                                val cities = ArrayList<String>()
                                                list_city.mapTo(cities) { it.areaName }

                                                if (cities.size > 0) {
                                                    loopView.visibility = View.VISIBLE
                                                    loopView.setItems(cities)
                                                    loopView.setCurrentPosition(0)
                                                } else loopView.visibility = View.INVISIBLE
                                            }

                                        })
                                    }

                                })
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
                        .params("bankSubName", et_bank.text.trim().toString())
                        .params("bankProvince", bankProvince)
                        .params("bankCity", bankCity)
                        .params("quota", et_limit.text.trim().toString())
                        .params("billDay", et_bill.text.trim().toString())
                        .params("repaymentDay", et_pay.text.trim().toString())
                        .params("tel", mTel)
                        .params("smsCode", YZM)
                        .execute(object : StringDialogCallback(baseContext) {
                            /*{
                                "msg": "收款成功",
                                "msgcode": 100,
                                "object": "https://shouyin.yeepay.com/nc-cashier-wap/wap/request/10016127996/21pDrF18M9579RwWZbqnJQ%3D%3D"
                            }*/
                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                OkLogger.i(msg)

                                val obj = JSONObject(response.body())

                                val intent = Intent(baseContext, WebActivity::class.java)
                                intent.putExtra("title", "支付验证")
                                intent.putExtra("url", obj.getString("object"))
                                startActivity(intent)
                            }

                        })
            }
        }
    }

    private fun getProvince(callback: ResultCallBack) {
        OkGo.post<CommonModel>(BaseHttp.city1_data)
                .tag(this@CreditCardActivity)
                .execute(object : JacksonDialogCallback<CommonModel>(baseContext, CommonModel::class.java) {

                    override fun onSuccess(response: Response<CommonModel>) {
                        list_province.apply {
                            clear()
                            addItems(response.body().areas)
                        }
                    }

                    override fun onFinish() {
                        if (list_province.size > 0) getCity(list_province[0].areaId, callback)
                    }

                })
    }

    private fun getCity(id: String, callback: ResultCallBack) {
        OkGo.post<CommonModel>(BaseHttp.city2_data)
                .tag(this@CreditCardActivity)
                .params("areaId", id)
                .execute(object : JacksonDialogCallback<CommonModel>(baseContext, CommonModel::class.java) {

                    override fun onSuccess(response: Response<CommonModel>) {
                        list_city.apply {
                            clear()
                            addItems(response.body().areas)
                        }
                    }

                    override fun onFinish() {
                        callback.doWork()
                    }

                })
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
                && et_yzm.text.isNotBlank()
                && et_bank.text.isNotBlank()
                && card_city.text.isNotBlank()) {
            card_sure.setBackgroundResource(R.drawable.rec_bg_blue)
            card_sure.isClickable = true
        } else {
            card_sure.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
            card_sure.isClickable = false
        }
    }

    override fun finish() {
        EventBus.getDefault().unregister(this@CreditCardActivity)
        super.finish()
    }

    @Subscribe
    fun onMessageEvent(event: RefreshMessageEvent) {
        when (event.name) {
            "选择银行" -> card_bank.text = event.id
        }
    }

    interface ResultCallBack {
        fun doWork()
    }
}
