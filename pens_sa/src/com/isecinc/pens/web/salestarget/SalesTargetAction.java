package com.isecinc.pens.web.salestarget;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import util.DBConnection;
import util.Utils;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class SalesTargetAction extends I_Action {

	public static int pageSize = 90;
	public static Map<String,String> STORE_TYPE_MAP = new HashMap<String, String>();
	
	public ActionForward prepareSearch(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareSearch");
		SalesTargetForm aForm = (SalesTargetForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		String pageName ="";
		String subPageName = "";//Exp TT
		try {
			String action = Utils.isNull(request.getParameter("action")); 
			pageName = Utils.isNull(request.getParameter("pageName"));
			subPageName = Utils.isNull(request.getParameter("subPageName"));
			aForm.setPageName(pageName);
			aForm.setSubPageName(subPageName);
			if("new".equals(action)){
				
				//clear session
				request.getSession().setAttribute("criteria_",null);
				request.getSession().setAttribute("RESULTS",null);
				request.getSession().setAttribute("productMKTList",null);
				request.getSession().setAttribute("salesrepList",null);
				request.getSession().setAttribute("dataMap",null);
				request.getSession().setAttribute("PERIOD_LIST",null);
				request.getSession().setAttribute("REPORT_TYPE_LIST",null);
				request.getSession().setAttribute("CUSTOMER_CATEGORY_LIST",null);
				request.getSession().setAttribute("SALES_CHANNEL_LIST",null);
				request.getSession().setAttribute("SALESREP_LIST",null);
				request.getSession().setAttribute("STATUS_LIST",null);
				request.getSession().setAttribute("SALES_ZONE_LIST",null);
				
				SalesTargetBean sales = new SalesTargetBean();
				logger.debug("User["+user.getUserName()+"]pageName["+pageName+"]subPageName["+subPageName+"]");
				
				//init Connection
				conn = DBConnection.getInstance().getConnection();
				
				if (SalesTargetConstants.PAGE_MKT.equalsIgnoreCase(pageName)){
					SalesTargetControlPage.prepareSearchMKT(request, conn, user,pageName);
					
				}else if (SalesTargetConstants.PAGE_SALES.equalsIgnoreCase(pageName) ){
					logger.debug("Prepare MT");
					SalesTargetControlPage.prepareSearchMT(request, conn, user,pageName);
					SalesTargetBean s = SalesTargetUtils.searchSalesrepCodeByUserName(conn,user);
					if(s != null){
						sales.setSalesrepId(s.getSalesrepId());
						sales.setSalesrepCode(s.getSalesrepCode());
					}
					
				}else if (SalesTargetConstants.PAGE_MTMGR.equalsIgnoreCase(pageName) ){
					sales = new SalesTargetBean();
					SalesTargetControlPage.prepareSearchMTMGR(request, conn, user,pageName);
					
				}else if (SalesTargetConstants.PAGE_REPORT_SALES_TARGET.equalsIgnoreCase(pageName) ){
					sales = new SalesTargetBean();
					SalesTargetControlPage.prepareSearchReportSalesTarget(request, conn, user,pageName,subPageName);
					
				}else if (SalesTargetConstants.PAGE_REPORT_SALES_TARGET_ALL.equalsIgnoreCase(pageName) ){
					sales = new SalesTargetBean();
					SalesTargetControlPage.prepareSearchReportSalesTargetAll(request, conn, user,pageName);
				
					/**************** TT ******************************/
				}else if (SalesTargetConstants.PAGE_MKT_TT.equalsIgnoreCase(pageName)){
					sales = new SalesTargetBean();
					SalesTargetTTControlPage.prepareSearchMKT_TT(request, conn, user,pageName);
					
					//for test
					sales.setBrand("501");
				}else if (SalesTargetConstants.PAGE_TTSUPER.equalsIgnoreCase(pageName)){
					sales = new SalesTargetBean();
					SalesTargetTTControlPage.prepareSearchTTSUPER(request, conn, user,pageName);
			
				}else if (SalesTargetConstants.PAGE_TTMGR.equalsIgnoreCase(pageName)){
					sales = new SalesTargetBean();
					SalesTargetTTControlPage.prepareSearchTTMGR(request, conn, user,pageName);
					
				}
				
				//set bean session
				aForm.setBean(sales);
				logger.debug("bean:"+aForm.getBean());
			}else if("back".equals(action)){
				pageName = aForm.getPageName();
				logger.debug("action["+action+"]User["+user.getUserName()+"]pageName["+pageName+"]");
				SalesTargetBean criteria_ = (SalesTargetBean)request.getSession().getAttribute("criteria_");
				
				//for case error back to main search
				if(criteria_ !=null){
					logger.debug("Back CustCatNo:"+criteria_.getCustCatNo());
					
					//save criteria back action
					request.getSession().setAttribute("criteria_",SalesTargetControlPage.convertToCriteriaBean(pageName,criteria_));
					
					//Search By MKT
					if (SalesTargetConstants.PAGE_MKT.equalsIgnoreCase(pageName)){
					    SalesTargetBean salesReuslt = SalesTargetDAO.searchTargetHeadByMKT(criteria_,user,pageName);
					    aForm.setBean(salesReuslt);
					    request.getSession().setAttribute("RESULTS", SalesTargetExport.genResultSearchTargetHeadByMKT(request,aForm.getBean(),user));
					   
					}else if (SalesTargetConstants.PAGE_SALES.equalsIgnoreCase(pageName)){
						SalesTargetBean salesReuslt = SalesTargetDAO.searchTargetHeadByMT(criteria_,user,pageName);
					    aForm.setBean(salesReuslt);
					    request.getSession().setAttribute("RESULTS", SalesTargetExport.genResultSearchTargetHeadByMT(request,aForm.getBean(),user));
					    
					}else if (SalesTargetConstants.PAGE_MTMGR.equalsIgnoreCase(pageName)){
						SalesTargetBean salesReuslt = SalesTargetDAO.searchTargetHeadByMT(criteria_,user,pageName);
					    aForm.setBean(salesReuslt);
					    request.getSession().setAttribute("RESULTS", SalesTargetExport.genResultSearchTargetHeadByMTMGR(request,aForm.getBean(),user));
					
					}else if (SalesTargetConstants.PAGE_MKT_TT.equalsIgnoreCase(pageName)){
						SalesTargetBean salesReuslt = SalesTargetTTDAO.searchTargetHeadByMKT_TT(criteria_,user,pageName);
					    aForm.setBean(salesReuslt);
					    request.getSession().setAttribute("RESULTS", SalesTargetTTExport.genResultSearchTargetHeadByMKT_TT(request,aForm.getBean(),user));
					
					}else if (SalesTargetConstants.PAGE_TTSUPER.equalsIgnoreCase(pageName)){
						SalesTargetBean salesReuslt = SalesTargetTTDAO.searchTargetHeadByTTSUPER_TT(criteria_,user,pageName);
					    aForm.setBean(salesReuslt);
					    request.getSession().setAttribute("RESULTS", SalesTargetTTExport.genResultSearchTargetHeadByTTSUPER(request,aForm.getBean(),user));
					
					}else if (SalesTargetConstants.PAGE_TTMGR.equalsIgnoreCase(pageName)){
						SalesTargetBean salesReuslt = SalesTargetTTDAO.searchTargetHeadByTTMGR_TT(criteria_,user,pageName);
					    aForm.setBean(salesReuslt);
					    request.getSession().setAttribute("RESULTS", SalesTargetTTExport.genResultSearchTargetHeadByTTMGR(request,aForm.getBean(),user));
					}
				}//if
			}
		} catch (Exception e) {
		    logger.error(e.getMessage(),e);
			//request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()+ e.getMessage());
			throw e;
		}finally{
			if(conn != null){
			  conn.close();conn=null;
			}
		}
		return mapping.findForward("search");
	}
	
	public ActionForward searchHead(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("search2");
		SalesTargetForm aForm = (SalesTargetForm) form;
		User user = (User) request.getSession().getAttribute("user");
		boolean foundData = false;
		String pageName = aForm.getPageName();
		try {
			logger.debug("search Head :pageName["+pageName+"]");
	
			//save criteria search head
			request.getSession().setAttribute("criteria_",SalesTargetControlPage.convertToCriteriaBean(pageName,aForm.getBean()));
			
			//Search By Role MKT(Marketing)
			if (SalesTargetConstants.PAGE_MKT.equalsIgnoreCase(pageName)){
				SalesTargetBean salesReuslt = SalesTargetDAO.searchTargetHeadByMKT(aForm.getBean(),user,pageName);
			    aForm.setBean(salesReuslt);
			   if(salesReuslt.getItems() != null && salesReuslt.getItems().size() >0){
				  request.getSession().setAttribute("RESULTS", SalesTargetExport.genResultSearchTargetHeadByMKT(request,aForm.getBean(),user));
				  foundData = true;
			   }
			 //Search By Role MT(sales)
			}else if (SalesTargetConstants.PAGE_SALES.equalsIgnoreCase(pageName)){
			   SalesTargetBean salesReuslt = SalesTargetDAO.searchTargetHeadByMT(aForm.getBean(),user,pageName);
			   aForm.setBean(salesReuslt);
			   if(salesReuslt.getItems() != null && salesReuslt.getItems().size() >0){
			     request.getSession().setAttribute("RESULTS", SalesTargetExport.genResultSearchTargetHeadByMT(request,aForm.getBean(),user));
			     foundData = true;
			  }
			  //Search By Role Manager MTMGR(sales)
			}else if (SalesTargetConstants.PAGE_MTMGR.equalsIgnoreCase(pageName)){
			   SalesTargetBean salesReuslt = SalesTargetDAO.searchTargetHeadByMT(aForm.getBean(),user,pageName);
			   aForm.setBean(salesReuslt);
			   if(salesReuslt.getItems() != null && salesReuslt.getItems().size() >0){
			     request.getSession().setAttribute("RESULTS", SalesTargetExport.genResultSearchTargetHeadByMTMGR(request,aForm.getBean(),user));
			     foundData = true;
			  }
			  //Search Report
			}else if(SalesTargetConstants.PAGE_REPORT_SALES_TARGET.equalsIgnoreCase(pageName)){
				 StringBuffer resultHtmlTable = SalesTargetReport.searchReport(aForm.getBean(),aForm.getSubPageName());
				 if(resultHtmlTable != null){
					  request.getSession().setAttribute("RESULTS",resultHtmlTable);
					  foundData = true;
				 }
			// Search Report All 
			}else if(SalesTargetConstants.PAGE_REPORT_SALES_TARGET_ALL.equalsIgnoreCase(pageName)){
				 StringBuffer resultHtmlTable = SalesTargetReport.searchReportAll(aForm.getBean());
				 if(resultHtmlTable != null){
					  request.getSession().setAttribute("RESULTS",resultHtmlTable);
					  foundData = true;
				 }
			//Search By MKT_TT
			}else if (SalesTargetConstants.PAGE_MKT_TT.equalsIgnoreCase(pageName)){
			   SalesTargetBean salesReuslt = SalesTargetTTDAO.searchTargetHeadByMKT_TT(aForm.getBean(),user,pageName);
			   aForm.setBean(salesReuslt);
			   if(salesReuslt.getItems() != null && salesReuslt.getItems().size() >0){
				  request.getSession().setAttribute("RESULTS", SalesTargetTTExport.genResultSearchTargetHeadByMKT_TT(request,aForm.getBean(),user));
				  foundData = true;
			   }
			 //Search By TTSUPER
			}else if (SalesTargetConstants.PAGE_TTSUPER.equalsIgnoreCase(pageName)){
				   SalesTargetBean salesReuslt = SalesTargetTTDAO.searchTargetHeadByTTSUPER_TT(aForm.getBean(),user,pageName);
				   aForm.setBean(salesReuslt);
				   if(salesReuslt.getItems() != null && salesReuslt.getItems().size() >0){
					  request.getSession().setAttribute("RESULTS", SalesTargetTTExport.genResultSearchTargetHeadByTTSUPER(request,aForm.getBean(),user));
					  foundData = true;
				   }
			 //Search By TTMGR
			}else if (SalesTargetConstants.PAGE_TTMGR.equalsIgnoreCase(pageName)){
				   SalesTargetBean salesReuslt = SalesTargetTTDAO.searchTargetHeadByTTMGR_TT(aForm.getBean(),user,pageName);
				   logger.debug("canFinish:"+salesReuslt.isCanFinish());
				   aForm.setBean(salesReuslt);
				   if(salesReuslt.getItems() != null && salesReuslt.getItems().size() >0){
					  request.getSession().setAttribute("RESULTS", SalesTargetTTExport.genResultSearchTargetHeadByTTMGR(request,aForm.getBean(),user));
					  foundData = true;
				   }
			}
			
			if(foundData==false){
			   request.setAttribute("Message", "ไม่พบข้อมูล");
			   request.getSession().setAttribute("RESULTS",null);
			}
			logger.debug("pageName:"+aForm.getPageName());
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			
		}
		return mapping.findForward("search");
	}
	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		User user = (User) request.getSession().getAttribute("user");
		String forward ="detail";
		SalesTargetForm aForm = (SalesTargetForm) form;
		String pageName = aForm.getPageName();
		//Search By MKT
		if (SalesTargetConstants.PAGE_MKT.equalsIgnoreCase(pageName) ){
		   return SalesTargetControlPage.prepareDetailMKT(form, request, response);
		   
		}else if (SalesTargetConstants.PAGE_SALES.equalsIgnoreCase(pageName) ){
		   return SalesTargetControlPage.prepareDetailMT(form, request, response);
		   
		}else if (SalesTargetConstants.PAGE_MTMGR.equalsIgnoreCase(pageName)){
		   return SalesTargetControlPage.prepareDetailMTMGR(form, request, response);
		
	    /*************** TT ************************************************/
		}else if (SalesTargetConstants.PAGE_MKT_TT.equalsIgnoreCase(pageName) ){
		   return SalesTargetTTControlPage.prepareDetailMKT_TT(form, request, response);
		   
		}else if (SalesTargetConstants.PAGE_TTSUPER.equalsIgnoreCase(pageName) ){
			return SalesTargetTTControlPage.prepareDetailTTSUPER_TT(form, request, response);
			
		}else if (SalesTargetConstants.PAGE_TTMGR.equalsIgnoreCase(pageName) ){
			return SalesTargetTTControlPage.prepareDetailTTMGR_TT(form, request, response);
		}
		return forward;
	}

	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		SalesTargetForm aForm = (SalesTargetForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String pageName = aForm.getPageName();
		try {
			if (SalesTargetConstants.PAGE_MKT.equalsIgnoreCase(pageName) ){
				return new SalesTargetMTAction().save(aForm, request, response);
				
			}else if (SalesTargetConstants.PAGE_MKT_TT.equalsIgnoreCase(pageName) ){
				return new SalesTargetTTAction().saveTTByMKT(aForm, request, response);
				
			}else if (SalesTargetConstants.PAGE_TTSUPER.equalsIgnoreCase(pageName) ){
				return new SalesTargetTTAction().saveTTSUPER(aForm, request, response);
			}
		} catch (Exception e) {
			
            e.printStackTrace();
			request.setAttribute("Message","ไม่สามารถบันทึกข้อมูลได้ \n"+ e.getMessage());
			try {
				
			} catch (Exception e2) {}
			return "detail";
		} finally {
			try {
				
			} catch (Exception e2) {}
		}
		return "detail";
	}
	public ActionForward copyFromLastMonth(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("copyFromLastMonth ");
		SalesTargetForm aForm = (SalesTargetForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String pageName = aForm.getPageName();
		try {
			if (SalesTargetConstants.PAGE_MKT.equalsIgnoreCase(pageName) ){
				 new SalesTargetMTAction().copyFromLastMonth(mapping, aForm, request, response);
			}else if (SalesTargetConstants.PAGE_MKT_TT.equalsIgnoreCase(pageName) ){
				 new SalesTargetTTAction().copyFromLastMonthTTByMKT(mapping, aForm, request, response);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		} finally {
			try {
				
			} catch (Exception e2) {}
		}
		return mapping.findForward("search");
	}
	
	public ActionForward deleteAll(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("Delete All By Marketing");
		Connection conn = null;
		SalesTargetForm aForm = (SalesTargetForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			SalesTargetBean h = aForm.getBean();
			
			//delete all;
			SalesTargetDAO.deleteAllByMKT(conn, h);

			request.setAttribute("Message","ลบข้อมูล เรียบร้อยแล้ว");
			
			conn.commit();
			//reset
			h.setId(0);
			h.setTotalOrderAmt12Month("");
			h.setTotalOrderAmt3Month("");
			h.setTotalTargetAmount("");
			h.setTotalTargetQty("");
			h.setItems(null);
			h.setItemsList(null);
			logger.debug("priceListId:"+h.getPriceListId());
			aForm.setResults(new ArrayList<SalesTargetBean>());
			aForm.setBean(h);
			
		} catch (Exception e) {
			conn.rollback();
			
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		} finally {
			try {
				if(conn != null){
					conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return mapping.findForward("detail");
	}
	
	public ActionForward updateStatusManual(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("updateStatusManual set status to:"+Utils.isNull(request.getParameter("status")));
		Connection conn = null;
		SalesTargetForm aForm = (SalesTargetForm) form;
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			SalesTargetBean h = aForm.getBean();
			//Update status head
			h.setStatus(Utils.isNull(request.getParameter("status")));
			h.setUpdateUser("GOD");
			SalesTargetDAO.updateStatusHeadByManual(conn, h);

			request.setAttribute("Message","อัพเดตข้อมูล Status to:"+Utils.isNull(request.getParameter("status"))+" เรียบร้อยแล้ว");
			
			conn.commit();
		} catch (Exception e) {
			conn.rollback();
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		} finally {
			try {
				if(conn != null){
					conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return mapping.findForward("detail");
	}
	
	public ActionForward postToSales(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("postToSales By Marketing");
		Connection conn = null;
		SalesTargetForm aForm = (SalesTargetForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String pageName = aForm.getPageName();
		try {
			if (SalesTargetConstants.PAGE_MKT.equalsIgnoreCase(pageName) ){
				return new SalesTargetMTAction().postToSales(mapping, aForm, request, response);
			}else if (SalesTargetConstants.PAGE_MKT_TT.equalsIgnoreCase(pageName) ){
				return new SalesTargetTTAction().postToSalesTT(mapping, aForm, request, response);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		} finally {
			try {
				if(conn != null){
					conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return mapping.findForward("detail");
	}
	
	public ActionForward salesAcceptToSalesManager(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("salesAcceptToSalesManager By MT(Sales) Or TTSUPER");

		SalesTargetForm aForm = (SalesTargetForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String pageName = aForm.getPageName();
		try {
			if (SalesTargetConstants.PAGE_TTSUPER.equalsIgnoreCase(pageName) ){
				return new SalesTargetTTAction().salesAcceptToSalesManagerTT(mapping, aForm, request, response);		
			}else{ //PAGE_SALES
				//MTSAles
				return new SalesTargetMTAction().salesAcceptToSalesManager(mapping, aForm, request, response);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		} finally {
		
		}
		return mapping.findForward("detail");
	}
	
	public ActionForward salesManagerFinish(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("salesManagerFinish By MT Manager");
		SalesTargetForm aForm = (SalesTargetForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String pageName = aForm.getPageName();
		try {
			if (SalesTargetConstants.PAGE_TTMGR.equalsIgnoreCase(pageName) ){
				return new SalesTargetTTAction().salesManagerFinishByTTMGR_TT(mapping, aForm, request, response);		
			}else{ //PAGE_MTMGR
				return new SalesTargetMTAction().salesManagerFinish(mapping, aForm, request, response);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		} finally {
			try {
				
			} catch (Exception e2) {}
		}
		return mapping.findForward("search");
	}
	
	public ActionForward exportToExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		logger.debug("exportToExcel : ");
		User user = (User) request.getSession().getAttribute("user");
		SalesTargetForm aForm = (SalesTargetForm) form;
		boolean foundData = false;
		StringBuffer resultHtmlTable = null;
		String pageName = aForm.getPageName();
		boolean excel = true;
		try {
			 //Search Report
			 if(SalesTargetConstants.PAGE_REPORT_SALES_TARGET.equalsIgnoreCase(pageName)){
				  resultHtmlTable = SalesTargetReport.searchReport(aForm.getBean(),excel,aForm.getSubPageName());
				 if(resultHtmlTable != null){
					  foundData = true;
				 }
			 }else  if(SalesTargetConstants.PAGE_SALES.equalsIgnoreCase(pageName)){
				  resultHtmlTable = SalesTargetExport.genExportExcelByMT(request, aForm.getBean(), user) ;
				  if(resultHtmlTable != null){
					  foundData = true;
				 }	
			 }
			 
			  if(foundData){
					java.io.OutputStream out = response.getOutputStream();
					response.setHeader("Content-Disposition", "attachment; filename=data.xls");
					response.setContentType("application/vnd.ms-excel");
					
					Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
					w.write(resultHtmlTable.toString());
				    w.flush();
				    w.close();
		
				    out.flush();
				    out.close();
			  }else{
				  request.setAttribute("Message","");
			  }
         
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
		} finally {
			try {
				// conn.close();
			} catch (Exception e2) {}
		}
		return null;
	}
	
	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return "prepare";
	}

	/**
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		SalesTargetForm orderForm = (SalesTargetForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{}
		return "search";
	}
	
	@Override
	protected String changeActive(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return null;
	}

	/**
	 * Set new Criteria
	 */
	protected void setNewCriteria(ActionForm form) throws Exception {

	}
	
	
}
