package com.ruanmeng.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.allen.library.SuperTextView
import com.jude.rollviewpager.RollPagerView
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.cache.CacheMode
import com.lzy.okgo.model.Response
import com.makeramen.roundedimageview.RoundedImageView
import com.ruanmeng.adapter.LoopAdapter
import com.ruanmeng.base.*
import com.ruanmeng.credit_card.BillActivity
import com.ruanmeng.credit_card.NoticeActivity
import com.ruanmeng.credit_card.R
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.CommonModel
import com.ruanmeng.model.CountMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.BankCardUtil
import com.ruanmeng.utils.NumberHelper
import com.ruanmeng.view.SwitcherTextView
import kotlinx.android.synthetic.main.layout_list.*
import kotlinx.android.synthetic.main.layout_title_main.*
import net.idik.lib.slimadapter.SlimAdapter
import net.idik.lib.slimadapter.SlimAdapterEx
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.util.ArrayList

class MainFirstFragment : BaseFragment() {

    private lateinit var banner: RollPagerView
    private lateinit var mLoopAdapter: LoopAdapter
    private lateinit var mSwitchText: SwitcherTextView

    private val list = ArrayList<Any>()
    private val list_slider = ArrayList<CommonData>()
    private val list_notice = ArrayList<CommonData>()

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

        EventBus.getDefault().register(this@MainFirstFragment)

        swipe_refresh.isRefreshing = true
        getData()
    }

    @SuppressLint("InflateParams")
    override fun init_title() {
        main_title.text = "诸葛信用管家"
        main_right.visibility = View.VISIBLE
        val count = getString("msgCount", "0").toInt()
        main_hint.visibility = if (count > 0) View.VISIBLE else View.INVISIBLE

        swipe_refresh.refresh { getData() }
        recycle_list.load_Linear(activity)

        mAdapterEx = SlimAdapter.create(SlimAdapterEx::class.java)
                .apply {
                    val view = LayoutInflater.from(activity).inflate(R.layout.header_first, null)
                    banner = view.findViewById(R.id.first_banner)
                    mSwitchText = view.findViewById(R.id.first_notice)
                    val mBill = view.findViewById(R.id.first_bill) as SuperTextView

                    mBill.setOnClickListener { startActivity(BillActivity::class.java) }

                    mLoopAdapter = LoopAdapter(activity, banner)
                    banner.apply {
                        setAdapter(mLoopAdapter)
                        setOnItemClickListener { position -> /*轮播图点击事件*/ }
                    }
                    addHeader(view)
                }
                .register<CommonData>(R.layout.item_first_list) { data, injector ->
                    injector.text(R.id.item_first_name, data.payType)
                            .text(R.id.item_first_price, "￥${NumberHelper.fmtMicrometer(data.paySum)}")
                            .text(R.id.item_first_type, if (data.cardType == "0") "储蓄卡" else "信用卡" + "还款")
                            .text(R.id.item_first_num, "尾号(${data.cardNo.substring(data.cardNo.length - 4)})")
                            .text(R.id.item_first_rate, "费率 ${if (data.rate.isEmpty()) "0.0" else (data.rate.toDouble() * 100).toString()}%")
                            .text(R.id.item_first_time, data.payTime)
                            .text(R.id.item_first_status, when (data.status) {
                                "1" -> "交易成功"
                                "-2" -> "交易中"
                                else -> "交易失败"
                            })

                            .with<RoundedImageView>(R.id.item_first_img) { view ->
                                when (BankCardUtil(data.cardNo).bankName) {
                                    "工商银行" -> view.setImageResource(R.mipmap.bank01)
                                    "农业银行" -> view.setImageResource(R.mipmap.bank02)
                                    "招商银行" -> view.setImageResource(R.mipmap.bank03)
                                    "建设银行" -> view.setImageResource(R.mipmap.bank04)
                                    "交通银行" -> view.setImageResource(R.mipmap.bank05)
                                    "中信银行" -> view.setImageResource(R.mipmap.bank06)
                                    "光大银行" -> view.setImageResource(R.mipmap.bank07)
                                    "北京银行" -> view.setImageResource(R.mipmap.bank08)
                                    "平安银行" -> view.setImageResource(R.mipmap.bank09)
                                    "中国银行" -> view.setImageResource(R.mipmap.bank10)
                                    "兴业银行" -> view.setImageResource(R.mipmap.bank11)
                                    "民生银行" -> view.setImageResource(R.mipmap.bank12)
                                    "华夏银行" -> view.setImageResource(R.mipmap.bank13)
                                }
                            }

                            .visibility(R.id.item_first_divider1, if (list.indexOf(data) == list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_first_divider2, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_first_divider3, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)

                            .clicked(R.id.item_first) { }
                }
                .attachTo(recycle_list)
    }

    override fun getData() {
        OkGo.post<CommonModel>(BaseHttp.index_alldata)
                .tag(this@MainFirstFragment)
                .headers("token", getString("token"))
                .cacheMode(CacheMode.REQUEST_FAILED_READ_CACHE)
                .cacheKey(BaseHttp.index_alldata)
                .execute(object : JacksonDialogCallback<CommonModel>(activity, CommonModel::class.java) {

                    override fun onSuccess(response: Response<CommonModel>) {
                        list_slider.clear()
                        list_notice.clear()
                        list.clear()

                        list_slider.addItems(response.body().slider)
                        list_notice.addItems(response.body().news)
                        list.addItems(response.body().payrecords)

                        mAdapterEx.updateData(list).notifyDataSetChanged()

                        val imgs = ArrayList<String>()
                        list_slider.mapTo(imgs) { it.sliderImg }
                        mLoopAdapter.setImgs(imgs)
                        if (imgs.size < 2) {
                            banner.pause()
                            banner.setHintViewVisibility(false)
                        } else {
                            banner.resume()
                            banner.setHintViewVisibility(true)
                        }

                        if (list_notice.size > 0) {
                            mSwitchText.setData(list_notice, {

                                startActivity(NoticeActivity::class.java)

                                /*val intent = Intent(activity, WebActivity::class.java)
                                intent.putExtra("title", "详情")
                                intent.putExtra("newsnoticeId", list_notice[position].newsnoticeId)
                                startActivity(intent)*/
                            }, mSwitchText)
                        }
                    }

                    override fun onFinish() {
                        super.onFinish()
                        swipe_refresh.isRefreshing = false
                    }

                })
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this@MainFirstFragment)
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
