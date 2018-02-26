package com.ruanmeng.credit_card

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.view.WindowManager
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.cache.CacheMode
import com.lzy.okgo.model.Response
import com.ruanmeng.adapter.GuideAdapter
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonModel
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.utils.ActivityStack
import com.ruanmeng.utils.CountDownTimer
import com.yanzhenjie.permission.AndPermission
import com.yanzhenjie.permission.PermissionListener
import kotlinx.android.synthetic.main.activity_guide.*

class GuideActivity : BaseActivity() {

    private var isReady: Boolean = false
    private lateinit var timer: CountDownTimer

    @SuppressLint("HandlerLeak")
    private var handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (isReady) getData()
            else isReady = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //隐藏状态栏（全屏）
        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)

        //取消全屏
        // window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        setContentView(R.layout.activity_guide)
        transparentStatusBar(false)

        window.decorView.postDelayed({ handler.sendEmptyMessage(0) }, 2000)

        AndPermission.with(this@GuideActivity)
                .permission(
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .callback(object : PermissionListener {
                    override fun onSucceed(requestCode: Int, grantPermissions: MutableList<String>) {
                        handler.sendEmptyMessage(0)
                    }

                    override fun onFailed(requestCode: Int, deniedPermissions: MutableList<String>) {
                        toast("请求权限被拒绝")
                        onBackPressed()
                    }
                })
                .start()

        timer = object : CountDownTimer(4000, 1000) {

            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                guide_in.text = "跳过 ${Math.ceil(millisUntilFinished * 1.0 / 1000).toInt()}S"
            }

            override fun onFinish() = quitGuide()
        }

        guide_in.setOnClickListener {
            timer.cancel()
            quitGuide()
        }
    }

    private fun quitGuide() {
        startActivity(if (getBoolean("isLogin")) MainActivity::class.java else LoginActivity::class.java)
        ActivityStack.getScreenManager().popActivities(this@GuideActivity::class.java)
    }

    private fun startGuide(imgs: ArrayList<String>) {
        val mLoopAdapter = GuideAdapter(baseContext)
        guide_banner.apply {
            setAdapter(mLoopAdapter)
            setHintViewVisibility(false)
            setOnItemSelectListener { position ->

                timer.cancel()
                if (position == imgs.size - 1) timer.start()
                else guide_in.text = "跳过"
            }
        }
        mLoopAdapter.setImgs(imgs)
        guide_in.visibility = View.VISIBLE
        if (imgs.size == 1) timer.start()
    }

    override fun getData() {
        OkGo.post<CommonModel>(BaseHttp.guide_info)
                .tag(this@GuideActivity)
                .cacheMode(CacheMode.REQUEST_FAILED_READ_CACHE)
                .cacheKey(BaseHttp.guide_info)
                .execute(object : JacksonDialogCallback<CommonModel>(baseContext, CommonModel::class.java) {

                    override fun onSuccess(response: Response<CommonModel>) {
                        val flag = getString("guide_flag")
                        val flag_get = response.body().changge
                        val msgCode = response.body().msgcode

                        if (msgCode == "100") {
                            if (flag == flag_get) quitGuide()
                            else {
                                putString("guide_flag", flag_get)

                                val list = ArrayList<String>()
                                list.addItems(response.body().imgs)
                                startGuide(list)
                            }
                        } else quitGuide()
                    }

                    override fun onError(response: Response<CommonModel>) {
                        super.onError(response)
                        quitGuide()
                    }

                })
    }
}
