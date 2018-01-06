package com.ruanmeng.credit_card

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.ruanmeng.base.BaseActivity
import com.ruanmeng.utils.Tools
import com.umeng.socialize.ShareAction
import com.umeng.socialize.UMShareAPI
import com.umeng.socialize.bean.SHARE_MEDIA
import com.umeng.socialize.media.UMImage
import kotlinx.android.synthetic.main.activity_gather_code.*

class GatherCodeActivity : BaseActivity() {

    private var qrcode: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gather_code)
        init_title("收款二维码")
    }

    override fun init_title() {
        super.init_title()
        ivRight.visibility = View.VISIBLE

        val url = intent.getStringExtra("url")
        qrcode = Tools.strToBitmap(url)
        gather_qrcode.setImageBitmap(qrcode)
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

                    ShareAction(baseContext)
                            .setPlatform(SHARE_MEDIA.WEIXIN)
                            .withText("诸葛信用管家")
                            .withMedia(UMImage(baseContext, qrcode))
                            .share()
                }
                ll_circle.setOnClickListener {
                    dialog.dismiss()

                    ShareAction(baseContext)
                            .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                            .withText("诸葛信用管家")
                            .withMedia(UMImage(baseContext, qrcode))
                            .share()
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
        UMShareAPI.get(this@GatherCodeActivity).onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        UMShareAPI.get(this@GatherCodeActivity).release()
    }
}
