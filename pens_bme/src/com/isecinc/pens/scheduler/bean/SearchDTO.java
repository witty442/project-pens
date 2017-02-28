package com.isecinc.pens.scheduler.bean;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * Form bean for a Struts application.
 * Users may access 2 fields on this form:
 * <ul>
 * <li>user - [your comment here]
 * <li>name - [your comment here]
 * </ul>
 * @version 	1.0
 * @author
 */
public class SearchDTO extends ActionForm
{
	private String tranDate;
	private String fromTranDate;
	private String toTranDate;
	private String entity;
	private String entity2;
	private String product;
	private String product2;
	private String consent;
	private String consent2;
	private String firstName;
	private String lastName;
	private String idNo;
	private String userId;
	private String group;
	private String groupUserId;
	
	
    public void reset(ActionMapping mapping, HttpServletRequest  request) {

        // Reset values are provided as samples only. Change as appropriate.

    }
    public ActionErrors validate(ActionMapping mapping,
            HttpServletRequest request) {

        ActionErrors errors = new ActionErrors();
        return errors;

    }
	public String getTranDate() {
		return tranDate;
	}
	public void setTranDate(String tranDate) {
		this.tranDate = tranDate;
	}
	public String getFromTranDate() {
		return fromTranDate;
	}
	public void setFromTranDate(String fromTranDate) {
		this.fromTranDate = fromTranDate;
	}
	public String getToTranDate() {
		return toTranDate;
	}
	public void setToTranDate(String toTranDate) {
		this.toTranDate = toTranDate;
	}
	public String getEntity() {
		return entity;
	}
	public void setEntity(String entity) {
		this.entity = entity;
	}
	public String getEntity2() {
		return entity2;
	}
	public void setEntity2(String entity2) {
		this.entity2 = entity2;
	}
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public String getProduct2() {
		return product2;
	}
	public void setProduct2(String product2) {
		this.product2 = product2;
	}
	public String getConsent() {
		return consent;
	}
	public void setConsent(String consent) {
		this.consent = consent;
	}
	public String getConsent2() {
		return consent2;
	}
	public void setConsent2(String consent2) {
		this.consent2 = consent2;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getIdNo() {
		return idNo;
	}
	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getGroupUserId() {
		return groupUserId;
	}
	public void setGroupUserId(String groupUserId) {
		this.groupUserId = groupUserId;
	}

}