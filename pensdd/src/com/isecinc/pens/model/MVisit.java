package com.isecinc.pens.model;

import java.sql.Connection;
import java.util.List;

import util.ConvertNullUtil;
import util.DateToolsUtil;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.Visit;
import com.isecinc.pens.process.SequenceProcess;
import com.isecinc.pens.process.document.VisitDocumentProcess;

/**
 * MVisit Class
 * 
 * @author Aneak.t
 * @version $Id: MVisit.java,v 1.0 22/10/2010 00:00:00 aneak.t Exp $
 * 
 */

public class MVisit extends I_Model<Visit> {

	private static final long serialVersionUID = -2093311753207981794L;

	public static String TABLE_NAME = "t_visit";
	public static String COLUMN_ID = "VISIT_ID";

	// Column
	private String[] columns = { COLUMN_ID, "CODE", "VISIT_DATE", "VISIT_TIME", "CUSTOMER_ID", "SALES_CLOSED",
			"UNCLOSED_REASON", "INTERFACES", "ISACTIVE", "CREATED_BY", "UPDATED_BY", "USER_ID", "CUSTOMER_NAME",
			"ORDER_TYPE" };

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Visit find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, Visit.class);
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
	public Visit[] search(String whereCause) throws Exception {
		List<Visit> pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, Visit.class);
		if (pos.size() == 0) return null;
		Visit[] array = new Visit[pos.size()];
		array = pos.toArray(array);
		return array;
	}

	/**
	 * Save
	 * 
	 * @param visit
	 * @param activeUserID
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public boolean save(Visit visit, String userCode, int activeUserID, Connection conn) throws Exception {
		int id = 0;
		if (visit.getId() == 0) {
			id = SequenceProcess.getNextValue(TABLE_NAME);
			String prefix = "";
			visit.setCode(new VisitDocumentProcess().getNextDocumentNo(userCode, activeUserID, conn));
		} else {
			id = visit.getId();
		}

		// check duplicate
		if (!checkDocumentDuplicate(TABLE_NAME, COLUMN_ID, "CODE", visit.getCode(), id, conn)) return false;

		Object[] values = { id, ConvertNullUtil.convertToString(visit.getCode()).trim(),
				DateToolsUtil.convertToTimeStamp(visit.getVisitDate()),
				ConvertNullUtil.convertToString(visit.getVisitTime()).trim(), visit.getCustomerId(),
				ConvertNullUtil.convertToString(visit.getSalesClose()).trim(),
				ConvertNullUtil.convertToString(visit.getUnClosedReason()).trim(),
				visit.getInterfaces() != null ? visit.getInterfaces() : "N",
				visit.getIsActive() != null ? visit.getIsActive() : "N", activeUserID, activeUserID, activeUserID,
				ConvertNullUtil.convertToString(visit.getCustomerLabel()), visit.getOrderType() };

		if (super.save(TABLE_NAME, columns, values, visit.getId(), conn)) {
			visit.setId(id);
		}
		return true;
	}
}
