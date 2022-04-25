package com.fanruan.platform.service;


import com.fanruan.platform.bean.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


import com.fanruan.platform.constant.CommonUtils;
import com.fanruan.platform.util.DBConn;
import javafx.beans.property.StringProperty;
import org.springframework.stereotype.Service;


@Service
public class InputPointsService {

    public static PAFCVersion getVersionPointsTYC(PAFCVersion pafcVersion){
        Connection conn = null;
		PreparedStatement cstmt = null;
		ResultSet rs = null;
		String tmpSQL = null;
		try {
			tmpSQL = "select * from table(F_CREDIT_TYC_INPUT(to_date('"+pafcVersion.getStartDate()+"','yyyy-mm-dd'),to_date('"+pafcVersion.getEndDate()+"','yyyy-mm-dd')))";
			conn = DBConn.borrowConnection();
			cstmt = conn.prepareStatement(tmpSQL);

			rs = cstmt.executeQuery();
			while(rs.next()) {
				pafcVersion.setSupplierName(rs.getString("SUPPLIER_NAME"));
//				pafcVersion.setAttentionUsedTotalPoints(CommonUtils.getIntegerValue(rs.getString("ATTENTION_USED_TOTAL_POINTS")));
				pafcVersion.setInterfaceUsedTotalPoints(CommonUtils.getIntegerValue(rs.getString("INTERFACE_USED_TOTAL_POINTS")));
				pafcVersion.setAttentionUsedTotalPoints(0);
			}

		} catch (SQLException e) {
			e.printStackTrace();


		}finally {
			DBConn.closeResource(rs);
			DBConn.closeStatement(cstmt);
			DBConn.closeConnection(conn);

		}

        return pafcVersion;
    }

	public static PAFCVersion getVersionPointsZCX(PAFCVersion pafcVersion){
		Connection conn = null;
		PreparedStatement cstmt = null;
		ResultSet rs = null;
		String tmpSQL = null;
		try {
			tmpSQL = "select * from table(F_CREDIT_ZCX_INPUT(to_date('"+pafcVersion.getStartDate()+"','yyyy-mm-dd'),to_date('"+pafcVersion.getEndDate()+"','yyyy-mm-dd')))";
			conn = DBConn.borrowConnection();
			cstmt = conn.prepareStatement(tmpSQL);

			rs = cstmt.executeQuery();
			while(rs.next()) {
				pafcVersion.setSupplierName(rs.getString("SUPPLIER_NAME"));
//				pafcVersion.setAttentionUsedTotalPoints(CommonUtils.getIntegerValue(rs.getString("ATTENTION_USED_TOTAL_POINTS")));
				pafcVersion.setAttentionUsedTotalPoints(0);
				pafcVersion.setInterfaceUsedTotalPoints(CommonUtils.getIntegerValue(rs.getString("INTERFACE_USED_TOTAL_POINTS")));
			}

		} catch (SQLException e) {
			e.printStackTrace();


		}finally {
			DBConn.closeResource(rs);
			DBConn.closeStatement(cstmt);
			DBConn.closeConnection(conn);

		}

		return pafcVersion;
	}

	public static List<PAFCPoints> getCompanyPoints(PAFCVersion pafcVersion){
		Connection conn = null;
		PreparedStatement cstmt = null;
		ResultSet rs = null;
		String tmpSQL = null;
		List<PAFCPoints> pafcPointsList = new ArrayList<>();
		PAFCPoints pafcPoints;
		try {
			tmpSQL = "select * from table(F_CREDIT_FIRM_INPUT(to_date('"+pafcVersion.getStartDate()+"','yyyy-mm-dd'),to_date('"+pafcVersion.getEndDate()+"','yyyy-mm-dd'),'"+pafcVersion.getSupplierName()+"'))";
			conn = DBConn.borrowConnection();
			cstmt = conn.prepareStatement(tmpSQL);

			rs = cstmt.executeQuery();
			while(rs.next()) {
				pafcPoints = new PAFCPoints();
				pafcPoints.setCompanyName(rs.getString("COMPANY_NAME"));
				pafcPoints.setInterfaceUsedPoints(CommonUtils.getIntegerValue(rs.getString("INTERFACE_USED_POINTS")));
				pafcPoints.setAttentionUsedPoints(0);
//				if(pafcVersion.getSupplierName().equals("天眼查")){
//					pafcPoints.setAttentionUsedPoints(CommonUtils.getIntegerValue(rs.getString("ATTENTION_USED_POINTS")));
//				}else{
//					pafcPoints.setAttentionUsedPoints(0);
//				}
				pafcPointsList.add(pafcPoints);
			}

		} catch (SQLException e) {
			e.printStackTrace();


		}finally {
			DBConn.closeResource(rs);
			DBConn.closeStatement(cstmt);
			DBConn.closeConnection(conn);

		}

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
