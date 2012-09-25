package com.isecinc.pens.report.shipmenttemp;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import util.DateToolsUtil;

import com.isecinc.core.report.I_ReportProcess;
import com.isecinc.pens.bean.User;

/**
 * Shipment Temporary Report Process
 * 
 * @author Aneak.t
 * @version $Id: ShipmentTempReportProcess.java,v 1.0 10/11/2010 15:52:00 aneak.t Exp $
 * 
 */

public class ShipmentTempReportProcess extends I_ReportProcess<ShipmentTempReport> {

	/**
	 * Search for shipment temporary report.
	 */
	public List<ShipmentTempReport> doReport(ShipmentTempReport t, User user, Connection conn) throws Exception {
		List<ShipmentTempReport> pos = new ArrayList<ShipmentTempReport>();
		ShipmentTempReport shipment = null;
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();

		try {
			sql.delete(0, sql.length());
			sql.append(" SELECT od.ORDER_NO, CONCAT(ad.LINE1,' ',ad.LINE2,' ',ad.LINE3,' ',ad.LINE4,' ', ");
			sql.append(" dt.NAME,' ',pv.NAME,' ',ad.POSTAL_CODE) AS SHIP_ADDRESS, ");
			sql.append(" odline.LINE_NO, cus.CODE, cus.NAME AS FULLNAME, ");
			sql.append(" ct.PHONE, ct.PHONE2, ct.MOBILE, ct.MOBILE2, ");
			sql.append(" CASE WHEN pro.NAME LIKE 'BCSO%' THEN mpr.ORDER_QTY ELSE 0 END AS ORANGE_QTY, ");
			sql.append(" CASE WHEN pro.NAME LIKE 'BCSS%' THEN mpr.ORDER_QTY ELSE 0 END AS BERRY_QTY, ");
			sql.append(" CASE WHEN pro.NAME LIKE 'BCSB%' THEN mpr.ORDER_QTY ELSE 0 END AS MIX_QTY ");
			sql.append(" FROM t_order od ");
			sql.append(" INNER JOIN t_order_line odline ON odline.ORDER_ID = od.ORDER_ID ");
			sql.append(" INNER JOIN m_customer cus ON od.CUSTOMER_ID = cus.CUSTOMER_ID ");
			sql.append(" LEFT JOIN m_member_product mpr ON mpr.CUSTOMER_ID = cus.CUSTOMER_ID ");
			sql.append(" LEFT JOIN m_product pro ON mpr.PRODUCT_ID = pro.PRODUCT_ID ");
			sql.append(" LEFT JOIN m_address ad ON ad.ADDRESS_ID = od.SHIP_ADDRESS_ID ");
			sql.append(" LEFT JOIN m_district dt ON ad.DISTRICT_ID = dt.DISTRICT_ID ");
			sql.append(" LEFT JOIN m_province pv ON ad.PROVINCE_ID = pv.PROVINCE_ID ");
			sql.append(" LEFT JOIN m_contact ct ON ct.CUSTOMER_ID = cus.CUSTOMER_ID ");
			sql.append(" WHERE cus.CUSTOMER_TYPE = '" + user.getCustomerType().getKey() + "' ");
			sql.append(" AND cus.ISACTIVE = 'Y' ");
			sql.append("  AND odline.ISCANCEL = 'N' ");
			// sql.append(" AND od.INTERFACES = 'Y' ");
			sql.append(" AND od.DOC_STATUS = 'SV' ");
			sql.append(" AND od.ORDER_TYPE = '" + user.getOrderType().getKey() + "' ");
			// sql.append(" AND od.USER_ID = '" + user.getId() + "'");
			sql.append(" AND odline.SHIPPING_DATE = '" + DateToolsUtil.convertToTimeStamp(t.getShipmentDate()) + "' ");
			sql.append(" AND ad.LINE1 LIKE '%108%' ");
			sql.append(" AND ad.LINE1 LIKE '%SHOP%' ");
			sql.append(" GROUP BY od.ORDER_NO, ORANGE_QTY, BERRY_QTY, MIX_QTY ");
			sql.append(" ORDER BY odline.LINE_NO, od.CUSTOMER_ID ");

			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			String mCode = "";
			while (rst.next()) {
				if (mCode.equals(rst.getString("CODE"))) {
					if (rst.getInt("ORANGE_QTY") != 0) {
						pos.get(pos.size() - 1).setOrangeQty(rst.getInt("ORANGE_QTY"));
					}
					if (rst.getInt("BERRY_QTY") != 0) {
						pos.get(pos.size() - 1).setBerryQty(rst.getInt("BERRY_QTY"));
					}
					if (rst.getInt("MIX_QTY") != 0) {
						pos.get(pos.size() - 1).setMixQty(rst.getInt("MIX_QTY"));
					}
				} else {
					shipment = new ShipmentTempReport();

					shipment.setOrderNo(rst.getString("ORDER_NO"));
					shipment.setShipAddress(rst.getString("SHIP_ADDRESS"));
					shipment.setLineNo(rst.getString("LINE_NO"));
					shipment.setMemberCode(rst.getString("CODE"));
					mCode = shipment.getMemberCode();
					shipment.setMemberName(rst.getString("FULLNAME"));
					shipment.setPhone(rst.getString("PHONE"));
					shipment.setPhone2(rst.getString("PHONE2"));
					shipment.setMobile(rst.getString("MOBILE"));
					shipment.setMobile2(rst.getString("MOBILE2"));
					shipment.setOrangeQty(rst.getInt("ORANGE_QTY"));
					shipment.setBerryQty(rst.getInt("BERRY_QTY"));
					shipment.setMixQty(rst.getInt("MIX_QTY"));

					pos.add(shipment);
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e2) {}
		}

		return pos;
	}

}
