package com.fanruan.platform.dao;

import com.fanruan.platform.bean.Area;
import com.fanruan.platform.bean.TianYanChaJson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TianYanChaJsonDao extends JpaRepository<TianYanChaJson,Integer> {
    Optional<TianYanChaJson> findById(Integer id);
    List<TianYanChaJson> findByNameAndJsonFlagOrderByUpdateTimeDesc(String name,String jsonFlag);
    List<TianYanChaJson> findByNameAndIdAndJsonFlagOrderByUpdateTimeDesc(String name,String id,String jsonFlag);
}
