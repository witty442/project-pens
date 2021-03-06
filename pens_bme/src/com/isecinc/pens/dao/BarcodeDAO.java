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
import com.isecinc.pens.dao.constants.PickConstants;
import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.Utils;
import com.pens.util.seq.SequenceProcess;

public class BarcodeDAO extends PickConstants{

	protected static Logger logger = Logger.getLogger("PENS");
	protected static SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Utils.local_th);
	
	public static int getTotalRecBarcodeList(Connection conn,Barcode o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int totalRec = 0;
		try {
			sql.append("\n select count(*) as c from(");
			sql.append("\n select M.* , D.qty as qty_temp from( ");
			sql.append("\n select i.* ,j.name ,j.status as job_status,j.store_code,j.store_no,j.sub_inv from PENSBI.PENSBME_PICK_BARCODE i INNER JOIN  \n");
			sql.append("\n ( ");
			sql.append("\n    select distinct job_id,name,status,store_code,store_no,sub_inv from PENSBME_PICK_JOB ");
			sql.append("\n ) j on i.job_id = j.job_id");
			sql.append("\n where 1=1   \n");
			sql.append(genWhereSql(o));
			sql.append("\n ) M LEFT OUTER JOIN ");
			sql.append("\n ( ");
			sql.append("\n  select i.job_id,i.box_no,count(*) as qty from PENSBME_PICK_BARCODE_ITEM i  ");
			sql.append("\n  where 1=1  ");
			sql.append("\n  group by i.job_id,i.box_no ");
			sql.append("\n ) D ON  M.job_id = D.job_id and M.box_no = D.box_no");
			sql.append("\n )");
			logger.debug("sql:"+sql);

			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			if(rst.next()) {
				totalRec = rst.getInt("c");
			}//if
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
	public static double getTotalQtyBarcodeList(Connection conn,Barcode o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		double totalRec = 0;
		try {
			sql.append("\n select sum(qty_temp) as c from(");
			sql.append("\n select M.* , D.qty as qty_temp from( ");
			sql.append("\n select i.* ,j.name ,j.status as job_status,j.store_code,j.store_no,j.sub_inv from PENSBI.PENSBME_PICK_BARCODE i INNER JOIN  \n");
			sql.append("\n ( ");
			sql.append("\n    select distinct job_id,name,status,store_code,store_no,sub_inv from PENSBME_PICK_JOB ");
			sql.append("\n ) j on i.job_id = j.job_id");
			sql.append("\n where 1=1   \n");
			sql.append(genWhereSql(o));
			sql.append("\n ) M LEFT OUTER JOIN ");
			sql.append("\n ( ");
			sql.append("\n  select i.job_id,i.box_no,count(*) as qty from PENSBME_PICK_BARCODE_ITEM i  ");
			sql.append("\n  where 1=1  ");
			sql.append("\n  group by i.job_id,i.box_no ");
			sql.append("\n ) D ON  M.job_id = D.job_id and M.box_no = D.box_no");
			sql.append("\n )");
			logger.debug("sql:"+sql);

			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			if(rst.next()) {
				totalRec = rst.getDouble("c");
			}//if
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
	
	public static List<Barcode> searchBarcodeList(Connection conn,Barcode o ,boolean allRec ,int currPage,int pageSize) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Barcode h = null;
		List<Barcode> items = new ArrayList<Barcode>();
		int r = 1;
		int c = 1;
		int totalQty = 0;
		try {
			sql.append("\n select M.* from (");
			sql.append("\n select A.* ,rownum as r__ from (");
				sql.append("\n select M.* , D.qty as qty_temp from( ");
				sql.append("\n select i.* ,j.name ,j.status as job_status,j.store_code,j.store_no,j.sub_inv ");
				sql.append("\n from PENSBI.PENSBME_PICK_BARCODE i INNER JOIN  \n");
				sql.append("\n ( ");
				sql.append("\n    select distinct job_id,name,status,store_code,store_no,sub_inv from PENSBME_PICK_JOB ");
				sql.append("\n ) j on i.job_id = j.job_id");
				sql.append("\n where 1=1   \n");
				sql.append(genWhereSql(o));
				sql.append("\n ) M LEFT OUTER JOIN ");
				sql.append("\n ( ");
				sql.append("\n  select i.job_id,i.box_no,count(*) as qty from PENSBME_PICK_BARCODE_ITEM i  ");
				sql.append("\n  where 1=1  ");
				sql.append("\n  group by i.job_id,i.box_no ");
				sql.append("\n ) D ON  M.job_id = D.job_id and M.box_no = D.box_no");
				sql.append("\n order by M.box_no desc ");
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
			   h = new Barcode();
			   h.setNo(r);
			   h.setJobId(rst.getString("job_id"));
			   h.setBoxNo(rst.getString("box_no"));
			   h.setTransactionDate(DateUtil.stringValue(rst.getTimestamp("transaction_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			   
			   h.setName(Utils.isNull(rst.getString("name"))); 
			   h.setRemark(Utils.isNull(rst.getString("remark"))); 
			   h.setStatus(Utils.isNull(rst.getString("status"))); 
			   h.setStatusDesc(JobDAO.getStatusDesc(Utils.isNull(rst.getString("status")))); 
			   
			   h.setStoreCode(Utils.isNull(rst.getString("store_code"))); 
			   h.setStoreNo(Utils.isNull(rst.getString("store_no"))); 
			   h.setSubInv(Utils.isNull(rst.getString("sub_inv"))); 
			   h.setCreateUser(Utils.isNull(rst.getString("create_user"))); 
			   h.setType(Utils.isNull(rst.getString("type"))); 
			   
			   if(Utils.isNull(rst.getString("job_status")).equals(JobDAO.STATUS_CLOSE)){
				   h.setCanEdit(false);  
			   }else{
				   if(Utils.isNull(rst.getString("status")).equals(JobDAO.STATUS_CANCEL) 
					|| Utils.isNull(rst.getString("status")).equals(JobDAO.STATUS_CLOSE) ){
					   h.setCanEdit(false);
				   }else{
					   h.setCanEdit(true); 
				   }
		       }
			   
			   //get Total Qty
			  // h.setQty(getTotalQtyByBoxNo(conn,h));
			  // h.setQtyTemp(rst.getInt("qty_temp"));
			   h.setQty(rst.getInt("qty_temp"));
			
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
	
	public static StringBuilder genWhereSql(Barcode o ) throws Exception {
		StringBuilder sql = new StringBuilder();
		boolean foundCriteria = false;
		try {
			
			if( !Utils.isNull(o.getJobId()).equals("")){
				sql.append("\n and i.job_id = "+Utils.isNull(o.getJobId())+"");
				foundCriteria = true;
			}
			if( !Utils.isNull(o.getBoxNo()).equals("")){
				sql.append("\n and i.box_no = '"+Utils.isNull(o.getBoxNo())+"'");
				foundCriteria = true;
			}
			if( !Utils.isNull(o.getRemark()).equals("")){
				sql.append("\n and i.remark like '%"+Utils.isNull(o.getRemark())+"%'");
				foundCriteria = true;
			}

			if( !Utils.isNull(o.getStoreCode()).equals("")){
				sql.append("\n and j.store_code = '"+Utils.isNull(o.getStoreCode())+"'  ");
				foundCriteria = true;
			}
			if( !Utils.isNull(o.getStoreNo()).equals("")){
				sql.append("\n and j.store_no = '"+Utils.isNull(o.getStoreNo())+"'  ");
				foundCriteria = true;
			}
			if( !Utils.isNull(o.getSubInv()).equals("")){
				sql.append("\n and j.sub_inv = '"+Utils.isNull(o.getSubInv())+"'  ");
				foundCriteria = true;
			}
			
			if( !Utils.isNull(o.getTransactionDate()).equals("")){
				Date tDate  = DateUtil.parse(o.getTransactionDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				String dateStr = DateUtil.stringValue(tDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
				
				sql.append("\n and i.TRANSACTION_DATE = to_date('"+dateStr+"','dd/mm/yyyy') ");
				foundCriteria = true;
			}
			if( !Utils.isNull(o.getStatus()).equals("")){
				sql.append("\n and i.status = '"+Utils.isNull(o.getStatus())+"'");
				foundCriteria = true;
			}
			if( !Utils.isNull(o.getCreateUser()).equals("")){
				sql.append("\n and i.create_user LIKE '%"+Utils.isNull(o.getCreateUser())+"%'");
				foundCriteria = true;
			}
			
			if( Utils.isNull(o.getIncludeCancel()).equals("")){
				sql.append("\n and i.status <> '"+PickConstants.STATUS_CANCEL+"'");
			}
			//Case no criteria default current date
			if(foundCriteria ==false){
				sql.append("\n and 1=2 ");
               //String dateStr = Utils.stringValue(new Date(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			  // sql.append("\n and i.TRANSACTION_DATE = to_date('"+dateStr+"','dd/mm/yyyy') ");
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				
			} catch (Exception e) {}
		}
		return sql;
	}
	
	private static int getTotalQtyByBoxNo(Connection conn,Barcode o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
        int totalQty = 0;
		try {

			sql.append("\n select i.box_no,count(*) as qty from PENSBI.PENSBME_PICK_BARCODE_ITEM i  ");
			sql.append("\n where 1=1  ");
			
			if( !Utils.isNull(o.getJobId()).equals("")){
				sql.append("\n and i.job_id = "+Utils.isNull(o.getJobId())+"");
			}
			if( !Utils.isNull(o.getBoxNo()).equals("")){
				sql.append("\n and i.box_no = '"+Utils.isNull(o.getBoxNo())+"'");
			}
			sql.append("\n group by i.box_no ");

			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			if(rst.next()) {
				totalQty = rst.getInt("qty");
			   
			}//while

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return totalQty;
	}
	
	public static Barcode searchReport(Barcode o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		Barcode h = null;
		int r = 1;
		int c = 1;
		try {
			sql.append("\n select i.* ,j.name  from PENSBI.PENSBME_PICK_BARCODE i INNER JOIN  ");
			sql.append("\n ( ");
			sql.append("\n    select distinct job_id,name from PENSBME_PICK_JOB ");
			sql.append("\n ) j on i.job_id = j.job_id");
			sql.append("\n where 1=1  ");
			//sql.append("\n and i.status in('"+PickConstants.BARCODE_ITEM_STATUS_CLOSE+"','"+PickConstants.BARCODE_ITEM_STATUS_OPEN+"')");
			
			if( !Utils.isNull(o.getBoxNo()).equals("")){
				sql.append("\n and i.box_no = '"+Utils.isNull(o.getBoxNo())+"'");
			}
			
			sql.append("\n order by i.job_id ,i.box_no asc ");
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()) {
			   h = new Barcode();
			   h.setNo(r);
			   h.setJobId(rst.getString("job_id"));
			   h.setBoxNo(rst.getString("box_no"));
			   h.setTransactionDate(DateUtil.stringValue(rst.getTimestamp("transaction_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			   
			   h.setName(Utils.isNull(rst.getString("name"))); 
			   h.setRemark(Utils.isNull(rst.getString("remark"))); 
			   h.setStatus(Utils.isNull(rst.getString("status"))); 
			   h.setStatusDesc(JobDAO.getStatusDesc(Utils.isNull(rst.getString("status")))); 
			   r++;
			}//while

			if(h != null){
				h.setItems(searchItemReport(conn,h));
			}
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
	
	public static Barcode search(Barcode o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		Barcode h = null;
		int r = 1;
		int c = 1;
		try {
			sql.append("\n select i.*,i.warehouse,j.name,j.status as job_status");
			sql.append("\n  ,j.store_code,j.store_no,j.sub_inv ");
			sql.append("\n  ,(select M.pens_desc from pensbi.PENSBME_MST_REFERENCE M ");
			sql.append("\n   where M.reference_code = 'Store' and M.pens_value = j.store_code) as STORE_NAME ");
			sql.append("\n  ,(SELECT m.pens_desc from PENSBME_MST_REFERENCE m ");
			sql.append("\n    where 1=1  and i.warehouse = m.pens_value and m.reference_code ='Warehouse') as warehouse_desc ");
			sql.append("\n from PENSBI.PENSBME_PICK_BARCODE i  ");
			sql.append("\n INNER JOIN ( ");
			sql.append("\n    select distinct warehouse,job_id,name,status,store_code,store_no,sub_inv  from PENSBME_PICK_JOB ");
			sql.append("\n ) j on i.job_id = j.job_id");
			sql.append("\n where 1=1   \n");
			
			if( !Utils.isNull(o.getJobId()).equals("")){
				sql.append("\n and i.job_id = "+Utils.isNull(o.getJobId())+"");
			}
			
			if( !Utils.isNull(o.getBoxNo()).equals("")){
				sql.append("\n and i.box_no ='"+Utils.isNull(o.getBoxNo())+"'  ");
			}
			
			if( !Utils.isNull(o.getTransactionDate()).equals("")){
				sql.append("\n and i.TRANSACTION_DATE = ? ");
			}
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			
			if( !Utils.isNull(o.getTransactionDate()).equals("")){
				Date tDate  = DateUtil.parse(o.getTransactionDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				ps.setDate(c++,new java.sql.Date(tDate.getTime()));
			}
			
			rst = ps.executeQuery();
			while(rst.next()) {
			   h = new Barcode();
			   h.setNo(r);
			   h.setJobId(rst.getString("job_id"));
			   h.setBoxNo(rst.getString("box_no"));
			   h.setTransactionDate(DateUtil.stringValue(rst.getDate("transaction_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));

			   h.setName(Utils.isNull(rst.getString("name"))); 
			   h.setRemark(Utils.isNull(rst.getString("remark"))); 
			   h.setStatus(Utils.isNull(rst.getString("status"))); 
			   h.setStatusDesc(getStatusDesc(Utils.isNull(rst.getString("status")))); 
			   
			   h.setStoreCode(Utils.isNull(rst.getString("store_code")));
			   h.setStoreName(Utils.isNull(rst.getString("store_name")));
			   h.setStoreNo(Utils.isNull(rst.getString("store_no")));
			   h.setSubInv(Utils.isNull(rst.getString("sub_inv")));
			   h.setWareHouse(Utils.isNull(rst.getString("warehouse")));
			   h.setWareHouseDesc(Utils.isNull(rst.getString("warehouse_desc")));
			   h.setGrNo(Utils.isNull(rst.getString("gr_no")));
			   
			   if(Utils.isNull(rst.getString("job_status")).equals(JobDAO.STATUS_CLOSE)){
				   h.setCanEdit(false);  
				   h.setCanCancel(false);
			   }else{
				   if(  Utils.isNull(rst.getString("status")).equals(JobDAO.STATUS_CANCEL) 
					 || Utils.isNull(rst.getString("status")).equals(JobDAO.STATUS_CLOSE)
				     ){
					   h.setCanEdit(false);
				   }else{
					   h.setCanEdit(true); 
				   }
				   
				   if( Utils.isNull(rst.getString("status")).equals(JobDAO.STATUS_CLOSE)
					   || Utils.isNull(rst.getString("status")).equals(JobDAO.STATUS_OPEN) ){
					  h.setCanCancel(true);
				   }else{
					  h.setCanCancel(false); 
				   }
			   }   
			   r++;
			}//while

			if(h != null){
				h.setItems(searchItem(conn,o));
			}
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
	
	public static List<Barcode> searchItem(Connection conn,Barcode o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Barcode h = null;
		int r = 1;
        List<Barcode> items = new ArrayList<Barcode>();
		try {

			sql.append("\n select i.* from PENSBI.PENSBME_PICK_BARCODE_ITEM i   \n");
			sql.append("\n where 1=1   \n");
			
			if( !Utils.isNull(o.getJobId()).equals("")){
				sql.append("\n and i.job_id = "+Utils.isNull(o.getJobId())+"");
			}
			if( !Utils.isNull(o.getBoxNo()).equals("")){
				sql.append("\n and i.box_no = '"+Utils.isNull(o.getBoxNo())+"'");
			}
			sql.append("\n order by i.line_id asc ");
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			
		
			rst = ps.executeQuery();

			while(rst.next()) {
			  
			   h = new Barcode();
			   h.setLineId(rst.getInt("line_id"));
			   h.setJobId(rst.getString("job_id"));
			   h.setBoxNo(rst.getString("box_no"));
			  
			   h.setBarcode(rst.getString("barcode"));
			   h.setMaterialMaster(rst.getString("MATERIAL_MASTER"));
			   h.setGroupCode(rst.getString("group_code"));
			   h.setPensItem(rst.getString("pens_item"));
			   h.setWholePriceBF(Utils.decimalFormat(rst.getDouble("WHOLE_PRICE_BF"), Utils.format_current_2_disgit));
			   h.setRetailPriceBF(Utils.decimalFormat(rst.getDouble("RETAIL_PRICE_BF"), Utils.format_current_2_disgit));
			   
			   h.setBarcodeReadonly("true");
			   h.setBarcodeStyle("disableText");
			   h.setStatus(Utils.isNull(rst.getString("status"))); 
			   h.setStatusDesc(getStatusDesc(Utils.isNull(rst.getString("status")))); 
			   
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
	
	public static Barcode searchManualStock(Barcode o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		Barcode h = null;
		int r = 1;
		int c = 1;
		try {
			sql.append("\n select i.*,i.warehouse,j.name,j.status as job_status");
			sql.append("\n  ,j.store_code,j.store_no,j.sub_inv ");
			sql.append("\n  ,(select M.pens_desc from pensbi.PENSBME_MST_REFERENCE M ");
			sql.append("\n   where M.reference_code = 'Store' and M.pens_value = j.store_code) as STORE_NAME ");
			sql.append("\n  ,(SELECT m.pens_desc from PENSBME_MST_REFERENCE m ");
			sql.append("\n    where 1=1  and i.warehouse = m.pens_value and m.reference_code ='Warehouse') as warehouse_desc ");
			sql.append("\n from PENSBI.PENSBME_PICK_BARCODE i  ");
			sql.append("\n INNER JOIN ( ");
			sql.append("\n    select distinct warehouse,job_id,name,status,store_code,store_no,sub_inv  from PENSBME_PICK_JOB ");
			sql.append("\n ) j on i.job_id = j.job_id");
			sql.append("\n where 1=1   \n");
			
			if( !Utils.isNull(o.getJobId()).equals("")){
				sql.append("\n and i.job_id = "+Utils.isNull(o.getJobId())+"");
			}
			
			if( !Utils.isNull(o.getBoxNo()).equals("")){
				sql.append("\n and i.box_no ='"+Utils.isNull(o.getBoxNo())+"'  ");
			}
			
			if( !Utils.isNull(o.getTransactionDate()).equals("")){
				sql.append("\n and i.TRANSACTION_DATE = ? ");
			}
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			
			if( !Utils.isNull(o.getTransactionDate()).equals("")){
				Date tDate  = DateUtil.parse(o.getTransactionDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				ps.setDate(c++,new java.sql.Date(tDate.getTime()));
			}
			
			rst = ps.executeQuery();
			while(rst.next()) {
			   h = new Barcode();
			   h.setNo(r);
			   h.setJobId(rst.getString("job_id"));
			   h.setBoxNo(rst.getString("box_no"));
			   h.setTransactionDate(DateUtil.stringValue(rst.getDate("transaction_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th));

			   h.setName(Utils.isNull(rst.getString("name"))); 
			   h.setRemark(Utils.isNull(rst.getString("remark"))); 
			   h.setStatus(Utils.isNull(rst.getString("status"))); 
			   h.setStatusDesc(getStatusDesc(Utils.isNull(rst.getString("status")))); 
			   
			   h.setStoreCode(Utils.isNull(rst.getString("store_code")));
			   h.setStoreName(Utils.isNull(rst.getString("store_name")));
			   h.setStoreNo(Utils.isNull(rst.getString("store_no")));
			   h.setSubInv(Utils.isNull(rst.getString("sub_inv")));
			   h.setWareHouse(Utils.isNull(rst.getString("warehouse")));
			   h.setWareHouseDesc(Utils.isNull(rst.getString("warehouse_desc")));
			   h.setGrNo(Utils.isNull(rst.getString("gr_no")));
			   
			   if(Utils.isNull(rst.getString("job_status")).equals(JobDAO.STATUS_CLOSE)){
				   h.setCanEdit(false);  
				   h.setCanCancel(false);
			   }else{
				   if(  Utils.isNull(rst.getString("status")).equals(JobDAO.STATUS_CANCEL) 
					 || Utils.isNull(rst.getString("status")).equals(JobDAO.STATUS_CLOSE)
				     ){
					   h.setCanEdit(false);
				   }else{
					   h.setCanEdit(true); 
				   }
				   
				   if( Utils.isNull(rst.getString("status")).equals(JobDAO.STATUS_CLOSE)
					   || Utils.isNull(rst.getString("status")).equals(JobDAO.STATUS_OPEN) ){
					  h.setCanCancel(true);
				   }else{
					  h.setCanCancel(false); 
				   }
			   }   
			   r++;
			}//while

			if(h != null){
				h.setItems(searchItemManualStock(conn,o));
			}
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
	
	public static List<Barcode> searchItemManualStock(Connection conn,Barcode o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Barcode h = null;
		int r = 1;
        List<Barcode> items = new ArrayList<Barcode>();
		try {
			sql.append("\n select i.pens_item,i.material_master ,i.barcode ");
			sql.append("\n ,i.group_code,i.whole_price_bf,i.retail_price_bf");
			sql.append("\n ,NVL(count(*),0) as qty ");
			sql.append("\n from PENSBI.PENSBME_PICK_BARCODE_ITEM i ");
			sql.append("\n where 1=1  ");
			
			if( !Utils.isNull(o.getJobId()).equals("")){
				sql.append("\n and i.job_id = "+Utils.isNull(o.getJobId())+"");
			}
			if( !Utils.isNull(o.getBoxNo()).equals("")){
				sql.append("\n and i.box_no = '"+Utils.isNull(o.getBoxNo())+"'");
			}
			sql.append("\n group by i.pens_item,i.material_master ,i.barcode ");
			sql.append("\n ,i.group_code,i.whole_price_bf,i.retail_price_bf");
			sql.append("\n order by i.pens_item,i.material_master asc ");
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()) {
			   h = new Barcode();
			   h.setJobId(Utils.isNull(o.getJobId()));
			   h.setBoxNo(Utils.isNull(o.getBoxNo()));
			  
			   h.setBarcode(rst.getString("barcode"));
			   h.setMaterialMaster(rst.getString("MATERIAL_MASTER"));
			   h.setGroupCode(rst.getString("group_code"));
			   h.setPensItem(rst.getString("pens_item"));
			   h.setWholePriceBF(Utils.decimalFormat(rst.getDouble("WHOLE_PRICE_BF"), Utils.format_current_2_disgit));
			   h.setRetailPriceBF(Utils.decimalFormat(rst.getDouble("RETAIL_PRICE_BF"), Utils.format_current_2_disgit));
			   h.setQty(rst.getInt("qty"));
			   
			 /*  h.setBarcodeReadonly("true");
			   h.setBarcodeStyle("disableText");
			   h.setStatus(Utils.isNull(rst.getString("status"))); 
			   h.setStatusDesc(getStatusDesc(Utils.isNull(rst.getString("status")))); 
			   */
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
	
	public static List<Barcode> searchItemStatusCloseOnly(Connection conn,Barcode o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Barcode h = null;
		int r = 1;
        List<Barcode> items = new ArrayList<Barcode>();
		try {

			sql.append("\n select i.* from PENSBI.PENSBME_PICK_BARCODE_ITEM i   \n");
			sql.append("\n where 1=1 and i.status ='"+STATUS_CLOSE+"' \n");
			
			if( !Utils.isNull(o.getJobId()).equals("")){
				sql.append("\n and i.job_id = "+Utils.isNull(o.getJobId())+"");
			}
			if( !Utils.isNull(o.getBoxNo()).equals("")){
				sql.append("\n and i.box_no = '"+Utils.isNull(o.getBoxNo())+"'");
			}
			sql.append("\n order by i.line_id asc ");
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			
		
			rst = ps.executeQuery();

			while(rst.next()) {
			  
			   h = new Barcode();
			   h.setLineId(rst.getInt("line_id"));
			   h.setJobId(rst.getString("job_id"));
			   h.setBoxNo(rst.getString("box_no"));
			  
			   h.setBarcode(rst.getString("barcode"));
			   h.setMaterialMaster(rst.getString("MATERIAL_MASTER"));
			   h.setGroupCode(rst.getString("group_code"));
			   h.setPensItem(rst.getString("pens_item"));
			   h.setWholePriceBF(Utils.decimalFormat(rst.getDouble("WHOLE_PRICE_BF"), Utils.format_current_2_disgit));
			   h.setRetailPriceBF(Utils.decimalFormat(rst.getDouble("RETAIL_PRICE_BF"), Utils.format_current_2_disgit));
			   
			   h.setBarcodeReadonly("true");
			   h.setBarcodeStyle("disableText");
			   h.setStatus(Utils.isNull(rst.getString("status"))); 
			   h.setStatusDesc(getStatusDesc(Utils.isNull(rst.getString("status")))); 
			   
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
	
	public static List<Barcode> searchItemReport(Connection conn,Barcode o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Barcode h = null;
		int r = 1;
        List<Barcode> items = new ArrayList<Barcode>();
		try {

			sql.append("\n select substr(i.group_code,1,6) as group_code,count(*) as qty from PENSBI.PENSBME_PICK_BARCODE_ITEM i ");
			sql.append("\n where 1=1   ");
			sql.append("\n and i.status in('"+PickConstants.STATUS_CLOSE+"','"+PickConstants.STATUS_OPEN+"')");
			
			if( !Utils.isNull(o.getJobId()).equals("")){
				sql.append("\n and i.job_id = "+Utils.isNull(o.getJobId())+"");
			}
			if( !Utils.isNull(o.getBoxNo()).equals("")){
				sql.append("\n and i.box_no = '"+Utils.isNull(o.getBoxNo())+"'");
			}
			sql.append("\n group by substr(i.group_code,1,6) ");
			sql.append("\n order by substr(i.group_code,1,6) asc ");
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()) {
			   h = new Barcode();
			   h.setNo(r);
			   h.setGroupCode(rst.getString("group_code"));
			   h.setQty(rst.getInt("qty"));
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
	
	/**
	 * 
	 * @param conn
	 * @param o
	 * @return
	 * @throws Exception
	 */
	public static boolean  canCancelJob(Connection conn,Job o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		boolean canCancelJob = true;
		try {

			sql.append("\n select count(*) as c from PENSBI.PENSBME_PICK_BARCODE i   \n");
			sql.append("\n where 1=1  and status <> '"+STATUS_CANCEL+"' \n");
			
			if( !Utils.isNull(o.getJobId()).equals("")){
				sql.append("\n and i.job_id = "+Utils.isNull(o.getJobId())+"");
			}
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			if(rst.next()) {
			  int c = rst.getInt("c");
			  if(c>0){
				  canCancelJob = false;
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
		return canCancelJob;
	}
	
	public static boolean canEditGrNo(String grNo) throws Exception {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		boolean canEditGrNo = true;
		try {
			sql.append("select count(*) as c from PENSBI.PENSBME_AUTOCN_HH i  \n");
			sql.append(" where gr_no = '"+Utils.isNull(grNo)+"' \n");

			logger.debug("sql:"+sql);
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			if(rst.next()) {
			  int c = rst.getInt("c");
			  if(c > 0 ){
				  canEditGrNo = false;
			  }
			 
			}//while

			//Gr no is null canEdit = true
			if(Utils.isNull(grNo).equals("")){
				canEditGrNo = true;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
				conn.close();
			} catch (Exception e) {}
		}
		return canEditGrNo;
	}
	
	public static String save(Barcode h) throws Exception{
		Connection conn = null;
		String boxNo = "";
		try{
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			//check documentNo
			if(Utils.isNull(h.getBoxNo()).equals("")){
				//Gen JobId
				h.setBoxNo(genBoxNo(new Date()) );
				h.setStatus(JobDAO.STATUS_OPEN);
				boxNo = h.getBoxNo();
				
				logger.debug("BoxNO:"+h.getBoxNo());
				//save Head
				saveHeadModel(conn, h);
				//save line
				if(h.getItems() != null && h.getItems().size()>0){
				   for(int i=0;i<h.getItems().size();i++){
					   Barcode l = (Barcode)h.getItems().get(i);
					   l.setJobId(h.getJobId());
					   l.setBoxNo(h.getBoxNo());
					   l.setLineId(i+1);
					   l.setStatus(STATUS_OPEN);
					   
				       saveItemModel(conn, l);
				   }
				}
			}else{
				//update 
				updateHeadModel(conn, h);
				boxNo = h.getBoxNo();
				
				//Validate can edit gr_no
				if(BarcodeDAO.canEditGrNo(h.getGrNo())){
					updateGrNoInHead(conn, h);
				}
				
				//delete item
				deleteItemModel(conn, h);
				
				//save line
				if(h.getItems() != null && h.getItems().size()>0){
				   for(int i=0;i<h.getItems().size();i++){
					   Barcode l = (Barcode)h.getItems().get(i);
					   l.setJobId(h.getJobId());
					   l.setBoxNo(h.getBoxNo());
					   l.setLineId(i+1);
					   l.setStatus(STATUS_OPEN);
					   
				       saveItemModel(conn, l);
				   }
				}
			}
			
			conn.commit();
			
			return boxNo;
		}catch(Exception e){
		  conn.rollback();
		  throw e;
		}finally{
			if(conn !=null){
				conn.close();conn=null;
			}
		}
	}
	
	public static String saveManualStock(Barcode h) throws Exception{
		Connection conn = null;
		String boxNo = "";
		int lineId= 0 ;
		try{
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			//set type ManualStock
			h.setType("ManualStock");
			
			//check documentNo
			if(Utils.isNull(h.getBoxNo()).equals("")){
				//Gen JobId
				h.setBoxNo(genBoxNo(new Date()) );
				h.setStatus(JobDAO.STATUS_OPEN);
				boxNo = h.getBoxNo();
				
				logger.debug("BoxNO:"+h.getBoxNo());
				//save Head
				saveHeadModel(conn, h);
				//save line
				if(h.getItems() != null && h.getItems().size()>0){
				   for(int i=0;i<h.getItems().size();i++){
					   Barcode l = (Barcode)h.getItems().get(i);
					   //loop by qty
					   int qtyLoop = l.getQty();
					   for(int j=0;j<qtyLoop;j++){
						   lineId++;
						   l.setJobId(h.getJobId());
						   l.setBoxNo(h.getBoxNo());
						   l.setLineId(lineId);
						   l.setStatus(STATUS_OPEN);
						   
					       saveItemModel(conn, l);
					   }
				   }
				}
			}else{
				//update 
				updateHeadModel(conn, h);
				boxNo = h.getBoxNo();
				
				//Validate can edit gr_no
				if(BarcodeDAO.canEditGrNo(h.getGrNo())){
					updateGrNoInHead(conn, h);
				}
				
				//delete item
				deleteItemModel(conn, h);
				
				//save line
				if(h.getItems() != null && h.getItems().size()>0){
				   for(int i=0;i<h.getItems().size();i++){
					   Barcode l = (Barcode)h.getItems().get(i);
					   //loop by qty
					   int qtyLoop = l.getQty();
					   for(int j=0;j<qtyLoop;j++){
						   lineId++;
						   l.setJobId(h.getJobId());
						   l.setBoxNo(h.getBoxNo());
						   l.setLineId(lineId);
						   l.setStatus(STATUS_OPEN);
						   
					       saveItemModel(conn, l);
					   }
				   }
				}
			}
			
			conn.commit();
			
			return boxNo;
		}catch(Exception e){
		  conn.rollback();
		  throw e;
		}finally{
			if(conn !=null){
				conn.close();conn=null;
			}
		}
	}
	
	// ( Running :  yymmxxxx  �� 57030001 )			
	 public static String genBoxNo(Date date) throws Exception{
       String docNo = "";
       Connection conn = null;
		   try{
			   conn = DBConnection.getInstance().getConnection(); 
			   
			   String today = df.format(date);
			   String[] d1 = today.split("/");
			   int curYear = Integer.parseInt(d1[0].substring(0,4));
			   int curMonth = Integer.parseInt(d1[1]);
			 
			 //get Seq
			   int seq = SequenceProcess.getNextValue(conn,"BOX_NO","BOX_NO",date);
			   
			   docNo = new DecimalFormat("00").format(curYear)+new DecimalFormat("00").format(curMonth)+new DecimalFormat("0000").format(seq);
		  
		   }catch(Exception e){
			   throw e;
		   }finally{
			   if(conn !=null){
				   conn.close();conn=null;
			   }
		   }
		  return docNo;
	}
		 
	 
	 public static void saveHeadModel(Connection conn,Barcode o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Insert");
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" INSERT INTO PENSBI.PENSBME_PICK_BARCODE \n");
				sql.append(" (JOB_ID,BOX_NO, TRANSACTION_DATE,   \n");
				sql.append("  STATUS, CREATE_DATE, CREATE_USER,REMARK,WAREHOUSE,GR_NO,TYPE)  \n");
			    sql.append(" VALUES (?, ?, ?, ?, ?, ?, ? , ?,?,?) \n");
				ps = conn.prepareStatement(sql.toString());
					
				Date openDate = DateUtil.parse( o.getTransactionDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				
				int c =1;
				
				ps.setInt(c++, Integer.parseInt(o.getJobId()));
				ps.setString(c++, o.getBoxNo());
				ps.setTimestamp(c++, new java.sql.Timestamp(openDate.getTime()));
				ps.setString(c++, o.getStatus());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setString(c++, o.getCreateUser());
				ps.setString(c++, o.getRemark());
				ps.setString(c++, o.getWareHouse());
				ps.setString(c++, o.getGrNo());
				ps.setString(c++, Utils.isNull(o.getType()));
				ps.executeUpdate();
				
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
	 
	 public static void saveHeadModelForMove(Connection conn,Barcode o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Insert box_no_ref["+o.getBoxNoRef()+"]");
			try{
				
				StringBuffer sql = new StringBuffer("");
				sql.append(" INSERT INTO PENSBI.PENSBME_PICK_BARCODE \n");
				sql.append(" (JOB_ID,BOX_NO, TRANSACTION_DATE,   \n");
				sql.append("  STATUS, CREATE_DATE, CREATE_USER,REMARK,BOX_NO_REF)  \n");
			
			    sql.append(" VALUES (?, ?, ?, ?, ?, ?, ? ,?) \n");
				
				ps = conn.prepareStatement(sql.toString());
					
				Date openDate = DateUtil.parse( o.getTransactionDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				
				int c =1;
				
				ps.setInt(c++, Integer.parseInt(o.getJobId()));
				ps.setString(c++, o.getBoxNo());
				ps.setTimestamp(c++, new java.sql.Timestamp(openDate.getTime()));
				ps.setString(c++, o.getStatus());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setString(c++, o.getCreateUser());
				ps.setString(c++, o.getRemark());
				ps.setString(c++, Utils.isNull(o.getBoxNoRef()));
				
				ps.executeUpdate();
				
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
	 
	 public static void saveItemModel(Connection conn,Barcode o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Insert");
			try{
				
				StringBuffer sql = new StringBuffer("");
				sql.append(" INSERT INTO PENSBI.PENSBME_PICK_BARCODE_ITEM \n");
				sql.append(" (JOB_ID, BOX_NO, LINE_ID, BARCODE, MATERIAL_MASTER, GROUP_CODE, PENS_ITEM,    \n");
				sql.append("  WHOLE_PRICE_BF, RETAIL_PRICE_BF, CREATE_DATE, CREATE_USER,STATUS) \n");
			
			    sql.append(" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? ,?) \n");
				
				ps = conn.prepareStatement(sql.toString());
				
				int c =1;
				
				ps.setInt(c++, Integer.parseInt(o.getJobId()));
				ps.setString(c++, o.getBoxNo());
				ps.setInt(c++, o.getLineId());
				ps.setString(c++, o.getBarcode());
				ps.setString(c++, o.getMaterialMaster());
				ps.setString(c++, o.getGroupCode());
				ps.setString(c++, o.getPensItem());
				ps.setDouble(c++, Utils.isDoubleNull(o.getWholePriceBF()));
				ps.setDouble(c++, Utils.isDoubleNull(o.getRetailPriceBF()));
				
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

		public static void updateHeadModel(Connection conn,Barcode o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Update");
			int  c = 1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" UPDATE PENSBI.PENSBME_PICK_BARCODE SET  \n");
				sql.append(" REMARK =? , STATUS = ? ,UPDATE_USER =? ,UPDATE_DATE = ?   \n");
				sql.append(" WHERE JOB_ID =? and BOX_NO = ?  \n" );
				ps = conn.prepareStatement(sql.toString());
				ps.setString(c++, o.getRemark());
				ps.setString(c++, o.getStatus());
				ps.setString(c++, o.getUpdateUser());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setInt(c++, Integer.parseInt(o.getJobId()));
				ps.setString(c++, Utils.isNull(o.getBoxNo()));
				
				ps.executeUpdate();
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
		
		public static void updateGrNoInHead(Connection conn,Barcode o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Update");
			int  c = 1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" UPDATE PENSBI.PENSBME_PICK_BARCODE SET  \n");
				sql.append(" GR_NO =? , STATUS = ? ,UPDATE_USER =? ,UPDATE_DATE = ?   \n");
				sql.append(" WHERE JOB_ID =? and BOX_NO = ?  \n" );

				ps = conn.prepareStatement(sql.toString());
					
				ps.setString(c++, o.getGrNo());
				ps.setString(c++, o.getStatus());
				ps.setString(c++, o.getUpdateUser());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setInt(c++, Integer.parseInt(o.getJobId()));
				ps.setString(c++, Utils.isNull(o.getBoxNo()));
				
				ps.executeUpdate();
				
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
		public static void updateBarcodeHeadStatusModelByPK(Connection conn,Barcode o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Update");
			int  c = 1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" UPDATE PENSBI.PENSBME_PICK_BARCODE SET  \n");
				sql.append(" STATUS = ? ,update_user =?,update_date =?   \n");
				
				sql.append(" WHERE JOB_ID =? and BOX_NO = ? \n" );

				logger.debug("sql:"+sql.toString());
				
				ps = conn.prepareStatement(sql.toString());
					
				ps.setString(c++, o.getStatus());
				ps.setString(c++, o.getUpdateUser());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setInt(c++, Integer.parseInt(o.getJobId()));
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
		
		public static void updateBarcodeHeadNewJobIdModelByPK(Connection conn,String newJobId,Barcode o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Update");
			int  c = 1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" UPDATE PENSBI.PENSBME_PICK_BARCODE SET  \n");
				sql.append(" JOB_ID = ? ,update_user =?,update_date =?   \n");
				
				sql.append(" WHERE JOB_ID =? and BOX_NO = ? \n" );

				logger.debug("sql:"+sql.toString());
				
				ps = conn.prepareStatement(sql.toString());
					
				ps.setString(c++, newJobId);
				ps.setString(c++, o.getUpdateUser());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setInt(c++, Integer.parseInt(o.getJobId()));
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
		
		public static void updateBarcodeNewWarehouseByPK(Connection conn,String warehouse,Barcode o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Update");
			int  c = 1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" UPDATE PENSBI.PENSBME_PICK_BARCODE SET  \n");
				sql.append(" remark =?,warehouse = ? ,update_user =?,update_date =?   \n");
				
				sql.append(" WHERE JOB_ID =? and BOX_NO = ? \n" );

				logger.debug("sql:"+sql.toString());
				
				ps = conn.prepareStatement(sql.toString());
				
				ps.setString(c++, Utils.isNull(o.getRemark()));
				ps.setString(c++, warehouse);
				ps.setString(c++, o.getUpdateUser());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setInt(c++, Integer.parseInt(o.getJobId()));
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
		
		public static void updateBarcodeLineNewJobIdModelByPK(Connection conn,String newJobId,Barcode o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Update");
			int  c = 1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" UPDATE PENSBI.PENSBME_PICK_BARCODE_ITEM SET  \n");
				sql.append(" JOB_ID = ?,update_user =?,update_date =?   \n");
				
				sql.append(" WHERE JOB_ID =? and BOX_NO = ? \n" );

				logger.debug("sql:"+sql.toString());
				
				ps = conn.prepareStatement(sql.toString());
					
				ps.setString(c++, newJobId);
				ps.setString(c++, o.getUpdateUser());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setInt(c++, Integer.parseInt(o.getJobId()));
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
		
		
		public static void updateBarcodeLineStatusModelByPK(Connection conn,Barcode o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Update");
			int  c = 1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" UPDATE PENSBI.PENSBME_PICK_BARCODE_ITEM SET  \n");
				sql.append(" STATUS = ?,update_user =?,update_date =?   \n");
				
				sql.append(" WHERE JOB_ID =? and BOX_NO = ? \n" );

				logger.debug("sql:"+sql.toString());
				
				ps = conn.prepareStatement(sql.toString());
					
				ps.setString(c++, o.getStatus());
				ps.setString(c++, o.getUpdateUser());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setInt(c++, Integer.parseInt(o.getJobId()));
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
		
		public static void updateBarcodeLineStatusModelByPKALL(Connection conn,Barcode o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Update");
			int  c = 1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" UPDATE PENSBI.PENSBME_PICK_BARCODE_ITEM SET  \n");
				sql.append(" STATUS = ?,update_user =?,update_date =?   \n");
				
				sql.append(" WHERE JOB_ID =? and BOX_NO = ? and LINE_ID =? \n" );

				logger.debug("sql:"+sql.toString());
				
				ps = conn.prepareStatement(sql.toString());
					
				ps.setString(c++, o.getStatus());
				ps.setString(c++, o.getUpdateUser());
				ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
				ps.setInt(c++, Integer.parseInt(o.getJobId()));
				ps.setString(c++, o.getBoxNo());
				ps.setInt(c++, o.getLineId());
				
				ps.executeUpdate();
				
			}catch(Exception e){
				throw e;
			}finally{
				if(ps != null){
					ps.close();ps=null;
				}
			}
		}
		
		public static void updateBarcodeHeadStatusModelByJobId(Connection conn,Barcode o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Update");
			int  c = 1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" UPDATE PENSBI.PENSBME_PICK_BARCODE SET  \n");
				sql.append(" STATUS = ? ,update_user =?,update_date =?   \n");
				
				sql.append(" WHERE JOB_ID =? AND STATUS ='"+STATUS_OPEN+"' \n" );

				logger.debug("sql:"+sql.toString());
				
				ps = conn.prepareStatement(sql.toString());
					
				ps.setString(c++, o.getStatus());
				ps.setString(c++, o.getUpdateUser());
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
		
		public static void updateBarcodeLineStatusModelByJobId(Connection conn,Barcode o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Update");
			int  c = 1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" UPDATE PENSBI.PENSBME_PICK_BARCODE_ITEM SET  \n");
				sql.append(" STATUS = ? ,update_user =?,update_date =?   \n");
				
				sql.append(" WHERE JOB_ID =? AND STATUS ='"+STATUS_OPEN+"' \n" );

				logger.debug("sql:"+sql.toString());
				
				ps = conn.prepareStatement(sql.toString());
					
				ps.setString(c++, o.getStatus());
				ps.setString(c++, o.getUpdateUser());
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
		
		public static void deleteItemModel(Connection conn,Barcode o) throws Exception{
			PreparedStatement ps = null;
			logger.debug("Update");
			int  c = 1;
			try{
				StringBuffer sql = new StringBuffer("");
				sql.append(" DELETE FROM PENSBI.PENSBME_PICK_BARCODE_ITEM  \n");
				sql.append(" WHERE JOB_ID =? \n" );
				sql.append(" AND BOX_NO =? \n" );

				ps = conn.prepareStatement(sql.toString());
					
				ps.setInt(c++, Integer.parseInt(o.getJobId()));
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
		
		
		@Deprecated
		public static List<Barcode> getItemsForPick(Connection conn,Barcode o ) throws Exception {
			PreparedStatement ps = null;
			ResultSet rst = null;
			StringBuilder sql = new StringBuilder();
			Barcode h = null;
	
	        List<Barcode> items = new ArrayList<Barcode>();
			try {
				sql.append("\n select i.* from PENSBI.PENSBME_PICK_BARCODE_ITEM i   \n");
				sql.append("\n where 1=1  and ( i.status ='' or i.status is null) \n");
				
				if( !Utils.isNull(o.getBoxNo()).equals("")){
					sql.append("\n and i.box_no = '"+Utils.isNull(o.getBoxNo())+"'");
				}
				if( !Utils.isNull(o.getMaterialMaster()).equals("")){
					sql.append("\n and i.material_master = '"+Utils.isNull(o.getMaterialMaster())+"'");
				}
				if( !Utils.isNull(o.getGroupCode()).equals("")){
					sql.append("\n and i.group_code = '"+Utils.isNull(o.getGroupCode())+"'");
				}
				if( !Utils.isNull(o.getPensItem()).equals("")){
					sql.append("\n and i.pens_item = '"+Utils.isNull(o.getPensItem())+"'");
				}
				
				sql.append("\n order by i.line_id asc ");
				logger.debug("sql:"+sql);
				
				ps = conn.prepareStatement(sql.toString());
				rst = ps.executeQuery();

				while(rst.next()) {
				
				   h = new Barcode();
				   h.setLineId(rst.getInt("line_id"));
				   h.setJobId(rst.getString("job_id"));
				   h.setBoxNo(rst.getString("box_no"));
				   h.setBarcode(rst.getString("barcode"));
				   h.setMaterialMaster(rst.getString("MATERIAL_MASTER"));
				   h.setGroupCode(rst.getString("group_code"));
				   h.setPensItem(rst.getString("pens_item"));
				   h.setWholePriceBF(Utils.decimalFormat(rst.getDouble("WHOLE_PRICE_BF"), Utils.format_current_2_disgit));
				   h.setRetailPriceBF(Utils.decimalFormat(rst.getDouble("RETAIL_PRICE_BF"), Utils.format_current_2_disgit));

				   items.add(h); 
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
		
		

}
