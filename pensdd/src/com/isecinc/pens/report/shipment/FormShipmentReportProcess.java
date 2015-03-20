package com.isecinc.pens.report.shipment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jfree.util.Log;

import util.ConvertNullUtil;
import util.DateToolsUtil;

import com.isecinc.core.report.I_ReportProcess;
import com.isecinc.pens.bean.MemberTripComment;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.model.MMemberTripComment;

/**
 * Shipment Report Process
 * 
 * @author Aneak.t
 * @version $Id: ShipmentReportProcess.java,v 1.0 10/11/2010 15:52:00 aneak.t Exp $
 * 
 */
public class FormShipmentReportProcess extends I_ReportProcess<FormShipmentReport> {

	/**
	 * Search for shipment report
	 */
	public List<FormShipmentReport> doReport(FormShipmentReport t, User user, Connection conn) throws Exception {
		PreparedStatement ppstmt = null;
		ResultSet rst = null;
		List<FormShipmentReport> pos = new ArrayList<FormShipmentReport>();
		StringBuffer sql = new StringBuffer("select sh.shipment_no as documentno , sh.shipment_date as document_date\n");
		sql.append("\t\t, cm.code as customer_code , concat(cm.name,' ',cm.name2) as customer_name\n")
		   .append("\t\t, ad.line1, ad.line2, ad.line3, ad.line4 , pv.name as province_name, ad.postal_code\n")
		   .append("\t\t, oh.order_no , ol.trip_no , ol.qty plan_qty , ol.actual_qty , sl.qty ship_qty \n")
		   .append("\t\t, pd.name as product_name , pd.code as product_code , sh.shipment_status \n")
		   .append("from t_shipment sh inner join t_order oh on sh.order_id = oh.order_id\n")
		   .append("inner join m_customer cm on cm.customer_id = oh.customer_id\n")
		   .append("inner join m_address ad on cm.customer_id = ad.customer_id\n")
		   .append("inner join m_province pv on ad.province_id = pv.province_id\n")
		   .append("inner join t_shipment_line sl on sh.shipment_id = sl.shipment_id\n")
		   .append("inner join t_order_line ol on sl.order_line_id = ol.order_line_id\n")
		   .append("inner join m_product pd on sl.product_id = pd.product_id\n")
		   .append("where sh.taxinvoice_id is null and ad.purpose = 'S' \n")
		   .append("and sh.shipment_date >= '"+DateToolsUtil.convertToTimeStamp(t.getShipmentDateFrom())+"' and sh.shipment_date <= '"+DateToolsUtil.convertToTimeStamp(t.getShipmentDateTo())+"' \n")
		   .append("and cm.code = coalesce(?,cm.code) \n")
		   .append("\norder by sh.shipment_no , cm.code ");
			/***************************************
			 **  Parameters 
			 ** 1   => Customer Code
			 ** 2,3 => ShipmentNo From-To 
			 ***************************************/

		try {
			ppstmt = conn.prepareStatement(sql.toString());
			
			logger.info("Report SQL "+sql.toString());
			logger.info("Customer Code "+t.getCustomerCode());
			
			ppstmt.setString(1, StringUtils.isEmpty(t.getCustomerCode())?null:t.getCustomerCode());
			
			rst = ppstmt.executeQuery();
			
			while(rst.next()){
				FormShipmentReport shipForm = new FormShipmentReport();
				shipForm.setShipmentNo(rst.getString("documentno"));
				shipForm.setCustomerCode(rst.getString("customer_code"));
				shipForm.setCustomerName(rst.getString("customer_name"));
				
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
				
				shipForm.setCustomerAddress(address);
				shipForm.setOrderNo(rst.getString("order_no"));
				shipForm.setShipmentDate(DateToolsUtil.convertToString(rst.getTimestamp("document_date")));
				shipForm.setTripNo(rst.getInt("trip_no"));
				shipForm.setProductName(rst.getString("product_code")+" "+rst.getString("product_name"));
				shipForm.setPlanQty(rst.getBigDecimal("plan_qty"));
				shipForm.setActualQty(rst.getBigDecimal("actual_qty"));
				
				shipForm.setShipmentStatus(rst.getString("shipment_status"));
				
				pos.add(shipForm);
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
