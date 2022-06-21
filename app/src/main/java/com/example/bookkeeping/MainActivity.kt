package com.example.bookkeeping

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.format.Time
import android.util.TypedValue
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.example.bookkeeping.beans.BudgetGetBean
import com.example.bookkeeping.beans.PostBean
import com.example.bookkeeping.chart.BookChart
import com.example.bookkeeping.databinding.ActivityMainBinding
import com.example.bookkeeping.fragments.MainViewPager
import com.example.bookkeeping.my.My
import com.example.bookkeeping.tools.HttpUtil
import com.example.bookkeeping.tools.LiveDataBus.*
import com.example.bookkeeping.tools.Tools
import com.example.bookkeeping.ui.CenterLayoutManager
import com.google.gson.Gson
import java.util.*


class MainActivity : AppCompatActivity() {
    private var mBinding: ActivityMainBinding? = null
    private var mAdapter: MyRecyclerAdapter? = null
    private var mIsShow: Boolean? = false

    private var mCenterLayoutManager: CenterLayoutManager? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inital()
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        initView()
        setContentView(mBinding!!.getRoot())
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        mBinding!!.setIsGone(true)
        gsonTitle()
        //选择日期
        initDate()
        //添加点击事件
        initButton()
        //添加默认预算
        initMo()
        initMenu();
    }

    private fun initMo() {
        val httpUtil = HttpUtil()
        val sharedPreferences =getSharedPreferences("login", MODE_PRIVATE)
        val handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                val budgetGetBean = msg.obj as BudgetGetBean
                //判断是否为空  如果为空就添加,不为空就显示
                if (budgetGetBean.data == null) {
                    val handler = object : Handler() {
                        override fun handleMessage(msg: Message) {

                        }
                    }
                    val gson = Gson()
                    val maps: MutableMap<String, Any> = HashMap()
                    val sharedPreferences = getSharedPreferences("login", MODE_PRIVATE)
                    maps["budgetMoney"] = 1000
                    maps["userId"] = sharedPreferences?.getInt("id", 0)!!
                    val toJson = gson.toJson(maps)
                    httpUtil.httpPost("/budget/addOne", toJson, handler, PostBean::class.java)
                }
            }
        }
        httpUtil.httpGet(
            "/budget/getone/${sharedPreferences?.getInt("id", 0)!!}",
            handler,
            BudgetGetBean::class.java
        )
    }

    private fun initMenu() {
        //显示menu
        mBinding!!.mainTitleMore.setOnClickListener {
            mBinding!!.setIsGone(false)
            mBinding!!.mainTitleImg.visibility = View.VISIBLE
        }
//
//        getInstance().with("matchMenu", Boolean::class.java).observe(this,
//            {
//                if (it) {
//                    mBinding?.mainTitleLayout?.layoutParams?.height =
//                        ActionBar.LayoutParams.MATCH_PARENT
//                    mBinding?.mainDate?.visibility = View.GONE
//                    getInstance().with("matchMenu", Boolean::class.java).value = false
//                }
//            })
//
//        getInstance().with("wrapMenu", Boolean::class.java).observe(this,
//            {
//                if (it) {
//                    mBinding?.mainTitleLayout?.layoutParams?.height =
//                        ActionBar.LayoutParams.WRAP_CONTENT
//                    mBinding?.mainDate?.visibility = View.VISIBLE
//                    getInstance().with("wrapMenu", Boolean::class.java).value = false
//                }
//            })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        getInstance().with("wrapMenu", Boolean::class.java).setValue(true)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRestart() {
        super.onRestart()
        gsonTitle()
        //选择日期
        initDate()
        //添加点击事件
        initButton()
    }

    private fun initView() {
        val sharedPreferences = getSharedPreferences("activitytheme", MODE_PRIVATE)
        val string = sharedPreferences.getString("color", "蓝色")
        when (string) {
            "蓝色" -> {
                mBinding?.addView?.setBackgroundColor(getResources().getColor(R.color.blue_primary))
            }
            "绿色" -> {
                mBinding?.addView?.setBackgroundColor(getResources().getColor(R.color.grenn_primary))
            }
            "橙色" -> {
                mBinding?.addView?.setBackgroundColor(getResources().getColor(R.color.orange_primary))
            }
            "粉色" -> {
                mBinding?.addView?.setBackgroundColor(getResources().getColor(R.color.pink_primary))
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun initButton() {
        //添加
        mBinding?.mainButtonFloatingbutton?.setOnClickListener {
            getInstance().with("updateMoneyFlag", Boolean::class.java).value = true
            mBinding?.mainTitleImg2?.visibility = View.VISIBLE
            mBinding?.mainButtonFragment?.visibility = View.VISIBLE
        }

        //修改
        getInstance().with("updateLayout", Boolean::class.java)
            .observe(this, androidx.lifecycle.Observer {
                if (it) {
                    mBinding?.mainTitleImg2?.visibility = View.VISIBLE
                    mBinding?.mainButtonFragment?.visibility = View.VISIBLE
                    getInstance().with("updateLayout", Boolean::class.java).value = false
                    getInstance().with("updateMoneyFlag", Boolean::class.java).value = false
                }
            })

        getInstance().with("cuoFlag", Boolean::class.java)
            .observe(this, androidx.lifecycle.Observer {
                if (it) {
                    getInstance().with("cuoFlag", Boolean::class.java).value = false
                    mBinding!!.mainTitleImg2.visibility = View.GONE
                    mBinding!!.mainButtonFragment.visibility = View.GONE
                    if (getInstance().with("updateMoneyFlag", Boolean::class.java).value == false) {
                        getInstance().with("initPiveFlag", Boolean::class.java).value = true
                    }
                }
            })
        getInstance().with("initPiveFlag", Boolean::class.java)
            .observe(this, androidx.lifecycle.Observer {
                if (it) {
                    getInstance().with("initPiveFlag", Boolean::class.java).value = false
                    initDate()
                }
            })
        mBinding?.mainScrollView?.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY - oldScrollY > 0 && mIsShow == true) {
                mIsShow = false
                val mHiddenAction = AnimationUtils.loadAnimation(this, R.anim.scale_out)
                mBinding?.mainButtonLayout?.setAnimation(mHiddenAction)
                //上滑
                mBinding?.mainButtonLayout?.setVisibility(View.GONE)
            } else if (scrollY - oldScrollY < 0 && mIsShow == false) {
                mIsShow = true
                val mShowAction = AnimationUtils.loadAnimation(this, R.anim.scale_in)
                mBinding?.mainButtonLayout?.setAnimation(mShowAction)
                //下滑
                mBinding?.mainButtonLayout?.setVisibility(View.VISIBLE)
            }
        }

        mBinding?.mainButtonReport?.setOnClickListener {
            startActivity(Intent(this,BookChart::class.java))
        }

        mBinding?.mainButtonMy?.setOnClickListener {
            startActivity(Intent(this, My::class.java))
        }
    }

    private fun initDate() {
        val time = Time("GMT+8")
        time.setToNow()
        val year = time.year
        val month = time.month + 1
        val day = time.monthDay

        //设置ViewPager
        initViewPager(year, month)

        if (getInstance().with("selectYear", Int::class.javaPrimitiveType).value != null
            && getInstance().with("selectMonth", Int::class.javaPrimitiveType).value != null
        ) {
            val years = getInstance().with("selectYear", Int::class.javaPrimitiveType).value;
            val months = getInstance().with("selectMonth", Int::class.javaPrimitiveType).value;
            mBinding!!.mainDate.text = "$years.$months";
        } else {
            mBinding!!.mainDate.text = "$year.$month"
        }

        if (getInstance().with("selectYear", Int::class.javaPrimitiveType).value != null
            && getInstance().with("selectMonth", Int::class.javaPrimitiveType).value != null
        ) {

        } else {
            getInstance().with("selectYear", Int::class.javaPrimitiveType).value = year
            getInstance().with("selectMonth", Int::class.javaPrimitiveType).value = month
        }

        //设置起始
        val startDate = Calendar.getInstance()
        val endDate = Calendar.getInstance()
        startDate[2019, 0] = 10
        endDate[2023, 11] = 10

        mBinding!!.mainDate.setOnClickListener {
            val selectDate = Calendar.getInstance()
            selectDate[getInstance()
                .with("selectYear", Int::class.javaPrimitiveType).value!!, getInstance()
                .with("selectMonth", Int::class.javaPrimitiveType).value!!] = 0;
            val pvTime = TimePickerBuilder(this@MainActivity, OnTimeSelectListener { date, v ->
                val calendar = Calendar.getInstance()
                calendar.time = date
                val years = calendar[Calendar.YEAR]
                val months = calendar[Calendar.MONTH] + 1
                getInstance().with("selectYear", Int::class.javaPrimitiveType).value =
                    years
                getInstance().with("selectMonth", Int::class.javaPrimitiveType).value =
                    months
                mBinding!!.mainDate.text = "$years.$months"
                initViewPager(years, months)
            }) //默认显示年月日,
                .setType(booleanArrayOf(true, true, false, false, false, false))
                .setLabel("年", "月", "", "", "", "") //选择的范围
                .setRangDate(startDate, endDate)
                .setDate(selectDate)
                .build()
            pvTime.show()
        }
    }

    private fun initViewPager(year: Int, month: Int) {
        //当月天数
        val monthIndex = Tools.getMonthOfDay(year, month)
        val stringList: MutableList<String> = ArrayList()
        val fragmentList: MutableList<Fragment> = ArrayList()
        for (i in 0 until monthIndex) {
            fragmentList.add(MainViewPager(i + 1))
            stringList.add((i + 1).toString() + "")
        }

        mBinding!!.mainViewpager2.adapter = MyViewPager2Adapter(this, fragmentList)
        mCenterLayoutManager = CenterLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mBinding!!.mainRecycler.layoutManager = mCenterLayoutManager
        mAdapter = MyRecyclerAdapter(stringList)
        mBinding!!.mainRecycler.adapter = mAdapter
        mBinding!!.mainViewpager2.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                mCenterLayoutManager!!.smoothScrollToPosition(
                    mBinding!!.mainRecycler,
                    RecyclerView.State(),
                    position
                )
                getInstance().with("selectDay", Int::class.java).value = position
                mAdapter!!.setIndex(position)
                mAdapter!!.notifyDataSetChanged()
            }
        })
        getInstance().with("addAdapter", Boolean::class.java).observe(
            this,
            androidx.lifecycle.Observer {
                if (it) {
                    getInstance().with("addAdapter", Boolean::class.java).value = false
                    initViewPager(
                        getInstance().with("selectYears", String::class.java).value?.toInt()!!,
                        getInstance().with("selectMonths", String::class.java).value?.toInt()!!
                    )
                }
            })
    }

    //判断主题
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

    //触摸事件
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (mBinding!!.mainTitleLayout2.visibility == View.VISIBLE) {
            if (ev.action == MotionEvent.ACTION_DOWN || ev.action == MotionEvent.ACTION_HOVER_MOVE) {
                val location = intArrayOf(0, 0)
                //获取当前View在屏幕中离四边的边距
                mBinding!!.mainTitleLayout.getLocationInWindow(location)
                val left = location[0]
                val right = mBinding!!.mainTitleLayout.width
                val top = location[1]
                val bottom = mBinding!!.mainTitleLayout.height + top
                //判断点击的范围是否在View的布局内
                if (ev.rawX < left || ev.rawX > right || ev.rawY < top || ev.rawY > bottom) {
                    mBinding!!.isGone = true
                    mBinding!!.mainTitleImg.visibility = View.GONE
                    getInstance().with("Budgetflag",Boolean::class.java).value  = true
                    return false
                }
            }
        }

        if (mBinding!!.mainButtonFragment.visibility == View.VISIBLE) {
            if (ev.action == MotionEvent.ACTION_DOWN || ev.action == MotionEvent.ACTION_HOVER_MOVE) {
                val location = intArrayOf(0, 0)
                //获取当前View在屏幕中离四边的边距
                mBinding!!.mainButtonFragment.getLocationInWindow(location)
                val left = location[0]
                val right = mBinding!!.mainButtonFragment.width
                val top = location[1]
                val bottom = mBinding!!.mainButtonFragment.height + top
                //判断点击的范围是否在View的布局内
                if (ev.rawX < left || ev.rawX > right || ev.rawY < top || ev.rawY > bottom) {
                    if (getInstance().with("updateMoneyFlag", Boolean::class.java).value == false) {
                        getInstance().with("gridViewFlag", Boolean::class.java).value = true
                        mBinding!!.mainTitleImg2.visibility = View.GONE
                        mBinding!!.mainButtonFragment.visibility = View.GONE
                    }
                    return false
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    //主题变化调用
    fun gsonTitle() {
        liveDataBus.with("themecolor", Boolean::class.javaObjectType)
            .observe(this, androidx.lifecycle.Observer {
                if (it) {
                    liveDataBus.with(
                        "themecolor",
                        Boolean::class.javaPrimitiveType
                    ).value = false
                    mBinding!!.mainLayout.background = resources.getDrawable(R.color.white)
                    mBinding!!.isGone = true
                    mBinding!!.mainRecycler.visibility = View.GONE
                    mBinding!!.mainViewpager2.visibility = View.GONE
                    finish()
                    overridePendingTransition(R.anim.anim_in, R.anim.anim_out)
                    startActivity(this@MainActivity.intent)
                }
            })
    }

    internal inner class MyViewPager2Adapter(
        fragmentActivity: FragmentActivity,
        var mFragmentList: List<Fragment>
    ) : FragmentStateAdapter(fragmentActivity) {
        override fun createFragment(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getItemCount(): Int {
            return mFragmentList.size
        }
    }

    internal inner class MyRecyclerAdapter(private val mStringList: List<String>) :
        RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder>() {
        private var index: Int

        fun setIndex(index: Int) {
            this.index = index
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(this@MainActivity)
                .inflate(R.layout.layout_rv_title, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.mTextView.text = mStringList[position]
            val background = holder.mTextView.background as GradientDrawable
            if (index == position) {
                val typedValue = TypedValue()
                theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
                holder.mTextView.setTextColor(Color.WHITE)
                background.setColor(typedValue.data)
            } else {
                holder.mTextView.setTextColor(Color.BLACK)
                background.setColor(Color.WHITE)
            }

            //布局点击事件
            holder.mTextView.setOnClickListener {
                mCenterLayoutManager!!.smoothScrollToPosition(
                    mBinding!!.mainRecycler,
                    RecyclerView.State(),
                    position
                )
                mAdapter!!.setIndex(position)
                mBinding!!.mainViewpager2.setCurrentItem(position, false)
                mAdapter!!.notifyDataSetChanged()
            }
        }

        override fun getItemCount(): Int {
            return mStringList.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var mTextView: TextView

            init {
                mTextView = itemView.findViewById(R.id.layout_rv_title_text)
            }
        }

        init {
            var day = 0;
            if (getInstance().with("selectDay", Int::class.java).value != null) {
                day = getInstance().with("selectDay", Int::class.java).value!!
            } else {
                val time = Time("GMT+8")
                time.setToNow()
                day = time.monthDay - 1
            }
            index = day
            mBinding!!.mainViewpager2.setCurrentItem(day, false)
            mCenterLayoutManager!!.smoothScrollToPosition(
                mBinding!!.mainRecycler,
                RecyclerView.State(),
                day
            )
        }
    }
}