# -*- coding: utf-8 -*-
"""
-------------------------------------------------
   File Name   :    readSetting
   Description :    读取配置文件
   Author      :    jccia
   date        :    2021/5/20
-------------------------------------------------
"""
import configparser


class Config(object):

    def __init__(self):
        conf = configparser.ConfigParser()
        conf.read("config.ini")

        self.yottawebIP = conf.get("rizhiyi", "yottawebIP")
        self.yottawebUserName = conf.get("rizhiyi", "yottawebUserName")
        self.yottawebPassWord = conf.get("rizhiyi", "yottawebPassWord")

        self.totalTransCountSpl = conf.get("spl", "totalTrans")

        self.logLevel = conf.get("logger", "logLevel")


def _init():
    global _global_dict
    _global_dict = {}


def set_value(name, value):
    """
    定义一个全局变量
    :param name:
    :param value:
    :return:
    """
    try:
        _global_dict[name] = value
    except KeyError:
        pass


def get_value(name, defValue=None):
    """
    获取一个全局变量值，不能存在则返回默认值
    :param name:
    :param defValue:
    :return:
    """
    try:
        return _global_dict[name]
    except KeyError:
        return defValue