package com.luoyue.coolweather.gson;

import com.google.gson.annotations.SerializedName;
/**
 * json数据--城市基本天气信息
 */
public class Basic {
    //json注解
    @SerializedName("city")
    public String cityName;//城市名

    @SerializedName("id")
    public String weatherId;//城市天气id

    public Update update;//天气更新时间

    public class  Update{
        @SerializedName("loc")
        public String updateTime;
    }
}
