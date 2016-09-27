package com.isecinc.pens.web.pick;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import util.BeanParameter;
import util.BundleUtil;
import util.ExcelHeader;
import util.ReportUtilServlet;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.bean.ReqPickStock;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.ReqPickStockDAO;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;

/**
 * ReqPickStockAction
 * 
 * @author WITTY
 * 
 */
public class ReqPickStockAction extends I_Action {

	public ActionForward prepare2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepare2");
		ReqPickStockForm aForm = (ReqPickStockForm) form;
		Connection conn = null;
		//User user = (User) request.getSession().getAttribute("user");
		try {
			String wareHouse = Utils.isNull(request.getParameter("wareHouse"));
			String action = Utils.isNull(request.getParameter("action"));
			
			if("new".equals(action)){
				request.getSession().setAttribute("results", null);
				request.getSession().setAttribute("resultsView", null);
				request.getSession().setAttribute("groupCodeMap", null);
				request.getSession().setAttribute("custGroupList", null);
				request.getSession().setAttribute("itemsBarcodeErrorMap", null);
				request.getSession().setAttribute("groupCodeErrorMap", null);
				
				aForm.setResultsSearch(null);
				ReqPickStock ad = new ReqPickStock();
				ad.setWareHouse(wareHouse);
				aForm.setBean(ad);

			}else if("back".equals(action)){
				//Clear Session data
				request.getSession().setAttribute("results", null);
				request.getSession().setAttribute("resultsView", null);
				request.getSession().setAttribute("groupCodeMap", null);
				request.getSession().setAttribute("custGroupList", null);
				request.getSession().setAttribute("itemsBarcodeErrorMap", null);
				request.getSession().setAttribute("groupCodeErrorMap", null);
				
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
		//User user = (User) request.getSession().getAttribute("user");
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
			ad.setWareHouse(aForm.getBean().getWareHouse());
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
		//User user = (User) request.getSession().getAttribute("user");
		try {
			//save old criteria
			aForm.setBeanCriteria(aForm.getBean());
			
			conn = DBConnection.getInstance().getConnection();
			
			String process = Utils.isNull(request.getParameter("process"));
            String issueReqNo = Utils.isNull(request.getParameter("issueReqNo"));
            String issueReqStatus = Utils.isNull(request.getParameter("issueReqStatus"));
            String mode = Utils.isNull(request.getParameter("mode"));
            
            logger.debug("Mode :"+mode);
            
            if( "confirm".equals(mode)){
            	request.getSession().setAttribute("results", null);
            	request.getSession().setAttribute("resultsView", null);
				request.getSession().setAttribute("groupCodeMap", null);
				request.getSession().setAttribute("itemsBarcodeErrorMap", null);
				request.getSession().setAttribute("groupCodeErrorMap", null);
				
				ReqPickStock p = new ReqPickStock();
				p.setIssueReqNo(issueReqNo);
				p.setNewSearch(true);//new search
				p.setNewReq(false);
				p.setStatus(issueReqStatus);
				p.setModeConfirm(true);
				p.setModeEdit(false);
				p.setWareHouse(aForm.getBean().getWareHouse());
				
				//Get Item and set data to session dataGroupCodeMapAll
				p = searchBypageCaseView(conn, p, request);
				aForm.setBean(p);
				
            }else  if( "view".equals(mode)){
            	request.getSession().setAttribute("results", null);
            	request.getSession().setAttribute("resultsView", null);
				request.getSession().setAttribute("groupCodeMap", null);
				request.getSession().setAttribute("itemsBarcodeErrorMap", null);
				request.getSession().setAttribute("groupCodeErrorMap", null);
				
				ReqPickStock p = new ReqPickStock();
				p.setIssueReqNo(issueReqNo);
				p.setNewSearch(true);//new search
				p.setNewReq(false);
				p.setStatus(issueReqStatus);
				p.setModeConfirm(false);
				p.setModeEdit(false);
				p.setCanExport(true);
				p.setWareHouse(aForm.getBean().getWareHouse());
				
				//Get Item and set data to session dataGroupCodeMapAll
				p = searchBypageCaseView(conn, p, request);
				logger.debug("totalQty:"+p.getTotalQty());
				aForm.setBean(p);	
				
            }else{
				if( !"".equals(issueReqNo)){
					logger.debug("prepare edit issueReqNo:"+issueReqNo);
					request.getSession().setAttribute("results", null);
					request.getSession().setAttribute("resultsView", null);
					request.getSession().setAttribute("groupCodeMap", null);
					request.getSession().setAttribute("itemsBarcodeErrorMap", null);
					request.getSession().setAttribute("groupCodeErrorMap", null);
					
					ReqPickStock p = new ReqPickStock();
					p.setIssueReqNo(issueReqNo);
					p.setNewSearch(true);//new search
					p.setNewReq(false);
					p.setStatus(issueReqStatus);
					p.setCanExport(true);
					p.setWareHouse(aForm.getBean().getWareHouse());
					p.setDisableCustGroup(true);
					
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
					
				}else{
					logger.debug("prepare Add issueReqNo");
					request.getSession().setAttribute("results", null);
					request.getSession().setAttribute("resultsView", null);
					request.getSession().setAttribute("groupCodeMap", null);
					request.getSession().setAttribute("itemsBarcodeErrorMap", null);
					request.getSession().setAttribute("groupCodeErrorMap", null);
					
					ReqPickStock p = new ReqPickStock();
					p.setWareHouse(aForm.getBean().getWareHouse());
					p.setIssueReqDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));

					//Test value
					/*p.setCustGroup("020049");
					p.setRequestor("Witty");
					p.setNeedDate("30/11/2557");
					p.setStoreCode("020049-10");
					p.setStoreName("BigC - สาขาบางพลี");
					p.setSubInv("G806");
					p.setStoreNo("11106");
					p.setRemark("xxxx พยย้า");*/
					/**********************/

					p.setCanEdit(true);
					
					p.setNewReq(true);
					p.setModeConfirm(false);
					p.setModeEdit(true);
					p.setNewSearch(true);//new search
	
					//search by page 
	                p = searchBypage(conn, p, request);
					aForm.setBean(p);
				}
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
	
	public ActionForward searchByCustGroup(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		ReqPickStockForm aForm = (ReqPickStockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		Connection conn = null;
		logger.debug("** searchByCustGroup **");
		try {
			conn = DBConnection.getInstance().getConnection();
		    //clear session
			request.getSession().setAttribute("results", null);
			request.getSession().setAttribute("resultsView", null);
			request.getSession().setAttribute("groupCodeMap", null);
			request.getSession().setAttribute("itemsBarcodeErrorMap", null);
			request.getSession().setAttribute("groupCodeErrorMap", null);
			
			ReqPickStock p = aForm.getBean();
			/*p.setWareHouse(aForm.getBean().getWareHouse());
			p.setIssueReqDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));

			p.setCanEdit(true);
			p.setNewReq(true);
			p.setModeConfirm(false);
			p.setModeEdit(true);
			p.setNewSearch(true);//new search
*/
			//search by page 
            p = searchBypage(conn, p, request);
			aForm.setBean(p);
			
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
		return mapping.findForward("prepare");
	}
	
	private ReqPickStock searchBypage(Connection conn,ReqPickStock p, HttpServletRequest request) {
		int totalRow = 0;
		int totalPage = 0;
		int pageNumber = 1;
		int totalQtyAll = 0;
		int totalCurPageQty = 0;
		int totalQtyNotInCurPage = 0;
		boolean newSearch = p.isNewSearch();
		List<ReqPickStock> results = new ArrayList<ReqPickStock>();
		try{
			logger.debug("pageNumber["+request.getParameter("pageNumber")+"]");
			logger.debug("newReq["+p.isNewReq()+"]");
			
			if(p.isNewSearch()){
				request.getSession().setAttribute("results", null);
				
				//Case newsearch Recalc page
				pageNumber = 1;
				
				//Display data cannot to save
				 if( Utils.isNull(p.getStatus()).equals(PickConstants.STATUS_ISSUED)
					|| Utils.isNull(p.getStatus()).equals(PickConstants.STATUS_POST)
					|| Utils.isNull(p.getStatus()).equals(PickConstants.STATUS_CANCEL)
					|| p.isModeConfirm()){
					 
					 totalRow = ReqPickStockDAO.getTotalRowInStockIssueItemCaseNoEditByGroupCode(conn, p);
				 }else{ 
					 totalRow = ReqPickStockDAO.getTotalRowInStockFinishGroupByGroupCode(conn,p);
				 }
				 
				totalPage = Utils.calcTotalPage(totalRow,PickConstants.REQ_PICK_PAGE_SIZE);
				request.getSession().setAttribute("totalPage", totalPage);
				request.getSession().setAttribute("totalRow", totalRow);

			}else{
			    pageNumber = !Utils.isNull(request.getParameter("pageNumber")).equals("")?Utils.convertStrToInt(request.getParameter("pageNumber")):1;
			}

			logger.debug("*** pageNumber["+pageNumber+"]********");
			
            //** Search Data and Display **/
			p = ReqPickStockDAO.searchReqPickStock(conn,p,false);//head only
			p.setNewSearch(newSearch);
			
			ReqPickStock pAllItem = null;
			
			if(  Utils.isNull(p.getStatus()).equals(PickConstants.STATUS_ISSUED)
				 || Utils.isNull(p.getStatus()).equals(PickConstants.STATUS_POST)
				 || Utils.isNull(p.getStatus()).equals(PickConstants.STATUS_CANCEL)
				 || p.isModeConfirm()){
				  
				// pAllItem = ReqPickStockDAO.getGroupCodeInStockListCaseNoEdit(conn, p,pageNumber,groupCodeMap,false);//
				
			}else{
				 pAllItem = ReqPickStockDAO.getGroupCodeInStockList(conn, p,pageNumber);
				 results = pAllItem.getItems();//display normal
		    }

			if( !Utils.isNull(p.getIssueReqNo()).equals("") ){
				 totalQtyAll =  ReqPickStockDAO.getTotalQtyInStockIssueItem(conn,p);
			 }

			logger.debug("totalQtyAll:"+totalQtyAll);
			 
			if (results != null  && results.size() >0) {
				request.getSession().setAttribute("results", results);
			} else {
				request.getSession().setAttribute("results", null);
				request.setAttribute("Message", "ไม่พบข่อมูล");
				//p.setCanEdit(false);
			}
			
			logger.debug("totalQtyAll["+totalQtyAll+"]");
			
			p.setTotalQty(totalQtyAll);
			p.setTotalQtyNotInCurPage(totalQtyNotInCurPage);
			
			//Validate Button
			if(PickConstants.STATUS_POST.equals(p.getStatus())){
				p.setCanPrint(true);
			}else{
				p.setCanPrint(false);
			}
			
			//set after first run = false
			p.setNewSearch(false);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return p;
	}

	private ReqPickStock searchBypageCaseView(Connection conn,ReqPickStock p, HttpServletRequest request) {
		int totalRow = 0;
		int totalPage = 0;
		int pageNumber = 1;
		int totalQtyAll = 0;
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

				totalRow = ReqPickStockDAO.getTotalRowInStockIssueItemCaseNoEditByItem(conn, p);
				totalQtyAll =  ReqPickStockDAO.getTotalQtyInStockIssueItem(conn,p);	
				p.setTotalQty(totalQtyAll);
				
				totalPage = (totalRow/ PickConstants.REQ_PICK_PAGE_SIZE)+1;
				request.getSession().setAttribute("totalPage", totalPage);
				request.getSession().setAttribute("totalRow", totalRow);

			}else{
			    pageNumber = !Utils.isNull(request.getParameter("pageNumber")).equals("")?Utils.convertStrToInt(request.getParameter("pageNumber")):1;
			}

			logger.debug("*** pageNumber["+pageNumber+"]********");
			
            //** Search Data and Display **/
			p = ReqPickStockDAO.searchReqPickStock(conn,p,false);//head only
			p.setNewSearch(newSearch);

			ReqPickStock  pAllItem = ReqPickStockDAO.getStockIssueItemCaseNoEdit(conn, p,pageNumber,false);//
			results = pAllItem.getItems();

			
			if (results != null  && results.size() >0) {
				request.getSession().setAttribute("resultsView", results);
			} else {
				request.getSession().setAttribute("resultsView", null);
				request.setAttribute("Message", "ไม่พบข่อมูล");
			}
			
			p.setTotalQtyNotInCurPage(totalQtyNotInCurPage);
				
			//Validate Button
			if(PickConstants.STATUS_POST.equals(p.getStatus())){
				p.setCanPrint(true);
			}else{
				p.setCanPrint(false);
			}
			
			//set after first run = false
			p.setNewSearch(false);
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
		ReqPickStockForm f = (ReqPickStockForm) form;
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

			//set Disp nextPage
			ReqPickStock p = searchBypage(conn, aForm.getBean(), request);
			
			//logger.debug("1totalQty:"+p.getTotalQty());
			
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
	

	public ActionForward searchFilter(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		ReqPickStockForm aForm = (ReqPickStockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		Connection conn = null;
		logger.debug("** SearchFilter **");
		try {
			List<ReqPickStock> reqPickListOld = (List)request.getSession().getAttribute("results");
			int oldtotalPage = ((Integer)request.getSession().getAttribute("totalPage")).intValue();
			int oldtotalRow = ((Integer)request.getSession().getAttribute("totalRow")).intValue();
			   
			
			conn = DBConnection.getInstance().getConnection();

			ReqPickStock searchNewByGroupCode  = aForm.getBean();
			searchNewByGroupCode.setNewSearch(true);
			
			ReqPickStock p = searchBypage(conn, searchNewByGroupCode, request);
			
		    if(p.getItems() != null && p.getItems().size() >0){
				//set to form
				aForm.setBean(p);
		    }else{
		    	
		    	msg ="ไม่พบสินค้าคงเหลือใน GroupCode["+p.getGroupCode()+"]";
		    	p.setGroupCode("");
		    	aForm.setBean(p);
		    	
		    	request.getSession().setAttribute("results", reqPickListOld);
		    	request.getSession().setAttribute("totalPage", oldtotalPage);
				request.getSession().setAttribute("totalRow", oldtotalRow);
				
		    }

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
		return mapping.findForward("search");
	}
	
	public ActionForward searchCaseView(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ReqPickStockForm aForm = (ReqPickStockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();

			//set Disp nextPage
			ReqPickStock p = searchBypageCaseView(conn, aForm.getBean(), request);
			
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
		return mapping.findForward("search");
	}

	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		ReqPickStockForm aForm = (ReqPickStockForm) form;
		try {
			conn = DBConnection.getInstance().getConnection();
            aForm.getBean().setNewSearch(false);
            aForm.getBean().setNewReq(false);
			
            //update 2 requestor ,remark ,need_date
            ReqPickStockDAO.updateStockIssue(conn, aForm.getBean());
            
			search(aForm, request, response);
			
			//set disable custGroup ,storeCode
			aForm.getBean().setDisableCustGroup(true);
			
			request.setAttribute("Message", "บันทึกข้อมูลเรียบร้อยแล้ว");
		} catch (Exception e) {
			//conn.rollback();
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


	public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("cancel");
		ReqPickStockForm aForm = (ReqPickStockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			ReqPickStock h = aForm.getBean();
			h.setStatus(ReqPickStockDAO.STATUS_CANCEL);//CANCEL
			h.setUpdateUser(user.getUserName());
		
			//update DB
			ReqPickStockDAO.updateStausStockIssue(conn, h);
			ReqPickStockDAO.updateStatusStockIssueItem(conn, h);
			
			//new search
			h.setNewSearch(true);
			h.setNewReq(false);
			h.setCanCancel(false);
			h.setCanEdit(false);
			
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
			h.setStatus(ReqPickStockDAO.STATUS_POST);//POST 
			h.setUpdateUser(user.getUserName());
		
			//update DB
			ReqPickStockDAO.updateStausStockIssue(conn, h);
			ReqPickStockDAO.updateStatusStockIssueItem(conn, h);
			
			conn.commit();
			
			//new search
			request.setAttribute("action", "newsearch");
			h.setNewReq(false);
			h.setNewSearch(true);
	        h = searchBypageCaseView(conn, h, request);
	        
			request.getSession().setAttribute("resultsView", h.getItems());
			
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
	
	public ActionForward printPostRequestReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		logger.debug("Search for report : " + this.getClass());
		ReqPickStockForm reportForm = (ReqPickStockForm) form;
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
				String fileName = "post_request_report";
				String fileJasper = BeanParameter.getReportPath() + fileName;
				conn = DBConnection.getInstance().getConnection();
				ReqPickStock pAllItem = ReqPickStockDAO.getStockIssueItemCaseNoEdit(conn, h,0,true);//
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
	
	public ActionForward exportToExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		logger.debug("exportToExcel: ");
		ReqPickStockForm reportForm = (ReqPickStockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		StringBuffer h = new StringBuffer("");
		int colSpan = 6;
		try {
	
			String fileType = SystemElements.PDF;
			logger.debug("fileType:"+fileType);

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
				ReqPickStock pAllItem = ReqPickStockDAO.getStockIssueItemCaseNoEdit(conn, bean,0,true);//
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
