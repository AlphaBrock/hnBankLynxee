/*
package com.sino.hnbank.screen;

import com.sino.hnbank.screen.mapper.TotalAssetsMapper;
import com.sino.hnbank.screen.pojo.TransDayData;
import com.sino.hnbank.screen.service.ToTalAssetsService;
import com.sino.hnbank.screen.service.TransDayDataService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

*/
/**
 * EEE MMM dd HH:mm:ss zzz yyyy
 *//*

@RunWith(SpringRunner.class)
@SpringBootTest
public class HainanScreenApplicationTests {
    //解析接收到的数据的日期
    private String dateParseStr = "";
    //解析数据库内的日期、当前的日期和实体类中的日期
    private String dbParseStr = "yyyyMMdd";
    private String dbParseYear="yyyy";
    private SimpleDateFormat dateParsesdf = new SimpleDateFormat(dateParseStr);
    private SimpleDateFormat dbParsedf = new SimpleDateFormat(dbParseStr);
    private SimpleDateFormat dbYeardf = new SimpleDateFormat(dbParseYear);

    @Autowired
    private TransDayDataService transDayDataService;

    @Autowired
    private ToTalAssetsService toTalAssetsService;

    @Test
    public void transDayDataListTest(){
        List<TransDayData> transDayDataList = transDayDataService.selectTransDayDataListBycurrentDate();
        for (TransDayData transDayData : transDayDataList) {
            transDayData.setLastUpdateTime(new Timestamp(System.currentTimeMillis()));
        }
    }

    @Test
    public void transDayDataByCodeAndDate(){
        TransDayData transDayData = transDayDataService.selectByDateAndCode("456456", "20181228");
        System.out.println(transDayData);
    }

    @Test
    public void insertTransDayData(){
        List<TransDayData> transDayDataList = new ArrayList<>();
        for (int i =456456;i<456466;i++){
            Date date = new Date();
            String dataDate = dbParsedf.format(date);
            String dataYear = dbYeardf.format(date);
            TransDayData transDayData1 = new TransDayData();
            String s = String.valueOf(i);
            transDayData1.setBranchCode(s);
            transDayData1.setDataDate(dataDate);
            transDayData1.setDataYear(dataYear);
            transDayData1.setBranchName("海南某行");
            transDayData1.setLastUpdateTime(new Timestamp(System.currentTimeMillis()));
            transDayData1.setTransInAmount(BigDecimal.valueOf(456456));
            transDayData1.setTransOutAmount(BigDecimal.valueOf(456456));
            transDayData1.setAccountOpenNum(BigDecimal.valueOf(123654));
            transDayData1.setTransCountTotal(BigDecimal.valueOf(666));
            transDayData1.setPrivateInAmount(BigDecimal.valueOf(789));
            transDayData1.setPrivateOutAmount(BigDecimal.valueOf(987));
            transDayData1.setPublicInAmount(BigDecimal.valueOf(159));
            transDayData1.setPublicOutAmount(BigDecimal.valueOf(951));
            transDayData1.setOtherBankInAmount(BigDecimal.valueOf(789));
            transDayData1.setOtherBankOutAmount(BigDecimal.valueOf(79878));
            transDayDataList.add(transDayData1);
        }
        for (TransDayData transDayData : transDayDataList) {
            transDayDataService.insertTransDayData(transDayData);
        }
    }
    @Test
    public void totalAmoutTest(){
        BigDecimal bigDecimal = transDayDataService.totalTransAmount();
        System.out.println(bigDecimal.toString());
    }

    @Test
    public void totalCountTest(){
        BigDecimal bigDecimal = transDayDataService.totalTransCount();
        System.out.println(bigDecimal.toString());
    }

    @Test
    public void selectOneByCode(){
        TransDayData transDayData = transDayDataService.selectByDateAndCode("461103");
        System.out.println(transDayData);
    }

    @Test
    public void sortByCount(){
        List<TransDayData> transDayDataList = transDayDataService.sortBranchByCount(2);
        for (TransDayData transDayData : transDayDataList) {
            System.out.println(transDayData);
        }
    }

    @Test
    public void sortByOpen(){
        List<TransDayData> transDayDataList = transDayDataService.sortBranchByOpen(4);
        for (TransDayData transDayData : transDayDataList) {
            System.out.println(transDayData);
        }

    }

   */
/* @Test
    public void totalOtherBankAmount(){
        Map<String, List> stringListMap = transDayDataService.totalOtherBankAoumt(2);
        Set<Map.Entry<String, List>> entries = stringListMap.entrySet();
        for (Map.Entry<String, List> entry : entries) {
            System.out.println(entry.getKey()+entry.getValue());
        }

    }*//*


   @Test
    public void totalAssetsTest(){
       List<Map<String, Object>> list = toTalAssetsService.totalAssets(4);
       for (Map<String, Object> map : list) {
           System.out.println(map);
       }

   }

}

*/
