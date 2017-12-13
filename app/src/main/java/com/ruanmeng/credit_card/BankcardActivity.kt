package com.ruanmeng.credit_card

import android.os.Bundle
import android.support.design.widget.TabLayout
import com.lzy.okgo.OkGo
import com.makeramen.roundedimageview.RoundedImageView
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.load_Linear
import com.ruanmeng.base.refresh
import com.ruanmeng.model.CommonData
import com.ruanmeng.utils.Tools
import kotlinx.android.synthetic.main.activity_bankcard.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter

class BankcardActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bankcard)
        init_title("我的银行卡")

        list.add(CommonData())
        list.add(CommonData())
        list.add(CommonData())
        list.add(CommonData())
        list.add(CommonData())
        list.add(CommonData())
        list.add(CommonData())
        list.add(CommonData())
        list.add(CommonData())
        list.add(CommonData())
        list.add(CommonData())
        list.add(CommonData())
        mAdapter.updateData(list).notifyDataSetChanged()
    }

    override fun init_title() {
        super.init_title()

        agency_tab.apply {
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

                override fun onTabReselected(tab: TabLayout.Tab) {}
                override fun onTabUnselected(tab: TabLayout.Tab) {}

                override fun onTabSelected(tab: TabLayout.Tab) {
                    mPosition = tab.position + 1

                    window.decorView.postDelayed({
                        runOnUiThread {
                            OkGo.getInstance().cancelTag(this@BankcardActivity)
                        }
                    }, 300)
                }

            })

            addTab(this.newTab().setText("储蓄卡"), true)
            addTab(this.newTab().setText("信用卡"), false)

            post { Tools.setIndicator(this, 50, 50) }
        }

        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_bankcard_list) { data, injector ->
                    injector.text(R.id.item_bankcard_name, data.title)
                            .with<RoundedImageView>(R.id.item_bankcard_img) { view ->  }
                            .clicked(R.id.item_bankcard) {

                            }
                }
                .attachTo(recycle_list)
    }
}
