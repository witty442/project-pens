package com.isecinc.pens.model;

import java.util.List;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.DocType;

/**
 * DocType Model
 * 
 * @author Atiz.b
 * @version $Id: MDocType.java,v 1.0 08/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class MDocType extends I_Model<DocType> {

	private static final long serialVersionUID = -242861840858521265L;

	public static String TABLE_NAME = "c_doctype";
	public static String COLUMN_ID = "DOCTYPE_ID";

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public DocType find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, DocType.class);
	}

	/**
	 * Active Document
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<DocType> getActiveDocument() throws Exception {
		String whereCause = "AND ISACTIVE = 'Y' ";
		return super.search(TABLE_NAME, COLUMN_ID, whereCause, DocType.class);
	}

	/**
	 * Search
	 * 
	 * @param whereCause
	 * @param classes
	 * @return
	 * @throws Exception
	 */
	public List<DocType> search(String whereCause) throws Exception {
		return super.search(TABLE_NAME, COLUMN_ID, whereCause, DocType.class);
	}

}
