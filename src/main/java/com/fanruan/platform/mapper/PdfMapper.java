package com.fanruan.platform.mapper;

import com.fanruan.platform.bean.ZhongXinBaoApplyProgressList;
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
            "SELECT /*+ optimizer_features_enable('9.2.0')*/  \n" +
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
            "\t\tORDER BY UPDATETIME DESC) t \n" +
            "\t\tWHERE 1 = 1 \n"+
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
    //jina
    @Select("<script> " +
            "select * from (" +
            "SELECT /*+ optimizer_features_enable('9.2.0')*/ ROWNUM as R," +
            "\t T.* \n" +
            "FROM\n" +
            "\t(\n" +
            "\tSELECT DISTINCT\n" +
            "\t\tA.CORPSERIALNO_IMPORT,--流水号\n" +
            "\t\tA.REPORTBUYERNO,--信保代码\n" +
            "\t\tA.REPORTCORPCHNNAME,--中文名称\n" +
            "\t\tA.REPORTCORPENGNAME,--英文名称\n" +
            "\t\tto_char(A.UPDATETIME,'yyyy-mm-dd hh24:mi:ss') AS TBTIME, --填报时间\n" +
            "\t\tA.APPROVECODE, --审批标识\n" +
            "\t\tR1.RESULTS AS ZXBRESULTS,--中信保反馈\n" +
            "\t\tU.NAME, --审核人\n" +
            "\t\tto_char(A.APPROVEDATE,'yyyy-mm-dd hh24:mi:ss') AS APPROVEDATE, --审核时间\n" +
            "\t\tCASE WHEN P.NOTICESERIALNO IS NULL\n"+
            "\t\tTHEN '无'\n"+
            "\t\tELSE P.NOTICESERIALNO|| '.pdf' END as reportName,\n" +
            "\t\tCASE WHEN R.CORPSERIALNO NOT IN (SELECT CORPSERIALNO_IMPORT FROM LOG_ZXB_APPLY)\n" +
            "\t\tTHEN '已发送给其他用户'\n" +
            "\t\tELSE nvl(to_char(p.UPDATETIME,'yyyy-mm-dd hh24:mi:ss'),'暂无报告') END AS UPDATETIME,\n" +
            "\t\tNVL(I.MAXTIME,'暂无摘要') AS GETTIME,\n" +
            "\t\tA.UPDATEBY  \n" +
            "\t\tFROM\n" +
            "\t\tLOG_ZXB_APPLY A\n" +
            "\t\tLEFT JOIN (\n" +
            "\t\tSELECT R.*,A.REPORTBUYERNO,A.reportcorpchnname,A.reportcorpengname \n" +
            "\t\tFROM ODS_ZXB_REPORTAPPROVE R \n" +
            "\t\tLEFT JOIN LOG_ZXB_APPLY A \n" +
            "\t\tON R.CORPSERIALNO = A.CORPSERIALNO_IMPORT \n" +
            "\t\tWHERE 1=1 \n" +
            "\t\tAND ORDERSTATE = '0') R \n"+
            "\t\tON R.REPORTBUYERNO  = A.REPORTBUYERNO \n"+
            "\t\tOR R.reportcorpchnname = A.reportcorpchnname \n"+
            "\t\tOR R.reportcorpengname = A.reportcorpengname \n"+

            "\t\tLEFT JOIN (SELECT distinct R.CORPSERIALNO,R.RESULTS FROM ODS_ZXB_REPORTAPPROVE R inner JOIN  \n"+
            "\t\t(SELECT CORPSERIALNO,MAX(SERIALNO) AS SERIALNO1 FROM ODS_ZXB_REPORTAPPROVE GROUP BY CORPSERIALNO )R2  \n"+
            "\t\tON R.SERIALNO=R2.SERIALNO1 AND R.CORPSERIALNO=R2.CORPSERIALNO  ) R1 ON A.CORPSERIALNO_IMPORT=R1.CORPSERIALNO \n"+

            "\t\tLEFT JOIN ODS_ZXB_PDF P  ON R.NOTICESERIALNO = P.NOTICESERIALNO  \n"+
            "\t\tLEFT JOIN ( SELECT NOTICESERIALNO, MAX( UPDATETIME ) MAXTIME FROM ODS_ZXB_PDF GROUP BY NOTICESERIALNO ) D ON D.MAXTIME = P.UPDATETIME\n"+
            "\t\tLEFT JOIN CREDIT_USER U ON A.UPDATEBY = U.USERNAME \n"+
            "\t\tLEFT JOIN ( SELECT BUYERENGNAME, BUYERCHNNAME, SINOSUREBUYERNO, MAX( UPDATETIME ) MAXTIME FROM ODS_ZXB_RATINGINFO GROUP BY BUYERENGNAME, BUYERCHNNAME,  \n"+
            "\t\tSINOSUREBUYERNO ) I ON I.BUYERENGNAME = A.REPORTCORPENGNAME  \n"+
            "\t\tOR I.BUYERCHNNAME = A.REPORTCORPCHNNAME\n"+
            "\t\tOR I.SINOSUREBUYERNO = A.REPORTBUYERNO \n"+
            "\t\tORDER BY TBTIME DESC) t\n"+
            "WHERE 1 = 1\n"+
            "AND T.UPDATEBY = #{userName}\n" +
            "<if test=\"name != null and name!=''\"> -- 中英文名\n" +
            "and (T.REPORTCORPCHNNAME like '%'||#{name, jdbcType=VARCHAR}||'%' OR t.REPORTCORPENGNAME like '%'||#{name, jdbcType=VARCHAR}||'%') \n" +
            "</if> \n" +
            "<if test=\"xcode != null and xcode!=''\">-- 信保代码\n" +
            "and (t.REPORTBUYERNO like '%'||#{xcode, jdbcType=VARCHAR}||'%')\n" +
            "</if> " +
            "<if test=\"approve != null and approve!=''\">-- 审批标识\n" +
            "and (t.APPROVECODE = #{approve, jdbcType=VARCHAR})\n" +
            "</if> " +
            "AND ROWNUM &lt;= #{page, jdbcType=INTEGER} * #{pageSize, jdbcType=INTEGER} )s \n" +
            "where r &gt; (#{page, jdbcType=INTEGER}-1) * #{pageSize, jdbcType=INTEGER}\n"+
            "</script> "
    )
//    @Select("<script> " +
//            "select * from (" +
//            "SELECT /*+ optimizer_features_enable('9.2.0')*/ ROWNUM as R," +
//            "\t T.* \n" +
//            "FROM\n" +
//            "\t(\n" +
//            "\tSELECT DISTINCT\n" +
//            "\t\tA.CORPSERIALNO_IMPORT,--流水号\n" +
//            "\t\tA.REPORTBUYERNO,--信保代码\n" +
//            "\t\tA.REPORTCORPCHNNAME,--中文名称\n" +
//            "\t\tA.REPORTCORPENGNAME,--英文名称\n" +
//            "\t\tto_char(A.UPDATETIME,'yyyy-mm-dd hh24:mm:ss') AS TBTIME, --填报时间\n" +
//            "\t\tA.APPROVECODE, --审批标识\n" +
//            "\t\tU.NAME, --审核人\n" +
//            "\t\tto_char(A.APPROVEDATE,'yyyy-mm-dd hh24:mm:ss') AS APPROVEDATE, --审核时间\n" +
//            "\t\tCASE WHEN P.NOTICESERIALNO IS NULL\n"+
//            "\t\tTHEN '无'\n"+
//            "\t\tELSE P.NOTICESERIALNO|| '.pdf' END as reportName,\n" +
//            "\t\tCASE WHEN R.CORPSERIALNO <![CDATA[ <> ]]> A.CORPSERIALNO_IMPORT\n" +
//            "\t\tTHEN '已被其他用户申请'\n" +
//            "\t\tELSE nvl(to_char(p.UPDATETIME,'yyyy-mm-dd hh24:mm:ss'),'暂无报告') END AS UPDATETIME,\n" +
//            "\t\tNVL(I.MAXTIME,'暂无摘要') AS GETTIME,\n" +
//            "\t\tA.UPDATEBY  \n" +
//            "\t\tFROM\n" +
//            "\t\tLOG_ZXB_APPLY A\n" +
//            "\t\tLEFT JOIN (\n" +
//            "\t\tSELECT R.*,A.REPORTBUYERNO\n" +
//            "\t\tFROM ODS_ZXB_REPORTAPPROVE R\n" +
//            "\t\tLEFT JOIN LOG_ZXB_APPLY A\n" +
//            "\t\tON R.CORPSERIALNO = A.CORPSERIALNO_IMPORT \n" +
//            "\t\tWHERE NVL(A.REPORTBUYERNO,'0')>'0'\n" +
//            "\t\tAND ORDERSTATE = '0') R ON R.REPORTBUYERNO  = A.REPORTBUYERNO \n"+
//            "\t\tLEFT JOIN ODS_ZXB_PDF P  ON R.NOTICESERIALNO = P.NOTICESERIALNO  \n"+
//            "\t\tLEFT JOIN ( SELECT NOTICESERIALNO, MAX( UPDATETIME ) MAXTIME FROM ODS_ZXB_PDF GROUP BY NOTICESERIALNO ) D ON D.MAXTIME = P.UPDATETIME\n"+
//            "\t\tLEFT JOIN CREDIT_USER U ON A.UPDATEBY = U.USERNAME \n"+
//            "\t\tLEFT JOIN ( SELECT BUYERENGNAME, BUYERCHNNAME, SINOSUREBUYERNO, MAX( UPDATETIME ) MAXTIME FROM ODS_ZXB_RATINGINFO GROUP BY BUYERENGNAME, BUYERCHNNAME,  \n"+
//            "\t\tSINOSUREBUYERNO ) I ON I.BUYERENGNAME = A.REPORTCORPENGNAME  \n"+
//            "\t\tOR I.BUYERCHNNAME = A.REPORTCORPCHNNAME\n"+
//            "\t\tOR I.SINOSUREBUYERNO = A.REPORTBUYERNO \n"+
//            "\t\tORDER BY TBTIME DESC) t\n"+
//            "WHERE 1 = 1\n"+
//            "AND T.UPDATEBY = #{userName}\n" +
//            "<if test=\"name != null and name!=''\"> -- 中英文名\n" +
//            "and (T.REPORTCORPCHNNAME like '%'||#{name, jdbcType=VARCHAR}||'%' OR t.REPORTCORPENGNAME like '%'||#{name, jdbcType=VARCHAR}||'%') \n" +
//            "</if> \n" +
//            "<if test=\"xcode != null and xcode!=''\">-- 信保代码\n" +
//            "and (t.REPORTBUYERNO like '%'||#{xcode, jdbcType=VARCHAR}||'%')\n" +
//            "</if> " +
//            "<if test=\"approve != null and approve!=''\">-- 审批标识\n" +
//            "and (t.APPROVECODE = #{approve, jdbcType=VARCHAR})\n" +
//            "</if> " +
//            "AND ROWNUM &lt;= #{page, jdbcType=INTEGER} * #{pageSize, jdbcType=INTEGER} )s \n" +
//            "where r &gt; (#{page, jdbcType=INTEGER}-1) * #{pageSize, jdbcType=INTEGER}\n"+
//            "</script> "
//    )
    @Results(id="zhongXinBaoApplyProgressList", value={
            @Result(property="reportbuyerno",   column="REPORTBUYERNO"),
            @Result(property="reportcorpchnname",   column="REPORTCORPCHNNAME"),
            @Result(property="reportcorpengname",   column="REPORTCORPENGNAME"),
            @Result(property="reportName",   column="reportName"),
            @Result(property="updatetime",   column="UPDATETIME"),
            @Result(property="getTime",   column="GETTIME"),
            @Result(property ="approveCode", column="APPROVECODE"),
            @Result(property ="approveby", column="NAME"),
            @Result(property ="approveDate", column="APPROVEDATE"),
            @Result(property ="ZXBresults", column="ZXBRESULTS"),
            @Result(property ="TBTIME", column="TBTIME")
    })
    public List<ZhongXinBaoApplyProgressList> selectZhongXinBaoApplyProgressList(@Param("xcode") String xcode, @Param("name") String name,@Param("userName") String userName,@Param("approve") String approve, @Param("pageSize") Integer pageSize, @Param("page") Integer page);
    /**
     * 分页
     * @param name
     * @return
     */
    @Select("<script> " +
            "SELECT /*+ optimizer_features_enable('9.2.0')*/  \n" +
            "\tCOUNT( 1 ) AS co \n" +
            "FROM (\n"+
            "SELECT  ROWNUM as R,\n"+
            "T.*\n"+
            "FROM" +
            "\t(\n" +
            "\tSELECT DISTINCT\n" +
            "\t\tA.CORPSERIALNO_IMPORT,--流水号\n" +
            "\t\tA.REPORTBUYERNO,--信保代码\n" +
            "\t\tA.REPORTCORPCHNNAME,--中文名称\n" +
            "\t\tA.REPORTCORPENGNAME,--英文名称\n" +
            "\t\tto_char(A.UPDATETIME,'yyyy-mm-dd hh24:mi:ss') AS TBTIME, --填报时间\n" +
            "\t\tA.APPROVECODE, --审批标识\n" +
            "\t\tR1.RESULTS,--中信保反馈\n" +
            "\t\tU.NAME, --审核人\n" +
            "\t\tto_char(A.APPROVEDATE,'yyyy-mm-dd hh24:mi:ss') AS APPROVEDATE, --审核时间\n" +
            "\t\tCASE WHEN P.NOTICESERIALNO IS NULL\n"+
            "\t\tTHEN '无'\n"+
            "\t\tELSE P.NOTICESERIALNO|| '.pdf' END as reportName,\n" +
            "\t\tCASE WHEN R.CORPSERIALNO NOT IN (SELECT CORPSERIALNO_IMPORT FROM LOG_ZXB_APPLY) \n" +
            "\t\tTHEN '已发送给其他用户'\n" +
            "\t\tELSE nvl(to_char(p.UPDATETIME,'yyyy-mm-dd hh24:mi:ss'),'暂无报告') END AS UPDATETIME,\n" +
            "\t\tNVL(I.MAXTIME,'暂无摘要') AS GETTIME,\n" +
            "\t\tA.UPDATEBY  \n" +
            "\t\tFROM\n" +
            "\t\tLOG_ZXB_APPLY A\n" +
            "\t\tLEFT JOIN (\n" +
            "\t\tSELECT R.*,A.REPORTBUYERNO,A.reportcorpchnname,A.reportcorpengname \n" +
            "\t\tFROM ODS_ZXB_REPORTAPPROVE R \n" +
            "\t\tLEFT JOIN LOG_ZXB_APPLY A \n" +
            "\t\tON R.CORPSERIALNO = A.CORPSERIALNO_IMPORT \n" +
            "\t\tWHERE 1=1 \n" +
            "\t\tAND ORDERSTATE = '0') R \n"+
            "\t\tON R.REPORTBUYERNO  = A.REPORTBUYERNO \n"+
            "\t\tOR R.reportcorpchnname = A.reportcorpchnname \n"+
            "\t\tOR R.reportcorpengname = A.reportcorpengname \n"+

            "\t\tLEFT JOIN (SELECT distinct R.CORPSERIALNO,R.RESULTS FROM ODS_ZXB_REPORTAPPROVE R inner JOIN  \n"+
            "\t\t(SELECT CORPSERIALNO,MAX(SERIALNO) AS SERIALNO1 FROM ODS_ZXB_REPORTAPPROVE GROUP BY CORPSERIALNO )R2  \n"+
            "\t\tON R.SERIALNO=R2.SERIALNO1 AND R.CORPSERIALNO=R2.CORPSERIALNO  ) R1 ON A.CORPSERIALNO_IMPORT=R1.CORPSERIALNO \n"+

            "\t\tLEFT JOIN ODS_ZXB_PDF P  ON R.NOTICESERIALNO = P.NOTICESERIALNO  \n"+
            "\t\tLEFT JOIN ( SELECT NOTICESERIALNO, MAX( UPDATETIME ) MAXTIME FROM ODS_ZXB_PDF GROUP BY NOTICESERIALNO ) D ON D.MAXTIME = P.UPDATETIME\n"+
            "\t\tLEFT JOIN CREDIT_USER U ON A.UPDATEBY = U.USERNAME \n"+
            "\t\tLEFT JOIN ( SELECT BUYERENGNAME, BUYERCHNNAME, SINOSUREBUYERNO, MAX( UPDATETIME ) MAXTIME FROM ODS_ZXB_RATINGINFO GROUP BY BUYERENGNAME, BUYERCHNNAME,  \n"+
            "\t\tSINOSUREBUYERNO ) I ON I.BUYERENGNAME = A.REPORTCORPENGNAME  \n"+
            "\t\tOR I.BUYERCHNNAME = A.REPORTCORPCHNNAME\n"+
            "\t\tOR I.SINOSUREBUYERNO = A.REPORTBUYERNO \n"+
            "\t\tORDER BY TBTIME DESC) t\n"+
            "WHERE 1 = 1\n"+
            "AND T.UPDATEBY = #{userName}\n" +
            "<if test=\"name != null and name!=''\"> -- 中英文名\n" +
            "and (T.REPORTCORPCHNNAME like '%'||#{name, jdbcType=VARCHAR}||'%' OR t.REPORTCORPENGNAME like '%'||#{name, jdbcType=VARCHAR}||'%') \n" +
            "</if> \n" +
            "<if test=\"xcode != null and xcode!=''\">-- 信保代码\n" +
            "and (t.REPORTBUYERNO like '%'||#{xcode, jdbcType=VARCHAR}||'%')\n" +
            "</if> " +
            "<if test=\"approve != null and approve!=''\">-- 审批标识\n" +
            "and (t.APPROVECODE = #{approve, jdbcType=VARCHAR})\n" +
            "</if> " +
            ")s \n" +
            "</script> "
    )
    @Results(id="zhongXinBaoApplyProgressListCount", value={
            @Result(property="co",   column="co")
    })
//    @Select("<script> " +
//            "SELECT /*+ optimizer_features_enable('9.2.0')*/  \n" +
//            "\tCOUNT( 1 ) AS co \n" +
//            "FROM (\n"+
//            "SELECT  ROWNUM as R,\n"+
//            "T.*\n"+
//            "FROM" +
//            "\t(\n" +
//            "\tSELECT DISTINCT\n" +
//            "\t\tA.CORPSERIALNO_IMPORT,--流水号\n" +
//            "\t\tA.REPORTBUYERNO,--信保代码\n" +
//            "\t\tA.REPORTCORPCHNNAME,--中文名称\n" +
//            "\t\tA.REPORTCORPENGNAME,--英文名称\n" +
//            "\t\tto_char(A.UPDATETIME,'yyyy-mm-dd hh24:mm:ss') AS TBTIME, --填报时间\n" +
//            "\t\tA.APPROVECODE, --审批标识\n" +
//            "\t\tU.NAME, --审核人\n" +
//            "\t\tto_char(A.APPROVEDATE,'yyyy-mm-dd hh24:mm:ss') AS APPROVEDATE, --审核时间\n" +
//            "\t\tCASE WHEN P.NOTICESERIALNO IS NULL\n"+
//            "\t\tTHEN '无'\n"+
//            "\t\tELSE P.NOTICESERIALNO|| '.pdf' END as reportName,\n" +
//            "\t\tCASE WHEN R.CORPSERIALNO <![CDATA[ <> ]]> A.CORPSERIALNO_IMPORT\n" +
//            "\t\tTHEN '已被其他用户申请'\n" +
//            "\t\tELSE nvl(to_char(p.UPDATETIME,'yyyy-mm-dd hh24:mm:ss'),'暂无报告') END AS UPDATETIME,\n" +
//            "\t\tNVL(I.MAXTIME,'暂无摘要') AS GETTIME,\n" +
//            "\t\tA.UPDATEBY  \n" +
//            "\t\tFROM\n" +
//            "\t\tLOG_ZXB_APPLY A\n" +
//            "\t\tLEFT JOIN  ODS_ZXB_REPORTAPPROVE R ON R.CORPSERIALNO = A.CORPSERIALNO_IMPORT  \n"+
//            "\t\tAND ORDERSTATE = '0'  \n"+
//            "\t\tLEFT JOIN ODS_ZXB_PDF P  ON R.NOTICESERIALNO = P.NOTICESERIALNO  \n"+
//            "\t\tLEFT JOIN ( SELECT NOTICESERIALNO, MAX( UPDATETIME ) MAXTIME FROM ODS_ZXB_PDF GROUP BY NOTICESERIALNO ) D ON D.MAXTIME = P.UPDATETIME\n"+
//            "\t\tLEFT JOIN CREDIT_USER U ON A.UPDATEBY = U.USERNAME \n"+
//            "\t\tLEFT JOIN ( SELECT BUYERENGNAME, BUYERCHNNAME, SINOSUREBUYERNO, MAX( UPDATETIME ) MAXTIME FROM ODS_ZXB_RATINGINFO GROUP BY BUYERENGNAME, BUYERCHNNAME,  \n"+
//            "\t\tSINOSUREBUYERNO ) I ON I.BUYERENGNAME = A.REPORTCORPENGNAME  \n"+
//            "\t\tOR I.BUYERCHNNAME = A.REPORTCORPCHNNAME\n"+
//            "\t\tOR I.SINOSUREBUYERNO = A.REPORTBUYERNO \n"+
//            "\t\tORDER BY TBTIME DESC) t\n"+
//            "WHERE 1 = 1\n"+
//            "AND T.UPDATEBY = #{userName}\n" +
//            "<if test=\"name != null and name!=''\"> -- 中英文名\n" +
//            "and (T.REPORTCORPCHNNAME like '%'||#{name, jdbcType=VARCHAR}||'%' OR t.REPORTCORPENGNAME like '%'||#{name, jdbcType=VARCHAR}||'%') \n" +
//            "</if> \n" +
//            "<if test=\"xcode != null and xcode!=''\">-- 信保代码\n" +
//            "and (t.REPORTBUYERNO like '%'||#{xcode, jdbcType=VARCHAR}||'%')\n" +
//            "</if> " +
//            "<if test=\"approve != null and approve!=''\">-- 审批标识\n" +
//            "and (t.APPROVECODE = #{approve, jdbcType=VARCHAR})\n" +
//            "</if> " +
//            ")s \n" +
//            "</script> "
//    )
//    @Results(id="zhongXinBaoApplyProgressListCount", value={
//            @Result(property="co",   column="co")
//    })
    public Integer selectZhongXinBaoApplyProgressListCount(@Param("xcode") String xcode,@Param("name") String name,@Param("userName") String userName,@Param("approve") String approve);
}