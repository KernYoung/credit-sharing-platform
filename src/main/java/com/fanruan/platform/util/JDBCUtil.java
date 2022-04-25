package com.fanruan.platform.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JDBCUtil {
   public static List<Map<String,Object>> selectSql(String tmpSQL){	   
	    Connection conn = null;
		PreparedStatement cstmt = null;
		ResultSet rs = null;
		
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		try {

			
			conn = DBConn.borrowConnection();
			cstmt = conn.prepareStatement(tmpSQL);
			
			rs = cstmt.executeQuery();
			
			ResultSetMetaData rsMetaData = rs.getMetaData();
			int count = rsMetaData.getColumnCount();
		    
			while(rs.next()) {
				Map<String,Object> map =new HashMap<>();
				for(int i = 1; i<=count; i++) {
			        map.put(rsMetaData.getColumnName(i), rs.getObject(rsMetaData.getColumnName(i)));
			    }
				result.add(map);
			}
		
		} catch (SQLException e) {
			e.printStackTrace();
		
			
		}finally {
			DBConn.closeResource(rs);
			DBConn.closeStatement(cstmt);
			DBConn.closeConnection(conn);
			
		}
		return result;
   }
   
   public static List<Map<String,Object>> selectSql2(String tmpSQL){	   
	    Connection conn = null;
		PreparedStatement cstmt = null;
		ResultSet rs = null;
		
		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
		try {

			conn = DBConn.borrowConnection();
			
			cstmt = conn.prepareStatement(tmpSQL);
			
			rs = cstmt.executeQuery();
			
			ResultSetMetaData rsMetaData = rs.getMetaData();
			int count = rsMetaData.getColumnCount();
		    
			while(rs.next()) {
				Map<String,Object> map =new HashMap<>();
				for(int i = 1; i<=count; i++) {
			        map.put(rsMetaData.getColumnLabel(i), rs.getObject(rsMetaData.getColumnLabel(i)));
			    }
				result.add(map);
			}
		
		} catch (SQLException e) {
			e.printStackTrace();
		
			
		}finally {
			DBConn.closeResource(rs);
			DBConn.closeStatement(cstmt);
			DBConn.closeConnection(conn);
			
		}
		return result;
  }
   
}
