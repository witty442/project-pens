package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;

import com.isecinc.core.bean.References;
import com.isecinc.pens.bean.Barcode;
import com.isecinc.pens.bean.Job;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.dao.constants.PickConstants;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.Utils;
import com.pens.util.seq.SequenceProcess;

public class JobDAO extends PickConstants{

	protected static Logger logger = Logger.getLogger("PENS");

	public static int searchJobListTotalRec(Connection conn,Job o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int totalRec = 0;
		try {
			sql.append("\n select count(*) as c from (");
			sql.append("\n    select j.* ");
			sql.append("\n    ,( select (case when v.intflag='S' then 'Y' ELSE '' END)");
			sql.append("\n      from PENSBI.PENSBME_APPROVE_TO_AUTOCN v ");
			sql.append("\n      where v.rtn_no = J.rtn_no ) as auto_cn_lotus ");
			sql.append("\n   ,( select (case when  max(v.intflag)='S' then 'Y' ELSE '' END)");
			sql.append("\n     from PENSBI.PENSBME_AUTOCN_HH v ");
			sql.append("\n     where v.job_id = J.job_id and v.intflag='S' ) as auto_cn_hh ");
			sql.append("\n   ,( select (case when v.intflag='S' then 'Y' ELSE '' END)");
			sql.append("\n     from PENSBI.PENSBME_AUTO_SUBTRANSFER v ");
			sql.append("\n     where v.job_id = J.job_id) as autosub_bigc ");
			sql.append("\n    from PENSBI.PENSBME_PICK_JOB J  ");
			sql.append("\n )A where 1=1 ");
			//genWHereCondSql
			sql.append(genWhereCondSql(o));
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			if(rst.next()){
				totalRec = rst.getInt("c");
			}
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
	
	public static List<Job> searchJobList(Connection conn,Job o,boolean allRec,int currPage,int pageSize ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Job h = null;
		List<Job> items = new ArrayList<Job>();
		int r = 1;
		int c = 1;
		try {
			sql.append("\n select M.* from (");
			sql.append("\n select A.* ,rownum as r__ from (");
			sql.append("\n select B.* FROM( " );
			sql.append("\n   select J.* " );
			sql.append("\n   ,(select M.Interface_desc from PENSBI.PENSBME_MST_REFERENCE m " );
	        sql.append("\n     where M.pens_value =J.cust_group  and M.reference_code = 'Idwacoal') as cust_group_desc ");
            sql.append("\n   ,( SELECT m.pens_desc from PENSBI.PENSBME_MST_REFERENCE m ");
			sql.append("\n     where j.store_code = m.pens_value and m.reference_code ='Store') as store_name ");
			sql.append("\n   ,( select (case when v.intflag='S' then 'Y' ELSE '' END)");
			sql.append("\n     from PENSBI.PENSBME_APPROVE_TO_AUTOCN v ");
			sql.append("\n     where v.rtn_no = J.rtn_no) as auto_cn_lotus ");
			sql.append("\n   ,( select (case when  max(v.intflag)='S' then 'Y' ELSE '' END)");
			sql.append("\n     from PENSBI.PENSBME_AUTOCN_HH v ");
			sql.append("\n     where v.job_id = J.job_id and v.intflag='S' ) as auto_cn_hh ");
			sql.append("\n   ,( select (case when v.intflag='S' then 'Y' ELSE '' END)");
			sql.append("\n     from PENSBI.PENSBME_AUTO_SUBTRANSFER v ");
			sql.append("\n     where v.job_id = J.job_id) as autosub_bigc ");
			sql.append("\n   from PENSBI.PENSBME_PICK_JOB J ");
			sql.append("\n  )B where 1=1   ");
			//genWHereCondSql
			sql.append(genWhereCondSql(o));
			sql.append("\n order by job_id desc ");
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
				   h = new Job();
				   h.setNo(r);
				   h.setWareHouse(Utils.isNull(rst.getString("warehouse"))); 
				   h.setCustGroup(Utils.isNull(rst.getString("cust_group")));
				   h.setCustGroupDesc(Utils.isNull(rst.getString("cust_group_desc")));
				   h.setJobId(rst.getString("job_id"));
				   h.setOpenDate(DateUtil.stringValue(rst.getTimestamp("open_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				   h.setCloseDate(DateUtil.stringValueNull(rst.getDate("close_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));  
				   h.setName(Utils.isNull(rst.getString("name"))); 
				   h.setStatus(Utils.isNull(rst.getString("status"))); 
				   h.setStatusDesc(getStatusDesc(Utils.isNull(rst.getString("status")))); 
				   h.setStatusMessage(Utils.isNull(rst.getString("status_message"))); 
				   h.setStoreCode(Utils.isNull(rst.getString("store_code"))); 
				   h.setStoreName(Utils.isNull(rst.getString("store_name"))); 
				   h.setStoreNo(Utils.isNull(rst.getString("store_no"))); 
				   h.setSubInv(Utils.isNull(rst.getString("sub_inv"))); 
				   h.setRefDoc(Utils.isNull(rst.getString("ref_doc")));
				   
				   if( Utils.isNull(h.getCustGroup()).equals(PickConstants.STORE_TYPE_LOTUS_CODE)){
					  h.setAutoCN(Utils.isNull(rst.getString("auto_cn_lotus")));
				   }
				   if( Utils.isNull(h.getCustGroup()).equals(PickConstants.STORE_TYPE_HISHER_CODE)){
					  h.setAutoCN(Utils.isNull(rst.getString("auto_cn_hh")));
				   }
				   if( Utils.isNull(h.getCustGroup()).equals(PickConstants.STORE_TYPE_BIGC_CODE)){
					  h.setAutoCN(Utils.isNull(rst.getString("autosub_bigc")));
				   }
					
				   h.setRtnQty(Utils.decimalFormat(rst.getDouble("rtn_qty"),Utils.format_current_no_disgit,""));
				   h.setRtnAmt(Utils.decimalFormat(rst.getDouble("rtn_amt"),Utils.format_current_2_disgit,""));
				   
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
				
			} catch (Exception e) {}
		}
		return items;
	}
	
	
	public static StringBuffer genWhereCondSql(Job o) throws Exception{
		StringBuffer sql = new StringBuffer("");
		if( !Utils.isNull(o.getYear()).equals("")){
			sql.append("\n and to_char(open_date,'yyyy') = '"+Utils.isNull(o.getYear())+"'");
		}
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
			Date tDate  = DateUtil.parse(o.getOpenDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			String tDateStr = DateUtil.stringValue(tDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
			sql.append("\n and OPEN_DATE = to_date('"+tDateStr+"','dd/mm/yyyy')");
		}
		if( !Utils.isNull(o.getCloseDate()).equals("")){
			Date tDate  = DateUtil.parse(o.getCloseDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			String tDateStr = DateUtil.stringValue(tDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
			sql.append("\n and CLOSE_DATE = to_date('"+tDateStr+"','dd/mm/yyyy')");
		}
		if( !Utils.isNull(o.getCustGroup()).equals("")){
			sql.append("\n and cust_group like '%"+Utils.isNull(o.getCustGroup())+"%'  ");
		}
		if( !Utils.isNull(o.getRtnNo()).equals("")){
			sql.append("\n and rtn_no = '"+Utils.isNull(o.getRtnNo())+"' ");
		}
		if( !Utils.isNull(o.getDispAutoCN()).equals("")){
			if( Utils.isNull(o.getCustGroup()).equals("")){
			 // sql.append("\n and ( auto_cn_lotus = 'Y' OR auto_cn_hh = 'Y' or autosub_bigc ='Y')");
			}else{
				if( Utils.isNull(o.getCustGroup()).equals(PickConstants.STORE_TYPE_LOTUS_CODE)){
					sql.append("\n and auto_cn_lotus = 'Y' ");
				}
				if( Utils.isNull(o.getCustGroup()).equals(PickConstants.STORE_TYPE_HISHER_CODE)){
					sql.append("\n and auto_cn_hh = 'Y' ");
				}
				if( Utils.isNull(o.getCustGroup()).equals(PickConstants.STORE_TYPE_BIGC_CODE)){
					sql.append("\n and autosub_bigc = 'Y' ");
				}
			}
		}
		return sql;
	}
	
	public static Job searchJobDetail(Connection conn,Job o ) throws Exception {
		try{
			return searchJobDetailModel(conn, o);
		}catch(Exception e){
			throw e;
		}finally{
			
		}
	}
	
	public static Job searchJobDetail(Job o ) throws Exception {
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnectionApps();
			return searchJobDetailModel(conn, o);
		}catch(Exception e){
			throw e;
		}finally{
			if(conn !=null){
				conn.close();conn=null;
			}
		}
	}
	
	public static Job searchJobDetailModel(Connection conn,Job o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Job h = null;
		int r = 1;
		int c = 1;
		Job jobDetail = null;
		try {
			sql.append("\n select j.*  ");
			sql.append("\n  ,(SELECT m.pens_desc from PENSBI.PENSBME_MST_REFERENCE m ");
			sql.append("\n    where 1=1  and j.store_code = m.pens_value and m.reference_code ='Store') as store_name ");
			sql.append("\n  ,(SELECT m.pens_desc from PENSBI.PENSBME_MST_REFERENCE m ");
			sql.append("\n    where 1=1  and j.warehouse = m.pens_value and m.reference_code ='Warehouse') as warehouse_desc ");
			sql.append("\n from PENSBI.PENSBME_PICK_JOB j where 1=1   ");
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
				Date tDate  = DateUtil.parse(o.getOpenDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				String cDate = DateUtil.stringValue(tDate, DateUtil.DD_MM_YYYY_WITH_SLASH,Locale.US);
				sql.append("\n and OPEN_DATE = to_date('"+cDate+"','dd/mm/yyyy') ");
			}
			if( !Utils.isNull(o.getCloseDate()).equals("")){
				Date tDate  = DateUtil.parse(o.getCloseDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				String cDate = DateUtil.stringValue(tDate, DateUtil.DD_MM_YYYY_WITH_SLASH,Locale.US);
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
				   h.setOpenDate(DateUtil.stringValue(rst.getDate("open_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				   
				   if(rst.getDate("close_date") !=null){
				      h.setCloseDate(DateUtil.stringValue(rst.getDate("close_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
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
				   
				   h.setRtnQty(Utils.decimalFormat(rst.getDouble("rtn_qty"),Utils.format_current_no_disgit,""));
				   h.setRtnAmt(Utils.decimalFormat(rst.getDouble("rtn_amt"),Utils.format_current_2_disgit,""));
				   h.setRtnNo(Utils.isNull(rst.getString("rtn_no"))); 
				   h.setForwarder(Utils.isNull(rst.getString("forwarder"))); 
				   h.setForwarderBox(Utils.decimalFormat(rst.getDouble("forwarder_Box"), Utils.format_current_no_disgit, "")); 
				   
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
			  
				   //get CN NO Case Lotus
				   if(h.getCustGroup().equals(PickConstants.STORE_TYPE_LOTUS_CODE)){
					   if( !Utils.isNull(h.getRtnNo()).equals("")){
						   h.setCnNo(getCNNO_Lotus(conn,Utils.isNull(h.getRtnNo())));
					   }
				   }else  if(h.getCustGroup().equals(PickConstants.STORE_TYPE_HISHER_CODE)){
					   jobDetail =  getGRNO_HisHer(conn,h.getJobId());
					   if(jobDetail != null){
					      h.setRtnNo(Utils.isNull(jobDetail.getGrNo())); 
					      h.setCnNo(Utils.isNull(jobDetail.getCnNo())); 
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
	public static String getCNNO_Lotus(Connection conn,String rtnNo) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		String cnNo = "";
		StringBuffer sql = new StringBuffer("");
		try{
			sql.append(" select distinct cn_no from xxpens_om_rtn_order_ref_v h \n");
			sql.append(" where rtn_no ='"+rtnNo+"' and cn_no is not null order by cn_no \n" );
            logger.debug("sql:\n"+sql.toString());
         
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			while(rs.next()){
				if( !Utils.isNull(rs.getString("cn_no")).equals(""))
				   cnNo += rs.getString("cn_no")+",";
			}
			if( !Utils.isNull(cnNo).equals("")){
				cnNo = cnNo.substring(0,cnNo.length()-1);
			}
			return cnNo;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}
	
	public static Job getGRNO_HisHer(Connection conn,String jobId) throws Exception{
		PreparedStatement ps = null;
		ResultSet rs = null;
		String cnNo = "";
		String grNo = "";
		String grNoSqlIn = "";
		StringBuffer sql = new StringBuffer("");
		Job jobDetail = new Job();
		try{
			sql.append(" select distinct gr_no from PENSBI.PENSBME_AUTOCN_HH h where job_id ='"+jobId+"' \n" );
			sql.append(" and intflag ='S' and gr_no is not null \n");
            logger.debug("sql:\n"+sql.toString());
         
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			while(rs.next()){
				if( !Utils.isNull(rs.getString("gr_no")).equals("")){
				   grNo += rs.getString("gr_no")+",";
				   grNoSqlIn += "'"+rs.getString("gr_no")+"',";
				}
			}
			if( !Utils.isNull(grNo).equals("")){
				grNo = grNo.substring(0,grNo.length()-1);
			}
			if( !Utils.isNull(grNo).equals("")){
				grNoSqlIn = grNoSqlIn.substring(0,grNoSqlIn.length()-1);
			}
		
			if( !Utils.isNull(grNoSqlIn).equals("")){
				//Get Cn_no by gr_no
				sql = new StringBuffer("");
				sql.append(" select distinct cn_no from xxpens_om_hh_order_ref_v h where gr_no in("+grNoSqlIn+")");
				sql.append(" and job_id = "+jobId+" \n" );
				sql.append(" and cn_no is not null order by cn_no \n" );
				ps = conn.prepareStatement(sql.toString());
				logger.debug("sql:\n"+sql.toString());
			         
				rs = ps.executeQuery();
				while(rs.next()){
					if( !Utils.isNull(rs.getString("cn_no")).equals("")){
					   cnNo += rs.getString("cn_no")+",";
					}
				}
				if( !Utils.isNull(cnNo).equals("")){
					cnNo = cnNo.substring(0,cnNo.length()-1);
				}
			}
			//
			jobDetail.setGrNo(grNo);
			jobDetail.setCnNo(cnNo);
			
			return jobDetail;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
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
				sql.append("  STATUS, STATUS_MESSAGE, CREATE_DATE, CREATE_USER ");
				sql.append(",CUST_GROUP,STORE_CODE,STORE_NO,SUB_INV,WAREHOUSE");
				sql.append(",REF_DOC,RTN_QTY,RTN_AMT,Forwarder,Forwarder_box)  \n");
			
			    sql.append(" VALUES (?, ?, ?, ?, ?, ?, ? ,?,?,?,?,?,? ,?,?,?,?) \n");
				
				ps = conn.prepareStatement(sql.toString());
					
				Date openDate = DateUtil.parse( o.getOpenDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				
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
				ps.setDouble(c++, Utils.convertStrToDouble(o.getRtnQty()));
				ps.setDouble(c++, Utils.convertStrToDouble(o.getRtnAmt()));
				ps.setString(c++, Utils.isNull(o.getForwarder()));
				ps.setDouble(c++, Utils.convertStrToDouble(o.getForwarderBox()));
				
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
					
				Date openDate = DateUtil.parse( o.getOpenDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				Date closeDate = DateUtil.parse( o.getCloseDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
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
				   Date tDate  = DateUtil.parse(o.getOpenDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				   String cDate = DateUtil.stringValue(tDate, DateUtil.DD_MM_YYYY_WITH_SLASH,Locale.US);
				   sql.append(" ,OPEN_DATE = to_date('"+cDate+"','dd/mm/yyyy')  \n");
				}
				if( !Utils.isNull(o.getCloseDate()).equals("")){
					 Date tDate  = DateUtil.parse(o.getCloseDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
					 String cDate = DateUtil.stringValue(tDate, DateUtil.DD_MM_YYYY_WITH_SLASH,Locale.US);
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
				sql.append(", RTN_QTY = "+Utils.convertStrToDouble(o.getRtnQty())+" \n" );
				sql.append(", RTN_AMT = "+Utils.convertStrToDouble(o.getRtnAmt())+" \n" );
				sql.append(", RTN_NO = '"+Utils.isNull(o.getRtnNo())+"' \n" );
				sql.append(", Forwarder = '"+Utils.isNull(o.getForwarder())+"' \n" );
				sql.append(", Forwarder_box = "+Utils.convertStrToDouble(o.getForwarderBox())+" \n" );
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
		
		public static void updateRefDocModel(Connection conn,Job o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Update");
			int  c = 1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" UPDATE PENSBI.PENSBME_PICK_JOB SET  \n");
				sql.append("  UPDATE_DATE = ? \n" );
				sql.append(", UPDATE_USER = '"+Utils.isNull(o.getUpdateUser())+"' \n" );
				sql.append(", REF_DOC = '"+Utils.isNull(o.getRefDoc())+"' \n" );
				sql.append(", RTN_QTY = "+Utils.convertStrToDouble(o.getRtnQty())+" \n" );
				sql.append(", RTN_AMT = "+Utils.convertStrToDouble(o.getRtnAmt())+" \n" );
				sql.append(" WHERE JOB_ID =? \n" );
                
				logger.debug("sql:"+sql.toString());
				ps = conn.prepareStatement(sql.toString());
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

		public static boolean validRtnNoInJob(String rtnNo) throws Exception{
		    Connection conn = null;
			PreparedStatement ps = null;
			ResultSet rs = null;
			try{
				conn = DBConnection.getInstance().getConnection();
				StringBuffer sql = new StringBuffer("");
				sql.append(" select count(*) as c from  PENSBME_PICK_JOB where rtn_no ='"+rtnNo+"' \n" );
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
		public static List<References> getYearList(){
			List<References> yearList = new ArrayList<References>();
		    Calendar c = Calendar.getInstance();
		    int year = c.get(Calendar.YEAR);
		    int yearThai =year+543;
		    for(int i=0;i<=4;i++){
		    	yearList.add(new References(year+"", yearThai+""));
		    	year = year-1;
		    	yearThai = yearThai-1;
		    }
			return yearList;
		}
		
}
