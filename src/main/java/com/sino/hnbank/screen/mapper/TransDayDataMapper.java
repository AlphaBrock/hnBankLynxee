package com.sino.hnbank.screen.mapper;

import com.sino.hnbank.screen.pojo.TransDayData;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface TransDayDataMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(TransDayData record);

    int insertSelective(TransDayData record);

    TransDayData selectByPrimaryKey(Integer id);

    List<String> selectDataDate();

    List<TransDayData> selectTransDayDataListBycurrentDate(String dataDate);

    TransDayData selectByDateAndCode(@Param("branchCode") String branchCode, @Param("dataDate")String dataDate);

    BigDecimal totalTransInAmount(String dataDate);
    BigDecimal totalTransOutAmount(String dataDate);

    BigDecimal totalTransCount(String dataDate);

    List<TransDayData> sortBranchByOpen(@Param("dataDate")String dataDate,@Param("topN")long topN);

    List<TransDayData> sortBranchByCount(@Param("dataDate")String dataDate,@Param("topN")long topN);

    List<Map<String,Object>> totalOtherBankInAmount(long dayN);
    List<Map<String,Object>> totalOtherBankOutAmount(long dayN);
    List<Map<String,Object>> totalOtherBankAmount(long dayN);

    int updateByPrimaryKeySelective(TransDayData record);

    int updateByPrimaryKey(TransDayData record);

    int updateByDateAndCode(TransDayData record);
}