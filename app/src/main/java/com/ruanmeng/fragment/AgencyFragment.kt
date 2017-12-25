package com.ruanmeng.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.makeramen.roundedimageview.RoundedImageView
import com.ruanmeng.base.*
import com.ruanmeng.credit_card.R
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.CommonModel
import com.ruanmeng.model.IncomeData
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter
import java.text.DecimalFormat

class AgencyFragment : BaseFragment() {

    private val list = ArrayList<Any>()
    private var url = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_agency, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init_title()

        swipe_refresh.isRefreshing = true
        getData(pageNum)
    }

    override fun init_title() {
        url = arguments.getString("url")

        empty_hint.text = when (url) {
            BaseHttp.profit_data -> "暂无相关收益信息！"
            else -> "暂无相关下级信息！"
        }

        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(activity, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapter = SlimAdapter.create()
                .register<IncomeData>(R.layout.item_agency_list_1) { data, injector ->
                    injector.text(R.id.item_agency_name, data.profitExplain)
                            .text(R.id.item_agency_time, data.createDate)
                            .text(R.id.item_agency_buy, "￥${DecimalFormat("########0.0#####").format(data.tradeSum.toDouble())}")
                            .text(R.id.item_agency_get, "￥${DecimalFormat("########0.0#####").format(data.profitSum.toDouble())}")

                            .visibility(R.id.item_agency_divider1, if (list.indexOf(data) == list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_agency_divider2, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)

                            .with<RoundedImageView>(R.id.item_agency_img) { view ->
                                GlideApp.with(activity)
                                        .load(BaseHttp.baseImg + data.userHead)
                                        .placeholder(R.mipmap.default_user) // 等待时的图片
                                        .error(R.mipmap.default_user)       // 加载失败的图片
                                        .centerCrop()
                                        .dontAnimate()
                                        .into(view)
                            }
                }
                .register<CommonData>(R.layout.item_agency_list_2) { data, injector ->
                    injector.text(R.id.item_agency_name, data.nickName)
                            .text(R.id.item_agency_phone, "电话 ${data.mobile}")
                            //最多保留几位小数就用几个#，最少几位就用几个0
                            .text(R.id.item_agency_price, DecimalFormat("########0.0#####").format(data.profitSum.toDouble()))
                            .gone(R.id.item_agency_divider1)

                            .visibility(R.id.item_agency_divider2, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)

                            .with<RoundedImageView>(R.id.item_agency_img) { view ->
                                GlideApp.with(activity)
                                        .load(BaseHttp.baseImg + data.userHead)
                                        .placeholder(R.mipmap.default_user) // 等待时的图片
                                        .error(R.mipmap.default_user)       // 加载失败的图片
                                        .centerCrop()
                                        .dontAnimate()
                                        .into(view)
                            }
                }
                .attachTo(recycle_list)
    }

    override fun getData(pindex: Int) {
        OkGo.post<CommonModel>(url)
                .tag(this@AgencyFragment)
                .headers("token", getString("token"))
                .params("page", pindex)
                .execute(object : JacksonDialogCallback<CommonModel>(activity, CommonModel::class.java) {

                    override fun onSuccess(response: Response<CommonModel>) {
                        list.apply {
                            if (pindex == 1) {
                                clear()
                                pageNum = pindex
                            }
                            if (url == BaseHttp.profit_data ) {
                                addItems(response.body().profits)
                                if (count(response.body().profits) > 0) pageNum++
                            } else {
                                addItems(response.body().ls)
                                if (count(response.body().ls) > 0) pageNum++
                            }
                        }

                        mAdapter.updateData(list).notifyDataSetChanged()
                    }

                    override fun onFinish() {
                        super.onFinish()
                        swipe_refresh.isRefreshing = false
                        isLoadingMore = false

                        empty_view.visibility = if (list.size == 0) View.VISIBLE else View.GONE
                    }

                })
    }
}
