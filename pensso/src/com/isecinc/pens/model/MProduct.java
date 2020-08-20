package com.isecinc.pens.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.PopupBean;
import com.isecinc.pens.bean.Product;
import com.isecinc.pens.bean.UOM;
import com.isecinc.pens.bean.UOMConversion;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.inf.helper.DBConnection;
import com.isecinc.pens.web.moveorder.MoveOrderProductCatalog;
import com.isecinc.pens.web.requisitionProduct.RequisitionProductCatalog;
import com.isecinc.pens.web.sales.CheckStockOnhandProcess;
import com.isecinc.pens.web.sales.bean.ProductCatalog;
import com.pens.util.ControlCode;
import com.pens.util.ConvertNullUtil;
import com.pens.util.DBCPConnectionProvider;
import com.pens.util.DBConnectionApps;
import com.pens.util.FileUtil;
import com.pens.util.Utils;

/**
 * MProduct Class
 * 
 * @author Aneak.t
 * @version $Id: MProduct.java,v 1.0 06/10/2010 00:00:00 aneak.t Exp $
 * 
 */

public class MProduct extends I_Model<Product>{

	private static final long serialVersionUID = 3881159581550423821L;
	
	public static String TABLE_NAME = "m_product";
	public static String COLUMN_ID = "Product_ID";
	
	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Product find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, Product.class);
	}
	public Product findOpt(String id) throws Exception {
		Statement stmt = null;
		ResultSet rst = null;
		Connection conn = null;
		Product p = null;
		try{
			String sql ="\n select * from m_product where product_id ="+id ;
			//logger.debug("sql:"+sql);
			conn = DBConnection.getInstance().getConnection();
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			if(rst.next()){
				p = new Product(rst);
			}
			return p;
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				rst.close();
		        conn.close();
				stmt.close();
			} catch (Exception e2) {}
		}
	}

	public Product find(Connection conn,String id) throws Exception {
		return super.find(conn,id, TABLE_NAME, COLUMN_ID, Product.class);
	}
	/**
	 * Search
	 * 
	 * @param id
	 * @param tableName
	 * @param columnID
	 * @return
	 * @throws Exception
	 */
	public Product[] search(String whereCause) throws Exception {
		List<Product> pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, Product.class);
		if (pos.size() == 0) return null;
		Product[] array = new Product[pos.size()];
		array = pos.toArray(array);
		return array;
	}
	
	/**
	 *   Note ** Class ProductCatalog  is encode in class unicode some function display problem thai ?? 
	 */
	public  List<ProductCatalog> getProductCatalogByBrand(String productCatCode,String orderDate,String pricelistId ,User u,boolean isCustHaveProductSpecial) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		String stockArr[] = null;
		boolean checkStockFlag = false;
		
		List<ProductCatalog> productL = new ArrayList<ProductCatalog>();
		StringBuffer sql = new StringBuffer("");
		sql.append("\n SELECT A.* FROM( ");
		sql.append(" \n SELECT pd.PRODUCT_ID , pd.NAME as PRODUCT_NAME , pd.CODE as PRODUCT_CODE , pp1.PRICE as PRICE1 ");
		sql.append(" \n , pp1.UOM_ID as UOM1 ,pp2.PRICE as PRICE2 , pp2.UOM_ID as UOM2 ");
		sql.append(" \n ,(CASE WHEN st.product_id  <> '' THEN '0' ELSE '1' end )as target_sort ");
		sql.append(" \n ,(CASE WHEN st.product_id  <> '' THEN 'Y' ELSE '' end )as target ");
		sql.append("\n  ,pd.taxable ");
		//sql.append("\n  ,oh.quantity,oh.primary_quantity,oh.second_quantity");
		sql.append("\n FROM PENSSO.M_Product pd ");
		sql.append("\n INNER JOIN PENSSO.M_Product_Price pp1 ON pd.Product_ID = pp1.Product_ID ");
		sql.append("\n AND pp1.UOM_ID = pd.UOM_ID ");
		sql.append("\n LEFT JOIN PENSSO.m_product_price pp2 ON pp2.PRODUCT_ID = pd.PRODUCT_ID ");
		sql.append("\n AND pp2.PRICELIST_ID = pp1.PRICELIST_ID AND pp2.ISACTIVE = 'Y' AND pp2.UOM_ID <> pd.UOM_ID ");
		sql.append("\n LEFT OUTER JOIN PENSSO.m_sales_target_new st ON  st.Product_ID = pp1.Product_ID ");
		sql.append("\n AND TO_CHAR(st.target_from, 'YYYYMM')  = TO_CHAR(sysdate, 'YYYYMM')");
		
		/*sql.append("\n LEFT JOIN APPS.xxpens_inv_onhand_z00_v oh ON oh.inventory_item_id = pd.product_id ");
		sql.append("\n and oh.subinventory_code='Z001'");*/
		
		sql.append("\n WHERE pp1.ISACTIVE = 'Y' AND pd.CODE LIKE '"+productCatCode+"%' AND pp1.PRICELIST_ID = "+pricelistId+" ");
		sql.append("\n AND ( ");
		sql.append("\n    pp1.UOM_ID IN ( ");
		sql.append("\n      SELECT UOM_ID FROM PENSSO.M_UOM_CONVERSION con WHERE con.PRODUCT_ID = pd.PRODUCT_ID AND COALESCE(con.DISABLE_DATE,sysdate) >= sysdate ");
		sql.append("\n     ) ");
		sql.append("\n     OR");
		sql.append("\n     pp2.UOM_ID IN ( ");
		sql.append("\n        SELECT UOM_ID FROM PENSSO.M_UOM_CONVERSION con WHERE con.PRODUCT_ID = pd.PRODUCT_ID AND COALESCE(con.DISABLE_DATE,sysdate) >= sysdate ");
		sql.append("\n      ) ");
		sql.append("\n   )");
        sql.append("\n  AND pd.CODE NOT IN (SELECT DISTINCT CODE FROM PENSSO.M_PRODUCT_UNUSED WHERE type ='"+u.getRole().getKey()+"') ");
        sql.append("\n )A");
        //Case product Special
        if(isCustHaveProductSpecial){
	        sql.append("\n WHERE A.product_id in(");
	        sql.append("\n select M.product_id from PENSSO.M_product_center C ,PENSSO.M_product M where M.code = C.code");
	        sql.append("\n ) ");
        }else{
        	sql.append("\n WHERE A.product_id not in(");
 	        sql.append("\n select M.product_id from PENSSO.M_product_center C ,PENSSO.M_product M where M.code = C.code");
 	        sql.append("\n ) ");
        }
        sql.append("\n ORDER BY A.target_sort,A.PRODUCT_CODE ");
		
        logger.debug("sql:"+sql);
        
        //control code check stock or not
        if(ControlCode.canExecuteMethod("Stock", "checkStock")){
        	checkStockFlag = true;
        }
        
		conn = DBConnectionApps.getInstance().getConnection();
		try {
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while(rst.next()){
				ProductCatalog catalog = new ProductCatalog();
				catalog.setTarget(rst.getString("target"));
				catalog.setProductId(rst.getInt("PRODUCT_ID"));
				catalog.setProductName(rst.getString("PRODUCT_NAME"));
				catalog.setProductCode( rst.getString("PRODUCT_CODE"));
				catalog.setPrice1(rst.getDouble("PRICE1"));
				catalog.setPrice2(rst.getDouble("PRICE2"));
				catalog.setUom1(rst.getString("UOM1"));
				catalog.setUom2(ConvertNullUtil.convertToString(rst.getString("UOM2")));
				catalog.setTaxable(ConvertNullUtil.convertToString(rst.getString("TAXABLE")));
				
				UOMConversion  uc1 = new MUOMConversion().getCurrentConversion(conn,catalog.getProductId(), "CTN");//default to CTN
			    UOMConversion  uc2 = new MUOMConversion().getCurrentConversion(conn,catalog.getProductId(), catalog.getUom2());

			    if(uc1 != null){
			    	catalog.setUom1ConvRate(Utils.decimalFormat(uc1.getConversionRate(),Utils.format_number_no_disgit));
			    	//logger.debug("uom1ConRate:"+catalog.getUom1ConvRate());
			    }
			    if(uc2 != null){
			    	catalog.setUom2ConvRate(Utils.decimalFormat(uc2.getConversionRate(),Utils.format_number_no_disgit));
			    	//logger.debug("uom2ConRate:"+catalog.getUom2ConvRate());
			    }
			    // TUB/ no conversion
			    if(uc1.getConversionRate()==0 && uc2.getConversionRate()==0){
			    	catalog.setUom2ConvRate("1");//default
			    }
			    
			    //For no or check stock onhand
			    if(checkStockFlag){
			    	//OLD METHOD
					stockArr = CheckStockOnhandProcess.checkStockOnhandItemProc(catalog.getProductCode(),catalog.getProductId()+"", catalog.getUom1(), catalog.getUom2());
					if(stockArr != null ){
						//logger.debug("stockArr[0]:"+stockArr[0]+",stockArr[1]:"+stockArr[1]);
						catalog.setStockOnhandQty(Utils.convertStrToDouble(stockArr[0]));
						catalog.setStockOnhandQty1(Utils.convertStrToInt(stockArr[1]));
						catalog.setStockOnhandQty2(Utils.convertStrToInt(stockArr[2]));
					}
					
			    	//NEW 
			    /*	catalog.setStockOnhandQty(rst.getDouble("quantity"));
			    	catalog.setStockOnhandQty1(rst.getInt("primary_quantity"));
					catalog.setStockOnhandQty2(rst.getInt("second_quantity"));*/
			    }
				productL.add(catalog);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		
		return productL;
	}
	
	/**
	 *   Note ** Class ProductCatalog  is encode in class unicode some function display problem thai ?? 
	 */
	public  List<ProductCatalog> getProductCatalogBySubBrand(String subBrandCode,String orderDate,String pricelistId ,User u,boolean isCustHaveProductSpecial) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		String stockArr[] = null;
		boolean checkStockFlag = false;
		
		List<ProductCatalog> productL = new ArrayList<ProductCatalog>();
		StringBuffer sql = new StringBuffer("");
		sql.append("\n SELECT A.* FROM( ");
		sql.append(" \n SELECT pd.PRODUCT_ID , pd.NAME as PRODUCT_NAME , pd.CODE as PRODUCT_CODE , pp1.PRICE as PRICE1 ");
		sql.append(" \n , pp1.UOM_ID as UOM1 ,pp2.PRICE as PRICE2 , pp2.UOM_ID as UOM2 ");
		sql.append(" \n ,(CASE WHEN st.product_id is not null THEN '0' ELSE '1' end )as target_sort ");
		sql.append(" \n ,(CASE WHEN st.product_id is not null THEN 'Y' ELSE '' end )as target ");
		sql.append("\n  ,pd.taxable ");
		sql.append("\n  ,(select count(*) as c from PENSSO.M_PRODUCT_DIVIDE mp where mp.product_code = pd.code) as check_input_half ");
		//sql.append("\n  ,oh.quantity,oh.primary_quantity,oh.second_quantity");
		sql.append("\n FROM PENSSO.M_Product pd ");
		sql.append("\n INNER JOIN PENSSO.M_Product_Price pp1 ON pd.Product_ID = pp1.Product_ID ");
		sql.append("\n AND pp1.UOM_ID = pd.UOM_ID ");
		sql.append("\n LEFT JOIN PENSSO.m_product_price pp2 ON pp2.PRODUCT_ID = pd.PRODUCT_ID ");
		sql.append("\n AND pp2.PRICELIST_ID = pp1.PRICELIST_ID AND pp2.ISACTIVE = 'Y' AND pp2.UOM_ID <> pd.UOM_ID ");
		sql.append("\n LEFT OUTER JOIN PENSSO.m_sales_target_new st ON  st.Product_ID = pp1.Product_ID ");
		sql.append("\n AND TO_CHAR(st.target_from, 'YYYYMM')  = TO_CHAR(sysdate, 'YYYYMM')");
		sql.append("\n AND USER_ID ="+u.getId());
		
		/*sql.append("\n LEFT JOIN APPS.xxpens_inv_onhand_z00_v oh ON oh.inventory_item_id = pd.product_id ");
		sql.append("\n and oh.subinventory_code='Z001'");*/
		
		sql.append("\n WHERE pp1.ISACTIVE = 'Y'  AND pp1.PRICELIST_ID = "+pricelistId+" ");
		sql.append("\n AND pd.product_id in(");
		sql.append("\n   select inventory_item_id FROM PENSBI.XXPENS_BI_MST_SUBBRAND sb ");
		sql.append("\n   WHERE subbrand_no ='"+subBrandCode+"' ");
		sql.append("\n ) ");
		
		sql.append("\n AND ( ");
		sql.append("\n    pp1.UOM_ID IN ( ");
		sql.append("\n      SELECT UOM_ID FROM PENSSO.M_UOM_CONVERSION con WHERE con.PRODUCT_ID = pd.PRODUCT_ID AND COALESCE(con.DISABLE_DATE,sysdate) >= sysdate ");
		sql.append("\n     ) ");
		sql.append("\n     OR");
		sql.append("\n     pp2.UOM_ID IN ( ");
		sql.append("\n        SELECT UOM_ID FROM PENSSO.M_UOM_CONVERSION con WHERE con.PRODUCT_ID = pd.PRODUCT_ID AND COALESCE(con.DISABLE_DATE,sysdate) >= sysdate ");
		sql.append("\n      ) ");
		sql.append("\n   )");
        sql.append("\n  AND pd.CODE NOT IN (SELECT DISTINCT CODE FROM PENSSO.M_PRODUCT_UNUSED WHERE type ='"+u.getRole().getKey()+"') ");
        sql.append("\n )A");
        //Case product Special
        if(isCustHaveProductSpecial){
	        sql.append("\n WHERE A.product_id in(");
	        sql.append("\n select M.product_id from PENSSO.M_product_center C ,PENSSO.M_product M where M.code = C.code");
	        sql.append("\n ) ");
        }else{
        	sql.append("\n WHERE A.product_id not in(");
 	        sql.append("\n select M.product_id from PENSSO.M_product_center C ,PENSSO.M_product M where M.code = C.code");
 	        sql.append("\n ) ");
        }
        sql.append("\n ORDER BY A.target_sort,A.PRODUCT_CODE ");
		
        logger.debug("sql:"+sql);
        
        //control code check stock or not
        if(ControlCode.canExecuteMethod("Stock", "checkStock")){
        	checkStockFlag = true;
        }
        
		conn = DBConnectionApps.getInstance().getConnection();
		try {
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while(rst.next()){
				ProductCatalog catalog = new ProductCatalog();
				catalog.setTarget(rst.getString("target"));
				catalog.setProductId(rst.getInt("PRODUCT_ID"));
				catalog.setProductName(rst.getString("PRODUCT_NAME"));
				catalog.setProductCode( rst.getString("PRODUCT_CODE"));
				catalog.setPrice1(rst.getDouble("PRICE1"));
				catalog.setPrice2(rst.getDouble("PRICE2"));
				catalog.setUom1(rst.getString("UOM1"));
				catalog.setUom2(ConvertNullUtil.convertToString(rst.getString("UOM2")));
				catalog.setTaxable(ConvertNullUtil.convertToString(rst.getString("TAXABLE")));
				//check_input_half (found in m_product_devide no check input half 
				catalog.setCheckInputHalf(rst.getInt("check_input_half")==0?"Y":"N");
				
				UOMConversion  uc1 = new MUOMConversion().getCurrentConversion(conn,catalog.getProductId(), "CTN");//default to CTN
			    UOMConversion  uc2 = new MUOMConversion().getCurrentConversion(conn,catalog.getProductId(), catalog.getUom2());

			    if(uc1 != null){
			    	catalog.setUom1ConvRate(Utils.decimalFormat(uc1.getConversionRate(),Utils.format_number_no_disgit));
			    	//logger.debug("uom1ConRate:"+catalog.getUom1ConvRate());
			    }
			    if(uc2 != null){
			    	catalog.setUom2ConvRate(Utils.decimalFormat(uc2.getConversionRate(),Utils.format_number_no_disgit));
			    	//logger.debug("uom2ConRate:"+catalog.getUom2ConvRate());
			    }
			    // TUB/ no conversion
			    if(uc1.getConversionRate()==0 && uc2.getConversionRate()==0){
			    	catalog.setUom2ConvRate("1");//default
			    }
			    
			    //For no or check stock onhand
			    if(checkStockFlag){
			    	//OLD METHOD
					stockArr = CheckStockOnhandProcess.checkStockOnhandItemProc(catalog.getProductCode(),catalog.getProductId()+"", catalog.getUom1(), catalog.getUom2());
					if(stockArr != null ){
						//logger.debug("stockArr[0]:"+stockArr[0]+",stockArr[1]:"+stockArr[1]);
						catalog.setStockOnhandQty(Utils.convertStrToDouble(stockArr[0]));
						catalog.setStockOnhandQty1(Utils.convertStrToInt(stockArr[1]));
						catalog.setStockOnhandQty2(Utils.convertStrToInt(stockArr[2]));
					}
					
			    	//NEW 
			    /*	catalog.setStockOnhandQty(rst.getDouble("quantity"));
			    	catalog.setStockOnhandQty1(rst.getInt("primary_quantity"));
					catalog.setStockOnhandQty2(rst.getInt("second_quantity"));*/
			    }
				productL.add(catalog);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		
		return productL;
	}
	
	public List<MoveOrderProductCatalog> getMoveOrderProductCatalogByBrand(String productCatCode,String orderDate,String pricelistId,User u) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		
		List<MoveOrderProductCatalog> productL = new ArrayList<MoveOrderProductCatalog>();
		
		StringBuffer sql = new StringBuffer("");
		sql.append("\n SELECT A.* FROM( ");
		sql.append(" \n SELECT pd.PRODUCT_ID , pd.NAME as PRODUCT_NAME , pd.CODE as PRODUCT_CODE , pp1.PRICE as PRICE1 , pp1.UOM_ID as UOM1 ,pp2.PRICE as PRICE2 , pp2.UOM_ID as UOM2 ");
		sql.append(" \n ,(CASE WHEN st.product_id  <> '' THEN '0' ELSE '1' end )as target_sort ");
		sql.append(" \n ,(CASE WHEN st.product_id  <> '' THEN 'Y' ELSE '' end )as target ");
		sql.append(" \n FROM M_Product pd ");
		sql.append(" \n INNER JOIN M_Product_Price pp1 ON pd.Product_ID = pp1.Product_ID AND pp1.UOM_ID = pd.UOM_ID ");
		sql.append(" \n LEFT JOIN m_product_price pp2 ON pp2.PRODUCT_ID = pd.PRODUCT_ID AND pp2.PRICELIST_ID = pp1.PRICELIST_ID AND pp2.ISACTIVE = 'Y' AND pp2.UOM_ID <> pd.UOM_ID ");
		sql.append(" \n LEFT OUTER JOIN m_sales_target_new st ON  st.Product_ID = pp1.Product_ID AND DATE_FORMAT(st.target_from, '%Y%m') = DATE_FORMAT(NOW(), '%Y%m')");
		sql.append(" \n WHERE pp1.ISACTIVE = 'Y' AND pd.CODE LIKE '"+productCatCode+"%' AND pp1.PRICELIST_ID = "+pricelistId+" ");
			
		sql.append("\n AND ( ");
		sql.append("\n    pp1.UOM_ID IN ( ");
		sql.append("\n      SELECT UOM_ID FROM M_UOM_CONVERSION con WHERE con.PRODUCT_ID = pd.PRODUCT_ID AND COALESCE(con.DISABLE_DATE,now()) >= now() ");
		sql.append("\n     ) ");
		sql.append("\n     OR");
		sql.append("\n     pp2.UOM_ID IN ( ");
		sql.append("\n        SELECT UOM_ID FROM M_UOM_CONVERSION con WHERE con.PRODUCT_ID = pd.PRODUCT_ID AND COALESCE(con.DISABLE_DATE,now()) >= now() ");
		sql.append("\n      ) ");
		sql.append("\n   )");
				
		sql.append(" \n AND pd.CODE NOT IN (SELECT DISTINCT CODE FROM M_PRODUCT_UNUSED WHERE type ='"+u.getRole().getKey()+"') ");
		sql.append("\n )A");
	    sql.append("\n ORDER BY A.target_sort,A.PRODUCT_CODE ");

		logger.debug("sql:"+sql);
		
		conn = new DBCPConnectionProvider().getConnection(conn);
		try {
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while(rst.next()){
				MoveOrderProductCatalog catalog = new MoveOrderProductCatalog();
				catalog.setProductId(rst.getInt("PRODUCT_ID"));
				catalog.setTarget(rst.getString("target"));
				catalog.setProductName(rst.getString("PRODUCT_NAME"));
				catalog.setProductCode( rst.getString("PRODUCT_CODE"));
				catalog.setPrice1(rst.getDouble("PRICE1"));
				catalog.setPrice2(rst.getDouble("PRICE2"));
				catalog.setUom1(rst.getString("UOM1"));
				catalog.setUom2(ConvertNullUtil.convertToString(rst.getString("UOM2")));
				
				//find PacQty2
				if(!"".equals(catalog.getUom2())){
					catalog.setPacQty2(new MUOMConversion().getCapacity(catalog.getProductId(), catalog.getUom1(), catalog.getUom2()));
				}
				
				productL.add(catalog);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		
		return productL;
	}
	
	public List<MoveOrderProductCatalog> getStockProductCatalogByBrand(String productCatCode,String orderDate,String pricelistId,User u) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		
		List<MoveOrderProductCatalog> productL = new ArrayList<MoveOrderProductCatalog>();
		
		StringBuffer sql = new StringBuffer("");
		sql.append("\n SELECT A.* FROM( ");
		sql.append(" \n SELECT pd.PRODUCT_ID , pd.NAME as PRODUCT_NAME , pd.CODE as PRODUCT_CODE , pp1.PRICE as PRICE1 , pp1.UOM_ID as UOM1 ,pp2.PRICE as PRICE2 , pp2.UOM_ID as UOM2 ");
		sql.append(" \n ,(CASE WHEN st.product_id  <> '' THEN '0' ELSE '1' end )as target_sort ");
		sql.append(" \n ,(CASE WHEN st.product_id  <> '' THEN 'Y' ELSE '' end )as target ");
		sql.append(" \n FROM M_Product pd ");
		sql.append(" \n INNER JOIN M_Product_Price pp1 ON pd.Product_ID = pp1.Product_ID AND pp1.UOM_ID = pd.UOM_ID ");
		sql.append(" \n LEFT JOIN m_product_price pp2 ON pp2.PRODUCT_ID = pd.PRODUCT_ID AND pp2.PRICELIST_ID = pp1.PRICELIST_ID AND pp2.ISACTIVE = 'Y' AND pp2.UOM_ID <> pd.UOM_ID ");
		sql.append(" \n LEFT OUTER JOIN m_sales_target_new st ON  st.Product_ID = pp1.Product_ID AND DATE_FORMAT(st.target_from, '%Y%m') = DATE_FORMAT(NOW(), '%Y%m')");
		sql.append(" \n WHERE pp1.ISACTIVE = 'Y' AND pd.CODE LIKE '"+productCatCode+"%' AND pp1.PRICELIST_ID = "+pricelistId+" ");
			
		sql.append("\n AND ( ");
		sql.append("\n    pp1.UOM_ID IN ( ");
		sql.append("\n      SELECT UOM_ID FROM M_UOM_CONVERSION con WHERE con.PRODUCT_ID = pd.PRODUCT_ID AND COALESCE(con.DISABLE_DATE,now()) >= now() ");
		sql.append("\n     ) ");
		sql.append("\n     OR");
		sql.append("\n     pp2.UOM_ID IN ( ");
		sql.append("\n        SELECT UOM_ID FROM M_UOM_CONVERSION con WHERE con.PRODUCT_ID = pd.PRODUCT_ID AND COALESCE(con.DISABLE_DATE,now()) >= now() ");
		sql.append("\n      ) ");
		sql.append("\n   )");
				
		sql.append(" \n AND pd.CODE NOT IN (SELECT DISTINCT CODE FROM M_PRODUCT_UNUSED WHERE type ='"+u.getRole().getKey()+"') ");
		sql.append("\n )A");
	    sql.append("\n ORDER BY A.target_sort,A.PRODUCT_CODE ");

		logger.debug("sql:"+sql);
		
		conn = new DBCPConnectionProvider().getConnection(conn);
		try {
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while(rst.next()){
				MoveOrderProductCatalog catalog = new MoveOrderProductCatalog();
				catalog.setProductId(rst.getInt("PRODUCT_ID"));
				catalog.setTarget(rst.getString("target"));
				catalog.setProductName(rst.getString("PRODUCT_NAME"));
				catalog.setProductCode( rst.getString("PRODUCT_CODE"));
				catalog.setPrice1(rst.getDouble("PRICE1"));
				catalog.setPrice2(rst.getDouble("PRICE2"));
				catalog.setUom1(rst.getString("UOM1"));
				catalog.setUom2(ConvertNullUtil.convertToString(rst.getString("UOM2")));
				
				//find PacQty2
				if(!"".equals(catalog.getUom2())){
					catalog.setPacQty2(new MUOMConversion().getCapacity(catalog.getProductId(), catalog.getUom1(), catalog.getUom2()));
				}
				
				productL.add(catalog);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		
		return productL;
	}
	
	public List<RequisitionProductCatalog> getRequisitionProductCatalogByBrand(String productCatCode,String orderDate,String pricelistId,User u) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		
		List<RequisitionProductCatalog> productL = new ArrayList<RequisitionProductCatalog>();
		
		StringBuffer sql = new StringBuffer("");
		sql.append("\n SELECT A.* FROM( ");
		sql.append(" \n SELECT pd.PRODUCT_ID , pd.NAME as PRODUCT_NAME , pd.CODE as PRODUCT_CODE , pp1.PRICE as PRICE1 , pp1.UOM_ID as UOM1 ,pp2.PRICE as PRICE2 , pp2.UOM_ID as UOM2 ");
		sql.append(" \n ,(CASE WHEN st.product_id  <> '' THEN '0' ELSE '1' end )as target_sort ");
		sql.append(" \n ,(CASE WHEN st.product_id  <> '' THEN 'Y' ELSE '' end )as target ");
		sql.append(" \n FROM M_Product pd ");
		sql.append(" \n INNER JOIN M_Product_Price pp1 ON pd.Product_ID = pp1.Product_ID AND pp1.UOM_ID = pd.UOM_ID ");
		sql.append(" \n LEFT JOIN m_product_price pp2 ON pp2.PRODUCT_ID = pd.PRODUCT_ID AND pp2.PRICELIST_ID = pp1.PRICELIST_ID AND pp2.ISACTIVE = 'Y' AND pp2.UOM_ID <> pd.UOM_ID ");
		sql.append(" \n LEFT OUTER JOIN m_sales_target_new st ON  st.Product_ID = pp1.Product_ID AND DATE_FORMAT(st.target_from, '%Y%m') = DATE_FORMAT(NOW(), '%Y%m')");
		sql.append( " \n WHERE pp1.ISACTIVE = 'Y' AND pd.CODE LIKE '"+productCatCode+"%' AND pp1.PRICELIST_ID = "+pricelistId+" ");
			
		sql.append("\n AND ( ");
		sql.append("\n    pp1.UOM_ID IN ( ");
		sql.append("\n      SELECT UOM_ID FROM M_UOM_CONVERSION con WHERE con.PRODUCT_ID = pd.PRODUCT_ID AND COALESCE(con.DISABLE_DATE,now()) >= now() ");
		sql.append("\n     ) ");
		sql.append("\n     OR");
		sql.append("\n     pp2.UOM_ID IN ( ");
		sql.append("\n        SELECT UOM_ID FROM M_UOM_CONVERSION con WHERE con.PRODUCT_ID = pd.PRODUCT_ID AND COALESCE(con.DISABLE_DATE,now()) >= now() ");
		sql.append("\n      ) ");
		sql.append("\n   )");
				
		sql.append(" \n AND pd.CODE NOT IN (SELECT DISTINCT CODE FROM M_PRODUCT_UNUSED WHERE type ='"+u.getRole().getKey()+"') ");
		sql.append("\n )A");
	    sql.append("\n ORDER BY A.target_sort,A.PRODUCT_CODE ");

		logger.debug("sql:"+sql);
		
		conn = new DBCPConnectionProvider().getConnection(conn);
		try {
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			while(rst.next()){
				RequisitionProductCatalog catalog = new RequisitionProductCatalog();
				catalog.setProductId(rst.getInt("PRODUCT_ID"));
				catalog.setTarget(rst.getString("target"));
				catalog.setProductName(rst.getString("PRODUCT_NAME"));
				catalog.setProductCode( rst.getString("PRODUCT_CODE"));
				catalog.setPrice1(rst.getDouble("PRICE1"));
				catalog.setPrice2(rst.getDouble("PRICE2"));
				catalog.setUom1(rst.getString("UOM1"));
				catalog.setUom2(ConvertNullUtil.convertToString(rst.getString("UOM2")));
				
				//find PacQty2
				if(!"".equals(catalog.getUom2())){
					catalog.setPacQty2(new MUOMConversion().getCapacity(catalog.getProductId(), catalog.getUom1(), catalog.getUom2()));
				}
				
				productL.add(catalog);
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				conn.close();
			} catch (Exception e2) {}
		}
		
		return productL;
	}
	
	public Product getStockProduct(String productCode,User user) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		Product catalog = null;
		StringBuffer sql = new StringBuffer("");
		String conversionRate = "";
		try {
	
			sql.append("\n SELECT A.* FROM( ");
			sql.append("\n SELECT pd.PRODUCT_ID , pd.NAME as PRODUCT_NAME , pd.CODE as PRODUCT_CODE");
			sql.append("\n , pp1.UOM_ID as UOM1 , pp2.UOM_ID as UOM2 ");
			sql.append("\n FROM M_Product pd ");
			
			sql.append("\n  INNER JOIN (");
			sql.append("\n    select distinct Product_ID,uom_id ,ISACTIVE from M_Product_Price ");
			sql.append("\n    where ISACTIVE = 'Y' ");
			sql.append("\n  )pp1 ON pd.Product_ID = pp1.Product_ID AND pp1.UOM_ID = pd.UOM_ID ");
			
			sql.append("\n  LEFT JOIN (");
			sql.append("\n    select distinct Product_ID,uom_id ,ISACTIVE from M_Product_Price ");
			sql.append("\n    where ISACTIVE = 'Y' ");
			sql.append("\n  )pp2 ON pd.Product_ID = pp2.Product_ID AND pp2.UOM_ID <> pd.UOM_ID ");
			
			sql.append("\n WHERE pd.code = '"+productCode+"'");
			sql.append("\n AND ( ");
			sql.append("\n    pp1.UOM_ID IN ( ");
			sql.append("\n      SELECT UOM_ID FROM M_UOM_CONVERSION con WHERE con.PRODUCT_ID = pd.PRODUCT_ID AND COALESCE(con.DISABLE_DATE,now()) >= now() ");
			sql.append("\n     ) ");
			sql.append("\n     OR");
			sql.append("\n     pp2.UOM_ID IN ( ");
			sql.append("\n        SELECT UOM_ID FROM M_UOM_CONVERSION con WHERE con.PRODUCT_ID = pd.PRODUCT_ID AND COALESCE(con.DISABLE_DATE,now()) >= now() ");
			sql.append("\n      ) ");
			sql.append("\n   )");
			sql.append(" \n AND pd.CODE NOT IN (SELECT DISTINCT CODE FROM M_PRODUCT_UNUSED WHERE type ='"+user.getRole().getKey()+"') ");
			sql.append("\n )A");
		    sql.append("\n ORDER BY A.PRODUCT_CODE ");
			logger.debug("sql:"+sql);
			
			conn = new DBCPConnectionProvider().getConnection(conn);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if(rst.next()){
				catalog = new Product();
				catalog.setId(rst.getInt("PRODUCT_ID"));
				catalog.setName(rst.getString("PRODUCT_NAME"));
				catalog.setCode( rst.getString("PRODUCT_CODE"));
				catalog.setUom1(rst.getString("UOM1"));
				catalog.setUom2(ConvertNullUtil.convertToString(rst.getString("UOM2")));
				
				
				catalog.setConversionRate(getConversionRate(catalog.getId(),catalog.getUom1(),catalog.getUom2()));
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		} finally {
			try {
				stmt.close();
				rst.close();
				conn.close();
			} catch (Exception e2) {}
		}
		
		return catalog;
	}
	
	public  String getConversionRate(int productId,String uom1,String uom2) throws Exception{
		String conversionRate = "";
		try{
			 UOMConversion  uc1 = new MUOMConversion().getCurrentConversion(productId, uom1);
			 conversionRate = Utils.decimalFormat(uc1.getConversionRate(),Utils.format_number_no_disgit);
			 if( !Utils.isNull(uom2).equals("")){
				  UOMConversion  uc2 = new MUOMConversion().getCurrentConversion(productId, uom2);
				  conversionRate += "/"+Utils.decimalFormat(uc2.getConversionRate(),Utils.format_number_no_disgit);
			 }
			 return conversionRate;
		}catch(Exception e){
			throw e;
		}
		
	}
	public Product getStockReturnProduct(String productCode,User user) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		Product catalog = null;
		StringBuffer sql = new StringBuffer("");
		String conversionRate = "";
		try {
	
			sql.append("\n SELECT A.* FROM( ");
			sql.append("\n SELECT pd.PRODUCT_ID , pd.NAME as PRODUCT_NAME , pd.CODE as PRODUCT_CODE");
			sql.append("\n , pp1.UOM_ID as UOM1 , pp2.UOM_ID as UOM2 ,pp1.price ");
			sql.append("\n FROM M_Product pd ");
			
			sql.append("\n  INNER JOIN (");
			sql.append("\n    select distinct Product_ID,uom_id ,ISACTIVE ,price from M_Product_Price ");
			sql.append("\n    where ISACTIVE = 'Y' ");
			sql.append("\n  )pp1 ON pd.Product_ID = pp1.Product_ID AND pp1.UOM_ID = pd.UOM_ID ");
			
			sql.append("\n  LEFT JOIN (");
			sql.append("\n    select distinct Product_ID,uom_id ,ISACTIVE from M_Product_Price ");
			sql.append("\n    where ISACTIVE = 'Y' ");
			sql.append("\n  )pp2 ON pd.Product_ID = pp2.Product_ID AND pp2.UOM_ID <> pd.UOM_ID ");
			
			sql.append("\n WHERE pd.code = '"+productCode+"'");
			sql.append("\n AND ( ");
			sql.append("\n    pp1.UOM_ID IN ( ");
			sql.append("\n      SELECT UOM_ID FROM M_UOM_CONVERSION con WHERE con.PRODUCT_ID = pd.PRODUCT_ID AND COALESCE(con.DISABLE_DATE,now()) >= now() ");
			sql.append("\n     ) ");
			sql.append("\n     OR");
			sql.append("\n     pp2.UOM_ID IN ( ");
			sql.append("\n        SELECT UOM_ID FROM M_UOM_CONVERSION con WHERE con.PRODUCT_ID = pd.PRODUCT_ID AND COALESCE(con.DISABLE_DATE,now()) >= now() ");
			sql.append("\n      ) ");
			sql.append("\n   )");
			sql.append(" \n AND pd.CODE NOT IN (SELECT DISTINCT CODE FROM M_PRODUCT_UNUSED WHERE type ='"+user.getRole().getKey()+"') ");
			sql.append("\n )A");
		    sql.append("\n ORDER BY A.PRODUCT_CODE ");
			//logger.debug("sql:"+sql);
			
			conn = new DBCPConnectionProvider().getConnection(conn);
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql.toString());
			if(rst.next()){
				catalog = new Product();
				catalog.setId(rst.getInt("PRODUCT_ID"));
				catalog.setName(rst.getString("PRODUCT_NAME"));
				catalog.setCode( rst.getString("PRODUCT_CODE"));
				catalog.setUom1(rst.getString("UOM1"));
				catalog.setUom2(ConvertNullUtil.convertToString(rst.getString("UOM2")));
				
				//uom1 pac
				UOMConversion  uc1 = new MUOMConversion().getCurrentConversion(catalog.getId(), catalog.getUom1());
				conversionRate = Utils.decimalFormat(uc1.getConversionRate(),Utils.format_number_no_disgit);
				catalog.setUom1Pac(conversionRate);
				catalog.setUom1ConvRate(uc1.getConversionRate()+"");
				
				//uom2 pac
			    UOMConversion  uc2 = new MUOMConversion().getCurrentConversion(catalog.getId(), catalog.getUom2());
			    conversionRate = Utils.decimalFormat(uc2.getConversionRate(),Utils.format_number_no_disgit);
			    catalog.setUom2Pac(conversionRate);
			    catalog.setUom2ConvRate(uc2.getConversionRate()+"");
			    
			    //ctn price from t_order_line
			   // catalog.setUom1Price(Utils.decimalFormat(rst.getDouble("price"),Utils.format_current_2_disgit));
			    
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		} finally {
			try {
				stmt.close();
				rst.close();
				conn.close();
			} catch (Exception e2) {}
		}
		
		return catalog;
	}
	
	public static Product getProductInfo(Connection conn,Product c) throws Exception {
		return getProductInfoModel(conn, c);
	}
	public static Product getProductInfo(Product c) throws Exception {
		Connection conn = null;
		try{
		    conn = DBConnectionApps.getInstance().getConnection();
		    return getProductInfoModel(conn, c);
		}catch(Exception e){
			throw e;
		}finally{
			conn.close();
		}
	}
	public static Product getProductInfoModel(Connection conn,Product c) throws Exception {
		Product item = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuilder sql = new StringBuilder();
		int pricelistId = 0;
		try {
			pricelistId = new MPriceList().getCurrentPriceList("CR").getId();
			
			sql.append("\n SELECT A.* FROM( ");
			sql.append(" \n SELECT pd.PRODUCT_ID , pd.NAME as PRODUCT_NAME , pd.CODE as PRODUCT_CODE , pp1.PRICE as PRICE1 , pp1.UOM_ID as UOM1 ,pp2.PRICE as PRICE2 , pp2.UOM_ID as UOM2 ");
			sql.append(" \n ,(CASE WHEN st.product_id  <> '' THEN '0' ELSE '1' end )as target_sort ");
			sql.append(" \n ,(CASE WHEN st.product_id  <> '' THEN 'Y' ELSE '' end )as target ");
			sql.append("\n  ,pd.taxable ");
			sql.append("\n FROM PENSSO.M_Product pd ");
			sql.append("\n INNER JOIN PENSSO.M_Product_Price pp1 ON pd.Product_ID = pp1.Product_ID AND pp1.UOM_ID = pd.UOM_ID ");
			sql.append("\n LEFT JOIN PENSSO.m_product_price pp2 ON pp2.PRODUCT_ID = pd.PRODUCT_ID ");
			sql.append("\n AND pp2.PRICELIST_ID = pp1.PRICELIST_ID AND pp2.ISACTIVE = 'Y' AND pp2.UOM_ID <> pd.UOM_ID ");
			sql.append("\n LEFT OUTER JOIN PENSSO.m_sales_target_new st ON  st.Product_ID = pp1.Product_ID ");
			sql.append("\n AND TO_CHAR(st.target_from, 'YYYYMM')  = TO_CHAR(sysdate, 'YYYYMM')");
			
			sql.append("\n WHERE pp1.ISACTIVE = 'Y' AND pd.CODE = '"+c.getCode()+"' AND pp1.PRICELIST_ID = "+pricelistId+" ");
			sql.append("\n AND ( ");
			sql.append("\n    pp1.UOM_ID IN ( ");
			sql.append("\n      SELECT UOM_ID FROM PENSSO.M_UOM_CONVERSION con WHERE con.PRODUCT_ID = pd.PRODUCT_ID AND COALESCE(con.DISABLE_DATE,sysdate) >= sysdate ");
			sql.append("\n     ) ");
			sql.append("\n     OR");
			sql.append("\n     pp2.UOM_ID IN ( ");
			sql.append("\n        SELECT UOM_ID FROM PENSSO.M_UOM_CONVERSION con WHERE con.PRODUCT_ID = pd.PRODUCT_ID AND COALESCE(con.DISABLE_DATE,sysdate) >= sysdate ");
			sql.append("\n      ) ");
			sql.append("\n   )");
	        sql.append("\n  AND pd.CODE NOT IN (SELECT DISTINCT CODE FROM PENSSO.M_PRODUCT_UNUSED ) ");
	        sql.append("\n )A");
	        sql.append("\n ORDER BY A.target_sort,A.PRODUCT_CODE ");
			
	        //logger.debug("sql:"+sql);
	        
			ps = conn.prepareStatement(sql.toString());
			rs = ps.executeQuery();
			if(rs.next()){
				item = new Product();
				item.setId(rs.getInt("product_id"));
				item.setCode(Utils.isNull(rs.getString("product_code")));
				item.setName(Utils.isNull(rs.getString("product_name")));
				item.setUom1(Utils.isNull(rs.getString("uom1")));
				item.setUom2(Utils.isNull(rs.getString("uom2")));
				//item.setPrice(p.getPrice1()+"/"+p.getPrice2());
			}
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				ps.close();
				rs.close();
			} catch (Exception e) {}
		}
		return item;
	}
}
