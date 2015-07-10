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

import util.BahtText;

import com.isecinc.pens.bean.Master;
import com.isecinc.pens.bean.NSBean;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.process.SequenceProcess;
import com.isecinc.pens.web.nissin.NSConstant;
import com.isecinc.pens.web.rt.RTConstant;


public class NSDAO {

	 private static Logger logger = Logger.getLogger("PENS");

	 public static NSBean searchHead(Connection conn,NSBean o,boolean getTrans,String page ) throws Exception {
		  return searchHeadModel(conn, o,getTrans,page);
		}
	   
	   public static NSBean searchHead(NSBean o ,boolean getTrans,String page) throws Exception {
		   Connection conn = null;
		   try{
			  conn = DBConnection.getInstance().getConnection();
			  return searchHeadModel(conn, o,getTrans,page);
		   }catch(Exception e){
			   throw e;
		   }finally{
			   conn.close();
		   }
		}
	   
		public static NSBean searchHeadModel(Connection conn,NSBean o ,boolean getTrans,String page) throws Exception {
				PreparedStatement ps = null;
				ResultSet rst = null;
				StringBuilder sql = new StringBuilder();
				NSBean h = null;
				List<NSBean> items = new ArrayList<NSBean>();
				int r = 1;
				Date docDate=null;
				try {
				    sql.append("\n select h.* from NISSIN_ORDER h where 1=1 ");
				   
					if( !Utils.isNull(o.getOrderId()).equals("")){
						sql.append("\n and h.order_id = '"+Utils.isNull(o.getOrderId())+"'");
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
					
				
					sql.append("\n order by h.order_id desc ");

					logger.debug("sql:"+sql);
					
					ps = conn.prepareStatement(sql.toString());
					
					rst = ps.executeQuery();
					while(rst.next()) {
					   h = new NSBean();
					   h.setOrderId(Utils.isNull(rst.getString("order_id")));
					   h.setOrderDate(Utils.stringValue(rst.getDate("order_date"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
					   h.setCustomerType(Utils.isNull(rst.getString("customer_type")));
					   h.setCustomerName(Utils.isNull(rst.getString("customer_name")));
					   h.setAddressLine1(Utils.isNull(rst.getString("address_line1")));
					   h.setAddressLine2(Utils.isNull(rst.getString("address_line2")));
					   h.setInvoiceNo(Utils.isNull(rst.getString("invoice_no")));
					   h.setSaleCode(Utils.isNull(rst.getString("sale_code")));
					   h.setPhone(Utils.isNull(rst.getString("phone")));
					   
					   if( rst.getDate("invoice_date") !=null){
					     h.setInvoiceDate(Utils.stringValue(rst.getDate("invoice_date"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
					   }else{
						   h.setInvoiceDate("");
					   }
					   h.setCupQty(Utils.isNull(rst.getString("CUP_QTY")));
					   h.setPacQty(Utils.isNull(rst.getString("PAC_QTY")));
					   h.setPoohQty(Utils.isNull(rst.getString("POOH_QTY")));
						   
					   h.setStatus(Utils.isNull(rst.getString("Status")));
					   h.setStatusDesc(NSConstant.getDesc(h.getStatus()));
					   h.setRemark(Utils.isNull(rst.getString("remark")));
					  
					  if(page.equalsIgnoreCase("nissin")){
						  if(NSConstant.STATUS_OPEN.equals(h.getStatus())){
							  h.setCanSave(true);
							  h.setCanCancel(true);
						  }else  if(NSConstant.STATUS_COMPLETE.equals(h.getStatus())){
							  h.setCanSave(false);
							  h.setCanCancel(false);
						  }else  if(NSConstant.STATUS_CANCEL.equals(h.getStatus())){
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
						  }else  if(NSConstant.STATUS_CANCEL.equals(h.getStatus())){
							  h.setCanSave(false);
							  h.setCanCancel(false);
						  }
					  }
					   items.add(h);
					   r++;
					}//while
					
					//set Result 
					o.setItems(items);
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
	
		
	public static void insertNissionOrderByNissin(Connection conn,NSBean o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("Insert");
		int  c = 1;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" INSERT INTO PENSBI.NISSIN_ORDER \n");
			sql.append(" (order_id, order_date, customer_type, customer_name, \n");
			sql.append(" address_line1, address_line2, phone, remark,  \n");
			sql.append(" STATUS, CREATE_DATE, CREATE_USER ) ");
			sql.append(" VALUES \n"); 
			sql.append(" (?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?) \n");
			
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
			sql.append(" UPDATE_USER =? ,UPDATE_DATE = ? ,REMARK =? ,phone=? \n");
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
		try{
			
			StringBuffer sql = new StringBuffer("");
			sql.append(" UPDATE NISSIN_ORDER SET STATUS =? ,INVOICE_DATE =?, INVOICE_NO =?, CUP_QTY=?,PAC_QTY = ?,POOH_QTY =?  \n");
			sql.append(" ,UPDATE_USER =? ,UPDATE_DATE = ? ,SALE_CODE =?  \n");
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
			ps.setInt(c++, Utils.convertStrToInt(o.getPacQty()));
			ps.setInt(c++, Utils.convertStrToInt(o.getPoohQty()));
			
			ps.setString(c++, o.getCreateUser());
			ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(c++, Utils.isNull(o.getSaleCode()));
			
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
	
	 //	�����ͧ͢�������ҹ���-yyxxxx  :LO-151234
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
}
