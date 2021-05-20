package com.sino.hnbank.screen.service;

import com.sino.hnbank.screen.pojo.TransDayData;
import com.sino.hnbank.screen.pojo.TransDayDataMap;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @ClassName TransDayDataService
 * @Description
 * @Author zhangsch
 * @Date 2018/12/27 11:15
 * @UpdateTime 2018/12/27 11:15
 **/
public interface TransDayDataService {

    public void insertTransDayData(TransDayData transDayData);
    public void updateByDateAndCode(TransDayData transDayData);
    public List<String> selectDataDate();
    public List<TransDayData> selectTransDayDataListBycurrentDate();
    public List<TransDayDataMap> selectTransDayDataMapListBycurrentDate();
    public TransDayData selectByDateAndCode(String branchCode,String dataDate);
    public BigDecimal totalTransAmount();
    public BigDecimal totalTransCount();
    public List<Map<String, Object>> totalOtherBankAoumt(long dayN);
    public TransDayData selectByDateAndCode(String branchCode);
    public List<TransDayData> sortBranchByOpen(long topN);
    public List<TransDayData> sortBranchByCount(long topN);
    public void addData(String date)throws Exception ;
}
