package com.isecinc.pens.web.pick;

import java.io.BufferedWriter;
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

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.PickStock;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.PickStockGroupDAO;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class PickStockGroupAction extends I_Action {

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
				aForm.setBean(aForm.getBeanCriteria());
				aForm.setResultsSearch(PickStockGroupDAO.searchHead(aForm.getBean()));
			}
		} catch (Exception e) {
			request.setAttribute("Message",e.getMessage());
			throw e;
		}finally{
			if(conn != null){
			  conn.close();conn=null;
			}
		}
		return mapping.findForward("search");
	}
	
	public ActionForward search2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("search2");
		PickStockForm aForm = (PickStockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			aForm.setResultsSearch(PickStockGroupDAO.searchHead(aForm.getBean()));
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
		return mapping.findForward("search");
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
		return mapping.findForward("search");
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
	
	public ActionForward prepareByGroup(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepareByGroup");
		PickStockForm aForm = (PickStockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		boolean getItems = true;
		boolean getOldDataOnly = false;
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
				p.setPickType(PickConstants.PICK_TYPE_GROUP);
				
				if("confirm".equalsIgnoreCase(process)){
					p.setModeConfirm(true);
					p.setModeEdit(false);
				}else{
					p.setModeConfirm(false);
					p.setModeEdit(true);
				}
				
				getItems = true;
				getOldDataOnly = true; //Show only Pick item
				p = PickStockGroupDAO.searchPickStock(conn, p, getItems,getOldDataOnly);
				
				List<PickStock>  allList = new ArrayList<PickStock>();
				if(p.isModeConfirm() || PickConstants.STATUS_ISSUED.equals(issueReqStatus)){
					allList.addAll(p.getItems());
				}else{
					if(PickConstants.STATUS_CANCEL.equals(issueReqStatus)){
					    allList.addAll(p.getItems());
					}else{
					    allList.addAll(p.getItems());
					}
				}
				p.setIssueReqStatus(issueReqStatus);
				
				aForm.setBean(p);
				aForm.setResults(allList);
			}else{
				logger.debug("prepare new issueReqNo");
				
				PickStock p = new PickStock();
				p.setPickType(PickConstants.PICK_TYPE_GROUP);
				p.setIssueReqDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				p.setCanEdit(true);
				p.setCanConfirm(false);
				
				p.setNewReq(true);
				p.setModeConfirm(false);
				p.setModeEdit(true);
				aForm.setBean(p);
				aForm.setResults(null);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
            conn.rollback();
		}finally{
			if(conn != null){
			   conn.close();conn=null;
			}
		}
		return mapping.findForward("prepareByGroup");
	}

	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		PickStockForm f = (PickStockForm) form;
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
		boolean getItems = true;
		boolean getOldDataOnly = false;
		try {
			conn = DBConnection.getInstance().getConnection();
		    PickStock p = aForm.getBean();
			p = PickStockGroupDAO.searchPickStock(conn, p, getItems,getOldDataOnly);
			aForm.setResults(p.getItems());

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
		return "prepareByGroup";
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
		return "prepareByGroup";
	}

	public ActionForward saveByGroup(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("Save By Group");
		Connection conn = null;
		PickStockForm aForm = (PickStockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		boolean getItems = true;
		boolean getOldDataOnly = false;
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			//PickStock Head
			PickStock h = aForm.getBean();
			h.setCreateUser(user.getUserName());
			h.setUpdateUser(user.getUserName());
			
			//Set Items
			List<PickStock> itemList = new ArrayList<PickStock>();
		
			String[] boxNo = request.getParameterValues("boxNo");
			String[] groupCode = request.getParameterValues("groupCode");
			String[] jobId = request.getParameterValues("jobId");
			String[] qty = request.getParameterValues("qty");
			String[] orgQty = request.getParameterValues("orgQty");
			String[] onhandQty = request.getParameterValues("onhandQty");
			

			//add value to Results
			if(boxNo != null && boxNo.length > 0){
				for(int i=0;i<boxNo.length;i++){
				
					if( (  !Utils.isNull(orgQty[i]).equals("") && !Utils.isNull(orgQty[i]).equals("0") ) 
						 || ( !Utils.isNull(qty[i]).equals("") && !Utils.isNull(qty[i]).equals("0") ) ){
						
						 PickStock l = new PickStock();
						 l.setIssueReqNo(Utils.isNull(h.getIssueReqNo())); 
						 l.setBoxNo(Utils.isNull(boxNo[i]));
						 l.setGroupCode(Utils.isNull(groupCode[i]));
						 l.setJobId(Utils.isNull(jobId[i]));
						 l.setQty(qty[i]);
						 l.setOnhandQty(onhandQty[i]);
						 l.setCreateUser(user.getUserName());
						 l.setUpdateUser(user.getUserName());
						 
						 itemList.add(l);
					}
				}
			}
			
			h.setItems(itemList);
			
			//Save DB
			PickStock resultPick = PickStockGroupDAO.saveByGroup(conn,h);
			
			logger.debug("result save :"+resultPick.isResultProcess());
			
			if(resultPick.isResultProcess()){
			    
			    //new search
				getItems = true;
				getOldDataOnly = true;
				PickStock p = PickStockGroupDAO.searchPickStock(conn,resultPick,getItems,getOldDataOnly);
				
				List<PickStock>  allList = new ArrayList<PickStock>();
				allList.addAll(p.getItems());
				
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
			return mapping.findForward("prepareByGroup");
		} finally {
			try {
				if(conn != null){
				   conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return mapping.findForward("prepareByGroup");
	}
	
	public ActionForward confirmAction(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("confirmAction");
		PickStockForm aForm = (PickStockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		boolean getItems = true;
		boolean getOldDataOnly = false;
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			PickStock h = aForm.getBean();
			  
			//update status stock Pick to cancel
			String curDateStr = Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			h.setUpdateUser(user.getUserName());
			h.setConfirmIssueDate(curDateStr);
			h.setIssueReqStatus(PickStockGroupDAO.STATUS_ISSUED);
			
			//Set Items
			List<PickStock> itemList = new ArrayList<PickStock>();
		
			String[] boxNo = request.getParameterValues("boxNo");
			String[] groupCode = request.getParameterValues("groupCode");
			String[] jobId = request.getParameterValues("jobId");
			String[] qty = request.getParameterValues("qty");
			String[] orgQty = request.getParameterValues("orgQty");
			String[] onhandQty = request.getParameterValues("onhandQty");
			String[] issueQty = request.getParameterValues("issueQty");
			

			//add value to Results
			if(boxNo != null && boxNo.length > 0){
				for(int i=0;i<boxNo.length;i++){
				
					if( (  !Utils.isNull(orgQty[i]).equals("") && !Utils.isNull(orgQty[i]).equals("0") ) 
						 || ( !Utils.isNull(issueQty[i]).equals("") && !Utils.isNull(issueQty[i]).equals("0") ) ){
						
						 PickStock l = new PickStock();
						 l.setIssueReqNo(Utils.isNull(h.getIssueReqNo())); 
						 l.setBoxNo(Utils.isNull(boxNo[i]));
						 l.setGroupCode(Utils.isNull(groupCode[i]));
						 l.setJobId(Utils.isNull(jobId[i]));
						 l.setQty(qty[i]);
						 l.setIssueQty(issueQty[i]);
						 l.setOnhandQty(onhandQty[i]);
						 l.setCreateUser(user.getUserName());
						 l.setUpdateUser(user.getUserName());
						 
						 itemList.add(l);
					}
				}
			}
			
			h.setItems(itemList);
			
			PickStockGroupDAO.confirmByGroup(conn, h);
			
			conn.commit();
			
			//new search
			getItems = true;
			getOldDataOnly = true;
			PickStock p = PickStockGroupDAO.searchPickStock(conn,h,getItems,getOldDataOnly);
			
			List<PickStock>  allList = new ArrayList<PickStock>();
			allList.addAll(p.getItems());
			
			aForm.setBean(p);
			aForm.setResults(allList);
				
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
		return mapping.findForward("prepareByGroup");
	}
	
	/**
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 * Run after Add or Edit
	 */
	public ActionForward cancelAction(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("cancelAction");
		PickStockForm aForm = (PickStockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Map<String,String> boxNoMapAll = new HashMap<String, String>();
		Connection conn = null;
		boolean getItems = true;
		boolean getOldDataOnly = false;
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			PickStock h = aForm.getBean();
			//Step 1 update status stock Pick to cancel
			h.setUpdateUser(user.getUserName());
			h.setIssueReqStatus(PickStockGroupDAO.STATUS_CANCEL);//CANCEL
			//update DB
			PickStockGroupDAO.updateCancelPickStockHeadModel(conn, h);
			PickStockGroupDAO.updatePickStockItemModel(conn,h);
			
			//Step2 update barcode to close 
			boxNoMapAll = PickStockGroupDAO.updateBarcodeToCloseFromStockPickItem(conn,h);
			
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
			conn.commit();
			
			//new search
			getItems = true;
			getOldDataOnly = true;
			PickStock p = PickStockGroupDAO.searchPickStock(conn,h,getItems,getOldDataOnly);
			
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
		return mapping.findForward("prepareByGroup");
	}
	
	/**
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 * Run after Confirm
	 */
	public ActionForward cancelIssueAction(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("cancelIssueAction");
		PickStockForm aForm = (PickStockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		Map<String,String> boxNoMapAll = new HashMap<String, String>();
		Connection conn = null;
		boolean getItems = true;
		boolean getOldDataOnly = false;
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			PickStock h = aForm.getBean();
			//Step 1 update status stock Pick to cancel
			h.setUpdateUser(user.getUserName());
			h.setIssueReqStatus(PickStockGroupDAO.STATUS_CANCEL);//CANCEL
			//update DB
			PickStockGroupDAO.updateCancelPickStockHeadModel(conn, h);
			PickStockGroupDAO.updatePickStockItemModel(conn,h);
			
			//Step2 update barcode to close 
			boxNoMapAll = PickStockGroupDAO.updateBarcodeToCloseFromStockPickItem(conn,h);
			
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
			
			conn.commit();
			
			//new search
			getItems = true;
			getOldDataOnly = true;
			PickStock p = PickStockGroupDAO.searchPickStock(conn,h,getItems,getOldDataOnly);
			
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
		return mapping.findForward("prepareByGroup");
	}
	
	public ActionForward exportExcel(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("exportExcel");
		PickStockForm aForm = (PickStockForm) form;
		//User user = (User) request.getSession().getAttribute("user");
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			
			PickStock h = aForm.getBean();
			h = PickStockGroupDAO.searchPickStockItemByIssueReqNo4Report(conn, h);
			
			StringBuffer htmlTable = genHTML(h);	 
			
			java.io.OutputStream out = response.getOutputStream();
			response.setHeader("Content-Disposition", "attachment; filename="+Utils.genFileName("pick_group")+".xls");
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
		return mapping.findForward("prepareByGroup");
	}
	
	
	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		PickStockForm aForm = (PickStockForm) form;
		try {
			PickStock ad = aForm.getBean();
			ad.setJobId("");
			ad.setJobName("");
			ad.setBoxNoFrom("");
			ad.setBoxNoTo("");
			ad.setGroupCodeFrom("");
			ad.setGroupCodeTo("");
			ad.setOrderBy("box");
		
			aForm.setBean(ad);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("prepareByGroup");
	}

	private StringBuffer genHTML(PickStock p){
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
			    List<PickStock> dataList =p.getItems();
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
					PickStock s = (PickStock)dataList.get(i);
				   h.append("<tr> \n");
				   h.append("<td>"+s.getJobId()+"&nbsp;</td> \n");
				   h.append("<td>"+s.getJobName()+"</td> \n");
				   h.append("<td>"+s.getBoxNo()+"</td> \n");
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
	
	private StringBuffer genHTMLXX(PickStock p){
		StringBuffer h = new StringBuffer("");
		try{
			//Header
			String title = "ใบเบิกเลขที่ :"+p.getIssueReqNo();
			
			h.append("<table border='1'> \n");
				h.append("<tr> \n");
				h.append("<td align='left' colspan='4'><b>"+title+" </b></td> \n");
				h.append("</tr> \n");
			h.append("</table> \n");

			if(p.getItems()!= null){
			    List<PickStock> dataList =p.getItems();
			    //Job Id	Job Name	เลขที่กล่อง		Wacoal Mat.	Group Code	Pens Item	QTY ที่เบิก

				h.append("<table border='1'> \n");
				h.append("<tr> \n");
					 h.append("<td><b>เลขที่กล่อง </b></td> \n");
					 h.append("<td><b>Group Code</b></td> \n");
					 if(PickConstants.STATUS_ISSUED.equals(p.getIssueReqStatus())){
						 h.append("<td><b>QTY ที่เบิก </b></td> \n");
					 }else{
					     h.append("<td><b>QTY ที่เบิก </b></td> \n");
					 }
					 h.append("<td><b>รับคืนจาก</b></td> \n");
					
				h.append("</tr> \n");
				
				for(int i=0;i<dataList.size();i++){
					PickStock s = (PickStock)dataList.get(i);
				   h.append("<tr> \n");
				   h.append("<td>"+s.getBoxNo()+"</td> \n");
				   h.append("<td>"+s.getGroupCode()+"&nbsp;</td> \n");
				   h.append("<td>"+s.getQty()+"</td> \n");
				   h.append("<td>"+s.getJobId()+"&nbsp;"+s.getJobName()+"</td> \n");
			
				   h.append("</tr>");
				}//for 
				
				h.append("<tr> \n");
				h.append("<td colspan='2' align='right'><b>Total</b></td> \n");
				h.append("<td><b>"+p.getTotalQty()+"</b></td> \n");
				h.append("<td></td> \n");
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
