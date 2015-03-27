package com.isecinc.pens.process;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.isecinc.pens.inf.helper.Utils;

/**
 * SequenceProcess Class
 * 
 * @author Atiz.b
 * @version $Id: SequenceProcess.java,v 1.0 18/07/2010 15:52:00 atiz.b Exp $
 * 
 */
public class SequenceProcess {

	private static Logger logger = Logger.getLogger("PENS");
	protected static SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Utils.local_th);

	
	public static Integer getNextValue(Connection conn,String sequenceType,String code,Date orderDateObj) throws Exception {
		return getNextValueModel(conn, sequenceType, code,orderDateObj);
	}
	
	public static Integer getNextValue(Connection conn,String sequenceType) throws Exception {
		return getNextValueModel(conn, sequenceType);
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
			sql.append("SELECT seq  FROM PENSBME_C_SEQUENCE WHERE SEQUENCE_TYPE ='"+sequenceType+"' AND CODE ='"+code+"' AND MONTH='"+curMonth+"' AND YEAR='"+curYear+"'");
			stmt = conn.createStatement();
			logger.debug(sql.toString());
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				nextValue = rst.getInt("seq");
				
				//update nextValue
				stmt = conn.createStatement();
				stmt.executeUpdate("UPDATE PENSBME_C_SEQUENCE SET SEQ ="+(nextValue+1)+" WHERE SEQUENCE_TYPE ='"+sequenceType+"' AND CODE ='"+code+"' AND MONTH='"+curMonth+"' AND YEAR='"+curYear+"'");
			} else{
				nextValue = 1;
				//not found -> insert
				stmt = conn.createStatement();
				stmt.executeUpdate("INSERT INTO PENSBME_C_SEQUENCE(SEQUENCE_TYPE,MONTH,YEAR,CODE,SEQ)VALUES('"+sequenceType+"','"+curMonth+"','"+curYear+"','"+code+"',"+(nextValue+1)+")");
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
	
	public static Integer getNextValueModel(Connection conn,String sequenceType) throws Exception {
		Integer nextValue = 0;
		Statement stmt = null;
		ResultSet rst = null;
		try {
		
			
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT seq  FROM PENSBME_C_SEQUENCE_ALL WHERE SEQUENCE_TYPE ='"+sequenceType+"'");
			stmt = conn.createStatement();
			logger.debug(sql.toString());
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				nextValue = rst.getInt("seq");
				
				//update nextValue
				stmt = conn.createStatement();
				stmt.executeUpdate("UPDATE PENSBME_C_SEQUENCE_ALL SET SEQ ="+(nextValue+1)+" WHERE SEQUENCE_TYPE ='"+sequenceType+"'");
			} else{
				nextValue = 1;
				//not found -> insert
				stmt = conn.createStatement();
				stmt.executeUpdate("INSERT INTO PENSBME_C_SEQUENCE_ALL(SEQUENCE_TYPE,SEQ)VALUES('"+sequenceType+"',"+(nextValue+1)+")");
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
}
