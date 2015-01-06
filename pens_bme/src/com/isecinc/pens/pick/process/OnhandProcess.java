package com.isecinc.pens.pick.process;

import java.sql.Connection;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.ReqPickStock;
import com.isecinc.pens.dao.OnhandProcessDAO;

public class OnhandProcess {
	protected static Logger logger = Logger.getLogger("PENS");
	
	public static void processBalanceOnhand(String userName){
		try{
			logger.debug("******* Start processBalanceOnhand *******************");
			
			OnhandProcessDAO.processBanlanceOnhandFromConfirmFinishing(userName);
			
			logger.debug("******* End processBalanceOnhand *******************");
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	
	public static void processUpdateBalanceOnhandByIssueReqNo(Connection conn,ReqPickStock req) throws Exception{
		try{
			logger.debug("******* Start processUpdateBalanceOnhandByIssueReqNo *******************");
			
			OnhandProcessDAO.processUpdateBanlanceOnhandFromStockIssue(conn,req);
			
			logger.debug("******* End processUpdateBalanceOnhandByIssueReqNo *******************");
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			throw e;
		}
	}
}
