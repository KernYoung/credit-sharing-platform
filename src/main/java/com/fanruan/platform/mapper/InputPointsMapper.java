package com.fanruan.platform.mapper;

import com.fanruan.platform.bean.PAFCPoints;
import com.fanruan.platform.bean.PAFCVersion;

import java.util.List;
import java.util.Map;

public interface InputPointsMapper {

    public List<Map<String,Object>> getVersionPointsTYC(PAFCVersion pafcVersion);

    public List<Map<String,Object>> getVersionPointsZCX(PAFCVersion pafcVersion);

    public List<Map<String,Object>> getCompanyPoints(PAFCVersion pafcVersion);
}
