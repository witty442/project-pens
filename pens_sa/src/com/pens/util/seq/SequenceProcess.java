package com.pens.util.seq;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.pens.util.DBConnection;
import com.pens.util.Utils;

/**
 * SequenceProcess Class (PENSBI.PENSBME_C_SEQUENCE)
 * 
 * @author Witty
 * @version $Id: SequenceProcess.java,v 1.0 18/07/2010 15:52:00  Exp $
 * 
 */
public class SequenceProcess {

	private static Logger logger = Logger.getLogger("PENS");
	protected static SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Utils.local_th);

	public static Integer getNextValue(String sequenceType,String code,Date orderDateObj) throws Exception {
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			return getNextValueModel(conn, sequenceType, code,orderDateObj);
		}catch(Exception e){
			throw e;
		}finally{
			if(conn != null){
				conn.close();
			}
		}
		
	}
	public static Integer getNextValue(Connection conn,String sequenceType,String code,Date orderDateObj) throws Exception {
		return getNextValueModel(conn, sequenceType, code,orderDateObj);
	}
	
	public static Integer getNextValueByYear(Connection conn,String sequenceType,String code,Date orderDateObj) throws Exception {
		return getNextValueByYearModel(conn, sequenceType, code,orderDateObj);
	}
	
	
	/**
	 * Get Next Value
	 * 
	 * @param sequenceType ="orderNo" 
	 *        code = "StoreCode"
	 * @return
	 * @throws Exception
	 */
	public static Integer getNextValueModel(Connection conn,String sequenceType,String code,Date orderDateObj) throws Exception {
		Integer nextValue = 0;
		Statement stmt = null;
		ResultSet rst = null;
		try {
			String today = df.format(orderDateObj);
			String[] d1 = today.split("/");
			String curYear = d1[0].substring(2,4);
			String curMonth = d1[1];
			logger.debug("curMonth["+curMonth+"]");
			logger.debug("curYear["+curYear+"]");
			
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT seq  FROM PENSBI.PENSBME_C_SEQUENCE WHERE SEQUENCE_TYPE ='"+sequenceType+"' AND CODE ='"+code+"' AND MONTH='"+curMonth+"' AND YEAR='"+curYear+"'");
			stmt = conn.createStatement();
			logger.debug(sql.toString());
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				nextValue = rst.getInt("seq");
				logger.debug("update PENSBME_C_SEQUENCE");
				//update nextValue
				stmt = conn.createStatement();
				stmt.executeUpdate("UPDATE PENSBI.PENSBME_C_SEQUENCE SET SEQ ="+(nextValue+1)+" WHERE SEQUENCE_TYPE ='"+sequenceType+"' AND CODE ='"+code+"' AND MONTH='"+curMonth+"' AND YEAR='"+curYear+"'");
			} else{
				logger.debug("Insert PENSBME_C_SEQUENCE");
				nextValue = 1;
				//not found -> insert
				stmt = conn.createStatement();
				stmt.executeUpdate("INSERT INTO PENSBI.PENSBME_C_SEQUENCE(SEQUENCE_TYPE,MONTH,YEAR,CODE,SEQ)VALUES('"+sequenceType+"','"+curMonth+"','"+curYear+"','"+code+"',"+(nextValue+1)+")");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
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
	
	public static Integer getNextValueByYearModel(Connection conn,String sequenceType,String code,Date orderDateObj) throws Exception {
		Integer nextValue = 0;
		Statement stmt = null;
		ResultSet rst = null;
		try {
			String today = df.format(orderDateObj);
			String[] d1 = today.split("/");
			String curYear = d1[0].substring(2,4);
			String curMonth = "00";
			logger.debug("curYear["+curYear+"]");
			logger.debug("curMonth["+curMonth+"]");
			
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT seq  FROM PENSBI.PENSBME_C_SEQUENCE WHERE SEQUENCE_TYPE ='"+sequenceType+"' AND CODE ='"+code+"' AND MONTH='"+curMonth+"' AND YEAR='"+curYear+"'");
			stmt = conn.createStatement();
			logger.debug(sql.toString());
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				nextValue = rst.getInt("seq");
				
				//update nextValue
				stmt = conn.createStatement();
				stmt.executeUpdate("UPDATE PENSBI.PENSBME_C_SEQUENCE SET SEQ ="+(nextValue+1)+" WHERE SEQUENCE_TYPE ='"+sequenceType+"' AND CODE ='"+code+"' AND MONTH='"+curMonth+"' AND YEAR='"+curYear+"'");
			} else{
				//not found -> insert
				nextValue = 1;
				stmt = conn.createStatement();
				stmt.executeUpdate("INSERT INTO PENSBI.PENSBME_C_SEQUENCE(SEQUENCE_TYPE,MONTH,YEAR,CODE,SEQ)VALUES('"+sequenceType+"','"+curMonth+"','"+curYear+"','"+code+"',"+(nextValue+1)+")");
			}
		} catch (Exception e) {
			throw e;
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

	/**
	 * 
	 * @param conn
	 * @param tableName
	 * @param columnId
	 * @return
	 * @throws Exception
	 */
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
