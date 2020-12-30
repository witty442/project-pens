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
import com.pens.util.seq.SequenceProcessAll;

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
			/** Record Type **/
			if( !Utils.isNull(o.getrType()).equals("")){
				 sql.append("\n and t.r_type ='"+Utils.isNull(o.getrType())+"'");
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
			sql.append("\n    ,t.delivery_date ");
			sql.append("\n    ,(select name from pensso.c_reference r where r.code='DocStatus' and r.value=t.doc_status) as doc_status_desc");
			sql.append("\n    ,nvl(sum(l.line_amount),0) as total_amount ,nvl((sum(l.line_amount) *0.07),0) as vat_amount ");
			sql.append("\n    ,(nvl(sum(l.line_amount),0)+nvl((sum(l.line_amount) *0.07),0) )as net_amount ");
			sql.append("\n    ,c.customer_id,c.code as customer_code ,c.name as customer_name");
			
			sql.append("\n    ,ship_address.address_id as ship_to_address_id ");
			sql.append("\n    ,ship_address.line1 as s_line1,ship_address.line2 as s_line2,ship_address.line3 as s_line3");
			sql.append("\n    ,ship_address.amphur as s_amphur,ship_address.province as s_province");
			sql.append("\n    ,ship_address.postal_code as s_postal_code ,ship_address.alternate_name as s_alternate_name ");
			sql.append("\n    ,t.r_type");
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
			
			/** Record Type **/
			if( !Utils.isNull(o.getrType()).equals("")){
				 sql.append("\n and t.r_type ='"+Utils.isNull(o.getrType())+"'");
			}
			sql.append("\n    group by t.header_id, t.picking_no ,t.cust_po_number,t.ordered_date ,t.doc_status,c.code,c.name");
			sql.append("\n    ,ship_address.line1 ,ship_address.line2 ,ship_address.line3 ");
			sql.append("\n    ,ship_address.amphur,ship_address.province");
			sql.append("\n    ,ship_address.postal_code,ship_address.alternate_name");
			sql.append("\n    ,t.r_type,ship_address.address_id ,c.customer_id ,t.delivery_date ");
			
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
			   h.setCustomerId(rst.getLong("customer_id"));
			   h.setCustomerCode(Utils.isNull(rst.getString("customer_code")));
			   h.setCustomerName(Utils.isNull(rst.getString("customer_name")));
			   h.setOrderDate(DateUtil.stringValue(rst.getDate("ordered_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
			   h.setDeliveryDate(DateUtil.stringValue(rst.getDate("delivery_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
			   h.setShipToAddress(getLineAddress("s", rst));
			   h.setShipToAddressId(rst.getString("ship_to_address_id"));
			   h.setTotalAmount(Utils.decimalFormat(rst.getDouble("total_amount"),Utils.format_current_2_disgit));
			   h.setVatAmount(Utils.decimalFormat(rst.getDouble("vat_amount"),Utils.format_current_2_disgit));
			   h.setNetAmount(Utils.decimalFormat(rst.getDouble("net_amount"),Utils.format_current_2_disgit));
			   h.setrType(Utils.isNull(rst.getString("r_type")));//record type(EDI MANUAL=M) blank = EDI(normal)
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
			sql.append("\n , (select max(bar_ea) from apps.xxpens_om_item_mst_v b where b.segment1 = p.code) barcode ");
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
				p.setBarcode(Utils.isNull(rst.getString("barcode")));
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
	
	public static long saveOrderEDIManual(OrderEDIBean h) throws Exception{
		Connection conn = null;
		long headerId = 0;
		try{
			conn = DBConnectionApps.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			//EDI SO
		    headerId = saveOrderEDIManualModel(conn, h);
		    
		    //EDI TEMP ORACLE
		    h.setHeaderId(headerId);
		    saveOrderEDITempOracleManualModel(conn,h);
		    
		    conn.commit();
		    return headerId;
		}catch(Exception e){
			conn.rollback();
			throw e;
		}finally{
			if(conn !=null){
				conn.close();conn=null;
			}
		}
	}
	
	public static long saveOrderEDITempOracleManual(OrderEDIBean h) throws Exception{
		Connection conn = null;
		long headerId = 0;
		try{
			conn = DBConnectionApps.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			//EDI SO
		    headerId = saveOrderEDIManualModel(conn, h);
		    
		    //EDI TEMP ORACLE
		    h.setHeaderId(headerId);
		    saveOrderEDITempOracleManualModel(conn,h);
		    
		    conn.commit();
		    return headerId;
		}catch(Exception e){
			conn.rollback();
			throw e;
		}finally{
			if(conn !=null){
				conn.close();conn=null;
			}
		}
	}
	
	public static void deleteOrderEDITempOracleManual(Connection conn,OrderEDIBean h) throws Exception{
		try{
			//delete header
			deleteOrderEDIManualTempOracleModel(conn, h);
			//delete line
		    deleteOrderEDILineManualTempOracleModel(conn, h);
		}catch(Exception e){
			throw e;
		}
			
	}
	
	public static long saveOrderEDIManualModel(Connection conn,OrderEDIBean h) throws Exception{
		long headerId = 0;
		int lineId = 0;
		try{
			
			if(h.getHeaderId()==0){
				//Get Next Seq
				headerId = SequenceProcessAll.getIns().getNextValueBySeqDBApp("apps.xxpens_edi_order_header_seq.nextval").longValue();
			    h.setHeaderId(headerId);
			    
				//insert head
				insertOrderEDIManaulModel(conn, h);
			}else{
			    //update head
			    updateOrderEDIManualModel(conn, h);
			    headerId = h.getHeaderId();
			}
			
            //delete line by lineIds
    	    deleteOrderEDILineModel(conn, h);
            
            if(h.getItemsList() != null && h.getItemsList().size()>0){
            	//get MaxLineId by headerId
               lineId = getMaxLineIdOrderEDILineModel(conn, h);
               
 			   for(int i=0;i<h.getItemsList().size();i++){
 				  OrderEDIBean l = (OrderEDIBean)h.getItemsList().get(i);
 				   l.setHeaderId(h.getHeaderId());
 				   l.setOrderDate(h.getOrderDate());
 				   l.setUserName(h.getUserName());
 				   
 				   if( !l.getStatus().equals("SUCCESS")){
 					   if(Utils.convertToInt(l.getLineId())==0){
 						  lineId++;
 						  l.setLineId(String.valueOf(lineId));
 						  insertOrderEDILineModel(conn, l);
 					   }else{
 					       updateOrderEDILineModel(conn, l);
 					   }
 				   }
 			   }//for
 			 }
			return headerId;
		}catch(Exception e){
		  throw e;
		}
	}
	
	public static long saveOrderEDITempOracleManualModel(Connection conn,OrderEDIBean h) throws Exception{
		long headerId = 0;
		int lineId = 0;
		try{
			int headerIdExist = getHeaderIdOrderEDITempOracleModel(conn, h);
			if(headerIdExist==0){
				//insert head
				insertOrderEDITempOracleManaulModel(conn, h);
			}else{
			    //update head
			    updateOrderEDITempOracleModel(conn, h);
			    headerId = h.getHeaderId();
			}
			  
            if(h.getItemsList() != null && h.getItemsList().size()>0){
            	
              //delete line by headerId
               deleteOrderEDILineManualTempOracleModel(conn, h);
               
 			   for(int i=0;i<h.getItemsList().size();i++){
 				  OrderEDIBean l = (OrderEDIBean)h.getItemsList().get(i);
 				   l.setHeaderId(h.getHeaderId());
 				   l.setOrderNo(h.getOrderNo());
 				   l.setUserName(h.getUserName());
 				   
 				   if( !l.getStatus().equals("SUCCESS")){
 						lineId++;
 						l.setLineId(String.valueOf(lineId));
 						insertOrderEDILineTemOracleModel(conn, l);

 				   }
 			   }//for
 			 }
			return headerId;
		}catch(Exception e){
		  throw e;
		}
	}
	
	public static void updateOrderEDIModel(OrderEDIBean o) throws Exception{
		Connection conn = null;
		try{
			conn = DBConnectionApps.getInstance().getConnection();
			conn.setAutoCommit(false);
			updateOrderEDIModel(conn, o);
			
			//case OrderEDI Manual delete EDI TEMP Oracle 
			logger.debug("RType:"+o.getrType());
			if("M".equalsIgnoreCase(o.getrType())){
				deleteOrderEDITempOracleManual(conn, o);
			}
			conn.commit();
		}catch(Exception e){
			conn.rollback();
			throw e;
		}finally{
			if(conn != null){
				conn.close();
			}
		}
	}
	public static void insertOrderEDIManaulModel(Connection conn,OrderEDIBean o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("insertOrderEDIModel");
		int index =0;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append("INSERT INTO PENSSO.T_EDI \n");
			sql.append("(HEADER_ID, CUST_PO_NUMBER, ORDERED_DATE, ORDER_TYPE_ID,  \n");
			sql.append("CUSTOMER_ID, BILL_TO_SITE_USE_ID, SHIP_TO_SITE_USE_ID, SALESREP_ID,  \n");
			sql.append("ORDER_SOURCE_ID, PRICE_LIST_ID, TRANSACTIONAL_CURR_CODE, PRICING_DATE,  \n");
			sql.append("REQUEST_DATE, FLOW_STATUS_CODE, ORIG_SYS_DOCUMENT_REF, PAYMENT_TERM_ID, \n"); 
			sql.append("DOC_STATUS, EXPORTED, BILL_TO_ADDRESS_ID, SHIP_TO_ADDRESS_ID, R_TYPE,DELIVERY_DATE) \n");//22
			sql.append("VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) \n");
		    logger.debug("sql:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			ps.setLong(++index, o.getHeaderId());
			ps.setString(++index, o.getOrderNo());
			ps.setDate(++index, new java.sql.Date(DateUtil.parse(o.getOrderDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th).getTime()));
			ps.setLong(++index,1);//
			ps.setLong(++index,o.getCustomerId());
			ps.setLong(++index,o.getBillToSiteUseId());
			ps.setLong(++index,o.getShipToSiteUseId());
			ps.setLong(++index, o.getUserId());
			ps.setLong(++index, 1001);//ORDER_SOURCE_ID
			ps.setLong(++index, 10012);//PRICE_LIST_ID
			ps.setString(++index, "THB");//TRANSACTIONAL_CURR_CODE
			ps.setDate(++index, new java.sql.Date(DateUtil.parse(o.getOrderDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th).getTime()));//PRICING_DATE
			ps.setDate(++index, new java.sql.Date(DateUtil.parse(o.getOrderDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th).getTime()));//REQUEST_DATE
			ps.setString(++index, "ENTERED");//FLOW_STATUS_CODE
			ps.setString(++index, o.getOrderNo());//ORIG_SYS_DOCUMENT_REF
			ps.setLong(++index, 1);//PAYMENT_TERM_ID
			ps.setString(++index, "");//DOC_STATUS
			ps.setString(++index, "N");//EXPORTED
			ps.setLong(++index,Utils.convertToLong(o.getBillToAddressId()));//BILL_TO_ADDRESS_ID
			ps.setLong(++index,Utils.convertToLong(o.getShipToAddressId()));//SHIP_TO_ADDRESS_ID
			ps.setString(++index, o.getrType());
			ps.setDate(++index, new java.sql.Date(DateUtil.parse(o.getOrderDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th).getTime()));
			ps.executeUpdate();
			
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}
	public static void updateOrderEDIManualModel(Connection conn,OrderEDIBean o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("updateOrderEDIManualModel");
		int index = 0;
		try{	
			StringBuffer sql = new StringBuffer("");
			sql.append(" UPDATE PENSSO.T_EDI SET \n");
			sql.append(" cust_po_number = ? , ORDERED_DATE = ? , delivery_date = ? , \n");
			sql.append(" CUSTOMER_ID=?, BILL_TO_SITE_USE_ID=?, SHIP_TO_SITE_USE_ID=? , \n");
			sql.append(" BILL_TO_ADDRESS_ID=? , SHIP_TO_ADDRESS_ID= ?, \n");
			sql.append(" doc_status = '"+Utils.isNull(o.getDocStatus())+"' \n");
			sql.append(" where header_Id="+o.getHeaderId() +"\n");
		    logger.debug("sql:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			
			ps.setString(++index, o.getOrderNo());
			ps.setDate(++index, new java.sql.Date(DateUtil.parse(o.getOrderDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th).getTime()));
			ps.setDate(++index, new java.sql.Date(DateUtil.parse(o.getDeliveryDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th).getTime()));
			ps.setLong(++index,o.getCustomerId());
			ps.setLong(++index,o.getBillToSiteUseId());
			ps.setLong(++index,o.getShipToSiteUseId());
			ps.setLong(++index,Utils.convertToLong(o.getBillToAddressId()));//BILL_TO_ADDRESS_ID
			ps.setLong(++index,Utils.convertToLong(o.getShipToAddressId()));//SHIP_TO_ADDRESS_ID
			
			ps.executeUpdate();
			
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
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
	
	public static void insertOrderEDITempOracleManaulModel(Connection conn,OrderEDIBean o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("insertOrderEDITempOracleManaulModel");
		int index =0;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append("INSERT INTO APPS.xxpens_edi_order_header_temp \n");
			sql.append("(PO_NO, ASN_CODE, PO_DATE, DELIVERY_DATE, CANCEL_DATE, \n"); 
			sql.append(" NET_AMOUNT, TAX_AMOUNT, TOTAL_AMOUNT, SPECIAL_DISCOUNT_AMOUNT,  INTFLAG , \n");
			sql.append(" INTMESSAGE, CUSTOMER_ID, SHIP_TO_SITE_USE_ID, BILL_TO_SITE_USE_ID, SALESREP_ID, \n"); 
			sql.append(" PRICELIST_ID, ORG_ID, ORDER_TYPE_ID, TERM_ID, HEADER_ID,ship_to_ean_loc_code,ORDER_SOURCE) \n");//21
			sql.append("VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) \n");
		    logger.debug("sql:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			ps.setString(++index, o.getOrderNo());
			ps.setString(++index, "");//ASN_CODE
			ps.setString(++index, DateUtil.stringValue(DateUtil.parse(o.getOrderDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th),DateUtil.YYYY_MM_DD_WITHOUT_SLASH));
			ps.setString(++index, DateUtil.stringValue(DateUtil.parse(o.getDeliveryDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th),DateUtil.YYYY_MM_DD_WITHOUT_SLASH));
			ps.setString(++index, "");//CANCEL_DATE
			ps.setDouble(++index,Utils.convertStrToDouble(o.getNetAmount()));
			ps.setDouble(++index,Utils.convertStrToDouble(o.getVatAmount()));
			ps.setDouble(++index,Utils.convertStrToDouble(o.getTotalAmount()));
			ps.setDouble(++index,0);//SPECIAL_DISCOUNT_AMOUNT
			ps.setString(++index, "P");//INTFLAG
			ps.setString(++index, "Generate from Sales online");//INTMESSAGE
			ps.setLong(++index,o.getCustomerId());
			ps.setLong(++index,o.getShipToSiteUseId());
			ps.setLong(++index,o.getBillToSiteUseId());
			ps.setLong(++index, o.getUserId());
			ps.setLong(++index, 10012);//PRICE_LIST_ID
			ps.setString(++index, "");//ORG_ID
			ps.setString(++index, "1044");//ORDER_TYPE_ID
			ps.setString(++index, "1000");//TERM_ID
			ps.setLong(++index, o.getHeaderId());
			ps.setString(++index, o.getShipToEanLocCode());//ship_to_ean_loc_code  get from m_address
			ps.setString(++index, "PENS BUDS");//ORDER_SOURCE
			
			ps.executeUpdate();
			
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}
	
	public static void updateOrderEDITempOracleModel(Connection conn,OrderEDIBean o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("updateOrderEDITempOracleModel");
		int index = 0;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" update APPS.xxpens_edi_order_header_temp set \n");
			sql.append(" PO_NO = ? ,PO_DATE =? ,DELIVERY_DATE = ? , \n");
			sql.append(" CUSTOMER_ID = ?, SHIP_TO_SITE_USE_ID=?, BILL_TO_SITE_USE_ID =? , \n");
			sql.append(" NET_AMOUNT= ? ,TAX_AMOUNT =? ,TOTAL_AMOUNT = ? ,ship_to_ean_loc_code = ?   \n");
			sql.append(" where header_Id=? \n");
		    logger.debug("sql:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			ps.setString(++index, o.getOrderNo());
			ps.setString(++index, DateUtil.stringValue(DateUtil.parse(o.getOrderDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th),DateUtil.YYYY_MM_DD_WITHOUT_SLASH));
			ps.setString(++index, DateUtil.stringValue(DateUtil.parse(o.getDeliveryDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th),DateUtil.YYYY_MM_DD_WITHOUT_SLASH));
			ps.setLong(++index,o.getCustomerId());
			ps.setLong(++index,o.getShipToSiteUseId());
			ps.setLong(++index,o.getBillToSiteUseId());
			ps.setDouble(++index,Utils.convertStrToDouble(o.getNetAmount()));
			ps.setDouble(++index,Utils.convertStrToDouble(o.getVatAmount()));
			ps.setDouble(++index,Utils.convertStrToDouble(o.getTotalAmount()));
			ps.setString(++index, o.getShipToEanLocCode());//ship_to_ean_loc_code  get from m_address
			ps.setLong(++index, o.getHeaderId());
			
			ps.executeUpdate();
			
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}
	public static void insertOrderEDILineModel(Connection conn,OrderEDIBean o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("insertOrderEDILineModel headerId["+o.getHeaderId()+"]");
		int index = 0;
		try{
			
			StringBuffer sql = new StringBuffer("");
			sql.append(" insert into pensso.t_edi_line \n");
			sql.append(" (HEADER_ID, LINE_NUMBER, INVENTORY_ITEM_ID, ORDERED_QUANTITY,"); 
			sql.append(" ORDER_QUANTITY_UOM, UNIT_PRICE, LINE_AMOUNT, SHIP_FROM_ORG_ID, ");
			sql.append(" SUBINVENTORY, SCHEDULE_SHIP_DATE, PROMISE_DATE, PRICING_DATE, ");
			sql.append(" CALCULATE_PRICE_FLAG, EXPORTED) ");//14
			sql.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
		    logger.debug("sql:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			
			ps.setLong(++index, o.getHeaderId());
			ps.setInt(++index, Utils.convertToInt(o.getLineId()));
			ps.setInt(++index, Utils.convertStrToInt(o.getProductId()));
			ps.setInt(++index, Utils.convertStrToInt(o.getQty()));
			ps.setString(++index, o.getUom());//
			ps.setDouble(++index, Utils.convertStrToDouble(o.getUnitPrice()));
			ps.setDouble(++index, Utils.convertStrToDouble(o.getLineAmount()));
			ps.setLong(++index, 206);//SHIP_FROM_ORG_ID
			ps.setString(++index, "U001");//SUBINVENTORY
			ps.setDate(++index, new java.sql.Date(DateUtil.parse(o.getOrderDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th).getTime()));
			ps.setDate(++index, new java.sql.Date(DateUtil.parse(o.getOrderDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th).getTime()));
			ps.setDate(++index, new java.sql.Date(DateUtil.parse(o.getOrderDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th).getTime()));
			ps.setString(++index, "Y");//CALCULATE_PRICE_FLAG
			ps.setString(++index, "N");//EXPORTED
		
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
	public static int getMaxLineIdOrderEDILineModel(Connection conn,OrderEDIBean o) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		int maxLineId = 0;
		//logger.debug("getMaxLineIdOrderEDILineModel");
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select max(line_number) as max_line_id from pensso.t_edi_line \n");
			sql.append(" where header_Id="+o.getHeaderId() +"\n");
		    logger.debug("sql:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				maxLineId = rs.getInt("max_line_id");
			}
			return maxLineId;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
			rs.close();
		}
	}
	public static void insertOrderEDILineTemOracleModel(Connection conn,OrderEDIBean o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("insertOrderEDILineTemOracleModel headerId["+o.getHeaderId()+"]");
		int index = 0;
		try{
			
			StringBuffer sql = new StringBuffer("");
			sql.append(" insert into APPS.xxpens_edi_order_detail1_temp \n");
			sql.append(" (PO_NO,LINE_NUMBER,PRODUCT_CODE , PRODUCT_DESC, \n"); 
			sql.append(" ORDERED_QTY,TOTAL_ORDER,LINE_AMOUNT,UNIT_PRICE, ");
			sql.append(" WAREHOUSE, SUBINVENTORY ,ORDERED_QUANTITY,PRIMARY_UOM,HEADER_ID) ");//13
			sql.append(" VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?) ");
		    logger.debug("sql:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			
			ps.setString(++index, o.getOrderNo());//
			ps.setInt(++index, Utils.convertToInt(o.getLineId()));
			ps.setString(++index, o.getBarcode());
			ps.setString(++index, o.getProductName());
			ps.setInt(++index, Utils.convertStrToInt(o.getQty()));//ORDERED_QTY
			ps.setInt(++index, Utils.convertStrToInt(o.getQty()));//TOTAL_ORDER
			ps.setDouble(++index, Utils.convertStrToDouble(o.getLineAmount()));
			ps.setDouble(++index, Utils.convertStrToDouble(o.getUnitPrice()));
			ps.setLong(++index, 206);//WAREHOUSE
			ps.setString(++index, "U001");//SUBINVENTORY
			ps.setInt(++index, Utils.convertStrToInt(o.getQty()));//ORDERED_QUANTITY
			ps.setString(++index, o.getUom());
			ps.setLong(++index, o.getHeaderId());
			
			ps.executeUpdate();
			
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}
	public static int getHeaderIdOrderEDITempOracleModel(Connection conn,OrderEDIBean o) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		int headerId = 0;
		//logger.debug("getMaxLineIdOrderEDILineTempOracleModel");
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" select header_id  from apps.xxpens_edi_order_header_temp \n");
			sql.append(" where header_Id="+o.getHeaderId() +"\n");
		    logger.debug("sql:"+sql.toString());
		    
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				headerId = rs.getInt("header_id");
			}
			return headerId;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
			rs.close();
		}
	}
	public static String getCustPONoOrderEDIExist(String poNumber) throws Exception{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		String poNumberResult="";
		StringBuffer sql = new StringBuffer("");
		try{
			sql.append(" select cust_po_number from pensso.t_edi \n");
			sql.append(" where cust_po_number='"+poNumber +"' \n");
		    logger.debug("sql:"+sql.toString());
		    
		    conn = DBConnectionApps.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				poNumberResult = rs.getString("cust_po_number");
			}
			return poNumberResult;
		}catch(Exception e){
			e.printStackTrace();
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
			rs.close();
			conn.close();
		}
	}
	public static void deleteOrderEDIManualTempOracleModel(Connection conn,OrderEDIBean o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("deleteOrderEDITempOracleModel by headerId");
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" delete from apps.xxpens_edi_order_header_temp \n");
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
	public static void deleteOrderEDILineManualTempOracleModel(Connection conn,OrderEDIBean o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("deleteOrderEDILineTempOracleModel by headerId");
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" delete from apps.xxpens_edi_order_detail1_temp \n");
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
