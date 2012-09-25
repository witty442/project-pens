package com.isecinc.pens.report.printsticker;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import util.DateToolsUtil;

/**
 * PrintSticker Process
 * 
 * @author Aneak.t
 * @version $Id: PrintStickerProcess.java,v 1.0 19/01/2011 15:52:00 aneak.t Exp $
 * 
 */

public class PrintStickerProcess {
	
	public List<PrintSticker> doReport(Connection conn, String requestDate) throws Exception{
		List<PrintSticker> lstData = new ArrayList<PrintSticker>();
		PrintSticker sticker = null;
		StringBuilder sql = new StringBuilder();
		ResultSet rs = null;
		Statement stmt = null;
	
		try {
			sql.delete(0, sql.length());
			sql.append(" SELECT l.REQUEST_DATE,l.TRIP_NO, CONCAT(c.NAME,' ',c.NAME2) AS NAME, ");
			sql.append(" CASE t.MOBILE WHEN '' THEN t.PHONE ELSE t.MOBILE END AS TELEPHONE, ");
			sql.append(" c.DELIVERY_GROUP, c.CODE, ");
			sql.append(" SUM(CASE WHEN p.NAME LIKE 'BCSO%' THEN l.QTY ELSE 0 END) AS ORANGE_QTY, ");
			sql.append(" SUM(CASE WHEN p.NAME LIKE 'BCSS%' THEN l.QTY ELSE 0 END) AS BERRY_QTY, ");
			sql.append(" SUM(CASE WHEN p.NAME LIKE 'BCSB%' THEN l.QTY ELSE 0 END) AS MIX_QTY ");
			sql.append(" FROM t_order_line l ");
			sql.append(" INNER JOIN t_order o ON l.ORDER_ID = o.ORDER_ID ");
			sql.append(" INNER JOIN m_customer c ON o.CUSTOMER_ID = c.CUSTOMER_ID ");
			sql.append(" INNER JOIN m_contact t ON t.CUSTOMER_ID = c.CUSTOMER_ID ");
			sql.append(" LEFT JOIN m_product p ON l.PRODUCT_ID = p.PRODUCT_ID ");
			sql.append(" WHERE c.CUSTOMER_TYPE = 'DD' ");
			sql.append(" AND l.REQUEST_DATE = '" + DateToolsUtil.convertToTimeStamp(requestDate) + "' ");
			sql.append(" GROUP BY NAME ");
			sql.append(" ORDER BY l.REQUEST_DATE, l.TRIP_NO, c.DELIVERY_GROUP  ");
			
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql.toString());
			int index = 0;
			
			while(rs.next()){
				if(index % 2 == 0){
					sticker = new PrintSticker();
					sticker.setHb1(rs.getString("CODE"));
					sticker.setRequestDate(rs.getString("REQUEST_DATE"));
					sticker.setTripNo(rs.getString("TRIP_NO"));
					sticker.setName(rs.getString("NAME"));
					sticker.setTelephone(rs.getString("TELEPHONE"));
					sticker.setDeliveryGroup(rs.getString("DELIVERY_GROUP"));
					sticker.setOrangeQty(rs.getDouble("ORANGE_QTY"));
					sticker.setBerryQty(rs.getDouble("BERRY_QTY"));
					sticker.setMixQty(rs.getDouble("MIX_QTY"));
					lstData.add(sticker);
				}else{
					sticker = lstData.get(lstData.size()-1);
					sticker.setHb2(rs.getString("CODE"));
					sticker.setRequestDate2(rs.getString("REQUEST_DATE"));
					sticker.setTripNo2(rs.getString("TRIP_NO"));
					sticker.setName2(rs.getString("NAME"));
					sticker.setTelephone2(rs.getString("TELEPHONE"));
					sticker.setDeliveryGroup2(rs.getString("DELIVERY_GROUP"));
					sticker.setOrangeQty2(rs.getDouble("ORANGE_QTY"));
					sticker.setBerryQty2(rs.getDouble("BERRY_QTY"));
					sticker.setMixQty2(rs.getDouble("MIX_QTY"));
					lstData.set(lstData.size()-1, sticker);
				}
				index++;
			}
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
}
