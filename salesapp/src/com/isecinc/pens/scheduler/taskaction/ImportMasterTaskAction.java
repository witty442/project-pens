package com.isecinc.pens.scheduler.taskaction;

import java.sql.Connection;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.MonitorBean;
import com.isecinc.pens.interfaces.imports.ImportMasterManager;
import com.isecinc.pens.scheduler.manager.ScheduleVO;
import com.isecinc.pens.scheduler.manager.SchedulerConstant;


public class ImportMasterTaskAction {
	protected static Logger logger = Logger.getLogger("PENS");
	public  ScheduleVO  execute(Connection conn ,ScheduleVO param) {
		try{
            /** Import Master txt file  From FTP Server  **/
			
		    //Run BatchTask Import Master
			MonitorBean monitor = ImportMasterManager.process(param.getUser());
			
			/** Set NoOfRecord and SizeOfFile */
		    param.setTransactionId(monitor.getTransactionId());
			param.setNoOfRecord("0");
			param.setSizeOfFile("0");
			
			//set status Complete
			param.setStatus(SchedulerConstant.STATUS_COMPLETE);
		}catch(Exception e){
			param.setStatus(SchedulerConstant.STATUS_FAIL);
			logger.error(e.getMessage(),e);
		}
		return param;
	}
	
	
}
