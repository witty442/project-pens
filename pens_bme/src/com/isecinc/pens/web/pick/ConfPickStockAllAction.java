package com.isecinc.pens.web.pick;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.ArrayList;
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

import com.isecinc.core.bean.Messages;
import com.isecinc.core.bean.References;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.bean.ReqPickStock;
import com.isecinc.pens.bean.ScanCheckBean;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.ConfPickStockDAO;
import com.isecinc.pens.dao.GeneralDAO;
import com.isecinc.pens.dao.ScanCheckDAO;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.pick.process.OnhandProcess;
import com.isecinc.pens.web.popup.PopupForm;
import com.pens.util.BeanParameter;
import com.pens.util.BundleUtil;
import com.pens.util.DBConnection;
import com.pens.util.ReportUtilServlet;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class ConfPickStockAllAction extends I_Action {

	public ActionForward prepare2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepare2");
		ConfPickStockForm aForm = (ConfPickStockForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action"));
			if("new".equals(action)){
				request.getSession().setAttribute("results", null);
				request.getSession().setAttribute("dataSaveMapAll", null);
				request.getSession().setAttribute("totalAllQty",null);
				request.getSession().setAttribute("curPageQtyMap",null);
				
				aForm.setResultsSearch(null);
				ReqPickStock ad = new ReqPickStock();
				ad.setStatus(PickConstants.STATUS_POST);
				aForm.setBean(ad);
				
				//init Listbox Session
				List<References> statusIssueReqList2 = new ArrayList<References>();
				References ref = new References("","");
				statusIssueReqList2.add(ref);
				statusIssueReqList2.addAll(PickConstants.getRequestStatusW2ListInPageConfPickStock());
				request.getSession().setAttribute("statusIssueReqList2",statusIssueReqList2);
				
				List<PopupForm> custGroupList = new ArrayList<PopupForm>();
				PopupForm refP = new PopupForm("",""); 
				custGroupList.add(refP);
				custGroupList.addAll(GeneralDAO.searchCustGroup( new PopupForm()));
				request.getSession().setAttribute("custGroupList",custGroupList);
				
				List<References> wareHouseList = new ArrayList<References>();
				References ref1 = new References("","");
				wareHouseList.add(ref1);
				wareHouseList.addAll(PickConstants.getWareHouseList("",""));
				request.getSession().setAttribute("wareHouseList2",wareHouseList);
				
			}else if("back".equals(action)){
				//clear session data
				request.getSession().setAttribute("results", null);
				request.getSession().setAttribute("dataSaveMapAll", null);
				request.getSession().setAttribute("totalAllQty",null);
				request.getSession().setAttribute("curPageQtyMap",null);
				
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
			ad.setStatus(PickConstants.STATUS_POST);
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
            String wareHouse = Utils.isNull(request.getParameter("wareHouse"));
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
				p.setWareHouse(wareHouse);
				
				p.setModeConfirm(mode.equalsIgnoreCase("confirm")?true:false);
				p.setModeEdit(mode.equalsIgnoreCase("edit")?true:false);
				p.setCanEditDeliveryDate(mode.equalsIgnoreCase("view")?true:false);
				 
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
		//int totalReqQtyAll = 0;
		int totalCurPageQty = 0;
		int totalQtyNotInCurPage = 0;
		boolean newSearch = p.isNewSearch();
		List<ReqPickStock> results = new ArrayList<ReqPickStock>();
		Map<String,String> curPageQtyMap = new HashMap<String,String>();
		try{
			logger.debug("pageNumber["+request.getParameter("pageNumber")+"]");
			logger.debug("newReq["+p.isNewReq()+"]");
			
			if(p.isNewSearch()){
				logger.debug("new search:"+p.isNewSearch());
				
				//init all to dataMap
				initDataSaveMapAllInFirstTimeSearch(conn,request,p);
				//init totalAllReq
				p.setTotalReqQty(ConfPickStockDAO.getTotalReqQtyInStockIssueItem(conn,p));
				//clear session List
				request.getSession().setAttribute("resultsView", null);
				
				//Case newsearch Recalc page
				pageNumber = 1;

				totalRow = ConfPickStockDAO.getTotalRowInStockIssueItemCaseNoEdit(conn, p);
				
				if(  Utils.isNull(p.getStatus()).equals(PickConstants.STATUS_ISSUED)){	
					totalQtyAll =  ConfPickStockDAO.getTotalIssueQtyInStockIssueItem(conn,p);//Real Qty	
				}else{
					 totalQtyAll =  ConfPickStockDAO.getTotalReqQtyInStockIssueItem(conn,p);	
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
				 
			}else if( Utils.isNull(p.getStatus()).equals(PickConstants.STATUS_BEF)){
			     ReqPickStock  pAllItem = ConfPickStockDAO.getStockIssueItemCaseEditPageAll(conn, p,pageNumber,false);//
			     results = pAllItem.getItems();
			    
			     totalCurPageQty = pAllItem.getTotalQty();
			}else{
			     ReqPickStock  pAllItem = ConfPickStockDAO.getStockIssueItemCaseEditPageAll(conn, p,pageNumber,false);//
			     results = pAllItem.getItems();
			    
			     totalCurPageQty = pAllItem.getTotalQty();
			}
			/* case Edit on Screen Get totalCurPageQty From curPageQtyMap session**/
			
			 if(request.getSession().getAttribute("curPageQtyMap") !=null){
				 curPageQtyMap = (Map)request.getSession().getAttribute("curPageQtyMap");
				// logger.debug("totalCurPageQty pageNumber["+pageNumber+"]=["+curPageQtyMap.get(String.valueOf(pageNumber))+"]");
				 
				 //Debug Test
				/* Iterator its = curPageQtyMap.keySet().iterator();
				 while(its.hasNext()){
					 String pkey = (String)its.next();
					 logger.debug("pkey["+pkey+"]=["+curPageQtyMap.get(pkey)+"]");
					 
				 }*/
				 
				 if(curPageQtyMap.get(String.valueOf(pageNumber)) != null){
					 totalCurPageQty =  Utils.convertStrToInt(curPageQtyMap.get(String.valueOf(pageNumber)),0);
				 }
			 }
			
			/** Calc SumQty **/
			if(request.getSession().getAttribute("totalAllQty") ==null){
				request.getSession().setAttribute("totalAllQty",String.valueOf(totalQtyAll));
				totalQtyNotInCurPage = totalQtyAll - totalCurPageQty;
			}else{
				
				//logger.debug("totalAllQty:"+(String)request.getSession().getAttribute("totalAllQty"));
				//request.getSession().getAttribute("totalAllQty");
				
				totalQtyAll = Utils.convertStrToInt((String)request.getSession().getAttribute("totalAllQty"));
				totalQtyNotInCurPage = totalQtyAll - totalCurPageQty;
				
			}
			
			if (results != null  && results.size() >0) {
				request.getSession(true).setAttribute("results", results);

			} else {
				request.getSession().setAttribute("results", null);
				request.setAttribute("Message", "ไม่พบข่อมูล");
			}
			
			logger.debug("totalCurPageQty["+totalCurPageQty+"]");
			logger.debug("totalQtyNotInCurPage["+totalQtyNotInCurPage+"]");
			logger.debug("totalQtyAll["+totalQtyAll+"]");
			
			p.setTotalQty(totalQtyAll);
			p.setTotalQtyNotInCurPage(totalQtyNotInCurPage);
				
			//set after first run = false
			p.setNewSearch(false);
			
			//display canPrint
			if(PickConstants.STATUS_ISSUED.equalsIgnoreCase(p.getStatus()) || PickConstants.STATUS_BEF.equalsIgnoreCase(p.getStatus())){
				p.setCanPrint(true);
			}else{
				p.setCanPrint(false);
			}
			//canCancel
			if(PickConstants.STATUS_POST.equalsIgnoreCase(p.getStatus())  || PickConstants.STATUS_BEF.equalsIgnoreCase(p.getStatus())){
				p.setCanCancel(true);
			}else{
				p.setCanCancel(false);
			}

			if(p.isModeEdit()){
			  p.setCanConfirm(false); 
			  if( Utils.isNull(p.getStatus()).equals(PickConstants.STATUS_POST) ||  Utils.isNull(p.getStatus()).equals(PickConstants.STATUS_BEF)){
				 p.setCanEdit(true);
			  }else{
				 p.setCanEdit(false); 
			  }
			}
			
			if(p.isModeConfirm()){
			  p.setCanEdit(false); 
			  p.setCanEditDeliveryDate(true);
			  
			  if( Utils.isNull(p.getStatus()).equals(PickConstants.STATUS_BEF)){
				 p.setCanConfirm(true);
			  }else{
				 p.setCanConfirm(false); 
			  }
			}
			
			/** Exported = 'Y' No edit delivery date**/
			if("Y".equalsIgnoreCase(p.getExported())){
			    p.setCanEditDeliveryDate(false);
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
			
			//setDisp result  Case Issue not set issueQty
			if(  !Utils.isNull(p.getStatus()).equals(PickConstants.STATUS_ISSUED)){
		  	   setDispResults(request, aForm);
			}
			
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

	private void initDataSaveMapAllInFirstTimeSearch(Connection conn,HttpServletRequest request,ReqPickStock p){
	    User user = (User) request.getSession().getAttribute("user");
	    try{
		    //dataSaveMap All
		    Map<String, ReqPickStock> dataSaveMapAll = new HashMap<String, ReqPickStock>();
		    if(request.getSession().getAttribute("dataSaveMapAll")!= null ){
		       dataSaveMapAll = (Map<String,ReqPickStock>)request.getSession().getAttribute("dataSaveMapAll");
		    } 
			logger.debug("before dataSaveMapAll Size:"+(dataSaveMapAll!=null?dataSaveMapAll.size():0));
			 
			ReqPickStock  pAllItem = ConfPickStockDAO.getStockIssueItemCaseEditPageAll(conn, p,0,true);//
		    List<ReqPickStock> results = pAllItem.getItems();
		     
			//add all item to dataSaveMapAll
			if(results != null && results.size() > 0){
				for(int i=0;i<results.size();i++){
					 ReqPickStock l = (ReqPickStock)results.get(i);
					 l.setUpdateUser(user.getUserName());
					 l.setCreateUser(user.getUserName());
					
					 //Key Map  
					 String key = p.getIssueReqNo()+"_"+l.getBarcode()+"_"+l.getMaterialMaster()+"_"+l.getGroupCode()+"_"+l.getPensItem();
					 logger.debug("mat["+l.getMaterialMaster()+"]issueQty["+l.getIssueQty()+"]");
					 dataSaveMapAll.put(key, l);
					 
				}//for
			}//if
	
			request.getSession().setAttribute("dataSaveMapAll",dataSaveMapAll);
			logger.debug("after dataSaveMapAll Size:"+(dataSaveMapAll!=null?dataSaveMapAll.size():0));
	    }catch(Exception e){
	    	logger.error(e.getMessage(),e);
	    }
	}
	
	/** Save Data prev page **/
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
				 String key = aForm.getBean().getIssueReqNo()+"_"+l.getBarcode()+"_"+l.getMaterialMaster()+"_"+l.getGroupCode()+"_"+l.getPensItem();
				 dataSaveMapAll.put(key, l); 
				 
			}//for
		}//if

		//data to display
		request.getSession().setAttribute("results",results);
		request.getSession().setAttribute("dataSaveMapAll",dataSaveMapAll);
		
		return dataSaveMapAll;
	}

	/** Set Data case req_qty ==0 **/
	private Map<String, ReqPickStock> setDispResults(HttpServletRequest request,ConfPickStockForm aForm){
	    //Data Disp Per Page
	    List<ReqPickStock> results = (List<ReqPickStock>)request.getSession(true).getAttribute("results"); //data per page
		
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
				 String key = aForm.getBean().getIssueReqNo()+"_"+l.getBarcode()+"_"+l.getMaterialMaster()+"_"+l.getGroupCode()+"_"+l.getPensItem();
				 
			     if(dataSaveMapAll.get(key) != null){
					 ReqPickStock r = (ReqPickStock) dataSaveMapAll.get(key); 
					 logger.debug("issueQty:"+r.getIssueQty());
					 
					l.setIssueQty(r.getIssueQty());
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
		boolean validate = true;
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			ReqPickStock h = aForm.getBean();
			h.setStatus(ConfPickStockDAO.STATUS_ISSUED);//ISSUE
			h.setUpdateUser(user.getUserName());
		
			//update status stock_issue
			ConfPickStockDAO.confirmStausStockIssue(conn, h);
			
			 //Set Data from screen to List
			Map<String, ReqPickStock> dataSaveMapAll = setDataSaveMap(request,aForm);
			
			//update req_qty=qty ,qty
			if(dataSaveMapAll != null && !dataSaveMapAll.isEmpty()){
				
				 Iterator its =  dataSaveMapAll.keySet().iterator();
					while(its.hasNext()){
					   String key = (String)its.next();
					   ReqPickStock p = (ReqPickStock)dataSaveMapAll.get(key);
					   
					   //Check case Exception
					   if(Utils.convertStrToInt(p.getIssueQty()) > Utils.convertStrToInt(p.getQty())){
						  // validate = false;
					   }                              

					   p.setStatus(ConfPickStockDAO.STATUS_ISSUED);//ISSUE
					   p.setUpdateUser(user.getUserName());
						
					   ConfPickStockDAO.updateStatusStockIssueItemByPK(conn, p);
					}
			}

			if(validate){
				//Update status to issue scan_checkout
				ScanCheckBean scan = new ScanCheckBean();
				scan.setIssueReqNo(h.getIssueReqNo());
				scan.setWareHouse(h.getWareHouse());
				scan.setStatus(PickConstants.STATUS_ISSUED);
				scan.setUpdateUser(user.getUserName());
				
				ScanCheckDAO.updateScanCheckOutStatusToIssue(conn, scan);
				
				//Balance Onhand
				OnhandProcess.processUpdateBalanceOnhandByIssueReqNo(conn,h);
				
				conn.commit();
				
				//new search
				h.setNewReq(true);
				h.setNewSearch(true);
		        h = searchBypage(conn, h, request);
		        
				request.getSession().setAttribute("results", h.getItems());
				
				aForm.setBean(h);
					
				request.setAttribute("Message", "ยืนยันรายการ เรียบร้อยแล้ว");
			}else{
				//error 
			}
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
	
	public ActionForward saveDeliveryDateAction(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("saveDeliveryDateAction");
		ConfPickStockForm aForm = (ConfPickStockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			ReqPickStock h = aForm.getBean();
			h.setUpdateUser(user.getUserName());
		
			//update delivery date ,total_ctn
			ConfPickStockDAO.confirmDeliveryStockIssue(conn, h);
			
			conn.commit();
			
			//new search
			h.setNewReq(true);
			h.setNewSearch(true);
	        h = searchBypage(conn, h, request);
	        
			request.getSession().setAttribute("results", h.getItems());
			
			aForm.setBean(h);
				
			request.setAttribute("Message", "บันทึกรายการ เรียบร้อยแล้ว");
			
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
	
	public ActionForward saveAction(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("saveAction");
		ConfPickStockForm aForm = (ConfPickStockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		boolean validate = true;
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			ReqPickStock h = aForm.getBean();
			h.setStatus(ConfPickStockDAO.STATUS_BEF);//BEF
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
					   
					   //Check case Exception
					   if(Utils.convertStrToInt(p.getIssueQty()) > Utils.convertStrToInt(p.getQty())){
						  // validate = false;
					   }                              

					   p.setStatus(ConfPickStockDAO.STATUS_BEF);//BEF
					   p.setUpdateUser(user.getUserName());
						
					   ConfPickStockDAO.updateStockIssueItemByPK(conn, p);
					}
			}

			if(validate){
			
				conn.commit();
				
				//Clear session 
				request.getSession().setAttribute("results", null);
				request.getSession().setAttribute("dataSaveMapAll", null);
				request.getSession().setAttribute("totalAllQty",null);
				request.getSession().setAttribute("curPageQtyMap",null);
				
				//new search
				h.setNewReq(true);
				h.setNewSearch(true);
				
		        h = searchBypage(conn, h, request);
		        
				request.getSession().setAttribute("results", h.getItems());
				
				aForm.setBean(h);
					
				request.setAttribute("Message", "ยืนยันรายการ เรียบร้อยแล้ว");
			}else{
				//error 
			}
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
	
	public ActionForward cancelAction(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("cancelAction");
		ConfPickStockForm aForm = (ConfPickStockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			ReqPickStock h = aForm.getBean();
			h.setStatus(ConfPickStockDAO.STATUS_CANCEL);//ISSUE
			h.setUpdateUser(user.getUserName());
		
			//update status=cancel stock_issue head
			ConfPickStockDAO.updateStausStockIssue(conn, h);
			//update status=cancel stock item
			ConfPickStockDAO.updateStatusStockIssueItemByIssueReqNo(conn, h);
	
			conn.commit();
			
			//new search
			h.setNewReq(false);
			h.setNewSearch(true);
	        h = searchBypage(conn, h, request);
	        
			request.getSession().setAttribute("results", h.getItems());
			
			aForm.setBean(h);
				
			request.setAttribute("Message", "ยกเลิกรายการ เรียบร้อยแล้ว");
			
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
	
	public ActionForward print(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response) {
		
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
				parameterMap.put("p_wareHouse", h.getWareHouse());
				
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
	
	public ActionForward printMini(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response) {
		
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
				parameterMap.put("p_title", "ฟอร์มยืนยัน การเบิกสินค้า");
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
				parameterMap.put("p_wareHouse", h.getWareHouse());
				
				//Gen Report
				String fileName = "conf_pick_mini_report";
				String fileJasper = BeanParameter.getReportPath() + fileName;
				
				conn = DBConnection.getInstance().getConnection();
				ReqPickStock  pAllItem = ConfPickStockDAO.getStockIssueItemCase4ReportMini(conn, h);//
				List items = pAllItem.getItems();
				
				parameterMap.put("p_total_qty", pAllItem.getTotalQty()+"");
				parameterMap.put("p_total_box", h.getTotalCtn()+"");
				
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
	
	public ActionForward printBillMini(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response) {
		
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
				parameterMap.put("p_title", "ฟอร์ม ใบเดินบิล");
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
				parameterMap.put("p_wareHouse", h.getWareHouse());
				
				//Gen Report
				String fileName = "conf_pick_bill_mini_report";
				String fileJasper = BeanParameter.getReportPath() + fileName;
				
				conn = DBConnection.getInstance().getConnection();
				ReqPickStock  pAllItem = ConfPickStockDAO.getStockIssueItemCase4ReportBillMini(conn, h);//
				List items = pAllItem.getItems();
				
				parameterMap.put("p_total_qty", pAllItem.getTotalQty()+"");
				parameterMap.put("p_total_box", h.getTotalCtn()+"");
				
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
	
public ActionForward printBillMiniAll(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response) {
		
		logger.debug("printBillMiniAll");
		ConfPickStockForm reportForm = (ConfPickStockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		ReportUtilServlet reportServlet = new ReportUtilServlet();
		HashMap parameterMap = new HashMap();
		ResourceBundle bundle = BundleUtil.getBundle("SystemElements", new Locale("th", "TH"));
		Connection conn = null;
		String issueReqNoAll = "";
		String issueReqNoAllDisp = "";
		try {
			String fileType = SystemElements.PDF;
			logger.debug("fileType:"+fileType);
            //Get lineChk
		    String[] issueReqNoArr= request.getParameterValues("linechk");
		    logger.debug("issueReqNoArr length:"+issueReqNoArr.length);
            for(int i=0;i<issueReqNoArr.length;i++){
		    	
		    	if(i==issueReqNoArr.length-1){
		    		issueReqNoAll += "'"+issueReqNoArr[i]+"'";
		    		issueReqNoAllDisp += issueReqNoArr[i]+"  ";
		    	}else{
		    		issueReqNoAll +="'"+issueReqNoArr[i]+"',";
			    	issueReqNoAllDisp +=issueReqNoArr[i]+" / ";
		    	}
		    }
		    
			ReqPickStock h = reportForm.getBean();
			if(h != null){
				logger.debug("ReqPickStock:"+h);
				//Head
				parameterMap.put("p_title", "ฟอร์ม ใบเดินบิล(รวม)");
				parameterMap.put("p_issueReqNo", issueReqNoAllDisp);
				
				//Gen Report
				String fileName = "conf_pick_bill_mini_all_report";
				String fileJasper = BeanParameter.getReportPath() + fileName;
				
				conn = DBConnection.getInstance().getConnection();
				ReqPickStock  pAllItem = ConfPickStockDAO.getStockIssueItemCase4ReportBillMiniAll(conn, h,issueReqNoAll);//
				List items = pAllItem.getItems();
				parameterMap.put("p_total_qty", pAllItem.getTotalQty()+"");
				
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

	public ActionForward printByGroupCode(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		logger.debug("printByGroupCode: " + this.getClass());
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
				String fileName = "conf_pick_groupcode_report";
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
	
	public ActionForward exportToExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response) {
		logger.debug("exportToExcel: ");
		ConfPickStockForm reportForm = (ConfPickStockForm) form;
		Connection conn = null;
		StringBuffer h = new StringBuffer("");
		int colSpan = 6;
		try {
			ReqPickStock bean = reportForm.getBean();
			if(bean != null){
				//logger.debug("ReqPickStock:"+h);
				
				h.append(ExcelHeader.EXCEL_HEADER);
				
				h.append("<table border='1'> \n");
				h.append("<tr> \n");
				h.append("<td align='left' colspan='"+colSpan+"'>รายงานยืนยัน การเบิกสินค้า </td> \n");
				h.append("</tr> \n");
				
				h.append("<tr> \n");
				h.append("<td align='left' colspan='"+colSpan+"' >Issue Request Date:"+bean.getIssueReqDate()+
						" &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+
						" วันที่รับของ :"+bean.getNeedDate()+
						"</td> \n");
				h.append("</tr> \n");

				h.append("<td align='left' colspan='"+colSpan+"' >Issue Request No:"+bean.getIssueReqNo()+
						" &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+
						" ผู้เบิก :"+bean.getRequestor()+
						"</td> \n");
				h.append("</tr> \n");
				
				h.append("<td align='left' colspan='"+colSpan+"' >กลุ่มร้านค้า:"+bean.getCustGroup()+
						" &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"+
						" ร้านค้า :"+bean.getStoreCode()+"-"+bean.getStoreName()+
						"</td> \n");
				h.append("</tr> \n");
				
				h.append("<td align='left' colspan='"+colSpan+"' >Sub inventory:"+bean.getSubInv()+
						" &nbsp;&nbsp;&nbsp;"+
						" Store :"+bean.getStoreNo()+"&nbsp;&nbsp;&nbsp; สถานะ:"+bean.getStatusDesc()+
						"</td> \n");
				h.append("</tr> \n");

			    h.append("</table> \n");
			    
				//Gen Report
				conn = DBConnection.getInstance().getConnection();
				ReqPickStock pAllItem = ConfPickStockDAO.getStockIssueItemCaseNoEdit(conn, bean,0,true);//
				List<ReqPickStock> items = pAllItem.getItems();

				if(items != null && items.size() >0){
					h.append("<table border='1'> \n");
					h.append("<tr> \n");
						h.append("<td>GroupCode.</td> \n");
						h.append("<td>PensItem.</td> \n");
						h.append("<td>MaterialMaster</td> \n");
						h.append("<td>Barcode</td> \n");
						h.append("<td>Qty ที่จะเบิก.</td> \n");
						h.append("<td>Qty ที่เบิกได้จริง.</td> \n");
					h.append("</tr>");
					for(int i=0 ;i<items.size();i++){
						ReqPickStock item = items.get(i);
						h.append("<tr> \n");
							h.append("<td>"+item.getGroupCode()+"</td> \n");
							h.append("<td>"+item.getPensItem()+"</td> \n");
							h.append("<td>"+item.getMaterialMaster()+"</td> \n");
							h.append("<td class='text'>"+item.getBarcode()+"</td> \n");
							h.append("<td>"+item.getQty()+"</td> \n");
							h.append("<td>"+Utils.isNull(item.getIssueQty())+"</td> \n");
					    h.append("</tr>");
					}
					
					java.io.OutputStream out = response.getOutputStream();
					response.setHeader("Content-Disposition", "attachment; filename=data.xls");
					response.setContentType("application/vnd.ms-excel");
					
					Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
					w.write(h.toString());
				    w.flush();
				    w.close();
	
				    out.flush();
				    out.close();
				}
				
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
	
	public ActionForward exportBarcodeToExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response) {
		logger.debug("exportBarcodeToExcel: ");
		ConfPickStockForm reportForm = (ConfPickStockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		StringBuffer h = new StringBuffer("");
		int qty = 0;
		int n=0;
		try {
			ReqPickStock bean = reportForm.getBean();
			if(bean != null){
				//logger.debug("ReqPickStock:"+h);
				h.append(ExcelHeader.EXCEL_HEADER);
				
				//Gen Report
				conn = DBConnection.getInstance().getConnection();
				ReqPickStock pAllItem = ConfPickStockDAO.getStockIssueItemCaseNoEdit(conn, bean,0,true);//
				List<ReqPickStock> items = pAllItem.getItems();

				if(items != null && items.size() >0){
					h.append("<table border='1'> \n");
					h.append("<tr> \n");
					    h.append("<td>Barcode</td> \n");
						h.append("<td>MaterialMaster</td> \n");
						h.append("<td>PensItem.</td> \n");
					h.append("</tr>");
					for(int i=0 ;i<items.size();i++){
						ReqPickStock item = items.get(i);
						qty = item.getIssueQtyInt();
						for(n=0;n<qty;n++){
							h.append("<tr> \n");
							    h.append("<td class='text'>"+item.getBarcode()+"</td> \n");
								h.append("<td>"+item.getMaterialMaster()+"</td> \n");
								h.append("<td>"+item.getPensItem()+"</td> \n");
						    h.append("</tr>");
						}
					}
					
					java.io.OutputStream out = response.getOutputStream();
					response.setHeader("Content-Disposition", "attachment; filename=data.xls");
					response.setContentType("application/vnd.ms-excel");
					
					Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
					w.write(h.toString());
				    w.flush();
				    w.close();
	
				    out.flush();
				    out.close();
				}
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
