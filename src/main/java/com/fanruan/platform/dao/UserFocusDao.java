package com.fanruan.platform.dao;

import com.fanruan.platform.bean.UserFocus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserFocusDao extends JpaRepository<UserFocus,Integer> {
    Optional<UserFocus> findById(Integer id);
//    Page<UserFocus> findAllByUserId(Pageable pageable,Integer userId);
    List<UserFocus> findAllByUserId(Integer userId);

    UserFocus findAllByUserIdAndCompanyId(Integer userId,Integer companyId);

}
