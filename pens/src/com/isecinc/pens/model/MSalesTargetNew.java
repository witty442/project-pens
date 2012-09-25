package com.isecinc.pens.model;

import java.util.List;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.SalesTargetNew;

/**
 * MSalesTargetNew Class
 * 
 * @author atiz.b
 * @version $Id: MSalesTargetNew.java,v 1.0 09/12/2010 00:00:00 atiz.b Exp $
 * 
 */

public class MSalesTargetNew extends I_Model<SalesTargetNew> {

	private static final long serialVersionUID = 7398827241670213562L;

	public static String TABLE_NAME = "m_sales_target_new";
	public static String COLUMN_ID = "SALES_TARGET_ID";

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public SalesTargetNew find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, SalesTargetNew.class);
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
	public SalesTargetNew[] search(String whereCause) throws Exception {
		List<SalesTargetNew> pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, SalesTargetNew.class);
		if (pos.size() == 0) return null;
		SalesTargetNew[] array = new SalesTargetNew[pos.size()];
		array = pos.toArray(array);
		return array;
	}

}
