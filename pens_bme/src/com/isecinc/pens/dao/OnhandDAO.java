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

import com.isecinc.pens.bean.Barcode;
import com.isecinc.pens.bean.Job;
import com.isecinc.pens.bean.Onhand;
import com.isecinc.pens.bean.PickStock;
import com.isecinc.pens.dao.constants.PickConstants;
import com.pens.util.DBConnection;
import com.pens.util.Utils;
import com.pens.util.helper.SequenceProcess;

public class OnhandDAO {
	
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

	public OnhandDAO() {
		// TODO Auto-generated constructor stub
	}
	
	//Action : open page PickStock
	public static void processBanlanceOnhandFromBarcode(String userName) throws Exception{
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
			
			Date startDate =new Date();
			List<Onhand> onhandItemList = searchBarcodeItemInStock(conn);
			logger.debug("searchBarcodeItemInStock>>Total Time:"+(new Date().getTime()-startDate.getTime()));
			 
			startDate =new Date();
			if(onhandItemList != null && onhandItemList.size() >0){
				for(int i=0;i<onhandItemList.size();i++){
					Onhand itemOnhand = (Onhand)onhandItemList.get(i);
					itemOnhand.setCreateUser(userName);
					itemOnhand.setUpdateUser(userName);
					
					int rUpdate = updateModel(conn, itemOnhand);
					if(rUpdate==0){
						insertModel(conn, itemOnhand);
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
	
	//After Action :Close Job
	public static void processBanlanceOnhandFromBarcodeByJobId(Connection conn,String userName,Job j) throws Exception{
		try{
			/*logger.debug("***** Start processBanlanceOnhandFromBarcodeByJobId jobId["+j.getJobId()+"] *****");
			
			List<Onhand> onhandItemList = searchBarcodeItemInStock(conn,j.getJobId());
		
			if(onhandItemList != null && onhandItemList.size() >0){
				for(int i=0;i<onhandItemList.size();i++){
					Onhand itemOnhand = (Onhand)onhandItemList.get(i);
					itemOnhand.setCreateUser(userName);
					itemOnhand.setUpdateUser(userName);
					
					int rUpdate = updateModel(conn, itemOnhand);
					if(rUpdate==0){
						insertModel(conn, itemOnhand);
					}
				}
			}
		
			logger.debug("***** End processBanlanceOnhandFromBarcodeByJobId jobId["+j.getJobId()+"] *****");*/
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			
		}
	}
	
	//After Action : 1Open Req Return 
	public static void processBanlanceOnhandFromBarcodeByBoxNo(Connection conn,String userName,String barcodeStatus,String whereInBoxNoSql) throws Exception{
		try{
			/*logger.debug("***** Start processBanlanceOnhandFromBarcodeByBoxNo whereInBoxNoSql["+whereInBoxNoSql+"] *****");
			
			List<Onhand> onhandItemList = searchBarcodeItemInStockByBoxNo(conn,barcodeStatus,whereInBoxNoSql);
		
			if(onhandItemList != null && onhandItemList.size() >0){
				for(int i=0;i<onhandItemList.size();i++){
					Onhand itemOnhand = (Onhand)onhandItemList.get(i);
					itemOnhand.setCreateUser(userName);
					itemOnhand.setUpdateUser(userName);
					
					int rUpdate = updateModel(conn, itemOnhand);
					if(rUpdate==0){
						insertModel(conn, itemOnhand);
					}
				}
			}
		
			logger.debug("***** End processBanlanceOnhandFromBarcodeByBoxNo whereInBoxNoSql["+whereInBoxNoSql+"] *****");*/
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}finally{
			
		}
	}
	
	// After Pick Stock Update Onhand By StockPick
	// Action :after pick stock :save, open, cancel
	public static void processBanlanceOnhandFromStockPick(Connection conn,String userName,String whereCondPensItem) throws Exception{
		try{
			logger.debug("***** Start processBanlanceOnhandFromStockPick *****");
			
			List<Onhand> onhandItemList = searchOnhandItemInPickOnhand(conn,whereCondPensItem);
			if(onhandItemList != null && onhandItemList.size() >0){
				for(int i=0;i<onhandItemList.size();i++){
					Onhand itemOnhand = (Onhand)onhandItemList.get(i);
					itemOnhand.setCreateUser(userName);
					itemOnhand.setUpdateUser(userName);
					
					int onhandQty = getQtyInBarcode(conn, itemOnhand);
					logger.debug("onhandQty:"+onhandQty);
					itemOnhand.setQty(String.valueOf(onhandQty));
					
					int rUpdate = updateModel(conn, itemOnhand);
					logger.debug("Update:"+rUpdate);
				}
			}
			logger.debug("***** END processBanlanceOnhandFromStockPick *****");
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		 
		}finally{
			
		}
	}
	
	/**
	 * 
	 * @param userName
	 * @throws Exception
	 * Run after save
	 */
	public static void processUpdateBarcodeHeadStatusClose(Connection conn,String userName,String whereCondBoxNo) throws Exception{
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		try{

			sql.append(" select i.* from PENSBI.PENSBME_PICK_BARCODE i   \n");
			sql.append(" where 1=1 and i.status ='"+PickConstants.STATUS_CLOSE+"'  \n");
			if( !Utils.isNull(whereCondBoxNo).equals("")){
				sql.append("and i.box_no in "+whereCondBoxNo +" \n");
			}
			logger.debug("sql:"+sql);
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			  boolean isIssueAll = isBoxNoIsIssueAll(conn,rst.getString("job_id"),rst.getString("box_no"));
			  if(isIssueAll){
				  Barcode b = new Barcode();
				  b.setJobId(rst.getString("job_id"));
				  b.setBoxNo(rst.getString("box_no"));
				  b.setStatus(PickConstants.STATUS_ISSUED);
				  
				  BarcodeDAO.updateBarcodeHeadStatusModelByPK(conn, b);
			  }
			}//while
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		    
		}finally{
			if(ps != null){
			   ps.close();
			}
			if(rst != null){
			   rst.close();
			}
		}
	}
	
	/**
	 * Run after cancel
	 * @param userName
	 * @throws Exception
	 * 
	 * Case box is ISSUE and Cancel Pick  update to Close
	 */
	public static void processUpdateBarcodeHeadStatusIssue(Connection conn,String userName,String whereCondBoxNo) throws Exception{
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		try{
			sql.append(" select i.* from PENSBI.PENSBME_PICK_BARCODE i   \n");
			sql.append(" where 1=1 and i.status ='"+PickConstants.STATUS_ISSUED+"'  \n");
			
			if( !Utils.isNull(whereCondBoxNo).equals("")){
			  sql.append(" and i.box_no in "+whereCondBoxNo +" \n");
			}
			
			logger.debug("sql:"+sql);
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			  boolean isIssueAll = isBoxNoIsOneCloseItem(conn,rst.getString("job_id"),rst.getString("box_no"));
			  logger.debug("BoxNo["+rst.getString("box_no")+"]isIssueAll="+isIssueAll);
			  if(isIssueAll){
				  Barcode b = new Barcode();
				  b.setJobId(rst.getString("job_id"));
				  b.setBoxNo(rst.getString("box_no"));
				  b.setStatus(PickConstants.STATUS_CLOSE); 
				  
				  BarcodeDAO.updateBarcodeHeadStatusModelByPK(conn, b);
			  }
			}//while
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
			   ps.close();
			}
			if(rst != null){
			   rst.close();
			}
		}
	}
	
	public static boolean isBoxNoIsIssueAll(Connection conn,String jobId,String boxNo ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		boolean r = true;
		try {
			sql.append("\n select count(*) as c from PENSBI.PENSBME_PICK_BARCODE_ITEM i   \n");
			sql.append("\n where 1=1  and i.status ='"+PickConstants.STATUS_CLOSE+"' \n");
			sql.append("\n and i.job_id ="+jobId+" and i.box_no ='"+boxNo+"' ");
			
			//logger.debug("sql:"+sql);
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			if(rst.next()) {
			  int c = rst.getInt("c");logger.debug("barcode item c:"+c);
			  if(c > 0){
                r = false;
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
		return r;
	}
	
	public static boolean isBoxNoIsOneCloseItem(Connection conn,String jobId,String boxNo ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		boolean r = false;
		try {
			sql.append("\n select count(*) as c from PENSBI.PENSBME_PICK_BARCODE_ITEM i   \n");
			sql.append("\n where 1=1  and i.status ='"+PickConstants.STATUS_CLOSE+"' \n");
			sql.append("\n and i.job_id ="+jobId+" and i.box_no ='"+boxNo+"'");
			
			//logger.debug("sql:"+sql);
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			if(rst.next()) {
              r = true;
			}
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
	
	
	
	public static boolean validateQtyInStock(Connection conn,PickStock o) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
	    boolean valid = false;
		try {
			sql.append("\n select sum(qty) as qty from (");
				sql.append("\n select qty ");
				sql.append("\n from PENSBI.PENSBME_PICK_ONHAND  ");
				sql.append("\n where 1=1 ");
				sql.append("\n and PENS_ITEM ='"+o.getPensItem()+"' " +
						   "\n and material_Master = '"+o.getMaterialMaster()+"' " +
						   "\n and group_code ='"+o.getGroupCode()+"'" );
	
				sql.append("\n union all");
				
				sql.append("\n select count(*) as qty  ");
				sql.append("\n from PENSBI.PENSBME_PICK_STOCK_I  ");
				sql.append("\n where 1=1 ");
				sql.append("\n and issue_req_no ='"+o.getIssueReqNo()+"'");
				sql.append("\n and PENS_ITEM ='"+o.getPensItem()+"' " +
						   "\n and material_Master = '"+o.getMaterialMaster()+"' " +
						   "\n and group_code ='"+o.getGroupCode()+"'" );
			sql.append("\n ) ");
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			if(rst.next()) {
              int onhandQty = rst.getInt("qty");
              int qty = Utils.convertStrToInt(o.getQty());
              logger.debug("validate pensItem["+o.getPensItem()+"]onhandQty["+onhandQty+"]qty["+qty+"]");
              
              if(qty <= onhandQty){
            	  valid = true;
              }
			}else{
		       logger.debug("validate pensItem["+o.getPensItem()+"]onhabdQty[0]qty["+o.getQty()+"]");
			}
			
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return valid;
	}
	
	public static List<Onhand> searchBarcodeItemInStock(Connection conn) throws Exception {
		return searchBarcodeItemInStockModel(conn,"");
	}
	
	public static List<Onhand> searchBarcodeItemInStock(Connection conn,String jobId) throws Exception {
		return searchBarcodeItemInStockModel(conn,jobId);
	}
	
	
	public static List<Onhand> searchBarcodeItemInStockModel(Connection conn,String jobId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Onhand h = null;
		int r = 1;
        List<Onhand> items = new ArrayList<Onhand>();
		try {

			sql.append("\n select  i.box_no,pens_item ,MATERIAL_MASTER,group_code,count(*) as qty " +
					"\n from PENSBI.PENSBME_PICK_BARCODE h ,PENSBI.PENSBME_PICK_BARCODE_ITEM i   ");
			sql.append("\n where 1=1  and (i.status ='' or i.status is null or i.status ='C' ) ");
			sql.append("\n and h.job_id = i.job_id and h.box_no = i.box_no ");
			sql.append("\n and h.status ='"+PickConstants.STATUS_CLOSE+"' ");
			if( !Utils.isNull(jobId).equals("")){
				sql.append("\n and h.job_id ="+jobId);
			}
			
			sql.append("\n group by i.box_no,pens_item,MATERIAL_MASTER,group_code ");
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			  
			   h = new Onhand();
			   h.setBoxNo(Utils.isNull(rst.getString("box_no")));
			   h.setMaterialMaster(rst.getString("MATERIAL_MASTER"));
			   h.setGroupCode(rst.getString("group_code"));
			   h.setPensItem(rst.getString("pens_item"));
			   h.setQty(Utils.decimalFormat(rst.getInt("qty"),Utils.format_current_no_disgit));
				
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
	
	public static List<Onhand> searchBarcodeItemInStockByBoxNo(Connection conn,String barcodeStatus,String whereInBoxNoSql) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Onhand h = null;
		int r = 1;
        List<Onhand> items = new ArrayList<Onhand>();
		try {
			sql.append("\n select  i.box_no,pens_item ,MATERIAL_MASTER,group_code,count(*) as qty " +
					"\n from PENSBI.PENSBME_PICK_BARCODE h ,PENSBI.PENSBME_PICK_BARCODE_ITEM i   ");
			sql.append("\n where 1=1 ");
			sql.append("\n and h.job_id = i.job_id and h.box_no = i.box_no ");
			sql.append("\n and i.status ='"+barcodeStatus+"' ");
			
			if( !Utils.isNull(whereInBoxNoSql).equals("")){
				sql.append("\n and i.box_no in ("+whereInBoxNoSql+")");
			}
			
			sql.append("\n group by i.box_no,pens_item,MATERIAL_MASTER,group_code ");
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			  
			   h = new Onhand();
			   h.setBoxNo(Utils.isNull(rst.getString("box_no")));
			   h.setMaterialMaster(rst.getString("MATERIAL_MASTER"));
			   h.setGroupCode(rst.getString("group_code"));
			   h.setPensItem(rst.getString("pens_item"));
			   h.setQty(Utils.decimalFormat(rst.getInt("qty"),Utils.format_current_no_disgit));
				
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
	 * @param whereCondPensItem :('1001','11002')
	 * @return
	 * @throws Exception
	 */
	public static List<Onhand> searchOnhandItemInPickOnhand(Connection conn,String whereCondPensItem) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Onhand h = null;
		int r = 1;
        List<Onhand> items = new ArrayList<Onhand>();
		try {
			sql.append("\n select * from PENSBI.PENSBME_PICK_ONHAND where 1=1");
			if( !Utils.isNull(whereCondPensItem).equals("")){
			   sql.append("\n and pens_item in "+whereCondPensItem);
			}
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   h = new Onhand();
			   h.setBoxNo(Utils.isNull(rst.getString("box_no")));
			   h.setMaterialMaster(rst.getString("MATERIAL_MASTER"));
			   h.setGroupCode(rst.getString("group_code"));
			   h.setPensItem(rst.getString("pens_item"));
			   h.setQty(Utils.decimalFormat(rst.getInt("qty"),Utils.format_current_no_disgit));
				
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
	
	private static void insertModel(Connection conn,Onhand o) throws Exception{
		PreparedStatement ps = null;
		logger.debug("Insert");
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" INSERT INTO PENSBI.PENSBME_PICK_ONHAND \n");
			sql.append(" (BOX_NO,PENS_ITEM,material_Master,group_code,QTY ,CREATE_DATE,CREATE_USER)  \n");
		    sql.append(" VALUES (?,?, ?, ? , ? ,? ,?) \n");
			
			ps = conn.prepareStatement(sql.toString());
				
			int c =1;
			ps.setString(c++, o.getBoxNo());
			ps.setString(c++, o.getPensItem());
			ps.setString(c++, o.getMaterialMaster());
			ps.setString(c++, o.getGroupCode());
			ps.setInt(c++, Utils.convertStrToInt(o.getQty()));
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

	public static int updateModel(Connection conn,Onhand o) throws Exception{
		PreparedStatement ps = null;
		int r = 0;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" UPDATE PENSBI.PENSBME_PICK_ONHAND SET QTY = ?,UPDATE_DATE=?,UPDATE_USER = ? \n");
			sql.append(" WHERE PENS_ITEM =?  and material_Master = ? and group_code = ? and box_no = ? \n" );

			ps = conn.prepareStatement(sql.toString());
				
			ps.setInt(1, Utils.convertStrToInt(o.getQty()));
			ps.setTimestamp(2, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(3, o.getUpdateUser());
			ps.setString(4, o.getPensItem());
			ps.setString(5, o.getMaterialMaster());
			ps.setString(6, o.getGroupCode());
			ps.setString(7, o.getBoxNo());
			
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
			sql.append(" DELETE PENSBI.PENSBME_PICK_ONHAND \n");
			sql.append(" WHERE PENS_ITEM =? and material_Master = ? and group_code = ? AND BOX_NO = ? \n" );

			ps = conn.prepareStatement(sql.toString());
				
			ps.setString(1, o.getPensItem());
			ps.setString(2, o.getMaterialMaster());
			ps.setString(3, o.getGroupCode());
			ps.setString(4, o.getGroupCode());
			
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
