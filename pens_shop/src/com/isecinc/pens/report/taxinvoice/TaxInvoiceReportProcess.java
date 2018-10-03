package com.isecinc.pens.report.taxinvoice;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import util.DateToolsUtil;

import com.isecinc.core.report.I_ReportProcess;
import com.isecinc.pens.bean.User;

/**
 * Tax Invoice Report Process
 * 
 * @author Aneak.t
 * @version $Id: TaxInvoiceReportProcess.java,v 1.0 01/12/2010 15:52:00 aneak.t Exp $
 * 
 */

public class TaxInvoiceReportProcess extends I_ReportProcess<TaxInvoiceReport> {

	/**
	 * Search for report
	 */
	public List<TaxInvoiceReport> doReport(TaxInvoiceReport t, User user, Connection conn) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		List<TaxInvoiceReport> pos = new ArrayList<TaxInvoiceReport>();
		TaxInvoiceReport taxInvoice = null;
		StringBuilder sql = new StringBuilder();

		try {
			sql.delete(0, sql.length());
			sql.append(" SELECT T.ADDRESS, T.ORDER_DATE, T.TAX_NO, T.CODE, T.NAME, T.UOM_ID, ");
			sql.append(" T.CUSTOMER_CODE, T.CUSTOMER_NAME, T.PRODUCT_CODE, T.PRODUCT_NAME, ");
			sql.append(" SUM(CASE WHEN T.SALE_PRICE <> 0 THEN T.MAIN_QTY ELSE 0 END) AS MAIN_QTY, ");
			sql.append(" SUM(CASE WHEN T.SALE_PRICE <> 0 THEN T.SUB_QTY ELSE 0 END) AS SUB_QTY, ");
			sql.append(" SUM(CASE T.SALE_PRICE WHEN 0 THEN T.ADD_QTY ELSE 0 END) AS ADD_QTY, ");
			sql.append(" SUM(T.SALE_PRICE) AS SALE_PRICE, ");
			sql.append(" (SUM(T.DISCOUNT)*100)/SUM(T.TOTAL_AMOUNT) AS PERCENT_DISCOUNT, ");
			sql.append(" SUM(T.LINE_AMOUNT) AS LINE_AMOUNT, SUM(T.VAT_AMOUNT) AS VAT_AMOUNT, ");
			sql.append(" SUM(CASE WHEN T.SALE_PRICE <> 0 THEN (T.LINE_AMOUNT) ELSE 0 END) AS TOTAL_AMOUNT, ");
			sql.append(" SUM(T.DISCOUNT) AS DISCOUNT, ");
			sql.append(" SUM(CASE WHEN T.SALE_PRICE <> 0 THEN T.TOTAL_AMOUNT ELSE 0 END) AS NET_AMOUNT ");
			sql.append(" FROM(SELECT CONCAT(ad.LINE1,' ', ad.LINE2,' ', ad.LINE3,' ', ad.LINE4,' ', ");
			sql.append(" ad.PROVINCE_NAME,' ',ad.POSTAL_CODE) AS ADDRESS,  od.ORDER_DATE, cus.TAX_NO, ");
			sql.append(" us.CODE, us.NAME, cus.CODE AS CUSTOMER_CODE,  cus.NAME AS CUSTOMER_NAME , ");
			sql.append(" pd.CODE AS PRODUCT_CODE, pd.NAME AS PRODUCT_NAME, ");
			sql.append(" CASE line.UOM_ID WHEN (SELECT DISTINCT(m_product.UOM_ID) FROM m_product ");
			sql.append(" WHERE m_product.PRODUCT_ID = line.PRODUCT_ID) THEN line.QTY ELSE 0 END AS MAIN_QTY, ");
			sql.append(" CASE WHEN line.UOM_ID <> (SELECT DISTINCT(m_product.UOM_ID) FROM m_product ");
			sql.append(" WHERE m_product.PRODUCT_ID = line.PRODUCT_ID) ");
			sql.append(" AND line.PRICE <> 0 THEN line.QTY ELSE 0 END AS SUB_QTY, ");
			sql.append(" CASE line.PRICE WHEN 0 THEN line.QTY ELSE 0 END AS ADD_QTY, ");
			sql.append(" (line.PRICE*line.QTY) AS SALE_PRICE,  line.DISCOUNT, ");
			sql.append(" (line.LINE_AMOUNT - line.DISCOUNT) AS LINE_AMOUNT, ");
			sql.append(" line.TOTAL_AMOUNT, line.VAT_AMOUNT, line.UOM_ID, ");
			sql.append(" CASE (line.PRICE*line.QTY) WHEN 0 THEN 'Y' ELSE 'N' END AS IS_ADD ");
			sql.append(" FROM t_order od ");
			sql.append(" INNER JOIN m_customer cus ON od.CUSTOMER_ID = cus.CUSTOMER_ID ");
			sql.append(" INNER JOIN m_address ad ON od.BILL_ADDRESS_ID = ad.ADDRESS_ID ");
			sql.append(" INNER JOIN ad_user us ON od.USER_ID = us.USER_ID ");
			sql.append(" LEFT JOIN t_order_line line ON line.ORDER_ID = od.ORDER_ID ");
			sql.append(" LEFT JOIN m_product pd ON line.PRODUCT_ID = pd.PRODUCT_ID ");
			// sql.append(" WHERE cus.CUSTOMER_TYPE = 'CV' ");
			sql.append(" WHERE 1 = 1 ");
			sql.append(" AND od.ORDER_ID = " + t.getOrderID());
			sql.append(" AND od.USER_ID = " + user.getId());
			sql.append(" AND od.ORDER_TYPE = '" + user.getOrderType().getKey() + "' ");
			sql.append(" ORDER BY line.LINE_NO) T ");
			sql.append(" GROUP BY T.PRODUCT_CODE, T.IS_ADD ");
			sql.append(" ORDER BY T.PRODUCT_CODE ");

			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			int i = 1;
			while (rst.next()) {
				taxInvoice = new TaxInvoiceReport();
				taxInvoice.setId(i++);
				taxInvoice.setAddress(rst.getString("ADDRESS"));
				taxInvoice.setOrderDate(DateToolsUtil.convertToString(rst.getDate("ORDER_DATE")));
				taxInvoice.setTaxNo(rst.getString("TAX_NO"));
				taxInvoice.setCode(rst.getString("CODE"));
				taxInvoice.setName(rst.getString("NAME"));
				taxInvoice.setCustomerCode(rst.getString("CUSTOMER_CODE"));
				taxInvoice.setCustomerName(rst.getString("CUSTOMER_NAME"));
				taxInvoice.setProductCode(rst.getString("PRODUCT_CODE"));
				taxInvoice.setProductName(rst.getString("PRODUCT_NAME"));
				taxInvoice.setUomId(rst.getString("UOM_ID"));
				taxInvoice.setMainQty(rst.getInt("MAIN_QTY"));
				taxInvoice.setSubQty(rst.getInt("SUB_QTY"));
				taxInvoice.setAddQty(rst.getInt("ADD_QTY"));
				taxInvoice.setSalePrice(rst.getDouble("SALE_PRICE"));
				taxInvoice.setPercentDiscount(rst.getDouble("PERCENT_DISCOUNT"));
				taxInvoice.setDiscount(rst.getDouble("DISCOUNT"));
				taxInvoice.setLineAmount(rst.getDouble("LINE_AMOUNT"));
				taxInvoice.setTotalAmount(rst.getDouble("TOTAL_AMOUNT"));
				taxInvoice.setVatAmount(rst.getDouble("VAT_AMOUNT"));
				taxInvoice.setNetAmount(rst.getDouble("NET_AMOUNT"));

				pos.add(taxInvoice);
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
