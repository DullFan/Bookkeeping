package com.example.bookkeeping.fragments

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.navigation.Navigation
import com.example.bookkeeping.R
import com.example.bookkeeping.databinding.FragmentMyDrawingBoardBinding
import com.example.bookkeeping.tools.LiveDataBus
import java.util.ArrayList


class MyDrawingBoard : Fragment() {
    var mBinding: FragmentMyDrawingBoardBinding? = null
    private var mAdapter: MyDrawingBoardAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = FragmentMyDrawingBoardBinding.inflate(inflater, container, false)
        //设置标题
        initTitle()
        initGridView()
        return mBinding!!.root
    }

    private fun initGridView() {
        val list: MutableList<String> = ArrayList()
        list.add("蓝色")
        list.add("绿色")
        list.add("橙色")
        list.add("粉色")
        mAdapter = MyDrawingBoardAdapter(list)
        mBinding!!.fragmentMyDrawingBoardGridview.adapter = mAdapter
        mBinding!!.fragmentMyDrawingBoardGridview.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                mAdapter!!.index = position
                mAdapter!!.notifyDataSetChanged()
                Navigation.findNavController(mBinding!!.root).navigateUp()
                val sharedPreferences =
                    requireContext().getSharedPreferences("activitytheme", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("color", list[position])
                editor.commit() //提交
                LiveDataBus.liveDataBus.with("themecolor", Boolean::class.javaPrimitiveType).value =
                    true
            }
    }

    private fun initTitle() {
        val back =
            mBinding!!.fragmentMyDrawingBoardLayout.findViewById<ImageView>(R.id.fragment_my_drawing_board_title_back)
        val img =
            mBinding!!.fragmentMyDrawingBoardLayout.findViewById<ImageView>(R.id.fragment_my_drawing_board_title_img)
        val name =
            mBinding!!.fragmentMyDrawingBoardLayout.findViewById<TextView>(R.id.fragment_my_drawing_board_title_name)
        img.setImageResource(R.drawable.palette)
        name.text = "个性化"
        back.setOnClickListener { v ->
            val controller = Navigation.findNavController(v)
            controller.navigateUp()
        }
    }

    internal inner class MyDrawingBoardAdapter(var mStringList: List<String>) : BaseAdapter() {
        var index = 0
        override fun getCount(): Int {
            return mStringList.size
        }

        override fun getItem(position: Int): Any {
            return mStringList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
            var convertView = convertView
            var viewHolder: ViewHolder = ViewHolder()
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.gv_layout, null)
                viewHolder.mImageView = convertView.findViewById(R.id.gv_layout_img)
                viewHolder.mTextView = convertView.findViewById(R.id.gv_layout_name)
                viewHolder.mLinearLayout = convertView.findViewById(R.id.gv_layout_layout)
                convertView.tag = viewHolder
            } else {
                viewHolder = convertView.tag as ViewHolder
            }
            viewHolder.mTextView!!.setTextColor(resources.getColor(R.color.white))
            viewHolder.mTextView!!.text = mStringList[position]
            when (position) {
                0 -> viewHolder.mImageView!!.background =
                    resources.getDrawable(R.drawable.gv_layout_colour)
                1 -> viewHolder.mImageView!!.background =
                    resources.getDrawable(R.drawable.gv_layout_colour2)
                2 -> viewHolder.mImageView!!.background =
                    resources.getDrawable(R.drawable.gv_layout_colour3)
                3 -> viewHolder.mImageView!!.background =
                    resources.getDrawable(R.drawable.gv_layout_colour4)
            }
            val drawable = viewHolder.mLinearLayout!!.background as GradientDrawable
            if (position == index) {
                drawable.setColor(Color.parseColor("#5AFFFFFF"))
            } else {
                drawable.color = null
            }
            return convertView
        }

        internal inner class ViewHolder {
            var mLinearLayout: LinearLayout? = null
            var mTextView: TextView? = null
            var mImageView: ImageView? = null
        }

        init {
            val sharedPreferences =
                context!!.getSharedPreferences("activitytheme", Context.MODE_PRIVATE)
            val string = sharedPreferences.getString("color", "蓝色")
            when (string) {
                "蓝色" -> index = 0
                "绿色" -> index = 1
                "橙色" -> index = 2
                "粉色" -> index = 3
            }
        }
    }
}