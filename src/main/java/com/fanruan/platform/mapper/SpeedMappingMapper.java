package com.fanruan.platform.mapper;

import com.fanruan.platform.bean.SpeedMapping;

import java.util.List;
import java.util.Map;

public interface SpeedMappingMapper {
    int deleteByPrimaryKey(String ID);

    int insert(SpeedMapping record);

    int insertSelective(SpeedMapping record);

    SpeedMapping selectByPrimaryKey(String ID);

    int updateByPrimaryKeySelective(SpeedMapping record);

    int updateByPrimaryKey(SpeedMapping record);

    List<SpeedMapping> listByMap(Map<String, Object> params);
}