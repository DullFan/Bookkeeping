package com.example.bookkeeping.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bookkeeping.R;
import com.example.bookkeeping.beans.BudgetGetBean;
import com.example.bookkeeping.beans.ConsumptionBean;
import com.example.bookkeeping.beans.PostBean;
import com.example.bookkeeping.databinding.FragmentBudgetBinding;
import com.example.bookkeeping.tools.HttpUtil;
import com.example.bookkeeping.tools.Tools;
import com.google.gson.Gson;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;


import static com.example.bookkeeping.tools.LiveDataBus.getInstance;

public class Budget extends Fragment {
    private FragmentBudgetBinding mBinding;
    private HttpUtil httpUtil;
    private double money;
    private int id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentBudgetBinding.inflate(inflater,container,false);
        ImageView imageView = mBinding.fragmentMyDrawingBoardLayout.findViewById(R.id.fragment_my_drawing_board_title_img);
        ImageView back = mBinding.fragmentMyDrawingBoardLayout.findViewById(R.id.fragment_my_drawing_board_title_back);
        TextView textView = mBinding.fragmentMyDrawingBoardLayout.findViewById(R.id.fragment_my_drawing_board_title_name);
        imageView.setImageResource(R.drawable.wallet);
        textView.setText("预算设置");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInstance().with("wrapMenu", Boolean.class).setValue(true);
                Navigation.findNavController(v).navigateUp();
                getInstance().with("Budgetflag",Boolean.class).observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean aBoolean) {
                        Navigation.findNavController(v).navigateUp();
                    }
                });
            }
        });

        //查询预算
        initData();
        //设置预算
        initButton();
        return mBinding.getRoot();
    }

    private void initButton() {
        mBinding.budgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                View view = View.inflate(getContext(),R.layout.layout_budget_dialog,null);
                EditText editText = view.findViewById(R.id.budget_dialog_edit);
                int index = (money+"").indexOf(".");
                String substring = (money + "").substring(0, index);
                editText.setText(substring);
                TextView queren = view.findViewById(R.id.budget_dialog_button);
                queren.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HttpUtil httpUtil = new HttpUtil();
                        if(editText.getText().toString().isEmpty()){
                            Tools.showToast(getContext(),"请输入信息");
                            return;
                        }
                        Handler handler = new Handler(){
                            @Override
                            public void handleMessage(@NonNull Message msg) {
                                PostBean postBean = (PostBean) msg.obj;
                                if(postBean.getCode().equals("200")){
                                    Tools.showToast(getContext(),"修改成功");
                                    //查询预算
                                    initData();
                                    getInstance().with("initBudget",Boolean.class).setValue(true);
                                    alertDialog.dismiss();
                                }
                            }
                        };
                        Gson gson = new Gson();
                        Map<String,Object>map = new HashMap<>();
                        map.put("userId",id);
                        map.put("budgetMoney",editText.getText().toString());
                        String s = gson.toJson(map);
                        httpUtil.okhttpPut("/budget/upd",s,handler, PostBean.class);
                    }
                });
                alertDialog.setView(view);
                alertDialog.show();
            }
        });
    }

    private void initData() {
        httpUtil = new HttpUtil();
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("login",getContext().MODE_PRIVATE);
        id = sharedPreferences.getInt("id", 0);
        Time time = new Time("GTM+8");
        time.setToNow();
        int year = time.year;
        int month = time.month+1;
        mBinding.budgetDate.setText(year+"年"+month+"月预算");
        Handler handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                BudgetGetBean budgetGetBean = (BudgetGetBean) msg.obj;
                if(budgetGetBean.getData().getBgMoney().contains(".")){
                    int index = budgetGetBean.getData().getBgMoney().indexOf(".");
                    String substring = budgetGetBean.getData().getBgMoney().substring(0, index);
                    mBinding.budgetProgressBar.setMax(Integer.valueOf(substring));
                }else{
                    mBinding.budgetProgressBar.setMax(Integer.valueOf(budgetGetBean.getData().getBgMoney()));
                }
                money = Double.valueOf(budgetGetBean.getData().getBgMoney());
                initMoney(Double.valueOf(budgetGetBean.getData().getBgMoney()), id,year+"-"+month);
            }
        };
        httpUtil.httpGet(
                "/budget/getone/"+ id,
                handler,
                BudgetGetBean.class
            );
    }

    private void initMoney(Double valueOf, int id, String s) {
        String url  ="/consumption/getmonth/"+id+"/"+s;
        Handler handler = new Handler(){
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(@NonNull Message msg) {
                ConsumptionBean bean = (ConsumptionBean) msg.obj;
                double num = 0.0;
                for (int i = 0; i < bean.getData().size(); i++) {
                    if(bean.getData().get(i).getCon_consume().equals("1")){
                        num += Double.valueOf(bean.getData().get(i).getCon_money());
                    }
                }

                if(num > valueOf){
                    mBinding.budgetSurplus.setText("剩余0%");
                    mBinding.budgetProgressBar.setProgress((int) num);
                    mBinding.budgetMoney1.setText("0");
                    mBinding.budgetMoney2.setText(valueOf+"");
                    mBinding.budgetMoney3.setText(""+num);
                }else{
                    NumberFormat numberFormat = NumberFormat.getInstance();
                    numberFormat.setMaximumFractionDigits(2);
                    mBinding.budgetSurplus.setText("剩余"+(numberFormat.format((valueOf -num)/valueOf * 100))+"%");
                    mBinding.budgetProgressBar.setProgress((int)num);
                    mBinding.budgetMoney1.setText(valueOf - num + "");
                    mBinding.budgetMoney2.setText(""+valueOf);
                    mBinding.budgetMoney3.setText(num+"");
                }
            }
        };
        httpUtil.httpGet(url,handler, ConsumptionBean.class);
    }


}