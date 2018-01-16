package com.ruanmeng.credit_card

import android.net.Uri
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
import io.rong.imkit.RongIM
import io.rong.imlib.model.UserInfo
import kotlinx.android.synthetic.main.layout_empty.*
import kotlinx.android.synthetic.main.layout_list.*
import net.idik.lib.slimadapter.SlimAdapter

class OnlineActivity : BaseActivity() {

    private val list = ArrayList<CommonData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_online)
        init_title("客服列表")

        @Suppress("UNCHECKED_CAST")
        val items = intent.getSerializableExtra("list") as ArrayList<CommonData>
        list.addAll(items)
        mAdapter.updateData(list).notifyDataSetChanged()
    }

    override fun init_title() {
        empty_hint.text = "暂无相关客服信息！"

        super.init_title()
        swipe_refresh.refresh { getData() }
        recycle_list.load_Linear(baseContext, swipe_refresh)

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_online_list) { data, injector ->
                    injector.text(R.id.item_online_name, data.nickName)

                            .visibility(R.id.item_online_divider1, if (list.indexOf(data) == list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_online_divider2, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)

                            .with<RoundedImageView>(R.id.item_online_img) { view ->
                                GlideApp.with(baseContext)
                                        .load(BaseHttp.baseImg + data.userhead)
                                        .placeholder(R.mipmap.default_user) // 等待时的图片
                                        .error(R.mipmap.default_user)       // 加载失败的图片
                                        .centerCrop()
                                        .dontAnimate()
                                        .into(view)
                            }

                            .clicked(R.id.item_online_contact) {
                                //融云刷新用户信息
                                RongIM.getInstance().refreshUserInfoCache(UserInfo(
                                        data.userInfoId,
                                        data.nickName,
                                        Uri.parse(BaseHttp.baseImg + data.userhead)))

                                //融云单聊
                                RongIM.getInstance().startPrivateChat(baseContext, data.userInfoId, data.nickName)
                            }
                }
                .attachTo(recycle_list)
    }

    override fun getData() {
        OkGo.post<CommonModel>(BaseHttp.customer_list)
                .tag(this@OnlineActivity)
                .execute(object : JacksonDialogCallback<CommonModel>(baseContext, CommonModel::class.java) {

                    override fun onSuccess(response: Response<CommonModel>) {
                        list.clear()
                        list.addItems(response.body().customers)

                        mAdapter.updateData(list).notifyDataSetChanged()
                    }

                    override fun onFinish() {
                        super.onFinish()
                        swipe_refresh.isRefreshing = false

                        empty_view.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                    }

                })
    }
}
