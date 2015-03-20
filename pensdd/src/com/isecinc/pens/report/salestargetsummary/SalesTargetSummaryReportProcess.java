package com.isecinc.pens.report.salestargetsummary;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import util.DateToolsUtil;

import com.isecinc.core.report.I_ReportProcess;
import com.isecinc.pens.bean.SalesTargetNew;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.model.MSalesTargetNew;
import com.isecinc.pens.model.MOrderLine;


/**
 * InvoiceDetailReportProcess Report
 * 
 * @author Aneak.t
 * @version $Id: PerformanceReportProcess.java,v 1.0 10/11/2010 15:52:00 aneak.t Exp $
 * 
 */

public class SalesTargetSummaryReportProcess extends I_ReportProcess<SalesTargetSummaryReport> {
	/**
	 * Search for performance report.
	 */
	public List<SalesTargetSummaryReport> doReport(SalesTargetSummaryReport report, User user, Connection conn) throws Exception {
		SalesTargetNew[] stns = null;
		
		int i =0;
		List<SalesTargetSummaryReport> pos = new ArrayList<SalesTargetSummaryReport>();
		// Get Sales Target Match With Sales Order
		
		String p_dateFrom =report.getDateFrom();
		String p_dateTo = report.getDateTo();
		String p_productCodeFrom = report.getProductCodeFrom();
		String p_productCodeTo = report.getProductCodeTo();
		int month = DateToolsUtil.convertToTimeStamp(p_dateFrom).getMonth()+1;
		
		StringBuffer whereClause = new StringBuffer();
		whereClause.append(" AND User_ID = "+user.getId());
		whereClause.append(" AND MONTH(TARGET_FROM) = "+month);
		if(p_productCodeFrom != null && p_productCodeFrom.length() >0)
			whereClause.append(" AND ProductCode >= "+p_productCodeFrom);
		
		if(p_productCodeTo != null && p_productCodeTo.length() >0 )
			whereClause.append(" AND ProductCode <= "+p_productCodeTo);
		
		MSalesTargetNew sales = new MSalesTargetNew();
		sales.TABLE_NAME = "M_Sales_Target_New_v";
		
		stns = new MSalesTargetNew().search(whereClause.toString());
		
		if(stns != null){
			new MOrderLine().compareSalesTarget(stns, p_dateFrom, p_dateTo);
			
			for(SalesTargetNew stn : stns){
				SalesTargetSummaryReport result = new SalesTargetSummaryReport();
				result.setDateFrom(p_dateFrom);
				result.setDateTo(p_dateTo);
				result.settDateFrom(DateToolsUtil.convertToTimeStamp(p_dateFrom));
				result.settDateTo(DateToolsUtil.convertToTimeStamp(p_dateTo));
				result.setProductCode(stn.getProduct().getCode());
				result.setProductName(stn.getProduct().getName());
				result.setUomId(stn.getUom().getId());
				result.setUomName(stn.getUom().getName());
				result.setSalesName(user.getName());
				result.setSalesCode(user.getCode());
				result.setTargetAmt(BigDecimal.valueOf(stn.getTargetAmount()));
				result.setTargetQty(stn.getTargetQty()+"/0");
				result.setSalesQty(stn.getBaseQty()+"/"+stn.getSubQty());
				result.setSalesAmt(BigDecimal.valueOf(stn.getSoldAmount()));
				result.setSalesCompareTargetPct(BigDecimal.valueOf(stn.getPercentCompare()));
				
				pos.add(result);
			}
		}
		
		// Include Order that don't have 
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT pd.code as ProductCode , pd.Name as ProductName ,uom.UOM_ID , uom.Name as UOM_NAME ")
			.append(", SUM(IF(pd.UOM_ID=odl.UOM_ID,odl.Qty,0)) as BaseQty ")
			.append(", SUM(IF(pd.UOM_ID<>odl.UOM_ID,odl.Qty,0)) as SecondQty " )
			.append(", SUM(IF(pd.UOM_ID=odl.UOM_ID,odl.Line_Amount,0)) as BaseLineAmt ")
			.append(", SUM(IF(pd.UOM_ID<>odl.UOM_ID,odl.Line_Amount,0)) as SecondLineAmt ")
			.append("FROM T_Order od ")
			.append("INNER JOIN T_Order_Line odl ON (od.Order_ID = odl.Order_ID) ")
			.append("INNER JOIN M_Product pd ON (pd.Product_Id = odl.Product_ID) ")
			.append("INNER JOIN M_UOM uom ON (uom.UOM_ID = pd.UOM_ID) ")
			.append("WHERE od.User_ID = ? ")
			.append("AND od.order_date >= ? ")
			.append("AND od.order_date <= ? ")
			.append("AND odl.Product_ID NOT IN ")
			.append("(SELECT stn.Product_ID FROM M_Sales_Target_New_v stn WHERE stn.User_ID = ? AND month(stn.Target_From) = ?) ");

			if(p_productCodeFrom != null && p_productCodeFrom.length() >0)
				sql.append(" AND pd.Code >= ? ");
		
			if(p_productCodeTo != null && p_productCodeTo.length() > 0)
				sql.append(" AND pd.Code <= ? ");
			
			sql.append("GROUP BY pd.code , pd.Name ,pd.UOM_ID ");
		
		PreparedStatement ppstmt = conn.prepareStatement(sql.toString());
		
		ppstmt.setInt(1, user.getId());
		ppstmt.setTimestamp(2,DateToolsUtil.convertToTimeStamp(p_dateFrom));
		ppstmt.setTimestamp(3, DateToolsUtil.convertToTimeStamp(p_dateTo));
		ppstmt.setInt(4, user.getId());
		ppstmt.setInt(5, month);
		
		int curr_param_idx = 5;
		if(p_productCodeFrom != null && p_productCodeFrom.length() >0){
			curr_param_idx++;
			ppstmt.setString(curr_param_idx, p_productCodeFrom);
		}
		
		if(p_productCodeTo != null && p_productCodeTo.length() >0){
			curr_param_idx++;
			ppstmt.setString(curr_param_idx, p_productCodeTo);
		}
		
		ResultSet rs = ppstmt.executeQuery();
		while(rs.next()){
			SalesTargetSummaryReport result = new SalesTargetSummaryReport();
			result.setDateFrom(p_dateFrom);
			result.setDateTo(p_dateTo);
			result.settDateFrom(DateToolsUtil.convertToTimeStamp(p_dateFrom));
			result.settDateTo(DateToolsUtil.convertToTimeStamp(p_dateTo));
			result.setProductCode(rs.getString("ProductCode"));
			result.setProductName(rs.getString("ProductName"));
			result.setUomId(rs.getString("UOM_ID"));
			result.setUomName(rs.getString("UOM_NAME"));
			result.setSalesName(user.getName());
			result.setSalesCode(user.getCode());
			result.setTargetAmt(BigDecimal.ZERO);
			result.setTargetQty("0");
			
			BigDecimal baseQty = rs.getBigDecimal("BaseQty");
			BigDecimal secondQty = rs.getBigDecimal("SecondQty");
			BigDecimal totalSalesAmt = rs.getBigDecimal("BaseLineAmt").add(rs.getBigDecimal("SecondLineAmt"));
			
			result.setSalesQty(baseQty.setScale(0,2).toString()+"/"+secondQty.setScale(0,2).toString());
			result.setSalesAmt(totalSalesAmt.setScale(2, BigDecimal.ROUND_HALF_UP));
			result.setSalesCompareTargetPct(BigDecimal.ZERO);
			BigDecimal.valueOf(100d);
			pos.add(result);
		}
		
		if(pos.size() >0)
			Collections.sort(pos);
		
		return pos;
	}
}
