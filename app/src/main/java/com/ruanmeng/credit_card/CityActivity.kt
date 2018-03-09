package com.ruanmeng.credit_card

import android.os.Bundle
import com.lzy.extend.StringDialogCallback
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.addItems
import com.ruanmeng.base.getString
import com.ruanmeng.base.toast
import com.ruanmeng.fragment.CityFirstFragment
import com.ruanmeng.fragment.CitySecondFragment
import com.ruanmeng.fragment.CityThirdFragment
import com.ruanmeng.fragment.OnFragmentItemSelectListener
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.CommonModel
import com.ruanmeng.model.CountMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import org.greenrobot.eventbus.EventBus
import java.util.ArrayList

class CityActivity : BaseActivity(), OnFragmentItemSelectListener {

    private var list_province = ArrayList<CommonData>()
    private var list_city = ArrayList<CommonData>()
    private var list_district = ArrayList<CommonData>()

    private lateinit var first: CityFirstFragment
    private lateinit var second: CitySecondFragment
    private lateinit var third: CityThirdFragment

    private var province = ""
    private var city = ""
    private var district = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city)
        init_title("选择省份")

        getProvince()
    }

    override fun onitemSelected(type: String,
                                id: String,
                                name: String) {
        when (type) {
            "省" -> {
                province = name
                getCity(id)
            }
            "市" -> {
                city = name
                getDistrict(id)
            }
            "区" -> {
                district = name
                getData()
            }
        }
    }

    override fun getData() {
        OkGo.post<String>(BaseHttp.update_user_agentCity)
                .tag(this@CityActivity)
                .isMultipart(true)
                .headers("token", getString("token"))
                .params("userProvince", province)
                .params("userCity", city)
                .params("userDistrict", district)
                .execute(object : StringDialogCallback(baseContext) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        EventBus.getDefault().post(CountMessageEvent("选择城市", city))
                        ActivityStack.getScreenManager().popActivities(this@CityActivity::class.java)
                    }

                })
    }

    private fun getProvince() {
        OkGo.post<CommonModel>(BaseHttp.city1_data)
                .tag(this@CityActivity)
                .execute(object : JacksonDialogCallback<CommonModel>(baseContext, CommonModel::class.java, true) {

                    override fun onSuccess(response: Response<CommonModel>) {
                        list_province.apply {
                            clear()
                            addItems(response.body().areas)
                        }

                        if (list_province.isNotEmpty()) {

                            first = CityFirstFragment()
                            first.arguments = Bundle().apply { putSerializable("list", list_province) }

                            supportFragmentManager
                                    .beginTransaction()
                                    .add(R.id.city_container, first)
                                    .commit()
                        }
                    }

                })
    }

    private fun getCity(id: String) {
        OkGo.post<CommonModel>(BaseHttp.city2_data)
                .tag(this@CityActivity)
                .params("areaId", id)
                .execute(object : JacksonDialogCallback<CommonModel>(baseContext, CommonModel::class.java, true) {

                    override fun onSuccess(response: Response<CommonModel>) {
                        list_city.apply {
                            clear()
                            addItems(response.body().areas)
                        }

                        if (list_city.isNotEmpty()) {
                            tvTitle.text = "选择城市"
                            second = CitySecondFragment()
                            second.arguments = Bundle().apply { putSerializable("list", list_city) }

                            supportFragmentManager.beginTransaction()
                                    .setCustomAnimations(
                                            R.anim.push_left_in,
                                            R.anim.push_left_out,
                                            R.anim.push_right_in,
                                            R.anim.push_right_out)
                                    .add(R.id.city_container, second)
                                    .addToBackStack(null)
                                    .commit()
                        }
                    }

                })
    }

    private fun getDistrict(id: String) {
        OkGo.post<CommonModel>(BaseHttp.area_data)
                .tag(this@CityActivity)
                .params("areaId", id)
                .execute(object : JacksonDialogCallback<CommonModel>(baseContext, CommonModel::class.java, true) {

                    override fun onSuccess(response: Response<CommonModel>) {
                        list_district.apply {
                            clear()
                            addItems(response.body().rows)
                        }

                        if (list_district.isNotEmpty()) {
                            tvTitle.text = "选择区域"
                            third = CityThirdFragment()
                            third.arguments = Bundle().apply { putSerializable("list", list_district) }

                            supportFragmentManager.beginTransaction()
                                    .setCustomAnimations(
                                            R.anim.push_left_in,
                                            R.anim.push_left_out,
                                            R.anim.push_right_in,
                                            R.anim.push_right_out)
                                    .add(R.id.city_container, third)
                                    .addToBackStack(null)
                                    .commit()
                        } else getData()
                    }

                })
    }
}
