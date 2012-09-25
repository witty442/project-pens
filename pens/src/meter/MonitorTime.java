package meter;

import java.util.Date;

import org.apache.log4j.Logger;

public class MonitorTime {
 private Date startDate = new Date();;
 private String stepName ;
 protected static  Logger logger = Logger.getLogger("METER");
 
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
			logger.info("process ["+this.stepName +"] Used Time:["+(endDate.getTime() - startDate.getTime())+"]");	
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
