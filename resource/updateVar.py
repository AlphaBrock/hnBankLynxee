# -*- coding: utf-8 -*-
"""
-------------------------------------------------
   File Name   :    updateVar
   Description :    处理接口返回数据, 并更新全局变量, 用以大屏快速取回结果数据
                    而不是直接去提交spl查询, 解决等待时间过长问题
   Author      :    jccia
   date        :    2021/5/20
-------------------------------------------------
"""
import copy
import json
import random
import threading
import time

import utils.config as globalVar
from utils.logger import Logger
from utils.threadLock import acquire

log = Logger(loggeNname=__name__)
# 创建锁用于保护变量
mutex = threading.Lock()


class updateVar(object):

    def __init__(self):
        self.retryInterval = 5

    def randomNumber(self, t1, t2):
        """
        创建低于spl查询出来结果的随机数
        :param t1:
        :param t2:
        :return:
        """
        if t1 > t2 + 1:
            min = t2
            max = t1
            rangeLong = min + round(random.random() * (max - min))
            return rangeLong
        elif t2 > t1 + 1:
            min = t1
            max = t2
            rangeLone = min + round(random.random() * (max - min))
            return rangeLone
        else:
            return t2

    def runner(self):
        """
        死循环不断的更新全局变量(全行交易量以及交易金额)
        :return:
        """
        while True:
            log.logger.debug("=========>循环开始<=========")
            # globalVars = copy.copy(globalVar._global_dict)
            # mutex.acquire()
            with acquire():
                lastAmount = int(globalVar.get_value("lastAmount"))
                lastCount = int(globalVar.get_value("lastCount"))
                lastUpdateTime = int(globalVar.get_value("updateTime"))
                currAmount = int(globalVar.get_value("totalTransAmount"))
                currCount = int(globalVar.get_value("totalTransCount"))
                tempCurrAmount = int(globalVar.get_value("tempTotalTransAmount"))
                tempCurrCount = int(globalVar.get_value("tempTotalTransCount"))
            diffNum = tempCurrCount - currCount
            # mutex.release()
            log.logger.debug("globalMap:{}".format(json.dumps(globalVar._global_dict)))


            # 当前值小于上一次值则不进行赋值
            if tempCurrAmount <= lastAmount or tempCurrCount <= lastCount:
                log.logger.warning("!!!!!! 发生了当前总金额或交易笔数小于或等于上一次值的事件，我不赋新值 !!!!!!")
                time.sleep(self.retryInterval)
                continue

            if tempCurrAmount == 0 or tempCurrCount == 0:
                log.logger.warning("!!!!! totalTransAmount 或 totalTransCount 为空, 进入睡眠状态, 等待SPL接口返回数据 !!!!!")
                time.sleep(self.retryInterval)
                continue

            # 确定上限和下限中间随机数的个数
            if diffNum >= 30:
                size = 30
            elif diffNum > 0:
                size = diffNum
            else:
                time.sleep(self.retryInterval)
                continue

            log.logger.debug("!!!!! 随机数个数:{} !!!!!".format(size))

            # 根据随机数创建一批比spl查询结果小的值
            amount = []
            count = []
            for i in range(size):
                amount.append(self.randomNumber(currAmount, tempCurrAmount))
                count.append(self.randomNumber(currCount, tempCurrCount))
            # 随机数排下序
            amount.sort()
            count.sort()
            log.logger.debug("amount:{}, count:{}".format(amount, count))

            # 更新全局变量
            for i in range(size):
                # 如果当前随机数等于上次随机数则map缓存数据为上次数据（业务数/总金额）
                # 检查下当前情况变量情况，如果更新了直接退出循环
                if int(globalVar.get_value("updateTime")) != lastUpdateTime:
                    log.logger.warning("!!!!! 发生交易型数据更新, 退出循环 !!!!!")
                    # if mutex.acquire(False):
                    with acquire(mutex):
                        globalVar.set_value("lastAmount", tempCurrAmount)
                        globalVar.set_value("lastCount", tempCurrCount)
                        globalVar.set_value("totalTransAmount", tempCurrAmount)
                        globalVar.set_value("totalTransCount", tempCurrCount)
                    # mutex.release()
                    break
                else:
                    with acquire(mutex):
                        globalVar.set_value("totalTransAmount", amount[i])
                        globalVar.set_value("totalTransCount", count[i])
                    # mutex.release()

                # # 根据随机数个数动态调整休息时间
                time.sleep(1)

    def setDefaultValue(self):
        """
        每天凌晨重置下数据
        :return:
        """
        log.logger.info("===========> 凌晨重置变量 <===========")
        with acquire(mutex):
            globalVar.set_value("lastAmount", 0)
            globalVar.set_value("lastCount", 0)
            globalVar.set_value('totalTransAmount', 0)
            globalVar.set_value('totalTransCount', 0)