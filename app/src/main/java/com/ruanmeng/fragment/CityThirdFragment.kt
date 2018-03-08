package com.ruanmeng.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ruanmeng.base.BaseFragment
import com.ruanmeng.base.load_Linear
import com.ruanmeng.credit_card.R
import com.ruanmeng.model.CommonData
import kotlinx.android.synthetic.main.fragment_city_third.*
import net.idik.lib.slimadapter.SlimAdapter
import java.util.*

class CityThirdFragment : BaseFragment() {

    private lateinit var list: ArrayList<CommonData>

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_city_third, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init_title()

        @Suppress("UNCHECKED_CAST")
        list = arguments.getSerializable("list") as ArrayList<CommonData>
        mAdapter.updateData(list)
    }

    override fun init_title() {
        recycle_list.load_Linear(activity)

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_city_list) { data, injector ->
                    injector.text(R.id.item_city_name, data.areaName)
                            .visibility(R.id.item_city_check, if (data.isChecked) View.VISIBLE else View.INVISIBLE)
                            .visibility(R.id.item_city_divider1, if (list.indexOf(data) == list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_city_divider2, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)

                            .clicked(R.id.item_city, {
                                list.filter { it.isChecked }.forEach { it.isChecked = false }
                                data.isChecked = true
                                mAdapter.notifyDataSetChanged()

                                (activity as OnFragmentItemSelectListener).onitemSelected(
                                        "区",
                                        data.areaId,
                                        data.areaName)
                            })
                }
                .attachTo(recycle_list)
    }
}
