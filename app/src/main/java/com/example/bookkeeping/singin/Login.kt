package com.example.bookkeeping.singin

import android.animation.Animator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.view.ViewAnimationUtils
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.example.bookkeeping.MainActivity
import com.example.bookkeeping.R
import com.example.bookkeeping.beans.SignlnBean
import com.example.bookkeeping.databinding.ActivityLoginBinding
import com.example.bookkeeping.tools.HttpUtil
import com.example.bookkeeping.tools.LiveDataBus
import com.example.bookkeeping.tools.Tools
import com.google.gson.Gson
import java.util.HashMap

class Login : AppCompatActivity() {
    private var mBinding: ActivityLoginBinding? = null
    private var mHttpUtil: HttpUtil? = null
    private var handler: Handler? = null
    private var animator: Animator? = null
    private var background = 0

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val sharedPreferences = getSharedPreferences("activitytheme",MODE_PRIVATE)
        val string = sharedPreferences.getString("color", "蓝色")
        LiveDataBus.getInstance().with("loginbuttoncolor", String::class.java).setValue(string)
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
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        setContentView(mBinding!!.getRoot())
        mBinding!!.singLayout.setBackgroundColor(background)
        mBinding!!.signinLayout.background.alpha = 0
        mBinding!!.singLayout.visibility = View.VISIBLE
        mBinding!!.singLinear.visibility = View.VISIBLE
        mBinding!!.signinZhuce.visibility = View.VISIBLE
        handler = Handler()
        mHttpUtil = HttpUtil()
        initIntent()
        initButton()
    }

    private fun initButton() {
        mBinding!!.signinButton.setOnClickListener(View.OnClickListener {
            mBinding!!.signinButton.isClickable = false
            val url = "/user/login"
            val user = mBinding!!.signinUsername.text.toString()
            val password = mBinding!!.signinPwd.text.toString()
            if (user.isEmpty()) {
                mBinding!!.signinLayout.background.alpha = 0
                mBinding!!.signinButton.regainBackground()
                Tools.showToast(this@Login, "请输入用户名")
                mBinding!!.signinButton.isClickable = true
                return@OnClickListener
            }
            if (password.isEmpty()) {
                mBinding!!.signinLayout.background.alpha = 0
                mBinding!!.signinButton.regainBackground()
                Tools.showToast(this@Login, "请输入密码")
                mBinding!!.signinButton.isClickable = true
                return@OnClickListener
            }
            mBinding!!.signinButton.startAnim()
            val handler: Handler = object : Handler() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                override fun handleMessage(msg: Message) {
                    val signlnBean = msg.obj as SignlnBean
                    mBinding!!.signinButton.isClickable = true
                    if (signlnBean.code == "500") {
                        mBinding!!.signinLayout.background.alpha = 0
                        mBinding!!.signinButton.regainBackground()
                        Tools.showToast(this@Login, signlnBean.msg)
                    } else {
                        val sharedPreferences = getSharedPreferences("login", MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("name", signlnBean.data.name)
                        editor.putInt("id", signlnBean.data.id)
                        editor.putString("time", signlnBean.data.time)
                        editor.putString("username", signlnBean.data.username)
                        editor.commit()
                        gotoNew()
                        Tools.showToast(this@Login, "登录成功")
                    }
                }
            }
            val gson = Gson()
            val maps: MutableMap<String, Any> = HashMap()
            maps["username"] = user
            maps["password"] = password
            val s = gson.toJson(maps)
            mHttpUtil!!.httpPost(url, s, handler, SignlnBean::class.java)
        })
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private fun gotoNew() {
        mBinding!!.signinButton.gotoNew()
        val intent = Intent(this, MainActivity::class.java)
        val xc = (mBinding!!.signinButton.left + mBinding!!.signinButton.right) / 2
        val yc = (mBinding!!.signinButton.top + mBinding!!.signinButton.bottom) / 2
        //设置布局成圆,实现圆形缩放动画
        animator = ViewAnimationUtils.createCircularReveal(mBinding!!.signinLayout, xc, yc, 0f, 1111f)
        //时长
        animator!!.setDuration(300)
        mBinding!!.singLayout.visibility = View.GONE
        mBinding!!.singLinear.visibility = View.GONE
        mBinding!!.signinZhuce.visibility = View.GONE
        //监听动画是否结束
        animator!!.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                handler!!.postDelayed({
                    finish()
                    startActivity(intent)
                    //设置转场动画
                    overridePendingTransition(R.anim.anim_in, R.anim.anim_out)
                }, 200)
            }

            override fun onAnimationEnd(animation: Animator) {}
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        animator!!.start()
        mBinding!!.signinLayout.background.alpha = 255
    }

    private fun initIntent() {
        val sharedPreferences = getSharedPreferences("login", MODE_PRIVATE)
        val string = sharedPreferences.getString("name", "无")
        if (string != "无") {
            startActivity(Intent(this@Login, MainActivity::class.java))
            finish()
        } else {
            mBinding!!.signinZhuce.setOnClickListener { startActivity(Intent(this@Login, Register::class.java)) }
            mBinding!!.signinForget.setOnClickListener { startActivity(Intent(this@Login, SetPwd::class.java)) }
            initButton()
        }
    }
}