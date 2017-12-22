package com.ruanmeng.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ruanmeng.base.BaseFragment
import com.ruanmeng.base.getString
import com.ruanmeng.credit_card.R
import com.ruanmeng.model.CountMessageEvent
import kotlinx.android.synthetic.main.layout_title_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class MainSecondFragment : BaseFragment() {

    //调用这个方法切换时不会释放掉Fragment
    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (this.view != null)
            this.view!!.visibility = if (menuVisible) View.VISIBLE else View.GONE
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main_second, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init_title()

        EventBus.getDefault().register(this@MainSecondFragment)
    }

    override fun init_title() {
        main_title.text = "发现"
        main_right.visibility = View.VISIBLE

        val count = getString("msgCount", "0").toInt()
        main_hint.visibility = if (count > 0) View.VISIBLE else View.INVISIBLE
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this@MainSecondFragment)
        super.onDestroy()
    }

    @Subscribe
    fun onMessageEvent(event: CountMessageEvent) {
        when (event.name) {
            "未读消息" -> {
                val count = event.count.toInt()
                main_hint.visibility = if (count > 0) View.VISIBLE else View.INVISIBLE
            }
        }
    }
}
