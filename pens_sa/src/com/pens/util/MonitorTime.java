package com.pens.util;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

public class MonitorTime {
 private Date startDate = new Date();
 private String stepName ;
 protected static  Logger logger = Logger.getLogger("PENS");
 
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}
	
	public MonitorTime(String stepName){
		try{
			this.stepName = stepName;
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public  void debugUsedTime(){
		try{
			Date endDate = new Date();
			long millis = endDate.getTime() - startDate.getTime();
			String disp = String.format("%d min, %d sec", 
				    TimeUnit.MILLISECONDS.toMinutes(millis),
				    TimeUnit.MILLISECONDS.toSeconds(millis) - 
				    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
				);
			logger.debug("process ["+this.stepName +"] \n Used Time:["+(disp)+"]");	
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
