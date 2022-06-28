package com.fanruan.platform.mapper;

import com.fanruan.platform.bean.HrZxbClient;

import java.util.List;
import java.util.Map;

public interface HrZxbClientMapper {

    int updateByPrimaryKeySelective(HrZxbClient record);

    int insert(HrZxbClient record);
    int getClientCount(Map<String, Object> params);

    List<HrZxbClient> listByMap(Map<String, Object> params);

    int getCount(Map<String, Object> params);
}