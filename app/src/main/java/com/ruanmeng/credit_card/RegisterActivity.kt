package com.ruanmeng.credit_card

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import com.lzy.extend.StringDialogCallback
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.CommonModel
import com.ruanmeng.model.RefreshMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.CommonUtil
import com.ruanmeng.utils.DialogHelper
import com.weigan.loopview.LoopView
import kotlinx.android.synthetic.main.activity_register.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.json.JSONObject
import java.util.ArrayList

class RegisterActivity : BaseActivity() {

    private var list_province = ArrayList<CommonData>()
    private var list_city = ArrayList<CommonData>()
    private var list_district = ArrayList<CommonData>()

    private var address = ""
    private var province = ""
    private var city = ""
    private var district = ""

    private var time_count: Int = 180
    private lateinit var thread: Runnable
    private var YZM: String = ""
    private var mTel: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        init_title("注册")

        EventBus.getDefault().register(this@RegisterActivity)
    }

    override fun init_title() {
        super.init_title()
        bt_sign.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
        bt_sign.isClickable = false

        et_name.addTextChangedListener(this@RegisterActivity)
        et_yzm.addTextChangedListener(this@RegisterActivity)
        et_pwd.addTextChangedListener(this@RegisterActivity)
        register_city.addTextChangedListener(this@RegisterActivity)
        register_agree.setOnCheckedChangeListener(this@RegisterActivity)
    }

    @SuppressLint("SetTextI18n")
    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.register_xieyi -> {
                val intent = Intent(baseContext, WebActivity::class.java)
                intent.putExtra("title", "服务协议")
                startActivity(intent)
            }
            R.id.register_qrcode -> startActivity(ScanActivity::class.java)
            R.id.bt_yzm -> {
                if (et_name.text.isBlank()) {
                    et_name.requestFocus()
                    toast("请输入手机号")
                    return
                }

                if (!CommonUtil.isMobileNumber(et_name.text.toString())) {
                    et_name.requestFocus()
                    et_name.setText("")
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

                OkGo.post<String>(BaseHttp.identify_get)
                        .tag(this@RegisterActivity)
                        .params("mobile", et_name.text.trim().toString())
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                YZM = JSONObject(response.body()).getString("object")
                                mTel = et_name.text.trim().toString()
                                if (BuildConfig.LOG_DEBUG) et_yzm.setText(YZM)

                                bt_yzm.isClickable = false
                                bt_yzm.setBackgroundResource(R.drawable.rec_bg_ova_d0d0d0)
                                time_count = 180
                                bt_yzm.post(thread)
                            }

                        })
            }
            R.id.register_city_ll -> {
                showLoadingDialog()

                getProvince(object : ResultCallBack {

                    override fun doWork() {
                        cancelLoadingDialog()

                        DialogHelper.showAddressDialog(
                                baseContext,
                                list_province,
                                list_city,
                                list_district,
                                object : DialogHelper.AreaCallBack {

                                    override fun doWork(pos_province: Int, pos_city: Int, pos_district: Int) {
                                        address = when {
                                            list_district.size > 0 -> list_province[pos_province].areaName + list_city[pos_city].areaName + list_district[pos_district].areaName
                                            list_city.size > 0 -> list_province[pos_province].areaName + list_city[pos_city].areaName
                                            else -> list_province[pos_province].areaName
                                        }

                                        province = list_province[pos_province].areaName
                                        city = if (list_city.isEmpty()) "" else list_city[pos_city].areaName
                                        district = if (list_district.isEmpty()) "" else list_district[pos_district].areaName

                                        register_city.text = address
                                    }

                                    override fun getCities(loopView: LoopView, loopView2: LoopView, pos: Int) {
                                        getCity(list_province[pos].areaId, object : ResultCallBack {

                                            override fun doWork() {
                                                val cities = ArrayList<String>()
                                                val districts = ArrayList<String>()

                                                list_city.mapTo(cities) { it.areaName }
                                                list_district.mapTo(districts) { it.areaName }

                                                if (cities.size > 0) loopView.setItems(cities)
                                                if (districts.size > 0) {
                                                    loopView2.visibility = View.VISIBLE
                                                    loopView2.setItems(districts)
                                                } else loopView2.visibility = View.INVISIBLE
                                            }

                                        })
                                    }

                                    override fun getDistricts(loopView: LoopView, pos: Int) {
                                        getDistrict(list_city[pos].areaId, object : ResultCallBack {

                                            override fun doWork() {
                                                val districts = ArrayList<String>()
                                                list_district.mapTo(districts) { it.areaName }

                                                if (districts.size > 0) {
                                                    loopView.visibility = View.VISIBLE
                                                    loopView.setItems(districts)
                                                } else loopView.visibility = View.INVISIBLE
                                            }

                                        })
                                    }

                                })
                    }
                })
            }
            R.id.bt_sign -> {
                if (!CommonUtil.isMobileNumber(et_name.text.toString())) {
                    et_name.requestFocus()
                    et_name.setText("")
                    toast("手机号码格式错误，请重新输入")
                    return
                }

                if (et_name.text.toString() != mTel) {
                    toast("手机号码不匹配，请重新获取验证码")
                    return
                }

                if (et_yzm.text.trim().toString() != YZM) {
                    et_yzm.requestFocus()
                    et_yzm.setText("")
                    toast("验证码错误，请重新输入")
                    return
                }

                if (et_pwd.text.length < 6) {
                    toast("新密码长度不少于6位")
                    return
                }

                OkGo.post<String>(BaseHttp.register_sub)
                        .tag(this@RegisterActivity)
                        .params("mobile", et_name.text.toString())
                        .params("smscode", et_yzm.text.toString())
                        .params("password", et_pwd.text.toString())
                        .params("toRecommend", et_code.text.toString())
                        .params("loginType", "mobile")
                        .params("userProvince", province)
                        .params("userCity", city)
                        .params("userDistrict", district)
                        .execute(object : StringDialogCallback(baseContext) {

                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                toast(msg)
                                ActivityStack.getScreenManager().popActivities(this@RegisterActivity::class.java)
                            }

                        })
            }
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (et_name.text.isNotBlank()
                && et_yzm.text.isNotBlank()
                && et_pwd.text.isNotBlank()
                && register_city.text.isNotBlank()
                && register_agree.isChecked) {
            bt_sign.setBackgroundResource(R.drawable.rec_bg_blue)
            bt_sign.isClickable = true
        } else {
            bt_sign.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
            bt_sign.isClickable = false
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (isChecked
                && et_name.text.isNotBlank()
                && et_yzm.text.isNotBlank()
                && et_pwd.text.isNotBlank()
                && register_city.text.isNotBlank()) {
            bt_sign.setBackgroundResource(R.drawable.rec_bg_blue)
            bt_sign.isClickable = true
        } else {
            bt_sign.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
            bt_sign.isClickable = false
        }
    }

    override fun finish() {
        super.finish()
        EventBus.getDefault().unregister(this@RegisterActivity)
    }

    @Subscribe
    fun onMessageEvent(event: RefreshMessageEvent) {
        when (event.name) {
            "扫描二维码" -> {
                val code = event.id.substring(event.id.length - 11)
                if (CommonUtil.isMobileNumber(code)) {
                    et_code.setText(code)
                    et_code.setSelection(et_code.text.length)
                }
            }
        }
    }

    private fun getProvince(callback: ResultCallBack) {
        OkGo.post<CommonModel>(BaseHttp.city1_data)
                .tag(this@RegisterActivity)
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
                .tag(this@RegisterActivity)
                .params("areaId", id)
                .execute(object : JacksonDialogCallback<CommonModel>(baseContext, CommonModel::class.java) {

                    override fun onSuccess(response: Response<CommonModel>) {
                        list_city.apply {
                            clear()
                            addItems(response.body().areas)
                        }
                    }

                    override fun onFinish() {
                        if (list_city.size > 0) getDistrict(list_city[0].areaId, callback)
                    }

                })
    }

    private fun getDistrict(id: String, callback: ResultCallBack) {
        OkGo.post<CommonModel>(BaseHttp.area_data)
                .tag(this@RegisterActivity)
                .params("areaId", id)
                .execute(object : JacksonDialogCallback<CommonModel>(baseContext, CommonModel::class.java) {

                    override fun onSuccess(response: Response<CommonModel>) {
                        list_district.apply {
                            clear()
                            addItems(response.body().rows)
                        }
                    }

                    override fun onFinish() {
                        callback.doWork()
                    }
                })
    }

    interface ResultCallBack {
        fun doWork()
    }
}
