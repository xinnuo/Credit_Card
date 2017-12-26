package com.ruanmeng.credit_card

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.base.GlideApp
import com.ruanmeng.base.getString
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.Tools
import com.umeng.socialize.UMShareAPI
import kotlinx.android.synthetic.main.activity_share.*
import org.json.JSONObject
import android.content.Intent
import android.graphics.Bitmap
import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder
import com.umeng.socialize.ShareAction
import com.umeng.socialize.bean.SHARE_MEDIA
import com.umeng.socialize.media.UMImage
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers


class ShareActivity : BaseActivity() {

    private var qrcode: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_share)
        init_title("分享赚钱")

        getData()
    }

    @SuppressLint("SetTextI18n")
    override fun init_title() {
        super.init_title()
        ivRight.visibility = View.VISIBLE

        share_hint.text = "我是${getString("nickName")}"
        GlideApp.with(baseContext)
                .load(BaseHttp.baseImg + getString("userhead"))
                .placeholder(R.mipmap.default_user)
                .error(R.mipmap.default_user)
                .dontAnimate()
                .into(share_img)
    }

    @SuppressLint("InflateParams")
    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.iv_nav_right -> {
                if (qrcode == null) return

                val view = LayoutInflater.from(baseContext).inflate(R.layout.dialog_qrcode_share, null) as View
                val ll_wechat = view.findViewById(R.id.ll_dialog_share_wechat) as LinearLayout
                val ll_circle = view.findViewById(R.id.ll_dialog_share_circle) as LinearLayout
                val ll_qq = view.findViewById(R.id.ll_dialog_share_qq) as LinearLayout
                val ll_zone = view.findViewById(R.id.ll_dialog_share_zone) as LinearLayout
                val tv_cancel = view.findViewById(R.id.btn_dialog_share_cancel) as TextView

                val dialog = BottomSheetDialog(baseContext)
                dialog.setContentView(view)
                dialog.show()

                ll_wechat.setOnClickListener {
                    dialog.dismiss()

                    /*ShareAction(baseContext)
                            .setPlatform(SHARE_MEDIA.WEIXIN)
                            .withTitle("")
                            .withText("")
                            .withTargetUrl("")
                            .withMedia(UMImage(baseContext, File("")))
                            .share()*/
                }
                ll_circle.setOnClickListener {
                    dialog.dismiss()

                    /*ShareAction(baseContext)
                            .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                            .withTitle("")
                            .withText("")
                            .withTargetUrl("")
                            .withMedia(UMImage(baseContext, File("")))
                            .share()*/
                }
                ll_qq.setOnClickListener {
                    dialog.dismiss()

                    ShareAction(baseContext)
                            .setPlatform(SHARE_MEDIA.QQ)
                            .withText("诸葛信用管家")
                            .withMedia(UMImage(baseContext, qrcode))
                            .share()
                }
                ll_zone.setOnClickListener {
                    dialog.dismiss()

                    ShareAction(baseContext)
                            .setPlatform(SHARE_MEDIA.QZONE)
                            .withText("诸葛信用管家")
                            .withMedia(UMImage(baseContext, qrcode))
                            .share()
                }
                tv_cancel.setOnClickListener { dialog.dismiss() }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        UMShareAPI.get(this@ShareActivity).onActivityResult(requestCode, resultCode, data)
    }

    override fun getData() {
        OkGo.post<String>(BaseHttp.get_userQrcode)
                .tag(this@ShareActivity)
                .headers("token", getString("token"))
                .execute(object : StringDialogCallback(baseContext) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        val qr_code = JSONObject(response.body()).getString("qrcode")

                        Observable.create(ObservableOnSubscribe<Bitmap> { e ->
                            qrcode = QRCodeEncoder.syncEncodeQRCode(qr_code, BGAQRCodeUtil.dp2px(baseContext, 200f))
                            e.onNext(qrcode)
                        }).subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe { bitmap -> share_qrcode.setImageBitmap(bitmap) }

                    }

                })
    }

    override fun onDestroy() {
        super.onDestroy()
        UMShareAPI.get(this@ShareActivity).release()
    }
}
