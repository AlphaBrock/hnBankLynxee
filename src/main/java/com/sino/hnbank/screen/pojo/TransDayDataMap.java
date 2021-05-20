package com.sino.hnbank.screen.pojo;

import java.util.List;
import java.util.Map;

/**
 * @ClassName TransDayDataMap
 * @Description 地图模块使用的Bean
 * @Author Administrator
 * @Date 2018/12/28 19:42
 * @UpdateTime 2018/12/28 19:42
 **/
public class TransDayDataMap {
    private String branchCode;
    private String branchName;
    private List<Map<String,Object>> branchDataList;
    private String city;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public List<Map<String, Object>> getBranchDataList() {
        return branchDataList;
    }

    public void setBranchDataList(List<Map<String, Object>> branchDataList) {
        this.branchDataList = branchDataList;
    }

    @Override
    public String toString() {
        return "TransDayDataMap{" +
                "branchCode='" + branchCode + '\'' +
                ", branchName='" + branchName + '\'' +
                ", branchDataList=" + branchDataList +
                '}';
    }
}
