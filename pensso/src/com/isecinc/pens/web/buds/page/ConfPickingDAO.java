package com.isecinc.pens.web.buds.page;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.isecinc.core.model.I_PO;
import com.isecinc.pens.bean.ConfPickingBean;
import com.isecinc.pens.bean.PopupBean;
import com.isecinc.pens.bean.UOMConversion;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.StockUtilsDAO;
import com.isecinc.pens.model.MPriceList;
import com.isecinc.pens.model.MUOMConversion;
import com.isecinc.pens.web.autokeypress.AutoKeypressDAO;
import com.pens.util.ControlCode;
import com.pens.util.DBCPConnectionProvider;
import com.pens.util.DBConnectionApps;
import com.pens.util.DateUtil;
import com.pens.util.FileUtil;
import com.pens.util.SQLHelper;
import com.pens.util.Utils;
import com.pens.util.excel.ExcelHeader;
import com.pens.util.seq.SequenceProcessAll;

public class ConfPickingDAO {
	/** Logger */
	public static Logger logger = Logger.getLogger("PENS");

	public static int searchTotalHead(Connection conn,String subPageName,ConfPickingBean o ) throws Exception {
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
			if( !Utils.isNull(o.getTransactionDateFrom()).equals("") && !Utils.isNull(o.getTransactionDateTo()).equals("")){
				sql.append("\n and t.transaction_date >= to_date('"+DateUtil.convBuddhistToChristDate(o.getTransactionDateFrom(), DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/mm/yyyy')");
				sql.append("\n and t.transaction_date <= to_date('"+DateUtil.convBuddhistToChristDate(o.getTransactionDateTo(), DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/mm/yyyy')");
			}
			if(subPageName.equalsIgnoreCase("BudsConfPicking")){
				sql.append("\n and t.print_picking_count >=1 ");
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
	
	public static ConfPickingBean searchHead(Connection conn,String subPageName,ConfPickingBean o ,boolean getTrans,boolean allRec ,int currPage,int pageSize) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		ConfPickingBean h = null;
		List<ConfPickingBean> items = new ArrayList<ConfPickingBean>();
		int r = 1;
		try {
			sql.append("\n select M.* from (");
			sql.append("\n select A.* ,rownum as r__ from (");
			sql.append("\n    SELECT t.picking_no ,t.transaction_date ,t.status,t.region");
			sql.append("\n    ,inv.invoice_count ,s.total_amount,s.vat_amount ,s.net_amount");
			sql.append("\n    FROM pensso.t_picking_trans t");
			sql.append("\n    LEFT OUTER JOIN (");
			sql.append("\n      select picking_no,count(*) as invoice_count from( ");
			sql.append("\n        select o.picking_no,inv.invoice_no from pensso.t_order o,pensso.t_invoice inv");
			sql.append("\n        where o.order_no = inv.ref_order");
			sql.append("\n        union ");
			sql.append("\n        select o.picking_no,inv.invoice_no from pensso.t_edi o,pensso.t_invoice inv");
			sql.append("\n        where o.cust_po_number = inv.ref_order");
			sql.append("\n       )group by picking_no ");
			sql.append("\n   )inv on t.picking_no = inv.picking_no");
			sql.append("\n   LEFT OUTER JOIN ");
			sql.append("\n   ( ");
			sql.append("\n    SELECT picking_no,sum(total_amount) as total_amount");
			sql.append("\n   ,sum(vat_amount) as vat_amount");
			sql.append("\n   ,sum(net_amount) as net_amount");
			sql.append("\n   FROM (");
			sql.append("\n     /** SALES_APP **/ ");
			sql.append("\n     SELECT t.picking_no ");
			sql.append("\n     ,l.total_amount ,l.vat_amount,(l.total_amount+l.vat_amount)as NET_AMOUNT");
			sql.append("\n     FROM pensso.t_order t ,pensso.t_order_line l");
			sql.append("\n     ,pensso.m_customer c,pensso.m_address a");
			sql.append("\n     ,pensso.m_product p ");
			sql.append("\n     WHERE t.order_id = l.order_id ");
			sql.append("\n     AND t.customer_id = c.customer_id ");
			sql.append("\n     AND c.customer_id = a.customer_id ");
			sql.append("\n     AND l.product_id = p.product_id ");
			sql.append("\n     AND t.ship_address_id = a.address_id ");
			sql.append("\n     AND a.purpose = 'S' ");
			
			sql.append("\n     UNION ALL ");
			
			sql.append("\n     /** EDI **/ ");
			sql.append("\n     SELECT t.picking_no");
			sql.append("\n     ,l.LINE_AMOUNT as total_amount ,(l.LINE_AMOUNT *0.07) as vat_amount,(l.LINE_AMOUNT+(l.LINE_AMOUNT *0.07)) as NET_AMOUNT");
			sql.append("\n     FROM pensso.T_EDI t ,pensso.T_EDI_LINE l");
			sql.append("\n     ,pensso.m_customer c,pensso.m_address a");
			sql.append("\n     ,pensso.m_product p ");
			sql.append("\n     WHERE t.HEADER_ID = l.HEADER_ID ");
			sql.append("\n     AND t.customer_id = c.customer_id ");
			sql.append("\n     AND c.customer_id = a.customer_id ");
			sql.append("\n     AND l.inventory_item_id = p.product_id ");
			sql.append("\n     AND t.ship_to_address_id = a.address_id ");
			sql.append("\n     AND a.purpose = 'S' ");
			sql.append("\n   )M ");
			sql.append("\n   group by M.picking_no ");
			sql.append("\n  )S ON S.picking_no = t.picking_no");
			
			sql.append("\n WHERE 1=1 ");
			if( !Utils.isNull(o.getPickingNo()).equals("")){
				sql.append("\n and t.picking_no = '"+Utils.isNull(o.getPickingNo())+"'");
			}
			if( !Utils.isNull(o.getTransactionDateFrom()).equals("") && !Utils.isNull(o.getTransactionDateTo()).equals("")){
				sql.append("\n and t.transaction_date >= to_date('"+DateUtil.convBuddhistToChristDate(o.getTransactionDateFrom(), DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/mm/yyyy')");
				sql.append("\n and t.transaction_date <= to_date('"+DateUtil.convBuddhistToChristDate(o.getTransactionDateTo(), DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/mm/yyyy')");
			}
			if(subPageName.equalsIgnoreCase("BudsConfPicking")){
				sql.append("\n and t.print_picking_count >=1 ");
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
			   h.setRegionCri(Utils.isNull(rst.getString("region")));
			   h.setInvoiceFlag(rst.getInt("invoice_count") > 0?"Y":"N");
			   //h = searchPickingSummary(conn, h,rst.getString("status"));
			   
			   h.setTotalAmount(Utils.decimalFormat(rst.getDouble("total_amount"),Utils.format_current_2_disgit));
			   h.setVatAmount(Utils.decimalFormat(rst.getDouble("vat_amount"),Utils.format_current_2_disgit));
			   h.setNetAmount(Utils.decimalFormat(rst.getDouble("net_amount"),Utils.format_current_2_disgit));
				
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
	public static void confirmPicking(ConfPickingBean o,User user) throws Exception{
		Connection conn = null;
		PreparedStatement ps = null;
		StringBuffer sql = new StringBuffer("");
		String pickingNo = "";
		try{
			conn = DBConnectionApps.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			if(Utils.isNull(o.getPickingNo()).equals("")){
			  //Gen pickingNo
			   pickingNo = genPickingNo(new Date());
			}else{
			   pickingNo = o.getPickingNo();
			   
			   logger.debug("--Update picking_no= null ,doc_status =RESERVE ** Case remove some area");
			   //Case Remove some area :update picking_no= null ,doc_status =RESERVE
			   //T_ORDER
			   sql = new StringBuffer("");
			   sql.append("\n update pensso.t_order set doc_status ='"+I_PO.STATUS_RESERVE+"' ,picking_no =null");
			   sql.append("\n ,updated =sysdate ,updated_by ="+user.getId());
			   sql.append("\n where picking_no ='"+pickingNo+"'");
			   logger.debug("sql:"+sql.toString());
			   ps = conn.prepareStatement(sql.toString());
			   ps.executeUpdate();
			   
			   //Case Remove some area :update picking_no= null ,doc_status =RESERVE
			   //T_EDI
			   sql = new StringBuffer("");
			   sql.append("\n update pensso.t_edi set doc_status =null ,picking_no =null");
			   sql.append("\n where picking_no ='"+pickingNo+"'");
			   logger.debug("sql:"+sql.toString());
			   ps = conn.prepareStatement(sql.toString());
			   ps.executeUpdate();
			}
			
			logger.debug("pickingNo:"+pickingNo);
			
			sql = new StringBuffer("");
			sql.append("\n update pensso.t_order set doc_status ='"+I_PO.STATUS_PICKING+"' ,picking_no ='"+pickingNo+"'");
			sql.append("\n ,updated =sysdate ,updated_by ="+user.getId());
			sql.append("\n where order_no in("+SQLHelper.converToTextSqlIn(o.getSelectedOrderNo())+") ");
			logger.debug("sql:"+sql.toString());
			
			ps = conn.prepareStatement(sql.toString());
			ps.executeUpdate();
			
			//update t_edi(MT from edi) doc_status ='ConfirmPicking'
			sql = new StringBuffer("");
			sql.append("\n update pensso.t_edi set doc_status ='"+I_PO.STATUS_PICKING+"' ,picking_no ='"+pickingNo+"'");
			sql.append("\n where CUST_PO_NUMBER in("+SQLHelper.converToTextSqlIn(o.getSelectedOrderNo())+") ");
			ps = conn.prepareStatement(sql.toString());
			ps.executeUpdate();
			
			//insert Picking History
			logger.debug("insert or update picking transaction");
			o.setPickingNo(pickingNo);
			o.setStatus(I_PO.STATUS_PICKING);//default
			int u= updatePickingTrans(conn, o);
			if(u==0){
				insertPickingTrans(conn, o);
			}
			
		}catch(Exception e){
			conn.rollback();
			throw e;
		} finally {
			try {
				ps.close();
				conn.close();
			} catch (Exception e) {}
		}
	}
	public static void addOrderManualPicking(ConfPickingBean o,User user) throws Exception{
		Connection conn = null;
		PreparedStatement ps = null;
		StringBuffer sql = new StringBuffer("");
		String pickingNo = "";
		try{
			conn = DBConnectionApps.getInstance().getConnection();
			conn.setAutoCommit(false);

			pickingNo = o.getPickingNo();
			logger.debug("pickingNo:"+pickingNo);
			
			sql = new StringBuffer("");
			sql.append("\n update pensso.t_order set doc_status ='"+I_PO.STATUS_PICKING+"' ,picking_no ='"+pickingNo+"'");
			sql.append("\n ,updated =sysdate ,updated_by ="+user.getId());
			sql.append("\n where order_no in("+SQLHelper.converToTextSqlIn(o.getSelectedOrderNo())+") ");
			logger.debug("sql:"+sql.toString());
			
			ps = conn.prepareStatement(sql.toString());
			ps.executeUpdate();
			
			//update t_edi(MT from edi) doc_status ='ConfirmPicking'
			sql = new StringBuffer("");
			sql.append("\n update pensso.t_edi set doc_status ='"+I_PO.STATUS_PICKING+"' ,picking_no ='"+pickingNo+"'");
			sql.append("\n where CUST_PO_NUMBER in("+SQLHelper.converToTextSqlIn(o.getSelectedOrderNo())+") ");
			ps = conn.prepareStatement(sql.toString());
			ps.executeUpdate();
			
			conn.commit();
		}catch(Exception e){
			conn.rollback();
			throw e;
		} finally {
			try {
				ps.close();
				conn.close();
			} catch (Exception e) {}
		}
	}
	public static void rejectPicking(ConfPickingBean o,User user) throws Exception{
		Connection conn = null;
		PreparedStatement ps = null;
		StringBuffer sql = new StringBuffer("");
		try{
			conn = DBConnectionApps.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			//update t_order doc_status ='ConfirmPicking'
			sql.append("\n update pensso.t_order set doc_status ='"+I_PO.STATUS_REJECT+"' ,picking_no =null ");
			sql.append("\n ,updated =sysdate ,updated_by ="+user.getId());
			sql.append("\n where doc_status in('"+I_PO.STATUS_PICKING+"')");
			sql.append("\n and order_no in("+SQLHelper.converToTextSqlIn(o.getSelectedOrderNo())+") ");
			logger.debug("sql:"+sql.toString());
			
			ps = conn.prepareStatement(sql.toString());
			ps.executeUpdate();
			
			//update t_order(MT from edi) doc_status ='ConfirmPicking'
			sql = new StringBuffer("");
			sql.append("\n update pensso.t_edi set doc_status ='"+I_PO.STATUS_REJECT+"' ,picking_no =null ");
			//sql.append("\n ,updated =sysdate ,updated_by ="+user.getId());
			sql.append("\n where doc_status in('"+I_PO.STATUS_PICKING+"')");
			sql.append("\n and cust_po_number in("+SQLHelper.converToTextSqlIn(o.getSelectedOrderNo())+") ");
			ps = conn.prepareStatement(sql.toString());
			ps.executeUpdate();
			
			conn.commit();
		}catch(Exception e){
			conn.rollback();
			throw e;
		} finally {
			try {
				ps.close();
				conn.close();
			} catch (Exception e) {}
		}
	}
	
	//Update STATUS :PICKING TO LOADING  -> Gen Order Temp
	public static void genInvoice(ConfPickingBean o,User user) throws Exception{
		Connection conn = null;
		PreparedStatement ps = null;
		StringBuffer sql = new StringBuffer("");
		try{
			conn = DBConnectionApps.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			//update t_order doc_status ='LOADING'
			sql.append("\n update pensso.t_order set doc_status ='"+I_PO.STATUS_LOADING+"' ");
			sql.append("\n ,updated =sysdate ,updated_by ="+user.getId());
			sql.append("\n where order_no in("+SQLHelper.converToTextSqlIn(o.getSelectedOrderNo())+") ");
			ps = conn.prepareStatement(sql.toString());
			ps.executeUpdate();
			
			//update t_edi(MT from edi) doc_status ='LOADING'
			sql = new StringBuffer("");
			sql.append("\n update pensso.t_edi set doc_status ='"+I_PO.STATUS_LOADING+"'");
			sql.append("\n where 1=1 ");
			sql.append("\n and CUST_PO_NUMBER in("+SQLHelper.converToTextSqlIn(o.getSelectedOrderNo())+") ");
			ps = conn.prepareStatement(sql.toString());
			ps.executeUpdate();
			
			//insert Picking History
			logger.debug("update picking transaction");
			o.setStatus(I_PO.STATUS_LOADING);//default
			updatePickingTrans(conn, o);

			conn.commit();
			
			//Call Proc Oracle Insert Order
			callProcInsertOrderTemp(o.getPickingNo());

		}catch(Exception e){
			conn.rollback();
			throw e;
		} finally {
			try {
				ps.close();
				conn.close();
			} catch (Exception e) {}
		}
	}
	public static boolean callProcInsertOrderTemp(String pickingNo) throws Exception{
		Connection conn = null;
		boolean r = true;
		CallableStatement  cs = null;
		StringBuffer sql = new StringBuffer("");
		logger.debug("callProcInsertOrderTemp PickingNo:"+pickingNo);
		try{
			conn = DBConnectionApps.getInstance().getConnection();
			
			sql.append("{ call  xxpens_om_sales_online_pkg.process_order(?) }");
			cs = conn.prepareCall(sql.toString());
			cs.setString(1, pickingNo);
			int u = cs.executeUpdate();
			logger.info("U:"+u);
			
			return r;
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}finally{
			cs.close();
			conn.close();
		}
	}
	// Format : YYMMxxxx ‡™Ëπ P63070001
	private static String genPickingNo(Date dateObj) throws Exception{
		String today = DateUtil.stringValue(dateObj, DateUtil.YYYY_MM_DD_WITH_SLASH,DateUtil.local_th);
		String[] d1 = today.split("/");
		String curYear = d1[0].substring(2,4);
		String curMonth = d1[1];
		BigDecimal nextVal = new SequenceProcessAll().getNextValue("PICKING_NO", dateObj);
		String pickingNo = "P"+curYear+curMonth+Utils.decimalFormat(nextVal.doubleValue(),"0000");
		return pickingNo;
	}
	
	public static ConfPickingBean searchPickingDetail(String mode,String subPageName,ConfPickingBean o,boolean excel,User user){
		ConfPickingBean item = null;
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		StringBuffer sql = new StringBuffer();
		StringBuffer html = null;
		int r = 0;
		double totalAmount = 0;
		double totalVatAmount = 0;
		double totalNetAmount = 0;
		String pickingNo = "";
		String status = "";
		try{
			//create connection
			conn = DBConnectionApps.getInstance().getConnection();
			
			if("ConfPicking".equalsIgnoreCase(subPageName)){
				sql = genSQLPicking(mode,o);
			}else if("ConfPickingAddOrderManual".equalsIgnoreCase(subPageName)){
				sql = genSQLPickingAddOrderManaual(mode, o);
			}else if("BudsConfPicking".equalsIgnoreCase(subPageName)){
				sql = genSQLBudConfirmPicking(o); 
			}
			//logger.debug("sql:"+sql);
			
			if(logger.isDebugEnabled()){
				FileUtil.writeFile("d://dev_temp//temp//sql.sql", sql.toString(),"TIS-620");
			}
			
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
			  item = new ConfPickingBean();
			  r++;
			  if(r==1){
       				 //gen Head Table
				 html = new StringBuffer("");
				 html.append(genHeadTable(subPageName,o,excel));
			  }
			  
			  item.setOrderDate(DateUtil.stringValue(rst.getDate("ORDER_DATE"), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
			  item.setOrderNo(Utils.isNull(rst.getString("order_no")));
			  item.setCustomerCode(Utils.isNull(rst.getString("customer_number")));
			  item.setCustomerName(Utils.isNull(rst.getString("customer_name")));
			  item.setAmphur(Utils.isNull(rst.getString("amphur")));
			  item.setProvince(Utils.isNull(rst.getString("province")));
			  item.setSalesrepCode(Utils.isNull(rst.getString("salesrep_code")));
			  
			  if( !"ConfPickingAddOrderManual".equalsIgnoreCase(subPageName)){
				item.setPickingNo(Utils.isNull(rst.getString("picking_no")));
			    item.setProductCode(Utils.isNull(rst.getString("product_code")));
			    item.setProductName(Utils.isNull(rst.getString("product_name")));
			    item.setUom(Utils.isNull(rst.getString("uom_id")));
			    item.setQty(Utils.decimalFormat(rst.getDouble("qty"),Utils.format_current_no_disgit));
			  }
			  item.setTotalAmount(Utils.decimalFormat(rst.getDouble("total_amount"),Utils.format_current_2_disgit));
			  item.setVatAmount(Utils.decimalFormat(rst.getDouble("vat_amount"),Utils.format_current_2_disgit));
			  item.setNetAmount(Utils.decimalFormat(rst.getDouble("net_amount"),Utils.format_current_2_disgit));
			  
			  totalAmount += rst.getDouble("total_amount");
			  totalVatAmount += rst.getDouble("vat_amount");
			  totalNetAmount += rst.getDouble("net_amount");
			  
			  if( !Utils.isNull(item.getPickingNo()).equals("")){
			     pickingNo =item.getPickingNo();
			  }
			  status = Utils.isNull(rst.getString("status"));
			  
			  //Gen Row Table
			  html.append(genRowTable(subPageName,o,excel,item,r));
			  
			}//while
	
			logger.debug("pickingNo:"+pickingNo);
			
			if( !"ConfPickingAddOrderManual".equalsIgnoreCase(subPageName)){
				//check can action btn
				if( !Utils.isNull(pickingNo).equals("")){
					if(r>0 && !I_PO.STATUS_LOADING.equalsIgnoreCase(status)){//dataSearch
					   o.setCanPrintPicking(true);
					   o.setCanFinish(true);
					   o.setCanReject(true);
					   o.setCanAddOrderManual(true);
					}else if(r>0 && I_PO.STATUS_LOADING.equalsIgnoreCase(status)){
					   o.setCanPrintPicking(false);
					   o.setCanPrintLoading(true);
					}
				}else{
					if(r>0){
						o.setCanConfirm(true);
					}
				}
			}
			//Check Execute Found data
			if(r>0){
			   // Get Total
			   o.setRowTotalStrBuffer(genRowTotal(subPageName,o,excel,totalAmount,totalVatAmount,totalNetAmount));
			   // gen end Table
			   html.append("<tbody> \n");
			   html.append("</table>");
			}
			logger.debug("subPageName["+subPageName+"]Record Found:"+r+",canConfirm:"+o.isCanConfirm());
			
		  //o.setItemsList(itemList);
		  o.setDataStrBuffer(html);
		  o.setStatus(status);
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
			
			if(ControlCode.canExecuteMethod("Picking", "OrderEDI")){
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
				sql.append("\n    AND t.ship_to_address_id = a.address_id ");
				sql.append("\n    AND a.purpose = 'S' ");
				sql.append("\n    AND t.doc_status in('"+status+"')" );
				sql.append("\n    AND t.picking_no ='"+o.getPickingNo()+"'" );
			}
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
		PopupBean product = null;
		PopupBean criProduct = null;
		//UOMConversion uc1 =null , uc2 = null;
		try{
			//create connection
			conn = DBConnectionApps.getInstance().getConnection();
			//get PriceList Id
			int pricelistId = new MPriceList().getCurrentPriceList(conn,"CR").getId();
			
			sql = genSQLReportPicking(o);
			
			//wait for GetConversion from SQL 
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
			  item.setAlternateName(Utils.isNull(rst.getString("alternate_name")));
			  item.setAmphur(Utils.isNull(rst.getString("amphur"))+"/"+Utils.isNull(rst.getString("province")));
			 // item.setSalesrepCode(Utils.isNull(rst.getString("salesrep_code")));
			  if("".equals(Utils.isNull(rst.getString("subbrand"))) ){
				 item.setSubBrand("NO_SUBBRAND");
				 item.setSubBrandName("NO_SUBBRAND");
			  }else{
			    item.setSubBrand(Utils.isNull(rst.getString("subbrand")));
			    item.setSubBrandName(Utils.isNull(rst.getString("subbrand_desc")));
			  }
			  item.setProductCode(Utils.isNull(rst.getString("product_code")));
			  item.setProductName(Utils.isNull(rst.getString("product_code"))+"-"+Utils.isNull(rst.getString("product_name")));
			
			  //main input
			  item.setUom(Utils.isNull(rst.getString("uom_id")));
			  item.setQtyInt(rst.getInt("qty"));
			  item.setQty(rst.getInt("qty")+"");
			  
		      //Get Product Info
			  criProduct = new PopupBean();
			  criProduct.setCodeSearch(item.getProductCode());
			  product = getProduct(conn, criProduct,pricelistId);
			  if(product != null){
				 item.setUom1(product.getUom1());  
				 item.setUom2(product.getUom2());  
				 
				 //uc1 = new MUOMConversion().getCurrentConversion(conn,Utils.convertStrToInt(product.getProductId()), product.getUom1());//default to CTN
				// uc2 = new MUOMConversion().getCurrentConversion(conn,Utils.convertStrToInt(product.getProductId()), product.getUom2());

				 //check line uom is uom1 or uom2
				 if(item.getUom().equalsIgnoreCase(item.getUom1())){//main uom
					//calc contain
				    if(    (product.getUom1ConversionRate() >0) 
				    	&& (product.getUom2ConversionRate() >0) ){
				    	item.setUom2Contain(Utils.decimalFormat(new Double(1*product.getUom1ConversionRate()),Utils.format_current_no_disgit));
				    	item.setSubQty(item.getQtyInt()*product.getUom1ConversionRate());
				    	if(item.getUom().equals(item.getUom1())){
				    		item.setPriQty(item.getQtyInt());
				    	}else{
				    		//calc pri qty uom2 to uom1
				    		item.setPriQty(Utils.convertStrToDouble(StockUtilsDAO.convertSecQtyToPriQty(conn, product.getProductId(), item.getUom1(), item.getUom2(), ""+item.getQtyInt())));
				    	}
				    }else{
				        // TUB/ no conversion
				    	item.setUom2Contain(Utils.decimalFormat(new Double(item.getQtyInt()*1),Utils.format_current_no_disgit));
				    	item.setSubQty(item.getQtyInt()*1);
				    	item.setUom2(item.getUom1());
				    	item.setPriQty(item.getQtyInt());
				    }
				 }else{
					 //uom = uom2 (sub uom)
					 if(   (product.getUom1ConversionRate() >0) 
				    	&& (product.getUom2ConversionRate() >0) ){
				    	item.setUom2Contain(Utils.decimalFormat(new Double(1*product.getUom1ConversionRate()),Utils.format_current_no_disgit));
				    	item.setSubQty(item.getQtyInt());
				    	if(item.getUom().equals(item.getUom1())){
				    		item.setPriQty(item.getQtyInt());
				    	}else{
				    		//calc pri qty uom2 to uom1
				    		item.setPriQty(Utils.convertStrToDouble(StockUtilsDAO.convertSecQtyToPriQty(conn, product.getProductId(), item.getUom1(), item.getUom2(), ""+item.getQtyInt())));
				    	}
				     }else{
				        // TUB/ no conversion
				    	item.setUom2Contain(Utils.decimalFormat(new Double(item.getQtyInt()*1),Utils.format_current_no_disgit));
				    	item.setSubQty(item.getQtyInt()*1);
				    	item.setUom2(item.getUom1());
				    	item.setPriQty(item.getQtyInt());
				    }
				 }
			  }//if
			  logger.debug("productCode["+item.getProductCode()+"]subbrand["+item.getSubBrand()+"]");
			  logger.debug("uom2Contain["+item.getUom2Contain()+"]");
			  logger.debug("uom["+item.getUom()+"]qtyInt["+item.getQtyInt()+"]");
			  logger.debug("uom1["+item.getUom1()+"]uom2["+item.getUom2()+"]");
			  logger.debug("subQty["+item.getSubQty()+"]priQty["+item.getPriQty()+"]");
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
	
	private static StringBuffer genSQLPicking(String mode,ConfPickingBean o) throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append("\n /** genSQLPicking **/ ");
		
		sql.append("\n  SELECT M.*");
		sql.append("\n  FROM (");
		sql.append("\n    /** SALES_APP **/ ");
		sql.append("\n    SELECT t.picking_no, t.ORDER_NO ,t.ORDER_DATE ,t.doc_status as status");
		sql.append("\n    ,c.code as customer_number ,c.name as CUSTOMER_NAME");
		sql.append("\n    ,(select name from pensso.m_district ad where ad.district_id = a.district_id) as amphur");
		sql.append("\n    ,(select name from pensso.m_province ad where ad.province_id = a.province_id) as province");
		sql.append("\n    ,(select code from pensso.ad_user ad where ad.user_id = t.user_id) as salesrep_code");
		sql.append("\n    ,p.name as product_name ,p.code as product_code ,l.uom_id ,l.qty");
		sql.append("\n    ,l.total_amount ,l.vat_amount,(l.total_amount+l.vat_amount)as NET_AMOUNT");
		sql.append("\n    FROM pensso.t_order t ,pensso.t_order_line l");
		sql.append("\n    ,pensso.m_customer c,pensso.m_product p ");
		sql.append("\n    ,pensso.m_address a ");
		sql.append("\n    WHERE t.order_id = l.order_id ");
		sql.append("\n    AND t.customer_id = c.customer_id ");
		sql.append("\n    AND c.customer_id = a.customer_id ");
		sql.append("\n    AND l.product_id = p.product_id ");
		sql.append("\n    AND t.ship_address_id = a.address_id ");
		sql.append("\n    AND a.purpose = 'S' ");
		/** Case Pre_order_flag by customer no get order_date <= sysdate **/
		sql.append("\n    AND t.order_date <= sysdate ");
		
		if( !"view".equalsIgnoreCase(mode)){
			if( !Utils.isNull(o.getPickingNo()).equals("")){
				sql.append("\n  and t.PICKING_NO ='"+o.getPickingNo()+"'");
			}else{
		       sql.append("\n    AND t.doc_status in('"+I_PO.STATUS_RESERVE+"','"+I_PO.STATUS_REJECT+"')" );
			}
		}else{
			//mode view
			if( !Utils.isNull(o.getPickingNo()).equals("")){
				sql.append("\n  and t.PICKING_NO ='"+o.getPickingNo()+"'");
			}
		}
		if(ControlCode.canExecuteMethod("Picking", "OrderEDI")){
			sql.append("\n    UNION ALL ");
			
			sql.append("\n    /** EDI **/ ");
			sql.append("\n    SELECT t.picking_no, t.CUST_PO_NUMBER as ORDER_NO ,t.ORDERED_DATE as ORDER_DATE ,t.doc_status as status");
			sql.append("\n    ,c.code as customer_number ,c.name as CUSTOMER_NAME");
			sql.append("\n    ,(select name from pensso.m_district ad where ad.district_id = a.district_id) as amphur");
			sql.append("\n    ,(select name from pensso.m_province ad where ad.province_id = a.province_id) as province");
			sql.append("\n    ,(select code from apps.xxpens_salesreps_v ad where ad.salesrep_id = t.salesrep_id) as salesrep_code");
			sql.append("\n    ,p.name as product_name ,p.code as product_code ,l.ORDER_QUANTITY_UOM as uom_id ,l.ORDERED_QUANTITY as qty");
			sql.append("\n    ,l.LINE_AMOUNT as total_amount ,(l.LINE_AMOUNT *0.07) as vat_amount,(l.LINE_AMOUNT+(l.LINE_AMOUNT *0.07)) as NET_AMOUNT");
			sql.append("\n    FROM pensso.T_EDI t ,pensso.T_EDI_LINE l");
			sql.append("\n    ,pensso.m_customer c,pensso.m_address a");
			sql.append("\n    ,pensso.m_product p ");
			sql.append("\n    WHERE t.HEADER_ID = l.HEADER_ID ");
			sql.append("\n    AND t.customer_id = c.customer_id ");
			sql.append("\n    AND c.customer_id = a.customer_id ");
			sql.append("\n    AND l.inventory_item_id = p.product_id ");
			sql.append("\n    AND t.ship_to_address_id = a.address_id ");
			sql.append("\n    AND a.purpose = 'S' ");
			if( !"view".equalsIgnoreCase(mode)){
				if( !Utils.isNull(o.getPickingNo()).equals("")){
				   sql.append("\n    AND t.PICKING_NO ='"+o.getPickingNo()+"'");
				}else{
				   sql.append("\n    AND t.doc_status in('"+I_PO.STATUS_RESERVE+"','"+I_PO.STATUS_REJECT+"')" );
				}
			}else{
				//mode view
				if( !Utils.isNull(o.getPickingNo()).equals("")){
					sql.append("\n  AND t.PICKING_NO ='"+o.getPickingNo()+"'");
				}
			}
		}
		sql.append("\n )M ");
		sql.append("\n where 1=1");
		if( !"view".equalsIgnoreCase(mode)){
			if( Utils.isNull(o.getPickingNo()).equals("")){
				String provinceCri = o.getProvinceCri().substring(0,o.getProvinceCri().length()-1);
				sql.append("\n and  M.province in ("+SQLHelper.converToTextSqlIn(provinceCri)+")");
				
				if( !Utils.isNull(o.getAmphurCri()).equals("")){
					String amphurCri = o.getAmphurCri().substring(0,o.getAmphurCri().length()-1);
					sql.append("\n and  M.amphur in ("+SQLHelper.converToTextSqlIn(amphurCri)+")");
				}
			}else{
				sql.append("\n  and M.PICKING_NO ='"+o.getPickingNo()+"'");
			}
		}else{
			//Mode view
			if( !Utils.isNull(o.getPickingNo()).equals("")){
				sql.append("\n  and M.PICKING_NO ='"+o.getPickingNo()+"'");
			}
		}
		sql.append("\n ORDER BY M.order_no,M.order_date ");
		
		return sql;
	}
	
	
	private static StringBuffer genSQLPickingAddOrderManaual(String mode,ConfPickingBean o) throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append("\n /** genSQLPickingAddOrderManaual **/ ");
		sql.append("\n SELECT M.order_no,M.order_date,status");
		sql.append("\n ,customer_number,customer_name,amphur,province,salesrep_code ");
		sql.append("\n ,SUM(M.total_amount) as total_amount");
		sql.append("\n ,SUM(M.vat_amount) as vat_amount");
		sql.append("\n ,SUM(M.net_amount) as net_amount");
		sql.append("\n  FROM (");
		sql.append("\n    /** SALES_APP **/ ");
		sql.append("\n    SELECT t.ORDER_NO ,t.ORDER_DATE ,t.doc_status as status");
		sql.append("\n    ,c.code as customer_number ,c.name as CUSTOMER_NAME");
		sql.append("\n    ,(select name from pensso.m_district ad where ad.district_id = a.district_id) as amphur");
		sql.append("\n    ,(select name from pensso.m_province ad where ad.province_id = a.province_id) as province");
		sql.append("\n    ,(select code from pensso.ad_user ad where ad.user_id = t.user_id) as salesrep_code");
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
		sql.append("\n    AND t.doc_status in('"+I_PO.STATUS_RESERVE+"','"+I_PO.STATUS_REJECT+"')" );
		sql.append("\n    AND (t.picking_no is null or t.picking_no ='')" );
		/** Case Pre_order_flag by customer no get order_date <= sysdate **/
		sql.append("\n    AND t.order_date <= sysdate ");
		
		if(ControlCode.canExecuteMethod("Picking", "OrderEDI")){
			sql.append("\n    UNION ALL ");
			
			sql.append("\n    /** EDI **/ ");
			sql.append("\n    SELECT t.CUST_PO_NUMBER as ORDER_NO ,t.ORDERED_DATE as ORDER_DATE ,t.doc_status as status");
			sql.append("\n    ,c.code as customer_number ,c.name as CUSTOMER_NAME");
			sql.append("\n    ,(select name from pensso.m_district ad where ad.district_id = a.district_id) as amphur");
			sql.append("\n    ,(select name from pensso.m_province ad where ad.province_id = a.province_id) as province");
			sql.append("\n    ,(select code from apps.xxpens_salesreps_v ad where ad.salesrep_id = t.salesrep_id) as salesrep_code");
			sql.append("\n    ,p.name as product_name ,p.code as product_code ,l.ORDER_QUANTITY_UOM as uom_id ,l.ORDERED_QUANTITY as qty");
			sql.append("\n    ,l.LINE_AMOUNT as total_amount ,(l.LINE_AMOUNT *0.07) as vat_amount");
			sql.append("\n    ,(l.LINE_AMOUNT+(l.LINE_AMOUNT *0.07)) as NET_AMOUNT");
			sql.append("\n    FROM pensso.T_EDI t ,pensso.T_EDI_LINE l");
			sql.append("\n    ,pensso.m_customer c,pensso.m_address a");
			sql.append("\n    ,pensso.m_product p ");
			sql.append("\n    WHERE t.HEADER_ID = l.HEADER_ID ");
			sql.append("\n    AND t.customer_id = c.customer_id ");
			sql.append("\n    AND c.customer_id = a.customer_id ");
			sql.append("\n    AND l.inventory_item_id = p.product_id ");
			sql.append("\n    AND t.ship_to_address_id = a.address_id ");
			sql.append("\n    AND a.purpose = 'S' ");
			sql.append("\n    AND (t.doc_status is null ");
			sql.append("\n        OR t.doc_status in('"+I_PO.STATUS_RESERVE+"','"+I_PO.STATUS_REJECT+"')" );
			sql.append("\n    )");
			sql.append("\n    AND (t.picking_no is null or t.picking_no ='')" );
		}
		sql.append("\n )M ");
		sql.append("\n where 1=1");
		
		String provinceCri = o.getProvinceCri().substring(0,o.getProvinceCri().length()-1);
		sql.append("\n and  M.province in ("+SQLHelper.converToTextSqlIn(provinceCri)+")");
		if( !Utils.isNull(o.getAmphurCri()).equals("")){
			String amphurCri = o.getAmphurCri().substring(0,o.getAmphurCri().length()-1);
			sql.append("\n and  M.amphur in ("+SQLHelper.converToTextSqlIn(amphurCri)+")");
		}
		sql.append("\n GROUP BY M.order_no,M.order_date,status,customer_number,customer_name,amphur,province ,salesrep_code");
		sql.append("\n ORDER BY M.order_no,M.order_date ");
		
		return sql;
	}
	private static StringBuffer genSQLReportPicking(ConfPickingBean o) throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append("\n /** genSQLReportPicking **/ ");
		sql.append("\n  SELECT M.*");
		sql.append("\n  FROM (");
		sql.append("\n    /** SALES_APP **/ ");
		sql.append("\n    SELECT t.picking_no, t.ORDER_NO ,t.ORDER_DATE ,t.doc_status as status");
		sql.append("\n    ,c.code as customer_number ,c.name as CUSTOMER_NAME");
		sql.append("\n    ,(select name from pensso.m_district ad where ad.district_id = a.district_id) as amphur");
		sql.append("\n    ,(select name from pensso.m_province ad where ad.province_id = a.province_id) as province");
		sql.append("\n    ,(select code from pensso.ad_user ad where ad.user_id = t.user_id) as salesrep_code");
		sql.append("\n    ,(select subbrand_no from PENSBI.XXPENS_BI_MST_SUBBRAND ad where ad.inventory_item_id = l.product_id) as subbrand");
		sql.append("\n    ,(select subbrand_desc from PENSBI.XXPENS_BI_MST_SUBBRAND ad where ad.inventory_item_id = l.product_id) as subbrand_desc");
		sql.append("\n    ,p.name as product_name ,p.code as product_code ,l.uom_id ,l.qty ,a.alternate_name");
		sql.append("\n    FROM pensso.t_order t ,pensso.t_order_line l");
		sql.append("\n    ,pensso.m_customer c,pensso.m_address a");
		sql.append("\n    ,pensso.m_product p ");
		sql.append("\n    WHERE t.order_id = l.order_id ");
		sql.append("\n    AND t.customer_id = c.customer_id ");
		sql.append("\n    AND c.customer_id = a.customer_id ");
		sql.append("\n    AND l.product_id = p.product_id ");
		sql.append("\n    AND t.ship_address_id = a.address_id ");
		sql.append("\n    AND a.purpose = 'S' ");
		sql.append("\n    and t.PICKING_NO ='"+o.getPickingNo()+"'");
		
		sql.append("\n    UNION ALL ");
		
		sql.append("\n    /** EDI **/ ");
		sql.append("\n    SELECT t.picking_no, t.CUST_PO_NUMBER as ORDER_NO ,t.ORDERED_DATE as ORDER_DATE ,t.doc_status as status");
		sql.append("\n    ,c.code as customer_number ,c.name as CUSTOMER_NAME");
		sql.append("\n    ,(select name from pensso.m_district ad where ad.district_id = a.district_id) as amphur");
		sql.append("\n    ,(select name from pensso.m_province ad where ad.province_id = a.province_id) as province");
		sql.append("\n    ,(select code from apps.xxpens_salesreps_v ad where ad.salesrep_id = t.salesrep_id) as salesrep_code");
		sql.append("\n    ,(select subbrand_no from PENSBI.XXPENS_BI_MST_SUBBRAND ad where ad.inventory_item_id = l.inventory_item_id) as subbrand");
		sql.append("\n    ,(select subbrand_desc from PENSBI.XXPENS_BI_MST_SUBBRAND ad where ad.inventory_item_id = l.inventory_item_id) as subbrand_desc");
		sql.append("\n    ,p.name as product_name ,p.code as product_code ,l.ORDER_QUANTITY_UOM as uom_id ,l.ORDERED_QUANTITY as qty,a.alternate_name");
		sql.append("\n    FROM pensso.T_EDI t ,pensso.T_EDI_LINE l");
		sql.append("\n    ,pensso.m_customer c,pensso.m_address a");
		sql.append("\n    ,pensso.m_product p ");
		sql.append("\n    WHERE t.HEADER_ID = l.HEADER_ID ");
		sql.append("\n    AND t.customer_id = c.customer_id ");
		sql.append("\n    AND c.customer_id = a.customer_id ");
		sql.append("\n    AND l.inventory_item_id = p.product_id ");
		sql.append("\n    AND t.ship_to_address_id = a.address_id ");
		sql.append("\n    AND a.purpose = 'S' ");
		sql.append("\n    and t.PICKING_NO ='"+o.getPickingNo()+"'");
		sql.append("\n )M ");
		sql.append("\n where 1=1");
		sql.append("\n and M.PICKING_NO ='"+o.getPickingNo()+"'");
		sql.append("\n ORDER BY M.subbrand,M.product_code ");
		
		return sql;
	}
	
	private static StringBuffer genSQLBudConfirmPicking(ConfPickingBean o) throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append("\n /** genSQLBudConfirmPicking **/ ");
		sql.append("\n  SELECT M.*");
		sql.append("\n  FROM (");
		sql.append("\n    SELECT t.picking_no, t.ORDER_NO ,t.ORDER_DATE,t.doc_status as status");
		sql.append("\n    ,c.code as customer_number ,c.name as CUSTOMER_NAME");
		sql.append("\n    ,(select name from pensso.m_district ad where ad.district_id = a.district_id) as amphur");
		sql.append("\n    ,(select name from pensso.m_province ad where ad.province_id = a.province_id) as province");
		sql.append("\n    ,(select code from pensso.ad_user ad where ad.user_id = t.user_id) as salesrep_code");
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
		sql.append("\n    AND t.doc_status in('"+I_PO.STATUS_PICKING+"','"+I_PO.STATUS_LOADING+"')");
		if( !Utils.isNull(o.getPickingNo()).equals("")){
			sql.append("\n   and t.PICKING_NO ='"+o.getPickingNo()+"'");
		}
		
		if(ControlCode.canExecuteMethod("Picking", "OrderEDI")){
	        sql.append("\n    UNION ALL ");
			
			sql.append("\n    /** EDI **/ ");
			sql.append("\n    SELECT t.picking_no, t.CUST_PO_NUMBER as ORDER_NO ,t.ORDERED_DATE as ORDER_DATE ,t.doc_status as status");
			sql.append("\n    ,c.code as customer_number ,c.name as CUSTOMER_NAME");
			sql.append("\n    ,(select name from pensso.m_district ad where ad.district_id = a.district_id) as amphur");
			sql.append("\n    ,(select name from pensso.m_province ad where ad.province_id = a.province_id) as province");
			sql.append("\n    ,(select code from apps.xxpens_salesreps_v ad where ad.salesrep_id = t.salesrep_id) as salesrep_code");
			sql.append("\n    ,p.name as product_name ,p.code as product_code ,l.ORDER_QUANTITY_UOM as uom_id ,l.ORDERED_QUANTITY as qty");
			sql.append("\n    ,NVL(l.line_amount,0) as total_amount ,NVL((l.line_amount*0.07),0) as vat_amount");
			sql.append("\n    ,(NVL(l.line_amount,0) + NVL((l.line_amount*0.07),0) ) as net_amount ");
			sql.append("\n    FROM pensso.T_EDI t ,pensso.T_EDI_LINE l");
			sql.append("\n    ,pensso.m_customer c,pensso.m_address a");
			sql.append("\n    ,pensso.m_product p ");
			sql.append("\n    WHERE t.HEADER_ID = l.HEADER_ID ");
			sql.append("\n    AND t.customer_id = c.customer_id ");
			sql.append("\n    AND c.customer_id = a.customer_id ");
			sql.append("\n    AND l.inventory_item_id = p.product_id ");
			sql.append("\n    AND t.ship_to_address_id = a.address_id ");
			sql.append("\n    AND a.purpose = 'S' ");
			sql.append("\n    AND t.doc_status in('"+I_PO.STATUS_PICKING+"','"+I_PO.STATUS_LOADING+"')");
			if( !Utils.isNull(o.getPickingNo()).equals("")){
				sql.append("\n   and t.PICKING_NO ='"+o.getPickingNo()+"'");
			}
		}
		sql.append("\n )M ");
		sql.append("\n where 1=1");
		sql.append("\n ORDER BY M.order_no,M.order_date ");
		
		return sql;
	}
	private static StringBuffer genHeadTable(String subPageName,ConfPickingBean head,boolean excel) throws Exception{
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
		if( !"ConfPickingAddOrderManual".equalsIgnoreCase(subPageName)){
			if("ConfPicking".equalsIgnoreCase(subPageName)){
			   h.append("<th></th> \n");
			}
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
		}else{
			h.append("<th></th> \n");
			h.append("<th>Order No / PO No</th> \n");
			h.append("<th>Order Date / PO Date</th> \n");
			h.append("<th>Customer No</th> \n");
			h.append("<th>Customer Name</th> \n");
			h.append("<th>Õ”‡¿Õ / ®—ßÀ«—¥</th> \n");
			h.append("<th>‡´≈≈Ï</th> \n");
			h.append("<th>Amount (Ex.vat)</th> \n");
			h.append("<th>Vat</th> \n");
			h.append("<th>Amount (In.vat)</th> \n");
		}
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
	private static StringBuffer genRowTable(String subPageName,ConfPickingBean head,boolean excel,ConfPickingBean item,int lineNo) throws Exception{
		StringBuffer h = new StringBuffer("");
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
		if( !"ConfPickingAddOrderManual".equalsIgnoreCase(subPageName)){
			if("ConfPicking".equalsIgnoreCase(subPageName)){
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
			}else{
				h.append("<td class='"+classNameCenter+"' width='8%'>"+item.getOrderNo());
		        h.append("<td class='"+classNameCenter+"' width='8%'>"+item.getOrderDate()+"</td> \n");
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
			}
		}else{
			h.append("<td class='"+classNameCenter+"' width='10%'>");
			h.append(" <input type='checkbox' name='chkOrder' value='"+item.getOrderNo()+"'/>");
			h.append("</td> \n");
			h.append("<td class='"+classNameCenter+"' width='10%'>"+item.getOrderNo());
	        h.append("<td class='"+classNameCenter+"' width='10%'>"+item.getOrderDate()+"</td> \n");
	        h.append("  <input type='hidden' name='orderNo' value='"+item.getOrderNo()+"'/>");
	        h.append("</td> \n");
			h.append("<td class='"+classNameCenter+"' width='10%'>"+item.getCustomerCode()+"</td> \n");
			h.append("<td class='"+className+"' width='15%'>"+item.getCustomerName()+"</td> \n");
			h.append("<td class='"+className+"' width='15%'>"+item.getAmphur()+"/"+item.getProvince()+"</td> \n");
			h.append("<td class='"+classNameCenter+"' width='9%'>"+item.getSalesrepCode()+"</td> \n");
	
			h.append("<td class='"+classNameNumber+"' width='7%'>"+item.getTotalAmount()+"</td> \n");
			h.append("<td class='"+classNameNumber+"' width='7%'>"+item.getVatAmount()+"</td> \n");
			h.append("<td class='"+classNameNumber+"' width='7%'>"+item.getNetAmount()+"</td> \n");
		}
		h.append("</tr> \n");
		return h;
	}
	private static StringBuffer genRowTotal(String subPageName,ConfPickingBean head,boolean excel,double totalAmount,double totalVatAmount,double totalNetAmount) throws Exception{
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
		if( !"ConfPickingAddOrderManual".equalsIgnoreCase(subPageName)){
			h.append("<table id='tblSummary' align='center' border='"+border+"' width='"+width+"%' cellpadding='3' cellspacing='1' class='tableSearchNoWidth'> \n");
			h.append("<tr class='hilight_text'> \n");
			if("ConfPicking".equalsIgnoreCase(subPageName)){
			   h.append("<td  colspan='11' width='78%' align='right'>Total</td> \n");
			}else{
			   h.append("<td  colspan='10' width='78%' align='right'>Total</td> \n");
			}
			h.append("<td class='"+classNameNumber+"' width='7%'>"+Utils.decimalFormat(totalAmount,Utils.format_current_2_disgit)+"</td> \n");
			h.append("<td class='"+classNameNumber+"' width='7%'>"+Utils.decimalFormat(totalVatAmount,Utils.format_current_2_disgit)+"</td> \n");
			h.append("<td class='"+classNameNumber+"' width='8%'>"+Utils.decimalFormat(totalNetAmount,Utils.format_current_2_disgit)+"</td> \n");
			h.append("</tr> \n");
			h.append("</table> \n");
		}
		return h;
	}
	
	public static List<PopupBean> searchPickingNoList(PopupBean mCriteria)
			throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		PopupBean m = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		List<PopupBean> dataList = new ArrayList<PopupBean>();
		try {
			conn = new DBCPConnectionProvider().getConnection(conn);
			sql.append("\n  SELECT t.picking_no,t.transaction_date ");
			sql.append("\n  FROM pensso.t_picking_trans t");
			sql.append("\n  WHERE 1=1");
			if( !Utils.isNull(mCriteria.getCodeSearch()).equals("")){
				sql.append("\n and t.picking_no like '%"+Utils.isNull(mCriteria.getCodeSearch())+"%'");
			}
			sql.append("\n  ORDER BY t.picking_no desc"); 
			logger.debug("sql:" + sql);

			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
				m = new PopupBean();
				m.setCode(Utils.isNull(rst.getString("picking_no")));
				m.setDesc(DateUtil.stringValue(rst.getDate("transaction_date"),DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
				dataList.add(m);
				
			}// while
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
				if (conn != null) {
					conn.close();
					conn = null;
				}
			} catch (Exception e) {
			}
		}
		return dataList;
	}
	public static ConfPickingBean searchPickingTrans(String pickingNo)throws Exception{
		Connection conn = null;
		try{
			conn = DBConnectionApps.getInstance().getConnection();
			return searchPickingTransModel(conn, pickingNo);
		}catch(Exception e){
			throw e;
		}finally{
			if(conn != null){
				conn.close();conn = null;
			}
		}
	}
	
	public static ConfPickingBean searchPickingTransModel(Connection conn,String pickingNo)
			throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		ConfPickingBean p = null;
		StringBuilder sql = new StringBuilder();
		try {
			sql.append("\n  SELECT t.* FROM pensso.t_picking_trans t");
			sql.append("\n  WHERE t.picking_no ='"+pickingNo+"'");
			logger.debug("sql:" + sql);

			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if (rst.next()) {
				p = new ConfPickingBean();
				p.setPickingNo(Utils.isNull(rst.getString("picking_no")));
				p.setTransactionDate(DateUtil.stringValue(rst.getDate("TRANSACTION_DATE"), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
				p.setRegionCri(Utils.isNull(rst.getString("region")));
				p.setProvinceCri(Utils.isNull(rst.getString("province")));
				p.setAmphurCri(Utils.isNull(rst.getString("amphur")));
			}
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {
			}
		}
		return p;
	}
	
	/**update print count **/
	public static int updatePrintCountPickingTrans(Connection conn,ConfPickingBean o,String printType) throws Exception{
		PreparedStatement ps = null;
		StringBuffer sql = new StringBuffer("");
		try{
			sql.append("\n update pensso.t_picking_trans ");
			if("PrintPicking".equalsIgnoreCase(printType)){
			   sql.append("\n set print_picking_count=(nvl(print_picking_count,0)+1)");
			}else{
			   sql.append("\n set print_loading_count=(nvl(print_loading_count,0)+1)");
			}
			sql.append("\n ,update_date=?,update_user=?");
			sql.append("\n where picking_no =? ");
			
			ps = conn.prepareStatement(sql.toString());
			ps.setDate(1, new java.sql.Date(new Date().getTime()));
			ps.setString(2, o.getUserName());
			ps.setString(3, o.getPickingNo());
			return ps.executeUpdate();
		}catch(Exception e){
			throw e;
		} finally {
			try {
				ps.close();
			} catch (Exception e) {}
		}
	}
	
	public static int updatePickingTrans(Connection conn,ConfPickingBean o) throws Exception{
		PreparedStatement ps = null;
		StringBuffer sql = new StringBuffer("");
		try{
			sql.append("\n update pensso.t_picking_trans set region=?,province=?,amphur=?,update_date=?,update_user=?,status=?");
			sql.append("\n where picking_no =? ");
			ps = conn.prepareStatement(sql.toString());
		
			ps.setString(1, o.getRegionCri());
			ps.setString(2, o.getProvinceCri());
			ps.setString(3, o.getAmphurCri());
			ps.setDate(4, new java.sql.Date(new Date().getTime()));
			ps.setString(5, o.getUserName());
			ps.setString(6, Utils.isNull(o.getStatus()));
			ps.setString(7, o.getPickingNo());
			return ps.executeUpdate();
		}catch(Exception e){
			throw e;
		} finally {
			try {
				ps.close();
			} catch (Exception e) {}
		}
	}
	public static void insertPickingTrans(Connection conn,ConfPickingBean o) throws Exception{
		PreparedStatement ps = null;
		StringBuffer sql = new StringBuffer("");
		try{
			sql.append("\n insert into pensso.t_picking_trans(picking_no,region,province"
					+ ",amphur,create_date,create_user,status,transaction_date)");
			sql.append("\n values(?,?,?,?,?,?,?,?) ");
			ps = conn.prepareStatement(sql.toString());
			ps.setString(1, o.getPickingNo());
			ps.setString(2, o.getRegionCri());
			ps.setString(3, o.getProvinceCri());
			ps.setString(4, o.getAmphurCri());
			ps.setDate(5, new java.sql.Date(new Date().getTime()));
			ps.setString(6, o.getUserName());
			ps.setString(7, Utils.isNull(o.getStatus()));
			ps.setDate(8, new java.sql.Date(new Date().getTime()));
			ps.executeUpdate();
		}catch(Exception e){
			throw e;
		} finally {
			try {
				ps.close();
			} catch (Exception e) {}
		}
	}
	
	public static PopupBean getProduct(Connection conn,PopupBean c,int pricelistId) throws Exception {
		PopupBean item = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuilder sql = new StringBuilder();
		try {
			
			sql.append("\n SELECT A.* FROM( ");
			sql.append("\n SELECT pd.PRODUCT_ID , pd.NAME as PRODUCT_NAME , pd.CODE as PRODUCT_CODE ");
			sql.append("\n ,pp1.PRICE as PRICE1 , pp1.UOM_ID as UOM1 ,pp2.PRICE as PRICE2 , pp2.UOM_ID as UOM2 ");
			sql.append("\n ,cv1.CONVERSION_RATE as UOM1_CONVERSION_RATE ,cv2.CONVERSION_RATE as UOM2_CONVERSION_RATE ");
			sql.append("\n ,pd.taxable ");
			sql.append("\n FROM PENSSO.M_Product pd ");
			sql.append("\n INNER JOIN PENSSO.M_Product_Price pp1 ON pd.Product_ID = pp1.Product_ID AND pp1.UOM_ID = pd.UOM_ID ");
			sql.append("\n LEFT JOIN PENSSO.m_product_price pp2 ON pp2.PRODUCT_ID = pd.PRODUCT_ID ");
			sql.append("\n AND pp2.PRICELIST_ID = pp1.PRICELIST_ID AND pp2.ISACTIVE = 'Y' AND pp2.UOM_ID <> pd.UOM_ID ");
			//uom1 conversion
			sql.append("\n LEFT JOIN pensso.m_uom_conversion cv1 ON cv1.PRODUCT_ID = pd.PRODUCT_ID ");
			sql.append("\n and cv1.uom_id = pp1.uom_id");
			sql.append("\n and (cv1.disable_date is null or cv1.disable_date >= trunc(sysdate)) ");
			//uom2 conversion
			sql.append("\n LEFT JOIN pensso.m_uom_conversion cv2 ON cv2.PRODUCT_ID = pd.PRODUCT_ID ");
			sql.append("\n and cv2.uom_id = pp2.uom_id");
			sql.append("\n and (cv2.disable_date is null or cv2.disable_date >= trunc(sysdate)) ");
			
			sql.append("\n WHERE pp1.ISACTIVE = 'Y' AND pd.CODE = '"+c.getCodeSearch()+"' AND pp1.PRICELIST_ID = "+pricelistId+" ");
			sql.append("\n AND ( ");
			sql.append("\n    pp1.UOM_ID IN ( ");
			sql.append("\n      SELECT UOM_ID FROM PENSSO.M_UOM_CONVERSION con WHERE con.PRODUCT_ID = pd.PRODUCT_ID AND COALESCE(con.DISABLE_DATE,sysdate) >= sysdate ");
			sql.append("\n     ) ");
			sql.append("\n     OR");
			sql.append("\n     pp2.UOM_ID IN ( ");
			sql.append("\n        SELECT UOM_ID FROM PENSSO.M_UOM_CONVERSION con WHERE con.PRODUCT_ID = pd.PRODUCT_ID AND COALESCE(con.DISABLE_DATE,sysdate) >= sysdate ");
			sql.append("\n      ) ");
			sql.append("\n   )");
	        sql.append("\n  AND pd.CODE NOT IN (SELECT DISTINCT CODE FROM PENSSO.M_PRODUCT_UNUSED ) ");
	        sql.append("\n )A");
			
	        logger.debug("sql:"+sql);
	        
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				item = new PopupBean();
				item.setProductId(Utils.isNull(rs.getString("product_id")));
				item.setCode(Utils.isNull(rs.getString("product_code")));
				item.setDesc(Utils.isNull(rs.getString("product_name")));
				item.setUom1(Utils.isNull(rs.getString("uom1")));
				item.setUom2(Utils.isNull(rs.getString("uom2")));
				item.setUom1ConversionRate(rs.getDouble("UOM1_CONVERSION_RATE"));
				item.setUom2ConversionRate(rs.getDouble("UOM2_CONVERSION_RATE"));
				//item.setPrice(p.getPrice1()+"/"+p.getPrice2());
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				ps.close();
				rs.close();
			} catch (Exception e) {}
		}
		return item;
	}
}
