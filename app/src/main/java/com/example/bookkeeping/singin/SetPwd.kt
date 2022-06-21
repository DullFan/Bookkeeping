package com.example.bookkeeping.singin

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.example.bookkeeping.R
import com.example.bookkeeping.beans.PostBean
import com.example.bookkeeping.databinding.ActivitySetPwdBinding
import com.example.bookkeeping.tools.HttpUtil
import com.example.bookkeeping.tools.Tools
import com.google.gson.Gson
import java.util.HashMap

class SetPwd : AppCompatActivity() {
    private var mBinding: ActivitySetPwdBinding? = null
    private var mHttpUtil: HttpUtil? = null
    private var background = 0
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences = getSharedPreferences("activitytheme", MODE_PRIVATE)
        val string = sharedPreferences.getString("color", "蓝色")
        when (string) {
            "蓝色" -> {
                setTheme(R.style.Theme_Blue)
                background = getColor(R.color.blue_primary)
            }
            "绿色" -> {
                setTheme(R.style.Theme_Green)
                background = getColor(R.color.grenn_primary)
            }
            "橙色" -> {
                setTheme(R.style.Theme_Orange)
                background = getColor(R.color.orange_primary)
            }
            "粉色" -> {
                setTheme(R.style.Theme_Pink)
                background = getColor(R.color.pink_primary)
            }
        }
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_set_pwd)
        setContentView(mBinding!!.getRoot())
        mBinding!!.forgetLayout.setBackgroundColor(background)
        mHttpUtil = HttpUtil()
        initBack()
        initForget()
        initLayout()
    }

    private fun initLayout() {
        //开启字数统计
        mBinding!!.forgetLayout2.isCounterEnabled = true
        mBinding!!.forgetLayout3.isCounterEnabled = true
        //最大字数
        mBinding!!.forgetLayout2.counterMaxLength = 8
        mBinding!!.forgetLayout3.counterMaxLength = 10
    }

    private fun initForget() {
        mBinding!!.forgetUsername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.toString().length < 3 || s.toString().length > 8) {
                    mBinding!!.forgetUsername.error = "用户名长度为3-8位"
                } else {
                    mBinding!!.forgetUsername.error = null //没有错误清空
                }
            }
        })
        mBinding!!.forgetPwd.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.toString().length > 10
                    || s.toString().length < 6) {
                    mBinding!!.forgetPwd.error = "密码长度为6-10位" //错误的提示信息
                } else {
                    mBinding!!.forgetPwd.error = null //没有错误清空
                }
            }
        })
        mBinding!!.forgetButton.setOnClickListener(View.OnClickListener {
            val username = mBinding!!.forgetUsername.text.toString()
            val pwd = mBinding!!.forgetPwd.text.toString()
            if (username.isEmpty() || pwd.isEmpty()) {
                Tools.showToast(this@SetPwd, "请输入信息")
                return@OnClickListener
            }
            val error = mBinding!!.forgetUsername.error
            val error2 = mBinding!!.forgetPwd.error
            if (error != null || error2 != null) {
                return@OnClickListener
            }
            val handler: Handler = object : Handler() {
                override fun handleMessage(msg: Message) {
                    val postBean = msg.obj as PostBean
                    if (postBean.code == "500") {
                        Tools.showToast(this@SetPwd, postBean.msg)
                        return
                    } else {
                        Tools.showToast(this@SetPwd, "修改成功")
                        finish()
                    }
                }
            }
            val url = "/user/setPwd"
            val gson = Gson()
            val map: MutableMap<String, Any> = HashMap()
            map["userUsername"] = username
            map["userPassword"] = pwd
            val s = gson.toJson(map)
            mHttpUtil!!.okhttpPut(url, s, handler, PostBean::class.java)
        })
    }

    private fun initBack() {
        mBinding!!.forgetBack.setOnClickListener { finish() }
    }
}