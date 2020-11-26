package com.luoyue.coolweather.util;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.luoyue.coolweather.db.County;
import com.luoyue.coolweather.db.Province;
import com.luoyue.coolweather.db.City;
import com.luoyue.coolweather.gson.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utility {
    /**
     * 解析和处理服务器返回的省级数据
     */
    public static boolean handleProvinceResponse(String response){
        //如果数据非空
        if(!TextUtils.isEmpty(response)){
            try{
                //创建josn格式解析数组，解析得到的数据
                JSONArray allProvinces=new JSONArray(response);
                //遍历每一个数据
                for (int i=0;i<allProvinces.length();i++){
                    JSONObject provinceObject=allProvinces.getJSONObject(i);
                    //创建一个省的类映射
                    Province province=new Province();
                    //给相应属性赋值
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    //保存,放到数据库
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    /**
     * 解析和处理服务器返回的市级数据
     */
    public static boolean handleCityResponse(String response,int provinceId){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray allCities=new JSONArray(response);
                for(int i=0;i<allCities.length();i++){
                    JSONObject cityObject=allCities.getJSONObject(i);
                    //创建一个市的类映射
                    City city=new City();

                    city.setCityName(cityObject.getString("name"));
                    city.setCityCode(cityObject.getInt("id"));
                    //设置相应的省id值
                    city.setProvinceId(provinceId);
                    //保存数据到数据库
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    /**
     * 解析和处理服务器返回的县级数据
     */
    public static boolean handleCountyResponse(String response,int cityId){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray allCounties=new JSONArray(response);
                for(int i=0;i<allCounties.length();i++){
                    JSONObject countyObject=allCounties.getJSONObject(i);
                    //创建一个县的类映射
                    County county=new County();
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    //设置相应的市id值
                    county.setCityId(cityId);
                    //保存数据到数据库
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    /**
     * 将返回的JSON数据解析成Weather实体类
     */
    public static Weather handleWeatherResponse(String response){
        try{
            //创建json解析对象
            JSONObject jsonObject=new JSONObject(response);
            //创建json解析数组
            JSONArray jsonArray=jsonObject.getJSONArray("HeWeather");
            //获得第一个数据
            String weatherContent=jsonArray.getJSONObject(0).toString();

            //将得到的json数据转化为weather对象返回
            return new Gson().fromJson(weatherContent,Weather.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
