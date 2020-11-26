package com.luoyue.coolweather.db;

import org.litepal.crud.LitePalSupport;
/**
 * 代表省份的数据表类映射
 */
public class Province extends LitePalSupport {
    //省份id
    private int id;
    //省份名称
    private String provinceName;
    //省份代号
    private int provinceCode;

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}
