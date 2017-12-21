package com.ruanmeng.credit_card

import android.os.Bundle
import android.support.v4.app.Fragment
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.adapter.TabFragmentAdapter
import com.ruanmeng.base.*
import com.ruanmeng.fragment.AgencyFragment
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.Tools
import kotlinx.android.synthetic.main.activity_agency.*
import org.json.JSONObject

class AgencyActivity : BaseActivity() {

    private var withdrawSum = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agency)
        init_title("代理商")

        getData()
    }

    override fun init_title() {
        super.init_title()
        val fragments = ArrayList<Fragment>()
        val titles = listOf("收益列表", "我的下级")

        fragments.add(AgencyFragment().apply { arguments = Bundle().apply { putString("url", BaseHttp.profit_data) } })
        fragments.add(AgencyFragment().apply { arguments = Bundle().apply { putString("url", BaseHttp.user_children_data) } })

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

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                        val obj = JSONObject(response.body()).getJSONObject("agentInfo")

                        withdrawSum = obj.getString("withdrawSum")
                        putString("withdrawSum", withdrawSum)

                        agency_name.text = obj.getString("nickName")
                        agency_total.text = obj.getString("profitSum")
                        agency_current.text = obj.getString("currentSum")

                        GlideApp.with(baseContext)
                                .load(BaseHttp.baseImg + obj.getString("userHead"))
                                .placeholder(R.mipmap.default_user)
                                .error(R.mipmap.default_user)
                                .dontAnimate()
                                .into(agency_img)
                    }

                })
    }
}
