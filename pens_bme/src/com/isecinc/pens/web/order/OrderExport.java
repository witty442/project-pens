package com.isecinc.pens.web.order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import org.apache.log4j.Logger;

import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;

import util.Constants;
import util.ExcelHeader;

public class OrderExport {
	
private static Logger logger = Logger.getLogger("PENS");
	
	public static StringBuffer exportSummaryAll(String orderDate) throws Exception{
		StringBuffer h = new StringBuffer("");
		PreparedStatement ps =null;
		ResultSet rs = null;
		StringBuffer sql = new StringBuffer("");
		Connection conn = null;
		int count = 0;
		double totalOrderQty = 0;
		double totalStore = 0;
		double totalRecord = 0;
		try{
			h.append(ExcelHeader.EXCEL_HEADER);
			
			conn = DBConnection.getInstance().getConnection();
			Date orderDateO = Utils.parse(orderDate, Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			
			//Total Store
			sql.append(" select count(distinct store_code) as c from PENSBME_ORDER WHERE trunc(order_date) =? \n");
            ps = conn.prepareStatement(sql.toString());
			ps.setTimestamp(1, new java.sql.Timestamp(orderDateO.getTime()));
			rs = ps.executeQuery();
			if(rs.next()){
				totalStore = rs.getDouble("c");
			}
			
			//Total record
			sql = new StringBuffer();
			sql.append(" select count(*) as c from PENSBME_ORDER WHERE trunc(order_date) =? \n");
            ps = conn.prepareStatement(sql.toString());
			ps.setTimestamp(1, new java.sql.Timestamp(orderDateO.getTime()));
			rs = ps.executeQuery();
			if(rs.next()){
				totalRecord = rs.getDouble("c");
			}
			
			//Summary Store Type
			sql = new StringBuffer();
			sql.append(" select store_type, sum(qty) as qty from PENSBME_ORDER WHERE trunc(order_date) =? group by store_type \n");
            ps = conn.prepareStatement(sql.toString());
			ps.setTimestamp(1, new java.sql.Timestamp(orderDateO.getTime()));
			rs = ps.executeQuery();
			
			//Header
			h.append("<table border='1'> \n");
			h.append("<tr> \n");
			h.append(" <td align='left' class='colum_head' colspan='2' nowrap><font size='3'>สรุปยอดรวม Order ประจำวันที่  :  "+orderDate+"</font></td> \n");
			h.append("</tr> \n");
			h.append("<tr> \n");
			h.append("  <td></td> \n");
			h.append("  <td></td> \n");
			h.append("</tr> \n");
			h.append("<tr> \n");
			h.append("  <td class='colum_head' align='center'>กลุ่มร้านค้า</td> \n");
			h.append("  <td class='colum_head' align='center'>ยอดรวม Order (ชิ้น)</td> \n");
			h.append("</tr> \n");
			
			 while(rs.next()){
				totalOrderQty +=rs.getDouble("qty");
				h.append("<tr> \n");
				h.append("  <td class='text' align='center'>"+Utils.isNull(rs.getString("store_type"))+"</td> \n");
				h.append("  <td class='num_currency' align='center'>"+Utils.decimalFormat(rs.getDouble("qty"),Utils.format_current_no_disgit)+"</td> \n");
				h.append("</tr> \n");
			 }
			 h.append("<tr> \n");
			 h.append("  <td class='colum_head' align='center'>Total Order</td> \n");
			 h.append("  <td class='colum_head' align='center'>"+Utils.decimalFormat(totalOrderQty,Utils.format_current_no_disgit)+"</td> \n");
			 h.append("</tr> \n");
			 h.append("<tr> \n");
			 h.append("  <td class='colum_head' align='center'>จำนวนสาขา</td> \n");
			 h.append("  <td class='colum_head' align='center'>"+Utils.decimalFormat(totalStore,Utils.format_current_no_disgit)+"</td> \n");
			 h.append("</tr> \n");
			 h.append("<tr> \n");
			 h.append("  <td class='colum_head' align='center'>จำนวนแถว</td> \n");
			 h.append("  <td class='colum_head' align='center'>"+Utils.decimalFormat(totalRecord,Utils.format_current_no_disgit)+"</td> \n");
			 h.append("</tr> \n");
			 h.append("</table> \n");
			 
			//Summary Order Qty Over Onhand Qty
			 sql = new StringBuffer();
			 sql.append("select A.barcode ,A.order_qty,A.onhand_qty \n");
			 sql.append(",(select OH.material_master FROM PENSBME_ONHAND_BME OH WHERE OH.barcode =A.barcode ) mat \n");
			 sql.append("from(\n");
			 sql.append("  select O.barcode, nvl(sum(O.qty) ,0) as order_qty\n");
			 sql.append("    ,( select sum(onhand_qty)  \n");
			 sql.append("       FROM  PENSBME_ONHAND_BME  OH \n");
			 sql.append("       where OH.barcode = O.barcode \n");
			 sql.append("       group by OH.barcode \n");
			 sql.append("     ) as onhand_qty\n");
			 sql.append("   from PENSBME_ORDER O\n");
			 sql.append("   WHERE trunc(O.order_date) = ? \n");
			 sql.append("   AND O.store_type not in('"+Constants.STORE_TYPE_FRIDAY_CODE+"','"+Constants.STORE_TYPE_OSHOPPING_CODE+"','"+Constants.STORE_TYPE_7CATALOG_CODE+"','"+Constants.STORE_TYPE_TVD_CODE+"') \n");
			 sql.append("   group by O.barcode \n");
			 sql.append(")A \n");
			 sql.append("where A.order_qty > A.onhand_qty \n");
			 
			 sql.append("UNION ALL \n");
			 
			 sql.append("select A.barcode ,A.order_qty,A.onhand_qty \n");
			 sql.append(",(select OH.material_master FROM PENSBME_ONHAND_BME_FRIDAY OH WHERE OH.barcode =A.barcode ) mat \n");
			 sql.append("from(\n");
			 sql.append("  select O.barcode, nvl(sum(O.qty) ,0) as order_qty\n");
			 sql.append("    ,( select sum(onhand_qty)  \n");
			 sql.append("       FROM  PENSBME_ONHAND_BME  OH \n");
			 sql.append("       where OH.barcode = O.barcode \n");
			 sql.append("       group by OH.barcode \n");
			 sql.append("     ) as onhand_qty\n");
			 sql.append("   from PENSBME_ORDER O\n");
			 sql.append("   WHERE trunc(O.order_date) = ? \n");
			 sql.append("   AND O.store_type in('"+Constants.STORE_TYPE_FRIDAY_CODE+"') \n");
			 sql.append("   group by O.barcode \n");
			 sql.append(")A\n");
			 sql.append("where A.order_qty > A.onhand_qty \n");
			 
	         sql.append("UNION ALL \n");
			 
			 sql.append("select A.barcode ,A.order_qty,A.onhand_qty \n");
			 sql.append(",(select OH.material_master FROM PENSBME_ONHAND_BME_OSHOPPING OH WHERE OH.barcode =A.barcode ) mat \n");
			 sql.append("from(\n");
			 sql.append("  select O.barcode, nvl(sum(O.qty) ,0) as order_qty\n");
			 sql.append("    ,( select sum(onhand_qty)  \n");
			 sql.append("       FROM  PENSBME_ONHAND_BME  OH \n");
			 sql.append("       where OH.barcode = O.barcode \n");
			 sql.append("       group by OH.barcode \n");
			 sql.append("     ) as onhand_qty\n");
			 sql.append("   from PENSBME_ORDER O\n");
			 sql.append("   WHERE trunc(O.order_date) = ? \n");
			 sql.append("   AND O.store_type in('"+Constants.STORE_TYPE_OSHOPPING_CODE+"') \n");
			 sql.append("   group by O.barcode \n");
			 sql.append(")A\n");
			 sql.append("where A.order_qty > A.onhand_qty \n");
			 
	         sql.append("UNION ALL \n");
			 
			 sql.append("select A.barcode ,A.order_qty,A.onhand_qty \n");
			 sql.append(",(select OH.material_master FROM PENSBME_ONHAND_BME_7CATALOG OH WHERE OH.barcode =A.barcode ) mat \n");
			 sql.append("from(\n");
			 sql.append("  select O.barcode, nvl(sum(O.qty) ,0) as order_qty\n");
			 sql.append("    ,( select sum(onhand_qty)  \n");
			 sql.append("       FROM  PENSBME_ONHAND_BME  OH \n");
			 sql.append("       where OH.barcode = O.barcode \n");
			 sql.append("       group by OH.barcode \n");
			 sql.append("     ) as onhand_qty\n");
			 sql.append("   from PENSBME_ORDER O\n");
			 sql.append("   WHERE trunc(O.order_date) = ? \n");
			 sql.append("   AND O.store_type in('"+Constants.STORE_TYPE_7CATALOG_CODE+"') \n");
			 sql.append("   group by O.barcode \n");
			 sql.append(")A\n");
			 sql.append("where A.order_qty > A.onhand_qty \n");
			 
	         sql.append("UNION ALL \n");
			 
			 sql.append("select A.barcode ,A.order_qty,A.onhand_qty \n");
			 sql.append(",(select OH.material_master FROM PENSBME_ONHAND_BME_TVDIRECT OH WHERE OH.barcode =A.barcode ) mat \n");
			 sql.append("from(\n");
			 sql.append("  select O.barcode, nvl(sum(O.qty) ,0) as order_qty\n");
			 sql.append("    ,( select sum(onhand_qty)  \n");
			 sql.append("       FROM  PENSBME_ONHAND_BME  OH \n");
			 sql.append("       where OH.barcode = O.barcode \n");
			 sql.append("       group by OH.barcode \n");
			 sql.append("     ) as onhand_qty\n");
			 sql.append("   from PENSBME_ORDER O\n");
			 sql.append("   WHERE trunc(O.order_date) = ? \n");
			 sql.append("   AND O.store_type in('"+Constants.STORE_TYPE_TVD_CODE+"') \n");
			 sql.append("   group by O.barcode \n");
			 sql.append(")A\n");
			 sql.append("where A.order_qty > A.onhand_qty \n");
			 
			 logger.debug("sql:"+sql.toString());
			 
	         ps = conn.prepareStatement(sql.toString());
		     ps.setTimestamp(1, new java.sql.Timestamp(orderDateO.getTime()));
		     ps.setTimestamp(2, new java.sql.Timestamp(orderDateO.getTime()));
		     ps.setTimestamp(3, new java.sql.Timestamp(orderDateO.getTime()));
		     ps.setTimestamp(4, new java.sql.Timestamp(orderDateO.getTime()));
		     ps.setTimestamp(5, new java.sql.Timestamp(orderDateO.getTime()));
		     
		     rs = ps.executeQuery();
		     
		     h.append("<table border='0'> \n");
		     h.append("<tr> \n");
			 h.append("  <td colspan='3'></td> \n");
			 h.append("</tr> \n");
			 h.append("<tr> \n");
			 h.append("  <td colspan='3'>แสดงรายการที่ยอด Order มากกว่า ยอด Stock Onhand ของ Wacoal  ( ให้แก้ไขก่อนที่จะส่ง Mail ให้ Wacoal )</td> \n");
			 h.append("</tr> \n");
			 h.append("<tr> \n");
			 h.append("  <td colspan='3'></td> \n");
			 h.append("</tr> \n");
			 h.append("</table> \n");
			 
		     h.append("<table border='1'> \n");
			 while(rs.next()){
				 if(count==0){
					h.append("<tr> \n");
					h.append("  <td class='colum_head' align='center'>Material Master</td> \n");
					h.append("  <td class='colum_head' align='center'>Order Qty</td> \n");
					h.append("  <td class='colum_head' align='center'>Onhand Qty</td> \n");
					h.append("</tr> \n");
				 }
				h.append("<tr> \n");
				h.append("  <td class='text' align='center'>"+Utils.isNull(rs.getString("mat"))+"</td> \n");
				h.append("  <td class='num_currency' align='center'>"+Utils.decimalFormat(rs.getDouble("order_qty"),Utils.format_current_no_disgit)+"</td> \n");
				h.append("  <td class='num_currency' align='center'>"+Utils.decimalFormat(rs.getDouble("onhand_qty"),Utils.format_current_no_disgit)+"</td> \n");
				h.append("</tr> \n");
				
				count ++;
			}
			h.append("</table> \n");
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
			   ps.close();ps = null;
			}
			if(rs != null){
			   rs.close();rs = null;
			}
			if(conn != null){
				conn.close();conn=null;
			}
		}
		return h;
	}
}
