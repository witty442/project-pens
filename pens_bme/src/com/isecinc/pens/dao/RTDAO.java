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
import com.isecinc.pens.bean.RTBean;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.web.rt.RTConstant;
import com.pens.util.BahtText;
import com.pens.util.Utils;
import com.pens.util.helper.SequenceProcess;


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
						docDate  = Utils.parse(o.getDocDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
						String dateStr = Utils.stringValue(docDate, Utils.DD_MM_YYYY_WITH_SLASH,Locale.US);
						sql.append("\n and h.tran_date = to_date('"+dateStr+"','dd/mm/yyyy')");
					    
					}
					if( !Utils.isNull(o.getNoPicRcv()).equals("")){
						sql.append("\n and h.PIC_RCV_DATE IS NULL ");
					}
					if( !Utils.isNull(o.getStoreCode()).equals("")){
						sql.append("\n and h.cust_no ='"+o.getStoreCode()+"'");
					}
					if( !Utils.isNull(o.getRefDoc()).equals("")){
						sql.append("\n and h.ref_doc ='"+o.getRefDoc()+"'");
					}
					if( !Utils.isNull(o.getRtnNo()).equals("")){
						sql.append("\n and h.rtn_no ='"+o.getRtnNo()+"'");
					}
					if( !Utils.isNull(o.getCustGroup()).equals("")){
						sql.append("\n and h.cust_group ='"+o.getCustGroup()+"'");
					}
					
					if( !Utils.isNull(o.getStatus()).equals("") && !Utils.isNull(o.getStatus()).equals("ALL")){
						sql.append("\n and h.status ='"+o.getStatus()+"'");
					}
					
					if( !Utils.isNull(o.getDeliveryDate()).equals("")){
						docDate  = Utils.parse(o.getDeliveryDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
						String dateStr = Utils.stringValue(docDate, Utils.DD_MM_YYYY_WITH_SLASH,Locale.US);
						sql.append("\n and h.delivery_date = to_date('"+dateStr+"','dd/mm/yyyy')");
					    
					}
					if( !Utils.isNull(o.getDeliveryBy()).equals("")){
						sql.append("\n and h.delivery_by ='"+o.getDeliveryBy()+"'");
					}
					
					if(Utils.isNull(o.getOrderType()).equals("docDate")){
						sql.append("\n order by h.TRAN_DATE desc ");
					}else if(Utils.isNull(o.getOrderType()).equals("docNo")){
						sql.append("\n order by h.Authorize_no desc ");
					}
					
					logger.debug("sql:"+sql);
					
					ps = conn.prepareStatement(sql.toString());
					
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
					   h.setStoreFullName(h.getStoreCode()+":"+h.getStoreName());
					   
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
					   h.setRemark(Utils.isNull(rst.getString("remark")));
					    
					   h.setDeliveryBy(Utils.isNull(rst.getString("delivery_by")));
					   if(rst.getDate("delivery_date") != null){
					     h.setDeliveryDate(Utils.stringValue(rst.getDate("delivery_date"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
					   }else{
						 h.setDeliveryDate("");
					   }
					   h.setDeliveryQty(Utils.isNull(rst.getString("delivery_qty")));
					   h.setAttach1(Utils.isNull(rst.getString("attach1")));
					   h.setAttach2(Utils.isNull(rst.getString("attach2")));
					   h.setAttach3(Utils.isNull(rst.getString("attach3")));
					   h.setAttach4(Utils.isNull(rst.getString("attach4")));
					   h.setRemarkTeamPic(Utils.isNull(rst.getString("Remark_team_Pic")));
					   
					  //
					  if(RTConstant.STATUS_OPEN.equals(h.getStatus())){
						  h.setCanSave(true);
						  h.setCanComplete(true);
						  h.setCanCancel(true);
					  }else  if(RTConstant.STATUS_COOMFIRM.equals(h.getStatus())){
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
	
		public static RTBean searchRTDetail(Connection conn,RTBean o) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			RTBean h = null;
			int r = 1;
			Date docDate=null;
			try {
			    sql.append("\n select h.* " +
			    		"\n ,(select pens_desc FROM PENSBME_MST_REFERENCE M WHERE 1=1  " +
						"\n     and M.reference_code = 'Store' and M.pens_value = h.cust_no) as store_name  "+
						"\n ,(select interface_desc FROM PENSBME_MST_REFERENCE M WHERE 1=1  " +
						"\n     and M.reference_code = 'Idwacoal' and M.pens_value = h.cust_group) as cust_group_name  "+
			    		"\n from PENSBME_RTN_CONTROL h where 1=1 ");

					sql.append("\n and h.Authorize_no = '"+Utils.isNull(o.getDocNo())+"'");
					
					docDate  = Utils.parse(o.getDocDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					String dateStr = Utils.stringValue(docDate, Utils.DD_MM_YYYY_WITH_SLASH,Locale.US);
					sql.append("\n and h.tran_date = to_date('"+dateStr+"','dd/mm/yyyy')");
					
					sql.append("\n and h.cust_no ='"+o.getStoreCode()+"'");
					sql.append("\n and h.cust_group ='"+o.getCustGroup()+"'");
				
				logger.debug("sql:"+sql);
				
				ps = conn.prepareStatement(sql.toString());
				
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
				   h.setStoreFullName(h.getStoreCode()+":"+h.getStoreName());
				   
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
				   h.setRemark(Utils.isNull(rst.getString("remark")));
				    
				   h.setDeliveryBy(Utils.isNull(rst.getString("delivery_by")));
				   if(rst.getDate("delivery_date") != null){
				     h.setDeliveryDate(Utils.stringValue(rst.getDate("delivery_date"),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				   }else{
					 h.setDeliveryDate("");
				   }
				   h.setDeliveryQty(Utils.isNull(rst.getString("delivery_qty")));
				   h.setAttach1(Utils.isNull(rst.getString("attach1")));
				   h.setAttach2(Utils.isNull(rst.getString("attach2")));
				   h.setAttach3(Utils.isNull(rst.getString("attach3")));
				   h.setAttach4(Utils.isNull(rst.getString("attach4")));
				   h.setRemarkTeamPic(Utils.isNull(rst.getString("Remark_team_Pic")));
				   
				  //
				  if(RTConstant.STATUS_OPEN.equals(h.getStatus())){
					  h.setCanSave(true);
					  h.setCanComplete(true);
					  h.setCanCancel(true);
				  }else  if(RTConstant.STATUS_COOMFIRM.equals(h.getStatus())){
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
		
	public static void insertRTNControlBySale(Connection conn,RTBean o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("Insert");
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" INSERT INTO PENSBI.PENSBME_RTN_CONTROL \n");
			sql.append(" (AUTHORIZE_NO, TRAN_DATE, REF_DOC, CUST_GROUP, \n");
			sql.append(" CUST_NO, RTN_NO, RTN_QTY_CTN, RTN_QTY_EA,  \n");
			sql.append(" STATUS, CREATE_DATE, CREATE_USER,REMARK  \n");
			sql.append(" ,delivery_by,delivery_date ,delivery_qty \n");
			sql.append(" ,attach1 ,attach2,attach3,attach4 ) ");
			sql.append(" VALUES \n"); 
			sql.append(" (?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?,?,?,?,?,?,?) \n");
			
			ps = conn.prepareStatement(sql.toString());
			
			Date docDate = Utils.parse(o.getDocDate(),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			
			int c =1;
			ps.setString(c++, o.getDocNo());
			ps.setDate(c++, new java.sql.Date(docDate.getTime()));
			ps.setString(c++, Utils.isNull(o.getRefDoc()));
			ps.setString(c++, Utils.isNull(o.getCustGroup()));
			ps.setString(c++, Utils.isNull(o.getStoreCode()));
			
			ps.setString(c++, Utils.isNull(o.getRtnNo()));
			ps.setString(c++, Utils.isNull(o.getRtnQtyCTN()));
			ps.setString(c++, Utils.isNull(o.getRtnQtyEA()));

			ps.setString(c++, Utils.isNull(o.getStatus()));
			ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(c++, o.getCreateUser());
			ps.setString(c++, Utils.isNull(o.getRemark()));
			
			ps.setString(c++, Utils.isNull(o.getDeliveryBy()));
			if( !Utils.isNull(o.getDeliveryDate()).equals("")){
			   Date d = Utils.parse(Utils.isNull(o.getDeliveryDate()), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			   ps.setDate(c++, new java.sql.Date(d.getTime()));
			}else{
			   ps.setDate(c++,null);
			}
			ps.setString(c++, Utils.isNull(o.getDeliveryQty()));
			ps.setString(c++, Utils.isNull(o.getAttach1()));
			ps.setString(c++, Utils.isNull(o.getAttach2()));
			ps.setString(c++, Utils.isNull(o.getAttach3()));
			ps.setString(c++, Utils.isNull(o.getAttach4()));
			
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
			sql.append(" UPDATE_USER =? ,UPDATE_DATE = ? ,REMARK =? ,delivery_by=?,delivery_date=? ,delivery_qty=?, \n");
			sql.append(" attach1 =?,attach2=?,attach3=?,attach4=? ");
			
			sql.append(" WHERE AUTHORIZE_NO = ?  \n" );

			ps = conn.prepareStatement(sql.toString());
			
			ps.setString(c++, Utils.isNull(o.getStoreCode()));
			ps.setString(c++, Utils.isNull(o.getRefDoc()));
			ps.setString(c++, Utils.isNull(o.getRtnNo()));
			ps.setString(c++, Utils.isNull(o.getRtnQtyCTN()));
			ps.setString(c++, Utils.isNull(o.getRtnQtyEA()));
			ps.setString(c++, o.getCreateUser());
			ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(c++, Utils.isNull(o.getRemark()));
			
			ps.setString(c++, Utils.isNull(o.getDeliveryBy()));
			if( !Utils.isNull(o.getDeliveryDate()).equals("")){
			   Date d = Utils.parse(Utils.isNull(o.getDeliveryDate()), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			   ps.setDate(c++, new java.sql.Date(d.getTime()));
			}else{
			   ps.setDate(c++,null);
			}
			ps.setString(c++, Utils.isNull(o.getDeliveryQty()));
			ps.setString(c++, Utils.isNull(o.getAttach1()));
			ps.setString(c++, Utils.isNull(o.getAttach2()));
			ps.setString(c++, Utils.isNull(o.getAttach3()));
			ps.setString(c++, Utils.isNull(o.getAttach4()));
			
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
			sql.append(" UPDATE_USER =? ,UPDATE_DATE = ? ,REMARK_TEAM_PIC =?   \n");
			sql.append(" WHERE AUTHORIZE_NO = ?  \n" );

			ps = conn.prepareStatement(sql.toString());
			
			if( !Utils.isNull(o.getPicRcvDate()).equals("") ){
			    picRcvDate = Utils.parse(o.getPicRcvDate(),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			}
			
			ps.setString(c++, Utils.isNull(o.getStatus()));
			if( !Utils.isNull(o.getPicRcvDate()).equals("") ){
				ps.setTimestamp(c++, new java.sql.Timestamp(picRcvDate.getTime()));
			 }else{
				 ps.setTimestamp(c++, null);
			 }
			ps.setString(c++, Utils.isNull(o.getPicRcvQtyCTN()));
			ps.setString(c++, Utils.isNull(o.getPicRcvQtyEA()));
			ps.setString(c++, o.getCreateUser());
			ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(c++, Utils.isNull(o.getRemarkTeamPic()));
			
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
	 
	 public static boolean validRefDoc(String refDoc) throws Exception{
		    Connection conn = null;
			PreparedStatement ps = null;
			ResultSet rs = null;
			try{
				conn = DBConnection.getInstance().getConnection();
				StringBuffer sql = new StringBuffer("");
				sql.append(" select count(*) as c from  PENSBME_RTN_CONTROL where ref_doc ='"+refDoc+"' and status <> 'AB' \n" );
                logger.debug("sql:/n"+sql.toString());
                
				ps = conn.prepareStatement(sql.toString());
				rs = ps.executeQuery();
				if(rs.next()){
					if(rs.getInt("c")>0){
						return false;
					}
				}
				
				return true;
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
				if(conn != null){
					conn.close();conn=null;
				}
			}
		}
	 
	 public static boolean validRtnNo(String rtnNo) throws Exception{
		    Connection conn = null;
			PreparedStatement ps = null;
			ResultSet rs = null;
			try{
				conn = DBConnection.getInstance().getConnection();
				StringBuffer sql = new StringBuffer("");
				sql.append(" select count(*) as c from  PENSBME_RTN_CONTROL where rtn_no ='"+rtnNo+"' and status <> 'AB' \n" );
             logger.debug("sql:/n"+sql.toString());
             
				ps = conn.prepareStatement(sql.toString());
				rs = ps.executeQuery();
				if(rs.next()){
					if(rs.getInt("c")>0){
						return false;
					}
				}
				
				return true;
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
				if(conn != null){
					conn.close();conn=null;
				}
			}
		}
}
