package com.ruanmeng.credit_card

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.flyco.dialog.widget.ActionSheetDialog
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.startActivity
import com.ruanmeng.model.CommonData
import kotlinx.android.synthetic.main.activity_savings_detail.*
import kotlinx.android.synthetic.main.layout_title_left.*

class SavingsDetailActivity : BaseActivity() {

    lateinit var data: CommonData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_savings_detail)
        setToolbarVisibility(false)
        init_title()
    }

    @SuppressLint("SetTextI18n")
    override fun init_title() {
        left_nav_title.text = "储蓄卡"
        left_nav_right.visibility = View.VISIBLE

        data = intent.getSerializableExtra("data") as CommonData

        when (data.bank) {
            "工商银行" -> bankcard_img.setImageResource(R.mipmap.bank01)
            "农业银行" -> bankcard_img.setImageResource(R.mipmap.bank02)
            "招商银行" -> bankcard_img.setImageResource(R.mipmap.bank03)
            "建设银行" -> bankcard_img.setImageResource(R.mipmap.bank04)
            "交通银行" -> bankcard_img.setImageResource(R.mipmap.bank05)
            "中信银行" -> bankcard_img.setImageResource(R.mipmap.bank06)
            "光大银行" -> bankcard_img.setImageResource(R.mipmap.bank07)
            "北京银行" -> bankcard_img.setImageResource(R.mipmap.bank08)
            "平安银行" -> bankcard_img.setImageResource(R.mipmap.bank09)
            "中国银行" -> bankcard_img.setImageResource(R.mipmap.bank10)
            "兴业银行" -> bankcard_img.setImageResource(R.mipmap.bank11)
            "民生银行" -> bankcard_img.setImageResource(R.mipmap.bank12)
            "华夏银行" -> bankcard_img.setImageResource(R.mipmap.bank13)
            "浦发银行" -> bankcard_img.setImageResource(R.mipmap.bank14)
            "广发银行" -> bankcard_img.setImageResource(R.mipmap.bank15)
            "邮政储蓄" -> bankcard_img.setImageResource(R.mipmap.bank16)
        }

        bankcard_name.text = data.bank + "储蓄卡"
        bankcard_tail.text = data.tel.substring(data.tel.length - 4)
        bankcard_num.text = data.depositcard.substring(data.depositcard.length - 4)
    }

    @Suppress("DEPRECATION")
    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.left_nav_right -> {
                val dialog = ActionSheetDialog(this, arrayOf("更换银行卡"), null)
                dialog.isTitleShow(false)
                        .lvBgColor(resources.getColor(R.color.white))
                        .itemTextColor(resources.getColor(R.color.black_dark))
                        .itemHeight(40f)
                        .itemTextSize(15f)
                        .cancelText(resources.getColor(R.color.light))
                        .cancelTextSize(15f)
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
            R.id.bankcard_cash -> { startActivity(CashoutActivity::class.java) }
        }
    }
}
