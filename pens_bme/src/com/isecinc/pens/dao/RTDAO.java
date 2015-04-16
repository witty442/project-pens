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

import org.apache.log4j.Logger;

import util.BahtText;

import com.isecinc.pens.bean.Master;
import com.isecinc.pens.bean.RTBean;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.process.SequenceProcess;
import com.isecinc.pens.web.rt.RTConstant;


public class RTDAO {

	 private static Logger logger = Logger.getLogger("PENS");

	 public static RTBean searchHead(Connection conn,RTBean o,boolean getTrans ) throws Exception {
		  return searchHeadModel(conn, o,getTrans);
		}
	   
	   public static RTBean searchHead(RTBean o ,boolean getTrans) throws Exception {
		   Connection conn = null;
		   try{
			  conn = DBConnection.getInstance().getConnection();
			  return searchHeadModel(conn, o,getTrans);
		   }catch(Exception e){
			   throw e;
		   }finally{
			   conn.close();
		   }
		}
	   
		public static RTBean searchHeadModel(Connection conn,RTBean o ,boolean getTrans) throws Exception {
				PreparedStatement ps = null;
				ResultSet rst = null;
				StringBuilder sql = new StringBuilder();
				RTBean h = null;
				List<RTBean> items = new ArrayList<RTBean>();
				int r = 1;
				Date docDate=null;
				try {
				    sql.append("\n select h.* " +
				    		"\n ,(select pens_desc FROM PENSBME_MST_REFERENCE M WHERE 1=1  " +
							"     and M.reference_code = 'Store' and M.pens_value = h.cust_no) as store_name  "+
							"\n ,(select interface_desc FROM PENSBME_MST_REFERENCE M WHERE 1=1  " +
							"     and M.reference_code = 'Idwacoal' and M.pens_value = h.cust_group) as cust_group_name  "+
				    		"\n from PENSBME_RTN_CONTROL h where 1=1 ");
				   
					if( !Utils.isNull(o.getDocNo()).equals("")){
						sql.append("\n and h.Authorize_no = '"+Utils.isNull(o.getDocNo())+"'");
					}
					if( !Utils.isNull(o.getDocDate()).equals("")){
						sql.append("\n and h.tran_date = ?");
					    docDate  = Utils.parse(o.getDocDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					}
					if( !Utils.isNull(o.getNoPicRcv()).equals("")){
						sql.append("\n and h.PIC_RCV_DATE IS NULL ");
					}
					sql.append("\n order by h.Authorize_no desc ");
					logger.debug("sql:"+sql);
					
					ps = conn.prepareStatement(sql.toString());
					
					if( !Utils.isNull(o.getDocDate()).equals("") ){
						ps.setDate(1, new java.sql.Date(docDate.getTime()));
					}
					
					rst = ps.executeQuery();
					while(rst.next()) {
					   h = new RTBean();
					   h.setDocNo(Utils.isNull(rst.getString("Authorize_no")));
					   h.setDocDate(Utils.stringValue(rst.getDate("tran_date"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
					   
					   h.setRefDoc(Utils.isNull(rst.getString("ref_doc")));
					   h.setCustGroup(Utils.isNull(rst.getString("Cust_group")));
					   h.setCustGroupName(Utils.isNull(rst.getString("Cust_group_name")));
					   h.setStoreCode(Utils.isNull(rst.getString("Cust_no")));
					   h.setStoreName(Utils.isNull(rst.getString("store_name")));
					   
					   h.setRtnNo(Utils.isNull(rst.getString("RTN_no")));
					   h.setRtnQtyCTN(Utils.isNull(rst.getString("RTN_Qty_CTN")));
					   h.setRtnQtyEA(Utils.isNull(rst.getString("RTN_Qty_EA")));
					   
					   if(rst.getDate("PIC_RCV_DATE") != null){
					      h.setPicRcvDate(Utils.stringValue(rst.getDate("PIC_RCV_DATE"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
					   }else{
						  h.setPicRcvDate("");
					   }
					   h.setPicRcvQtyCTN(Utils.isNull(rst.getString("PIC_rcv_Qty_CTN")));
					   h.setPicRcvQtyEA(Utils.isNull(rst.getString("PIC_rcv_Qty_EA")));
					   h.setStatus(Utils.isNull(rst.getString("Status")));
					   h.setStatusDesc(RTConstant.getDesc(h.getStatus()));
					   
					   
					  //
					  if(RTConstant.STATUS_OPEN.equals(h.getStatus())){
						  h.setCanSave(true);
						  h.setCanComplete(true);
						  h.setCanCancel(true);
					  }else  if(RTConstant.STATUS_COMPLETE.equals(h.getStatus())){
						  h.setCanSave(false);
						  h.setCanComplete(false);
						  h.setCanCancel(false);
					  }else  if(RTConstant.STATUS_CANCEL.equals(h.getStatus())){
						  h.setCanSave(false);
						  h.setCanComplete(false);
						  h.setCanCancel(false);
					  }else  if(RTConstant.STATUS_RECEIVED.equals(h.getStatus())){
						  h.setCanSave(false);
						  h.setCanComplete(false);
						  h.setCanCancel(false);
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
	
		
	public static void insertRTNControlBySale(Connection conn,RTBean o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("Insert");
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" INSERT INTO PENSBI.PENSBME_RTN_CONTROL \n");
			sql.append(" (AUTHORIZE_NO, TRAN_DATE, REF_DOC, CUST_GROUP, \n");
			sql.append(" CUST_NO, RTN_NO, RTN_QTY_CTN, RTN_QTY_EA,  \n");
			sql.append(" STATUS, CREATE_DATE, CREATE_USER) \n");
			sql.append(" VALUES \n"); 
			sql.append(" (?, ?, ?, ?, ?, ?, ?, ?, ?,?,?) \n");
			
			ps = conn.prepareStatement(sql.toString());
			
			Date docDate = Utils.parse(o.getDocDate(),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			
			int c =1;
			ps.setString(c++, o.getDocNo());
			ps.setTimestamp(c++, new java.sql.Timestamp(docDate.getTime()));
			ps.setString(c++, Utils.isNull(o.getRefDoc()));
			ps.setString(c++, Utils.isNull(o.getCustGroup()));
			ps.setString(c++, Utils.isNull(o.getStoreCode()));
			
			ps.setString(c++, Utils.isNull(o.getRtnNo()));
			ps.setString(c++, Utils.isNull(o.getRtnQtyCTN()));
			ps.setString(c++, Utils.isNull(o.getRtnQtyEA()));

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

	public static int updateStatusRTNControl(Connection conn,RTBean o) throws Exception{
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
		
			ps.setString(c++, Utils.isNull(o.getDocNo()));

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
	
	public static int updateRTNControlBySale(Connection conn,RTBean o) throws Exception{
		PreparedStatement ps = null;
		int  c = 1;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" UPDATE PENSBME_RTN_CONTROL SET CUST_NO =? ,REF_DOC = ? ,RTN_NO=?,RTN_QTY_CTN =?,RTN_QTY_EA =?,  \n");
			sql.append(" UPDATE_USER =? ,UPDATE_DATE = ?   \n");
			
			sql.append(" WHERE AUTHORIZE_NO = ?  \n" );

			ps = conn.prepareStatement(sql.toString());
			
			ps.setString(c++, Utils.isNull(o.getStoreCode()));
			ps.setString(c++, Utils.isNull(o.getRefDoc()));
			ps.setString(c++, Utils.isNull(o.getRtnNo()));
			ps.setString(c++, Utils.isNull(o.getRtnQtyCTN()));
			ps.setString(c++, Utils.isNull(o.getRtnQtyEA()));
			ps.setString(c++, o.getCreateUser());
			ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(c++, Utils.isNull(o.getDocNo()));

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

	public static int updateRTNControlByPic(Connection conn,RTBean o) throws Exception{
		PreparedStatement ps = null;
		int  c = 1;
		Date picRcvDate = null;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" UPDATE PENSBME_RTN_CONTROL SET STATUS =? ,PIC_RCV_DATE =?, PIC_RCV_QTY_CTN =?, PIC_RCV_QTY_EA=?,  \n");
			sql.append(" UPDATE_USER =? ,UPDATE_DATE = ?   \n");
			
			sql.append(" WHERE AUTHORIZE_NO = ?  \n" );

			ps = conn.prepareStatement(sql.toString());
			
			if( !Utils.isNull(o.getPicRcvDate()).equals("") ){
			    picRcvDate = Utils.parse(o.getPicRcvDate(),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			}
			
			ps.setString(c++, Utils.isNull(o.getStatus()));
			if( !Utils.isNull(o.getPicRcvDate()).equals("") ){
				ps.setTimestamp(c++, new java.sql.Timestamp(picRcvDate.getTime()));
			 }
			ps.setString(c++, Utils.isNull(o.getPicRcvQtyCTN()));
			ps.setString(c++, Utils.isNull(o.getPicRcvQtyEA()));
			ps.setString(c++, o.getCreateUser());
			ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
			
			ps.setString(c++, Utils.isNull(o.getDocNo()));

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
}
