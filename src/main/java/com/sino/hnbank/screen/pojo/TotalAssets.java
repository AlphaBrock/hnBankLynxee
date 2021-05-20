package com.sino.hnbank.screen.pojo;

import java.math.BigDecimal;

public class TotalAssets {
    private Integer id;

    private String date;

    private BigDecimal totalAsset;

    private BigDecimal totalLiability;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date == null ? null : date.trim();
    }

    public BigDecimal getTotalAsset() {
        return totalAsset;
    }

    public void setTotalAsset(BigDecimal totalAsset) {
        this.totalAsset = totalAsset;
    }

    public BigDecimal getTotalLiability() {
        return totalLiability;
    }

    public void setTotalLiability(BigDecimal totalLiability) {
        this.totalLiability = totalLiability;
    }
}