package com.luoyue.coolweather.db;

import org.litepal.crud.LitePalSupport;
/**
 * 县数据表的类映射
 */
public class County extends LitePalSupport {
    //县的id
    private int id;
    //县的名称
    private String countyName;
    //天气id
    private String weatherId;
    //所属市的id值
    private int CityId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public int getCityId() {
        return CityId;
    }

    public void setCityId(int cityId) {
        CityId = cityId;
    }
}
