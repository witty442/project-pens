package com.isecinc.pens.model;

import java.util.ArrayList;
import java.util.List;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.ModifierAttr;

/**
 * MModifierAttr Class
 * 
 * @author Atiz.b
 * @version $Id: MModifierAttr.java,v 1.0 08/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class MModifierAttr extends I_Model<ModifierAttr> {

	private static final long serialVersionUID = 5856401903008785498L;

	public static String TABLE_NAME = "m_modifier_attr";
	public static String COLUMN_ID = "Modifier_Attr_ID";

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public ModifierAttr find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, ModifierAttr.class);
	}

	/**
	 * Search
	 * 
	 * @param whereCause
	 * @return
	 * @throws Exception
	 */
	public ModifierAttr[] search(String whereCause) throws Exception {
		List<ModifierAttr> pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, ModifierAttr.class);
		if (pos.size() == 0) return null;
		ModifierAttr[] array = new ModifierAttr[pos.size()];
		array = pos.toArray(array);
		return array;
	}

	/**
	 * Get of Line
	 * 
	 * @param modifierLineId
	 * @return
	 */
	public ModifierAttr getOfLine(int modiferId,int modifierLineId) {
		List<ModifierAttr> pos = new ArrayList<ModifierAttr>();
		try {
			String whereCause = " AND MODIFIER_ID = "+modiferId+""
				    + " AND MODIFIER_LINE_ID = " + modifierLineId
					+ "  AND ISEXCLUDE = 'N' ORDER BY MODIFIER_ATTR_ID ";
			pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, ModifierAttr.class);
			if (pos.size() > 0) return pos.get(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ModifierAttr();
	}

	/**
	 * Look Up
	 */
	public List<ModifierAttr> lookUp(int modifierLineId) {
		List<ModifierAttr> pos = new ArrayList<ModifierAttr>();
		try {
			String whereCause = " AND MODIFIER_LINE_ID = " + modifierLineId + " ORDER BY MODIFIER_ATTR_ID ";
			pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, ModifierAttr.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pos;
	}
}
