package com.isecinc.pens.web.report.creditpaid;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import util.DateToolsUtil;

import com.isecinc.core.bean.Messages;
import com.isecinc.core.bean.References;
import com.isecinc.core.report.I_ReportAction;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialMessages;

/**
 * Detailed Sales Report Action.
 * 
 * @author Aneak.t
 * @version $Id: DetailedSalesReportAction.java,v 1.0 20/01/2011 15:52:00 aneak.t Exp $
 * 
 */

public class CreditPaidReportAction extends I_ReportAction<CreditPaidReport>{

	public ActionForward prepareReport(ActionMapping mapping, ActionForm form, HttpServletRequest request,HttpServletResponse response) {
		logger.debug("Customer Search Page");
		CreditPaidReportForm reportForm = (CreditPaidReportForm)form;
		User user = (User) request.getSession().getAttribute("user");
		try {
			if(Utils.isNull(request.getParameter("action")).equals("new")){
			   reportForm.setBean(new CreditPaidReport());
			   //init monthList
			   request.getSession().setAttribute("monthList", initMonthList());
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			request.setAttribute("Message", InitialMessages.getMessages().get(Messages.FETAL_ERROR).getDesc()
			+ e.getMessage());
		}
		return mapping.findForward("report"); 
	}
	/**
	 * Search for report.
	 */
	protected List<CreditPaidReport> searchReport(ActionForm form,
			HttpServletRequest request, HttpServletResponse response,
			HashMap parameterMap, Connection conn)
			throws Exception {
		
		CreditPaidReportForm reportForm = (CreditPaidReportForm)form;
		User user = (User)request.getSession().getAttribute("user");
		List<CreditPaidReport> listData = null;
		//Initial parameter
		try{
			//convert date to display
			Date dateDisp = Utils.parse(reportForm.getBean().getMonth()+"01", "yyyyMMdd");
			String dateDispStr = Utils.stringValue(dateDisp, Utils.MMMM_YYYY,Utils.local_th);
			
			parameterMap.put("monthDisp",dateDispStr);
			parameterMap.put("user_code",user.getCode());
			parameterMap.put("user_name",user.getName());
			
			CreditPaidReport report = null;
			if( Utils.isNull(user.getPdPaid()).equalsIgnoreCase("Y")){
				report = getDataCreditPaid_HavePDReportProcess(reportForm.getBean(), user, conn);
			}else{
				report = getDataCreditPaid_NoPDReportProcess(reportForm.getBean(), user, conn);
			}
			if(report != null){
				parameterMap.put("total_order_amt",report.getTotalOrderAmt());
				listData = report.getItems();
			}
			setFileType(reportForm.getFileType());
			setFileName("credit_paid_report");
		
		  return listData;
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}
	}

public CreditPaidReport getDataCreditPaid_HavePDReportProcess(CreditPaidReport t, User user,Connection conn) throws Exception {
		
		List<CreditPaidReport> lstData = new ArrayList<CreditPaidReport>();
		CreditPaidReport detailedSales = null;
		StringBuilder sql = new StringBuilder();
		Statement stmt = null;
		ResultSet rs = null;
		int no = 0;
		double totalOrderAmt = 0;
		try {
			sql.append("\n  SELECT  ");
			sql.append("\n  o.order_no  ");
			sql.append("\n  ,o.order_date  ");
			sql.append("\n  ,cus.CODE AS CUSTOMER_CODE ");
			sql.append("\n  ,cus.NAME AS CUSTOMER_NAME ");
			sql.append("\n  ,o.doc_status   ");
			sql.append("\n  ,r.pdpaid_date  ");
			sql.append("\n  ,r.PAYMENT_METHOD2 ");
			sql.append("\n  ,(select cheque_date from t_pd_receipt_his h where h.order_no = o.order_no) as cheque_date");
			sql.append("\n  ,SUM(o.NET_AMOUNT)  AS RECEIPT_AMOUNT ");
			sql.append("\n   from t_order o, m_customer cus  ");
			sql.append("\n   ,( ");
			sql.append("\n     select l.order_id  ");
			sql.append("\n     ,h.payment_method  ");
			sql.append("\n     ,h.PDPAID_DATE ");
			sql.append("\n     ,h.PD_PAYMENTMETHOD  as PAYMENT_METHOD2 ");
			sql.append("\n     from t_receipt h, t_receipt_line l ,t_receipt_match m, t_receipt_by b ");
			sql.append("\n     where h.receipt_id = l.receipt_id  ");
			sql.append("\n     and m.RECEIPT_LINE_ID = l.RECEIPT_LINE_ID  ");
			sql.append("\n     and b.RECEIPT_BY_ID = m.RECEIPT_BY_ID  ");
			sql.append("\n     AND h.ISPDPAID = 'Y' and h.doc_status ='SV'"); //get pd paid only
			sql.append("\n   ) r ");
			sql.append("\n   where 1=1 ");
			sql.append("\n   and o.CUSTOMER_ID = cus.CUSTOMER_ID ");
			sql.append("\n   and o.order_id = r.order_id ");
			sql.append("\n   and o.DOC_STATUS ='SV' ");
			sql.append("\n   and o.ORDER_TYPE = '" + user.getOrderType().getKey() + "' ");
			sql.append("\n   and o.USER_ID = " + user.getId());
			//sql.append("\n   and DATE(o.ORDER_DATE) >= '2018-04-01'");
			sql.append("\n   and DATE_FORMAT(r.pdpaid_date, '%Y%m')  = '" + t.getMonth() + "' ");
			sql.append("\n   and o.IsCash = 'N' ");//Credit Only
			sql.append("\n   group by o.order_no,o.order_date ,cus.CODE ,cus.NAME ,o.doc_status ,r.pdpaid_date ,r.PAYMENT_METHOD2 ");
			sql.append("\n   ORDER BY o.order_no  ");
			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql.toString());
			while(rs.next()){
				detailedSales = new CreditPaidReport();
				no++;
				detailedSales.setId(no);
				detailedSales.setOrderNo(rs.getString("ORDER_NO"));
				detailedSales.setStatus(Utils.isNull(rs.getString("doc_status")));
				detailedSales.setOrderDate(DateToolsUtil.convertToString(rs.getTimestamp("ORDER_DATE")));
				detailedSales.setCustomerCode(Utils.isNull(rs.getString("CUSTOMER_CODE")));
				detailedSales.setCustomerName(Utils.isNull(rs.getString("CUSTOMER_NAME")));
				detailedSales.setOrderAmount(rs.getDouble("RECEIPT_AMOUNT"));
				
				if(rs.getTimestamp("pdpaid_date") != null){
				   detailedSales.setPdDate(DateToolsUtil.convertToString(rs.getTimestamp("pdpaid_date")));
				}else{
				   detailedSales.setPdDate("");
				}
				if("CS".equals(Utils.isNull(rs.getString("PAYMENT_METHOD2")))){
				   detailedSales.setPaymentMethod("à§Ô¹Ê´");
				}else if("CH".equals(Utils.isNull(rs.getString("PAYMENT_METHOD2")))){
				   detailedSales.setPaymentMethod("àªç¤");
				}
				if(rs.getTimestamp("cheque_date") != null){
				   detailedSales.setChequeDate(DateToolsUtil.convertToString(rs.getTimestamp("cheque_date")));
				}else{
				   detailedSales.setChequeDate("");
				}
				
				totalOrderAmt += detailedSales.getOrderAmount();
				
				lstData.add(detailedSales);
			}
			
			t.setTotalOrderAmt(totalOrderAmt);
			t.setItems(lstData);
			
		} catch (Exception e) {
			throw e;
		}finally{
			try {
				rs.close();
				stmt.close();
			} catch (Exception e2) {}
		}
		
		return t;
	}

public CreditPaidReport getDataCreditPaid_NoPDReportProcess(CreditPaidReport t, User user,Connection conn) throws Exception {
	List<CreditPaidReport> lstData = new ArrayList<CreditPaidReport>();
	CreditPaidReport detailedSales = null;
	StringBuilder sql = new StringBuilder();
	Statement stmt = null;
	ResultSet rs = null;
	int no = 0;
	double totalOrderAmt = 0;
	try {
		sql.append("\n  SELECT  ");
		sql.append("\n   o.order_no  ");
		sql.append("\n  ,o.order_date  ");
		sql.append("\n  ,cus.CODE AS CUSTOMER_CODE ");
		sql.append("\n  ,cus.NAME AS CUSTOMER_NAME ");
		sql.append("\n  ,'SV' as doc_status   ");
		sql.append("\n  ,o.pdpaid_date  ");
		sql.append("\n  ,o.PD_PAYMENTMETHOD ");
		sql.append("\n  ,(select cheque_date from t_pd_receipt_his h where h.order_no = o.order_no) as cheque_date");
		sql.append("\n  ,SUM(o.RECEIPT_AMOUNT)  AS RECEIPT_AMOUNT ");
		sql.append("\n   from t_receipt_pdpaid_no o, m_customer cus  ");
		sql.append("\n   where 1=1 ");
		sql.append("\n   and o.CUSTOMER_ID = cus.CUSTOMER_ID ");
		sql.append("\n   and o.CREATED_BY = " + user.getId());
		//sql.append("\n   and DATE(o.ORDER_DATE) >= '2018-04-01'");
		sql.append("\n   and DATE_FORMAT(o.pdpaid_date, '%Y%m')  = '" + t.getMonth() + "' ");
		sql.append("\n   group by o.order_no,o.order_date ,cus.CODE ,cus.NAME ,o.pdpaid_date ,o.PD_PAYMENTMETHOD ");
		sql.append("\n   ORDER BY o.order_no  ");
		
		logger.debug("sql:"+sql);
		stmt = conn.createStatement();
		rs = stmt.executeQuery(sql.toString());
		while(rs.next()){
			detailedSales = new CreditPaidReport();
			no++;
			detailedSales.setId(no);
			detailedSales.setOrderNo(rs.getString("ORDER_NO"));
			detailedSales.setStatus(Utils.isNull(rs.getString("doc_status")));
			detailedSales.setOrderDate(DateToolsUtil.convertToString(rs.getTimestamp("ORDER_DATE")));
			detailedSales.setCustomerCode(Utils.isNull(rs.getString("CUSTOMER_CODE")));
			detailedSales.setCustomerName(Utils.isNull(rs.getString("CUSTOMER_NAME")));
			detailedSales.setOrderAmount(rs.getDouble("RECEIPT_AMOUNT"));
			
			if(rs.getTimestamp("pdpaid_date") != null){
			   detailedSales.setPdDate(DateToolsUtil.convertToString(rs.getTimestamp("pdpaid_date")));
			}else{
			   detailedSales.setPdDate("");
			}
			if("CS".equals(Utils.isNull(rs.getString("PD_PAYMENTMETHOD")))){
			   detailedSales.setPaymentMethod("à§Ô¹Ê´");
			}else if("CH".equals(Utils.isNull(rs.getString("PD_PAYMENTMETHOD")))){
			   detailedSales.setPaymentMethod("àªç¤");
			}
			if(rs.getTimestamp("cheque_date") != null){
			   detailedSales.setChequeDate(DateToolsUtil.convertToString(rs.getTimestamp("cheque_date")));
			}else{
			   detailedSales.setChequeDate("");
			}
			totalOrderAmt += detailedSales.getOrderAmount();
			
			lstData.add(detailedSales);
		}
		
		t.setTotalOrderAmt(totalOrderAmt);
		t.setItems(lstData);
		
	} catch (Exception e) {
		throw e;
	}finally{
		try {
			rs.close();
			stmt.close();
		} catch (Exception e2) {}
	}
	
	return t;
}
	 public List<References> initMonthList() throws Exception{
	    	List<References> monthList = new ArrayList<References>();
	    	References ref = null;
	    	String yyyymm = "";
	    	String mmmyyyyDisp = "";
	    	Calendar c = Calendar.getInstance();
	    	for(int i=0;i<=12;i++){
	    		yyyymm = Utils.stringValue(c.getTime(), Utils.YYYY_MM);
	    		mmmyyyyDisp = Utils.stringValue(c.getTime(), Utils.MMMM_YYYY,Utils.local_th);
	    		ref = new References(yyyymm,mmmyyyyDisp);
	    		monthList.add(ref);
	    		//step prev month
	    		c.add(Calendar.MONTH, -1);
	    	}
	    	
	    	return monthList;
	    }
	/**
	 * Set New criteria.
	 */
	protected void setNewCriteria(ActionForm form) throws Exception {
	}
}
