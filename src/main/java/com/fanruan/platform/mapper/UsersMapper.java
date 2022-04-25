package com.fanruan.platform.mapper;

import com.fanruan.platform.bean.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

/**
 * <p></p>
 *
 * @author DELk
 * @@version 1.0.0
 * @since 2020/9/29
 */
@Repository
public interface UsersMapper {
    /**子管理员查询用户*/
    List<User> getSubUserList(HashMap<String, Object> hs, Integer pageIndex,Integer pageSize,
                              String username,String name,Integer status,Integer isSubAdmin,String operator,String companyCode);
    /**子管理员查询用户-记录数*/
    Integer getSubUserListCount(Integer pageIndex,Integer pageSize,
                                String username,String name,Integer status,Integer isSubAdmin,String operator,String companyCode);
    /**门户用户表*/
    Integer insertStep1(User user);
    /**门户-用户角色中间表*/
    Integer insertStep2(User user);
    /**门户-成员公司表*/
    Integer insertStep3(User user);
    /**门户-成员公司二级公司中间表*/
    Integer insertStep4(User user);
    /**门户-用户部门中间表*/
    Integer insertStep5(User user);

    List<User> getUserListLevel(HashMap<String, Object> hs, Integer pageIndex,Integer pageSize,
                                String username,String name,Integer status,Integer isSubAdmin,String companyCode,String roleName);

    Integer getUserListLevelCount(HashMap<String, Object> hs, Integer pageIndex,Integer pageSize,
                                  String username,String name,Integer status,Integer isSubAdmin,String companyCode,String roleName);

    /** 获取是否存在信保审核专员*/
    String getReviewer(@Param(value="userName") String userName);
}
