package com.isecinc.pens.web.receiptAll;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.OrderLine;
import com.isecinc.pens.bean.ReceiptConfirm;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.model.MOrderLine;
import com.isecinc.pens.model.MReceipt;
/**
 * Member Order Action
 * 
 * @author atiz.b
 * @version $Id: MemberOrderAction.java,v 1.0 20/01/2010 00:00:00 atiz.b Exp $
 * 
 */
public class ReceiptAllAction extends I_Action {
	
	protected String search(ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ReceiptAllForm receiptAllForm = (ReceiptAllForm) form;
		try {

			ReceiptAllCriteria criteria = getSearchCriteria(request,receiptAllForm.getReceiptAllCriteria(), this.getClass().toString());
			receiptAllForm.setReceiptAllCriteria(criteria);

			List<OrderLine> resultsList = new MOrderLine().searchOrderLineConfirmReceipt(receiptAllForm);
			OrderLine[] results = null;
			if (resultsList != null && resultsList.size() > 0) {
				results = new OrderLine[resultsList.size()];
				resultsList.toArray(results);
			}
			receiptAllForm.setResults(results);
			if (results != null) {
				receiptAllForm.getReceiptAllCriteria().setSearchResult(results.length);
			} else {
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc());
			}
		} catch (Exception e) {
			request.setAttribute("Message",InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()+ e.getMessage());
			throw e;
		}
		return "search";
	}

	@Override
	protected String changeActive(ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String prepare(String id, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
	protected String save(ActionForm form, HttpServletRequest request,HttpServletResponse response) throws Exception 
	{
		ReceiptAllForm confirmForm = (ReceiptAllForm)form;
		User user = (User) request.getSession().getAttribute("user");
		
		List<ReceiptConfirm> allL = confirmForm.getConfirms();
		List<ReceiptConfirm> confirmL = new ArrayList<ReceiptConfirm>();
		
		for(ReceiptConfirm confirm :allL){
			if("Y".equals(confirm.getIsConfirm()))
				confirmL.add(confirm);
		}
		
		int no_of_confirmations = confirmL.size();
		log.debug("Total Confirmation =>"+no_of_confirmations);
		
		String receiptDate = confirmForm.getConfirmDate();
		
		MReceipt receiptService = new MReceipt();
		com.isecinc.pens.bean.ReceiptGenerateSummary summary =receiptService.createReceiptFromReceiptConfirmation(confirmL,receiptDate,user);
		confirmForm.setSummary(summary);
		
		return "summary";
	}


	@Override
	protected void setNewCriteria(ActionForm form) throws Exception {
		// TODO Auto-generated method stub
		ReceiptAllForm receiptForm = (ReceiptAllForm)form;
		receiptForm.getOrder().setOrderDateFrom("");
		receiptForm.getOrder().setOrderDateTo("");
		receiptForm.getOrder().setPaymentMethod("CS");
	}

}