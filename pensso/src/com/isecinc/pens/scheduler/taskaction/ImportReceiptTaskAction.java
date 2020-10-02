package com.isecinc.pens.scheduler.taskaction;

import java.math.BigDecimal;
import java.sql.Connection;

import org.apache.log4j.Logger;

import com.isecinc.pens.scheduler.manager.ScheduleVO;
import com.isecinc.pens.scheduler.manager.SchedulerConstant;
import com.isecinc.pens.web.receipt.InterfaceReceiptProcess;


public class ImportReceiptTaskAction {
	protected static Logger logger = Logger.getLogger("PENS");
	public  ScheduleVO  execute(Connection conn ,ScheduleVO param) {
		try{
            /** Import Receipt txt file  From FTP Server  **/
		    //Run BatchTask Import Receipt By SalesCode
		    BigDecimal transactionId = InterfaceReceiptProcess.processImportReceiptAllSales(param.getUser());
			
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
