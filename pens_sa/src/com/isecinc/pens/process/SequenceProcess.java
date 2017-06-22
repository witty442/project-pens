package com.isecinc.pens.process;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.isecinc.pens.report.salesanalyst.helper.DBConnection;

import util.DBCPConnectionProvider;

/**
 * SequenceProcess Class
 * 
 * @author Atiz.b
 * @version $Id: SequenceProcess.java,v 1.0 18/07/2010 15:52:00 atiz.b Exp $
 * 
 */
public class SequenceProcess {

	private static Logger logger = Logger.getLogger("PENS");

	
	public static Integer getNextValue(Connection conn,String tableName,String columnId) throws Exception {
		return getNextValueModel(conn, tableName, columnId);
	}
	
	public static Integer getNextValue(String tableName,String columnId) throws Exception {
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			return getNextValueModel(conn, tableName, columnId);
		}catch(Exception e){
			throw e;
		}finally{
			DBConnection.getInstance().closeConn(conn, null, null);
		}
	}
	/**
	 * Get Next Value
	 * 
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public static Integer getNextValueModel(Connection conn,String tableName,String columnId) throws Exception {
		Integer nextValue = 1;
		Statement stmt = null;
		ResultSet rst = null;
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT max("+columnId+")+1 as nextValue FROM "+tableName+" ");
			stmt = conn.createStatement();
			logger.debug(sql.toString());
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				nextValue = rst.getInt("nextValue");
			} 
		} catch (Exception e) {
			logger.debug(e.toString());
			e.printStackTrace();
		} finally {
			try {
				rst.close();
			} catch (Exception e2) {}
			try {
				stmt.close();
			} catch (Exception e2) {}
		}
		return nextValue;
	}
}
