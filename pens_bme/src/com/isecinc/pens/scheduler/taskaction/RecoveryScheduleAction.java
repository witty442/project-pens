package com.isecinc.pens.scheduler.taskaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.scheduler.manager.ScheduleServiceManager;
import com.isecinc.pens.scheduler.manager.ScheduleVO;
import com.isecinc.pens.scheduler.manager.SchedulerConstant;


public class RecoveryScheduleAction {
	
	private static Logger logger = Logger.getLogger(RecoveryScheduleAction.class);
	public ScheduleVO execute(Connection conn,ScheduleVO vo) {
		try{
			reScheduleTask(conn);
			//reInitialJob(conn);
			vo.setStatus(SchedulerConstant.STATUS_COMPLETE);
		}catch(Exception e){
		   vo.setStatus(SchedulerConstant.STATUS_FAIL);
		   logger.error(e.getMessage(),e);
		}
		return vo; //success
	}
	
	private void  reScheduleTask(Connection conn) throws Exception{
		try{
			List dataList = getTaskForReschedule(conn);
			ScheduleServiceManager service = new  ScheduleServiceManager();
			if(dataList != null && dataList.size() > 0){
				for(int i=0 ;i<dataList.size();i++){
				   ScheduleVO paramItem =(ScheduleVO)dataList.get(i);
				   service.runJob(paramItem,conn);
				}
			}
		}catch(Exception e){
			throw e;
		}
	}
	
	private void  reInitialJob(Connection conn) throws Exception{
		try{
			List dataList = getReInitialJobList(conn);
			ScheduleServiceManager service = new  ScheduleServiceManager();
			if(dataList != null){
				for(int i=0 ;i<dataList.size();i++){
				   ScheduleVO paramItem =(ScheduleVO)dataList.get(i);
				  // service.runJob(paramItem);
				}
			}
		}catch(Exception e){
			throw e;
		}
	}
	
	/**
	 * getTaskForReschedule
	 * @param conn
	 * @return
	 * @throws Exception
	 * Desc : case Server down or stop 
	 *  select schedule Task for Reschedule (paramenter from Table)
	 */
	private  List getTaskForReschedule(Connection conn) throws Exception {
	    	PreparedStatement ps = null;
	    	//PreparedStatement psUpdate = null;
	    	ResultSet rs = null;
	    	List dataList = new ArrayList();
	    	try{
	    		StringBuffer sql =new StringBuffer("");
	    		sql.append(" SELECT * FROM TCB_SCHEDULE_LOG \n");
	    	    sql.append(" WHERE  1=1  \n");
	    	    sql.append(" AND TYPE IN('"+SchedulerConstant.SCHEDULE_TYPE_WEEKLY+"') \n");
	    	   // sql.append(" AND UPDATE_DATE >= sysdate-1 \n");
	    	   // sql.append(" AND UPDATE_DATE <= sysdate  \n");
	    	    
	    	    ps = conn.prepareStatement(sql.toString());
	    	    
	    	    //sql = new StringBuffer("update TCB_SCHEDULE_LOG set STATUS ='"+SchedulerConstant.STATUS_CANCEL+"' where NO = ? ");
	    	    //psUpdate = conn.prepareStatement(sql.toString());
	    	    
	    	    rs = ps.executeQuery();
	    		while(rs.next()){
	    			 ScheduleVO paramItem = new ScheduleVO();
	    			 paramItem.setGroupId(rs.getString("group_id"));
	    			 paramItem.setProgramId(rs.getString("program_id"));
	    			 paramItem.setJobId(rs.getString("job_id"));
	    			 paramItem.setProgramName(rs.getString("program_name"));
	    			 paramItem.setType(rs.getString("type"));
	    			 paramItem.setCrontriggerExp(rs.getString("crontrigger_exp"));
	    			 dataList.add(paramItem);
	    			 
	    			 //psUpdate.setBigDecimal(1,rs.getBigDecimal("NO"));
	    			 //psUpdate.addBatch();
	    		}
	    		
	    		//logger.debug("Result isClose:"+rs.isClosed());
	    		//psUpdate.executeBatch();
	    		return dataList;
	    	}catch(Exception e){
	    		throw e;
	    	}finally{
	    		if(ps != null){
	    		   ps.close();
	    		}
	    		//if(psUpdate != null){
		    	//	   psUpdate.close();
		    	//}
	    		if(rs != null){
	     		  // rs.close();
	     		}
	    	}
	    }
	
	private  List getReInitialJobList(Connection conn) throws Exception {
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	List dataList = new ArrayList();
    	try{
    		StringBuffer sql =new StringBuffer("");
    		sql.append(" SELECT * FROM TCB_JOB_INITIAL \n");
    	    sql.append(" WHERE 1=1 \n");
    	    sql.append(" AND VISIBLE = 'TRUE' \n");
    	    ps = conn.prepareStatement(sql.toString());
    	    
    	    rs = ps.executeQuery();
    		while(rs.next()){
    			 ScheduleVO paramItem = new ScheduleVO();
    			 paramItem.setGroupId(SchedulerConstant.GROUP_JOB_DEFAULT);
    			 paramItem.setProgramId(rs.getString("program_id"));
    			 paramItem.setJobId(rs.getString("job_id"));
    			 paramItem.setProgramName(rs.getString("program_name"));
    			 paramItem.setType(rs.getString("type"));
    			 paramItem.setDays(SchedulerConstant.JOB_DAYS_DEFAULT);
    			 paramItem.setStartDate(SchedulerConstant.JOB_START_DATE_DEFAULT);
    			 paramItem.setStartHour(SchedulerConstant.JOB_START_HOUR_DEFAULT);
    			 paramItem.setStartMinute(SchedulerConstant.JOB_START_HOUR_DEFAULT); 
    			 dataList.add(paramItem);
    		}
    		
    		return dataList;
    	}catch(Exception e){
    		throw e;
    	}finally{
    		if(ps != null){
    		   ps.close();
    		}
    		if(rs != null){
     		   //rs.close();
     		}
    	}
    }
}
