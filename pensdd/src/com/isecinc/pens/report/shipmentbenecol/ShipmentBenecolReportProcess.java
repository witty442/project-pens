package com.isecinc.pens.report.shipmentbenecol;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import util.ConvertNullUtil;
import util.DateToolsUtil;

import com.isecinc.core.report.I_ReportProcess;
import com.isecinc.pens.bean.Address;
import com.isecinc.pens.bean.Contact;
import com.isecinc.pens.bean.MemberTripComment;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.model.MAddress;
import com.isecinc.pens.model.MContact;
import com.isecinc.pens.model.MMemberTripComment;

/**
 * Shipment Benecol Report Process
 * 
 * @author Aneak.t
 * @version $Id: ShipmentBenecolReportProcess.java,v 1.0 10/11/2010 15:52:00 aneak.t Exp $
 * 
 */

public class ShipmentBenecolReportProcess extends I_ReportProcess<ShipmentBenecolReport> {

	/**
	 * Search for report
	 */
	public List<ShipmentBenecolReport> doReport(ShipmentBenecolReport t, User user, Connection conn) throws Exception {
		List<ShipmentBenecolReport> pos = new ArrayList<ShipmentBenecolReport>();
		ShipmentBenecolReport shipmentBenecol = null;
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();

		try {
			// sql.delete(0, sql.length());
			// sql.append(" SELECT line.SHIPPING_DATE, us.NAME AS SHIP_USER, cus.CODE AS CUSTOMER_CODE, ");
			// sql.append(" cus.NAME AS CUSTOMER_NAME, line.QTY, ");
			// sql.append(" CASE line.PAYMENT WHEN 'N' THEN line.LINE_AMOUNT ELSE 0 END AS LINE_AMOUNT, ");
			// sql.append(" '' AS RECEIVE_USER, '' AS BANK_ACCOUNT, ");
			// sql.append(" CONCAT(ad.LINE1,' ',ad.LINE2,' ',ad.LINE3,' ',ad.LINE4,' ',pv.NAME,' ', ");
			// sql.append(" ct.MOBILE,',',ct.MOBILE2,',',ct.PHONE,',',ct.PHONE2) AS ADDRESS ");
			// sql.append(" FROM t_order od ");
			// sql.append(" INNER JOIN t_order_line line ON line.ORDER_ID = od.ORDER_ID ");
			// sql.append(" INNER JOIN m_customer cus ON od.CUSTOMER_ID = cus.CUSTOMER_ID ");
			// sql.append(" INNER JOIN ad_user us ON cus.USER_ID = us.USER_ID ");
			// sql.append(" INNER JOIN m_address ad ON ad.CUSTOMER_ID = cus.CUSTOMER_ID ");
			// sql.append(" LEFT JOIN m_province pv ON ad.PROVINCE_ID = pv.PROVINCE_ID ");
			// sql.append(" LEFT JOIN m_contact ct ON ct.CUSTOMER_ID = cus.CUSTOMER_ID ");
			// sql.append(" WHERE cus.CUSTOMER_TYPE = '" + user.getCustomerType().getKey() + "' ");
			// sql.append(" AND od.DOC_STATUS = 'SV' ");
			// sql.append(" AND od.ORDER_TYPE = '" + user.getOrderType().getKey() + "' ");
			// sql.append(" AND line.SHIPPING_DATE = '" + DateToolsUtil.convertToTimeStamp(t.getShippingDate()) + "' ");
			// sql.append(" AND ad.LINE1 NOT LIKE '%108%' ");
			// sql.append(" AND ad.LINE1 NOT LIKE '%SHOP%' ");
			// sql.append(" GROUP BY cus.CUSTOMER_ID ");
			// sql.append(" ORDER BY line.SHIPPING_DATE ");

			sql.delete(0, sql.length());
			sql.append("SELECT cus.CUSTOMER_ID, line.SHIPPING_DATE, us.NAME AS SHIP_USER, \r\n ");
			sql.append("cus.CODE AS CUSTOMER_CODE,  cus.NAME AS CUSTOMER_NAME, \r\n ");
			sql.append("sum(line.QTY) as QTY,  CASE line.PAYMENT WHEN 'N' ");
			sql.append("THEN sum(coalesce(IF(line.payment_method='CS',line.need_bill,0),0)) ELSE 0 END AS LINE_AMOUNT, \r\n ");
			sql.append("'' AS RECEIVE_USER, '' AS BANK_ACCOUNT \r\n ");
			sql.append(",od.ORDER_ID,line.TRIP_NO, od.REMARK \r\n");
			sql.append("FROM t_order od \r\n ");
			sql.append("INNER JOIN t_order_line line ON line.ORDER_ID = od.ORDER_ID \r\n ");
			sql.append("INNER JOIN m_customer cus ON od.CUSTOMER_ID = cus.CUSTOMER_ID \r\n ");
			sql.append("INNER JOIN ad_user us ON cus.USER_ID = us.USER_ID \r\n ");
			sql.append("WHERE 1=1 \r\n ");
			sql.append("  AND od.DOC_STATUS = 'SV' \r\n ");
			sql.append("  AND od.ORDER_TYPE = 'DD' \r\n ");
			sql.append("  AND line.ISCANCEL = 'N' \r\n");
			sql.append("  AND line.SHIPPING_DATE = '" + DateToolsUtil.convertToTimeStamp(t.getShippingDate())
					+ "' \r\n ");
			sql.append("  AND cus.CUSTOMER_ID IN ( \r\n ");
			sql.append("	SELECT cu.CUSTOMER_ID FROM m_customer cu \r\n ");
			sql.append("	WHERE cu.CUSTOMER_ID in ( \r\n ");
			sql.append("		select distinct addr.CUSTOMER_ID \r\n ");
			sql.append("		from m_address addr \r\n ");
			sql.append("		where 1=1 \r\n ");
			sql.append("		  AND addr.LINE1 NOT LIKE '%108%SHOP%' \r\n ");
			sql.append("	  	  AND addr.PURPOSE = 'S' \r\n ");
			sql.append("		) \r\n ");
			sql.append("	  and cu.customer_type = 'DD' \r\n ");
			sql.append("  ) \r\n ");
			sql.append("GROUP BY order_id, cus.CUSTOMER_ID \r\n ");
			sql.append("ORDER BY line.SHIPPING_DATE,cast(cus.CODE as unsigned) \r\n ");

			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			int i = 1;
			int memberId;
			MAddress mAddress = new MAddress();
			MContact mContact = new MContact();
			Address[] addrs;
			Contact[] conts;
			String addressLabel = "";
			String contactLabel = "";
			String whereCause = "";

			List<MemberTripComment> tripComments = null;

			MMemberTripComment mTripComment = new MMemberTripComment();
			String comment = "";
			while (rst.next()) {
				memberId = rst.getInt("CUSTOMER_ID");
				whereCause = " and customer_id = " + memberId;
				whereCause += " and purpose = 'S' ";
				addrs = mAddress.search(whereCause);
				if (addrs != null) {
					for (Address a : addrs) {
						addressLabel = a.getLineString();
						break;
					}
				} else {
					whereCause = " and customer_id = " + memberId;
					whereCause += " and purpose = 'B' ";
					addrs = mAddress.search(whereCause);
					if (addrs != null) {
						for (Address a : addrs) {
							addressLabel = a.getLineString();
							break;
						}
					}
				}
				whereCause = " and customer_id = " + memberId;
				whereCause += "  and isactive = 'Y' ";
				conts = mContact.search(whereCause);
				if (conts != null) {
					contactLabel = conts[0].getMobile() + ",";
					contactLabel += conts[0].getMobile2() + ",";
					contactLabel += conts[0].getPhone() + ",";
					contactLabel += conts[0].getPhone2();
				}

				shipmentBenecol = new ShipmentBenecolReport();
				shipmentBenecol.setId(i++);
				shipmentBenecol.setShippingDate(rst.getString("SHIPPING_DATE"));
				shipmentBenecol.setShippingUser(rst.getString("SHIP_USER"));
				shipmentBenecol.setCustomerCode(rst.getString("CUSTOMER_CODE"));
				shipmentBenecol.setCustomerName(rst.getString("CUSTOMER_NAME"));
				shipmentBenecol.setQty(rst.getInt("QTY"));
				shipmentBenecol.setLineAmount(rst.getDouble("LINE_AMOUNT"));
				shipmentBenecol.setReceiveUser(rst.getString("RECEIVE_USER"));
				shipmentBenecol.setBankAccount(rst.getString("BANK_ACCOUNT"));

				// shipmentBenecol.setAddress(rst.getString("ADDRESS"));
				shipmentBenecol.setAddress(addressLabel + " " + contactLabel);

				comment = "";
				comment = ConvertNullUtil.convertToString(rst.getString("REMARK")).trim();

				tripComments = mTripComment.lookUp(rst.getInt("ORDER_ID"), rst.getInt("TRIP_NO"));

				if (tripComments.size() > 0) {
					comment += "\r\n" + ConvertNullUtil.convertToString(tripComments.get(0).getTripComment()).trim();
				}
				shipmentBenecol.setTripComment(comment);

				shipmentBenecol.setTripNo(rst.getInt("TRIP_NO"));

				pos.add(shipmentBenecol);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				if(rst != null){
				  rst.close(); rst =null;
				}
				if(stmt != null){
				  stmt.close();stmt = null;
				}
			} catch (Exception e2) {}
		}

		return pos;
	}

}
