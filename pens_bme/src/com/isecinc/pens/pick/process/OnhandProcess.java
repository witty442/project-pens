package com.isecinc.pens.pick.process;

import java.sql.Connection;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.ReqPickStock;
import com.isecinc.pens.dao.OnhandProcessDAO;

public class OnhandProcess {
	protected static Logger logger = Logger.getLogger("PENS");
	
	//Action :after confirm req finishing
	public static void processBalanceOnhand(String warehouse,String requestNo,String userName){
		try{
			logger.debug("******* Start processBalanceOnhand *******************");
			
			OnhandProcessDAO.processBanlanceOnhandFromConfirmFinishing(warehouse,requestNo,userName);
			
			logger.debug("******* End processBalanceOnhand *******************");
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
	
	//Action :after req pick stock
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
	
	public static void processUpdateBalanceOnhandByIssueReqNoCaseCancel(Connection conn,ReqPickStock req) throws Exception{
		try{
			logger.debug("******* Start processUpdateBanlanceOnhandFromStockIssueCaseCancelReq *******************");
			
			OnhandProcessDAO.processUpdateBanlanceOnhandFromStockIssueCaseCancelReq(conn,req);
			
			logger.debug("******* End processUpdateBanlanceOnhandFromStockIssueCaseCancelReq *******************");
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			throw e;
		}
	}
}
