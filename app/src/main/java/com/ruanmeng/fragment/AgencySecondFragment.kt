package com.ruanmeng.fragment

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.lzy.okgo.utils.HttpUtils.runOnUiThread
import com.makeramen.roundedimageview.RoundedImageView
import com.ruanmeng.base.*
import com.ruanmeng.credit_card.R
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.CommonModel
import com.ruanmeng.model.IncomeData
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.Tools
import kotlinx.android.synthetic.main.fragment_agency_second.*
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter
import java.text.DecimalFormat

class AgencySecondFragment : BaseFragment() {

    private val list = ArrayList<Any>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_agency_second, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init_title()
    }

    override fun init_title() {
        empty_hint.text = "暂无相关下级信息！"

        agency_second_tab.apply {
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

                override fun onTabReselected(tab: TabLayout.Tab) {}
                override fun onTabUnselected(tab: TabLayout.Tab) {}

                override fun onTabSelected(tab: TabLayout.Tab) {
                    mPosition = tab.position + 1
                    OkGo.getInstance().cancelTag(this@AgencySecondFragment)

                    activity.window.decorView.postDelayed({ runOnUiThread { updateList() } }, 300)
                }

            })

            addTab(this.newTab().setText("一级"), true)
            addTab(this.newTab().setText("二级"), false)
            addTab(this.newTab().setText("三级"), false)

            post { Tools.setIndicator(this, 30, 30) }
        }

        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(activity, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_agency_list_2) { data, injector ->
                    injector.text(R.id.item_agency_name, data.nickName)
                            .text(R.id.item_agency_phone, "电话：${data.mobile}")
                            //最多保留几位小数就用几个#，最少几位就用几个0
                            .text(R.id.item_agency_price, DecimalFormat("########0.00").format(data.profitSum.toDouble()))
                            .text(R.id.item_agency_time, "注册时间：" + data.createDate)
                            .gone(R.id.item_agency_divider1)

                            .visibility(R.id.item_agency_divider2, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)

                            .with<RoundedImageView>(R.id.item_agency_img) { view ->
                                GlideApp.with(activity)
                                        .load(BaseHttp.baseImg + data.userHead)
                                        .placeholder(R.mipmap.ic_launcher_round) // 等待时的图片
                                        .error(R.mipmap.ic_launcher_round)       // 加载失败的图片
                                        .centerCrop()
                                        .dontAnimate()
                                        .into(view)
                            }
                }
                .attachTo(recycle_list)
    }

    override fun getData(pindex: Int) {
        OkGo.post<CommonModel>(BaseHttp.user_children_data)
                .tag(this@AgencySecondFragment)
                .headers("token", getString("token"))
                .params("page", pindex)
                .params("grade", mPosition)
                .execute(object : JacksonDialogCallback<CommonModel>(activity, CommonModel::class.java) {

                    override fun onSuccess(response: Response<CommonModel>) {
                        list.apply {
                            if (pindex == 1) {
                                clear()
                                pageNum = pindex
                            }
                            addItems(response.body().ls)
                            if (count(response.body().ls) > 0) pageNum++
                        }

                        mAdapter.updateData(list)
                    }

                    override fun onFinish() {
                        super.onFinish()
                        swipe_refresh.isRefreshing = false
                        isLoadingMore = false

                        empty_view.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                    }

                })
    }

    fun updateList() {
        swipe_refresh.isRefreshing = true
        if (list.size > 0) {
            list.clear()
            mAdapter.notifyDataSetChanged()
        }
        pageNum = 1
        getData(pageNum)
    }
}
