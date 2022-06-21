package com.example.bookkeeping.tools;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtil {
    static final String IP_PORT = "http://124.220.166.72:8080";
    private OkHttpClient client = new OkHttpClient();

    public void httpPost(String url, String data, Handler handler, final Class clazz){
        url = IP_PORT+url;
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody requestBody = RequestBody.create(mediaType,data);
        final Request request = new Request.Builder()
                .post(requestBody)
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override//请求失败调用
            public void onFailure(Call call, IOException e) {
                Log.d("TAG", "onFailure: "+call.toString());
                e.printStackTrace();
            }

            @Override//请求成功调用
            public void onResponse(Call call, Response response) throws IOException {
                Message message = new Message();
                //body代表返回体
                message.obj = new Gson().fromJson(response.body().string(),clazz);
                //发送数据
                handler.sendMessage(message);
            }
        });
    }

    //不带toKen的get请求
    public void httpGet(String url,Handler handler,Class clazz){
        url = IP_PORT + url;
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override//失败时调用
            public void onFailure(Call call, IOException e) {
                Log.i("TAG", "onFailure: "+call.toString());
                e.printStackTrace();
            }

            @Override//成功时调用
            public void onResponse(Call call, Response response) throws IOException {
                Message message = new Message();
                message.obj = new Gson().fromJson(response.body().string(),clazz);
                handler.sendMessage(message);
            }
        });
    }

    //带toKem的put请求
    public void okhttpPut(String url, String data, final Handler handler, Class clazz){
        String allUrl = IP_PORT+url;
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, data);
        Request request = new Request.Builder()
                .url(allUrl)
                .method("PUT", body)
                .addHeader("Content-Type", "application/json")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message message = new Message();
                message.obj = new Gson().fromJson(response.body().string(),clazz);
                handler.sendMessage(message);
            }
        });
    }

    //带toKen的delete请求
    public void okhttpDelete(String url,String data,final Handler handler,final Class clazz){
        url = IP_PORT+url;
        MediaType mediaType = MediaType.parse("application/josn; charset=utf-8");
        RequestBody requestBody = RequestBody.create(mediaType,data);
        final Request request = new Request.Builder()
                .delete(requestBody)
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("TAG", "onFailure: "+call.toString());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message message = new Message();
                message.obj = new Gson().fromJson(response.body().string(),clazz);
                handler.sendMessage(message);
            }
        });
    }
}
