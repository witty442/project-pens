package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
import com.pens.util.Utils;

public class OnhandProcessDAO {
	
	protected static Logger logger = Logger.getLogger("PENS");
	protected static SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Utils.local_th);
	
	public static void main(String[] args ){
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			//processUpdateBarcodeHeadStatusClose(conn, "xx");
			
			//processUpdateBarcodeHeadStatusIssue(conn, "xx","");
			
			conn.commit();
		}catch(Exception e){
			try{
				conn.rollback();
				e.printStackTrace();
			}catch(Exception ee){}
		}finally{
			try{conn.close();}catch(Exception eee){}
		}
	}

	public OnhandProcessDAO() {
		// TODO Auto-generated constructor stub
	}
	
	//Action : After Confirm Finishing
	public static void processBanlanceOnhandFromConfirmFinishing(String warehouse,String requestNo,String userName) throws Exception{
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			Date startDate = new Date();
			List<Onhand> onhandItemList = searchItemFormReqFinishing(conn,warehouse,requestNo);
			logger.debug("searchBarcodeItemInStock>>Total Time:"+(new Date().getTime()-startDate.getTime()) +",Result :"+onhandItemList.size());
			 
			startDate =new Date();
			if(onhandItemList != null && onhandItemList.size() >0){
				for(int i=0;i<onhandItemList.size();i++){
					Onhand itemOnhand = (Onhand)onhandItemList.get(i);
					itemOnhand.setCreateUser(userName);
					itemOnhand.setUpdateUser(userName);
					
					int rUpdate = updateOnhandQtyModel(conn, itemOnhand);
					if(rUpdate==0){
						insertOnhandQtyModel(conn, itemOnhand);
					}
				}
			}
			logger.debug("insertOrUpdateOnhand>>Total Time:"+(new Date().getTime()-startDate.getTime()));
			conn.commit();
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		    conn.rollback();
		}finally{
			if(conn !=null){
				conn.close();conn=null;
			}
		}
	}
	
	//Action :after confirm in Confirm by dept
	public static void processUpdateBanlanceOnhandFromStockIssue(Connection conn,ReqPickStock req) throws Exception{
		try{
			Date startDate = new Date();
			List<Onhand> onhandItemList = searchItemFormStockIssueItem(conn, req);
			logger.debug("onhandItemList>>Total Time:"+(new Date().getTime()-startDate.getTime()) +",Result :"+onhandItemList.size());
			 
			startDate =new Date();
			if(onhandItemList != null && onhandItemList.size() >0){
				for(int i=0;i<onhandItemList.size();i++){
					Onhand itemOnhand = (Onhand)onhandItemList.get(i);
					itemOnhand.setWareHouse(req.getWareHouse());
					itemOnhand.setCreateUser(req.getUpdateUser());
					itemOnhand.setUpdateUser(req.getUpdateUser());
					
					int rUpdate = updateOnhandQtyModelCaseConfirmByDept(conn, itemOnhand);
					logger.debug("result update:"+rUpdate);
					
				}
			}
			logger.debug("insertOrUpdateOnhand>>Total Time:"+(new Date().getTime()-startDate.getTime()));
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
		}
	}
	//Action :after cancel Confirm by dept
	public static void processUpdateBanlanceOnhandFromStockIssueCaseCancelReq(Connection conn,ReqPickStock req) throws Exception{
		try{
			Date startDate = new Date();
			List<Onhand> onhandItemList = searchItemFormStockIssueItem(conn, req);
			logger.debug("onhandItemList>>Total Time:"+(new Date().getTime()-startDate.getTime()) +",Result :"+onhandItemList.size());
			 
			startDate =new Date();
			if(onhandItemList != null && onhandItemList.size() >0){
				for(int i=0;i<onhandItemList.size();i++){
					Onhand itemOnhand = (Onhand)onhandItemList.get(i);
					itemOnhand.setWareHouse(req.getWareHouse());
					itemOnhand.setCreateUser(req.getUpdateUser());
					itemOnhand.setUpdateUser(req.getUpdateUser());
					
					int rUpdate = updateOnhandQtyModelCaseCancelConfirmByDept(conn, itemOnhand);
					logger.debug("result update:"+rUpdate);
					
				}
			}
			logger.debug("insertOrUpdateOnhand>>Total Time:"+(new Date().getTime()-startDate.getTime()));
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
		}
	}
	
	public static List<Onhand> searchItemFormReqFinishing(Connection conn,String warehouse,String requestNo) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Onhand h = null;
		int r = 1;
        List<Onhand> items = new ArrayList<Onhand>();
		try {
			sql.append("\n select  h.warehouse,i.barcode,i.pens_item ,i.barcode,i.MATERIAL_MASTER,group_code,count(*) as qty " +
					   "\n from PENSBI.PENSBME_REQ_FINISHING h ,PENSBI.PENSBME_REQ_FINISHING_BARCODE i   ");
			sql.append("\n where 1=1");
			sql.append("\n and h.request_no = i.request_no  ");
			sql.append("\n and h.request_no = '"+requestNo+"'");
			sql.append("\n and h.status ='"+PickConstants.STATUS_FINISH+"' ");
			sql.append("\n and i.status ='"+PickConstants.STATUS_FINISH+"'");
			sql.append("\n and h.warehouse ='"+warehouse+"'");
			
			sql.append("\n group by i.barcode,pens_item,MATERIAL_MASTER,group_code ,h.warehouse");
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   h = new Onhand();
			   h.setBarcode(Utils.isNull(rst.getString("BARCODE")));
			   h.setMaterialMaster(rst.getString("MATERIAL_MASTER"));
			   h.setGroupCode(rst.getString("group_code"));
			   h.setPensItem(rst.getString("pens_item"));
			   h.setBarcode(rst.getString("barcode"));
			   h.setOnhandQty(Utils.decimalFormat(rst.getInt("qty"),Utils.format_current_no_disgit));
			   h.setWareHouse(warehouse);
			   
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
	
	public static List<Onhand> searchItemFormStockIssueItem(Connection conn,ReqPickStock req) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Onhand h = null;
		int r = 1;
        List<Onhand> items = new ArrayList<Onhand>();
		try {
			sql.append("\n select i.barcode,i.pens_item ,i.barcode,i.MATERIAL_MASTER,group_code,issue_qty" +
					   "\n from PENSBI.PENSBME_STOCK_ISSUE_ITEM i   ");
			sql.append("\n where 1=1");
			sql.append("\n and i.issue_req_no ='"+req.getIssueReqNo()+"'");
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			  
			   h = new Onhand();
			   h.setBarcode(Utils.isNull(rst.getString("BARCODE")));
			   h.setMaterialMaster(rst.getString("MATERIAL_MASTER"));
			   h.setGroupCode(rst.getString("group_code"));
			   h.setPensItem(rst.getString("pens_item"));
			   h.setBarcode(rst.getString("barcode"));
			   h.setIssueQty(Utils.decimalFormat(rst.getInt("issue_qty"),Utils.format_current_no_disgit));
			   
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
	public static Onhand getItemInStockByPKITEM(Connection conn,ReqPickStock o) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Onhand h = null;
		try {
			sql.append("\n select barcode,MATERIAL_MASTER,group_code,pens_item,SUM(onhand_qty) as onhand_qty from ( ");
			sql.append("\n  select barcode,MATERIAL_MASTER,group_code,pens_item,(nvl(onhand_qty,0)-nvl(issue_qty,0)) as onhand_qty " );
			sql.append("\n  from PENSBI.PENSBME_STOCK_FINISHED  ");
			sql.append("\n  where 1=1 ");
			sql.append("\n  and warehouse ='"+o.getWareHouse()+"'");
			sql.append("\n  and group_code ='"+o.getGroupCode()+"'");
			sql.append("\n  and pens_item ='"+o.getPensItem()+"'");
			sql.append("\n  and barcode ='"+o.getBarcode()+"'");
			sql.append("\n  and material_master ='"+o.getMaterialMaster()+"'");
			
			sql.append("\n  UNION ALL ");
			
			 // substract from Stock issue status = O(Open)
			sql.append("\n 	SELECT BARCODE,MATERIAL_MASTER,group_code,pens_item  ,(-1* nvl(req_qty,0) ) as onhand_qty ");
			sql.append("\n  FROM PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i  ");
			sql.append("\n 	WHERE 1=1  ");
			sql.append("\n  and h.warehouse ='"+o.getWareHouse()+"'");
			sql.append("\n 	AND h.issue_req_no = i.issue_req_no ");
			sql.append("\n 	AND h.status ='"+PickConstants.STATUS_OPEN+"'");
			sql.append("\n  and i.group_code ='"+o.getGroupCode()+"'");
			sql.append("\n  and i.pens_item ='"+o.getPensItem()+"'");
			sql.append("\n  and i.barcode ='"+o.getBarcode()+"'");
			sql.append("\n  and i.material_master ='"+o.getMaterialMaster()+"'");
			
			//add data save in line barcode case edit
			if( !Utils.isNull(o.getIssueReqNo()).equals("")){
			    sql.append("\n  UNION ALL ");
			    
			    sql.append("\n 	SELECT BARCODE,MATERIAL_MASTER,group_code,pens_item  ,(req_qty) as onhand_qty ");
				sql.append("\n  FROM PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i  ");
				sql.append("\n 	WHERE 1=1  ");
				sql.append("\n  and h.warehouse ='"+o.getWareHouse()+"'");
				sql.append("\n 	AND h.issue_req_no = i.issue_req_no ");
				sql.append("\n 	AND h.status ='"+PickConstants.STATUS_OPEN+"'");
				sql.append("\n  and i.issue_req_no ='"+o.getIssueReqNo()+"'");
				sql.append("\n  and i.group_code ='"+o.getGroupCode()+"'");
				sql.append("\n  and i.pens_item ='"+o.getPensItem()+"'");
				sql.append("\n  and i.barcode ='"+o.getBarcode()+"'");
				sql.append("\n  and i.material_master ='"+o.getMaterialMaster()+"'");
			}
			sql.append("\n )M ");
			sql.append("\n GROUP BY M.barcode,M.MATERIAL_MASTER,M.group_code,M.pens_item");
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			if(rst.next()) {
			   h = new Onhand();
			   h.setWareHouse(o.getWareHouse());
			   h.setBarcode(Utils.isNull(rst.getString("barcode")));
			   h.setMaterialMaster(rst.getString("MATERIAL_MASTER"));
			   h.setGroupCode(rst.getString("group_code"));
			   h.setPensItem(rst.getString("pens_item"));
			   h.setOnhandQty(Utils.decimalFormat(rst.getInt("onhand_qty"),Utils.format_current_no_disgit));
  
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
	
	public static Onhand getItemInStockByGroupCode(Connection conn,ReqPickStock o) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Onhand h = null;
		try {
			sql.append("\n select group_code,SUM(onhand_qty) as onhand_qty from ( ");
			sql.append("\n  select group_code,sum(onhand_qty) as onhand_qty from PENSBI.PENSBME_STOCK_FINISHED  ");
			sql.append("\n  where 1=1 ");
			sql.append("\n  and group_code ='"+o.getGroupCode()+"'");
			sql.append("\n  group by group_code ");
			
			sql.append("\n  UNION ALL ");
			
			 // substract from Stock issue status = O(Open)
			sql.append("\n 	SELECT group_code  ,(-1* sum(qty) ) as onhand_qty ");
			sql.append("\n  FROM PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i  ");
			sql.append("\n 	WHERE 1=1  ");
			sql.append("\n 	AND h.issue_req_no = i.issue_req_no ");
			sql.append("\n 	AND h.status ='"+PickConstants.STATUS_OPEN+"'");
			sql.append("\n  and i.group_code ='"+o.getGroupCode()+"'");
			sql.append("\n   group by i.group_code");
			
			//add data save in line barcode case edit
			if( !Utils.isNull(o.getIssueReqNo()).equals("")){
			    sql.append("\n  UNION ALL ");
			    
			    sql.append("\n 	SELECT Bgroup_code,sum(qty) as onhand_qty ");
				sql.append("\n  FROM PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i  ");
				sql.append("\n 	WHERE 1=1  ");
				sql.append("\n 	AND h.issue_req_no = i.issue_req_no ");
				sql.append("\n 	AND h.status ='"+PickConstants.STATUS_OPEN+"'");
				sql.append("\n  and i.issue_req_no ='"+o.getIssueReqNo()+"'");
				sql.append("\n  and i.group_code ='"+o.getGroupCode()+"'");
				sql.append("\n  group by i.group_code");
			}
			sql.append("\n )M ");
			sql.append("\n GROUP BY M.group_code");
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			if(rst.next()) {
			   h = new Onhand();
			   h.setGroupCode(rst.getString("group_code"));
			   h.setOnhandQty(Utils.decimalFormat(rst.getInt("onhand_qty"),Utils.format_current_no_disgit));
  
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
	
	
	private static void insertOnhandQtyModel(Connection conn,Onhand o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("Insert");
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" INSERT INTO PENSBI.PENSBME_STOCK_FINISHED \n");
			sql.append(" (BARCODE,PENS_ITEM,material_Master,group_code,ONHAND_QTY ,CREATE_DATE,CREATE_USER,WAREHOUSE)  \n");
		    sql.append(" VALUES (?,?, ?, ? , ? ,? ,?, ?) \n");
			
			ps = conn.prepareStatement(sql.toString());
				
			int c =1;
			ps.setString(c++, o.getBarcode());
			ps.setString(c++, o.getPensItem());
			ps.setString(c++, o.getMaterialMaster());
			ps.setString(c++, o.getGroupCode());
			ps.setInt(c++, Utils.convertStrToInt(o.getOnhandQty()));
			ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(c++, o.getCreateUser());
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

	public static int updateOnhandQtyModel(Connection conn,Onhand o) throws Exception{
		PreparedStatement ps = null;
		int r = 0;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" UPDATE PENSBI.PENSBME_STOCK_FINISHED SET ONHAND_QTY =(ONHAND_QTY + ?),UPDATE_DATE=?,UPDATE_USER = ? \n");
			sql.append(" WHERE PENS_ITEM =?  and material_Master = ? and group_code = ?  and barcode = ? and warehouse=? \n" );

			ps = conn.prepareStatement(sql.toString());
				
			ps.setInt(1, Utils.convertStrToInt(o.getOnhandQty()));
			ps.setTimestamp(2, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(3, o.getUpdateUser());
			ps.setString(4, o.getPensItem());
			ps.setString(5, o.getMaterialMaster());
			ps.setString(6, o.getGroupCode());
			ps.setString(7, o.getBarcode());
			ps.setString(8, o.getWareHouse());
			
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
	
	public static int updateOnhandQtyModelCaseConfirmByDept(Connection conn,Onhand o) throws Exception{
		PreparedStatement ps = null;
		int r = 0;
		int c =1;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" UPDATE PENSBI.PENSBME_STOCK_FINISHED SET ISSUE_QTY=(NVl(ISSUE_QTY,0) + ?) ,UPDATE_DATE=?,UPDATE_USER = ? \n");
			sql.append(" WHERE PENS_ITEM =?  and material_Master = ? and group_code = ?  and barcode = ? and warehouse= ? \n" );

			ps = conn.prepareStatement(sql.toString());
				
			ps.setInt(c++, Utils.convertStrToInt(o.getIssueQty())); // issue_qty = issue_qty + qty ,
			ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(c++, o.getUpdateUser());
			ps.setString(c++, o.getPensItem());
			ps.setString(c++, o.getMaterialMaster());
			ps.setString(c++, o.getGroupCode());
			ps.setString(c++, o.getBarcode());
			ps.setString(c++, o.getWareHouse());
			
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
	
	public static int updateOnhandQtyModelCaseCancelConfirmByDept(Connection conn,Onhand o) throws Exception{
		PreparedStatement ps = null;
		int r = 0;
		int c =1;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" UPDATE PENSBI.PENSBME_STOCK_FINISHED SET ISSUE_QTY=(NVl(ISSUE_QTY,0) - ?) ,UPDATE_DATE=?,UPDATE_USER = ? \n");
			sql.append(" WHERE PENS_ITEM =?  and material_Master = ? and group_code = ?  and barcode = ? and warehouse = ? \n" );

			ps = conn.prepareStatement(sql.toString());
				
			ps.setInt(c++, Utils.convertStrToInt(o.getIssueQty())); // issue_qty = issue_qty - qty ,
			ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(c++, o.getUpdateUser());
			ps.setString(c++, o.getPensItem());
			ps.setString(c++, o.getMaterialMaster());
			ps.setString(c++, o.getGroupCode());
			ps.setString(c++, o.getBarcode());
			ps.setString(c++, o.getWareHouse());
			
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
	
	public static void deleteModel(Connection conn,Onhand o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("Update");
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" DELETE PENSBI.PENSBME_STOCK_FINISHED \n");
			sql.append(" WHERE PENS_ITEM =? and material_Master = ? and group_code = ? and BARCODE =? and warehouse =? \n" );

			ps = conn.prepareStatement(sql.toString());
				
			ps.setString(1, o.getPensItem());
			ps.setString(2, o.getMaterialMaster());
			ps.setString(3, o.getGroupCode());
			ps.setString(4, o.getBarcode());
			ps.setString(5, o.getWareHouse());
			
			ps.executeUpdate();
			
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();ps=null;
			}
		}
	}
	
	public static int getQtyInBarcode(Connection conn,Onhand o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int qty = 0;
		int r = 1;
		try {
			sql.append("\n select count(*) as qty from PENSBI.PENSBME_PICK_BARCODE_ITEM i ");
			sql.append("\n where 1=1 and ( i.status is null or i.status ='' or i.status ='C')");
			if( !Utils.isNull(o.getBoxNo()).equals("")){
				sql.append("\n and i.box_no = '"+Utils.isNull(o.getBoxNo())+"'");
			}
			if( !Utils.isNull(o.getPensItem()).equals("")){
				sql.append("\n and i.pens_item = '"+Utils.isNull(o.getPensItem())+"'");
			}
			if( !Utils.isNull(o.getMaterialMaster()).equals("")){
				sql.append("\n and i.material_Master = '"+Utils.isNull(o.getMaterialMaster())+"'");
			}
			if( !Utils.isNull(o.getGroupCode()).equals("")){
				sql.append("\n and i.group_code = '"+Utils.isNull(o.getGroupCode())+"'");
			}
			sql.append("\n group by box_no,pens_item,material_Master,group_code ");
			
			logger.debug("sql:"+sql);
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			if(rst.next()) {
              qty = rst.getInt("qty");
			}//while

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return qty;
	}
	
    /**
     * 
     * @param dataPickStockList
     * @return ('1001','1oo2')
     */
	public static String getWhereCondPensItem_PickStock(List<PickStock> dataPickStockList){
		String sql = "";
		Map<String, String> pensItemMap = new HashMap<String, String>();
		try{
			if(dataPickStockList != null && dataPickStockList.size() >0){
				for(int i=0;i<dataPickStockList.size();i++){
					PickStock p = (PickStock)dataPickStockList.get(i);
					if(pensItemMap.get(p.getPensItem()) == null){
						sql +="'"+p.getPensItem()+"',";
						
						pensItemMap.put(p.getPensItem(), p.getPensItem());
					}
					
				}
				if(sql.length()>1){
					sql = "("+sql.substring(0,sql.length()-1)+")";
				}
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return sql;
	}
	

	/** Get pens_item by job_id) **/
	public static String getWhereCondPensItem_FromBarcodeByJobId(Connection conn,String jobId){
		String sqlWhere = "";
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		try{
			sql.append("\n select distinct i.pens_item from PENSBI.PENSBME_PICK_BARCODE_ITEM i   \n");
			sql.append("\n where 1=1 and i.status ='"+PickConstants.STATUS_CLOSE+"' \n");
			sql.append("\n and i.job_id = "+jobId+"");
		
			logger.debug("sql:"+sql.toString());
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()){
				sqlWhere +="'"+Utils.isNull(rst.getString("pens_item"))+"',";

			}
			if(sql.length()>1){
				sqlWhere = "("+sqlWhere.substring(0,sqlWhere.length()-1)+")";
			}
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return sqlWhere;
	}
	
	
	public static String getWhereCondBoxNo_PickStock(List<PickStock> dataPickStockList){
		String sql = "";
		Map<String, String> boxNoMap = new HashMap<String, String>();
		try{
			if(dataPickStockList != null && dataPickStockList.size() >0){
				for(int i=0;i<dataPickStockList.size();i++){
					PickStock p = (PickStock)dataPickStockList.get(i);
					if(boxNoMap.get(p.getBoxNo()) == null){
						sql +="'"+p.getBoxNo()+"',";
						
						boxNoMap.put(p.getBoxNo(), p.getBoxNo());
					}
					
				}
				if(sql.length()>1){
					sql = "("+sql.substring(0,sql.length()-1)+")";
				}
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return sql;
	}
	
	public static String getWhereCondPensItem_PickStock(Map<String, PickStock> dataSaveMapAll){
		String sql = "";
		Map<String, String> pensItemMap = new HashMap<String, String>();
		try{
			Iterator its =  dataSaveMapAll.keySet().iterator();
			while(its.hasNext()){
				String key = (String)its.next();
			    PickStock p = (PickStock)dataSaveMapAll.get(key);
				   
				if(pensItemMap.get(p.getPensItem()) == null){
					sql +="'"+p.getPensItem()+"',";
					
					pensItemMap.put(p.getPensItem(), p.getPensItem());
				}	
			}//while
			
			if(sql.length()>1){
				sql = "("+sql.substring(0,sql.length()-1)+")";
			}
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return sql;
	}
	
	public static String getWhereCondPensItem_PickStockType2(Map<String, String> dataSaveMapAll){
		String sql = "";
		Map<String, String> pensItemMap = new HashMap<String, String>();
		try{
			Iterator its =  dataSaveMapAll.keySet().iterator();
			while(its.hasNext()){
				String key = (String)its.next();
			    sql +="'"+key+"',";
			}//while
			
			if(sql.length()>1){
				sql = "("+sql.substring(0,sql.length()-1)+")";
			}
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return sql;
	}
	
	public static String getWhereCondBoxNo_PickStock(Map<String, PickStock> dataSaveMapAll){
		String sql = "";
		Map<String, String> boxNoMap = new HashMap<String, String>();
		try{
			Iterator its =  dataSaveMapAll.keySet().iterator();
			while(its.hasNext()){
				String key = (String)its.next();
			    PickStock p = (PickStock)dataSaveMapAll.get(key);
				   
				if(boxNoMap.get(p.getBoxNo()) == null){
					sql +="'"+p.getBoxNo()+"',";
					
					boxNoMap.put(p.getBoxNo(), p.getBoxNo());
				}	
			}//while
			
			if(sql.length()>1){
				sql = "("+sql.substring(0,sql.length()-1)+")";
			}
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
		return sql;
	}
}
