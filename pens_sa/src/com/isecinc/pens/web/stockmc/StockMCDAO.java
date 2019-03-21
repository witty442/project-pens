package com.isecinc.pens.web.stockmc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.pens.process.SequenceProcessAll;

import util.DBConnection;
import util.Utils;

public class StockMCDAO {
	protected static Logger logger = Logger.getLogger("PENS");
	
	public static int searchStockMCListTotalRec(Connection conn,StockMCBean o) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int totalRec = 0;
		try {
			sql.append("\n select count(*) as c FROM ");
			sql.append("\n PENSBI.MC_COUNTSTK_HEADER H");
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
	public static List<StockMCBean> searchStockMCList(Connection conn,StockMCBean o,boolean allRec,int currPage,int pageSize ) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		StockMCBean h = null;
		List<StockMCBean> items = new ArrayList<StockMCBean>();
		try {
			sql.append("\n select M.* from (");
			sql.append("\n select A.* ,rownum as r__ from (");
			sql.append("\n select H.ID, H.stock_date ,H.customer_code,H.store_code ,H.create_user,H.mc_name");
			sql.append("\n ,(select m.customer_name from pensbi.mc_cust m where H.customer_code = m.customer_code) as customer_name ");
			sql.append("\n FROM PENSBI.MC_COUNTSTK_HEADER H");
			sql.append("\n WHERE 1=1");
			 //GenWhereSQL
			sql.append(" "+genWhereCondSql(conn,o));
		
			sql.append("\n    ORDER BY H.ID desc");
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
			   h.setId(rst.getInt("id"));
			   h.setStockDate(Utils.stringValueChkNull(rst.getDate("stock_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			   h.setCustomerCode(Utils.isNull(rst.getString("customer_code")));
			   h.setCustomerName(Utils.isNull(rst.getString("customer_name")));
			   h.setStoreCode(Utils.isNull(rst.getString("store_code")));
			   h.setStoreName(Utils.isNull(rst.getString("store_code")));
			   h.setCreateUser(Utils.isNull(rst.getString("create_user")));
			   h.setMcName(rst.getString("mc_name"));  
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
	public StockMCBean searchStockMC(StockMCBean o,boolean getItems) throws Exception {
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			return searchStockMC(conn, o, getItems);
		}catch(Exception e){
			throw e;
		}finally{
			conn.close();
		}
	}
	public StockMCBean searchStockMC(Connection conn,StockMCBean o,boolean getItems) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		StockMCBean h = null;
		try {
			sql.append("\n select H.*");
			sql.append("\n ,(select M.customer_name from pensbi.mc_cust M ");
			sql.append("\n   where M.customer_code = H.customer_code ) as customer_name");
			sql.append("\n FROM PENSBI.MC_COUNTSTK_HEADER H");
			sql.append("\n WHERE 1=1");
			 //GenWhereSQL
			sql.append(" "+genWhereCondSql(conn,o));
			sql.append("\n  ORDER BY H.stock_date desc");
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			if(rst.next()) {
			   h = new StockMCBean();
			   h.setId(rst.getInt("id"));
			   h.setStockDate(Utils.stringValueChkNull(rst.getDate("stock_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			   h.setCustomerCode(Utils.isNull(rst.getString("customer_code")));
			   h.setCustomerName(Utils.isNull(rst.getString("customer_name")));
			   h.setStoreCode(Utils.isNull(rst.getString("store_code")));
			   h.setStoreName(Utils.isNull(rst.getString("store_code")));
			   h.setCreateUser(Utils.isNull(rst.getString("create_user")));
			   h.setMcName(rst.getString("mc_name"));  
			   if(getItems){
				   h.setItems(searchStockMCDetail(conn, h.getId()));
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
	public  List<StockMCBean> searchStockMCDetail(Connection conn,int id) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		StockMCBean h = null;
		List<StockMCBean> items = new ArrayList<StockMCBean>();
		int no =0;
		try {
			sql.append("\n select D.* ");
			sql.append("\n ,(select max(M.description) from pensbi.mc_item_cust M ");
			sql.append("\n  where H.customer_code = M.customer_code ");
			sql.append("\n  and M.item_pens = D.product_code and M.barcode = D.barcode) as description");
			sql.append("\n FROM PENSBI.MC_COUNTSTK_HEADER H,PENSBI.MC_COUNTSTK_DETAIL D");
			sql.append("\n WHERE H.id = D.id ");
			sql.append("\n AND D.id = "+id);
			sql.append("\n ORDER BY D.line_id asc");
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()) {
			   no++;
			   h = new StockMCBean();
			   h.setId(rst.getInt("id"));
			   h.setNo(no);
			   h.setLineId(rst.getInt("line_id"));
			   h.setProductCode(Utils.isNull(rst.getString("product_code")));
			   h.setBarcode(Utils.isNull(rst.getString("barcode")));
			   h.setProductName(Utils.isNull(rst.getString("description")));
			   h.setProductPackSize(Utils.isNull(rst.getString("PRODUCT_PACKSIZE")));
			   h.setProductAge(Utils.isNull(rst.getString("PRODUCT_AGE")));
			   h.setRetailPriceBF(Utils.isNullDoubleStrToBlank(Utils.decimalFormat(rst.getDouble("RETAIL_PRICE_BF"), Utils.format_current_2_disgit)));
			   h.setPromotionPrice(Utils.isNullDoubleStrToBlank(Utils.decimalFormat(rst.getDouble("PROMOTION_PRICE"), Utils.format_current_2_disgit)));
			   h.setLegQty(Utils.isNullDoubleStrToBlank(Utils.decimalFormat(rst.getDouble("LEG_QTY"), Utils.format_current_no_disgit)));
			   h.setBackendQty(Utils.isNullDoubleStrToBlank(Utils.decimalFormat(rst.getDouble("BACKEND_QTY"), Utils.format_current_no_disgit)));
			   h.setInStoreQty(Utils.isNullDoubleStrToBlank(Utils.decimalFormat(rst.getDouble("IN_STORE_QTY"), Utils.format_current_no_disgit)));
			   h.setUom(Utils.isNull(rst.getString("uom")));
			   //1
			   h.setFrontendQty1(Utils.isNullDoubleStrToBlank(Utils.decimalFormat(rst.getDouble("FRONTEND_QTY_1"), Utils.format_current_no_disgit)));
			   h.setUom1(Utils.isNull(rst.getString("uom_1")));
			   h.setExpireDate1(Utils.stringValueChkNull(rst.getDate("EXPIRE_DATE_1"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			   //2
			   h.setFrontendQty2(Utils.isNullDoubleStrToBlank(Utils.decimalFormat(rst.getDouble("FRONTEND_QTY_2"), Utils.format_current_no_disgit)));
			   h.setUom2(Utils.isNull(rst.getString("uom_2")));
			   h.setExpireDate2(Utils.stringValueChkNull(rst.getDate("EXPIRE_DATE_2"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			   //3
			   h.setFrontendQty3(Utils.isNullDoubleStrToBlank(Utils.decimalFormat(rst.getDouble("FRONTEND_QTY_3"), Utils.format_current_no_disgit)));
			   h.setUom3(Utils.isNull(rst.getString("uom_3")));
			   h.setExpireDate3(Utils.stringValueChkNull(rst.getDate("EXPIRE_DATE_3"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			
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
	
	public static StringBuffer genWhereCondSql(Connection conn,StockMCBean o) throws Exception{
		StringBuffer sql = new StringBuffer("");
		if( o.getId() != 0){
			sql.append("\n and H.id = "+o.getId()+"");
		}
		if( !Utils.isNull(o.getCustomerCode()).equals("")){
			sql.append("\n and H.customer_code = '"+Utils.isNull(o.getCustomerCode())+"'");
		}
		if( !Utils.isNull(o.getStoreCode()).equals("")){
			sql.append("\n and H.store_code = '"+Utils.isNull(o.getStoreCode())+"'");
		}
	     if(!Utils.isNull(o.getStockDate()).equalsIgnoreCase("")){
			Date endDate = Utils.parse(o.getStockDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
			String endDateStr = Utils.stringValue(endDate, Utils.DD_MM_YYYY_WITH_SLASH);
			sql.append("\n and H.stock_date = to_date('"+endDateStr+"','dd/mm/yyyy')");
		}
		return sql;
	}
	
	public static List<StockMCBean> getProductMCItemList(Connection conn,String customerCode) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		StockMCBean h = null;
		List<StockMCBean> itemList = new ArrayList<StockMCBean>();
		int no= 0;
		try {
			sql.append("\n select H.* FROM PENSBI.MC_ITEM_CUST H");
			sql.append("\n WHERE customer_code = '"+customerCode+"'");
			logger.debug("sql:"+sql.toString());
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()) {
			   no++;
			   h = new StockMCBean();
			   h.setNo(no);
			   h.setProductCode(Utils.isNull(rst.getString("item_pens")));
			   h.setBarcode(Utils.isNull(rst.getString("barcode")));
			   h.setItemCust(Utils.isNull(rst.getString("item_cust")));
			   h.setProductName(Utils.isNull(rst.getString("description")));
			   h.setProductPackSize(Utils.isNull(rst.getString("packsize")));
			   h.setProductAge(Utils.isNull(rst.getString("PRODUCT_AGE")));
			   h.setRetailPriceBF(Utils.decimalFormat(rst.getDouble("retail_price"), Utils.format_current_2_disgit));
			   h.setStartDate(Utils.stringValueChkNull(rst.getDate("start_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			  
			   h.setPromotionPrice("");
			   h.setLegQty("");
			   h.setBackendQty("");
			   h.setInStoreQty("");
			   h.setUom("");
			   //1
			   h.setFrontendQty1("");
			   h.setUom1("");
			   h.setExpireDate1("");
			   //2
			   h.setFrontendQty2("");
			   h.setUom2("");
			   h.setExpireDate2("");
			   //3
			   h.setFrontendQty3("");
			   h.setUom3("");
			   h.setExpireDate3("");
			  
			   itemList.add(h);
			}//while

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return itemList;
	}
	
	public static StockMCBean getProductMCItem(String storeCode,String productCode,String barcode) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		StockMCBean h = null;
		Connection conn = null;
		try {
		    conn = DBConnection.getInstance().getConnection();
		    
			sql.append("\n select H.* FROM PENSBI.MC_ITEM_CUST H");
			sql.append("\n WHERE customer_code = '"+storeCode+"'");
			if( !Utils.isNull(productCode).equals("")){
			   sql.append("\n and item_pens ='"+productCode+"'");
			}
			if( !Utils.isNull(barcode).equals("")){
			   sql.append("\n and barcode ='"+barcode+"'");
			}
			logger.debug("sql:"+sql.toString());
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()) {
			   h = new StockMCBean();
			   h.setProductCode(Utils.isNull(rst.getString("item_pens")));
			   h.setBarcode(Utils.isNull(rst.getString("barcode")));
			   h.setItemCust(Utils.isNull(rst.getString("item_cust")));
			   h.setProductName(Utils.isNull(rst.getString("description")));
			   h.setProductPackSize(Utils.isNull(rst.getString("packsize")));
			   h.setProductAge(rst.getString("PRODUCT_AGE"));
			   h.setRetailPriceBF(Utils.decimalFormat(rst.getDouble("retail_price"), Utils.format_current_2_disgit));
			   h.setStartDate(Utils.stringValueChkNull(rst.getDate("start_date"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			   h.setExpireDate1(Utils.stringValueChkNull(rst.getDate("expired"), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
			}//while

		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		} finally {
			try {
				rst.close();
				ps.close();
				conn.close();
			} catch (Exception e) {}
		}
		return h;
	}
	
	public static int getMaxLineId(Connection conn,int id) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int maxLineId = 0;
		try {
			sql.append("\n select max(line_id) as max_line_id FROM PENSBI.MC_COUNTSTK_DETAIL H");
			sql.append("\n WHERE id = "+id+"");
			
			logger.debug("sql:"+sql.toString());
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			if(rst.next()) {
				maxLineId = rst.getInt("max_line_id");
			}//while

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
				ps.close();
			} catch (Exception e) {}
		}
		return maxLineId;
	}
	
	public StockMCBean save(StockMCBean head) throws Exception {
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
	
			if(head.getId() == 0){
				head = insertStockMCHead(conn , head);
			}else{
				updateStockMCHead(conn , head);
			}
	
			if(head.getItems() != null && head.getItems().size() > 0){
			    int maxLineId = getMaxLineId(conn, head.getId());//getMaxId by ID
				// Process normal
				for(int i =0;i< head.getItems().size();i++){
					StockMCBean line = (StockMCBean)head.getItems().get(i);
					logger.debug("check id:"+head.getId());
					logger.debug("check lineId:"+line.getLineId());
					if(line.getLineId() ==0){
					   maxLineId++;
					   line.setId(head.getId());
				       line.setLineId((maxLineId));
				       
				       insertStockMCLine(conn,line);
					}else{
					   line.setId(head.getId());
					   updateStockMCLine(conn,line);
					}
				}//for
			}
			
			//delete stockMCItem not in 
			deleteStockMCLine(conn, head.getId() ,head.getLineIdDeletes());
			
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
	
	private StockMCBean insertStockMCHead(Connection conn ,StockMCBean model) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		StringBuffer sql = new StringBuffer("");
		int index = 0;
		try {
			sql.append(" INSERT INTO PENSBI.MC_COUNTSTK_HEADER( \n");
			sql.append(" ID, STOCK_DATE, CUSTOMER_CODE,STORE_CODE,MC_NAME, \n");
			sql.append(" CREATE_DATE,CREATE_USER) \n");
			sql.append(" VALUES (?,?,?,?,?,?,?) \n");
			  
			//logger.debug("SQL:"+sql);
			//Gen ID running
			model.setId(SequenceProcessAll.getNextValue("MC_COUNTSTK_HEADER"));

			ps = conn.prepareStatement(sql.toString());
			
			ps.setLong(++index, model.getId());
			ps.setDate(++index, new java.sql.Date(Utils.parse(model.getStockDate(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th).getTime()));
			ps.setString(++index, Utils.isNull(model.getCustomerCode()));
			ps.setString(++index, Utils.isNull(model.getStoreCode()));
			ps.setString(++index, Utils.isNull(model.getMcName()));
			ps.setTimestamp(++index, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(++index, model.getCreateUser());
			
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
	
	private boolean updateStockMCHead(Connection conn ,StockMCBean model) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		StringBuffer sql = new StringBuffer("");
		try {
			sql.append(" UPDATE PENSBI.MC_COUNTSTK_HEADER \n");
			sql.append(" SET UPDATE_DATE =? , UPDATE_USER =? \n");
			sql.append(" where id = ? \n");
			  
			logger.debug("SQL:"+sql);
			
			int index = 0;
			ps = conn.prepareStatement(sql.toString());
			
			ps.setTimestamp(++index, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(++index, model.getCreateUser());
			ps.setLong(++index, model.getId());
			
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
		return result;
	}
	
	private boolean insertStockMCLine(Connection conn ,StockMCBean line) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		int index = 0;
		try {
			logger.debug("insertStockMCLine id["+line.getId()+"]lineId["+line.getLineId()+"]promotionPrice["+line.getPromotionPrice()+"]");
			
			StringBuffer sql = new StringBuffer("");
			sql.append(" INSERT INTO PENSBI.MC_COUNTSTK_DETAIL( \n");
			sql.append(" ID, LINE_ID, PRODUCT_CODE,PRODUCT_PACKSIZE,PRODUCT_AGE,RETAIL_PRICE_BF, \n"); //6
			sql.append(" PROMOTION_PRICE,LEG_QTY, BACKEND_QTY,IN_STORE_QTY, UOM,  \n");//11
			sql.append(" FRONTEND_QTY_1, UOM_1, EXPIRE_DATE_1,FRONTEND_QTY_2,UOM_2, \n");//16
			sql.append(" EXPIRE_DATE_2, FRONTEND_QTY_3, UOM_3, EXPIRE_DATE_3,CREATE_DATE,CREATE_USER,BARCODE ) \n");//22
			sql.append(" VALUES (?,?,?,?,?,?,"
					          + "?,?,?,?,?,?,"
					          + "?,?,?,?,?,?,"
					          + "?,?,?,?,?) \n");//23

			logger.debug("SQL:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			
			ps.setInt(++index, line.getId());//1
			ps.setLong(++index, line.getLineId());//2
			ps.setString(++index, line.getProductCode());//3
			ps.setString(++index, line.getProductPackSize());//4
			ps.setString(++index,Utils.isNull(line.getProductAge()) );//5
			ps.setDouble(++index, Utils.convertStrToDouble(line.getRetailPriceBF()));//6
			ps.setDouble(++index, Utils.convertStrToDouble(line.getPromotionPrice()));//7
			ps.setDouble(++index, Utils.convertStrToDouble(line.getLegQty()));//8
			ps.setDouble(++index, Utils.convertStrToDouble(line.getBackendQty()));//9
			ps.setDouble(++index, Utils.convertStrToDouble(line.getInStoreQty()));//10
			ps.setString(++index, line.getUom());//11
			
			/*** 1 **************************************************************/
			ps.setDouble(++index, Utils.convertStrToDouble(line.getFrontendQty1()));//12
			ps.setString(++index, line.getUom1());//13
			if( !Utils.isNull(line.getExpireDate1()).equals("")){//14
				 ps.setDate(++index, new java.sql.Date(Utils.parse(line.getExpireDate1(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th).getTime()));
			}else{
				 ps.setNull(++index,java.sql.Types.DATE);
			}
			/*** 2 **************************************************************/
			ps.setDouble(++index, Utils.convertStrToDouble(line.getFrontendQty2()));//15
			ps.setString(++index, line.getUom2());//16
			if( !Utils.isNull(line.getExpireDate2()).equals("")){//17
				 ps.setDate(++index, new java.sql.Date(Utils.parse(line.getExpireDate2(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th).getTime()));
			}else{
				 ps.setNull(++index,java.sql.Types.DATE);
			}
			/*** 3 **************************************************************/
			ps.setDouble(++index, Utils.convertStrToDouble(line.getFrontendQty3()));//18
			ps.setString(++index, line.getUom3());//19
			if( !Utils.isNull(line.getExpireDate3()).equals("")){//20
				 ps.setDate(++index, new java.sql.Date(Utils.parse(line.getExpireDate3(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th).getTime()));
			}else{
				 ps.setNull(++index,java.sql.Types.DATE);
			}
			ps.setTimestamp(++index, new java.sql.Timestamp(new Date().getTime()));//21
			ps.setString(++index, line.getCreateUser());//22
			ps.setString(++index, line.getBarcode());//23
			
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
		return result;
	}
	
	private boolean updateStockMCLine(Connection conn ,StockMCBean line) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		try {
			logger.debug("updateStockMCLine id["+line.getId()+"]lineId["+line.getLineId()+"]promotionPrice["+line.getPromotionPrice()+"]");
			StringBuffer sql = new StringBuffer("");
			sql.append(" UPDATE PENSBI.MC_COUNTSTK_DETAIL SET \n");
			sql.append(" PROMOTION_PRICE=? ,LEG_QTY=? , BACKEND_QTY=? ,IN_STORE_QTY=?  , UOM=? ,  \n");//5
			sql.append(" FRONTEND_QTY_1=? , UOM_1=? , EXPIRE_DATE_1=? ,FRONTEND_QTY_2=? ,UOM_2=? , \n");//10
			sql.append(" EXPIRE_DATE_2=? , FRONTEND_QTY_3=? , UOM_3=? , EXPIRE_DATE_3=? ,UPDATE_DATE=? ,UPDATE_USER =?  \n");//16
			
			sql.append(" WHERE ID = ? AND LINE_ID = ? \n");//18

			//logger.debug("SQL:"+sql);
			int index = 0;
			
			ps = conn.prepareStatement(sql.toString());
			
			ps.setDouble(++index, Utils.convertStrToDouble(line.getPromotionPrice()));
			ps.setDouble(++index, Utils.convertStrToDouble(line.getLegQty()));
			ps.setDouble(++index, Utils.convertStrToDouble(line.getInStoreQty()));
			ps.setDouble(++index, Utils.convertStrToDouble(line.getBackendQty()));
			ps.setString(++index, line.getUom());//5
			
			/*** 1 **************************************************************/
			ps.setDouble(++index, Utils.convertStrToDouble(line.getFrontendQty1()));
			ps.setString(++index, line.getUom1());
			if( !Utils.isNull(line.getExpireDate1()).equals("")){
				 ps.setDate(++index, new java.sql.Date(Utils.parse(line.getExpireDate1(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th).getTime()));
			}else{
				 ps.setNull(++index,java.sql.Types.DATE);
			}
			/*** 2 **************************************************************/
			ps.setDouble(++index, Utils.convertStrToDouble(line.getFrontendQty2()));
			ps.setString(++index, line.getUom2());
			if( !Utils.isNull(line.getExpireDate2()).equals("")){ //11
				 ps.setDate(++index, new java.sql.Date(Utils.parse(line.getExpireDate2(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th).getTime()));
			}else{
				 ps.setNull(++index,java.sql.Types.DATE);
			}
			/*** 3 **************************************************************/
			ps.setDouble(++index, Utils.convertStrToDouble(line.getFrontendQty3()));
			ps.setString(++index, line.getUom3());
			if( !Utils.isNull(line.getExpireDate3()).equals("")){
				 ps.setDate(++index, new java.sql.Date(Utils.parse(line.getExpireDate3(), Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th).getTime()));
			}else{
				 ps.setNull(++index,java.sql.Types.DATE);
			}
			
			ps.setTimestamp(++index, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(++index, line.getUpdateUser());
			
			//where 
			ps.setInt(++index, line.getId());
			ps.setLong(++index, line.getLineId());//18
			
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
		return result;
	}
	
	private boolean deleteStockMCLine(Connection conn ,int id,String lineIdDelete) throws Exception {
		boolean result = false;
		PreparedStatement ps = null;
		try {
			StringBuffer sql = new StringBuffer("");
			sql.append(" DELETE PENSBI.MC_COUNTSTK_DETAIL  \n");
			sql.append(" WHERE ID = "+id+" AND LINE_ID IN("+Utils.converToTextSqlIn(lineIdDelete)+") \n");

			logger.debug("SQL:"+sql);

			ps = conn.prepareStatement(sql.toString());
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
		return result;
	}
}
