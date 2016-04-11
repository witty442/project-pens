package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.Barcode;
import com.isecinc.pens.bean.Job;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.process.SequenceProcess;

public class JobDAO extends PickConstants{

	protected static Logger logger = Logger.getLogger("PENS");
	protected static SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Utils.local_th);

	public static List<Job> searchHead(Job o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		Job h = null;
		List<Job> items = new ArrayList<Job>();
		int r = 1;
		int c = 1;
		try {

			sql.append("\n select J.* " +
			        " \n ,(select M.Interface_desc " +
			        " \n   from PENSBME_MST_REFERENCE M WHERE 1=1 " +
			        " \n  and M.pens_value =J.cust_group  and M.reference_code = 'Idwacoal') as cust_group_desc "+
			       
			        " \n  ,(SELECT m.pens_desc from PENSBME_MST_REFERENCE m "+
					" \n    where 1=1  and j.store_code = m.pens_value and m.reference_code ='Store') as store_name "+
			        
					"\n from PENSBME_PICK_JOB J    ");
			sql.append("\n where 1=1   ");
			
			if( !Utils.isNull(o.getJobId()).equals("")){
				sql.append("\n and job_id = "+Utils.isNull(o.getJobId())+"");
			}
			if( !Utils.isNull(o.getRefDoc()).equals("")){
				sql.append("\n and ref_doc = '"+Utils.isNull(o.getRefDoc())+"'");
			}
			
			if( !Utils.isNull(o.getWareHouse()).equals("")){
				sql.append("\n and warehouse = '"+Utils.isNull(o.getWareHouse())+"'");
			}
			
			if( !Utils.isNull(o.getName()).equals("")){
				sql.append("\n and name like '%"+Utils.isNull(o.getName())+"%'  ");
			}
			
			if( !Utils.isNull(o.getStoreCode()).equals("")){
				sql.append("\n and store_code = '"+Utils.isNull(o.getStoreCode())+"'  ");
			}
			if( !Utils.isNull(o.getStoreNo()).equals("")){
				sql.append("\n and store_no = '"+Utils.isNull(o.getStoreNo())+"'  ");
			}
			if( !Utils.isNull(o.getSubInv()).equals("")){
				sql.append("\n and sub_inv = '"+Utils.isNull(o.getSubInv())+"'  ");
			}
			
			if( !Utils.isNull(o.getStatus()).equals("")){
				sql.append("\n and STATUS = '"+Utils.isNull(o.getStatus())+"' ");
			}
			if( !Utils.isNull(o.getOpenDate()).equals("")){
				sql.append("\n and OPEN_DATE = ? ");
			}
			if( !Utils.isNull(o.getCloseDate()).equals("")){
				sql.append("\n and CLOSE_DATE = ? ");
			}
			
			if( !Utils.isNull(o.getCustGroup()).equals("")){
				sql.append("\n and cust_group like '%"+Utils.isNull(o.getCustGroup())+"%'  ");
			}
			
			sql.append("\n order by job_id desc ");
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			
			if( !Utils.isNull(o.getOpenDate()).equals("")){
				Date tDate  = Utils.parse(o.getOpenDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				ps.setDate(c++,new java.sql.Date(tDate.getTime()));
			}
			if( !Utils.isNull(o.getCloseDate()).equals("")){
				Date tDate  = Utils.parse(o.getCloseDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				ps.setDate(c++,new java.sql.Date(tDate.getTime()));
			}
			
			rst = ps.executeQuery();
			while(rst.next()) {
				   h = new Job();
				   h.setNo(r);
				   h.setWareHouse(Utils.isNull(rst.getString("warehouse"))); 
				   h.setCustGroup(Utils.isNull(rst.getString("cust_group")));
				   h.setCustGroupDesc(Utils.isNull(rst.getString("cust_group_desc")));
				   h.setJobId(rst.getString("job_id"));
				   h.setOpenDate(Utils.stringValue(rst.getTimestamp("open_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				   
				   if(rst.getDate("close_date") !=null){
				      h.setCloseDate(Utils.stringValue(rst.getDate("close_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				   }
				   h.setName(Utils.isNull(rst.getString("name"))); 
				   h.setStatus(Utils.isNull(rst.getString("status"))); 
				   h.setStatusDesc(getStatusDesc(Utils.isNull(rst.getString("status")))); 
				   h.setStatusMessage(Utils.isNull(rst.getString("status_message"))); 
				   
				   h.setStoreCode(Utils.isNull(rst.getString("store_code"))); 
				   h.setStoreName(Utils.isNull(rst.getString("store_name"))); 
				   h.setStoreNo(Utils.isNull(rst.getString("store_no"))); 
				   h.setSubInv(Utils.isNull(rst.getString("sub_inv"))); 
				   h.setRefDoc(Utils.isNull(rst.getString("ref_doc")));
				   
				   if(Utils.isNull(rst.getString("status")).equals(STATUS_CANCEL) 
					|| Utils.isNull(rst.getString("status")).equals(STATUS_CLOSE) ){
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
	
	public static Job search(Connection conn,Job o ) throws Exception {
		try{
			return searchModel(conn, o);
		}catch(Exception e){
			throw e;
		}finally{
			
		}
	}
	
	public static Job search(Job o ) throws Exception {
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			return searchModel(conn, o);
		}catch(Exception e){
			throw e;
		}finally{
			if(conn !=null){
				conn.close();conn=null;
			}
		}
	}
	
	public static Job searchModel(Connection conn,Job o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Job h = null;
		int r = 1;
		int c = 1;
		try {
			sql.append("\n select j.*    \n");
			sql.append("\n  ,(SELECT m.pens_desc from PENSBME_MST_REFERENCE m ");
			sql.append("\n    where 1=1  and j.store_code = m.pens_value and m.reference_code ='Store') as store_name ");
			sql.append("\n  ,(SELECT m.pens_desc from PENSBME_MST_REFERENCE m ");
			sql.append("\n    where 1=1  and j.warehouse = m.pens_value and m.reference_code ='Warehouse') as warehouse_desc ");
			sql.append("\n from PENSBI.PENSBME_PICK_JOB j where 1=1   \n");
			
			if( !Utils.isNull(o.getJobId()).equals("")){
				sql.append("\n and job_id = "+Utils.isNull(o.getJobId())+"");
			}
			if( !Utils.isNull(o.getRefDoc()).equals("")){
				sql.append("\n and ref_doc = '"+Utils.isNull(o.getRefDoc())+"'");
			}
			
			if( !Utils.isNull(o.getName()).equals("")){
				sql.append("\n and name like '%"+Utils.isNull(o.getName())+"%'  ");
			}
			
			if( !Utils.isNull(o.getOpenDate()).equals("")){
				Date tDate  = Utils.parse(o.getOpenDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				String cDate = Utils.stringValue(tDate, Utils.DD_MM_YYYY_WITH_SLASH,Locale.US);
				
				sql.append("\n and OPEN_DATE = to_date('"+cDate+"','dd/mm/yyyy') ");
			}
			if( !Utils.isNull(o.getCloseDate()).equals("")){
				Date tDate  = Utils.parse(o.getCloseDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				String cDate = Utils.stringValue(tDate, Utils.DD_MM_YYYY_WITH_SLASH,Locale.US);
				sql.append("\n and CLOSE_DATE = to_date('"+cDate+"','dd/mm/yyyy') ");
			}
			
			sql.append("\n order by job_id asc ");
			logger.debug("sql:"+sql);
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			  
				   h = new Job();
				   h.setNo(r);
				   h.setJobId(rst.getString("job_id"));
				   h.setCustGroup(Utils.isNull(rst.getString("cust_group")));
				   h.setOpenDate(Utils.stringValue(rst.getDate("open_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				   
				   if(rst.getDate("close_date") !=null){
				      h.setCloseDate(Utils.stringValue(rst.getDate("close_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				   }
				   h.setName(Utils.isNull(rst.getString("name"))); 
				   h.setStatus(Utils.isNull(rst.getString("status"))); 
				   h.setStatusDesc(getStatusDesc(Utils.isNull(rst.getString("status")))); 
				   h.setStatusMessage(Utils.isNull(rst.getString("status_message"))); 
				   
				   h.setStoreCode(Utils.isNull(rst.getString("store_code"))); 
				   h.setStoreName(Utils.isNull(rst.getString("store_name"))); 
				   h.setStoreNo(Utils.isNull(rst.getString("store_no"))); 
				   h.setSubInv(Utils.isNull(rst.getString("sub_inv"))); 
				   h.setWareHouse(Utils.isNull(rst.getString("warehouse"))); 
				   h.setWareHouseDesc(Utils.isNull(rst.getString("warehouse_desc"))); 
				   h.setRefDoc(Utils.isNull(rst.getString("ref_doc"))); 
				   
				   if(Utils.isNull(rst.getString("status")).equals(STATUS_CANCEL) 
					|| Utils.isNull(rst.getString("status")).equals(STATUS_CLOSE) ){
					   h.setCanEdit(false);
				   }else{
					   h.setCanEdit(true); 
				   }
 
				   if(Utils.isNull(rst.getString("status")).equals(STATUS_CLOSE) 
					  || Utils.isNull(rst.getString("status")).equals(STATUS_OPEN)){
					   h.setCanCancel(true);
				   }else{
					   h.setCanEdit(false); 
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
	
	public static Job save(Job h,User user) throws Exception{
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			//check documentNo
			if(Utils.isNull(h.getJobId()).equals("")){
				//Gen JobId
				h.setJobId(genJobId()+"");
				h.setStatus(STATUS_OPEN);
				
				logger.debug("job id:"+h.getJobId());
				saveModel(conn, h);
			}else{
				if( !Utils.isNull(h.getCloseDate()).equals("")){
					h.setStatus(STATUS_CLOSE);
				}
				
				//update 
				updateModel(conn, h);
				
				if(h.getStatus().equals(STATUS_CLOSE)){
				  Barcode b = new Barcode();
				  b.setStatus(h.getStatus());
				  b.setJobId(h.getJobId());
				  b.setUpdateUser(h.getUpdateUser());
				  
				  //update status in barcode to close
				  BarcodeDAO.updateBarcodeHeadStatusModelByJobId(conn, b);
				  BarcodeDAO.updateBarcodeLineStatusModelByJobId(conn, b);
				  
				 //Process Onhand  by pens_item in job
				  OnhandDAO.processBanlanceOnhandFromBarcodeByJobId(conn,user.getUserName(),h);
				  
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
	
	public static Job saveCaseMoveWarehouse(Connection conn,Job h,User user) throws Exception{
		try{

			//check documentNo
			if(Utils.isNull(h.getJobId()).equals("")){
				//Gen JobId
				h.setJobId(genJobId()+"");
				saveAllModel(conn, h);
			}
		
			return h;
		}catch(Exception e){
		
		  throw e;
		}finally{
		}
	}
	
	public static Job saveCaseGenCn(Connection conn,Job h,User user) throws Exception{
		try{

			//check documentNo
			if(Utils.isNull(h.getJobId()).equals("")){
				//Gen JobId
				h.setJobId(genJobId()+"");
				saveAllModel(conn, h);
			}
		
			return h;
		}catch(Exception e){
		
		  throw e;
		}finally{
		}
	}
	
	// ( Running :  yyyymm+running  เช่น 201403001 )			
	 private static int genJobId() throws Exception{
		 int seq = 0;
		 Connection conn =null;
		   try{
			   conn = DBConnection.getInstance().getConnection();
			   //get Seq
			   seq = SequenceProcess.getNextValue(conn,"JOB_ID");
		   }catch(Exception e){
			   throw e;
		   }finally{
			   if(conn !=null){
				   conn.close();conn=null;
			   }
		   }
		  return seq;
	}
		 
	 
	 private static void saveModel(Connection conn,Job o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Insert");
			try{
				
				StringBuffer sql = new StringBuffer("");
				sql.append(" INSERT INTO PENSBI.PENSBME_PICK_JOB \n");
				sql.append(" (JOB_ID, NAME, OPEN_DATE,   \n");
				sql.append("  STATUS, STATUS_MESSAGE, CREATE_DATE, CREATE_USER ,CUST_GROUP,STORE_CODE,STORE_NO,SUB_INV,WAREHOUSE,REF_DOC)  \n");
			
			    sql.append(" VALUES (?, ?, ?, ?, ?, ?, ? ,?,?,?,?,?,?) \n");
				
				ps = conn.prepareStatement(sql.toString());
					
				Date openDate = Utils.parse( o.getOpenDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				
				int c =1;
				
				ps.setInt(c++, Integer.parseInt(o.getJobId()));
				ps.setString(c++, o.getName());
				ps.setTimestamp(c++, new java.sql.Timestamp(openDate.getTime()));
				ps.setString(c++, o.getStatus());
				ps.setString(c++, o.getStatusMessage());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setString(c++, o.getCreateUser());
				ps.setString(c++, o.getCustGroup());
				ps.setString(c++, o.getStoreCode());
				ps.setString(c++, o.getStoreNo());
				ps.setString(c++, o.getSubInv());
				ps.setString(c++, o.getWareHouse());
				ps.setString(c++, Utils.isNull(o.getRefDoc()));
				ps.executeUpdate();
				
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
	 
	 private static void saveAllModel(Connection conn,Job o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Insert");
			try{
				
				StringBuffer sql = new StringBuffer("");
				sql.append(" INSERT INTO PENSBI.PENSBME_PICK_JOB \n");
				sql.append(" (JOB_ID, NAME, OPEN_DATE,CLOSE_DATE,   \n");
				sql.append("  STATUS, STATUS_MESSAGE, CREATE_DATE, CREATE_USER ,CUST_GROUP,STORE_CODE,STORE_NO,SUB_INV,WAREHOUSE)  \n");
			
			    sql.append(" VALUES (?, ?, ?, ?, ?, ?, ? ,?,?,?,?,?,?) \n");
				
				ps = conn.prepareStatement(sql.toString());
					
				Date openDate = Utils.parse( o.getOpenDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				Date closeDate = Utils.parse( o.getCloseDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				int c =1;
				
				ps.setInt(c++, Integer.parseInt(o.getJobId()));
				ps.setString(c++, o.getName());
				ps.setTimestamp(c++, new java.sql.Timestamp(openDate.getTime()));
				ps.setTimestamp(c++, new java.sql.Timestamp(closeDate.getTime()));
				ps.setString(c++, o.getStatus());
				ps.setString(c++, o.getStatusMessage());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setString(c++, o.getCreateUser());
				ps.setString(c++, o.getCustGroup());
				ps.setString(c++, o.getStoreCode());
				ps.setString(c++, o.getStoreNo());
				ps.setString(c++, o.getSubInv());
				ps.setString(c++, o.getWareHouse());
				
				ps.executeUpdate();
				
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}

		public static void updateModel(Connection conn,Job o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Update");
			int  c = 1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" UPDATE PENSBI.PENSBME_PICK_JOB SET NAME = ? \n");
				sql.append(" ,STATUS = ?   \n");
				if( !Utils.isNull(o.getOpenDate()).equals("")){
				   Date tDate  = Utils.parse(o.getOpenDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				   String cDate = Utils.stringValue(tDate, Utils.DD_MM_YYYY_WITH_SLASH,Locale.US);
				   sql.append(" ,OPEN_DATE = to_date('"+cDate+"','dd/mm/yyyy')  \n");
				}
				if( !Utils.isNull(o.getCloseDate()).equals("")){
					 Date tDate  = Utils.parse(o.getCloseDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					 String cDate = Utils.stringValue(tDate, Utils.DD_MM_YYYY_WITH_SLASH,Locale.US);
					 sql.append(" ,CLOSE_DATE = to_date('"+cDate+"','dd/mm/yyyy')  \n");
				}
				
				sql.append(", CUST_GROUP = '"+Utils.isNull(o.getCustGroup())+"' \n" );
				sql.append(", STORE_CODE = '"+Utils.isNull(o.getStoreCode())+"' \n" );
				sql.append(", STORE_NO = '"+Utils.isNull(o.getStoreNo())+"' \n" );
				sql.append(", SUB_INV = '"+Utils.isNull(o.getSubInv())+"' \n" );
				sql.append(", WAREHOUSE = '"+Utils.isNull(o.getWareHouse())+"' \n" );
				sql.append(", UPDATE_DATE = ? \n" );
				sql.append(", UPDATE_USER = '"+Utils.isNull(o.getUpdateUser())+"' \n" );
				sql.append(", REF_DOC = '"+Utils.isNull(o.getRefDoc())+"' \n" );
				sql.append(" WHERE JOB_ID =? \n" );
                
				logger.debug("sql:"+sql.toString());
				
				ps = conn.prepareStatement(sql.toString());
					
				ps.setString(c++, o.getName());
				ps.setString(c++, o.getStatus());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setInt(c++, Integer.parseInt(o.getJobId()));

				ps.executeUpdate();
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
		
		public static void cancelJob(Connection conn,Job o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Cancel Job");
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" UPDATE PENSBI.PENSBME_PICK_JOB SET STATUS = ? \n");
				sql.append(" WHERE JOB_ID =? \n" );

				ps = conn.prepareStatement(sql.toString());
				
				ps.setString(1, STATUS_CANCEL);	
				ps.setString(2, o.getJobId());
				
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
