# -*- coding: utf-8 -*-
"""
-------------------------------------------------
   File Name   :    rizhiyiSearch
   Description :    日志易SPL查询API
   Author      :    jccia
   date        :    2021/5/20
-------------------------------------------------
"""
import requests
import json
import time
from utils.logger import Logger
from utils.config import Config
import utils.config as globalVar
from utils.threadLock import acquire
import threading

log = Logger(loggeNname=__name__)
# 创建锁用于保护变量
mutex = threading.RLock()


class Rizhiyi(Config):
    """
    简单的接口查询，只使用一个spl完成数据统计
    """

    def __init__(self):
        super().__init__()

    def search(self):
        url = "http://" + self.yottawebIP + "/api/v2/search/sheets/"

        params = {
            "query": self.totalTransCountSpl,
            "time_range": "now/d,now",
            "operator": self.yottawebUserName,
            "searchMode": "simple",
            "highlight": False,
            "statsevents": False,
            "fields": False,
            "timeline": False,
            "queryScope": [{"queryString": "*"}],
            "size": 10000
        }

        header = {
            "Content-Type": "application/json"
        }

        try:
            startTime = round(time.time() * 1000)
            response = requests.request("GET", url, headers=header, params=params,
                                        auth=(self.yottawebUserName, self.yottawebPassWord), timeout=5)
            endTime = round(time.time() * 1000)
            cost = str(endTime - startTime) + "ms"
            log.logger.debug("======> spl:{} ".format(self.totalTransCountSpl))
            log.logger.debug("========> 请求耗时:{}".format(cost))
            log.logger.debug("===========> 请求返回状态码:{}".format(response.status_code))
            log.logger.debug("==============> 请求返回结果:{}".format(response.text))
            if response.status_code != 200:
                log.logger.error("================>接口获取异常, 返回结果: " + response.text)
            else:
                text = json.loads(response.text)
                if text["result"] is False:
                    log.logger.error("================>接口返回码不为0, 返回结果: " + response.text)
                else:
                    for row in text["results"]["sheets"]["rows"]:
                        # 加锁并更新全行交易金额以及交易量
                        # mutex.acquire()
                        with acquire():
                            globalVar.set_value("tempTotalTransAmount", int(row.get("totalTransAmount", 0)))
                            globalVar.set_value("tempTotalTransCount", int(row.get("totalTransCount", 0)))
                            globalVar.set_value("updateTime", int(round(time.time())))
                        # mutex.release()
                        log.logger.info("totalTransAmount:{}, totalTransCount:{}".format(row.get("totalTransAmount", 0),
                                                                                         row.get("totalTransCount", 0)))
        except Exception as e:
            log.logger.exception(e)