package com.isecinc.pens.bean;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Locale;

import util.DateToolsUtil;

import com.isecinc.core.bean.References;
import com.isecinc.core.model.I_PO;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.init.InitialReferences;
import com.isecinc.pens.model.MUser;

/**
 * Member Follow
 * 
 * @author Aneak.t
 * @version $Id: MemberFollow.java,v 1.0 14/12/2010 15:52:00 aneak.t Exp $
 * 
 */

public class MemberFollow extends I_PO implements Serializable {

	private static final long serialVersionUID = 2597787264466572669L;

	/**
	 * Default Constructor
	 */
	public MemberFollow() {

	}

	/**
	 * Constructor with rst
	 * 
	 * @param rst
	 * @throws Exception
	 */
	public MemberFollow(ResultSet rst) throws Exception {

		setId(rst.getInt("MEMBER_FOLLOW_ID"));
		setCustomerId(rst.getInt("CUSTOMER_ID"));
		setUser(new MUser().find(rst.getString("USER_ID")));
		setFollowDate(DateToolsUtil.convertToString(rst.getDate("FOLLOW_DATE")));
		setFollowTime(rst.getString("FOLLOW_TIME"));
		setFollowBy(rst.getString("FOLLOW_BY"));
		setFollowDetail(rst.getString("FOLLOW_DETAIL"));
		setRenewed(rst.getString("RENEWED"));
		setIsActive(rst.getString("ISACTIVE"));
		setCreated(rst.getTimestamp("CREATED"));
		setCreatedBy(new MUser().find(String.valueOf(rst.getInt("CREATED_BY"))));
		setUpdated(rst.getTimestamp("UPDATED"));
		setUpdatedBy(new MUser().find(String.valueOf(rst.getInt("UPDATED_BY"))));

		// set display label
		setDisplayLabel();

	}

	/**
	 * Set display label.
	 */
	protected void setDisplayLabel() throws Exception {
		for (References r : InitialReferences.getReferenes().get(InitialReferences.ACTIVE)) {
			if (r.getKey().equalsIgnoreCase(getIsActive())) {
				setActiveLabel(r.getName());
				break;
			}
		}
	}

	private int id;
	private int customerId;
	private User user = new User();
	private String followDate;
	private String followTime;
	private String followBy;
	private String followDetail;
	private String renewed;
	private String isActive;
	private Timestamp created;
	private User createdBy = new User();
	private Timestamp updated;
	private User updatedBy = new User();

	private String followByLabel;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public String getFollowDate() {
		return followDate;
	}

	public void setFollowDate(String followDate) {
		this.followDate = followDate;
	}

	public String getFollowTime() {
		return followTime;
	}

	public void setFollowTime(String followTime) {
		this.followTime = followTime;
	}

	public String getFollowBy() {
		return followBy;
	}

	public void setFollowBy(String followBy) {
		this.followBy = followBy;
	}

	public String getFollowDetail() {
		return followDetail;
	}

	public void setFollowDetail(String followDetail) {
		this.followDetail = followDetail;
	}

	public String getRenewed() {
		return renewed;
	}

	public void setRenewed(String renewed) {
		this.renewed = renewed;
	}

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

	public Timestamp getCreated() {
		return created;
	}

	public void setCreated(Timestamp created) {
		this.created = created;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getUpdated() {
		return updated;
	}

	public void setUpdated(Timestamp updated) {
		this.updated = updated;
	}

	public User getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(User updatedBy) {
		this.updatedBy = updatedBy;
	}

	public String getFollowByLabel() {
		if (followBy.equals("T")) {
			followByLabel = SystemElements.getCaption(SystemElements.Telephone, new Locale("th", "TH"));
		} else if (followBy.equals("E")) {
			followByLabel = SystemElements.getCaption(SystemElements.Email, new Locale("th", "TH"));
		} else if (followBy.equals("O")) {
			followByLabel = SystemElements.getCaption(SystemElements.Other, new Locale("th", "TH"));
		} else {
			followByLabel = "";
		}

		return followByLabel;
	}

	public void setFollowByLabel(String followByLabel) {
		this.followByLabel = followByLabel;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
