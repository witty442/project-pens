package com.isecinc.pens.bean;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.Timestamp;

import util.ConvertNullUtil;
import util.DateToolsUtil;

import com.isecinc.core.bean.References;
import com.isecinc.core.model.I_PO;
import com.isecinc.pens.init.InitialReferences;
import com.isecinc.pens.model.MUser;

/**
 * Visit
 * 
 * @author Aneak.t
 * @version $Id: Visit.java,v 1.0 23/10/2010 15:52:00 aneak.t Exp $
 * 
 */

public class Visit extends I_PO implements Serializable {

	private static final long serialVersionUID = 2927981379993837933L;

	/**
	 * Default Constructor
	 */
	public Visit() {

	}

	/**
	 * Constructor with rst
	 * 
	 * @param rst
	 * @throws Exception
	 */
	public Visit(ResultSet rst) throws Exception {
		// Mandatory
		setId(rst.getInt("VISIT_ID"));
		setCode(rst.getString("CODE").trim());
		setVisitDate(DateToolsUtil.convertToString(rst.getDate("VISIT_DATE")));
		setVisitTime(rst.getString("VISIT_TIME").trim());
		setInterfaces(rst.getString("INTERFACES").trim());
		setCustomerId(rst.getInt("CUSTOMER_ID"));
		setSalesClose(rst.getString("SALES_CLOSED").trim());
		setUnClosedReason(ConvertNullUtil.convertToString(rst.getString("UNCLOSED_REASON")).trim());
		setIsActive(rst.getString("ISACTIVE"));
		setUser(new MUser().find(String.valueOf(rst.getInt("USER_ID"))));
		setCreated(rst.getTimestamp("CREATED"));
		setCreatedBy(new MUser().find(String.valueOf(rst.getInt("CREATED_BY"))));
		setUpdated(rst.getTimestamp("UPDATED"));
		setUpdatedBy(new MUser().find(String.valueOf(rst.getInt("UPDATED_BY"))));
		setCustomerLabel(ConvertNullUtil.convertToString(rst.getString("CUSTOMER_NAME")));
		setOrderType(ConvertNullUtil.convertToString(rst.getString("ORDER_TYPE")));

		setExported(rst.getString("EXPORTED"));
		// set display label
		setDisplayLabel();

	}

	/**
	 * Set Display Label
	 */
	protected void setDisplayLabel() {
		for (References r : InitialReferences.getReferenes().get(InitialReferences.ACTIVE)) {
			if (r.getKey().equalsIgnoreCase(getIsActive())) {
				setActiveLabel(r.getName());
				break;
			}
		}
		for (References r : InitialReferences.getReferenes().get(InitialReferences.UNCLOSED_REASON)) {
			if (r.getKey().equalsIgnoreCase(getUnClosedReason())) {
				setUnclosedReasonLabel(r.getName());
				break;
			}
		}
	}

	private int id;
	private String code;
	private String visitDate;
	private String visitTime;
	private int customerId;
	private String salesClose;
	private String interfaces;
	private String unClosedReason;
	private String isActive;
	private User user = new User();
	private Timestamp created;
	private User createdBy = new User();
	private Timestamp updated;
	private User updatedBy = new User();

	private String customerLabel;
	private String unclosedReasonLabel;
	private String dateFrom;
	private String dateTo;

	private String orderType;
	/** EXPORTED */
	private String exported;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getVisitDate() {
		return visitDate;
	}

	public void setVisitDate(String visitDate) {
		this.visitDate = visitDate;
	}

	public String getVisitTime() {
		return visitTime;
	}

	public void setVisitTime(String visitTime) {
		this.visitTime = visitTime;
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public String getSalesClose() {
		return salesClose;
	}

	public void setSalesClose(String salesClose) {
		this.salesClose = salesClose;
	}

	public String getUnClosedReason() {
		return unClosedReason;
	}

	public void setUnClosedReason(String unClosedReason) {
		this.unClosedReason = unClosedReason;
	}

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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

	public String getCustomerLabel() {
		return customerLabel;
	}

	public void setCustomerLabel(String customerLabel) {
		this.customerLabel = customerLabel;
	}

	public String getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(String dateFrom) {
		this.dateFrom = dateFrom;
	}

	public String getDateTo() {
		return dateTo;
	}

	public void setDateTo(String dateTo) {
		this.dateTo = dateTo;
	}

	public String getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(String interfaces) {
		this.interfaces = interfaces;
	}

	public String getUnclosedReasonLabel() {
		return unclosedReasonLabel;
	}

	public void setUnclosedReasonLabel(String unclosedReasonLabel) {
		this.unclosedReasonLabel = unclosedReasonLabel;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getExported() {
		return exported;
	}

	public void setExported(String exported) {
		this.exported = exported;
	}

}
