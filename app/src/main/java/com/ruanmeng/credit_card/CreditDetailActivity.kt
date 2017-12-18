package com.ruanmeng.credit_card

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import com.flyco.dialog.widget.ActionSheetDialog
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.base.startActivity
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.DialogHelper
import com.ruanmeng.utils.NumberHelper
import kotlinx.android.synthetic.main.activity_credit_detail.*
import kotlinx.android.synthetic.main.layout_title_left.*
import org.json.JSONObject

class CreditDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credit_detail)
        setToolbarVisibility(false)
        init_title()

        getData()
    }

    override fun init_title() {
        left_nav_title.text = "信用卡"
        left_nav_right.visibility = View.VISIBLE
    }

    @Suppress("DEPRECATION")
    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.left_nav_right -> {
                val dialog = ActionSheetDialog(this, arrayOf("删除该信用卡", "预约删除未执行的计划"), null)
                dialog.isTitleShow(false)
                        .lvBgColor(resources.getColor(R.color.white))
                        .dividerColor(resources.getColor(R.color.divider))
                        .dividerHeight(0.5f)
                        .itemTextColor(resources.getColor(R.color.black_dark))
                        .itemHeight(40f)
                        .itemTextSize(15f)
                        .cancelText(resources.getColor(R.color.light))
                        .cancelTextSize(15f)
                        .layoutAnimation(null)
                        .show()
                dialog.setOnOperItemClickL { _, _, position, _ ->
                    dialog.dismiss()

                    when (position) {
                        0 -> {
                            DialogHelper.showDialog(baseContext)
                        }
                        1 -> startActivity(PlanBackActivity::class.java)
                    }
                }
            }
            R.id.credit_add -> {
                val dialog = ActionSheetDialog(this, arrayOf("消费计划", "还款计划"), null)
                dialog.isTitleShow(false)
                        .lvBgColor(resources.getColor(R.color.white))
                        .dividerColor(resources.getColor(R.color.divider))
                        .dividerHeight(0.5f)
                        .itemTextColor(resources.getColor(R.color.black_dark))
                        .itemHeight(40f)
                        .itemTextSize(15f)
                        .cancelText(resources.getColor(R.color.light))
                        .cancelTextSize(15f)
                        .layoutAnimation(null)
                        .show()
                dialog.setOnOperItemClickL { _, _, position, _ ->
                    dialog.dismiss()

                    when (position) {
                        0 -> startActivity(PlanPayActivity::class.java)
                        1 -> startActivity(PlanBackActivity::class.java)
                    }
                }
            }
            R.id.credit_look -> startActivity(PlanActivity::class.java)
        }
    }

    override fun getData() {
        OkGo.post<String>(BaseHttp.creditcard_details)
                .tag(this@CreditDetailActivity)
                .headers("token", getString("token"))
                .params("creditcardId", intent.getStringExtra("creditcardId"))
                .execute(object : StringDialogCallback(baseContext) {

                    @SuppressLint("SetTextI18n")
                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        val obj = JSONObject(response.body())
                        val creditcard = obj.getString("creditcard")

                        credit_bank.text = obj.getString("bank")
                        credit_tail.text = "尾号${creditcard.substring(creditcard.length - 4)}"
                        credit_bill.setRightString(obj.getString("billDay") + "日")
                        credit_pay.setRightString(obj.getString("repaymentDay") + "日")
                        credit_yi.setRightString(NumberHelper.fmtMicrometer(obj.getString("paySum")))
                        credit_wei.setRightString(NumberHelper.fmtMicrometer(obj.getString("nopaySum")))
                        credit_dang.setRightString("￥" + NumberHelper.fmtMicrometer(obj.getString("currentSum")))
                    }

                })
    }
}
