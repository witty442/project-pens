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

import com.isecinc.pens.bean.AdjustStockSA;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.dao.constants.Constants;
import com.isecinc.pens.web.popup.PopupForm;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.Utils;
import com.pens.util.seq.SequenceProcess;

public class AdjustStockSADAO {
	
	protected static Logger logger = Logger.getLogger("PENS");
	protected static SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Utils.local_th);
	
	private static String BANK_NO = "01-0000-911530-000-821-000-001";
	public static String STATUS_OPEN ="O";
	public static String STATUS_CANCEL ="AB";
	static int count = 0;
	
	public AdjustStockSADAO() {
		// TODO Auto-generated constructor stub
	}

	public static void save(AdjustStockSA h) throws Exception{
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			Date tDate  = DateUtil.parse(h.getTransactionDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			//check documentNo
			if(Utils.isNull(h.getDocumentNo()).equals("")){
				//Gen DocNo
				h.setDocumentNo(genDocumentNo(tDate));
				h.setStatus(STATUS_OPEN);
				
				logger.debug("documentNo:"+h.getDocumentNo());
				
				//insert List
				if(h.getItems() != null && h.getItems().size() >0){
					for(int i=0;i<h.getItems().size();i++){
					   AdjustStockSA l = (AdjustStockSA)h.getItems().get(i);
					   l.setDocumentNo(h.getDocumentNo());
					   l.setTransactionDate(h.getTransactionDate());
					
					   l.setStoreCode(h.getStoreCode());
					   l.setStoreName(h.getStoreName());
					  
					   l.setCreateUser(h.getCreateUser());
					   l.setUpdateUser(h.getUpdateUser());
					   l.setStatus(h.getStatus());
					   
				       saveModel(conn, l);
					}
				}
			}else{
				//delete by document_no
				deleteModel(conn, h);
				
				//insert List
				if(h.getItems() != null && h.getItems().size() >0){
					for(int i=0;i<h.getItems().size();i++){
					   AdjustStockSA l = (AdjustStockSA)h.getItems().get(i);
					   l.setDocumentNo(h.getDocumentNo());
					   l.setTransactionDate(h.getTransactionDate());
					   l.setStoreCode(h.getStoreCode());
					   l.setStoreName(h.getStoreName());
					   l.setCreateUser(h.getCreateUser());
					   l.setUpdateUser(h.getUpdateUser());
					   l.setStatus(STATUS_OPEN);
					   
				       saveModel(conn, l);
					}
				}
			}
			
			conn.commit();
		}catch(Exception e){
		  conn.rollback();
		  throw e;
		}finally{
			if(conn !=null){
				conn.close();conn=null;
			}
		}
	}
	
	public static List<AdjustStockSA> searchHead(AdjustStockSA o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		AdjustStockSA h = null;
		List<AdjustStockSA> items = new ArrayList<AdjustStockSA>();
		int r = 1;
		int c = 1;
		try {
			sql.append("\n SELECT DISTINCT DOCUMENT_NO,TRANSACTION_DATE,STORE_CODE,");
			sql.append("\n   STORE_NAME,STATUS,STATUS_MESSAGE ");
			sql.append("\n from PENSBME_ADJUST_SALES    \n");
			sql.append("\n where 1=1   \n");
			
			if( !Utils.isNull(o.getDocumentNo()).equals("")){
				sql.append("\n and document_no = '"+Utils.isNull(o.getDocumentNo())+"'  ");
			}
			
			if( !Utils.isNull(o.getStoreCode()).equals("")){
				sql.append("\n and store_code = '"+Utils.isNull(o.getStoreCode())+"'  ");
			}
			
			if( !Utils.isNull(o.getTransactionDate()).equals("")){
				sql.append("\n and TRANSACTION_DATE = ? ");
			}
			sql.append("\n order by document_no desc ");
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			if( !Utils.isNull(o.getTransactionDate()).equals("")){
				Date tDate  = DateUtil.parse(o.getTransactionDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				ps.setDate(c++,new java.sql.Date(tDate.getTime()));
			}
			
			rst = ps.executeQuery();

			while(rst.next()) {
			  
				   h = new AdjustStockSA();
				   h.setNo(r);
				   h.setTransactionDate(DateUtil.stringValue(rst.getDate("transaction_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				   h.setDocumentNo(Utils.isNull(rst.getString("document_no")));
				   h.setStoreCode(Utils.isNull(rst.getString("store_code")));
				   h.setStoreName(Utils.isNull(rst.getString("store_name")));
				   h.setStatus(Utils.isNull(rst.getString("status"))); 
				   h.setStatusDesc(getStatusDesc(Utils.isNull(rst.getString("status")))); 
				   h.setStatusMessage(Utils.isNull(rst.getString("status_message"))); 
				   
				   if(Utils.isNull(rst.getString("status")).equals(STATUS_CANCEL) ){
					   h.setCanEdit(false);
				   }else{
					   h.setCanEdit(true); 
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
				conn.close();
			} catch (Exception e) {}
		}
		return items;
	}
	
	public static AdjustStockSA searchDetail(AdjustStockSA o ,String orderBy) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		AdjustStockSA h = null;
		List<AdjustStockSA> items = new ArrayList<AdjustStockSA>();
		int c = 1;
		int r = 1;
		boolean found = false;
		try {
			sql.append("\n SELECT *  from PENSBME_ADJUST_SALES ");
			sql.append("\n where 1=1 ");
			
			if( !Utils.isNull(o.getDocumentNo()).equals("")){
				sql.append("\n and document_no = '"+Utils.isNull(o.getDocumentNo())+"'  ");
			}
			
			if( !Utils.isNull(o.getStoreCode()).equals("")){
				sql.append("\n and store_code = '"+Utils.isNull(o.getStoreCode())+"'  ");
			}
			if( !Utils.isNull(o.getTransactionDate()).equals("")){
				sql.append("\n and TRANSACTION_DATE = ? ");
			}
			sql.append("\n "+orderBy);
			
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			if( !Utils.isNull(o.getTransactionDate()).equals("")){
				Date tDate  = DateUtil.parse(o.getTransactionDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				ps.setDate(c++,new java.sql.Date(tDate.getTime()));
			}
			
			rst = ps.executeQuery();

			while(rst.next()) {
			   found = true;
			   if(r==1)	{
				   h = new AdjustStockSA();
				   h.setTransactionDate(DateUtil.stringValue(rst.getDate("transaction_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				   h.setDocumentNo(Utils.isNull(rst.getString("document_no")));
				   h.setStoreCode(Utils.isNull(rst.getString("store_code")));
				   h.setStoreName(Utils.isNull(rst.getString("store_name")));
				   h.setStatus(Utils.isNull(rst.getString("status"))); 
				   h.setStatusDesc(getStatusDesc(Utils.isNull(rst.getString("status")))); 
				   h.setStatusMessage(Utils.isNull(rst.getString("status_message"))); 
				   
				   if(Utils.isNull(rst.getString("status")).equals(STATUS_CANCEL) ){
					   h.setCanEdit(false);
				   }else{
					   h.setCanEdit(true); 
				   }
				   
				   if( Utils.isNull(rst.getString("status")).equals(STATUS_OPEN) ){
					   h.setCanCancel(true);  
				   }else{
					   h.setCanCancel(false);   
				   }
				   
				   //Case Found data disable transactionDate and storeCode
				   h.setDisableHead(true);
			   }   
			   
			   AdjustStockSA l = new AdjustStockSA();
			   l.setDocumentNo(h.getDocumentNo());
			   l.setTransactionDate(h.getTransactionDate());
			   l.setSeqNo(rst.getInt("seq_no"));
			
			   l.setStoreCode(h.getStoreCode());
			   l.setStoreName(h.getStoreName());
			   
			 //check can edit
			   if(Utils.isNull(rst.getString("status")).equals(STATUS_CANCEL) ){
				   l.setCanEdit(false);
			   }else{
				   l.setCanEdit(true); 
			   }
		      
			   l.setItemAdjust(Utils.isNull(rst.getString("item_adjust")));
			   l.setGroupCode(Utils.isNull(rst.getString("item_adjust_desc")));
			   l.setItemAdjustUom(Utils.isNull(rst.getString("item_adjust_uom")));
			   l.setItemAdjustQty(rst.getString("item_adjust_qty"));
			  
			   
			   items.add(l);
			   r++;
			   
			}//while
			
			if(!found){
			
			  h.setTransactionDate(o.getTransactionDate());
			  h.setStoreCode(o.getStoreCode());
			  h.setStoreName(o.getStoreName());
			  
			}else{
			  h.setItems(items);
			}
			h.setStoreName(getStoreName(conn,h.getStoreCode()));
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
				conn.close();
			} catch (Exception e) {}
		}
		return h;
	}

	
	public static String getStatusDesc(String status){
		String d = "";
		if(STATUS_OPEN.equals(status)){
			d ="OPEN";
		}else if(STATUS_CANCEL.equals(status)){
			d = "CANCEL";
		}
		return d;
	}
	
	public static String getStoreName(Connection conn,String storeCode) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		String storeName = "";
		StringBuilder sql = new StringBuilder();
		try {
			sql.delete(0, sql.length());
			sql.append("\n  SELECT * from PENSBME_MST_REFERENCE ");
			sql.append("\n  where 1=1 and reference_code ='Store' ");
		    sql.append("\n and pens_value ='"+storeCode+"' \n");

			logger.debug("sql:"+sql);
			conn = DBConnection.getInstance().getConnection();
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
		
			if (rst.next()) {
				storeName = Utils.isNull(rst.getString("pens_desc"));
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
		return storeName;
	}
	
	public static boolean isDuplicateDocNo(String docNo) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		boolean dup = false;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
			sql.delete(0, sql.length());
			sql.append("\n  SELECT count(*) as c from PENSBME_ADJUST_SALES ");
			sql.append("\n  where document_no ='"+docNo+"' \n");

			logger.debug("sql:"+sql);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
		
			if (rst.next()) {
				if(rst.getInt("c") >0){
					dup = true;
				}
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
		return dup;
	}
	
	 private static String genDocumentNo(Date tDate) {
		 String docNo = "";
		 count++;
		 try{
			 docNo = genDocumentNoModel(tDate);
			 boolean isDup =isDuplicateDocNo(docNo);
			 logger.debug("count["+count+"]:docNo["+docNo+"] isDup["+isDup+"]");
			 if(isDup){
				 docNo = genDocumentNo(tDate);
			 }
		 }catch(Exception e){
			 logger.error(e.getMessage(),e);
		 }finally{
			
		 }
		 return docNo;
	 }
	 
	// ( Running :  yyyymm+running  เช่น 201403001 )			
	 private static String genDocumentNoModel(Date tDate) {
		   String docNo = "";
		   Connection conn = null;
		   try{
			   conn = DBConnection.getInstance().getConnection();
			   String today = df.format(tDate);
			   String[] d1 = today.split("/");
			   int curYear = Integer.parseInt(d1[0].substring(0,4));
			   int curMonth = Integer.parseInt(d1[1]);
			 
			   //get Seq
			   int seq = SequenceProcess.getNextValue(conn,"ADJUST_SA", "DOCUMENT_NO",tDate);
			   
			   docNo = new DecimalFormat("0000").format(curYear)+new DecimalFormat("00").format(curMonth)+new DecimalFormat("000").format(seq);
		   }catch(Exception e){
			  logger.error(e.getMessage(),e);
		   }finally{
			   try{
				   if(conn != null){
					   conn.close();conn=null;
				   }
			   }catch(Exception e){}
		   }
		  return docNo;
	}
	 
	
	
	private static void saveModel(Connection conn,AdjustStockSA o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("Insert");
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" INSERT INTO PENSBI.PENSBME_ADJUST_SALES \n");
			sql.append(" (DOCUMENT_NO, SEQ_NO, TRANSACTION_DATE, STORE_CODE,  \n");
			sql.append(" STORE_NAME,STATUS, ITEM_ADJUST, item_adjust_desc,   \n");
			sql.append(" ITEM_ADJUST_UOM, ITEM_ADJUST_QTY,  \n");
			sql.append(" CREATE_DATE, CREATE_USER) \n");
		    sql.append(" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?) \n");
			
			ps = conn.prepareStatement(sql.toString());
				
			Date tDate = DateUtil.parse( o.getTransactionDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			int c =1;
			
			ps.setString(c++, o.getDocumentNo());
			ps.setInt(c++, o.getSeqNo());
			ps.setTimestamp(c++, new java.sql.Timestamp(tDate.getTime()));
			ps.setString(c++, o.getStoreCode());
			
			ps.setString(c++, o.getStoreName());
			
			ps.setString(c++, o.getStatus());
			
			ps.setString(c++, o.getItemAdjust());
			ps.setString(c++, o.getGroupCode());
			ps.setString(c++, o.getItemAdjustUom());
			if( !Utils.isNull(o.getItemAdjustQty()).equals("")){
			   ps.setDouble(c++, Integer.parseInt(o.getItemAdjustQty()));
			}else{
			   ps.setDouble(c++,0);

			}
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

	public static void deleteModel(Connection conn,AdjustStockSA o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("Update");
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" DELETE PENSBME_ADJUST_SALES \n");
			sql.append(" WHERE DOCUMENT_NO =? \n" );

			ps = conn.prepareStatement(sql.toString());
				
			ps.setString(1, o.getDocumentNo());
			
			ps.executeUpdate();
			
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}
	
	public static void updateStatus(Connection conn,AdjustStockSA o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("Update");
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" UPDATE PENSBME_ADJUST_SALES SET STATUS = ? \n");
			sql.append(" WHERE DOCUMENT_NO =? \n" );

			ps = conn.prepareStatement(sql.toString());
			
			ps.setString(1, o.getStatus());	
			ps.setString(2, o.getDocumentNo());
			
			ps.executeUpdate();
			
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}

}
