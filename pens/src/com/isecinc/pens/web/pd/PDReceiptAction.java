package com.isecinc.pens.web.pd;

import java.sql.Connection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;

import util.DBCPConnectionProvider;
import util.DateToolsUtil;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.Receipt;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.model.MReceipt;
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
		
		PDReceiptForm pdForm = (PDReceiptForm)form;
		
		String orderNoFrom = pdForm.getReceipt().getOrderNoFrom();
		String orderNoTo = pdForm.getReceipt().getOrderNoTo();
		
		String receiptDateFrom = pdForm.getReceipt().getReceiptDateFrom();
		String receiptDateTo = pdForm.getReceipt().getReceiptDateTo();
		
		StringBuffer whereClause = new StringBuffer("AND ISPDPAID = 'N' AND DOC_STATUS = 'SV' ");
		if(!StringUtils.isEmpty(orderNoFrom)){
			whereClause.append(" AND RECEIPT_NO >= '"+orderNoFrom+"'");
		}
		
		if(!StringUtils.isEmpty(orderNoTo)){
			whereClause.append(" AND RECEIPT_NO >= '"+orderNoTo+"'");
		}
		
		if(!StringUtils.isEmpty(receiptDateFrom)){
			whereClause.append(" AND RECEIPT_DATE >= '"+DateToolsUtil.convertToTimeStamp(receiptDateFrom)+"'");
		}
		
		if(!StringUtils.isEmpty(receiptDateTo)){
			whereClause.append(" AND RECEIPT_DATE <= '"+DateToolsUtil.convertToTimeStamp(receiptDateTo)+"'");
		}
		
		Receipt[] pdReceipts = new MReceipt().search(whereClause.toString());
		
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
		
		User user = (User)request.getSession().getAttribute("user");
		
		if(!StringUtils.isEmpty(ids)){
			MReceipt mReceipt = new MReceipt();
			
			String[] pm = pms.split("[,]");
			String[] pdate = pdates.split("[,]");
			int idx = 0;
			
			Connection conn = new DBCPConnectionProvider().getConnection(null);
			
			for(String id:ids.split("[,]")){
				Receipt receipt = mReceipt.find(id);
				receipt.setPdPaymentMethod(pm[idx]);
				receipt.setPdPaidDate(pdate[idx]);
				receipt.setIsPDPaid("Y");
				idx++;
				
				mReceipt.saveWOCheckDup(receipt, user.getId(), conn);
			}
		}
		
		return "prepare";
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
