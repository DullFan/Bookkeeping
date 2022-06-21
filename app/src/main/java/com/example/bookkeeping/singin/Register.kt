package com.example.bookkeeping.singin

import android.content.SharedPreferences
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
import com.example.bookkeeping.databinding.ActivityRegisterBinding
import com.example.bookkeeping.tools.HttpUtil
import com.example.bookkeeping.tools.Tools
import com.google.gson.Gson
import java.util.HashMap

class Register : AppCompatActivity() {
    private var mBinding: ActivityRegisterBinding? = null
    private var mHttpUtil: HttpUtil? = null
    private var background = 0
    @RequiresApi(Build.VERSION_CODES.M)
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPreferences: SharedPreferences = getSharedPreferences("activitytheme", MODE_PRIVATE)
        val string: String? = sharedPreferences.getString("color", "蓝色")
        when (string) {
            "蓝色" -> {
                setTheme(R.style.Theme_Blue)
                background = this.getColor(R.color.blue_primary)
            }
            "绿色" -> {
                setTheme(R.style.Theme_Green)
                background = this.getColor(R.color.grenn_primary)
            }
            "橙色" -> {
                setTheme(R.style.Theme_Orange)
                background = this.getColor(R.color.orange_primary)
            }
            "粉色" -> {
                setTheme(R.style.Theme_Pink)
                background = this.getColor(R.color.pink_primary)
            }
        }
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_register)
        setContentView(mBinding!!.root)
        mBinding!!.registerLayout.setBackgroundColor(background)
        mHttpUtil = HttpUtil()
        initBack()
        initRegister()
    }

    private fun initRegister() {
        mBinding!!.registerUsername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.toString().length < 3 || s.toString().length > 8) {
                    mBinding!!.registerUsername.error = "用户名长度为3-8位"
                } else {
                    mBinding!!.registerUsername.error = null //没有错误清空
                }
            }
        })
        mBinding!!.registerPwd.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.toString().length > 10
                    || s.toString().length < 6) {
                    mBinding!!.registerPwd.error = "密码长度为6-10位" //错误的提示信息
                } else {
                    mBinding!!.registerPwd.error = null //没有错误清空
                }
            }
        })
        mBinding!!.registerName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.toString().length < 2
                    || s.toString().length > 6) {
                    mBinding!!.registerName.error = "名称长度为2-6位" //错误的提示信息
                } else {
                    mBinding!!.registerName.error = null //没有错误清空
                }
            }
        })
        mBinding!!.registerButton.setOnClickListener(View.OnClickListener {
            val username = mBinding!!.registerUsername.text.toString()
            val pwd = mBinding!!.registerPwd.text.toString()
            val name = mBinding!!.registerName.text.toString()
            if (username.isEmpty() || pwd.isEmpty() || name.isEmpty()) {
                Tools.showToast(this@Register, "请输入信息")
                return@OnClickListener
            }
            val error = mBinding!!.registerUsername.error
            val error2 = mBinding!!.registerPwd.error
            val error3 = mBinding!!.registerName.error
            if (error != null || error2 != null || error3 != null) {
                return@OnClickListener
            }
            val handler: Handler = object : Handler() {
                override fun handleMessage(msg: Message) {
                    val postBean: PostBean = msg.obj as PostBean
                    if (postBean.getCode() == "500") {
                        Tools.showToast(this@Register, postBean.getMsg())
                        return
                    } else {
                        Tools.showToast(this@Register, "注册成功")
                        finish()
                    }
                }
            }
            val url = "/user/register"
            val gson = Gson()
            val map: MutableMap<String, Any> = HashMap()
            map["userName"] = username
            map["userPassword"] = pwd
            map["name"] = name
            val s: String = gson.toJson(map)
            mHttpUtil!!.httpPost(url, s, handler, PostBean::class.java)
        })
    }

    private fun initBack() {
        mBinding!!.registerBack.setOnClickListener { finish() }
    }
}