package com.ruanmeng.credit_card

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.adapter.TabFragmentAdapter
import com.ruanmeng.base.*
import com.ruanmeng.fragment.AgencyFragment
import com.ruanmeng.fragment.AgencySecondFragment
import com.ruanmeng.model.RefreshMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.Tools
import kotlinx.android.synthetic.main.activity_agency.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.json.JSONObject

class AgencyActivity : BaseActivity() {

    private var withdrawSum = ""
    private var levelName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agency)
        init_title("代理商")

        EventBus.getDefault().register(this@AgencyActivity)

        getData()
    }

    override fun init_title() {
        super.init_title()
        val fragments = ArrayList<Fragment>()
        val titles = listOf("收益列表", "我的下级")

        fragments.add(AgencyFragment())
        fragments.add(AgencySecondFragment())

        agency_tab.apply {
            post { Tools.setIndicator(this, 50, 50) }
            removeAllTabs()
        }
        agency_pager.adapter = TabFragmentAdapter(supportFragmentManager, titles, fragments)
        // 为TabLayout设置ViewPager
        agency_tab.setupWithViewPager(agency_pager)

        agency_cash.setOnClickListener {
            if (withdrawSum.isNotEmpty()) startActivity(CashoutActivity::class.java)
        }
    }

    override fun getData() {
        OkGo.post<String>(BaseHttp.agent_userInfo)
                .tag(this@AgencyActivity)
                .headers("token", getString("token"))
                .execute(object : StringDialogCallback(baseContext) {

                    @SuppressLint("SetTextI18n")
                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                        val data = JSONObject(response.body())
                        val obj = data.getJSONObject("agentInfo")

                        levelName = data.getString("levelName")
                        withdrawSum = obj.getString("currentSum")
                        putString("withdrawSum", withdrawSum)
                        putString("levelName", levelName)

                        agency_name.text = obj.getString("nickName") + " ($levelName)"
                        agency_total.text = String.format("%.2f", obj.getString("profitSum").toDouble())
                        agency_current.text = String.format("%.2f", obj.getString("currentSum").toDouble())

                        GlideApp.with(baseContext)
                                .load(BaseHttp.baseImg + obj.getString("userHead"))
                                .placeholder(R.mipmap.ic_launcher_round)
                                .error(R.mipmap.ic_launcher_round)
                                .dontAnimate()
                                .into(agency_img)
                    }

                })
    }

    override fun finish() {
        EventBus.getDefault().unregister(this@AgencyActivity)
        super.finish()
    }

    @Subscribe
    fun onMessageEvent(event: RefreshMessageEvent) {
        when (event.name) {
            "提现" -> getData()
        }
    }
}
