package com.isecinc.pens.web.pd;

import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import util.DBCPConnectionProvider;
import util.DateToolsUtil;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.OrderLine;
import com.isecinc.pens.bean.Receipt;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.model.MOrder;
import com.isecinc.pens.model.MOrderLine;
import com.isecinc.pens.model.MReceipt;
import com.isecinc.pens.process.order.OrderProcess;
import com.isecinc.pens.web.sales.OrderForm;
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
		Connection conn = null;
		User user = (User)request.getSession().getAttribute("user");
		try{
			if(!StringUtils.isEmpty(ids)){
		
				String[] pm = pms.split("[,]");
				String[] pdate = pdates.split("[,]");
				int idx = 0;
				
				conn = new DBCPConnectionProvider().getConnection(null);
				conn.setAutoCommit(false);
				
				//Case PD_PAID =N
				if( !Utils.isNull(user.getPdPaid()).equalsIgnoreCase("Y")){
					MOrder mOrder = new MOrder();
					MReceipt mReceipt = new MReceipt();
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
						idx++;
						
						mReceipt.saveReceiptCasePDPaidNo(conn,receiptSave,user);
					}//for
				}else{
					//Case PD_PAID =Y
					MReceipt mReceipt = new MReceipt();
					for(String id:ids.split("[,]")){
						Receipt receipt = mReceipt.find(id);
						receipt.setPdPaymentMethod(pm[idx]);
						receipt.setPdPaidDate(pdate[idx]);
						receipt.setIsPDPaid("Y");
						idx++;
						
						mReceipt.saveWOCheckDup(receipt, user.getId(), conn);
					}//for
				}//if
			}//if ids
			
			conn.commit();
			request.setAttribute("Message","บันทึกข้อมูลเรียบร้อยแล้ว");
			
		}catch(Exception e){
			conn.rollback();
			logger.error(e.getMessage(),e);
		}finally{
			conn.close();
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
