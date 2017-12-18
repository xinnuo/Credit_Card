package com.ruanmeng.credit_card

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import com.flyco.dialog.listener.OnOperItemClickL
import com.flyco.dialog.widget.ActionSheetDialog
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.model.CommonData
import com.ruanmeng.share.Const
import com.ruanmeng.utils.PreferencesUtils
import kotlinx.android.synthetic.main.activity_savings_detail.*
import kotlinx.android.synthetic.main.layout_title_left.*
import org.json.JSONObject

class SavingsDetailActivity : BaseActivity() {

    lateinit var data: CommonData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_savings_detail)
        setToolbarVisibility(false)
        init_title()
    }

    override fun init_title() {
        left_nav_title.text = "储蓄卡"
        left_nav_right.visibility = View.VISIBLE

        data = intent.getSerializableExtra("data") as CommonData
        bankcard_name.text = data.bank
        bankcard_tail.text = data.tel.substring(data.tel.length - 4)
        bankcard_num.text = data.depositcard.substring(data.depositcard.length - 4)
    }

    @Suppress("DEPRECATION")
    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.left_nav_right -> {
                val dialog = ActionSheetDialog(this, arrayOf("更好银行卡"), null)
                dialog.isTitleShow(false)
                        .lvBgColor(resources.getColor(R.color.white))
                        .itemTextColor(resources.getColor(R.color.black_dark))
                        .itemHeight(40f)
                        .itemTextSize(14f)
                        .cancelText(resources.getColor(R.color.light))
                        .cancelTextSize(14f)
                        .layoutAnimation(null)
                        .show()
                dialog.setOnOperItemClickL { _, _, _, _ ->
                    dialog.dismiss()

                    window.decorView.postDelayed({
                        val intent = Intent(baseContext, SavingsCardActivity::class.java)
                        intent.putExtra("isChanged", true)
                        startActivity(intent)
                    }, 300)
                }
            }
            R.id.bankcard_cash -> {
            }
        }
    }
}
