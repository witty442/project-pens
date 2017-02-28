package com.isecinc.pens.scheduler.task;


import java.sql.Connection;
import java.util.Date;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;

import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.scheduler.manager.ScheduleServiceManager;
import com.isecinc.pens.scheduler.manager.ScheduleVO;
import com.isecinc.pens.scheduler.manager.SchedulerConstant;
import com.isecinc.pens.scheduler.taskaction.RecoveryScheduleAction;

public class RecoveryScheduleTask implements org.quartz.Job{
	
	private static Logger logger = Logger.getLogger(RecoveryScheduleTask.class);
	
	 public void execute(JobExecutionContext jobExecutionContext) {
		 Connection conn = null;
		 ScheduleVO param = null;
		 try{
		    param = (ScheduleVO)jobExecutionContext.getJobDetail().getJobDataMap().get(SchedulerConstant.JOB_DATAMAP_PARAM);
		    conn = DBConnection.getInstance().getConnection();
		  
		    logger.debug("RecoveryScheduleTask Process Task ...AT:"+new Date());
		    RecoveryScheduleAction action = new RecoveryScheduleAction();
		    
		    /** action job */
		    param = action.execute(conn,param);
		    
		    /** after Finish task */
		    param.setNextRunDate(jobExecutionContext.getNextFireTime());
		    param.setLastRunDate(jobExecutionContext.getPreviousFireTime());
		    postExecute(conn,param);
		    /**********************/
		    
		 }catch(Exception e){
			logger.error(e.getMessage(),e);
			param.setStatus(SchedulerConstant.STATUS_FAIL);
			postExecute(conn,param);
		 }finally{
			try{
			    if(conn != null){
			    	conn.close();conn = null;
			    }
			 }catch(Exception e){
				  
			  }
		  }
	  }
	 
	 /** After Finish Action Task  Check ActionTask is Success or Fail 
	     *  For update status in TCB_SCHEDULE_LOG
	  */
	 private void postExecute(Connection conn,ScheduleVO vo) {
		 try{
			 ScheduleServiceManager service  = new ScheduleServiceManager();
			 service.processAfterFinishTask(conn, vo);
		 }catch(Exception e){
			 
		 }
	 }
}
