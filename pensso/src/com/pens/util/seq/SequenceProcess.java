package com.pens.util.seq;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

import org.apache.log4j.Logger;

import com.isecinc.pens.inf.helper.DBConnection;
import com.pens.util.DBCPConnectionProvider;

/**
 * SequenceProcess Class
 * 
 * @author Atiz.b
 * @version $Id: SequenceProcess.java,v 1.0 18/07/2010 15:52:00 atiz.b Exp $
 * 
 */
public class SequenceProcess {

	private static Logger logger = Logger.getLogger("PENS");
    private  static SequenceProcess _sequenceProcess;
	
	public static  SequenceProcess getIns(){
	  if(_sequenceProcess ==null)
		  _sequenceProcess = new SequenceProcess();
	  return _sequenceProcess;
	}
	
	public static BigDecimal getNextValueBySeq(String sequenceName) throws Exception {
		BigDecimal nextValue = new BigDecimal("0");
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs =null;
		try{
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement("select ("+sequenceName+") as nextVal from dual");
			rs = ps.executeQuery();
			if(rs.next()){
				nextValue = new BigDecimal(rs.getString("nextVal"));
			}
			return nextValue;
		}catch(Exception e){
		   throw e;
		}finally{
			ps.close();
			rs.close();
		    conn.close();
		}
	}
	/**
	 * Get Next Value
	 * 
	 * @param code
	 * @return
	 * @throws Exception
	 */
	public static BigDecimal getNextValue(String tableName) throws Exception {
		BigDecimal nextValue = new BigDecimal("0");
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		try {
			conn = new DBCPConnectionProvider().getConnection(conn);
			conn.setAutoCommit(false);
			StringBuilder sql = new StringBuilder();
			// Get Last Value
			sql.delete(0, sql.length());
			sql.append("SELECT NextValue FROM c_sequence ");
			sql.append("WHERE NAME = '" + tableName + "' ");
			stmt = conn.createStatement();
			logger.debug(sql.toString());
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				nextValue = rst.getBigDecimal("nextValue");
			} else {
				sql.delete(0, sql.length());
				sql.append("INSERT INTO c_sequence(NAME,STARTNO,NEXTVALUE) ");
				sql.append("VALUES('" + tableName + "',1,1)");
				logger.debug(sql.toString());
				stmt.execute(sql.toString());
				nextValue = new BigDecimal("1");
			}
			// Update Value
			sql.delete(0, sql.length());
			sql.append("UPDATE c_sequence SET NextValue = NextValue + 1 ");
			sql.append("WHERE NAME = '" + tableName + "' ");
			logger.debug(sql.toString());
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
	
	public static Integer getNextValueInt(String tableName) throws Exception {
		Integer nextValue = 0;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		try {
			conn = new DBCPConnectionProvider().getConnection(conn);
			conn.setAutoCommit(false);
			StringBuilder sql = new StringBuilder();
			// Get Last Value
			sql.delete(0, sql.length());
			sql.append("SELECT NextValue FROM c_sequence ");
			sql.append("WHERE NAME = '" + tableName + "' ");
			stmt = conn.createStatement();
			logger.debug(sql.toString());
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				nextValue = rst.getInt("nextValue");
			} else {
				sql.delete(0, sql.length());
				sql.append("INSERT INTO c_sequence(NAME,STARTNO,NEXTVALUE) ");
				sql.append("VALUES('" + tableName + "',1,1)");
				logger.debug(sql.toString());
				stmt.execute(sql.toString());
				nextValue = 1;
			}
			// Update Value
			sql.delete(0, sql.length());
			sql.append("UPDATE c_sequence SET NextValue = NextValue + 1 ");
			sql.append("WHERE NAME = '" + tableName + "' ");
			logger.debug(sql.toString());
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
