package com.isecinc.pens.model;

import java.sql.Connection;
import java.util.List;

import util.DateToolsUtil;

import com.isecinc.core.model.I_Model;
import com.isecinc.pens.bean.MemberFollow;
import com.isecinc.pens.process.SequenceProcess;

/**
 * MMemberFollow Class
 * 
 * @author Aneak.t
 * @version $Id: MMemberFollow.java,v 1.0 14/12/2010 00:00:00 aneak.t Exp $
 * 
 */

public class MMemberFollow extends I_Model<MemberFollow>{

	private static final long serialVersionUID = 6816413024658989988L;

	public static String TABLE_NAME = "m_member_follow";
	public static String COLUMN_ID = "Member_Follow_ID";

	// Column
	private String[] columns = { COLUMN_ID, "CUSTOMER_ID", "USER_ID",
			"FOLLOW_DATE", "FOLLOW_TIME", "FOLLOW_BY", "FOLLOW_DETAIL",
			"RENEWED", "ISACTIVE", "CREATED_BY", "UPDATED_BY" };

	/**
	 * Find
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public MemberFollow find(String id) throws Exception {
		return super.find(id, TABLE_NAME, COLUMN_ID, MemberFollow.class);
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
	public MemberFollow[] search(String whereCause) throws Exception {
		List<MemberFollow> pos = super.search(TABLE_NAME, COLUMN_ID, whereCause, MemberFollow.class);
		if (pos.size() == 0) return null;
		MemberFollow[] array = new MemberFollow[pos.size()];
		array = pos.toArray(array);
		return array;
	}

	/**
	 * Save
	 * 
	 * @param memberFollow
	 * @param activeUserID
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public boolean save(MemberFollow memberFollow, int activeUserID, Connection conn) throws Exception {
		int id = 0;
		if (memberFollow.getId() == 0) {
			id = SequenceProcess.getNextValue(TABLE_NAME);
		} else {
			id = memberFollow.getId();
		}

		Object[] values = { id, memberFollow.getCustomerId(),
				activeUserID, DateToolsUtil.convertToTimeStamp(memberFollow.getFollowDate()),
				memberFollow.getFollowTime(), memberFollow.getFollowBy(),
				memberFollow.getFollowDetail(), memberFollow.getRenewed() != null ? memberFollow.getRenewed() : "N",
				memberFollow.getIsActive() != null ? memberFollow.getIsActive() : "Y" , activeUserID, activeUserID };

		if (super.save(TABLE_NAME, columns, values, memberFollow.getId(), conn)) {
			memberFollow.setId(id);
		}
		return true;
	}
}
