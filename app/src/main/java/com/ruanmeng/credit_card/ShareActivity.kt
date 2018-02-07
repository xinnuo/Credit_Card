package com.ruanmeng.credit_card

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.BottomSheetDialog
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.maning.mndialoglibrary.MToast
import com.ruanmeng.base.*
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.share.Const
import com.ruanmeng.utils.Tools
import com.umeng.socialize.ShareAction
import com.umeng.socialize.UMShareAPI
import com.umeng.socialize.bean.SHARE_MEDIA
import com.umeng.socialize.media.UMImage
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_share.*
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ShareActivity : BaseActivity() {

    private var qrcode: Bitmap? = null
    private var mSaveFile: File? = null

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

                    ShareAction(baseContext)
                            .setPlatform(SHARE_MEDIA.WEIXIN)
                            .withText("诸葛信用管家")
                            .withMedia(UMImage(baseContext, Tools.getViewBitmap(share_fl)))
                            .share()
                }
                ll_circle.setOnClickListener {
                    dialog.dismiss()

                    ShareAction(baseContext)
                            .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                            .withText("诸葛信用管家")
                            .withMedia(UMImage(baseContext, Tools.getViewBitmap(share_fl)))
                            .share()
                }
                ll_qq.setOnClickListener {
                    dialog.dismiss()

                    ShareAction(baseContext)
                            .setPlatform(SHARE_MEDIA.QQ)
                            .withText("诸葛信用管家")
                            .withMedia(UMImage(baseContext, Tools.getViewBitmap(share_fl)))
                            .share()
                }
                ll_zone.setOnClickListener {
                    dialog.dismiss()

                    ShareAction(baseContext)
                            .setPlatform(SHARE_MEDIA.QZONE)
                            .withText("诸葛信用管家")
                            .withMedia(UMImage(baseContext, Tools.getViewBitmap(share_fl)))
                            .share()
                }
                tv_cancel.setOnClickListener { dialog.dismiss() }

                if (mSaveFile == null) saveFile(Tools.getViewBitmap(share_fl))
                else showText("图片保存至：${mSaveFile!!.absoluteFile}")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        UMShareAPI.get(this@ShareActivity).onActivityResult(requestCode, resultCode, data)
    }

    private fun showText(hint: String) {
        MToast.makeTextLong(this, hint).apply {
            setGravity(Gravity.CENTER, 0 ,0)
            show()
        }
    }

    /**
     * 保存Bitmap图片为本地文件
     */
    private fun saveFile(bitmap: Bitmap?) {
        val dir = File(Environment.getExternalStorageDirectory().absolutePath, Const.SAVE_FILE)
        if (!dir.exists()) dir.mkdirs()
        mSaveFile = File(dir, "/qrcode_share.jpg")
        try {
            if (!mSaveFile!!.exists()) mSaveFile!!.createNewFile()
            val out = FileOutputStream(mSaveFile)
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()

            showText("图片保存至：${mSaveFile!!.absoluteFile}")

            // 保存图片到相册显示的方法（没有则只有重启后才有）
            val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            val uri = Uri.fromFile(mSaveFile)
            intent.data = uri
            sendBroadcast(intent)
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    override fun getData() {
        OkGo.post<String>(BaseHttp.get_userQrcode)
                .tag(this@ShareActivity)
                .headers("token", getString("token"))
                .execute(object : StringDialogCallback(baseContext) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        val qr_code = JSONObject(response.body()).getString("qrcode")

                        Observable.create(ObservableOnSubscribe<Bitmap> { e ->
                            qrcode = QRCodeEncoder.syncEncodeQRCode(
                                    qr_code,
                                    BGAQRCodeUtil.dp2px(baseContext, 200f),
                                    Color.BLACK,
                                    BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                            e.onNext(qrcode)
                        }).subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe { bitmap ->
                                    share_qrcode.setImageBitmap(bitmap)
                                }

                    }

                })
    }

    override fun onDestroy() {
        super.onDestroy()
        UMShareAPI.get(this@ShareActivity).release()
    }
}
