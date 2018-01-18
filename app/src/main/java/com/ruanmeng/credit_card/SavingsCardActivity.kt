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
import kotlinx.android.synthetic.main.activity_savings_card.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.json.JSONObject
import java.util.*

class SavingsCardActivity : BaseActivity() {

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
        setContentView(R.layout.activity_savings_card)
        init_title(if (intent.getBooleanExtra("isChanged", false)) "更换银行卡" else "新增储蓄卡")

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
        et_bank.addTextChangedListener(this@SavingsCardActivity)
        card_city.addTextChangedListener(this@SavingsCardActivity)

        saving_name.text = getString("real_name")
        saving_num.text = CommonUtil.idCardReplaceWithStar(getString("real_IDCard"))
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
                        time_count = 180
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
                    toast("请输入正确的储蓄卡卡号")
                    return
                }

                /*if (BankCardUtil(et_card.rawText).bankName != card_bank.text.toString()) {
                    toast("卡号与所属银行不符，请选择正确的所属银行")
                    return
                }*/

                if (!CommonUtil.isMobileNumber(et_phone.text.trim().toString())) {
                    toast("请输入正确的手机号码")
                    return
                }

                /*if (et_phone.text.trim().toString() != mTel) {
                    toast("手机号码不匹配，请重新获取验证码")
                    return
                }*/

                /*if (et_yzm.text.trim().toString() != YZM) {
                    toast("验证码错误，请重新输入")
                    return
                }*/

                OkGo.post<String>(BaseHttp.depositcard_add_sub)
                        .tag(this@SavingsCardActivity)
                        .isMultipart(true)
                        .headers("token", getString("token"))
                        .params("name", getString("real_name"))
                        .params("identityCard", getString("real_IDCard"))
                        .params("depositcard", et_card.rawText)
                        .params("bank", card_bank.text.toString())
                        .params("bankSubName", et_bank.text.trim().toString())
                        .params("bankProvince", bankProvince)
                        .params("bankCity", bankCity)
                        .params("tel", et_phone.text.trim().toString())
                        .params("type", if (intent.getBooleanExtra("isChanged", false)) 1 else 0)
                        .execute(object : StringDialogCallback(baseContext) {
                            /*{
                                "msg": "储蓄卡添加成功",
                                "msgcode": 100
                            }*/
                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                OkLogger.i(msg)

                                if (intent.getBooleanExtra("isChanged", false)) {
                                    EventBus.getDefault().post(RefreshMessageEvent("更换银行卡"))

                                    val intent = Intent(baseContext, BankDoneActivity::class.java)
                                    intent.putExtra("title", "更换银行卡")
                                    intent.putExtra("hint", "更换银行卡成功！")
                                    startActivity(intent)
                                } else {
                                    EventBus.getDefault().post(RefreshMessageEvent("新增储蓄卡"))

                                    val intent = Intent(baseContext, BankDoneActivity::class.java)
                                    intent.putExtra("title", "新增储蓄卡")
                                    intent.putExtra("hint", "新增储蓄卡成功！")
                                    startActivity(intent)
                                }
                            }

                        })
            }
        }
    }

    private fun getProvince(callback: ResultCallBack) {
        OkGo.post<CommonModel>(BaseHttp.city1_data)
                .tag(this@SavingsCardActivity)
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
                .tag(this@SavingsCardActivity)
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
        if (saving_name.text.isNotBlank()
                && saving_num.text.isNotBlank()
                && et_card.text.isNotBlank()
                && card_bank.text.isNotBlank()
                && et_phone.text.isNotBlank()
                && card_city.text.isNotBlank()
                && et_bank.text.isNotBlank()) {
            card_sure.setBackgroundResource(R.drawable.rec_bg_blue)
            card_sure.isClickable = true
        } else {
            card_sure.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
            card_sure.isClickable = false
        }
    }

    override fun finish() {
        EventBus.getDefault().unregister(this@SavingsCardActivity)
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
