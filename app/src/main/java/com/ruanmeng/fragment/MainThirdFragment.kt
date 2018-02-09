package com.ruanmeng.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lzy.extend.StringDialogCallback
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.cache.CacheMode
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.credit_card.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.CommonModel
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.NumberHelper
import io.rong.imkit.RongIM
import io.rong.imlib.model.UserInfo
import kotlinx.android.synthetic.main.fragment_main_third.*
import kotlinx.android.synthetic.main.layout_title_main.*
import org.json.JSONObject

class MainThirdFragment : BaseFragment() {

    private val list = ArrayList<CommonData>()

    //调用这个方法切换时不会释放掉Fragment
    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (this.view != null)
            this.view!!.visibility = if (menuVisible) View.VISIBLE else View.GONE
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main_third, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init_title()
    }

    override fun init_title() {
        main_title.text = "个人中心"
        main_right.visibility = View.INVISIBLE

        third_info.setOnClickListener { startActivity(InfoActivity::class.java) }
        third_card.setOnClickListener { startActivity(BankcardActivity::class.java) }
        third_bill.setOnClickListener { startActivity(BillActivity::class.java) }
        third_real.setOnClickListener {
            when (getString("isPass")) {
                "-1" -> {
                    toast("实名认证审核失败，请重新上传")
                    startActivity(RealNameActivity::class.java)
                }
                "0" -> toast("实名认证信息正在审核中")
                "1" -> startActivity(RealActivity::class.java)
                else -> startActivity(RealNameActivity::class.java)
            }
        }
        third_share.setOnClickListener { startActivity(ShareActivity::class.java) }
        third_online.setOnClickListener { getOnlineData() }
        third_setting.setOnClickListener { startActivity(SettingActivity::class.java) }
        third_hotline.setOnClickListener {
            if (getString("platform").isNotEmpty()) {
                AlertDialog.Builder(activity)
                        .setTitle("客服热线")
                        .setMessage("拨打客服热线电话 " + getString("platform"))
                        .setPositiveButton("呼叫") { _, _ ->
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + getString("platform")))
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }
                        .setNegativeButton("取消") { _, _ -> }
                        .create()
                        .show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        getData()
    }

    override fun getData() {
        OkGo.post<String>(BaseHttp.user_msg_data)
                .tag(this@MainThirdFragment)
                .headers("token", getString("token"))
                .cacheMode(CacheMode.REQUEST_FAILED_READ_CACHE)
                .cacheKey(BaseHttp.user_msg_data)
                .execute(object : StringDialogCallback(activity, false) {
                    @SuppressLint("SetTextI18n")
                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        val obj = JSONObject(response.body())
                        putString("balanceSum", obj.getString("balanceSum"))
                        putString("profitSum", obj.getString("profitSum"))
                        putString("levelName", obj.getString("levelName") ?: "")
                        putString("platform", obj.getString("lxwm") ?: "")

                        val data = obj.getJSONObject("userMsg")
                        putString("age", data.getString("age"))
                        putString("mobile", data.getString("mobile"))
                        putString("nickName", data.getString("nickName"))
                        putString("sex", data.getString("sex"))
                        putString("userhead", data.getString("userhead"))

                        third_name.text = getString("nickName")
                        third_phone.text = "（${getString("mobile")}）"
                        third_rank.text = getString("levelName")
                        third_money.text = "￥${NumberHelper.fmtMicrometer(getString("balanceSum"))}"
                        third_hotline.setRightString(getString("platform"))
                        if (third_img.getTag(R.id.third_img) == null) {
                            GlideApp.with(activity)
                                    .load(BaseHttp.baseImg + getString("userhead"))
                                    .placeholder(R.mipmap.default_user)
                                    .error(R.mipmap.default_user)
                                    .dontAnimate()
                                    .into(third_img)

                            third_img.setTag(R.id.third_img, getString("userhead"))
                        } else {
                            if (third_img.getTag(R.id.third_img) != getString("userhead")) {
                                GlideApp.with(activity)
                                        .load(BaseHttp.baseImg + getString("userhead"))
                                        .placeholder(R.mipmap.default_user)
                                        .error(R.mipmap.default_user)
                                        .dontAnimate()
                                        .into(third_img)

                                third_img.setTag(R.id.third_img, getString("userhead"))
                            }
                        }

                        RongIM.getInstance().refreshUserInfoCache(UserInfo(
                                getString("token"),
                                getString("nickName"),
                                Uri.parse(BaseHttp.baseImg + getString("userhead"))))
                    }

                })
    }

    private fun getOnlineData() {
        OkGo.post<CommonModel>(BaseHttp.customer_list)
                .tag(this@MainThirdFragment)
                .execute(object : JacksonDialogCallback<CommonModel>(activity, CommonModel::class.java, true) {

                    override fun onSuccess(response: Response<CommonModel>) {
                        list.clear()
                        list.addItems(response.body().customers)

                        if (list.isNotEmpty()) {
                            if (list.any { it.userInfoId == getString("token") })
                                startActivity(ConversationListActivity::class.java)
                            else {
                                val intent = Intent(activity, OnlineActivity::class.java)
                                intent.putExtra("list", list)
                                startActivity(intent)
                            }
                        }
                    }

                })
    }
 }
