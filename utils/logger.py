# -*- coding: utf-8 -*-
"""
-------------------------------------------------
   File Name   :    logger
   Description :    重写日志打印
   Author      :    jccia
   date        :    2021/5/20
-------------------------------------------------
"""
import logging
from logging import handlers
from utils.config import Config


class Logger(Config):

    level_relations = {
        'debug': logging.DEBUG,
        'info': logging.INFO,
        'warning': logging.WARNING,
        'error': logging.ERROR,
        'crit': logging.CRITICAL
    }

    def __init__(self, logname="log/HaiNanBankLynxee.log", loggeNname=None, when='D', backCount=3):
        """
            指定保存日志的文件路径，日志级别，以及调用文件
            将日志存入到指定的文件中
        """
        super().__init__()
        loglevel = self.logLevel
        # 创建一个logger
        self.logger = logging.getLogger(loggeNname)
        self.logger.setLevel(self.level_relations.get(loglevel))
        # 创建一个handler，用于写入日志文件
        fh = handlers.TimedRotatingFileHandler(filename=logname, when=when, backupCount=backCount,
                                               encoding='utf-8')
        fh.setLevel(self.level_relations.get(loglevel))
        if not self.logger.handlers:
            # 再创建一个handler，用于输出到控制台
            ch = logging.StreamHandler()
            ch.setLevel(self.level_relations.get(loglevel))
            # 定义handler的输出格式
            formatter = logging.Formatter(
                '[%(asctime)s] [%(levelname)s] [%(filename)s:%(module)s.%(funcName)s:%(lineno)d] [%(process)d] [%(threadName)s] %(message)s')
            fh.setFormatter(formatter)
            ch.setFormatter(formatter)
            # 给logger添加handler
            self.logger.addHandler(fh)
            self.logger.addHandler(ch)
