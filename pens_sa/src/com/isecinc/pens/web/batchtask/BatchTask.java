package com.isecinc.pens.web.batchtask;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.MonitorBean;
import com.isecinc.pens.bean.MonitorItemBean;
import com.isecinc.pens.process.SequenceProcessAll;
import com.pens.util.Constants;

public class BatchTask {
	public static Logger logger = Logger.getLogger("PENS");
	
	public static void insertMonitorItemColumnHeadTableResult(Connection conn,BigDecimal monitorItemId,String columnHead) throws Exception {
		PreparedStatement ps = null;
		try {
			logger.debug("insertMonitorItemResult mnitor_item_id:"+monitorItemId);
			String sql = "INSERT INTO PENSBI.MONITOR_ITEM_COLUMN_RESULT(MONITOR_ITEM_ID, COLUMN_HEAD)VALUES(?,?) ";
			//logger.info("SQL:"+sql);
			int index = 0;
			ps = conn.prepareStatement(sql);
			ps.setBigDecimal(++index, monitorItemId);
			ps.setString(++index,columnHead);
			int r = ps.executeUpdate();
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
		}
	}
	
	public static void insertMonitorItemResult(Connection conn,BigDecimal monitorItemId,int no,String status,String msg) throws Exception {
		PreparedStatement ps = null;
		try {
			//logger.debug("insertMonitorItemResult monitor_item_id:"+monitorItemId);
			String sql = "INSERT INTO PENSBI.MONITOR_ITEM_RESULT(MONITOR_ITEM_ID, STATUS, MESSAGE,NO)VALUES(?,?,?,?) ";
			//logger.info("SQL:"+sql);
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
	
	public static MonitorItemBean prepareMonitorItemBean(MonitorBean monitorModel) throws Exception{
		MonitorItemBean modelItem = new MonitorItemBean();
		modelItem.setMonitorId(monitorModel.getMonitorId());
		modelItem.setSource("FILE");
		modelItem.setDestination("ORACLE");
		modelItem.setStatus(Constants.STATUS_START);
		modelItem.setSubmitDate(new Date());
		modelItem.setId(new BigDecimal(SequenceProcessAll.getIns().getNextValue("monitor_item")));
		
		return modelItem;
	}

}
