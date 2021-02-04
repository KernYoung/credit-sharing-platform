package com.fanruan.platform.mapper;

import com.fanruan.platform.bean.*;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CommonMapper {
    @Select("<script> " +
            "SELECT DISTINCT\n" +
            "S.NAME,--股东名称\n" +
            "S.RATIO,--持股比例（%)\n" +
            "S.SHARESHOLD,--股份数额\n" +
            "S.VALUEHOLD,--持股金额\n" +
            "S.VALUEUSD--持股金额折美元\n" +
            "FROM\n" +
            "   ODS_ZXB_SHARESINFO S\n" +
            "INNER JOIN (\n" +
            "   SELECT\n" +
            "       NOTICESERIALNO,\n" +
            "       MAX (UPDATETIME) MAXTIME\n" +
            "   FROM\n" +
            "       ODS_ZXB_SHARESINFO\n" +
            "   \n" +
            "   GROUP BY\n" +
            "   NOTICESERIALNO\n" +
            ") D ON D .MAXTIME = S.UPDATETIME AND D.NOTICESERIALNO=S.NOTICESERIALNO\n" +
            "INNER JOIN ODS_ZXB_RATINGINFO F ON F.NOTICESERIALNO=S.NOTICESERIALNO " +
            "WHERE 1 = 1" +
            "<if test=\"name != null or engName != null\">"+
            " and (F.BUYERCHNNAME = #{name, jdbcType=VARCHAR}  OR  F.BUYERENGNAME = #{engName, jdbcType=VARCHAR} OR  F.SINOSUREBUYERNO =#{reportbuyerno,jdbcType=VARCHAR}) "+
            "</if> "
        +"</script> ")
    @Results(id="zhongXinBaoShare",  value={
            @Result(property="name", column="NAME"),
            @Result(property="ratio", column="RATIO"),
            @Result(property="sharesHold", column="SHARESHOLD"),
            @Result(property="valueHold", column="VALUEHOLD"),
            @Result(property="valueUSD", column="VALUEUSD")
    })
    public List<ZhongXinBaoShare> getZhongXinBaoShare(@Param("name") String name,@Param("engName") String engName,@Param("reportbuyerno") String reportbuyerno);

    @Select("select PERMISSION_ROLE,PERMISSION_ROLE_NAME  \n" +
            "from CREDIT_PERMISSOION_POINT where PERMISSION_ROLE is not null GROUP BY PERMISSION_ROLE,PERMISSION_ROLE_NAME ")
    @Results(id="permissionPoint",  value={
            @Result(property="permissionRole", column="PERMISSION_ROLE"),
            @Result(property="permissionRoleName", column="PERMISSION_ROLE_NAME"),
    })
    public List<PermissionPoint> getPermissionPoint();

    @Select(" SELECT CODE,NAME --自身\n" +
            "FROM BIUSER.ODS_HR_ORG where CODE IS NOT NULL and NAME IS NOT NULL GROUP BY CODE,NAME")
    @Results(id="userCompany1",  value={
            @Result(property="code", column="CODE"),
            @Result(property="name", column="NAME")
    })
    public List<UserCompany> getAllUserCompany();


    @Select("SELECT CODE,NAME --自身\n" +
            "FROM BIUSER.ODS_HR_ORG A\n" +
            "WHERE  CODE=(SELECT COMPANY_CODE FROM CREDIT_USER WHERE USERNAME=#{username, jdbcType=VARCHAR})  \n" +
            "UNION ALL\n" +
            "SELECT CODE,NAME --下级公司\n" +
            "FROM BIUSER.ODS_HR_ORG A\n" +
            "WHERE SCODE=(SELECT COMPANY_CODE FROM CREDIT_USER WHERE USERNAME=#{username, jdbcType=VARCHAR})  \n" +
            "UNION ALL  \n" +
            "SELECT B.CODE,B.NAME --下下级公司\n" +
            "FROM BIUSER.ODS_HR_ORG A \n" +
            "INNER JOIN BIUSER.ODS_HR_ORG B ON A.CODE=B.SCODE\n" +
            "WHERE A.SCODE=(SELECT COMPANY_CODE FROM CREDIT_USER WHERE USERNAME=#{username, jdbcType=VARCHAR})  \n" +
            "UNION ALL  \n" +
            "SELECT C.CODE,C.NAME --下下下级公司\n" +
            "FROM BIUSER.ODS_HR_ORG A \n" +
            "INNER JOIN BIUSER.ODS_HR_ORG B ON A.CODE=B.SCODE\n" +
            "INNER JOIN BIUSER.ODS_HR_ORG C ON B.CODE=C.SCODE\n" +
            "WHERE A.SCODE=(SELECT COMPANY_CODE FROM CREDIT_USER WHERE USERNAME=#{username, jdbcType=VARCHAR})  \n" +
            "UNION ALL  \n" +
            "SELECT D.CODE,D.NAME --下下下下级公司\n" +
            "FROM BIUSER.ODS_HR_ORG A \n" +
            "INNER JOIN BIUSER.ODS_HR_ORG B ON A.CODE=B.SCODE\n" +
            "INNER JOIN BIUSER.ODS_HR_ORG C ON B.CODE=C.SCODE\n" +
            "INNER JOIN BIUSER.ODS_HR_ORG D ON C.CODE=D.SCODE\n" +
            "WHERE A.SCODE=(SELECT COMPANY_CODE FROM CREDIT_USER WHERE USERNAME=#{username, jdbcType=VARCHAR}) ")
    @Results(id="userCompany",  value={
            @Result(property="code", column="CODE"),
            @Result(property="name", column="NAME")
    })
    public List<UserCompany> getUserCompany(@Param("username") String username);


    @Select("SELECT \n" +
            "USER_ID,USERNAME,NAME FROM CREDIT_USER\n" +
            "WHERE COMPANY_CODE IN (\n" +
            "SELECT CODE --自身\n" +
            "FROM BIUSER.ODS_HR_ORG A\n" +
            "WHERE  CODE=(SELECT COMPANY_CODE FROM CREDIT_USER WHERE USERNAME=#{username, jdbcType=VARCHAR})  \n" +
            "UNION ALL\n" +
            "SELECT CODE --下级公司\n" +
            "FROM BIUSER.ODS_HR_ORG A\n" +
            "WHERE SCODE=(SELECT COMPANY_CODE FROM CREDIT_USER WHERE USERNAME=#{username, jdbcType=VARCHAR})  \n" +
            "UNION ALL  \n" +
            "SELECT B.CODE --下下级公司\n" +
            "FROM BIUSER.ODS_HR_ORG A \n" +
            "INNER JOIN BIUSER.ODS_HR_ORG B ON A.CODE=B.SCODE\n" +
            "WHERE A.SCODE=(SELECT COMPANY_CODE FROM CREDIT_USER WHERE USERNAME=#{username, jdbcType=VARCHAR})  \n" +
            "UNION ALL  \n" +
            "SELECT C.CODE --下下下级公司\n" +
            "FROM BIUSER.ODS_HR_ORG A \n" +
            "INNER JOIN BIUSER.ODS_HR_ORG B ON A.CODE=B.SCODE\n" +
            "INNER JOIN BIUSER.ODS_HR_ORG C ON B.CODE=C.SCODE\n" +
            "WHERE A.SCODE=(SELECT COMPANY_CODE FROM CREDIT_USER WHERE USERNAME=#{username, jdbcType=VARCHAR})  \n" +
            "UNION ALL  \n" +
            "SELECT D.CODE --下下下下级公司\n" +
            "FROM BIUSER.ODS_HR_ORG A \n" +
            "INNER JOIN BIUSER.ODS_HR_ORG B ON A.CODE=B.SCODE\n" +
            "INNER JOIN BIUSER.ODS_HR_ORG C ON B.CODE=C.SCODE\n" +
            "INNER JOIN BIUSER.ODS_HR_ORG D ON C.CODE=D.SCODE\n" +
            "WHERE A.SCODE=(SELECT COMPANY_CODE FROM CREDIT_USER WHERE USERNAME=#{username, jdbcType=VARCHAR})  \n" +
            " )")
    @Results(id="user1",  value={
            @Result(property="userId", column="USER_ID"),
            @Result(property="username", column="USERNAME"),
            @Result(property="name", column="NAME")
    })
    public List<User> getPermissionUsers(@Param("username") String username);



    @Select("<script> " +
            "SELECT\n" +
            "BUYERCHNNAME,\n" +
            "BUYERENGNAME,\n" +
            "SINOSUREBUYERNO,\n" +
            "REPORTNO,\n" +
            "LEGALFORM,\n" +
            "DATEREGISTERED,\n" +
            "REGISTERCAPITAL,\n" +
            "REGISTERADDR,\n" +
            "EMPLOYEENUM,\n" +
            "BRANCHEMPLOYEENUM,\n" +
            "CASE OPERATIONSTATUS WHEN '1' THEN '正常经营'\n" +
            "WHEN '2' THEN '停歇业/休眠'\n" +
            "WHEN '3' THEN '无有效注册'\n" +
            "WHEN '4' THEN '破产保护'\n" +
            "WHEN '5' THEN '破产清算'\n" +
            "WHEN '6' THEN '被并购撤销'\n" +
            "WHEN '9' THEN '其他' END AS OPERATIONSTATUS,\n" +
            "PARENT,\n" +
            "CASE LISTEDFLAG WHEN  '1' THEN '是' WHEN '0' THEN '否' ELSE '未维护' END AS  LISTEDFLAG,\n" +
            "TOCKEXCHANGE,\n" +
            "PRODUCTS,\n" +
            "I.INDUSTNAME,\n" +
            "PRODUCTIONCAPACITY,\n" +
            "CASE LOCATION  WHEN '1' THEN '商业区' WHEN '2' THEN '工业区' WHEN '3' THEN '居民区' WHEN '4' THEN '港口' ELSE '其他' END AS LOCATIONTYPE,\n" +
            "PURCHASINGAREA,\n" +
            "FOREIGNPURCHASERATIO,\n" +
            " round(((months_between(TO_DATE( to_char(sysdate,'yyyy-MM-dd') ,'yyyy-mm-dd hh24:mi:ss'),TO_DATE(DATEREGISTERED,'yyyy-mm-dd hh24:mi:ss')))/12),0)||'年' as gisteryear,\n" +
            "UPDATETIME\n" +
            "FROM\n" +
            " ODS_ZXB_RATINGINFO R\n" +
            "INNER JOIN (\n" +
            " SELECT\n" +
            "  NOTICESERIALNO,\n" +
            "  MAX (UPDATETIME) MAXTIME\n" +
            " FROM\n" +
            "  ODS_ZXB_RATINGINFO\n" +
            "   \n" +
            " GROUP BY\n" +
            "  NOTICESERIALNO\n" +
            ") D ON D .MAXTIME = R.UPDATETIME AND D.NOTICESERIALNO=R.NOTICESERIALNO\n" +
            "LEFT JOIN DIM_ZXB_INDUSTRY I ON I.INDUSTCODE=R.CHINASIC\n" +
            "WHERE 1 = 1" +
            "AND ROWNUM = 1"+
            "<if test=\"name != null or engName != null\">"+
            " and (R.BUYERCHNNAME = #{name, jdbcType=VARCHAR}  OR  R.BUYERENGNAME = #{engName, jdbcType=VARCHAR} OR R.SINOSUREBUYERNO =#{reportbuyerno, jdbcType=VARCHAR}) "+
            "</if> " +
            "</script> "
    )

    @Results(id="zhongXinBaoInfo",  value={
            @Result(property="buyerchnName", column="BUYERCHNNAME"),
            @Result(property="buyerengName", column="BUYERENGNAME"),
            @Result(property="sinosureBuyerno", column="SINOSUREBUYERNO"),
            @Result(property="reportNo", column="REPORTNO"),
            @Result(property="legalForm", column="LEGALFORM"),
            @Result(property="dateRegistered", column="DATEREGISTERED"),

            @Result(property="registerCapital", column="REGISTERCAPITAL"),
            @Result(property="registerAddr", column="REGISTERADDR"),
            @Result(property="employeeNum", column="EMPLOYEENUM"),
            @Result(property="branchEmployeeNum", column="BRANCHEMPLOYEENUM"),
            @Result(property="operationStatus", column="OPERATIONSTATUS"),

            @Result(property="parent", column="PARENT"),
            @Result(property="listedFlag", column="LISTEDFLAG"),
            @Result(property="tockexChange", column="TOCKEXCHANGE"),
            @Result(property="products", column="PRODUCTS"),
            @Result(property="industName", column="INDUSTNAME"),

            @Result(property="productionCapacity", column="PRODUCTIONCAPACITY"),
            @Result(property="locationType", column="LOCATIONTYPE"),
            @Result(property="purchasingArea", column="PURCHASINGAREA"),
            @Result(property="foreignPurchaseRatio", column="FOREIGNPURCHASERATIO"),
            @Result(property="gisterYear", column="GISTERYEAR"),

            @Result(property="updateTime", column="UPDATETIME")
    })
    public Optional<ZhongXinBaoInfo> getZhongXinBaoInfo(@Param("name") String name,@Param("engName") String engName,@Param("reportbuyerno") String reportbuyerno);

}
