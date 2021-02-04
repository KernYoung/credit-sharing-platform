package com.fanruan.platform.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fanruan.platform.bean.*;
import com.fanruan.platform.constant.CommonUtils;
import com.fanruan.platform.dao.*;
import com.fanruan.platform.mapper.CommonMapper;
import com.fanruan.platform.mapper.CommonsMapper;
import com.fanruan.platform.mapper.CompanyReportMapper;
import com.fanruan.platform.mapper.PdfMapper;
import com.fanruan.platform.util.CommonUtil;
import com.fanruan.platform.util.DateUtil;
import com.fanruan.platform.util.MD5Util;
import com.fanruan.platform.util.StringUtil;
import com.google.common.collect.Lists;;
import com.google.common.collect.Maps;
import com.sinosure.exchange.edi.po.EdiFeedback;
import com.sinosure.exchange.edi.po.EntrustInput;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;

import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import javax.xml.bind.JAXBElement;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;


@Service
public class CompanyService {

    @Autowired
    private CompanyDao companyDao;

    @Autowired
    private CommonMapper commonMapper;

    @Autowired
    private CommonsMapper commonsMapper;

    @Autowired
    private SearchWordsDao searchWordsDao;

    @Autowired
    private ZhongXinBaoLogDao zhongXinBaoLogDao;

    @Autowired
    private ElasticSearchService elasticSearchService;

    @Autowired
    private CompanyExtendInfoDao companyExtendInfoDao;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RiskInfoDao riskInfoDao;

    @Autowired
    private ReportPushDao reportPushDao;

    @Autowired
    private NewCompanyDao newCompanyDao;

    @Autowired
    private CompanyReportMapper companyReportMapper;

    @Autowired
    TianYanChaConcernDao tianYanChaConcernDao;

    @Autowired
    private TianYanChaInfoDao tianYanChaInfoDao;

    @Autowired
    private UserDao userDao;
    @Autowired
    PdfMapper pdfMapper;
    @Autowired
    private ZhongChengXinConcernDao concernDao;

    @Autowired
    private ReportDao reportDao;

    @Autowired
    private BlackInfoDao blackInfoDao;

    @Autowired
    private CompanyIDVerificationDao companyIDVerificationDao;
    @Autowired
    private AreaDao areaDao;

    public List<RiskInfo> findLatestRisk(String riskSource) {
        Date date = DateUtils.addDays(new Date(), -7);
        List<RiskInfo> riskInfoList = null;
        if(StringUtils.isNotBlank(riskSource)){
            riskInfoList = riskInfoDao.findByPushTimeAfterAndRiskSourceOrderByPushTimeDesc(new Timestamp(date.getTime()), riskSource);
        }else {
            riskInfoList = riskInfoDao.findByPushTimeAfterOrderByPushTimeDesc(new Timestamp(date.getTime()));
        }
        return riskInfoList;
    }

    public List<Company> searchList(Integer userId, HashMap<String, Object> hs, String keyword, Integer page) {
        //001先走搜索引擎，看是否有返回结果
        if(StringUtils.isBlank(keyword)){
            return Collections.emptyList();
        }

        List<Company> storeCompanyList = elasticSearchService.QueryCompanyList(keyword,page);
        //002如果在有效期内有数据，则直接返回数据集，没有，调用模糊搜索接口
        if(CollectionUtils.isEmpty(storeCompanyList)){
            storeCompanyList = directSearchList(userId,keyword, page);
            hs.put("sourceType","模糊查询接口");
        }else {
            SearchWords searchWords = new SearchWords();
            searchWords.setKeyWord(keyword);
            searchWords.setUserId(userId);
            searchWords.setSearchTime(new Timestamp(System.currentTimeMillis()));
            searchWordsDao.saveAndFlush(searchWords);
            hs.put("sourceType","客商主档库");
        }
        //003如果调用了模糊搜索接口，数据分别查询es和oracle
        return storeCompanyList;
    }

//            "legal_person_desc": "法定代表人",
//              "ent_name_ws": "<em>康美</em>药业股份有限公司",
//              "legal_person": "马兴田",
//              "es_date": "1997-06-18T00:00:00.000Z",
//              "code": "91445200231131526C",
//              "reg_no": "440000000006711",
//              "ent_name": "康美药业股份有限公司"
    public List<Company> directSearchList(Integer userId,String keyword, Integer page)  {
        SearchWords searchWords = new SearchWords();
        searchWords.setUserId(userId);
        searchWords.setKeyWord(keyword);
        searchWords.setSearchTime(new Timestamp(System.currentTimeMillis()));
        searchWordsDao.saveAndFlush(searchWords);
        //001调用模糊搜索接口
        //002数据分别存es和oracle
//        if(page==null||page<0){
//            page=1;
//        }
////        List<Company> companyList = getCompaniesOld(keyword, page);
        List<Company> companyList = getCompanies(keyword);
        List<String> creditCodeList =  CommonUtils.getCreditCode(companyList);
        List<String> companyNames =  CommonUtils.getCompanyName(companyList);
        List<Company> companys = companyDao.findAllByCreditCodeIn(creditCodeList);
        List<String> creditCodeExists = CommonUtils.getCreditCode(companys);
        List<String> creditNameExists = CommonUtils.getCompanyName(companys);
        List<Company> needStore = Lists.newArrayList();
        for(Company company:companyList){
            if(StringUtils.isNotBlank(company.getCreditCode())&&!creditCodeExists.contains(company.getCreditCode())) {
                needStore.add(company);
            }else if(StringUtils.isBlank(company.getCreditCode())&&!creditNameExists.contains(company.getCompanyName())) {
                needStore.add(company);
            }
        }
        List<Company> storeCompanyList = companyDao.saveAll(needStore);
        Integer code = elasticSearchService.saveCompanyList(storeCompanyList);
        if(code>0){
            System.out.println("error elasticsearch");
        }
      // return companyDao.findAllByCreditCodeIn(creditCodeList);
       return companyDao.findAllByCompanyNameIn(companyNames);
    }


    public JSONObject getDirectSearch(String keyword){
        Map param = new HashMap();
        param.put("word", keyword);
        String dataStr = requestTianYanChaAPI(param,"/services/open/search/2.0?");
        return JSONObject.parseObject(dataStr);
    }


    private List<Company> getCompanies(String keyword) {

        Map param = new HashMap();
        param.put("word", keyword);
        String dataStr = requestTianYanChaAPI(param,"/services/open/search/2.0?");
        JSONObject jsonObject = JSONObject.parseObject(dataStr);
        Integer errorCode = jsonObject.getInteger("error_code");
        List<Company> companyList = Lists.newArrayList();
        if(errorCode==0){
            JSONObject result = jsonObject.getJSONObject("result");
            JSONArray items = result.getJSONArray("items");
            Integer total = result.getInteger("total");
            for(int i=0;i<items.size();i++){
                JSONObject item = items.getJSONObject(i);
                String legalPerson = item.getString("legalPersonName");
                String esDate = item.getString("estiblishTime");
                String regCode = item.getString("creditCode");
                Long id = item.getLong("id");
                String entName = StringUtil.getOrginName(item.getString("name"));
                String regCapital = item.getString("regCapital");
//                if(StringUtils.isBlank(regCode)){   // 取消过滤
//                    continue;
//                }
                Company company = new Company();
                company.setId(id);
                company.setRegistCapi(regCapital);
                company.setCreditCode(regCode);
                company.setOperName(legalPerson);
                company.setBuildDate(DateUtil.formatDate(esDate));
                company.setCompanyName(entName);
                company.setUpdateTime(new Timestamp(System.currentTimeMillis()));
                company.setTotal(total);
                companyList.add(company);
            }
        }
        return companyList;
    }

    private List<Company> getCompaniesOld(String keyword, Integer page) {
        String url = CommonUtil.URI+"/enterprise/query";
        Map param = new HashMap();
        param.put("page", page);
        param.put("keyword", keyword);
        JSONObject jsonObject = getRestApi(url, param);
        String message = jsonObject.getString("message");
        List<Company> companyList = Lists.newArrayList();
        if(StringUtils.equals(message,"SUCCESS")){
            JSONObject data = jsonObject.getJSONObject("data");
            Integer total = data.getInteger("total");
            JSONArray items = data.getJSONArray("items");
            for(int i=0;i<items.size();i++){
                JSONObject item = items.getJSONObject(i);
                String legalPerson = item.getString("legal_person");
                String esDate = item.getString("es_date");
                String regCode = item.getString("code");
                String entName = item.getString("ent_name");
                if(StringUtils.isBlank(regCode)){
                    continue;
                }
                Company company = new Company();
                company.setCreditCode(regCode);
                company.setOperName(legalPerson);
                company.setBuildDate(DateUtil.transform(esDate));
                company.setCompanyName(entName);
                company.setUpdateTime(new Timestamp(System.currentTimeMillis()));
                company.setTotal(total);
                companyList.add(company);
            }
        }
        return companyList;
    }

    private JSONObject getRestApi(String url, Map param) {

        String requestId = UUID.randomUUID().toString();
        Map body = fixParam(param, requestId);
        HttpHeaders headers = new  HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("account", CommonUtil.ACCOUNT);
        List<Charset> charsetList = Lists.newArrayList();
        charsetList.add(Charset.forName("UTF-8"));
        headers.setAcceptCharset(charsetList);
        HttpEntity httpEntity = new  HttpEntity(body, headers);

        return restTemplate.postForObject(url, httpEntity, JSONObject.class);
    }

    private InputStream getRestStream(String url, Map param) throws IOException {
        String requestId = UUID.randomUUID().toString();
//        String requestId = String.valueOf(new Date().getTime());
        Map body = fixParam(param, requestId);
        HttpHeaders headers = new  HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("account", CommonUtil.ACCOUNT);
        List<Charset> charsetList = Lists.newArrayList();
        charsetList.add(Charset.forName("UTF-8"));
        headers.setAcceptCharset(charsetList);
        HttpEntity httpEntity = new  HttpEntity(body, headers);
        ResponseEntity<Resource> responseEntity = restTemplate.postForEntity(url, httpEntity, Resource.class);
        return responseEntity.getBody().getInputStream();
    }

    private Map fixParam(Object obj, String time) {
        Map param = new HashMap<>();
        String paramStr = JSONObject.toJSONString(obj);
        param.put("requestId", time);
        param.put("param", paramStr);
        param.put("sign", MD5Util.MD5(param.get("param") + CommonUtil.APIKEY + time));
        return param;
    }

    public List<SearchWords> getLatestWords(Integer userId, int limit) {
        return companyReportMapper.getDistinctSearchWords(userId, limit);
    }
    public Company getCompanyById(Integer companyId){
        Optional<Company> companyOptional = companyDao.findById(companyId);
        return CommonUtils.getCompanyValue(companyOptional);
    }

    public List<CompanyExtendInfo> searchDetailList(Map<String,Object> param) {
        Integer companyId = CommonUtils.getIntegerValue(param.get("companyId")) ;
        if(companyId == null){
            return null;
        }
        List<CompanyExtendInfo> companyExtendInfoList = companyExtendInfoDao.findByCid(companyId);
        Optional<Company> companyOptional = companyDao.findById(companyId);
        Company company = CommonUtils.getCompanyValue(companyOptional);
//        JSONObject liteRating = getLiteRating(company, param);

        JSONObject queryAreaDetails = getQueryAreaDetails(param);
        String year = (String) param.get("year");
        String code = (String) param.get("code");
        JSONObject reportObject = getQueryReport(company,year,code);


        //detailList里面调各种接口，数据存到CompanyExtendInfo
        //如果没查询到，则调接口，然后存库
        if(CollectionUtils.isEmpty(companyExtendInfoList)){
            //公开财报查询
//            saveQueryInfo(companyId, company, reportObject,SourceConstant.QUERYREPORT);
//            //城投企业信用评价  这里条件很复杂
//            String cityUrl = CommonUtil.URI+"/cityInvRating";
//            Map cityParam = new HashMap();
////            cityParam.put("areaCode", "110100");
////            cityParam.put("level", 2);
//            JSONObject cityObject = getRestApi(cityUrl, cityParam);
//
//            //区域信用评价
//            String regionUrl = CommonUtil.URI+"/regionRating";
//            Map regionParam = new HashMap();
//
//            regionParam.put("ver", "1.0");
//            regionParam.put("type", company.getCreditCode());
//            regionParam.put("industry", "医药制造业");
//            Map cParam = new HashMap();
//            cParam.put("areaCode", "110100");
//            cParam.put("level", 2);
//            regionParam.put("T0", cParam);
//            JSONObject regionObject = getRestApi(regionUrl, regionParam);

        }

        buildExtendInfo(company,"reportObject", reportObject);
//        companyExtendInfoList.add(buildExtendInfo(company,"liteRating", liteRating));
        companyExtendInfoList.add(buildExtendInfo(company,"queryAreaDetails", queryAreaDetails));
        companyExtendInfoList.add( buildExtendInfo(company,"reportObject", reportObject));
//        JSON json = JSONSerializer.toJSON(liteRating.toJSONString());
//        XMLSerializer xmlSerializer = new XMLSerializer();
//        xmlSerializer.setTypeHintsEnabled( false );
//        xmlSerializer.setRootName("body" );
//        mXML = xmlSerializer.write( json );

        return companyExtendInfoList;
    }

    private CompanyExtendInfo buildExtendInfo(Company company,String flag, JSONObject jsonObject) {
        CompanyExtendInfo companyExtendInfo = new CompanyExtendInfo();
        companyExtendInfo.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        companyExtendInfo.setSourceType(flag);
        companyExtendInfo.setExtendInfo(jsonObject.toJSONString());
        companyExtendInfo.setCreditCode(company.getCreditCode());
        companyExtendInfo.setCompanyName(company.getCompanyName());
        return companyExtendInfo;
    }

    /**
     * {
     * "areaCode": "520000",
     * "level": "省级",
     * }
     * @param param
     * @return
     */
    public JSONObject getQueryAreaDetails( Map<String, Object> param) {
        String reportUrl = CommonUtil.URI+"/queryAreaDetails";
        Map reportParam = new HashMap();
        String areaCode = (String)param.get("areaCode");
        String level = (String)param.get("level");
        reportParam.put("areaCode",areaCode );
        reportParam.put("level", level);
        return getRestApi(reportUrl, reportParam);
    }

    private void saveQueryInfo(Integer companyId, Company company,JSONObject reportObject,String type) {
        String data = reportObject.getString("data");
        Integer code = reportObject.getInteger("code");
        if(code == 200){
            CompanyExtendInfo companyExtendInfo = new CompanyExtendInfo();
            companyExtendInfo.setCid(companyId);
            companyExtendInfo.setCompanyName(company.getCompanyName());
            companyExtendInfo.setCreditCode(company.getCreditCode());
            companyExtendInfo.setExtendInfo(data);
            companyExtendInfo.setSourceType(type);
            companyExtendInfo.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            companyExtendInfoDao.saveAndFlush(companyExtendInfo);
        }
    }

    private JSONObject getQueryReport(Company company, String year,String code) {
        String reportUrl = CommonUtil.URI+"/queryReport";

        if(StringUtils.isBlank(code)&&company!=null&&StringUtils.isNotBlank(company.getCreditCode())){
            code = company.getCreditCode();
        }
        if(StringUtils.isBlank(year)||StringUtils.isBlank(code)){
            return new JSONObject();
        }
        Map reportParam = new HashMap();
        reportParam.put("year", year);
        reportParam.put("code", code);
        return getRestApi(reportUrl, reportParam);
    }

    /**
     *    reportParam.put("code", "91445200231131526C");
     *         reportParam.put("year", "2018");
     *         JSONObject queryReport1 = getQueryReport(company, "2018", "91445200231131526C");
     *         JSONObject queryReport2 = getQueryReport(company, "2017", "91445200231131526C");
     *         reportParam.put("industry", "化学纤维制造业");
     *         reportParam.put("entName", "康美药业");
     *         reportParam.put("industryCategory", "制造业");
     *         reportParam.put("nature", "中央国有企业");
     *         reportParam.put("isIndustryLeader", "非行业头部企业");
     *         Map<String,Object> map = Maps.newHashMap();
     *         Map<String,Object> map1 = (Map<String,Object>)queryReport1.getJSONObject("data");
     *         Map<String,Object> map2 = (Map<String,Object>)queryReport2.getJSONObject("data");
     *         map.put("T0",map1);
     *         map.put("T1",map2);
     *         reportParam.put("financial", map);
     * @param company
     * @param param
     * @return
     */
    public JSONObject getLiteRating(Company company, Map<String, Object> param) {
        Map<String,Object> reportParam = new HashMap();
        String reportUrl = buildLiteRatingParam(company, param, reportParam);
        return getRestApi(reportUrl, reportParam);
    }

    private String buildLiteRatingParam(Company company, Map<String, Object> param, Map<String, Object> reportParam) {
        String reportUrl = CommonUtil.URI+"/html/liteRating";
        String code = (String) param.get("creditCode");
        String entName = (String) param.get("entName");
        if(company!=null){
            code = company.getCreditCode();
            reportParam.put("entName",company.getCompanyName());
            reportParam.put("code", company.getCreditCode());
        }else {
            reportParam.put("entName",entName);
            reportParam.put("code", code);
        }

        String year = (String)param.getOrDefault("year", CommonUtils.getLastYear());
        reportParam.put("year", year);
        String industry = (String)param.get("industry");
        String industryCategory = (String)param.get("industryCategory");
        Boolean isIndustryLeader = (Boolean)param.get("isIndustryLeader");
        String nature = (String)param.get("nature");
        if(StringUtils.isBlank(industry)||StringUtils.isBlank(industryCategory)
                ||isIndustryLeader==null||StringUtils.isBlank(nature)){
            return null;
        }
        reportParam.put("industry", industry);

        reportParam.put("industryCategory", industryCategory);

        reportParam.put("nature", nature);

        reportParam.put("isIndustryLeader", isIndustryLeader);
        JSONObject queryReport1 = getQueryReport(company,  CommonUtils.getLastYear(), code);
        JSONObject queryReport2 = getQueryReport(company,  CommonUtils.getLastSecondYear(), code);

        Map<String,Object> map = Maps.newHashMap();
        Map<String,Object> map1 = (Map<String,Object>)queryReport1.getJSONObject("data");
        Map<String,Object> map2 = (Map<String,Object>)queryReport2.getJSONObject("data");
        if(CollectionUtils.isEmpty(map1)||CollectionUtils.isEmpty(map2)){
            return null;
        }
        map.put("T0",map1);
        map.put("T1",map2);
        reportParam.put("financial", map);
        return reportUrl;
    }

    public void saveZhongXinBaoLog(User user, EdiFeedback feedback, EntrustInput entrustInput) {
        if(user==null){
           return ;
        }
        ZhongXinBaoLog log = new ZhongXinBaoLog();
        log.setCorpSerialNo(getValue(entrustInput.getCorpSerialNo()));
        log.setClientNo(getValue(entrustInput.getClientNo()));
        log.setReportbuyerNo(getValue(entrustInput.getReportbuyerNo()));
        log.setReportCorpCountryCode(getValue(entrustInput.getReportCorpCountryCode()));
        log.setReportCorpChnName(getValue(entrustInput.getReportCorpChnName()));
        log.setReportCorpEngName(getValue(entrustInput.getReportCorpEngName()));
        log.setReportCorpaddress(getValue(entrustInput.getReportCorpaddress()));
        log.setCreditno(getValue(entrustInput.getCreditno()));
        log.setIstranslation(getValue(entrustInput.getIstranslation()));
        log.setCorpSerialNoOut(getValue(entrustInput.getCorpSerialNo()));
        log.setApproveCode(getValue(feedback.getReturnCode()));
        log.setApproveMsg(getValue(feedback.getReturnMsg()));
        log.setClientNoOut(getValue(feedback.getClientNo()));
        log.setOtherMsg(getValue(feedback.getOtherMsg()));
        log.setUpdateBy(user.getUsername());
        log.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        zhongXinBaoLogDao.saveAndFlush(log);
    }

    public void saveZhongXinBaoLog(User user, EntrustInput entrustInput) {
        if(user==null){
            return ;
        }
        ZhongXinBaoLog log = new ZhongXinBaoLog();
        log.setCorpSerialNo(getValue(entrustInput.getCorpSerialNo()));
        log.setClientNo(getValue(entrustInput.getClientNo()));
        log.setReportbuyerNo(getValue(entrustInput.getReportbuyerNo()));
        log.setReportCorpCountryCode(getValue(entrustInput.getReportCorpCountryCode()));
        log.setReportCorpChnName(getValue(entrustInput.getReportCorpChnName()));
        log.setReportCorpEngName(getValue(entrustInput.getReportCorpEngName()));
        log.setReportCorpaddress(getValue(entrustInput.getReportCorpaddress()));
        log.setCreditno(getValue(entrustInput.getCreditno()));
        log.setIstranslation(getValue(entrustInput.getIstranslation()));
        log.setCorpSerialNoOut(getValue(entrustInput.getCorpSerialNo()));
        log.setUpdateBy(user.getUsername());
        log.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        log.setApproveCode("2");
        zhongXinBaoLogDao.saveAndFlush(log);
    }

    public void approveZhongXinBaoLog(User user, EdiFeedback feedback, EntrustInput entrustInput) {
        if(user==null){
            return ;
        }
        ZhongXinBaoLog log;
        Optional<ZhongXinBaoLog> optional = zhongXinBaoLogDao.findByCorpSerialNo(getValue(entrustInput.getCorpSerialNo()));
        if(optional.isPresent()){
            log = optional.get();
            log.setApproveCode(getValue(feedback.getReturnCode()));
            log.setApproveMsg(getValue(feedback.getReturnMsg()));
            log.setClientNoOut(getValue(feedback.getClientNo()));
            log.setOtherMsg(getValue(feedback.getOtherMsg()));
            log.setApproveby(user.getUsername());
            log.setApproveDate(new Timestamp(System.currentTimeMillis()));
            zhongXinBaoLogDao.saveAndFlush(log);
        }else {
            log = new ZhongXinBaoLog();
            return;
        }
    }

    public void insertOAMsg(String updateBy, String approveBy){
        commonsMapper.insertOAMsg(updateBy, approveBy);
    }


    public List<CompanyLevel> getAllCompanyLevel(String companyCode){
       return  commonsMapper.getAllCompanyLevel(companyCode);
    }
    public CompanyLevel getCompanyLevel(String companyCode){
        return  commonsMapper.getCompanyLevel(companyCode);
    }
    public void  getTreeData(String companyCode, List<CompanyLevel> allCompanyLevel){
        CompanyLevel companyLevelLord = this.getCompanyLevel(companyCode);
        List<CompanyLevel> allChildCompanyLevel = this.getAllCompanyLevel(companyCode);
        if(null == companyLevelLord || null == allChildCompanyLevel){
            return;
        }
        allCompanyLevel.add(companyLevelLord);
        for(CompanyLevel companyLevel:allChildCompanyLevel){
            getTreeData(companyLevel.getCode(),allCompanyLevel);
        }
    }

    public void saveZhongXinBaoLog(ZhongXinBaoLog log){
        zhongXinBaoLogDao.saveAndFlush(log);
    }

    private String getValue(JAXBElement<String> element) {
        if(element!=null){
            return element.getValue();
        }
        return null;
    }

    public void saveReportPushInfo(User user, EntrustInput entrustInput) {
        if(user==null){
            return ;
        }
        ReportPush push = new ReportPush();
        push.setUserId(user.getUserId());
        push.setCorpSerialNo(getValue(entrustInput.getCorpSerialNo()));
        push.setClientNo(getValue(entrustInput.getClientNo()));
        push.setEmailFlag(0);
        push.setPushFlag(0);
        reportPushDao.save(push);
    }

    public ReportPush getReportPushInfo(Integer userId, String clientNo) {
        List<ReportPush> reportPushList = reportPushDao.findAllByUserIdAndClientNo(userId, clientNo);
        if(!CollectionUtils.isEmpty(reportPushList)){
            ReportPush reportPush = reportPushList.get(0);
//            if(reportPush.getEmailFlag()>0){
//                return reportPush;
//            }
            return reportPush;
        }
        return null;
    }

    public boolean updateConcernInfo(Map<String, Object> param, Integer companyId, Integer userId, Integer tianyanchaFlag, Integer zhongchengxinFlag) {
        Optional<Company> companyOptional = companyDao.findById(companyId);
        Company company = CommonUtils.getCompanyValue(companyOptional);
        Optional<User> userOptional = userDao.findById(userId);
        User user = CommonUtils.getUserValue(userOptional);
        String requestId = CommonUtils.getRandomCode();
        saveTianYanChaConcern(tianyanchaFlag, company, user);
        saveZhongChengXinConcern(param, zhongchengxinFlag, company, user,requestId);

        Map<String, Object> xinTuo = new HashMap<>();
        xinTuo.put("userCode",user.getUsername());
        xinTuo.put("companyName", company.getCompanyName());
        if(null != tianyanchaFlag){
            xinTuo.put("tianyanchaflag",tianyanchaFlag);
        }
        if(null != zhongchengxinFlag){
            xinTuo.put("zhongchengxinflag",zhongchengxinFlag);
        }
        String entName = (String) param.get("entName");

        xinTuo.put("enttype", param.get("entType"));
        xinTuo.put("arealevel",param.get("areaLevel"));
        xinTuo.put("provincename",param.get("provinceName"));
        xinTuo.put("provincecode",param.get("provinceCode"));
        xinTuo.put("cityname", param.get("cityName"));
        xinTuo.put("citycode", param.get("cityCode"));
        xinTuo.put("countycode", param.get("countyCode"));
        xinTuo.put("countyName",param.get("countyName"));
        String xinTuoINfo = putXinTuo(xinTuo);
        System.out.println("---------------------------------------");
        System.out.println(xinTuoINfo);
        System.out.println("---------------------------------------");
        return true;
    }

    public String putXinTuo(Map<String,Object> param){
//        String xinTuo = "http://localhost:9001/api/common/concern";
        String xinTuo = "http://10.64.33.182:8088/tcmp/SynCompInfoServlet";
        HttpPost post = new HttpPost(xinTuo);
        HttpClient client = new DefaultHttpClient();
        StringEntity entity = new StringEntity(JSONObject.toJSONString(param), Consts.UTF_8);
        post.setEntity(entity);
        System.out.println(JSONObject.toJSONString(param));
        String dataStr = null;
        HttpResponse execute = null;
        try {
            execute = client.execute(post);
            dataStr = EntityUtils.toString(execute.getEntity(), Consts.UTF_8);
        }catch (IOException e){
            e.printStackTrace();
        }
        System.out.println(execute);
        return dataStr;
    }


    public boolean updateConcernInfo(Map<String, Object> param,Integer tianyanchaFlag, Integer zhongchengxinFlag,Company company, User user){
        Map<String, Object> zcxParam = new HashMap<>();
        String entName = (String) param.get("entName");
        String countyName = (String) param.get("countyName");
        String countyCode = (String) param.get("countyCode");
        String cityCode = (String) param.get("cityCode");
        String cityName = (String) param.get("cityName");
        String areaLevel = (String) param.get("areaLevel");
        String provinceCode = (String) param.get("provinceCode");
        String provinceName = (String) param.get("provinceName");
        Integer entType = CommonUtils.getIntegerValue(param.get("entType"));
        zcxParam.put("countyName", countyName);
        zcxParam.put("countyCode", countyCode);
        zcxParam.put("cityCode", cityCode);
        zcxParam.put("cityName", cityName);
        zcxParam.put("provinceCode",provinceCode);
        zcxParam.put("provinceName", provinceName);
        zcxParam.put("adminLevel", areaLevel);
        zcxParam.put("entType", entType);
        zcxParam.put("entName", entName);
        zcxParam.put("code", company.getCreditCode());

        String requestId = CommonUtils.getRandomCode();
        if(null == company.getCreditCode()){
            return false;
        }
        if(null != tianyanchaFlag){
            String tycStr = tycMonitoring(company.getCompanyName());
            JSONObject tycJson = JSONObject.parseObject(tycStr);
            if((null != tycJson && "ok".equals(tycJson.get("state"))) || 0 == tianyanchaFlag){
                saveTianYanChaConcern(tianyanchaFlag, company, user);
                return true;
            }
        }

        if(null != zhongchengxinFlag){
            String zcxStr = zcxMonitoring(zcxParam,requestId);
            JSONObject zcxJson = JSONObject.parseObject(zcxStr);
            if((null != zcxJson && (200 == CommonUtils.getIntegerValue(zcxJson.get("code")) || 3004 == CommonUtils.getIntegerValue(zcxJson.get("code")))) || 0 == zhongchengxinFlag){
                saveZhongChengXinConcern(param, zhongchengxinFlag, company, user,requestId);
                return true;
            }
        }
        return false;
    }


    private void saveTianYanChaConcern(Integer tianyanchaFlag, Company company, User user) {
        if(tianyanchaFlag!=null){
            TianYanChaConcern concern = new TianYanChaConcern();
            //Optional<TianYanChaConcern> byCode = tianYanChaConcernDao.findByCode(company.getCreditCode());
            String creditCode = company.getCreditCode();
            String updateby = user.getUsername();
            Optional<TianYanChaConcern> byCode = tianYanChaConcernDao.findByCodeAndUpdateby(creditCode,updateby);
            if(byCode.isPresent()){
                concern = byCode.get();
            }
            concern.setCode(company.getCreditCode());
            concern.setConcernFlag(String.valueOf(tianyanchaFlag));
            concern.setEntName(company.getCompanyName());
            concern.setUpdateby(user.getUsername());
            concern.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            tianYanChaConcernDao.saveAndFlush(concern);
        }
    }


    /**
     *  天眼查监控接口
     * @param domainNames   需要关注的公司信息
     * @return
     */
    private String tycMonitoring(String domainNames){
        String tycMonitoringUrl = "https://std.tianyancha.com/cloud-monitor/group/map.json";
        HttpPost post = new HttpPost(tycMonitoringUrl);
        HttpClient client = new DefaultHttpClient();
        List<NameValuePair> params=new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("_username",CommonUtil.TIAN_YAN_CHA_MONITORING_USERNAME));
        params.add(new BasicNameValuePair("_authId",CommonUtil.TIAN_YAN_CHA_MONITORING_AUTHID));
        params.add(new BasicNameValuePair("_sign",MD5Util.MD5(CommonUtil.TIAN_YAN_CHA_MONITORING_USERNAME+CommonUtil.TIAN_YAN_CHA_MONITORING_KEY)));
        params.add(new BasicNameValuePair("domainNames",domainNames));
        UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params, Consts.UTF_8);
        post.setEntity(urlEncodedFormEntity);
        String dataStr = null;
        HttpResponse execute = null;
        try {
            execute = client.execute(post);
            dataStr = EntityUtils.toString(execute.getEntity(), Consts.UTF_8);
        }catch (IOException e){
            e.printStackTrace();
        }
        return dataStr;
    }



    private boolean saveZhongChengXinConcern(Map<String, Object> param, Integer zhongchengxinFlag, Company company, User user,String requestId) {
        if(zhongchengxinFlag!=null){
            ZhongChengXinConcern zhongChengXinConcern = new ZhongChengXinConcern();
            String entType = (String) param.get("entType");
//            Optional<ZhongChengXinConcern> byCode = concernDao.findByCode(company.getCreditCode());
            String creditCode = company.getCreditCode();
            String updateby = user.getUsername();
            Optional<ZhongChengXinConcern> byCode = concernDao.findByCodeAndUpdateBy(creditCode,updateby);
            if(byCode.isPresent()){
                zhongChengXinConcern = byCode.get();
            }
            String entName = (String) param.get("entName");
            String countyName = (String) param.get("countyName");
            String countyCode = (String) param.get("countyCode");
            String cityCode = (String) param.get("cityCode");
            String cityName = (String) param.get("cityName");
            String areaLevel = (String) param.get("areaLevel");
            String provinceCode = (String) param.get("provinceCode");
            String provinceName = (String) param.get("provinceName");
            String updateBy = user.getUsername();
            zhongChengXinConcern.setConcernFlag(String.valueOf(zhongchengxinFlag));
            zhongChengXinConcern.setEntName(entName);
            zhongChengXinConcern.setCountyName(countyName);
            zhongChengXinConcern.setCountyCode(countyCode);
            zhongChengXinConcern.setCityCode(cityCode);
            zhongChengXinConcern.setCityName(cityName);
            zhongChengXinConcern.setProvinceCode(provinceCode);
            zhongChengXinConcern.setAreaLevel(areaLevel);
            zhongChengXinConcern.setProvinceName(provinceName);
            zhongChengXinConcern.setUpdateBy(updateBy);
            zhongChengXinConcern.setRequestId(requestId);
            zhongChengXinConcern.setCode(company.getCreditCode());
            zhongChengXinConcern.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            zhongChengXinConcern.setEntType(entType);
            concernDao.saveAndFlush(zhongChengXinConcern);
        }
        return false;
    }

    /**
     * 中诚信监控接口
     * @param param
     * @return
     */
    private String zcxMonitoring(Map<String, Object> param, String requestId){
        String zcxMonitoringUrl = "http://creditrisk.ccx.com.cn/open-api/addSupervisory";
        HttpPost post = new HttpPost(zcxMonitoringUrl);
        HttpClient client = new DefaultHttpClient();
        post.setHeader("account",CommonUtil.ZHONG_CHEN_XIN_MONITORING_ACOUNT);
        JSONObject body = new JSONObject();
        body.put("param",param);
        body.put("requestId",requestId);
        body.put("sign",MD5Util.MD5(JSONObject.toJSONString(param) + CommonUtil.ZHONG_CHEN_XIN_MONITORING_API_KEY+requestId));
        System.out.println(JSONObject.toJSONString(param));
        StringEntity entity = new StringEntity(body.toString(), Consts.UTF_8);
        post.setEntity(entity);
        String dataStr = null;
        HttpResponse execute = null;
        try {
            execute = client.execute(post);
            dataStr = EntityUtils.toString(execute.getEntity(), Consts.UTF_8);
        }catch (IOException e){
            e.printStackTrace();
        }
        return dataStr;
    }


    public ZhongXinBaoLog getCodeInfo(Integer userId, Integer companyId) {
        Optional<User> userOptional = userDao.findById(userId);
        User user = CommonUtils.getUserValue(userOptional);
        Optional<Company> companyOptional = companyDao.findById(companyId);
        Company company = CommonUtils.getCompanyValue(companyOptional);
        if(user==null||company==null||StringUtils.isBlank(user.getCompanyCode())||StringUtils.isBlank(company.getCreditCode())){
            return null;
        }
        return companyReportMapper.getReportbuyerNo(user.getCompanyCode(),company.getCreditCode());
    }

    public ZhongXinBaoLog getCodeInfo(Integer userId) {
        Optional<User> userOptional = userDao.findById(userId);
        User user = CommonUtils.getUserValue(userOptional);
        if(user==null||StringUtils.isBlank(user.getUsername())){
            return null;
        }
        return companyReportMapper.getReportbuyerNo2(user.getUsername());
    }

    public List<NewCompany> getNewCompany() {
        return newCompanyDao.findAll();
    }

    public InputStream getLiteRatingHtml( Company company,Map<String, Object> param) {

        Map<String,Object> reportParam = new HashMap();
        String reportUrl = buildLiteRatingParam(company, param, reportParam);
        if(StringUtils.isBlank(reportUrl)){
            return null;
        }
        try {
            return getRestStream(reportUrl, reportParam);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Optional<Company> findById(Integer companyId) {
        return companyDao.findById(companyId);
    }

    public Report saveReport(Company company, Map<String, Object> param, FileInputStream fileIs,String reportType) {
        Report report = new Report();
        String code = (String) param.get("creditCode");
        Integer userId = CommonUtils.getIntegerValue(param.get("userId"));
        String entName = (String) param.get("entName");
        report.setUpdateBy(String.valueOf(userId));
        String areaCode = (String) param.get("areaCode");
        String industry = (String) param.get("industry");
        String nature = (String) param.get("nature");
        Area area = new Area();
        if(company==null){
            report.setCompanyName(entName);
            report.setCreditCode(code);
        }else {
            report.setCompanyName(company.getCompanyName());
            report.setCreditCode(company.getCreditCode());
        }
        Optional<Area> byId = areaDao.findByAreaCode(areaCode);
        if(byId.isPresent()) {
            area = byId.get();
        }
        report.setArea(null == area.getAreaName() ? "/" : area.getAreaName());
        report.setIndustry(null == industry ? "/" : industry);
        report.setCompanyType(null == nature ? "/" : nature);
        report.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        String fileName = report.getCreditCode() + "_" + report.getUpdateTime().getTime()+".html";
        String pdfName = report.getCreditCode() + "_" + report.getUpdateTime().getTime()+".pdf";
        report.setPdfName(pdfName);
        report.setReportType(reportType);
        report.setFileName(fileName);
        try {
            if(fileIs==null){
                report.setReportHtml(null);
            }else {
                report.setReportHtml(IOUtils.toByteArray(fileIs));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reportDao.saveAndFlush(report);
    }

    public Report getReport( String fileName) {
        Optional<Report> byFileName = reportDao.findByFileName(fileName);
        if(byFileName.isPresent()){
            return byFileName.get();
        }
        return null;
    }

    public TianYanChaInfo getTianYanChaInfo(String companyName,Integer userId){
        TianYanChaInfo tianYanChaInfo = new TianYanChaInfo();
        Optional<TianYanChaInfo> byName = tianYanChaInfoDao.findByName(companyName);
        if(byName.isPresent()){
            tianYanChaInfo = byName.get();
            String updateTime = tianYanChaInfo.getUpdateTime();
            Date day = Calendar.getInstance().getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String format = sdf.format(day);
            if(StringUtils.isNotBlank(updateTime) && format.equals(updateTime.substring(0,10))){
                return tianYanChaInfo;
            }
        }
        Map<String, String> paramMap = Maps.newHashMap();
        paramMap.put("name",companyName);
        String dataStr = requestTianYanChaAPI(paramMap,"/services/open/cb/ic/2.0?");
        JSONObject jsonObject = JSONObject.parseObject(dataStr);

        JSONObject result = jsonObject.getJSONObject("result");
        if(result==null){
            return null;
        }
        String creditCode = result.getString("creditCode");
        String industry = result.getString("industry");
        String id = result.getString("id");
        String fromTime = result.getString("fromTime");
        String toTime = result.getString("toTime");
        String name = result.getString("name");
        String companyOrgType = result.getString("companyOrgType");
        String estiblishTime = result.getString("estiblishTime");
        String regLocation = result.getString("regLocation");
        String amomon = result.getString("regCapital");
        String legalPersonName = result.getString("legalPersonName");
        CompanyExtendInfo companyExtendInfo = new CompanyExtendInfo();
        companyExtendInfo.setCreditCode(creditCode);
        companyExtendInfo.setExtendInfo(dataStr);
        companyExtendInfo.setCompanyName(name);
        companyExtendInfo.setSourceType("tianyanchaSearch");
        companyExtendInfo.setUpdateby(userId);
        companyExtendInfo.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        companyExtendInfoDao.saveAndFlush(companyExtendInfo);

        tianYanChaInfo.setIndustry(industry);
        tianYanChaInfo.setId(id);
        tianYanChaInfo.setFromTime(fromTime);
        tianYanChaInfo.setToTime(toTime);
        tianYanChaInfo.setName(name);
        tianYanChaInfo.setCompanyorgtype(companyOrgType);
        tianYanChaInfo.setRegcapital(amomon);
        tianYanChaInfo.setEstiblishtime(estiblishTime);
        tianYanChaInfo.setReglocation(regLocation);
        tianYanChaInfo.setGsCreditcode(creditCode);
        tianYanChaInfo.setDsCreditcode(creditCode);
        tianYanChaInfo.setRegCredidtcode(creditCode);
        tianYanChaInfo.setRegtCredidtcode(creditCode);
        tianYanChaInfo.setCreditCode(creditCode);
        tianYanChaInfo.setLegalPersonName(legalPersonName);
        String format = DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss");
        tianYanChaInfo.setUpdateTime(format);
        return tianYanChaInfoDao.saveAndFlush(tianYanChaInfo);

    }

    public TianYanChaInfo getTianYanChaInfo(Integer companyId,Integer userId,String companyName) {
        Optional<Company> companyOptional = null;
        if(null == companyId){
            companyOptional = companyDao.findByCompanyName(companyName);
        }else{
            companyOptional = companyDao.findById(companyId);
        }
        Company company = CommonUtils.getCompanyValue(companyOptional);
        if(company==null||StringUtils.isBlank(company.getCreditCode())){
            return null;
        }
        TianYanChaInfo tianYanChaInfo = new TianYanChaInfo();
        Optional<TianYanChaInfo> tianYanChaInfoOptional = null;
        if(null == companyId){
            tianYanChaInfoOptional = tianYanChaInfoDao.findByName(company.getCompanyName());
        }else{
            tianYanChaInfoOptional = tianYanChaInfoDao.findByCreditCode(company.getCreditCode());
        }
        if(tianYanChaInfoOptional.isPresent()){
            tianYanChaInfo = tianYanChaInfoOptional.get();
            String updateTime = tianYanChaInfo.getUpdateTime();
            Date day = Calendar.getInstance().getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String format = sdf.format(day);
            if(StringUtils.isNotBlank(updateTime) && format.equals(updateTime.substring(0,10))){
                return tianYanChaInfo;
            }
        }
        Map<String, String> paramMap = Maps.newHashMap();
        paramMap.put("name",company.getCompanyName());
        String dataStr = requestTianYanChaAPI(paramMap,"/services/open/cb/ic/2.0?");
        JSONObject jsonObject = JSONObject.parseObject(dataStr);

        JSONObject result = jsonObject.getJSONObject("result");
        if(result==null){
            return null;
        }
        String creditCode = result.getString("creditCode");
        String industry = result.getString("industry");
        String id = result.getString("id");
        String fromTime = result.getString("fromTime");
        String toTime = result.getString("toTime");
        String name = result.getString("name");
        String companyOrgType = result.getString("companyOrgType");
        String estiblishTime = result.getString("estiblishTime");
        String regLocation = result.getString("regLocation");
        String amomon = result.getString("regCapital");
        String legalPersonName = result.getString("legalPersonName");
        CompanyExtendInfo companyExtendInfo = new CompanyExtendInfo();
        companyExtendInfo.setCreditCode(creditCode);
        companyExtendInfo.setExtendInfo(dataStr);
        companyExtendInfo.setCompanyName(name);
        companyExtendInfo.setSourceType("tianyanchaSearch");
        companyExtendInfo.setUpdateby(userId);
        companyExtendInfo.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        companyExtendInfoDao.saveAndFlush(companyExtendInfo);

        tianYanChaInfo.setIndustry(industry);
        tianYanChaInfo.setId(id);
        tianYanChaInfo.setFromTime(fromTime);
        tianYanChaInfo.setToTime(toTime);
        tianYanChaInfo.setName(name);
        tianYanChaInfo.setCompanyorgtype(companyOrgType);
        tianYanChaInfo.setRegcapital(amomon);
        tianYanChaInfo.setEstiblishtime(estiblishTime);
        tianYanChaInfo.setReglocation(regLocation);
        tianYanChaInfo.setGsCreditcode(creditCode);
        tianYanChaInfo.setDsCreditcode(creditCode);
        tianYanChaInfo.setRegCredidtcode(creditCode);
        tianYanChaInfo.setRegtCredidtcode(creditCode);
        tianYanChaInfo.setCreditCode(creditCode);
        tianYanChaInfo.setLegalPersonName(legalPersonName);
        String format = DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss");
        tianYanChaInfo.setUpdateTime(format);
        return tianYanChaInfoDao.saveAndFlush(tianYanChaInfo);
    }

    private String requestTianYanChaAPI(Map<String,String> paramMap,String uri) {
        String md5Hex = DigestUtils.md5Hex(CommonUtil.TIAN_YAN_CHA_USERNAME+CommonUtil.TIAN_YAN_CHA_KEY);
        StringBuffer sb = new StringBuffer(CommonUtil.TIAN_YAN_CHA+uri);
        sb.append("username="+CommonUtil.TIAN_YAN_CHA_USERNAME)
        .append("&authId="+CommonUtil.TIAN_YAN_CHA_AUTHID)
        .append("&sign="+md5Hex);
        for(Map.Entry<String,String> kv:paramMap.entrySet()){
            String key = kv.getKey();
            String value = kv.getValue();
            sb.append("&").append(key).append("=").append(value);
        }
//        .append("&name="+company.getCompanyName())
        HttpGet get = new HttpGet(sb.toString());
        get.setHeader("Authorization", CommonUtil.TIAN_YAN_CHA_AUTH);
        HttpClient client = new DefaultHttpClient();
        HttpResponse rese = null;
        String dataStr = null;
        try {
            rese = client.execute(get);
            dataStr = EntityUtils.toString(rese.getEntity());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataStr;
    }

    public List<Report> getReportList(Company company, String reportType) {
        if(company==null||StringUtils.isBlank(company.getCreditCode())){
            return null;
        }
        if(StringUtils.isNotBlank(reportType)){
            return reportDao.findAllByCreditCodeAndAndReportTypeOrderByUpdateTimeDesc(company.getCreditCode(), reportType);
        }else {
          //  return reportDao.findAllByCreditCodeOrderByUpdateTimeDesc(company.getCreditCode());
            return commonsMapper.getAllHistoricalReport(company.getCreditCode());
        }

    }

    public Company findCompanyByCode(String creditCode) {
        Optional<Company> byCreditCode = companyDao.findByCreditCode(creditCode);
        if(byCreditCode.isPresent()){
            return byCreditCode.get();
        }
        return null;
    }

    public InputStream getRiskScreenHtml(Company company, Map<String, Object> param) {
        Map<String,Object> reportParam = new HashMap();
        String reportUrl = buildRiskScreenHtmlParam(company, param, reportParam);
        if(StringUtils.isBlank(reportUrl)){
            return null;
        }
        try {
//            String url = CommonUtil.URI+"/riskScreen";
//            getRestApi(url,reportParam);
            return getRestStream(reportUrl, reportParam);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String buildRiskScreenHtmlParam(Company company, Map<String, Object> param, Map<String, Object> reportParam) {
        String reportUrl = CommonUtil.URI+"/html/riskScreen";
        String code = (String) param.get("creditCode");
        String  ver = (String) param.get("ver");
        if(company!=null){
            reportParam.put("code", company.getCreditCode());
        }else {
            reportParam.put("code", code);
        }
        if(StringUtils.isNotBlank(ver)){
            reportParam.put("ver", ver);
        }else {
            reportParam.put("ver", "1.2");
        }

        return reportUrl;
    }

    public InputStream getCityInvRatingHtml(Company company, Map<String, Object> param) {
        Map<String,Object> reportParam = new HashMap();
        String reportUrl = buildCityInvRatingHtmlParam(company, param, reportParam);
        if(StringUtils.isBlank(reportUrl)){
            return null;
        }
        try {
            return getRestStream(reportUrl, reportParam);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String buildCityInvRatingHtmlParam(Company company, Map<String, Object> param, Map<String, Object> reportParam) {
        String reportUrl = CommonUtil.URI+"/html/cityInvRating";
        String code = (String) param.get("creditCode");
        Boolean isImportant = (Boolean) param.get("isImportant");
        String ver = (String) param.get("ver");
        String type = (String) param.get("type");
        String areaCode = (String)param.get("areaCode");
        if(company!=null){
            code = company.getCreditCode();
            reportParam.put("code", company.getCreditCode());
        }else {
            reportParam.put("code", code);
        }
        reportParam.put("ver",ver);
        reportParam.put("type",type);
        reportParam.put("isImportant",isImportant);
        String year = (String)param.getOrDefault("year", CommonUtils.getLastYear());
        reportParam.put("year", year);
        JSONObject queryReport1 = getQueryReport(company,  CommonUtils.getLastYear(), code);
        JSONObject queryReport2 = getQueryReport(company,  CommonUtils.getLastSecondYear(), code);
        JSONObject queryAreaDetails = getQueryAreaDetails(param);
        JSONObject data = queryAreaDetails.getJSONObject("data");
        if(data==null){
            return null;
        }
        JSONArray year1 = data.getJSONArray("years");
        if(year1==null){
            return null;
        }
        List<String> list = JSONObject.parseArray(year1.toJSONString(), String.class);
        JSONObject values = data.getJSONObject("values");
        Map<String,Object> areaMap = Maps.newHashMap();
        if(list!=null&&list.contains(CommonUtils.getLastYear())){
            Map<String, Object> objMap1 =values.getJSONObject(CommonUtils.getLastYear());
            areaMap.put("T0",objMap1);
        }
        if(list!=null&&list.contains(CommonUtils.getLastSecondYear())){
            Map<String, Object> objMap2 =values.getJSONObject(CommonUtils.getLastSecondYear());
            areaMap.put("T1",objMap2);
        }
        Map<String,Object> map = Maps.newHashMap();
        Map<String,Object> map1 = (Map<String,Object>)queryReport1.getJSONObject("data");
        Map<String,Object> map2 = (Map<String,Object>)queryReport2.getJSONObject("data");
        if(CollectionUtils.isEmpty(map1)||CollectionUtils.isEmpty(map2)){
            return null;
        }
        map.put("T0",map1);
        map.put("T1",map2);
        reportParam.put("reportData", map);
        reportParam.put("regionData", areaMap);
        reportParam.put("regionCode", areaCode);
        return reportUrl;
    }

    public InputStream getRegionRatingHtml(Company company, Map<String, Object> param) {
        Map<String,Object> reportParam = new HashMap();
        String reportUrl = buildRegionRatingHtmlParam(company, param, reportParam);
        if(StringUtils.isBlank(reportUrl)){
            return null;
        }
        try {
            return getRestStream(reportUrl, reportParam);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String buildRegionRatingHtmlParam(Company company, Map<String, Object> param, Map<String, Object> reportParam) {
        String reportUrl = CommonUtil.URI+"/html/regionRating";
        String code = (String) param.get("creditCode");
        String industry = (String) param.get("industry");
        String ver = (String) param.get("ver");
        String type = (String) param.get("type");
        String year = (String) param.get("year");
        String areaCode = (String)param.get("areaCode");
        if(company!=null){
            reportParam.put("code", company.getCreditCode());
        }else {
            reportParam.put("code", code);
        }
        if(StringUtils.isBlank(year)){
            year = CommonUtils.getLastYear();
        }
        reportParam.put("ver",ver);
        reportParam.put("type",type);
        reportParam.put("year",year);
        reportParam.put("industry",industry);
        JSONObject queryAreaDetails = getQueryAreaDetails(param);
        JSONObject data = queryAreaDetails.getJSONObject("data");
        if(data==null){
            return null;
        }
        JSONArray year1 = data.getJSONArray("years");
        if(year1==null){
            return null;
        }
        List<String> list = JSONObject.parseArray(year1.toJSONString(), String.class);
        JSONObject values = data.getJSONObject("values");
        if(list!=null&&list.contains(CommonUtils.getLastYear())){
            Map<String, Object> objMap1 =values.getJSONObject(CommonUtils.getLastYear());
            reportParam.put("T0",objMap1);
        }
        if(list!=null&&list.contains(CommonUtils.getLastSecondYear())){
            Map<String, Object> objMap2 =values.getJSONObject(CommonUtils.getLastSecondYear());
            reportParam.put("T1",objMap2);
        }
        reportParam.put("regionCode", areaCode);
        return reportUrl;
    }

    public List<ZhongXinBaoShare> getShareInfo( Integer companyId) {
        Optional<Company> byCompanyId = companyDao.findByCompanyId(companyId);
        Company company = CommonUtils.getCompanyValue(byCompanyId);
        if(company==null||StringUtils.isBlank(company.getCompanyName())){
            return null;
        }
        return commonMapper.getZhongXinBaoShare(company.getCompanyName(),"","");
    }

    public ZhongXinBaoInfo getBusinessInfo(Integer companyId) {
        Optional<Company> byCompanyId = companyDao.findByCompanyId(companyId);
        Company company = CommonUtils.getCompanyValue(byCompanyId);
        if(company==null||StringUtils.isBlank(company.getCompanyName())){
            return null;
        }
        Optional<ZhongXinBaoInfo> zhongXinBaoInfo = commonMapper.getZhongXinBaoInfo(company.getCompanyName(),"","");
        if(zhongXinBaoInfo.isPresent()){
            return zhongXinBaoInfo.get();
        }
        return null;
    }

    public HashMap<String,Object> getBusinessInfo(String name,String engName,String reportbuyerno,HashMap<String,Object> hs) {
        Optional<ZhongXinBaoInfo> zhongXinBaoInfo = commonMapper.getZhongXinBaoInfo(name,engName,reportbuyerno);
        List<ZhongXinBaoShare> zhongXinBaoShare = commonMapper.getZhongXinBaoShare(name,engName,reportbuyerno);
        List<ZhongXinBaoPDF> zhongXinBaoPdf =  pdfMapper.selectZhongXinBaoPDF(name,engName,reportbuyerno);

        hs.put("businessInfo",zhongXinBaoInfo.get());
        hs.put("shareList",zhongXinBaoShare);
        hs.put("pdfList",zhongXinBaoPdf);
        hs.put("code","0");
        return hs;
    }

    public InputStream getFinancialDeminingHtml(Company company, Map<String, Object> param) {

        Map<String,Object> reportParam = new HashMap();
        String reportUrl = buildFinancialDeminingParam(company, param, reportParam);
        if(StringUtils.isBlank(reportUrl)){
            return null;
        }
        try {
            return getRestStream(reportUrl, reportParam);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Company getCompanyByName(String companyName){
        if(StringUtils.isBlank(companyName)){
            return null;
        }
        Optional<Company> companyInfo = companyDao.findTop1ByCompanyName(companyName);
        if(companyInfo.isPresent()){
            return companyInfo.get();
        }
        return null;
    }

    public ZhongXinBaoLog findByCorpSerialNo(String corpSerialNo){
        Optional<ZhongXinBaoLog> optional = zhongXinBaoLogDao.findByCorpSerialNo(corpSerialNo);
        if(optional.isPresent()){
            return optional.get();
        }
        return null;
    }

    private String buildFinancialDeminingParam(Company company, Map<String, Object> param, Map<String, Object> reportParam) {

        String reportUrl = CommonUtil.URI+"/html/financialDemining";
        String code = (String) param.get("creditCode");
        String industry = (String) param.get("industry");
        String ver = (String) param.get("ver");
        String year = (String) param.get("year");
        String nature = (String) param.get("nature");
        Double interestExpense = (Double) param.get("interestExpense");
        Double interestIncome = (Double) param.get("interestIncome");
        Double shPledgeRatio = (Double) param.get("shPledgeRatio");
        if(company!=null){
            code = company.getCreditCode();
            reportParam.put("code", company.getCreditCode());
        }else {
            reportParam.put("code", code);
        }
        if(StringUtils.isBlank(year)){
            year = CommonUtils.getLastYear();
        }
        reportParam.put("ver",ver);
        reportParam.put("year",year);
        reportParam.put("industry",industry);
        reportParam.put("nature",nature);
        reportParam.put("interestExpense",interestExpense);
        reportParam.put("interestIncome",interestIncome);
        reportParam.put("shPledgeRatio",shPledgeRatio);

        JSONObject queryReport1 = getQueryReport(company,  CommonUtils.getLastYear(), code);
        JSONObject queryReport2 = getQueryReport(company,  CommonUtils.getLastSecondYear(), code);
        JSONObject queryReport3 = getQueryReport(company,  CommonUtils.getLastThirdYear(), code);

        Map<String,Object> map1 = (Map<String,Object>)queryReport1.getJSONObject("data");
        Map<String,Object> map2 = (Map<String,Object>)queryReport2.getJSONObject("data");
        Map<String,Object> map3 = (Map<String,Object>)queryReport3.getJSONObject("data");
        if(CollectionUtils.isEmpty(map1)||CollectionUtils.isEmpty(map2)||CollectionUtils.isEmpty(map3)){
            return null;
        }
        List<Map<String,Object>> reportData = Lists.newArrayList();
        Map<String,Object> obj1 = Maps.newHashMap();
        obj1.put("year",CommonUtils.getLastYear());
        obj1.put("reportType",0);
        obj1.put("reportDataMap",map1);
        reportData.add(obj1);

        Map<String,Object> obj2 = Maps.newHashMap();
        obj2.put("year",CommonUtils.getLastSecondYear());
        obj2.put("reportType",0);
        obj2.put("reportDataMap",map2);
        reportData.add(obj2);

        Map<String,Object> obj3 = Maps.newHashMap();
        obj3.put("year",CommonUtils.getLastThirdYear());
        obj3.put("reportType",0);
        obj3.put("reportDataMap",map3);
        reportData.add(obj3);

        reportParam.put("reportData",reportData);
        return reportUrl;
    }

    /**
     * 根据公司工号查找对应公司工号校验规则
     * @param code   公司code
     * @return
     */
    public CompanyIDVerification getCompanyIDVerification(String code){
        return companyIDVerificationDao.findByCode(code);
    }

    /**
     *  获取公司状态
     * @param companyCode  公司code
     * @return 1 启用  0 没有启用
     */
    public String getCompanyStatus(String companyCode){
        return commonsMapper.getCompanyStatus(companyCode);
    }


    public List<CompayNameCode> getCompayNameAndCreditCode(){
        return commonsMapper.getCompayNameAndCreditCode();
    }

    /**
     *  关注下发接口使用
     * @param companyName
     * @param userId
     * @return
     */
    public  Company  creditCompany(String companyName,Integer userId){
        TianYanChaInfo tianYanChaInfo = getTianYanChaInfo(companyName, userId);
        if(null == tianYanChaInfo || null == tianYanChaInfo.getCreditCode()){
            return null;
        }
        Company company = new Company();
        company.setId(Long.parseLong(tianYanChaInfo.getId()));
        company.setCompanyName(companyName);
        company.setCreditCode(tianYanChaInfo.getCreditCode());
        company.setBuildDate(tianYanChaInfo.getEstiblishtime());
        company.setOperName(tianYanChaInfo.getLegalPersonName());
        company.setRegistCapi(tianYanChaInfo.getRegcapital());
        company.setRegistAddress(tianYanChaInfo.getReglocation());
        company.setEntType(tianYanChaInfo.getCompanyorgtype());
        company.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        companyDao.saveAndFlush(company);
        return company;
    }

    public List<String> getCompanyNameList(String userName){
      return commonsMapper.getCompayNameList(userName);
    }

    public List<String> getZCXCompayNameList(String userName){
        return commonsMapper.getZCXCompayNameList(userName);
    }

    public Optional<BlackInfo> getCompanyStatus(String compayName, String creditCode,String updateBy) {
        return blackInfoDao.findByEntNameAndCodeAndUpdateBy(compayName, creditCode,updateBy);
    }
}
