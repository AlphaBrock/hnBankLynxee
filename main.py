# -*- coding: utf-8 -*-
"""
-------------------------------------------------
   File Name   :    main
   Description :
   Author      :    jccia
   date        :    2021/5/23
-------------------------------------------------
"""
import utils.config as globalVar
import threading
from resource.rizhiyiSearch import Rizhiyi
from resource.updateVar import updateVar
from resource.api import runDaemon as flaskApi
from apscheduler.schedulers.background import BackgroundScheduler
from apscheduler.executors.pool import ThreadPoolExecutor

rzy = Rizhiyi()
updateVars = updateVar()
executors = {
        'default': ThreadPoolExecutor(max_workers=2)
}
scheduler = BackgroundScheduler(executors=executors)


def scheduleRunner(scheduleType):
    """
    定时任务器
    :return:
    """
    if scheduleType == "spl查询":
        scheduler.add_job(func=rzy.search, trigger="interval", seconds=20)
    elif scheduleType == "凌晨更新变量":
        scheduler.add_job(func=updateVars.setDefaultValue, trigger="cron", hour="0", minute='1')


def main():
    # 启动spl定时查询
    scheduleTypes = ["spl查询", "凌晨更新变量"]
    for scheduleType in scheduleTypes:
        scheduleRunner(scheduleType)
    scheduler.start()

    # 启动变量更新
    thread2 = threading.Thread(target=updateVars.runner)
    thread2.setDaemon(True)
    thread2.start()

    # 启动api
    thread3 = threading.Thread(target=flaskApi())
    thread3.setDaemon(True)
    thread3.start()


if __name__ == '__main__':
    globalVar._init()
    globalVar.set_value('totalTransAmount', 0)
    globalVar.set_value('totalTransCount', 0)
    globalVar.set_value('lastAmount', 0)
    globalVar.set_value('lastCount', 0)
    globalVar.set_value('updateTime', 0)
    globalVar.set_value('tempTotalTransAmount', 0)
    globalVar.set_value('tempTotalTransCount', 0)
    main()
