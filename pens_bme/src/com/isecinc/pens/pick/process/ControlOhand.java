package com.isecinc.pens.pick.process;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.DBConnection;
import com.pens.util.Utils;
import com.pens.util.helper.SequenceProcess;

public class ControlOhand {
	protected static  Logger logger = Logger.getLogger("PENS");
	
	
    public static void startBalanceOnhand(HttpServletRequest request,User user) throws Exception {
		//canRun :Check Task Is Success
	}
    
    public static void endBalanceOnhand(HttpServletRequest request,User user) throws Exception {
		
	}
    
    public static void canBalanceOnhand(HttpServletRequest request,User user) throws Exception {
		
	}
	
	//wait
	public static ControlOnhandBean insertControlRunOnhand(ControlOnhandBean bean,User user) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DBConnection.getInstance().getConnectionApps();
			
			String sql = "INSERT INTO PENSBI.PENSBME_CONTROL_RUN_OHHAND(" +
			" TASK_ID," +
			" TASK_DATE," +
			" TASk_NAME ," +
			" STATUS ," +
			" create_date ," +
			" create_user )" +
			" VALUES (?,?,?,?,?,?) ";
			logger.debug("SQL:"+sql);
			
			bean.setTaskId(new BigDecimal(SequenceProcess.getNextValue("CONTROL_SAVE")));
			logger.debug("Save id:"+bean.getTaskId());

			int index = 0;
			ps = conn.prepareStatement(sql);
			ps.setBigDecimal(++index, bean.getTaskId());
			ps.setTimestamp(++index, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(++index, Utils.isNull(bean.getTaskName()));
			ps.setString(++index, Utils.isNull(bean.getStatus()));
			ps.setTimestamp(++index, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(++index, user.getName());
			
			int ch = ps.executeUpdate();
			logger.debug("ins:"+ch);
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
			if(conn !=null){
				conn.close();conn=null;
			}
		}
		return bean;
	}
	//Wait
	public static ControlOnhandBean updateControlRunOnhand(ControlOnhandBean bean,User user) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DBConnection.getInstance().getConnectionApps();
			
			String sql = "UPDATE PENSBI.PENSBME_CONTROL_RUN_OHHAND SET " +
			" STATUS =?," +
			" update_date =? ," +
			" update_user =? " +
			" WHERE TASK_ID = ? ";
			logger.debug("SQL:"+sql);
	
			int index = 0;
			ps = conn.prepareStatement(sql);
		
			ps.setString(++index, Utils.isNull(bean.getStatus()));
			ps.setTimestamp(++index, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(++index, user.getName());
			ps.setBigDecimal(++index, bean.getTaskId());
			
			int ch = ps.executeUpdate();
			logger.debug("ins:"+ch);
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
			if(conn !=null){
				conn.close();conn=null;
			}
		}
		return bean;
	}
}
