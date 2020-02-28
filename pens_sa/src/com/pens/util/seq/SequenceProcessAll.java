package com.pens.util.seq;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.pens.util.DBConnection;

/**
 * SequenceProcessAll Class
 * 
 * @author Witty
 * @version $Id: SequenceProcessAll.java,v 1.0 18/07/2010 15:52:00 atiz.b Exp $
 * 
 */
public class SequenceProcessAll {

	private static Logger logger = Logger.getLogger("PENS");
    private  static SequenceProcessAll _sequenceProcessAll;
	
	public static  SequenceProcessAll getIns(){
	  if(_sequenceProcessAll ==null)
		  _sequenceProcessAll = new SequenceProcessAll();
	  return _sequenceProcessAll;
	}
	
	public BigDecimal getNextValue(String sequenceType) throws Exception {
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
	public BigDecimal getNextValue(Connection conn,String sequenceType) throws Exception {
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
	public BigDecimal getNextValueModel(Connection conn,String sequenceType) throws Exception {
		BigDecimal nextValue = new BigDecimal("0");
		Statement stmt = null;
		ResultSet rst = null;
		try {
			
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT seq  FROM PENSBI.PENSBME_C_SEQUENCE_ALL WHERE SEQUENCE_TYPE ='"+sequenceType+"'");
			stmt = conn.createStatement();
			logger.debug(sql.toString());
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				nextValue = rst.getBigDecimal("seq");
				
				//update nextValue
				stmt = conn.createStatement();
				stmt.executeUpdate("UPDATE PENSBI.PENSBME_C_SEQUENCE_ALL SET SEQ ="+(nextValue.add(new BigDecimal("1")))+" WHERE SEQUENCE_TYPE ='"+sequenceType+"'");
			} else{
				nextValue = new BigDecimal("1");
				//not found -> insert
				stmt = conn.createStatement();
				stmt.executeUpdate("INSERT INTO PENSBI.PENSBME_C_SEQUENCE_ALL(SEQUENCE_TYPE,SEQ)VALUES('"+sequenceType+"',"+(nextValue.add(new BigDecimal("1")))+")");
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
