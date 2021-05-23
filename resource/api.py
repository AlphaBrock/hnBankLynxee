# -*- coding: utf-8 -*-
"""
-------------------------------------------------
   File Name   :    app
   Description :    接口主函数
   Author      :    jccia
   date        :    2021/5/20
-------------------------------------------------
"""
import json
import copy
from flask import Flask, make_response

import utils.config as globalVar
from utils.logger import Logger

log = Logger(loggeNname=__name__)
app = Flask(__name__)


@app.route('/api/getAllBankMetric', methods=['GET'])
def getAllBankMetric():
    # 取出全局变量: 全行交易金额与笔数
    allBank = copy.copy(globalVar._global_dict)
    allBank.pop("updateTime")
    allBank.pop("lastAmount")
    allBank.pop("lastCount")
    allBank.pop("tempTotalTransAmount")
    allBank.pop("tempTotalTransCount")
    log.logger.debug(json.dumps(allBank))
    return make_response(allBank, 200)


def runDaemon():
    app.run(host="0.0.0.0", port=8081, debug=False, threaded=True)


if __name__ == '__main__':
    app.run(host="0.0.0.0", port=8081, debug=False, threaded=True)
