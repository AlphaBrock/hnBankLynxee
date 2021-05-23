#!/usr/bin/env bash

curDir=$(pwd)

# 检查下运行环境还在不在
if [ ! -d ${curDir}/python ];
then
  tar czf ${curDir}/python.tar.gz ${curDir}
fi

# 启动服务
nohup ${curDir}/python/bin/python3 ${curDir}/main.py &