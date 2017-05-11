package com.isecinc.pens.scheduler.manager.objectGen;


import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.isecinc.pens.inf.helper.DBConnection;

public class ObjectGenerate {

	
	 /**
     * 
     * @param table 
     * 			ชื่อ Table
     * @param keyField
     * 			ชื่อ Column ที่เป็น PK
     * @return long
     * @throws Exception
     */
    public BigDecimal getNextSequenceId() throws Exception {
    	PreparedStatement ps = null;
        ResultSet rs = null;
    	Connection con = null;
    	BigDecimal id = new BigDecimal("0");
        try{
	      
	        con = DBConnection.getInstance().getConnection();
	        ps = con.prepareStatement("select max(no) as max_id from MONITOR_SCHEDULE "); 
	        rs = ps.executeQuery();
			if(rs.next()){
				BigDecimal bb = rs.getBigDecimal("max_id")==null?new BigDecimal("0"):rs.getBigDecimal("max_id");
				id = bb.add(new BigDecimal("1"));
			}
	        return id;
        }catch(Exception e){
            throw e;
        }finally{
        	if(ps != null){
        		ps.close();
        	}
        	if(rs != null){
        		rs.close();
        	}
        	if(con != null){
        		con.close();
        	}
        }
    }
}
