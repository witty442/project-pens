package com.isecinc.pens.scheduler.taskaction;

import java.math.BigDecimal;
import java.sql.Connection;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.MonitorBean;
import com.isecinc.pens.interfaces.imports.ImportReceiptManager;
import com.isecinc.pens.interfaces.imports.ImportReceiptProcess;
import com.isecinc.pens.scheduler.manager.ScheduleVO;
import com.isecinc.pens.scheduler.manager.SchedulerConstant;


public class ImportReceiptTaskAction {
	protected static Logger logger = Logger.getLogger("PENS");
	public  ScheduleVO  execute(Connection conn ,ScheduleVO param) {
		try{
            /** Import Receipt txt file  From FTP Server  **/

			MonitorBean monitor = new ImportReceiptManager().process(param.getUser());
		    BigDecimal transactionId = monitor.getTransactionId();
			
			/** Set NoOfRecord and SizeOfFile */
		    param.setTransactionId(transactionId);
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
