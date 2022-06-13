package com.fanruan.platform.mapper;

import com.fanruan.platform.bean.HrZxbClient;

import java.util.List;
import java.util.Map;

public interface HrZxbClientMapper {

    int updateByPrimaryKeySelective(HrZxbClient record);


    List<HrZxbClient> listByMap(Map<String, Object> params);
}