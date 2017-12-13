package com.ruanmeng.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ruanmeng.base.BaseFragment
import com.ruanmeng.base.startActivity
import com.ruanmeng.credit_card.*
import kotlinx.android.synthetic.main.fragment_main_third.*
import kotlinx.android.synthetic.main.layout_title_main.*

class MainThirdFragment : BaseFragment() {

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

        third_img.setOnClickListener { startActivity(InfoActivity::class.java) }
        third_card.setOnClickListener { startActivity(BankcardActivity::class.java) }
        third_bill.setOnClickListener { startActivity(BillActivity::class.java) }
        third_real.setOnClickListener { startActivity(RealActivity::class.java) }
        third_share.setOnClickListener { startActivity(ShareActivity::class.java) }
        third_setting.setOnClickListener { startActivity(SettingActivity::class.java) }
    }
}
