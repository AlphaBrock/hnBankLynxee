package com.sino.hnbank.screen.mapper;

import com.sino.hnbank.screen.pojo.TotalAssets;

import java.util.List;
import java.util.Map;

public interface TotalAssetsMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TotalAssets record);

    int insertSelective(TotalAssets record);

    TotalAssets selectByPrimaryKey(Integer id);

    List<Map<String,Object>> totalAssets(long dayN);

    int updateByPrimaryKeySelective(TotalAssets record);

    int updateByPrimaryKey(TotalAssets record);

    int updateByDate(TotalAssets record);

    TotalAssets selectByDate(String date);
}