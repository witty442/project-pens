package com.isecinc.pens.web.batchtask.task;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.log4j.Logger;

public class BatchTask {
	public static Logger logger = Logger.getLogger("PENS");
	public static void insertMonitorItemResult(Connection conn,BigDecimal monitorItemId,int no,String status,String msg) throws Exception {
		PreparedStatement ps = null;
		try {
			String sql = "INSERT INTO MONITOR_ITEM_RESULT(MONITOR_ITEM_ID, STATUS, MESSAGE,NO)VALUES(?,?,?,?) ";
			logger.info("SQL:"+sql);
			int index = 0;
			ps = conn.prepareStatement(sql);
			ps.setBigDecimal(++index, monitorItemId);
			ps.setString(++index,status);
			ps.setString(++index,msg);
			ps.setInt(++index,no);
			
			int r = ps.executeUpdate();
			
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
		}
	}
}
