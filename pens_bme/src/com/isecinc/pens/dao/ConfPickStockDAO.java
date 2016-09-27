package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.Onhand;
import com.isecinc.pens.bean.PickStock;
import com.isecinc.pens.bean.ReqPickStock;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.process.SequenceProcess;

public class ConfPickStockDAO extends PickConstants{
	
	protected static Logger logger = Logger.getLogger("PENS");
	protected static SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Utils.local_th);

	public static ReqPickStock save(Connection conn,ReqPickStock h,Map<String, ReqPickStock> dataSaveMapAll) throws Exception{
		ReqPickStock result = new ReqPickStock();
		result.setResultProcess(true);//default
		
		try{
			
			return result;
		}catch(Exception e){
		  throw e;
		}finally{
			
		}
	}
	
	
	private static int updateOldStockItemToStockFinishBarcode(Connection conn,ReqPickStock o) throws Exception{
		PreparedStatement ps = null;
		int r = 0;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" UPDATE PENSBI.PENSBME_STOCK_FINISHED SET ONHAND_QTY = (ONHAND_QTY+ ?),UPDATE_DATE=?,UPDATE_USER = ? \n");
			sql.append(" WHERE PENS_ITEM =?  and material_Master = ? and group_code = ?  and barcode = ? \n" );

			ps = conn.prepareStatement(sql.toString());
				
			ps.setInt(1, Utils.convertStrToInt(o.getQty()));
			ps.setTimestamp(2, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(3, o.getUpdateUser());
			ps.setString(4, o.getPensItem());
			ps.setString(5, o.getMaterialMaster());
			ps.setString(6, o.getGroupCode());
			ps.setString(7, o.getBarcode());
			
			r = ps.executeUpdate();
			return r;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}
	

	public static List<ReqPickStock> searchHead(ReqPickStock o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		ReqPickStock h = null;
		List<ReqPickStock> items = new ArrayList<ReqPickStock>();
		int r = 1;
		int c = 1;
		try {
			sql.append("\n SELECT j.* ");
			sql.append("\n  ,(select M.pens_desc from pensbi.PENSBME_MST_REFERENCE M ");
			sql.append("\n   where M.reference_code = 'Store' and M.pens_value = j.customer_no) as STORE_NAME ");
			sql.append("\n  ,(select sum(s.req_qty) from PENSBME_STOCK_ISSUE_ITEM s Where s.issue_req_no=j.issue_req_no )as total_req_qty");
			sql.append("\n  ,(select sum(s.issue_qty) from PENSBME_STOCK_ISSUE_ITEM s Where s.issue_req_no=j.issue_req_no )as total_issue_qty");
			sql.append("\n from PENSBME_STOCK_ISSUE j ");
			sql.append("\n where 1=1   ");
			sql.append("\n and status in('"+PickConstants.STATUS_POST+"','"+PickConstants.STATUS_ISSUED+"','"+PickConstants.STATUS_BEF+"')");
			
			if( !Utils.isNull(o.getWareHouse()).equals("")){
				sql.append("\n and warehouse = '"+Utils.isNull(o.getWareHouse())+"'  ");
			}
			if( !Utils.isNull(o.getIssueReqNo()).equals("")){
				sql.append("\n and issue_req_no = '"+Utils.isNull(o.getIssueReqNo())+"'  ");
			}
			if( !Utils.isNull(o.getStatus()).equals("")){
				sql.append("\n and status = '"+Utils.isNull(o.getStatus())+"'  ");
			}
			if( !Utils.isNull(o.getCustGroup()).equals("")){
				sql.append("\n and CUST_GROUP = '"+Utils.isNull(o.getCustGroup())+"'  ");
			}
			if( !Utils.isNull(o.getStoreCode()).equals("")){
				sql.append("\n and CUSTOMER_NO = '"+Utils.isNull(o.getStoreCode())+"'  ");
			}
			if( !Utils.isNull(o.getIssueReqDate()).equals("")){
				Date tDate  = Utils.parse(o.getIssueReqDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				String returnDateStr = Utils.stringValue(tDate, Utils.DD_MM_YYYY_WITH_SLASH);
				
				sql.append("\n and ISSUE_REQ_DATE = to_date('"+returnDateStr+"','dd/mm/yyyy') ");
			}
			if( !Utils.isNull(o.getStatusDate()).equals("")){
				Date tDate  = Utils.parse(o.getStatusDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				String returnDateStr = Utils.stringValue(tDate, Utils.DD_MM_YYYY_WITH_SLASH);
				
				sql.append("\n and TRUNC(STATUS_DATE) = to_date('"+returnDateStr+"','dd/mm/yyyy') ");
			}
			
			sql.append("\n order by issue_req_no DESC ");
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   h = new ReqPickStock();
			   h.setIssueReqDate(Utils.stringValue(rst.getDate("issue_req_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			   h.setIssueReqNo(Utils.isNull(rst.getString("issue_req_no")));
			   h.setStatus(Utils.isNull(rst.getString("status")));
			   h.setStatusDesc(getStatusDesc(h.getStatus()));
			   h.setRemark(Utils.isNull(rst.getString("remark")));
			   h.setRequestor(Utils.isNull(rst.getString("requestor")));
			   h.setStoreCode(Utils.isNull(rst.getString("customer_no")));
			   h.setWareHouse(Utils.isNull(rst.getString("warehouse")));
			   h.setStoreName(Utils.isNull(rst.getString("store_name")));
			   
			   if(rst.getDate("need_date") != null){
			     h.setNeedDate(Utils.stringValue(rst.getDate("need_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			   }
			   if(rst.getDate("delivery_date") != null){
				   h.setDeliveryDate(Utils.stringValue(rst.getDate("delivery_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				}
			   h.setTotalCtn(rst.getInt("total_ctn"));
			   h.setExported(Utils.isNull(rst.getString("exported")));
			  
			   h.setTotalReqQty(rst.getInt("total_req_qty"));
			   h.setTotalIssueQty(rst.getInt("total_issue_qty"));
			   
		
			   
			   if(Utils.isNull(rst.getString("status")).equals(STATUS_BEF)){
				   h.setCanConfirm(true);
			   }else{
				   h.setCanConfirm(false); 
			   }
			   
			   if(Utils.isNull(rst.getString("status")).equals(STATUS_POST) 
					   || Utils.isNull(rst.getString("status")).equals(STATUS_BEF)){
				   h.setCanEdit(true);
			   }else{
				   h.setCanEdit(false); 
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
	
	//barcode status = close or null()
	public static List<ReqPickStock> searchBarcodeItemInStock(Connection conn,ReqPickStock o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		ReqPickStock h = null;
		int r = 1;
        List<ReqPickStock> items = new ArrayList<ReqPickStock>();
		try {

			sql.append("\n select i.* from PENSBI.PENSBME_STOCK_ISSUE_ITEM i   \n");
			sql.append("\n where 1=1 and (status ='' or status is null or status ='"+STATUS_CLOSE+"') \n");
			
			if( !Utils.isNull(o.getGroupCode()).equals("")){
				sql.append("\n and i.group_code = '"+Utils.isNull(o.getGroupCode())+"'");
			}
			if( !Utils.isNull(o.getMaterialMaster()).equals("")){
				sql.append("\n and i.material_master = '"+Utils.isNull(o.getMaterialMaster())+"'");
			}
			if( !Utils.isNull(o.getPensItem()).equals("")){
				sql.append("\n and i.pens_item = '"+Utils.isNull(o.getPensItem())+"'");
			}
			sql.append("\n order by line_id asc ");
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			  
			   h = new ReqPickStock();
			   h.setLineId(rst.getInt("line_id"));
			 
			   h.setMaterialMaster(rst.getString("MATERIAL_MASTER"));
			   h.setGroupCode(rst.getString("group_code"));
			   h.setPensItem(rst.getString("pens_item"));
			   
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
	
	public static List<ReqPickStock> searchReqPickStockItem(Connection conn,ReqPickStock o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		ReqPickStock h = null;
		int r = 1;
        List<ReqPickStock> items = new ArrayList<ReqPickStock>();
		try {

			sql.append("\n select i.* from PENSBI.PENSBME_STOCK_ISSUE i   \n");
			sql.append("\n where 1=1   \n");
			
			if( !Utils.isNull(o.getGroupCode()).equals("")){
				sql.append("\n and i.group_code = '"+Utils.isNull(o.getGroupCode())+"'");
			}
			if( !Utils.isNull(o.getMaterialMaster()).equals("")){
				sql.append("\n and i.material_master = '"+Utils.isNull(o.getMaterialMaster())+"'");
			}
			if( !Utils.isNull(o.getPensItem()).equals("")){
				sql.append("\n and i.pens_item = '"+Utils.isNull(o.getPensItem())+"'");
			}
			if( !Utils.isNull(o.getIssueReqNo()).equals("")){
				sql.append("\n and i.issue_req_no = '"+Utils.isNull(o.getIssueReqNo())+"'");
			}
			
			sql.append("\n order by line_id asc ");
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			  
			   h = new ReqPickStock();
			   h.setIssueReqNo(rst.getString("issue_req_no"));
			   h.setLineId(rst.getInt("line_id"));
			  
			   h.setMaterialMaster(rst.getString("MATERIAL_MASTER"));
			   h.setGroupCode(rst.getString("group_code"));
			   h.setPensItem(rst.getString("pens_item"));
			   
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
	
	public static List<ReqPickStock> searchReqPickStockItemByIssueReqNo(Connection conn,ReqPickStock o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		ReqPickStock h = null;
		int r = 1;
        List<ReqPickStock> items = new ArrayList<ReqPickStock>();
     
		try {

			sql.append("\n select i.* from PENSBI.PENSBME_STOCK_ISSUE i ");
			sql.append("\n where 1=1 ");
			if( !Utils.isNull(o.getIssueReqNo()).equals("")){
				sql.append("\n and i.issue_req_no = '"+Utils.isNull(o.getIssueReqNo())+"'");
			}
			sql.append("\n order by line_id asc ");
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			  
			   h = new ReqPickStock();
			   h.setLineId(rst.getInt("line_id"));
			   
			   h.setMaterialMaster(rst.getString("MATERIAL_MASTER"));
			   h.setGroupCode(rst.getString("group_code"));
			   h.setPensItem(rst.getString("pens_item"));
			  	   
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

	
	/** Get Total row in Stock Finish */
	public static int getTotalRowInStockFinish(Connection conn) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int totalRecord = 0;
		try {
			sql.append("\n select count(*) as total_row ");
			sql.append("\n from PENSBME_STOCK_FINISHED ");

			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			if(rst.next()) {
			   totalRecord = rst.getInt("total_row");
			}//while

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return totalRecord;
	}
	
	public static int getTotalRowInStockIssueItemCaseNoEdit(Connection conn,ReqPickStock p) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int totalRecord = 0;
		try {
			// substract from Stock issue status = O(Open)
			sql.append("\n  select count(*) as total_row FROM( ");
			sql.append("\n 	  SELECT distinct group_code ,pens_item ,material_master,barcode ");
			sql.append("\n 	  FROM PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i  ");
			sql.append("\n 	  WHERE 1=1  ");
			sql.append("\n 	  AND h.issue_req_no = i.issue_req_no ");
			sql.append("\n 	  AND h.issue_req_no ='"+p.getIssueReqNo()+"'");
			sql.append("\n )");
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			if(rst.next()) {
			   totalRecord = rst.getInt("total_row");
			}//while

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return totalRecord;
	}
	
	public static int getTotalRowInStockFinishGroupByGroupCode(Connection conn,ReqPickStock p) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int totalRecord = 0;
		try {
			//Edit
			if( !p.isNewReq()){
				sql.append("\n SELECT count(*) as total_row FROM(");
				sql.append("\n    SELECT distinct group_code FROM( ");
				sql.append("\n       SELECT group_code, sum(onhand_qty) as onhand_qty FROM( ");
				sql.append("\n         SELECT group_code ,sum(onhand_qty) as onhand_qty");
				sql.append("\n  	   from PENSBME_STOCK_FINISHED ");
				sql.append("\n  	   GROUP BY group_code ");
				sql.append("\n         UNION ALL ");
				sql.append("\n 		   SELECT group_code ,( (-1) * sum(qty) )as onhand_qty");
				sql.append("\n 		   FROM PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i  ");
				sql.append("\n 		   WHERE 1=1  ");
				sql.append("\n 		   AND h.issue_req_no = i.issue_req_no ");
				sql.append("\n 		   AND h.issue_req_no <> '"+p.getIssueReqNo()+"'");
				sql.append("\n  	   GROUP BY group_code ");
				sql.append("\n       ) GROUP BY group_code ");
				sql.append("\n    ) WHERE onhand_qty > 0 ");
				
				sql.append("\n    UNION  ");
				 // substract from Stock issue status = O(Open)
				sql.append("\n 	  SELECT distinct group_code ");
				sql.append("\n 	  FROM PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i  ");
				sql.append("\n 	  WHERE 1=1  ");
				sql.append("\n 	  AND h.issue_req_no = i.issue_req_no ");
				sql.append("\n 	  AND h.issue_req_no ='"+p.getIssueReqNo()+"'");
				sql.append("\n   )");
			}else{
				//Case Add new check onhand_qty > 0
				sql.append("\n SELECT count(*) as total_row from(");
				sql.append("\n  SELECT distinct M2.group_code from( ");
				sql.append("\n    SELECT M.group_code,M.pens_item,NVL(SUM(onhand_qty),0) as onhand_qty,0 as qty FROM ( ");
				sql.append("\n  	 select group_code,pens_item,onhand_qty  ");
				sql.append("\n  	 from PENSBME_STOCK_FINISHED ");
				sql.append("\n  	 where 1=1   ");
				sql.append("\n  	 and onhand_qty <> 0 ");
				sql.append("\n       UNION ALL ");
				 // substract from Stock issue status = O(Open)
				sql.append("\n 		 SELECT group_code,pens_item  ,(-1* qty ) as onhand_qty ");
				sql.append("\n 		 FROM PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i  ");
				sql.append("\n 		 WHERE 1=1  ");
				sql.append("\n 		 AND h.issue_req_no = i.issue_req_no ");
				sql.append("\n 		 AND h.status ='"+PickConstants.STATUS_OPEN+"'");
				sql.append("\n     )M  ");
				sql.append("\n    GROUP BY M.group_code,M.pens_item");
				sql.append("\n   )M2 WHERE M2.ONHAND_QTY > 0");
				sql.append("\n )");
			}
		
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			if(rst.next()) {
			   totalRecord = rst.getInt("total_row");
			}//while

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return totalRecord;
	}
	
	/*** Get Total Qty in Stock issue Item **/
	public static int getTotalReqQtyInStockIssueItem(Connection conn,ReqPickStock pickStock ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int totalRecord = 0;
		try {
			sql.append("\n select nvl(sum(req_qty),0) as qty ");
			sql.append("\n from PENSBME_STOCK_ISSUE_ITEM i  ");
			sql.append("\n where 1=1  ");
			if( !Utils.isNull(pickStock.getIssueReqNo()).equals("")){
			   sql.append("\n and i.issue_req_no ='"+pickStock.getIssueReqNo()+"'");
			}
			sql.append("\n group by i.issue_req_no");
				
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			if(rst.next()) {
			   totalRecord = rst.getInt("qty");
			}//while

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return totalRecord;
	}
	
	public static int getTotalReqQtyInScanCheckout(Connection conn,ReqPickStock pickStock ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int totalRecord = 0;
		try {
			sql.append("\n select nvl(count(*),0) as qty ");
			sql.append("\n from PENSBME_SCAN_CHECKOUT_ITEM i  ");
			sql.append("\n where 1=1  ");
			if( !Utils.isNull(pickStock.getIssueReqNo()).equals("")){
			   sql.append("\n and i.issue_req_no ='"+pickStock.getIssueReqNo()+"'");
			}
			sql.append("\n group by i.issue_req_no");
				
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			if(rst.next()) {
			   totalRecord = rst.getInt("qty");
			}//while

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return totalRecord;
	}
	
	public static int getTotalIssueQtyInStockIssueItem(Connection conn,ReqPickStock pickStock ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int totalRecord = 0;
		try {
			sql.append("\n select nvl(sum(issue_qty),0) as qty     ");
			sql.append("\n from PENSBME_STOCK_ISSUE_ITEM i  ");
			sql.append("\n where 1=1  ");
			if( !Utils.isNull(pickStock.getIssueReqNo()).equals("")){
			   sql.append("\n and i.issue_req_no ='"+pickStock.getIssueReqNo()+"'");
			}
			sql.append("\n group by i.issue_req_no");
				
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			if(rst.next()) {
			   totalRecord = rst.getInt("qty");
			}//while

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return totalRecord;
	}
	
	
	
	public static List<ReqPickStock> getStockItemListByIssueReqNo(Connection conn,ReqPickStock o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		ReqPickStock h = null;
		int r = 1;
        List<ReqPickStock> items = new ArrayList<ReqPickStock>();
		try {
			sql.append("\n select i.barcode,i.MATERIAL_MASTER,i.group_code,i.pens_item ,qty ");
			sql.append("\n from PENSBI.PENSBME_STOCK_ISSUE_ITEM i ");
			sql.append("\n where 1=1 ");
			if( !Utils.isNull(o.getIssueReqNo()).equals("")){
				sql.append("\n and i.issue_req_no = '"+Utils.isNull(o.getIssueReqNo())+"'");
			}
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   h = new ReqPickStock();
			   h.setIssueReqNo(o.getIssueReqNo());
			   
			   h.setMaterialMaster(rst.getString("MATERIAL_MASTER"));
			   h.setGroupCode(rst.getString("group_code"));
			   h.setPensItem(rst.getString("pens_item"));
			   h.setBarcode(rst.getString("barcode"));
			   
			   h.setQty(Utils.decimalFormat(rst.getInt("qty"), Utils.format_current_no_disgit));
			   h.setQtyInt(rst.getInt("qty"));
			  	   
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
	
	public static ReqPickStock searchReqPickStockItemByIssueReqNo4Report(Connection conn,ReqPickStock o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		ReqPickStock h = null;
		int r = 1;
        List<ReqPickStock> items = new ArrayList<ReqPickStock>();
        int totalQty = 0;
		try {
			sql.append("\n select i.job_id,i.box_no,i.MATERIAL_MASTER,i.group_code,i.pens_item ,count(*) as qty ");
			sql.append("\n ,(select max(name) from PENSBME_PICK_JOB j where j.job_id = i.job_id) as job_name ");
			sql.append("\n from PENSBI.PENSBME_PICK_STOCK_I i ");
			sql.append("\n where 1=1 ");
			if( !Utils.isNull(o.getIssueReqNo()).equals("")){
				sql.append("\n and i.issue_req_no = '"+Utils.isNull(o.getIssueReqNo())+"'");
			}
			sql.append("\n group by i.job_id,i.box_no,i.MATERIAL_MASTER,i.group_code,i.pens_item ");
			sql.append("\n order by i.job_id,i.box_no,i.MATERIAL_MASTER,i.group_code,i.pens_item  asc ");
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   h = new ReqPickStock();
			
			   h.setMaterialMaster(rst.getString("MATERIAL_MASTER"));
			   h.setGroupCode(rst.getString("group_code"));
			   h.setPensItem(rst.getString("pens_item"));
			   
			   h.setQty(Utils.decimalFormat(rst.getInt("qty"), Utils.format_current_no_disgit));
			   h.setQtyInt(rst.getInt("qty"));
			   
			   totalQty += rst.getInt("qty");
					   
			   items.add(h);
			   r++;
			   
			}//while
			
			o.setItems(items);
			o.setTotalQty(totalQty);
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
	
	public static ReqPickStock searchReqPickStock(Connection conn,ReqPickStock h ,boolean getItems) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		try {
			sql.append("\n select  j.*  ");
			sql.append("\n  ,(SELECT m.pens_desc from PENSBME_MST_REFERENCE m ");
			sql.append("\n    where 1=1  and j.customer_no = m.pens_value and m.reference_code ='Store') as store_name ");
			sql.append("\n from PENSBME_STOCK_ISSUE j ");
			sql.append("\n where 1=1   ");
			sql.append("\n and issue_req_no = '"+h.getIssueReqNo()+"'");

			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			if(rst.next()) {
				
			   h.setIssueReqNo(Utils.isNull(rst.getString("issue_req_no")));
			   if(rst.getDate("issue_req_date") != null){
				  String dateStr = Utils.stringValue(rst.getDate("issue_req_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			      h.setIssueReqDate(dateStr);
			   }
			   if(rst.getDate("need_date") != null){
				  String dateStr = Utils.stringValue(rst.getDate("need_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				  h.setNeedDate(dateStr);
			    }
			   h.setStatus(rst.getString("status"));
			   h.setStatus(rst.getString("status"));
			   h.setStatusDesc(getStatusDesc(h.getStatus()));
			   h.setRequestor(rst.getString("requestor"));
			   h.setRemark(rst.getString("remark"));
			   
			   h.setCustGroup(Utils.isNull(rst.getString("cust_group"))); 
			   h.setStoreCode(Utils.isNull(rst.getString("customer_no"))); 
			   h.setStoreName(Utils.isNull(rst.getString("store_name"))); 
			   h.setStoreNo(Utils.isNull(rst.getString("store_no"))); 
			   h.setSubInv(Utils.isNull(rst.getString("sub_inv"))); 
			   h.setWareHouse(Utils.isNull(rst.getString("warehouse"))); 
			   
			   if(rst.getDate("delivery_date") != null){
				   h.setDeliveryDate(Utils.stringValue(rst.getDate("delivery_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				}
			   h.setTotalCtn(rst.getInt("total_ctn"));
			   h.setExported(Utils.isNull(rst.getString("exported")));

			 
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

	
	public static ReqPickStock getStockIssueItemCaseNoEdit(Connection conn,ReqPickStock pickStock,int pageNumber,boolean allRec) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		List<ReqPickStock> items = new ArrayList<ReqPickStock>();
		int r = 1;
		int totalQty = 0;
		logger.debug("***getGroupCodeInStockListCaseNoEdit***");
		try {
				//Case Edit
				sql.append("\n SELECT  M2.* FROM(");//M2
				sql.append("\n   SELECT  M.*  FROM("); //M
				sql.append("\n     SELECT A.*, rownum r__ FROM ( ");//A
				sql.append("\n        SELECT  U.*  FROM( ");//U
				
		                sql.append("\n 	SELECT M.* ,NVL(P.issue_qty,0) as issue_qty ,NVL(P.req_qty,0) as req_qty  FROM ( ");
						sql.append("\n 		select group_code,pens_item,material_master,barcode  ");
						sql.append("\n 		from PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i  ");
						sql.append("\n 		where 1=1  ");
						sql.append("\n 		and h.issue_req_no = i.issue_req_no ");
						sql.append("\n 		and h.warehouse = '"+pickStock.getWareHouse()+"'");
						if( !Utils.isNull(pickStock.getIssueReqNo()).equals("")){
						sql.append("\n 		and h.issue_req_no ='"+pickStock.getIssueReqNo()+"'");
						}
						sql.append("\n 		group by i.BARCODE, MATERIAL_MASTER,group_code,pens_item ");

						sql.append("\n   ) M LEFT OUTER JOIN  ");
						
						sql.append("\n 	 ( select group_code,pens_item,material_master,barcode ,NVL(SUM(i.issue_qty),0) as issue_qty,NVL(SUM(i.req_qty),0) as req_qty  ");
						sql.append("\n 		 from PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i  ");
						sql.append("\n 		 where 1=1  ");
						sql.append("\n		 and h.issue_req_no = i.issue_req_no ");
						sql.append("\n 		 and h.warehouse = '"+pickStock.getWareHouse()+"'");
						if( !Utils.isNull(pickStock.getIssueReqNo()).equals("")){
						sql.append("\n 		 and h.issue_req_no ='"+pickStock.getIssueReqNo()+"'");
						}
						sql.append("\n 		 group by group_code,pens_item,material_master,barcode  ");
						sql.append("\n 	 )P ");
						sql.append("\n 	 ON  M.pens_item = P.pens_item and M.group_code =P.group_code "  +
								" AND M.material_master = P.material_master AND M.barcode =P.barcode   ");
				sql.append("\n   	  )U ");
			    sql.append("\n 		  order by U.group_code,U.pens_item,U.material_master asc");
				  
				sql.append("\n      )A  ");
				if( !allRec){
				  sql.append("\n     WHERE rownum < (("+pageNumber+" * "+PickConstants.CONF_PICK_PAGE_SIZE+") + 1 )  ");
			    }
				sql.append("\n   )M  ");
				if( !allRec){
				   sql.append("\n  WHERE r__ >= ((("+pageNumber+"-1) * "+PickConstants.CONF_PICK_PAGE_SIZE+") + 1)  ");
				}
				sql.append("\n )M2  ");
				
	
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   ReqPickStock h = new ReqPickStock();
			   h.setIssueReqNo(pickStock.getIssueReqNo());
			   h.setGroupCode(rst.getString("group_code"));
			   h.setPensItem(rst.getString("pens_item"));
			   h.setMaterialMaster(rst.getString("material_master"));
			   h.setBarcode(rst.getString("barcode"));

			  // logger.debug("issue_qty["+rst.getInt("issue_qty")+"]");
			   h.setIssueQty(Utils.decimalFormat(rst.getInt("issue_qty"),Utils.format_current_no_disgit));
			   //logger.debug("issue_qty2["+h.getIssueQty()+"]");
			   
			   h.setQty(Utils.decimalFormat(rst.getInt("req_qty"),Utils.format_current_no_disgit));
			   
			   //display report
			   h.setIssueQtyInt(rst.getInt("issue_qty"));
			   h.setQtyInt(rst.getInt("req_qty"));
			   
			   int issueQty =  rst.getInt("issue_qty");//issue_qty
			   
			   totalQty +=issueQty;
			   
			   items.add(h);
			   r++;
			   
			}//while
			
			pickStock.setItems(items);
			pickStock.setTotalQty(totalQty);
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return pickStock;
	}
	
	public static ReqPickStock getStockIssueItemCase4ReportMini(Connection conn,ReqPickStock pickStock) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		List<ReqPickStock> items = new ArrayList<ReqPickStock>();

		int totalQty = 0;
		logger.debug("***getStockIssueItemCase4ReportMini***");
		try {
			//Case Edit
			sql.append("\n 	select group_code,count(*) as countGroup ,SUM(NVL(issue_qty,0)) as issue_qty  ");
			sql.append("\n 	from PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i  ");
			sql.append("\n 	where 1=1  ");
			sql.append("\n 	and h.issue_req_no = i.issue_req_no ");
			sql.append("\n 	and h.warehouse = '"+pickStock.getWareHouse()+"'");
			if( !Utils.isNull(pickStock.getIssueReqNo()).equals("")){
			sql.append("\n 	and h.issue_req_no ='"+pickStock.getIssueReqNo()+"'");
			}
			sql.append("\n 	group by group_code ");
			sql.append("\n 	order by group_code ");

			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			 
				items.addAll(genMatDetail4Report(conn,pickStock,Utils.isNull(rst.getString("group_code")),rst.getInt("countGroup"),rst.getInt("issue_qty")));
				
			   totalQty +=rst.getInt("issue_qty");
			}//while
			
			pickStock.setItems(items);
			pickStock.setTotalQty(totalQty);
			
			//debug
			if(items != null && items.size()>0){
				for(int r=0;r<items.size();r++){
					ReqPickStock p = items.get(r);
					logger.debug("groupCode["+p.getGroupCode()+"]mat["+p.getMaterialMaster()+"]");
				}
			}
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return pickStock;
	}
	
	public static ReqPickStock getStockIssueItemCase4ReportBillMini(Connection conn,ReqPickStock pickStock) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		List<ReqPickStock> items = new ArrayList<ReqPickStock>();

		int totalQty = 0;
		logger.debug("***getStockIssueItemCase4ReportBillMini***");
		try {
			//Case Edit
			sql.append("\n 	select group_code,count(*) as countGroup ,SUM(NVL(req_qty,0)) as req_qty  ");
			sql.append("\n 	from PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i  ");
			sql.append("\n 	where 1=1  ");
			sql.append("\n 	and h.issue_req_no = i.issue_req_no ");
			sql.append("\n 	and h.warehouse = '"+pickStock.getWareHouse()+"'");
			if( !Utils.isNull(pickStock.getIssueReqNo()).equals("")){
			sql.append("\n 	and h.issue_req_no ='"+pickStock.getIssueReqNo()+"'");
			}
			sql.append("\n 	group by group_code ");
			sql.append("\n 	order by group_code ");

			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			 
				items.addAll(genMatDetail4BillReport(conn,pickStock,Utils.isNull(rst.getString("group_code")),rst.getInt("countGroup"),rst.getInt("req_qty")));
				
			   totalQty +=rst.getInt("req_qty");
			}//while
			
			pickStock.setItems(items);
			pickStock.setTotalQty(totalQty);
			
			//debug
			if(items != null && items.size()>0){
				for(int r=0;r<items.size();r++){
					ReqPickStock p = items.get(r);
					logger.debug("groupCode["+p.getGroupCode()+"]mat["+p.getMaterialMaster()+"]");
				}
			}
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return pickStock;
	}
	
	public static ReqPickStock getStockIssueItemCase4ReportBillMiniAll(Connection conn,ReqPickStock pickStock,String issueReqNoAll) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		List<ReqPickStock> items = new ArrayList<ReqPickStock>();

		int totalQty = 0;
		logger.debug("***getStockIssueItemCase4ReportBillMini***");
		try {
			//Case Edit
			sql.append("\n 	select group_code,count(*) as countGroup ,SUM(NVL(req_qty,0)) as req_qty  ");
			sql.append("\n 	from PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i  ");
			sql.append("\n 	where 1=1  ");
			sql.append("\n 	and h.issue_req_no = i.issue_req_no ");
			sql.append("\n 	and h.issue_req_no in("+issueReqNoAll+")");
			
			sql.append("\n 	group by group_code ");
			sql.append("\n 	order by group_code ");

			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			 
				items.addAll(genMatDetail4BillAllReport(conn,issueReqNoAll,Utils.isNull(rst.getString("group_code")),rst.getInt("countGroup"),rst.getInt("req_qty")));
				
			   totalQty +=rst.getInt("req_qty");
			}//while
			
			pickStock.setItems(items);
			pickStock.setTotalQty(totalQty);
			
			//debug
			if(items != null && items.size()>0){
				for(int r=0;r<items.size();r++){
					ReqPickStock p = items.get(r);
					//logger.debug("groupCode["+p.getGroupCode()+"]mat["+p.getMaterialMaster()+"]");
				}
			}
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return pickStock;
	}
	public static List<ReqPickStock> genMatDetail4BillAllReport(Connection conn,String issueReqnoAll,String groupCode,int countGroup,int issueQty) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		List<ReqPickStock> matList = new ArrayList<ReqPickStock>();
		int rowMax = 9;
		int count = 0;
		String mat = "";
		String rowMat = "";
		int r = 0;
		int sumInt = 0;
		logger.debug("***getMatDetail4Report*** RowMax["+rowMax+"]");
		try {
			//Case Edit
			sql.append("\n 	select group_code,material_master,nvl(sum(req_qty),0) as issue_qty");
			sql.append("\n 	from PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i  ");
			sql.append("\n 	where 1=1  ");
			sql.append("\n 	and h.issue_req_no = i.issue_req_no ");
			sql.append("\n 	and group_code = '"+groupCode+"'");
			sql.append("\n 	and h.issue_req_no in ("+issueReqnoAll+")");
			
			sql.append("\n 	group by group_code,material_master ");
			sql.append("\n 	order by group_code,material_master ");

			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()) {
				count++;
				if(issueQty >0 && countGroup >= rowMax){
					logger.debug("Case 1 countGroup >= rowMax");
					if(count < rowMax){
						logger.debug("count["+count+"]["+rst.getString("material_master")+"] mat++");
						mat = rst.getString("material_master");
						if(mat.length()==10){
						   mat = mat.substring(6,10);
						}else if(mat.length()==10){
						  mat = mat.substring(6,9);
						}
						if(rst.getInt("issue_qty") > 0)
						   rowMat += mat+" "+rst.getInt("issue_qty")+" / ";
					
					}else{
						logger.debug("count["+count+"] ["+rst.getString("material_master")+"] mat++");
						mat = rst.getString("material_master");
						if(mat.length()==10){
						   mat = mat.substring(6,10);
						}else if(mat.length()==10){
						  mat = mat.substring(6,9);
						}
						if(rst.getInt("issue_qty") > 0)
						   rowMat += mat+" "+rst.getInt("issue_qty")+" / ";
						
						r++;
						logger.debug("count["+count+"] r["+r+"] add to list");
						ReqPickStock p = new ReqPickStock();
						if(r==1){
							p.setGroupCode(groupCode+" [ "+issueQty+" ]");
						}else{
							p.setGroupCode("");
						}
						p.setMaterialMaster(rowMat);
						matList.add(p);
						
						//reset
						rowMat = "";
						count = 0;
						
					}
				}else if(issueQty > 0 ){
					logger.debug("Case 2 countGroup < rowMax ["+rst.getString("material_master")+"]");
					mat = rst.getString("material_master");
					if(mat.length()==10){
					   mat = mat.substring(6,10);
					}else if(mat.length()==10){
					  mat = mat.substring(6,9);
					}
					
					if(rst.getInt("issue_qty") >0)
					  rowMat += mat+" "+rst.getInt("issue_qty")+" / ";
					
					if(count == countGroup){
						ReqPickStock p = new ReqPickStock();
						p.setGroupCode(groupCode+" [ "+issueQty+" ]");
						p.setMaterialMaster(rowMat);
						matList.add(p);
						
						//reset
						rowMat = "";
						count = 0;
					}
				}
				
			}//while
			
			/** Case split remain from Case 1 **/
			if( !rowMat.equals("")){
				r++;
				logger.debug("r["+r+"]rowMat["+rowMat+"] add remain to list");
				ReqPickStock p = new ReqPickStock();
				if(r==1){
					p.setGroupCode(groupCode+" [ "+issueQty+" ]");
				}else{
					p.setGroupCode("");
				}
				p.setMaterialMaster(rowMat);
				matList.add(p);
			}
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return matList;
	}
	
	public static List<ReqPickStock> genMatDetail4BillReport(Connection conn,ReqPickStock pickStock,String groupCode,int countGroup,int issueQty) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		List<ReqPickStock> matList = new ArrayList<ReqPickStock>();
		int rowMax = 10;
		int count = 0;
		String mat = "";
		String rowMat = "";
		int r = 0;
		int sumInt = 0;
		logger.debug("***getMatDetail4Report*** RowMax["+rowMax+"]");
		try {
			//Case Edit
			sql.append("\n 	select group_code,material_master,nvl(sum(req_qty),0) as issue_qty");
			sql.append("\n 	from PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i  ");
			sql.append("\n 	where 1=1  ");
			sql.append("\n 	and h.issue_req_no = i.issue_req_no ");
			sql.append("\n 	and h.warehouse = '"+pickStock.getWareHouse()+"'");
			sql.append("\n 	and group_code = '"+groupCode+"'");
			if( !Utils.isNull(pickStock.getIssueReqNo()).equals("")){
			sql.append("\n 	and h.issue_req_no ='"+pickStock.getIssueReqNo()+"'");
			}
			sql.append("\n 	group by group_code,material_master ");
			sql.append("\n 	order by group_code,material_master ");

			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()) {
				count++;
				if(issueQty >0 && countGroup >= rowMax){
					logger.debug("Case 1 countGroup >= rowMax");
					if(count < rowMax){
						logger.debug("count["+count+"]["+rst.getString("material_master")+"] mat++");
						mat = rst.getString("material_master");
						mat = mat.substring(6,10);
						if(rst.getInt("issue_qty") > 0)
						   rowMat += mat+" "+rst.getInt("issue_qty")+" / ";
					
					}else{
						logger.debug("count["+count+"] ["+rst.getString("material_master")+"] mat++");
						mat = rst.getString("material_master");
						mat = mat.substring(6,10);
						if(rst.getInt("issue_qty") > 0)
						   rowMat += mat+" "+rst.getInt("issue_qty")+" / ";
						
						r++;
						logger.debug("count["+count+"] r["+r+"] add to list");
						ReqPickStock p = new ReqPickStock();
						if(r==1){
							p.setGroupCode(groupCode+" [ "+issueQty+" ]");
						}else{
							p.setGroupCode("");
						}
						p.setMaterialMaster(rowMat);
						matList.add(p);
						
						//reset
						rowMat = "";
						count = 0;
						
					}
				}else if(issueQty > 0 ){
					logger.debug("Case 2 countGroup < rowMax ["+rst.getString("material_master")+"]");
					mat = rst.getString("material_master");
					mat = mat.substring(6,10);
					if(rst.getInt("issue_qty") >0)
					  rowMat += mat+" "+rst.getInt("issue_qty")+" / ";
					
					if(count == countGroup){
						ReqPickStock p = new ReqPickStock();
						p.setGroupCode(groupCode+" [ "+issueQty+" ]");
						p.setMaterialMaster(rowMat);
						matList.add(p);
						
						//reset
						rowMat = "";
						count = 0;
					}
				}
				
			}//while
			
			/** Case split remain from Case 1 **/
			if( !rowMat.equals("")){
				r++;
				logger.debug("r["+r+"]rowMat["+rowMat+"] add remain to list");
				ReqPickStock p = new ReqPickStock();
				if(r==1){
					p.setGroupCode(groupCode+" [ "+issueQty+" ]");
				}else{
					p.setGroupCode("");
				}
				p.setMaterialMaster(rowMat);
				matList.add(p);
			}
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return matList;
	}
	
	public static List<ReqPickStock> genMatDetail4Report(Connection conn,ReqPickStock pickStock,String groupCode,int countGroup,int issueQty) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		List<ReqPickStock> matList = new ArrayList<ReqPickStock>();
		int rowMax = 10;
		int count = 0;
		String mat = "";
		String rowMat = "";
		int r = 0;
		int sumInt = 0;
		logger.debug("***getMatDetail4Report*** RowMax["+rowMax+"]");
		try {
			//Case Edit
			sql.append("\n 	select group_code,material_master,nvl(sum(issue_qty),0) as issue_qty");
			sql.append("\n 	from PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i  ");
			sql.append("\n 	where 1=1  ");
			sql.append("\n 	and h.issue_req_no = i.issue_req_no ");
			sql.append("\n 	and h.warehouse = '"+pickStock.getWareHouse()+"'");
			sql.append("\n 	and group_code = '"+groupCode+"'");
			if( !Utils.isNull(pickStock.getIssueReqNo()).equals("")){
			sql.append("\n 	and h.issue_req_no ='"+pickStock.getIssueReqNo()+"'");
			}
			sql.append("\n 	group by group_code,material_master ");
			sql.append("\n 	order by group_code,material_master ");

			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()) {
				count++;
				if(issueQty >0 && countGroup >= rowMax){
					logger.debug("Case 1 countGroup >= rowMax");
					if(count < rowMax){
						logger.debug("count["+count+"]["+rst.getString("material_master")+"] mat++");
						mat = rst.getString("material_master");
						mat = mat.substring(6,10);
						if(rst.getInt("issue_qty") > 0)
						   rowMat += mat+" "+rst.getInt("issue_qty")+" / ";
					
					}else{
						logger.debug("count["+count+"] ["+rst.getString("material_master")+"] mat++");
						mat = rst.getString("material_master");
						mat = mat.substring(6,10);
						if(rst.getInt("issue_qty") > 0)
						   rowMat += mat+" "+rst.getInt("issue_qty")+" / ";
						
						r++;
						logger.debug("count["+count+"] r["+r+"] add to list");
						ReqPickStock p = new ReqPickStock();
						if(r==1){
							p.setGroupCode(groupCode+" [ "+issueQty+" ]");
						}else{
							p.setGroupCode("");
						}
						p.setMaterialMaster(rowMat);
						matList.add(p);
						
						//reset
						rowMat = "";
						count = 0;
						
					}
				}else if(issueQty > 0 ){
					logger.debug("Case 2 countGroup < rowMax ["+rst.getString("material_master")+"]");
					mat = rst.getString("material_master");
					mat = mat.substring(6,10);
					if(rst.getInt("issue_qty") >0)
					  rowMat += mat+" "+rst.getInt("issue_qty")+" / ";
					
					if(count == countGroup){
						ReqPickStock p = new ReqPickStock();
						p.setGroupCode(groupCode+" [ "+issueQty+" ]");
						p.setMaterialMaster(rowMat);
						matList.add(p);
						
						//reset
						rowMat = "";
						count = 0;
					}
				}
				
			}//while
			
			/** Case split remain from Case 1 **/
			if( !rowMat.equals("")){
				r++;
				logger.debug("r["+r+"]rowMat["+rowMat+"] add remain to list");
				ReqPickStock p = new ReqPickStock();
				if(r==1){
					p.setGroupCode(groupCode+" [ "+issueQty+" ]");
				}else{
					p.setGroupCode("");
				}
				p.setMaterialMaster(rowMat);
				matList.add(p);
			}
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return matList;
	}
	
	public static ReqPickStock getStockIssueItemCaseEdit(Connection conn,ReqPickStock pickStock,int pageNumber,boolean allRec) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		List<ReqPickStock> items = new ArrayList<ReqPickStock>();
		int r = 1;
		int totalQty = 0;
		logger.debug("***getGroupCodeInStockListCaseNoEdit***");
		try {
				//Case Edit
				sql.append("\n SELECT  M2.* FROM(");//M2
				sql.append("\n   SELECT  M.*  FROM("); //M
				sql.append("\n     SELECT A.*, rownum r__ FROM ( ");//A
				sql.append("\n        SELECT  U.*  FROM( ");//U
				
		                sql.append("\n 	SELECT M.* ,NVL(C.issue_qty,0) as issue_qty,NVL(P.req_qty,0) as req_qty  FROM ( ");
						sql.append("\n 		select group_code,pens_item,material_master,barcode  ");
						sql.append("\n 		from PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i  ");
						sql.append("\n 		where 1=1  ");
						sql.append("\n 		and h.issue_req_no = i.issue_req_no ");
						sql.append("\n 		and h.warehouse = '"+pickStock.getWareHouse()+"'");
						if( !Utils.isNull(pickStock.getIssueReqNo()).equals("")){
						sql.append("\n 		and h.issue_req_no ='"+pickStock.getIssueReqNo()+"'");
						}
						sql.append("\n 		group by i.BARCODE, MATERIAL_MASTER,group_code,pens_item ");

						sql.append("\n   ) M LEFT OUTER JOIN  ");
						
						sql.append("\n 	 ( select group_code,pens_item,material_master,barcode ,NVL(SUM(i.issue_qty),0) as issue_qty ,NVL(SUM(i.req_qty),0) as req_qty ");
						sql.append("\n 		 from PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i  ");
						sql.append("\n 		 where 1=1  ");
						sql.append("\n		 and h.issue_req_no = i.issue_req_no ");
						sql.append("\n 		 and h.warehouse = '"+pickStock.getWareHouse()+"'");
						if( !Utils.isNull(pickStock.getIssueReqNo()).equals("")){
						sql.append("\n 		 and h.issue_req_no ='"+pickStock.getIssueReqNo()+"'");
						}
						sql.append("\n 		 group by group_code,pens_item,material_master,barcode  ");
						sql.append("\n 	 )P ");
						sql.append("\n 	 ON  M.pens_item = P.pens_item and M.group_code =P.group_code "  +
								"       AND M.material_master = P.material_master AND M.barcode =P.barcode   ");
						
						sql.append("\n   LEFT OUTER JOIN  ");
						/** get qty from scan checkout ***/
						sql.append("\n 	 ( select group_code,pens_item,material_master,barcode ,NVL(COUNT(*),0) as issue_qty  ");
						sql.append("\n 		 from PENSBME_SCAN_CHECKOUT h, PENSBME_SCAN_CHECKOUT_ITEM i  ");
						sql.append("\n 		 where 1=1  ");
						sql.append("\n		 and h.issue_req_no = i.issue_req_no ");
						sql.append("\n 		 and h.warehouse = '"+pickStock.getWareHouse()+"'");
						if( !Utils.isNull(pickStock.getIssueReqNo()).equals("")){
						sql.append("\n 		 and h.issue_req_no ='"+pickStock.getIssueReqNo()+"'");
						}
						sql.append("\n 		 group by group_code,pens_item,material_master,barcode  ");
						sql.append("\n 	 )C ");
						sql.append("\n 	 ON  M.pens_item = C.pens_item and M.group_code =C.group_code "  +
								"       AND M.material_master = C.material_master AND M.barcode = C.barcode   ");
				
				sql.append("\n   	  )U ");
			    sql.append("\n 		  order by U.group_code,U.pens_item ,U.material_master asc");
				  
				sql.append("\n      )A  ");
				if( !allRec){
				  sql.append("\n     WHERE rownum < (("+pageNumber+" * "+PickConstants.CONF_PICK_PAGE_SIZE+") + 1 )  ");
			    }
				sql.append("\n   )M  ");
				if( !allRec){
				   sql.append("\n  WHERE r__ >= ((("+pageNumber+"-1) * "+PickConstants.CONF_PICK_PAGE_SIZE+") + 1)  ");
				}
				sql.append("\n )M2  ");
				
	
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   ReqPickStock h = new ReqPickStock();
			   h.setIssueReqNo(pickStock.getIssueReqNo());
			   h.setGroupCode(rst.getString("group_code"));
			   h.setPensItem(rst.getString("pens_item"));
			   h.setMaterialMaster(rst.getString("material_master"));
			   h.setBarcode(rst.getString("barcode"));
			   int reqQty =  rst.getInt("req_qty");
			   int issueQty =  rst.getInt("issue_qty");
			   
			   //Old code
			   //Case First access set issue_qty = qty
			   //Case status !=B can be set
			   /*if( !PickConstants.STATUS_BEF.equals(pickStock.getStatus())){
				   if(issueQty==0){
					   issueQty = reqQty;
				   }
			   }*/
			   //new code get from scan checkout
			   h.setQty(Utils.decimalFormat(reqQty,Utils.format_current_no_disgit));
			   if(issueQty != 0)
			      h.setIssueQty(Utils.decimalFormat(issueQty,Utils.format_current_no_disgit));
			   
			   totalQty +=issueQty;
			   
			   items.add(h);
			   r++;
			   
			}//while
			
			pickStock.setItems(items);
			pickStock.setTotalQty(totalQty);
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return pickStock;
	}
	
	public static ReqPickStock getStockIssueItemCaseEditPageAll(Connection conn,ReqPickStock pickStock,int pageNumber,boolean allRec) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		List<ReqPickStock> items = new ArrayList<ReqPickStock>();
		int r = 1;
		int totalQty = 0;
		logger.debug("***getGroupCodeInStockListCaseNoEdit***");
		try {
				//Case Edit
				sql.append("\n SELECT  M2.* FROM(");//M2
				sql.append("\n   SELECT  M.*  FROM("); //M
				sql.append("\n     SELECT A.*, rownum r__ FROM ( ");//A
				sql.append("\n        SELECT  U.*  FROM( ");//U
				
		                sql.append("\n 	SELECT M.* ,NVL(P.req_qty,0) as issue_qty,NVL(P.req_qty,0) as req_qty  FROM ( ");
						sql.append("\n 		select group_code,pens_item,material_master,barcode  ");
						sql.append("\n 		from PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i  ");
						sql.append("\n 		where 1=1  ");
						sql.append("\n 		and h.issue_req_no = i.issue_req_no ");
						sql.append("\n 		and h.warehouse = '"+pickStock.getWareHouse()+"'");
						if( !Utils.isNull(pickStock.getIssueReqNo()).equals("")){
						sql.append("\n 		and h.issue_req_no ='"+pickStock.getIssueReqNo()+"'");
						}
						sql.append("\n 		group by i.BARCODE, MATERIAL_MASTER,group_code,pens_item ");

						sql.append("\n   ) M LEFT OUTER JOIN  ");
						
						sql.append("\n 	 ( select group_code,pens_item,material_master,barcode ,NVL(SUM(i.issue_qty),0) as issue_qty ,NVL(SUM(i.req_qty),0) as req_qty ");
						sql.append("\n 		 from PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i  ");
						sql.append("\n 		 where 1=1  ");
						sql.append("\n		 and h.issue_req_no = i.issue_req_no ");
						sql.append("\n 		 and h.warehouse = '"+pickStock.getWareHouse()+"'");
						if( !Utils.isNull(pickStock.getIssueReqNo()).equals("")){
						sql.append("\n 		 and h.issue_req_no ='"+pickStock.getIssueReqNo()+"'");
						}
						sql.append("\n 		 group by group_code,pens_item,material_master,barcode  ");
						sql.append("\n 	 )P ");
						sql.append("\n 	 ON  M.pens_item = P.pens_item and M.group_code =P.group_code "  +
								"       AND M.material_master = P.material_master AND M.barcode =P.barcode   ");
				
				sql.append("\n   	  )U ");
			    sql.append("\n 		  order by U.group_code,U.pens_item ,U.material_master asc");
				  
				sql.append("\n      )A  ");
				if( !allRec){
				  sql.append("\n     WHERE rownum < (("+pageNumber+" * "+PickConstants.CONF_PICK_PAGE_SIZE+") + 1 )  ");
			    }
				sql.append("\n   )M  ");
				if( !allRec){
				   sql.append("\n  WHERE r__ >= ((("+pageNumber+"-1) * "+PickConstants.CONF_PICK_PAGE_SIZE+") + 1)  ");
				}
				sql.append("\n )M2  ");
				
	
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   ReqPickStock h = new ReqPickStock();
			   h.setIssueReqNo(pickStock.getIssueReqNo());
			   h.setGroupCode(rst.getString("group_code"));
			   h.setPensItem(rst.getString("pens_item"));
			   h.setMaterialMaster(rst.getString("material_master"));
			   h.setBarcode(rst.getString("barcode"));
			   int reqQty =  rst.getInt("req_qty");
			   int issueQty =  rst.getInt("issue_qty");
			   
			   if(issueQty==0){
				   issueQty = reqQty;
			   }
			 
			   //new code get from scan checkout
			   h.setQty(Utils.decimalFormat(reqQty,Utils.format_current_no_disgit));
			   if(issueQty != 0)
			      h.setIssueQty(Utils.decimalFormat(issueQty,Utils.format_current_no_disgit));
			   
			   totalQty +=issueQty;
			   
			   items.add(h);
			   r++;
			   
			}//while
			
			pickStock.setItems(items);
			pickStock.setTotalQty(totalQty);
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return pickStock;
	}
	
	public static int getTotalQtyInReqPickStock(Connection conn,ReqPickStock pickStock ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int totalRecord = 0;
		try {
			sql.append("\n select count(*) as qty   ");
			sql.append("\n from PENSBME_PICK_STOCK h,  PENSBME_PICK_STOCK_I i  ");
			sql.append("\n where 1=1 and h.pick_type ='ITEM'  ");
			sql.append("\n and h.issue_req_no = i.issue_req_no ");
			if( !Utils.isNull(pickStock.getIssueReqNo()).equals("")){
			   sql.append("\n and h.issue_req_no ='"+pickStock.getIssueReqNo()+"'");
			}
			sql.append("\n group by h.issue_req_no");
				
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			if(rst.next()) {
			   totalRecord = rst.getInt("qty");
			}//while

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return totalRecord;
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
	
	
	public static void updateStausStockIssue(Connection conn,ReqPickStock o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("updateHeadModel");
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" UPDATE PENSBI.PENSBME_STOCK_ISSUE \n");
			sql.append(" SET  STATUS=?,STATUS_DATE=? ,UPDATE_DATE =?,UPDATE_USER =? \n");
		    sql.append(" WHERE ISSUE_REQ_NO = ? \n");
			
			ps = conn.prepareStatement(sql.toString());
			
			int c =1;
			
			ps.setString(c++, o.getStatus());
			ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
			ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(c++, o.getUpdateUser());
			ps.setString(c++, o.getIssueReqNo());
			
			ps.executeUpdate();
			
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}
	
	public static void confirmStausStockIssue(Connection conn,ReqPickStock o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("confirmStausStockIssue");
		Date deliveryDate = null;
		try{
			deliveryDate = Utils.parse(o.getDeliveryDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th); 
					
			StringBuffer sql = new StringBuffer("");
			sql.append(" UPDATE PENSBI.PENSBME_STOCK_ISSUE \n");
			sql.append(" SET  STATUS=?,STATUS_DATE=? ,UPDATE_DATE =?,UPDATE_USER =? ,DELIVERY_DATE = ?,TOTAL_CTN = ? \n");
		    sql.append(" WHERE ISSUE_REQ_NO = ? \n");
			
			ps = conn.prepareStatement(sql.toString());
			
			int c =1;
			
			ps.setString(c++, o.getStatus());
			ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
			ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(c++, o.getUpdateUser());
			ps.setTimestamp(c++, new java.sql.Timestamp(deliveryDate.getTime()));
			ps.setInt(c++, o.getTotalCtn());
			ps.setString(c++, o.getIssueReqNo());
			
			ps.executeUpdate();
			
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}
	
	public static void confirmDeliveryStockIssue(Connection conn,ReqPickStock o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("confirmDeliveryStockIssue");
		Date deliveryDate = null;
		try{
			deliveryDate = Utils.parse(o.getDeliveryDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th); 
					
			StringBuffer sql = new StringBuffer("");
			sql.append(" UPDATE PENSBI.PENSBME_STOCK_ISSUE \n");
			sql.append(" SET  UPDATE_DATE =?,UPDATE_USER =? ,DELIVERY_DATE = ?,TOTAL_CTN = ? \n");
		    sql.append(" WHERE ISSUE_REQ_NO = ? \n");
			
			ps = conn.prepareStatement(sql.toString());
			
			int c =1;
			
			ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(c++, o.getUpdateUser());
			ps.setTimestamp(c++, new java.sql.Timestamp(deliveryDate.getTime()));
			ps.setInt(c++, o.getTotalCtn());
			
			ps.setString(c++, o.getIssueReqNo());
			
			ps.executeUpdate();
			
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}
	
	public static void updateStatusStockIssueItemByIssueReqNo(Connection conn,ReqPickStock o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("updateHeadModel");
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" UPDATE PENSBI.PENSBME_STOCK_ISSUE_ITEM \n");
			sql.append(" SET  STATUS=?,STATUS_DATE=? ,UPDATE_DATE =?,UPDATE_USER =?\n");
		    sql.append(" WHERE ISSUE_REQ_NO = ? \n");
			ps = conn.prepareStatement(sql.toString());
			
			int c =1;
			
			ps.setString(c++, o.getStatus());
			ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
			ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(c++, o.getUpdateUser());
			ps.setString(c++, o.getIssueReqNo());
	
			ps.executeUpdate();
			
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}
	
	public static void updateStockIssueItemByPK(Connection conn,ReqPickStock o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("updateHeadModel");
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" UPDATE PENSBI.PENSBME_STOCK_ISSUE_ITEM \n");
			sql.append(" SET  STATUS=?,STATUS_DATE=? ,UPDATE_DATE =?,UPDATE_USER =?,REQ_QTY =?,ISSUE_QTY =? \n");
		    sql.append(" WHERE ISSUE_REQ_NO = ? \n");
		    sql.append(" AND GROUP_CODE = ? \n");
		    sql.append(" AND PENS_ITEM = ? \n");
		    sql.append(" AND MATERIAL_MASTER = ? \n");
		    sql.append(" AND BARCODE = ? \n");
			ps = conn.prepareStatement(sql.toString());
			
			int c =1;
			
			ps.setString(c++, o.getStatus());
			ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
			ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(c++, o.getUpdateUser());
			ps.setInt(c++, Utils.convertStrToInt(o.getQty()));
			ps.setInt(c++, Utils.convertStrToInt(o.getIssueQty()));
			
			ps.setString(c++, o.getIssueReqNo());
			ps.setString(c++, o.getGroupCode());
			ps.setString(c++, o.getPensItem());
			ps.setString(c++, o.getMaterialMaster());
			ps.setString(c++, o.getBarcode());
			ps.executeUpdate();
			
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}
	
	public static void updateStatusStockIssueItemByPK(Connection conn,ReqPickStock o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("updateHeadModel");
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" UPDATE PENSBI.PENSBME_STOCK_ISSUE_ITEM \n");
			sql.append(" SET  STATUS=?,STATUS_DATE=? ,UPDATE_DATE =?,UPDATE_USER =? \n");
		    sql.append(" WHERE ISSUE_REQ_NO = ? \n");
		    sql.append(" AND GROUP_CODE = ? \n");
		    sql.append(" AND PENS_ITEM = ? \n");
		    sql.append(" AND MATERIAL_MASTER = ? \n");
		    sql.append(" AND BARCODE = ? \n");
			ps = conn.prepareStatement(sql.toString());
			
			int c =1;
			
			ps.setString(c++, o.getStatus());
			ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
			ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(c++, o.getUpdateUser());
			
			ps.setString(c++, o.getIssueReqNo());
			ps.setString(c++, o.getGroupCode());
			ps.setString(c++, o.getPensItem());
			ps.setString(c++, o.getMaterialMaster());
			ps.setString(c++, o.getBarcode());
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
