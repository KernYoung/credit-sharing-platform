package com.fanruan.platform.dao;

import com.fanruan.platform.bean.Company;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyDao extends JpaRepository<Company,Integer> {
    Optional<Company> findByCompanyId(Integer companyId);
    List<Company> findAllByCreditCodeIn(List<String> creditCodeList);
    List<Company> findAllByCompanyNameIn(List<String> CompanyName);
    List<Company> findAllByCompanyIdIn(List<Integer> companyIdList);
    Optional<Company> findByCreditCode(String creditCode);
    Optional<Company> findByCompanyName(String companyName);

    Optional<Company> findTop1ByCompanyName(String companyName);

    List<Company> findAllByIdIn(List<Long> idList);
//    List<Company> findAllByIdInOrderByIdUpdateTimeDesc(List<Long> idList);
    List<Company> findAllByCompanyNameInOrderByUpdateTime(List<String> CompanyName);
}
