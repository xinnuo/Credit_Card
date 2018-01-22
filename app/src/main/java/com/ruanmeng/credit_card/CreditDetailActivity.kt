package com.ruanmeng.credit_card

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.flyco.dialog.widget.ActionSheetDialog
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.getString
import com.ruanmeng.base.toast
import com.ruanmeng.model.RefreshMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.DialogHelper
import com.ruanmeng.utils.NumberHelper
import kotlinx.android.synthetic.main.activity_credit_detail.*
import kotlinx.android.synthetic.main.layout_title_left.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.json.JSONObject
import java.text.DecimalFormat
import kotlin.math.roundToInt

class CreditDetailActivity : BaseActivity() {

    private var creditcardId = ""
    private var creditcard = ""
    private var bank = ""
    private var billDay = ""
    private var repaymentDay = ""
    private var quota = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_credit_detail)
        setToolbarVisibility(false)
        init_title()

        EventBus.getDefault().register(this@CreditDetailActivity)

        getData()
    }

    override fun init_title() {
        left_nav_title.text = "信用卡"
        val isPay = intent.getBooleanExtra("isPay", false)
        if (!isPay) left_nav_right.visibility = View.VISIBLE

        quota = intent.getStringExtra("quota")
        credit_quota.setRightString(quota.toDouble().toInt().toString() + "元")

        credit_bill.setOnClickListener {
            if (creditcardId.isEmpty()) return@setOnClickListener

            val intent = Intent(baseContext, CreditModifyActivity::class.java)
            intent.putExtra("title", "修改账单日")
            intent.putExtra("creditcardId", creditcardId)
            intent.putExtra("day", billDay)
            startActivity(intent)
        }
        credit_pay.setOnClickListener {
            if (creditcardId.isEmpty()) return@setOnClickListener

            val intent = Intent(baseContext, CreditModifyActivity::class.java)
            intent.putExtra("title", "修改还款日")
            intent.putExtra("creditcardId", creditcardId)
            intent.putExtra("day", repaymentDay)
            startActivity(intent)
        }
        credit_quota.setOnClickListener {
            val intent = Intent(baseContext, CreditModifyActivity::class.java)
            intent.putExtra("title", "修改额度")
            intent.putExtra("creditcardId", creditcardId)
            intent.putExtra("day", quota.toDouble().toInt().toString())
            startActivity(intent)
        }
    }

    @Suppress("DEPRECATION")
    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.left_nav_right -> {
                val dialog = ActionSheetDialog(this, arrayOf("删除该信用卡"), null)
                dialog.isTitleShow(false)
                        .lvBgColor(resources.getColor(R.color.white))
                        .dividerColor(resources.getColor(R.color.divider))
                        .dividerHeight(0.5f)
                        .itemTextColor(resources.getColor(R.color.black_dark))
                        .itemHeight(40f)
                        .itemTextSize(15f)
                        .itemPositonColor(0, resources.getColor(R.color.red))
                        .cancelText(resources.getColor(R.color.light))
                        .cancelTextSize(15f)
                        .layoutAnimation(null)
                        .show()
                dialog.setOnOperItemClickL { _, _, position, _ ->
                    dialog.dismiss()

                    when (position) {
                        0 -> {
                            DialogHelper.showDialog(
                                    baseContext,
                                    "温馨提示",
                                    "确定要删除该信用卡吗？",
                                    "取消",
                                    "确定",
                                    null) {
                                OkGo.post<String>(BaseHttp.creditcard_del_sub)
                                        .tag(this@CreditDetailActivity)
                                        .headers("token", getString("token"))
                                        .params("creditcardId", intent.getStringExtra("creditcardId"))
                                        .execute(object : StringDialogCallback(baseContext) {

                                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                                                toast(msg)
                                                EventBus.getDefault().post(RefreshMessageEvent("删除信用卡"))
                                                ActivityStack.getScreenManager().popActivities(this@CreditDetailActivity::class.java)
                                            }

                                        })
                            }
                        }
                    }
                }
            }
            R.id.credit_add -> {
                val dialog = ActionSheetDialog(this, arrayOf(/*"消费计划", */"还款计划"), null)
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

                    if (creditcardId.isEmpty()) return@setOnOperItemClickL

                    when (position) {
                        /*0 -> {
                            val intent = Intent(baseContext, PlanPayActivity::class.java)
                            intent.putExtra("creditcardId", creditcardId)
                            intent.putExtra("creditcard", creditcard)
                            intent.putExtra("bank", bank)
                            startActivity(intent)
                        }*/
                        0 -> {
                            val intent = Intent(baseContext, PlanBackActivity::class.java)
                            intent.putExtra("creditcardId", creditcardId)
                            intent.putExtra("billDay", billDay)
                            intent.putExtra("repaymentDay", repaymentDay)
                            startActivity(intent)
                        }
                    }
                }
            }
            R.id.credit_look -> {
                val intent = Intent(baseContext, PlanActivity::class.java)
                intent.putExtra("creditcardId", creditcardId)
                startActivity(intent)
            }
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
                        creditcardId = obj.getString("creditcardId")
                        creditcard = obj.getString("creditcard")
                        bank = obj.getString("bank")
                        billDay = obj.getString("billDay")
                        repaymentDay = obj.getString("repaymentDay")

                        val list_bank = resources.getStringArray(R.array.bank_credit).asList()
                        if (!list_bank.contains(bank)) {
                            credit_add.visibility = View.GONE
                            credit_look.visibility = View.GONE
                            toast("该信用卡暂不支持还款功能")
                        }

                        when (bank) {
                            "工商银行" -> credit_img.setImageResource(R.mipmap.bank01)
                            "农业银行" -> credit_img.setImageResource(R.mipmap.bank02)
                            "招商银行" -> credit_img.setImageResource(R.mipmap.bank03)
                            "建设银行" -> credit_img.setImageResource(R.mipmap.bank04)
                            "交通银行" -> credit_img.setImageResource(R.mipmap.bank05)
                            "中信银行" -> credit_img.setImageResource(R.mipmap.bank06)
                            "光大银行" -> credit_img.setImageResource(R.mipmap.bank07)
                            "北京银行" -> credit_img.setImageResource(R.mipmap.bank08)
                            "平安银行" -> credit_img.setImageResource(R.mipmap.bank09)
                            "中国银行" -> credit_img.setImageResource(R.mipmap.bank10)
                            "兴业银行" -> credit_img.setImageResource(R.mipmap.bank11)
                            "民生银行" -> credit_img.setImageResource(R.mipmap.bank12)
                            "华夏银行" -> credit_img.setImageResource(R.mipmap.bank13)
                            "浦发银行" -> credit_img.setImageResource(R.mipmap.bank14)
                            "广发银行" -> credit_img.setImageResource(R.mipmap.bank15)
                            "邮政储蓄" -> credit_img.setImageResource(R.mipmap.bank16)
                        }

                        credit_bank.text = bank + "信用卡"
                        credit_tail.text = "尾号${creditcard.substring(creditcard.length - 4)}"
                        credit_bill.setRightString(billDay + "日")
                        credit_pay.setRightString(repaymentDay + "日")

                        if (!obj.isNull("paySum")) {
                            val paySum = DecimalFormat("########0.00####").format(obj.getString("paySum").toDouble())
                            credit_yi.setRightString(NumberHelper.fmtMicrometer(paySum) + "元")
                        }

                        if (!obj.isNull("nopaySum")) {
                            val nopaySum = DecimalFormat("########0.00####").format(obj.getString("nopaySum").toDouble())
                            credit_wei.setRightString(NumberHelper.fmtMicrometer(nopaySum) + "元")
                        }
                    }

                })
    }

    override fun finish() {
        EventBus.getDefault().unregister(this@CreditDetailActivity)
        super.finish()
    }

    @Subscribe
    fun onMessageEvent(event: RefreshMessageEvent) {
        when (event.name) {
            "修改账单日" -> {
                billDay = event.id
                credit_bill.setRightString(billDay + "日")
            }
            "修改还款日" -> {
                repaymentDay = event.id
                credit_pay.setRightString(repaymentDay + "日")
            }
            "修改额度" -> {
                quota = event.id
                credit_quota.setRightString(quota + "元")
            }
        }
    }
}
