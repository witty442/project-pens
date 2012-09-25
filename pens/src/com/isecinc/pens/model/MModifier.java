package com.isecinc.pens.model;

import java.util.List;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.Modifier;

/**
 * MModifier Class
 * 
 * @author Atiz.b
 * @version $Id: MModifier.java,v 1.0 08/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class MModifier extends I_Model<Modifier> {

	private static final long serialVersionUID = 5856401903008785498L;

	public static String TABLE_NAME = "m_modifier";
	public static String COLUMN_ID = "Modifier_ID";

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Modifier find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, Modifier.class);
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
	public Modifier[] search(String whereCause) throws Exception {
		List<Modifier> pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, Modifier.class);
		if (pos.size() == 0) return null;
		Modifier[] array = new Modifier[pos.size()];
		array = pos.toArray(array);
		return array;
	}
}
