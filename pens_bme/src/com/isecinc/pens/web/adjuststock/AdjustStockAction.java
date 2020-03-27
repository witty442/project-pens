package com.isecinc.pens.web.adjuststock;

import java.sql.Connection;
import java.util.ArrayList;
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
import com.isecinc.pens.bean.AdjustStock;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.AdjustStockDAO;
import com.isecinc.pens.init.InitialMessages;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.Utils;

/**
 * Summary Action
 * 
 * @author WITTY
 * 
 */
public class AdjustStockAction extends I_Action {

	public static int pageSize = 60;
	public static Map<String,String> STORE_TYPE_MAP = new HashMap<String, String>();
	
	
	public ActionForward prepare2(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("prepare2");
		AdjustStockForm aForm = (AdjustStockForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			String action = Utils.isNull(request.getParameter("action"));
			if("new".equals(action)){
				aForm.setResultsSearch(null);
				AdjustStock ad = new AdjustStock();
				//ad.setTransactionDate(Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				
				aForm.setAdjustStock(ad);
			}else if("back".equals(action)){
				conn = DBConnection.getInstance().getConnectionApps();
				aForm.setAdjustStock(aForm.getAdjustStockCriteria());
				aForm.setResultsSearch(AdjustStockDAO.searchHeadList(conn,aForm.getAdjustStock(),false,1,pageSize));
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()+ e.getMessage());
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
		AdjustStockForm aForm = (AdjustStockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		int currPage = 1;
		boolean allRec = false;
		Connection conn = null;
		try {
			String action = Utils.isNull(request.getParameter("action"));
			logger.debug("action:"+action);
			
			conn = DBConnection.getInstance().getConnectionApps();
			
			if("newsearch".equalsIgnoreCase(action) || "back".equalsIgnoreCase(action)){
				//case  back
				if("back".equalsIgnoreCase(action)){
					aForm.setAdjustStockCriteria(aForm.getAdjustStock());
				}
				//default currPage = 1
				aForm.setCurrPage(currPage);
				
				//get Total Record
				aForm.setTotalRecord(AdjustStockDAO.searchHeadListTotalRec(conn,aForm.getAdjustStock()));
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
			   
				List<AdjustStock> items = AdjustStockDAO.searchHeadList(conn,aForm.getAdjustStock(),allRec,currPage,pageSize);
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
			    List<AdjustStock> items = AdjustStockDAO.searchHeadList(conn,aForm.getAdjustStock(),allRec,currPage,pageSize);
				aForm.setResultsSearch(items);
				
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
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
		AdjustStockForm aForm = (AdjustStockForm) form;
		try {
			aForm.setResultsSearch(new ArrayList<AdjustStock>());
			aForm.setAdjustStock(new AdjustStock());
			aForm.setVerify(false);
			
			AdjustStock ad = new AdjustStock();
			ad.setTransactionDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			aForm.setAdjustStock(ad);
			
		} catch (Exception e) {
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
		AdjustStockForm aForm = (AdjustStockForm) form;
		Connection conn = null;
		User user = (User) request.getSession().getAttribute("user");
		try {
			//save old criteria
			aForm.setAdjustStockCriteria(aForm.getAdjustStock());
			
            String documentNo = Utils.isNull(request.getParameter("documentNo"));
            String mode = Utils.isNull(request.getParameter("mode"));
            
			if( !"".equals(documentNo)){
				logger.debug("prepare edit documentNo:"+documentNo);
				AdjustStock c = new AdjustStock();
				c.setDocumentNo(documentNo);
				AdjustStock aS = AdjustStockDAO.search(c);
				
				aForm.setResults(aS.getItems());
				aForm.setAdjustStock(aS);
				aForm.setVerify(true);
				
				aForm.setMode(mode);//Mode Edit
				
			}else{
				logger.debug("prepare new documentNo");
				aForm.setResults(new ArrayList<AdjustStock>());
				AdjustStock ad = new AdjustStock();
				ad.setTransactionDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				
				aForm.setAdjustStock(ad);
				aForm.setVerify(false);
				aForm.setMode(mode);//Mode Add new
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			if(conn != null){
			  conn.close();conn=null;
			}
		}
		return forward;
	}

	/**
	 * Prepare with ID
	 */
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		AdjustStockForm summaryForm = (AdjustStockForm) form;
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
		AdjustStockForm orderForm = (AdjustStockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		String msg = "";
		try {
			AdjustStock aS = AdjustStockDAO.search(orderForm.getAdjustStock());
			orderForm.setResults(aS.getItems());
			
			if(Utils.isNull(aS.getStoreName()).equals("")){
				msg = "ไม่สามารถทำรายการได้  ไม่พบข้อมูลร้านค้า";
				orderForm.setVerify(false);
			}else{
				if(Utils.isNull(aS.getSubInv()).equals("") || Utils.isNull(aS.getOrg()).equals("") ){
					msg = "ไม่สามารถทำรายการได้  ไม่พบข้อมูล Suv inv/Org";
				}else{
					orderForm.setVerify(true);
				}
			}
			orderForm.setAdjustStock(aS);
			
			request.setAttribute("Message", msg);
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			
		}
		return "search";
	}
	
	public ActionForward verifyData(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("verifyData");
		String msg= "";
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			AdjustStockForm aForm = (AdjustStockForm) form;
			AdjustStock aS = AdjustStockDAO.getOrgSubInv(conn, aForm.getAdjustStock());
			
			if(Utils.isNull(aS.getStoreName()).equals("")){
				
				msg = "ไม่สามารถทำรายการได้  ไม่พบข้อมูลร้านค้า";
				aForm.setVerify(false);
			}else{
				if(Utils.isNull(aS.getSubInv()).equals("") || Utils.isNull(aS.getOrg()).equals("") ){
					msg = "ไม่สามารถทำรายการได้  ไม่พบข้อมูล Suv inv/Org";
				}else{
					aForm.setVerify(true);
				}
			}
			aForm.getAdjustStock().setOrg(aS.getOrg());
			aForm.getAdjustStock().setSubInv(aS.getSubInv());
			aForm.getAdjustStock().setBankNo(aS.getBankNo());
			
			request.setAttribute("Message", msg);
			
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}finally{
			
		}
		return mapping.findForward("search");
	}
	
	/**
	 * Save
	 */
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Connection conn = null;
		AdjustStockForm aForm = (AdjustStockForm) form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
            //Set Data from screen to List
			List<AdjustStock> results = setDataToResults(request,aForm);
			AdjustStock h = aForm.getAdjustStock();
			h.setItems(results);
			h.setCreateUser(user.getUserName());
			h.setUpdateUser(user.getUserName());
			
			AdjustStockDAO.save(h);
			
			conn.commit();
			
			//Search 
			AdjustStock aS = AdjustStockDAO.search(aForm.getAdjustStock());
			aForm.setAdjustStock(aS);
			aForm.setResults(aS.getItems());
			
			request.setAttribute("Message", "บันทึกข้อมูลเรียบร้อยแล้ว");
		} catch (Exception e) {
			conn.rollback();
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
	
	public ActionForward exported(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("exported");
		AdjustStockForm aForm = (AdjustStockForm) form;
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			AdjustStock h = aForm.getAdjustStock();
			h.setStatus(AdjustStockDAO.STATUS_CLOSE);
			h.setStatusDesc(AdjustStockDAO.getStatusDesc(h.getStatus()));
			
			//update
			AdjustStockDAO.updateStatus(conn,h);
			
			//Search 
			AdjustStock aS = AdjustStockDAO.search(h);
			aForm.setAdjustStock(aS);
			aForm.setResults(aS.getItems());
			
			request.setAttribute("Message", "ยืนยันการส่งข้อมูล เรียบร้อยแล้ว");
		} catch (Exception e) {
			conn.rollback();
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		} finally {
			try {
				if(conn != null){
				   conn.close();conn=null;
				}
			} catch (Exception e2) {}
		}
		return mapping.findForward("search");
	}

	public ActionForward addRow(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("addRow");
		AdjustStockForm aForm = (AdjustStockForm) form;
		try {
			List<AdjustStock> results = aForm.getResults();
			logger.debug("Result Size:"+(results!=null?results.size():0));
			
			if(results != null && results.size() > 0){
				//add data screen to sessin result
				results = setDataToResults(request,aForm);
				
				AdjustStock lastAd = (AdjustStock)results.get(results.size()-1);
				
				results.add(createNewAdjustStock(lastAd));
				
				aForm.setResults(results);
			}else{
				//new 
				AdjustStock lastAd = new AdjustStock();
				lastAd.setSeqNo(0);
				
				results = new ArrayList<AdjustStock>();
                results.add(createNewAdjustStock(lastAd));
				
				aForm.setResults(results);
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("addRow");
	}
	
	public ActionForward removeRow(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("removeRow");
		AdjustStockForm aForm = (AdjustStockForm) form;
		List<AdjustStock> newResults = new ArrayList<AdjustStock>();
		try {
			List<AdjustStock> results = setDataToResults(request,aForm);
			//for check 
			String[] linechk = request.getParameterValues("linechk");
			
			int seqNo = 0;
			if(results != null && results.size() > 0){
				for(int i=0;i<results.size();i++){
				   AdjustStock a = (AdjustStock)results.get(i);
				   boolean e = false;
				   for(int n=0;n<linechk.length;n++){
					   if( Utils.isNull(linechk[n]).equals(String.valueOf(a.getSeqNo()))){
						  e = true;
					   }
					   logger.debug("compare seqNo:["+a.getSeqNo()+"]:["+linechk[n]+"]e["+e+"]");
				   }//for 2
				   
				   logger.debug("result seqNo:"+a.getSeqNo()+":"+e);
				   if(e==false){
					   seqNo++;
					   a.setSeqNo(seqNo);
					   newResults.add(a);
				   }
				 
				}//for 1
				
				aForm.setResults(newResults);
			}//for
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc() + e.toString());
		}
		return mapping.findForward("addRow");
	}
	
	private List<AdjustStock> setDataToResults(HttpServletRequest request,AdjustStockForm aForm){
		List<AdjustStock> results = aForm.getResults();
		logger.debug("Result Size:"+(results!=null?results.size():0));
		//String[] linechk = request.getParameterValues("linechk");
		String[] itemIssue = request.getParameterValues("itemIssue");
		String[] itemIssueDesc = request.getParameterValues("itemIssueDesc");
		String[] itemIssueUom = request.getParameterValues("itemIssueUom");
		String[] itemIssueRetailNonVat = request.getParameterValues("itemIssueRetailNonVat");
		String[] itemIssueQty = request.getParameterValues("itemIssueQty");
		String[] itemReceipt = request.getParameterValues("itemReceipt");
		String[] itemReceiptDesc = request.getParameterValues("itemReceiptDesc");
		String[] itemReceiptUom = request.getParameterValues("itemReceiptUom");
		String[] itemReceiptRetailNonVat = request.getParameterValues("itemReceiptRetailNonVat");
		String[] itemReceiptQty = request.getParameterValues("itemReceiptQty");
		String[] diffCost = request.getParameterValues("diffCost");
		
		//logger.debug("linechk:"+linechk.length);
		logger.debug("itemIssue:"+itemIssue.length);
		
		//add value to Results
		if(results != null && results.size() > 0){
			for(int i=0;i<results.size();i++){
				 AdjustStock l = (AdjustStock)results.get(i);
				 
				 l.setItemIssue(Utils.isNull(itemIssue[i]));
				 l.setItemIssueDesc(Utils.isNull(itemIssueDesc[i]));
				 l.setItemIssueUom(Utils.isNull(itemIssueUom[i]));
				 l.setItemIssueQty(Integer.parseInt(itemIssueQty[i])); 
				 l.setItemIssueRetailNonVat(Utils.isDoubleNull(itemIssueRetailNonVat[i]));
				  
				 l.setItemReceipt(Utils.isNull(itemReceipt[i]));
				 l.setItemReceiptDesc(Utils.isNull(itemReceiptDesc[i]));
				 l.setItemReceiptUom(Utils.isNull(itemReceiptUom[i]));
				 l.setItemReceiptQty(Integer.parseInt(itemReceiptQty[i]));
				 l.setItemReceiptRetailNonVat(Utils.isDoubleNull(itemReceiptRetailNonVat[i]));
				  
				 l.setDiffCost(Utils.isDoubleNull(diffCost[i]));
				 
				 results.set(i, l);
			}
		}
		return results;
	}
	
	private AdjustStock createNewAdjustStock(AdjustStock lastAd){
		AdjustStock aNew = new AdjustStock();
		aNew.setSeqNo(lastAd.getSeqNo()+1);
		
		return aNew;
	}

	public ActionForward clear(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response)  throws Exception {
		logger.debug("clear");
		AdjustStockForm aForm = (AdjustStockForm) form;
		try {
			aForm.setResults(new ArrayList<AdjustStock>());
			aForm.setAdjustStock(new AdjustStock());
			aForm.setVerify(false);
			
			AdjustStock ad = new AdjustStock();
			ad.setTransactionDate(DateUtil.stringValue(new Date(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			aForm.setAdjustStock(ad);
			
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
