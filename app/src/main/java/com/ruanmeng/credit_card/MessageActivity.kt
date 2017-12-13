package com.ruanmeng.credit_card

import android.os.Bundle
import android.view.View
import com.makeramen.roundedimageview.RoundedImageView
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.load_Linear
import com.ruanmeng.base.refresh
import com.ruanmeng.model.CommonData
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

                            .visibility(R.id.item_message_divider1, if (list.indexOf(data) == list.size - 1) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_message_divider2, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)

                            .with<RoundedImageView>(R.id.item_message_img) { view -> view.setImageResource(R.mipmap.icon_msg) }

                            .clicked(R.id.item_message) { }
                }
                .attachTo(recycle_list)
    }
}
