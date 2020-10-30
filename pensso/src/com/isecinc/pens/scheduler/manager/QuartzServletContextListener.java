package com.isecinc.pens.scheduler.manager;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.ee.servlet.QuartzInitializerServlet;
import org.quartz.impl.StdSchedulerFactory;

public class QuartzServletContextListener implements ServletContextListener {
public QuartzServletContextListener() {
}

private static Log logger = LogFactory.getLog(QuartzServletContextListener.class);
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
		 try {
			 System.out.println("QuartzServletContextListener start:");
		  /*factory = new StdSchedulerFactory();
		  // Start the scheduler now
		  factory.getScheduler().start();
		  logger.info("Storing QuartzScheduler Factory at" + QUARTZ_FACTORY_KEY);
		  ctx.setAttribute(QUARTZ_FACTORY_KEY, factory);
		  */
		}catch (Exception ex) {
		  logger.error("Quartz failed to initialize", ex);
		}
	}

	/**
	* contextDestroyed
	*
	* @param servletContextEvent ServletContextEvent
	*/
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		try {
		       factory.getDefaultScheduler().shutdown();
		}
		catch (SchedulerException ex) {
		  logger.error("Error stopping Quartz", ex);
		}
	   }
}

