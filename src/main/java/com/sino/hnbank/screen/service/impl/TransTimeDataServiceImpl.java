package com.sino.hnbank.screen.service.impl;

import com.sino.hnbank.screen.mapper.TransTimeDataMapper;
import com.sino.hnbank.screen.pojo.TransTimeData;
import com.sino.hnbank.screen.service.TransTimeDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName TransTimeDataServiceImpl
 * @Description TODO
 * @Author Administrator
 * @Date 2018/12/27 16:28
 * @UpdateTime 2018/12/27 16:28
 **/
@Service
public class TransTimeDataServiceImpl implements TransTimeDataService {

    @Autowired
    private TransTimeDataMapper transTimeDataMapper;

    @Override
    public void insertTransTimeData(TransTimeData transTimeData) {
        transTimeDataMapper.insert(transTimeData);
    }
}
