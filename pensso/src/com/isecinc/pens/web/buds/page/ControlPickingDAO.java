package com.isecinc.pens.web.buds.page;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.core.model.I_PO;
import com.isecinc.pens.bean.ConfPickingBean;
import com.isecinc.pens.bean.User;
import com.pens.util.DBConnectionApps;
import com.pens.util.DateUtil;
import com.pens.util.FileUtil;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;

public class ControlPickingDAO {
	/** Logger */
	public static Logger logger = Logger.getLogger("PENS");

	public static int searchTotalHead(Connection conn,ConfPickingBean o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int totalRec = 0;
		try {
			sql.append("\n select count(*) as c from(");
			sql.append("\n    SELECT distinct t.picking_no ,t.status");
			sql.append("\n    FROM pensso.t_picking_trans t ,pensso.t_order o,pensso.t_invoice inv");
			sql.append("\n    WHERE t.status ='"+I_PO.STATUS_LOADING+"'");
			sql.append("\n    and t.picking_no = o.picking_no ");
			sql.append("\n    and o.order_no = inv.ref_order ");
			if( !Utils.isNull(o.getPickingNo()).equals("")){
				sql.append("\n and t.picking_no = '"+Utils.isNull(o.getPickingNo())+"'");
			}
			if( !Utils.isNull(o.getTransactionDate()).equals("")){
				sql.append("\n and t.transaction_date = to_date('"+DateUtil.convBuddhistToChristDate(o.getTransactionDate(), DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/mm/yyyy')");
			}
			sql.append("\n   order by t.picking_no desc ");
            sql.append("\n )A ");
			logger.debug("sql:"+sql);
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			if(rst.next()) {
				totalRec = rst.getInt("c");
			}//while
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return totalRec;
	}
	
	public static ConfPickingBean searchHead(Connection conn,ConfPickingBean o ,boolean getTrans,boolean allRec ,int currPage,int pageSize) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		ConfPickingBean h = null;
		List<ConfPickingBean> items = new ArrayList<ConfPickingBean>();
		int r = 1;
		try {
			sql.append("\n select M.* from (");
			sql.append("\n select A.* ,rownum as r__ from (");
			sql.append("\n    SELECT distinct t.picking_no ,t.transaction_date ,t.status");
			sql.append("\n    FROM pensso.t_picking_trans t ,pensso.t_order o,pensso.t_invoice inv");
			sql.append("\n    WHERE t.status ='"+I_PO.STATUS_LOADING+"'");
			sql.append("\n    and t.picking_no = o.picking_no ");
			sql.append("\n    and o.order_no = inv.ref_order ");
			if( !Utils.isNull(o.getPickingNo()).equals("")){
				sql.append("\n and t.picking_no = '"+Utils.isNull(o.getPickingNo())+"'");
			}
			if( !Utils.isNull(o.getTransactionDate()).equals("")){
				sql.append("\n and t.transaction_date = to_date('"+DateUtil.convBuddhistToChristDate(o.getTransactionDate(), DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/mm/yyyy')");
			}
			sql.append("\n   order by t.picking_no desc ");
            sql.append("\n  )A ");
        	// get record start to end 
            if( !allRec){
        	  sql.append("\n    WHERE rownum < (("+currPage+" * "+pageSize+") + 1 )  ");
            } 
	        sql.append("\n )M  ");
	        if( !allRec){
				sql.append("\n  WHERE r__ >= ((("+currPage+"-1) * "+pageSize+") + 1)  ");
			}
			logger.debug("sql:"+sql);
			ps = conn.prepareStatement(sql.toString());
			
			rst = ps.executeQuery();
			while(rst.next()) {
			   h = new ConfPickingBean();
			   h.setPickingNo(Utils.isNull(rst.getString("picking_no")));
			   h.setStatus(Utils.isNull(rst.getString("status")));
			   h.setTransactionDate(DateUtil.stringValue(rst.getDate("transaction_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
			  
			   h = searchPickingSummary(conn, h,rst.getString("status"));
			   
			   items.add(h);
			   r++;
			}//while
			
			//set Result 
			o.setItemsList(items);
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
	return o;
}
	public static ConfPickingBean searchPickingSummary(Connection conn,ConfPickingBean o,String status){
		Statement stmt = null;
		ResultSet rst = null;
		StringBuffer sql = new StringBuffer();
		double totalAmount = 0;
		double totalVatAmount = 0;
		double totalNetAmount = 0;
		try{
			
			sql.append("\n  SELECT sum(total_amount) as total_amount");
			sql.append("\n  ,sum(vat_amount) as vat_amount");
			sql.append("\n  ,sum(net_amount) as net_amount");
			sql.append("\n  FROM (");
			sql.append("\n    /** SALES_APP **/ ");
			sql.append("\n    SELECT t.picking_no, t.ORDER_NO ,t.ORDER_DATE ,t.doc_status as status");
			sql.append("\n    ,c.code as customer_number ,c.name as CUSTOMER_NAME");
			sql.append("\n    ,(select name from pensso.m_district ad where ad.district_id = a.district_id) as amphur");
			sql.append("\n    ,(select name from pensso.m_province ad where ad.province_id = a.province_id) as province");
			sql.append("\n    ,(select code from pensso.ad_user ad where ad.user_id = t.user_id) as salesrep_code");
			sql.append("\n    ,(select subbrand_no from PENSBI.XXPENS_BI_MST_SUBBRAND ad where ad.inventory_item_id = l.product_id) as subbrand");
			sql.append("\n    ,(select subbrand_desc from PENSBI.XXPENS_BI_MST_SUBBRAND ad where ad.inventory_item_id = l.product_id) as subbrand_desc");
			sql.append("\n    ,p.name as product_name ,p.code as product_code ,l.uom_id ,l.qty");
			sql.append("\n    ,l.total_amount ,l.vat_amount,(l.total_amount+l.vat_amount)as NET_AMOUNT");
			sql.append("\n    FROM pensso.t_order t ,pensso.t_order_line l");
			sql.append("\n    ,pensso.m_customer c,pensso.m_address a");
			sql.append("\n    ,pensso.m_product p ");
			sql.append("\n    WHERE t.order_id = l.order_id ");
			sql.append("\n    AND t.customer_id = c.customer_id ");
			sql.append("\n    AND c.customer_id = a.customer_id ");
			sql.append("\n    AND l.product_id = p.product_id ");
			sql.append("\n    AND t.ship_address_id = a.address_id ");
			sql.append("\n    AND a.purpose = 'S' ");
			sql.append("\n    AND t.doc_status in('"+status+"')" );
			sql.append("\n    AND t.picking_no ='"+o.getPickingNo()+"'" );
			sql.append("\n    UNION ALL ");
			
			sql.append("\n    /** EDI **/ ");
			sql.append("\n    SELECT t.picking_no, t.CUST_PO_NUMBER as ORDER_NO ,t.ORDERED_DATE as ORDER_DATE ,t.doc_status as status");
			sql.append("\n    ,c.code as customer_number ,c.name as CUSTOMER_NAME");
			sql.append("\n    ,(select name from pensso.m_district ad where ad.district_id = a.district_id) as amphur");
			sql.append("\n    ,(select name from pensso.m_province ad where ad.province_id = a.province_id) as province");
			sql.append("\n    ,(select code from apps.xxpens_salesreps_v ad where ad.salesrep_id = t.salesrep_id) as salesrep_code");
			sql.append("\n    ,(select subbrand_no from PENSBI.XXPENS_BI_MST_SUBBRAND ad where ad.inventory_item_id = l.inventory_item_id) as subbrand");
			sql.append("\n    ,(select subbrand_desc from PENSBI.XXPENS_BI_MST_SUBBRAND ad where ad.inventory_item_id = l.inventory_item_id) as subbrand_desc");
			sql.append("\n    ,p.name as product_name ,p.code as product_code ,l.ORDER_QUANTITY_UOM as uom_id ,l.ORDERED_QUANTITY as qty");
			sql.append("\n    ,l.LINE_AMOUNT as total_amount ,(l.LINE_AMOUNT *0.07) as vat_amount,(l.LINE_AMOUNT+(l.LINE_AMOUNT *0.07)) as NET_AMOUNT");
			sql.append("\n    FROM pensso.T_EDI t ,pensso.T_EDI_LINE l");
			sql.append("\n    ,pensso.m_customer c,pensso.m_address a");
			sql.append("\n    ,pensso.m_product p ");
			sql.append("\n    WHERE t.HEADER_ID = l.HEADER_ID ");
			sql.append("\n    AND t.customer_id = c.customer_id ");
			sql.append("\n    AND c.customer_id = a.customer_id ");
			sql.append("\n    AND l.inventory_item_id = p.product_id ");
			sql.append("\n    AND t.ship_to_site_use_id = a.address_id ");
			sql.append("\n    AND a.purpose = 'S' ");
			sql.append("\n    AND t.doc_status in('"+status+"')" );
			sql.append("\n    AND t.picking_no ='"+o.getPickingNo()+"'" );
			sql.append("\n )M ");
			sql.append("\n where M.PICKING_NO ='"+o.getPickingNo()+"'");
			sql.append("\n group by M.picking_no ");
			
			logger.debug("sql:"+sql);
			if(logger.isDebugEnabled()){
				FileUtil.writeFile("d://dev_temp//temp//sql.sql", sql.toString(),"TIS-620");
			}
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if(rst.next()) {
			   totalAmount += rst.getDouble("total_amount");
			   totalVatAmount += rst.getDouble("vat_amount");
			   totalNetAmount += rst.getDouble("net_amount");
			}//while
			
			o.setTotalAmount(Utils.decimalFormat(totalAmount,Utils.format_current_2_disgit));
			o.setVatAmount(Utils.decimalFormat(totalVatAmount,Utils.format_current_2_disgit));
			o.setNetAmount(Utils.decimalFormat(totalNetAmount,Utils.format_current_2_disgit));
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			//e.printStackTrace();
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {}
		}
	  return o;
	}
	public static List<ConfPickingBean> searchPickingReport(ConfPickingBean o,User user){
		ConfPickingBean item = null;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		StringBuffer sql = new StringBuffer();
		int r = 0;
		List<ConfPickingBean> results = new ArrayList<ConfPickingBean>();
		double totalAmount = 0;
		double totalVatAmount = 0;
		double totalNetAmount = 0;
		try{
			//create connection
			conn = DBConnectionApps.getInstance().getConnection();
			sql = genSQLControlPicking(o);
			
			logger.debug("sql:"+sql);
			if(logger.isDebugEnabled()){
				FileUtil.writeFile("d://dev_temp//temp//sql.sql", sql.toString(),"TIS-620");
			}
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
			  item = new ConfPickingBean();
			  r++;
			  item.setInvoiceNo("X0000");
			  item.setInvoiceDate(DateUtil.stringValue(rst.getDate("ORDER_DATE"), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
			  item.setMobile("0894255455");
			  item.setPickingNo(Utils.isNull(rst.getString("picking_no")));
			  item.setOrderDate(DateUtil.stringValue(rst.getDate("ORDER_DATE"), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
			  item.setOrderNo(Utils.isNull(rst.getString("order_no")));
			  item.setCustomerCode(Utils.isNull(rst.getString("customer_number")));
			  item.setCustomerName(Utils.isNull(rst.getString("customer_name")));
			  item.setSalesrepCode(Utils.isNull(rst.getString("salesrep_code")));
			  item.setSalesrepName(Utils.isNull(rst.getString("salesrep_name")));
			  item.setTotalAmountD(rst.getDouble("total_amount"));
			  item.setVatAmountD(rst.getDouble("vat_amount"));
			  item.setNetAmountD(rst.getDouble("net_amount"));
			  
			  totalAmount += rst.getDouble("total_amount");
			  totalVatAmount += rst.getDouble("vat_amount");
			  totalNetAmount += rst.getDouble("net_amount");
			  
			  results.add(item);
			  
			}//while
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			//e.printStackTrace();
		} finally {
			try {
				rst.close();
				stmt.close();
				conn.close();
			} catch (Exception e) {}
		}
	  return results;
	}
	
	private static StringBuffer genSQLControlPicking(ConfPickingBean o) throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append("\n  SELECT M.*");
		sql.append("\n  FROM (");
		sql.append("\n    /** SALES_APP **/ ");
		sql.append("\n    SELECT t.picking_no, t.ORDER_NO ,t.ORDER_DATE ,t.doc_status as status");
		sql.append("\n    ,c.code as customer_number ,c.name as CUSTOMER_NAME");
		sql.append("\n    ,s.code as salesrep_code,s.name as salesrep_name");
		sql.append("\n    ,t.total_amount ,t.vat_amount,(t.total_amount+t.vat_amount) as NET_AMOUNT");
		sql.append("\n    FROM pensso.t_order t ,pensso.t_order_line l");
		sql.append("\n    ,pensso.m_customer c ,pensso.ad_user s");
		sql.append("\n    WHERE t.order_id = l.order_id ");
		sql.append("\n    AND t.customer_id = c.customer_id ");
		sql.append("\n    AND s.user_id = t.user_id ");
		sql.append("\n    AND t.doc_status ='"+I_PO.STATUS_LOADING+"'" );
		if( !Utils.isNull(o.getPickingNo()).equals("")){
		   sql.append("\n    AND t.PICKING_NO ='"+o.getPickingNo()+"'");
		}
		
		sql.append("\n    UNION ALL ");
		
		sql.append("\n    /** EDI **/ ");
		sql.append("\n    SELECT t.picking_no, t.CUST_PO_NUMBER as ORDER_NO ,t.ORDERED_DATE as ORDER_DATE ,t.doc_status as status");
		sql.append("\n    ,c.code as customer_number ,c.name as CUSTOMER_NAME");
		sql.append("\n    ,s.code as salesrep_code,s.name as salesrep_name");
		sql.append("\n    ,l.LINE_AMOUNT as total_amount ,(l.LINE_AMOUNT *0.07) as vat_amount,(l.LINE_AMOUNT+(l.LINE_AMOUNT *0.07)) as NET_AMOUNT");
		sql.append("\n    FROM pensso.T_EDI t ,pensso.T_EDI_LINE l");
		sql.append("\n    ,pensso.m_customer c,pensso.ad_user s");
		sql.append("\n    WHERE t.HEADER_ID = l.HEADER_ID ");
		sql.append("\n    AND t.customer_id = c.customer_id ");
		sql.append("\n    AND s.user_id = t.salesrep_id ");
		sql.append("\n    AND t.doc_status ='"+I_PO.STATUS_LOADING+"'" );
		if( !Utils.isNull(o.getPickingNo()).equals("")){
		   sql.append("\n    AND t.PICKING_NO ='"+o.getPickingNo()+"'");
		}
		sql.append("\n )M ");
		sql.append("\n where 1=1");
		sql.append("\n ORDER BY M.salesrep_code,M.order_date ");
		
		return sql;
	}
	
	
	
}
