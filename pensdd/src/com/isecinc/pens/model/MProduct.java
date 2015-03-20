package com.isecinc.pens.model;

import java.util.List;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.Product;

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
	
}
