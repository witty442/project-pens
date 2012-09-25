package com.isecinc.pens.web.interim.autoreceipt;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import util.DBCPConnectionProvider;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.OrderLine;
import com.isecinc.pens.bean.Receipt;
import com.isecinc.pens.bean.ReceiptBy;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.interim.bean.IOrderToReceipt;
import com.isecinc.pens.interim.bean.MOrderToReceipt;
import com.isecinc.pens.interim.process.AutoCreateReceiptProcess;
import com.isecinc.pens.model.MOrder;
import com.isecinc.pens.model.MOrderLine;
import com.isecinc.pens.model.MReceipt;
import com.isecinc.pens.model.MReceiptLine;
import com.isecinc.pens.process.order.OrderProcess;

public class AutoReceiptAction extends I_Action {

	protected String prepare(String id, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		return "prepare";
	}

	protected String search(ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		return "search";
	}

	protected String save(ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		logger.debug("generate");
		
		try{
			AutoReceiptForm actionForm = (AutoReceiptForm) form;
			AutoCreateReceiptProcess process = new AutoCreateReceiptProcess();
			
			List<String> invoiceL = process.getInvoiceListFromExcel(actionForm.getInvoiceFile());
			if(invoiceL.size()==0){
				request.setAttribute("Message", "No Invoice To Load");
			}
			
			Connection conn = null;
			conn = new DBCPConnectionProvider().getConnection(conn);
			
			String sessionId = request.getSession().getId();
			
			process.loadInvoiceNoListToTable(invoiceL, conn,sessionId );
			String message = process.createReceipt(conn,sessionId);
			
			List<IOrderToReceipt> results = MOrderToReceipt.getOrderToReceiptList(conn, "AND SESSION_ID = ? ", new Object[]{sessionId});
			request.setAttribute("Message", message);
			actionForm.setResults(results);
		}
		catch(Exception ex){
			request.setAttribute("Message", ex.getMessage());
		}
		
		return "prepare";
	}

	protected String changeActive(ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	protected void setNewCriteria(ActionForm form) throws Exception {
		// TODO Auto-generated method stub

	}
	
	public ActionForward preparePD(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		return mapping.findForward("preparePD");
	}
	
	public ActionForward createPD(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) {
		
		logger.debug("generate PD Receipt");
		String month = request.getParameter("month");
		
		AutoReceiptForm actionForm = (AutoReceiptForm) form;
		
		try{
			if(StringUtils.isEmpty(month)){
				request.setAttribute("Message", "No Month");
			}
			else{
				StringBuffer whereClause = new StringBuffer("AND ISCASH = 'N' AND PAYMENT = 'N' AND MONTH(ORDER_DATE)= "+month);
				whereClause.append(" AND NOT EXISTS (SELECT 1 FROM T_RECEIPT rh , T_RECEIPT_LINE rl WHERE rh.RECEIPT_ID = rl.RECEIPT_ID ");
				whereClause.append(" 					AND rh.DOC_STATUS = 'SV' AND rl.ORDER_ID = T_ORDER.ORDER_ID) ");
				whereClause.append(" AND EXISTS (SELECT 1 FROM AD_USER u WHERE u.USER_ID = T_ORDER.USER_ID AND u.PD_PAID = 'Y') ");
				
				Order[] orders = new MOrder().search(whereClause.toString());
				
				List<IOrderToReceipt> result = new ArrayList<IOrderToReceipt>();
				
				int success = 0 ;
				int error = 0;
				
				if(orders != null && orders.length >0){
					MReceiptLine mReceipt = new MReceiptLine();
						
					for(Order order : orders){
						try {
							Receipt receipt = new Receipt();
							receipt.setIsPDPaid("N");
							receipt.setReceiptNo(order.getOrderNo());
							receipt.setReceiptDate(order.getOrderDate());
							receipt.setInternalBank("002");
							ReceiptBy by = new ReceiptBy();
							double creditAmt = mReceipt
									.calculateCreditAmount(order);
							by.setReceiptAmount(creditAmt);
							by.setRemainAmount(0d);
							by.setPaidAmount(creditAmt);
							by.setAllPaid("" + creditAmt);
							by.setAllBillId("" + order.getId());
							by.setPaymentMethod("CS");
							by.setWriteOff("N");
							by.setChequeDate("");
							List<ReceiptBy> byL = new ArrayList<ReceiptBy>();
							byL.add(by);
							List<OrderLine> lstLines = new MOrderLine()
									.lookUp(order.getId());
							if (!new OrderProcess().createAutoReceipt(
									receipt, order, lstLines, byL, null,
									order.getSalesRepresent(),
									new DBCPConnectionProvider()
											.getConnection(null))) {
								throw new Exception(InitialMessages
										.getMessages()
										.get(Messages.DUPLICATE).getDesc());
							}
							
							success++;
						} catch (Exception e) {
							// TODO: handle exception
							IOrderToReceipt ret = new IOrderToReceipt();
							ret.setOrder(order);
							ret.setErrMsg(e.getMessage());
							
							result.add(ret);
							error ++;
						}
					}
					actionForm.setResults(result);
					request.setAttribute("Message", "SUCESS "+success+" ERROR "+error);
				}
				else{
					throw new Exception("No Order To Generate");
				}
			}
		}
		catch(Exception ex){
			request.setAttribute("Message","ERROR : "+ ex.getMessage());
		}
		
		return mapping.findForward("preparePD");
	}
}
