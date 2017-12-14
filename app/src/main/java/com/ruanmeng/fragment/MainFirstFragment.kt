package com.ruanmeng.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jude.rollviewpager.RollPagerView
import com.ruanmeng.adapter.LoopAdapter
import com.ruanmeng.base.BaseFragment
import com.ruanmeng.base.load_Linear
import com.ruanmeng.base.refresh
import com.ruanmeng.credit_card.R
import com.ruanmeng.model.CommonData
import kotlinx.android.synthetic.main.layout_list.*
import kotlinx.android.synthetic.main.layout_title_main.*
import net.idik.lib.slimadapter.SlimAdapter
import net.idik.lib.slimadapter.SlimAdapterEx
import java.util.ArrayList

class MainFirstFragment : BaseFragment() {

    private lateinit var banner: RollPagerView
    private lateinit var mLoopAdapter: LoopAdapter

    private val list = ArrayList<Any>()

    //调用这个方法切换时不会释放掉Fragment
    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (this.view != null)
            this.view!!.visibility = if (menuVisible) View.VISIBLE else View.GONE
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main_first, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init_title()

        list.add(CommonData().apply { title = "100.0" })
        list.add(CommonData().apply { title = "200.0" })
        list.add(CommonData().apply { title = "300.0" })
        list.add(CommonData().apply { title = "400.0" })
        list.add(CommonData().apply { title = "500.0" })
        list.add(CommonData().apply { title = "600.0" })
        mAdapterEx.updateData(list).notifyDataSetChanged()
    }

    @SuppressLint("InflateParams")
    override fun init_title() {
        main_title.text = "诸葛智能管家"
        main_right.visibility = View.VISIBLE

        swipe_refresh.refresh { }
        recycle_list.load_Linear(activity)

        mAdapterEx = SlimAdapter.create(SlimAdapterEx::class.java)
                .apply {
                    val view = LayoutInflater.from(activity).inflate(R.layout.header_first, null)
                    banner = view.findViewById(R.id.first_banner)

                    mLoopAdapter = LoopAdapter(activity, banner)
                    banner.apply {
                        setAdapter(mLoopAdapter)
                        setOnItemClickListener { position ->
                            //轮播图点击事件
                        }
                    }
                    addHeader(view)
                }
                .register<CommonData>(R.layout.item_first_list) { data, injector ->
                    injector.text(R.id.item_first_name, "还款消费")
                            .text(R.id.item_first_price, "￥${data.title}")

                            .visibility(R.id.item_first_divider1, if (list.indexOf(data) == list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_first_divider2, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_first_divider3, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)

                            .clicked(R.id.item_first) { }
                }
                .attachTo(recycle_list)
    }
}
