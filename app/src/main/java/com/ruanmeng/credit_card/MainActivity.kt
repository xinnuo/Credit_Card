package com.ruanmeng.credit_card

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.view.KeyEvent
import android.view.View
import android.widget.CompoundButton
import com.lzy.extend.StringDialogCallback
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import com.ruanmeng.base.*
import com.ruanmeng.fragment.MainFirstFragment
import com.ruanmeng.fragment.MainSecondFragment
import com.ruanmeng.fragment.MainThirdFragment
import com.ruanmeng.share.BaseHttp
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setToolbarVisibility(false)
        init_title()

        main_check1.performClick()
    }

    override fun init_title() {
        main_check1.setOnCheckedChangeListener(this)
        main_check2.setOnCheckedChangeListener(this)
        main_check3.setOnCheckedChangeListener(this)
    }

    override fun onStart() {
        super.onStart()

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
                    }

                })
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
            R.id.first_pay -> {
                val intent = Intent(baseContext, BankcardActivity::class.java)
                intent.putExtra("isPlan", true)
                startActivity(intent)
            }
            R.id.first_get -> {
                when (getString("isPass")) {
                    "-1" -> toast("实名认证审核未通过，无法进行收款")
                    "0" -> toast("实名认证信息正在审核中，无法进行收款")
                    "1" -> startActivity(GatherActivity::class.java)
                }
            }
            R.id.first_new -> {
                val intent = Intent(baseContext, WebActivity::class.java)
                intent.putExtra("title", "信用卡攻略")
                startActivity(intent)
            }
            R.id.second_update -> startActivity(MemberActivity::class.java)
        }
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
}
