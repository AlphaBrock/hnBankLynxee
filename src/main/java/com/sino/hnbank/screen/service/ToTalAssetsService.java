package com.sino.hnbank.screen.service;

import com.sino.hnbank.screen.pojo.TotalAssets;

import java.util.List;
import java.util.Map;

/**
 * @ClassName ToTalAssetsService
 * @Description
 * @Author zhangsch
 * @Date 2018/12/28 19:28
 * @UpdateTime 2018/12/28 19:28
 **/
public interface ToTalAssetsService {
    public List<Map<String,Object>> totalAssets(long dayN);
    public void insert(TotalAssets record);
    public void updateByDate(TotalAssets record);
}
