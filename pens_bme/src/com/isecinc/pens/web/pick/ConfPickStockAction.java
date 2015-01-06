package com.isecinc.pens.web.pick;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import util.BeanParameter;
import util.BundleUtil;
import util.ReportUtilServlet;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.bean.Onhand;
import com.isecinc.pens.bean.ReqPickStock;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.OnhandProcessDAO;
import com.isecinc.pens.dao.ConfPickStockDAO;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.pick.process.OnhandProcess;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class ConfPickStockAction extends I_Action {

	public ActionForward prepare2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepare2");
		ConfPickStockForm aForm = (ConfPickStockForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action"));
			if("new".equals(action)){
				request.getSession().setAttribute("results", null);
				request.getSession().setAttribute("custGroupList", null);
				request.getSession().setAttribute("dataSaveMapAll", null);
				request.getSession().setAttribute("totalAllQty",null);
				
				aForm.setResultsSearch(null);
				ReqPickStock ad = new ReqPickStock();
				aForm.setBean(ad);
				
			}else if("back".equals(action)){
				//clear session data
				request.getSession().setAttribute("results", null);
				request.getSession().setAttribute("custGroupList", null);
				request.getSession().setAttribute("dataSaveMapAll", null);
				request.getSession().setAttribute("totalAllQty",null);
				
				aForm.setBean(aForm.getBeanCriteria());
				aForm.setResultsSearch(ConfPickStockDAO.searchHead(aForm.getBean()));
			}
		} catch (Exception e) {
			request.setAttribute("Message",e.getMessage());
			throw e;
		}finally{
			if(conn != null){
			  conn.close();conn=null;
			}
		}
		return mapping.findForward("prepare2");
	}
	
	public ActionForward search2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("search2");
		ConfPickStockForm aForm = (ConfPickStockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			aForm.setResultsSearch(ConfPickStockDAO.searchHead(aForm.getBean()));
			if(aForm.getResultsSearch().size() <=0){
			   request.setAttribute("Message", "ไม่พบข้อมูล");
			   aForm.setResultsSearch(null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			
		}
		return mapping.findForward("search2");
	}
	
	public ActionForward clear2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear2");
		ConfPickStockForm aForm = (ConfPickStockForm) form;
		try {
			aForm.setResultsSearch(null);
			
			ReqPickStock ad = new ReqPickStock();
			//ad.setIssueReqDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			aForm.setBean(ad);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("clear2");
	}
	
	/**
	 * Prepare without ID
	 */
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "prepare";
		ConfPickStockForm aForm = (ConfPickStockForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			//save old criteria
			aForm.setBeanCriteria(aForm.getBean());
			
			conn = DBConnection.getInstance().getConnection();
			
			String process = Utils.isNull(request.getParameter("process"));
            String issueReqNo = Utils.isNull(request.getParameter("issueReqNo"));
            String issueReqStatus = Utils.isNull(request.getParameter("issueReqStatus"));
            String mode = Utils.isNull(request.getParameter("mode"));
            
			if( !"".equals(issueReqNo)){
				logger.debug("prepare edit issueReqNo:"+issueReqNo);
				request.getSession().setAttribute("results", null);
				request.getSession().setAttribute("dataSaveMapAll", null);
				request.getSession().setAttribute("totalAllQty",null);
				
				ReqPickStock p = new ReqPickStock();
				p.setIssueReqNo(issueReqNo);
				p.setNewSearch(true);//new search
				p.setNewReq(false);
				p.setStatus(issueReqStatus);
				
				if("confirm".equalsIgnoreCase(process)){
					p.setModeConfirm(true);
					p.setModeEdit(false);
				}else{
					p.setModeConfirm(false);
					p.setModeEdit(true);
				}
				
				//Get Item and set data to session dataGroupCodeMapAll
				p = searchBypage(conn, p, request);
				aForm.setBean(p);
				
			}
            
		} catch (Exception e) {
			request.setAttribute("Message", "error:"+ e.getMessage());
			throw e;
		}finally{
			if(conn != null){
			  conn.close();conn=null;
			}
		}
		return forward;
	}
	
	private ReqPickStock searchBypage(Connection conn,ReqPickStock p, HttpServletRequest request) {
		int totalRow = 0;
		int totalPage = 0;
		int pageNumber = 1;
		int totalQtyAll = 0;
		int totalReqQtyAll = 0;
		int totalCurPageQty = 0;
		int totalQtyNotInCurPage = 0;
		boolean newSearch = p.isNewSearch();
		List<ReqPickStock> results = new ArrayList<ReqPickStock>();
		try{
			logger.debug("pageNumber["+request.getParameter("pageNumber")+"]");
			logger.debug("newReq["+p.isNewReq()+"]");
			
			if(p.isNewSearch()){
				request.getSession().setAttribute("resultsView", null);
				
				//Case newsearch Recalc page
				pageNumber = 1;

				totalRow = ConfPickStockDAO.getTotalRowInStockIssueItemCaseNoEdit(conn, p);
				
				if(  Utils.isNull(p.getStatus()).equals(PickConstants.STATUS_ISSUED)){
					totalReqQtyAll =  ConfPickStockDAO.getTotalReqQtyInStockIssueItem(conn,p);	
					totalQtyAll =  ConfPickStockDAO.getTotalIssueQtyInStockIssueItem(conn,p);	
				}else{
				       totalQtyAll =  ConfPickStockDAO.getTotalReqQtyInStockIssueItem(conn,p);	
				       totalReqQtyAll = totalQtyAll;
				}
				
				totalPage = Utils.calcTotalPage(totalRow,PickConstants.CONF_PICK_PAGE_SIZE);
				request.getSession().setAttribute("totalPage", totalPage);
				request.getSession().setAttribute("totalRow", totalRow);

			}else{
			    pageNumber = !Utils.isNull(request.getParameter("pageNumber")).equals("")?Utils.convertStrToInt(request.getParameter("pageNumber")):1;
			}

			logger.debug("*** pageNumber["+pageNumber+"]********");
			
            //** Search Data and Display **/
			p = ConfPickStockDAO.searchReqPickStock(conn,p,false);//head only
			p.setNewSearch(newSearch);
			
			if(  Utils.isNull(p.getStatus()).equals(PickConstants.STATUS_ISSUED)){
				 ReqPickStock  pAllItem = ConfPickStockDAO.getStockIssueItemCaseNoEdit(conn, p,pageNumber,false);//
				 results = pAllItem.getItems();
				    
				 totalCurPageQty = pAllItem.getTotalQty();
			}else{
			     ReqPickStock  pAllItem = ConfPickStockDAO.getStockIssueItemCaseEdit(conn, p,pageNumber,false);//
			     results = pAllItem.getItems();
			    
			     totalCurPageQty = pAllItem.getTotalQty();
			}
			
			/** Calc SumQty **/
			if(request.getSession().getAttribute("totalAllQty") ==null){
				request.getSession().setAttribute("totalAllQty",String.valueOf(totalQtyAll));
				totalQtyNotInCurPage = totalQtyAll -totalCurPageQty;
			}else{
				//logger.debug("totalAllQty:"+(String)request.getSession().getAttribute("totalAllQty"));
				request.getSession().getAttribute("totalAllQty");
				totalQtyAll = Utils.convertStrToInt((String)request.getSession().getAttribute("totalAllQty"));
				totalQtyNotInCurPage = totalQtyAll -totalCurPageQty;
			}
			
			if (results != null  && results.size() >0) {
				request.getSession().setAttribute("results", results);
			} else {
				request.getSession().setAttribute("results", null);
				request.setAttribute("Message", "ไม่พบข่อมูล");
			}
			
			logger.debug("totalCurPageQty["+totalCurPageQty+"]");
			logger.debug("totalQtyNotInCurPage["+totalQtyNotInCurPage+"]");
			logger.debug("totalQtyAll["+totalQtyAll+"]");
			
			p.setTotalQty(totalQtyAll);
			p.setTotalReqQty(totalReqQtyAll);
			p.setTotalQtyNotInCurPage(totalQtyNotInCurPage);
				
			//set after first run = false
			p.setNewSearch(false);
			
			//display canPrint
			if(PickConstants.STATUS_ISSUED.equalsIgnoreCase(p.getStatus())){
				p.setCanPrint(true);
			}else{
				p.setCanPrint(false);
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return p;
	}
	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ConfPickStockForm f = (ConfPickStockForm) form;
		try {
			logger.debug("prepare 2");
			
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return "prepare";
	}

	/**
	 * Search
	 */
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ConfPickStockForm aForm = (ConfPickStockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();

			//save data Prev Page to dataSaveList
			setDataSaveMap(request, aForm);
			
			//set Disp nextPage
			ReqPickStock p = searchBypage(conn, aForm.getBean(), request);
			
			//setDisp result
			setDispResults(request, aForm);
			
			//set to form
			aForm.setBean(p);
			
			logger.debug("2totalQty:"+aForm.getBean().getTotalQty());
			
			request.setAttribute("Message", msg);
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			if(conn != null){
			   conn.close();conn = null;
			}
		}
		return "search";
	}

	
	private Map<String, ReqPickStock> setDataSaveMap(HttpServletRequest request,ConfPickStockForm aForm){
	    User user = (User) request.getSession().getAttribute("user");
	    //Data Disp Per Page
	    List<ReqPickStock> results = (List<ReqPickStock>)request.getSession().getAttribute("results"); //data per page
		
	    //dataSaveMap All
	    Map<String, ReqPickStock> dataSaveMapAll = new HashMap<String, ReqPickStock>();
	    if(request.getSession().getAttribute("dataSaveMapAll")!= null ){
	       dataSaveMapAll = (Map)request.getSession().getAttribute("dataSaveMapAll");
	    }
	    
		logger.debug("dataSaveMapAll Size:"+(dataSaveMapAll!=null?dataSaveMapAll.size():0));
		logger.debug("results Size:"+(results!=null?results.size():0));
		
		String[] issueQty = request.getParameterValues("issueQty");
		
		//add value to Results
		if(results != null && results.size() > 0 && issueQty != null){
			for(int i=0;i<results.size();i++){
				 ReqPickStock l = (ReqPickStock)results.get(i);
				 l.setIssueQty(issueQty[i]);
				 
				 l.setUpdateUser(user.getUserName());
				 l.setCreateUser(user.getUserName());
				 
				 //set data to list disp
				 results.set(i, l);
				 
				 //Key Map  
				 String key = l.getBarcode()+"_"+l.getMaterialMaster()+"_"+l.getGroupCode()+"_"+l.getPensItem();
				 if( !Utils.isNull(l.getIssueQty()).equals("")){
					 dataSaveMapAll.put(key, l);
				 }
				 
			}//for
		}//if

		//data to display
		request.getSession().setAttribute("results",results);
		request.getSession().setAttribute("dataSaveMapAll",dataSaveMapAll);
		
		return dataSaveMapAll;
	}

	private Map<String, ReqPickStock> setDispResults(HttpServletRequest request,ConfPickStockForm aForm){
	    //Data Disp Per Page
	    List<ReqPickStock> results = (List<ReqPickStock>)request.getSession().getAttribute("results"); //data per page
		
	    //dataSaveMap All
	    Map<String, ReqPickStock> dataSaveMapAll = new HashMap<String, ReqPickStock>();
	    if(request.getSession().getAttribute("dataSaveMapAll")!= null ){
	       dataSaveMapAll = (Map)request.getSession().getAttribute("dataSaveMapAll");
	    }
	    
		logger.debug("dataSaveMapAll Size:"+(dataSaveMapAll!=null?dataSaveMapAll.size():0));
		logger.debug("results Size:"+(results!=null?results.size():0));
		
		//add value to Results
		if(results != null && results.size() > 0){
			for(int i=0;i<results.size();i++){
				ReqPickStock l = (ReqPickStock)results.get(i);
				//Key Map  
				 String key = l.getBarcode()+"_"+l.getMaterialMaster()+"_"+l.getGroupCode()+"_"+l.getPensItem();
				 if(dataSaveMapAll.get(key) != null){
					 ReqPickStock pOld = (ReqPickStock) dataSaveMapAll.get(key); 
				     l.setIssueQty(pOld.getIssueQty());
				 }
				 //set data to list disp
				 results.set(i, l);
			}//for
		}//if
	
		//data to display
		request.getSession().setAttribute("results",results);
		
		return dataSaveMapAll;
	}
	
	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		ConfPickStockForm aForm = (ConfPickStockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			
		} catch (Exception e) {
			conn.rollback();
            logger.error(e.getMessage(),e);
			request.setAttribute("Message","ไม่สามารถบันทึกข้อมูลได้ \n"+ e.getMessage());
			try {
				
			} catch (Exception e2) {}
			return "prepare";
		} finally {
			try {
				if(conn != null){
				   conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return "search";
	}

	public ActionForward confirmAction(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("confirmAction");
		ConfPickStockForm aForm = (ConfPickStockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			ReqPickStock h = aForm.getBean();
			h.setStatus(ConfPickStockDAO.STATUS_ISSUED);//ISSUE
			h.setUpdateUser(user.getUserName());
		
			//update status stock_issue
			ConfPickStockDAO.updateStausStockIssue(conn, h);
			
			 //Set Data from screen to List
			Map<String, ReqPickStock> dataSaveMapAll = setDataSaveMap(request,aForm);
			
			//update req_qty=qty ,qty
			if(dataSaveMapAll != null && !dataSaveMapAll.isEmpty()){
				
				 Iterator its =  dataSaveMapAll.keySet().iterator();
					while(its.hasNext()){
					   String key = (String)its.next();
					   ReqPickStock p = (ReqPickStock)dataSaveMapAll.get(key);

					   p.setStatus(ConfPickStockDAO.STATUS_ISSUED);//ISSUE
					   p.setUpdateUser(user.getUserName());
						
					   ConfPickStockDAO.updateStockIssueItemByPK(conn, p);
					}
			}

			//Balance Onhand
			OnhandProcess.processUpdateBalanceOnhandByIssueReqNo(conn,h);
			
			conn.commit();
			
			//new search
			h.setNewReq(false);
			h.setNewSearch(true);
	        h = searchBypage(conn, h, request);
	        
			request.getSession().setAttribute("results", h.getItems());
			
			aForm.setBean(h);
				
			request.setAttribute("Message", "ยืนยันรายการ เรียบร้อยแล้ว");
			
		} catch (Exception e) {
			conn.rollback();
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}finally{
			if(conn != null){
			   conn.close();conn=null;
			}
		}
		return mapping.findForward("clear");
	}
	
	public ActionForward print(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		logger.debug("Search for report : " + this.getClass());
		ConfPickStockForm reportForm = (ConfPickStockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		ReportUtilServlet reportServlet = new ReportUtilServlet();
		HashMap parameterMap = new HashMap();
		ResourceBundle bundle = BundleUtil.getBundle("SystemElements", new Locale("th", "TH"));
		Connection conn = null;
		try {
	
			String fileType = SystemElements.PDF;
			logger.debug("fileType:"+fileType);

			ReqPickStock h = reportForm.getBean();
			if(h != null){
				logger.debug("ReqPickStock:"+h);
				//Head
				parameterMap.put("p_title", "รายงานยืนยัน การเบิกสินค้า");
				parameterMap.put("p_issueReqDate", h.getIssueReqDate());
				parameterMap.put("p_issueReqNo", h.getIssueReqNo());
				parameterMap.put("p_statusDesc", h.getStatusDesc());
				parameterMap.put("p_requestor", h.getRequestor());
				parameterMap.put("p_custGroupDesc", h.getCustGroup());
				parameterMap.put("p_needDate", h.getNeedDate());
				parameterMap.put("p_storeCode", h.getStoreCode()+"-"+h.getStoreName());
				parameterMap.put("p_subInv", h.getSubInv());
				parameterMap.put("p_storeNo", h.getStoreNo());
				parameterMap.put("p_remark", h.getRemark());
				
				//Gen Report
				String fileName = "conf_pick_report";
				String fileJasper = BeanParameter.getReportPath() + fileName;
				
				conn = DBConnection.getInstance().getConnection();
				ReqPickStock  pAllItem = ConfPickStockDAO.getStockIssueItemCaseNoEdit(conn, h,0,true);//
				List items = pAllItem.getItems();
				
				logger.debug("items size:"+items.size());
				reportServlet.runReport(request, response, conn, fileJasper, fileType, parameterMap, fileName, items);
				
			}else{
				
				request.setAttribute("Message", "ไม่พบข้อมูล  ");
				return  mapping.findForward("prepare");
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute("Message", e.getMessage());
		} finally {
			try {
				 conn.close();
			} catch (Exception e2) {}
		}
		return null;
	}
	
	
	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		ConfPickStockForm aForm = (ConfPickStockForm) form;
		try {
			request.getSession().setAttribute("dataSaveMapAll", null);
			request.getSession().setAttribute("dataEditMapAll", null);
			request.getSession().setAttribute("results", null);
			ReqPickStock ad = new ReqPickStock();
			//ad.setIssueReqDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			aForm.setBean(ad);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("clear");
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
