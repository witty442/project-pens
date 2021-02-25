package com.pens.util.seq;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.Utils;

/**
 * SequenceProcessAll Class (PENSONLINE.C_SEQUENCE_ALL)
 * 
 * @author Witty
 * @version $Id: SequenceProcessAll.java,v 1.0 18/07/2012 15:52:00 Exp $
 * 
 */
public class SequenceProcessAll {

	private static Logger logger = Logger.getLogger("PENS");
	//protected static SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Utils.local_th);
    private  static SequenceProcessAll _sequenceProcessAll;
	
	public static  SequenceProcessAll getIns(){
	  if(_sequenceProcessAll ==null)
		  _sequenceProcessAll = new SequenceProcessAll();
	  return _sequenceProcessAll;
	}
	
	public synchronized BigDecimal getNextValueBySeq(String sequenceName) throws Exception {
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
	public synchronized BigDecimal getNextValueBySeqDBApp(String sequenceName) throws Exception {
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
	
	public synchronized BigDecimal getNextValue(String sequenceType) throws Exception {
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			return getNextValueModel(conn, sequenceType);
		}catch(Exception e){
			throw e;
		}finally{
			if(conn != null){
				conn.close();
			}
		}
		
	}
	public synchronized BigDecimal getNextValue(Connection conn,String sequenceType) throws Exception {
		return getNextValueModel(conn, sequenceType);
	}
	
	public synchronized Integer getNextValueInt(Connection conn,String sequenceType) throws Exception {
		return getNextValueModel(conn, sequenceType).intValue();
	}
	
	public synchronized Integer getNextValueInt(String sequenceType) throws Exception {
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			return getNextValueModel(conn, sequenceType).intValue();
		}catch(Exception e){
			throw e;
		}finally{
			if(conn != null){
				conn.close();
			}
		}
		
	}
	
	
	/**
	 * Get Next Value
	 * 
	 * @param sequenceType ="TableName" 
	 * @return
	 * @throws Exception
	 */
	public synchronized BigDecimal getNextValueModel(Connection conn,String sequenceType) throws Exception {
		BigDecimal nextValue = new BigDecimal("0");
		Statement stmt = null;
		ResultSet rst = null;
		try {
			
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT seq  FROM PENSONLINE.C_SEQUENCE_ALL WHERE SEQUENCE_TYPE ='"+sequenceType+"'");
			stmt = conn.createStatement();
			logger.debug(sql.toString());
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				nextValue = rst.getBigDecimal("seq");
				
				//update nextValue
				stmt = conn.createStatement();
				stmt.executeUpdate("UPDATE PENSONLINE.C_SEQUENCE_ALL SET SEQ ="+(nextValue.add(new BigDecimal("1")))+" WHERE SEQUENCE_TYPE ='"+sequenceType+"'");
			} else{
				nextValue = new BigDecimal("1");
				//not found -> insert
				stmt = conn.createStatement();
				stmt.executeUpdate("INSERT INTO PENSONLINE.C_SEQUENCE_ALL(SEQUENCE_TYPE,SEQ)VALUES('"+sequenceType+"',"+(nextValue.add(new BigDecimal("1")))+")");
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
	 * @param sequenceType
	 * @param dateObj (month,year)
	 * @return
	 * @throws Exception
	 */
	public synchronized BigDecimal getNextValue(String sequenceType,Date dateObj) throws Exception {
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			return getNextValueModel(conn, sequenceType,dateObj);
		}catch(Exception e){
			throw e;
		}finally{
			if(conn != null){
				conn.close();
			}
		}
	}
	
	/**
	 * 
	 * @param sequenceType
	 * @param dateObj (month,year)
	 * @return
	 * @throws Exception
	 */
	public synchronized BigDecimal getNextValue(Connection conn,String sequenceType,Date dateObj) throws Exception {
		return getNextValueModel(conn, sequenceType,dateObj);
	}
	/**
	 * 
	 * @param conn
	 * @param sequenceType
	 * @param dateObj (year,month)
	 * @return
	 * @throws Exception
	 */
	public static synchronized BigDecimal getNextValueModel(Connection conn,String sequenceType,Date dateObj) throws Exception {
		BigDecimal nextValue = new BigDecimal("0");
		Statement stmt = null;
		ResultSet rst = null;
		try {
			//yyyy/MM/dd
			String today = DateUtil.stringValue(dateObj, DateUtil.YYYY_MM_DD_WITH_SLASH,DateUtil.local_th) ;// df.format(dateObj);
			String[] d1 = today.split("/");
			String curYear = d1[0].substring(2,4);
			String curMonth = d1[1];
			logger.debug("curMonth["+curMonth+"]");
			logger.debug("curYear["+curYear+"]");
			
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT seq  FROM PENSONLINE.C_SEQUENCE_ALL WHERE SEQUENCE_TYPE ='"+sequenceType+"'  AND MONTH='"+curMonth+"' AND YEAR='"+curYear+"'");
			stmt = conn.createStatement();
			logger.debug(sql.toString());
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				nextValue = rst.getBigDecimal("seq");
				logger.debug("update PENSBME_C_SEQUENCE");
				//update nextValue
				stmt = conn.createStatement();
				stmt.executeUpdate("UPDATE PENSONLINE.C_SEQUENCE_ALL SET SEQ ="+(nextValue.add(new BigDecimal("1")))+" WHERE SEQUENCE_TYPE ='"+sequenceType+"' AND MONTH='"+curMonth+"' AND YEAR='"+curYear+"'");
			} else{
				logger.debug("Insert PENSBME_C_SEQUENCE");
				nextValue = new BigDecimal("1");
				//not found -> insert
				stmt = conn.createStatement();
				stmt.executeUpdate("INSERT INTO PENSONLINE.C_SEQUENCE_ALL(SEQUENCE_TYPE,MONTH,YEAR,SEQ)VALUES('"+sequenceType+"','"+curMonth+"','"+curYear+"',"+(nextValue.add(new BigDecimal("1")))+")");
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
	
}
