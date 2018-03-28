package com.ruanmeng.credit_card

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.view.View
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.compress.Luban
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.maning.imagebrowserlibrary.MNImageBrowser
import com.ruanmeng.base.*
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.share.Const
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.CommonUtil
import com.ruanmeng.utils.NameLengthFilter
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_real_name.*
import java.io.File

class RealNameActivity : BaseActivity() {

    private var selectList = ArrayList<LocalMedia>()

    private var gender = ""
    private var image_type = 1
    private var image_first = ""
    private var image_second = ""
    private var image_third = ""
    private var image_fourth = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_real_name)
        init_title("实名认证")
    }

    @SuppressLint("SetTextI18n")
    override fun init_title() {
        super.init_title()
        et_name.filters = arrayOf<InputFilter>(NameLengthFilter(12))
        val list = resources.getStringArray(R.array.bank_saving).asList()
        real_hint.text = "注：当前支持的储蓄卡银行有，${list.toString()
                .replace("[", "")
                .replace("]", "")
                .replace(", ", "、")}"

        bt_submit.apply {
            setBackgroundResource(R.drawable.rec_bg_d0d0d0)
            isClickable = false
        }

        et_name.addTextChangedListener(this@RealNameActivity)
        et_card.addTextChangedListener(this@RealNameActivity)

        rg_check.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_check1 -> gender = "0"
                R.id.rb_check2 -> gender = "1"
            }
        }

        rb_check1.performClick()
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.real_img1 -> {
                image_type = 1
                selectPicture(true)
            }
            R.id.real_img2 -> {
                image_type = 2
                selectPicture(true)
            }
            R.id.real_img3 -> {
                image_type = 3
                selectPicture(true)
            }
            R.id.real_img4 -> {
                image_type = 4
                selectPicture(false)
            }
            R.id.real_demo -> MNImageBrowser.showImageBrowser(baseContext, real_demo, 0, arrayListOf(R.mipmap.real_logo))
            R.id.bt_submit -> {
                if (image_first.isEmpty() || image_second.isEmpty()) {
                    toast("请上传身份证正反面照")
                    return
                }

                if (image_third.isEmpty()) {
                    toast("请上传银行卡正面照")
                    return
                }

                if (image_fourth.isEmpty()) {
                    toast("请上传手持身份证、银行卡合照")
                    return
                }

                if (!CommonUtil.IDCardValidate(et_card.text.trim().toString())) {
                    toast("请输入正确的身份证号")
                    return
                }

                OkGo.post<String>(BaseHttp.certification_sub)
                        .tag(this@RealNameActivity)
                        .isMultipart(true)
                        .headers("token", getString("token"))
                        .params("userName", et_name.text.trim().toString())
                        .params("cardNo", et_card.text.trim().toString())
                        .params("sex", gender)
                        .params("idCardPhoto", File(image_first))
                        .params("idCardBackPhoto", File(image_second))
                        .params("bankCardPhoto", File(image_third))
                        .params("personPhoto", File(image_fourth))
                        .execute(object : StringDialogCallback(baseContext) {
                            /*{
                                "msg": "提交成功，请等待审核",
                                "msgcode": 100
                            }*/
                            override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                                toast(msg)
                                putString("isPass", "0")
                                ActivityStack.getScreenManager().popActivities(this@RealNameActivity::class.java)
                            }

                        })
            }
        }
    }

    private fun selectPicture(isCrop: Boolean) {
        PictureSelector.create(this@RealNameActivity)
                .openGallery(PictureMimeType.ofImage())
                .theme(R.style.picture_customer_style)
                .maxSelectNum(1)
                .minSelectNum(1)
                .imageSpanCount(4)
                .selectionMode(PictureConfig.MULTIPLE)
                .previewImage(true)
                .previewVideo(false)
                .isCamera(true)
                .imageFormat(PictureMimeType.PNG)
                .isZoomAnim(true)
                .setOutputCameraPath(Const.SAVE_FILE)
                .compress(true)
                .glideOverride(160, 160)
                .enableCrop(isCrop)
                .withAspectRatio(4, 3)
                .hideBottomControls(true)
                .compressSavePath(cacheDir.absolutePath)
                .freeStyleCropEnabled(false)
                .circleDimmedLayer(false)
                .showCropFrame(true)
                .showCropGrid(true)
                .isGif(false)
                .openClickSound(false)
                .selectionMedia(selectList.apply { clear() })
                .previewEggs(true)
                .minimumCompressSize(100)
                .forResult(PictureConfig.CHOOSE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST -> {
                    // 图片选择结果回调
                    selectList = PictureSelector.obtainMultipleResult(data) as ArrayList<LocalMedia>
                    // LocalMedia 里面返回三种path
                    // 1.media.getPath(); 为原图path
                    // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                    // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                    // 如果裁剪并压缩了，已取压缩路径为准，因为是先裁剪后压缩的

                    when (image_type) {
                        1 -> {
                            image_first = selectList[0].compressPath

                            GlideApp.with(baseContext)
                                    .load(image_first)
                                    .dontAnimate()
                                    .into(real_img1)

                            compress(1, selectList[0].compressPath)
                        }
                        2 -> {
                            image_second = selectList[0].compressPath

                            GlideApp.with(baseContext)
                                    .load(image_second)
                                    .dontAnimate()
                                    .into(real_img2)

                            compress(2, selectList[0].compressPath)
                        }
                        3 -> {
                            image_third = selectList[0].compressPath

                            GlideApp.with(baseContext)
                                    .load(image_third)
                                    .dontAnimate()
                                    .into(real_img3)

                            compress(3, selectList[0].compressPath)
                        }
                        4 -> {
                            image_fourth = selectList[0].compressPath

                            GlideApp.with(baseContext)
                                    .load(image_fourth)
                                    .dontAnimate()
                                    .into(real_img4)

                            compress(4, selectList[0].compressPath)
                        }
                    }
                }
            }
        }
    }

    private fun compress(type: Int, path: String) {
        Flowable.just<List<LocalMedia>>(listOf(LocalMedia().apply { this.path = path }))
                .map { list ->
                    return@map Luban.with(baseContext)
                            .setTargetDir(cacheDir.absolutePath)
                            .ignoreBy(400)
                            .loadLocalMedia(list)
                            .get()
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { files ->
                    when (type) {
                        1 -> image_first = files[0].absolutePath
                        2 -> image_second = files[0].absolutePath
                        3 -> image_third = files[0].absolutePath
                        4 -> image_fourth = files[0].absolutePath
                    }
                }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (et_name.text.isNotBlank()
                && et_card.text.isNotBlank()) {
            bt_submit.setBackgroundResource(R.drawable.rec_bg_blue)
            bt_submit.isClickable = true
        } else {
            bt_submit.setBackgroundResource(R.drawable.rec_bg_d0d0d0)
            bt_submit.isClickable = false
        }
    }
}
