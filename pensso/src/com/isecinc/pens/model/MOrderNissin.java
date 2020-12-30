package com.isecinc.pens.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.core.model.I_Model;
import com.isecinc.core.model.I_PO;
import com.isecinc.pens.bean.CustomerNissin;
import com.isecinc.pens.bean.OrderNissin;
import com.isecinc.pens.bean.OrderNissinLine;
import com.isecinc.pens.bean.Product;
import com.isecinc.pens.bean.SalesrepBean;
import com.isecinc.pens.bean.UOM;
import com.isecinc.pens.bean.User;
import com.pens.util.DBConnection;
import com.pens.util.DBConnectionApps;
import com.pens.util.DateUtil;
import com.pens.util.UserUtils;
import com.pens.util.Utils;
import com.pens.util.seq.SequenceProcessAll;

public class MOrderNissin {
	
	/** Logger */
	public static Logger logger = Logger.getLogger("PENS");

	public OrderNissin insertOrderNissin(Connection conn,OrderNissin o) throws Exception {
		PreparedStatement ps = null;
		int index = 0;
		try{
			String sql = "insert into pensso.t_order_nis " +
			        "\n (ORDER_ID,ORDER_DATE ,CUSTOMER_ID,DOC_STATUS ,REMARK,NIS_CREATE_DATE,NIS_CREATE_USER)"+
			        "\n VALUES(?,?,?,?,?,?,?)";
			
			 logger.debug("sql:"+sql);
			 
			 long orderId = SequenceProcessAll.getIns().getNextValue("t.order_nis.order_id").longValue();
			 o.setId(String.valueOf(orderId));
			 
			 ps = conn.prepareStatement(sql);
			 ps.setLong(++index, Utils.convertToLong(o.getId()));
			 ps.setDate(++index, new java.sql.Date(DateUtil.parse(o.getOrderDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th).getTime()));
			 ps.setLong(++index,o.getCustomerNis().getId());
			 ps.setString(++index, o.getDocStatus());
			 ps.setString(++index, o.getRemark());
			 ps.setTimestamp(++index, new Timestamp(System.currentTimeMillis()));
			 ps.setString(++index, o.getNisCreateUser());
			 ps.executeUpdate();
			 return o;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
			   ps.close();ps=null;
			}
		}
	}
	
	public boolean updateOrderNissin(Connection conn,OrderNissin o) throws Exception {
		PreparedStatement ps = null;
		try{
			String sql = "update pensso.t_order_nis set " +
			        "\n    NIS_UPDATE_DATE = sysdate"+
			        "\n   ,NIS_UPDATE_USER='"+o.getUser().getUserName()+"'"+
			        "\n   ,remark='"+o.getRemark()+"'"+
					"\n  where order_id ="+ o.getId();
			
			 logger.debug("sql:"+sql);
			 
			 ps = conn.prepareStatement(sql);
			 ps.executeUpdate();
			 return true;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
			   ps.close();ps=null;
			}
		}
	}
	
	public boolean updateOrderNissinByPens(Connection conn,OrderNissin o) throws Exception {
		PreparedStatement ps = null;
		try{
			String sql = "update pensso.t_order_nis set " +
					"\n    DOC_STATUS = '"+o.getDocStatus()+"'"+
					"\n   ,SALESREP_CODE ='"+o.getSalesrepCode()+"'"+
					"\n   ,SALESREP_ID ="+o.getSalesrepId()+""+
					"\n   ,ORDER_NO ='"+o.getOrderNo()+"'"+
					"\n   ,PENS_NOTE ='"+o.getPensNote()+"'";
			
			 if( !Utils.isNull(o.getInvoiceNo()).equals("")){
				sql +="\n   ,INVOICE_NO ='"+o.getInvoiceNo()+"'";
					if( !Utils.isNull(o.getInvoiceDate()).equals("")){
						sql+= "\n   ,INVOICE_DATE =to_date('"+DateUtil.convBuddhistToChristDate(o.getInvoiceDate(),"dd/mm/yyyy")+"','dd/mm/yyyy')";
					}
			 
			     sql +="\n   ,ORA_CUSTOMER_ID="+Utils.isNull(o.getOraCustomerId())+""+
					   "\n   ,ORA_CUSTOMER_CODE='"+Utils.isNull(o.getOraCustomerCode())+"'"+
					   "\n   ,ORA_CUSTOMER_NAME='"+Utils.isNull(o.getOraCustomerName())+"'";
			  }
			  //Update by Pens User Action
			  sql +="\n   ,PENS_ACTION_DATE = sysdate"+
			        "\n   ,PENS_ACTION_USER='"+o.getUser().getUserName()+"'"+
			        "\n   ,PENDING_REASON='"+Utils.isNull(o.getReason())+"'";
			  //update in case complete action
			  if( !Utils.isNull(o.getCompleteActionUser()).equals("")){    
				  sql +="\n   ,COMPLETE_ACTION_DATE = sysdate"+
					    "\n   ,COMPLETE_ACTION_USER='"+o.getUser().getUserName()+"'";
			  }
			  sql +="\n   where order_id ="+ o.getId();
			
			 logger.debug("sql:"+sql);
			 
			 ps = conn.prepareStatement(sql);
			 ps.executeUpdate();
			 return true;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
			   ps.close();ps=null;
			}
		}
	}
	public boolean updateOrderNissinBySalesPens(Connection conn,OrderNissin o) throws Exception {
		PreparedStatement ps = null;
		try{
			String sql = "update pensso.t_order_nis set " +
					"\n    ORDER_NO = '"+o.getOrderNo()+"'"+
					"\n   ,SALES_ACTION_DATE = sysdate"+
			        "\n   ,SALES_ACTION_USER='"+o.getUser().getUserName()+"'"+
					"\n  where order_id ="+ o.getId();
			
			 logger.debug("sql:"+sql);
			 
			 ps = conn.prepareStatement(sql);
			 ps.executeUpdate();
			 return true;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
			   ps.close();ps=null;
			}
		}
	}
	public boolean insertOrderNissinLine(Connection conn,OrderNissin h,OrderNissinLine o) throws Exception {
		PreparedStatement ps = null;
		int index = 0;
		try{
			String sql = "insert into pensso.t_order_nis_line " +
			        "\n (ORDER_LINE_ID,ORDER_ID,LINE_NO ,PRODUCT_ID,UOM_ID ,UOM_ID2,QTY,QTY2)"+
			        "\n VALUES(?,?,?,?,?,?,?,?)";
			
			 logger.debug("sql:"+sql);
			 
			 long orderLineId = SequenceProcessAll.getIns().getNextValue("t.order_nis_line.order_line.id").longValue();
			 
			 ps = conn.prepareStatement(sql);
			 ps.setLong(++index, orderLineId);
			 ps.setLong(++index, Utils.convertToLong(h.getId()));
			 ps.setInt(++index, o.getLineNo());
			 ps.setInt(++index, o.getProduct().getId());
			 ps.setString(++index, o.getUom1().getId());
			 ps.setString(++index, o.getUom2().getId());
			 ps.setDouble(++index, o.getQty1());
			 ps.setDouble(++index, o.getQty2());
			 ps.executeUpdate();
			 return true;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
			   ps.close();ps=null;
			}
		}
	}
	
	public boolean updateOrderNissinLine(Connection conn,OrderNissin h, OrderNissinLine o) throws Exception {
		PreparedStatement ps = null;
		try{
			String sql = "update pensso.t_order_nis_line set " +
					"\n  qty="+o.getQty1()+""+
					"\n ,qty2="+o.getQty2()+""+
					"\n  where order_id ="+ h.getId()+
			        "\n  and order_line_id ="+ o.getId();
			 logger.debug("sql:"+sql);
			 
			 ps = conn.prepareStatement(sql);
			 ps.executeUpdate();
			 return true;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
			   ps.close();ps=null;
			}
		}
	}
	public boolean deleteOrderNissinLine(Connection conn, String deleteOrderLineId) throws Exception {
		PreparedStatement ps = null;
		try{
			String sql = "delete pensso.t_order_nis_line  " +
					"\n  where order_line_id in("+ deleteOrderLineId+")";
			 logger.debug("sql:"+sql);
			 
			 ps = conn.prepareStatement(sql);
			 ps.executeUpdate();
			 return true;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
			   ps.close();ps=null;
			}
		}
	}
	public static int searchTotalHead(Connection conn ,User user,OrderNissin o) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int totalRec = 0;
		try {
			sql.append("\n select count(*) as c from(");
			sql.append("\n    SELECT distinct t.order_id ");
			sql.append("\n    FROM pensso.t_order_nis t");
			sql.append("\n    ,pensso.m_customer_nis c ");
			sql.append("\n    WHERE 1=1");
			sql.append("\n    AND t.customer_id = c.customer_id");
			//Where Condition
			sql.append(genSearchHeadWhereSQL(o));
			
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
	public static OrderNissin searchHead(User user,OrderNissin o ,boolean allRec ,int currPage,int pageSize ) throws Exception {
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			return searchHead(conn,user, o, allRec, currPage, pageSize);
		}catch(Exception e){
			throw e;
		}finally{
			if(conn != null){
				conn.close();
			}
		}
	}
	
	public static OrderNissin searchHead(Connection conn ,User user,OrderNissin o ,boolean allRec ,int currPage,int pageSize) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		OrderNissin h = null;
		List<OrderNissin> items = new ArrayList<OrderNissin>();
		int r = 1;
		CustomerNissin custNis = null;
		try {
			sql.append("\n select M.* from (");
			sql.append("\n select A.* ,rownum as r__ from (");
			sql.append("\n    SELECT t.order_id,t.order_date ,t.doc_status");
			sql.append("\n    ,t.salesrep_code,t.invoice_no,t.invoice_date ,t.ora_customer_code ");
			sql.append("\n    ,c.customer_id ,c.name as customer_name,t.nis_create_user");
			sql.append("\n    FROM pensso.t_order_nis t ,pensso.m_customer_nis c ");
			sql.append("\n    WHERE t.customer_id = c.customer_id");
			
			//Where Condition
			sql.append(genSearchHeadWhereSQL(o));
			
			sql.append("\n   order by t.order_id desc ");
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
			   h = new OrderNissin();
			   h.setId(Utils.isNull(rst.getString("order_id")));
			   h.setDocStatus(Utils.isNull(rst.getString("doc_status")));
			   h.setCustomerId(rst.getLong("customer_id"));
			   h.setOrderDate(DateUtil.stringValue(rst.getDate("order_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
			   h.setSalesrepCode(Utils.isNull(rst.getString("SALESREP_CODE")));
			   h.setInvoiceNo(Utils.isNull(rst.getString("invoice_no")));
			   h.setInvoiceDate(DateUtil.stringValueNull(rst.getDate("invoice_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
			   h.setOraCustomerCode(Utils.isNull(rst.getString("ora_customer_code")));
			   h.setNisCreateUser(Utils.isNull(rst.getString("nis_create_user")));
			   
			   //Get Customer Nissin Detail
			   custNis = new MCustomerNissin().findOpt(conn,h.getCustomerId());
			   h.setCustomerNis(custNis);
			   
			   //check can edit
			   if(UserUtils.userInRole("ROLE_ALL",o.getUser(),new String[]{User.ADMIN,User.NIS})){
			       if(Utils.isNull(h.getDocStatus()).equals("OPEN") 
			    		&& Utils.isNull(h.getSalesrepCode()).equals("")){
			          h.setCanEdit(true);
			       }
			   }else   if(UserUtils.userInRole("ROLE_ALL",o.getUser(),new String[]{User.ADMIN,User.NIS_PENS})){
				   if(Utils.isNull(h.getDocStatus()).equals("OPEN") ){
				       h.setCanEdit(true);
				   } 
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
	
	public static StringBuffer genSearchHeadWhereSQL(OrderNissin o) throws Exception{
		StringBuffer sql = new StringBuffer();
		
		if( o.getCustomerId() !=0){
			sql.append("\n and c.customer_id ="+o.getCustomerId()+"");
		}
		if( !Utils.isNull(o.getId()).equals("0") && !Utils.isNull(o.getId()).equals("")){
			sql.append("\n and t.order_id ="+o.getId()+"");
		}
		if( !Utils.isNull(o.getDocStatus()).equals("")){
			sql.append("\n and t.doc_status = '"+Utils.isNull(o.getDocStatus())+"'");
		}
		if( !Utils.isNull(o.getCustomerNis().getName()).equals("")){
			sql.append("\n and c.name like '%"+Utils.isNull(o.getCustomerNis().getName())+"%'");
		}
		if( !Utils.isNull(o.getOrderDateFrom()).equals("") &&  !Utils.isNull(o.getOrderDateTo()).equals("")){
			sql.append("\n and t.order_date >= to_date('"+DateUtil.convBuddhistToChristDate(o.getOrderDateFrom(), DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/mm/yyyy')");
			sql.append("\n and t.order_date <= to_date('"+DateUtil.convBuddhistToChristDate(o.getOrderDateTo(), DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/mm/yyyy')");
		}else{
			if( !Utils.isNull(o.getOrderDateFrom()).equals("")){
			  sql.append("\n and t.order_date = to_date('"+DateUtil.convBuddhistToChristDate(o.getOrderDateFrom(), DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/mm/yyyy')");
			}
			if( !Utils.isNull(o.getOrderDateTo()).equals("")){
			  sql.append("\n and t.order_date = to_date('"+DateUtil.convBuddhistToChristDate(o.getOrderDateTo(), DateUtil.DD_MM_YYYY_WITH_SLASH)+"','dd/mm/yyyy')");
			}
		}
		
		if( !Utils.isNull(o.getCustomerNis().getProvinceId()).equals("")){
			sql.append("\n and c.province_id = '"+Utils.isNull(o.getCustomerNis().getProvinceId())+"'");
		}
		if( !Utils.isNull(o.getCustomerNis().getDistrictId()).equals("")){
			sql.append("\n and c.district_id = '"+Utils.isNull(o.getCustomerNis().getDistrictId())+"'");
		}
		if( !Utils.isNull(o.getCustomerNis().getCustomerType()).equals("")){
			sql.append("\n and c.customer_type = '"+Utils.isNull(o.getCustomerNis().getCustomerType())+"'");
		}
		//For Sales Nissin 
		if(UserUtils.userInRole("ROLE_ALL",o.getUser(),new String[]{User.NIS})){
			sql.append("\n and t.nis_create_user='"+o.getUser().getUserName()+"'");
			sql.append("\n and c.province_id in (select province_id from pensso.m_province_sales_nis where salesrep_code ='"+o.getUser().getUserName()+"')" );
		}
		
		if( !Utils.isNull(o.getSalesrepCode()).equals("")){
			sql.append("\n and t.salesrep_code ='"+Utils.isNull(o.getSalesrepCode())+"'");
		}
		
		return sql;
	}
	public static OrderNissin searchOrderNissin(OrderNissin o) throws Exception {
		Connection conn = null;
		try{
			conn = DBConnectionApps.getInstance().getConnection();
			return searchOrderNissin(conn, o);
		}catch(Exception e){
			throw e;
		}finally{
			if(conn !=null){
				conn.close();
			}
		}
	}
	
		public static OrderNissin searchOrderNissin(Connection conn ,OrderNissin o) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			OrderNissin h = null;
			CustomerNissin cust = null;
			int r = 0;
			try {
				sql.append("\n  SELECT t.* ");
				sql.append("\n  FROM pensso.t_order_nis t ");
				sql.append("\n  WHERE 1=1");
				if( !Utils.isNull(o.getId()).equals("0") && !Utils.isNull(o.getId()).equals("")){
					sql.append("\n and t.order_id ="+o.getId()+"");
				}
				if( o.getCustomerId() !=0){
					sql.append("\n and t.customer_id ="+o.getCustomerId()+"");
				}
				sql.append("\n  order by t.order_id desc ");
	            
				logger.debug("sql:"+sql);
				ps = conn.prepareStatement(sql.toString());
				rst = ps.executeQuery();
				if(rst.next()) {
				   h = new OrderNissin();
				   h.setId(Utils.isNull(rst.getString("order_id")));
				   h.setDocStatus(Utils.isNull(rst.getString("doc_status")));
				   h.setOrderDate(DateUtil.stringValue(rst.getDate("order_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
				   h.setOrderNo(Utils.isNull(rst.getString("order_no")));
				   h.setRemark(Utils.isNull(rst.getString("remark")));
				   h.setNisCreateUser(Utils.isNull(rst.getString("nis_create_user")));
			       cust = new MCustomerNissin().findOpt(conn,rst.getInt("customer_id"));
			       h.setCustomerNis(cust);
			       
			       //Oracle input by pens
			       h.setInvoiceNo(Utils.isNull(rst.getString("invoice_no")));
				   h.setInvoiceDate(DateUtil.stringValueNull(rst.getDate("invoice_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
				   h.setSalesrepCode(Utils.isNull(rst.getString("salesrep_code")));
				   h.setSalesrepId(Utils.isNull(rst.getString("salesrep_id")));
				   h.setOraCustomerId(Utils.isNull(rst.getString("ora_customer_id")));
				   h.setOraCustomerCode(Utils.isNull(rst.getString("ora_customer_code")));
				   h.setOraCustomerName(Utils.isNull(rst.getString("ora_customer_name")));
				   
				   h.setPensActionDate(DateUtil.stringValueNull(rst.getTimestamp("pens_action_date"), DateUtil.DD_MM_YYYY__HH_mm_ss_WITH_SLASH,DateUtil.local_th));
				   h.setSalesActionDate(DateUtil.stringValueNull(rst.getTimestamp("sales_action_date"), DateUtil.DD_MM_YYYY__HH_mm_ss_WITH_SLASH,DateUtil.local_th));
				   h.setCompleteActionDate(DateUtil.stringValueNull(rst.getTimestamp("complete_action_date"), DateUtil.DD_MM_YYYY__HH_mm_ss_WITH_SLASH,DateUtil.local_th));
				   h.setPensNote(Utils.isNull(rst.getString("PENS_NOTE")));
				   
				   if(h.getDocStatus().equalsIgnoreCase("PENDING")){
					   h.setPendingStatus("PENDING");
				   }
				   h.setReason(Utils.isNull(rst.getString("pending_reason")));
				   
				   logger.debug("user role_all["+o.getUser().getRoleAll()+"]");
				   //set enable OrderNo for key Van Sales
				   if(o.getUser().getType().equalsIgnoreCase("VAN")){
					    logger.debug("set 1 Role VAN");
						h.setReadonlyText(true);
						h.setReadonlyStyle("disableText");
						//can enable orderNo
						h.setReadonlyOrderNoText(false);
						h.setReadonlyOrderNoStyle("\" autoComplete=\"off");
						 
						//check can edit orderNo
						if(Utils.isNull(h.getSalesrepCode()).equals("")){
						   h.setCanEdit(false);
						}
					}else if(UserUtils.userInRole("ROLE_ALL",o.getUser(),new String[]{User.ADMIN,User.NIS_PENS})){
						logger.debug("set 2 NISSINPENS");
						h.setReadonlyText(false);
						h.setReadonlyStyle("\" autoComplete=\"off");
						
						//can enable orderNo
						h.setReadonlyOrderNoText(false);
						h.setReadonlyOrderNoStyle("\" autoComplete=\"off");
						
						//check can edit orderNo
						if( !Utils.isNull(h.getDocStatus()).equals("COMPLETE")){
						   h.setCanEdit(true);
						}
						
					}else if(UserUtils.userInRole("ROLE_ALL",o.getUser(),new String[]{User.ADMIN,User.NIS})){
						logger.debug("set 3 NISSIN");
						h.setReadonlyText(false);
						h.setReadonlyStyle("\" autoComplete=\"off");
						
						//can enable orderNo
						h.setReadonlyOrderNoText(false);
						h.setReadonlyOrderNoStyle("\" autoComplete=\"off");
						
						logger.debug("SalesrepCode:"+h.getSalesrepCode());
						if(Utils.isNull(h.getSalesrepCode()).equals("")){
						   h.setCanEdit(true);
						}
					}else{
						logger.debug("set 4 NISSINVIEW");
						//NISSINVIEW DISABLE ALL
						h.setReadonlyText(true);
						h.setReadonlyStyle("disableText");
						h.setReadonlyOrderNoText(true);
						h.setReadonlyOrderNoStyle("disableText");
						 
						//check can edit orderNo
						h.setCanEdit(false);
					}
					
			       h.setLinesList(searchOrderNissinItemList(conn, h));
			       
				   r++;
				}//while
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					ps.close();
				} catch (Exception e) {}
			}
		return h;
	}
	public static List<OrderNissinLine> searchOrderNissinItemList(Connection conn ,OrderNissin o) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		List<OrderNissinLine> linesList = new ArrayList<OrderNissinLine>();
		OrderNissinLine h = null;
		int r = 0;
		UOM uom = null;
		try {
			sql.append("\n  SELECT t.* ");
			sql.append("\n  FROM pensso.t_order_nis_line t ");
			sql.append("\n  WHERE 1=1");
			if( !Utils.isNull(o.getId()).equals("0") && !Utils.isNull(o.getId()).equals("")){
				sql.append("\n and t.order_id ="+o.getId()+"");
			}
			sql.append("\n  order by t.order_id desc ");
	        
			logger.debug("sql:"+sql);
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()) {
			   h = new OrderNissinLine();
			   h.setId(rst.getLong("order_line_id"));
			   h.setId(rst.getLong("ORDER_LINE_ID"));
			   h.setLineNo(rst.getInt("LINE_NO"));
			   h.setOrderId(rst.getLong("ORDER_ID"));
			   Product productInfo = new MProduct().findOpt(rst.getString("PRODUCT_ID"));
			   h.setProduct(productInfo);

			   uom = new UOM();
			   uom.setId(rst.getString("UOM_ID"));
			   uom.setCode(rst.getString("UOM_ID"));
			   uom.setName(rst.getString("UOM_ID"));
			   h.setUom1(uom);
			   
			   uom = new UOM();
			   uom.setId(rst.getString("UOM_ID2"));
			   uom.setCode(rst.getString("UOM_ID2"));
			   uom.setName(rst.getString("UOM_ID2"));
			   h.setUom2(uom);
			   
			   h.setQty(rst.getDouble("QTY"));
			   h.setQty1(rst.getDouble("QTY"));
			   h.setQty2(rst.getDouble("QTY2"));
			   r++;
			   linesList.add(h);
			}//while
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					ps.close();
				} catch (Exception e) {}
			}
		return linesList;
	}
	
	public static List<SalesrepBean> getSalesCodeByCustomerNisDetail(CustomerNissin custNis) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		List<SalesrepBean> salescodeList = new ArrayList<SalesrepBean>();
		SalesrepBean salesrepBean = null;
		Connection conn =null;
		try {
			conn = DBConnectionApps.getInstance().getConnection();
			
			sql.append("\n SELECT distinct code,primary_salesrep_id ");
			sql.append("\n FROM apps.xxpens_ar_cust_sales_all");
			sql.append("\n WHERE province ='"+custNis.getProvinceName()+"'");
			sql.append("\n AND AMPHUR ='"+custNis.getDistrictName()+"'");
			sql.append("\n AND CODE LIKE 'V%' ");
			sql.append("\n AND LENGTH(CODE) = 4 ");
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()) {
				salesrepBean = new SalesrepBean();
				salesrepBean.setCode(Utils.isNull(rst.getString("code")));
				salesrepBean.setSalesrepId(Utils.isNull(rst.getString("primary_salesrep_id")));
			    salescodeList.add(salesrepBean);
			}//while
		} catch (Exception e) {
			throw e;
		} finally {
			if(rst != null)rst.close();
			if(ps != null)ps.close();
			if(conn !=null)conn.close();
		}
		return salescodeList;
	}
}
