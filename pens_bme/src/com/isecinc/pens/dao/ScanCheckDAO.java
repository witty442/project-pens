package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;


import com.isecinc.pens.bean.Barcode;
import com.isecinc.pens.bean.Job;
import com.isecinc.pens.bean.ScanCheckBean;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.process.SequenceProcess;

public class ScanCheckDAO extends PickConstants{

	protected static Logger logger = Logger.getLogger("PENS");
	protected static SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Utils.local_th);
	
	
	public static ScanCheckBean searchHead(ScanCheckBean o ) throws Exception {
		if(o.getSummaryType().equalsIgnoreCase("box")){
			return searchByBox(o);
		}else{
			return searchByDetail(o);
		}
	}
	public static ScanCheckBean save(ScanCheckBean h) throws Exception{
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			//check documentNo is insert 
			boolean found = isDataExist(conn,h);
			if(found ==false){
				//Insert new 
				h.setStatus(STATUS_OPEN);
				
				//Case New Box gen next boxNo
				if( Utils.isNull(h.getBoxNo()).equals("")){
					//case new gen boxNo
					h.setBoxNo(genBoxNo(h.getIssueReqNo())+"");
				}
				
				logger.debug("BoxNO:"+h.getBoxNo());
				//save Head
				saveHeadModel(conn, h);
				
				//delete item by box
				 deleteItemModel(conn, h);
				 
				//save line
				if(h.getItems() != null && h.getItems().size()>0){
				   for(int i=0;i<h.getItems().size();i++){
					   ScanCheckBean l = (ScanCheckBean)h.getItems().get(i);
					   l.setIssueReqNo(h.getIssueReqNo());
					   l.setBoxNo(h.getBoxNo());
					   l.setLineId(i+1);
					   l.setStatus(STATUS_OPEN);
					   
				       insertItemModel(conn, l);
				   }
				}
			}else{
				h.setStatus(STATUS_OPEN);
				//update 
				updateStatusHeadModel(conn, h);
				
				//Case New Box gen next boxNo
				if( Utils.isNull(h.getBoxNo()).equals("")){
					//case new gen boxNo
					h.setBoxNo(genBoxNo(h.getIssueReqNo())+"");
				}
				
				//delete item by box
				 deleteItemModel(conn, h);
				  
				//save line
				if(h.getItems() != null && h.getItems().size()>0){
				   for(int i=0;i<h.getItems().size();i++){
					   ScanCheckBean l = (ScanCheckBean)h.getItems().get(i);
					   l.setIssueReqNo(h.getIssueReqNo());
					   l.setBoxNo(h.getBoxNo());
					   l.setLineId(i+1);
					   l.setStatus(STATUS_OPEN);
					   
				       insertItemModel(conn, l);
				   }
				}
			}
			
			conn.commit();
			
			return h;
		}catch(Exception e){
		  conn.rollback();
		  throw e;
		}finally{
			if(conn !=null){
				conn.close();conn=null;
			}
		}
	}

	public static void deleteItemModel(Connection conn,ScanCheckBean o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("Update");
		int  c = 1;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" DELETE FROM PENSBME_SCAN_CHECKOUT_ITEM  \n");
			sql.append(" WHERE ISSUE_REQ_NO =? \n" );
			sql.append(" AND BOX_NO =? \n" );

			ps = conn.prepareStatement(sql.toString());
				
			ps.setString(c++, Utils.isNull(o.getIssueReqNo()));
			ps.setString(c++, o.getBoxNo());
			
			ps.executeUpdate();
			
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}
	
	// ( Running :  1 )			
	 public static int genBoxNo(String issueReqNo) throws Exception{
       Connection conn = null;
		   try{
			   conn = DBConnection.getInstance().getConnection(); 
			 //get Seq
			   int seq = getMaxBoxNoByIssueReqNo(conn, issueReqNo);
			   if(seq ==0){
				   seq = 1;
			   }else{
				   seq =seq+1;
			   }
			   
			   return seq;
		   }catch(Exception e){
			   throw e;
		   }finally{
			   if(conn !=null){
				   conn.close();conn=null;
			   }
		   }
	}
		 
	 
	 public static void saveHeadModel(Connection conn,ScanCheckBean o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Insert");
			try{
				
				StringBuffer sql = new StringBuffer("");
				sql.append(" INSERT INTO PENSBME_SCAN_CHECKOUT \n");
				sql.append(" (ISSUE_REQ_NO, WAREHOUSE, CUST_GROUP, CUSTOMER_NO   \n");
				sql.append(" ,CHECKOUT_DATE, TOTAL_REQ_QTY, STATUS, CREATE_DATE, CREATE_USER )  \n");
			
			    sql.append(" VALUES (?, ?, ?, ?, ?, ?, ? , ?,?) \n");
				
				ps = conn.prepareStatement(sql.toString());
					
				Date checkDate = Utils.parse( o.getCheckOutDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				
				int c =1;
				
				ps.setString(c++, o.getIssueReqNo());
				ps.setString(c++, o.getWareHouse());
				ps.setString(c++, o.getCustGroup());
				ps.setString(c++, o.getStoreCode());
				ps.setTimestamp(c++, new java.sql.Timestamp(checkDate.getTime()));
				ps.setInt(c++, o.getTotalReqQty());
				ps.setString(c++, o.getStatus());
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
	 
	 public static void insertItemModel(Connection conn,ScanCheckBean o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Insert");
			try{
				
				StringBuffer sql = new StringBuffer("");
				sql.append(" INSERT INTO PENSBME_SCAN_CHECKOUT_ITEM \n");
				sql.append(" (Issue_req_no,BOX_NO,LINE_ID" +
						   " ,BARCODE,MATERIAL_MASTER,GROUP_CODE" +
						   ",PENS_ITEM,CREATE_DATE,CREATE_USER,STATUS) \n");
			
			    sql.append(" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) \n");
				
				ps = conn.prepareStatement(sql.toString());
				
				int c =1;
				
				ps.setString(c++, o.getIssueReqNo());
				ps.setString(c++, o.getBoxNo());
				ps.setInt(c++, o.getLineId());
				ps.setString(c++, o.getBarcode());
				ps.setString(c++, o.getMaterialMaster());
				ps.setString(c++, o.getGroupCode());
				ps.setString(c++, o.getPensItem());
				
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setString(c++, o.getCreateUser());
				ps.setString(c++, o.getStatus());
				
				ps.executeUpdate();
				
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
	 
	 public static void updateScanCheckOutStatusToIssue(Connection conn,ScanCheckBean o) throws Exception{
		try{
			updateStatusHeadModel(conn,o);
			updateStatusItemModel(conn,o);
		}catch(Exception e){
			throw e;
		}
		 
	}
	 
	 public static void updateStatusHeadModel(Connection conn,ScanCheckBean o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Update");
			int  c = 1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" UPDATE PENSBME_SCAN_CHECKOUT SET  \n");
				sql.append("  STATUS = ? ,UPDATE_USER =? ,UPDATE_DATE = ?   \n");
				
				sql.append(" WHERE ISSUE_REQ_NO =? and warehouse =? \n" );

				ps = conn.prepareStatement(sql.toString());
					
				ps.setString(c++, o.getStatus());
				ps.setString(c++, o.getUpdateUser());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setString(c++, Utils.isNull(o.getIssueReqNo()));
				ps.setString(c++, Utils.isNull(o.getWareHouse()));
				
				ps.executeUpdate();
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
	
	 public static void updateStatusItemModel(Connection conn,ScanCheckBean o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Update");
			int  c = 1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" UPDATE PENSBME_SCAN_CHECKOUT_ITEM SET  \n");
				sql.append(" STATUS = ? ,UPDATE_USER =? ,UPDATE_DATE = ?   \n");
				sql.append(" WHERE ISSUE_REQ_NO =?  \n" );
				sql.append(" AND  ISSUE_REQ_NO IN(  \n" );
				sql.append("   SELECT ISSUE_REQ_NO FROM PENSBME_SCAN_CHECKOUT WHERE ISSUE_REQ_NO =? AND WAREHOUSE =? \n" );
				sql.append(" )  \n" );
				
				ps = conn.prepareStatement(sql.toString());
					
				ps.setString(c++, o.getStatus());
				ps.setString(c++, o.getUpdateUser());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setString(c++, Utils.isNull(o.getIssueReqNo()));
				ps.setString(c++, Utils.isNull(o.getIssueReqNo()));
				ps.setString(c++, Utils.isNull(o.getWareHouse()));
				ps.executeUpdate();
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
	public static ScanCheckBean searchByBox(ScanCheckBean o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		ScanCheckBean h = null;
		List<ScanCheckBean> items = new ArrayList<ScanCheckBean>();
		int r = 1;
		int totalQty = 0;
		try {
			sql.append("\n select  \n");
			sql.append("\n  h.issue_req_no,h.Checkout_date,h.cust_group,h.Customer_no, ");
			sql.append("\n  h.warehouse,h.Total_Req_Qty,h.status ,i.box_no ");
			sql.append("\n  ,(select M.pens_desc from pensbi.PENSBME_MST_REFERENCE M ");
			sql.append("\n   where M.reference_code = 'Store' and M.pens_value = h.customer_no) as customer_NAME ");
			sql.append("\n ,NVL(count(*),0) total_qty ");
			sql.append("\n  from PENSBME_SCAN_CHECKOUT h ,PENSBME_SCAN_CHECKOUT_ITEM i ");
			sql.append("\n where 1=1   ");
			sql.append("\n and h.issue_req_no = i.issue_req_no  ");
			
			if( !Utils.isNull(o.getIssueReqNo()).equals("")){
				sql.append("\n and h.issue_req_no = '"+Utils.isNull(o.getIssueReqNo())+"'");
			}
			/*if( !Utils.isNull(o.getBoxNo()).equals("")){
				sql.append("\n and i.box_no = '"+Utils.isNull(o.getBoxNo())+"'");
			}*/
			if( !Utils.isNull(o.getWareHouse()).equals("")){
				sql.append("\n and h.warehouse = '"+Utils.isNull(o.getWareHouse())+"'");
			}
			if( !Utils.isNull(o.getCustGroup()).equals("")){
				sql.append("\n and h.cust_group = '"+Utils.isNull(o.getCustGroup())+"'  ");
			}
			if( !Utils.isNull(o.getStoreCode()).equals("")){
				sql.append("\n and h.Customer_no = '"+Utils.isNull(o.getStoreCode())+"'  ");
			}
			if( !Utils.isNull(o.getStatus()).equals("")){
				sql.append("\n and h.status = '"+Utils.isNull(o.getStatus())+"'  ");
			}
			sql.append("\n group by ");
			sql.append("\n  h.issue_req_no,h.Checkout_date,h.cust_group,h.Customer_no, ");
			sql.append("\n  h.warehouse,h.Total_Req_Qty,h.status ,i.box_no ");
			
			sql.append("\n order by h.issue_req_no desc ");
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   h = new ScanCheckBean();
			   h.setNo(r);
			   h.setIssueReqNo(Utils.isNull(rst.getString("issue_req_no")));
			   h.setBoxNo(Utils.isNull(rst.getString("box_no")));
			   h.setCheckOutDate(Utils.stringValue(rst.getTimestamp("Checkout_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			   h.setStatus(Utils.isNull(rst.getString("status"))); 
			   h.setStatusDesc(PickConstants.getStatusDesc(Utils.isNull(rst.getString("status")))); 
			   h.setStoreCode(Utils.isNull(rst.getString("Customer_no"))); 
			   h.setStoreName(Utils.isNull(rst.getString("Customer_name")));
			   h.setWareHouse(Utils.isNull(rst.getString("warehouse")));
			   h.setTotalQty(rst.getInt("total_qty"));
			   
			   totalQty += h.getTotalQty();
			   
			   if(Utils.isNull(rst.getString("status")).equals(PickConstants.STATUS_ISSUED)){
				   h.setCanEdit(false);  
			   }else{
					h.setCanEdit(true); 
		       }
 
			   items.add(h);
			   r++;
			}//while

			//set Result 
			o.setTotalQty(totalQty);
			o.setItems(items);

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
				conn.close();
			} catch (Exception e) {}
		}
		return o;
	}
	public static ScanCheckBean searchByDetail(ScanCheckBean o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		ScanCheckBean h = null;
		List<ScanCheckBean> items = new ArrayList<ScanCheckBean>();
		int r = 1;
		int totalQty = 0;
		try {
			sql.append("\n select  \n");
			sql.append("\n  h.Customer_no,h.warehouse,i.material_master ");
			sql.append("\n  ,i.pens_item,i.barcode ");
			sql.append("\n  ,(select M.pens_desc from pensbi.PENSBME_MST_REFERENCE M ");
			sql.append("\n   where M.reference_code = 'Store' and M.pens_value = h.customer_no) as customer_NAME ");
			sql.append("\n ,NVL(count(*),0) total_qty ");
			sql.append("\n  from PENSBME_SCAN_CHECKOUT h ,PENSBME_SCAN_CHECKOUT_ITEM i ");
			sql.append("\n where 1=1   ");
			sql.append("\n and h.issue_req_no = i.issue_req_no  ");
			
			if( !Utils.isNull(o.getIssueReqNo()).equals("")){
				sql.append("\n and h.issue_req_no = '"+Utils.isNull(o.getIssueReqNo())+"'");
			}
			/*if( !Utils.isNull(o.getBoxNo()).equals("")){
				sql.append("\n and i.box_no = '"+Utils.isNull(o.getBoxNo())+"'");
			}*/
			if( !Utils.isNull(o.getWareHouse()).equals("")){
				sql.append("\n and h.warehouse = '"+Utils.isNull(o.getWareHouse())+"'");
			}
			if( !Utils.isNull(o.getCustGroup()).equals("")){
				sql.append("\n and h.cust_group = '"+Utils.isNull(o.getCustGroup())+"'  ");
			}
			if( !Utils.isNull(o.getStoreCode()).equals("")){
				sql.append("\n and h.Customer_no = '"+Utils.isNull(o.getStoreCode())+"'  ");
			}
			if( !Utils.isNull(o.getStatus()).equals("")){
				sql.append("\n and h.status = '"+Utils.isNull(o.getStatus())+"'  ");
			}
			sql.append("\n group by ");
			sql.append("\n h.Customer_no,h.warehouse,i.material_master,i.pens_item,i.barcode ");
			sql.append("\n order by i.material_master   ");

			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()) {
			   h = new ScanCheckBean();
			   h.setNo(r);
			   h.setMaterialMaster(Utils.isNull(rst.getString("material_master")));
			   h.setPensItem(Utils.isNull(rst.getString("pens_item")));
			   h.setBarcode(Utils.isNull(rst.getString("barcode"))); 
			   h.setWareHouse(Utils.isNull(rst.getString("warehouse")));
			   h.setTotalQty(rst.getInt("total_qty"));
			   h.setStoreCode(Utils.isNull(rst.getString("Customer_no"))); 
			   h.setStoreName(Utils.isNull(rst.getString("Customer_name"))); 
			   
			   totalQty += h.getTotalQty();
			  
			   items.add(h);
			   r++;
			}//while

			//set Result 
			o.setTotalQty(totalQty);
			o.setItems(items);

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
				conn.close();
			} catch (Exception e) {}
		}
		return o;
	}
	
	public static ScanCheckBean searchDetail(ScanCheckBean o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		ScanCheckBean h = null;
		List<ScanCheckBean> items = new ArrayList<ScanCheckBean>();
		int r = 1;
		int totalQty = 0;
		try {
			sql.append("\n select  \n");
			sql.append("\n  h.* ");
			sql.append("\n  from PENSBME_SCAN_CHECKOUT h  ");
			sql.append("\n where 1=1   ");
			
			if( !Utils.isNull(o.getIssueReqNo()).equals("")){
				sql.append("\n and h.issue_req_no = '"+Utils.isNull(o.getIssueReqNo())+"'");
			}
			if( !Utils.isNull(o.getWareHouse()).equals("")){
				sql.append("\n and h.warehouse = '"+Utils.isNull(o.getWareHouse())+"'");
			}
			if( !Utils.isNull(o.getCustGroup()).equals("")){
				sql.append("\n and h.cust_group = '"+Utils.isNull(o.getCustGroup())+"'  ");
			}
			if( !Utils.isNull(o.getStoreCode()).equals("")){
				sql.append("\n and h.Customer_no = '"+Utils.isNull(o.getStoreCode())+"'  ");
			}

			sql.append("\n order by h.issue_req_no desc ");
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			if(rst.next()) {
			   h = new ScanCheckBean();
			   h.setNo(r);
			   h.setIssueReqNo(Utils.isNull(rst.getString("issue_req_no")));
			   h.setBoxNo(o.getBoxNo());
			   h.setCheckOutDate(Utils.stringValue(rst.getTimestamp("Checkout_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			   h.setStatus(Utils.isNull(rst.getString("status"))); 
			   h.setStatusDesc(PickConstants.getStatusDesc(Utils.isNull(rst.getString("status")))); 
			   h.setStoreCode(Utils.isNull(rst.getString("Customer_no"))); 
			   h.setWareHouse(Utils.isNull(rst.getString("warehouse")));
			   
			   if(Utils.isNull(rst.getString("status")).equals(PickConstants.STATUS_ISSUED)){
				   h.setCanEdit(false);  
			   }else{
					h.setCanEdit(true); 
		       }
 
			   items.add(h);
			   r++;
			}//while

			//get sum all not in current box_no
			h.setTotalQty(getTotalQtyNotInBoxNo(conn,h));
			
			//search detail 
			if(h != null)
			   h.setItems(searchDetailItems(conn,h));

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
	
	public static boolean isDataExist(Connection conn,ScanCheckBean o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		boolean found = false;
		try {
			sql.append("\n select count(*) as c from PENSBME_SCAN_CHECKOUT h  ");
			sql.append("\n where 1=1   ");
		
			if( !Utils.isNull(o.getIssueReqNo()).equals("")){
				sql.append("\n and h.issue_req_no = '"+Utils.isNull(o.getIssueReqNo())+"'");
			}
			if( !Utils.isNull(o.getWareHouse()).equals("")){
				sql.append("\n and h.warehouse = '"+Utils.isNull(o.getWareHouse())+"'");
			}
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			if(rst.next()) {
				if(rst.getInt("c") > 0){
					found = true;
				}
			}//while

			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return found;
	}
	
	public static int getMaxBoxNoByIssueReqNo(Connection conn,String issueReqNo ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int maxBoxNo = 0;
		try {
			sql.append("\n select MAX(BOX_NO) as max_box_no from PENSBME_SCAN_CHECKOUT_ITEM h  ");
			sql.append("\n where 1=1   ");
		    sql.append("\n and h.issue_req_no = '"+Utils.isNull(issueReqNo)+"'");
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			if(rst.next()) {
				maxBoxNo = rst.getInt("max_box_no");
			}//while
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return maxBoxNo;
	}
	
	public static List<ScanCheckBean> searchDetailItems(Connection conn,ScanCheckBean o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		ScanCheckBean h = null;
		List<ScanCheckBean> items = new ArrayList<ScanCheckBean>();
		int r = 1;
		try {
			sql.append("\n select i.*");
			sql.append("\n from PENSBME_SCAN_CHECKOUT h, PENSBME_SCAN_CHECKOUT_ITEM i ");
			sql.append("\n where 1=1   ");
			sql.append("\n and h.issue_req_no = i.issue_req_no  ");
			if( !Utils.isNull(o.getWareHouse()).equals("")){
				sql.append("\n and h.warehouse = '"+Utils.isNull(o.getWareHouse())+"'");
			}
			if( !Utils.isNull(o.getIssueReqNo()).equals("")){
				sql.append("\n and i.issue_req_no = '"+Utils.isNull(o.getIssueReqNo())+"'");
			}
			if( !Utils.isNull(o.getBoxNo()).equals("")){
				sql.append("\n and i.box_no = '"+Utils.isNull(o.getBoxNo())+"'");
			}

			sql.append("\n  order by i.line_id asc  ");
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   h = new ScanCheckBean();
			   h.setLineId(rst.getInt("line_id"));
			   h.setMaterialMaster(Utils.isNull(rst.getString("material_master")));
			   h.setPensItem(Utils.isNull(rst.getString("pens_item")));
			   h.setBarcode(Utils.isNull(rst.getString("barcode"))); 
			   h.setGroupCode(Utils.isNull(rst.getString("group_code"))); 
			   h.setStatus(Utils.isNull(rst.getString("status"))); 
			   h.setBarcodeStyle("disableText");
			   
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
	
	public static int getTotalQtyNotInBoxNo(Connection conn,ScanCheckBean o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int r = 0;
		try {
			sql.append("\n select count(*) as qty ");
			sql.append("\n from PENSBME_SCAN_CHECKOUT h, PENSBME_SCAN_CHECKOUT_ITEM i ");
			sql.append("\n where 1=1   ");
			sql.append("\n and h.issue_req_no = i.issue_req_no  ");
			if( !Utils.isNull(o.getWareHouse()).equals("")){
				sql.append("\n and h.warehouse = '"+Utils.isNull(o.getWareHouse())+"'");
			}
			if( !Utils.isNull(o.getIssueReqNo()).equals("")){
				sql.append("\n and i.issue_req_no = '"+Utils.isNull(o.getIssueReqNo())+"'");
			}
			if( !Utils.isNull(o.getBoxNo()).equals("")){
				sql.append("\n and i.box_no <> '"+Utils.isNull(o.getBoxNo())+"'");
			}

			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			if(rst.next()) {
			   r = rst.getInt("qty");
			}//while

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return r;
	}
	
}
