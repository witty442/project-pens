package com.isecinc.pens.web.admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;

import util.DBCPConnectionProvider;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.web.I_Action;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.OrderLine;
import com.isecinc.pens.bean.OrderTransaction;
import com.isecinc.pens.bean.Receipt;
import com.isecinc.pens.bean.ReceiptGenerateSummary;
import com.isecinc.pens.bean.ReceiptLine;
import com.isecinc.pens.bean.TaxInvoice;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;
import com.isecinc.pens.model.MOrder;
import com.isecinc.pens.model.MOrderLine;
import com.isecinc.pens.model.MReceipt;
import com.isecinc.pens.model.MReceiptLine;
import com.isecinc.pens.model.MTaxInvoice;
import com.isecinc.pens.process.administer.ManageOrderReceiptProcess;
import com.isecinc.pens.process.confirm.ControlOrderTransaction;

public class ManageOrderReceiptAction extends I_Action {

	@Override
	protected String search(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ManageOrderReceiptForm manageOrderReceiptForm = (ManageOrderReceiptForm) form;
		try {
			/*manageOrderReceiptForm.setOrders(new ManageOrderReceiptProcess().getOrders(manageOrderReceiptForm
					.getDocumentDate()));*/
			manageOrderReceiptForm.setReceipts(new ManageOrderReceiptProcess().getReceipt(manageOrderReceiptForm
					.getDocumentDate()));
			/*manageOrderReceiptForm.setOrderSize(manageOrderReceiptForm.getOrders().size());*/
			manageOrderReceiptForm.setReceiptSize(manageOrderReceiptForm.getReceipts().size());

			if (manageOrderReceiptForm.getOrderSize() + manageOrderReceiptForm.getReceiptSize() == 0) {
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.RECORD_NOT_FOUND).getDesc());
			}
		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return "search";
	}

	@Override
	protected String save(ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		ManageOrderReceiptForm manageOrderReceiptForm = (ManageOrderReceiptForm) form;
		ReceiptGenerateSummary summary = new ReceiptGenerateSummary();
		try {
			User user = (User) request.getSession().getAttribute("user");
			Connection conn = null;
			Statement stmt = null;
			PreparedStatement psUpdateShipment = null;
			int tripNo = 0;
			int orderId = 0;
			try {
				conn = new DBCPConnectionProvider().getConnection(conn);
				String type = request.getParameter("type");
				Receipt receipt = null;
				for(Receipt confirm : manageOrderReceiptForm.getReceipts()){
					try{
						int receiptId = confirm.getId();
						tripNo = confirm.getTripNo();
						
						logger.debug("receipt_id["+receiptId+"]isCancel["+confirm.getIsCancel()+"]");
						
						if("Y".equals(confirm.getIsCancel()) && (type != null && receiptId != 0) ){	
							// cancel receipt
							receipt = new MReceipt().find(String.valueOf(receiptId));
							receipt.setDocStatus(Order.DOC_VOID);
							new MReceipt().saveWOCheckDup(receipt, user.getId(), conn);
							
							//ReceiptLine
							List<ReceiptLine> receiptLineList = new MReceiptLine().findTaxInvoiceId(receipt.getId());
							if(receiptLineList != null && receiptLineList.size()  >0){
								for(int i=0;i<receiptLineList.size();i++){
									ReceiptLine receiptLine = (ReceiptLine)receiptLineList.get(i);
									if(receiptLine != null && receiptLine.getTaxinvoiceId() != 0){
										
										/** Update TaxInvoice **/
										logger.debug("taxinvoiceId["+receiptLine.getTaxinvoiceId()+"]");
										TaxInvoice taxinvoice = new MTaxInvoice().getTaxInvoice(receiptLine.getTaxinvoiceId(), conn);
										taxinvoice.setTaxInvoiceStatus(Order.DOC_VOID);
										new MTaxInvoice().save(taxinvoice, false, user.getId(), conn);
										
									}
								}
							}
	
							// unpaid to order
							List<ReceiptLine> lines = new MReceiptLine().lookUp(receiptId);
							for (ReceiptLine l : lines) {
								
								if (l.getOrder() != null) {
									orderId = l.getOrder().getId();
									l.getOrder().setPayment("N");
									l.getOrder().setIsCash("N");
									new MOrder().save(l.getOrder(), user.getId(), conn);
									if (l.getOrderLine() == null || l.getOrderLine().getId() == 0) {
										List<OrderLine> orliLines = new MOrderLine().lookUp(l.getOrder().getId());
										for (OrderLine orliLine : orliLines) {
											orliLine.setPayment("N");
											orliLine.setConfirmReceiptDate(null);
											orliLine.setPrepay("N");
											if(!"CS".equals(orliLine.getPaymentMethod()))
												orliLine.setActNeedBill(0d);
											
											logger.debug("Update OrderLineId["+orliLine.getId()+"]");
											new MOrderLine().save(orliLine, user.getId(), conn);
										}
									} else {
										if (l.getOrderLine().getId() != 0) {
											l.getOrderLine().setPayment("N");
											l.getOrderLine().setConfirmReceiptDate(null);
											l.getOrderLine().setPrepay("N");
											if(!"CS".equals(l.getOrderLine().getPaymentMethod()))
												l.getOrderLine().setActNeedBill(0d);
											
											logger.debug("Update2 OrderLineId["+l.getOrderLine().getId()+"]");
											
											new MOrderLine().save(l.getOrderLine(), user.getId(), conn);
										}
									}
								}
							}//for
							
							/** Update Order Transaction **/
							OrderTransaction m = new OrderTransaction();
							m.setOrderId(orderId);
							m.setTripNo(tripNo);
							/** Update TotalTaxinvoice and TotalReceipt **/
							ControlOrderTransaction.processOrderTransactionCaseReceipt(conn, m, false);
                            
							/** add to Summary **/
							summary.addSucessReceipt(receipt);

						  }//if
					}catch(Exception e){
						e.printStackTrace();
						summary.addFailCancelReceipt(receipt, e.getMessage());
					}
				}//for

				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.SAVE_SUCCESS).getDesc());
			} catch (Exception e) {
				e.printStackTrace();
				request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
						+ e.getMessage());
			} finally {
				try {
					conn.setAutoCommit(true);
				} catch (Exception e2) {}
				try {
					if(psUpdateShipment != null){
						psUpdateShipment.close();psUpdateShipment=null;
					}
					if(conn != null){
						conn.close();
						conn = null;
					}
				} catch (Exception e2) {}
			}

			manageOrderReceiptForm.setOrders(new ManageOrderReceiptProcess().getOrders(manageOrderReceiptForm
					.getDocumentDate()));
			manageOrderReceiptForm.setReceipts(new ManageOrderReceiptProcess().getReceipt(manageOrderReceiptForm
					.getDocumentDate()));
			manageOrderReceiptForm.setOrderSize(manageOrderReceiptForm.getOrders().size());
			manageOrderReceiptForm.setReceiptSize(manageOrderReceiptForm.getReceipts().size());
			
			manageOrderReceiptForm.setSummary(summary);

		} catch (Exception e) {
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
					+ e.getMessage());
			throw e;
		}
		return "summaryCancel";
	}

	@Override
	protected String changeActive(ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String prepare(String id, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void setNewCriteria(ActionForm form) throws Exception {
	// TODO Auto-generated method stub

	}

}
