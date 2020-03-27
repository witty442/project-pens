package com.isecinc.pens.web.pick;

import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.Barcode;
import com.isecinc.pens.bean.PickStock;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.AutoSubOutDAO;
import com.isecinc.pens.dao.BarcodeDAO;
import com.isecinc.pens.dao.PickStockDAO;
import com.isecinc.pens.dao.PickStockGroupDAO;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.web.autosubin.AutoSubInBean;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;
import com.pens.util.pdf.StampBoxNoPickAllBoxReportPdf;
import com.pens.util.pdf.StampBoxNoReportPdf;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class PickStockAction extends I_Action {
	public static int pageSize = 60;
	
	public ActionForward prepare2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepare2");
		PickStockForm aForm = (PickStockForm) form;
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
				PickStock ad = new PickStock();
				//ad.setTransactionDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				
				aForm.setBean(ad);
				 
			}else if("back".equals(action)){
				//init connection
				conn = DBConnection.getInstance().getConnection();
				aForm.setBean(aForm.getBeanCriteria());
				
				aForm.setResultsSearch(PickStockDAO.searchHead(conn,aForm.getBean(),false,1,pageSize));
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
		PickStockForm aForm = (PickStockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		int currPage = 1;
		boolean allRec = false;
		Connection conn = null;
		try {
			String action = Utils.isNull(request.getParameter("action"));
			logger.debug("action:"+action);
			//init connection
			conn = DBConnection.getInstance().getConnection();
			
			if("newsearch".equalsIgnoreCase(action) || "back".equalsIgnoreCase(action)){
				//case  back
				if("back".equalsIgnoreCase(action)){
					aForm.setBean(aForm.getBeanCriteria());
				}
				//default currPage = 1
				aForm.setCurrPage(currPage);
				
				//get Total Record
				aForm.setTotalRecord(PickStockDAO.searchTotaRecHead(conn,aForm.getBean()));
				//calc TotalPage
				aForm.setTotalPage(Utils.calcTotalPage(aForm.getTotalRecord(), pageSize));
				//calc startRec endRec
				int startRec = ((currPage-1)*pageSize)+1;
				int endRec = (currPage * pageSize);
			    if(endRec > aForm.getTotalRecord()){
				   endRec = aForm.getTotalRecord();
			    }
			    aForm.setStartRec(startRec);
			    aForm.setEndRec(endRec);
			    
				//get Items Show by Page Size
			    List<PickStock> items = PickStockDAO.searchHead(conn,aForm.getBean(),allRec,currPage,pageSize);
				aForm.setResultsSearch(items);
				
				if(items.size() <=0){
				   request.setAttribute("Message", "ไม่พบข้อมูล");
				   aForm.setResultsSearch(null);
				}
			}else{
				// Goto from Page
				currPage = Utils.convertStrToInt(request.getParameter("currPage"));
				logger.debug("currPage:"+currPage);
				
				//calc startRec endRec
				int startRec = ((currPage-1)*pageSize)+1;
				int endRec = (currPage * pageSize);
			    if(endRec > aForm.getTotalRecord()){
				   endRec = aForm.getTotalRecord();
			    }
			    aForm.setStartRec(startRec);
			    aForm.setEndRec(endRec);
			    
				//get Items Show by Page Size
			    List<PickStock> items = PickStockDAO.searchHead(conn,aForm.getBean(),allRec,currPage,pageSize);
				aForm.setResultsSearch(items);
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
			throw e;
		}finally{
			if(conn != null){
				conn.close();
			}
		}
		return mapping.findForward("search2");
	}
	
	public ActionForward clear2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear2");
		PickStockForm aForm = (PickStockForm) form;
		try {
			aForm.setResultsSearch(null);
			
			PickStock ad = new PickStock();
			//ad.setIssueReqDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			aForm.setBean(ad);
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("clear2");
	}
	
	public ActionForward printStampBoxNoReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		PickStockForm reportForm = (PickStockForm) form;
		try {
			reportForm.getBean().setBoxNo("");
			InputStream in= StampBoxNoPickAllBoxReportPdf.generate(request,reportForm.getBean());// 
			java.io.OutputStream out = response.getOutputStream();
			response.setHeader("Content-Disposition", "attachment; filename=stamp_box_no.pdf");
			response.setContentType("application/vnd.ms-excel");
			
			IOUtils.copy(in,out);

		    out.flush();
		    out.close();
		    
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
		} finally {
			try {
			
			} catch (Exception e2) {}
		}
		// return null;
		return null;
	}
	
	/**
	 * Prepare without ID
	 */
	@Deprecated
	protected String prepare(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String forward = "prepare";
		PickStockForm aForm = (PickStockForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			
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
	
	public ActionForward prepareAllBox(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareAllBox");
		PickStockForm aForm = (PickStockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		try {
			//save old criteria
			aForm.setBeanCriteria(aForm.getBean());
			
			conn = DBConnection.getInstance().getConnection();
			
			String process = Utils.isNull(request.getParameter("process"));
            String issueReqNo = Utils.isNull(request.getParameter("issueReqNo"));
            String issueReqStatus = Utils.isNull(request.getParameter("issueReqStatus"));
            
			if( !"".equals(issueReqNo)){
				logger.debug("prepare edit issueReqNo:"+issueReqNo +"issueReqStatus["+issueReqStatus+"]process["+process+"]");
				
				PickStock p = new PickStock();
				p.setIssueReqNo(issueReqNo);
				p.setPickType(PickConstants.PICK_TYPE_BOX);
				
				if("confirm".equalsIgnoreCase(process)){
					p.setModeConfirm(true);
					p.setModeEdit(false);
				}else{
					p.setModeConfirm(false);
					p.setModeEdit(true);
				}
				
				p = PickStockDAO.searchPickStock(conn, p, true);
				
				List<PickStock>  allList = new ArrayList<PickStock>();
				if(p.isModeConfirm() || PickConstants.STATUS_ISSUED.equals(issueReqStatus)){
					allList.addAll(p.getItems());
				}else{
					if(PickConstants.STATUS_CANCEL.equals(issueReqStatus)){
					   allList.addAll(p.getItems());
					}else{
					  allList.addAll(p.getItems());
					  allList.addAll(PickStockDAO.searchBarcoceItemStatusCloseW3(conn));
					}
				}
				
				p.setIssueReqStatus(issueReqStatus);
				
				aForm.setBean(p);
				aForm.setResults(allList);
               
			}else{
				logger.debug("prepare new issueReqNo");
				
				PickStock p = new PickStock();
				p.setIssueReqDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				p.setCanEdit(true);
				p.setCanConfirm(false);
				
				p.setNewReq(true);
				p.setModeConfirm(false);
				p.setModeEdit(true);
				
				aForm.setResults(PickStockDAO.searchBarcoceItemStatusCloseW3(conn));
				aForm.setBean(p);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
            conn.rollback();
		}finally{
			if(conn != null){
			   conn.close();conn=null;
			}
		}
		return mapping.findForward("prepareAllBox");
	}
	
	private PickStock searchBypage(Connection conn,PickStock p, HttpServletRequest request) {
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
				 if( Utils.isNull(p.getIssueReqStatus()).equals(PickConstants.STATUS_ISSUED)
					|| Utils.isNull(p.getIssueReqStatus()).equals(PickConstants.STATUS_CANCEL)
					|| p.isModeConfirm()){
					 
					 totalRow = PickStockDAO.getCountPickStockItemAllCaseNoEdit(conn, p);
					 
					 totalQtyAll = PickStockDAO.getTotalQtyInPickStock(conn, p);
				 }else{
					 totalRow = PickStockDAO.getCountPickStockItemAllCaseEdit(conn, p);
					 
					 if( !"".equals(Utils.isNull(p.getIssueReqNo()))){
					    totalQtyAll = PickStockDAO.getTotalQtyInPickStock(conn, p);
					 }
				 }
				 
				totalPage = (totalRow/ PickConstants.ONHAND_PAGE_SIZE)+1;
				request.getSession().setAttribute("totalPage", totalPage);
				request.getSession().setAttribute("totalRow", totalRow);

			}else{
			    pageNumber = !Utils.isNull(request.getParameter("pageNumber")).equals("")?Utils.convertStrToInt(request.getParameter("pageNumber")):1;
			}

            //** Search Data and Display **/
			p = PickStockDAO.searchPickStock(conn,p,false);//head only
			PickStock pAllItem = null;
			if( Utils.isNull(p.getIssueReqStatus()).equals(PickConstants.STATUS_ISSUED)
				 || Utils.isNull(p.getIssueReqStatus()).equals(PickConstants.STATUS_CANCEL)
				 || p.isModeConfirm()){
				  
				 pAllItem = PickStockDAO.searchPickStockItemByPageByPKCaseNoEdit(conn, p,pageNumber,PickConstants.ONHAND_PAGE_SIZE);//
				 //set 
				 totalCurPageQty = pAllItem.getTotalQty();
			}else{
				 pAllItem = PickStockDAO.searchPickStockItemAllByPageCaseEdit(conn, p,pageNumber,PickConstants.ONHAND_PAGE_SIZE);//
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
			
			List<PickStock> results = pAllItem.getItems();
			
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
		PickStockForm summaryForm = (PickStockForm) form;
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
		PickStockForm aForm = (PickStockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			
			//save data Prev Page to dataSaveList
			setDataSaveMap(request, aForm);
			 
			//set Disp nextPage
			PickStock p = searchBypage(conn, aForm.getBean(), request);
			
			//setDisp result
			setDispResults(request, aForm);
			
			logger.debug("1totalQty:"+p.getTotalQty());
			
			//set to form
			aForm.setBean(p);
			
			logger.debug("2totalQty:"+aForm.getBean().getTotalQty());
			
			request.setAttribute("Message", msg);
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
			logger.error(e.getMessage(),e);
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
	@Deprecated
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		PickStockForm aForm = (PickStockForm) form;
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

	public ActionForward saveBox(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("Save Box");
		Connection conn = null;
		PickStockForm aForm = (PickStockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Map<String,String> boxNoMapAll = new HashMap<String, String>();
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			//PickStock Head
			PickStock h = aForm.getBean();
			h.setCreateUser(user.getUserName());
			h.setUpdateUser(user.getUserName());
			
			//Set Items
			List<PickStock> itemList = new ArrayList<PickStock>();
		
			String[] lineId = request.getParameterValues("lineId");
			String[] boxNo = request.getParameterValues("boxNo");
			String[] jobId = request.getParameterValues("jobId");
			String[] qty = request.getParameterValues("qty");
			
			logger.debug("lineId:"+lineId.length);
			
			//add value to Results
			if(boxNo != null && boxNo.length > 0){
				for(int i=0;i<boxNo.length;i++){
					if( !Utils.isNull(lineId[i]).equals("")){
						 PickStock l = new PickStock();
						 l.setLineId(i+1);
						 l.setBoxNo(Utils.isNull(boxNo[i]));
						 l.setJobId(Utils.isNull(jobId[i]));
						 l.setQty(qty[i]);
						 l.setCreateUser(user.getUserName());
						 l.setUpdateUser(user.getUserName());
					       
						 itemList.add(l);
					}//if
				}//for
			}//if
			
			h.setItems(itemList);
			
			//Save DB
			PickStock resultPick = PickStockDAO.saveBox(conn,h);
			logger.debug("result save :"+resultPick.isResultProcess());
			
			if(resultPick.isResultProcess()){
				boxNoMapAll = resultPick.getBoxNoMapAll();
				
				/** Check all item ==ISSUE and Update Head == ISSUE **/
				if( !boxNoMapAll.isEmpty()){
					logger.debug("Validate Barcode Item amd update barcode Head");
					
					Iterator<String> its = boxNoMapAll.keySet().iterator();
					String boxNoTemp = "";
					while(its.hasNext()){
						boxNoTemp = its.next(); 
					    PickStockGroupDAO.processUpdateStatusBarcodeHead(conn,boxNoTemp,h.getCreateUser());
					}
				}
				
			    //new search
				PickStock p = PickStockDAO.searchPickStock(conn,resultPick,true);
				
				List<PickStock>  allList = new ArrayList<PickStock>();
				allList.addAll(p.getItems());
				allList.addAll(PickStockDAO.searchBarcoceItemStatusCloseW3(conn));
				
				logger.debug(""+p.isCanCancel());
				
				p.setCanConfirm(false);
				
				aForm.setBean(p);
				aForm.setResults(allList);
				
				conn.commit();
			    request.setAttribute("Message", "บันทึกข้อมูลเรียบร้อยแล้ว");
			}else{
				conn.rollback();
				request.setAttribute("Message", "ไม่สามารถบันทึกข้อมูลได้ โปรดตรวจสอบยอด Onhand");
				
				request.getSession().setAttribute("results", resultPick.getItems());
			
			}
		} catch (Exception e) {
			conn.rollback();
            logger.error(e.getMessage(),e);
			request.setAttribute("Message","ไม่สามารถบันทึกข้อมูลได้ \n"+ e.getMessage());
			try {
				
				
			} catch (Exception e2) {}
			return mapping.findForward("prepareAllBox");
		} finally {
			try {
				if(conn != null){
				   conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return mapping.findForward("prepareAllBox");
	}
	public ActionForward saveAutoSubOut(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("saveAutoSubOut Action");
		PickStockForm aForm = (PickStockForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			PickStock confPickStock = aForm.getBean();
			
			//prepare bean
			AutoSubInBean autoSubBean = new AutoSubInBean();
			autoSubBean.setJobId(confPickStock.getIssueReqNo());
			autoSubBean.setCustGroup(confPickStock.getCustGroup());
			autoSubBean.setStoreCode(confPickStock.getStoreCode());
			autoSubBean.setSubInv(confPickStock.getSubInv());
		    autoSubBean.setTotalBox(""+confPickStock.getTotalBox());//old
			autoSubBean.setTotalQty(""+confPickStock.getTotalQty());//old
			autoSubBean.setForwarder(confPickStock.getForwarder());
			autoSubBean.setForwarderBox(""+confPickStock.getTotalBox());
			autoSubBean.setUserName(user.getUserName());
			
			boolean exist = AutoSubOutDAO.isAutoSubTransOutExist(autoSubBean.getJobId()); 
			if( !exist){
			   AutoSubOutDAO.saveSubTransOutPickStockByAllBox(conn, autoSubBean);
			   conn.commit();

			   request.setAttribute("Message", "บันทึกข้อมูล Auto Sub Transfer เรียบร้อยแล้ว");
			   
			   //new search
			   PickStock p = PickStockDAO.searchPickStock(conn,confPickStock,true);
			   List<PickStock>  allList = new ArrayList<PickStock>();
			   allList.addAll(p.getItems());
			   allList.addAll(PickStockDAO.searchBarcoceItemStatusCloseW3(conn));
		       p.setCanConfirm(false);
		       
			}else{
			   request.setAttribute("Message", "RefNo นี้ เคยมีการบันทึกการใช้งานไปแล้ว");
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
		return mapping.findForward("prepareAllBox");
	}
	
	public ActionForward confirmAllBox(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("confirmAction");
		PickStockForm aForm = (PickStockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			PickStock h = aForm.getBean();
			  
			//update status stock Pick to cancel
			String curDateStr = DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			h.setUpdateUser(user.getUserName());
			h.setConfirmIssueDate(curDateStr);
			h.setIssueReqStatus(PickStockDAO.STATUS_ISSUED);
			
			//witty:edit 27/10/2019 add total_issue_qty ,total_box to PENSBME_PICK_STOCK
			
			//update DB
			PickStockDAO.updatePickStockHeadModel(conn, h);
			PickStockDAO.updatePickStockItemModel(conn,h);
			
			conn.commit();
			
			//new search
			PickStock p = PickStockDAO.searchPickStock(conn,h,true);
			
			List<PickStock>  allList = new ArrayList<PickStock>();
			allList.addAll(p.getItems());
			
			aForm.setBean(p);
			aForm.setResults(allList);
				
			request.setAttribute("Message", "ยืนยันรายการ เรียบร้อยแล้ว");
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}finally{
			if(conn != null){
			   conn.close();conn=null;
			}
		}
		return mapping.findForward("prepareAllBox");
	}
	
	public ActionForward cancelAllBox(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("cancelAllBox");
		PickStockForm aForm = (PickStockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Map<String,String> boxNoMapAll = new HashMap<String, String>();
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			PickStock h = aForm.getBean();
			  
			//update status stock Pick to cancel
			h.setIssueReqStatus(PickStockDAO.STATUS_CANCEL);
			h.setUpdateUser(user.getUserName());
			PickStockDAO.updatePickStockHeadModel(conn, h);
			PickStockDAO.updatePickStockItemModel(conn,h);
			 
			//Step2 update barcode to close 
			boxNoMapAll = PickStockDAO.updateBarcodeToCloseFromStockPickItem(conn,h);
			
			/** Check all item ==ISSUE and Update Head == ISSUE **/
			if( !boxNoMapAll.isEmpty()){
				logger.debug("Validate Barcode Item amd update barcode Head");
				
				Iterator<String> its = boxNoMapAll.keySet().iterator();
				String boxNo = "";
				while(its.hasNext()){
				   boxNo = its.next(); 
				   PickStockGroupDAO.processUpdateStatusBarcodeHead(conn,boxNo,h.getCreateUser());
				}
			}
	
			//new search
			PickStock p = PickStockDAO.searchPickStock(conn,h,true);
			
			List<PickStock>  allList = new ArrayList<PickStock>();
			allList.addAll(p.getItems());
			
			aForm.setBean(p);
			aForm.setResults(allList);
			
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
		return mapping.findForward("prepareAllBox");
	}
	
	public ActionForward cancelIssueAllBoxAction(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("cancelIssueAllBoxAction");
		PickStockForm aForm = (PickStockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			PickStock h = aForm.getBean();
			//Step 1 update status stock Pick to cancel
			h.setUpdateUser(user.getUserName());
			h.setIssueReqStatus(PickStockDAO.STATUS_CANCEL);//CANCEL
			//update DB
			PickStockDAO.updateCancelPickStockHeadModel(conn, h);
			PickStockDAO.updatePickStockItemModel(conn,h);
			
			//Step2 update barcode to close 
			h.setIssueReqStatus(PickStockDAO.STATUS_CLOSE);//CLOSE
			PickStockDAO.updateBarcodeByIssueReqNo(conn, h);
			PickStockDAO.updateBarcodeItemByIssueReqNo(conn, h);
			conn.commit();
			
			//new search
			PickStock p = PickStockDAO.searchPickStock(conn,h,true);
			
			List<PickStock>  allList = new ArrayList<PickStock>();
			allList.addAll(p.getItems());
			
			aForm.setBean(p);
			aForm.setResults(allList);
				
			request.setAttribute("Message", "ยกเลิกรายการ เรียบร้อยแล้ว");
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}finally{
			if(conn != null){
			   conn.close();conn=null;
			}
		}
		return mapping.findForward("prepareAllBox");
	}
   private Map<String, PickStock> setDataSaveMap(HttpServletRequest request,PickStockForm aForm){
	    User user = (User) request.getSession().getAttribute("user");
	    //Data Disp Per Page
	    List<PickStock> results = (List<PickStock>)request.getSession().getAttribute("results"); //data per page
		
	    //dataSaveMap All
	    Map<String, PickStock> dataSaveMapAll = new HashMap<String, PickStock>();
	    if(request.getSession().getAttribute("dataSaveMapAll")!= null ){
	       dataSaveMapAll = (Map)request.getSession().getAttribute("dataSaveMapAll");
	    }
	    
	    //dataEditMap All
	    Map<String, PickStock> dataEditMapAll = new HashMap<String, PickStock>();
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
				 PickStock l = (PickStock)results.get(i);
				 l.setQty(qty[i]);
				 l.setUpdateUser(user.getUserName());
				 l.setCreateUser(user.getUserName());
				 
				 //set data to list disp
				 results.set(i, l);
				 
				 //Key Map  
				 String key = l.getBoxNo()+"_"+l.getMaterialMaster()+"_"+l.getGroupCode()+"_"+l.getPensItem();
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

	private Map<String, PickStock> setDispResults(HttpServletRequest request,PickStockForm aForm){
	    //Data Disp Per Page
	    List<PickStock> results = (List<PickStock>)request.getSession().getAttribute("results"); //data per page
		
	    //dataSaveMap All
	    Map<String, PickStock> dataSaveMapAll = new HashMap<String, PickStock>();
	    if(request.getSession().getAttribute("dataSaveMapAll")!= null ){
	       dataSaveMapAll = (Map)request.getSession().getAttribute("dataSaveMapAll");
	    }
	    
		logger.debug("dataSaveMapAll Size:"+(dataSaveMapAll!=null?dataSaveMapAll.size():0));
		logger.debug("results Size:"+(results!=null?results.size():0));

		String[] qty = request.getParameterValues("qty");
		
		//add value to Results
		if(results != null && results.size() > 0){
			for(int i=0;i<results.size();i++){
				 PickStock l = (PickStock)results.get(i);
				//Key Map  
				 String key = l.getBoxNo()+"_"+l.getMaterialMaster()+"_"+l.getGroupCode()+"_"+l.getPensItem();
				 if(dataSaveMapAll.get(key) != null){
					 PickStock pOld = (PickStock) dataSaveMapAll.get(key); 
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
	
	public ActionForward exportExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("exportExcel");
		PickStockForm aForm = (PickStockForm) form;
		//User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			
			PickStock h = aForm.getBean();
			h = PickStockDAO.searchPickStockItemByIssueReqNo4Report(conn, h);
			
			StringBuffer htmlTable = genReportHtml(h);	 
			
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
	
	public ActionForward exportBarcodeToExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("exportBarcodeToExcel");
		PickStockForm aForm = (PickStockForm) form;
		//User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			
			PickStock h = aForm.getBean();
			h = PickStockDAO.searchPickStockItemByIssueReqNo4Report(conn, h);
			
			StringBuffer htmlTable = genBarcodeReportHtml(h);	 
			
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
		PickStockForm aForm = (PickStockForm) form;
		try {
			request.getSession().setAttribute("dataSaveMapAll", null);
			request.getSession().setAttribute("dataEditMapAll", null);
			request.getSession().setAttribute("results", null);
			PickStock ad = new PickStock();
			//ad.setIssueReqDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			aForm.setBean(ad);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("clear");
	}

	private StringBuffer genReportHtml(PickStock p){
		StringBuffer h = new StringBuffer("");
		try{
			h.append(ExcelHeader.EXCEL_HEADER);
			//Header
			String title = "ใบเบิกเลขที่ :"+p.getIssueReqNo();
			
			h.append("<table border='1'> \n");
				h.append("<tr> \n");
				h.append("<td align='left' colspan='11'><b>"+title+" </b></td> \n");
				h.append("</tr> \n");
			h.append("</table> \n");

			if(p.getItems()!= null){
			    List<PickStock> dataList =p.getItems();
			    //Job Id	Job Name	เลขที่กล่อง		Wacoal Mat.	Group Code	Pens Item	QTY ที่เบิก

				h.append("<table border='1'> \n");
				h.append("<tr> \n");
					 h.append("<td><b>Job Id</b></td> \n");
					 h.append("<td><b>Job Name</b></td> \n");
					 h.append("<td><b>เลขที่กล่อง </b></td> \n");
					 h.append("<td><b>Barcode</b></td> \n");
					 h.append("<td><b>Wacoal Mat</b></td> \n");
					 h.append("<td><b>Group Code</b></td> \n");
					 h.append("<td><b>Pens Item </b></td> \n");
					 h.append("<td><b>QTY ที่เบิก </b></td> \n");
					 h.append("<td>Issue req</td> \n");
					 h.append("<td>ร้านค้า</td> \n");
					 h.append("<td>Sub Inv</td> \n");
					 h.append("<td>Invoice No</td> \n");
				h.append("</tr> \n");
				
				for(int i=0;i<dataList.size();i++){
					PickStock s = (PickStock)dataList.get(i);
				   h.append("<tr> \n");
				   h.append("<td>"+s.getJobId()+"&nbsp;</td> \n");
				   h.append("<td>"+s.getJobName()+"</td> \n");
				   h.append("<td>"+s.getBoxNo()+"</td> \n");
				   h.append("<td class='text'>"+s.getBarcode()+"</td> \n");
				   h.append("<td>"+s.getMaterialMaster()+"</td> \n");
				   h.append("<td>"+s.getGroupCode()+"&nbsp;</td> \n");
				   h.append("<td>"+s.getPensItem()+"</td> \n");
				   h.append("<td>"+s.getQty()+"</td> \n");
				   h.append("<td class='text'>"+p.getIssueReqNo()+"</td> \n");
				   h.append("<td class='text'>"+p.getStoreCode()+"-"+p.getStoreName()+"</td> \n");
				   h.append("<td class='text'>"+p.getSubInv()+"</td> \n");
				   h.append("<td class='text'>"+p.getInvoiceNo()+"</td> \n");
				   h.append("</tr>");
				}//for 
				
				h.append("<tr> \n");
				h.append("<td colspan='6' align='right'><b>Total</b></td> \n");
				h.append("<td><b>"+p.getTotalQty()+"</b></td> \n");
				h.append("<td colspan='3' align='right'></td> \n");
				h.append("</tr>");	
			h.append("</table> \n");
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return h;
	}
	
	private StringBuffer genBarcodeReportHtml(PickStock p){
		StringBuffer h = new StringBuffer("");
		int r = 0;
		int qty = 0;
		try{
			h.append(ExcelHeader.EXCEL_HEADER);
			if(p.getItems()!= null){
			    List<PickStock> dataList =p.getItems();
				h.append("<table border='1'> \n");
				h.append("<tr> \n");
					 h.append("<td><b>Barcode</b></td> \n");
					 h.append("<td><b>Materail Master</b></td> \n");
					 h.append("<td><b>Pens Item </b></td> \n");
				h.append("</tr> \n");
				
				for(int i=0;i<dataList.size();i++){
					PickStock s = (PickStock)dataList.get(i);
					qty = s.getQtyInt();
					for(r=0;r<qty;r++){
					    h.append("<tr> \n");
					    h.append("<td class='text'>"+s.getBarcode()+"</td> \n");
					    h.append("<td>"+s.getMaterialMaster()+"</td> \n");
					    h.append("<td>"+s.getPensItem()+"</td> \n");
					    h.append("</tr>");
					}
				}//for 
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
