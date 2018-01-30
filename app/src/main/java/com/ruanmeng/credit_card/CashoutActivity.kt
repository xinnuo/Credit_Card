package com.ruanmeng.credit_card

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.jungly.gridpasswordview.GridPasswordView
import com.jungly.gridpasswordview.PasswordType
import com.lzy.extend.StringDialogCallback
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.CommonModel
import com.ruanmeng.model.RefreshMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.DialogHelper
import com.ruanmeng.utils.KeyboardHelper
import com.ruanmeng.utils.NumberHelper
import kotlinx.android.synthetic.main.activity_cashout.*
import kotlinx.android.synthetic.main.layout_title_left.*
import org.greenrobot.eventbus.EventBus
import java.math.BigDecimal

class CashoutActivity : BaseActivity() {

    private val list = ArrayList<CommonData>()
    private var depositcardId = ""

    private var withdrawSum = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cashout)
        setToolbarVisibility(false)
        init_title()

        getData()
    }

    @SuppressLint("SetTextI18n")
    override fun init_title() {
        left_nav_title.text = "提现"

        if (getString("withdrawSum").isNotEmpty()) {
            val withdrawString = getString("withdrawSum")
            withdrawSum = BigDecimal(withdrawString.toDouble())
                    .setScale(2, BigDecimal.ROUND_HALF_UP)
                    .toDouble()
        }
        cashout_total.text = String.format("%.2f", withdrawSum) + "元"

        cashout_ok.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
        cashout_ok.isClickable = false

        cashout_count.addTextChangedListener(this@CashoutActivity)
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.cashout_ok -> {
                if (depositcardId.isEmpty()) return

                val mCount = cashout_count.text.toString().toDouble()
                if (mCount == 0.0) {
                    toast("提现金额输入为0.00元，请重新输入")
                    return
                }

                showPaySheetDialog(cashout_count.text.toString())
            }
        }
    }

    private fun showPaySheetDialog(value: String) {
        if (getString("isPayPwd") != "1") {
            DialogHelper.showDialog(
                    baseContext,
                    "温馨提示",
                    "未设置支付密码，无法进行收款，是否现在去设置？",
                    "取消",
                    "去设置") {

                startActivity(PasswordPayActivity::class.java)
            }
        } else {
            val dialog = object : BottomDialog(baseContext) {

                private lateinit var iv_close: ImageView
                private lateinit var tv_title: TextView
                private lateinit var tv_hint: TextView
                private lateinit var gpv_pwd: GridPasswordView

                @SuppressLint("SetTextI18n")
                override fun onCreateView(): View {
                    val view = View.inflate(baseContext, R.layout.dialog_memeber_pwd, null)

                    iv_close = view.findViewById(R.id.withdraw_close)
                    tv_title = view.findViewById(R.id.withdraw_title)
                    tv_hint = view.findViewById(R.id.withdraw_hint)
                    gpv_pwd = view.findViewById(R.id.withdraw_pwd)

                    tv_title.text = "确认提现"
                    tv_hint.text = "提现金额${NumberHelper.fmtMicrometer(value)}元"
                    tv_hint.textSize = 22f
                    gpv_pwd.setPasswordType(PasswordType.NUMBER)

                    return view
                }

                override fun setUiBeforShow() {

                    iv_close.setOnClickListener { dismiss() }
                    gpv_pwd.setOnPasswordChangedListener(object : GridPasswordView.OnPasswordChangedListener {

                        override fun onTextChanged(psw: String) {}

                        override fun onInputFinish(psw: String) {
                            OkGo.post<String>(BaseHttp.paypawd_check)
                                    .tag(this@CashoutActivity)
                                    .headers("token", getString("token"))
                                    .params("paypawd", psw)
                                    .execute(object : StringDialogCallback(baseContext, false) {

                                        override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                            dismiss()

                                            window.decorView.postDelayed({ runOnUiThread { getWithdraw() } }, 300)
                                        }

                                        override fun onSuccessResponseErrorCode(
                                                response: Response<String>,
                                                msg: String,
                                                msgCode: String) = dismiss()
                                    })
                        }

                    })

                }
            }
            dialog.setOnShowListener { KeyboardHelper.showSoftInput(this@CashoutActivity) }
            dialog.show()
        }
    }

    private fun getWithdraw() {
        OkGo.post<String>(BaseHttp.add_withdraw)
                .tag(this@CashoutActivity)
                .headers("token", getString("token"))
                .params("withdrawSum", cashout_count.text.toString())
                .params("bussKey", depositcardId)
                .params("cardType", 0)
                .execute(object : StringDialogCallback(baseContext, false) {
                    /*{
                        "msg": "提现成功",
                        "msgcode": 100,
                        "object": "https://shouyin.yeepay.com/nc-cashier-wap/wap/request/10016127996/rZZIga6mFzmWwVHne*OWsg%3D%3D"
                    }*/
                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        toast(msg)
                        EventBus.getDefault().post(RefreshMessageEvent("提现", "", ""))
                        ActivityStack.getScreenManager().popActivities(this@CashoutActivity::class.java)
                    }

                })
    }

    override fun getData() {
        OkGo.post<CommonModel>(BaseHttp.depositcard_data)
                .tag(this@CashoutActivity)
                .headers("token", getString("token"))
                .execute(object : JacksonDialogCallback<CommonModel>(baseContext, CommonModel::class.java, true) {

                    @SuppressLint("SetTextI18n")
                    override fun onSuccess(response: Response<CommonModel>) {

                        list.addItems(response.body().depositcards)

                        if (list.isEmpty()) toast("您还没有添加储蓄卡，无法进行提现")
                        else {
                            depositcardId = list[0].depositcardId
                            cashout_hint.text = list[0].bank
                            cashout_tail.text = "（${list[0].depositcard.substring(list[0].depositcard.length - 4)}）"
                        }
                    }

                })
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (cashout_count.text.isNotBlank()) {
            cashout_ok.setBackgroundResource(R.drawable.rec_bg_blue)
            cashout_ok.isClickable = true
        } else {
            cashout_ok.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
            cashout_ok.isClickable = false
        }

        if (s.isNotEmpty()) {
            if ("." == s.toString()) {
                cashout_count.setText("0.")
                cashout_count.setSelection(cashout_count.text.length) //设置光标的位置
                return
            }

            val inputCount = s.toString().toDouble()
            if (inputCount > withdrawSum) {
                cashout_count.setText(String.format("%.2f", withdrawSum))
                cashout_count.setSelection(cashout_count.text.length)
            } else {
                if (s.length > String.format("%.2f", withdrawSum).length) {
                    cashout_count.setText(String.format("%.2f", withdrawSum))
                    cashout_count.setSelection(cashout_count.text.length)
                }
            }
        }
    }

    override fun afterTextChanged(s: Editable) {
        val temp = s.toString()
        val posDot = temp.indexOf(".")
        if (posDot < 0) {
            if (temp.length > 7) s.delete(7, 8)
        } else {
            if (temp.length - posDot - 1 > 2) s.delete(posDot + 3, posDot + 4)
        }
    }
}
