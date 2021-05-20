package com.sino.hnbank.screen;

import java.util.Arrays;
import java.util.Random;

/**
 * @Description:
 * @Author: zhangsch
 * @Version: 1.0
 * @Create Date Time: 2019-05-10 09:30
 * @Update Date Time:
 * @see
 */
public class RandomNumTest {
    public static void main(String[] args) {

        int size = 60;
        Long[] amount = new Long[60];
        Long[] count = new Long[60];
        long lastAmount = 111111111;
        long lastCount = 22222;

        long currAmount = 222222222;
        long currCount = 33333;

        /**
         * 获取随机数
         */
        amount[0] = lastAmount;
        count[0] = lastCount;
        for (int i = 1; i < size; i++) {
            amount[i] = random(lastAmount, currAmount);
            count[i] = random(lastCount, currCount);
        }
        Arrays.sort(amount);
        Arrays.sort(count);
        System.out.println("continue");
    }

    private static Long random(long t1, long t2) {
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
}
