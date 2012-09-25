package com.isecinc.pens.model;

import java.util.ArrayList;
import java.util.List;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.UOM;

/**
 * Unit Of Measure
 * 
 * @author Aneak.t
 * @version $Id: MUOM.java,v 1.0 06/10/2010 15:52:00 aneak.t Exp $
 * 
 */

public class MUOM extends I_Model<UOM> {

	public static String TABLE_NAME = "m_uom";
	public static String COLUMN_ID = "UOM_ID";
	
	private static final long serialVersionUID = 2037639420365632469L;

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public UOM find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, UOM.class);
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
	public UOM[] search(String whereCause) throws Exception {
		List<UOM> pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, UOM.class);
		if (pos.size() == 0) return null;
		UOM[] array = new UOM[pos.size()];
		array = pos.toArray(array);
		return array;
	}
	
	/**
	 * Look Up
	 */
	public List<UOM> lookUp() {
		List<UOM> pos = new ArrayList<UOM>();
		try {
			String whereCause = "  AND ISACTIVE = 'Y' ORDER BY NAME ";
			pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, UOM.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pos;
	}
}
