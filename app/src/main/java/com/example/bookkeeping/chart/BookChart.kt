package com.example.bookkeeping.chart

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookkeeping.R
import com.example.bookkeeping.beans.ConsumptionBean
import com.example.bookkeeping.beans.MoneyDayBean
import com.example.bookkeeping.databinding.ActivityChartBinding
import com.example.bookkeeping.tools.HttpUtil
import com.example.bookkeeping.tools.LiveDataBus.*
import com.example.bookkeeping.tools.Tools
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import java.text.NumberFormat
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList


class BookChart : AppCompatActivity() {
    private var mBinding: ActivityChartBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inital()
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_chart)
        setContentView(mBinding?.root)
        initView()
        initBack()
        //数据
        initData()
    }



    private fun initLineChart() {
        val stringListX1: MutableList<MoneyDayBean> = getInstance().with(
            "stringListXY1",
            List::class.java
        ).value as MutableList<MoneyDayBean>

        val stringListX2: MutableList<MoneyDayBean> = getInstance().with(
            "stringListXY2",
            List::class.java
        ).value as MutableList<MoneyDayBean>

        val chartLinechart: LineChart? = mBinding?.chartLinechart;
        chartLinechart?.setScaleEnabled(false)//是否缩放
        chartLinechart?.isDoubleTapToZoomEnabled = false//双击不放大

        //描述
        val description = chartLinechart?.description
        description?.isEnabled = false

        //X轴
        val xAxis = chartLinechart?.xAxis
        xAxis?.setDrawGridLines(false)
        xAxis?.setDrawAxisLine(true)
        xAxis!!.granularity = 1f
        xAxis.setLabelCount(stringListX1.size, false)
        //底部显示
        xAxis?.position = XAxis.XAxisPosition.BOTTOM
        val stringList: MutableList<String> = ArrayList()
        stringListX1.forEachIndexed { index, moneyDayBean ->
            if (index == 0) {
                stringList.add(moneyDayBean.data)
            } else if (index == stringListX1.size / 2) {
                stringList.add(moneyDayBean.data)
            } else if (index == stringListX1.size - 1) {
                stringList.add(moneyDayBean.data)
            } else {
                stringList.add("")
            }
        }
        xAxis?.valueFormatter = IndexAxisValueFormatter(stringList)

        //左
        val axisLeft = chartLinechart?.axisLeft
        axisLeft?.isEnabled = true
        axisLeft?.axisMinimum = 0F
        axisLeft?.setDrawGridLines(true)
        axisLeft?.setDrawLabels(true)
        val listEntry: MutableList<Entry> = ArrayList()


        if (getInstance().with("chartFlag", Boolean::class.java).value == true) {
            stringListX1.forEachIndexed { index, moneyDayBean ->
                        listEntry.add(Entry(index.toFloat(), moneyDayBean.money.toFloat()))
            }
        } else {
            stringListX2.forEachIndexed { index, moneyDayBean ->
                listEntry.add(Entry(index.toFloat(), moneyDayBean.money.toFloat()))
            }
        }


        //右
        val axisRight = chartLinechart?.axisRight
        axisRight?.isEnabled = false
        val lineDataSet =
            LineDataSet(listEntry, getInstance().with("chartStr", String::class.java).value)

        if (getInstance().with("chartFlag", Boolean::class.java).value == true) {
                lineDataSet?.setColor(resources.getColor(R.color.cuo))
                lineDataSet?.setCircleColor(resources.getColor(R.color.cuo))
                val drawable = ContextCompat.getDrawable(this, R.color.cuo2)
                lineDataSet?.fillDrawable = drawable
                lineDataSet?.setDrawFilled(true)
        } else {
            lineDataSet?.setColor(resources.getColor(R.color.dui))
            lineDataSet?.setCircleColor(resources.getColor(R.color.dui))
            val drawable = ContextCompat.getDrawable(this, R.color.dui2)
            lineDataSet?.fillDrawable = drawable
            lineDataSet?.setDrawFilled(true)
        }

        //设置圆点是实心还是空心
        lineDataSet?.setDrawCircleHole(false)
        val lineData = LineData(lineDataSet)
        lineData?.setDrawValues(false)
        chartLinechart?.data = lineData
        val detailsMarkerView = DetailsMarkerView(this, R.layout.chart_layout)
        detailsMarkerView.chartView = chartLinechart
        chartLinechart?.marker = detailsMarkerView
        chartLinechart?.invalidate()
    }

    private fun initData() {
        //获取用户ID
        var sharedPreferences = getSharedPreferences("login", MODE_PRIVATE)
        var id = sharedPreferences?.getInt("id", 0)
        val year = liveDataBus.with("selectYear", Int::class.java).value
        val month = liveDataBus.with("selectMonth", Int::class.java).value
        val dayNum = Tools.getMonthOfDay(year!!, month!!)
        val stringListXY1: MutableList<MoneyDayBean> = ArrayList()
        val stringListXY2: MutableList<MoneyDayBean> = ArrayList()
        for (i in 1..dayNum) {
            stringListXY1.add(MoneyDayBean(0.0, "$month-$i"))
            stringListXY2.add(MoneyDayBean(0.0, "$month-$i"))
        }
        val date = "$year-$month"
        val url = "/consumption/getmonth/$id/$date"
        val httpUtil = HttpUtil()
        val handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                var consumptionBean = msg.obj as ConsumptionBean
                //排序
                consumptionBean.data.sortWith(Comparator { dataDTO, dataDTO2 ->
                    val date1: Date = Tools.stringToDate2(dataDTO.con_dissipate)
                    val date2: Date = Tools.stringToDate2(dataDTO2.con_dissipate)
                    return@Comparator date1.compareTo(date2)
                })
                //每日金额
                initDay(consumptionBean.data, year, stringListXY1, stringListXY2)
                //当月分类消费
                initClassification(consumptionBean.data)
                //折线图
                initLineChart()
                //饼图
                initPieChart()
                //排行
                initRecycler();
            }
        }
        httpUtil.httpGet(url, handler, ConsumptionBean::class.java)
    }

    private fun initRecycler() {
        val recyclerList: List<MoneyDayBean> = getInstance().with(
            "classification1",
            List::class.java
        ).value as List<MoneyDayBean>
        val recyclerList2: List<MoneyDayBean> = getInstance().with(
            "classification0",
            List::class.java
        ).value as List<MoneyDayBean>
        var recyclerNum = 0.0
        var recyclerNum2 = 0.0
        recyclerList.forEach {
            recyclerNum += it.money
        }
        recyclerList2.forEach {
            recyclerNum2 += it.money
        }

        val sortedWith1 = recyclerList.sortedWith(Comparator { dataDTO, dataDTO2 ->
            val date1: Double = dataDTO.money
            val date2: Double = dataDTO2.money
            return@Comparator date2.compareTo(date1)
        })

        val sortedWith2 = recyclerList2.sortedWith(Comparator { o1, o2 ->
            val date1: Double = o1.money
            val date2: Double = o2.money
            return@Comparator date1.compareTo(date2)
        })
        var adapter:BookChartRecyclerViewAdapter
        if( getInstance().with("chartFlag", Boolean::class.java).value == true){
            getInstance().with("chartListNum", Double::class.java).value = recyclerNum
            adapter = BookChartRecyclerViewAdapter(sortedWith1)
        }else{
            getInstance().with("chartListNum", Double::class.java).value = recyclerNum2
            adapter = BookChartRecyclerViewAdapter(sortedWith2)
        }
        mBinding?.chartRecycler?.layoutManager = LinearLayoutManager(this)
        mBinding?.chartRecycler?.adapter = adapter
    }

    private fun initPieChart() {
        val pieChartList: List<MoneyDayBean> = getInstance().with(
            "classification1",
            List::class.java
        ).value as List<MoneyDayBean>
        val pieChartList2: List<MoneyDayBean> = getInstance().with(
            "classification0",
            List::class.java
        ).value as List<MoneyDayBean>
        //设置颜色集
        val colors = ArrayList<Int>()
        initColor(colors)
        mBinding?.chartPieChart?.setCenterText("")
        val description = mBinding?.chartPieChart?.description
        description?.isEnabled = false

        //图例居中
        val legend: Legend? = mBinding?.chartPieChart?.legend
        legend?.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
        legend?.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER

        //显示百分比
        mBinding?.chartPieChart?.setUsePercentValues(true);

        val pieEntry: MutableList<PieEntry> = ArrayList()
        if (getInstance().with("chartFlag", Boolean::class.java).value == true) {
            pieChartList.forEachIndexed { index, moneyDayBean ->
                pieEntry.add(PieEntry(moneyDayBean.money.toFloat(), moneyDayBean.data))
            }
        } else {
            pieChartList2.forEachIndexed { index, moneyDayBean ->
                pieEntry.add(PieEntry(moneyDayBean.money.toFloat(), moneyDayBean.data))
            }
        }
        val pieDataSet: PieDataSet = PieDataSet(pieEntry, "")
        pieDataSet?.setColors(colors)

        //链接线,设置数据的位置
        pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieDataSet.setValueLinePart1Length(0.3f)//设置描述连接线长度
        pieDataSet.setValueLinePart2Length(0.3f)
        //设置两根连接线的颜色
        pieDataSet.setValueLineColor(Color.BLACK)

        //动画
        mBinding?.chartPieChart?.animateXY(500, 500)
        mBinding?.chartLinechart?.animateXY(500, 500)

        //数据字体颜色
        mBinding?.chartPieChart?.setCenterTextColor(Color.BLACK)
        //描述字体颜色
        mBinding?.chartPieChart?.setEntryLabelColor(Color.BLACK)

        val pieData = PieData(pieDataSet)
        //百分比显示
        pieData.setValueFormatter(PercentFormatter(mBinding?.chartPieChart))
        pieData.setValueTextColor(Color.BLACK)
        pieData.setValueTextSize(14f)
        mBinding?.chartPieChart?.data = pieData
        mBinding?.chartPieChart?.invalidate()
        //点击事件
        mBinding?.chartPieChart?.setOnChartValueSelectedListener(object :
            OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {

                if (getInstance().with("chartFlag", Boolean::class.java).value == true) {
                    mBinding?.chartPieChart?.setCenterText(pieChartList[h?.x?.toInt()!!].data + " " + pieChartList[h?.x?.toInt()!!].money)

                } else {
                    mBinding?.chartPieChart?.setCenterText(pieChartList2[h?.x?.toInt()!!].data + " " + pieChartList2[h?.x?.toInt()!!].money)
                }
            }

            override fun onNothingSelected() {
                mBinding?.chartPieChart?.setCenterText("")
            }
        })
    }



    private fun initColor(colors: java.util.ArrayList<Int>) {
        colors.add(resources.getColor(R.color.teal_200))
        colors.add(resources.getColor(R.color.purple_200))
        colors.add(resources.getColor(R.color.purple_500))
        colors.add(resources.getColor(R.color.teal_700))
        colors.add(resources.getColor(R.color.pink_emphasize))
        colors.add(resources.getColor(R.color.dui))
        colors.add(resources.getColor(R.color.textcolor))
        colors.add(resources.getColor(R.color.cuo))
        colors.add(resources.getColor(R.color.pink_dark))
        colors.add(resources.getColor(R.color.pink_primary))
        colors.add(resources.getColor(R.color.orange_primary))
        colors.add(resources.getColor(R.color.orange_dark))
        colors.add(resources.getColor(R.color.grenn_primary))
        colors.add(resources.getColor(R.color.grenn_dark))
        colors.add(resources.getColor(R.color.blue_dark))
        colors.add(resources.getColor(R.color.blue_emphasize))
        colors.add(resources.getColor(R.color.blue_light))
        colors.add(resources.getColor(R.color.orange_light))
        colors.add(resources.getColor(R.color.pink_light))
        colors.add(resources.getColor(R.color.grenn_light))
    }

    private fun initClassification(data: List<ConsumptionBean.DataDTO>) {
        var list1: MutableList<MoneyDayBean> = ArrayList()
        var list2: MutableList<MoneyDayBean> = ArrayList()
        var dayList1: MutableList<String> = ArrayList()
        var dayList2: MutableList<String> = ArrayList()
        data.forEachIndexed { index, dataDTO ->
            if (dataDTO.con_consume.equals("1")) {
                dayList1.add(dataDTO.con_record)
            } else {
                dayList2.add(dataDTO.con_record)
            }
        }
        val toList1 = dayList1.toSet().toList()
        val toList2 = dayList2.toSet().toList()
        toList1.forEachIndexed { index, s ->
            var double: Double = 0.0
            data.forEachIndexed { index, dataDTO ->
                if (dataDTO.con_consume.equals("1")) {
                    if (s.equals(dataDTO.con_record)) {
                        double += dataDTO.con_money.toDouble()
                    }
                }
            }
            list1.add(MoneyDayBean(double, s))
        }

        toList2.forEachIndexed { index, s ->
            var double: Double = 0.0
            data.forEachIndexed { index, dataDTO ->
                if (dataDTO.con_consume.equals("0")) {
                    if (s.equals(dataDTO.con_record)) {
                        double += dataDTO.con_money.toDouble()
                    }
                }
            }
            list2.add(MoneyDayBean(double, s))
        }
        getInstance().with("classification1", List::class.java).value = list1
        getInstance().with("classification0", List::class.java).value = list2
    }


    private fun initDay(
        data: List<ConsumptionBean.DataDTO>,
        year: Int,
        stringListXY1: MutableList<MoneyDayBean>,
        stringListXY2: MutableList<MoneyDayBean>
    ) {

        var list1: MutableList<MoneyDayBean> = ArrayList()
        var list2: MutableList<MoneyDayBean> = ArrayList()
        var dayList1: MutableList<String> = ArrayList()
        var dayList2: MutableList<String> = ArrayList()
        data.forEachIndexed { index, dataDTO ->
            if (dataDTO.con_consume.equals("1")) {
                dayList1.add(dataDTO.con_dissipate)
            } else {
                dayList2.add(dataDTO.con_dissipate)
            }
        }
        val toList1 = dayList1.toSet().toList()
        val toList2 = dayList2.toSet().toList()
        toList1.forEachIndexed { index, s ->
            var double: Double = 0.0
            data.forEachIndexed { index, dataDTO ->
                if (dataDTO.con_consume.equals("1")) {
                    if (s.equals(dataDTO.con_dissipate)) {
                        double += dataDTO.con_money.toDouble()
                    }
                }
            }
            list1.add(MoneyDayBean(double, s))
        }

        toList2.forEachIndexed { index, s ->
            var double: Double = 0.0
            data.forEachIndexed { index, dataDTO ->
                if (dataDTO.con_consume.equals("0")) {
                    if (s.equals(dataDTO.con_dissipate)) {
                        double += dataDTO.con_money.toDouble()
                    }
                }
            }
            list2.add(MoneyDayBean(double, s))
        }

        stringListXY1.forEachIndexed { index, moneyDayBean1 ->
            list1.forEachIndexed { index, moneyDayBean2 ->
                if (moneyDayBean1.data.equals(moneyDayBean2.data.substring(5))) {
                    moneyDayBean1.money = moneyDayBean2.money
                }
            }
        }

        stringListXY2.forEachIndexed { index, moneyDayBean1 ->
            list2.forEachIndexed { index, moneyDayBean2 ->
                if (moneyDayBean1.data.equals(moneyDayBean2.data.substring(5))) {
                    moneyDayBean1.money = moneyDayBean2.money
                }
            }
        }

        getInstance().with("stringListXY1", List::class.java).value = stringListXY1
        getInstance().with("stringListXY2", List::class.java).value = stringListXY2

    }


    private fun initBack() {
        mBinding?.chartBack?.setOnClickListener {
            finish()
        }
        getInstance().with("chartFlag", Boolean::class.java).value = true
        getInstance().with("chartStr", String::class.java).value = "支出"
        mBinding?.chartTitle?.setOnClickListener {
            if (mBinding?.chartTitle?.text.toString().equals("支出")) {
                mBinding?.chartTitle?.text = "收入"
                getInstance().with("chartFlag", Boolean::class.java).value = false
                getInstance().with("chartStr", String::class.java).value = "收入"
                mBinding?.chartText1?.text = "收入走势"
                mBinding?.chartText2?.text = "收入占比"
                mBinding?.chartText3?.text = "收入排行"
                initLineChart()
                initPieChart()
                initRecycler()
            } else {
                mBinding?.chartTitle?.text = "支出"
                getInstance().with("chartFlag", Boolean::class.java).value = true
                mBinding?.chartText1?.text = "支出走势"
                mBinding?.chartText2?.text = "支出占比"
                mBinding?.chartText3?.text = "收入排行"
                getInstance().with("chartStr", String::class.java).value = "支出"
                initLineChart()
                initPieChart()
                initRecycler()
            }
        }
    }

    private fun initView() {
        val sharedPreferences = getSharedPreferences("activitytheme", MODE_PRIVATE)
        val string = sharedPreferences.getString("color", "蓝色")
        when (string) {
            "蓝色" -> {
                mBinding?.chartTitleLayout?.setBackgroundColor(getResources().getColor(R.color.blue_primary))
            }
            "绿色" -> {
                mBinding?.chartTitleLayout?.setBackgroundColor(getResources().getColor(R.color.grenn_primary))
            }
            "橙色" -> {
                mBinding?.chartTitleLayout?.setBackgroundColor(getResources().getColor(R.color.orange_primary))
            }
            "粉色" -> {
                mBinding?.chartTitleLayout?.setBackgroundColor(getResources().getColor(R.color.pink_primary))
            }
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

    inner class BookChartRecyclerViewAdapter(private var list: List<MoneyDayBean>) :
        RecyclerView.Adapter<BookChartRecyclerViewAdapter.ViewHoler3>() {
        inner class ViewHoler3(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var img:ImageView
            var text1:TextView
            var text2:TextView
            var text3:TextView
            init {
                img = itemView.findViewById(R.id.bookchart_recycler_img)
                text1 = itemView.findViewById(R.id.bookchart_recycler_text1)
                text2 = itemView.findViewById(R.id.bookchart_recycler_text2)
                text3 = itemView.findViewById(R.id.bookchart_recycler_text3)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHoler3 {
            val view:View = LayoutInflater.from(this@BookChart).inflate(R.layout.bookchart_recycler_layout,
            parent,false)
            return ViewHoler3(view)
        }

        override fun onBindViewHolder(holder: ViewHoler3, position: Int) {
            val money = list[position].money
            val data = list[position].data
            holder.img.setImageResource(Tools.itemImgInt(this@BookChart,data))
            holder.text1.text = data
            var numberFormat: NumberFormat = NumberFormat.getInstance()
            numberFormat.setMaximumFractionDigits(1)
            val value = getInstance().with("chartListNum", Double::class.java).value
            holder.text2.text = "${getInstance().with("chartStr", String::class.java).value}占比${
                numberFormat.format(money/ value!! *100)}%"
            holder.text3.text = "${money}元"
        }

        override fun getItemCount(): Int {
            return list.size
        }

    }
}