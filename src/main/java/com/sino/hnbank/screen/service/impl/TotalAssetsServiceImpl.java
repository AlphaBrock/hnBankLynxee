package com.sino.hnbank.screen.service.impl;

import com.sino.hnbank.screen.mapper.TotalAssetsMapper;
import com.sino.hnbank.screen.pojo.TotalAssets;
import com.sino.hnbank.screen.service.ToTalAssetsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName TotalAssetsServiceImpl
 * @Description TODO
 * @Author Administrator
 * @Date 2018/12/28 19:30
 * @UpdateTime 2018/12/28 19:30
 **/
@Service
public class TotalAssetsServiceImpl implements ToTalAssetsService {

    @Autowired
    private TotalAssetsMapper totalAssetsMapper;

    @Override
    public List<Map<String, Object>> totalAssets(long dayN) {
        List<Map<String, Object>> list = totalAssetsMapper.totalAssets(dayN);
        for (Map<String, Object> stringObjectMap : list) {
            String data_date = (String) stringObjectMap.get("date");
            StringBuffer sb = new StringBuffer(data_date);
            sb.insert(6, ".");
            sb.insert(4, ".");
            stringObjectMap.put("date", sb.toString());
        }
        return list;
    }

    @Override
    public void insert(TotalAssets record) {
        totalAssetsMapper.insert(record);
    }

    @Override
    public void updateByDate(TotalAssets record) {
        if (totalAssetsMapper.selectByDate(record.getDate()) != null){
            totalAssetsMapper.updateByDate(record);
        }else {
            totalAssetsMapper.insert(record);
        }
    }
}
