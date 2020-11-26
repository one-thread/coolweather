package com.luoyue.coolweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;
/**
 * 综合的天气信息
 */
public class Weather {
    public String status;//状态,成功-返回ok,失败-返回原因

    public Basic basic;//基础

    public AQI aqi;//aqi信息

    public Now now;//当前天气

    public Suggestion suggestion;//建议

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;//未来多日天气信息
}
