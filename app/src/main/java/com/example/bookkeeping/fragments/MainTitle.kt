package com.example.bookkeeping.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookkeeping.R
import com.example.bookkeeping.beans.RecyclerDataBean
import com.example.bookkeeping.databinding.FragmentMainTitleBinding
import com.example.bookkeeping.tools.LiveDataBus
import java.util.ArrayList

class MainTitle : Fragment() {
    var mBinding: FragmentMainTitleBinding? = null
    var mRecyclerDataBeans: MutableList<RecyclerDataBean>? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mBinding = FragmentMainTitleBinding.inflate(inflater, container, false)
        mRecyclerDataBeans = ArrayList()
        mRecyclerDataBeans!!.add(RecyclerDataBean("个性化", R.drawable.palette))
        mRecyclerDataBeans!!.add(RecyclerDataBean("货币", R.drawable.money))
        mRecyclerDataBeans!!.add(RecyclerDataBean("预算", R.drawable.wallet))
        mRecyclerDataBeans!!.add(RecyclerDataBean("关于", R.drawable.about))
        mBinding!!.fragmentMainTitleRecycler.layoutManager = LinearLayoutManager(context)
        mBinding!!.fragmentMainTitleRecycler.adapter = MyRecyclerAdapter(mRecyclerDataBeans as ArrayList<RecyclerDataBean>)
        return mBinding!!.root
    }

    internal inner class MyRecyclerAdapter(var mRecyclerDataBeans: List<RecyclerDataBean>) : RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.main_title_rv_layout, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.mImageView.setImageResource(mRecyclerDataBeans[position].img)
            holder.mTextView.text = mRecyclerDataBeans[position].name
            holder.mConstraintLayout.setOnClickListener { v ->
                val navigation = Navigation.findNavController(v)
                when (position) {
                    0 -> navigation.navigate(R.id.myDrawingBoard)
                    1 -> {
                    }
                    2 -> {
                        LiveDataBus.getInstance().with("matchMenu", Boolean::class.java).value = true
                        navigation.navigate(R.id.budget)
                    }
                    3 -> {
                    }
                }
            }
        }

        override fun getItemCount(): Int {
            return mRecyclerDataBeans.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var mImageView: ImageView
            var mTextView: TextView
            var mConstraintLayout: ConstraintLayout

            init {
                mTextView = itemView.findViewById(R.id.main_title_rv_layout_name)
                mImageView = itemView.findViewById(R.id.main_title_rv_layout_img)
                mConstraintLayout = itemView.findViewById(R.id.main_title_rv_layout_layout)
            }
        }
    }
}