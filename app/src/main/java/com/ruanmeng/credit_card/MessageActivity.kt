package com.ruanmeng.credit_card

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.makeramen.roundedimageview.RoundedImageView
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.CommonModel
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import kotlinx.android.synthetic.main.layout_title_left.*
import net.idik.lib.slimadapter.SlimAdapter

class MessageActivity : BaseActivity() {

    private val list = ArrayList<Any>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        setToolbarVisibility(false)
        init_title()

        swipe_refresh.isRefreshing = true
        getData(pageNum)
    }

    override fun init_title() {
        left_nav_title.text = "消息中心"
        empty_hint.text = "暂无相关消息！"

        swipe_refresh.refresh { getData(1) }
        recycle_list.load_Linear(baseContext, swipe_refresh) {
            if (!isLoadingMore) {
                isLoadingMore = true
                getData(pageNum)
            }
        }

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_message_list) { data, injector ->
                    injector.text(R.id.item_message_title, data.title)
                            .text(R.id.item_message_time, data.sendDate)
                            .text(R.id.item_message_content, data.content)
                            .visibility(R.id.item_message_status, if (data.status == "1") View.INVISIBLE else View.VISIBLE)
                            .visibility(R.id.item_message_divider1, if (list.indexOf(data) == list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_message_divider2, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)

                            .with<RoundedImageView>(R.id.item_message_img) { view ->
                                view.setImageResource(if (data.type == "1") R.mipmap.com02 else R.mipmap.com01)
                            }

                            .clicked(R.id.item_message) {
                                data.status = "1"

                                window.decorView.postDelayed({ runOnUiThread {
                                    mAdapter.updateData(list).notifyItemChanged(list.indexOf(data))
                                } }, 500)

                                val intent = Intent(baseContext, WebActivity::class.java)
                                intent.putExtra("title", "消息详情")
                                intent.putExtra("msgReciveId", data.msgReciveId)
                                startActivity(intent)
                            }
                }
                .attachTo(recycle_list)
    }

    override fun getData(pindex: Int) {
        OkGo.post<CommonModel>(BaseHttp.get_MsgInfo)
                .tag(this@MessageActivity)
                .headers("token", getString("token"))
                .params("page", pindex)
                .execute(object : JacksonDialogCallback<CommonModel>(baseContext, CommonModel::class.java) {

                    override fun onSuccess(response: Response<CommonModel>) {
                        list.apply {
                            if (pindex == 1) {
                                clear()
                                pageNum = pindex
                            }
                            addItems(response.body().msgs)
                            if (count(response.body().msgs) > 0) pageNum++
                        }

                        mAdapter.updateData(list).notifyDataSetChanged()
                    }

                    override fun onFinish() {
                        super.onFinish()
                        swipe_refresh.isRefreshing = false
                        isLoadingMore = false

                        empty_view.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                    }

                })
    }
}
