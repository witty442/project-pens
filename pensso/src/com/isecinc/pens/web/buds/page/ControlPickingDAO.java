package com.isecinc.pens.web.buds.page;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import util.ExcelHeader;
import util.SQLHelper;

import com.isecinc.core.model.I_PO;
import com.isecinc.pens.bean.ConfPickingBean;
import com.isecinc.pens.bean.PopupBean;
import com.isecinc.pens.bean.UOMConversion;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.StockUtilsDAO;
import com.isecinc.pens.model.MUOMConversion;
import com.isecinc.pens.web.autokeypress.AutoKeypressDAO;
import com.pens.util.DBConnectionApps;
import com.pens.util.DateUtil;
import com.pens.util.FileUtil;
import com.pens.util.Utils;

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
			sql.append("\n    SELECT t.picking_no ,t.status");
			sql.append("\n    FROM pensso.t_picking_trans t");
			sql.append("\n    WHERE 1=1 ");
			if( !Utils.isNull(o.getPickingNo()).equals("")){
				sql.append("\n and t.picking_no = '"+Utils.isNull(o.getPickingNo())+"'");
			}
			if( !Utils.isNull(o.getTransactionDate()).equals("")){
				sql.append("\n and t.transaction_date = to_date('"+DateUtil.convBuddhistToChristDate(o.getTransactionDate(), DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/mm/yyyy')");
			}
			sql.append("\n   group by t.picking_no ");
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
			sql.append("\n    SELECT t.picking_no ,t.transaction_date ,t.status");
			sql.append("\n    FROM pensso.t_picking_trans t");
			sql.append("\n    WHERE 1=1 ");
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
			sql.append("\n    ,t.total_amount ,t.vat_amount,(t.total_amount+t.vat_amount)as NET_AMOUNT");
			sql.append("\n    FROM pensso.t_order t ,pensso.t_order_line l");
			sql.append("\n    ,pensso.m_customer c,pensso.m_address a");
			sql.append("\n    ,pensso.m_product p ");
			sql.append("\n    WHERE t.order_id = l.order_id ");
			sql.append("\n    AND t.customer_id = c.customer_id ");
			sql.append("\n    AND c.customer_id = a.customer_id ");
			sql.append("\n    AND l.product_id = p.product_id ");
			sql.append("\n    AND a.purpose = 'S' ");
			sql.append("\n    AND t.doc_status in('"+status+"')" );
			
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
			sql.append("\n    AND a.purpose = 'S' ");
			sql.append("\n    AND t.doc_status in('"+status+"')" );
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
			  
			  item.setPickingNo(Utils.isNull(rst.getString("picking_no")));
			  item.setOrderDate(DateUtil.stringValue(rst.getDate("ORDER_DATE"), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
			  item.setOrderNo(Utils.isNull(rst.getString("order_no")));
			  item.setCustomerCode(Utils.isNull(rst.getString("customer_number")));
			  item.setCustomerName(Utils.isNull(rst.getString("customer_name")));
			  item.setSalesrepCode(Utils.isNull(rst.getString("salesrep_code")));
			  item.setSalesrepName(Utils.isNull(rst.getString("salesrep_name")));
			  item.setTotalAmount(Utils.decimalFormat(rst.getDouble("total_amount"),Utils.format_current_2_disgit));
			  item.setVatAmount(Utils.decimalFormat(rst.getDouble("vat_amount"),Utils.format_current_2_disgit));
			  item.setNetAmount(Utils.decimalFormat(rst.getDouble("net_amount"),Utils.format_current_2_disgit));
			  
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
	
	
	private static StringBuffer genHeadTable(ConfPickingBean head,boolean excel) throws Exception{
		StringBuffer h = new StringBuffer("");
		String width="100%",border="0";
		if(excel){
			border="1";
			h.append(ExcelHeader.EXCEL_HEADER);
			h.append("<table id='tblProduct' align='center' border='"+border+"' width='"+width+"' cellpadding='3' cellspacing='1' class='tableSearchNoWidth'> \n");
			h.append("<tr><td colspan='13'><b> Picking No:"+head.getPickingNo()+"   Trasnsaction Date:"+head.getTransactionDate()+"</b></td> </tr>\n");
			h.append("<tr><td colspan='13'></td></tr>\n");
			h.append("</table>\n");
		}
		
		h.append("<table id='tblProduct' align='center' border='"+border+"' width='"+width+"' cellpadding='3' cellspacing='1' class='table table-condensed table-striped'> \n");
		h.append("<thead><tr> \n");
	
		   h.append("<th></th> \n");
		
		h.append("<th>Order No / PO No</th> \n");
		h.append("<th>Order Date / PO Date</th> \n");
		h.append("<th>Customer No</th> \n");
		h.append("<th>Customer Name</th> \n");
		h.append("<th>Õ”‡¿Õ / ®—ßÀ«—¥</th> \n");
		h.append("<th>‡´≈≈Ï</th> \n");
		h.append("<th>SKU</th> \n");
		h.append("<th>SKU Name</th> \n");
		h.append("<th>Qty</th> \n");
		h.append("<th>UOM</th> \n");
		h.append("<th>Amount (Ex.vat)</th> \n");
		h.append("<th>Vat</th> \n");
		h.append("<th>Amount (In.vat)</th> \n");
		h.append("</tr></thead> \n");
		h.append("<tbody> \n");
		return h;
	}
	
	/**
	 * 
	 * @param columnNameArr
	 * @param ROWVALUE_MAP
	 * @param ROWDESC_MAP
	 * @param o
	 * @return
	 * @throws Exception
	 */
	private static StringBuffer genRowTable(ConfPickingBean head,boolean excel,ConfPickingBean item,int lineNo) throws Exception{
		StringBuffer h = new StringBuffer("");
		char singleQuote ='"';
		String trClass =lineNo%2==0?"lineE":"lineO";
		String className ="";
		String classNameCenter ="td_text_center";
		String classNameNumber = "td_number";
		if(excel){
			className = "text";
			classNameCenter ="text";
			classNameNumber = "num_currency";
		}
		h.append("<tr class='"+trClass+"'> \n");
		h.append("<td class='"+classNameCenter+"' width='5%'>");
		h.append(" <input type='checkbox' name='chkOrder' value='"+item.getOrderNo()+"'/>");
		h.append("</td> \n");
		h.append("<td class='"+classNameCenter+"' width='6%'>"+item.getOrderNo());
        h.append("<td class='"+classNameCenter+"' width='5%'>"+item.getOrderDate()+"</td> \n");
        h.append("  <input type='hidden' name='orderNo' value='"+item.getOrderNo()+"'/>");
        h.append("</td> \n");
		h.append("<td class='"+classNameCenter+"' width='7%'>"+item.getCustomerCode()+"</td> \n");
		h.append("<td class='"+className+"' width='15%'>"+item.getCustomerName()+"</td> \n");
		h.append("<td class='"+className+"' width='15%'>"+item.getAmphur()+"/"+item.getProvince()+"</td> \n");
		h.append("<td class='"+classNameCenter+"' width='3%'>"+item.getSalesrepCode()+"</td> \n");
		h.append("<td class='"+classNameCenter+"' width='5%'>"+item.getProductCode()+"</td> \n");
		h.append("<td class='"+className+"' width='10%'>"+item.getProductName()+"</td> \n");
		h.append("<td class='"+classNameNumber+"' width='5%'>"+item.getQty()+"</td> \n");
		h.append("<td class='"+classNameCenter+"' width='3%'>"+item.getUom()+"</td> \n");
		h.append("<td class='"+classNameNumber+"' width='7%'>"+item.getTotalAmount()+"</td> \n");
		h.append("<td class='"+classNameNumber+"' width='7%'>"+item.getVatAmount()+"</td> \n");
		h.append("<td class='"+classNameNumber+"' width='7%'>"+item.getNetAmount()+"</td> \n");
		h.append("</tr> \n");
		return h;
	}
	private static StringBuffer genRowTotal(ConfPickingBean head,boolean excel,double totalAmount,double totalVatAmount,double totalNetAmount) throws Exception{
		StringBuffer h = new StringBuffer("");
		String className ="";
		String classNameCenter ="td_text_center";
		String classNameNumber = "td_number",border="0";
		String width="100";
		if(excel){
			border="1";
			className = "text";
			classNameCenter ="text";
			classNameNumber = "num_currency";
		}
		h.append("<table id='tblSummary' align='center' border='"+border+"' width='"+width+"%' cellpadding='3' cellspacing='1' class='tableSearchNoWidth'> \n");
		h.append("<tr class='hilight_text'> \n");
	    h.append("<td  colspan='11' width='78%' align='right'>Total</td> \n");
		h.append("<td class='"+classNameNumber+"' width='7%'>"+Utils.decimalFormat(totalAmount,Utils.format_current_2_disgit)+"</td> \n");
		h.append("<td class='"+classNameNumber+"' width='7%'>"+Utils.decimalFormat(totalVatAmount,Utils.format_current_2_disgit)+"</td> \n");
		h.append("<td class='"+classNameNumber+"' width='8%'>"+Utils.decimalFormat(totalNetAmount,Utils.format_current_2_disgit)+"</td> \n");
		h.append("</tr> \n");
		h.append("</table> \n");
		return h;
	}
}
