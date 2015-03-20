package com.isecinc.pens.model;

import java.util.List;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.Qualifier;

/**
 * MQualifier Class
 * 
 * @author Atiz.b
 * @version $Id: MQualifier.java,v 1.0 08/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class MQualifier extends I_Model<Qualifier> {

	private static final long serialVersionUID = 5856401903008785498L;

	public static String TABLE_NAME = "m_qualifier";
	public static String COLUMN_ID = "Qualifier_ID";

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Qualifier find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, Qualifier.class);
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
	public Qualifier[] search(String whereCause) throws Exception {
		List<Qualifier> pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, Qualifier.class);
		if (pos.size() == 0) return null;
		Qualifier[] array = new Qualifier[pos.size()];
		array = pos.toArray(array);
		return array;
	}
}
