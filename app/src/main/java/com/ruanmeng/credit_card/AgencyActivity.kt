package com.ruanmeng.credit_card

import android.os.Bundle
import android.support.v4.app.Fragment
import com.ruanmeng.adapter.TabFragmentAdapter
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.startActivity
import com.ruanmeng.fragment.AgencyFragment
import com.ruanmeng.utils.Tools
import kotlinx.android.synthetic.main.activity_agency.*

class AgencyActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agency)
        init_title("代理商")
    }

    override fun init_title() {
        super.init_title()
        val fragments = ArrayList<Fragment>()
        val titles = listOf("收益列表", "我的下级")

        fragments.add(AgencyFragment())
        fragments.add(AgencyFragment())

        agency_tab.apply {
            post { Tools.setIndicator(this, 50, 50) }
            removeAllTabs()
        }
        agency_pager.adapter = TabFragmentAdapter(supportFragmentManager, titles, fragments)
        // 为TabLayout设置ViewPager
        agency_tab.setupWithViewPager(agency_pager)

        agency_cash.setOnClickListener { startActivity(CashoutActivity::class.java) }
    }
}
