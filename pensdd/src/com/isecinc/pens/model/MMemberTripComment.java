package com.isecinc.pens.model;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import util.ConvertNullUtil;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.MemberTripComment;
import com.isecinc.pens.process.SequenceProcess;

/**
 * MMemberTripComment Class
 * 
 * @author atiz.b
 * @version $Id: MMemberTripComment.java,v 1.0 26/01/2011 00:00:00 atiz.b Exp $
 * 
 */
public class MMemberTripComment extends I_Model<MemberTripComment> {

	private static final long serialVersionUID = -4119649113903274964L;

	public static String TABLE_NAME = "t_member_trip_comment";
	public static String COLUMN_ID = "TRIP_COMMENT_ID";

	// Column Sales Online Side active
	private String[] columns = { COLUMN_ID, "TRIP_NO", "ORDER_ID", "TRIP_COMMENT", "CREATED_BY", "UPDATED_BY" };

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public MemberTripComment find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, MemberTripComment.class);
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
	public MemberTripComment[] search(String whereCause) throws Exception {
		List<MemberTripComment> pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, MemberTripComment.class);
		if (pos.size() == 0) return null;
		MemberTripComment[] array = new MemberTripComment[pos.size()];
		array = pos.toArray(array);
		return array;
	}

	/**
	 * Save
	 * 
	 * @param address
	 * @param activeUserID
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public boolean save(MemberTripComment tripComment, int activeUserID, Connection conn) throws Exception {
		int id = 0;
		if (tripComment.getId() == 0) {
			id = SequenceProcess.getNextValue("trip_comment");
		} else {
			id = tripComment.getId();
		}
		Object[] values = { id, tripComment.getTripNo(), tripComment.getOrderId(),
				ConvertNullUtil.convertToString(tripComment.getTripComment()).trim(), activeUserID, activeUserID };
		if (super.save(TABLE_NAME, columns, values, tripComment.getId(), conn)) {
			tripComment.setId(id);
		}
		return true;
	}

	/**
	 * Look Up
	 */
	public List<MemberTripComment> lookUp(int orderId, int tripNo) {
		List<MemberTripComment> pos = new ArrayList<MemberTripComment>();
		try {
			String whereCause = " AND ORDER_ID = " + orderId + " AND TRIP_NO = " + tripNo;
			pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, MemberTripComment.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pos;
	}
}
