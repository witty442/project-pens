package com.isecinc.pens.web.stockmc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.Utils;
import com.pens.util.seq.SequenceProcessAll;

public class StockMCMasterItemDAO {
	protected static Logger logger = Logger.getLogger("PENS");
	
	public static int searchStockMCMasterItemListTotalRec(Connection conn,StockMCBean o) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int totalRec = 0;
		try {
			sql.append("\n select count(*) as c FROM ");
			sql.append("\n PENSBI.mc_item_cust H");
			sql.append("\n WHERE 1=1");
			 //GenWhereSQL
			sql.append(" "+genWhereCondSql(conn,o));
		
			logger.debug("sql:"+sql);

			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();

			while(rst.next()) {
				totalRec = rst.getInt("c");
			}//while

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
	public static List<StockMCBean> searchStockMCMasterItemList(Connection conn,StockMCBean o,boolean allRec,int currPage,int pageSize ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		StockMCBean h = null;
		List<StockMCBean> items = new ArrayList<StockMCBean>();
		try {
			sql.append("\n select M.* from (");
			sql.append("\n select A.* ,rownum as r__ from (");
			sql.append("\n select H.*");
			sql.append("\n ,(select m.customer_name from pensbi.mc_cust m where H.customer_code = m.customer_code) as customer_name ");
			sql.append("\n FROM PENSBI.mc_item_cust H");
			sql.append("\n WHERE 1=1");
			 //GenWhereSQL
			sql.append(" "+genWhereCondSql(conn,o));
		
			sql.append("\n    ORDER BY H.CUSTOMER_CODE,ITEM_PENS desc");
			sql.append("\n  )A ");
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
			   h = new StockMCBean();
			   h.setCustomerCode(Utils.isNull(rst.getString("customer_code")));
			   h.setCustomerName(Utils.isNull(rst.getString("customer_name")));
			   h.setProductCode(Utils.isNull(rst.getString("item_pens")));
			   h.setProductName(Utils.isNull(rst.getString("description")));
			   h.setBarcode(Utils.isNull(rst.getString("barcode")));
			   h.setItemCust(Utils.isNull(rst.getString("item_cust")));
			   h.setProductPackSize(Utils.isNull(rst.getString("packsize")));
			   h.setProductAge(Utils.isNull(rst.getString("product_age")));
			   h.setRetailPriceBF(Utils.decimalFormat(rst.getDouble("retail_price"), Utils.format_current_2_disgit));
			   h.setStartDate(DateUtil.stringValueChkNull(rst.getDate("start_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
			   h.setEndDate(DateUtil.stringValueChkNull(rst.getDate("end_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
			   h.setBrand(Utils.isNull(rst.getString("brand")));
			   h.setUom(Utils.isNull(rst.getString("uom")));
			   h.setStatus(Utils.isNull(rst.getString("status")));
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
	public StockMCBean searchStockMCMasterItem(StockMCBean o,boolean getItems) throws Exception {
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			return searchStockMCMasterItem(conn, o);
		}catch(Exception e){
			throw e;
		}finally{
			conn.close();
		}
	}
	public StockMCBean searchStockMCMasterItem(Connection conn,StockMCBean o) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		StockMCBean h = null;
		try {
			sql.append("\n select H.*");
			sql.append("\n ,(select M.customer_name from pensbi.mc_cust M ");
			sql.append("\n   where M.customer_code = H.customer_code ) as customer_name");
			sql.append("\n FROM PENSBI.mc_item_cust H");
			sql.append("\n WHERE 1=1");
			 //GenWhereSQL
			sql.append(" "+genWhereCondSql(conn,o));
			sql.append("\n  ORDER BY H.item_pens desc");
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			if(rst.next()) {
			   h = new StockMCBean();
			   h.setCustomerCode(Utils.isNull(rst.getString("customer_code")));
			   h.setCustomerName(Utils.isNull(rst.getString("customer_name")));
			   h.setProductCode(Utils.isNull(rst.getString("item_pens")));
			   h.setProductName(Utils.isNull(rst.getString("description")));
			   h.setBarcode(Utils.isNull(rst.getString("barcode")));
			   h.setItemCust(Utils.isNull(rst.getString("item_cust")));
			   h.setProductPackSize(Utils.isNull(rst.getString("packsize")));
			   h.setProductAge(Utils.isNull(rst.getString("product_age")));
			   h.setRetailPriceBF(Utils.decimalFormat(rst.getDouble("retail_price"), Utils.format_current_2_disgit));
			   h.setStartDate(DateUtil.stringValueChkNull(rst.getDate("start_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
			   h.setEndDate(DateUtil.stringValueChkNull(rst.getDate("end_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
			
			   h.setUom(Utils.isNull(rst.getString("uom")));
			   h.setBrand(Utils.isNull(rst.getString("brand")));
			   h.setStatus(Utils.isNull(rst.getString("status")));
			   if(Utils.isNull(h.getStatus()).equalsIgnoreCase("active")){
				   h.setStatusFlag("true");
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
		return h;
	}
	
	public static StringBuffer genWhereCondSql(Connection conn,StockMCBean o) throws Exception{
		StringBuffer sql = new StringBuffer("");
		if( !Utils.isNull(o.getCustomerCode()).equals("")){
			sql.append("\n and H.customer_code = '"+Utils.isNull(o.getCustomerCode())+"'");
		}
		if( !Utils.isNull(o.getBarcode()).equals("")){
			sql.append("\n and H.barcode = '"+Utils.isNull(o.getBarcode())+"'");
		}
		return sql;
	}
	
	public StockMCBean save(StockMCBean head) throws Exception {
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
	
			int r = updateStockMCMasterItem(conn , head);
			if(r==0){
				head = insertStockMCMasterItem(conn , head);
			}
			
			conn.commit();
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			conn.rollback();
			throw e;
		}finally{
			if(conn != null){
			   conn.close();conn=null;
			}
		}
		return head;
	}
	
	private StockMCBean insertStockMCMasterItem(Connection conn ,StockMCBean model) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		StringBuffer sql = new StringBuffer("");
		int index = 0;
		try {
			sql.append(" INSERT INTO PENSBI.MC_ITEM_CUST( \n");
			sql.append(" CUSTOMER_CODE, ITEM_PENS, ITEM_CUST,BARCODE,DESCRIPTION, \n");
			sql.append(" PACKSIZE,PRODUCT_AGE,RETAIL_PRICE,UOM,BRAND,STATUS) \n");
			sql.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?) \n");
			  
			//logger.debug("SQL:"+sql);
			//Gen ID running
			model.setId(SequenceProcessAll.getIns().getNextValue("MC_COUNTSTK_HEADER").intValue());

			ps = conn.prepareStatement(sql.toString());
			
			ps.setString(++index, Utils.isNull(model.getCustomerCode()));
			ps.setString(++index, Utils.isNull(model.getProductCode()));
			ps.setString(++index, Utils.isNull(model.getItemCust()));
			ps.setString(++index, Utils.isNull(model.getBarcode()));
			ps.setString(++index, Utils.isNull(model.getProductName()));
			ps.setString(++index, Utils.isNull(model.getProductPackSize()));
			ps.setString(++index, Utils.isNull(model.getProductAge()));
			ps.setDouble(++index, Utils.convertStrToDouble(model.getRetailPriceBF()));
			ps.setString(++index, Utils.isNull(model.getUom()));
			ps.setString(++index, Utils.isNull(model.getBrand()));
			ps.setString(++index, !Utils.isNull(model.getStatusFlag()).equalsIgnoreCase("")?"ACTIVE":"");
			int ch = ps.executeUpdate();
			result = ch>0?true:false;
			
			logger.debug("ins:"+ch);
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
		}
		return model;
	}
	
	private int updateStockMCMasterItem(Connection conn ,StockMCBean model) throws Exception {
		int r=0;
		PreparedStatement ps = null;
		StringBuffer sql = new StringBuffer("");
		try {
			sql.append(" UPDATE PENSBI.mc_item_cust \n");
			sql.append(" SET ITEM_PENS =? , ITEM_CUST =? \n");
			sql.append(" ,DESCRIPTION =? , PACKSIZE =? \n");
			sql.append(" ,PRODUCT_AGE =? , RETAIL_PRICE =? \n");
			sql.append(" ,UOM =? , BRAND =? \n");
			sql.append(" ,STATUS =?  \n");
			sql.append(" where customer_code = ? and barcode =? \n");
			  
			logger.debug("SQL:"+sql);
			
			int index = 0;
			ps = conn.prepareStatement(sql.toString());
			
			ps.setString(++index, Utils.isNull(model.getProductCode()));
			ps.setString(++index, Utils.isNull(model.getItemCust()));
			ps.setString(++index, Utils.isNull(model.getProductName()));
			ps.setString(++index, Utils.isNull(model.getProductPackSize()));
			ps.setString(++index, Utils.isNull(model.getProductAge()));
			ps.setDouble(++index, Utils.convertStrToDouble(model.getRetailPriceBF()));
			ps.setString(++index, Utils.isNull(model.getUom()));
			ps.setString(++index, Utils.isNull(model.getBrand()));
			ps.setString(++index, !Utils.isNull(model.getStatusFlag()).equalsIgnoreCase("")?"ACTIVE":"");
			
			ps.setString(++index, Utils.isNull(model.getCustomerCode()));
			ps.setString(++index, Utils.isNull(model.getBarcode()));
			
			r = ps.executeUpdate();

			logger.debug("update:"+r);
		} catch (Exception ex) {
			throw ex;
		} finally {
			if(ps != null){
				ps.close();ps = null;
			}
		}
		return r;
	}
	
	
}
