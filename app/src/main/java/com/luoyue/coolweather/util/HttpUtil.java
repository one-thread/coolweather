package com.luoyue.coolweather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpUtil {
    public static void sendOkHttpRequest(String address,okhttp3.Callback callback){
        //创建一个OkHttpClient实例
        OkHttpClient client=new OkHttpClient();
        //创建Request实例,用以发送http请求,address是目标地址
        Request request=new Request.Builder().url(address).build();
        //注册回调来处理服务器响应
        client.newCall(request).enqueue(callback);
    }
}
