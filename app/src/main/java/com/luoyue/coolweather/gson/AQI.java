package com.luoyue.coolweather.gson;
/**
 * json数据--aqi信息
 */
public class AQI {
    //城市
    public AQICity city;

    public class AQICity{
        public String aqi;//aqi指数

        public String pm25;//pm25指数
    }
}
