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
import com.isecinc.pens.bean.ReqPickStock;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.process.SequenceProcess;

public class ReqPickStockDAO extends PickConstants{
	
	protected static Logger logger = Logger.getLogger("PENS");
	protected static SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Utils.local_th);

	public static ReqPickStock save(Connection conn,ReqPickStock h,Map<String, ReqPickStock> dataSaveMapAll) throws Exception{
		ReqPickStock result = new ReqPickStock();
		result.setResultProcess(true);//default
		
		try{
			
			Date tDate  = Utils.parse(h.getIssueReqDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			//check documentNo
			if(Utils.isNull(h.getIssueReqNo()).equals("")){
				//Gen issueReqNo
				h.setIssueReqNo(genIssueReqNo(conn,tDate));
				h.setStatus(STATUS_OPEN);
				
				logger.debug("New IssueReqNo:"+h.getIssueReqNo());
				
				//save head
				saveReqPickStockHeadModel(conn, h);
				
				logger.debug("dataSaveMapAll["+dataSaveMapAll.size()+"]");
				
				//validate and insert ,update status barcode item
				result = validateAndInsert(conn,result,h,dataSaveMapAll);
				//Reset value Case Error
				if( !result.isResultProcess()){
					h.setIssueReqNo("");
					h.setStatus("");
				}
	
			}else{
				//Edit
                h.setStatus(STATUS_OPEN);
				logger.debug("Update IssueReqNo:"+h.getIssueReqNo());
				
				//save head
				updateStockIssueModel(conn, h);

				logger.debug("dataSaveMapAll["+dataSaveMapAll.size()+"]");
				
				//delete pickStockItem and update barcode item status=''
				deleteStockIssueItemByIssueReqNo(conn,h);
				
				//validate stock and insert ,update status barcode item
				result = validateAndInsert(conn,result,h,dataSaveMapAll);
			}
			
			return result;
		}catch(Exception e){
		  throw e;
		}finally{
			
		}
	}
	
	private static void addSaveStockItemQtyToOnhandQty(Connection conn,ReqPickStock h) throws Exception{ 
		List<ReqPickStock> itemList = new ArrayList<ReqPickStock>();	
		logger.debug("**********start vaddStockItemQtyToOnhandQty*****************");
		try{
			List<ReqPickStock> stockItemList = getStockItemListByIssueReqNo(conn,h);
			if(stockItemList != null){
			   for(int i=0;i<stockItemList.size();i++){
				   ReqPickStock p = stockItemList.get(i);
				   p.setUpdateUser(h.getUpdateUser());
	
				   int rUpdate = updateOldStockItemToStockFinishBarcode(conn, p);
				  
			   }//for
			}//if

			logger.debug("**********end  addStockItemQtyToOnhandQty*****************");
		}catch(Exception e){
			throw e;
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
	
	/**
	 * 
	 * @param conn
	 * @param result
	 * @param h
	 * @param dataSaveLists
	 * @return
	 * @throws Exception
	 */
	private static ReqPickStock validateAndInsert(Connection conn,ReqPickStock result,ReqPickStock h,Map<String, ReqPickStock> dataSaveMapAll) throws Exception{ 
		List<ReqPickStock> itemList = new ArrayList<ReqPickStock>();
		Map<String, ReqPickStock> itemsBarcodeErrorMap = new HashMap<String, ReqPickStock>();
		Map<String, ReqPickStock> groupCodeErrorMap = new HashMap<String, ReqPickStock>();
		
		boolean foundError = false;
		logger.debug("**********start validate and insert item*****************");
		try{
			//insert List
            Iterator its =  dataSaveMapAll.keySet().iterator();
			while(its.hasNext()){
			   String key = (String)its.next();
			   ReqPickStock p = (ReqPickStock)dataSaveMapAll.get(key);
			   logger.debug("Keymap["+key+"]="+p);
			   
			   int qty = Utils.convertStrToInt(p.getQty());//PICK
			   
			   //Step 1 validate in Stock onhand
			   Onhand onhand = OnhandProcessDAO.getItemInStockByPKITEM(conn, p);
			   int onhandQty = onhand!=null?Utils.convertStrToInt(onhand.getOnhandQty()):0;
			   
			   logger.debug("valid masterial_master["+p.getMaterialMaster()+"]onhadnQty["+onhandQty+"]qty["+qty+"]");
			   if(qty > onhandQty){
				   //fail
				   p.setLineErrorStock(true);
				   p.setLineItemStyle("lineError");
				   foundError = true;
				   //For Dispaly Error
				   itemsBarcodeErrorMap.put(key, p);
				   groupCodeErrorMap.put(p.getGroupCode(), p);
				   break;
			   }
			   
			   p.setIssueReqNo(h.getIssueReqNo());
			   p.setStatus(h.getStatus());
			   p.setCreateUser(h.getCreateUser());
			   p.setUpdateUser(h.getUpdateUser());
			   
			   /** Store to DataMap **/
			   dataSaveMapAll.put(key, p);
			   
			   //save pick_stock item
			   saveStockIssueItemeModel(conn, p);
			   
		       itemList.add(p);

			}//while 1
			
			if(foundError){
				result.setResultProcess(false);
				result.setItemsBarcodeErrorMap(itemsBarcodeErrorMap);
				result.setGroupCodeErrorMap(groupCodeErrorMap);
			}
			result.setItems(itemList);
			
			logger.debug("**********end   validate and insert item*****************");
			return result;
		}catch(Exception e){
			throw e;
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
			sql.append("\n SELECT * ");
			sql.append("\n from PENSBME_STOCK_ISSUE \n");
			sql.append("\n where 1=1   \n");
			
			if( !Utils.isNull(o.getIssueReqNo()).equals("")){
				sql.append("\n and issue_req_no = '"+Utils.isNull(o.getIssueReqNo())+"'  ");
			}
			
			if( !Utils.isNull(o.getStatus()).equals("")){
				sql.append("\n and status = '"+Utils.isNull(o.getStatus())+"'  ");
			}
			
			if( !Utils.isNull(o.getIssueReqDate()).equals("")){
				Date tDate  = Utils.parse(o.getIssueReqDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				String returnDateStr = Utils.stringValue(tDate, Utils.DD_MM_YYYY_WITH_SLASH);
				
				sql.append("\n and ISSUE_REQ_DATE = to_date('"+returnDateStr+"','dd/mm/yyyy') ");
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
	public static int getTotalQtyInStockIssueItem(Connection conn,ReqPickStock pickStock ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int totalRecord = 0;
		try {
			sql.append("\n select nvl(sum(qty),0) as qty   ");
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
	
	public static ReqPickStock getGroupCodeInStockList(Connection conn,ReqPickStock pickStock,int pageNumber,Map<String,ReqPickStock> groupCodeMap,Map<String,ReqPickStock> groupCodeErrorMap) throws Exception {
        return getGroupCodeInStockListModel(conn, pickStock, pageNumber, groupCodeMap, groupCodeErrorMap, false);
	}
	
	public static ReqPickStock getGroupCodeInStockList(Connection conn,ReqPickStock pickStock,Map<String,ReqPickStock> groupCodeMap,Map<String,ReqPickStock> groupCodeErrorMap,boolean allRec) throws Exception {
        return getGroupCodeInStockListModel(conn, pickStock, 0, groupCodeMap, groupCodeErrorMap, allRec);
	}
	
	public static ReqPickStock getGroupCodeInStockListModel(Connection conn,ReqPickStock pickStock,int pageNumber,Map<String,ReqPickStock> groupCodeMap,Map<String,ReqPickStock> groupCodeErrorMap,boolean allRec) throws Exception {
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
				
		                sql.append("\n 	SELECT M.* ,NVL(P.qty,0) as qty ,( O.onhand_qty) as onhand_qty  FROM ( ");
						sql.append("\n 		select group_code,pens_item  ");
						sql.append("\n 		from PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i  ");
						sql.append("\n 		where 1=1  ");
						sql.append("\n 		and h.issue_req_no = i.issue_req_no ");
						if( !Utils.isNull(pickStock.getIssueReqNo()).equals("")){
						sql.append("\n 		and h.issue_req_no ='"+pickStock.getIssueReqNo()+"'");
						}
						sql.append("\n 		group by i.BARCODE, MATERIAL_MASTER,group_code,pens_item ");
			
						sql.append("\n 		UNION ");
						
						sql.append("\n 		SELECT DISTINCT OH2.group_code,OH2.pens_item FROM ( ");
						sql.append("\n 			SELECT OH.group_code,OH.pens_item,NVL(SUM(onhand_qty),0) as onhand_qty FROM( ");   
						sql.append("\n 				SELECT  group_code,pens_item  ,(nvl(onhand_qty,0)-(nvl(issue_qty,0))) as onhand_qty ");
						sql.append("\n 				FROM PENSBME_STOCK_FINISHED ");  
						sql.append("\n 				WHERE 1=1 ");    
						sql.append("\n              UNION ALL ");
						sql.append("\n 				SELECT group_code,pens_item  ,(-1* qty ) as onhand_qty  ");
						sql.append("\n 				FROM PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i  "); 
						sql.append("\n 				WHERE 1=1   ");
						sql.append("\n 				AND h.issue_req_no = i.issue_req_no  ");
						sql.append("\n 				AND h.status ='"+PickConstants.STATUS_OPEN+"' ");
						if( !Utils.isNull(pickStock.getIssueReqNo()).equals("")){
						sql.append("\n 		        AND h.issue_req_no <>'"+pickStock.getIssueReqNo()+"'");
						}
						sql.append("\n 			)OH ");
						sql.append("\n			 GROUP BY OH.group_code,OH.pens_item ");
						sql.append("\n 		)OH2 ");
						sql.append("\n 		WHERE OH2.onhand_qty > 0 ");

						sql.append("\n   ) M LEFT OUTER JOIN  ");
						
						sql.append("\n 	 ( select group_code,pens_item,NVL(SUM(i.qty),0) as qty  ");
						sql.append("\n 		 from PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i  ");
						sql.append("\n 		 where 1=1  ");
						sql.append("\n		 and h.issue_req_no = i.issue_req_no ");
						if( !Utils.isNull(pickStock.getIssueReqNo()).equals("")){
						sql.append("\n 		 and h.issue_req_no ='"+pickStock.getIssueReqNo()+"'");
						}
						sql.append("\n 		 group by group_code,pens_item ");
						sql.append("\n 	 )P ");
						sql.append("\n 	 ON  M.pens_item = P.pens_item and M.group_code =P.group_code  ");
						
						sql.append("\n 	 LEFT OUTER JOIN  ");
						sql.append("\n 	 ( SELECT O2.group_code,O2.pens_item,NVL(SUM(onhand_qty),0) as onhand_qty ");
						sql.append("\n 	   FROM(   ");
						sql.append("\n 	     SELECT group_code,pens_item,(nvl(onhand_qty,0)-(nvl(issue_qty,0))) as onhand_qty  ");
						sql.append("\n 	     FROM PENSBME_STOCK_FINISHED ");
						
						sql.append("\n       UNION ALL ");
						 // substract from Stock issue status = O(Open)
						sql.append("\n 		  SELECT group_code,pens_item  ,(-1* qty ) as onhand_qty ");
						sql.append("\n 		  FROM PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i  ");
						sql.append("\n 		  WHERE 1=1  ");
						sql.append("\n 		  AND h.issue_req_no = i.issue_req_no ");
						sql.append("\n 		  AND h.status ='"+PickConstants.STATUS_OPEN+"'");
						sql.append("\n 	    )O2    ");
						sql.append("\n 	    GROUP BY O2.group_code,O2.pens_item  ");
						sql.append("\n 	  ) O   ");
						sql.append("\n 	 ON M.pens_item = O.pens_item and M.group_code = O.group_code  ");
				
				sql.append("\n   	  )U ");
			    sql.append("\n 		  order by U.group_code,U.pens_item");
				  
				sql.append("\n      )A  ");
				if( !allRec){
				  sql.append("\n     WHERE rownum < (("+pageNumber+" * "+PickConstants.REQ_PICK_PAGE_SIZE+") + 1 )  ");
			    }
				sql.append("\n   )M  ");
				if( !allRec){
				   sql.append("\n  WHERE r__ >= ((("+pageNumber+"-1) * "+PickConstants.REQ_PICK_PAGE_SIZE+") + 1)  ");
				}
				sql.append("\n )M2  ");
				
		    }else{
		    	//Case new
				sql.append("\n  SELECT M.*  FROM( ");
				sql.append("\n   SELECT a.*, rownum r__ FROM (");
				sql.append("\n   SELECT M2.* FROM (");
			    	sql.append("\n   SELECT M.group_code,M.pens_item,NVL(SUM(onhand_qty),0) as onhand_qty,0 as qty FROM ( ");
					sql.append("\n  	select group_code,pens_item,onhand_qty  ");
					sql.append("\n  	from PENSBME_STOCK_FINISHED ");
					sql.append("\n  	where 1=1   ");
					sql.append("\n  	and onhand_qty <> 0 ");
					sql.append("\n      UNION ALL ");
					 // substract from Stock issue status = O(Open)
					sql.append("\n 		SELECT group_code,pens_item  ,(-1* qty ) as onhand_qty ");
					sql.append("\n 		FROM PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i  ");
					sql.append("\n 		WHERE 1=1  ");
					sql.append("\n 		AND h.issue_req_no = i.issue_req_no ");
					sql.append("\n 		AND h.status ='"+PickConstants.STATUS_OPEN+"'");
					sql.append("\n    )M  ");
					sql.append("\n    GROUP BY M.group_code,M.pens_item");
					sql.append("\n  )M2  WHERE M2.onhand_qty > 0");
					sql.append("\n  ORDER BY M2.group_code,M2.pens_item");
				sql.append("\n    )a  ");
				if( !allRec){
				  sql.append("\n  WHERE rownum < (("+pageNumber+" * "+PickConstants.REQ_PICK_PAGE_SIZE+") + 1 )  ");
				}
				sql.append("\n  )M  ");
				if( !allRec){
				  sql.append("\n  WHERE r__ >= ((("+pageNumber+"-1) * "+PickConstants.REQ_PICK_PAGE_SIZE+") + 1)  ");
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
			   
			   //Add only first action 
			   ReqPickStock groupCodeBean = groupCodeMap.get(h.getGroupCode());
			   logger.debug("Check groupCodeBean["+groupCodeBean+"]");
			   
			   if(groupCodeBean==null){
				   logger.debug("add session setItemsBarcodeMap ["+h.getGroupCode()+"]");
				   //get items by group code
				   Map<String,ReqPickStock> itemsBarcodeMap = new HashMap<String, ReqPickStock>();
				   ReqPickStock itemBygroup = getItemInStockListByGroupCode(conn,h,itemsBarcodeMap,null);
				   //For session value display 
				   h.setItemsBarcodeMap(itemBygroup.getItemsBarcodeMap());
				   
				  //Display Error in groupCode 
				   if(groupCodeErrorMap !=null){
					   if(groupCodeErrorMap.get(h.getGroupCode()) !=null){
						   h.setLineItemStyle("lineError");
					   }
				   }

				 //Display Error in groupCode 
				   if(groupCodeErrorMap !=null){
					   if(groupCodeErrorMap.get(h.getGroupCode()) !=null){
						   logger.debug("set groupCode error:"+h.getGroupCode());
						   h.setLineItemStyle("lineError");
					   }
				   }
				   
				   //add key by group code
				   groupCodeMap.put(h.getGroupCode(),h);
			   }else{
				   //Case new Search refresh data in session
				   if( !h.isNewSearch()){
					   h = groupCodeBean;//set to old session bean
				   }

				   //Display Error in groupCode 
				   if(groupCodeErrorMap !=null){
					   if(groupCodeErrorMap.get(h.getGroupCode()) !=null){
						   logger.debug("set groupCode error:"+h.getGroupCode());
						   h.setLineItemStyle("lineError");
					   }
				   }

				   //add key by group code
				   groupCodeMap.put(h.getGroupCode(),h);
			   }

			   items.add(h);
			   r++;
			   
			}//while
			
			pickStock.setItems(items);
			pickStock.setTotalQty(totalQty);
			pickStock.setGroupCodeMap(groupCodeMap);
			
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
				
		                sql.append("\n 	SELECT M.* ,NVL(P.qty,0) as qty ,( O.onhand_qty) as onhand_qty  FROM ( ");
						sql.append("\n 		select group_code,pens_item,material_master,barcode  ");
						sql.append("\n 		from PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i  ");
						sql.append("\n 		where 1=1  ");
						sql.append("\n 		and h.issue_req_no = i.issue_req_no ");
						if( !Utils.isNull(pickStock.getIssueReqNo()).equals("")){
						sql.append("\n 		and h.issue_req_no ='"+pickStock.getIssueReqNo()+"'");
						}
						sql.append("\n 		group by i.BARCODE, MATERIAL_MASTER,group_code,pens_item ");

						sql.append("\n   ) M LEFT OUTER JOIN  ");
						
						sql.append("\n 	 ( select group_code,pens_item,material_master,barcode ,NVL(SUM(i.qty),0) as qty  ");
						sql.append("\n 		 from PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i  ");
						sql.append("\n 		 where 1=1  ");
						sql.append("\n		 and h.issue_req_no = i.issue_req_no ");
						if( !Utils.isNull(pickStock.getIssueReqNo()).equals("")){
						sql.append("\n 		 and h.issue_req_no ='"+pickStock.getIssueReqNo()+"'");
						}
						sql.append("\n 		 group by group_code,pens_item,material_master,barcode  ");
						sql.append("\n 	 )P ");
						sql.append("\n 	 ON  M.pens_item = P.pens_item and M.group_code =P.group_code "  +
								" AND M.material_master = P.material_master AND M.barcode =P.barcode   ");
						
						sql.append("\n 	 LEFT OUTER JOIN  ");
						sql.append("\n 	 ( SELECT O2.group_code,O2.pens_item,O2.material_master,O2.barcode ,NVL(SUM(onhand_qty),0) as onhand_qty ");
						sql.append("\n 	   FROM(   ");
						sql.append("\n 	     SELECT group_code,pens_item,material_master,barcode ,(nvl(onhand_qty,0)-nvl(issue_qty,0)) as onhand_qty  ");
						sql.append("\n 	     FROM PENSBME_STOCK_FINISHED ");
						
						sql.append("\n       UNION ALL ");
						 // substract from Stock issue status = O(Open)
						sql.append("\n 		  SELECT group_code,pens_item ,material_master,barcode  ,(-1* qty ) as onhand_qty ");
						sql.append("\n 		  FROM PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i  ");
						sql.append("\n 		  WHERE 1=1  ");
						sql.append("\n 		  AND h.issue_req_no = i.issue_req_no ");
						sql.append("\n 		  AND h.status ='"+PickConstants.STATUS_OPEN+"'");
						sql.append("\n 	    )O2    ");
						sql.append("\n 	    GROUP BY O2.group_code,O2.pens_item,O2.material_master,O2.barcode   ");
						sql.append("\n 	  ) O   ");
						sql.append("\n 	 ON M.pens_item = O.pens_item and M.group_code = O.group_code " +
								" AND M.material_master = O.material_master AND M.barcode =O.barcode   ");
				
				sql.append("\n   	  )U ");
			    sql.append("\n 		  order by U.group_code,U.pens_item");
				  
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
			 
			   int onhandQty = rst.getInt("onhand_qty");
			   int qty =  rst.getInt("qty");
			   h.setOnhandQty(Utils.decimalFormat(onhandQty,Utils.format_current_no_disgit));
			   
			   if(qty != 0){
			     h.setQty(Utils.decimalFormat(qty,Utils.format_current_no_disgit));
			   }
			   
			   items.add(h);
			   r++;
			   
			}//while
			
			pickStock.setCanPrint(true);
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
	
	public static ReqPickStock getItemInStockListByGroupCode(Connection conn,ReqPickStock pickStock,Map<String,ReqPickStock> itemsBarcodeMap,Map<String,ReqPickStock> itemsBarcodeErrorMap) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		List<ReqPickStock> items = new ArrayList<ReqPickStock>();
		int c = 1;
		int r = 1;
		int totalQty = 0;
		logger.debug("***getItemInStockListByGroupCode***");
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
						if( !Utils.isNull(pickStock.getGroupCode()).equals("")){
						   sql.append("\n 		and i.group_code ='"+Utils.isNull(pickStock.getGroupCode())+"'");
						}
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
						sql.append("\n 				group by BARCODE, MATERIAL_MASTER,group_code,pens_item  ");
						sql.append("\n 				UNION ALL ");
						sql.append("\n 				select i.BARCODE,MATERIAL_MASTER,group_code,pens_item  ,(-1*SUM(i.qty)) as onhand_qty ");
						sql.append("\n 				from PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i   ");
						sql.append("\n 				where 1=1   ");
						sql.append("\n 				and h.issue_req_no = i.issue_req_no  ");
						sql.append("\n 				and i.group_code ='"+Utils.isNull(pickStock.getGroupCode())+"'");
						sql.append("\n 				and h.issue_req_no <> '"+pickStock.getIssueReqNo()+"'");
						sql.append("\n 				group by i.BARCODE, MATERIAL_MASTER,group_code,pens_item  ");
						sql.append("\n 			) GROUP BY BARCODE, MATERIAL_MASTER,group_code,pens_item ");
						sql.append("\n 		)A WHERE A.onhand_qty > 0 ");
	
						
						/*sql.append("\n 		select BARCODE,MATERIAL_MASTER,group_code,pens_item  "); 
						sql.append("\n 		from PENSBME_STOCK_FINISHED  ");
						sql.append("\n 		where 1=1    ");
						sql.append("\n 		and onhand_qty <> 0  ");
						if( !Utils.isNull(pickStock.getGroupCode()).equals("")){
							sql.append("\n 		and group_code ='"+Utils.isNull(pickStock.getGroupCode())+"'");
						}*/
						
						sql.append("\n   ) M LEFT OUTER JOIN  ");
						
						sql.append("\n 	 ( select  i.BARCODE,MATERIAL_MASTER,group_code,pens_item,NVL(SUM(i.qty),0) as qty  ");
						sql.append("\n 		 from PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i  ");
						sql.append("\n 		 where 1=1  ");
						sql.append("\n		 and h.issue_req_no = i.issue_req_no ");
						if( !Utils.isNull(pickStock.getIssueReqNo()).equals("")){
						   sql.append("\n 		and h.issue_req_no ='"+pickStock.getIssueReqNo()+"'");
						}
						if( !Utils.isNull(pickStock.getGroupCode()).equals("")){
						  sql.append("\n        and i.group_code ='"+Utils.isNull(pickStock.getGroupCode())+"'");
						}
						sql.append("\n 		 group by i.BARCODE, MATERIAL_MASTER,group_code,pens_item ");
						sql.append("\n 	 )P ");
						sql.append("\n 	 ON M.BARCODE = P.BARCODE and M.pens_item = P.pens_item  " +
								   "\n 	 and M.MATERIAL_MASTER = p.MATERIAL_MASTER  and M.group_code =P.group_code  ");
						
						sql.append("\n 	 LEFT OUTER JOIN  ");
						sql.append("\n 	 ( SELECT O2.BARCODE,O2.MATERIAL_MASTER,O2.group_code,O2.pens_item,NVL(SUM(onhand_qty),0) as onhand_qty ");
						sql.append("\n 	   FROM(   ");
						sql.append("\n 	     SELECT BARCODE,MATERIAL_MASTER,group_code,pens_item,(nvl(onhand_qty,0)-nvl(issue_qty,0)) as onhand_qty  ");
						sql.append("\n 	     FROM PENSBME_STOCK_FINISHED ");
						if( !Utils.isNull(pickStock.getGroupCode()).equals("")){
							sql.append("\n 		 WHERE group_code ='"+Utils.isNull(pickStock.getGroupCode())+"'");
						}
						sql.append("\n       UNION ALL ");
						 // substract from Stock issue status = O(Open)
						sql.append("\n 		  SELECT BARCODE,MATERIAL_MASTER,group_code,pens_item  ,(-1* qty ) as onhand_qty ");
						sql.append("\n 		  FROM PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i  ");
						sql.append("\n 		  WHERE 1=1  ");
						sql.append("\n 		  AND h.issue_req_no = i.issue_req_no ");
						sql.append("\n 		  AND h.status ='"+PickConstants.STATUS_OPEN+"'");
						if( !Utils.isNull(pickStock.getGroupCode()).equals("")){
							   sql.append("\n 		and i.group_code ='"+Utils.isNull(pickStock.getGroupCode())+"'");
						}
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
					sql.append("\n  	and onhand_qty <> 0 ");
					if( !Utils.isNull(pickStock.getGroupCode()).equals("")){
						sql.append("\n 		and group_code ='"+Utils.isNull(pickStock.getGroupCode())+"'");
					}
					sql.append("\n      UNION ALL ");
					 // substract from Stock issue status = O(Open)
					sql.append("\n 		SELECT BARCODE,MATERIAL_MASTER,group_code,pens_item  ,(-1* qty ) as onhand_qty ");
					sql.append("\n 		FROM PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i  ");
					sql.append("\n 		WHERE 1=1  ");
					sql.append("\n 		AND h.issue_req_no = i.issue_req_no ");
					sql.append("\n 		AND h.status ='"+PickConstants.STATUS_OPEN+"'");
					if( !Utils.isNull(pickStock.getGroupCode()).equals("")){
						  sql.append("\n 		and group_code ='"+Utils.isNull(pickStock.getGroupCode())+"'");
					}
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
			   
			   //get data from screen or data save key =barcode
			   String key = h.getBarcode();
			   if(itemsBarcodeMap != null && itemsBarcodeMap.get(key) != null){
				   ReqPickStock lineOldData = itemsBarcodeMap.get(key);
				   if(lineOldData != null){
					  logger.debug("lineOldData.getQty():"+lineOldData.getQty());
				      qty = Utils.convertStrToInt(lineOldData.getQty());
				   }
				   
				   //get old value from session
				   if(qty != 0){
				     h.setQty(qty+"");
				   }
			   }
 
			   //Case STATUS OPEN 
			   if( STATUS_OPEN.equals(pickStock.getStatus())){
				   boolean itemBarcodeExist = isItemBarcodeExist(conn, h);
				   if(itemBarcodeExist){
					  onhandQty +=qty;
				   }
			   }
			   h.setOnhandQty(Utils.decimalFormat(onhandQty,Utils.format_current_no_disgit));
			   if(qty != 0){
			     h.setQty(Utils.decimalFormat(qty,Utils.format_current_no_disgit));
			   }
			   
			   //display error
			   if(itemsBarcodeErrorMap != null){
				   if(itemsBarcodeErrorMap.get(h.getBarcode()) != null){
					   h.setLineItemStyle("lineError");  
				   }
			   }
			   
			   //Add to session by barcode
			   itemsBarcodeMap.put(h.getBarcode(), h);
			   
			   //sum total qty
			   totalQty += qty;
			   
			   items.add(h);
			   r++;
			   
			}//while
			
			//add line item error to display
			 if(itemsBarcodeErrorMap != null){
				 Iterator<String> its = itemsBarcodeErrorMap.keySet().iterator();
				 while(its.hasNext()){
					 String key = its.next();
					 ReqPickStock lineItem = itemsBarcodeErrorMap.get(key);
					 //get new onhand qty 
					 Onhand onhand = OnhandProcessDAO.getItemInStockByPKITEM(conn, lineItem);
					 int onhandQty = onhand!=null?Utils.convertStrToInt(onhand.getOnhandQty()):0;
					 
					 lineItem.setOnhandQty(onhandQty+"");
					 lineItem.setLineItemStyle("lineError");
					 
					 items.add(lineItem);
				 }
				 
			  }
			   
			
			pickStock.setItems(items);
			pickStock.setTotalQty(totalQty);
			pickStock.setItemsBarcodeMap(itemsBarcodeMap);
			
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
	 private static String genIssueReqNo(Connection conn,Date tDate) throws Exception{
		   String orderNo = "";
		   try{
			   String today = df.format(tDate);
			   String[] d1 = today.split("/");
			   int curYear = Integer.parseInt(d1[0].substring(2,4));
			   int curMonth = Integer.parseInt(d1[1]);
			 
			   //get Seq
			   int seq = SequenceProcess.getNextValue(conn,"STOCK_ISSUE", "ISSUE_REQ_NO",tDate);
			   
			   orderNo = "T"+new DecimalFormat("00").format(curYear)+new DecimalFormat("00").format(curMonth)+new DecimalFormat("0000").format(seq);
		   }catch(Exception e){
			   throw e;
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
					"CREATE_DATE,CREATE_USER,CUST_GROUP,CUSTOMER_NO,STORE_NO,SUB_INV,NEED_DATE)  \n");
		    sql.append(" VALUES (?, ?, ?, ?, ?, ? , ? ,?,?,?,?,?,?) \n");
			
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
			sql.append(" (ISSUE_REQ_NO,BARCODE,STATUS,STATUS_DATE,QTY,PENS_ITEM,material_Master,group_code,CREATE_DATE,CREATE_USER,REQ_QTY)  \n");
		    sql.append(" VALUES (?, ?, ?, ?, ?, ? , ? ,? , ?, ? ,?) \n");
			
			ps = conn.prepareStatement(sql.toString());

			int c =1;
			
			ps.setString(c++, o.getIssueReqNo());
			ps.setString(c++, o.getBarcode());
			ps.setString(c++, o.getStatus());
			ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));//STATUS DATE
			ps.setInt(c++, Utils.convertStrToInt(o.getQty()));//qty
			ps.setString(c++, o.getPensItem());
			ps.setString(c++, o.getMaterialMaster());
			ps.setString(c++, o.getGroupCode());
			ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(c++, o.getCreateUser());
			ps.setInt(c++, Utils.convertStrToInt(o.getQty()));//req_qty = qty
			
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
	public static void deleteStockIssueItemByIssueReqNo(Connection conn,ReqPickStock o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("deleteStockIssueItemByIssueReqNo");
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" DELETE PENSBI.PENSBME_STOCK_ISSUE_ITEM \n");
			sql.append(" WHERE ISSUE_REQ_NO ='"+o.getIssueReqNo()+"' \n" );
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
