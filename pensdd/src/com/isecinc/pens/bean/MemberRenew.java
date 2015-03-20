package com.isecinc.pens.bean;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.Timestamp;

import util.DateToolsUtil;

import com.isecinc.core.bean.References;
import com.isecinc.core.model.I_PO;
import com.isecinc.pens.init.InitialReferences;
import com.isecinc.pens.model.MMember;
import com.isecinc.pens.model.MUser;

/**
 * Member Renew
 * 
 * @author Aneak.t
 * @version $Id: MemberRenew.java,v 1.0 01/11/2010 15:52:00 aneak.t Exp $
 * 
 */

public class MemberRenew extends I_PO implements Serializable {

	private static final long serialVersionUID = 7761933264113572825L;

	/**
	 * Default Constructor
	 */
	public MemberRenew() {

	}

	/**
	 * Constructor with rst
	 * 
	 * @param rst
	 * @throws Exception
	 */
	public MemberRenew(ResultSet rst) throws Exception {
		// Mandatory
		setId(rst.getInt("RENEW_ID"));
		setMember(new MMember().find(rst.getString("CUSTOMER_ID")));
		setExpiredDate(DateToolsUtil.convertToString(rst.getDate("EXPIRED_DATE")));
		setRenewedDate(DateToolsUtil.convertToString(rst.getDate("RENEWED_DATE")));
		setMemberType(rst.getString("MEMBER_TYPE").trim());
		setAppliedDate(DateToolsUtil.convertToString(rst.getDate("APPLIED_DATE")));
		setCreated(rst.getTimestamp("CREATED"));
		setCreatedBy(new MUser().find(String.valueOf(rst.getInt("CREATED_BY"))));
		setUpdated(rst.getTimestamp("UPDATED"));
		setUpdatedBy(new MUser().find(String.valueOf(rst.getInt("UPDATED_BY"))));

		// set display label
		setDisplayLabel();

	}

	/**
	 * Set Display Label
	 */
	protected void setDisplayLabel() {
		for (References r : InitialReferences.getReferenes().get(InitialReferences.ACTIVE)) {
			if (r.getKey().equalsIgnoreCase(getMember().getIsActive())) {
				setActiveLabel(r.getName());
				break;
			}
		}

		for (References r : InitialReferences.getReferenes().get(InitialReferences.MEMBER_TYPE)) {
			if (r.getKey().equalsIgnoreCase(getMemberType())) {
				setMemberTypeLabel(r.getName());
				break;
			}
		}
		for (References r : InitialReferences.getReferenes().get(InitialReferences.MEMBER_STATUS)) {
			if (r.getKey().equalsIgnoreCase(getMember().getMemberLevel())) {
				setMemberLevelLabel(r.getName());
				break;
			}
		}
	}

	private int id;
	private Member member = new Member();
	private String expiredDate;
	private String renewedDate;
	private String memberType;
	private String appliedDate;
	private Timestamp created;
	private User createdBy = new User();
	private Timestamp updated;
	private User updatedBy = new User();

	/** Member Type Label **/
	private String memberTypeLabel;

	/** Member Level Label **/
	private String memberLevelLabel;

	/** For Search criteria **/
	private String alertPeriod;
	private String expiredDateFrom;
	private String expiredDateTo;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public String getExpiredDate() {
		return expiredDate;
	}

	public void setExpiredDate(String expiredDate) {
		this.expiredDate = expiredDate;
	}

	public String getRenewedDate() {
		return renewedDate;
	}

	public void setRenewedDate(String renewedDate) {
		this.renewedDate = renewedDate;
	}

	public String getMemberType() {
		return memberType;
	}

	public void setMemberType(String memberType) {
		this.memberType = memberType;
	}

	public String getAppliedDate() {
		return appliedDate;
	}

	public void setAppliedDate(String appliedDate) {
		this.appliedDate = appliedDate;
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

	public String getMemberTypeLabel() {
		return memberTypeLabel;
	}

	public void setMemberTypeLabel(String memberTypeLabel) {
		this.memberTypeLabel = memberTypeLabel;
	}

	public String getMemberLevelLabel() {
		return memberLevelLabel;
	}

	public void setMemberLevelLabel(String memberLevelLabel) {
		this.memberLevelLabel = memberLevelLabel;
	}

	public String getExpiredDateFrom() {
		return expiredDateFrom;
	}

	public void setExpiredDateFrom(String expiredDateFrom) {
		this.expiredDateFrom = expiredDateFrom;
	}

	public String getExpiredDateTo() {
		return expiredDateTo;
	}

	public void setExpiredDateTo(String expiredDateTo) {
		this.expiredDateTo = expiredDateTo;
	}

	public String getAlertPeriod() {
		return alertPeriod;
	}

	public void setAlertPeriod(String alertPeriod) {
		this.alertPeriod = alertPeriod;
	}

}
