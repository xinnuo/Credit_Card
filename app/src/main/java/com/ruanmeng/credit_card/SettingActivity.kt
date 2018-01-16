package com.ruanmeng.credit_card

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AlertDialog
import com.lzy.extend.jackson.JacksonDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.model.CommonData
import com.ruanmeng.model.CommonModel
import com.ruanmeng.share.BaseHttp
import com.ruanmeng.share.Const
import com.ruanmeng.utils.OkGoUpdateHttpUtil
import com.ruanmeng.utils.Tools
import com.vector.update_app.UpdateAppBean
import com.vector.update_app.UpdateAppManager
import com.vector.update_app_kotlin.check
import com.vector.update_app_kotlin.updateApp
import io.rong.imkit.RongIM
import io.rong.imlib.model.UserInfo
import kotlinx.android.synthetic.main.activity_setting.*
import org.json.JSONObject

class SettingActivity : BaseActivity() {

    private val list = ArrayList<CommonData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        init_title("设置")
    }

    override fun init_title() {
        super.init_title()
        setting_version.setRightString("v" + Tools.getVersion(baseContext))

        setting_password.setOnClickListener { startActivity(SettingPasswordActivity::class.java) }
        setting_online.setOnClickListener { getData() }
        setting_version.setOnClickListener { checkUpdate() }
        setting_feedback.setOnClickListener { startActivity(FeedbackActivity::class.java) }
        setting_deal.setOnClickListener {
            val intent = Intent(baseContext, WebActivity::class.java)
            intent.putExtra("title", "用户协议")
            startActivity(intent)
        }
        setting_about.setOnClickListener {
            val intent = Intent(baseContext, WebActivity::class.java)
            intent.putExtra("title", "关于我们")
            startActivity(intent)
        }
        setting_quit.setOnClickListener {
            AlertDialog.Builder(baseContext)
                    .setTitle("退出登录")
                    .setMessage("确定要退出当前账号吗？")
                    .setPositiveButton("退出") { _, _ ->
                        val intent = Intent(baseContext, LoginActivity::class.java)
                        intent.putExtra("offLine", true)
                        startActivity(intent)
                    }
                    .setNegativeButton("取消") { _, _ -> }
                    .create()
                    .show()
        }
    }

    override fun getData() {
        OkGo.post<CommonModel>(BaseHttp.customer_list)
                .tag(this@SettingActivity)
                .execute(object : JacksonDialogCallback<CommonModel>(baseContext, CommonModel::class.java, true) {

                    override fun onSuccess(response: Response<CommonModel>) {
                        list.addItems(response.body().customers)

                        if (list.isNotEmpty()) {
                            //融云刷新用户信息
                            RongIM.getInstance().refreshUserInfoCache(UserInfo(
                                    list[0].userInfoId,
                                    list[0].nickName,
                                    Uri.parse(BaseHttp.baseImg + list[0].userhead)))
                            //融云单聊
                            RongIM.getInstance().startPrivateChat(
                                    baseContext,
                                    list[0].userInfoId,
                                    list[0].nickName)
                        }
                    }

                })
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
            onBefore { showLoadingDialog() }
            parseJson {
                val obj = JSONObject(it)
                val version_new = obj.optString("versionNo").replace(".", "").toInt()
                val version_old = Tools.getVersion(baseContext).replace(".", "").toInt()

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
                        .setConstraint(false)
            }
            hasNewApp { updateApp, updateAppManager -> showDownloadDialog(updateApp, updateAppManager) }
            noNewApp { toast("当前已是最新版本！") }
            onAfter { cancelLoadingDialog() }
        }
    }

    /**
     * 自定义对话框
     */
    private fun showDownloadDialog(updateApp: UpdateAppBean, updateAppManager: UpdateAppManager) {
        dialog("版本更新", "是否升级到${updateApp.newVersion}版本？\n\n${updateApp.updateLog}") {
            positiveButton("升级") { updateAppManager.download() }
            negativeButton("暂不升级") {  }
            show()
        }
    }
}
