#!/usr/bin/env bash

ps -ef|grep "HaiNanBankLynxee"|grep -v grep|awk -F " " '{print $2}'|xargs kill -9