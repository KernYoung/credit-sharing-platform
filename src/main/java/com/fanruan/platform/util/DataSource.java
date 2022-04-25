package com.fanruan.platform.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.pool.DruidDataSource;

import static com.fanruan.platform.util.JDBCUtil.selectSql;

/**
 * 
 * @author Administrator
 * 
 * 短信数据库-DataSource
 *
 */
public final class DataSource {

	private DruidDataSource dataSource;

	private DataSource() {
		dataSource = init();

	}

	public javax.sql.DataSource getDataSource() {
		return dataSource;
	}

	public void close() {
		dataSource.close();
	}

	private DruidDataSource init() {
		DruidDataSource druidDataSource = new DruidDataSource();
		druidDataSource.setDriverClassName("oracle.jdbc.OracleDriver");
		druidDataSource.setUsername("CREDITUSER");
		druidDataSource.setPassword("GMzx2021");
		druidDataSource.setUrl("jdbc:oracle:thin:@10.0.130.27:1521:orcl");
		// 初始化连接大小
		druidDataSource.setInitialSize(1);
		// 连接池最大使用连接数量
		druidDataSource.setMaxActive(20);
		// 连接池最大空闲
		// dataSource.setMaxIdle(5);
		// 连接池最小空闲
		druidDataSource.setMinIdle(1);
		druidDataSource.setMaxActive(200);
		// 获取连接最大等待时间
		druidDataSource.setMaxWait(5000);

		druidDataSource.setTestWhileIdle(true);

		// 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒 timeBetweenEvictionRunsMillis
		druidDataSource.setTimeBetweenEvictionRunsMillis(60000);
		// 配置一个连接在池中最小生存的时间，单位是毫秒 minEvictableIdleTimeMillis
		druidDataSource.setMinEvictableIdleTimeMillis(25200000);
		// 打开removeAbandoned功能
		druidDataSource.setRemoveAbandoned(true);
		druidDataSource.setRemoveAbandonedTimeout(1800);// 1800秒，也就是30分钟
		// 关闭abanded连接时输出错误日志
		druidDataSource.setLogAbandoned(false);
		druidDataSource.setTestOnBorrow(false);
		druidDataSource.setTestOnReturn(false);

		return druidDataSource;
	}

	public static DataSource getInstance() {
		return LazyHolder.INSTANCE;
	}

	private static class LazyHolder {
		private static final DataSource INSTANCE = new DataSource();
	}
	
	public static void main(String[] args) {

		List<Map<String,Object>> list = new ArrayList<>();

		list =  selectSql("SELECT * FROM PAFC_INPUT_COMPANY");
		int i = 1;
//		Connection conn = null;
//		PreparedStatement cstmt = null;
//		ResultSet rs = null;
//		String tmpSQL = null;
//		try {
//
//			tmpSQL = "SELECT * FROM PAFC_INPUT_COMPANY";
//			conn = DBConn.borrowConnection();
//			cstmt = conn.prepareStatement(tmpSQL);
//
//			rs = cstmt.executeQuery();
//			while(rs.next()) {
//				System.out.println(rs.getString("def1")+"\t"+rs.getString("code"));
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

	}
}

