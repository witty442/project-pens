package com.isecinc.pens.report.shipment;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
public class ShipmentReportProcess extends I_ReportProcess<ShipmentReport> {

	/**
	 * Search for shipment report
	 */
	public List<ShipmentReport> doReport(ShipmentReport t, User user, Connection conn) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		List<ShipmentReport> pos = new ArrayList<ShipmentReport>();
		ShipmentReport shipment = null;
		StringBuilder sql = new StringBuilder();

		try {

			sql.delete(0, sql.length());
			sql.append(" SELECT DELIVERY_GROUP,SHIPPING_DATE,CODE, Customer_name as FullName, ");
			sql.append(" SUM(ORANGE_QTY) as ORANGE_QTY, ");
			sql.append(" SUM(BERRY_QTY) as BERRY_QTY, ");
			sql.append(" SUM(MIX_QTY) as MIX_QTY, ");
			sql.append(" LINE_NO, TOTAL_LINE  , REMARK, ORDER_ID,  sum(amt  ) as amt ");
			sql.append(" FROM( ");
			sql.append(" SELECT odline.SHIPPING_DATE, cus.CODE, cus.NAME AS FullName, od.customer_name, ");
			sql.append(" CASE WHEN pro.NAME LIKE 'BCSO%' THEN odline.QTY ELSE 0 END AS ORANGE_QTY, ");
			sql.append(" CASE WHEN pro.NAME LIKE 'BCSS%' THEN odline.QTY ELSE 0 END AS BERRY_QTY, ");
			sql.append(" CASE WHEN pro.NAME LIKE 'BCSB%' THEN odline.QTY ELSE 0 END AS MIX_QTY, ");
			sql.append(" odline.trip_no as LINE_NO,(SELECT MAX(odline.trip_no) ");
			sql.append(" FROM t_order od ");
			sql.append(" INNER JOIN t_order_line odline ON odline.ORDER_ID = od.ORDER_ID ");
			sql.append(" LEFT JOIN m_customer cus ON od.CUSTOMER_ID = cus.CUSTOMER_ID ");
			sql.append(" LEFT JOIN m_product pro ON odline.PRODUCT_ID = pro.PRODUCT_ID ");
			sql.append(" WHERE cus.CUSTOMER_TYPE = '" + user.getCustomerType().getKey() + "' ");
			sql.append(" AND cus.ISACTIVE = 'Y' ");
			sql.append("  AND odline.ISCANCEL = 'N' ");
			sql.append(" AND od.DOC_STATUS = 'SV' ");
			sql.append(" AND od.ORDER_TYPE = '" + user.getOrderType().getKey() + "' ");
			sql.append(" AND odline.SHIPPING_DATE = '" + DateToolsUtil.convertToTimeStamp(t.getShipmentDate()) + "' ");
			sql.append(" ORDER BY odline.LINE_NO DESC ) AS TOTAL_LINE ");
			sql.append("  ,cus.DELIVERY_GROUP, od.REMARK, od.ORDER_ID, odline.total_amount as amt ");
			sql.append(" FROM t_order od ");
			sql.append(" INNER JOIN t_order_line odline ON odline.ORDER_ID = od.ORDER_ID ");
			sql.append(" LEFT JOIN m_customer cus ON od.CUSTOMER_ID = cus.CUSTOMER_ID ");
			sql.append(" LEFT JOIN m_product pro ON odline.PRODUCT_ID = pro.PRODUCT_ID ");
			sql.append(" WHERE cus.CUSTOMER_TYPE = '" + user.getCustomerType().getKey() + "' ");
			sql.append(" AND cus.ISACTIVE = 'Y' ");
			sql.append("  AND odline.ISCANCEL = 'N' ");
			sql.append(" AND od.DOC_STATUS = 'SV' ");
			sql.append(" AND od.ORDER_TYPE = '" + user.getOrderType().getKey() + "' ");
			sql.append(" AND odline.SHIPPING_DATE = '" + DateToolsUtil.convertToTimeStamp(t.getShipmentDate()) + "' ");
			sql.append(") A ");
			sql.append(" GROUP BY CODE, FullName, SHIPPING_DATE, LINE_NO,TOTAL_LINE ");
			sql.append("  ,DELIVERY_GROUP, REMARK, ORDER_ID ");
			sql.append(" ORDER BY DELIVERY_GROUP, cast(CODE as unsigned), LINE_NO ");

			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			int i = 1;
			String mCode = "";
			// String lineNo = "";
			String combineCode = "";

			List<MemberTripComment> tripComments = null;
			MMemberTripComment mTripComment = new MMemberTripComment();
			String comment = "";

			while (rst.next()) {
				// if (mCode.equals(rst.getString("CODE")) && lineNo.equals(rst.getString("LINE_NO"))) {
				if (combineCode.equalsIgnoreCase(rst.getString("CODE") + "-" + rst.getString("ORDER_ID"))) {
					if (rst.getInt("ORANGE_QTY") != 0) {
						pos.get(pos.size() - 1).setOrangeQty(
								pos.get(pos.size() - 1).getOrangeQty() + rst.getInt("ORANGE_QTY"));
					}
					if (rst.getInt("BERRY_QTY") != 0) {
						pos.get(pos.size() - 1).setBerryQty(
								pos.get(pos.size() - 1).getBerryQty() + rst.getInt("BERRY_QTY"));
					}
					if (rst.getInt("MIX_QTY") != 0) {
						pos.get(pos.size() - 1).setMixQty(pos.get(pos.size() - 1).getMixQty() + rst.getInt("MIX_QTY"));
					}
					pos.get(pos.size() - 1).setAmt(pos.get(pos.size() - 1).getAmt() + rst.getDouble("amt"));
				} else {
					if (pos.size() > 0) {
						pos.get(pos.size() - 1).setRemark(
								ConvertNullUtil.convertToString(pos.get(pos.size() - 1).getRemark()) + " " + comment);
					}

					shipment = new ShipmentReport();
					shipment.setId(i++);
					shipment.setShipmentDate(rst.getString("SHIPPING_DATE"));
					shipment.setMemberCode(rst.getString("CODE"));

					mCode = shipment.getMemberCode().trim();
					shipment.setMemberName(rst.getString("FullName"));
					shipment.setLineNo(rst.getString("LINE_NO"));
					// lineNo = shipment.getLineNo().trim();

					combineCode = mCode + "-" + rst.getInt("ORDER_ID");

					shipment.setTotalLine(rst.getString("TOTAL_LINE"));
					shipment.setOrangeQty(rst.getInt("ORANGE_QTY"));
					shipment.setBerryQty(rst.getInt("BERRY_QTY"));
					shipment.setMixQty(rst.getInt("MIX_QTY"));
					shipment.setDeliveryGroup(ConvertNullUtil.convertToString(rst.getString("DELIVERY_GROUP")).trim());
					// shipment.setRemark(rst.getString(""));

					comment = "";
					comment = ConvertNullUtil.convertToString(rst.getString("REMARK")).trim();

					tripComments = mTripComment.lookUp(rst.getInt("ORDER_ID"), rst.getInt("LINE_NO"));

					if (tripComments.size() > 0) {
						comment += " " + ConvertNullUtil.convertToString(tripComments.get(0).getTripComment()).trim();
					}

					shipment.setAmt(rst.getDouble("amt"));
					pos.add(shipment);
				}
			}
			// last comment
			if (pos.size() > 0) {
				pos.get(pos.size() - 1).setRemark(comment);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}

		return pos;
	}

}
