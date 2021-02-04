package com.fanruan.platform.mapper;

import com.fanruan.platform.bean.ZhongXinBaoPDF;
import com.fanruan.platform.bean.ZhongXinBaoPDFList;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface PdfMapper {
    @Select("<script> " +
            "SELECT DISTINCT \n" +
            "P.NOTICESERIALNO||'.pdf' as NOTICESERIALNO,\n" +
            "p.UPDATETIME \n" +
            "FROM ODS_ZXB_PDF P\n" +
            /*"INNER JOIN (SELECT NOTICESERIALNO,MAX(UPDATETIME) MAXTIME FROM ODS_ZXB_PDF  GROUP BY NOTICESERIALNO) D \n" +
            "ON D.MAXTIME=P.UPDATETIME and p.NOTICESERIALNO=D.NOTICESERIALNO\n" +*/
            "INNER JOIN ODS_ZXB_REPORTAPPROVE R ON R.NOTICESERIALNO=P.NOTICESERIALNO \n" +
            "LEFT JOIN LOG_ZXB_APPLY A ON A.CORPSERIALNO_IMPORT=R.CORPSERIALNO \n" +
            "LEFT JOIN ODS_ZXB_RATINGINFO F ON F.SINOSUREBUYERNO=A.REPORTBUYERNO " +
            "WHERE 1 = 1" +
            "<if test=\"(name != null and name != '')  or (engName != null and engName != '')\">"+
            " and ORDERSTATE='0' AND  A.REPORTCORPCHNNAME= #{name,jdbcType=VARCHAR}  OR F.BUYERCHNNAME=#{name,jdbcType=VARCHAR}  and A.REPORTCORPENGNAME=#{engName,jdbcType=VARCHAR} OR F.BUYERENGNAME=#{engName,jdbcType=VARCHAR} OR  F.SINOSUREBUYERNO =#{reportbuyerno,jdbcType=VARCHAR}"+
            "</if> \n" +
            "ORDER BY p.UPDATETIME DESC"+
            "</script> "
    )
    @Results(id="zhongXinBaoPDF", value={
            @Result(property="noticeSerialno",   column="NOTICESERIALNO"),
            @Result(property="updateTime",   column="UPDATETIME")
    })
    public List<ZhongXinBaoPDF> selectZhongXinBaoPDF(@Param("name") String name,@Param("engName") String engName,@Param("reportbuyerno") String reportbuyerno);

    @Select("<script> " +
            "select * from (" +
            "SELECT /*+ optimizer_features_enable('9.2.0')*/ ROWNUM as r," +
            "\t t.* \n" +
            "FROM\n" +
            "\t(\n" +
            "\tSELECT DISTINCT\n" +
            "\t\tA.CORPSERIALNO_IMPORT,--流水号\n" +
            "\t\tA.REPORTBUYERNO,--信保代码\n" +
            "\t\tA.REPORTCORPCHNNAME,--中文名称\n" +
            "\t\tA.REPORTCORPENGNAME,--英文名称\n" +
            "\t\tP.NOTICESERIALNO || '.pdf' as reportName,\n" +
            "\t\tNVL(I.MAXTIME,'暂无摘要') AS GETTIME,\n" +
            "\t\tp.UPDATETIME \n" +
            "\tFROM\n" +
            "\t\tODS_ZXB_PDF P\n" +
            "\t\tINNER JOIN ( SELECT NOTICESERIALNO, MAX( UPDATETIME ) MAXTIME FROM ODS_ZXB_PDF GROUP BY NOTICESERIALNO ) D ON D.MAXTIME = P.UPDATETIME\n" +
            "\t\tINNER JOIN ODS_ZXB_REPORTAPPROVE R ON R.NOTICESERIALNO = P.NOTICESERIALNO \n" +
            "\t\tAND ORDERSTATE = '0'\n" +
            "\t\tINNER JOIN LOG_ZXB_APPLY A ON R.CORPSERIALNO = A.CORPSERIALNO_IMPORT\n" +
            "\t\tLEFT JOIN ( SELECT BUYERENGNAME, BUYERCHNNAME, SINOSUREBUYERNO, MAX( UPDATETIME ) MAXTIME FROM ODS_ZXB_RATINGINFO GROUP BY BUYERENGNAME, BUYERCHNNAME, SINOSUREBUYERNO ) I ON I.BUYERENGNAME = A.REPORTCORPENGNAME \n" +
            "\t\tOR I.BUYERCHNNAME = A.REPORTCORPCHNNAME \n" +
            "\t\tOR I.SINOSUREBUYERNO = A.REPORTBUYERNO \n"+
            "\t\tORDER BY UPDATETIME DESC) t " +
            "WHERE 1 = 1"+
            "<if test=\"name != null and name!=''\">"+
            "and (t.REPORTCORPCHNNAME like '%'||#{name, jdbcType=VARCHAR}||'%' OR t.REPORTCORPENGNAME like '%'||#{name, jdbcType=VARCHAR}||'%') "+
            "</if> "+
            "<if test=\"xcode != null and xcode!=''\">"+
            "and (t.REPORTBUYERNO like '%'||#{xcode, jdbcType=VARCHAR}||'%')"+
            "</if> "+
            "AND ROWNUM &lt;= #{page, jdbcType=INTEGER} * #{pageSize, jdbcType=INTEGER} )s \n" +
            "where r &gt; (#{page, jdbcType=INTEGER}-1) * #{pageSize, jdbcType=INTEGER} \n" +
            "</script> "
    )
    @Results(id="zhongXinBaoPDFList", value={
            @Result(property="reportbuyerno",   column="REPORTBUYERNO"),
            @Result(property="reportcorpchnname",   column="REPORTCORPCHNNAME"),
            @Result(property="reportcorpengname",   column="REPORTCORPENGNAME"),
            @Result(property="reportName",   column="reportName"),
            @Result(property="updatetime",   column="UPDATETIME"),
            @Result(property="getTime",   column="GETTIME")
    })
    public List<ZhongXinBaoPDFList> selectZhongXinBaoPDFList(@Param("xcode") String xcode, @Param("name") String name, @Param("pageSize") Integer pageSize, @Param("page") Integer page);


    /**
     * 分页
     * @param name
     * @return
     */
    @Select("<script> " +
            "SELECT\n" +
            "\tCOUNT( 1 ) AS co \n" +
            "FROM\n" +
            "\t(\n" +
            "\tSELECT DISTINCT\n" +
            "\t\tA.CORPSERIALNO_IMPORT,--流水号\n" +
            "\t\tA.REPORTBUYERNO,--信保代码\n" +
            "\t\tA.REPORTCORPCHNNAME,--中文名称\n" +
            "\t\tA.REPORTCORPENGNAME,--英文名称\n" +
            "\t\tP.NOTICESERIALNO || '.pdf' as reportName,\n" +
            "\t\tNVL(I.MAXTIME,'暂无摘要') AS GETTIME,\n" +
            "\t\tp.UPDATETIME \n" +
            "\tFROM\n" +
            "\t\tODS_ZXB_PDF P\n" +
            "\t\tINNER JOIN ( SELECT NOTICESERIALNO, MAX( UPDATETIME ) MAXTIME FROM ODS_ZXB_PDF GROUP BY NOTICESERIALNO ) D ON D.MAXTIME = P.UPDATETIME\n" +
            "\t\tINNER JOIN ODS_ZXB_REPORTAPPROVE R ON R.NOTICESERIALNO = P.NOTICESERIALNO \n" +
            "\t\tAND ORDERSTATE = '0'\n" +
            "\t\tINNER JOIN LOG_ZXB_APPLY A ON R.CORPSERIALNO = A.CORPSERIALNO_IMPORT\n" +
            "\t\tLEFT JOIN ( SELECT BUYERENGNAME, BUYERCHNNAME, SINOSUREBUYERNO, MAX( UPDATETIME ) MAXTIME FROM ODS_ZXB_RATINGINFO GROUP BY BUYERENGNAME, BUYERCHNNAME, SINOSUREBUYERNO ) I ON I.BUYERENGNAME = A.REPORTCORPENGNAME \n" +
            "\t\tOR I.BUYERCHNNAME = A.REPORTCORPCHNNAME \n" +
            "\t\tOR I.SINOSUREBUYERNO = A.REPORTBUYERNO \n"+
            "\t\tORDER BY UPDATETIME DESC) t " +
            "WHERE 1 = 1"+
            "<if test=\"name != null and name!=''\">"+
            "and (t.REPORTCORPCHNNAME like '%'||#{name, jdbcType=VARCHAR}||'%' OR t.REPORTCORPENGNAME like '%'||#{name, jdbcType=VARCHAR}||'%') "+
            "</if> "+
            "<if test=\"xcode != null and xcode!=''\">"+
            "and (t.REPORTBUYERNO like '%'||#{xcode, jdbcType=VARCHAR}||'%')"+
            "</if> "+
            "</script> "
    )
    @Results(id="zhongXinBaoPDFListCount", value={
            @Result(property="co",   column="co")
    })
    public Integer selectZhongXinBaoPDFListCount(@Param("xcode") String xcode,@Param("name") String name);
}