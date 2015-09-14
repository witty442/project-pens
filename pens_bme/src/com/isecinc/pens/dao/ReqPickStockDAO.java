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
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.ReqPickStock;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.process.SequenceProcess;

public class ReqPickStockDAO extends PickConstants{
	
	protected static Logger logger = Logger.getLogger("PENS");
	protected static SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Utils.local_th);

	public static ReqPickStock save(Connection conn,ReqPickStock h) throws Exception{
		h.setResultProcess(true);//default
		try{
			logger.debug("h.getIssueReqDate():"+h.getIssueReqDate());
			Date tDate  = Utils.parse(h.getIssueReqDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);

			//check documentNo
			if(Utils.isNull(h.getIssueReqNo()).equals("")){
				h.setActionDB("FIRST_SAVE");
				
				//Gen issueReqNo
				h.setIssueReqNo(genIssueReqNo(tDate));
				h.setStatus(STATUS_OPEN);
				
				logger.debug("New IssueReqNo:"+h.getIssueReqNo());
				
				//save head
				saveReqPickStockHeadModel(conn, h);
				
				//save line
				if(h.getItems() != null && h.getItems().size() >0){
				    for(int i=0;i<h.getItems().size();i++){
				       ReqPickStock p = h.getItems().get(i);
					   p.setIssueReqNo(h.getIssueReqNo());
					   p.setStatus(h.getStatus());
					   p.setCreateUser(h.getCreateUser());
					   p.setUpdateUser(h.getUpdateUser());

					   //save pick_stock item
					   if(Utils.convertStrToInt(p.getQty()) > 0){
					      saveStockIssueItemeModel(conn, p);
					   }
				    }
				}

			}else{
				//Edit
				logger.debug("Update IssueReqNo:"+h.getIssueReqNo());
			
				//delete pickStockItem by reqno,groupCode
				//deleteStockIssueItemByIssueReqNoAndGroup(conn,h);
				
				//save line
				if(h.getItems() != null && h.getItems().size() >0){
				    for(int i=0;i<h.getItems().size();i++){
				       ReqPickStock p = h.getItems().get(i);
					   p.setIssueReqNo(h.getIssueReqNo());
					   p.setStatus(h.getStatus());
					   p.setCreateUser(h.getCreateUser());
					   p.setUpdateUser(h.getUpdateUser());

					   //check is Exist
					   boolean isExist = isItemExist(conn,p);
					   if(isExist){
						   if( Utils.convertStrToInt(p.getQty())==0){
							   deleteStockIssueItemeModel(conn, p);
						   }else{
						       updateStockIssueItemeModel(conn, p);
						   }
					   }else{
						   //save pick_stock item
						   if(Utils.convertStrToInt(p.getQty()) > 0){
						      saveStockIssueItemeModel(conn, p);
						   }
					   }
				    }
				}
			}
			return h;
		}catch(Exception e){
		  throw e;
		}finally{
			
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
			sql.append("\n SELECT H.* ");
			
			sql.append("\n  ,(select NVL(SUM(req_qty),0) from PENSBI.PENSBME_STOCK_ISSUE_ITEM i ");
			sql.append("\n   where  status <>'"+STATUS_CANCEL+"'" );
			sql.append("\n   and H.issue_req_no = i.issue_req_no group by i.issue_req_no) as total_req_qty \n");
			
			sql.append("\n  ,(select M.pens_desc from pensbi.PENSBME_MST_REFERENCE M ");
			sql.append("\n   where M.reference_code = 'Store' and M.pens_value = H.customer_no) as STORE_NAME ");
			
			sql.append("\n from PENSBME_STOCK_ISSUE H \n");
			sql.append("\n where 1=1   \n");
			sql.append("\n and warehouse ='"+o.getWareHouse()+"' \n");
			
			if( !Utils.isNull(o.getIssueReqNo()).equals("")){
				sql.append("\n and H.issue_req_no = '"+Utils.isNull(o.getIssueReqNo())+"'  ");
			}
			
			if( !Utils.isNull(o.getStatus()).equals("")){
				sql.append("\n and H.status = '"+Utils.isNull(o.getStatus())+"'  ");
			}
			
			if( !Utils.isNull(o.getIssueReqDate()).equals("")){
				Date tDate  = Utils.parse(o.getIssueReqDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				String returnDateStr = Utils.stringValue(tDate, Utils.DD_MM_YYYY_WITH_SLASH);
				
				sql.append("\n and H.ISSUE_REQ_DATE = to_date('"+returnDateStr+"','dd/mm/yyyy') ");
			}
			
			sql.append("\n order by H.issue_req_no DESC ");
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
			   h.setTotalReqQty(rst.getInt("total_req_qty"));
			   
			   h.setStoreCode(Utils.isNull(rst.getString("customer_no")));
			   h.setStoreName(Utils.isNull(rst.getString("store_name")));
			   
			   if(Utils.isNull(rst.getString("status")).equals(STATUS_OPEN)){
				   h.setCanEdit(true);
			   }else{
				   h.setCanEdit(false); 
			   }
			   
			   if(Utils.isNull(rst.getString("status")).equals(STATUS_OPEN) ){
				   h.setCanConfirm(true);
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
	
	public static boolean isItemExist(Connection conn,ReqPickStock o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		boolean found = false;
		try {

			sql.append("\n select i.* from PENSBI.PENSBME_STOCK_ISSUE_ITEM i   \n");
			sql.append("\n where 1=1  \n");
			if( !Utils.isNull(o.getIssueReqNo()).equals("")){
				sql.append("\n and i.issue_req_no = '"+Utils.isNull(o.getIssueReqNo())+"'");
			}
			if( !Utils.isNull(o.getGroupCode()).equals("")){
				sql.append("\n and i.group_code = '"+Utils.isNull(o.getGroupCode())+"'");
			}
			if( !Utils.isNull(o.getMaterialMaster()).equals("")){
				sql.append("\n and i.material_master = '"+Utils.isNull(o.getMaterialMaster())+"'");
			}
			if( !Utils.isNull(o.getPensItem()).equals("")){
				sql.append("\n and i.pens_item = '"+Utils.isNull(o.getPensItem())+"'");
			}
			if( !Utils.isNull(o.getBarcode()).equals("")){
				sql.append("\n and i.barcode = '"+Utils.isNull(o.getBarcode())+"'");
			}
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			if(rst.next()) {
			  found = true;
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
	
	public static int getTotalRowInStockIssueItemCaseNoEditByGroupCode(Connection conn,ReqPickStock p) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int totalRecord = 0;
		try {
			// substract from Stock issue status = O(Open)
			sql.append("\n  select count(*) as total_row FROM( ");
			sql.append("\n 	  SELECT distinct group_code ,pens_item  ");
			sql.append("\n 	  FROM PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i  ");
			sql.append("\n 	  WHERE 1=1  ");
			sql.append("\n 	  AND h.warehouse ='"+p.getWareHouse()+"'");
			sql.append("\n 	  AND h.issue_req_no = i.issue_req_no ");
			sql.append("\n 	  AND h.issue_req_no ='"+p.getIssueReqNo()+"'");
			if( !Utils.isNull(p.getGroupCode()).equals("")){
				sql.append("\n 	AND i.group_code LIKE '%"+p.getGroupCode()+"%'");
			}
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
	
	public static int getTotalRowInStockIssueItemCaseNoEditByItem(Connection conn,ReqPickStock p) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int totalRecord = 0;
		try {
			// substract from Stock issue status = O(Open)
			sql.append("\n  select count(*) as total_row FROM( ");
			sql.append("\n 	  SELECT distinct group_code ,pens_item,material_master,barcode  ");
			sql.append("\n 	  FROM PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i  ");
			sql.append("\n 	  WHERE 1=1  ");
			sql.append("\n 	  AND h.warehouse ='"+p.getWareHouse()+"'");
			sql.append("\n 	  AND h.issue_req_no = i.issue_req_no ");
			sql.append("\n 	  AND h.issue_req_no ='"+p.getIssueReqNo()+"'");
			if( !Utils.isNull(p.getGroupCode()).equals("")){
				sql.append("\n 	AND i.group_code LIKE '%"+p.getGroupCode()+"%'");
			}
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
				sql.append("\n    SELECT M2.group_code,M2.pens_item FROM( ");
				sql.append("\n       SELECT group_code,pens_item, sum(onhand_qty) as onhand_qty FROM( ");
				sql.append("\n         SELECT group_code,pens_item ,(nvl(sum(onhand_qty),0)-nvl(sum(issue_qty),0)) as onhand_qty ");
				sql.append("\n  	   from PENSBME_STOCK_FINISHED WHERE 1=1 ");
				
				if( !Utils.isNull(p.getGroupCode()).equals("")){
					sql.append("\n 		AND group_code LIKE '%"+p.getGroupCode()+"%'");
				}
				sql.append("\n 	       AND warehouse ='"+p.getWareHouse()+"'");
				sql.append("\n  	   GROUP BY group_code,pens_item ");
				sql.append("\n         UNION ALL ");
				sql.append("\n 		   SELECT group_code ,pens_item,( (-1) * sum(req_qty) )as onhand_qty");
				sql.append("\n 		   FROM PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i  ");
				sql.append("\n 		   WHERE 1=1  ");
				sql.append("\n 	       AND h.warehouse ='"+p.getWareHouse()+"'");
				sql.append("\n 		   AND h.issue_req_no = i.issue_req_no ");
				sql.append("\n 		   AND h.issue_req_no <> '"+p.getIssueReqNo()+"'");
				sql.append("\n 		   AND h.status in('"+STATUS_OPEN+"','"+STATUS_POST+"')");
				if( !Utils.isNull(p.getGroupCode()).equals("")){
					sql.append("\n 		   AND i.group_code LIKE '%"+p.getGroupCode()+"%'");
				}
				sql.append("\n  	   GROUP BY group_code,pens_item ");
				sql.append("\n       ) GROUP BY group_code ,pens_item ");
				sql.append("\n    )M2 WHERE onhand_qty > 0 ");
				
				sql.append("\n    UNION  ");
				 // substract from Stock issue status = O(Open)
				sql.append("\n 	  SELECT distinct group_code ,pens_item ");
				sql.append("\n 	  FROM PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i  ");
				sql.append("\n 	  WHERE 1=1  ");
				sql.append("\n 	  AND h.warehouse ='"+p.getWareHouse()+"'");
				sql.append("\n 	  AND h.issue_req_no = i.issue_req_no ");
				sql.append("\n 	  AND h.issue_req_no ='"+p.getIssueReqNo()+"'");
				if( !Utils.isNull(p.getGroupCode()).equals("")){
					sql.append("\n 		   AND i.group_code LIKE '%"+p.getGroupCode()+"%'");
				}
				sql.append("\n   )");
				
			}else{
				//Case Add new check onhand_qty > 0
				sql.append("\n SELECT count(*) as total_row from( ");
				sql.append("\n  SELECT M2.* from( ");
				sql.append("\n    SELECT M.group_code,M.pens_item,NVL(SUM(onhand_qty),0) as onhand_qty,0 as qty FROM ( ");
				sql.append("\n  	 select group_code,pens_item,(nvl(onhand_qty,0)-nvl(issue_qty,0)) as onhand_qty ");
				sql.append("\n  	 from PENSBME_STOCK_FINISHED ");
				sql.append("\n  	 where 1=1   ");
				if( !Utils.isNull(p.getGroupCode()).equals("")){
					sql.append("\n 		   AND group_code LIKE '%"+p.getGroupCode()+"%'");
				}
				sql.append("\n 	     AND warehouse ='"+p.getWareHouse()+"'");
				sql.append("\n       UNION ALL ");
				 // substract from Stock issue status = O(Open)
				sql.append("\n 		 SELECT group_code,pens_item  ,(-1* req_qty ) as onhand_qty ");
				sql.append("\n 		 FROM PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i  ");
				sql.append("\n 		 WHERE 1=1  ");
				sql.append("\n 	     AND h.warehouse ='"+p.getWareHouse()+"'");
				sql.append("\n 		 AND h.issue_req_no = i.issue_req_no ");
				sql.append("\n 		 AND h.status in('"+STATUS_OPEN+"','"+STATUS_POST+"')");
				if( !Utils.isNull(p.getGroupCode()).equals("")){
					sql.append("\n 		AND i.group_code LIKE '%"+p.getGroupCode()+"%'");
				}
				sql.append("\n     )M  ");
				sql.append("\n    GROUP BY M.group_code,M.pens_item");
				sql.append("\n   )M2 WHERE M2.ONHAND_QTY > 0");
				sql.append("\n ) ");
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
	public static int getTotalQtyInStockIssueItem(Connection conn,ReqPickStock pickStock ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int totalRecord = 0;
		try {
			sql.append("\n select nvl(sum(req_qty),0) as req_qty   ");
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
			   totalRecord = rst.getInt("req_qty");
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
			sql.append("\n select i.barcode,i.MATERIAL_MASTER,i.group_code,i.pens_item ,req_qty ");
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
			   
			   h.setQty(Utils.decimalFormat(rst.getInt("req_qty"), Utils.format_current_no_disgit));
			   h.setQtyInt(rst.getInt("req_qty"));
			  	   
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
			sql.append("\n AND warehouse ='"+h.getWareHouse()+"'");
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
               
			   if(h.isModeEdit()){
				   //can edit
				   if( Utils.isNull(h.getStatus()).equals(STATUS_ISSUED)
				    || Utils.isNull(h.getStatus()).equals(STATUS_POST)
					|| Utils.isNull(h.getStatus()).equals(STATUS_CANCEL)){
					   h.setCanEdit(false);
				   }else{
					   h.setCanEdit(true);
				   }
				   
				   //can cancel
				   if( Utils.isNull(h.getStatus()).equals(STATUS_OPEN)){
					   h.setCanCancel(true);
				   }else{
					   h.setCanCancel(false); 
				   }
			   }else{
				   h.setCanEdit(false);
				   h.setCanCancel(false); 
				   h.setCanPrint(false); 
			   }
			   
			   //can confirm 
			   if(h.isModeConfirm()){
				   if( Utils.isNull(h.getStatus()).equals(STATUS_OPEN)){
					   h.setCanConfirm(true);
				   }else{
					   h.setCanConfirm(false); 
				   }
			   }else{
				   h.setCanConfirm(false);  
			   }
			   
			   //Can Print
			   if( Utils.isNull(h.getStatus()).equals(STATUS_POST)){
				   h.setCanPrint(true); 
			   }else{
				   h.setCanPrint(false); 
			   }
			   
			}//while
			
			if(h != null){
				if(getItems){
				  if( Utils.isNull(h.getStatus()).equals(STATUS_ISSUED)
				    ||  Utils.isNull(h.getStatus()).equals(STATUS_POST)
					|| Utils.isNull(h.getStatus()).equals(STATUS_CANCEL)){
					  
			             h = searchReqPickStockItemByPK(conn,h);
				  }else{
					     h = searchReqPickStockItemAll(conn,h); 
				  }
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
		return h;
	}
	
	public static ReqPickStock searchReqPickStockItemAll(Connection conn,ReqPickStock pickStock) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		List<ReqPickStock> items = new ArrayList<ReqPickStock>();
		int c = 1;
		int r = 1;
		int totalQty = 0;
		try {
			
			if( !pickStock.isNewReq()){
				sql.append("\n SELECT M.*,P.qty FROM ( ");
				sql.append("\n select BOX_NO,MATERIAL_MASTER,group_code,pens_item,qty as onhand_qty  ");
				sql.append("\n from PENSBME_PICK_ONHAND ");
				sql.append("\n where 1=1   ");
				sql.append("\n ) M LEFT OUTER JOIN  ");
				sql.append("\n (  ");
				sql.append("\n select  i.box_no,MATERIAL_MASTER,group_code,pens_item,count(*) as qty   ");
				sql.append("\n from PENSBME_PICK_STOCK h,  PENSBME_PICK_STOCK_I i  ");
				sql.append("\n where 1=1   ");
				sql.append("\n and h.issue_req_no = i.issue_req_no ");
				if( !Utils.isNull(pickStock.getIssueReqNo()).equals("")){
				   sql.append("\n and h.issue_req_no ='"+pickStock.getIssueReqNo()+"'");
				}
				sql.append("\n group by i.box_no, MATERIAL_MASTER,group_code,pens_item ");
				sql.append("\n )P ");
				sql.append("\n ON m.BOX_NO = p.BOX_NO and M.pens_item = P.pens_item  " +
						   "\n and M.MATERIAL_MASTER = p.MATERIAL_MASTER  and M.group_code =P.group_code  ");
				sql.append("\n order by m.box_no,M.MATERIAL_MASTER,M.group_code,M.pens_item ");
		    }else{
		    	sql.append("\n SELECT M.*,0 as qty FROM ( ");
				sql.append("\n select BOX_NO,MATERIAL_MASTER,group_code,pens_item,qty as onhand_qty  ");
				sql.append("\n from PENSBME_PICK_ONHAND ");
				sql.append("\n where 1=1   ");
				sql.append("\n )M  ");
				sql.append("\n order by m.box_no,M.MATERIAL_MASTER,M.group_code,M.pens_item ");
		    }
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			  
			   ReqPickStock h = new ReqPickStock();
			   h.setMaterialMaster(rst.getString("MATERIAL_MASTER"));
			   h.setGroupCode(rst.getString("group_code"));
			   h.setPensItem(rst.getString("pens_item"));
			   
			   //Case Edit 
			   int onhandQty = rst.getInt("onhand_qty");
			   int qty =  rst.getInt("qty");
			   
			   //Case cancel no add
			   if( !STATUS_CANCEL.equals(pickStock.getStatus())){
				   if(!pickStock.isNewReq()){
					  onhandQty +=qty;
				   }
			   }
			 //  h.setOnhandQty(Utils.decimalFormat(onhandQty,Utils.format_current_no_disgit));
			   
			   if(rst.getInt("qty") != 0){
			     h.setQty(Utils.decimalFormat(rst.getInt("qty"),Utils.format_current_no_disgit));
			   }
			     
			   items.add(h);
			   r++;
			   
			   //sum total qty
			   totalQty += rst.getInt("qty");
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
	
	public static ReqPickStock getGroupCodeInStockList(Connection conn,ReqPickStock pickStock,int pageNumber) throws Exception {
        return getGroupCodeInStockListModel(conn, pickStock, pageNumber, false);
	}
	
	public static ReqPickStock getGroupCodeInStockList(Connection conn,ReqPickStock pickStock,boolean allRec) throws Exception {
        return getGroupCodeInStockListModel(conn, pickStock, 0, allRec);
	}
	
	public static ReqPickStock getGroupCodeInStockListModel(Connection conn,ReqPickStock pickStock,int pageNumber,boolean allRec) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		List<ReqPickStock> items = new ArrayList<ReqPickStock>();
		int r = 1;
		int totalQty = 0;
		logger.debug("***getGroupCodeInStockList***");
		try {
			if( !pickStock.isNewReq()){
				//Case Edit
				sql.append("\n SELECT  M2.* FROM(");//M2
				sql.append("\n   SELECT  M.*  FROM("); //M
				sql.append("\n     SELECT A.*, rownum r__ FROM ( ");//A
				sql.append("\n        SELECT  U.*  FROM( ");//U
				
		                sql.append("\n 	SELECT M.* , NVL(P.qty,0) as qty , nvl(O.onhand_qty,0) as onhand_qty  FROM ( ");
						sql.append("\n 		select group_code,pens_item  ");
						sql.append("\n 		from PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i  ");
						sql.append("\n 		where 1=1  ");
						sql.append("\n 	    AND h.warehouse ='"+pickStock.getWareHouse()+"'");
						sql.append("\n 		and h.issue_req_no = i.issue_req_no ");
						if( !Utils.isNull(pickStock.getIssueReqNo()).equals("")){
						  sql.append("\n    and h.issue_req_no ='"+pickStock.getIssueReqNo()+"'");
						}
						if( !Utils.isNull(pickStock.getGroupCode()).equals("")){
						  sql.append("\n    and i.group_code LIKE '%"+pickStock.getGroupCode()+"%'");
						}
						sql.append("\n 		group by i.BARCODE, MATERIAL_MASTER,group_code,pens_item ");
			
						sql.append("\n 		UNION ");
						
						sql.append("\n 		SELECT DISTINCT OH2.group_code,OH2.pens_item FROM ( ");
						sql.append("\n 			SELECT OH.group_code,OH.pens_item FROM( ");   
						sql.append("\n 				SELECT  group_code,pens_item  ");
						sql.append("\n 				FROM PENSBME_STOCK_FINISHED ");  
						sql.append("\n 				WHERE 1=1 ");    
						sql.append("\n 	            AND warehouse ='"+pickStock.getWareHouse()+"'");
						if( !Utils.isNull(pickStock.getGroupCode()).equals("")){
							sql.append("\n 		    and group_code LIKE '%"+pickStock.getGroupCode()+"%'");
						}
						sql.append("\n              UNION ALL ");
						sql.append("\n 				SELECT group_code,pens_item");
						sql.append("\n 				FROM PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i  "); 
						sql.append("\n 				WHERE 1=1   ");
						sql.append("\n 	            AND h.warehouse ='"+pickStock.getWareHouse()+"'");
						sql.append("\n 				AND h.issue_req_no = i.issue_req_no  ");
						sql.append("\n 				AND h.status in('"+STATUS_OPEN+"','"+STATUS_POST+"')");
						if( !Utils.isNull(pickStock.getIssueReqNo()).equals("")){
						    sql.append("\n 		    AND h.issue_req_no <>'"+pickStock.getIssueReqNo()+"'");
						}
						if( !Utils.isNull(pickStock.getGroupCode()).equals("")){
							sql.append("\n 		    and i.group_code LIKE '%"+pickStock.getGroupCode()+"%'");
						}
						sql.append("\n 			)OH ");
						sql.append("\n			 GROUP BY OH.group_code,OH.pens_item ");
						sql.append("\n 		)OH2 ");
						sql.append("\n   ) M LEFT OUTER JOIN  ");
						
						sql.append("\n 	 ( select group_code,pens_item,NVL(SUM(i.req_qty),0) as qty  ");
						sql.append("\n 		 from PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i  ");
						sql.append("\n 		 where 1=1  ");
						sql.append("\n 	     AND h.warehouse ='"+pickStock.getWareHouse()+"'");
						sql.append("\n		 and h.issue_req_no = i.issue_req_no ");
						if( !Utils.isNull(pickStock.getIssueReqNo()).equals("")){
						   sql.append("\n 		 and h.issue_req_no ='"+pickStock.getIssueReqNo()+"'");
						}
						if( !Utils.isNull(pickStock.getGroupCode()).equals("")){
							sql.append("\n 		and i.group_code LIKE '%"+pickStock.getGroupCode()+"%'");
						}
						sql.append("\n 		 group by group_code,pens_item ");
						sql.append("\n 	 )P ");
						sql.append("\n 	 ON  M.pens_item = P.pens_item and M.group_code =P.group_code  ");
						
						sql.append("\n 	 LEFT OUTER JOIN  ");
						sql.append("\n 	 ( SELECT O2.group_code,O2.pens_item,NVL(SUM(onhand_qty),0) as onhand_qty ");
						sql.append("\n 	   FROM(   ");
						sql.append("\n 	     SELECT group_code,pens_item,(nvl(onhand_qty,0)-nvl(issue_qty,0)) as onhand_qty  ");
						sql.append("\n 	     FROM PENSBME_STOCK_FINISHED WHERE 1=1");
						sql.append("\n 	     AND warehouse ='"+pickStock.getWareHouse()+"'");
						if( !Utils.isNull(pickStock.getGroupCode()).equals("")){
							sql.append("\n 	 AND group_code LIKE '%"+pickStock.getGroupCode()+"%'");
						}
						sql.append("\n       UNION ALL ");
						 // substract from Stock issue status = O(Open)
						sql.append("\n 		  SELECT group_code,pens_item  ,(-1* nvl(req_qty,0) ) as onhand_qty ");
						sql.append("\n 		  FROM PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i  ");
						sql.append("\n 		  WHERE 1=1  ");
						sql.append("\n 	      AND h.warehouse ='"+pickStock.getWareHouse()+"'");
						sql.append("\n 		  AND h.issue_req_no = i.issue_req_no ");
						sql.append("\n 		  AND h.status IN('"+STATUS_OPEN+"','"+STATUS_POST+"')");
						if( !Utils.isNull(pickStock.getGroupCode()).equals("")){
							sql.append("\n 	  AND i.group_code LIKE '%"+pickStock.getGroupCode()+"%'");
						}
						sql.append("\n 	    )O2    ");
						sql.append("\n 	    GROUP BY O2.group_code,O2.pens_item  ");
						sql.append("\n 	  ) O   ");
						sql.append("\n 	 ON M.pens_item = O.pens_item and M.group_code = O.group_code  ");
				
				sql.append("\n   	  )U ");
				sql.append("\n   	  WHERE (U.onhand_qty > 0 OR U.qty > 0)");
				
				
			    sql.append("\n 		  ORDER BY U.group_code,U.pens_item");
				  
				sql.append("\n      )A  ");
				if( !allRec){
				  sql.append("\n     WHERE rownum < (("+pageNumber+" * "+REQ_PICK_PAGE_SIZE+") + 1 )  ");
			    }
				sql.append("\n   )M  ");
				if( !allRec){
				   sql.append("\n  WHERE r__ >= ((("+pageNumber+"-1) * "+REQ_PICK_PAGE_SIZE+") + 1)  ");
				}
				sql.append("\n )M2  ");
				
		    }else{
		    	//Case new
				sql.append("\n  SELECT M.*  FROM( ");
				sql.append("\n   SELECT a.*, rownum r__ FROM (");
				sql.append("\n   SELECT M2.* FROM (");
			    	sql.append("\n   SELECT M.group_code,M.pens_item,NVL(SUM(onhand_qty),0) as onhand_qty,0 as qty FROM ( ");
					sql.append("\n  	select group_code,pens_item,(nvl(onhand_qty,0)-(nvl(issue_qty,0))) as onhand_qty  ");
					sql.append("\n  	from PENSBME_STOCK_FINISHED ");
					sql.append("\n  	where 1=1   ");
					sql.append("\n 	    AND warehouse ='"+pickStock.getWareHouse()+"'");
					if( !Utils.isNull(pickStock.getGroupCode()).equals("")){
						sql.append("\n 	and group_code LIKE '%"+pickStock.getGroupCode()+"%'");
					}
					sql.append("\n      UNION ALL ");
					 // substract from Stock issue status = O(Open)
					sql.append("\n 		SELECT group_code,pens_item  ,(-1* nvl(req_qty,0)) as onhand_qty ");
					sql.append("\n 		FROM PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i  ");
					sql.append("\n 		WHERE 1=1  ");
					sql.append("\n 	    AND h.warehouse ='"+pickStock.getWareHouse()+"'");
					sql.append("\n 		AND h.issue_req_no = i.issue_req_no ");
					sql.append("\n 		AND h.status in('"+STATUS_OPEN+"','"+STATUS_POST+"')");
					if( !Utils.isNull(pickStock.getGroupCode()).equals("")){
						sql.append("\n 	and i.group_code LIKE '%"+pickStock.getGroupCode()+"%'");
					}
					sql.append("\n    )M  ");
					sql.append("\n    GROUP BY M.group_code,M.pens_item");
					sql.append("\n  )M2  WHERE M2.onhand_qty > 0");
					sql.append("\n  ORDER BY M2.group_code,M2.pens_item");
				sql.append("\n    )a  ");
				if( !allRec){
				  sql.append("\n  WHERE rownum < (("+pageNumber+" * "+REQ_PICK_PAGE_SIZE+") + 1 )  ");
				}
				sql.append("\n  )M  ");
				if( !allRec){
				  sql.append("\n  WHERE r__ >= ((("+pageNumber+"-1) * "+REQ_PICK_PAGE_SIZE+") + 1)  ");
				}
		    }
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   ReqPickStock h = new ReqPickStock();
			   h.setIssueReqNo(pickStock.getIssueReqNo());
			   h.setGroupCode(rst.getString("group_code"));
			   h.setPensItem(rst.getString("pens_item"));
			   h.setNewReq(pickStock.isNewReq());
			   h.setNewSearch(pickStock.isNewSearch());
               h.setWareHouse(pickStock.getWareHouse());
               
			   //Case Edit 
			   int onhandQty = rst.getInt("onhand_qty");
			   int qty =  rst.getInt("qty");
			   
			   //Case cancel no add
			   if( !STATUS_CANCEL.equals(pickStock.getStatus())){
				   if(!pickStock.isNewReq()){
					  onhandQty +=qty;
				   }
			   }
			   h.setOnhandQty(Utils.decimalFormat(onhandQty,Utils.format_current_no_disgit));
			   
			   if(qty != 0){
			     h.setQty(Utils.decimalFormat(qty,Utils.format_current_no_disgit));
			   }

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
				
		                sql.append("\n 	SELECT M.* ,NVL(P.issue_qty,0) as issue_qty ,NVL(P.req_qty,0) as req_qty FROM ( ");
						sql.append("\n 		select group_code,pens_item,material_master,barcode  ");
						sql.append("\n 		from PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i  ");
						sql.append("\n 		where 1=1  ");
						sql.append("\n 		and h.issue_req_no = i.issue_req_no ");
						if( !Utils.isNull(pickStock.getIssueReqNo()).equals("")){
						sql.append("\n 		and h.issue_req_no ='"+pickStock.getIssueReqNo()+"'");
						}
						sql.append("\n 		group by i.BARCODE, MATERIAL_MASTER,group_code,pens_item ");

						sql.append("\n   ) M LEFT OUTER JOIN  ");
						
						sql.append("\n 	 ( select group_code,pens_item,material_master,barcode ,NVL(SUM(i.req_qty),0) as req_qty,NVL(SUM(i.issue_qty),0) as issue_qty  ");
						sql.append("\n 		 from PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i  ");
						sql.append("\n 		 where 1=1  ");
						sql.append("\n		 and h.issue_req_no = i.issue_req_no ");
						if( !Utils.isNull(pickStock.getIssueReqNo()).equals("")){
						  sql.append("\n 		 and h.issue_req_no ='"+pickStock.getIssueReqNo()+"'");
						}
						sql.append("\n 		 group by group_code,pens_item,material_master,barcode  ");
						sql.append("\n 	 )P ");
						sql.append("\n 	 ON  M.pens_item = P.pens_item and M.group_code = P.group_code "  +
								       " AND M.material_master = P.material_master AND M.barcode =P.barcode   ");
				sql.append("\n   	  )U ");
			    sql.append("\n 		  order by U.group_code,U.pens_item, U.material_master");
				  
				sql.append("\n      )A  ");
				if( !allRec){
				  sql.append("\n     WHERE rownum < (("+pageNumber+" * "+PickConstants.REQ_PICK_PAGE_SIZE+") + 1 )  ");
			    }
				sql.append("\n   )M  ");
				if( !allRec){
				   sql.append("\n  WHERE r__ >= ((("+pageNumber+"-1) * "+PickConstants.REQ_PICK_PAGE_SIZE+") + 1)  ");
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
			   
			   if(issueQty != 0){
			     h.setIssueQty(Utils.decimalFormat(issueQty,Utils.format_current_no_disgit));
			   }
			   
			   if(reqQty != 0){
				  h.setQty(Utils.decimalFormat(reqQty,Utils.format_current_no_disgit));
				  h.setQtyInt(reqQty);
			   }
			   
			   items.add(h);
			   r++;
			   
			}//while
			
			pickStock.setCanPrint(true);
			pickStock.setItems(items);
			//pickStock.setTotalQty(totalQty);
			
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
	
	public static ReqPickStock getItemInStockListByGroupCode(Connection conn,ReqPickStock pickStock,Map<String,ReqPickStock> itemsBarcodeMap) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		List<ReqPickStock> items = new ArrayList<ReqPickStock>();
		int c = 1;
		int r = 1;
		int totalQty = 0;
		int totalOnhandQty = 0;
		logger.debug("***getItemInStockListByGroupCode***");
		try {
			if( !pickStock.isNewReq()){
				//Case Edit
				sql.append("\n SELECT  M2.* FROM(");//M2
				sql.append("\n   SELECT  M.*  FROM("); //M
				sql.append("\n     SELECT A.*, rownum r__ FROM ( ");//A
				sql.append("\n        SELECT  U.*  FROM( ");//U
				
		                sql.append("\n 	SELECT M.* ,NVL(P.qty,0) as qty ,( O.onhand_qty) as onhand_qty  FROM ( ");
		                //Data on table save
						sql.append("\n 		select i.BARCODE,MATERIAL_MASTER,group_code,pens_item  ");
						sql.append("\n 		from PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i  ");
						sql.append("\n 		where 1=1  ");
						sql.append("\n 		and h.issue_req_no = i.issue_req_no ");
						sql.append("\n      and h.warehouse ='"+pickStock.getWareHouse()+"'");
					    sql.append("\n 	    and i.group_code ='"+Utils.isNull(pickStock.getGroupCode())+"'");
						sql.append("\n 		and i.pens_item ='"+Utils.isNull(pickStock.getPensItem())+"'");
						if( !Utils.isNull(pickStock.getIssueReqNo()).equals("")){
						   sql.append("\n 		and h.issue_req_no ='"+pickStock.getIssueReqNo()+"'");
						}
						sql.append("\n 		group by i.BARCODE, MATERIAL_MASTER,group_code,pens_item ");
			
						sql.append("\n 		UNION ");
						
						sql.append("\n 		select BARCODE,MATERIAL_MASTER,group_code,pens_item FROM( ");
						sql.append("\n 			select BARCODE,MATERIAL_MASTER,group_code,pens_item  ,SUM(onhand_qty) as onhand_qty FROM( ");
						sql.append("\n 				select BARCODE,MATERIAL_MASTER,group_code,pens_item  ,SUM(nvl(onhand_qty,0)-nvl(issue_qty,0)) as onhand_qty ");
						sql.append("\n 				from PENSBME_STOCK_FINISHED   ");
						sql.append("\n 				WHERE group_code ='"+Utils.isNull(pickStock.getGroupCode())+"'");
						sql.append("\n 				and pens_item ='"+Utils.isNull(pickStock.getPensItem())+"'");
						sql.append("\n              and warehouse ='"+pickStock.getWareHouse()+"'");
						sql.append("\n 				group by BARCODE, MATERIAL_MASTER,group_code,pens_item  ");
						sql.append("\n 				UNION ALL ");
						sql.append("\n 				select i.BARCODE,MATERIAL_MASTER,group_code,pens_item  ,(-1*SUM(i.req_qty)) as onhand_qty ");
						sql.append("\n 				from PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i   ");
						sql.append("\n 				where 1=1   ");
						sql.append("\n 				and h.issue_req_no = i.issue_req_no  ");
						sql.append("\n              and h.warehouse ='"+pickStock.getWareHouse()+"'");
						sql.append("\n 		        and i.status IN('"+STATUS_OPEN+"','"+STATUS_POST+"')");
						sql.append("\n 				and i.group_code ='"+Utils.isNull(pickStock.getGroupCode())+"'");
						sql.append("\n 				and i.pens_item ='"+Utils.isNull(pickStock.getPensItem())+"'");
						sql.append("\n 				and h.issue_req_no <> '"+pickStock.getIssueReqNo()+"'");
						sql.append("\n 				group by i.BARCODE, MATERIAL_MASTER,group_code,pens_item  ");
						sql.append("\n 			) GROUP BY BARCODE, MATERIAL_MASTER,group_code,pens_item ");
						sql.append("\n 		)A WHERE A.onhand_qty > 0 ");
	
						sql.append("\n   ) M LEFT OUTER JOIN  ");
						
						sql.append("\n 	 ( select  i.BARCODE,MATERIAL_MASTER,group_code,pens_item,NVL(SUM(i.req_qty),0) as qty  ");
						sql.append("\n 		 from PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i  ");
						sql.append("\n 		 where 1=1  ");
						sql.append("\n       and h.warehouse ='"+pickStock.getWareHouse()+"'");
						sql.append("\n		 and h.issue_req_no = i.issue_req_no ");
						if( !Utils.isNull(pickStock.getIssueReqNo()).equals("")){
						   sql.append("\n 		and h.issue_req_no ='"+pickStock.getIssueReqNo()+"'");
						}
					    sql.append("\n       and i.group_code ='"+Utils.isNull(pickStock.getGroupCode())+"'");
					    sql.append("\n 		 and i.pens_item ='"+Utils.isNull(pickStock.getPensItem())+"'");
						sql.append("\n 		 group by i.BARCODE, MATERIAL_MASTER,group_code,pens_item ");
						sql.append("\n 	 )P ");
						sql.append("\n 	 ON M.BARCODE = P.BARCODE and M.pens_item = P.pens_item  " +
								   "\n 	 and M.MATERIAL_MASTER = p.MATERIAL_MASTER  and M.group_code =P.group_code  ");
						
						sql.append("\n 	 LEFT OUTER JOIN  ");
						sql.append("\n 	 ( SELECT O2.BARCODE,O2.MATERIAL_MASTER,O2.group_code,O2.pens_item,NVL(SUM(onhand_qty),0) as onhand_qty ");
						sql.append("\n 	   FROM(   ");
						sql.append("\n 	     SELECT BARCODE,MATERIAL_MASTER,group_code,pens_item,(nvl(onhand_qty,0)-nvl(issue_qty,0)) as onhand_qty  ");
						sql.append("\n 	     FROM PENSBME_STOCK_FINISHED ");
						sql.append("\n 		 WHERE group_code ='"+Utils.isNull(pickStock.getGroupCode())+"'");
						sql.append("\n       and warehouse ='"+pickStock.getWareHouse()+"'");
						sql.append("\n 		 and pens_item ='"+Utils.isNull(pickStock.getPensItem())+"'");
						sql.append("\n       UNION ALL ");
						 // substract from Stock issue status = O(Open)
						sql.append("\n 		  SELECT BARCODE,MATERIAL_MASTER,group_code,pens_item  ,(-1* nvl(req_qty,0) ) as onhand_qty ");
						sql.append("\n 		  FROM PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i  ");
						sql.append("\n 		  WHERE 1=1  ");
						sql.append("\n        and h.warehouse ='"+pickStock.getWareHouse()+"'");
						sql.append("\n 		  AND h.issue_req_no = i.issue_req_no ");
						sql.append("\n 		  AND h.status IN('"+STATUS_OPEN+"','"+STATUS_POST+"')");
					    sql.append("\n 		  AND i.group_code ='"+Utils.isNull(pickStock.getGroupCode())+"'");
					    sql.append("\n 		  AND i.pens_item ='"+Utils.isNull(pickStock.getPensItem())+"'");
					    
						sql.append("\n 	    )O2    ");
						sql.append("\n 	    GROUP BY O2.BARCODE,O2.MATERIAL_MASTER,O2.group_code,O2.pens_item  ");
						sql.append("\n 	  ) O   ");
						sql.append("\n 	 ON M.BARCODE = O. BARCODE and M.pens_item = O.pens_item  " +
								   "\n 	 and M.MATERIAL_MASTER = O.MATERIAL_MASTER  and M.group_code = O.group_code  ");
				
				sql.append("\n   	  )U ");
			    sql.append("\n 		  order by U.group_code,U.pens_item,U.MATERIAL_MASTER,U.BARCODE");
				  
				sql.append("\n      )A  ");
				sql.append("\n   )M  ");
				sql.append("\n )M2  ");
				
		    }else{
		    	//Case new
				sql.append("\n  SELECT M.*  FROM( ");
				sql.append("\n   SELECT a.*, rownum r__ FROM (");
				sql.append("\n   SELECT M2.* FROM (");
			    	sql.append("\n   SELECT M.BARCODE,M.MATERIAL_MASTER,M.group_code,M.pens_item,NVL(SUM(onhand_qty),0) as onhand_qty,0 as qty FROM ( ");
					sql.append("\n  	select BARCODE,MATERIAL_MASTER,group_code,pens_item,(nvl(onhand_qty,0)-nvl(issue_qty,0)) as onhand_qty  ");
					sql.append("\n  	from PENSBME_STOCK_FINISHED ");
					sql.append("\n  	where 1=1   ");
					sql.append("\n      and warehouse ='"+pickStock.getWareHouse()+"'");
					sql.append("\n 		and group_code ='"+Utils.isNull(pickStock.getGroupCode())+"'");
					sql.append("\n 		and pens_item ='"+Utils.isNull(pickStock.getPensItem())+"'");
					sql.append("\n      UNION ALL ");
					 // substract from Stock issue status = O(Open)
					sql.append("\n 		SELECT BARCODE,MATERIAL_MASTER,group_code,pens_item  ,(-1* nvl(req_qty,0) ) as onhand_qty ");
					sql.append("\n 		FROM PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i  ");
					sql.append("\n 		WHERE 1=1  ");
					sql.append("\n      and h.warehouse ='"+pickStock.getWareHouse()+"'");
					sql.append("\n 		AND h.issue_req_no = i.issue_req_no ");
					sql.append("\n 		AND h.status in('"+STATUS_OPEN+"','"+STATUS_POST+"')");
					sql.append("\n 		and i.group_code ='"+Utils.isNull(pickStock.getGroupCode())+"'");
					sql.append("\n 	    and i.pens_item ='"+Utils.isNull(pickStock.getPensItem())+"'");
					
					sql.append("\n    )M  ");
					sql.append("\n    GROUP BY M.group_code,M.pens_item,M.MATERIAL_MASTER,M.BARCODE");
					sql.append("\n  )M2  WHERE M2.onhand_qty > 0");
					sql.append("\n  ORDER BY M2.group_code,M2.pens_item,M2.MATERIAL_MASTER,M2.BARCODE");
				sql.append("\n    )a  ");
				sql.append("\n  )M  ");
		    }
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   ReqPickStock h = new ReqPickStock();

			   h.setBarcode(Utils.isNull(rst.getString("BARCODE")));
			   h.setMaterialMaster(rst.getString("MATERIAL_MASTER"));
			   h.setGroupCode(rst.getString("group_code"));
			   h.setPensItem(rst.getString("pens_item"));
			   h.setNewReq(pickStock.isNewReq());
				  
			   //Case Edit 
			   int onhandQty = rst.getInt("onhand_qty");
			   int qty =  rst.getInt("qty");
			   
			   //case Edit onhandQty=+ qty save
			   boolean itemBarcodeExist = isItemBarcodeExist(conn, h);
			   if(itemBarcodeExist){
				  onhandQty +=qty;
			   }
			   
			   h.setOnhandQty(Utils.decimalFormat(onhandQty,Utils.format_current_no_disgit));
			   
			   if(qty != 0){
				  h.setQty(Utils.decimalFormat(qty,Utils.format_current_no_disgit));  
			   }
			   
			   //Add to session by barcode
			   itemsBarcodeMap.put(h.getBarcode(), h);
			   
			   //sum total qty
			   totalQty += qty;
			   totalOnhandQty +=onhandQty;
			   
			   items.add(h);
			   r++;
			   
			}//while

			pickStock.setItems(items);
			pickStock.setTotalQty(totalQty);
			pickStock.setTotalOnhandQty(totalOnhandQty);
		
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
	
	@Deprecated
	public static ReqPickStock searchReqPickStockItemAllByPageCaseEdit(Connection conn,ReqPickStock pickStock,int pageNumber,int pageSize ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		List<ReqPickStock> items = new ArrayList<ReqPickStock>();
		int c = 1;
		int r = 1;
		int totalQty = 0;
		String groupCodeTemp ="";
		int groupCodeNo = 0;
		try {
			if( !pickStock.isNewReq()){
				//Case Edit
				sql.append("\n SELECT  M2.* FROM(");//M2
				sql.append("\n   SELECT  M.*  FROM("); //M
				sql.append("\n     SELECT A.*, rownum r__ FROM ( ");//A
				sql.append("\n        SELECT  U.*  FROM( ");//U
				
		                sql.append("\n 	SELECT M.* ,NVL(P.qty,0) as qty ,( O.onhand_qty) as onhand_qty  FROM ( ");
						sql.append("\n 		select i.BARCODE,MATERIAL_MASTER,group_code,pens_item  ");
						sql.append("\n 		from PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i  ");
						sql.append("\n 		where 1=1  ");
						sql.append("\n 		and h.issue_req_no = i.issue_req_no ");
						if( !Utils.isNull(pickStock.getIssueReqNo()).equals("")){
						   sql.append("\n 		and h.issue_req_no ='"+pickStock.getIssueReqNo()+"'");
						}
						sql.append("\n 		group by i.BARCODE, MATERIAL_MASTER,group_code,pens_item ");
			
						sql.append("\n 		UNION ");
						sql.append("\n 		select BARCODE,MATERIAL_MASTER,group_code,pens_item  "); 
						sql.append("\n 		from PENSBME_STOCK_FINISHED  ");
						sql.append("\n 		where 1=1    ");
						sql.append("\n 		and onhand_qty <> 0  ");
						
						sql.append("\n   ) M LEFT OUTER JOIN  ");
						
						sql.append("\n 	 ( select  i.BARCODE,MATERIAL_MASTER,group_code,pens_item,NVL(SUM(i.qty),0) as qty  ");
						sql.append("\n 		 from PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i  ");
						sql.append("\n 		 where 1=1  ");
						sql.append("\n		 and h.issue_req_no = i.issue_req_no ");
						if( !Utils.isNull(pickStock.getIssueReqNo()).equals("")){
						   sql.append("\n 		and h.issue_req_no ='"+pickStock.getIssueReqNo()+"'");
						}
						sql.append("\n 		 group by i.BARCODE, MATERIAL_MASTER,group_code,pens_item ");
						sql.append("\n 	 )P ");
						sql.append("\n 	 ON M.BARCODE = P.BARCODE and M.pens_item = P.pens_item  " +
								   "\n 	 and M.MATERIAL_MASTER = p.MATERIAL_MASTER  and M.group_code =P.group_code  ");
						
						sql.append("\n 	 LEFT OUTER JOIN  ");
						sql.append("\n 	 ( SELECT O2.BARCODE,O2.MATERIAL_MASTER,O2.group_code,O2.pens_item,NVL(SUM(onhand_qty),0) as onhand_qty ");
						sql.append("\n 	   FROM(   ");
						sql.append("\n 	     SELECT BARCODE,MATERIAL_MASTER,group_code,pens_item,onhand_qty  ");
						sql.append("\n 	     FROM PENSBME_STOCK_FINISHED ");
						
						sql.append("\n       UNION ALL ");
						 // substract from Stock issue status = O(Open)
						sql.append("\n 		  SELECT BARCODE,MATERIAL_MASTER,group_code,pens_item  ,(-1* qty ) as onhand_qty ");
						sql.append("\n 		  FROM PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i  ");
						sql.append("\n 		  WHERE 1=1  ");
						sql.append("\n 		  AND h.issue_req_no = i.issue_req_no ");
						sql.append("\n 		  AND h.status ='"+PickConstants.STATUS_OPEN+"'");
						sql.append("\n 	    )O2    ");
						sql.append("\n 	    GROUP BY O2.BARCODE,O2.MATERIAL_MASTER,O2.group_code,O2.pens_item  ");
						sql.append("\n 	  ) O   ");
						sql.append("\n 	 ON M.BARCODE = O. BARCODE and M.pens_item = O.pens_item  " +
								   "\n 	 and M.MATERIAL_MASTER = O.MATERIAL_MASTER  and M.group_code = O.group_code  ");
				
				sql.append("\n   	  )U ");
			    sql.append("\n 		  order by U.group_code,U.pens_item,U.MATERIAL_MASTER,U.BARCODE");
				  
				sql.append("\n      )A  ");
				sql.append("\n     WHERE rownum < (("+pageNumber+" * "+pageSize+") + 1 )  ");
				sql.append("\n   )M  ");
				sql.append("\n  WHERE r__ >= ((("+pageNumber+"-1) * "+pageSize+") + 1)  ");
				sql.append("\n )M2  ");
				
		    }else{
		    	//Case new
				sql.append("\n  SELECT M.*  FROM( ");
				sql.append("\n   SELECT a.*, rownum r__ FROM (");
				sql.append("\n   SELECT M2.* FROM (");
			    	sql.append("\n   SELECT M.BARCODE,M.MATERIAL_MASTER,M.group_code,M.pens_item,NVL(SUM(onhand_qty),0) as onhand_qty,0 as qty FROM ( ");
					sql.append("\n  	select BARCODE,MATERIAL_MASTER,group_code,pens_item,onhand_qty  ");
					sql.append("\n  	from PENSBME_STOCK_FINISHED ");
					sql.append("\n  	where 1=1   ");
					sql.append("\n  	and onhand_qty <> 0 ");
					sql.append("\n      UNION ALL ");
					 // substract from Stock issue status = O(Open)
					sql.append("\n 		SELECT BARCODE,MATERIAL_MASTER,group_code,pens_item  ,(-1* qty ) as onhand_qty ");
					sql.append("\n 		FROM PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i  ");
					sql.append("\n 		WHERE 1=1  ");
					sql.append("\n 		AND h.issue_req_no = i.issue_req_no ");
					sql.append("\n 		AND h.status ='"+PickConstants.STATUS_OPEN+"'");
					sql.append("\n    )M  ");
					sql.append("\n    GROUP BY M.group_code,M.pens_item,M.MATERIAL_MASTER,M.BARCODE");
					sql.append("\n  )M2  WHERE M2.onhand_qty > 0");
					sql.append("\n  ORDER BY M2.group_code,M2.pens_item,M2.MATERIAL_MASTER,M2.BARCODE");
				sql.append("\n    )a  ");
				sql.append("\n   WHERE rownum < (("+pageNumber+" * "+pageSize+") + 1 )  ");
				sql.append("\n  )M  ");
				sql.append("\n  WHERE r__ >= ((("+pageNumber+"-1) * "+pageSize+") + 1)  ");
		    }
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			  
			   ReqPickStock h = new ReqPickStock();
			   h.setBarcode(Utils.isNull(rst.getString("BARCODE")));
			   h.setMaterialMaster(rst.getString("MATERIAL_MASTER"));
			   h.setGroupCode(rst.getString("group_code"));
			   h.setPensItem(rst.getString("pens_item"));
			   
			   //Check display Line group by group code
			   if( !groupCodeTemp.equals(h.getGroupCode())){
				   h.setLineItemStyle("lineGroupBy");
				   groupCodeTemp = h.getGroupCode();
				   groupCodeNo = 0;
				   
				   h.setLineItemId(groupCodeTemp+"_"+groupCodeNo);
				   
			   }else{
				   h.setLineItemStyle("lineSubGroupBy");
				   h.setLineItemId(groupCodeTemp+"_"+groupCodeNo);
			   }
			   groupCodeNo++;
			   
			   //Case Edit 
			   int onhandQty = rst.getInt("onhand_qty");
			   int qty =  rst.getInt("qty");
			   
			   //Case cancel no add
			   if( !STATUS_CANCEL.equals(pickStock.getStatus())){
				   if(!pickStock.isNewReq()){
					  onhandQty +=qty;
				   }
			   }
			   h.setOnhandQty(Utils.decimalFormat(onhandQty,Utils.format_current_no_disgit));
			   
			   if(rst.getInt("qty") != 0){
			     h.setQty(Utils.decimalFormat(rst.getInt("qty"),Utils.format_current_no_disgit));
			   }
			   
			   h.setNewReq(pickStock.isNewReq());
			   
			   items.add(h);
			   r++;
			   
			   //sum total qty
			   totalQty += rst.getInt("qty");

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
	

	public static Map<String,ReqPickStock> searchPickStockItemByPKAllToMap(Connection conn,ReqPickStock pickStock) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Map<String,ReqPickStock> dataEditMapAll = new HashMap<String, ReqPickStock>();
		int c = 1;
		int r = 1;
		try {
			sql.append("\n select  i.BARCODE,MATERIAL_MASTER,group_code,pens_item,NVL(SUM(qty),0) as qty   ");
			sql.append("\n from PENSBME_STOCK_ISSUE h,  PENSBME_STOCK_ISSUE_ITEM i  ");
			sql.append("\n where 1=1 ");
			sql.append("\n and h.issue_req_no = i.issue_req_no ");
			if( !Utils.isNull(pickStock.getIssueReqNo()).equals("")){
			   sql.append("\n and h.issue_req_no ='"+pickStock.getIssueReqNo()+"'");
			}
			sql.append("\n group by i.BARCODE, MATERIAL_MASTER,group_code,pens_item ");
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   ReqPickStock h = new ReqPickStock();
			   h.setBarcode(Utils.isNull(rst.getString("BARCODE")));
			   h.setMaterialMaster(rst.getString("MATERIAL_MASTER"));
			   h.setGroupCode(rst.getString("group_code"));
			   h.setPensItem(rst.getString("pens_item"));
			 
			   if(rst.getInt("qty") != 0){
			     h.setQty(Utils.decimalFormat(rst.getInt("qty"),Utils.format_current_no_disgit));
			   }
			   //Key Map  
			   String key = h.getBarcode()+"_"+h.getMaterialMaster()+"_"+h.getGroupCode()+"_"+h.getPensItem();
			   dataEditMapAll.put(key, h);
			   
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
		return dataEditMapAll;
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
	
	
	
	public static boolean isItemBarcodeExist(Connection conn,ReqPickStock pickStock) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
	    boolean found = false;
		try {
			sql.append("\n SELECT count(*) as c FROM PENSBME_STOCK_ISSUE_ITEM   ");
			sql.append("\n where 1=1   ");
			
			if( !Utils.isNull(pickStock.getIssueReqNo()).equals("")){
				  sql.append("\n and issue_req_no ='"+pickStock.getIssueReqNo()+"'");
		    }
			if( !Utils.isNull(pickStock.getGroupCode()).equals("")){
				  sql.append("\n and group_code ='"+pickStock.getGroupCode()+"'");
			}
			if( !Utils.isNull(pickStock.getPensItem()).equals("")){
				  sql.append("\n and pens_item ='"+pickStock.getPensItem()+"'");
			}
			if( !Utils.isNull(pickStock.getMaterialMaster()).equals("")){
				  sql.append("\n and material_master ='"+pickStock.getMaterialMaster()+"'");
			}
			if( !Utils.isNull(pickStock.getBarcode()).equals("")){
				  sql.append("\n and barcode ='"+pickStock.getBarcode()+"'");
			}
				
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
            if(rst.next()){
            	int c = rst.getInt("c");
            	if(c>0){
            		found = true;
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
		return found;
	}

	
	
	public static ReqPickStock searchReqPickStockItemByPK(Connection conn,ReqPickStock pickStock) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		List<ReqPickStock> items = new ArrayList<ReqPickStock>();
		int c = 1;
		int r = 1;
		int totalQty = 0;
		try {
			sql.append("\n SELECT M.*,P.qty FROM ( ");
				sql.append("\n   ");
				sql.append("\n select  i.barcode,MATERIAL_MASTER,group_code,pens_item,sum(qty) as qty   ");
				sql.append("\n from PENSBME_STOCK_ISSUE h,  PENSBME_STICK_ISSUE_ITEM i  ");
				sql.append("\n where 1=1   ");
				sql.append("\n and h.issue_req_no = i.issue_req_no ");
				if( !Utils.isNull(pickStock.getIssueReqNo()).equals("")){
				   sql.append("\n and h.issue_req_no ='"+pickStock.getIssueReqNo()+"'");
				}
				sql.append("\n group by i.barcode, MATERIAL_MASTER,group_code,pens_item ");
				sql.append("\n )P INNER JOIN  ");
			
				sql.append("\n ( ");
				sql.append("\n select BARCODE,MATERIAL_MASTER,group_code,pens_item,qty as onhand_qty  ");
				sql.append("\n from PENSBME_STOCK_FINISHED ");
				sql.append("\n where 1=1   ");
				sql.append("\n ) M  ");
				
				sql.append("\n ON m.BARCODE = p.BARCODE and M.pens_item = P.pens_item  " +
						   "\n and M.MATERIAL_MASTER = p.MATERIAL_MASTER  and M.group_code =P.group_code  ");
				
				sql.append("\n order by m.BARCODE,M.MATERIAL_MASTER,M.group_code,M.pens_item ");
		    
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			  
			   ReqPickStock h = new ReqPickStock();
			 
			   h.setMaterialMaster(rst.getString("MATERIAL_MASTER"));
			   h.setGroupCode(rst.getString("group_code"));
			   h.setPensItem(rst.getString("pens_item"));
			   
			   //Case Edit 
			   int onhandQty = rst.getInt("onhand_qty");
			   int qty =  rst.getInt("qty");
			   
			   //Case cancel no add
			   if( !STATUS_CANCEL.equals(pickStock.getStatus())){
				  onhandQty +=qty;
			   }
			 //  h.setOnhandQty(Utils.decimalFormat(onhandQty,Utils.format_current_no_disgit));
			   
			   if(rst.getInt("qty") != 0){
			     h.setQty(Utils.decimalFormat(rst.getInt("qty"),Utils.format_current_no_disgit));
			   }
			   items.add(h);
			   r++;
			   
			   //sum total qty
			   totalQty += rst.getInt("qty");
			   
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
	
	
	// ( Running :  TYYMMxxxx  เช่น T57040001 เป็นต้น	
	 private static String genIssueReqNo(Date tDate) throws Exception{
		   String orderNo = "";
		   Connection conn = null;
		   try{
			   conn = DBConnection.getInstance().getConnection();
			   String today = df.format(tDate);
			   String[] d1 = today.split("/");
			   int curYear = Integer.parseInt(d1[0].substring(2,4));
			   int curMonth = Integer.parseInt(d1[1]);
			 
			   //get Seq
			   int seq = SequenceProcess.getNextValue(conn,"STOCK_ISSUE", "ISSUE_REQ_NO",tDate);
			   
			   orderNo = "T"+new DecimalFormat("00").format(curYear)+new DecimalFormat("00").format(curMonth)+new DecimalFormat("0000").format(seq);
		   }catch(Exception e){
			   throw e;
		   }finally{
			   if(conn !=null){
				   conn.close();conn=null;
			   }
		   }
		  return orderNo;
	}
	 

	private static void saveReqPickStockHeadModel(Connection conn,ReqPickStock o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("Insert");
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" INSERT INTO PENSBI.PENSBME_STOCK_ISSUE \n");
			sql.append(" (ISSUE_REQ_NO, ISSUE_REQ_DATE, STATUS,STATUS_DATE, REQUESTOR,REMARK ," +
					"CREATE_DATE,CREATE_USER,CUST_GROUP,CUSTOMER_NO,STORE_NO,SUB_INV,NEED_DATE,WAREHOUSE)  \n");
		    sql.append(" VALUES (?, ?, ?, ?, ?, ? , ? ,?,?,?,?,?,?,?) \n");
			
			ps = conn.prepareStatement(sql.toString());
				
			Date issueDate = Utils.parse( o.getIssueReqDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			Date needDate = Utils.parse( o.getNeedDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			int c =1;
			
			ps.setString(c++, o.getIssueReqNo());
			ps.setTimestamp(c++, new java.sql.Timestamp(issueDate.getTime()));
			ps.setString(c++, o.getStatus());
			ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));//status_date
			ps.setString(c++, o.getRequestor());
			ps.setString(c++, o.getRemark());
			ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(c++, o.getCreateUser());
			ps.setString(c++, o.getCustGroup());
			ps.setString(c++, o.getStoreCode());
			ps.setString(c++, o.getStoreNo());
			ps.setString(c++, o.getSubInv());
			ps.setTimestamp(c++, new java.sql.Timestamp(needDate.getTime()));
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
	
	private static void updateStockIssueModel(Connection conn,ReqPickStock o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("updateHeadModel");
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" UPDATE PENSBI.PENSBME_STOCK_ISSUE \n");
			sql.append(" SET  REQUESTOR = ?,REMARK =? ,UPDATE_DATE =?,UPDATE_USER =? \n");
		    sql.append(" WHERE ISSUE_REQ_NO = ? \n");
			
			ps = conn.prepareStatement(sql.toString());
			
			int c =1;
			
			ps.setString(c++, o.getRequestor());
			ps.setString(c++, o.getRemark());
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
	
	public static void updateStatusStockIssueItem(Connection conn,ReqPickStock o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("updateHeadModel");
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" UPDATE PENSBI.PENSBME_STOCK_ISSUE_ITEM \n");
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
	
	private static void saveStockIssueItemeModel(Connection conn,ReqPickStock o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("Insert");
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" INSERT INTO PENSBI.PENSBME_STOCK_ISSUE_ITEM \n");
			sql.append(" (ISSUE_REQ_NO,BARCODE,STATUS,STATUS_DATE,REQ_QTY,PENS_ITEM,material_Master,group_code,CREATE_DATE,CREATE_USER)  \n");
		    sql.append(" VALUES (?, ?, ?, ?, ?, ? , ? ,? , ?, ? ) \n");
			
			ps = conn.prepareStatement(sql.toString());

			int c =1;
			
			ps.setString(c++, o.getIssueReqNo());
			ps.setString(c++, o.getBarcode());
			ps.setString(c++, o.getStatus());
			ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));//STATUS DATE
			ps.setInt(c++, Utils.convertStrToInt(o.getQty()));//req_qty
			ps.setString(c++, o.getPensItem());
			ps.setString(c++, o.getMaterialMaster());
			ps.setString(c++, o.getGroupCode());
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
	
	private static void updateStockIssueItemeModel(Connection conn,ReqPickStock o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("update item");
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" UPDATE PENSBI.PENSBME_STOCK_ISSUE_ITEM \n");
			sql.append(" SET REQ_QTY = ? ,UPDATE_DATE = ?,UPDATE_USER =? \n");
		    sql.append(" WHERE 1=1 \n");
		    sql.append("\n and issue_req_no ='"+o.getIssueReqNo()+"'");
		    sql.append("\n and group_code ='"+o.getGroupCode()+"'");
		    sql.append("\n and pens_item ='"+o.getPensItem()+"'");
			sql.append("\n and material_master ='"+o.getMaterialMaster()+"'");
			sql.append("\n and barcode ='"+o.getBarcode()+"'");
			
			ps = conn.prepareStatement(sql.toString());

			int c =1;
			
			ps.setInt(c++, Utils.convertStrToInt(o.getQty()));//req_qty
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
	
	private static void deleteStockIssueItemeModel(Connection conn,ReqPickStock o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("delete item");
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" DELETE PENSBI.PENSBME_STOCK_ISSUE_ITEM \n");
		    sql.append(" WHERE 1=1 \n");
		    sql.append("\n and issue_req_no ='"+o.getIssueReqNo()+"'");
		    sql.append("\n and group_code ='"+o.getGroupCode()+"'");
		    sql.append("\n and pens_item ='"+o.getPensItem()+"'");
			sql.append("\n and material_master ='"+o.getMaterialMaster()+"'");
			sql.append("\n and barcode ='"+o.getBarcode()+"'");
			
			ps = conn.prepareStatement(sql.toString());
			ps.executeUpdate();
			
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}

	/**
	 * deleteReqPickStockIssueItemByIssueReqNo
	 * @param conn
	 * @param o
	 * @throws Exception
	 */
	public static void deleteStockIssueItemByIssueReqNoAndGroup(Connection conn,ReqPickStock o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("deleteStockIssueItemByIssueReqNo");
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" DELETE PENSBI.PENSBME_STOCK_ISSUE_ITEM \n");
			sql.append(" WHERE ISSUE_REQ_NO ='"+o.getIssueReqNo()+"' \n" );
			sql.append(" AND GROUP_CODE ='"+o.getGroupCode()+"' \n" );
			logger.debug("sql:"+sql.toString());
			
			ps = conn.prepareStatement(sql.toString());
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
