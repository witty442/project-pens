package com.isecinc.pens.report.shipment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import util.DateToolsUtil;

import com.isecinc.core.report.I_ReportProcess;
import com.isecinc.pens.bean.User;

/**
 * Shipment Report Process
 * 
 * @author Aneak.t
 * @version $Id: ShipmentReportProcess.java,v 1.0 10/11/2010 15:52:00 aneak.t Exp $
 * 
 */
public class TaxShipmentProcess extends I_ReportProcess<TaxShipmentReport> {

	/**
	 * Search for shipment report
	 */
	public List<TaxShipmentReport> doReport(TaxShipmentReport t, User user, Connection conn) throws Exception {
		PreparedStatement ppstmt = null;
		ResultSet rst = null;
		List<TaxShipmentReport> pos = new ArrayList<TaxShipmentReport>();
		StringBuffer sql = new StringBuffer("select th.taxinvoice_no as documentno , th.taxinvoice_date as document_date\n");
		sql.append("\t\t, cm.code as customer_code , concat(cm.name,' ',cm.name2) as customer_name\n")
		   .append("\t\t, ad.line1, ad.line2, ad.line3, ad.line4 , pv.name as province_name, ad.postal_code\n")
		   .append("\t\t, oh.order_no , ol.trip_no , ol.qty plan_qty , coalesce(ol.actual_qty,0) as actual_qty , tl.qty invoice_qty \n")
		   .append("\t\t, pd.name as product_name , pd.code as product_code \n")
		   .append("\t\t, th.taxinvoice_status ,th.total_amount , th.vat_amount , th.lines_amount as sum_lines_amt , tl.lines_amount as line_amt \n")		
		   .append("from t_shipment sh inner join t_taxinvoice th on th.taxinvoice_id = sh.taxinvoice_id \n")
		   .append("inner join t_order oh on sh.order_id = oh.order_id\n")
		   .append("inner join m_customer cm on cm.customer_id = oh.customer_id\n")
		   .append("inner join m_address ad on cm.customer_id = ad.customer_id\n")
		   .append("inner join m_province pv on ad.province_id = pv.province_id\n")
		   .append("inner join t_taxinvoice_line tl on sh.taxinvoice_id = tl.taxinvoice_id\n")
		   .append("inner join t_order_line ol on tl.order_line_id = ol.order_line_id\n")
		   .append("inner join m_product pd on tl.product_id = pd.product_id\n")
		   .append("where sh.taxinvoice_id is not null and ad.purpose = 'B' \n")
		   .append("and sh.shipment_date >= '"+DateToolsUtil.convertToTimeStamp(t.getTaxinvoiceDateFrom())+"' and sh.shipment_date <= '"+DateToolsUtil.convertToTimeStamp(t.getTaxinvoiceDateTo())+"' \n")
		   .append("and cm.code = coalesce(?,cm.code) \n")
		   .append("\n order by th.taxinvoice_no , cm.code ");
			/***************************************
			 **  Parameters 
			 ** 1   => Customer Code 
			 ***************************************/

		try {
			ppstmt = conn.prepareStatement(sql.toString());
			
			logger.info("Report SQL "+sql.toString());
			logger.info("Customer Code "+t.getCustomerCode());
			
			ppstmt.setString(1, StringUtils.isEmpty(t.getCustomerCode())?null:t.getCustomerCode());
			
			rst = ppstmt.executeQuery();
			
			while(rst.next()){
				TaxShipmentReport taxShipment = new TaxShipmentReport();
				taxShipment.setTaxinvoiceNo(rst.getString("documentno"));
				taxShipment.setCustomerCode(rst.getString("customer_code"));
				taxShipment.setCustomerName(rst.getString("customer_name"));
				
				String line1 = rst.getString("line1");
				String line2 = rst.getString("line2");
				String line3 = rst.getString("line3");
				String line4 = rst.getString("line4");
				String provinceName = rst.getString("province_name");
				String postal = rst.getString("postal_code");
				
				String address = line1;
				address +=(StringUtils.isEmpty(line2)?"":" "+line2);
				address +=(StringUtils.isEmpty(line3)?"":" "+line3);
				address	+=(StringUtils.isEmpty(line4)?"":" "+line4);
				
				address += " "+provinceName ;
				address += " "+postal;
				
				taxShipment.setCustomerAddress(address);
				taxShipment.setOrderNo(rst.getString("order_no"));
				taxShipment.setTaxinvoiceDate(DateToolsUtil.convertToString(rst.getTimestamp("document_date")));
				taxShipment.setTripNo(rst.getInt("trip_no"));
				
				taxShipment.setProductName(rst.getString("product_code")+" "+rst.getString("product_name"));
				taxShipment.setPlanQty(rst.getBigDecimal("plan_qty"));
				taxShipment.setActualQty(rst.getBigDecimal("actual_qty"));
				
				taxShipment.setStatus(rst.getString("taxinvoice_status"));
				
				taxShipment.setLineAmt(rst.getBigDecimal("line_amt"));
				taxShipment.setSumLinesAmt(rst.getBigDecimal("sum_lines_amt"));
				taxShipment.setVatAmt(rst.getBigDecimal("vat_amount"));
				taxShipment.setSumTotalAmt(rst.getBigDecimal("total_amount"));
				
				pos.add(taxShipment);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ppstmt.close();
			} catch (Exception e) {}
		}

		return pos;
	}

}
