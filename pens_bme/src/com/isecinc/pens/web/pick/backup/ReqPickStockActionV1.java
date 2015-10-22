package com.isecinc.pens.web.pick.backup;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.PickStock;
import com.isecinc.pens.bean.ReqPickStock;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.ReqPickStockDAO;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.pick.ReqPickStockForm;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class ReqPickStockActionV1 extends I_Action {

	public ActionForward prepare2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepare2");
		ReqPickStockForm aForm = (ReqPickStockForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action"));
			if("new".equals(action)){
				request.getSession().setAttribute("results", null);
				request.getSession().setAttribute("dataSaveMapAll", null);
				request.getSession().setAttribute("dataEditMapAll", null);
				request.getSession().setAttribute("totalQtyByPageMap", null);
				request.getSession().setAttribute("statusIssueReqList", null);
				request.getSession().setAttribute("pickTypeList", null);
				request.getSession().setAttribute("custGroupList", null);
				request.getSession().setAttribute("totalAllQty",null);
				
				aForm.setResultsSearch(null);
				ReqPickStock ad = new ReqPickStock();
				//ad.setTransactionDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				
				aForm.setBean(ad);
				
				// Balance Onhand
				Date startDate = new Date();
				//OnhandDAO.processBanlanceOnhandFromBarcode(user.getUserName());
                logger.debug("processBanlanceOnhandFromBarcode>>Total Time:"+(new Date().getTime()-startDate.getTime()));
                
			}else if("back".equals(action)){
				aForm.setBean(aForm.getBeanCriteria());
				aForm.setResultsSearch(ReqPickStockDAO.searchHead(aForm.getBean()));
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
		ReqPickStockForm aForm = (ReqPickStockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			aForm.setResultsSearch(ReqPickStockDAO.searchHead(aForm.getBean()));
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
		ReqPickStockForm aForm = (ReqPickStockForm) form;
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
		ReqPickStockForm aForm = (ReqPickStockForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			//save old criteria
			aForm.setBeanCriteria(aForm.getBean());
			
			conn = DBConnection.getInstance().getConnection();
			
			String process = Utils.isNull(request.getParameter("process"));
            String issueReqNo = Utils.isNull(request.getParameter("issueReqNo"));
            String issueReqStatus = Utils.isNull(request.getParameter("issueReqStatus"));
            
			if( !"".equals(issueReqNo)){
				logger.debug("prepare edit issueReqNo:"+issueReqNo);
				request.getSession().setAttribute("results", null);
				request.getSession().setAttribute("dataSaveMapAll", null);
				request.getSession().setAttribute("dataEditMapAll", null);
				request.getSession().setAttribute("totalAllQty",null);
				
				ReqPickStock p = new ReqPickStock();
				p.setIssueReqNo(issueReqNo);
				
				//new search
				request.setAttribute("action", "newsearch");
				p.setNewReq(false);
				
			    //search data item old for Display
			    request.getSession().setAttribute("dataEditMapAll", ReqPickStockDAO.searchPickStockItemByPKAllToMap(conn, p));
			    
				//search by page
				p.setStatus(issueReqStatus);
				if("confirm".equalsIgnoreCase(process)){
					p.setModeConfirm(true);
					p.setModeEdit(false);
				}else{
					p.setModeConfirm(false);
					p.setModeEdit(true);
				}
				
				ReqPickStock pNew = searchBypage(conn,p,request);
				aForm.setBean(pNew);
				
			}else{
				logger.debug("prepare new issueReqNo");
				request.getSession().setAttribute("results", null);
				
				ReqPickStock p = new ReqPickStock();
				p.setIssueReqDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				p.setCanEdit(true);
				
				p.setNewReq(true);
				p.setModeConfirm(false);
				p.setModeEdit(true);
					
				//new search
				request.setAttribute("action", "newsearch");
				//search by page 
                p = searchBypage(conn,p,request);
				
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
		String action = "";
		int totalQtyAll = 0;
		int totalCurPageQty = 0;
		int totalQtyNotInCurPage = 0;
	
		try{
			action = Utils.isNull(request.getParameter("action")).equals("")?Utils.isNull(request.getAttribute("action")):Utils.isNull(request.getParameter("action"));

			logger.debug("pageNumber["+request.getParameter("pageNumber")+"]");
			logger.debug("newReq["+p.isNewReq()+"]");
			logger.debug("action["+action+"]");
			
			if("newsearch".equals(action)){
				request.setAttribute("action", "newsearch");
				request.getSession().setAttribute("results", null);
				request.getSession().setAttribute("totalQtyByPageMap", null);
				request.getSession().setAttribute("totalAllQty",null);
				
				//Case newsearch Recalc page
				pageNumber = 1;
				
				//Display data save
				 if( Utils.isNull(p.getStatus()).equals(PickConstants.STATUS_ISSUED)
					|| Utils.isNull(p.getStatus()).equals(PickConstants.STATUS_POST)
					|| Utils.isNull(p.getStatus()).equals(PickConstants.STATUS_CANCEL)
					|| p.isModeConfirm()){
					 
					// totalRow = //PickStockDAO.getCountPickStockItemAllCaseNoEdit(conn, p);
					 
					// totalQtyAll = PickStockDAO.getTotalQtyInPickStock(conn, p);
				 }else{
					  totalRow = ReqPickStockDAO.getTotalRowInStockFinish(conn);
					 
					  //For case edit sum qty 
					 if( !"".equals(Utils.isNull(p.getIssueReqNo()))){
					    totalQtyAll = ReqPickStockDAO.getTotalQtyInStockIssueItem(conn, p);
					 }
				 }
				 
				totalPage = (totalRow/ PickConstants.REQ_PICK_PAGE_SIZE)+1;
				request.getSession().setAttribute("totalPage", totalPage);
				request.getSession().setAttribute("totalRow", totalRow);

			}else{
			    pageNumber = !Utils.isNull(request.getParameter("pageNumber")).equals("")?Utils.convertStrToInt(request.getParameter("pageNumber")):1;
			}

            //** Search Data and Display **/
			
			p = ReqPickStockDAO.searchReqPickStock(conn,p,false);//head only
			ReqPickStock pAllItem = null;
			if(  Utils.isNull(p.getStatus()).equals(PickConstants.STATUS_ISSUED)
				 || Utils.isNull(p.getStatus()).equals(PickConstants.STATUS_POST)
				 || Utils.isNull(p.getStatus()).equals(PickConstants.STATUS_CANCEL)
				 || p.isModeConfirm()){
				  
				 //pAllItem = PickStockDAO.searchPickStockItemByPageByPKCaseNoEdit(conn, p,pageNumber,PickConstants.ONHAND_PAGE_SIZE);//
				 //set 
				 totalCurPageQty = pAllItem.getTotalQty();
			}else{
				
				 pAllItem = ReqPickStockDAO.searchReqPickStockItemAllByPageCaseEdit(conn, p, pageNumber, PickConstants.REQ_PICK_PAGE_SIZE);
				 totalCurPageQty = pAllItem.getTotalQty();
		    }
			
			/** Calc SumQty **/
			if(request.getSession().getAttribute("totalAllQty") ==null){
				request.getSession().setAttribute("totalAllQty",totalQtyAll);
				totalQtyNotInCurPage = totalQtyAll -totalCurPageQty;
			}else{
				logger.debug("totalAllQty:"+(String)request.getSession().getAttribute("totalAllQty"));
				totalQtyAll = Integer.parseInt((String)request.getSession().getAttribute("totalAllQty"));
				totalQtyNotInCurPage = totalQtyAll -totalCurPageQty;
			}
			
			List<ReqPickStock> results = pAllItem.getItems();
			
			if (results != null  && results.size() >0) {
				request.getSession().setAttribute("results", results);
			} else {
				request.getSession().setAttribute("results", null);
				request.setAttribute("Message", "ไม่พบข่อมูล");
			}
			
			logger.debug("totalQtyAll["+totalQtyAll+"]");
			
			p.setTotalQty(totalQtyAll);
			p.setTotalQtyNotInCurPage(totalQtyNotInCurPage);
			
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
		ReqPickStockForm summaryForm = (ReqPickStockForm) form;
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
		ReqPickStockForm aForm = (ReqPickStockForm) form;
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
			
			logger.debug("1totalQty:"+p.getTotalQty());
			
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

	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		ReqPickStockForm aForm = (ReqPickStockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
            //Set Data from screen to List
			Map<String, ReqPickStock> dataSaveMapAll = setDataSaveMap(request,aForm);
			//ReqPickStock Head
			ReqPickStock h = aForm.getBean();
			h.setCreateUser(user.getUserName());
			h.setUpdateUser(user.getUserName());
			
			ReqPickStock resultProcess = null;//ReqPickStockDAO.save(conn,h,dataSaveMapAll);
			logger.debug("result save :"+resultProcess.isResultProcess());
			
			if(resultProcess.isResultProcess()){
			
			    //new search
				request.setAttribute("action", "newsearch");
				h.setNewReq(false);
				ReqPickStock p = searchBypage(conn, h, request);
				
				logger.debug(""+p.isCanCancel());
				
				p.setCanConfirm(false);
				
				aForm.setBean(p);
				
				request.getSession().setAttribute("results", p.getItems());
			    
				conn.commit();
				
			    request.setAttribute("Message", "บันทึกข้อมูลเรียบร้อยแล้ว");
			}else{
				
				conn.rollback();
				request.setAttribute("Message", "ไม่สามารถบันทึกข้อมูลได้ โปรดตรวจสอบยอด Onhand");
				
				//Mapping to Display
				/*List<ReqPickStock> screenItemList = (List<ReqPickStock>)request.getSession().getAttribute("results");
				if(screenItemList != null && screenItemList.size() >0){
					for(int t=0;t<screenItemList.size();t++){
						ReqPickStock item = (ReqPickStock)screenItemList.get(t);
						 String key = l.getBarcode()+"_"+l.getMaterialMaster()+"_"+l.getGroupCode()+"_"+l.getPensItem();
						 
					}
				}*/
				//logger.debug("resultProcess.getItems() size:"+resultProcess.getItems().size());
				//request.getSession().setAttribute("results", resultProcess.getItems());
				
				request.setAttribute("action", "newsearch");
				h.setNewReq(true);
				ReqPickStock p = searchBypage(conn, h, request);
				request.getSession().setAttribute("results", p.getItems());
				
				
			
			}
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

	private Map<String, ReqPickStock> setDataSaveMap(HttpServletRequest request,ReqPickStockForm aForm){
	    User user = (User) request.getSession().getAttribute("user");
	    //Data Disp Per Page
	    List<ReqPickStock> results = (List<ReqPickStock>)request.getSession().getAttribute("results"); //data per page
		
	    //dataSaveMap All
	    Map<String, ReqPickStock> dataSaveMapAll = new HashMap<String, ReqPickStock>();
	    if(request.getSession().getAttribute("dataSaveMapAll")!= null ){
	       dataSaveMapAll = (Map)request.getSession().getAttribute("dataSaveMapAll");
	    }
	    
	    //dataEditMap All
	    Map<String, ReqPickStock> dataEditMapAll = new HashMap<String, ReqPickStock>();
	    if(request.getSession().getAttribute("dataEditMapAll")!= null ){
	    	dataEditMapAll = (Map)request.getSession().getAttribute("dataEditMapAll");
	    }
	    
		logger.debug("dataEditMapAll Size:"+(dataEditMapAll!=null?dataEditMapAll.size():0));
		logger.debug("dataSaveMapAll Size:"+(dataSaveMapAll!=null?dataSaveMapAll.size():0));
		logger.debug("results Size:"+(results!=null?results.size():0));
		
		String[] qty = request.getParameterValues("qty");
		
		//add value to Results
		if(results != null && results.size() > 0 && qty != null){
			for(int i=0;i<results.size();i++){
				 ReqPickStock l = (ReqPickStock)results.get(i);
				 l.setQty(qty[i]);
				 l.setUpdateUser(user.getUserName());
				 l.setCreateUser(user.getUserName());
				 
				 //set data to list disp
				 results.set(i, l);
				 
				 //Key Map  
				 String key = l.getBarcode()+"_"+l.getMaterialMaster()+"_"+l.getGroupCode()+"_"+l.getPensItem();
				 //check pens_items is old record
				 if(dataEditMapAll.get(key) != null){
					 dataSaveMapAll.put(key, l);
				 }else{
					 if( !Utils.isNull(l.getQty()).equals("")){
						 dataSaveMapAll.put(key, l);
					 }
				 }
			}//for
		}//if

		//data to display
		request.getSession().setAttribute("results",results);
		request.getSession().setAttribute("dataSaveMapAll",dataSaveMapAll);
		
		return dataSaveMapAll;
	}

	private Map<String, ReqPickStock> setDispResults(HttpServletRequest request,ReqPickStockForm aForm){
	    //Data Disp Per Page
	    List<ReqPickStock> results = (List<ReqPickStock>)request.getSession().getAttribute("results"); //data per page
		
	    //dataSaveMap All
	    Map<String, ReqPickStock> dataSaveMapAll = new HashMap<String, ReqPickStock>();
	    if(request.getSession().getAttribute("dataSaveMapAll")!= null ){
	       dataSaveMapAll = (Map)request.getSession().getAttribute("dataSaveMapAll");
	    }
	    
		logger.debug("dataSaveMapAll Size:"+(dataSaveMapAll!=null?dataSaveMapAll.size():0));
		logger.debug("results Size:"+(results!=null?results.size():0));

		String[] qty = request.getParameterValues("qty");
		
		//add value to Results
		if(results != null && results.size() > 0){
			for(int i=0;i<results.size();i++){
				ReqPickStock l = (ReqPickStock)results.get(i);
				//Key Map  
				 String key = l.getBarcode()+"_"+l.getMaterialMaster()+"_"+l.getGroupCode()+"_"+l.getPensItem();
				 if(dataSaveMapAll.get(key) != null){
					 ReqPickStock pOld = (ReqPickStock) dataSaveMapAll.get(key); 
				     l.setQty(pOld.getQty());
				 }
				 //set data to list disp
				 results.set(i, l);
			}//for
		}//if
	
		//data to display
		request.getSession().setAttribute("results",results);
		
		return dataSaveMapAll;
	}
	
	public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("cancel");
		ReqPickStockForm aForm = (ReqPickStockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			ReqPickStock h = aForm.getBean();
			//Step1 get Data From pickStockItem 
			List<ReqPickStock> pickStockItemList = ReqPickStockDAO.searchReqPickStockItemByIssueReqNo(conn, h);
			logger.debug("pickStockItemList:"+pickStockItemList.size());
			  
			//update status stock Pick to cancel
			h.setStatus(ReqPickStockDAO.STATUS_CANCEL);
			ReqPickStockDAO.updateStausStockIssue(conn, h);
			 
			 if(pickStockItemList !=null && pickStockItemList.size() >0){
				 for(int k=0;k<pickStockItemList.size();k++){
					 ReqPickStock p = (ReqPickStock)pickStockItemList.get(k);
					 p.setCreateUser(user.getUserName());
					 p.setUpdateUser(user.getUserName());
					 
					 //delete Pick Stock Line
					 //logger.debug("update barcode item:"+p.getPensItem()+",boxNo["+p.getBoxNo()+"]lineId:"+p.getLineId());

				     //update barcode item (status = C)
				     //p.setBarcodeItemStatus(PickConstants.STATUS_CLOSE);
				    //ReqPickStockDAO.updateStatusBarcodeItemModel(conn, p);
					   
				  }//for 2
			  }//if 
	
		
			 //search
			//new search
			request.setAttribute("action", "newsearch");
			h.setNewReq(false);
			
			request.getSession().setAttribute("results", h.getItems());
			aForm.setBean(h);
				
			conn.commit();
			request.setAttribute("Message", "ยกเลิกรายการ เรียบร้อยแล้ว");
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
            conn.rollback();
		}finally{
			if(conn != null){
			   conn.close();conn=null;
			}
		}
		return mapping.findForward("clear");
	}
	
	
	public ActionForward confirmAction(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("confirmAction");
		ReqPickStockForm aForm = (ReqPickStockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			ReqPickStock h = aForm.getBean();
			//Step1 get Data From pickStockItem 
			List<ReqPickStock> pickStockItemList = ReqPickStockDAO.searchReqPickStockItemByIssueReqNo(conn, h);
			logger.debug("pickStockItemList:"+pickStockItemList.size());
			  
			//update status stock Pick to cancel
			String curDateStr = Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			
			h.setStatus(ReqPickStockDAO.STATUS_ISSUED);
			//update DB
			ReqPickStockDAO.updateStausStockIssue(conn, h);
			
			conn.commit();
			
			//new search
			request.setAttribute("action", "newsearch");
			h.setNewReq(false);
	       // h = null//searchBypage(conn, h, request);
			request.getSession().setAttribute("results", h.getItems());
			
			aForm.setBean(h);
				
			request.setAttribute("Message", "ยืนยันรายการ เรียบร้อยแล้ว");
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}finally{
			if(conn != null){
			   conn.close();conn=null;
			}
		}
		return mapping.findForward("clear");
	}
	
	
	
	public ActionForward exportExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("exportExcel");
		ReqPickStockForm aForm = (ReqPickStockForm) form;
		//User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			
			ReqPickStock h = aForm.getBean();
			h = ReqPickStockDAO.searchReqPickStockItemByIssueReqNo4Report(conn, h);
			
			StringBuffer htmlTable = genHTML(h);	 
			
			java.io.OutputStream out = response.getOutputStream();
			response.setHeader("Content-Disposition", "attachment; filename="+Utils.genFileName("pick")+".xls");
			response.setContentType("application/vnd.ms-excel");
			
			Writer w = new BufferedWriter(new OutputStreamWriter(out,"UTF-8")); 
			w.write(htmlTable.toString());
		    w.flush();
		    w.close();

		    out.flush();
		    out.close();

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}finally{
			if(conn != null){
			   conn.close();conn=null;
			}
		}
		return mapping.findForward("clear");
	}
	
	
	
	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		ReqPickStockForm aForm = (ReqPickStockForm) form;
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

	private StringBuffer genHTML(ReqPickStock p){
		StringBuffer h = new StringBuffer("");
		try{
			//Header
			String title = "ใบเบิกเลขที่ :"+p.getIssueReqNo();
			
			h.append("<table border='1'> \n");
				h.append("<tr> \n");
				h.append("<td align='left' colspan='7'><b>"+title+" </b></td> \n");
				h.append("</tr> \n");
			h.append("</table> \n");

			if(p.getItems()!= null){
			    List<ReqPickStock> dataList =p.getItems();
			    //Job Id	Job Name	เลขที่กล่อง		Wacoal Mat.	Group Code	Pens Item	QTY ที่เบิก

				h.append("<table border='1'> \n");
				h.append("<tr> \n");
					 h.append("<td><b>Job Id</b></td> \n");
					 h.append("<td><b>Job Name</b></td> \n");
					 h.append("<td><b>เลขที่กล่อง </b></td> \n");
					 h.append("<td><b>Wacoal Mat</b></td> \n");
					 h.append("<td><b>Group Code</b></td> \n");
					 h.append("<td><b>Pens Item </b></td> \n");
					 h.append("<td><b>QTY ที่เบิก </b></td> \n");
				h.append("</tr> \n");
				
				for(int i=0;i<dataList.size();i++){
					ReqPickStock s = (ReqPickStock)dataList.get(i);
				   h.append("<tr> \n");
				  // h.append("<td>"+s.getJobId()+"&nbsp;</td> \n");
				  // h.append("<td>"+s.getJobName()+"</td> \n");
				  // h.append("<td>"+s.getBoxNo()+"</td> \n");
				   h.append("<td>"+s.getMaterialMaster()+"</td> \n");
				   h.append("<td>"+s.getGroupCode()+"&nbsp;</td> \n");
				   h.append("<td>"+s.getPensItem()+"</td> \n");
				   h.append("<td>"+s.getQty()+"</td> \n");
				   h.append("</tr>");
				}//for 
				
				h.append("<tr> \n");
				h.append("<td colspan='6' align='right'><b>Total</b></td> \n");
				h.append("<td><b>"+p.getTotalQty()+"</b></td> \n");
				h.append("</tr>");	
			h.append("</table> \n");
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
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
