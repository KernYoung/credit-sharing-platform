package com.fanruan.platform.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * OA
 * 
 * @author Administrator
 * 
 */
public final class DBConn {

	private DBConn() {

	}

	public static Connection borrowConnection() throws SQLException {
		Connection conn = DataSource.getInstance().getDataSource().getConnection();
		// System.out.println("数据库连接>>>>>>>>>>>>>:" + conn);
		return conn;
	}

	public static void returnConnection(Connection conn) {
		closeResource(conn);
	}

	public static void closeResource(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException sqle) {
				// System.out.println("Warning! Failed to close ResultSet - " +
				// sqle.getMessage());
			}
		}
	}

	public static void closeResource(PreparedStatement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException sqle) {
				// System.out.println("Warning! Failed to close PreparedStatement - "
				// + sqle.getMessage());
			}
		}

	}

	public static void closeResource(Statement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
			} catch (SQLException sqle) {
				// System.out.println("Warning! Failed to close PreparedStatement - "
				// + sqle.getMessage());
			}
		}

	}

	public static void closeResource(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException sqle) {
				// System.out.println("Warning! Failed to close Connection - " +
				// sqle.getMessage());
			}
		}
	}

	public static void handleException(SQLException sqle) {
		sqle.printStackTrace();
	}

	// 关闭连接
	public static void closeConnection(Connection con) {
		try {
			if (con != null) {
				con.close();
			}
		} catch (SQLException e) {

		}
		con = null;
	}

	public static void closeStatement(Statement statement) {
		try {
			if (statement != null) {
				statement.close();
			}
		} catch (SQLException e) {
		}
		statement = null;
	}

	public static void closeResultSet(ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) {
		}
		rs = null;
	}
}