package com.ruanmeng.credit_card

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.allen.library.SuperTextView
import com.allinpay.appayassistex.APPayAssistEx
import com.jungly.gridpasswordview.GridPasswordView
import com.jungly.gridpasswordview.PasswordType
import com.lzy.extend.StringDialogCallback
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.lzy.okgo.utils.OkLogger
import com.ruanmeng.allinpay.PaaCreator
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.CommonModel
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.share.Const
import com.ruanmeng.utils.*
import kotlinx.android.synthetic.main.activity_member.*
import kotlinx.android.synthetic.main.layout_title_left.*
import net.cachapa.expandablelayout.ExpandableLayout
import net.idik.lib.slimadapter.SlimAdapter
import org.json.JSONObject
import java.text.DecimalFormat

class MemberActivity : BaseActivity() {

    private val list = ArrayList<CommonData>()
    private val list_cards = ArrayList<CommonData>()

    private var mPrice = ""
    private var levelName = ""
    private var agentId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member)
        setToolbarVisibility(false)
        init_title()

        getData()
    }

    override fun init_title() {
        left_nav_title.text = "升级会员"
        member_sure.visibility = View.GONE

        member_list.load_Linear(baseContext)

        mAdapter = SlimAdapter.create()
                .register<CommonData>(R.layout.item_member_list) { data, injector ->
                    injector.text(R.id.item_member_name, data.levelName)
                            .text(R.id.item_member_price,
                                    "￥${DecimalFormat("########0.0#####").format(data.cost.toDouble())}")
                            .checked(R.id.item_member_check, data.isChecked)
                            .visibility(R.id.item_member_logo, if (data.levelName == "钻石代理") View.VISIBLE else View.GONE)

                            .visibility(R.id.item_member_divider1, if (list.indexOf(data) == 0) View.GONE else View.VISIBLE)
                            .visibility(R.id.item_member_divider2, if (data.levelName == "钻石代理") View.VISIBLE else View.GONE)

                            .with<ExpandableLayout>(R.id.item_member_expand) { view ->
                                if (data.isChecked) view.expand()
                                else view.collapse()
                            }

                            .with<ImageView>(R.id.item_member_img) { view ->
                                GlideApp.with(baseContext)
                                        .load(BaseHttp.baseImg + data.levelExplain)
                                        .dontAnimate()
                                        .into(view)
                            }

                            .clicked(R.id.item_member) {
                                list.filter { it.isChecked }.forEach { it.isChecked = false }

                                data.isChecked = true
                                agentId = data.agentId
                                mPrice = DecimalFormat("########0.0#####").format(data.cost.toDouble())
                                levelName = data.levelName

                                mAdapter.updateData(list).notifyDataSetChanged()
                            }
                }
                .attachTo(member_list)
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.member_sure -> {
                if (levelName.isEmpty()) {
                    toast("请选择升级会员类型")
                    return
                }

                if (ACache.get(baseContext).getAsBoolean("isUpdating", false)) {
                    toast("您的会员升级已经提交，正在处理中")
                    return
                }

                if (list_cards.isNotEmpty()) showSheetDialog()
                else {
                    OkGo.post<CommonModel>(BaseHttp.card_Info_list)
                            .tag(this@MemberActivity)
                            .headers("token", getString("token"))
                            .params("cardType", "0")
                            .execute(object : JacksonDialogCallback<CommonModel>(baseContext, CommonModel::class.java, true) {

                                override fun onSuccess(response: Response<CommonModel>) {

                                    list_cards.addItems(response.body().cards)

                                    if (list_cards.isEmpty()) toast("您还没有添加储蓄卡，无法升级会员")
                                    else {
                                        showSheetDialog()
                                    }
                                }

                            })
                }
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun showSheetDialog() {
        val view = LayoutInflater.from(baseContext).inflate(R.layout.dialog_member_confirm, null) as View
        val iv_close = view.findViewById(R.id.dialog_member_close) as ImageView
        val tv_price = view.findViewById(R.id.dialog_member_price) as TextView
        val tv_type = view.findViewById(R.id.dialog_member_type) as SuperTextView
        val tv_bank = view.findViewById(R.id.dialog_member_bank) as SuperTextView
        val bt_next = view.findViewById(R.id.dialog_member_next) as Button
        val dialog = BottomSheetDialog(baseContext)

        tv_price.text = NumberHelper.fmtMicrometer(mPrice)
        tv_type.setRightString(levelName)
        tv_bank.setRightString("${list_cards[0].bank}储蓄卡 (尾号${list_cards[0].cardNo.substring(list_cards[0].cardNo.length - 4)})")

        iv_close.setOnClickListener { dialog.dismiss() }
        bt_next.setOnClickListener {
            dialog.dismiss()

            // window.decorView.postDelayed({ runOnUiThread { showPaySheetDialog() } }, 300)
            window.decorView.postDelayed({ runOnUiThread { getPayData() } }, 300)
        }

        dialog.setContentView(view)
        dialog.show()
    }

    private fun showPaySheetDialog() {
        if (getString("isPayPwd") != "1") {
            DialogHelper.showDialog(
                    baseContext,
                    "温馨提示",
                    "未设置支付密码，无法进行付款，是否现在去设置？",
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

                    tv_title.text = "确认付款"
                    tv_hint.text = "付款金额${NumberHelper.fmtMicrometer(mPrice)}元"
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
                                    .tag(this@MemberActivity)
                                    .headers("token", getString("token"))
                                    .params("paypawd", psw)
                                    .execute(object : StringDialogCallback(baseContext, false) {

                                        override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                            dismiss()

                                            window.decorView.postDelayed({ runOnUiThread { startPay() } }, 300)
                                        }

                                    })
                        }

                    })

                }
            }
            dialog.setOnShowListener { KeyboardHelper.showSoftInput(this@MemberActivity) }
            dialog.show()
        }
    }

    private fun getPayData() {
        OkGo.post<String>(BaseHttp.allinpay_register)
                .tag(this@MemberActivity)
                .headers("token", getString("token"))
                .params("cardId", list_cards[0].cardId)
                .params("payVipSum", mPrice)
                .params("agentId", agentId)
                .params("cardType", 0)
                .execute(object : StringDialogCallback(baseContext) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        startPay()
                    }

                })
    }

    override fun getData() {
        OkGo.post<CommonModel>(BaseHttp.up_vip_list)
                .tag(this@MemberActivity)
                .headers("token", getString("token"))
                .execute(object : JacksonDialogCallback<CommonModel>(baseContext, CommonModel::class.java, true) {

                    override fun onSuccess(response: Response<CommonModel>) {

                        member_level.text = response.body().levelName
                        list.addItems(response.body().las)

                        if (list.isEmpty()) {
                            dialog("温馨提示", "更高代理级别请联系客服人员！\n客服热线：${response.body().lxwm}") {
                                positiveButton("联系客服") {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + response.body().lxwm))
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                }
                                show()
                            }
                        } else member_sure.visibility = View.VISIBLE

                        mAdapter.updateData(list).notifyDataSetChanged()
                    }

                })
    }

    private fun startPay() {
        val payData = PaaCreator.randomPaa().toString()
        OkLogger.i(payData)
        APPayAssistEx.startPay(
                this@MemberActivity,
                payData,
                APPayAssistEx.MODE_PRODUCT,
                Const.authority)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        /*{
            "allinpay_pay_res": "allinpay_pay_success",
            "payAmount": "1",
            "payTime": "2018-01-15 10:56:03",
            "payOrderId": ""
        }*/
        if (APPayAssistEx.REQUESTCODE == requestCode) {
            if (null != data) {
                val resultJson = JSONObject(data.extras.getString("result"))
                if (!resultJson.isNull(APPayAssistEx.KEY_PAY_RES)
                        && resultJson.getString(APPayAssistEx.KEY_PAY_RES) == APPayAssistEx.RES_SUCCESS) {

                    ACache.get(baseContext).put("isUpdating", true, 5 * 60)
                    OkLogger.d(resultJson.toString())
                    toast("支付成功，请等待后台审核！")
                } else {

                    OkLogger.e(resultJson.toString())
                    toast("支付失败！")
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
