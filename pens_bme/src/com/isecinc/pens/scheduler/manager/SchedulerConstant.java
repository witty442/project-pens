
package com.isecinc.pens.scheduler.manager;

/**
 * @author WItty
 *
 */
public class SchedulerConstant {

	public final static String SCHEDULE_TYPE_NOW = "NOW";
	public final static String SCHEDULE_TYPE_ONCE = "ONCE";
	public final static String SCHEDULE_TYPE_WEEKLY = "WEEKLY";
	public final static String SCHEDULE_TYPE_DAILY = "DAILY";
	
	public final static String STATUS_FAIL ="FAIL";
	public final static String STATUS_SCHEDULE ="SCHEDULE";
	public final static String STATUS_COMPLETE ="COMPLETE";
	public final static String STATUS_RUNNING ="RUNNING";
	public final static String STATUS_CANCEL ="CENCEL";
	
	public final static String GROUP_JOB_DEFAULT ="TREX";
	
	public final static String[] JOB_DAYS_DEFAULT = {"TUE"};
	public final static String JOB_START_DATE_DEFAULT ="01/02/2009";
	public final static String JOB_START_HOUR_DEFAULT ="09";
	public final static String JOB_START_MINUTE_DEFAULT ="09";
	public final static String JOB_STOP_DATE_DEFAULT ="01/02/2009";
	public final static String JOB_STOP_HOUR_DEFAULT ="00";
	public final static String JOB_STOP_MINUTE_DEFAULT ="00";
	
	
	//PORGRAMID
	public final static String PROGRAM_ID_TCB001 = "TCB001";
	public final static String PROGRAM_ID_RECOVERY = "RECOVERY";
	
	public final static String DEPAULT_PACKAGE = "com.isecinc.pens.scheduler.task";
	
	//JOb DataMap
	public final static String JOB_DATAMAP_PARAM ="ScheduleVO";
}
