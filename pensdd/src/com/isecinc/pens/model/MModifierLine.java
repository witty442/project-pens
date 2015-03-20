package com.isecinc.pens.model;

import java.util.ArrayList;
import java.util.List;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.ModifierLine;
import com.isecinc.pens.bean.ModifierRelation;

/**
 * MModifierLine Class
 * 
 * @author Atiz.b
 * @version $Id: MModifierLine.java,v 1.0 08/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class MModifierLine extends I_Model<ModifierLine> {

	private static final long serialVersionUID = 5856401903008785498L;

	public static String TABLE_NAME = "m_modifier_line";
	public static String COLUMN_ID = "Modifier_Line_ID";

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public ModifierLine find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, ModifierLine.class);
	}

	/**
	 * Look Up
	 * 
	 * @throws Exception
	 */
	public List<ModifierLine> lookUp(int modifierId) throws Exception {
		List<ModifierLine> pos = new ArrayList<ModifierLine>();
		try {
			String whereCause = " AND MODIFIER_ID = " + modifierId + "  and isactive = 'Y' "
					+ "  and modifier_line_id not in (select modifier_line_to_id from m_relation_modifier) "
					+ " ORDER BY TYPE, LEVEL, BREAK_TYPE, APPLICATION_METHOD ";
			// + " ORDER BY MODIFIER_LINE_ID ";
			pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, ModifierLine.class);
		} catch (Exception e) {
			throw e;
		}
		return pos;
	}

	/**
	 * Get Relate Modifier Line
	 * 
	 * @param modifierLineFromId
	 * @return
	 * @throws Exception
	 */
	public List<ModifierLine> getRelateModifier(int modifierLineFromId) throws Exception {
		List<ModifierLine> pos = new ArrayList<ModifierLine>();
		try {
			List<ModifierRelation> re = new MModifierRelation().lookUp(modifierLineFromId);
			for (ModifierRelation r : re) {
				pos.add(new MModifierLine().find(String.valueOf(r.getLineToId())));
			}
		} catch (Exception e) {
			throw e;
		}
		return pos;
	}
}
