package com.isecinc.pens.web.pd;

import java.sql.Connection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import util.DateToolsUtil;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.OrderLine;
import com.isecinc.pens.bean.Receipt;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.model.MOrder;
import com.isecinc.pens.model.MOrderLine;
import com.isecinc.pens.model.MReceipt;
import com.isecinc.pens.process.order.OrderProcess;
import com.isecinc.pens.web.sales.OrderForm;
import com.pens.util.DBCPConnectionProvider;
/**
 * Receipt Action
 * 
 * @author atiz.b
 * @version $Id: ReceiptAction.java,v 1.0 26/10/2010 00:00:00 atiz.b Exp $
 * 
 *          atiz.b : search all in order type by dd user
 * 
 */
public class PDReceiptAction extends I_Action {

	protected String prepare(String id, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	protected String search(ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		User user = (User) request.getSession(true).getAttribute("user");
		PDReceiptForm pdForm = (PDReceiptForm)form;
		
		String orderNoFrom = pdForm.getReceipt().getOrderNoFrom();
		String orderNoTo = pdForm.getReceipt().getOrderNoTo();
		
		String receiptDateFrom = pdForm.getReceipt().getReceiptDateFrom();
		String receiptDateTo = pdForm.getReceipt().getReceiptDateTo();
		
		StringBuffer whereClause = new StringBuffer("\n AND ISPDPAID = 'N' AND DOC_STATUS = 'SV' ");
		if(!StringUtils.isEmpty(orderNoFrom)){
			whereClause.append("\n  AND RECEIPT_NO >= '"+orderNoFrom+"'");
		}
		
		if(!StringUtils.isEmpty(orderNoTo)){
			whereClause.append("\n  AND RECEIPT_NO <= '"+orderNoTo+"'");
		}
		
		if(!StringUtils.isEmpty(receiptDateFrom)){
			whereClause.append("\n  AND RECEIPT_DATE >= '"+DateToolsUtil.convertToTimeStamp(receiptDateFrom)+"'");
		}
		
		if(!StringUtils.isEmpty(receiptDateTo)){
			whereClause.append("\n  AND RECEIPT_DATE <= '"+DateToolsUtil.convertToTimeStamp(receiptDateTo)+"'");
		}
		logger.debug("sql:"+whereClause);
		
		Receipt[] pdReceipts = null; 
		 
		//WIT Edit Case PD_PAID = N and pay by credit
		if( !Utils.isNull(user.getPdPaid()).equalsIgnoreCase("Y")){
		   pdReceipts = new MReceipt().searchOptCasePDPAID_NO(pdForm,user);
		}else{
		   pdReceipts = new MReceipt().search(whereClause.toString());
		}
		
		if(pdReceipts != null && pdReceipts.length > 0){
			pdForm.getCriteria().setSearchResult(pdReceipts.length);
			pdForm.setPdReceipts(pdReceipts);
		}
		else{
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc());
		}
		
		return "prepare";
	}

	protected String save(ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub

		String ids = request.getParameter("ids");
		String pms = request.getParameter("pms");
		String pdates = request.getParameter("pdates");
		String chequedates = request.getParameter("pchequeDates");
		Connection conn = null;
		User user = (User)request.getSession().getAttribute("user");
		MOrder mOrder = new MOrder();
		MReceipt mReceipt = new MReceipt();
		int idx = 0;
		try{
			logger.debug("ids:"+ids);
			logger.debug("chequedates:"+chequedates);
			logger.debug("chequedates:"+chequedates.split("[,]").length);
			logger.debug("pdates:"+pdates);
			logger.debug("pdates:"+pdates.split("[,]").length);
			
			if(!StringUtils.isEmpty(ids)){
				logger.debug("PD_PAID:"+user.getPdPaid());
				String[] pm = pms.split("[,]");
				String[] pdate = pdates.split("[,]");
				String[] chequedate = null;
				if( !Utils.isNull(chequedates).equals("")){
					chequedate = chequedates.split("[,]");
				}
					
				conn = DBConnection.getInstance().getConnection();
				conn.setAutoCommit(false);
				
				//Case PD_PAID =N
				if( !Utils.isNull(user.getPdPaid()).equalsIgnoreCase("Y")){
					logger.debug("PD PAID = N ");
					for(String id:ids.split("[,]")){
						Order order = mOrder.find(id);
						Receipt receiptSave = new Receipt();
						receiptSave.setReceiptNo(order.getOrderNo());
						receiptSave.setReceiptDate(order.getOrderDate());
						receiptSave.setCustomerId(order.getCustomerId());
						receiptSave.setReceiptAmount(order.getNetAmount());
						receiptSave.setPdPaymentMethod(pm[idx]);
						receiptSave.setPdPaidDate(pdate[idx]);
						receiptSave.setIsPDPaid("Y");
						if( !Utils.isNull(chequedate[idx]).equals("null") && !Utils.isNull(chequedate[idx]).equals("")){
						    receiptSave.setChequeDate(chequedate[idx]);
						}else{
							receiptSave.setChequeDate("");	
						}
						
						//Save to DB
						mReceipt.saveReceiptCasePDPaidNo(conn,receiptSave,user);
						
						/** Save Receipt History **/
						mReceipt.savePDReceiptHis(conn,receiptSave,user);
						
						idx++;
					}//for
				}else{
					//Case PD_PAID =Y
					logger.debug("PD PAID = Y ");
					for(String id:ids.split("[,]")){
						Receipt receipt = mReceipt.find(id);
						receipt.setPdPaymentMethod(pm[idx]);
						receipt.setPdPaidDate(pdate[idx]);
						receipt.setIsPDPaid("Y");
						//Edit 09/01/2562 add 
						if( !Utils.isNull(chequedate[idx]).equals("null") && !Utils.isNull(chequedate[idx]).equals("")){
						   receipt.setChequeDate(chequedate[idx]);
						}else{
						   receipt.setChequeDate("");	
						}
						
						//Save to DB
						mReceipt.updateReceiptFromPDReceipt(receipt, user.getId(), conn);
						
						/** Save Receipt History **/
						/** Case Van PD_PAID =Y  (order_no = receipt_no) **/
						Order order = mOrder.findByWhereCond(conn,"where order_no ='"+receipt.getReceiptNo()+"'");
						logger.debug("orderNo:"+order.getOrderNo());
						Receipt receiptSave = new Receipt();
						receiptSave.setReceiptNo(order.getOrderNo());
						receiptSave.setReceiptDate(order.getOrderDate());
						receiptSave.setCustomerId(order.getCustomerId());
						receiptSave.setReceiptAmount(order.getNetAmount());
						receiptSave.setPdPaymentMethod(pm[idx]);
						receiptSave.setPdPaidDate(pdate[idx]);
						if( !Utils.isNull(chequedate[idx]).equals("null") && !Utils.isNull(chequedate[idx]).equals("")){
						    receiptSave.setChequeDate(chequedate[idx]);
						}else{
							receiptSave.setChequeDate("");	
						}
						
						//Save To DB
						mReceipt.savePDReceiptHis(conn,receiptSave,user);
						
						idx++;
					}//for
				}//if
				
			}//if ids
	
			conn.commit();
			request.setAttribute("Message","บันทึกข้อมูลเรียบร้อยแล้ว");
			logger.debug("Conn commit");
		}catch(Exception e){
			conn.rollback();
			logger.error(e.getMessage(),e);
		}finally{
			try{
			  conn.close();
			}catch(Exception ee){
				ee.printStackTrace();
			}
		}
		return "prepare";
	}
	
	public ActionForward clearForm(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		PDReceiptForm aForm = (PDReceiptForm) form;
		try {
			aForm.getCriteria().setReceipt(new Receipt());
			
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			e.printStackTrace();
		}
		return mapping.findForward("prepare");
	}
	
	public ActionForward prepareAdmin(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		PDReceiptForm aForm = (PDReceiptForm) form;
		try {
			String curDate = DateToolsUtil.stringDateValue(new Date(), DateToolsUtil.DD_MM_YYYY_WITH_SLASH,DateToolsUtil.local_th);
			Receipt r = new Receipt();
			r.setReceiptDateFrom(curDate);
			r.setReceiptDateTo(curDate);
			aForm.setReceipt(r);
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			e.printStackTrace();
		}
		return mapping.findForward("admin");
	}
	public ActionForward searchAdmin(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		PDReceiptForm pdForm = (PDReceiptForm) form;
		User user = (User)request.getSession().getAttribute("user");
		try {
			String orderNoFrom = pdForm.getReceipt().getOrderNoFrom();
			String orderNoTo = pdForm.getReceipt().getOrderNoTo();
			
			String receiptDateFrom = pdForm.getReceipt().getReceiptDateFrom();
			String receiptDateTo = pdForm.getReceipt().getReceiptDateTo();
			
			StringBuffer whereClause = new StringBuffer("\n AND ISPDPAID = 'N' AND DOC_STATUS = 'SV' ");
			if(!StringUtils.isEmpty(orderNoFrom)){
				whereClause.append("\n  AND RECEIPT_NO >= '"+orderNoFrom+"'");
			}
			
			if(!StringUtils.isEmpty(orderNoTo)){
				whereClause.append("\n  AND RECEIPT_NO >= '"+orderNoTo+"'");
			}
			
			if(!StringUtils.isEmpty(receiptDateFrom)){
				whereClause.append("\n  AND RECEIPT_DATE >= '"+DateToolsUtil.convertToTimeStamp(receiptDateFrom)+"'");
			}
			
			if(!StringUtils.isEmpty(receiptDateTo)){
				whereClause.append("\n  AND RECEIPT_DATE <= '"+DateToolsUtil.convertToTimeStamp(receiptDateTo)+"'");
			}
			logger.debug("sql:"+whereClause);
			
			Receipt[] pdReceipts = null; 
			pdReceipts = PDReceiptDAO.searchPDReceiptHistory(pdForm, user);
			
			if(pdReceipts != null && pdReceipts.length > 0){
				pdForm.getCriteria().setSearchResult(pdReceipts.length);
				pdForm.setPdReceipts(pdReceipts);
			}
			else{
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc());
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			e.printStackTrace();
		}
		return mapping.findForward("admin");
	}
	public ActionForward saveAdmin(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		PDReceiptForm aForm = (PDReceiptForm) form;
		User user = (User)request.getSession().getAttribute("user");
		MOrder mOrder = new MOrder();
		MReceipt mReceipt = new MReceipt();
		Connection conn = null;
		String pdPaid = "";
		try {
			logger.debug("saveAdmin");
			conn = DBConnection.getInstance().getConnection();
			String orderNo = Utils.isNull(request.getParameter("orderNo"));
			
			//get pdPaid not userAdmin
			pdPaid = PDReceiptDAO.getPdPaid(conn);
			
			logger.debug("orderNo["+orderNo+"]pdPaid["+pdPaid+"]");
			
			//Case PD_PAID =N
			if( !Utils.isNull(pdPaid).equalsIgnoreCase("Y")){
				Receipt receipt = new Receipt();
				receipt.setReceiptNo(orderNo);
				
				//delete t_pd_receipt_his To DB
				PDReceiptDAO.deletePdReceiptHis(conn, receipt);
				
				//delete t_receipt_pdpaid_no
				PDReceiptDAO.deleteReceiptPdPaidNo(conn,receipt);
				
			}else{
				//PD_PAID =Y
				
				//Step 1 -> update receipt IsPDPaid,pdPaidDate =null
				Receipt[] receiptArr = mReceipt.search(" and receipt_no ='"+orderNo+"'");
				Receipt receipt = receiptArr[0];
				receipt.setPdPaymentMethod("");
				receipt.setPdPaidDate("");
				receipt.setIsPDPaid("N");
				receipt.setChequeDate("");
				//update receipt
				PDReceiptDAO.updateReceiptCancelPdPaid(conn,receipt);
				
				//delete pd_receipt_his To DB
				PDReceiptDAO.deletePdReceiptHis(conn, receipt);
			}
			
			request.setAttribute("Message","ยกเลิก รายการบันทึกรับเงินเชื่อ เรียบร้อยแล้ว");
		} catch (Exception e) {
			request.setAttribute("Message","Cannot save \n"+e.getMessage());
			e.printStackTrace();
		}
		return mapping.findForward("admin");
	}
	@Override
	protected String changeActive(ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void setNewCriteria(ActionForm form) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
