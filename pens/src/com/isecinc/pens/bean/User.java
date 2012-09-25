package com.isecinc.pens.bean;

import static util.ConvertNullUtil.convertToString;

import java.io.Serializable;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.isecinc.core.bean.References;
import com.isecinc.core.model.I_PO;
import com.isecinc.pens.init.InitialReferences;

/**
 * User
 * 
 * @author Atiz.b
 * @version $Id: User.java,v 1.0 13/06/2010 15:52:00 atiz.b Exp $
 * 
 */
public class User extends I_PO implements Serializable {

	public static final String ADMIN = "AD";
	public static final String TT = "TT";
	public static final String VAN = "VAN";
	public static final String DD = "DD";
	public static final String NB = "NB";

	private static final long serialVersionUID = 2247823086169174428L;

	/**
	 * Default Constructor
	 */
	public User() {}

	/**
	 * Constructor with rst
	 * 
	 * @param rst
	 * @throws Exception
	 */
	public User(ResultSet rst) throws Exception {
		// Mandatory
		setId(rst.getInt("USER_ID"));
		setCode(rst.getString("CODE").trim());
		setName(rst.getString("NAME").trim());
		setType(convertToString(rst.getString("ROLE")).trim());
		setActive(rst.getString("ISACTIVE").trim());

		// oracle fields
		setCategory(convertToString(rst.getString("CATEGORY")).trim());
		setOrganization(convertToString(rst.getString("ORGANIZATION")).trim());
		setSourceName(convertToString(rst.getString("SOURCE_NAME")).trim());
		setIdCardNo(convertToString(rst.getString("ID_CARD_NO")).trim());
		setStartDate(convertToString(rst.getString("START_DATE")).trim());
		setEndDate(convertToString(rst.getString("END_DATE")).trim());
		setTerritory(convertToString(rst.getString("TERRITORY")).trim());

		// sales online fields
		setUserName(convertToString(rst.getString("USER_NAME")).trim());
		setPassword(convertToString(rst.getString("PASSWORD")).trim());
		setConfirmPassword(convertToString(rst.getString("PASSWORD")).trim());

		// set display label
		setDisplayLabel();

		// active role information
		// customer type, sales group, order type
		activeRoleInfo();
		
		// Add PD Paid for VAN Sales
		setPdPaid(rst.getString("PD_PAID"));
	}

	/**
	 * Set Display Label
	 */
	protected void setDisplayLabel() {
		for (References r : InitialReferences.getReferenes().get(InitialReferences.ACTIVE)) {
			if (r.getKey().equalsIgnoreCase(getActive())) {
				setActiveLabel(r.getName());
				break;
			}
		}
	}

	/**
	 * Active Role Info
	 */
	public void activeRoleInfo() {
		List<References> ref = new ArrayList<References>();
		// customer type, sales group, order type
		if (getType().equalsIgnoreCase(ADMIN)) {
			// No Role Info
		} else if (getType().equalsIgnoreCase(TT)) {
			ref = InitialReferences.getReferenes().get(InitialReferences.ROLE_TT);
		} else if (getType().equalsIgnoreCase(VAN)) {
			ref = InitialReferences.getReferenes().get(InitialReferences.ROLE_VAN);
		} else if (getType().equalsIgnoreCase(DD)) {
			ref = InitialReferences.getReferenes().get(InitialReferences.ROLE_DD);
		} else if (getType().equalsIgnoreCase(NB)) {
			ref = InitialReferences.getReferenes().get(InitialReferences.ROLE_DD);
		}
		for (References r : ref) {
			if (r.getName().equalsIgnoreCase(InitialReferences.CUSTOMER_TYPE)) {
				setCustomerType(r);
			} else if (r.getName().equalsIgnoreCase(InitialReferences.SALES_GROUP)) {
				setSalesGroup(r);
			} else if (r.getName().equalsIgnoreCase(InitialReferences.ORDER_TYPE)) {
				setOrderType(r);
			}
		}
		for (References r : InitialReferences.getReferenes().get(InitialReferences.ROLE)) {
			if (r.getKey().equalsIgnoreCase(getType())) {
				setRole(r);
				break;
			}
		}
	}

	/**
	 * To String
	 */
	public String toString() {
		return String.format("User[%s-%s] Customer Type[%s] Order Type[%s] Sales Group[%s]", getId(), getName(),
				getCustomerType().getKey(), getOrderType().getKey(), getSalesGroup().getKey());
	}

	/** ID */
	private int id;

	/** CODE(NO) */
	private String code;

	/** NAME */
	private String name;

	/** CATEGORY */
	private String category;

	/** ORGANIZATION */
	private String organization;

	/** START DATE */
	private String startDate;

	/** END DATE */
	private String endDate;

	/** SOURCE NAME */
	private String sourceName;

	/** ID CARD NO */
	private String idCardNo;

	/** PASSWORD */
	private String password;

	/** CONFIRM PASSWORD */
	private String confirmPassword;

	/** ACTIVE */
	private String active;

	/** TYPE */
	private String type;

	/** CUSTOMER TYPE */
	private References customerType;

	/** SALES GROUP */
	private References salesGroup;

	/** ORDER TYPE */
	private References orderType;

	/** ROLE */
	private References role;

	/** VAN NO */
	private String vanNo;

	/** USER NAME */
	private String userName;

	/** TERRITORY */
	private String territory;
	
	/** PD_PAID for VAN Sales **/
	private String pdPaid;

	public String getPdPaid() {
		return pdPaid;
	}

	public void setPdPaid(String pdPaid) {
		if(pdPaid == null || pdPaid.trim().length() == 0)
			pdPaid = "N";
		this.pdPaid = pdPaid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public References getCustomerType() {
		return customerType;
	}

	public void setCustomerType(References customerType) {
		this.customerType = customerType;
	}

	public References getSalesGroup() {
		return salesGroup;
	}

	public void setSalesGroup(References salesGroup) {
		this.salesGroup = salesGroup;
	}

	public References getOrderType() {
		return orderType;
	}

	public void setOrderType(References orderType) {
		this.orderType = orderType;
	}

	public String getVanNo() {
		return vanNo;
	}

	public void setVanNo(String vanNo) {
		this.vanNo = vanNo;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public String getIdCardNo() {
		return idCardNo;
	}

	public void setIdCardNo(String idCardNo) {
		this.idCardNo = idCardNo;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public References getRole() {
		return role;
	}

	public void setRole(References role) {
		this.role = role;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public String getTerritory() {
		return territory;
	}

	public void setTerritory(String territory) {
		this.territory = territory;
	}
	
	public boolean isPDPaid(){
		if(this.pdPaid == null)
			this.pdPaid = "N";
		
		return "Y".equals(this.pdPaid);
	}

}
