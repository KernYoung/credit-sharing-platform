package com.fanruan.platform.service;

import com.fanruan.platform.bean.HrOrg;
import com.fanruan.platform.bean.HrZxbClient;
import com.fanruan.platform.bean.SpeedMapping;
import com.fanruan.platform.mapper.HrOrgMapper;
import com.fanruan.platform.mapper.HrZxbClientMapper;
import com.fanruan.platform.mapper.SpeedMappingMapper;
import com.fanruan.platform.util.DateUtil;
import com.fanruan.platform.util.ReturnJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class SpeedMappingService {
    @Autowired
    SpeedMappingMapper speedMappingMapper;

    @Autowired
    HrZxbClientMapper hrZxbClientMapper;

    @Autowired
    HrOrgMapper hrOrgMapper;


    public String saveSpeedMapping(SpeedMapping speedMapping) throws Exception{
       String msg = speedMapping.getId()==null?"保存成功":"修改成功";
        if(speedMapping.getId()==null||speedMapping.getId().equals("")){
            msg = "保存成功";
            onCheck(speedMapping);
            String id = UUID.randomUUID().toString();
            speedMapping.setId(id);
            speedMappingMapper.insert(speedMapping);
        }else{
            msg = "修改成功";
            onCheck(speedMapping);
            speedMappingMapper.updateByPrimaryKey(speedMapping);
        }
        return ReturnJson.getJson("0",msg,null);
    }

    public String getSpeedMapping(Map<String,Object> param) throws Exception{
        List<SpeedMapping> speedMappingList = speedMappingMapper.listByMap(param);
        return ReturnJson.getJson("0","查询工程",speedMappingList);
    }

    /**
     * 信保代码保存
     * @param param
     * @return
     * @throws Exception
     */
    public String saveXbMapping(Map<String,Object> param) throws Exception{
        HrZxbClient hrZxbClient = new HrZxbClient();
        hrZxbClient.setClientNo(param.get("clientNo")==null?"":param.get("clientNo").toString());
        hrZxbClient.setCode(param.get("code")==null?"":param.get("code").toString());
        if("".equals(hrZxbClient.getCode())||"".equals(hrZxbClient.getClientNo())){
            return ReturnJson.getJson("1","信保代码为空",null);
        }

        int size = hrZxbClientMapper.getClientCount(param);
        if(size>0){
            hrZxbClient.setUpdateBy(param.get("updateBy")==null?"":param.get("updateBy").toString());
            hrZxbClientMapper.updateByPrimaryKeySelective(hrZxbClient);
            return ReturnJson.getJson("0","保存成功",null);
        }else{
            hrZxbClient.setId(UUID.randomUUID().toString());
            hrZxbClient.setName(param.get("name")==null?"":param.get("name").toString());
            hrZxbClient.setCompanyType(param.get("companyType")==null?"":param.get("companyType").toString());
            hrZxbClient.setUpdateBy(param.get("updateBy")==null?"":param.get("updateBy").toString());
            int result=hrZxbClientMapper.insert(hrZxbClient);
            if(result==1){
                return ReturnJson.getJson("0","保存成功",null);
            }else{
                return ReturnJson.getJson("1","保存失败",null);
            }

        }

    }

    public String getXbMapping(Map<String,Object> param) throws Exception{
        int size = hrZxbClientMapper.getCount(param);
        List<HrZxbClient> hrZxbClientList = hrZxbClientMapper.listByMap(param);
//        int size = hrZxbClientList==null?0:hrZxbClientList.size();
        return ReturnJson.getJson1("0","查询工程",hrZxbClientList,size);
    }

    /**
     * 查询组织机构
     * @param param
     * @return
     * @throws Exception
     */
    public String getHrOrgEnable(Map<String,Object> param) throws Exception{
        //flag 0代表树查询，不显示启用  1可以查询未启用
        String sCode = param.get("code")==null?"":param.get("code").toString();
        HrOrg org = new HrOrg();
        if(sCode.equals("")){
            org.setCode(null);
        }else{
            org.setCode(sCode);
        }
        Map<String, Object> params = new HashMap<>();
        if(org.getCode()==null||org.getCode().equals("")){
            params.put("code","010");
        }else{
            params.put("code",org.getCode());
        }
        List<HrOrg> orgVOs = hrOrgMapper.listByMap1(params);
        if(orgVOs==null||orgVOs.size()==0){
            return ReturnJson.getJson("1","查询为空",null);
        }
        HrOrg org1 = new HrOrg();
        org1.setScode(org.getCode());
        List<HrOrg> hrOrgList = getTreeDataEnable(org1);
        return ReturnJson.getJson("0","保存成功",hrOrgList);
    }

    /**
     * 查询组织机构
     * @param param
     * @return
     * @throws Exception
     */
    public String getHrOrg(Map<String,Object> param) throws Exception{
        //flag 0代表树查询，不显示启用  1可以查询未启用
        String sCode = param.get("code")==null?"":param.get("code").toString();
        HrOrg org = new HrOrg();
        if(sCode.equals("")){
            org.setCode(null);
        }else{
            org.setCode(sCode);
        }
        Map<String, Object> params = new HashMap<>();
        if(org.getCode()==null||org.getCode().equals("")){
            params.put("code","010");
        }else{
            params.put("code",org.getCode());
        }
        List<HrOrg> orgVOs = hrOrgMapper.listByMap(params);
        if(orgVOs==null||orgVOs.size()==0){
            return ReturnJson.getJson("1","查询为空",null);
        }
        HrOrg org1 = new HrOrg();
        org1.setScode(org.getCode());
        List<HrOrg> hrOrgList = getTreeData(org1);
        return ReturnJson.getJson("0","保存成功",hrOrgList);
    }

    public List<HrOrg> getTreeDataEnable(HrOrg hrOrg){
        Map<String, Object> params = new HashMap<>();
        params.put("scode",hrOrg.getScode());
        //当前组
        List<HrOrg> hrOrgs = hrOrgMapper.listByMap1(params);
        if(hrOrgs!=null&&hrOrgs.size()>0){
            HrOrg hrOrg1 = new HrOrg();
            for (int i = 0; i < hrOrgs.size(); i++) {
                hrOrg1.setScode(hrOrgs.get(i).getCode());
                List<HrOrg> hrOrgs1 = getTreeDataEnable(hrOrg1);
                hrOrgs.get(i).setChildHrOrg(hrOrgs1);
            }
        }

        return hrOrgs;
    }


    public List<HrOrg> getTreeData(HrOrg hrOrg){
        Map<String, Object> params = new HashMap<>();
        params.put("scode",hrOrg.getScode());
        //当前组
        List<HrOrg> hrOrgs = hrOrgMapper.listByMap(params);
        if(hrOrgs!=null&&hrOrgs.size()>0){
            HrOrg hrOrg1 = new HrOrg();
            for (int i = 0; i < hrOrgs.size(); i++) {
                hrOrg1.setScode(hrOrgs.get(i).getCode());
                //List<HrOrg> hrOrgs1 = getTreeData(hrOrg1);
                //hrOrgs.get(i).setChildHrOrg(hrOrgs1);
            }
        }

        return hrOrgs;
    }



    /**
     * 组织架构保存
     * @param param
     * @return
     * @throws Exception
     */
    public String saveHrOrg(Map<String,Object> param) throws Exception{
        String orgType = param.get("orgType")==null?"":param.get("orgType").toString();
        if(orgType!=null&&!orgType.equals("手工")){
            return ReturnJson.getJson("0","来源为HR系统不能修改和增加",null);
        }
        HrOrg hrOrg = new HrOrg();
        hrOrg.setCode(param.get("code")==null?"":param.get("code").toString());
        hrOrg.setName(param.get("name")==null?"":param.get("name").toString());
        hrOrg.setDr(param.get("dr")==null?0:new Integer(param.get("dr").toString()));
        hrOrg.setEnableState(param.get("enableState")==null?0:new Integer(param.get("enableState").toString()));
        hrOrg.setPkOrg(param.get("pkOrg")==null?"":param.get("pkOrg").toString());
        hrOrg.setShortName(param.get("shortName")==null?"":param.get("shortName").toString());
        hrOrg.setScode(param.get("scode")==null?"":param.get("scode").toString());
        hrOrg.setSname(param.get("sname")==null?"":param.get("sname").toString());
        //String time = DateUtil.trans2StandardFormat(new Date());
        hrOrg.setUpdateTimeBy(param.get("updateTimeBy")== null?"":param.get("updateTimeBy").toString());
        hrOrg.setPkOrg(param.get("pkOrg")==null?"":param.get("pkOrg").toString());
        hrOrg.setRule(param.get("rule")==null?"":param.get("rule").toString());
        if(hrOrg.getPkOrg()==null||hrOrg.getPkOrg().equals("")){
            hrOrg.setPkOrg(UUID.randomUUID().toString());
            hrOrgMapper.insert(hrOrg);
            return ReturnJson.getJson("0","创建成功",null);
        }else if(hrOrg.getPkOrg()!=null&&!hrOrg.getPkOrg().equals("")&&hrOrg.getDr()==1){
            hrOrgMapper.deleteByPrimaryKey(hrOrg.getPkOrg());
            return ReturnJson.getJson("0","删除成功",null);
        }else{
            hrOrgMapper.updateByPrimaryKeySelective(hrOrg);
            return ReturnJson.getJson("0","保存成功",null);
        }


    }
    @Transactional(rollbackFor=Exception.class)
    public String uploadSpeedMapping(List<SpeedMapping> list) throws Exception{
        if(list!=null&&list.size()>0){
            speedMappingMapper.deleteAll();
            for (SpeedMapping speedMapping:
                    list) {
                speedMapping.setId(UUID.randomUUID().toString());
            }
            speedMappingMapper.insertBatch(list);
        }
        return null;
    }
    public void onCheck(SpeedMapping speedMapping) throws Exception{
        //校验是否有相同的国家和紧急程度
        String nation_name = speedMapping.getNationName();//国家
        String speed = speedMapping.getSpeed();
        Map<String, Object> params = new HashMap<>();
        params.put("nationName",speedMapping.getNationName());
        params.put("speed",speed);
        params.put("pageIndex","1");
        params.put("pageSize","10");
        List<SpeedMapping> speedMappingList = speedMappingMapper.listByMap(params);
        //判断如果是修改需要剔除自身id
        if(speedMapping.getId()==null||speedMapping.getId().equals("")){
            if(speedMappingList!=null&&speedMappingList.size()>0){
                throw new Exception(nation_name+"--"+speed+"  已存在!");
            }
        }else{
            for (SpeedMapping sspeedMapping:
                 speedMappingList ) {
                String Id = sspeedMapping.getId();
                if(!Id.equals(speedMapping.getId())){
                    throw new Exception(nation_name+"--"+speed+"  已存在!");
                }
            }
        }
    }
}
