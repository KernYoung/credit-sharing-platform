package com.fanruan.platform.dao;

import com.fanruan.platform.bean.Area;
import com.fanruan.platform.bean.BlackInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BlackInfoDao  extends JpaRepository<BlackInfo,Integer> {
    Optional<BlackInfo> findByPid(String code);

    /*Optional<BlackInfo> findByEntNameAndCodeAndUpdateBy(String entName,String code,String updateBy);*/
    default Optional<BlackInfo> findByEntNameAndCodeAndPublishBy(String entName, String code, String updateBy) {
        return null;
    }

    default Optional<BlackInfo> findByEntNameAndCodeAndSecondaryDepartment(String entName, String code, String secondaryDepartment) {
        return null;
    }

    //jina
    List<BlackInfo> findByEntName(String entName);
}
