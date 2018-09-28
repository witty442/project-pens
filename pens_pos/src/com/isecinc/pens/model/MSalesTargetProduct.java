package com.isecinc.pens.model;

import java.util.List;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.SalesTargetProduct;

/**
 * MSalesTargetProduct Class
 * 
 * @author Aneak.t
 * @version $Id: MSalesTargetProduct.java,v 1.0 07/10/2010 00:00:00 aneak.t Exp $
 * 
 */

public class MSalesTargetProduct extends I_Model<SalesTargetProduct>{

	private static final long serialVersionUID = 3626617179515519115L;
	
	public static String TABLE_NAME = "m_sales_target_product";
	public static String COLUMN_ID = "Sales_Target_Product_ID";
	
	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public SalesTargetProduct find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, SalesTargetProduct.class);
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
	public SalesTargetProduct[] search(String whereCause) throws Exception {
		List<SalesTargetProduct> pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, SalesTargetProduct.class);
		if (pos.size() == 0) return null;
		SalesTargetProduct[] array = new SalesTargetProduct[pos.size()];
		array = pos.toArray(array);
		return array;
	}

}
