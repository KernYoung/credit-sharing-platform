package com.fanruan.platform.service;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.fanruan.platform.bean.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;


import com.fanruan.platform.constant.CommonUtils;
import com.fanruan.platform.mapper.InputPointsMapper;
import com.fanruan.platform.util.DBConn;
import javafx.beans.property.StringProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class InputPointsService {

	@Autowired
	InputPointsMapper inputPointsMapper;
	@DS("uat")
    public  PAFCVersion getVersionPointsTYC(PAFCVersion pafcVersion){
		List<Map<String,Object>> list =inputPointsMapper.getVersionPointsTYC(pafcVersion);
		if(list!=null&&list.size()>0){
			Map<String,Object> map = list.get(0);
			pafcVersion.setSupplierName(map.get("SUPPLIER_NAME")==null?"":map.get("SUPPLIER_NAME").toString());
			pafcVersion.setInterfaceUsedTotalPoints(CommonUtils.getIntegerValue(map.get("INTERFACE_USED_TOTAL_POINTS")));
			pafcVersion.setAttentionUsedTotalPoints(0);
		}
//        Connection conn = null;
//		PreparedStatement cstmt = null;
//		ResultSet rs = null;
//		String tmpSQL = null;
//		try {
//			tmpSQL = "select * from table(F_CREDIT_TYC_INPUT(to_date('"+pafcVersion.getStartDate()+"','yyyy-mm-dd'),to_date('"+pafcVersion.getEndDate()+"','yyyy-mm-dd')))";
//			conn = DBConn.borrowConnection();
//			cstmt = conn.prepareStatement(tmpSQL);
//
//			rs = cstmt.executeQuery();
//			while(rs.next()) {
//				pafcVersion.setSupplierName(rs.getString("SUPPLIER_NAME"));
//				Object nn = CommonUtils.getIntegerValue(rs.getString("ATTENTION_USED_TOTAL_POINTS"));
////				pafcVersion.setAttentionUsedTotalPoints(CommonUtils.getIntegerValue(rs.getString("ATTENTION_USED_TOTAL_POINTS")));
//				pafcVersion.setInterfaceUsedTotalPoints(CommonUtils.getIntegerValue(rs.getString("INTERFACE_USED_TOTAL_POINTS")));
//				pafcVersion.setAttentionUsedTotalPoints(0);
//			}
//
//		} catch (SQLException e) {
//			e.printStackTrace();
//
//
//		}finally {
//			DBConn.closeResource(rs);
//			DBConn.closeStatement(cstmt);
//			DBConn.closeConnection(conn);
//
//		}

        return pafcVersion;
    }
	@DS("uat")
	public  PAFCVersion getVersionPointsZCX(PAFCVersion pafcVersion){
		List<Map<String,Object>> list =inputPointsMapper.getVersionPointsZCX(pafcVersion);
		if(list!=null&&list.size()>0){
			Map<String,Object> map = list.get(0);
			pafcVersion.setSupplierName(map.get("SUPPLIER_NAME")==null?"":map.get("SUPPLIER_NAME").toString());
			pafcVersion.setAttentionUsedTotalPoints(0);
			pafcVersion.setInterfaceUsedTotalPoints(CommonUtils.getIntegerValue(map.get("INTERFACE_USED_TOTAL_POINTS")));
		}

//		Connection conn = null;
//		PreparedStatement cstmt = null;
//		ResultSet rs = null;
//		String tmpSQL = null;
//		try {
//			tmpSQL = "select * from table(F_CREDIT_ZCX_INPUT(to_date('"+pafcVersion.getStartDate()+"','yyyy-mm-dd'),to_date('"+pafcVersion.getEndDate()+"','yyyy-mm-dd')))";
//			conn = DBConn.borrowConnection();
//			cstmt = conn.prepareStatement(tmpSQL);
//
//			rs = cstmt.executeQuery();
//			while(rs.next()) {
//				pafcVersion.setSupplierName(rs.getString("SUPPLIER_NAME"));
////				pafcVersion.setAttentionUsedTotalPoints(CommonUtils.getIntegerValue(rs.getString("ATTENTION_USED_TOTAL_POINTS")));
//				pafcVersion.setAttentionUsedTotalPoints(0);
//				pafcVersion.setInterfaceUsedTotalPoints(CommonUtils.getIntegerValue(rs.getString("INTERFACE_USED_TOTAL_POINTS")));
//			}
//
//		} catch (SQLException e) {
//			e.printStackTrace();
//
//
//		}finally {
//			DBConn.closeResource(rs);
//			DBConn.closeStatement(cstmt);
//			DBConn.closeConnection(conn);
//
//		}

		return pafcVersion;
	}
	@DS("uat")
	public  List<PAFCPoints> getCompanyPoints(PAFCVersion pafcVersion){
		List<Map<String,Object>> list =inputPointsMapper.getCompanyPoints(pafcVersion);
		List<PAFCPoints> pafcPointsList = new ArrayList<>();
		if(list!=null&&list.size()>0){

			PAFCPoints pafcPoints;
			for (int i = 0; i < list.size(); i++) {
				Map<String,Object> map = list.get(i);
				pafcPoints = new PAFCPoints();
				pafcPoints.setCompanyName(map.get("COMPANY_NAME")==null?"":map.get("COMPANY_NAME").toString());
				pafcPoints.setInterfaceUsedPoints(CommonUtils.getIntegerValue(map.get("INTERFACE_USED_POINTS")));
				pafcPoints.setAttentionUsedPoints(0);
//				if(pafcVersion.getSupplierName().equals("天眼查")){
//					pafcPoints.setAttentionUsedPoints(CommonUtils.getIntegerValue(rs.getString("ATTENTION_USED_POINTS")));
//				}else{
//					pafcPoints.setAttentionUsedPoints(0);
//				}
				pafcPointsList.add(pafcPoints);
			}
			return pafcPointsList;
		}


//		Connection conn = null;
//		PreparedStatement cstmt = null;
//		ResultSet rs = null;
//		String tmpSQL = null;
//		List<PAFCPoints> pafcPointsList = new ArrayList<>();
//		PAFCPoints pafcPoints;
//		try {
//			tmpSQL = "select * from table(F_CREDIT_FIRM_INPUT(to_date('"+pafcVersion.getStartDate()+"','yyyy-mm-dd'),to_date('"+pafcVersion.getEndDate()+"','yyyy-mm-dd'),'"+pafcVersion.getSupplierName()+"'))";
//			conn = DBConn.borrowConnection();
//			cstmt = conn.prepareStatement(tmpSQL);
//
//			rs = cstmt.executeQuery();
//			while(rs.next()) {
//				pafcPoints = new PAFCPoints();
//				pafcPoints.setCompanyName(rs.getString("COMPANY_NAME"));
//				pafcPoints.setInterfaceUsedPoints(CommonUtils.getIntegerValue(rs.getString("INTERFACE_USED_POINTS")));
//				pafcPoints.setAttentionUsedPoints(0);
////				if(pafcVersion.getSupplierName().equals("天眼查")){
////					pafcPoints.setAttentionUsedPoints(CommonUtils.getIntegerValue(rs.getString("ATTENTION_USED_POINTS")));
////				}else{
////					pafcPoints.setAttentionUsedPoints(0);
////				}
//				pafcPointsList.add(pafcPoints);
//			}
//
//		} catch (SQLException e) {
//			e.printStackTrace();
//
//
//		}finally {
//			DBConn.closeResource(rs);
//			DBConn.closeStatement(cstmt);
//			DBConn.closeConnection(conn);
//
//		}

		return pafcPointsList;
	}

    public  static  void main(String[] args){
//		PAFCVersion pafcVersionTYC= new PAFCVersion();
//		pafcVersionTYC.setStartDate("2021-11-01");
//		pafcVersionTYC.setEndDate("2022-03-16");
//		pafcVersionTYC = getVersionPointsTYC(pafcVersionTYC);
//        System.out.println(pafcVersionTYC.getSupplierName());

//		PAFCVersion pafcVersionZCX= new PAFCVersion();
//		pafcVersionZCX.setStartDate("2021-01-01");
//		pafcVersionZCX.setEndDate("2022-03-19");
//		pafcVersionZCX = getVersionPointsZCX(pafcVersionZCX);
//		System.out.println(pafcVersionZCX.getSupplierName());

//		PAFCVersion pafcVersionCompany= new PAFCVersion();
//		pafcVersionCompany.setStartDate("2021-11-01");
//		pafcVersionCompany.setEndDate("2022-03-16");
//		pafcVersionCompany.setSupplierName("天眼查");
//		List<PAFCPoints>  companyPointsList = new ArrayList<>();
//		companyPointsList = getCompanyPoints(pafcVersionCompany);
//		System.out.println(companyPointsList.size());

    }



}
