package com.isecinc.pens.model;

import java.util.List;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.InventoryTransaction;

/**
 * MInventoryTransaction Class
 * 
 * @author Aneak.t
 * @version $Id: MInventoryTransaction.java,v 1.0 11/10/2010 00:00:00 aneak.t Exp $
 * 
 */

public class MInventoryTransaction extends I_Model<InventoryTransaction>{

	private static final long serialVersionUID = 2082977140447302132L;

	public static String TABLE_NAME = "m_inventory_transaction";
	public static String COLUMN_ID = "Inventory_Transaction_ID";
	
	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public InventoryTransaction find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, InventoryTransaction.class);
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
	public InventoryTransaction[] search(String whereCause) throws Exception {
		List<InventoryTransaction> pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, InventoryTransaction.class);
		if (pos.size() == 0) return null;
		InventoryTransaction[] array = new InventoryTransaction[pos.size()];
		array = pos.toArray(array);
		return array;
	}
}
