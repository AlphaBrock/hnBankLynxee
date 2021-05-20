package com.sino.hnbank.screen.quartz;

import com.google.gson.JsonObject;
import com.sino.hnbank.screen.pojo.TransDayData;
import com.sino.hnbank.screen.service.TransDayDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.sino.hnbank.screen.quartz.ScreenData.mapJson;

/**
 * @ClassName QuartzService
 * @Description 定时拉取大屏需要的数据
 * @Author zhangsch
 * @Date 2018/12/26 18:43
 * @UpdateTime 2018/12/26 18:43
 **/
@Component
@PropertySource(value= "file:config/quartz.properties",ignoreResourceNotFound=false,encoding="UTF-8")
public class ScreenDataQuartz {
    private static final Logger logger = LoggerFactory.getLogger(ScreenDataQuartz.class);

    @Autowired
    private TransDayDataService transDayDataService;

    /**
     * 用来获取splunk在大屏的缓存数据的对象
     */
    private ScreenData screenData = new ScreenData();

    /**
     * 定时往数据库更新数据
     */
    @Scheduled(cron = "${Schedule}")
    public void updateMySQLDB() {
        try {
            List<TransDayData> transDayDataList = screenData.buildTransDayDataList();
            logger.info("<--- start to update MySQLDB --->");
            //根据银行代码和日期查询数据库，不为空则更新，为空则插入
            for (TransDayData transDayData : transDayDataList) {
                String dataDate = transDayData.getDataDate();
                String branchCode = transDayData.getBranchCode();
                TransDayData transDayDataBySelect = transDayDataService.selectByDateAndCode(branchCode, dataDate);
                if (transDayDataBySelect != null) {
                    //更新最后数据获取时间
                    transDayData.setLastUpdateTime(new Timestamp(System.currentTimeMillis()));
                    //更新数据库表项
                    transDayDataService.updateByDateAndCode(transDayData);
                } else {
                    //更新最后数据获取时间
                    transDayData.setLastUpdateTime(new Timestamp(System.currentTimeMillis()));
                    //插入表项
                    transDayDataService.insertTransDayData(transDayData);
                }
            }
            //更新或插入完成后清空集合
            transDayDataList.clear();
        } catch (ClassNotFoundException e) {
            logger.info(" class not found{}",e);
        } catch (NoSuchMethodException e) {
            logger.info(" no such method {}",e);
        } catch (IllegalAccessException e) {
            logger.info(" illgeal access exception{}",e);
        } catch (InvocationTargetException e) {
            logger.info(" invocation target exception{}",e);
        } catch (InstantiationException e) {
            logger.info(" instantiation exception{}",e);
        }
    }

    /**
     * 定时执行SPL获取数据
     */
    @Scheduled(cron = "${splCron}")
    public void excuteRun(){
        logger.info("******* start excute spl *******");
        ScreenData.run();
    }

    /**
     * 每天零点初始化map
     */
    @Scheduled(cron="${defaultDataCron}")
    public void clearMap(){
        ScreenData.setDefaultData();
    }
}
