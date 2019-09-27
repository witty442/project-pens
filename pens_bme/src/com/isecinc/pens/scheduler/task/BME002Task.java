package com.isecinc.pens.scheduler.task;


import java.sql.Connection;
import java.util.Date;

import org.apache.log4j.Logger;
import org.quartz.JobExecutionContext;

import com.isecinc.pens.scheduler.manager.ScheduleServiceManager;
import com.isecinc.pens.scheduler.manager.ScheduleVO;
import com.isecinc.pens.scheduler.manager.SchedulerConstant;
import com.isecinc.pens.scheduler.taskaction.BME002Action;
import com.pens.util.DBConnection;

public class BME002Task implements org.quartz.Job{
	
	protected static Logger logger = Logger.getLogger("PENS");
	
	 public void execute(JobExecutionContext jobExecutionContext) {
		 Connection conn = null;
		 ScheduleVO param = null;
		 try{
			 /** Get ScheduleVO From JonDataMap */
		    param = (ScheduleVO)jobExecutionContext.getJobDetail().getJobDataMap().get(SchedulerConstant.JOB_DATAMAP_PARAM);
		    conn = DBConnection.getInstance().getConnection();
		    
		    logger.debug("BME002Task"+":Process Task ...AT:"+new Date());
		    
		    /******** Action************/
		    BME002Action action = new BME002Action();
		    param = action.execute(conn,param);
		    /**********************************/
		    
		    logger.debug("*** Last Run Date:"+jobExecutionContext.getPreviousFireTime());
		    logger.debug("*** Next Run Date:"+jobExecutionContext.getNextFireTime());
		    
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
	     *  For update status in MONITOR_SCHEDULE
	  */
	 private void postExecute(Connection conn,ScheduleVO vo) {
		 try{
			 ScheduleServiceManager service  = new ScheduleServiceManager();
			 service.processAfterFinishTask(conn, vo);
		 }catch(Exception e){
			 
		 }
	 }
}
