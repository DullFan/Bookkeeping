package com.example.bookkeeping.fragments

import android.app.ActionBar
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Color.*
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.format.Time
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.bigkoo.pickerview.adapter.ArrayWheelAdapter
import com.example.bookkeeping.R
import com.example.bookkeeping.beans.ConsumptionBean
import com.example.bookkeeping.beans.PostBean
import com.example.bookkeeping.beans.RecyclerDataBean
import com.example.bookkeeping.databinding.FragmentAddBinding

import com.example.bookkeeping.tools.HttpUtil
import com.example.bookkeeping.tools.LiveDataBus.*
import com.example.bookkeeping.tools.Tools
import com.google.gson.Gson
import java.util.*
import kotlin.collections.ArrayList


class AddFragment : Fragment() {
    private var mBinding: FragmentAddBinding? = null
    lateinit var adapter: GricViewApdater
    lateinit var mList: MutableList<RecyclerDataBean>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        getInstance().with("zhishouflag", Int::class.java).value = 1
        mBinding = FragmentAddBinding.inflate(inflater, container, false)
        //设置修改和添加
        initUpDate()
        return mBinding!!.root
    }

    private fun initUpDate() {
        getInstance().with("updateMoneyFlag", Boolean::class.java).observe(viewLifecycleOwner,
            Observer {
                //设置显示动画
                initAnim()
                //设置GridView
                initGridView()
                mBinding?.addMoney?.text = "￥0"
                adapter.index = 0
                mBinding?.addCuo?.setImageDrawable(context?.resources?.getDrawable(R.drawable.cuowuimg))
                mBinding?.addFlag?.text = "支出"
                getInstance().with("zhishouflag", Int::class.java).value = 1
                mBinding?.addRemarks?.setText("")
                getInstance().with("gridViewFlag", Boolean::class.java).observe(
                    viewLifecycleOwner,
                    Observer {
                        if (it) {
                            getInstance().with("gridViewFlag", Boolean::class.java).value = false
                            mBinding?.addGridview?.numColumns = 6
                            mBinding?.addGridview?.layoutParams?.height =
                                ActionBar.LayoutParams.WRAP_CONTENT
                            mBinding?.addDui?.visibility = View.VISIBLE
                            mBinding?.addCuo?.visibility = View.VISIBLE
                        }
                    })
                adapter.notifyDataSetChanged()
                //设置年月日选择器
                initPive()
                //设置取消和添加
                initButton()
                //设置键盘
                initMoney()

                if (!it) {
                    val value: ConsumptionBean.DataDTO? =
                        getInstance().with("updateData", ConsumptionBean.DataDTO::class.java).value

                    mBinding?.addFlag?.isClickable = false
                    mBinding?.addCuo?.setImageDrawable(context?.resources?.getDrawable(R.drawable.delimg))
                    mBinding?.addMoney?.text = "￥${value?.con_money}"
                    //是否有备注
                    if (value?.con_remarks?.isEmpty() == false) {
                        mBinding?.addRemarks?.setText("${value?.con_remarks}")
                    }
                    if (value?.con_consume.equals("1")) {
                        mBinding?.addFlag?.text = "支出"
                        getInstance().with("zhishouflag", Int::class.java).value = 1
                        mList.clear()
                        mList.plusAssign(RecyclerDataBean("三餐", R.drawable.addimg1))
                        mList.plusAssign(RecyclerDataBean("零食", R.drawable.addimg2))
                        mList.plusAssign(RecyclerDataBean("衣服", R.drawable.addimg3))
                        mList.plusAssign(RecyclerDataBean("交通", R.drawable.addimg4))
                        mList.plusAssign(RecyclerDataBean("旅游", R.drawable.addimg5))
                        mList.plusAssign(RecyclerDataBean("更多", R.drawable.main_title_more))
                        when (value?.con_record) {
                            "三餐" -> adapter.index = 0
                            "零食" -> adapter.index = 1
                            "衣服" -> adapter.index = 2
                            "交通" -> adapter.index = 3
                            "旅游" -> adapter.index = 4
                            else -> {
                                mList.clear()
                                mList.plusAssign(RecyclerDataBean("三餐", R.drawable.addimg1))
                                mList.plusAssign(RecyclerDataBean("零食", R.drawable.addimg2))
                                mList.plusAssign(RecyclerDataBean("衣服", R.drawable.addimg3))
                                mList.plusAssign(RecyclerDataBean("交通", R.drawable.addimg4))
                                mList.plusAssign(
                                    RecyclerDataBean(
                                        "${value?.con_record}",
                                        Tools.itemImgInt(context, value?.con_record)
                                    )
                                )
                                mList.plusAssign(RecyclerDataBean("更多", R.drawable.main_title_more))
                                adapter.index = 4
                            }
                        }
                        adapter.notifyDataSetChanged()
                    } else {
                        mBinding?.addFlag?.text = "收入"
                        getInstance().with("zhishouflag", Int::class.java).value = 0
                        mList.clear()
                        mList.plusAssign(RecyclerDataBean("工资", R.drawable.addimgshou1))
                        mList.plusAssign(RecyclerDataBean("兼职", R.drawable.addimgshou2))
                        mList.plusAssign(RecyclerDataBean("营业", R.drawable.addimgshou7))
                        mList.plusAssign(RecyclerDataBean("股票", R.drawable.addimgshou4))
                        mList.plusAssign(RecyclerDataBean("基金", R.drawable.addimgshou5))
                        mList.plusAssign(RecyclerDataBean("更多", R.drawable.main_title_more))
                        when (value?.con_record) {
                            "工资" -> adapter.index = 0
                            "兼职" -> adapter.index = 1
                            "营业" -> adapter.index = 2
                            "股票" -> adapter.index = 3
                            "基金" -> adapter.index = 4
                            else -> {
                                mList.clear()
                                mList.plusAssign(RecyclerDataBean("工资", R.drawable.addimgshou1))
                                mList.plusAssign(RecyclerDataBean("兼职", R.drawable.addimgshou2))
                                mList.plusAssign(RecyclerDataBean("营业", R.drawable.addimgshou7))
                                mList.plusAssign(RecyclerDataBean("股票", R.drawable.addimgshou4))
                                mList.plusAssign(
                                    RecyclerDataBean(
                                        "${value?.con_record}",
                                        Tools.itemImgInt(context, value?.con_record)
                                    )
                                )
                                mList.plusAssign(RecyclerDataBean("更多", R.drawable.main_title_more))
                                adapter.index = 4
                            }
                        }
                        adapter.notifyDataSetChanged()
                    }
                }
            })
    }

    private fun initAnim() {

    }

    private fun initMoney() {
        mBinding?.addMoney?.setOnClickListener {
            if (mBinding?.addMoneyLayout?.visibility == View.VISIBLE) {
                mBinding?.addMoneyLayout?.visibility = View.GONE
            } else {
                mBinding?.addMoneyLayout?.visibility = View.VISIBLE
            }
        }

        mBinding?.addMoneyOne?.setOnClickListener {
            initAdd("1")
        }

        mBinding?.addMoneyTwo?.setOnClickListener {
            initAdd("2")
        }

        mBinding?.addMoneyThree?.setOnClickListener {
            initAdd("3")
        }

        mBinding?.addMoneyFour?.setOnClickListener {
            initAdd("4")
        }

        mBinding?.addMoneyFive?.setOnClickListener {
            initAdd("5")
        }

        mBinding?.addMoneySix?.setOnClickListener {
            initAdd("6")
        }

        mBinding?.addMoneySeven?.setOnClickListener {
            initAdd("7")
        }

        mBinding?.addMoneyEight?.setOnClickListener {
            initAdd("8")
        }


        mBinding?.addMoneyNine?.setOnClickListener {
            initAdd("9")
        }

        mBinding?.addMoneyDian?.setOnClickListener {
            if (!getMoneyString().contains(".")) {
                mBinding?.addMoney?.text = "${mBinding?.addMoney?.text}."
            }
        }

        mBinding?.addMoneyZero?.setOnClickListener {
            initAdd("0")
        }

        mBinding?.addMoneyDel?.setOnClickListener {
            val text = getMoneyString()
            if (text.toDouble() == 0.0) {
                if (text.contains(".")) {
                    mBinding?.addMoney?.text = "￥0"
                    return@setOnClickListener
                }
                val anim: Animation = AnimationUtils.loadAnimation(
                    context,
                    R.anim.money_layout
                )
                mBinding?.addMoney?.startAnimation(anim)
                return@setOnClickListener
            } else {
                mBinding?.addMoney?.text = "￥${text.substring(0, text.length - 1)}"
                if (getMoneyString().isEmpty()) {
                    mBinding?.addMoney?.text = "￥0"
                }
            }
        }

        mBinding?.addMoneyDel?.setOnLongClickListener {
            mBinding?.addMoney?.text = "￥0"
            return@setOnLongClickListener false
        }
    }

    private fun initAdd(string: String) {
        val text = getMoneyString()
        if (text.contains(".")) {
            val strings = text.substring(text.indexOf(".") + 1)
            if (strings.length == 2) {
                val anim: Animation = AnimationUtils.loadAnimation(
                    context,
                    R.anim.money_layout
                )
                mBinding?.addMoney?.startAnimation(anim)
                return
            }

            if (text.toDouble() == 0.0) {
                mBinding?.addMoney?.text = "${mBinding?.addMoney?.text}$string"
                return
            }
        }
        if (text.length!! >= 5 && !text.contains(".")) {
            val anim: Animation = AnimationUtils.loadAnimation(
                context,
                R.anim.money_layout
            )
            mBinding?.addMoney?.startAnimation(anim)
            return
        }
        if (text.toDouble() == 0.0) {
            mBinding?.addMoney?.text = "￥$string"
        } else {
            mBinding?.addMoney?.text = "${mBinding?.addMoney?.text}$string"
        }
    }

    private fun getMoneyString(): String {
        return mBinding?.addMoney?.text.toString().substring(1)
    }

    private fun initButton() {
        val drawable: GradientDrawable = mBinding?.addDui?.background as GradientDrawable
        val drawable2: GradientDrawable = mBinding?.addCuo?.background as GradientDrawable
        drawable.setColor(Color.parseColor("#31D138"))
        drawable2.setColor(Color.parseColor("#F31212"))
        mBinding?.addCuo?.setOnClickListener {
            val value: Boolean =
                getInstance().with("updateMoneyFlag", Boolean::class.java).value == true
            if (value) {
                getInstance().with("cuoFlag", Boolean::class.java).value = true
            } else {
                val dialog: AlertDialog.Builder = AlertDialog.Builder(context).apply {
                    setTitle("确认删除")
                    setMessage("删除后数据不可恢复!")
                    setPositiveButton("确定", DialogInterface.OnClickListener { dialog, which ->
                        val value: ConsumptionBean.DataDTO? =
                            getInstance().with(
                                "updateData",
                                ConsumptionBean.DataDTO::class.java
                            ).value
                        val url = "/consumption/delitem/" + value?.con_id
                        val handler: Handler = object : Handler() {
                            override fun handleMessage(msg: Message) {
                                val postBean = msg.obj as PostBean
                                if (postBean.code.equals("500")) {
                                    Tools.showToast(context, "删除失败")
                                } else {
                                    Tools.showToast(context, "删除成功")
                                    getInstance().with("cuoFlag", Boolean::class.java).value = true
                                }
                            }
                        }
                        val httpUtil = HttpUtil()
                        httpUtil.okhttpDelete(url, "", handler, PostBean::class.java)
                    })
                    setNegativeButton("取消", null)
                }
                dialog.show()
            }
        }

        mBinding?.addDui?.setOnClickListener {
            //描述
            val describe = getInstance().with("selectDescribe", String::class.java).value
            //时间
            val date = "${getInstance().with("selectYears", String::class.java).value}" +
                    "-${getInstance().with("selectMonths", String::class.java).value}" +
                    "-${getInstance().with("selectDays", String::class.java).value}"
            Log.i("TAG", "initButton: " + date)
            //金额
            val money = mBinding?.addMoney?.text.toString().substring(1)
            if (money.toDouble() == 0.0) {
                val anim: Animation = AnimationUtils.loadAnimation(
                    context,
                    R.anim.money_layout
                )
                mBinding?.addMoney?.startAnimation(anim)
                return@setOnClickListener
            }
            //备注
            val remarks = mBinding?.addRemarks?.text.toString()
            //用户id
            var id =
                "${context?.getSharedPreferences("login", Context.MODE_PRIVATE)?.getInt("id", 0)}"
            //1为支出  0为收入
            val flag = "${getInstance().with("zhishouflag", Int::class.java).value}"
            val httpUtils = HttpUtil()

            if(getInstance().with("updateMoneyFlag", Boolean::class.java).value == true){
                val handler: Handler = object : Handler() {
                    override fun handleMessage(msg: Message) {
                        val bean: PostBean = msg.obj as PostBean
                        if (bean.msg.equals("500")) {
                            Tools.showToast(context, "${bean.data}")
                            return
                        } else {
                            Tools.showToast(context, "${bean.data}")
                            getInstance().with("cuoFlag", Boolean::class.java).value = true
                            getInstance().with("addAdapter", Boolean::class.java).value = true
                            mBinding?.addMoney?.text = "￥0"
                        }
                    }
                }
                val gson = Gson()
                val map = mutableMapOf<String, String>()
                map.put("conUserid", id)
                map.put("conRemarks", remarks)
                map.put("conRecord", describe!!)
                map.put("conMoney", money)
                map.put("conDissipate", date)
                map.put("conConsume", flag)
                val s: String = gson.toJson(map)
                httpUtils.httpPost("/consumption/addOne", s, handler, PostBean::class.java)
            }else{
                val handler: Handler = object : Handler() {
                    override fun handleMessage(msg: Message) {
                        val bean: PostBean = msg.obj as PostBean
                        if (bean.msg.equals("500")) {
                            Tools.showToast(context, "${bean.data}")
                            return
                        } else {
                            Tools.showToast(context, "${bean.data}")
                            getInstance().with("cuoFlag", Boolean::class.java).value = true
                            getInstance().with("addAdapter", Boolean::class.java).value = true
                            mBinding?.addMoney?.text = "￥0"
                        }
                    }
                }
                val value: ConsumptionBean.DataDTO? =
                    getInstance().with("updateData", ConsumptionBean.DataDTO::class.java).value
                //修改金额
                val utl = "/consumption/setmoney"
                val gson = Gson()
                val map: MutableMap<String, String> = HashMap()
                map["conDate"] = value!!.con_date
                map["conDissipate"] = date
                map["conMoney"] = money
                map["conRecord"] = describe!!
                map["conRemarks"] = remarks
                val s = gson.toJson(map)
                httpUtils.okhttpPut(utl, s, handler, PostBean::class.java)
            }
        }
    }

    private fun initPive() {
        //年
        mBinding?.addYear?.setCyclic(false) //是否循环展示
        mBinding?.addMonth?.setCyclic(false)
        var list1: MutableList<String> = ArrayList()
        list1.add("2019")
        list1.add("2020")
        list1.add("2021")
        list1.add("2022")
        list1.add("2023")
        //设置适配器
        mBinding?.addYear?.adapter = ArrayWheelAdapter(list1)
        list1.forEachIndexed { index, item ->
            if (list1[index].toInt() == getInstance().with(
                    "selectYear",
                    Int::class.javaPrimitiveType
                ).value
            ) {
                mBinding?.addYear?.currentItem = index
            }
        }

        //月
        var list2: MutableList<String> = ArrayList()
        for (i in 1..12) {
            list2.add("${i}")
        }
        mBinding?.addMonth?.adapter = ArrayWheelAdapter(list2)
        mBinding?.addMonth?.currentItem =
            getInstance().with("selectMonth", Int::class.javaPrimitiveType).value!! - 1
        //日
        val currentItem: Int? = mBinding?.addYear?.currentItem
        val currentItem1: Int? = mBinding?.addMonth?.currentItem
        mBinding?.addDay?.setCyclic(false)
        var list3: MutableList<String> = ArrayList()
        for (i in 1..Tools.getMonthOfDay(
            list1[currentItem!!].toInt(),
            list2[currentItem1!!].toInt()
        )) {
            list3.add("${i}")
        }
        mBinding?.addDay?.adapter = ArrayWheelAdapter(list3)
        val time = Time("GMT+8")
        time.setToNow()
        var day = time.monthDay - 1
        if (getInstance().with("selectDay", Int::class.java).value != null) {
            day = getInstance().with("selectDay", Int::class.java).value!!
        }
        //默认选中
        mBinding?.addDay?.currentItem = day
        getInstance().with("selectDays", String::class.java).value = "${day + 1}"
        //设置选中和未选中的颜色
        mBinding?.addDay?.setTextColorCenter(WHITE)
        mBinding?.addDay?.setTextColorOut(this.resources.getColor(R.color.select))
        mBinding?.addYear?.setTextColorCenter(WHITE)
        mBinding?.addYear?.setTextColorOut(this.resources.getColor(R.color.select))
        mBinding?.addMonth?.setTextColorCenter(WHITE)
        mBinding?.addMonth?.setTextColorOut(this.resources.getColor(R.color.select))
        val type = TypedValue()
        context?.theme?.resolveAttribute(R.attr.colorPrimary, type, false)
        //设置滚动条颜色
        mBinding?.addMonth?.setDividerColor(1)
        mBinding?.addYear?.setDividerColor(1)
        mBinding?.addDay?.setDividerColor(1)

        getInstance().with("selectYear", Int::class.javaPrimitiveType)
            .observe(viewLifecycleOwner, Observer {
                if (mBinding?.addYear?.adapter != null) {
                    list1.forEachIndexed { index, item ->
                        if (list1[index].toInt() == getInstance().with(
                                "selectYear",
                                Int::class.javaPrimitiveType
                            ).value
                        ) {
                            getInstance().with("selectYears", String::class.java).value =
                                list1[index]
                            mBinding?.addYear?.currentItem = index
                        }
                    }
                }
            })

        getInstance().with("selectMonth", Int::class.javaPrimitiveType)
            .observe(viewLifecycleOwner, Observer {
                if (mBinding?.addMonth?.adapter != null) {
                    mBinding?.addMonth?.currentItem = it - 1
                    getInstance().with("selectMonths", String::class.java).value = "${it}"
                }

            })

        getInstance().with("selectDay", Int::class.javaPrimitiveType)
            .observe(viewLifecycleOwner, Observer {
                if (mBinding?.addDay?.adapter != null) {
                    mBinding?.addDay?.currentItem = it
                    getInstance().with("selectDays", String::class.java).value = "${it + 1}"
                }
            })

        mBinding?.addYear?.setOnItemSelectedListener {
            getInstance().with("selectYears", String::class.java).value = list1[it]
        }

        mBinding?.addMonth?.setOnItemSelectedListener {
            getInstance().with("selectMonths", String::class.java).value = "${it + 1}"
            //日
            val currentItem: Int? = mBinding?.addYear?.currentItem
            val currentItem1: Int? = mBinding?.addMonth?.currentItem
            mBinding?.addDay?.setCyclic(false)
            var list3: MutableList<String> = ArrayList()
            for (i in 1..Tools.getMonthOfDay(
                list1[currentItem!!].toInt(),
                list2[currentItem1!!].toInt()
            )) {
                list3.add("${i}")
            }
            mBinding?.addDay?.adapter = ArrayWheelAdapter(list3)
            mBinding?.addDay?.currentItem = 0
        }

        mBinding?.addDay?.setOnItemSelectedListener {
            getInstance().with("selectDays", String::class.java).value = "${it + 1}"
        }

    }

    private fun initGridView() {
        mList = ArrayList()
        mList.plusAssign(RecyclerDataBean("三餐", R.drawable.addimg1))
        mList.plusAssign(RecyclerDataBean("零食", R.drawable.addimg2))
        mList.plusAssign(RecyclerDataBean("衣服", R.drawable.addimg3))
        mList.plusAssign(RecyclerDataBean("交通", R.drawable.addimg4))
        mList.plusAssign(RecyclerDataBean("旅游", R.drawable.addimg5))
        mList.plusAssign(RecyclerDataBean("更多", R.drawable.main_title_more))

        adapter = GricViewApdater(mList)
        mBinding?.addGridview?.adapter = adapter
        mBinding?.addFlag?.setOnClickListener {
            if (mBinding?.addFlag?.text.toString().equals("支出")) {
                mBinding?.addFlag?.text = "收入"
                mList.clear()
                getInstance().with("zhishouflag", Int::class.java).value = 0
                mList.plusAssign(RecyclerDataBean("工资", R.drawable.addimgshou1))
                mList.plusAssign(RecyclerDataBean("兼职", R.drawable.addimgshou2))
                mList.plusAssign(RecyclerDataBean("营业", R.drawable.addimgshou7))
                mList.plusAssign(RecyclerDataBean("股票", R.drawable.addimgshou4))
                mList.plusAssign(RecyclerDataBean("基金", R.drawable.addimgshou5))
                mList.plusAssign(RecyclerDataBean("更多", R.drawable.main_title_more))
                adapter.notifyDataSetChanged()
            } else {
                mBinding?.addFlag?.text = "支出"
                mList.clear()
                getInstance().with("zhishouflag", Int::class.java).value = 1
                mList.plusAssign(RecyclerDataBean("三餐", R.drawable.addimg1))
                mList.plusAssign(RecyclerDataBean("零食", R.drawable.addimg2))
                mList.plusAssign(RecyclerDataBean("衣服", R.drawable.addimg3))
                mList.plusAssign(RecyclerDataBean("交通", R.drawable.addimg4))
                mList.plusAssign(RecyclerDataBean("旅游", R.drawable.addimg5))
                mList.plusAssign(RecyclerDataBean("更多", R.drawable.main_title_more))
                adapter.notifyDataSetChanged()
            }
        }
    }

    inner class GricViewApdater(val list: List<RecyclerDataBean>) : BaseAdapter() {
        var index = 0

        init {
            getInstance().with("selposition", Int::class.java).value = 0
        }

        override fun getCount(): Int {
            return list.size
        }

        override fun getItem(position: Int): Any {
            return list[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var view: View = LayoutInflater.from(parent?.context)
                .inflate(R.layout.add_gridview_layout, parent, false)
            val text: TextView = view.findViewById(R.id.add_gridview_name)
            val img: ImageView = view.findViewById(R.id.add_gridview_img)
            val layout: ConstraintLayout = view.findViewById(R.id.add_gridview_layout)
            if (index == position) {
                if (position < 4) {
                    getInstance().with("selposition", Int::class.java).value = position
                }
                img.setColorFilter(WHITE)
                text.setTextColor(WHITE)
                getInstance().with("selectDescribe", String::class.java).value =
                    list.get(position).name
            } else {
                img.setColorFilter(parseColor("#8A676464"))
                text.setTextColor(parseColor("#8A676464"))
            }

            if (list.size - 1 == position && list.size == 6) {
                img.setColorFilter(WHITE)
            }

            if (list.size == 6) {
                text.visibility = View.GONE
            }

            layout.setOnClickListener {
                //true为支出 false为收入
                val flag = getInstance().with("zhishouflag", Int::class.java).value

                if (list.size == 6 && position == 5) {
                    mList.clear()
                    initListData()
                    mBinding?.addDui?.visibility = View.GONE
                    mBinding?.addCuo?.visibility = View.GONE
                    mBinding?.addGridview?.numColumns = 5
                    mBinding?.addGridview?.layoutParams?.height =
                        ActionBar.LayoutParams.MATCH_PARENT
                    if (getInstance().with("updateMoneyFlag", Boolean::class.java).value == true) {
                        if (getInstance().with("selposition", Int::class.java).value != 0) {
                            adapter.index =
                                getInstance().with("selposition", Int::class.java).value!!
                        }
                    } else {
                        val value: ConsumptionBean.DataDTO? =
                            getInstance().with(
                                "updateData",
                                ConsumptionBean.DataDTO::class.java
                            ).value
                        mList.forEachIndexed { index, item ->
                            if (mList[index].name.equals(value?.con_record)) {
                                adapter.index = index
                            }
                        }
                    }
                    adapter.notifyDataSetChanged()
                    return@setOnClickListener
                }

                if (list.size != 6) {
                    val data: RecyclerDataBean = mList.get(position)
                    mList.clear()
                    mBinding?.addGridview?.numColumns = 6
                    mBinding?.addDui?.visibility = View.VISIBLE
                    mBinding?.addCuo?.visibility = View.VISIBLE
                    mBinding?.addGridview?.layoutParams?.height =
                        ActionBar.LayoutParams.WRAP_CONTENT
                    if (getInstance().with("zhishouflag", Int::class.java).value == 1) {
                        if (position < 4) {
                            mList.plusAssign(RecyclerDataBean("三餐", R.drawable.addimg1))
                            mList.plusAssign(RecyclerDataBean("零食", R.drawable.addimg2))
                            mList.plusAssign(RecyclerDataBean("衣服", R.drawable.addimg3))
                            mList.plusAssign(RecyclerDataBean("交通", R.drawable.addimg4))
                            mList.plusAssign(RecyclerDataBean("旅游", R.drawable.addimg5))
                            mList.plusAssign(RecyclerDataBean("更多", R.drawable.main_title_more))
                            adapter.index = position
                        } else {
                            getInstance().with("selposition", Int::class.java).value = position
                            mList.plusAssign(RecyclerDataBean("三餐", R.drawable.addimg1))
                            mList.plusAssign(RecyclerDataBean("零食", R.drawable.addimg2))
                            mList.plusAssign(RecyclerDataBean("衣服", R.drawable.addimg3))
                            mList.plusAssign(RecyclerDataBean("交通", R.drawable.addimg4))
                            mList.plusAssign(data)
                            mList.plusAssign(RecyclerDataBean("更多", R.drawable.main_title_more))
                            adapter.index = 4
                        }
                    } else {
                        if (position < 4) {
                            mList.plusAssign(RecyclerDataBean("工资", R.drawable.addimgshou1))
                            mList.plusAssign(RecyclerDataBean("兼职", R.drawable.addimgshou2))
                            mList.plusAssign(RecyclerDataBean("营业", R.drawable.addimgshou7))
                            mList.plusAssign(RecyclerDataBean("股票", R.drawable.addimgshou4))
                            mList.plusAssign(RecyclerDataBean("基金", R.drawable.addimgshou5))
                            mList.plusAssign(RecyclerDataBean("更多", R.drawable.main_title_more))
                            adapter.index = position
                        } else {
                            getInstance().with("selposition", Int::class.java).value = position
                            mList.plusAssign(RecyclerDataBean("工资", R.drawable.addimgshou1))
                            mList.plusAssign(RecyclerDataBean("兼职", R.drawable.addimgshou2))
                            mList.plusAssign(RecyclerDataBean("营业", R.drawable.addimgshou7))
                            mList.plusAssign(RecyclerDataBean("股票", R.drawable.addimgshou4))
                            mList.plusAssign(data)
                            mList.plusAssign(RecyclerDataBean("更多", R.drawable.main_title_more))
                            adapter.index = 4
                        }
                    }
                    adapter.notifyDataSetChanged()
                    return@setOnClickListener
                }
                adapter.index = position
                adapter.notifyDataSetChanged()
            }

            text.text = list.get(position).name
            img.setImageResource(list[position].img)
            return view
        }
    }

    private fun initListData() {
        Log.i("TAG", "initListData: "+getInstance().with("zhishouflag", Int::class.java).value)
        if (getInstance().with("zhishouflag", Int::class.java).value == 1) {
            mList.plusAssign(RecyclerDataBean("三餐", R.drawable.addimg1))
            mList.plusAssign(RecyclerDataBean("零食", R.drawable.addimg2))
            mList.plusAssign(RecyclerDataBean("衣服", R.drawable.addimg3))
            mList.plusAssign(RecyclerDataBean("交通", R.drawable.addimg4))
            mList.plusAssign(RecyclerDataBean("旅游", R.drawable.addimg5))
            mList.plusAssign(RecyclerDataBean("孩子", R.drawable.addimg6))
            mList.plusAssign(RecyclerDataBean("宠物", R.drawable.addimg7))
            mList.plusAssign(RecyclerDataBean("通讯", R.drawable.addimg8))
            mList.plusAssign(RecyclerDataBean("烟酒", R.drawable.addimg9))
            mList.plusAssign(RecyclerDataBean("学习", R.drawable.addimg10))
            mList.plusAssign(RecyclerDataBean("日用", R.drawable.addimg11))
            mList.plusAssign(RecyclerDataBean("住房", R.drawable.addimg12))
            mList.plusAssign(RecyclerDataBean("美妆", R.drawable.addimg13))
            mList.plusAssign(RecyclerDataBean("医疗", R.drawable.addimg14))
            mList.plusAssign(RecyclerDataBean("礼金", R.drawable.addimg15))
            mList.plusAssign(RecyclerDataBean("娱乐", R.drawable.addimg16))
            mList.plusAssign(RecyclerDataBean("请客", R.drawable.addimg17))
            mList.plusAssign(RecyclerDataBean("数码", R.drawable.addimg18))
            mList.plusAssign(RecyclerDataBean("运动", R.drawable.addimg19))
            mList.plusAssign(RecyclerDataBean("办公", R.drawable.addimg20))
            mList.plusAssign(RecyclerDataBean("其他", R.drawable.addimg21))
        } else {
            mList.plusAssign(RecyclerDataBean("工资", R.drawable.addimgshou1))
            mList.plusAssign(RecyclerDataBean("兼职", R.drawable.addimgshou2))
            mList.plusAssign(RecyclerDataBean("营业", R.drawable.addimgshou7))
            mList.plusAssign(RecyclerDataBean("股票", R.drawable.addimgshou4))
            mList.plusAssign(RecyclerDataBean("基金", R.drawable.addimgshou5))
            mList.plusAssign(RecyclerDataBean("中奖", R.drawable.addimgshou6))
            mList.plusAssign(RecyclerDataBean("其他", R.drawable.addimg21))
        }
    }
}
