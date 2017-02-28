package com.isecinc.pens.scheduler.manager;

import java.sql.Connection;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.impl.StdSchedulerFactory;

import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.scheduler.utils.JobUtils;

public class ScheduleServletContextListener implements ServletContextListener {
public ScheduleServletContextListener() {
}

private static Log logger = LogFactory.getLog(ScheduleServletContextListener.class);
public static final String QUARTZ_FACTORY_KEY ="org.quartz.impl.StdSchedulerFactory.KEY";
private ServletContext ctx = null;
private StdSchedulerFactory factory = null;

	/**
	* contextInitialized
	*
	* @param servletContextEvent ServletContextEvent
	*/
	public void contextInitialized(ServletContextEvent servletContextEvent) {
	   ctx = servletContextEvent.getServletContext();
	   Connection conn = null;
		 try {
		  factory = new StdSchedulerFactory();
		  // Start the scheduler now
		  factory.getScheduler().start();
		  logger.info("Storing QuartzScheduler Factory at" + QUARTZ_FACTORY_KEY);
		  ctx.setAttribute(QUARTZ_FACTORY_KEY, factory);
		  
		  // rerun job is Fail or schedule
		  ScheduleServiceManager service = new  ScheduleServiceManager();
		  
		  ScheduleVO param = new ScheduleVO();
		  param.setGroupId(SchedulerConstant.GROUP_JOB_DEFAULT);
		  param.setProgramId(SchedulerConstant.PROGRAM_ID_RECOVERY);
		  param.setProgramName("Recovery (rerun)Task after Server is Stop or Hult");
		  param.setType(SchedulerConstant.SCHEDULE_TYPE_NOW);
		  param.setJobId(JobUtils.genJobId(param));
		  
		  conn = DBConnection.getInstance().getConnection();
		  service.runJob(param,conn);
		  
		}catch (Exception ex) {
		  logger.error("Quartz failed to initialize", ex);
		}finally{
			try{
				if(conn != null){
				  conn.close();conn = null;
				}
			}catch(Exception e){}
		}
	}

	/**
	* contextDestroyed
	*
	* @param servletContextEvent ServletContextEvent
	*/
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		try {
		      // factory.getDefaultScheduler().shutdown();
		}
		catch (Exception ex) {
		  logger.error("Error stopping Quartz", ex);
		}
	   }
}

