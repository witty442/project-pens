package com.isecinc.pens.web.buds.page;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.isecinc.core.model.I_PO;
import com.isecinc.pens.bean.OrderEDIBean;
import com.isecinc.pens.bean.User;
import com.pens.util.DBConnection;
import com.pens.util.DBConnectionApps;
import com.pens.util.DateUtil;
import com.pens.util.SQLHelper;
import com.pens.util.Utils;

public class OrderEDIDAO {
	/** Logger */
	public static Logger logger = Logger.getLogger("PENS");

	public static int searchTotalHead(Connection conn ,User user,String subPageName,OrderEDIBean o) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int totalRec = 0;
		try {
			sql.append("\n select count(*) as c from(");
			sql.append("\n    SELECT distinct t.cust_po_number ");
			sql.append("\n    FROM pensso.t_edi t");
			sql.append("\n    ,pensso.m_customer c ");
			sql.append("\n    ,(select a.customer_id,a.address_id,a.line1,a.line2,a.line3 ,a.postal_code,a.alternate_name ");
			sql.append("\n      ,(select name from pensso.m_district ad where ad.district_id = a.district_id) as amphur ");
			sql.append("\n      ,(select name from pensso.m_province ad where ad.province_id = a.province_id) as province ");
			sql.append("\n      from pensso.m_address a where a.purpose ='S'");
			sql.append("\n     ) ship_address");
			sql.append("\n    WHERE 1=1");
			sql.append("\n    AND t.customer_id = c.customer_id");
			sql.append("\n    AND c.customer_id = ship_address.customer_id ");
			sql.append("\n    AND t.ship_to_site_use_id = ship_address.address_id ");
		
			if( !user.getUserName().equalsIgnoreCase("admin")){
				sql.append("\n    AND t.salesrep_id = "+user.getId());
			}
			if( !Utils.isNull(o.getPickingNo()).equals("")){
				sql.append("\n and t.picking_no = '"+Utils.isNull(o.getPickingNo())+"'");
			}
			if( !Utils.isNull(o.getOrderNo()).equals("")){
				sql.append("\n and t.cust_po_number = '"+Utils.isNull(o.getOrderNo())+"'");
			}
			if( !Utils.isNull(o.getCustomerCode()).equals("")){
				sql.append("\n and c.code like '%"+Utils.isNull(o.getCustomerCode())+"%'");
			}
			if( !Utils.isNull(o.getCustomerName()).equals("")){
				sql.append("\n and c.name like '%"+Utils.isNull(o.getCustomerName())+"%'");
			}
			if( !Utils.isNull(o.getOrderDateFrom()).equals("") &&  !Utils.isNull(o.getOrderDateTo()).equals("")){
				sql.append("\n and t.ordered_date >= to_date('"+DateUtil.convBuddhistToChristDate(o.getOrderDateFrom(), DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/mm/yyyy')");
				sql.append("\n and t.ordered_date <= to_date('"+DateUtil.convBuddhistToChristDate(o.getOrderDateTo(), DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/mm/yyyy')");
			}else{
				if( !Utils.isNull(o.getOrderDateFrom()).equals("")){
				  sql.append("\n and t.ordered_date = to_date('"+DateUtil.convBuddhistToChristDate(o.getOrderDateFrom(), DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/mm/yyyy')");
				}
				if( !Utils.isNull(o.getOrderDateTo()).equals("")){
				  sql.append("\n and t.ordered_date = to_date('"+DateUtil.convBuddhistToChristDate(o.getOrderDateTo(), DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/mm/yyyy')");
				}
			}
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
	public static OrderEDIBean searchHead(User user,String subPageName,OrderEDIBean o ,boolean allRec ,int currPage,int pageSize ) throws Exception {
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			return searchHead(conn,user, subPageName, o, allRec, currPage, pageSize);
		}catch(Exception e){
			throw e;
		}finally{
			if(conn != null){
				conn.close();
			}
		}
	}
	
	public static OrderEDIBean searchHead(Connection conn ,User user,String subPageName,OrderEDIBean o ,boolean allRec ,int currPage,int pageSize) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		OrderEDIBean h = null;
		List<OrderEDIBean> items = new ArrayList<OrderEDIBean>();
		int r = 1;
		try {
			sql.append("\n select M.* from (");
			sql.append("\n select A.* ,rownum as r__ from (");
			sql.append("\n    SELECT t.header_id, t.picking_no ,t.cust_po_number,t.ordered_date ,t.doc_status");
			sql.append("\n    ,(select name from pensso.c_reference r where r.code='DocStatus' and r.value=t.doc_status) as doc_status_desc");
			sql.append("\n    ,nvl(sum(l.line_amount),0) as total_amount ,nvl((sum(l.line_amount) *0.07),0) as vat_amount ");
			sql.append("\n    ,(nvl(sum(l.line_amount),0)+nvl((sum(l.line_amount) *0.07),0) )as net_amount ");
			sql.append("\n    ,c.code as customer_code ,c.name as customer_name");
			
			sql.append("\n    ,ship_address.line1 as s_line1,ship_address.line2 as s_line2,ship_address.line3 as s_line3");
			sql.append("\n    ,ship_address.amphur as s_amphur,ship_address.province as s_province");
			sql.append("\n    ,ship_address.postal_code as s_postal_code ,ship_address.alternate_name as s_alternate_name ");

			sql.append("\n    FROM pensso.t_edi t ,pensso.t_edi_line l");
			sql.append("\n    ,pensso.m_customer c ");
			sql.append("\n    ,(select a.customer_id,a.address_id,a.line1,a.line2,a.line3 ,a.postal_code,a.alternate_name ");
			sql.append("\n      ,(select name from pensso.m_district ad where ad.district_id = a.district_id) as amphur ");
			sql.append("\n      ,(select name from pensso.m_province ad where ad.province_id = a.province_id) as province ");
			sql.append("\n      from pensso.m_address a where a.purpose ='S'");
			sql.append("\n     ) ship_address");
			sql.append("\n    WHERE t.header_id = l.header_id");
			sql.append("\n    AND t.customer_id = c.customer_id");
			sql.append("\n    AND c.customer_id = ship_address.customer_id ");
			sql.append("\n    AND t.ship_to_address_id = ship_address.address_id ");
			
			if( !user.getUserName().equalsIgnoreCase("admin")){
				sql.append("\n    AND t.salesrep_id = "+user.getId());
			}
			if( !Utils.isNull(o.getPickingNo()).equals("")){
				sql.append("\n and t.picking_no = '"+Utils.isNull(o.getPickingNo())+"'");
			}
			if( !Utils.isNull(o.getOrderNo()).equals("")){
				sql.append("\n and t.cust_po_number = '"+Utils.isNull(o.getOrderNo())+"'");
			}
			if( !Utils.isNull(o.getCustomerCode()).equals("")){
				sql.append("\n and c.code like '%"+Utils.isNull(o.getCustomerCode())+"%'");
			}
			if( !Utils.isNull(o.getCustomerName()).equals("")){
				sql.append("\n and c.name like '%"+Utils.isNull(o.getCustomerName())+"%'");
			}
			if( !Utils.isNull(o.getOrderDateFrom()).equals("") &&  !Utils.isNull(o.getOrderDateTo()).equals("")){
				sql.append("\n and t.ordered_date >= to_date('"+DateUtil.convBuddhistToChristDate(o.getOrderDateFrom(), DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/mm/yyyy')");
				sql.append("\n and t.ordered_date <= to_date('"+DateUtil.convBuddhistToChristDate(o.getOrderDateTo(), DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/mm/yyyy')");
			}else{
				if( !Utils.isNull(o.getOrderDateFrom()).equals("")){
				  sql.append("\n and t.ordered_date = to_date('"+DateUtil.convBuddhistToChristDate(o.getOrderDateFrom(), DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/mm/yyyy')");
				}
				if( !Utils.isNull(o.getOrderDateTo()).equals("")){
				  sql.append("\n and t.ordered_date = to_date('"+DateUtil.convBuddhistToChristDate(o.getOrderDateTo(), DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/mm/yyyy')");
				}
			}
			sql.append("\n    group by t.header_id, t.picking_no ,t.cust_po_number,t.ordered_date ,t.doc_status,c.code,c.name");
			sql.append("\n    ,ship_address.line1 ,ship_address.line2 ,ship_address.line3 ");
			sql.append("\n    ,ship_address.amphur,ship_address.province");
			sql.append("\n    ,ship_address.postal_code,ship_address.alternate_name");
			sql.append("\n   order by t.cust_po_number desc ");
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
			   h = new OrderEDIBean();
			   h.setHeaderId(rst.getLong("header_id"));
			   h.setPickingNo(Utils.isNull(rst.getString("picking_no")));
			   h.setOrderNo(Utils.isNull(rst.getString("cust_po_number")));
			   h.setDocStatus(Utils.isNull(rst.getString("doc_status")));
			   h.setDocStatusDesc(Utils.isNull(rst.getString("doc_status_desc")));
			   h.setCustomerCode(Utils.isNull(rst.getString("customer_code")));
			   h.setCustomerName(Utils.isNull(rst.getString("customer_name")));
			   h.setOrderDate(DateUtil.stringValue(rst.getDate("ordered_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
			   h.setShipToAddress(getLineAddress("s", rst));
			   h.setTotalAmount(Utils.decimalFormat(rst.getDouble("total_amount"),Utils.format_current_2_disgit));
			   h.setVatAmount(Utils.decimalFormat(rst.getDouble("vat_amount"),Utils.format_current_2_disgit));
			   h.setNetAmount(Utils.decimalFormat(rst.getDouble("net_amount"),Utils.format_current_2_disgit));
			   //check can edit
		       if(     !Utils.isNull(h.getDocStatus()).equals(I_PO.STATUS_CANCEL)
		        	&& !Utils.isNull(h.getDocStatus()).equals(I_PO.STATUS_PICKING)	
		        	&& !Utils.isNull(h.getDocStatus()).equals(I_PO.STATUS_LOADING) ){
		        	h.setCanEdit(true);
		        }
			   
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
	
	public static OrderEDIBean searchOrderEDIDetail(String orderNo,Map<String, String> productErrorMap)throws Exception{
		Connection conn = null;
		try{
			conn = DBConnectionApps.getInstance().getConnection();
			return searchOrderEDIDetailModel(conn, orderNo,productErrorMap);
		}catch(Exception e){
			throw e;
		}finally{
			if(conn != null){
				conn.close();conn = null;
			}
		}
	}
	
	public static OrderEDIBean searchOrderEDIDetailModel(Connection conn,String orderNo,Map<String, String> productErrorMap)
			throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		OrderEDIBean p = null ;
		OrderEDIBean order = new OrderEDIBean();
		StringBuilder sql = new StringBuilder();
		List<OrderEDIBean> itemsList = new ArrayList<OrderEDIBean>();
		int no=0;
		try {
			sql.append("\n  SELECT l.* ,p.code ,p.name ");
			sql.append("\n  ,h.cust_po_number ,h.ordered_date,h.doc_status,h.picking_no");
			sql.append("\n  ,h.header_id");
			sql.append("\n  FROM pensso.t_edi h ,pensso.t_edi_line l");
			sql.append("\n  ,pensso.m_product p ");
			sql.append("\n  WHERE h.header_id = l.header_id");
			sql.append("\n  and l.inventory_item_id = p.product_id");
			sql.append("\n  AND h.cust_po_number ='"+orderNo+"'");
			logger.debug("sql:" + sql);

			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while (rst.next()) {
				no++;
				p = new OrderEDIBean();
				//head
				p.setHeaderId(rst.getLong("header_id"));
				p.setPickingNo(Utils.isNull(rst.getString("picking_no")));
				p.setOrderNo(Utils.isNull(rst.getString("cust_po_number")));
				p.setDocStatus(Utils.isNull(rst.getString("doc_status")));
				p.setOrderDate(DateUtil.stringValue(rst.getDate("ordered_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
				
				//detail
				p.setReservationId(rst.getLong("reservation_Id"));
				p.setNo(no);
				p.setLineId(rst.getString("line_number"));
				p.setProductId(Utils.isNull(rst.getString("inventory_item_id")));
				p.setProductCode(Utils.isNull(rst.getString("code")));
				p.setProductName(Utils.isNull(rst.getString("name")));
				p.setUom(Utils.isNull(rst.getString("order_quantity_uom")));
				p.setQty(Utils.decimalFormat(rst.getDouble("ordered_quantity"),Utils.format_number_no_disgit));
				p.setUnitPrice(Utils.decimalFormat(rst.getDouble("unit_price"),Utils.format_current_2_disgit));
				p.setLineAmount(Utils.decimalFormat(rst.getDouble("line_amount"),Utils.format_current_2_disgit));
				
				//Case Display Row Product Error cannot reserve stock onhand
				if(productErrorMap != null && productErrorMap.get(p.getProductId()) != null){
					p.setRowStyle("lineError2");
				}
				
				itemsList.add(p);
			}
		
			order.setItemsList(itemsList);
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();
			} catch (Exception e) {
			}
		}
		return order;
	}
	
	public static long saveOrderEDI(Connection conn,OrderEDIBean h) throws Exception{
		try{
		   return saveOrderEDIModel(conn, h);
		}catch(Exception e){
			throw e;
		}
	}
	
	public static long saveOrderEDI(OrderEDIBean h) throws Exception{
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
		   return saveOrderEDIModel(conn, h);
		}catch(Exception e){
			throw e;
		}finally{
			if(conn !=null){
				conn.close();conn=null;
			}
		}
	}
	public static long saveOrderEDIModel(Connection conn,OrderEDIBean h) throws Exception{
		long id = 0;
		try{
			conn.setAutoCommit(false);
			
			//update head
			updateOrderEDIModel(conn, h);
			
            //delete line by lineIds
    	    deleteOrderEDILineModel(conn, h);
  
            if(h.getItemsList() != null && h.getItemsList().size()>0){
 			   for(int i=0;i<h.getItemsList().size();i++){
 				  OrderEDIBean l = (OrderEDIBean)h.getItemsList().get(i);
 				   l.setHeaderId(h.getHeaderId());
 				   l.setUserName(h.getUserName());
 				   
 				   if( !l.getStatus().equals("SUCCESS")){
 					  updateOrderEDILineModel(conn, l);
 				   }
 			   }//for
 			 }
			conn.commit();
			return id;
		}catch(Exception e){
		  conn.rollback();
		  throw e;
		}finally{
			
		}
	}
	public static void updateOrderEDIModel(OrderEDIBean o) throws Exception{
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			updateOrderEDIModel(conn, o);
		}catch(Exception e){
			throw e;
		}finally{
			if(conn != null){
				conn.close();
			}
		}
	}
	
	public static void updateOrderEDIModel(Connection conn,OrderEDIBean o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("updateOrderEDIModel");
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" update pensso.t_edi \n");
			sql.append(" set doc_status = '"+Utils.isNull(o.getDocStatus())+"' \n");
			sql.append(" where header_Id="+o.getHeaderId() +"\n");
		    logger.debug("sql:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			ps.executeUpdate();
			
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}
	public static void updateOrderEDILineModel(Connection conn,OrderEDIBean o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("updateOrderEDILineModel");
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" update pensso.t_edi_line \n");
			sql.append(" set ordered_quantity = "+o.getQty()+" ,line_amount ="+Utils.convertStrToDouble(o.getLineAmount())+"\n");
			sql.append(" where header_Id="+o.getHeaderId() +"\n");
		    sql.append(" and line_number="+o.getLineId() +"\n");
		    logger.debug("sql:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			ps.executeUpdate();
			
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}
	
	public static void deleteOrderEDILineModel(Connection conn,OrderEDIBean o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("deleteOrderEDILineModel by lineIds");
		try{
			StringBuffer sql = new StringBuffer("");
			if( !Utils.isNull(o.getDeleteLineIds()).equals("")){
				sql.append(" delete from pensso.t_edi_line where header_id ="+o.getHeaderId()+" \n");
				sql.append(" and line_number in("+SQLHelper.converToTextSqlIn(o.getDeleteLineIds())+") \n");
				
				logger.debug("sql :"+sql.toString());
				ps = conn.prepareStatement(sql.toString());
				ps.executeUpdate();
			}
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}
	
	public static String getLineAddress( String purpose,ResultSet rs) throws Exception{
		String lineString;
		lineString = "";
		if( !Utils.isNull(rs.getString(purpose+"_alternate_name")).equals("")){
		   lineString = "("+Utils.isNull(rs.getString(purpose+"_alternate_name"))+") ";
		}
		lineString += Utils.isNull(rs.getString(purpose+"_line1")) + " ";
		lineString += Utils.isNull(rs.getString(purpose+"_line2")) + " ";
		if ("กรุงเทพฯ".equalsIgnoreCase(rs.getString(purpose+"_province"))
				|| "กรุงเทพมหานคร".equalsIgnoreCase(rs.getString(purpose+"_province"))) {
			//lineString += "แขวง";
			lineString += (rs.getString(purpose+"_line3")) + " ";
			//lineString += "เขต";
			lineString += (rs.getString(purpose+"_amphur")) + " ";
			lineString += "";
		} else {
			lineString += "ตำบล";
			lineString += (rs.getString(purpose+"_line3")) + " ";
			lineString += "อำเภอ";
			lineString += (rs.getString(purpose+"_amphur")) + " ";
			lineString += "จังหวัด";
		}
		lineString += (rs.getString(purpose+"_province")) + " ";
		lineString += Utils.isNull(rs.getString(purpose+"_postal_code"));
		return lineString;
	}
}
