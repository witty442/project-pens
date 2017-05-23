package com.isecinc.pens.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.bean.MoveStockWarehouseBean;
import com.isecinc.pens.dao.constants.PickConstants;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.web.popup.PopupForm;

public class MoveStockWarehoseDAO extends PickConstants{
	
	protected static Logger logger = Logger.getLogger("PENS");
	protected static SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Utils.local_th);

	
	public static int searchTotalRowMoveStockHis(MoveStockWarehouseBean bean) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int totalRow = 0;
		Connection conn = null;
		try {
		    conn = DBConnection.getInstance().getConnection();
		    
			sql.append("\n SELECT count(*) as c FROM PENSBME_MOVE_STOCK_FINISH_HIS");
			sql.append("\n WHERE 1=1");
			if( !Utils.isNull(bean.getDateFrom()).equals("") && !Utils.isNull(bean.getDateTo()).equals("")){
			    Date dateFrom = Utils.parse(bean.getDateFrom(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			    String dateFromStr = Utils.stringValue(dateFrom, Utils.DD_MM_YYYY_WITH_SLASH);
			   
			    Date dateTo = Utils.parse(bean.getDateTo(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			    String dateToStr = Utils.stringValue(dateTo, Utils.DD_MM_YYYY_WITH_SLASH);
			   
			    sql.append("\n and create_date >= to_date('"+dateFromStr+"','dd/mm/yyyy')");
			    sql.append("\n and create_date <= to_date('"+dateToStr+"','dd/mm/yyyy')");
			}
			if( !Utils.isNull(bean.getMaterialMaster()).equals("")){
				 sql.append("\n and material_master ='"+Utils.isNull(bean.getMaterialMaster())+"'");
			}
			if( !Utils.isNull(bean.getGroupCodeSearch()).equals("")){
				 sql.append("\n and substr(MATERIAL_MASTER,0,6) ='"+Utils.isNull(bean.getGroupCodeSearch())+"'");
			}
			if( !Utils.isNull(bean.getWarehouseFrom()).equals("")){
				sql.append("\n and warehouse_from = '"+Utils.isNull(bean.getWarehouseFrom())+"'");
			}
			if( !Utils.isNull(bean.getWarehouseTo()).equals("")){
				sql.append("\n and warehouse_to = '"+Utils.isNull(bean.getWarehouseTo())+"'");
			}
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			if(rst.next()) {
				totalRow = rst.getInt("c");
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
		return totalRow;
	}
	
	public static List<MoveStockWarehouseBean> searchMoveStockHis(MoveStockWarehouseBean bean,int pageNumber,int pageSize) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		List<MoveStockWarehouseBean> pos = new ArrayList<MoveStockWarehouseBean>();
		Connection conn = null;
		try {
		    conn = DBConnection.getInstance().getConnection();
		    sql.append("\n SELECT * FROM( \n");
			sql.append("\n SELECT a.*, rownum r__ \n");
			sql.append("\n FROM ( \n");
				sql.append("\n SELECT * FROM PENSBME_MOVE_STOCK_FINISH_HIS");
				sql.append("\n WHERE 1=1");
				if( !Utils.isNull(bean.getDateFrom()).equals("") && !Utils.isNull(bean.getDateTo()).equals("")){
				    Date dateFrom = Utils.parse(bean.getDateFrom(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				    String dateFromStr = Utils.stringValue(dateFrom, Utils.DD_MM_YYYY_WITH_SLASH);
				   
				    Date dateTo = Utils.parse(bean.getDateTo(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
				    String dateToStr = Utils.stringValue(dateTo, Utils.DD_MM_YYYY_WITH_SLASH);
				   
				    sql.append("\n and create_date >= to_date('"+dateFromStr+"','dd/mm/yyyy')");
				    sql.append("\n and create_date <= to_date('"+dateToStr+"','dd/mm/yyyy')");
				}
				if( !Utils.isNull(bean.getMaterialMaster()).equals("")){
					 sql.append("\n and material_master ='"+Utils.isNull(bean.getMaterialMaster())+"'");
				}
				if( !Utils.isNull(bean.getGroupCodeSearch()).equals("")){
					 sql.append("\n and substr(MATERIAL_MASTER,0,6) ='"+Utils.isNull(bean.getGroupCodeSearch())+"'");
				}
				if( !Utils.isNull(bean.getWarehouseFrom()).equals("")){
					sql.append("\n and warehouse_from = '"+Utils.isNull(bean.getWarehouseFrom())+"'");
				}
				if( !Utils.isNull(bean.getWarehouseTo()).equals("")){
					sql.append("\n and warehouse_to = '"+Utils.isNull(bean.getWarehouseTo())+"'");
				}
				sql.append("\n  ORDER BY CREATE_DATE DESC ");
				sql.append("\n ) a  ");
				sql.append("\n  WHERE rownum < (("+pageNumber+" * "+pageSize+") + 1 )  ");
				sql.append("\n )  ");
				sql.append("\n WHERE r__ >= ((("+pageNumber+"-1) * "+pageSize+") + 1)  ");
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
				MoveStockWarehouseBean item = new MoveStockWarehouseBean();
				item.setCreateDate(Utils.stringValue(rst.getDate("CREATE_DATE"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
				item.setWarehouseFrom(rst.getString("WAREHOUSE_FROM"));
				item.setWarehouseTo(rst.getString("WAREHOUSE_TO"));
		
				item.setMaterialMaster(Utils.isNull(rst.getString("MATERIAL_MASTER")));
				item.setTransferQty(rst.getInt("TRANSFER_QTY")+"");
				
				pos.add(item);
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
		return pos;
	}
	
	public static MoveStockWarehouseBean searchMoveStock(MoveStockWarehouseBean o ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		Connection conn = null;
		MoveStockWarehouseBean h = null;
		List<MoveStockWarehouseBean> items = new ArrayList<MoveStockWarehouseBean>();
		int no=1;
		int totalOnhandQty = 0;
		try {
			sql.append("\n  SELECT M.*  FROM( ");
			sql.append("\n   SELECT a.*, rownum r__ FROM (");
			sql.append("\n   SELECT M2.* FROM (");
		    	sql.append("\n   SELECT M.BARCODE,M.MATERIAL_MASTER,M.group_code,NVL(SUM(onhand_qty),0) as onhand_qty,0 as qty FROM ( ");
				sql.append("\n  	select BARCODE,MATERIAL_MASTER,group_code,(nvl(onhand_qty,0)-nvl(issue_qty,0)) as onhand_qty  ");
				sql.append("\n  	from PENSBME_STOCK_FINISHED ");
				sql.append("\n  	where 1=1   ");
				sql.append("\n      and warehouse ='"+o.getWarehouseFrom()+"'");
				if( !Utils.isNull(o.getGroupCodeSearch()).equals("")){
			    sql.append("\n      and group_code ='"+o.getGroupCodeSearch()+"'");
				}
				sql.append("\n      UNION ALL ");
				 // substract from Stock issue status = O(Open) POST ,BEF
				sql.append("\n 		SELECT BARCODE,MATERIAL_MASTER,group_code ,(-1* nvl(req_qty,0) ) as onhand_qty ");
				sql.append("\n 		FROM PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i  ");
				sql.append("\n 		WHERE 1=1  ");
				sql.append("\n      and h.warehouse ='"+o.getWarehouseFrom()+"'");
				sql.append("\n 		AND h.issue_req_no = i.issue_req_no ");
				sql.append("\n 		AND h.status in('"+STATUS_OPEN+"','"+STATUS_POST+"','"+STATUS_BEF+"')");
				if( !Utils.isNull(o.getGroupCodeSearch()).equals("")){
				sql.append("\n      and i.group_code ='"+o.getGroupCodeSearch()+"'");
				}
				sql.append("\n    )M  ");
				sql.append("\n    GROUP BY M.group_code,M.MATERIAL_MASTER,M.BARCODE");
				sql.append("\n  )M2  WHERE M2.onhand_qty > 0");
				sql.append("\n  ORDER BY M2.group_code,M2.MATERIAL_MASTER,M2.BARCODE");
			sql.append("\n    )a  ");
			sql.append("\n  )M  ");
		
			logger.debug("sql:"+sql);
			
			conn = DBConnection.getInstance().getConnection();
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
			   h = new MoveStockWarehouseBean();

			   h.setBarcode(Utils.isNull(rst.getString("BARCODE")));
			   h.setMaterialMaster(rst.getString("MATERIAL_MASTER"));
			   h.setGroupCode(rst.getString("group_code"));
			   int onhandQty = rst.getInt("onhand_qty");
			   h.setOnhandQty(Utils.decimalFormat(onhandQty,Utils.format_current_no_disgit));

			   //sum total qty
			   totalOnhandQty +=onhandQty;
			   
			   items.add(h);

			   no++;
			}//while

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
	
	public static int delStockFinishFromWarehouse(Connection conn,MoveStockWarehouseBean o) throws Exception{
		PreparedStatement ps = null;
		int r = 0;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" UPDATE PENSBME_STOCK_FINISHED SET ONHAND_QTY =(ONHAND_QTY - ?),UPDATE_DATE=?,UPDATE_USER = ? \n");
			sql.append(" WHERE  material_Master = ?  and warehouse=? and pens_item = ?\n" );

			ps = conn.prepareStatement(sql.toString());
				
			ps.setInt(1, Utils.convertStrToInt(o.getTransferQty()));
			ps.setTimestamp(2, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(3, o.getUpdateUser());
			ps.setString(4, o.getMaterialMaster());
			ps.setString(5, o.getWarehouseFrom());
			ps.setString(6, o.getPensItem());
			
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
	
	public static void processUpdateStockFinishToWarehouse(Connection conn,MoveStockWarehouseBean o) throws Exception{
		int update= 0;
		try{
			update = updateStockFinishToWarehouse(conn,o);
			if(update==0){
				insertStockFinishToWarehouse(conn,o);
			}
			
		}catch(Exception e){
			throw e;
		}
	}
	public static int updateStockFinishToWarehouse(Connection conn,MoveStockWarehouseBean o) throws Exception{
		PreparedStatement ps = null;
		int r = 0;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" UPDATE PENSBME_STOCK_FINISHED SET ONHAND_QTY =(ONHAND_QTY + ?),UPDATE_DATE=?,UPDATE_USER = ? \n");
			sql.append(" WHERE  material_Master = ?  and warehouse=? and pens_item  =?\n" );

			ps = conn.prepareStatement(sql.toString());
				
			ps.setInt(1, Utils.convertStrToInt(o.getTransferQty()));
			ps.setTimestamp(2, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(3, o.getUpdateUser());
			ps.setString(4, o.getMaterialMaster());
			ps.setString(5, o.getWarehouseTo());
			ps.setString(6, o.getPensItem());
			
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
	
	public static int insertStockFinishToWarehouse(Connection conn,MoveStockWarehouseBean o) throws Exception{
		PreparedStatement ps = null;
		int r = 0;
		int index = 1;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" INSERT INTO PENSBME_STOCK_FINISHED \n");
			sql.append("( PENS_ITEM, \n");
			sql.append("  MATERIAL_MASTER,  \n");
			sql.append("  BARCODE,  \n");
			sql.append("  GROUP_CODE, \n"); 
			sql.append("  ONHAND_QTY, \n"); 
			sql.append("  CREATE_DATE, \n"); 
			sql.append("  CREATE_USER, \n");
			sql.append("  WAREHOUSE \n");
			sql.append(") \n");
			
			sql.append("SELECT \n");
			sql.append("  PENS_ITEM, \n");
			sql.append("  MATERIAL_MASTER,  \n");
			sql.append("  BARCODE,  \n");
			sql.append("  GROUP_CODE, \n"); 
			sql.append("  "+o.getTransferQty()+" as ONHAND_QTY, \n"); 
			sql.append("  sysdate as CREATE_DATE, \n"); 
			sql.append("  '"+o.getUpdateUser()+"' as CREATE_USER, \n");
			sql.append("  '"+o.getWarehouseTo()+"' as WAREHOUSE \n");
			sql.append("  FROM  PENSBME_STOCK_FINISHED \n");
			sql.append("WHERE  material_Master = ?  and warehouse=? and pens_item = ? \n" );

			logger.debug("sql:"+sql.toString());
			ps = conn.prepareStatement(sql.toString());

			ps.setString(index++, o.getMaterialMaster());
			ps.setString(index++, o.getWarehouseFrom());
			ps.setString(index++, o.getPensItem());
			
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
	
	public static List<PopupForm>  getPensItemListStockFinish(String warehouse,String mat) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		List<PopupForm> pos = new ArrayList<PopupForm>();
		Connection conn = null;
		try {
		    conn = DBConnection.getInstance().getConnection();
		    
			sql.append("\n    SELECT M.material_master,pens_item, sum(M.onhand_qty) as onhand_qty FROM( ");
			sql.append("\n        SELECT material_master,pens_item ,(nvl(sum(onhand_qty),0)-nvl(sum(issue_qty),0)) as onhand_qty ");
			sql.append("\n  	  from PENSBME_STOCK_FINISHED WHERE 1=1 ");
			sql.append("\n 		  AND material_master ='"+mat+"'");
			sql.append("\n 	      AND warehouse ='"+warehouse+"'");
			sql.append("\n  	  GROUP BY material_master ,pens_item ");
			
			/*sql.append("\n         UNION ALL ");
			
			sql.append("\n 		   SELECT material_master,( (-1) * sum(req_qty) )as onhand_qty");
			sql.append("\n 		   FROM PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i  ");
			sql.append("\n 		   WHERE 1=1  ");
			sql.append("\n 	       AND h.warehouse ='"+p.getWarehouseFrom()+"'");
			sql.append("\n 		   AND h.issue_req_no = i.issue_req_no ");
			sql.append("\n 		   AND h.status in('"+STATUS_OPEN+"','"+STATUS_POST+"')");
			sql.append("\n 		   AND i.material_master = '"+p.getMaterialMaster()+"'");
			sql.append("\n  	   GROUP BY material_master ");*/
			
			sql.append("\n  )M ");
			sql.append("\n  GROUP BY M.material_master ,M.pens_item");
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
				PopupForm item = new PopupForm();
			    item.setMat(Utils.isNull(rst.getString("material_master")));
			    item.setPensItem(Utils.isNull(rst.getString("pens_item")));
			    item.setQty(rst.getInt("onhand_qty")+"");
				pos.add(item);
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
		return pos;
	}
	
	public static MoveStockWarehouseBean canMoveStockFinish(Connection conn,MoveStockWarehouseBean p) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		MoveStockWarehouseBean r = null;
		try {
		
			sql.append("\n       SELECT M.material_master, sum(M.onhand_qty) as onhand_qty FROM( ");
			sql.append("\n         SELECT material_master ,(nvl(sum(onhand_qty),0)-nvl(sum(issue_qty),0)) as onhand_qty ");
			sql.append("\n  	   from PENSBME_STOCK_FINISHED WHERE 1=1 ");
			sql.append("\n 		   AND material_master ='"+p.getMaterialMaster()+"'");
			sql.append("\n 	       AND warehouse ='"+p.getWarehouseFrom()+"'");
			sql.append("\n 	       AND pens_item ='"+p.getPensItem()+"'");
			sql.append("\n  	   GROUP BY material_master ");
			
			/*sql.append("\n         UNION ALL ");
			
			sql.append("\n 		   SELECT material_master,( (-1) * sum(req_qty) )as onhand_qty");
			sql.append("\n 		   FROM PENSBME_STOCK_ISSUE h, PENSBME_STOCK_ISSUE_ITEM i  ");
			sql.append("\n 		   WHERE 1=1  ");
			sql.append("\n 	       AND h.warehouse ='"+p.getWarehouseFrom()+"'");
			sql.append("\n 		   AND h.issue_req_no = i.issue_req_no ");
			sql.append("\n 		   AND h.status in('"+STATUS_OPEN+"','"+STATUS_POST+"')");
			sql.append("\n 		   AND i.material_master = '"+p.getMaterialMaster()+"'");
			sql.append("\n  	   GROUP BY material_master ");*/
			
			sql.append("\n  )M ");
			sql.append("\n  GROUP BY M.material_master");
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			if(rst.next()) {
				r = new MoveStockWarehouseBean();
			    r.setOnhandQty(rst.getString("onhand_qty"));
			   
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
	
	public static void insertMoveStockFinishHistory(Connection conn,MoveStockWarehouseBean o) throws Exception{
		PreparedStatement ps = null; 
		int c =1;
		logger.debug("insertMoveStockFinishHistory");
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append(" INSERT INTO PENSBME_MOVE_STOCK_FINISH_HIS \n");
			sql.append(" (warehouse_from,warehouse_to,MATERIAL_MASTER,create_date,create_user,transfer_qty,pens_item) \n");
		    sql.append(" values(?,?,?,?,?,?,?) \n");
			
			ps = conn.prepareStatement(sql.toString());
			
			ps.setString(c++, o.getWarehouseFrom());
			ps.setString(c++, o.getWarehouseTo());
			ps.setString(c++, o.getMaterialMaster());
			ps.setTimestamp(c++, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(c++, o.getUpdateUser());
			ps.setString(c++, o.getTransferQty());
			ps.setString(c++, o.getPensItem());
			
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
