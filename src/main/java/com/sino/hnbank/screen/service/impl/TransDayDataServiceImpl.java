package com.sino.hnbank.screen.service.impl;

import com.sino.hnbank.screen.mapper.TransDayDataMapper;
import com.sino.hnbank.screen.pojo.TransDayData;
import com.sino.hnbank.screen.pojo.TransDayDataMap;
import com.sino.hnbank.screen.service.TransDayDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName TransDayDataServiceImpl
 * @Description
 * @Author zhangsch
 * @Date 2018/12/27 16:00
 * @UpdateTime 2018/12/27 16:00
 **/
@Service
public class TransDayDataServiceImpl implements TransDayDataService {

    private String currentParse = "yyyyMMdd";
    private SimpleDateFormat sdf = new SimpleDateFormat(currentParse);
    private Date currentDate ;

    @Autowired
    private TransDayDataMapper transDayDataMapper;

    /**
     * 插入实体类
     * @param transDayData
     */
    @Override
    public void insertTransDayData(TransDayData transDayData) {
        transDayDataMapper.insert(transDayData);
    }

    /**
     * 根据日期和银行代码更新表
     * @param transDayData
     */
    @Override
    public void updateByDateAndCode(TransDayData transDayData) {
        transDayDataMapper.updateByDateAndCode(transDayData);
    }

    /**
     * 获取表中日期集合
     * @return
     */
    @Override
    public List<String> selectDataDate() {
        return transDayDataMapper.selectDataDate();
    }
    /**
     * 根据当前日期获得表项集合
     * @return
     */
    @Override
    public List<TransDayData> selectTransDayDataListBycurrentDate() {
        currentDate = new Date();
        String currentDateStr = sdf.format(currentDate);
        List<TransDayData> transDayDataList = transDayDataMapper.selectTransDayDataListBycurrentDate(currentDateStr);
        return transDayDataList;
    }

    /**
     * 根据当前日期获得表项集合（地图实体）
     * @return
     */
    @Override
    public List<TransDayDataMap> selectTransDayDataMapListBycurrentDate() {
        currentDate = new Date();
        String currentDateStr = sdf.format(currentDate);
        List<TransDayData> transDayDataList = transDayDataMapper.selectTransDayDataListBycurrentDate(currentDateStr);
        List<TransDayDataMap> transDayDataMapList = new ArrayList<>();
        //遍历总实体，进行地图实体的封装
        for (TransDayData transDayData : transDayDataList) {
            TransDayDataMap transDayDataMap = new TransDayDataMap();
            transDayDataMap.setBranchCode(transDayData.getBranchCode());
            transDayDataMap.setBranchName(transDayData.getBranchName());
            transDayDataMap.setCity(transDayData.getBranchName());
            List<Map<String, Object>> branchDataList = new ArrayList<>();
            Map<String,Object> map = new HashMap<>();
            map.put("key","对公开户数(户)：");
            map.put("value",transDayData.getAccountOpenNumPub());
            branchDataList.add(map);
            map = new HashMap<>();
            map.put("key","对私开户数(户)：");
            map.put("value",transDayData.getAccountOpenNumPri());
            branchDataList.add(map);
            map = new HashMap<>();
            map.put("key","交易笔数(笔)：");
            map.put("value",transDayData.getTransCountTotal());
            branchDataList.add(map);
            map = new HashMap<>();
            map.put("key","交易金额(进账/元)：");
            map.put("value",transDayData.getTransInAmount());
            branchDataList.add(map);
            map = new HashMap<>();
            map.put("key","交易金额(支出/元)：");
            map.put("value",transDayData.getTransOutAmount());
            branchDataList.add(map);

            //将支行信息放入实体
            transDayDataMap.setBranchDataList(branchDataList);
            //将地图模块实体放入list
            transDayDataMapList.add(transDayDataMap);
        }
        return transDayDataMapList;
    }

    /**
     * 根据银行代码和日期获得实体
     * @param branchCode
     * @param dataDate
     * @return
     */
    @Override
    public TransDayData selectByDateAndCode(String branchCode,String dataDate) {
        TransDayData transDayData = transDayDataMapper.selectByDateAndCode(branchCode,dataDate);
        return transDayData;
    }

    /**
     * 根据当前日期获取全行交易金额（含行内）
     * @return
     */
    @Override
    public BigDecimal totalTransAmount() {
        //当前系统时间
        currentDate = new Date();
        String currentDateStr = sdf.format(currentDate);
        //支出和收入金额
        BigDecimal inAmount = transDayDataMapper.totalTransInAmount(currentDateStr);
        BigDecimal outAmount = transDayDataMapper.totalTransOutAmount(currentDateStr);

        //计算总和
        BigDecimal totalAmount = new BigDecimal(0);
        totalAmount = totalAmount.add(inAmount);
        totalAmount = totalAmount.add(outAmount);
        return totalAmount;
    }

    /**
     * 获取当前日期业务笔数的总和
     * @return
     */
    @Override
    public BigDecimal totalTransCount() {
        //当前系统时间
        currentDate = new Date();
        String currentDateStr = sdf.format(currentDate);
        //业务笔数总和
        BigDecimal totalTransCount = transDayDataMapper.totalTransCount(currentDateStr);
        return totalTransCount;
    }

    /**
     * 根据银行代码获取当前日期的银行信息
     * @param branchCode
     * @return
     */
    @Override
    public TransDayData selectByDateAndCode(String branchCode) {
        //当前系统时间
        currentDate = new Date();
        String currentDateStr = sdf.format(currentDate);
        TransDayData transDayData = transDayDataMapper.selectByDateAndCode(branchCode, currentDateStr);
        return transDayData;
    }

    /**
     * 获取当前日期的开户笔数前五的银行信息
     * @return
     */
    @Override
    public List<TransDayData> sortBranchByOpen(long topN) {
        //当前系统时间
        currentDate = new Date();
        String currentDateStr = sdf.format(currentDate);
        List<TransDayData> transDayDataList = transDayDataMapper.sortBranchByOpen(currentDateStr,topN);
        return transDayDataList;
    }


    /**
     * 获取当前日期的业务笔数前五的银行信息
     * @return
     */
    @Override
    public List<TransDayData> sortBranchByCount(long topN) {
        //当前系统时间
        currentDate = new Date();
        String currentDateStr = sdf.format(currentDate);
        List<TransDayData> transDayDataList = transDayDataMapper.sortBranchByCount(currentDateStr,topN);
        return transDayDataList;
    }

    /**
     * 获取与他行来往总额（收入和支出分开）
     * @return
     */
    @Override
    public List<Map<String, Object>> totalOtherBankAoumt(long dayN) {
        List<Map<String, Object>> list = transDayDataMapper.totalOtherBankAmount(dayN);
        for (Map<String, Object> stringObjectMap : list) {
            String data_date = (String) stringObjectMap.get("data_date");
            StringBuffer sb = new StringBuffer(data_date);
            sb.insert(6,".");
            sb.insert(4,".");
            stringObjectMap.put("data_date",sb.toString());
        }

        return list;
    }

    /**
     * 向数据库插入当天的数据
     * @throws Exception
     */
    @Override
    public void addData(String date) throws Exception {
        TransDayData[] transDayDataList =new  TransDayData[23];
        for(int i =0;i<23;i++){
            TransDayData transDayData1 = new TransDayData();
            transDayData1.setDataDate(date);
            transDayData1.setSortNum(i+1);
            transDayData1.setLastUpdateTime(new Timestamp(System.currentTimeMillis()));
            transDayData1.setTransInAmount(BigDecimal.valueOf(456756+i*1000));
            transDayData1.setTransOutAmount(BigDecimal.valueOf(456456+i*1000));
            transDayData1.setAccountOpenNumPri(BigDecimal.valueOf(123654+i*1000));
            transDayData1.setAccountOpenNumPub(BigDecimal.valueOf(123754+i*1000));
            transDayData1.setTransCountTotal(BigDecimal.valueOf(666+i*1000));
            transDayData1.setPrivateInAmount(BigDecimal.valueOf(789+i*1000));
            transDayData1.setPrivateOutAmount(BigDecimal.valueOf(987+i*1000));
            transDayData1.setPublicInAmount(BigDecimal.valueOf(159+i*1000));
            transDayData1.setPublicOutAmount(BigDecimal.valueOf(951+i*1000));
            transDayData1.setOtherBankInAmount(BigDecimal.valueOf(789+i*1000));
            transDayData1.setOtherBankOutAmount(BigDecimal.valueOf(79878+i*1000));
            transDayDataList[i]=transDayData1;
        }
        transDayDataList[0].setBranchCode("461103");transDayDataList[0].setBranchName("海口海甸支行");
        transDayDataList[1].setBranchCode("465531");transDayDataList[1].setBranchName("昌江支行");
        transDayDataList[2].setBranchCode("466601");transDayDataList[2].setBranchName("琼海支行");
        transDayDataList[3].setBranchCode("465502");transDayDataList[3].setBranchName("洋浦支行");
        transDayDataList[4].setBranchCode("461101");transDayDataList[4].setBranchName("海口滨海支行");
        transDayDataList[5].setBranchCode("465551");transDayDataList[5].setBranchName("澄迈科技支行");
        transDayDataList[6].setBranchCode("466631");transDayDataList[6].setBranchName("万宁支行");
        transDayDataList[7].setBranchCode("466621");transDayDataList[7].setBranchName("定安支行");
        transDayDataList[8].setBranchCode("461000");transDayDataList[8].setBranchName("海南银行总行");
        transDayDataList[9].setBranchCode("461102");transDayDataList[9].setBranchName("海口红城湖支行");
        transDayDataList[10].setBranchCode("463301");transDayDataList[10].setBranchName("三沙支行");
        transDayDataList[11].setBranchCode("462241");transDayDataList[11].setBranchName("乐东支行");
        transDayDataList[12].setBranchCode("465561");transDayDataList[12].setBranchName("白沙支行");
        transDayDataList[13].setBranchCode("462251");transDayDataList[13].setBranchName("保亭支行");
        transDayDataList[14].setBranchCode("462231");transDayDataList[14].setBranchName("陵水支行");
        transDayDataList[15].setBranchCode("465552");transDayDataList[15].setBranchName("澄迈支行");
        transDayDataList[16].setBranchCode("465541");transDayDataList[16].setBranchName("东方支行");
        transDayDataList[17].setBranchCode("467701");transDayDataList[17].setBranchName("五指山支行");
        transDayDataList[18].setBranchCode("466611");transDayDataList[18].setBranchName("文昌支行");
        transDayDataList[19].setBranchCode("461104");transDayDataList[19].setBranchName("海口五源河支行");
        transDayDataList[20].setBranchCode("465521");transDayDataList[20].setBranchName("临高支行");
        transDayDataList[21].setBranchCode("467711");transDayDataList[21].setBranchName("屯昌支行");
        transDayDataList[22].setBranchCode("467721");transDayDataList[22].setBranchName("琼中支行");

        for (TransDayData transDayData : transDayDataList) {
            transDayDataMapper.insert(transDayData);
        }
    }
}
