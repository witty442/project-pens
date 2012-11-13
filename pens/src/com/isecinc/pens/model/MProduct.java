package com.isecinc.pens.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import util.ConvertNullUtil;
import util.DBCPConnectionProvider;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.Product;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.web.moveorder.MoveOrderProductCatalog;
import com.isecinc.pens.web.sales.bean.ProductCatalog;

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
	
	public List<ProductCatalog> getProductCatalogByBrand(String productCatCode,String orderDate,String pricelistId ,User u) throws Exception {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rst = null;
		
		List<ProductCatalog> productL = new ArrayList<ProductCatalog>();
		
		String sql = " \n SELECT pd.PRODUCT_ID , pd.NAME as PRODUCT_NAME , pd.CODE as PRODUCT_CODE , pp1.PRICE as PRICE1 , pp1.UOM_ID as UOM1 ,pp2.PRICE as PRICE2 , pp2.UOM_ID as UOM2 " +
					 "\n FROM M_Product pd "+
					 "\n INNER JOIN M_Product_Price pp1 ON pd.Product_ID = pp1.Product_ID AND pp1.UOM_ID = pd.UOM_ID "+
					 "\n LEFT JOIN m_product_price pp2 ON pp2.PRODUCT_ID = pd.PRODUCT_ID AND pp2.PRICELIST_ID = pp1.PRICELIST_ID AND pp2.ISACTIVE = 'Y' AND pp2.UOM_ID <> pd.UOM_ID "+
					 "\n WHERE pp1.ISACTIVE = 'Y' AND pd.CODE LIKE '"+productCatCode+"%' AND pp1.PRICELIST_ID = "+pricelistId+" "+
					 "\n AND COALESCE(pp2.UOM_ID,pp1.UOM_ID) IN (SELECT UOM_ID FROM M_UOM_CONVERSION con WHERE con.PRODUCT_ID = pd.PRODUCT_ID AND COALESCE(con.DISABLE_DATE,now()) >= now()) "+
					 "\n AND pd.CODE NOT IN (SELECT DISTINCT CODE FROM M_PRODUCT_UNUSED WHERE type ='"+u.getRole().getKey()+"') "+
					 "\n ORDER BY pd.CODE ";
		
        logger.debug("sql:"+sql);
		conn = new DBCPConnectionProvider().getConnection(conn);
		try {
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			while(rst.next()){
				ProductCatalog catalog = new ProductCatalog();
				catalog.setProductId(rst.getInt("PRODUCT_ID"));
				catalog.setProductName(rst.getString("PRODUCT_NAME"));
				catalog.setProductCode( rst.getString("PRODUCT_CODE"));
				catalog.setPrice1(rst.getDouble("PRICE1"));
				catalog.setPrice2(rst.getDouble("PRICE2"));
				catalog.setUom1(rst.getString("UOM1"));
				catalog.setUom2(ConvertNullUtil.convertToString(rst.getString("UOM2")));
				
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
		
		String sql = " \n SELECT pd.PRODUCT_ID , pd.NAME as PRODUCT_NAME , pd.CODE as PRODUCT_CODE , pp1.PRICE as PRICE1 , pp1.UOM_ID as UOM1 ,pp2.PRICE as PRICE2 , pp2.UOM_ID as UOM2 " +
					 " \n FROM M_Product pd "+
					 " \n INNER JOIN M_Product_Price pp1 ON pd.Product_ID = pp1.Product_ID AND pp1.UOM_ID = pd.UOM_ID "+
					 " \n LEFT JOIN m_product_price pp2 ON pp2.PRODUCT_ID = pd.PRODUCT_ID AND pp2.PRICELIST_ID = pp1.PRICELIST_ID AND pp2.ISACTIVE = 'Y' AND pp2.UOM_ID <> pd.UOM_ID "+
					 " \n WHERE pp1.ISACTIVE = 'Y' AND pd.CODE LIKE '"+productCatCode+"%' AND pp1.PRICELIST_ID = "+pricelistId+" "+
					 " \n AND COALESCE(pp2.UOM_ID,pp1.UOM_ID) IN (SELECT UOM_ID FROM M_UOM_CONVERSION con WHERE con.PRODUCT_ID = pd.PRODUCT_ID AND COALESCE(con.DISABLE_DATE,now()) >= now()) "+
					 " \n AND pd.CODE NOT IN (SELECT DISTINCT CODE FROM M_PRODUCT_UNUSED WHERE type ='"+u.getRole().getKey()+"') "+
					 " \n ORDER BY pd.CODE ";

		logger.debug("sql:"+sql);
		
		conn = new DBCPConnectionProvider().getConnection(conn);
		try {
			stmt = conn.createStatement();
			rst = stmt.executeQuery(sql);
			while(rst.next()){
				MoveOrderProductCatalog catalog = new MoveOrderProductCatalog();
				catalog.setProductId(rst.getInt("PRODUCT_ID"));
				catalog.setProductName(rst.getString("PRODUCT_NAME"));
				catalog.setProductCode( rst.getString("PRODUCT_CODE"));
				catalog.setPrice1(rst.getDouble("PRICE1"));
				catalog.setPrice2(rst.getDouble("PRICE2"));
				catalog.setUom1(rst.getString("UOM1"));
				catalog.setUom2(ConvertNullUtil.convertToString(rst.getString("UOM2")));
				
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
}
