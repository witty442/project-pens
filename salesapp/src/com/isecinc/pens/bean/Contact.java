package com.isecinc.pens.bean;

import java.io.Serializable;
import java.sql.ResultSet;

import com.isecinc.core.bean.References;
import com.isecinc.core.model.I_PO;
import com.isecinc.pens.init.InitialReferences;
import com.pens.util.ConvertNullUtil;

/**
 * Contact
 * 
 * @author Aneak.t
 * @version $Id: Address.java,v 1.0 07/10/2010 15:52:00 aneak.t Exp $
 * 
 */
public class Contact extends I_PO implements Serializable {

	private static final long serialVersionUID = -7983462273587759003L;

	/**
	 * Default Constructor
	 */
	public Contact() {}

	/**
	 * Default Constructor with rst
	 * 
	 * @param rst
	 * @throws Exception
	 */
	public Contact(ResultSet rst) throws Exception {
		setId(rst.getInt("CONTACT_ID"));
		setCustomerId(rst.getInt("CUSTOMER_ID"));
		setContactTo(rst.getString("CONTACT_TO").trim());
		setRelation(ConvertNullUtil.convertToString(rst.getString("RELATION")).trim());
		setPhone(rst.getString("PHONE").trim());
		setFax(ConvertNullUtil.convertToString(rst.getString("FAX")).trim());
		setIsActive(rst.getString("ISACTIVE").trim());
		setPhone2(ConvertNullUtil.convertToString(rst.getString("PHONE2")).trim());
		setMobile(ConvertNullUtil.convertToString(rst.getString("MOBILE")).trim());
		setMobile2(ConvertNullUtil.convertToString(rst.getString("MOBILE2")).trim());
		try {
			setPhoneSub1(ConvertNullUtil.convertToString(rst.getString("PHONE_SUB1")).trim());
			setPhoneSub2(ConvertNullUtil.convertToString(rst.getString("PHONE_SUB2")).trim());
		} catch (Exception e) {
			e.printStackTrace();
		}
		/** set display label */
		setDisplayLabel();
	}

	/**
	 * Set Display Label
	 * 
	 * @throws Exception
	 */
	protected void setDisplayLabel() throws Exception {
		for (References r : InitialReferences.getReferenes().get(InitialReferences.ACTIVE)) {
			if (r.getKey().equalsIgnoreCase(getIsActive())) {
				setActiveLabel(r.getName());
				break;
			}
		}
	}

	/** ID */
	private int id;

	/** CONTACT TO */
	private String contactTo;

	/** RELATION */
	private String relation;

	/** PHONE */
	private String phone;

	/** FAX */
	private String fax;

	/** ACTIVE */
	private String isActive;

	/** CUSTOMER ID */
	private int customerId;

	/** PHONE */
	private String phone2;

	/** MOBILE */
	private String mobile;

	/** MOBILE2 */
	private String mobile2;

	/** PHONE_SUB1 */
	private String phoneSub1;

	/** PHONE_SUB2 */
	private String phoneSub2;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContactTo() {
		return contactTo;
	}

	public void setContactTo(String contactTo) {
		this.contactTo = contactTo;
	}

	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public String getPhone2() {
		return phone2;
	}

	public void setPhone2(String phone2) {
		this.phone2 = phone2;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getMobile2() {
		return mobile2;
	}

	public void setMobile2(String mobile2) {
		this.mobile2 = mobile2;
	}

	public String getPhoneSub1() {
		return phoneSub1;
	}

	public void setPhoneSub1(String phoneSub1) {
		this.phoneSub1 = phoneSub1;
	}

	public String getPhoneSub2() {
		return phoneSub2;
	}

	public void setPhoneSub2(String phoneSub2) {
		this.phoneSub2 = phoneSub2;
	}

}
