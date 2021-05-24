# -*- coding: utf-8 -*-
"""
-------------------------------------------------
   File Name   :    threadLock
   Description :    用以解决线程死锁
   Author      :    jccia
   date        :    2021/5/23
-------------------------------------------------
"""
import threading
from contextlib import contextmanager

# 用来存储local的数据
_local = threading.local()


@contextmanager
def acquire(*locks): 
    # 对锁按照id进行排序
    locks = sorted(locks, key=lambda x: id(x))
    # 如果已经持有锁当中的序号有比当前更大的，说明策略失败
    acquired = getattr(_local,'acquired', [])
    if acquired and max(id(lock) for lock in acquired) >= id(locks[0]):
        raise RuntimeError('Lock Order Violation')
    # 获取所有锁
    acquired.extend(locks)
    _local.acquired = acquired
    try:
        for lock in locks:
            lock.acquire()
        yield
    finally:
        # 倒叙释放
        for lock in reversed(locks):
            lock.release()
        del acquired[-len(locks):]
