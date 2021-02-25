package com.isecinc.pens.model;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.VisitLine;
import com.pens.util.seq.SequenceProcessAll;

/**
 * MVisitLine Class
 * 
 * @author Aneak.t
 * @version $Id: MVisitLine.java,v 1.0 22/10/2010 00:00:00 aneak.t Exp $
 * 
 */

public class MVisitLine extends I_Model<VisitLine> {

	private static final long serialVersionUID = -1629704924761316791L;

	public static String TABLE_NAME = "t_visit_line";
	public static String COLUMN_ID = "VISIT_LINE_ID";

	// Column
	private String[] columns = { COLUMN_ID, "LINE_NO", "PRODUCT_ID", "ISACTIVE", "CREATED_BY", "UPDATED_BY", "AMOUNT",
			"VISIT_ID", "UOM_ID", "AMOUNT2" };

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public VisitLine find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, VisitLine.class);
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
	public VisitLine[] search(String whereCause) throws Exception {
		List<VisitLine> pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, VisitLine.class);
		if (pos.size() == 0) return null;
		VisitLine[] array = new VisitLine[pos.size()];
		array = pos.toArray(array);
		return array;
	}

	/**
	 * Save
	 * 
	 * @param visitLine
	 * @param activeUserID
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public boolean save(VisitLine visitLine, int activeUserID, Connection conn) throws Exception {
		int id = 0;
		if (visitLine.getId() == 0) {
			id = SequenceProcessAll.getIns().getNextValue("t_visit_line.visit_line_id").intValue();
		} else {
			id = visitLine.getId();
		}

		Object[] values = { id, visitLine.getLineNo(), visitLine.getProduct().getId(),
				visitLine.getIsActive() != null ? visitLine.getIsActive() : "N", activeUserID, activeUserID,
				visitLine.getAmount(), visitLine.getVisitId(), visitLine.getUom().getId(), visitLine.getAmount2() };

		if (super.save(TABLE_NAME, columns, values, visitLine.getId(), conn)) {
			visitLine.setId(id);
		}
		return true;
	}

	/**
	 * Look Up
	 */
	public List<VisitLine> lookUp(int visitId) {
		List<VisitLine> pos = new ArrayList<VisitLine>();
		try {
			String whereCause = " AND VISIT_ID = " + visitId + " ORDER BY LINE_NO ";
			pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, VisitLine.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pos;
	}

	/**
	 * Delete
	 * 
	 * @param deleteId
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public boolean delete(String deleteId, Connection conn) throws Exception {
		return super.delete(TABLE_NAME, COLUMN_ID, deleteId, conn);
	}
}
