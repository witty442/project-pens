package com.isecinc.pens.inf.helper;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.pens.util.DBCPConnectionProvider;

/**
 * SequenceProcess Class
 * 
 * @author Atiz.b
 * @version $Id: SequenceProcess.java,v 1.0 18/07/2010 15:52:00 atiz.b Exp $
 * 
 */
public class SequenceHelper {

	private static Logger logger = Logger.getLogger("PENS");

	/**
	 * Get Next Value
	 * 
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public static Integer getNextValue(String tableName) throws Exception {
		Integer nextValue = 0;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		try {
			//conn = new DBCPConnectionProvider().getConnection(conn);
			conn = DBConnection.getInstance().getConnection();
			
			conn.setAutoCommit(false);
			StringBuilder sql = new StringBuilder();
			// Get Last Value
			sql.delete(0, sql.length());
			sql.append("SELECT NextValue FROM c_sequence ");
			sql.append("WHERE NAME = '" + tableName + "' ");
			stmt = conn.createStatement();
			//logger.debug(sql.toString());
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				nextValue = rst.getInt("nextValue");
			} else {
				sql.delete(0, sql.length());
				sql.append("INSERT INTO c_sequence(NAME,STARTNO,NEXTVALUE) ");
				sql.append("VALUES('" + tableName + "',1,1)");
				//logger.debug(sql.toString());
				stmt.execute(sql.toString());
				nextValue = 1;
			}
			// Update Value
			sql.delete(0, sql.length());
			sql.append("UPDATE c_sequence SET NextValue = NextValue + 1 ");
			sql.append("WHERE NAME = '" + tableName + "' ");
			//logger.debug(sql.toString());
			stmt.execute(sql.toString());
			// commit
			conn.commit();
		} catch (Exception e) {
			logger.debug(e.toString());
		} finally {
			try {
				rst.close();
			} catch (Exception e2) {}
			try {
				stmt.close();
			} catch (Exception e2) {}
			try {
				conn.setAutoCommit(true);
			} catch (Exception e) {}
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		return nextValue;
	}
}
