package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.Master;
import com.isecinc.pens.bean.NSBean;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.web.nissin.NSConstant;
import com.isecinc.pens.web.popup.PopupForm;
import com.isecinc.pens.web.rt.RTConstant;
import com.isecinc.pens.web.shop.ShopBean;
import com.pens.util.BahtText;
import com.pens.util.Utils;
import com.pens.util.helper.SequenceProcess;


public class NSDAO {

	 private static Logger logger = Logger.getLogger("PENS");

	 public static int searchHeadTotalRecList(Connection conn,NSBean o) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
	    int totalRec = 0;
		try {
            sql.append("\n select count(*) as c FROM PENSBI.NISSIN_ORDER h ");
		    sql.append("\n where 1=1 ");
		    //Where Condition
		    sql.append(genWhereSearchList(o).toString());
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
	 public static List<NSBean> searchHeadList(Connection conn,NSBean o,boolean allRec ,int currPage,int pageSize,String pageName) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
		    NSBean h = null;
			List<NSBean> items = new ArrayList<NSBean>();
			int r=0;
			try {
				sql.append("\n select M.* from (");
				sql.append("\n select A.* ,rownum as r__ from (");
				sql.append("\n select h.* " );
				sql.append("\n ,(select a.channel_name from pens_sales_channel a where a.channel_id = h.channel_id) as channel_name");
				sql.append("\n ,(select a.province_name from pens_province a where a.channel_id = h.channel_id and a.province_id = h.province_id) as province_name");
				sql.append("\n  from NISSIN_ORDER h where 1=1 ");
			    //Where Condition
			    sql.append(genWhereSearchList(o).toString());
				sql.append("\n order by to_number(h.order_id) desc");
	            sql.append("\n   )A ");
	         
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
				   h = new NSBean();
				   h.setOrderId(Utils.isNull(rst.getString("order_id")));
				   h.setOrderDate(Utils.stringValue(rst.getDate("order_date"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				   h.setCustomerCode(Utils.isNull(rst.getString("customer_code")));
				   h.setCustomerType(Utils.isNull(rst.getString("customer_type")));
				   h.setCustomerSubType(Utils.isNull(rst.getString("customer_sub_type")));
				   h.setCustomerName(Utils.isNull(rst.getString("customer_name")));
				   h.setAddressLine1(Utils.isNull(rst.getString("address_line1")));
				   h.setAddressLine2(Utils.isNull(rst.getString("address_line2")));
				   h.setInvoiceNo(Utils.isNull(rst.getString("invoice_no")));
				   h.setSaleCode(Utils.isNull(rst.getString("sale_code")));
				   h.setChannelName(Utils.isNull(rst.getString("channel_name")));
				   h.setProvinceName(Utils.isNull(rst.getString("province_name")));
				   
				   h.setPhone(Utils.isNull(rst.getString("phone")));
				   h.setChannelId(Utils.isNull(rst.getString("channel_id")));
				   h.setProvinceId(Utils.isNull(rst.getString("province_id")));
				    
				   if( rst.getDate("invoice_date") !=null){
				     h.setInvoiceDate(Utils.stringValue(rst.getDate("invoice_date"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				   }else{
					   h.setInvoiceDate("");
				   }
				   h.setCupQty(Utils.isNull(rst.getInt("CUP_QTY")));
				   h.setPac6CTNQty(Utils.isNull(rst.getInt("PAC_QTY")));
				   h.setPoohQty(Utils.isNull(rst.getInt("POOH_QTY")));
				   
				   h.setCupNQty(Utils.isNull(rst.getInt("CUP_QTY_N")));
				   h.setPac6Qty(Utils.isNull(rst.getInt("PAC_QTY_N")));
				   h.setPac10CTNQty(Utils.isNull(rst.getInt("PAC_QTY_CTN_10")));
				   h.setPac10Qty(Utils.isNull(rst.getInt("PAC_QTY_10")));
				   h.setPoohNQty(Utils.isNull(rst.getInt("POOH_QTY_N")));
					
				   h.setStatus(Utils.isNull(rst.getString("Status")));
				   h.setStatusDesc(NSConstant.getDesc(h.getStatus()));
				   h.setRemark(Utils.isNull(rst.getString("remark")));
				   h.setPendingReason(Utils.isNull(rst.getString("pending_Reason")));
				  
				  if(pageName.equalsIgnoreCase("nissin")){
					  if(NSConstant.STATUS_OPEN.equals(h.getStatus())){
						  h.setCanSave(true);
						  h.setCanCancel(true);
					  }else  if(NSConstant.STATUS_COMPLETE.equals(h.getStatus())){
						  h.setCanSave(false);
						  h.setCanCancel(false);
					  }
				  }else{
					  //pens
					  if(NSConstant.STATUS_OPEN.equals(h.getStatus())){
						  h.setCanSave(true);
						  h.setCanCancel(true);
					  }else  if(NSConstant.STATUS_COMPLETE.equals(h.getStatus())){
						  h.setCanSave(false);
						  h.setCanCancel(false);
					  }else  if(NSConstant.STATUS_PENDING.equals(h.getStatus())){
						  h.setCanSave(true);
						  h.setCanCancel(true);
					  }
				  }
				   items.add(h);
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
			return items;
		}
	 public static NSBean searchNissinOrder(NSBean o,String pageName) throws Exception {
		 Connection conn  =null;
		 try{
			  conn = DBConnection.getInstance().getConnection();
			  return searchNissinOrderModel(conn, o, pageName);
		 }catch(Exception e){
			 throw e;
		 }finally{
			 conn.close();
		 }
	 }
	 public static NSBean searchNissinOrder(Connection conn,NSBean o,String pageName) throws Exception {
		 return searchNissinOrderModel(conn, o, pageName); 
	 }
	 public static NSBean searchNissinOrderModel(Connection conn,NSBean o,String pageName) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
		    NSBean h = null;
			int r=0;
			try {
				sql.append("\n select h.* " );
				sql.append("\n ,(select a.channel_name from pens_sales_channel a where a.channel_id = h.channel_id) as channel_name");
				sql.append("\n ,(select a.province_name from pens_province a where a.channel_id = h.channel_id and a.province_id = h.province_id) as province_name");
				sql.append("\n  from NISSIN_ORDER h where 1=1 ");
			    //Where Condition
			    sql.append(genWhereSearchList(o).toString());
				sql.append("\n order by to_number(h.order_id) desc");
	          
				logger.debug("sql:"+sql);
              
				ps = conn.prepareStatement(sql.toString());
				rst = ps.executeQuery();
				while(rst.next()) {
				   h = new NSBean();
				   h.setOrderId(Utils.isNull(rst.getString("order_id")));
				   h.setOrderDate(Utils.stringValue(rst.getDate("order_date"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				   h.setCustomerCode(Utils.isNull(rst.getString("customer_code")));
				   h.setCustomerType(Utils.isNull(rst.getString("customer_type")));
				   h.setCustomerSubType(Utils.isNull(rst.getString("customer_sub_type")));
				   h.setCustomerName(Utils.isNull(rst.getString("customer_name")));
				   h.setAddressLine1(Utils.isNull(rst.getString("address_line1")));
				   h.setAddressLine2(Utils.isNull(rst.getString("address_line2")));
				   h.setInvoiceNo(Utils.isNull(rst.getString("invoice_no")));
				   h.setSaleCode(Utils.isNull(rst.getString("sale_code")));
				   h.setChannelName(Utils.isNull(rst.getString("channel_name")));
				   h.setProvinceName(Utils.isNull(rst.getString("province_name")));
				   
				   h.setPhone(Utils.isNull(rst.getString("phone")));
				   h.setChannelId(Utils.isNull(rst.getString("channel_id")));
				   h.setProvinceId(Utils.isNull(rst.getString("province_id")));
				    
				   if( rst.getDate("invoice_date") !=null){
				     h.setInvoiceDate(Utils.stringValue(rst.getDate("invoice_date"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				   }else{
					   h.setInvoiceDate("");
				   }
				   h.setCupQty(Utils.isNull(rst.getInt("CUP_QTY")));
				   h.setPac6CTNQty(Utils.isNull(rst.getInt("PAC_QTY")));
				   h.setPoohQty(Utils.isNull(rst.getInt("POOH_QTY")));
				   
				   h.setCupNQty(Utils.isNull(rst.getInt("CUP_QTY_N")));
				   h.setPac6Qty(Utils.isNull(rst.getInt("PAC_QTY_N")));
				   h.setPac10CTNQty(Utils.isNull(rst.getInt("PAC_QTY_CTN_10")));
				   h.setPac10Qty(Utils.isNull(rst.getInt("PAC_QTY_10")));
				   h.setPoohNQty(Utils.isNull(rst.getInt("POOH_QTY_N")));
					
				   h.setStatus(Utils.isNull(rst.getString("Status")));
				   h.setStatusDesc(NSConstant.getDesc(h.getStatus()));
				   h.setRemark(Utils.isNull(rst.getString("remark")));
				   h.setPendingReason(Utils.isNull(rst.getString("pending_Reason")));
				  
				  if(pageName.equalsIgnoreCase("nissin")){
					  if(NSConstant.STATUS_OPEN.equals(h.getStatus())){
						  h.setCanSave(true);
						  h.setCanCancel(true);
					  }else  if(NSConstant.STATUS_COMPLETE.equals(h.getStatus())){
						  h.setCanSave(false);
						  h.setCanCancel(false);
					  }
				  }else{
					  //pens
					  if(NSConstant.STATUS_OPEN.equals(h.getStatus())){
						  h.setCanSave(true);
						  h.setCanCancel(true);
					  }else  if(NSConstant.STATUS_COMPLETE.equals(h.getStatus())){
						  h.setCanSave(false);
						  h.setCanCancel(false);
					  }else  if(NSConstant.STATUS_PENDING.equals(h.getStatus())){
						  h.setCanSave(true);
						  h.setCanCancel(true);
					  }
				  }
	
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
	 
	 public static StringBuffer genWhereSearchList(NSBean o) throws Exception{
		 StringBuffer sql = new StringBuffer();
		 Date docDate=null;
		 if( !Utils.isNull(o.getOrderId()).equals("")){
				sql.append("\n and h.order_id = '"+Utils.isNull(o.getOrderId())+"'");
			}
		if( !Utils.isNull(o.getStatus()).equals("")){
			sql.append("\n and h.status = '"+Utils.isNull(o.getStatus())+"'");
		}
		
		if( !Utils.isNull(o.getOrderDateFrom()).equals("") &&  !Utils.isNull(o.getOrderDateTo()).equals("")){
			docDate  = Utils.parse(o.getOrderDateFrom(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			String dateStr = Utils.stringValue(docDate, Utils.DD_MM_YYYY_WITH_SLASH,Locale.US);
			
			sql.append("\n and h.order_date >= to_date('"+dateStr+"','dd/mm/yyyy')");
			
			docDate  = Utils.parse(o.getOrderDateTo(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			String dateStrTo = Utils.stringValue(docDate, Utils.DD_MM_YYYY_WITH_SLASH,Locale.US);
			sql.append("\n and h.order_date <= to_date('"+dateStrTo+"','dd/mm/yyyy')");
		    
		}else if( !Utils.isNull(o.getOrderDateFrom()).equals("")){
			docDate  = Utils.parse(o.getOrderDateFrom(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			String dateStr = Utils.stringValue(docDate, Utils.DD_MM_YYYY_WITH_SLASH,Locale.US);
			
			sql.append("\n and h.order_date = to_date('"+dateStr+"','dd/mm/yyyy')");
		}
		
		if( !Utils.isNull(o.getNoPicRcv()).equals("")){
			sql.append("\n and h.PIC_RCV_DATE IS NULL ");
		}
		if( !Utils.isNull(o.getCustomerType()).equals("")){
			sql.append("\n and h.customer_type ='"+o.getCustomerType()+"'");
		}
		if( !Utils.isNull(o.getCustomerSubType()).equals("")){
			sql.append("\n and h.customer_sub_type ='"+o.getCustomerSubType()+"'");
		}
		if( !Utils.isNull(o.getChannelId()).equals("")){
			sql.append("\n and h.channel_id ='"+o.getChannelId()+"'");
		}
		return sql;
	 }
	 

	public static NSBean searchHeadSummary(Connection conn,NSBean o ) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			NSBean summary = null;
			int r = 1;
			try {
			    sql.append("\n select  " );
			    sql.append("\n nvl(sum(cup_qty),0) as cup_qty " );
			    sql.append("\n ,nvl(sum(pac_qty),0) as pac_qty " );
			    sql.append("\n ,nvl(sum(pooh_qty),0) as pooh_qty " );
			    
			    sql.append("\n ,nvl(sum(cup_qty_n),0) as cup_qty_n " );
			    sql.append("\n ,nvl(sum(pac_qty_n),0) as pac_qty_n " );
			    sql.append("\n ,nvl(sum(pac_qty_ctn_10),0) as pac_qty_ctn_10 " );
			    sql.append("\n ,nvl(sum(pac_qty_10),0) as pac_qty_10 " );
			    
			    sql.append("\n ,nvl(sum(pooh_qty_n),0) as pooh_qty_n " );
			    sql.append("\n from PENSBI.NISSIN_ORDER h where 1=1 ");
			    sql.append(genWhereSearchList(o));
				//sql.append("\n group by");

				logger.debug("sql:"+sql);
				
				ps = conn.prepareStatement(sql.toString());
				rst = ps.executeQuery();
				if(rst.next()) {
					summary = new NSBean();
					summary.setCupQty(Utils.decimalFormat(rst.getInt("cup_qty"),Utils.format_current_no_disgit));
					summary.setCupNQty(Utils.decimalFormat(rst.getInt("cup_qty_n"),Utils.format_current_no_disgit));
					
					summary.setPoohQty(Utils.decimalFormat(rst.getInt("pooh_qty"),Utils.format_current_no_disgit));
					summary.setPoohNQty(Utils.decimalFormat(rst.getInt("pooh_qty_n"),Utils.format_current_no_disgit));
					
					
					summary.setPac6CTNQty(Utils.decimalFormat(rst.getInt("pac_qty"),Utils.format_current_no_disgit));
					summary.setPac6Qty(Utils.decimalFormat(rst.getInt("pac_qty_n"),Utils.format_current_no_disgit));
					summary.setPac10CTNQty(Utils.decimalFormat(rst.getInt("pac_qty_ctn_10"),Utils.format_current_no_disgit));
					summary.setPac10Qty(Utils.decimalFormat(rst.getInt("pac_qty_10"),Utils.format_current_no_disgit));
					
					
				}//while
			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					ps.close();
				} catch (Exception e) {}
			}
		return summary;
	}
	public static void insertNissionOrderByNissin(Connection conn,NSBean o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("Insert");
		int  c = 1;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" INSERT INTO PENSBI.NISSIN_ORDER \n");
			sql.append(" (order_id, order_date, customer_type, customer_name, \n");
			sql.append(" address_line1, address_line2, phone, remark,  \n");
			sql.append(" STATUS, CREATE_DATE, CREATE_USER,channel_id,province_id ,customer_sub_type) ");
			sql.append(" VALUES \n"); 
			sql.append(" (?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?,?,?,?) \n");
			
			ps = conn.prepareStatement(sql.toString());
			
			Date d = Utils.parse(Utils.isNull(o.getOrderDate()), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			
			ps.setString(c++, Utils.isNull(o.getOrderId()));
			ps.setDate(c++, new java.sql.Date(d.getTime()));
			ps.setString(c++, Utils.isNull(o.getCustomerType()));
			ps.setString(c++, Utils.isNull(o.getCustomerName()));
			ps.setString(c++, Utils.isNull(o.getAddressLine1()));
			ps.setString(c++, Utils.isNull(o.getAddressLine2()));
			ps.setString(c++, Utils.isNull(o.getPhone()));
			ps.setString(c++, Utils.isNull(o.getRemark()));
			ps.setString(c++, Utils.isNull(o.getStatus()));
			ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(c++, o.getCreateUser());
			ps.setString(c++, Utils.isNull(o.getChannelId()));
			ps.setString(c++, Utils.isNull(o.getProvinceId()));
			ps.setString(c++, Utils.isNull(o.getCustomerSubType()));
			ps.executeUpdate();
			
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}
	
	public static int updateNissinOrderByNissin(Connection conn,NSBean o) throws Exception{
		PreparedStatement ps = null;
		int  c = 1;
		try{

			StringBuffer sql = new StringBuffer("");
			sql.append(" UPDATE NISSIN_ORDER SET order_date =? ,customer_type = ? ,customer_name=?,address_line1 =?,address_line2 =?,  \n");
			sql.append(" UPDATE_USER =? ,UPDATE_DATE = ? ,REMARK =? ,phone=? ,channel_id =?,province_id = ? ,customer_sub_type = ? \n");
			sql.append(" WHERE order_id = ?  \n" );

			ps = conn.prepareStatement(sql.toString());
			
			Date d = Utils.parse(Utils.isNull(o.getOrderDate()), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			ps.setDate(c++, new java.sql.Date(d.getTime()));
			
			ps.setString(c++, Utils.isNull(o.getCustomerType()));
			ps.setString(c++, Utils.isNull(o.getCustomerName()));
			ps.setString(c++, Utils.isNull(o.getAddressLine1()));
			ps.setString(c++, Utils.isNull(o.getAddressLine2()));
		
			ps.setString(c++, o.getCreateUser());
			ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(c++, Utils.isNull(o.getRemark()));
			ps.setString(c++, Utils.isNull(o.getPhone()));
			ps.setString(c++, Utils.isNull(o.getChannelId()));
			ps.setString(c++, Utils.isNull(o.getProvinceId()));
			ps.setString(c++, Utils.isNull(o.getCustomerSubType()));
			
			ps.setString(c++, Utils.isNull(o.getOrderId()));

			int r =ps.executeUpdate();
			return r;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}
	
	public static int updateStatusRTNControl(Connection conn,NSBean o) throws Exception{
		PreparedStatement ps = null;
		int  c = 1;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" UPDATE PENSBME_RTN_CONTROL SET STATUS = ? \n");
			sql.append(" ,UPDATE_USER =? ,UPDATE_DATE = ?   \n");
			
			sql.append(" WHERE AUTHORIZE_NO = ?  \n" );

			ps = conn.prepareStatement(sql.toString());
			
			ps.setString(c++, Utils.isNull(o.getStatus()));
			ps.setString(c++, o.getCreateUser());
			ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
		
			//ps.setString(c++, Utils.isNull(o.getDocNo()));

			int r =ps.executeUpdate();
			return r;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}
	
	
	public static int updateNissinOrderByPens(Connection conn,NSBean o) throws Exception{
		PreparedStatement ps = null;
		int  c = 1;
		Date picRcvDate = null;
		logger.debug("updateNissinOrderByPens");
		try{
			
			StringBuffer sql = new StringBuffer("");
			sql.append(" UPDATE NISSIN_ORDER SET STATUS =? ,INVOICE_DATE =?, INVOICE_NO =?, CUP_QTY=?,CUP_QTY_N=?" +
					",PAC_QTY = ?, PAC_QTY_N = ?, PAC_QTY_CTN_10=?, PAC_QTY_10 =?, POOH_QTY =?,POOH_QTY_N =?   \n");
			sql.append(" ,UPDATE_USER =? ,UPDATE_DATE = ? ,SALE_CODE =? ,CUSTOMER_CODE =? ,PENDING_REASON =?,CUSTOMER_NAME =? ,ADDRESS_LINE1 =?,ADDRESS_LINE2 =? \n");
			sql.append(" WHERE ORDER_ID = ?  \n" );

			ps = conn.prepareStatement(sql.toString());
			
			if( !Utils.isNull(o.getInvoiceDate()).equals("") ){
			    picRcvDate = Utils.parse(o.getInvoiceDate(),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			}
			
			ps.setString(c++, Utils.isNull(o.getStatus()));
			if( !Utils.isNull(o.getInvoiceDate()).equals("") ){
				ps.setTimestamp(c++, new java.sql.Timestamp(picRcvDate.getTime()));
		     }else{
		    	ps.setTimestamp(c++, null);
			}
			ps.setString(c++, Utils.isNull(o.getInvoiceNo()));
			ps.setInt(c++, Utils.convertStrToInt(o.getCupQty()));
			ps.setInt(c++, Utils.convertStrToInt(o.getCupNQty()));
			
			ps.setInt(c++, Utils.convertStrToInt(o.getPac6CTNQty()));
			ps.setInt(c++, Utils.convertStrToInt(o.getPac6Qty()));
			ps.setInt(c++, Utils.convertStrToInt(o.getPac10CTNQty()));
			ps.setInt(c++, Utils.convertStrToInt(o.getPac10Qty()));
			
			ps.setInt(c++, Utils.convertStrToInt(o.getPoohQty()));
			ps.setInt(c++, Utils.convertStrToInt(o.getPoohNQty()));
			ps.setString(c++, o.getCreateUser());
			ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(c++, Utils.isNull(o.getSaleCode()));
			ps.setString(c++, Utils.isNull(o.getCustomerCode()));
			ps.setString(c++, Utils.isNull(o.getPendingReason()));
			
			ps.setString(c++, Utils.isNull(o.getCustomerName()));
			ps.setString(c++, Utils.isNull(o.getAddressLine1()));
			ps.setString(c++, Utils.isNull(o.getAddressLine2()));
			
			ps.setString(c++, Utils.isNull(o.getOrderId()));

			int r =ps.executeUpdate();
			
			return r;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}
	
	 //	ตัวย่อของกลุ่มร้านค้า-yyxxxx  :LO-151234
	 public static String genDocNo(Date date,String custGroup) throws Exception{
	       String docNo = "";
	       Connection conn = null;
	       String prefix = "";
	       SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Utils.local_th);
		   try{
			   String today = df.format(date);
			   String[] d1 = today.split("/");
			   int curYear = Integer.parseInt(d1[0].substring(2,4));
			   
			   conn = DBConnection.getInstance().getConnection(); 
			   Master m = GeneralDAO.getMasterIdwacoal(conn,"Idwacoal",custGroup);
			   prefix = m!= null?m.getPensDesc():"XX";
			   
			   int seq = SequenceProcess.getNextValueByYear(conn,"AUTHORIZE_NO",custGroup, date);
			   
			   docNo = prefix +"-"+curYear+new DecimalFormat("0000").format(seq);
			   
			   /*logger.debug("seq:"+seq);
			   logger.debug("docNo:"+docNo);*/
		    
		   }catch(Exception e){
			   throw e;
		   }finally{
			   if(conn !=null){
				   conn.close();conn=null;
			   }
		   }
		  return docNo;
	}
	 
	 public static List<PopupForm> searchChannelList(PopupForm c,String operation) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				sql.delete(0, sql.length());
				sql.append("\n  SELECT M.* \n");
				sql.append("\n  from pens_sales_channel M");
				
				sql.append("\n  where 1=1  ");
			
				sql.append("\n  ORDER BY channel_id asc \n");
				
				logger.debug("sql:"+sql);
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no=0;
				while (rst.next()) {
					no++;
					PopupForm item = new PopupForm();
					item.setNo(no);
					item.setCode(Utils.isNull(rst.getString("Channel_id")));
					item.setDesc(Utils.isNull(rst.getString("Channel_name")));
					pos.add(item);
					
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
					conn.close();
				} catch (Exception e) {}
			}
			return pos;
		}
	 
	 public static List<PopupForm> searchProvinceList(String channelId,String operation) throws Exception {
			Statement stmt = null;
			ResultSet rst = null;
			List<PopupForm> pos = new ArrayList<PopupForm>();
			StringBuilder sql = new StringBuilder();
			Connection conn = null;
			try {
				sql.delete(0, sql.length());
				sql.append("\n  SELECT M.* ");
				sql.append("\n  from pens_province M");
				sql.append("\n  where 1=1  ");
			    
				if( !Utils.isNull(channelId).equals("")){
					sql.append("\n  and M.channel_id = "+Utils.isNull(channelId));	
				}
				sql.append("\n  ORDER BY province_id asc ");
				logger.debug("sql:"+sql);
				
				conn = DBConnection.getInstance().getConnection();
				stmt = conn.createStatement();
				rst = stmt.executeQuery(sql.toString());
				int no=0;
				while (rst.next()) {
					no++;
					PopupForm item = new PopupForm();
					item.setNo(no);
					item.setCode(Utils.isNull(rst.getString("province_id")));
					item.setDesc(Utils.isNull(rst.getString("province_name")));
					pos.add(item);
					
				}//while

			} catch (Exception e) {
				throw e;
			} finally {
				try {
					rst.close();
					stmt.close();
					conn.close();
				} catch (Exception e) {}
			}
			return pos;
		}
}
