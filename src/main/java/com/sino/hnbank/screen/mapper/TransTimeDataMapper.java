package com.sino.hnbank.screen.mapper;

import com.sino.hnbank.screen.pojo.TransTimeData;

public interface TransTimeDataMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TransTimeData record);

    int insertSelective(TransTimeData record);

    TransTimeData selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TransTimeData record);

    int updateByPrimaryKey(TransTimeData record);
}