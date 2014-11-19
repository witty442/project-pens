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

import util.Constants;

import com.isecinc.pens.bean.AdjustStock;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.process.SequenceProcess;
import com.isecinc.pens.web.popup.PopupForm;

public class AdjustStockDAO {
	
	protected static Logger logger = Logger.getLogger("PENS");
	protected static SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Utils.local_th);
	
	private static String BANK_NO = "01-0000-911530-000-821-000-001";
	public static String STATUS_OPEN ="O";
	public static String STATUS_CLOSE ="C";
	public static String STATUS_INTERFACED ="I";
	
	public AdjustStockDAO() {
		// TODO Auto-generated constructor stub
	}

	public static void save(AdjustStock h) throws Exception{
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			Date tDate  = Utils.parse(h.getTransactionDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			//check documentNo
			if(Utils.isNull(h.getDocumentNo()).equals("")){
				//Gen DocNo
				h.setDocumentNo(genDocumentNo(conn,tDate,h.getStoreCode()));
				h.setStatus(STATUS_OPEN);
				
				logger.debug("documentNo:"+h.getDocumentNo());
				
				//insert List
				if(h.getItems() != null && h.getItems().size() >0){
					for(int i=0;i<h.getItems().size();i++){
					   AdjustStock l = (AdjustStock)h.getItems().get(i);
					   l.setDocumentNo(h.getDocumentNo());
					   l.setTransactionDate(h.getTransactionDate());
					   l.setSubInv(h.getSubInv());
					   l.setOrg(h.getOrg());
					   l.setStoreCode(h.getStoreCode());
					   l.setStoreName(h.getStoreName());
					   l.setBankNo(h.getBankNo());
					   l.setRef(h.getRef());
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
					   AdjustStock l = (AdjustStock)h.getItems().get(i);
					   l.setDocumentNo(h.getDocumentNo());
					   l.setTransactionDate(h.getTransactionDate());
					   l.setSubInv(h.getSubInv());
					   l.setOrg(h.getOrg());
					   l.setStoreCode(h.getStoreCode());
					   l.setStoreName(h.getStoreName());
					   l.setBankNo(h.getBankNo());
					   l.setRef(h.getRef());
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
	
	public static List<AdjustStock> searchHead(AdjustStock o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		AdjustStock h = null;
		List<AdjustStock> items = new ArrayList<AdjustStock>();
		int r = 1;
		int c = 1;
		try {
			sql.append("\n SELECT DISTINCT DOCUMENT_NO,TRANSACTION_DATE,STORE_CODE,");
			sql.append("\n   STORE_NAME,BANK_NO,ORG,SUB_INV,REFERENCE,STATUS,STATUS_MESSAGE ");
			sql.append("\n from PENSBME_ADJUST_INVENTORY    \n");
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
			sql.append("\n order by document_no asc ");
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			if( !Utils.isNull(o.getTransactionDate()).equals("")){
				Date tDate  = Utils.parse(o.getTransactionDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				ps.setDate(c++,new java.sql.Date(tDate.getTime()));
			}
			
			rst = ps.executeQuery();

			while(rst.next()) {
			  
				   h = new AdjustStock();
				   h.setNo(r);
				   h.setTransactionDate(Utils.stringValue(rst.getDate("transaction_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				   h.setDocumentNo(Utils.isNull(rst.getString("document_no")));
				   h.setSubInv(Utils.isNull(rst.getString("sub_inv")));
				   h.setOrg(Utils.isNull(rst.getString("org")));
				   h.setStoreCode(Utils.isNull(rst.getString("store_code")));
				   h.setStoreName(Utils.isNull(rst.getString("store_name")));
				   h.setBankNo(Utils.isNull(rst.getString("bank_no")));
				   h.setRef(Utils.isNull(rst.getString("reference")));
				   h.setStatus(Utils.isNull(rst.getString("status"))); 
				   h.setStatusDesc(getStatusDesc(Utils.isNull(rst.getString("status")))); 
				   h.setStatusMessage(Utils.isNull(rst.getString("status_message"))); 
				   
				   if(Utils.isNull(rst.getString("status")).equals(STATUS_INTERFACED) || Utils.isNull(rst.getString("status")).equals(STATUS_CLOSE) ){
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
	
	public static AdjustStock search(AdjustStock o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		AdjustStock h = null;
		List<AdjustStock> items = new ArrayList<AdjustStock>();
		int c = 1;
		int r = 1;
		boolean found = false;
		try {
			sql.append("\n SELECT *  from PENSBME_ADJUST_INVENTORY    \n");
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
			
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			if( !Utils.isNull(o.getTransactionDate()).equals("")){
				Date tDate  = Utils.parse(o.getTransactionDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				ps.setDate(c++,new java.sql.Date(tDate.getTime()));
			}
			
			rst = ps.executeQuery();

			while(rst.next()) {
			   found = true;
			   if(r==1)	{
				   h = new AdjustStock();
				   h.setTransactionDate(Utils.stringValue(rst.getDate("transaction_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				   h.setDocumentNo(Utils.isNull(rst.getString("document_no")));
				   h.setSubInv(Utils.isNull(rst.getString("sub_inv")));
				   h.setOrg(Utils.isNull(rst.getString("org")));
				   h.setStoreCode(Utils.isNull(rst.getString("store_code")));
				   h.setStoreName(Utils.isNull(rst.getString("store_name")));
				   h.setBankNo(Utils.isNull(rst.getString("bank_no")));
				   h.setRef(Utils.isNull(rst.getString("reference")));
				   h.setStatus(Utils.isNull(rst.getString("status"))); 
				   h.setStatusDesc(getStatusDesc(Utils.isNull(rst.getString("status")))); 
				   h.setStatusMessage(Utils.isNull(rst.getString("status_message"))); 
				   
				   if(Utils.isNull(rst.getString("status")).equals(STATUS_INTERFACED) || Utils.isNull(rst.getString("status")).equals(STATUS_CLOSE) ){
					   h.setCanEdit(false);
				   }else{
					   h.setCanEdit(true); 
				   }
				   
				   if(Utils.isNull(rst.getString("status")).equals("") || Utils.isNull(rst.getString("status")).equals(STATUS_OPEN) ){
					   h.setCanExport(true);  
				   }else{
					   h.setCanExport(false);   
				   }
				   
				   //Case Found data disable transactionDate and storeCode
				   h.setDisableHead(true);
			   }   
			   
			   AdjustStock l = new AdjustStock();
			   l.setDocumentNo(h.getDocumentNo());
			   l.setTransactionDate(h.getTransactionDate());
			   l.setSeqNo(rst.getInt("seq_no"));
			   l.setSubInv(h.getSubInv());
			   l.setOrg(h.getOrg());
			   l.setStoreCode(h.getStoreCode());
			   l.setStoreName(h.getStoreName());
			   l.setBankNo(h.getBankNo());
			   l.setRef(h.getRef());
			 //check can edit
			   if(Utils.isNull(rst.getString("status")).equals(STATUS_INTERFACED) || Utils.isNull(rst.getString("status")).equals(STATUS_CLOSE) ){
				   l.setCanEdit(false);
			   }else{
				   l.setCanEdit(true); 
			   }
		      
			   l.setItemIssue(Utils.isNull(rst.getString("item_issue")));
			   l.setItemIssueDesc(Utils.isNull(rst.getString("item_issue_desc")));
			   l.setItemIssueUom(Utils.isNull(rst.getString("item_issue_uom")));
			   l.setItemIssueQty(rst.getInt("item_issue_qty"));
			   l.setItemIssueRetailNonVat(rst.getDouble("item_issue_retail_non_vat"));
			  
			   l.setItemReceipt(Utils.isNull(rst.getString("item_receipt")));
			   l.setItemReceiptDesc(Utils.isNull(rst.getString("item_receipt_desc")));
			   l.setItemReceiptUom(Utils.isNull(rst.getString("item_receipt_uom")));
			   l.setItemReceiptQty(rst.getInt("item_receipt_qty"));
			   l.setItemReceiptRetailNonVat(rst.getDouble("item_receipt_retail_non_vat"));
			   
			   l.setDiffCost(rst.getDouble("diff_cost"));
			   
			   items.add(l);
			   r++;
			   
			}//while
			
			if(!found){
			  h = getOrgSubInv(conn, o);
			  h.setBankNo(BANK_NO);
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
		}else if(STATUS_CLOSE.equals(status)){
			d = "CLOSE";
		}else if(STATUS_INTERFACED.equals(status)){
			d = "INTERFACED";
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
	
	public static AdjustStock getOrgSubInv(Connection conn,AdjustStock o) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		try {
			sql.delete(0, sql.length());
			sql.append("\n SELECT interface_value, interface_desc FROM  ");
			sql.append("\n PENSBI.PENSBME_MST_REFERENCE where reference_code = 'SubInv' and pens_value ='"+o.getStoreCode()+"'");
			sql.append("\n  ORDER BY interface_value asc \n");
			
			logger.debug("sql:"+sql);
			conn = DBConnection.getInstance().getConnection();
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			
			if (rst.next()) {
				o.setOrg(rst.getString("interface_value"));
				o.setSubInv(rst.getString("interface_desc"));
				o.setBankNo(BANK_NO);
				o.setStoreName(getStoreName(conn,o.getStoreCode()));
			}//while

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				stmt.close();	
			} catch (Exception e) {}
		}
		return o;
	}
	
	// ( Running :  yyyymm+running  เช่น 201403001 )			
	 private static String genDocumentNo(Connection conn,Date tDate,String storeCode) throws Exception{
		   String orderNo = "";
		   try{
			   String today = df.format(tDate);
			   String[] d1 = today.split("/");
			   int curYear = Integer.parseInt(d1[0].substring(0,4));
			   int curMonth = Integer.parseInt(d1[1]);
			 
			   //get Seq
			   int seq = SequenceProcess.getNextValue(conn,"ADJUST", "DOCUMENT_NO",tDate);
			   
			   orderNo = new DecimalFormat("0000").format(curYear)+new DecimalFormat("00").format(curMonth)+new DecimalFormat("000").format(seq);
		   }catch(Exception e){
			   throw e;
		   }
		  return orderNo;
	}
	 
	
	
	private static void saveModel(Connection conn,AdjustStock o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("Insert");
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" INSERT INTO PENSBI.PENSBME_ADJUST_INVENTORY \n");
			sql.append(" (DOCUMENT_NO, SEQ_NO, TRANSACTION_DATE, STORE_CODE,  \n");
			sql.append(" STORE_NAME, BANK_NO, ORG, SUB_INV,  \n");
			sql.append(" REFERENCE, STATUS, ITEM_ISSUE, ITEM_ISSUE_DESC,  \n");
			sql.append(" ITEM_ISSUE_UOM, ITEM_ISSUE_QTY, ITEM_ISSUE_RETAIL_NON_VAT, ITEM_RECEIPT,  \n");
			sql.append(" ITEM_RECEIPT_DESC, ITEM_RECEIPT_UOM, ITEM_RECEIPT_QTY, ITEM_RECEIPT_RETAIL_NON_VAT,  \n");
			sql.append(" DIFF_COST, CREATE_DATE, CREATE_USER) \n");

		    sql.append(" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) \n");
			
			ps = conn.prepareStatement(sql.toString());
				
			Date tDate = Utils.parse( o.getTransactionDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			int c =1;
			
			ps.setString(c++, o.getDocumentNo());
			ps.setInt(c++, o.getSeqNo());
			ps.setTimestamp(c++, new java.sql.Timestamp(tDate.getTime()));
			ps.setString(c++, o.getStoreCode());
			
			ps.setString(c++, o.getStoreName());
			ps.setString(c++, o.getBankNo());
			ps.setString(c++, o.getOrg());
			ps.setString(c++, o.getSubInv());
			
			ps.setString(c++, o.getRef());
			ps.setString(c++, o.getStatus());
			
			ps.setString(c++, o.getItemIssue());
			ps.setString(c++, o.getItemIssueDesc());
			ps.setString(c++, o.getItemIssueUom());
			ps.setDouble(c++, o.getItemIssueQty());
			ps.setDouble(c++, o.getItemIssueRetailNonVat());
			
			ps.setString(c++, o.getItemReceipt());
			ps.setString(c++, o.getItemReceiptDesc());
			ps.setString(c++, o.getItemReceiptUom());
			ps.setDouble(c++, o.getItemReceiptQty());
			ps.setDouble(c++, o.getItemReceiptRetailNonVat());
			
			ps.setDouble(c++, o.getDiffCost());
			
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

	public static void deleteModel(Connection conn,AdjustStock o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("Update");
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" DELETE PENSBI.PENSBME_ADJUST_INVENTORY \n");
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
	
	public static void updateStatus(Connection conn,AdjustStock o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("Update");
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" UPDATE PENSBI.PENSBME_ADJUST_INVENTORY SET STATUS = ? \n");
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
