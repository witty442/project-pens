package com.isecinc.pens.model;

import java.util.ArrayList;
import java.util.List;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.SubInventory;

/**
 * Sub Inventory Model
 * 
 * @author atiz.b
 * @version $Id: MSubInventory.java,v 1.0 29/10/2010 15:52:00 atiz.b Exp $
 * 
 */
public class MSubInventory extends I_Model<SubInventory> {

	private static final long serialVersionUID = 809948235588958790L;

	public static String TABLE_NAME = "m_sub_inventory";
	public static String COLUMN_ID = "SUB_INVENTORY_ID";

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public SubInventory find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, SubInventory.class);
	}

	/**
	 * Look UP
	 */
	public List<SubInventory> lookUp() {
		List<SubInventory> pos = new ArrayList<SubInventory>();
		try {
			String whereCause = " AND TYPE <> 'VAN'  ORDER BY NAME ";
			pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, SubInventory.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pos;
	}

	/**
	 * Look UP
	 */
	public List<SubInventory> lookUp(String vanCode) {
		List<SubInventory> pos = new ArrayList<SubInventory>();
		try {
			String whereCause = " AND TYPE <> 'VAN' OR NAME = '" + vanCode + "'  ORDER BY TYPE, NAME ";
			pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, SubInventory.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pos;
	}

	
	/**
	 * Look UP
	 */
	public List<SubInventory> lookUpVAN(String vanCode) {
		List<SubInventory> pos = new ArrayList<SubInventory>();
		try {
			String whereCause = " AND NAME = '" + vanCode + "'  ORDER BY TYPE, NAME ";
			pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, SubInventory.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pos;
	}
}
