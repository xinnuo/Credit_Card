package com.ruanmeng.credit_card

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.flyco.dialog.widget.base.BottomBaseDialog
import com.jungly.gridpasswordview.GridPasswordView
import com.jungly.gridpasswordview.PasswordType
import com.lzy.extend.StringDialogCallback
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.makeramen.roundedimageview.RoundedImageView
import com.ruanmeng.base.*
import com.ruanmeng.model.CardData
import com.ruanmeng.model.CommonModel
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.DialogHelper
import com.ruanmeng.utils.KeyboardHelper
import com.ruanmeng.utils.NumberHelper
import kotlinx.android.synthetic.main.activity_gather.*
import net.idik.lib.slimadapter.SlimAdapter
import org.json.JSONObject
import java.util.*

class GatherActivity : BaseActivity() {

    private val list = ArrayList<CardData>()
    private var cardNo = ""
    private var source = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gather)
        init_title("我要收款")

        getData()
    }

    override fun init_title() {
        super.init_title()

        gather_ok.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
        gather_ok.isClickable = false

        gather_count.addTextChangedListener(this@GatherActivity)
    }

    @SuppressLint("SetTextI18n")
    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.gather_bank -> {
                if (list.isEmpty()) return

                showSheetDialog()
            }
            R.id.gather_type_ll -> {
                DialogHelper.showItemDialog(
                        baseContext,
                        "选择收款方式",
                        Arrays.asList("在线收款", "二维码收款")) { position, name ->
                    gather_type.text = name
                    source = if (position == 0) "B" else "T"
                }
            }
            R.id.gather_ok -> {
                if (cardNo.isEmpty()) {
                    toast("请选择收款信用卡")
                    return
                }

                if (source.isEmpty()) {
                    toast("请选择收款方式")
                    return
                }

                if (gather_count.text.toString().toDouble() < 100) {
                    toast("最低收款金额不少于100.0元")
                    return
                }

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
                    val mCount = gather_count.text.toString()
                    val dialog = object : BottomDialog(baseContext) {

                        private lateinit var iv_close: ImageView
                        private lateinit var tv_title: TextView
                        private lateinit var tv_hint: TextView
                        private lateinit var gpv_pwd: GridPasswordView

                        override fun onCreateView(): View {
                            val view = View.inflate(baseContext, R.layout.dialog_memeber_pwd, null)

                            iv_close = view.findViewById(R.id.withdraw_close)
                            tv_title = view.findViewById(R.id.withdraw_title)
                            tv_hint = view.findViewById(R.id.withdraw_hint)
                            gpv_pwd = view.findViewById(R.id.withdraw_pwd)

                            tv_title.text = "确认收款"
                            tv_hint.text = "收款金额${NumberHelper.fmtMicrometer(mCount)}元"
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
                                            .tag(this@GatherActivity)
                                            .headers("token", getString("token"))
                                            .params("paypawd", psw)
                                            .execute(object : StringDialogCallback(baseContext, false) {

                                                override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                                    dismiss()
                                                    getPay(mCount)
                                                }

                                            })
                                }

                            })

                        }
                    }
                    dialog.setOnShowListener { KeyboardHelper.showSoftInput(this@GatherActivity) }
                    dialog.show()
                }
            }
        }
    }

    private fun getPay(count: String) {
        OkGo.post<String>(BaseHttp.appYee_repayment)
                .tag(this@GatherActivity)
                .headers("token", getString("token"))
                .params("carno", cardNo)
                .params("amount", count)
                .params("source", source)
                .execute(object : StringDialogCallback(baseContext, false) {
                    /*{
                        "msg": "收款成功",
                        "msgcode": 100,
                        "object": "https://shouyin.yeepay.com/nc-cashier-wap/wap/request/10016127996/rZZIga6mFzmWwVHne*OWsg%3D%3D"
                    }*/
                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        val obj = JSONObject(response.body())

                        when (source) {
                            "B" -> {
                                val intent = Intent(baseContext, WebActivity::class.java)
                                intent.putExtra("title", "收款支付")
                                intent.putExtra("url", obj.getString("object"))
                                startActivity(intent)

                                ActivityStack.getScreenManager().popActivities(this@GatherActivity::class.java)
                            }
                            "T" -> {
                                val intent = Intent(baseContext, GatherCodeActivity::class.java)
                                intent.putExtra("title", "收款二维码")
                                intent.putExtra("url", obj.getString("object"))
                                startActivity(intent)

                                ActivityStack.getScreenManager().popActivities(this@GatherActivity::class.java)
                            }
                        }

                    }

                })
    }

    override fun getData() {
        OkGo.post<CommonModel>(BaseHttp.creditcard_data)
                .tag(this@GatherActivity)
                .headers("token", getString("token"))
                .execute(object : JacksonDialogCallback<CommonModel>(baseContext, CommonModel::class.java, true) {

                    @SuppressLint("SetTextI18n")
                    override fun onSuccess(response: Response<CommonModel>) {

                        list.addItems(response.body().creditcards)

                        if (list.isEmpty()) toast("您还没有添加信用卡")
                        else {
                            cardNo = list[0].creditcard
                            gather_card.text = list[0].bank
                            gather_tail.text = "（${list[0].creditcard.substring(list[0].creditcard.length - 4)}）"
                            list[0].isChecked = true
                        }
                    }

                })
    }

    @SuppressLint("InflateParams", "SetTextI18n")
    private fun showSheetDialog() {
        val view = LayoutInflater.from(baseContext).inflate(R.layout.dialog_cash_bank, null) as View
        val tv_title = view.findViewById(R.id.dialog_bankcard_title) as TextView
        val iv_close = view.findViewById(R.id.dialog_bankcard_close) as ImageView
        val recyclerView = view.findViewById(R.id.dialog_bankcard_list) as RecyclerView
        val dialog = BottomSheetDialog(baseContext)

        tv_title.text = "选择收款信用卡"
        iv_close.setOnClickListener { dialog.dismiss() }

        recyclerView.apply {
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(baseContext)
            adapter = SlimAdapter.create()
                    .register<CardData>(R.layout.item_bank_list, { data, injector ->
                        injector.visibility(R.id.item_bank_img_check, if (data.isChecked) View.VISIBLE else View.INVISIBLE)
                                .visibility(R.id.item_bank_divider, if (list.indexOf(data) != list.size - 1) View.GONE else View.VISIBLE)
                                .text(R.id.item_bank_name, data.bank)
                                .text(R.id.item_bank_tail,
                                        "(${data.creditcard.substring(data.creditcard.length - 4)})")

                                .with<RoundedImageView>(R.id.item_bank_img, { view ->
                                    when (data.bank) {
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
                                })

                                .clicked(R.id.item_bank, {
                                    list.filter { it.isChecked }.forEach { it.isChecked = false }
                                    data.isChecked = true
                                    dialog.dismiss()

                                    cardNo = data.creditcard
                                    gather_card.text = data.bank
                                    gather_tail.text = "（${data.creditcard.substring(data.creditcard.length - 4)}）"
                                })
                    })
                    .attachTo(recyclerView)
        }
        (recyclerView.adapter as SlimAdapter).updateData(list).notifyDataSetChanged()

        dialog.setContentView(view)
        dialog.show()
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (gather_count.text.isNotBlank()) {
            gather_ok.setBackgroundResource(R.drawable.rec_bg_blue)
            gather_ok.isClickable = true
        } else {
            gather_ok.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
            gather_ok.isClickable = false
        }

        if (s.isNotEmpty()) {
            if ("." == s.toString()) {
                gather_count.setText("0.")
                gather_count.setSelection(gather_count.text.length) //设置光标的位置
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
