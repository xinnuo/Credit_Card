package com.ruanmeng.credit_card

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.CompoundButton
import cn.jpush.android.api.JPushInterface
import com.lzy.extend.StringDialogCallback
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.lzy.okgo.utils.OkLogger
import com.ruanmeng.RongCloudContext
import com.ruanmeng.base.*
import com.ruanmeng.fragment.MainFirstFragment
import com.ruanmeng.fragment.MainSecondFragment
import com.ruanmeng.fragment.MainThirdFragment
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.CommonModel
import com.ruanmeng.model.CountMessageEvent
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.share.Const
import com.ruanmeng.utils.OkGoUpdateHttpUtil
import com.ruanmeng.utils.Tools
import com.vector.update_app.UpdateAppBean
import com.vector.update_app.UpdateAppManager
import com.vector.update_app.service.DownloadService
import com.vector.update_app_kotlin.check
import com.vector.update_app_kotlin.updateApp
import io.rong.imkit.RongIM
import io.rong.imlib.RongIMClient
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import java.io.File
import java.util.ArrayList

class MainActivity : BaseActivity() {

    private var isNewVersion = false
    private var isDownloading = false
    private var isFirst = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setToolbarVisibility(false)
        init_title()

        JPushInterface.resumePush(applicationContext)
        //设置别名（先注册）
        JPushInterface.setAlias(
                applicationContext,
                Const.JPUSH_SEQUENCE,
                getString("token"))

        if (getString("rongtoken").isNotEmpty()) {
            connect(getString("rongtoken"))
        }

        checkUpdate()

        main_check1.performClick()
    }

    override fun init_title() {
        main_check1.setOnCheckedChangeListener(this)
        main_check2.setOnCheckedChangeListener(this)
        main_check3.setOnCheckedChangeListener(this)
    }

    override fun onStart() {
        super.onStart()

        getIdentityInfo()
        getMessageCount()
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        // instantiateItem从FragmentManager中查找Fragment，找不到就getItem新建一个，
        // setPrimaryItem设置隐藏和显示，最后finishUpdate提交事务。
        if (isChecked) {
            val fragment = mFragmentPagerAdapter
                    .instantiateItem(main_container, buttonView.id) as Fragment
            mFragmentPagerAdapter.setPrimaryItem(main_container, 0, fragment)
            mFragmentPagerAdapter.finishUpdate(main_container)
        }
    }

    private val mFragmentPagerAdapter = object : FragmentPagerAdapter(
            supportFragmentManager) {

        override fun getItem(position: Int): Fragment = when (position) {
            R.id.main_check1 -> MainFirstFragment()
            R.id.main_check2 -> MainSecondFragment()
            R.id.main_check3 -> MainThirdFragment()
            else -> MainFirstFragment()
        }

        override fun getCount(): Int = 3
    }

    override fun doClick(v: View) {
        super.doClick(v)
        when (v.id) {
            R.id.main_right -> startActivity(MessageActivity::class.java)
            R.id.first_agency -> startActivity(AgencyActivity::class.java)
            R.id.first_pay -> startActivity(RepaymentActivity::class.java)
            R.id.first_get -> {
                when (getString("isPass")) {
                    "-1" -> toast("实名认证审核未通过，无法进行收款")
                    "0" -> toast("实名认证信息正在审核中，无法进行收款")
                    "1" -> startActivity(GatherActivity::class.java)
                }
            }
            R.id.first_card -> startActivity(OnlineCardActivity::class.java)
            R.id.first_express -> {
                val intent = Intent(baseContext, WebActivity::class.java)
                intent.putExtra("title", "快递查询")
                startActivity(intent)
            }
            R.id.first_offend -> {
                val intent = Intent(baseContext, WebActivity::class.java)
                intent.putExtra("title", "违章查询")
                startActivity(intent)
            }
            R.id.first_new -> {
                val intent = Intent(baseContext, WebActivity::class.java)
                intent.putExtra("title", "新手攻略")
                startActivity(intent)
            }
            R.id.first_safe -> {
                val intent = Intent(baseContext, WebActivity::class.java)
                intent.putExtra("title", "安全保障")
                startActivity(intent)
            }
            R.id.second_update -> startActivity(MemberActivity::class.java)
        }
    }

    private fun getIdentityInfo() {
        OkGo.post<String>(BaseHttp.identity_Info)
                .tag(this@MainActivity)
                .headers("token", getString("token"))
                .execute(object : StringDialogCallback(baseContext, false) {

                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {
                        val obj = JSONObject(response.body())

                        putString("isPass", obj.getString("pass"))
                        putString("isPayPwd", obj.getString("payPass"))
                        putString("real_name", if (obj.isNull("userName")) "" else obj.getString("userName"))
                        putString("real_sex", if (obj.isNull("sex")) "" else obj.getString("sex"))
                        putString("real_IDCard", if (obj.isNull("cardNo")) "" else obj.getString("cardNo"))

                        getCardData()
                    }

                })
    }

    private fun getMessageCount() {
        OkGo.post<String>(BaseHttp.get_msgCount)
                .tag(this@MainActivity)
                .headers("token", getString("token"))
                .execute(object : StringDialogCallback(baseContext, false) {
                    /*{
                        "count": 5,
                        "msg": "",
                        "msgcode": 100,
                        "success": true
                    }*/
                    override fun onSuccessResponse(response: Response<String>, msg: String, msgCode: String) {

                        val msgCount = JSONObject(response.body()).getString("count")
                        putString("msgCount", msgCount)
                        EventBus.getDefault().post(CountMessageEvent("未读消息", msgCount))
                    }

                })
    }

    private fun getCardData() {
        OkGo.post<CommonModel>(BaseHttp.depositcard_data)
                .tag(this@MainActivity)
                .headers("token", getString("token"))
                .execute(object : JacksonDialogCallback<CommonModel>(baseContext, CommonModel::class.java) {

                    override fun onSuccess(response: Response<CommonModel>) {

                        val list = ArrayList<CommonData>()
                        list.addItems(response.body().depositcards)

                        if (!isFirst && !isNewVersion) {
                            isFirst = true

                            when (getString("isPass")) {
                                "-1" -> showHintDialog(
                                        "实名认证审核未通过，是否重新认证？",
                                        "去认证") {
                                    startActivity(RealNameActivity::class.java)
                                }
                                "0" -> {
                                }
                                "1" -> if (list.isEmpty()) {
                                    showHintDialog(
                                            "您还未绑定储蓄卡，是否现在去添加储蓄卡？",
                                            "去添加") {
                                        startActivity(BankcardActivity::class.java)
                                    }
                                }
                                else -> showHintDialog(
                                        "您还没有进行实名认证，是否现在去认证？",
                                        "去认证") {
                                    startActivity(RealNameActivity::class.java)
                                }
                            }
                        }
                    }

                })
    }

    private var exitTime: Long = 0
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - exitTime > 2000) {
                toast("再按一次退出程序")
                exitTime = System.currentTimeMillis()
            } else {
                onBackPressed()
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onBackPressed() {
        RongCloudContext.getInstance().clearNotificationMessage()
        RongIM.getInstance().disconnect()
        super.onBackPressed()
    }

    private fun connect(token: String) {
        /**
         * IMKit SDK调用第二步,建立与服务器的连接
         *
         * <p>连接服务器，在整个应用程序全局，只需要调用一次，需在 {@link #init(Context)} 之后调用。</p>
         * <p>如果调用此接口遇到连接失败，SDK 会自动启动重连机制进行最多10次重连，分别是1, 2, 4, 8, 16, 32, 64, 128, 256, 512秒后。
         * 在这之后如果仍没有连接成功，还会在当检测到设备网络状态变化时再次进行重连。</p>
         *
         * @param token    从服务端获取的用户身份令牌（Token）。
         * @param callback 连接回调。
         * @return RongIM  客户端核心类的实例。
         */
        RongIM.connect(token, object : RongIMClient.ConnectCallback() {
            /*
             * 连接融云成功，返回当前 token 对应的用户 id
             */
            override fun onSuccess(userid: String) {
                OkLogger.i("融云连接成功， 用户ID：" + userid)
                OkLogger.i(RongIMClient.getInstance().currentConnectionStatus.message)

                RongCloudContext.getInstance().connectedListener()
            }

            /*
             * 连接融云失败 errorCode 错误码，可到官网 查看错误码对应的注释
             */
            override fun onError(errorCode: RongIMClient.ErrorCode) {
                OkLogger.e("融云连接失败，错误码：" + errorCode.message)
            }

            /*
             * Token 错误。可以从下面两点检查
             * 1.  Token 是否过期，如果过期您需要向 App Server 重新请求一个新的 Token
             * 2.  token 对应的 appKey 和工程里设置的 appKey 是否一致
             */
            override fun onTokenIncorrect() {
                OkLogger.e("融云token错误！！！")
            }
        })
    }

    private fun showHintDialog(hint: String, hintButton: String, click: () -> Unit) {
        dialog("温馨提示", hint) {
            positiveButton(hintButton) { click() }
            show()
        }
    }

    /**
     * 版本更新
     */
    private fun checkUpdate() {
        //下载路径
        val path = Environment.getExternalStorageDirectory().absolutePath + Const.SAVE_FILE

        updateApp(BaseHttp.get_versioninfo, OkGoUpdateHttpUtil()) {
            //设置请求方式，默认get
            isPost = true
            //设置apk下砸路径
            targetPath = path
        }.check {
            parseJson {
                val obj = JSONObject(it)
                val version_new = obj.optString("versionNo").replace(".", "").toInt()
                val version_old = Tools.getVersion(baseContext).replace(".", "").toInt()

                if (version_new < 111)
                    EventBus.getDefault().post(CountMessageEvent("在线办卡", ""))

                UpdateAppBean()
                        //（必须）是否更新Yes,No
                        .setUpdate(if (version_new > version_old) "Yes" else "No")
                        //（必须）新版本号，
                        .setNewVersion(obj.optString("versionNo"))
                        //（必须）下载地址
                        // .setApkFileUrl(obj.optString("url"))
                        .setApkFileUrl(Const.URL_DOWNLOAD)
                        //（必须）更新内容
                        .setUpdateLog(obj.optString("content"))
                        //是否强制更新，可以不设置
                        // .setConstraint(obj.optString("forces") == "1")
                        .setConstraint(false)
            }
            hasNewApp { updateApp, updateAppManager ->
                isNewVersion = true
                showDownloadDialog(updateApp, updateAppManager)
            }
        }
    }

    /**
     * 自定义对话框
     */
    private fun showDownloadDialog(updateApp: UpdateAppBean, updateAppManager: UpdateAppManager) {
        dialog("版本更新", "是否升级到${updateApp.newVersion}版本？\n\n${updateApp.updateLog}") {
            positiveButton("升级") { }
            cancellable(!updateApp.isConstraint)
            onKey { _, _ -> return@onKey updateApp.isConstraint }
            if (!updateApp.isConstraint) negativeButton("暂不升级") { }
            show()
            // 必须要先调show()方法，后面的getButton才有效
            getPositiveButton().setOnClickListener { view ->
                if (!updateApp.isConstraint) {
                    dismiss()
                    updateAppManager.download()
                } else {
                    when (isDownloading) {
                        true -> toast("正在下载中")
                        false -> updateAppManager.download(object : DownloadService.DownloadCallback {

                            override fun onInstallAppAndAppOnForeground(file: File?): Boolean = false

                            override fun onFinish(file: File): Boolean {
                                isDownloading = false
                                (view as Button).text = "升级"
                                return true
                            }

                            override fun setMax(totalSize: Long) = OkLogger.e("totalSize: $totalSize")

                            override fun onProgress(progress: Float, totalSize: Long) = OkLogger.e("progress: $progress")

                            override fun onError(msg: String) = OkLogger.e(msg)

                            override fun onStart() {
                                isDownloading = true
                                (view as Button).text = "下载中"
                            }

                        })
                    }
                }
            }
        }
    }
}
