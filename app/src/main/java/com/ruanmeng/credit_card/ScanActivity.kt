package com.ruanmeng.credit_card

import android.content.Context
import android.os.Bundle
import android.os.Vibrator
import android.view.WindowManager
import cn.bingoogolapple.qrcode.core.QRCodeView
import com.lzy.okgo.utils.OkLogger
import com.ruanmeng.base.BaseActivity
import kotlinx.android.synthetic.main.activity_scan.*

class ScanActivity : BaseActivity(), QRCodeView.Delegate {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //隐藏状态栏（全屏）
        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_scan)
        transparentStatusBar(false)
        init_title()

        scan_view.startSpotDelay(1000)
    }

    override fun init_title() {
        scan_view.setDelegate(this@ScanActivity)
    }

    override fun onStart() {
        super.onStart()
        scan_view.startCamera()
        scan_view.showScanRect()
    }

    override fun onStop() {
        super.onStop()
        scan_view.stopCamera()
    }

    override fun onDestroy() {
        scan_view.onDestroy()
        super.onDestroy()
    }

    @Suppress("DEPRECATION")
    private fun vibrate() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(200)
    }

    override fun onScanQRCodeSuccess(result: String) {
        vibrate()
        OkLogger.i(result)
    }

    override fun onScanQRCodeOpenCameraError() {
        OkLogger.e("打开相机失败！")
    }
}
