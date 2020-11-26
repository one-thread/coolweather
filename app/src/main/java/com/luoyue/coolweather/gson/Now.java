package com.luoyue.coolweather.gson;

import com.google.gson.annotations.SerializedName;
/**
 * JSON数据--当前天气
 */
public class Now {
    @SerializedName("tmp")
    public String temperature;//温度

    @SerializedName("cond")
    public More mroe;//天气情况

    public class More{
        @SerializedName("txt")
        public String info;
    }
}
