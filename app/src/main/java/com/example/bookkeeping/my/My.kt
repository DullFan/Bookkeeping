package com.example.bookkeeping.my

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.bookkeeping.R
import com.example.bookkeeping.databinding.ActivityMyBinding
import com.example.bookkeeping.singin.Login

class My : AppCompatActivity() {
    var mBinding: ActivityMyBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inital()
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_my)
        setContentView(mBinding?.root)
        initView()
        initBack()
    }

    private fun initBack() {
        mBinding?.myBack?.setOnClickListener {
            finish()
        }

        mBinding?.myButton?.setOnClickListener {
            val sharedPreferences = getSharedPreferences("login", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("name", "无")
            editor.putInt("id", 0)
            editor.putString("time", "无")
            editor.putString("username", "无")
            editor.commit()
            startActivity(Intent(this,Login::class.java))
        }
    }

    private fun inital() {
        val sharedPreferences = getSharedPreferences("activitytheme", MODE_PRIVATE)
        val string = sharedPreferences.getString("color", "蓝色")
        when (string) {
            "蓝色" -> {
                setTheme(R.style.Theme_Blue)
            }
            "绿色" -> {
                setTheme(R.style.Theme_Green)
            }
            "橙色" -> {
                setTheme(R.style.Theme_Orange)
            }
            "粉色" -> {
                setTheme(R.style.Theme_Pink)
            }
        }
    }

    private fun initView() {
        val sharedPreferences = getSharedPreferences("activitytheme", MODE_PRIVATE)
        val string = sharedPreferences.getString("color", "蓝色")
        when (string) {
            "蓝色" -> {
                mBinding?.myTitleLayout?.setBackgroundColor(getResources().getColor(R.color.blue_primary))
                mBinding?.myButton?.setBackgroundColor(getResources().getColor(R.color.blue_primary))
            }
            "绿色" -> {
                mBinding?.myTitleLayout?.setBackgroundColor(getResources().getColor(R.color.grenn_primary))
                mBinding?.myButton?.setBackgroundColor(getResources().getColor(R.color.grenn_primary))
            }
            "橙色" -> {
                mBinding?.myTitleLayout?.setBackgroundColor(getResources().getColor(R.color.orange_primary))
                mBinding?.myButton?.setBackgroundColor(getResources().getColor(R.color.orange_primary))
            }
            "粉色" -> {
                mBinding?.myTitleLayout?.setBackgroundColor(getResources().getColor(R.color.pink_primary))
                mBinding?.myButton?.setBackgroundColor(getResources().getColor(R.color.pink_primary))
            }
        }

    }
}
