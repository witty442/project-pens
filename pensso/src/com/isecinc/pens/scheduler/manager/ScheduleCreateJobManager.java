package com.isecinc.pens.scheduler.manager;

import java.util.Date;

import org.apache.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import com.isecinc.pens.scheduler.utils.CronExpressionUtil;
import com.pens.util.DateUtil;
import com.pens.util.Utils;

public class ScheduleCreateJobManager {

	protected static Logger logger = Logger.getLogger("PENS");
    
	 /**
     * 
     * @param param
     * @return
     * @throws Exception
     */
	public ScheduleBean createScheduleBean(ScheduleVO param) throws Exception{
		
		ScheduleBean bean = new ScheduleBean();
		logger.debug("2.groupid:"+param.getGroupId());
		JobDetail jobDetail = createJobDetail(param);
		
		if(SchedulerConstant.SCHEDULE_TYPE_NOW.equals(param.getType())){
			bean.setTrigger(createTriggerByNow(param));
			
		}else if(SchedulerConstant.SCHEDULE_TYPE_ONCE.equals(param.getType())){
			bean.setTrigger(createTriggerByOnce(param));
			
		}else if(SchedulerConstant.SCHEDULE_TYPE_DAILY.equals(param.getType())){
			bean.setCronTrigger(createCrontrigger(param));
			
		}else if(SchedulerConstant.SCHEDULE_TYPE_WEEKLY.equals(param.getType())){
			bean.setCronTrigger(createCrontrigger(param));
			
		}else if(SchedulerConstant.SCHEDULE_TYPE_MONTHLY.equals(param.getType())){
			bean.setCronTrigger(createCrontrigger(param));
			
		}
		bean.setJobDetail(jobDetail);
		
		return bean;
	}
	
	/**
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public JobDetail createJobDetail(ScheduleVO param) throws Exception {
		  Class c = java.lang.Class.forName(SchedulerConstant.DEPAULT_PACKAGE+"."+param.getProgramId()+"Task");
		
		  logger.debug("Class:"+SchedulerConstant.DEPAULT_PACKAGE+"."+param.getProgramId()+"Task");
		  logger.debug("jobId:"+param.getJobId());
		  logger.debug("groupId:"+param.getGroupId());
		  
		  JobDetail job = JobBuilder.newJob(c).withIdentity(param.getJobId()).build();
		  job.getJobDataMap().put(SchedulerConstant.JOB_DATAMAP_PARAM, param);
		  return job;
	}
	 
	/**
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 * Desc :Run Now
	 */
	private Trigger createTriggerByNow(ScheduleVO param) throws Exception{
		Date startTime = DateUtil.parse(param.getStartDate() + param.getStartHour() + param.getStartMinute() + "00", "dd/MM/yyyyHHmmss");
		//SimpleTrigger trigger = new SimpleTrigger(param.getJobId(),param.getGroupId(), startTime, null,0, 0L);
		
		Trigger trigger = TriggerBuilder
				.newTrigger()
				.startNow()
				.withIdentity(param.getJobId(), param.getGroupId())
				.build();
		
		return trigger;
	}
	
	/**
	 * 
	 * @param param
	 * @return 
	 * @throws Exception
	 * Desc : Run Once
	 */
	private Trigger createTriggerByOnce(ScheduleVO param) throws Exception{
		Date date = DateUtil.parse(param.getStartDate(),DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
		String dateStr = DateUtil.stringValue(date, DateUtil.DD_MM_YYYY_WITH_SLASH);
		logger.debug("Trigger dateStr:"+dateStr + param.getStartHour() + param.getStartMinute() + "00");
		
		Date startTime = DateUtil.parse(dateStr + param.getStartHour() + param.getStartMinute() + "00", "dd/MM/yyyyHHmmss");
		
		logger.debug("Trigger StartTime:"+startTime);
		//SimpleTrigger trigger = new SimpleTrigger(param.getJobId(),param.getGroupId(), startTime);
		
		Trigger trigger = TriggerBuilder
				.newTrigger()
				.startAt(startTime)
				.forJob(param.getJobId())
				.withIdentity(param.getJobId(), param.getGroupId())
				.build();
		
		logger.debug("trigger:"+trigger);
		
		return trigger;
	}
	
	/**
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	private CronTrigger createCrontrigger(ScheduleVO param)throws Exception{
		String cronTriggerStr = "";
		if( !Utils.isNull(param.getCrontriggerExp()).equals("")){
			cronTriggerStr = param.getCrontriggerExp();	
		}else{
			  //CronTrigger trigger = new CronTrigger(param.getJobId(),param.getGroup(),"0 5 16-17 ? * MON");  
			  // CronTrigger trigger = new CronTrigger(param.getProgramId(),param.getGroup(),"0 0/1 * * * ?");
			  Date startTime = DateUtil.parse(param.getStartDate() + param.getStartHour() + param.getStartMinute() + "00", "dd/MM/yyyyHHmmss");
			  //Date stopTime = DateUtil.parse(param.getStopDate() + param.getStopHour() + param.getStopMinute() + "00", "dd/MM/yyyyHHmmss");
			  
		      if(SchedulerConstant.SCHEDULE_TYPE_DAILY.equals(param.getType())){
		    	 //Daily 
		    	  //default every day =1
		    	  if(Utils.isNull(param.getEveryDay()).equals("")){
		    		  param.setEveryDay("1");
		    	  }
		    	  cronTriggerStr = CronExpressionUtil.dailyToCronExpr(param.getEveryDay(),param.getEveryType(),param.getEveryMinutes(),param.getEveryHourly(), startTime);
		    	 
		      }else if(SchedulerConstant.SCHEDULE_TYPE_WEEKLY.equals(param.getType())){
				  //weekly
				  cronTriggerStr = CronExpressionUtil.weeklyToCronExpr(param.getDays(), startTime);
				 //cronTriggerStr  =  "0 45 17 ? * TUE"; //run every tueday start at 10:15  //sample cronStr
				  
		      }else if(SchedulerConstant.SCHEDULE_TYPE_MONTHLY.equals(param.getType())){
				  //monthly
				  cronTriggerStr = CronExpressionUtil.nthDayOfMonthToCronExpr(Integer.parseInt(param.getNDay()), startTime);
			  }
		  
		}
		logger.debug("cronTriggerStr :"+cronTriggerStr);
		//CronTrigger trigger = new CronTrigger(param.getJobId(),param.getGroupId(),cronTriggerStr);
		CronTrigger trigger = TriggerBuilder.newTrigger()
				.withIdentity(param.getJobId(),param.getGroupId())
				.withSchedule(CronScheduleBuilder.cronSchedule(cronTriggerStr))
				.build();
        logger.debug("CronTrigger:"+trigger);
		logger.debug("triggerName:"+trigger.getKey().getName());
		return trigger;
	}

}
