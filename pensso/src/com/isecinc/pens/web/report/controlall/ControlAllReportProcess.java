package com.isecinc.pens.web.report.controlall;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.isecinc.core.report.I_ReportProcess;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.report.invoicepayment.InvoicePaymentAllReport;
import com.isecinc.pens.report.invoicepayment.InvoicePaymentReport;
import com.pens.util.DateToolsUtil;

/**
 * Detailed Sales Report
 * 
 * @author Aneak.t
 * @version $Id: CreditControlReport.java,v 1.0 20/01/2011 15:52:00 aneak.t Exp $
 * 
 */

public class ControlAllReportProcess extends I_ReportProcess<ControlAllReport>{

	/**
	 * Search for report.
	 */
	public List<ControlAllReport> doReport(ControlAllReport t, User user,
			Connection conn) throws Exception {
		
		List<ControlAllReport> lstData = new ArrayList<ControlAllReport>();
		ControlAllReport detailedSales = null;
		StringBuilder sql = new StringBuilder();
		Statement stmt = null;
		ResultSet rs = null;
		int no = 0;
		try {
			
			
		} catch (Exception e) {
			throw e;
		}finally{
			try {
				rs.close();
				stmt.close();
			} catch (Exception e2) {}
		}
		
		return lstData;
	}
	
	public ControlAllReport getMoveOrder(ControlAllReport t, User user,Connection conn,String moveOrderType) throws Exception {
	
		List<ControlAllReport> lstAllData = new ArrayList<ControlAllReport>();
		List<ControlAllReport> lstData = new ArrayList<ControlAllReport>();
		ControlAllReport detailedSales = null;
		StringBuilder sql = new StringBuilder();
		Statement stmt = null;
		ResultSet rs = null;
		Map<String, String> chkDup = new HashMap<String, String>();
		int no = 0;
		try {
			sql.append("\n SELECT  o.request_number ,o.request_date,o.status  ");
			sql.append("\n  from t_move_order o ");
			sql.append("\n  where 1=1 ");
			sql.append("\n  and o.move_order_type = '" + moveOrderType+"'");
			sql.append("\n  and o.USER_ID = " + user.getId());
			sql.append("\n  and o.status IN('SV','VO') ");
			sql.append("\n  and o.request_date >= '" + DateToolsUtil.convertToTimeStamp(t.getStartDate()) + "' ");
			sql.append("\n  and o.request_date <= '" + DateToolsUtil.convertToTimeStamp(t.getEndDate()) + "' ");
			sql.append("\n  order by o.request_date,o.request_number asc");
			
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql.toString());
			while(rs.next()){
				detailedSales = new ControlAllReport();
				no++;
				detailedSales.setRecordType("");
				
				if(chkDup.get(rs.getString("request_date"))==null){
					detailedSales.setDate(Utils.stringValue(rs.getDate("request_date"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				}else{
					detailedSales.setDate("");
				}
				
				if(Utils.isNull(rs.getString("status")).equalsIgnoreCase("VO")){
				    detailedSales.setDocNo(Utils.isNull(rs.getString("request_number")) +" (ยกเลิก)");
                }else{
                	detailedSales.setDocNo(Utils.isNull(rs.getString("request_number")));
                }
				lstData.add(detailedSales);
				//set for check dup date
				chkDup.put(rs.getString("request_date"), rs.getString("request_date"));
			}
			
			//add Header By Type
			detailedSales = new ControlAllReport();
			if(moveOrderType.equalsIgnoreCase("MoveOrderRequisition")){
			   detailedSales.setRecordType("ใบเบิกสินค้า        รวม "+lstData.size()+"  ใบ");
			}else{
			   detailedSales.setRecordType("ใบคืนสินค้า        รวม "+lstData.size()+"  ใบ");
			}
			detailedSales.setDate("");
			detailedSales.setDocNo("");
			
			lstAllData.add(detailedSales);
			lstAllData.addAll(lstData);
			
			t.setItems(lstAllData);
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

	public ControlAllReport getOrder(ControlAllReport t, User user,Connection conn) throws Exception {
		
		List<ControlAllReport> lstAllData = new ArrayList<ControlAllReport>();
		List<ControlAllReport> lstData = new ArrayList<ControlAllReport>();
		ControlAllReport detailedSales = null;
		StringBuilder sql = new StringBuilder();
		Statement stmt = null;
		ResultSet rs = null;
		Map<String, String> chkDup = new HashMap<String, String>();
		int no = 0;
		try {
			sql.append("\n SELECT o.order_no ,o.order_date ,doc_status ");
			sql.append("\n  from t_order o ");
			sql.append("\n  where 1=1 ");
			sql.append("\n  and o.USER_ID = " + user.getId());
			//sql.append("\n  and o.doc_status ='SV' "); get All status 14/09/2562
			sql.append("\n  and o.order_date >= '" + DateToolsUtil.convertToTimeStamp(t.getStartDate()) + "' ");
			sql.append("\n  and o.order_date <= '" + DateToolsUtil.convertToTimeStamp(t.getEndDate()) + "' ");
			sql.append("\n  order by o.order_date,o.order_no asc");
			
			logger.debug("sql:"+sql);
			
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql.toString());
			while(rs.next()){
				detailedSales = new ControlAllReport();
				no++;
				detailedSales.setRecordType("");
			
				if(chkDup.get(rs.getString("order_date"))==null){
					detailedSales.setDate(Utils.stringValue(rs.getDate("order_date"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				}else{
					detailedSales.setDate("");
				}
				if("VO".equalsIgnoreCase(rs.getString("doc_status"))){
				   detailedSales.setDocNo(Utils.isNull(rs.getString("order_no")) +"   (ยกเลิก)");
				}else{
				   detailedSales.setDocNo(Utils.isNull(rs.getString("order_no")));
				}
				
				lstData.add(detailedSales);
				//set for check dup date
				chkDup.put(rs.getString("order_date"), rs.getString("order_date"));
			}
			
			//add Header By Type
			detailedSales = new ControlAllReport();
			detailedSales.setRecordType("บิลขาย       รวม "+lstData.size()+"  ใบ");
			detailedSales.setDate("");
			detailedSales.setDocNo("");
			
			lstAllData.add(detailedSales);
			lstAllData.addAll(lstData);
			
			t.setItems(lstAllData);
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
	
}
