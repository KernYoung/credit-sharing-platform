package com.fanruan.platform.service;

import com.fanruan.platform.bean.HrOrg;
import com.fanruan.platform.bean.HrZxbClient;
import com.fanruan.platform.bean.SpeedMapping;
import com.fanruan.platform.mapper.HrOrgMapper;
import com.fanruan.platform.mapper.HrZxbClientMapper;
import com.fanruan.platform.mapper.SpeedMappingMapper;
import com.fanruan.platform.util.ReturnJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        hrZxbClient.setCompanyType(param.get("companyType")==null?"":param.get("companyType").toString());
        hrZxbClientMapper.updateByPrimaryKeySelective(hrZxbClient);
        return ReturnJson.getJson("0","保存成功",null);
    }

    public String getXbMapping(Map<String,Object> param) throws Exception{
        List<HrZxbClient> hrZxbClientList = hrZxbClientMapper.listByMap(param);
        return ReturnJson.getJson("0","查询工程",hrZxbClientList);
    }

    /**
     * 查询组织机构
     * @param param
     * @return
     * @throws Exception
     */
    public String getHrOrg(Map<String,Object> param) throws Exception{
        List<HrOrg> hrOrgList = hrOrgMapper.listByMap(param);
        return ReturnJson.getJson("0","保存成功",hrOrgList);
    }

    public String getHrOrgLevel() throws Exception{
        List<HrOrg> treeData = new ArrayList<>();

        return null;
    }

    public void getTreeData(String orgCode,List<HrOrg> allHrOrg){
        //当前组织
        Map<String,Object> param = new HashMap<>();
        param.put("code",orgCode);
        List<HrOrg> hrOrgList = hrOrgMapper.listByMap(param);
        HrOrg hrOrg = hrOrgList.get(0);

    }



    /**
     * 组织架构保存
     * @param param
     * @return
     * @throws Exception
     */
    public String saveHrOrg(Map<String,Object> param) throws Exception{
        HrOrg hrOrg = new HrOrg();
        hrOrg.setCode(param.get("code")==null?"":param.get("code").toString());
        hrOrg.setName(param.get("name")==null?"":param.get("name").toString());
        hrOrg.setDr(param.get("dr")==null?0:new Integer(param.get("dr").toString()));
        hrOrg.setEnableState(param.get("enableState")==null?0:new Integer(param.get("enableState").toString()));
        hrOrg.setPkOrg(param.get("pkOrg")==null?"":param.get("pkOrg").toString());
        hrOrg.setShortName(param.get("shortName")==null?"":param.get("shortName").toString());
        hrOrg.setSCode(param.get("sCode")==null?"":param.get("sCode").toString());
        hrOrg.setSName(param.get("sName")==null?"":param.get("sName").toString());
        hrOrg.setUpdateTimeBy(param.get("updateTimeBy")==null?"":param.get("updateTimeBy").toString());
        hrOrg.setPkOrg(param.get("pkOrg")==null?"":param.get("pkOrg").toString());
        hrOrg.setRule(param.get("rule")==null?"":param.get("rule").toString());
        hrOrgMapper.updateByPrimaryKeySelective(hrOrg);
        return ReturnJson.getJson("0","保存成功",null);
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
