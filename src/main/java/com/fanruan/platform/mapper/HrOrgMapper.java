package com.fanruan.platform.mapper;

import com.fanruan.platform.bean.HrOrg;

import java.util.List;
import java.util.Map;

public interface HrOrgMapper {
    int deleteByPrimaryKey(String pk_org);

    int insert(HrOrg record);

    int insertSelective(HrOrg record);

    HrOrg selectByPrimaryKey(String CODE);

    int updateByPrimaryKeySelective(HrOrg record);

    int updateByPrimaryKey(HrOrg record);

    List<HrOrg> listByMap(Map<String, Object> params);
    //查询启用
    List<HrOrg> listByMap1(Map<String, Object> params);

}