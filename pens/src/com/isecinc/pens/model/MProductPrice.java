package com.isecinc.pens.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import util.DBCPConnectionProvider;

import com.isecinc.core.Database;
import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.Product;
import com.isecinc.pens.bean.ProductPrice;

/**
 * MProductPrice Class
 * 
 * @author Aneak.t
 * @version $Id: MProductPrice.java,v 1.0 06/10/2010 00:00:00 aneak.t Exp $
 * 
 */

public class MProductPrice extends I_Model<ProductPrice> {

	private static final long serialVersionUID = 1061987589677830892L;

	public static String TABLE_NAME = "m_product_price";
	public static String COLUMN_ID = "Product_Price_ID";

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public ProductPrice find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, ProductPrice.class);
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
	public ProductPrice[] search(String whereCause) throws Exception {
		List<ProductPrice> pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, ProductPrice.class);
		if (pos.size() == 0) return null;
		ProductPrice[] array = new ProductPrice[pos.size()];
		array = pos.toArray(array);
		return array;
	}

	/**
	 * Look Up
	 * 
	 * @param productId
	 * @param pricelistId
	 * @return
	 * @throws Exception
	 */
	public List<ProductPrice> lookUp(int productId, int pricelistId) throws Exception {
		String whereCause = "  and pricelist_id = " + pricelistId;
		whereCause += "  and product_id = " + productId;
		whereCause += "  and isactive = 'Y' ";
		return super.search(TABLE_NAME, COLUMN_ID, whereCause, ProductPrice.class);
	}

	/**
	 * Look up
	 * 
	 * @param productId
	 * @param pricelistId
	 * @param uomId
	 * @return
	 * @throws Exception
	 */
	public ProductPrice lookUp(int productId, int pricelistId, String uomId) throws Exception {
		String whereCause = "  and pricelist_id = " + pricelistId;
		whereCause += "  and product_id = " + productId;
		whereCause += "  and uom_id = '" + uomId + "' ";
		whereCause += "  and isactive = 'Y' ";
		logger.debug("sql:"+whereCause);
		
		List<ProductPrice> pp = super.search(TABLE_NAME, COLUMN_ID, whereCause, ProductPrice.class);
		if (pp.size() > 0) return pp.get(0);
		else return null;
	}

	/**
	 * Get Current Price
	 * 
	 * @param productId
	 * @param pricelistId
	 * @param uomId
	 * @return
	 * @throws Exception
	 */
	public List<ProductPrice> getCurrentPrice(String productId, String pricelistId, String uomId) throws Exception {
		String whereCause = "";
		whereCause += " AND PRODUCT_ID = " + productId;
		whereCause += " AND PRICELIST_ID = " + pricelistId;
		whereCause += " AND UOM_ID = '" + uomId + "' ";
		whereCause += " AND ISACTIVE = 'Y'";
		return super.search(TABLE_NAME, COLUMN_ID, whereCause, ProductPrice.class);
	}
}
