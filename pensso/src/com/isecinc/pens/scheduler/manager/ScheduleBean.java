package com.isecinc.pens.scheduler.manager;

import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;

public class ScheduleBean {
	
	private Trigger trigger;
	private CronTrigger cronTrigger;
	private JobDetail jobDetail;
//	private SimpleTrigger simpleTrigger;
	
	/*
	public SimpleTrigger getSimpleTrigger() {
		return simpleTrigger;
	}
	public void setSimpleTrigger(SimpleTrigger simpleTrigger) {
		this.simpleTrigger = simpleTrigger;
	}*/
	public JobDetail getJobDetail() {
		return jobDetail;
	}
	public void setJobDetail(JobDetail jobDetail) {
		this.jobDetail = jobDetail;
	}
	public Trigger getTrigger() {
		return trigger;
	}
	public void setTrigger(Trigger trigger) {
		this.trigger = trigger;
	}
	public CronTrigger getCronTrigger() {
		return cronTrigger;
	}
	public void setCronTrigger(CronTrigger cronTrigger) {
		this.cronTrigger = cronTrigger;
	}
	
	
    
}
