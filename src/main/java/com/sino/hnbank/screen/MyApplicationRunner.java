package com.sino.hnbank.screen;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sino.hnbank.screen.quartz.ScreenData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.tomcat.jni.Thread;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;

/**
 * Created by llb on 19/1/2.
 */
@Component
public class MyApplicationRunner implements ApplicationRunner, Ordered {
    private static final Logger logger = LoggerFactory.getLogger(MyApplicationRunner.class);


    // by liulb 2020-12-9 为配合凌晨数据置为null  将4个变量置为静态
//    private  Long lastAmount = null;
//
//    private  Long currAmount = null;
//
//    private  Long lastCount = null;
//
//    private  Long currCount = null;

    // by liulb 2020-12-9 为配合凌晨数据置为null  将4个变量置为静态
    private static Long lastAmount = null;

    private static Long currAmount = null;

    private static Long lastCount = null;

    private static Long currCount = null;

    private static final int retryInterval = 1000 * 10;

    @Override
    public int getOrder() {
        return 1;//通过设置这里的数字来知道指定顺序
    }

    @Override
    public void run(ApplicationArguments var1) throws Exception {
        ScreenData screenData = new ScreenData();
        ScreenData.allBank.put("totalTransAmount", 0L);
        ScreenData.allBank.put("totalTransCount", 0L);
        while (true) {
            /**
             * 重新给下限赋值  本代码在此之前未注释掉，  2020-12-03 因为当前金额小于上次金额问题被注释调，修改为新的赋值逻辑
             */
//            lastAmount = currAmount;
//            lastCount = currCount;

            /**
             * 2020-12-03 增加当前值小于上一次值则不进行赋值。此代码开启，请将上段赋值代码注释掉
             */
            if(lastAmount != null && lastCount != null && currAmount != null && currCount != null) {
                if (currAmount >= lastAmount && currCount >= lastCount) {
                    lastAmount = currAmount;
                    lastCount = currCount;
                }else{
                    logger.info("!!!!!!@0发生了当前总金额或交易笔数小于上一次值的事件，我不赋新值!!!!!!");
                }
            }else{
                lastAmount = currAmount;
                lastCount = currCount;
            }
            // end

            /**
             * 从内存获得新数据（作为上限）
             */
            JsonObject totalTransAmount = screenData.buildMapJson("totalTransAmount");
            JsonObject totalTransCount = screenData.buildMapJson("totalTransCount");
            if (totalTransAmount == null || totalTransCount == null) {
                java.lang.Thread.sleep(retryInterval);
                continue;
            }
            try {
                currAmount = totalTransAmount.get("totalTransAmount").getAsBigDecimal().longValue();
                currCount = totalTransCount.get("totalTransCount").getAsBigDecimal().longValue();
            } catch (Exception e) {
                currAmount = lastAmount;
                currCount = lastCount;
                java.lang.Thread.sleep(retryInterval);
                continue;
            }
            /**
             * 如果下限不存在或者数值为0，则map缓存为上限数据
             */
            if (lastAmount == null || lastAmount == 0 || lastCount == null || lastCount == 0) {
                ScreenData.allBank.put("totalTransAmount", currAmount);
                ScreenData.allBank.put("totalTransCount", currCount);
                java.lang.Thread.sleep(retryInterval);
                continue;
            }

            /**
             * 2020-12-3 by liulb 如果当前值小于上一次值，直接等待休眠，重新进入循环。
             */
            if (currAmount < lastAmount || currCount < lastCount) {
                java.lang.Thread.sleep(retryInterval);
                logger.info("!!!!!!@4等待10秒，因为：发生了当前总金额或交易笔数小于上一次值的事件!!!!!!");
                continue;
            }


            /**
             * 如果上限和下限相等(总金额或总业务笔数)，则map缓存为下限数据,并且回滚上限
             */
            if (currAmount.equals(lastAmount) || currCount.equals(lastCount)) {
                ScreenData.allBank.put("totalTransAmount", lastAmount);
                ScreenData.allBank.put("totalTransCount", lastCount);
                currAmount = lastAmount;
                currCount = lastCount;
                java.lang.Thread.sleep(retryInterval);
                continue;
            }

            /**
             * 确定上限和下限中间随机数的个数
             */
            int size = 0;
            if ((currCount - lastCount) >= 60) {
                size = 60;
            } else if ((currCount - lastCount) > 0) {
                size = currCount.intValue() - lastCount.intValue();
            } else {
                java.lang.Thread.sleep(retryInterval);
                continue;
            }
            Long[] amount = new Long[size];
            Long[] count = new Long[size];


            /**
             * 获取随机数
             */
            for (int i = 0; i < size; i++) {
                amount[i] = random(lastAmount, currAmount);
                count[i] = random(lastCount, currCount);
            }

            /**
             * 对随机数排序desc
             */
            Arrays.sort(amount);
            Arrays.sort(count);


            long lastTempCount = 0L;
            long lastTempAmount = 0L;
            for (int i = 0; i < size; i++) {
                if (lastTempCount != 0L && lastTempAmount != 0L && lastTempCount == count[i]) {
                    //如果当前随机数等于上次随机数则map缓存数据为上次数据（业务数）
                    ScreenData.allBank.put("totalTransAmount", lastTempAmount);
                    ScreenData.allBank.put("totalTransCount", count[i]);
                } else if (lastTempCount != 0L && lastTempAmount != 0L && lastTempAmount == amount[i]) {
                    //如果当前随机数等于上次随机数则map缓存数据为上次数据（总金额）
                    ScreenData.allBank.put("totalTransAmount", amount[i]);
                    ScreenData.allBank.put("totalTransCount", lastTempCount);
                } else {
                    //每次获得到新的上下限后先将第一个随机数赋值给map缓存
                    ScreenData.allBank.put("totalTransAmount", amount[i]);
                    ScreenData.allBank.put("totalTransCount", count[i]);
                    //对临时数据进行赋值
                    lastTempCount = count[i];
                    lastTempAmount = amount[i];
                }
                //根据随机数个数动态调整休息时间
                java.lang.Thread.sleep(1000 * (60 / size));
                logger.info("totalTransAmount:" + ScreenData.allBank.get("totalTransAmount"));
                logger.info("totalTransCount" + ScreenData.allBank.get("totalTransCount"));
            }
        }
    }

    private Long random(long t1, long t2) {
        if (t1 > t2 + 1) {
            long min = t2;
            long max = t1;
            long rangeLong = min + (((long) (new Random().nextDouble() * (max - min))));
            System.out.println(rangeLong);
            return rangeLong;
        } else if (t2 > t1 + 1) {
            long min = t1;
            long max = t2;
            long rangeLong = min + (((long) (new Random().nextDouble() * (max - min))));
            System.out.println(rangeLong);
            return rangeLong;
        }
        return t2;
    }

    /**
     * 每日凌晨设置值为空   by liulb 2020-12-9
     */
    public static void setDefalut(){
        lastAmount = null;
        currAmount = null;
        lastCount = null;
        currCount = null;
    }


//
//    //=============我是分割线=========下面的程序测试使用=========正式发布请注释调=========2020-12-03 by liulb =============
//
//    /**
//     * 测试用main函数
//     *
//     * @param args
//     * @throws Exception
//     */
//    public static void main(String[] args) throws Exception {
//        System.out.println("======《《《我是测试程序开始执行了》》》======");
//
//        /**
//         * 测试程序参数初始化区
//         */
//        Long lastAmount = null;
//
//        Long currAmount = null;
//
//        Long lastCount = null;
//
//        Long currCount = null;
//
//        int retryInterval = 1000 * 10;
//
//        int count_time = 0;
//        ScreenData.allBank.put("totalTransAmount", 0L);
//        ScreenData.allBank.put("totalTransCount", 0L);
//        while (true) {
//            System.out.println("执行第"+count_time+"次");
//            count_time++;
//            Map tem = ScreenData.allBank;
//            System.out.println("@@@@@totalTransAmount 当前值："+tem.get("totalTransAmount"));
//            System.out.println("@@@@@totalTransAmount 当前值："+tem.get("totalTransAmount"));
//
//            /**
//             * 重新给下限赋值
//             */
//            //lastAmount = currAmount;
//            //lastCount = currCount;
//            if(lastAmount != null && lastCount != null && currAmount != null && currCount != null) {
//                 if (currAmount >= lastAmount && currCount >= lastCount) {
//                     lastAmount = currAmount;
//                     lastCount = currCount;
//                 }else{
//                     System.out.println("!!!!!!@0发生了当前总金额或交易笔数小于上一次值的事件，我不赋新值!!!!!!");
//                 }
//            }else{
//               lastAmount = currAmount;
//               lastCount = currCount;
//            }
//
//            /**
//             * 从内存获得新数据（作为上限）
//             */
////            JsonObject totalTransAmount = screenData.buildMapJson("totalTransAmount");
////            JsonObject totalTransCount = screenData.buildMapJson("totalTransCount");
////
////
////
////            if (totalTransAmount == null || totalTransCount == null) {
////                java.lang.Thread.sleep(retryInterval);
////                System.out.println("@1");
////                continue;
////            }
//            try {
////                currAmount = totalTransAmount.get("totalTransAmount").getAsBigDecimal().longValue();
////                currCount = totalTransCount.get("totalTransCount").getAsBigDecimal().longValue();
//
//                /**
//                 * 测试区域：手动提供值
//                 */
//                if (count_time ==1) {
//                    currAmount =  0L;
//                    currCount =  0L;
//                }
//
//
//                if (count_time ==2) {
//                    currAmount =  10L;
//                    currCount =  10L;
//                }
//
//                if (count_time ==3) {
//                    currAmount =  20L;
//                    currCount =  20L;
//                }
//
//                if (count_time ==4) {
//                    currAmount =  10L;
//                    currCount =  10L;
//                }
//
//                if (count_time ==5) {
//                    currAmount =  30L;
//                    currCount =  30L;
//                }
//
//                if (count_time ==6) {
//                    currAmount =  40L;
//                    currCount =  40L;
//                }
//
//                if (count_time ==7) {
//                    currAmount =  10L;
//                    currCount =  10L;
//                }
//
//                if (count_time ==8) {
//                    currAmount =  50L;
//                    currCount =  50L;
//                }
//
//
//                if (count_time ==9) {
//                    currAmount =  60L;
//                    currCount =  60L;
//                }
//
//                if (count_time ==10) {
//                    currAmount =  70L;
//                    currCount =  70L;
//                }
//
//            } catch (Exception e) {
//                currAmount = lastAmount;
//                currCount = lastCount;
//                java.lang.Thread.sleep(retryInterval);
//                System.out.println("@2");
//                continue;
//            }
//            /**
//             * 如果下限不存在或者数值为0，则map缓存为上限数据
//             */
//            if (lastAmount == null || lastAmount == 0 || lastCount == null || lastCount == 0) {
//                ScreenData.allBank.put("totalTransAmount", currAmount);
//                ScreenData.allBank.put("totalTransCount", currCount);
//                java.lang.Thread.sleep(retryInterval);
//                System.out.println("@3:第一次进入正常执行");
//                continue;
//            }
//
//            /**
//             * 2020-12-3 by liulb 如果当前值小于上一次值，直接等待休眠，重新进入循环。
//             */
//            if (currAmount < lastAmount || currCount < lastCount) {
//                java.lang.Thread.sleep(retryInterval);
//                System.out.println("!!!!!!@4等待10秒，因为：发生了当前总金额或交易笔数小于上一次值的事件!!!!!!");
//                continue;
//            }
//
//
//            /**
//             * 如果上限和下限相等(总金额或总业务笔数)，则map缓存为下限数据,并且回滚上限
//             */
//            if (currAmount.equals(lastAmount) || currCount.equals(lastCount)) {
//                ScreenData.allBank.put("totalTransAmount", lastAmount);
//                ScreenData.allBank.put("totalTransCount", lastCount);
//                currAmount = lastAmount;
//                currCount = lastCount;
//                java.lang.Thread.sleep(retryInterval);
//                System.out.println("@5");
//                continue;
//            }
//
//            /**
//             * 确定上限和下限中间随机数的个数
//             */
//            int size = 0;
//            if ((currCount - lastCount) >= 60) {
//                size = 60;
//            } else if ((currCount - lastCount) > 0) {
//                size = currCount.intValue() - lastCount.intValue();
//            } else {
//                java.lang.Thread.sleep(retryInterval);
//                System.out.println("@6");
//                continue;
//            }
//            Long[] amount = new Long[size];
//            Long[] count = new Long[size];
//
//
//            /**
//             * 获取随机数
//             */
//            for (int i = 0; i < size; i++) {
//                amount[i] = random2(lastAmount, currAmount);
//                count[i] = random2(lastCount, currCount);
//            }
//
//            /**
//             * 对随机数排序desc
//             */
//            Arrays.sort(amount);
//            Arrays.sort(count);
//
//
//            long lastTempCount = 0L;
//            long lastTempAmount = 0L;
//            for (int i = 0; i < size; i++) {
//                if (lastTempCount != 0L && lastTempAmount != 0L && lastTempCount == count[i]) {
//                    //如果当前随机数等于上次随机数则map缓存数据为上次数据（业务数）
//                    ScreenData.allBank.put("totalTransAmount", lastTempAmount);
//                    ScreenData.allBank.put("totalTransCount", count[i]);
//                } else if (lastTempCount != 0L && lastTempAmount != 0L && lastTempAmount == amount[i]) {
//                    //如果当前随机数等于上次随机数则map缓存数据为上次数据（总金额）
//                    ScreenData.allBank.put("totalTransAmount", amount[i]);
//                    ScreenData.allBank.put("totalTransCount", lastTempCount);
//                } else {
//                    //每次获得到新的上下限后先将第一个随机数赋值给map缓存
//                    ScreenData.allBank.put("totalTransAmount", amount[i]);
//                    ScreenData.allBank.put("totalTransCount", count[i]);
//                    //对临时数据进行赋值
//                    lastTempCount = count[i];
//                    lastTempAmount = amount[i];
//                }
//                //根据随机数个数动态调整休息时间
//                java.lang.Thread.sleep(1000 * (60 / size));
//                System.out.println("totalTransAmount:" + ScreenData.allBank.get("totalTransAmount"));
//                System.out.println("totalTransCount:" + ScreenData.allBank.get("totalTransCount"));
//            }
//        }
//    }
//
//    /**
//     * 供测试使用的静态类
//     *
//     * @param t1
//     * @param t2
//     * @return
//     */
//    public static Long random2(long t1, long t2) {
//        if (t1 > t2 + 1) {
//            long min = t2;
//            long max = t1;
//            long rangeLong = min + (((long) (new Random().nextDouble() * (max - min))));
//            //System.out.println(rangeLong);
//            return rangeLong;
//        } else if (t2 > t1 + 1) {
//            long min = t1;
//            long max = t2;
//            long rangeLong = min + (((long) (new Random().nextDouble() * (max - min))));
//            //System.out.println(rangeLong);
//            return rangeLong;
//        }
//        return t2;
//    }
//
//    //==========测试代码结束=============
//
}
