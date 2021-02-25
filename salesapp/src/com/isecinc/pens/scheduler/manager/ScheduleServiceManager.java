package com.isecinc.pens.scheduler.manager;


import java.sql.Connection;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SchedulerMetaData;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;

import com.isecinc.pens.scheduler.manager.objectGen.ObjectIdGenerator;

public class ScheduleServiceManager {
	
	protected static Logger logger = Logger.getLogger("PENS");
	 
	 public ScheduleServiceManager(){
		 
	 }
	 /**
	  * RunJob By Type of Schedule
	  * @param scheduleVO
	  * @param conn
	  * @throws Exception
	  */
	 public void runJob(ScheduleVO scheduleVO,Connection conn) throws Exception{
		 Locale.setDefault(Locale.US);
		 Scheduler schedule = getScheduler();
		 ScheduleBean scheduleBean = null;
		 try{
			 
			 //choose Type for Schedule
			 if(SchedulerConstant.SCHEDULE_TYPE_NOW.equals(scheduleVO.getType())){
				 runTypeScheduleNow(conn,schedule,scheduleVO);
				 
			 }else  if(SchedulerConstant.SCHEDULE_TYPE_ONCE.equals(scheduleVO.getType())){
				 runTypeScheduleOnce(conn,schedule,scheduleVO);
				 
			 }else  if(SchedulerConstant.SCHEDULE_TYPE_DAILY.equals(scheduleVO.getType())){
				 runTypeScheduleCron(conn,schedule,scheduleVO);
				 
			 }else  if(SchedulerConstant.SCHEDULE_TYPE_WEEKLY.equals(scheduleVO.getType())){
				 runTypeScheduleCron(conn,schedule,scheduleVO);
				 
			 }else  if(SchedulerConstant.SCHEDULE_TYPE_MONTHLY.equals(scheduleVO.getType())){
				 runTypeScheduleCron(conn,schedule,scheduleVO);
				 
			 }
				
			 logger.debug("Type:"+scheduleVO.getType());
		 }catch(Exception e){
			 throw e;
		 }

	 } 
	 

	 public void deleteJob(ScheduleVO scheduleVO,Connection conn) throws Exception{
		 Locale.setDefault(Locale.US);
		 Scheduler schedule = getScheduler();
		 ScheduleBean scheduleBean = new ScheduleBean();
		 ScheduleServiceManagerHelper helper = new ScheduleServiceManagerHelper();
		 try{
			 ScheduleVO jobDBVO = helper.findBatchTaskByNo(conn, scheduleVO);
			 scheduleBean.setJobDetail(new ScheduleCreateJobManager().createJobDetail(jobDBVO));
		     
	    	 ScheduleVO jobExitsInQuartz = isJobExistQuartz(schedule, scheduleBean);
	    	 
	    	 /** JOB IS Exist IN TCB_MONITOR_SCHEDULE(TAB) */
	         if(jobDBVO != null && jobExitsInQuartz != null){
		    	/** delete Job in Quartz Ram */
		    	deleteJobInRamPool(schedule, scheduleBean);
		    	/** delete Job in TCB_MONITOR_SCHEDULE */
		    	deleteBatchTask(conn, jobDBVO);
	         }else if(jobDBVO != null && jobExitsInQuartz == null){
	        	/** delete Job in TCB_MONITOR_SCHEDULE */
		    	deleteBatchTask(conn, jobDBVO);
	         }else if(jobDBVO == null && jobExitsInQuartz != null){
	        	/** delete Job in Quartz Ram */
		    	deleteJobInRamPool(schedule, scheduleBean);
	         }
				
		 }catch(Exception e){
			 throw e;
		 }

	 } 
	 
	 public void clearAllJob(ScheduleVO scheduleVO,Connection conn) throws Exception{
		 Locale.setDefault(Locale.US);
		 Scheduler schedule = getScheduler();
		 ScheduleBean scheduleBean = new ScheduleBean();
		 ScheduleServiceManagerHelper helper = new ScheduleServiceManagerHelper();
		 try{
			 
				
		 }catch(Exception e){
			 throw e;
		 }

	 } 
	
	 public void processAfterFinishTask(Connection conn,ScheduleVO vo) {
		 ScheduleBean scheduleBean = new ScheduleBean();
		 try{
			  ScheduleServiceManagerHelper helper = new ScheduleServiceManagerHelper();
			  ScheduleVO jobDBVO = helper.findBatchTaskByNo(conn, vo);
			  scheduleBean.setJobDetail(new ScheduleCreateJobManager().createJobDetail(jobDBVO));
			  
			  /** create schedule */
			  Scheduler schedule = getScheduler();
			  
			 /** Case Schedule Once or Now after finish ,delete job in QuartzRAMPool*/
			  if(vo.getType().equals(SchedulerConstant.SCHEDULE_TYPE_ONCE) || vo.getType().equals(SchedulerConstant.SCHEDULE_TYPE_NOW)){
				  
		    	  ScheduleVO jobExitsInQuartz = isJobExistQuartz(schedule, scheduleBean);
		    	 
		    	 /** JOB IS Exist IN TCB_MONITOR_SCHEDULE(TAB) */
		         if(jobExitsInQuartz != null ){
			    	/** delete Job in Quartz RamPool */
			    	deleteJobInRamPool(schedule, scheduleBean);
		         }
			  }
			  
			  /** update status in TCB_MONITOR_SCHEDULE(TAB) */
			  updateStatusBatchTask(conn, vo);
			  
			  /** debug Task in Running in RAMPool */
			  debug(schedule);
			  
		 }catch(Exception e){
			 logger.error(e.getMessage(),e);
		 }
	 }
	 
	 public boolean runTypeScheduleNowRegen(Connection conn,ScheduleVO scheduleVO) throws Exception{
		 Scheduler schedule = getScheduler();
		 /** create schedule Bean (JobDetail,Trigger)  */
		 ScheduleBean scheduleBean = new ScheduleCreateJobManager().createScheduleBean(scheduleVO);
	     
		 logger.debug("schedule:"+schedule);
	     logger.debug("JobDetail:"+scheduleBean.getJobDetail());
	     logger.debug("Trigger:"+scheduleBean.getTrigger());
	         
		 scheduleVO.setNo(ObjectIdGenerator.getInstance().getNextSequenceId(conn));
		 schedule.scheduleJob(scheduleBean.getJobDetail(),scheduleBean.getTrigger());
		 //start create BatchTask 
		 createBatchTask(conn, scheduleVO);
		 return true;
	 }
	 
	 /**
	  * runTypeScheduleNow
	  * @param conn
	  * @param schedule
	  * @param scheduleBean
	  * @param scheduleVO
	  * @return
	  * @throws Exception
	  */
	 private boolean runTypeScheduleNow(Connection conn,Scheduler schedule,ScheduleVO scheduleVO) throws Exception{
		 /** create schedule Bean (JobDetail,Trigger)  */
		 ScheduleBean scheduleBean = new ScheduleCreateJobManager().createScheduleBean(scheduleVO);
	     
		 logger.debug("schedule:"+schedule);
	     logger.debug("JobDetail:"+scheduleBean.getJobDetail());
	     logger.debug("Trigger:"+scheduleBean.getTrigger());
	         
		 scheduleVO.setNo(ObjectIdGenerator.getInstance().getNextSequenceId(conn));
		 schedule.scheduleJob(scheduleBean.getJobDetail(),scheduleBean.getTrigger());
		 //start create BatchTask 
		 createBatchTask(conn, scheduleVO);
		 return true;
	 }
	 
	/**
	 * runTypeScheduleOnce
	 * @param conn
	 * @param schedule
	 * @param scheduleBean
	 * @param scheduleVO
	 * @return
	 * @throws Exception
	 */
    private boolean runTypeScheduleOnce(Connection conn,Scheduler schedule,ScheduleVO scheduleVO) throws Exception{
        
       //Create JobDetail and Trigger
         ScheduleBean scheduleBean = new ScheduleCreateJobManager().createScheduleBean(scheduleVO);
         logger.debug("schedule:"+schedule);
         logger.debug("JobDetail:"+scheduleBean.getJobDetail());
         logger.debug("Trigger:"+scheduleBean.getTrigger());
         
         //ScheduleVO jobExitsInQuartz = isJobExistQuartz(schedule, scheduleBean);
         //logger.debug("exist :"+jobExitsInQuartz.getProgramId());
         
         scheduleVO.setNo(ObjectIdGenerator.getInstance().getNextSequenceId(conn));
		 schedule.scheduleJob(scheduleBean.getJobDetail(),scheduleBean.getTrigger());
		 //start create BatchTask 
		 createBatchTask(conn, scheduleVO);
		 
		 return true;
	 }
     
   /**
    * runTypeScheduleWeekly
    * @param conn
    * @param schedule
    * @param scheduleBean
    * @param scheduleVO
    * @return
    * @throws Exception
    */
    private boolean runTypeScheduleCron(Connection conn,Scheduler schedule,ScheduleVO scheduleVO) throws Exception{
    	try{
    		//Create JobDetail and Trigger
            ScheduleBean scheduleBean = new ScheduleCreateJobManager().createScheduleBean(scheduleVO);
            
	    	/** check job isExist IN MONITOR_SCHEDULE */
	    	ScheduleServiceManagerHelper helper = new ScheduleServiceManagerHelper();
	    	ScheduleVO jobDBVO = helper.findLastRunDateByProgramId(conn, scheduleVO); 
	    	
	    	/** JOB IS Exist IN MONITOR_SCHEDULE(TAB) */
	        if(jobDBVO != null){
	        	ScheduleVO jobExitsInQuartz = isJobExistQuartz(schedule, scheduleBean);
	        	if(jobExitsInQuartz != null){ /** Case Job Exist in Quartz Ram */
		    		/** delete Job in Quartz Ram */
		    		deleteJobInRamPool(schedule, scheduleBean);
		    		
		    		/** GET Data From Quartz Ram*/
			    	scheduleVO.setNo(jobDBVO.getNo());
			    	scheduleVO.setLastRunDate(jobDBVO.getLastRunDate());
			    		        
			        /** create Job */
			        schedule.scheduleJob(scheduleBean.getJobDetail(),scheduleBean.getCronTrigger());
			        
			        /** get Next Fire Time From Crontrigger */
			        scheduleVO.setNextRunDate(scheduleBean.getCronTrigger().getNextFireTime());
			        scheduleVO.setCrontriggerExp(scheduleBean.getCronTrigger().getCronExpression());
			        
			        /** Update MONITOR_SCHEDULE(TAB) */
			        updateBatchTaskSchedule(conn, scheduleVO);
			        
		    	}else{ /** Case Not found in Quartz  */ 
		    		/** Get Data From MONITOR_SCHEDULE(TAB) */
			    	scheduleVO.setNo(jobDBVO.getNo());
			    	scheduleVO.setLastRunDate(jobDBVO.getLastRunDate());
			    	
			        /** create Job */
			        schedule.scheduleJob(scheduleBean.getJobDetail(),scheduleBean.getCronTrigger());
			        
			        /** get Next Fire Time From Crontrigger */
			        scheduleVO.setNextRunDate(scheduleBean.getCronTrigger().getNextFireTime());
			        scheduleVO.setCrontriggerExp(scheduleBean.getCronTrigger().getCronExpression());
			        
			        /** Update MONITOR_SCHEDULE(TAB) */
			        updateBatchTaskSchedule(conn, scheduleVO);
		    	}
	        	
	        }else{ /** Not found In MONITOR_SCHEDULE(TAB) */
	        	
	        	ScheduleVO jobExitsInQuartz = isJobExistQuartz(schedule, scheduleBean);
	        	if(jobExitsInQuartz != null){ /** Case Job Exist in Quartz Ram And not found in TCB_MONITOR_SCHEDULE */
		    		/** No data New */
			    	scheduleVO.setNo(jobExitsInQuartz.getNo());
			    	scheduleVO.setLastRunDate(jobExitsInQuartz.getLastRunDate());
			    			        
			    	/** delete Job in Quartz Ram */
		    		deleteJobInRamPool(schedule, scheduleBean);
		    		
			        /** create Job */
			        schedule.scheduleJob(scheduleBean.getJobDetail(),scheduleBean.getCronTrigger());
			        
			        /** get Next Fire Time From Crontrigger */
			        scheduleVO.setNextRunDate(scheduleBean.getCronTrigger().getNextFireTime());
			        scheduleVO.setCrontriggerExp(scheduleBean.getCronTrigger().getCronExpression());
	
			        /** Create New MONITOR_SCHEDULE(TAB) */
			        createBatchTask(conn, scheduleVO);
			        
		    	}else{ /** Case Not found in Quartz And not found in TCB_MONITOR_SCHEDULE */ 
		    		
		    		/** No data New initial */
			    	scheduleVO.setNo(ObjectIdGenerator.getInstance().getNextSequenceId(conn));
			    	scheduleVO.setLastRunDate(null);
			    			        
			        /** create Job */
			        schedule.scheduleJob(scheduleBean.getJobDetail(),scheduleBean.getCronTrigger());
			        
			        /** get Next Fire Time From Crontrigger */
			        scheduleVO.setNextRunDate(scheduleBean.getCronTrigger().getNextFireTime());
			        scheduleVO.setCrontriggerExp(scheduleBean.getCronTrigger().getCronExpression());
	
			        /** Create New MONITOR_SCHEDULE(TAB) */
			        createBatchTask(conn, scheduleVO);
		    	}
	        	
	        }//end if
	        
	        schedule.start();
    	}catch(Exception e){
    		logger.error(e.getMessage(),e);
    		throw e;
    	}
	   return true;
	}
    
   
	
	/**
	 * 
	 * @param conn
	 * @param param
	 * @throws Exception
	 */
    private void createBatchTask(Connection conn,ScheduleVO param) throws Exception{
    	ScheduleServiceManagerHelper helper = new ScheduleServiceManagerHelper();
    	helper.createBatchTask(conn, param);
    }
	 
    private void deleteBatchTask(Connection conn,ScheduleVO param) throws Exception{
    	ScheduleServiceManagerHelper helper = new ScheduleServiceManagerHelper();
    	helper.deleteBatchTask(conn, param);
    }
    /**
     * 
     * @param conn
     * @param param
     * @throws Exception
     */
    public  void updateStatusBatchTask(Connection conn,ScheduleVO param) throws Exception {
    	ScheduleServiceManagerHelper helper = new ScheduleServiceManagerHelper();
    	helper.updateStatusBatchTask(conn, param);
    }
    
    
    /**
     * 
     * @param conn
     * @param param
     * @throws Exception
     */
    public  void updateBatchTaskSchedule(Connection conn,ScheduleVO param) throws Exception {
    	ScheduleServiceManagerHelper helper = new ScheduleServiceManagerHelper();
    	helper.updateBatchTaskCaseSchedule(conn, param);
    }
   
    /**
     * 
     * @param schedule
     * @throws Exception
     */
	public void debug(Scheduler schedule) throws Exception{
		   SchedulerMetaData metaData = schedule.getMetaData();
		  // System.out.println("numJobsExecuted:"+metaData.numJobsExecuted());
	}
	
	/**
	 * 
	 * @param schedule
	 * @throws Exception
	 */
	private void runScheduler(Scheduler schedule) throws Exception {
		//System.out.println("Start Job");
		schedule.start();
	}
	
	/**
	 * 
	 * @param schedule
	 * @throws Exception
	 */
	private void stopScheduler(Scheduler schedule) throws Exception {
		System.out.println("Shutdown Job");
		schedule.shutdown();
	}
	
	/**
	 * 
	 * @param schedule
	 * @param param
	 * @throws Exception
	 */
	private void pauseJobInRamPool(Scheduler schedule,ScheduleVO param) throws Exception {
		//System.out.println("Start Job");
		//schedule.pauseJob(param.getJobId(), param.getGroupId());
		//schedule.pauseTrigger(param.getJobId(), param.getGroupId());
	}
	
	/**
	 * 
	 * @param schedule
	 * @param sb
	 * @throws Exception
	 */
	private void deleteJobInRamPool(Scheduler schedule ,ScheduleBean sb) throws Exception {
		if(sb != null){
	      logger.debug("delete JobId In RamPool:"+sb.getJobDetail().getKey().getName()+":"+sb.getJobDetail().getKey().getGroup());
		  try{
		     schedule.deleteJob(sb.getJobDetail().getKey());
			
		     schedule.unscheduleJob(new TriggerKey(sb.getJobDetail().getKey().getName(),sb.getJobDetail().getKey().getGroup()));
		  }catch(Exception e){
			  logger.error("ERROR DEL JOB IN RAMPool \n"+e.getMessage());
		  }
		}
	}
	
	/**
	 * 
	 * @param schedule
	 * @param scheduleBean
	 * @return
	 */
	private  ScheduleVO isJobExistQuartz(Scheduler schedule,ScheduleBean scheduleBean){
		ScheduleVO vo = null;
		try{
			logger.debug("Check Job IsExist JobName:"+scheduleBean.getJobDetail().getKey().getName()+"Group:"+scheduleBean.getJobDetail().getKey().getGroup());
			JobDetail job = schedule.getJobDetail(scheduleBean.getJobDetail().getKey());
			//schedule.get
			logger.debug("job:"+job);
			if(job != null){
				//job isExist in Quartz
				logger.debug("Found Job");
				logger.debug("JobName:"+job.getKey().getName());
				logger.debug("JobGroup:"+job.getKey().getName());
				vo = (ScheduleVO)job.getJobDataMap().get(SchedulerConstant.JOB_DATAMAP_PARAM);
				logger.debug("NO:"+vo.getNo());
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return vo;
	  }
	
	
	 
	/**
	 * 
	 * @return
	 * @throws SchedulerException
	 */
    private  Scheduler getScheduler() throws SchedulerException {
		   SchedulerFactory sf = new StdSchedulerFactory();
	       Scheduler sched1 = sf.getScheduler();
	       logger.debug("sched1:"+sched1);
	       //Scheduler sched = sf.getScheduler();
	      // sched1.start();
		   return sched1;
	}
    
}
