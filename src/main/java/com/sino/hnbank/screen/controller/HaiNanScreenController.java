package com.sino.hnbank.screen.controller;

import com.alibaba.fastjson.JSON;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sino.hnbank.screen.pojo.TotalAssets;
import com.sino.hnbank.screen.pojo.TransDayData;
import com.sino.hnbank.screen.quartz.ScreenData;
import com.sino.hnbank.screen.service.ToTalAssetsService;
import com.sino.hnbank.screen.service.TransDayDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

/**
 * @ClassName HnbScreenController
 * @Description
 * @Author zhangsch
 * @Date 2018/12/26 16:10
 * @UpdateTime 2018/12/26 16:10
 **/
@Controller
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class HaiNanScreenController {
    @Autowired
    private TransDayDataService transDayDataService;

    @Autowired
    private ToTalAssetsService toTalAssetsService;

    @Autowired
    private ScreenData screenData;

    private static final Logger logger = LoggerFactory.getLogger(HaiNanScreenController.class);

    /**
     * 获取当天开户数TOPN的支行,默认TOP5
     *
     * @param topN
     * @return
     */
    @ResponseBody
    @GetMapping("/getBranchAccountOpenNum")
    public List<TransDayData> getBranchAccountOpenNum(
            @RequestParam(name = "topN", required = false, defaultValue = "5")
                    int topN) throws Exception {
        List<TransDayData> transDayDataList = screenData.buildBranchAccountOpenNum(topN);

        return transDayDataList;
    }

    /**
     * 获取当天业务笔数TOPN的支行,默认TOP5
     *
     * @param topN
     * @return
     */
    @ResponseBody
    @GetMapping("/getBranchTransCountTotal")
    public List<TransDayData> getBranchTransCountTotal(
            @RequestParam(name = "topN", required = false, defaultValue = "5")
                    int topN) throws Exception {
        List<TransDayData> transDayDataList = screenData.buildBranchTransCountTotal(topN);
        return transDayDataList;
    }

    /**
     * 获取当天各支行全部指标信息,包含支行ID,名称,开户数,交易金额(进账),交易金额(出账),对私转出金额,对私存入金额,对公转出金额,对公存入金额
     *
     * @return
     */
    @ResponseBody
    @GetMapping("/getBranchAllMetric")
    public List<TransDayData> getBranchAllMetric(@RequestParam(name = "branchCodes", required = false, defaultValue = "")
                                                         String branchCodes) throws Exception {

        List<TransDayData> list = screenData.buildBranchAllMetric();

        try {
            if (!"".equals(branchCodes)) {
                String[] branchCodeList = branchCodes.split(",");
                List<TransDayData> transDayDataList = new ArrayList<>();
                for (TransDayData transDayData : list) {
                    String branchCode = transDayData.getBranchCode();
                    for (String s : branchCodeList) {
                        if (branchCode.equals(s)) {
                            transDayDataList.add(transDayData);
                        }
                    }

                }
                return transDayDataList;
            }
        } catch (Exception e) {
            logger.error("cant format branchCodes {}", e);
        }
        for (TransDayData transDayData : list) {
            if ("461998".equals(transDayData.getBranchCode())) {
                list.remove(transDayData);
                break;
            }
        }
        return list;
    }


    /**
     * 获取当天全行交易总额,业务笔数
     *
     * @return
     */
    @ResponseBody
    @GetMapping("/getAllBankMetric")
    public Map<String, Long> getAllBankMetric() throws Exception {
        Map<String, Long> map = ScreenData.allBank;
        return map;
    }


    /**
     * 获取当天与他行往来总金额,包括支出,收入
     *
     * @return
     */
    @ResponseBody
    @GetMapping("/getAllBankToOtherBank")
    public Map<String, BigDecimal> getAllBankToOtherBank() throws Exception {
        Map<String, BigDecimal> map = screenData.buildAllBankToOtherBank();
        return map;
    }

    /**
     * 获取对公和对私的总客户数
     * @return
     * @throws Exception
     */
    @ResponseBody
    @GetMapping("/getCustomerNum")
    public Map<String, Long> getCustomerNum() throws Exception {
        Map<String, Long> map = screenData.buildCustomerNum();
        return map;
    }

    /**
     * 根据key获取ListJson数据
     *
     * @return
     */
    @ResponseBody
    @GetMapping("/getListJson")
    public String getListJson(
            @RequestParam(name = "key", required = true) String key,
            @RequestParam(name = "branchCodes", required = false, defaultValue = "")
                    String branchCodes) {
        JsonArray jsonElements = screenData.buildListJson(key);
        if (jsonElements == null || jsonElements.isJsonNull()) {
            return "";
        }
        String json = jsonElements.toString();
        return json;
    }

    @ResponseBody
    @GetMapping("/getMapJson")
    public String getMapJson(
            @RequestParam(name = "key", required = true) String key,
            @RequestParam(name = "branchCodes", required = false, defaultValue = "")
                    String branchCodes) {
        JsonObject jsonObject = screenData.buildMapJson(key);
        if (jsonObject == null || jsonObject.isJsonNull()) {
            return "";
        }
        String json = jsonObject.toString();
        return json;
    }



    /**
     * 插入指定日期的测试数据（默认当天）
     *
     * @param date
     * @return
     */
    @ResponseBody
    @GetMapping("/addData")
    public Map<String, String> addData(@RequestParam(name = "date", required = false, defaultValue = "7")
                                               String date) {
        Map<String, String> resultMap = new HashMap<>();
        try {
            transDayDataService.addData(date);
            resultMap.put("addAcitve", "SUCCESS");
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("addAcitve", "FAILED");
            return resultMap;
        }

    }

    /**
     * 添加（更新）总资产和总负债
     *
     * @param date
     * @param totalAssets
     * @param totalLiability
     * @return
     */
    @ResponseBody
    @GetMapping("/updateTotalAssets")
    public Map<String, String> updateTotalAssets(
            @RequestParam(name = "date", required = true) String date,
            @RequestParam(name = "totalAssets", required = true) BigDecimal totalAssets,
            @RequestParam(name = "totalLiability", required = true) BigDecimal totalLiability
    ) {
        Map<String, String> resultMap = new HashMap<>();
        try {
            TotalAssets totalAssets1 = new TotalAssets();
            totalAssets1.setDate(date);
            totalAssets1.setTotalAsset(totalAssets);
            totalAssets1.setTotalLiability(totalLiability);

            toTalAssetsService.updateByDate(totalAssets1);
            resultMap.put("updateTotalAssets", "SUCCESS");
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            resultMap.put("updateTotalAssets", "FAILED");
            return resultMap;
        }
    }
}
