package com.pens.util.seq;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Logger;

import com.pens.util.DBConnection;

/**
 * SequenceProcess Class
 * 
 * @author Atiz.b
 * @version $Id: SequenceProcess.java,v 1.0 18/07/2010 15:52:00 atiz.b Exp $
 * 
 */
public class SequenceProcessAll {

	private static Logger logger = Logger.getLogger("PENS");
	protected static SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Locale.US);

	public static Integer getNextValue(String sequenceType) throws Exception {
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
