package com.isecinc.pens.model;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import util.DateToolsUtil;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.Member;
import com.isecinc.pens.bean.MemberRenew;
import com.isecinc.pens.process.SequenceProcess;

/**
 * MMemberRenew Class
 * 
 * @author Aneak.t
 * @version $Id: MMemberRenew.java,v 1.0 01/11/2010 00:00:00 aneak.t Exp $
 * 
 */

public class MMemberRenew extends I_Model<MemberRenew> {

	private static final long serialVersionUID = 2150214083631640638L;

	public static String TABLE_NAME = "m_member_renew";
	public static String COLUMN_ID = "RENEW_ID";

	// Column
	private String[] columns = { COLUMN_ID, "CUSTOMER_ID", "EXPIRED_DATE", "RENEWED_DATE", "MEMBER_TYPE",
			"APPLIED_DATE", "CREATED_BY", "UPDATED_BY" };

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public MemberRenew find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, MemberRenew.class);
	}
	
	public MemberRenew findByIdLastRenew(String customerId) throws Exception {
		String whereCause = "";
		whereCause += " AND customer_id = " + customerId + " \n order by expired_date desc";
		MemberRenew[] pos = search(whereCause);
		if (pos != null) return pos[0];
		return null;
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
	public MemberRenew[] search(String whereCause) throws Exception {
		List<MemberRenew> pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, MemberRenew.class);
		if (pos.size() == 0) return null;
		MemberRenew[] array = new MemberRenew[pos.size()];
		array = pos.toArray(array);
		return array;
	}

	/**
	 * Save
	 * 
	 * @param member
	 * @param activeUserID
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public boolean save(MemberRenew memberRenew, String userCode, int activeUserID, Connection conn) throws Exception {
		int id = 0;
		if (memberRenew.getId() == 0) {
			id = SequenceProcess.getNextValue(TABLE_NAME);
		} else {
			id = memberRenew.getId();
		}

		Object[] values = { id, memberRenew.getMember().getId(),
				DateToolsUtil.convertToTimeStamp(memberRenew.getExpiredDate()),
				DateToolsUtil.convertToTimeStamp(memberRenew.getRenewedDate()), memberRenew.getMemberType(),
				DateToolsUtil.convertToTimeStamp(memberRenew.getAppliedDate()), activeUserID, activeUserID };

		if (super.save(TABLE_NAME, columns, values, memberRenew.getId(), conn)) {
			memberRenew.setId(id);
		}
		return true;
	}

	/**
	 * Look Up
	 * 
	 * @param memberId
	 * @return
	 */
	public List<MemberRenew> lookUp(int memberId) {
		List<MemberRenew> pos = new ArrayList<MemberRenew>();
		try {
			String whereCause = " AND CUSTOMER_ID = " + memberId + " ORDER BY RENEWED_DATE DESC ";
			pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, MemberRenew.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pos;
	}
	
	public List<MemberRenew> lookUpLast(int memberId) {
		List<MemberRenew> pos = new ArrayList<MemberRenew>();
		try {
			String whereCause = " AND CUSTOMER_ID = " + memberId + " and m_member_renew.renew_id = (select max(m.renew_id) from m_member_renew m where m_member_renew.CUSTOMER_ID = m.CUSTOMER_ID) ORDER BY RENEWED_DATE DESC ";
			pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, MemberRenew.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pos;
	}
}
