package com.isecinc.pens.model;

import java.util.ArrayList;
import java.util.List;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.Province;

/**
 * MProvince Class
 * 
 * @author atiz.b
 * @version $Id: MProvince.java,v 1.0 12/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class MProvince extends I_Model<Province> {

	private static final long serialVersionUID = -6254839726381212634L;

	public static String TABLE_NAME = "m_province";
	public static String COLUMN_ID = "PROVINCE_ID";

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Province find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, Province.class);
	}

	/**
	 * Search
	 * 
	 * @param whereCause
	 * @return
	 * @throws Exception
	 */
	public Province[] search(String whereCause) throws Exception {
		List<Province> pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, Province.class);
		if (pos.size() == 0) return null;
		Province[] array = new Province[pos.size()];
		array = pos.toArray(array);
		return array;
	}

	/**
	 * Look Up
	 */
	public List<Province> lookUp() {
		List<Province> pos = new ArrayList<Province>();
		try {
			String whereCause = "  ORDER BY NAME ";
			pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, Province.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pos;
	}

	/**
	 * Look Up
	 */
	public List<Province> lookUp(int territory) {
		List<Province> pos = new ArrayList<Province>();
		try {
			String whereCause = "   and province_id in (";
			whereCause += " select m.province_id from m_map_province m,c_reference r ";
			whereCause += " where m.reference_id = r.reference_id and r.value = " + territory + ") ";
			whereCause += "  ORDER BY NAME ";
			
			logger.debug("whereClause :"+whereCause);
			
			pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, Province.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pos;
	}
}
