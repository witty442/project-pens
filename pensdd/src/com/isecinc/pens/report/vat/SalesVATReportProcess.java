package com.isecinc.pens.report.vat;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jfree.util.Log;

import util.DateToolsUtil;

import com.isecinc.core.bean.References;
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

public class SalesVATReportProcess extends I_ReportProcess<SalesVATReport> {
	/**
	 * Search for performance report.
	 */
	public List<SalesVATReport> doReport(SalesVATReport report, User user, Connection conn) throws Exception {
		SalesVATReport[] stns = null;
		PreparedStatement ppstmt = null;
		ResultSet rset = null;
		int i =0;
		List<SalesVATReport> pos = new ArrayList<SalesVATReport>();
		try{
			StringBuffer sql= new StringBuffer("select th.taxinvoice_date , th.taxinvoice_no , concat(cm.name,' ',cm.name2) as customer_name,th.taxinvoice_status \n");
			sql.append("\t\t,th.lines_amount ,th.vat_amount , th.total_amount, month(th.taxinvoice_date) as month , year(th.taxinvoice_date) as year \n")
				.append("from t_taxinvoice th inner join t_order oh on th.order_id = oh.order_id \n")
				.append("inner join m_customer cm on cm.customer_id = oh.customer_id\n")
				.append("where date_format(th.taxinvoice_date,'%Y%m') = ? \n")
				.append("and th.taxinvoice_status = COALESCE(?,th.taxinvoice_status) \n")
				.append("order by th.taxinvoice_date,th.taxinvoice_no ");
			
			logger.info(sql.toString());
			logger.info("Parameter Period : "+report.getPeriod());
			logger.info("Parameter Is Show Cancel : "+report.getIsShowCancel());
			ppstmt = conn.prepareStatement(sql.toString());
			ppstmt.setString(1, report.getPeriod());
			if("Y".equals(report.getIsShowCancel()))
				ppstmt.setString(2, null);
			else
				ppstmt.setString(2, "SV");
			
			rset = ppstmt.executeQuery();
			while(rset.next()){
				SalesVATReport salesVAT = new SalesVATReport();
				salesVAT.setTaxinvoiceDate(DateToolsUtil.convertToString(rset.getTimestamp("taxinvoice_date")));
				salesVAT.setTaxinvoiceNo(rset.getString("taxinvoice_no"));
				salesVAT.setCustomerName(rset.getString("customer_name"));
				salesVAT.setTaxbaseAmt(rset.getBigDecimal("lines_amount"));
				salesVAT.setVatAmt(rset.getBigDecimal("vat_amount"));
				salesVAT.setTotalAmt(rset.getBigDecimal("total_amount"));
				salesVAT.setTaxinvoiceStatus(rset.getString("taxinvoice_status"));
				
				String month = DateToolsUtil.getMonthOfNum(rset.getInt("month")-1);
				int year = rset.getInt("year")+543;
				salesVAT.setParamDisplay("เดือน : "+month+" ปี : "+year);
				
				pos.add(salesVAT);
			}
		}finally{
			if(ppstmt != null){
				ppstmt.close();ppstmt=null;
			}
			if(rset != null){
				rset.close();rset=null;
			}
		}
		return pos;
	}
}
