package com.fanruan.platform.service;

import com.fanruan.platform.bean.*;
import com.fanruan.platform.mapper.ReportUseMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.search.aggregations.metrics.InternalHDRPercentiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ReportUseService {

    @Autowired
    ReportUseMapper reportUseMapper;
    /**
     * 在库报告使用情况
     * @param rpVO
     * @return
     */
    public String getXbReportUse(ReportParameter rpVO) throws Exception{
        //在库报告使用
        List<XbReportUse> xbReportUse = reportUseMapper.getXbReportUse(rpVO);
        //信保报告使用记录
        List<XbReportUseList> xbReportUseList = reportUseMapper.getXbReportUseList(rpVO);

        ObjectMapper objectMapper=new ObjectMapper();
        HashMap<String,Object> hs=new HashMap<>();
        hs.put("code","0");
        hs.put("msg","查询成功");
        hs.put("xbReportUse",xbReportUse);
        hs.put("xbReportUseList",xbReportUseList);
        return objectMapper.writeValueAsString(hs);
    }

    /**
     * 信保报告使用记录
     * @param rpVO
     * @return
     */
    public String getXbReportUseList(ReportParameter rpVO) throws Exception{
        return null;
    }

    /**
     * 公司在库报告使用情况
     * @param rpVO
     * @return
     */
    public String getXbReportUseByCompany(ReportParameter rpVO) throws Exception{
        List<XbReportUseByCompany> xbReportUse = reportUseMapper.getXbReportUseByCompany(rpVO);

        if(xbReportUse!=null&&xbReportUse.size()>0){
            for (XbReportUseByCompany xb :xbReportUse) {
                xb.setDownLoadNum(0);
                xb.setPreviewNum(0);
            }
        }

        List<Map<String,Object>> list = reportUseMapper.getUseNum(rpVO);
        if(list!=null&&list.size()>0){
            for (XbReportUseByCompany xbReportUseByCompany:
                    xbReportUse) {
                String reportCode = xbReportUseByCompany.getReportCode();
                for (int i = 0; i < list.size(); i++) {
                    Map<String,Object> map = list.get(i);
                    String name = map.get("NAME")==null?"":map.get("NAME").toString();
                    String code = map.get("CODE")==null?"":map.get("CODE").toString();
                    String type = map.get("TYPE")==null?"":map.get("TYPE").toString();
                    Integer num = map.get("NUM")==null?0:new Integer(map.get("NUM").toString());
                    if(code.equals(reportCode)){
                        if(type.equals("1")){
                            xbReportUseByCompany.setDownLoadNum(num);
                        }else if(type.equals("0")){
                            xbReportUseByCompany.setPreviewNum(num);
                        }
                    }
                }
            }
        }
        ObjectMapper objectMapper=new ObjectMapper();
        HashMap<String,Object> hs=new HashMap<>();
        hs.put("code","0");
        hs.put("msg","查询成功");
        hs.put("data",xbReportUse);
        return objectMapper.writeValueAsString(hs);
    }

    public ZcxReportUse getZcxReportUseByType(List<ZcxReportUse> zcxReportUseList,String type){
        for (ZcxReportUse zcxReportUse:
                zcxReportUseList) {
            String module = zcxReportUse.getModule();
            if(module.equals(type)){
                return zcxReportUse;
            }
        }
        ZcxReportUse newZcxReportUse = new ZcxReportUse();
        newZcxReportUse.setModule(type);
        return newZcxReportUse;
    }

    /**
     * 中诚信使用情况
     * @return
     */
    public String getZcxReportUse(ReportParameter rpVO) throws Exception{
        List<ZcxReportUse> zcxReportUses = new ArrayList<>();
        List<ZcxReportUse> zcxReportUseList  = reportUseMapper.getZcxReportUse(rpVO);
        List<Map<String,Object>> zcxZgz = reportUseMapper.getZcxReportGz(rpVO);

//        if(zcxReportUseList!=null&&zcxReportUseList.size()>0){
        //风险初筛
        ZcxReportUse fxcs = getZcxReportUseByType(zcxReportUseList,"风险初筛");
        fxcs.setNumberOfHits("10点/次");
        fxcs.setNo("1");
        zcxReportUses.add(fxcs);
        //财务排雷
        ZcxReportUse cwpl = getZcxReportUseByType(zcxReportUseList,"财务排雷");
        cwpl.setNumberOfHits("2点/次");
        cwpl.setNo("2");
        zcxReportUses.add(cwpl);
        //产业企业评价
        ZcxReportUse cyqypj = getZcxReportUseByType(zcxReportUseList,"产业企业信用评价");
        cyqypj.setNumberOfHits("2点/次");
        cyqypj.setNo("3");
        zcxReportUses.add(cyqypj);
        //区域信用评价
        ZcxReportUse qyxypj = getZcxReportUseByType(zcxReportUseList,"区域信用评价");
        qyxypj.setNumberOfHits("2点/次");
        qyxypj.setNo("4");
        zcxReportUses.add(qyxypj);

        //城投企业评价
        ZcxReportUse ctqypj = getZcxReportUseByType(zcxReportUseList,"城投企业信用评价");
        ctqypj.setNumberOfHits("2点/次");
        ctqypj.setNo("5");
        zcxReportUses.add(ctqypj);
        //风险预警（关注不与时间联动）
        ZcxReportUse fxyj = getZcxReportUseByType(zcxReportUseList,"风险预警（关注不与时间联动）");
        fxyj.setNumberOfHits("100点/企业/年");
        fxyj.setNo("6");
        if(zcxZgz!=null&&zcxZgz.size()>0){
            int num  = zcxZgz.get(0).get("COUNT")==null?0:new Integer(zcxZgz.get(0).get("COUNT").toString());
            fxyj.setUserNumber(num);
        }
        zcxReportUses.add(fxyj);

        ZcxReportUse total = new ZcxReportUse();
        total.setNo("7");
        total.setModule("合计");
        Integer ds = 10*getInteger(fxcs.getUserNumber())+2*(getInteger(cwpl.getUserNumber())+getInteger(cyqypj.getUserNumber())

                +getInteger(qyxypj.getUserNumber())+getInteger(ctqypj.getUserNumber()))+100*getInteger(fxyj.getUserNumber());
        total.setNumberOfHits(""+ds);
        Integer totalUserNum = getInteger(fxcs.getUserNumber())+getInteger(cwpl.getUserNumber())
                +getInteger(cyqypj.getUserNumber())+getInteger(qyxypj.getUserNumber())+
                getInteger(ctqypj.getUserNumber());
        total.setUserNumber(totalUserNum);
        Integer shareNumber = getInteger(fxcs.getShareNumber())+getInteger(cwpl.getShareNumber())
                +getInteger(cyqypj.getShareNumber())+getInteger(qyxypj.getShareNumber())+
                getInteger(ctqypj.getShareNumber());

        total.setShareNumber(shareNumber);
        //中诚信共享求和
//        Map<String,Object> zcxShare = reportUseMapper.getZxcShareSum(rpVO);
//        Integer shareNum  = zcxShare.get("NUM")==null?0:new Integer(zcxShare.get("NUM").toString());
//        total.setShareNumber(shareNum);
        zcxReportUses.add(total);
//        }


        ObjectMapper objectMapper=new ObjectMapper();
        HashMap<String,Object> hs=new HashMap<>();
        hs.put("code","0");
        hs.put("msg","查询成功");
        hs.put("data",zcxReportUses);
        return objectMapper.writeValueAsString(hs);
    }

    public Integer getInteger(Object obj){
        if(obj!=null&&!obj.equals("")){
            return new Integer(obj.toString());
        }
        return 0;
    }

    /**
     * 中诚信申请记录
     * @return
     */
    public String getZcxReportUseList(ReportParameter rpVO) throws Exception{
        List<ZcxReportUseList> zcxReportUseList = reportUseMapper.getZcxReportUseList(rpVO);
        ObjectMapper objectMapper=new ObjectMapper();
        HashMap<String,Object> hs=new HashMap<>();
        hs.put("code","0");
        hs.put("msg","查询成功");
        hs.put("data",zcxReportUseList);
        return objectMapper.writeValueAsString(hs);
    }

    /**
     * 监控记录
     * @param rpVO
     * @return
     */
    public String getMonitoring(ReportParameter rpVO) throws Exception{
        List<Monitoring> monitoring  = reportUseMapper.getMonitoring(rpVO);
        //按公司名称排序
        if(monitoring!=null&&monitoring.size()>0){
            List<String> namelist = new ArrayList<>();
            int no = 0;
            for (int i = 0; i < monitoring.size(); i++) {
                String companyName = monitoring.get(i).getGzCompanyName();
                if(namelist.contains(companyName)){
                    monitoring.get(i).setNo(no+"");
                }else{
                    no++;
                    namelist.add(companyName);
                    monitoring.get(i).setNo(no+"");

                }
            }
        }
        ObjectMapper objectMapper=new ObjectMapper();
        HashMap<String,Object> hs=new HashMap<>();
        hs.put("code","0");
        hs.put("msg","查询成功");
        hs.put("data",monitoring);
        return objectMapper.writeValueAsString(hs);
    }

    /**
     * 天眼查使用情况
     * @param rpVO
     * @return
     */
    public String getTycUse(ReportParameter rpVO) throws Exception{
        String companyName =null;
        if(rpVO.getCompanyName()!=null){
            companyName =rpVO.getCompanyName().replaceAll("RES_ENAIL(B.COMPANY_NAME)","RES_ENAIL(B.COMPANY_NAME)");
        }
        rpVO.setCompanyName(companyName);
        List<TycUse> tycUse = new ArrayList<>();
        //天眼查总关注
        Map<String,Object> tycZgz = reportUseMapper.getTycZgz(rpVO);
        Map<String,Object> tycZgzBld = reportUseMapper.getTycZgzBld(rpVO);
        //添加监控（不与时间联动）
        TycUse tjjk = new TycUse();
        tjjk.setModule("添加监控（不与时间联动）");
        tjjk.setSegmentModule("--");
        tjjk.setNum("12000家");
        Integer num = getInteger(tycZgz.get("NUM"));

        Integer bldNum = getInteger(tycZgzBld.get("NUM"));
        tjjk.setUserNum(num);
        Integer syNum = 12000-bldNum;
        tjjk.setSurplusNum(syNum+"");
        tycUse.add(tjjk);


        //模糊查询
        TycUse mhcx = new TycUse();
        mhcx.setModule("API接口");
        mhcx.setSegmentModule("模糊查询");
        mhcx.setNum("100万次");
        Map<String,Object> tycmhcx = reportUseMapper.getTycMhcx(rpVO);
        num = getInteger(tycmhcx.get("NUM"));
        mhcx.setUserNum(num);
        mhcx.setSurplusNum("--");
        tycUse.add(mhcx);

        //详情页-工商信息
        TycUse xqy = new TycUse();
        xqy.setModule("API接口");
        xqy.setSegmentModule("详情页-工商信息");
        xqy.setNum("100万次");
        xqy.setSurplusNum("--");
        Map<String,Object> tycBase = reportUseMapper.getTycBase(rpVO);
        num = getInteger(tycBase.get("NUM"));
        xqy.setUserNum(num);
        tycUse.add(xqy);



        //客商初筛-工商信息
        TycUse khFilter = new TycUse();
        khFilter.setModule("API接口");
        khFilter.setSegmentModule("客商初筛-工商信息");
        khFilter.setNum("100万次");
        khFilter.setSurplusNum("--");

        if(rpVO.getCompanyName()!=null){
            companyName =rpVO.getCompanyName().replaceAll("RES_ENAIL(B.COMPANY_NAME)","RES_ENAIL(B.COMPANY_NAME)");
        }
        rpVO.setCompanyName(companyName);

        Map<String,Object> tycFilter = reportUseMapper.getTycFilterCustomer(rpVO);
        num = getInteger(tycFilter.get("NUM"));
        khFilter.setUserNum(num);
        tycUse.add(khFilter);

        if(rpVO.getCompanyName()!=null){
            companyName =rpVO.getCompanyName().replaceAll("RES_ENAIL(B.COMPANY_NAME)","RES_ENAIL(B.COMPANY_NAME)");
        }

        rpVO.setCompanyName(companyName);



        //下发接口-工商信息
        TycUse xfItf = new TycUse();
        xfItf.setModule("API接口");
        xfItf.setSegmentModule("下发接口-工商信息");
        xfItf.setNum("100万次");
        xfItf.setSurplusNum("--");
        Map<String,Object> tycXf = reportUseMapper.getTycXf(rpVO);
        if(tycXf!=null){
            num = getInteger(tycXf.get("NUM"));
        }else{
            num=0;
        }
        xfItf.setUserNum(num);
        tycUse.add(xfItf);


        //专业版嵌入
        TycUse zybQr = new TycUse();
        zybQr.setModule("专业版嵌入");
        zybQr.setSegmentModule("--");
        zybQr.setNum("200并发");
        Map<String,Object> tycQr = reportUseMapper.getTycQr(rpVO);
        num = getInteger(tycQr.get("NUM"));
        zybQr.setUserNum(num);
        zybQr.setSurplusNum("不限次数，按年计费");
        tycUse.add(zybQr);

//        companyName =rpVO.getCompanyName().replaceAll("c.companyName","d.companyName");
//        rpVO.setCompanyName(companyName);
//        List<Map<String,Object>> tycFilterCustomerList = reportUseMapper.getCustomFilter(rpVO);



        ObjectMapper objectMapper=new ObjectMapper();
        HashMap<String,Object> hs=new HashMap<>();
        hs.put("code","0");
        hs.put("msg","查询成功");
        hs.put("data",tycUse);
//        hs.put("tycFilterCustomerList",tycFilterCustomerList);//客户初筛调用次数
        return objectMapper.writeValueAsString(hs);
    }

    /**
     * 客户初筛调用明细
     * @return
     * @throws Exception
     */
    public String getTycFilterCustomerList(ReportParameter rpVO) throws Exception{
        List<TycFilterCustomerList> tycFilterCustomerList = reportUseMapper.getTycFilterCustomerList(rpVO);

        ObjectMapper objectMapper=new ObjectMapper();
        HashMap<String,Object> hs=new HashMap<>();
        hs.put("code","0");
        hs.put("msg","查询成功");
        hs.put("data",tycFilterCustomerList);
        return objectMapper.writeValueAsString(hs);
    }

    /**
     * 	客商初筛调用次数
     * @param rpVO
     * @return
     */
    public String getCustomFilter(ReportParameter rpVO) throws Exception{

        List<Map<String,Object>> map = reportUseMapper.getCustomFilter(rpVO);
        ObjectMapper objectMapper=new ObjectMapper();
        HashMap<String,Object> hs=new HashMap<>();
        hs.put("code","0");
        hs.put("msg","查询成功");
        hs.put("data",map);
        return objectMapper.writeValueAsString(hs);
    }

    /**
     * 页面热度
     * @param rpVO
     * @return
     */
    public String getPageActive(ReportParameter rpVO) throws Exception{
        List<pageActive> pageActiveList = reportUseMapper.getPageActive(rpVO);
        List<String> listPageName = reportUseMapper.getPageActivePageDesc(rpVO);


        pageActiveList = sortByPageNameNum(pageActiveList,listPageName);



        //每一个页面排序




        //按公司名称排序
        if(pageActiveList!=null&&pageActiveList.size()>0){
            List<String> pageNames = new ArrayList<>();
            int no = 0;
            for (int i = 0; i < pageActiveList.size(); i++) {
                String pageName = pageActiveList.get(i).getPageName();
                if(pageNames.contains(pageName)){
                    pageActiveList.get(i).setNo(no+"");
                }else{
                    no++;
                    pageNames.add(pageName);
                    pageActiveList.get(i).setNo(no+"");

                }
            }
        }

        ObjectMapper objectMapper=new ObjectMapper();
        HashMap<String,Object> hs=new HashMap<>();
        hs.put("code","0");
        hs.put("msg","查询成功");
        hs.put("data",pageActiveList);
        return objectMapper.writeValueAsString(hs);
    }


    private List<pageActive> sortByPageNameNum(List<pageActive> pageActiveList,List<String> listPageName){
        List<pageActive> pageActiveList1 = new ArrayList<>();
        Map<String,List<pageActive>> pageNameMap = new HashMap<>();

        for (int i = 0; i < listPageName.size(); i++) {
            String pagename = listPageName.get(i)==null?"":listPageName.get(i).toString();
            for (int j = 0; j < pageActiveList.size(); j++) {
                String name = pageActiveList.get(j).getPageName()==null?"无":pageActiveList.get(j).getPageName().toString();
                if(pagename.equals(name)){
                    if(pageNameMap.containsKey(pagename)){
                        List<pageActive> l = pageNameMap.get(pagename);
                        l.add(pageActiveList.get(j));
                        pageNameMap.put(pagename,l);
                    }else{
                        List<pageActive> l = new ArrayList<>();
                        l.add(pageActiveList.get(j));
                        pageNameMap.put(pagename,l);
                    }
                }
            }
        }
        Iterator<String> keys = pageNameMap.keySet().iterator();
        while(keys.hasNext()){
            String pageName = keys.next();
            List<pageActive> list = pageNameMap.get(pageName);
            Collections.sort(list,new Comparator<pageActive>(){
                @Override
                public int compare(pageActive pa1,pageActive pa2){
                    if(pa1.getNum()>pa2.getNum()){
                        return -1;
                    }
                    if (pa1.getNum()==pa2.getNum()) {
                        return 0;
                    }
                    return 1;
                }
            });
        }
//        /**
//         *根据页面名称最终排序
//         */
        for (int i = 0; i < listPageName.size(); i++) {
            String pageName = listPageName.get(i);
            if(pageName==null||pageName.equals("")){
                pageName = "无";
            }
            List<pageActive> list = pageNameMap.get(pageName);
            pageActiveList1.addAll(list);
        }


        return  pageActiveList1;
    }

    private void sortMap(Map<String,Integer> map){
        List<Map.Entry<String,Integer>> lsitid = new ArrayList<Map.Entry<String,Integer>>(map.entrySet());

        Collections.sort(lsitid, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return (o2.getValue()-o1.getValue());
            }
        });
    }

    /**
     * 模糊查询
     * @param rpVO
     * @return
     */
    public String getLikeQuery(ReportParameter rpVO) throws Exception{
        List<LikeQuery> likeQuery = reportUseMapper.getLikeQuery(rpVO);
        ObjectMapper objectMapper=new ObjectMapper();
        HashMap<String,Object> hs=new HashMap<>();
        hs.put("code","0");
        hs.put("msg","查询成功");
        hs.put("data",likeQuery);
        return objectMapper.writeValueAsString(hs);
    }

}
