package com.example.bookkeeping.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.os.*
import android.text.format.Time
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookkeeping.R
import com.example.bookkeeping.beans.BudgetGetBean
import com.example.bookkeeping.beans.ConsumptionBean
import com.example.bookkeeping.databinding.FragmentMainViewPagerBinding
import com.example.bookkeeping.tools.HttpUtil
import com.example.bookkeeping.tools.LiveDataBus.*
import com.example.bookkeeping.tools.Loading
import com.example.bookkeeping.tools.Tools
import com.google.gson.Gson
import java.text.NumberFormat
import java.util.*
import kotlin.collections.HashMap


class MainViewPager : Fragment {
    private var mBinding: FragmentMainViewPagerBinding? = null
    private var index = 0
    private val mHttpUtils = HttpUtil()
    lateinit var mDialog: Loading



    constructor(index: Int) {
        this.index = index
    }

    constructor()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mDialog = Loading(context, R.style.DialogStyle)
        mDialog.show()
        mBinding = FragmentMainViewPagerBinding.inflate(inflater, container, false)
        mBinding?.fragmentViewPagerNull?.visibility = View.GONE
        mBinding?.fragmentViewPagerLayout?.visibility = View.GONE
        //获取数据

        initData();
        return mBinding!!.root
    }


    private fun initData() {
        //获取用户ID
        var sharedPreferences =
            context?.getSharedPreferences("login", MODE_PRIVATE)
        var id = sharedPreferences?.getInt("id", 0)
        val year = liveDataBus.with("selectYear", Int::class.java).value
        val month = liveDataBus.with("selectMonth", Int::class.java).value
        val date = "$year-$month-$index"
        val url = "/consumption/getmonth/$id/$date"
        var handler: Handler = object : Handler() {
            @SuppressLint("HandlerLeak")
            override fun handleMessage(msg: Message) {
                val bean = msg.obj as ConsumptionBean
                mDialog.dismiss()
                if (bean.data.size == 0) {
                    mBinding?.fragmentViewPagerNull?.visibility = View.VISIBLE
                    mBinding?.fragmentViewPagerLayout?.visibility = View.GONE
                    getInstance().with("buttonIsFlag", Boolean::class.java).value = false
                } else {
                    getInstance().with("buttonIsFlag", Boolean::class.java).value = true
                    mBinding?.fragmentViewPagerLayout?.visibility = View.VISIBLE
                    mBinding?.fragmentViewPagerNull?.visibility = View.GONE
                    mBinding?.fragmentViewPagerRecycler?.layoutManager =
                        LinearLayoutManager(context)
                    bean.data.reverse()
                    initDayMoney(bean.data)
                    //设置预算
                    initBudget();
                    val adapter = ViewPagerRecyclerAdapter(bean.data, context)
                    mBinding?.fragmentViewPagerRecycler?.adapter = adapter
                }
            }
        }
        mHttpUtils.httpGet(url, handler, ConsumptionBean::class.java)
    }

    private fun initBudget() {
        getInstance().with("initBudget",Boolean::class.java).observe(this, androidx.lifecycle.Observer {
            if(it){
                initBudget();
                getInstance().with("initBudget",Boolean::class.java).value = false
            }
        })
        val time: Time = Time("GMT+8")
        time.setToNow()
        val year = time.year
        val month = time.month + 1
        val selectYear = getInstance().with("selectYear", Int::class.javaPrimitiveType).value
        val selectMonth = getInstance().with("selectMonth", Int::class.javaPrimitiveType).value
        //判断是否为当月
        if (selectMonth == month && selectYear == year) {
            mBinding?.mainBudgetLayout?.visibility = View.VISIBLE
            mBinding?.mainBudgetText?.visibility = View.GONE
            mBinding?.mainBudgetDate?.text = "${year}年${month}月${index}日预算"
            val httpUtil = HttpUtil()
            val sharedPreferences = context?.getSharedPreferences("login", MODE_PRIVATE)
            val handler = object : Handler() {
                override fun handleMessage(msg: Message) {
                    val budgetGetBean = msg.obj as BudgetGetBean
                    //判断是否为空  如果为空就添加,不为空就显示
                    if (budgetGetBean.data != null) {
                        initGet(budgetGetBean.data)
                    }
                }
            }
            httpUtil.httpGet(
                "/budget/getone/${sharedPreferences?.getInt("id", 0)!!}",
                handler,
                BudgetGetBean::class.java
            )
        } else {
            mBinding?.mainBudgetLayout?.visibility = View.GONE
            mBinding?.mainBudgetText?.visibility = View.VISIBLE
        }
    }

    private fun initGet(money: BudgetGetBean.DataDTO) {
        if(money.bgMoney.contains(".")){
            val indexOf = money.bgMoney.indexOf(".");
            val substring = money.bgMoney.substring(0, indexOf)
            mBinding?.mainBudgetProgressBar?.max = substring.toInt()
        }else{
            mBinding?.mainBudgetProgressBar?.max = money.bgMoney.toInt()
        }
        //获取用户ID
        var sharedPreferences =
            context?.getSharedPreferences("login", MODE_PRIVATE)
        var id = sharedPreferences?.getInt("id", 0)
        val year = liveDataBus.with("selectYear", Int::class.java).value
        val month = liveDataBus.with("selectMonth", Int::class.java).value
        val date = "$year-$month"
        val url = "/consumption/getmonth/$id/$date"
        var handler: Handler = object : Handler() {
            @SuppressLint("HandlerLeak")
            override fun handleMessage(msg: Message) {
                val bean = msg.obj as ConsumptionBean
                var num: Double = 0.0
                bean.data.forEachIndexed { index, dataDTO ->
                    val replace = dataDTO.con_dissipate.substring(dataDTO.con_dissipate.length - 2)
                        .replace("-", "").toInt()
                    if (replace <= this@MainViewPager.index) {
                        if (dataDTO.con_consume.equals("1")) {
                            num += dataDTO.con_money.toDouble()
                        }
                    }
                }
                if (num > money.bgMoney.toDouble()) {
                    mBinding?.mainBudgetProgressBar?.progress = num.toInt()
                    mBinding?.mainBudgetSurplus?.text = "剩余0%"
                    mBinding?.mainBudgetMoney1?.text = "0"
                    mBinding?.mainBudgetMoney2?.text = "${money.bgMoney.toDouble()}"
                    mBinding?.mainBudgetMoney3?.text = "${num}"
                } else {
                    val numberFormat: NumberFormat = NumberFormat.getInstance()
                    //数设置显示的数字位数为格式化对象设定小数点后的显示的最多位,但注意的是显示的最后位是舍入的
                    numberFormat.setMaximumFractionDigits(2)
                    mBinding?.mainBudgetSurplus?.text =
                        "剩余${(numberFormat.format((money.bgMoney.toDouble() - num)/ money.bgMoney.toDouble() * 100))}%"
                    mBinding?.mainBudgetProgressBar?.progress = num.toInt()
                    mBinding?.mainBudgetMoney1?.text = "${money.bgMoney.toDouble() - num}"
                    mBinding?.mainBudgetMoney2?.text = "${money.bgMoney.toDouble()}"
                    mBinding?.mainBudgetMoney3?.text = "${num}"
                }
            }
        }
        mHttpUtils.httpGet(url, handler, ConsumptionBean::class.java)
    }

    private fun initDayMoney(data: List<ConsumptionBean.DataDTO>) {
        var num1: Double = 0.0
        var num2: Double = 0.0

        data.forEachIndexed { index, item ->
            if (data[index].con_consume.equals("1")) {
                num1 += data[index].con_money.toDouble()
            } else {
                num2 += data[index].con_money.toDouble()
            }
        }

        mBinding?.fragmentViewPagerDayshou?.text = "￥${num2}"
        mBinding?.fragmentViewPagerDayzhi?.text = "￥${num1}"
    }


    class ViewPagerRecyclerAdapter(
        private val list: List<ConsumptionBean.DataDTO>,
        private val context: Context?
    ) :
        RecyclerView.Adapter<ViewPagerRecyclerAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var describe: TextView
            var money: TextView
            var time: TextView
            var img: ImageView
            var layout: ConstraintLayout

            init {
                describe = itemView.findViewById(R.id.view_pager_bg_text);
                money = itemView.findViewById(R.id.view_pager_bg_money);
                time = itemView.findViewById(R.id.view_pager_bg_text2);
                img = itemView.findViewById(R.id.view_pager_bg_img);
                layout = itemView.findViewById(R.id.view_pager_bg_layout);
            }
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.img.setImageDrawable(Tools.itemImg(context, list[position].con_record))
            if (list[position].con_consume.equals("1")) {
                holder.money.text = "-￥${list[position].con_money}"
                val typedValue = TypedValue()
                context?.theme?.resolveAttribute(R.attr.colorPrimary, typedValue, true)
                holder.layout.setBackgroundColor(typedValue.data)
            } else {
                holder.money.text = "￥${list[position].con_money}"
                val typedValue = TypedValue()
                context?.theme?.resolveAttribute(R.attr.colorOnPrimary, typedValue, true)
                holder.layout.setBackgroundColor(typedValue.data)
            }

            if (list[position].con_remarks.isEmpty()) {
                holder.time.visibility = View.GONE
            } else {
                holder.time.visibility = View.VISIBLE
            }
            holder.time.text = list[position].con_remarks
            holder.describe.text = list[position].con_record

            holder.layout.setOnClickListener {
                getInstance().with("updateData", ConsumptionBean.DataDTO::class.java).value =
                    list[position]
                getInstance().with("updateLayout", Boolean::class.java).value = true
            }
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view: View =
                LayoutInflater.from(context).inflate(R.layout.main_view_pager_bg, parent, false)
            return ViewHolder(view)
        }
    }
}
