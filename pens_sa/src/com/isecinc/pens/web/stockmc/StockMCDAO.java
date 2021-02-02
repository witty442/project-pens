package com.isecinc.pens.web.stockmc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.pens.util.DBConnection;
import com.pens.util.DateUtil;
import com.pens.util.FileUtil;
import com.pens.util.SQLHelper;
import com.pens.util.Utils;
import com.pens.util.seq.SequenceProcessAll;

public class StockMCDAO {
	protected static Logger logger = Logger.getLogger("PENS");
	
	public static int searchStockMCListTotalRec(Connection conn,StockMCBean o) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		int totalRec = 0;
		try {
			sql.append("\n select count(*) as c FROM ");
			sql.append("\n PENSBI.MC_COUNTSTK_HEADER H ");
			sql.append("\n ,( ");
			sql.append("\n   SELECT DISTINCT C.CUSTOMER_CODE,C.CUSTOMER_NAME ");
			sql.append("\n   ,B.BRANCH_NO,B.BRANCH_NAME ");
			sql.append("\n   FROM PENSBI.MC_CUST C ,PENSBI.MC_CUST_BRANCH B");
			sql.append("\n   WHERE C.customer_code = B.customer_code ");
			sql.append("\n )C ");
			sql.append("\n WHERE H.customer_code = C.customer_code ");
			sql.append("\n AND H.store_code = C.branch_no ");

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
			sql.append("\n ,C.customer_name ,C.branch_name");
			sql.append("\n FROM PENSBI.MC_COUNTSTK_HEADER H ");
			sql.append("\n ,( ");
			sql.append("\n   SELECT DISTINCT C.CUSTOMER_CODE,C.CUSTOMER_NAME ");
			sql.append("\n   ,B.BRANCH_NO,B.BRANCH_NAME ");
			sql.append("\n   FROM PENSBI.MC_CUST C ,PENSBI.MC_CUST_BRANCH B");
			sql.append("\n   WHERE C.customer_code = B.customer_code ");
			sql.append("\n )C ");
			sql.append("\n WHERE H.customer_code = C.customer_code ");
			sql.append("\n AND H.store_code = C.branch_no ");
			
			
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
			   h.setStockDate(DateUtil.stringValueChkNull(rst.getDate("stock_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
			   h.setCustomerCode(Utils.isNull(rst.getString("customer_code")));
			   h.setCustomerName(Utils.isNull(rst.getString("customer_name")));
			   h.setStoreCode(Utils.isNull(rst.getString("store_code")));
			   h.setStoreName(Utils.isNull(rst.getString("branch_name")));
			   h.setCreateUser(Utils.isNull(rst.getString("create_user")));
			   h.setMcName(rst.getString("mc_name"));  
			   
			   if(DateUtil.compareWithToday(h.getStockDate()) ==0){
				   h.setCanEdit(true);
			   }
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
			sql.append("\n ,C.customer_name ,C.branch_name");
			sql.append("\n FROM PENSBI.MC_COUNTSTK_HEADER H");
			sql.append("\n ,( ");
			sql.append("\n   SELECT DISTINCT C.CUSTOMER_CODE,C.CUSTOMER_NAME ");
			sql.append("\n   ,B.BRANCH_NO,B.BRANCH_NAME ");
			sql.append("\n   FROM PENSBI.MC_CUST C ,PENSBI.MC_CUST_BRANCH B");
			sql.append("\n   WHERE C.customer_code = B.customer_code ");
			sql.append("\n )C ");
			sql.append("\n WHERE H.customer_code = C.customer_code ");
			sql.append("\n AND H.store_code = C.branch_no ");
			 //GenWhereSQL
			sql.append(" "+genWhereCondSql(conn,o));
			sql.append("\n  ORDER BY H.stock_date desc");
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			if(rst.next()) {
			   h = new StockMCBean();
			   h.setId(rst.getInt("id"));
			   h.setStockDate(DateUtil.stringValueChkNull(rst.getDate("stock_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
			   h.setCustomerCode(Utils.isNull(rst.getString("customer_code")));
			   h.setCustomerName(Utils.isNull(rst.getString("customer_name")));
			   h.setStoreCode(Utils.isNull(rst.getString("store_code")));
			   h.setStoreName(Utils.isNull(rst.getString("branch_name")));
			   h.setCreateUser(Utils.isNull(rst.getString("create_user")));
			   h.setMcName(rst.getString("mc_name"));  
			   
			   //check can edit
			   if(DateUtil.compareWithToday(h.getStockDate())==0){
				   h.setCanEdit(true);
			   }
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
			
			sql.append("\n ,(select max(M.brand) from pensbi.mc_item_cust M ");
			sql.append("\n  where H.customer_code = M.customer_code ");
			sql.append("\n  and M.item_pens = D.product_code and M.barcode = D.barcode) as brand");
			
			sql.append("\n ,(select max(M.description) from pensbi.mc_item_cust M ");
			sql.append("\n  where H.customer_code = M.customer_code ");
			sql.append("\n  and M.item_pens = D.product_code and M.barcode = D.barcode) as brand_name");
			
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
			   h.setBrand(Utils.isNull(rst.getString("brand")));
			   h.setBrandName(Utils.isNull(rst.getString("brand_name")));
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
			   h.setExpireDate1(DateUtil.stringValueChkNull(rst.getDate("EXPIRE_DATE_1"), DateUtil.DD_MM_YYYY_WITH_SLASH));
			   //2
			   h.setFrontendQty2(Utils.isNullDoubleStrToBlank(Utils.decimalFormat(rst.getDouble("FRONTEND_QTY_2"), Utils.format_current_no_disgit)));
			   h.setUom2(Utils.isNull(rst.getString("uom_2")));
			   h.setExpireDate2(DateUtil.stringValueChkNull(rst.getDate("EXPIRE_DATE_2"), DateUtil.DD_MM_YYYY_WITH_SLASH));
			   //3
			   h.setFrontendQty3(Utils.isNullDoubleStrToBlank(Utils.decimalFormat(rst.getDouble("FRONTEND_QTY_3"), Utils.format_current_no_disgit)));
			   h.setUom3(Utils.isNull(rst.getString("uom_3")));
			   h.setExpireDate3(DateUtil.stringValueChkNull(rst.getDate("EXPIRE_DATE_3"), DateUtil.DD_MM_YYYY_WITH_SLASH));
			
			   h.setItemCheck(Utils.isNull(rst.getString("item_check")));
			   h.setDateInStore(DateUtil.stringValueChkNull(rst.getDate("date_In_Store"), DateUtil.DD_MM_YYYY_WITH_SLASH));
			   h.setDateInStoreQty(Utils.isNullDoubleStrToBlank(Utils.decimalFormat(rst.getDouble("date_In_Store_qty"), Utils.format_current_no_disgit)));
			   h.setReasonNId(Utils.isNull(rst.getString("reason_n_id")));
			   h.setReasonDId(Utils.isNull(rst.getString("reason_d_id")));
			   
			   h.setNote(Utils.isNull(rst.getString("note")));
			   
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
	public  StockMCBean searchStockMCDetail(String id,String productCode) throws Exception {
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnectionApps();
			return searchStockMCDetail(conn, id, productCode);
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			throw e;
		}finally{
			conn.close();
		}
	}
	public  StockMCBean searchStockMCDetail(Connection conn,String id,String productCode) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		StockMCBean h = null;
		int no =0;
		try {
			sql.append("\n select D.* ");
			
			sql.append("\n ,(select max(M.description) from pensbi.mc_item_cust M ");
			sql.append("\n  where H.customer_code = M.customer_code ");
			sql.append("\n  and M.item_pens = D.product_code and M.barcode = D.barcode) as description");
			
			sql.append("\n ,(select max(M.brand) from pensbi.mc_item_cust M ");
			sql.append("\n  where H.customer_code = M.customer_code ");
			sql.append("\n  and M.item_pens = D.product_code and M.barcode = D.barcode) as brand");
			
			sql.append("\n ,(select max(M.master_leg_qty) from pensbi.mc_item_cust M ");
			sql.append("\n  where H.customer_code = M.customer_code ");
			sql.append("\n  and M.item_pens = D.product_code and M.barcode = D.barcode) as master_leg_qty");
			
			sql.append("\n FROM PENSBI.MC_COUNTSTK_HEADER H,PENSBI.MC_COUNTSTK_DETAIL D");
			sql.append("\n WHERE H.id = D.id ");
			sql.append("\n AND D.id = "+id);
			sql.append("\n AND D.product_code = '"+productCode+"'");
			sql.append("\n ORDER BY D.line_id asc");
			
			logger.debug("sql:"+sql);
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			if(rst.next()) {
			   no++;
			   h = new StockMCBean();
			   h.setId(rst.getInt("id"));
			   h.setNo(no);
			   h.setLineId(rst.getInt("line_id"));
			   h.setProductCode(Utils.isNull(rst.getString("product_code")));
			   h.setBrand(Utils.isNull(rst.getString("brand")));
			   h.setBarcode(Utils.isNull(rst.getString("barcode")));
			   h.setProductName(Utils.isNull(rst.getString("description")));
			   h.setProductPackSize(Utils.isNull(rst.getString("PRODUCT_PACKSIZE")));
			   h.setProductAge(Utils.isNull(rst.getString("PRODUCT_AGE")));
			   h.setRetailPriceBF(Utils.isNullDoubleStrToBlank(Utils.decimalFormat(rst.getDouble("RETAIL_PRICE_BF"), Utils.format_current_2_disgit)));
			   h.setPromotionPrice(Utils.isNullDoubleStrToBlank(Utils.decimalFormat(rst.getDouble("PROMOTION_PRICE"), Utils.format_current_2_disgit)));
			   h.setMasterLegQty(Utils.isNullDoubleStrToBlank(Utils.decimalFormat(rst.getDouble("MASTER_LEG_QTY"), Utils.format_current_no_disgit)));
			   h.setLegQty(Utils.isNullDoubleStrToBlank(Utils.decimalFormat(rst.getDouble("LEG_QTY"), Utils.format_current_no_disgit)));
			   h.setBackendQty(Utils.isNullDoubleStrToBlank(Utils.decimalFormat(rst.getDouble("BACKEND_QTY"), Utils.format_current_no_disgit)));
			   h.setInStoreQty(Utils.isNullDoubleStrToBlank(Utils.decimalFormat(rst.getDouble("IN_STORE_QTY"), Utils.format_current_no_disgit)));
			   h.setUom(Utils.isNull(rst.getString("uom")));
			   //1
			   h.setFrontendQty1(Utils.isNullDoubleStrToBlank(Utils.decimalFormat(rst.getDouble("FRONTEND_QTY_1"), Utils.format_current_no_disgit)));
			   h.setUom1(Utils.isNull(rst.getString("uom_1")));
			   h.setExpireDate1(DateUtil.stringValueChkNull(rst.getDate("EXPIRE_DATE_1"), DateUtil.DD_MM_YYYY_WITH_SLASH));
			   //2
			   h.setFrontendQty2(Utils.isNullDoubleStrToBlank(Utils.decimalFormat(rst.getDouble("FRONTEND_QTY_2"), Utils.format_current_no_disgit)));
			   h.setUom2(Utils.isNull(rst.getString("uom_2")));
			   h.setExpireDate2(DateUtil.stringValueChkNull(rst.getDate("EXPIRE_DATE_2"), DateUtil.DD_MM_YYYY_WITH_SLASH));
			   //3
			   h.setFrontendQty3(Utils.isNullDoubleStrToBlank(Utils.decimalFormat(rst.getDouble("FRONTEND_QTY_3"), Utils.format_current_no_disgit)));
			   h.setUom3(Utils.isNull(rst.getString("uom_3")));
			   h.setExpireDate3(DateUtil.stringValueChkNull(rst.getDate("EXPIRE_DATE_3"), DateUtil.DD_MM_YYYY_WITH_SLASH));
			
			   h.setItemCheck(Utils.isNull(rst.getString("item_check")));
			   h.setDateInStore(DateUtil.stringValueChkNull(rst.getDate("date_In_Store"), DateUtil.DD_MM_YYYY_WITH_SLASH));
			   h.setDateInStoreQty(Utils.isNullDoubleStrToBlank(Utils.decimalFormat(rst.getDouble("date_In_Store_qty"), Utils.format_current_no_disgit)));
			   h.setReasonNId(Utils.isNull(rst.getString("reason_n_id")));
			   h.setReasonDId(Utils.isNull(rst.getString("reason_d_id")));
			   
			   h.setNote(Utils.isNull(rst.getString("note")));
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
	
	
	public static List<StockMCBean> searchStockMCReport(String pageName,Connection conn,StockMCBean o,String idSelected) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		StockMCBean h = null;
		List<StockMCBean> items = new ArrayList<StockMCBean>();
		int no =0;
		try {
			sql.append("\n select ");
			sql.append("\n  H.stock_date,H.customer_code,H.store_code,H.create_user");
			sql.append("\n , C.customer_name ,C.branch_name");
			sql.append("\n , D.* ");
			sql.append("\n , P.brand");
			
			sql.append("\n ,(select max(M.description) from pensbi.mc_item_cust M ");
			sql.append("\n  where H.customer_code = M.customer_code ");
			sql.append("\n  and M.item_pens = D.product_code and M.barcode = D.barcode) as description");
			
			sql.append("\n ,(select oos_reason from pensbi.MC_OOS_REASON M ");
			sql.append("\n   where M.oos_id = D.reason_n_id ) as reason_n_desc");
			sql.append("\n ,(select derange_reason from pensbi.MC_DERANGED_REASON M ");
			sql.append("\n   where M.derange_id = D.reason_d_id ) as reason_d_desc");
			
			sql.append("\n FROM PENSBI.MC_COUNTSTK_HEADER H,PENSBI.MC_COUNTSTK_DETAIL D");
			
			/** latest by product brand **/
			if( !Utils.isNull(o.getDispCheckStockLatest()).equals("")){
				sql.append("\n ,( ");
				sql.append("\n   SELECT max(H.stock_date) as max_stock_date");
				sql.append("\n   ,H.CUSTOMER_CODE,H.STORE_CODE,D.PRODUCT_CODE ");
				sql.append("\n   FROM PENSBI.MC_COUNTSTK_HEADER H,PENSBI.MC_COUNTSTK_DETAIL D");
				sql.append("\n   WHERE H.id = D.id");
				sql.append("\n   GROUP BY H.CUSTOMER_CODE,H.STORE_CODE,D.PRODUCT_CODE");
				sql.append("\n )L ");
			}
			sql.append("\n ,( ");
			sql.append("\n   SELECT DISTINCT C.CUSTOMER_CODE,C.CUSTOMER_NAME ");
			sql.append("\n   ,B.BRANCH_NO,B.BRANCH_NAME ");
			sql.append("\n   FROM PENSBI.MC_CUST C ,PENSBI.MC_CUST_BRANCH B");
			sql.append("\n   WHERE C.customer_code = B.customer_code ");
			sql.append("\n )C ");
			sql.append("\n ,apps.xxpens_om_item_mst_v P");
			sql.append("\n WHERE H.id = D.id ");
			sql.append("\n AND P.segment1 = D.product_code ");
			sql.append("\n AND H.customer_code = C.customer_code ");
			sql.append("\n AND H.store_code = C.branch_no ");
			
			if( !Utils.isNull(o.getDispCheckStockLatest()).equals("")){
				sql.append("\n AND H.stock_date = L.max_stock_date ");
				sql.append("\n AND H.customer_code = L.customer_code ");
				sql.append("\n AND H.store_code = L.store_code ");
				sql.append("\n AND D.product_code = L.product_code ");
			}
			
			if( !Utils.isNull(idSelected).equals("")){
			   sql.append("\n AND D.id in( "+SQLHelper.converToTextSqlIn(idSelected)+")");
			}
			if(o !=null && !Utils.isNull(o.getDispHaveCheckStock()).equals("")){
			   sql.append("\n AND ( ");
			   sql.append("\n    D.PROMOTION_PRICE <> 0 OR D.LEG_QTY <> 0 OR D.BACKEND_QTY <> 0 OR D.IN_STORE_QTY <> 0  ");
			   sql.append("\n    OR FRONTEND_QTY_1 <> 0 OR FRONTEND_QTY_2 <> 0 OR FRONTEND_QTY_3 <> 0 ");
			   sql.append("\n ) ");
			}
			if( !Utils.isNull(o.getStockDateFrom()).equals("") && !Utils.isNull(o.getStockDateTo()).equals("")){
				sql.append("\n AND H.stock_date >=to_date('"+o.getStockDateFrom()+"','dd/mm/yyyy')");
				sql.append("\n AND H.stock_date <=to_date('"+o.getStockDateTo()+"','dd/mm/yyyy')");
			}else{
				if( !Utils.isNull(o.getStockDateFrom()).equals("")){
					sql.append("\n AND H.stock_date =to_date('"+o.getStockDateFrom()+"','dd/mm/yyyy')");
				}
				if( !Utils.isNull(o.getStockDateTo()).equals("")){
					sql.append("\n AND H.stock_date =to_date('"+o.getStockDateTo()+"','dd/mm/yyyy')");
				}
			}
			if( !Utils.isNull(o.getCustomerCode()).equals("")){
				sql.append("\n AND H.customer_code ='"+o.getCustomerCode()+"'");
			}
			if( !Utils.isNull(o.getStoreCode()).equals("")){
				sql.append("\n AND H.store_code ='"+o.getStoreCode()+"'");
			}
			if( !Utils.isNull(o.getBrandFrom()).equals("") && !Utils.isNull(o.getBrandTo()).equals("")){
				sql.append("\n AND P.brand >= '"+o.getBrandFrom()+"'");
				sql.append("\n AND P.brand <= '"+o.getBrandTo()+"'");
			}else{
				if( !Utils.isNull(o.getBrandFrom()).equals("") ){
					sql.append("\n AND P.brand = '"+o.getBrandFrom()+"'");	
				}
				if( !Utils.isNull(o.getBrandTo()).equals("") ){
					sql.append("\n AND P.brand = '"+o.getBrandTo()+"'");	
				}
			}
			if( !Utils.isNull(o.getProductCodeFrom()).equals("") && !Utils.isNull(o.getProductCodeTo()).equals("")){
				sql.append("\n AND D.product_code >= '"+o.getProductCodeFrom()+"'");
				sql.append("\n AND D.product_code <= '"+o.getProductCodeTo()+"'");
			}else{
				if( !Utils.isNull(o.getProductCodeFrom()).equals("")){
					sql.append("\n AND D.product_code = '"+o.getProductCodeFrom()+"'");
				}
				if( !Utils.isNull(o.getProductCodeTo()).equals("")){
					sql.append("\n AND D.product_code = '"+o.getProductCodeTo()+"'");
				}
			}
			if( !Utils.isNull(o.getTypeSearch()).equals("ALL")){
				sql.append("\n AND D.ITEM_CHECK ='"+o.getTypeSearch()+"'");
			}
			
			if("STOCKMCQuery".equalsIgnoreCase(pageName)){
			   sql.append("\n ORDER BY H.stock_date desc ,H.customer_code,H.store_code asc, D.line_id asc");
			}else{
			   sql.append("\n ORDER BY H.stock_date,H.customer_code,H.store_code , D.line_id asc");
			}
			
			//logger.debug("sql:"+sql);
			if(logger.isDebugEnabled()){
				FileUtil.writeFile("d://dev_temp//temp//sql.sql", sql.toString(), "TIS-620");
			}
			
			ps = conn.prepareStatement(sql.toString());
			rst = ps.executeQuery();
			while(rst.next()) {
			   no++;
			   h = new StockMCBean();
			   h.setStockDate(DateUtil.stringValueChkNull(rst.getDate("stock_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
			   h.setCustomerCode(Utils.isNull(rst.getString("customer_code")));
			   h.setCustomerName(Utils.isNull(rst.getString("customer_name")));
			   h.setStoreCode(Utils.isNull(rst.getString("store_code")));
			   h.setStoreName(Utils.isNull(rst.getString("branch_name")));
			   h.setCreateUser(Utils.isNull(rst.getString("create_user")));
			   //h.setMcName(rst.getString("mc_name"));  
			   
			   h.setId(rst.getInt("id"));
			   h.setNo(no);
			   h.setLineId(rst.getInt("line_id"));
			   h.setBrand(Utils.isNull(rst.getString("brand")));
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
			   h.setExpireDate1(DateUtil.stringValueChkNull(rst.getDate("EXPIRE_DATE_1"), DateUtil.DD_MM_YYYY_WITH_SLASH));
			   //2
			   h.setFrontendQty2(Utils.isNullDoubleStrToBlank(Utils.decimalFormat(rst.getDouble("FRONTEND_QTY_2"), Utils.format_current_no_disgit)));
			   h.setUom2(Utils.isNull(rst.getString("uom_2")));
			   h.setExpireDate2(DateUtil.stringValueChkNull(rst.getDate("EXPIRE_DATE_2"), DateUtil.DD_MM_YYYY_WITH_SLASH));
			   //3
			   h.setFrontendQty3(Utils.isNullDoubleStrToBlank(Utils.decimalFormat(rst.getDouble("FRONTEND_QTY_3"), Utils.format_current_no_disgit)));
			   h.setUom3(Utils.isNull(rst.getString("uom_3")));
			   h.setExpireDate3(DateUtil.stringValueChkNull(rst.getDate("EXPIRE_DATE_3"), DateUtil.DD_MM_YYYY_WITH_SLASH));
			
			   h.setItemCheck(Utils.isNull(rst.getString("item_check")));
			   h.setDateInStore(DateUtil.stringValueChkNull(rst.getDate("date_In_Store"), DateUtil.DD_MM_YYYY_WITH_SLASH));
			   h.setDateInStoreQty(Utils.isNullDoubleStrToBlank(Utils.decimalFormat(rst.getDouble("date_In_Store_qty"), Utils.format_current_no_disgit)));
			   
			   h.setReasonNDesc(Utils.isNull(rst.getString("reason_n_desc")));
			   h.setReasonDDesc(Utils.isNull(rst.getString("reason_d_desc")));
			   h.setNote(Utils.isNull(rst.getString("note")));
			   
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
		if( !Utils.isNull(o.getCustomerName()).equals("")){
			sql.append("\n and C.customer_name LIKE  '%"+Utils.isNull(o.getCustomerName())+"%'");
		}
		if( !Utils.isNull(o.getStoreCode()).equals("")){
			sql.append("\n and H.store_code = '"+Utils.isNull(o.getStoreCode())+"'");
		}
		if( !Utils.isNull(o.getStoreName()).equals("")){
			sql.append("\n and C.branch_name LIKE  '%"+Utils.isNull(o.getStoreName())+"%'");
		}
	     if(!Utils.isNull(o.getStockDate()).equalsIgnoreCase("")){
			Date endDate = DateUtil.parse(o.getStockDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th);
			String endDateStr = DateUtil.stringValue(endDate, DateUtil.DD_MM_YYYY_WITH_SLASH);
			sql.append("\n and H.stock_date = to_date('"+endDateStr+"','dd/mm/yyyy')");
		}
	     if( !Utils.isNull(o.getCreateUser()).equals("")){
	    	 sql.append("\n and H.create_user = '"+o.getCreateUser()+"'");
	     }
		return sql;
	}
	
	public static List<StockMCBean> getProductMCItemList(Connection conn,String customerCode,String brand,int headerId) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		StockMCBean h = null;
		List<StockMCBean> itemList = new ArrayList<StockMCBean>();
		int no= 0;
		try {
			sql.append("\n select H.* FROM PENSBI.MC_ITEM_CUST H");
			sql.append("\n WHERE customer_code = '"+customerCode+"'");
			sql.append("\n AND status ='ACTIVE' ");
			if( !Utils.isNull(brand).equals("")){
				sql.append("\n AND brand = '"+brand+"'");
			}
			if(headerId != 0){
				sql.append("\n AND item_pens not in(");
				sql.append("\n   SELECT product_code from PENSBI.MC_COUNTSTK_DETAIL");
				sql.append("\n   WHERE ID = "+headerId);
				sql.append("\n )");
			}
			sql.append("\n order by h.item_pens ");
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
			   h.setStartDate(DateUtil.stringValueChkNull(rst.getDate("start_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
			  
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
	
	
	public static StockMCBean getProductMCItemInfo(String customerCode,String productCode) throws Exception {
		PreparedStatement ps = null;
		ResultSet rst = null;
		StringBuilder sql = new StringBuilder();
		StockMCBean h = null;
		Connection conn = null;
		int no= 0;
		try {
			conn = DBConnection.getInstance().getConnectionApps();
			sql.append("\n select H.* FROM PENSBI.MC_ITEM_CUST H");
			sql.append("\n WHERE customer_code = '"+customerCode+"'");
			sql.append("\n AND item_pens = '"+productCode+"'");
			sql.append("\n AND status = 'ACTIVE'");
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
			   h.setStartDate(DateUtil.stringValueChkNull(rst.getDate("start_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
			   h.setBrand(Utils.isNull(rst.getString("brand")));
			   h.setMasterLegQty(Utils.isNull(rst.getString("master_leg_qty")));
			   h.setUom(Utils.isNull(rst.getString("uom")));
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
			   h.setStartDate(DateUtil.stringValueChkNull(rst.getDate("start_date"), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
			   h.setExpireDate1(DateUtil.stringValueChkNull(rst.getDate("expired"), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th));
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
	
	public StockMCBean saveByProduct(StockMCBean head) throws Exception {
		Connection conn = null;
		try{
			conn = DBConnection.getInstance().getConnection();
			conn.setAutoCommit(false);
	
			//replace '&#8203;'= space : what in put from ??
			//head.setMcName(head.getMcName().replaceAll("//&#8203;", ""));
			
			if(head.getId() == 0){
				head = insertStockMCHead(conn , head);
			}else{
				updateStockMCHead(conn , head);
			}
	
			int maxLineId = getMaxLineId(conn, head.getId());//getMaxId by ID
			StockMCBean line = head;
			logger.debug("check id:"+head.getId());
			logger.debug("check lineId:"+line.getLineId());
			
			//clear value case change TEM_CHECK ,Y,N,D
			if(line.getItemCheck().equals("Y")){
			   //clear N
				line.setReasonNId("");
				line.setDateInStore("");
				line.setDateInStoreQty("");
				
			   //clear D
				line.setReasonDId("");
			}else if(line.getItemCheck().equals("N")){
				//clear Y
				line.setLegQty("");
				line.setInStoreQty("");
				line.setBackendQty("");
				line.setUom("");
				line.setFrontendQty1("");
				line.setUom1("");
				line.setExpireDate1("");
				line.setFrontendQty2("");
				line.setUom2("");
				line.setExpireDate2("");
				line.setFrontendQty3("");
				line.setUom3("");
				line.setExpireDate3("");
				
				//clear D
				line.setReasonDId("");
			}else if(line.getItemCheck().equals("D")){
				//clear Y
				line.setLegQty("");
				line.setInStoreQty("");
				line.setBackendQty("");
				line.setUom("");
				line.setFrontendQty1("");
				line.setUom1("");
				line.setExpireDate1("");
				line.setFrontendQty2("");
				line.setUom2("");
				line.setExpireDate2("");
				line.setFrontendQty3("");
				line.setUom3("");
				line.setExpireDate3("");
				
				//clear N
				line.setReasonNId("");
				line.setDateInStore("");
				line.setDateInStoreQty("");
			}
			
			if(line.getLineId() ==0){
			   maxLineId++;
			   line.setId(head.getId());
		       line.setLineId((maxLineId));
		       
		       insertStockMCLine(conn,line);
			}else{
			   line.setId(head.getId());
			   updateStockMCLine(conn,line);
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
			model.setId(SequenceProcessAll.getIns().getNextValue("MC_COUNTSTK_HEADER").intValue());

			ps = conn.prepareStatement(sql.toString());
			
			ps.setLong(++index, model.getId());
			ps.setDate(++index, new java.sql.Date(DateUtil.parse(model.getStockDate(), DateUtil.DD_MM_YYYY_WITH_SLASH,DateUtil.local_th).getTime()));
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
			sql.append(" EXPIRE_DATE_2, FRONTEND_QTY_3, UOM_3, EXPIRE_DATE_3,CREATE_DATE  \n");//
			sql.append(" ,CREATE_USER,BARCODE,ITEM_CHECK,date_In_Store,date_In_Store_qty,reason_n_id,reason_d_id");
			sql.append(" ,note)");
			sql.append(" VALUES (?,?,?,?,?,?,"
					          + "?,?,?,?,?,?,"
					          + "?,?,?,?,?,?,"
					          + "?,?,?,?,?,?,"
					          + "?,?,?,?,?) \n");//

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
				 ps.setDate(++index, new java.sql.Date(DateUtil.parse(line.getExpireDate1(), DateUtil.DD_MM_YYYY_WITH_SLASH).getTime()));
			}else{
				 ps.setNull(++index,java.sql.Types.DATE);
			}
			/*** 2 **************************************************************/
			ps.setDouble(++index, Utils.convertStrToDouble(line.getFrontendQty2()));//15
			ps.setString(++index, line.getUom2());//16
			if( !Utils.isNull(line.getExpireDate2()).equals("")){//17
				 ps.setDate(++index, new java.sql.Date(DateUtil.parse(line.getExpireDate2(), DateUtil.DD_MM_YYYY_WITH_SLASH).getTime()));
			}else{
				 ps.setNull(++index,java.sql.Types.DATE);
			}
			/*** 3 **************************************************************/
			ps.setDouble(++index, Utils.convertStrToDouble(line.getFrontendQty3()));//18
			ps.setString(++index, line.getUom3());//19
			if( !Utils.isNull(line.getExpireDate3()).equals("")){//20
				 ps.setDate(++index, new java.sql.Date(DateUtil.parse(line.getExpireDate3(), DateUtil.DD_MM_YYYY_WITH_SLASH).getTime()));
			}else{
				 ps.setNull(++index,java.sql.Types.DATE);
			}
			ps.setTimestamp(++index, new java.sql.Timestamp(new Date().getTime()));//21
			ps.setString(++index, line.getCreateUser());//22
			ps.setString(++index, line.getBarcode());//23
			
			ps.setString(++index, Utils.isNull(line.getItemCheck()));//24
			
			if( !Utils.isNull(line.getDateInStore()).equals("")){//25
				 ps.setDate(++index, new java.sql.Date(DateUtil.parse(line.getDateInStore(), DateUtil.DD_MM_YYYY_WITH_SLASH).getTime()));
			}else{
				 ps.setNull(++index,java.sql.Types.DATE);
			}
			ps.setDouble(++index, Utils.convertStrToDouble(line.getDateInStoreQty()));//26
			ps.setString(++index, Utils.isNull(line.getReasonNId()));//27
			ps.setString(++index, Utils.isNull(line.getReasonDId()));//28
			ps.setString(++index, Utils.isNull(line.getNote()));//29
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
			sql.append(" PROMOTION_PRICE=? ,LEG_QTY=? ,IN_STORE_QTY=?, BACKEND_QTY=?  , UOM=? ,  \n");//5
			sql.append(" FRONTEND_QTY_1=? , UOM_1=? , EXPIRE_DATE_1=? ,FRONTEND_QTY_2=? ,UOM_2=? , \n");//10
			sql.append(" EXPIRE_DATE_2=? , FRONTEND_QTY_3=? , UOM_3=? , EXPIRE_DATE_3=? ,UPDATE_DATE=? ,UPDATE_USER =? , \n");//16
			sql.append(" ITEM_CHECK=? ,date_In_Store=? ,date_In_Store_qty = ? ,reason_n_id = ? ,reason_d_id = ?, \n");
			sql.append(" NOTE=? \n");
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
				 ps.setDate(++index, new java.sql.Date(DateUtil.parse(line.getExpireDate1(), DateUtil.DD_MM_YYYY_WITH_SLASH).getTime()));
			}else{
				 ps.setNull(++index,java.sql.Types.DATE);
			}
			/*** 2 **************************************************************/
			ps.setDouble(++index, Utils.convertStrToDouble(line.getFrontendQty2()));
			ps.setString(++index, line.getUom2());
			if( !Utils.isNull(line.getExpireDate2()).equals("")){ //11
				 ps.setDate(++index, new java.sql.Date(DateUtil.parse(line.getExpireDate2(), DateUtil.DD_MM_YYYY_WITH_SLASH).getTime()));
			}else{
				 ps.setNull(++index,java.sql.Types.DATE);
			}
			/*** 3 **************************************************************/
			ps.setDouble(++index, Utils.convertStrToDouble(line.getFrontendQty3()));
			ps.setString(++index, line.getUom3());
			if( !Utils.isNull(line.getExpireDate3()).equals("")){
				 ps.setDate(++index, new java.sql.Date(DateUtil.parse(line.getExpireDate3(), DateUtil.DD_MM_YYYY_WITH_SLASH).getTime()));
			}else{
				 ps.setNull(++index,java.sql.Types.DATE);
			}
			
			ps.setTimestamp(++index, new java.sql.Timestamp(new Date().getTime()));
			ps.setString(++index, line.getUpdateUser());
			ps.setString(++index, Utils.isNull(line.getItemCheck()));
			if( !Utils.isNull(line.getDateInStore()).equals("")){
				 ps.setDate(++index, new java.sql.Date(DateUtil.parse(line.getDateInStore(), DateUtil.DD_MM_YYYY_WITH_SLASH).getTime()));
			}else{
				 ps.setNull(++index,java.sql.Types.DATE);
			}
			ps.setDouble(++index, Utils.convertStrToDouble(line.getDateInStoreQty()));
			ps.setString(++index, Utils.isNull(line.getReasonNId()));
			ps.setString(++index, Utils.isNull(line.getReasonDId()));
			ps.setString(++index, Utils.isNull(line.getNote()));
			
			//key update
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
			sql.append(" WHERE ID = "+id+" AND LINE_ID IN("+SQLHelper.converToTextSqlIn(lineIdDelete)+") \n");

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
