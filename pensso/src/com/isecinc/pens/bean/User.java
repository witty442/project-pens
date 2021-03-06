package com.isecinc.pens.bean;

import static com.pens.util.ConvertNullUtil.convertToString;

import java.io.Serializable;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.isecinc.core.bean.References;
import com.isecinc.core.model.I_PO;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialReferences;
import com.jcraft.jsch.Logger;

/**
 * User
 * 
 * @author Wittaya.b
 * @version $Id: User.java,v 1.0 20/11/2020
 * 
 */
public class User extends I_PO implements Serializable {

	public static final String ADMIN = "AD";
	public static final String TT = "TT";
	public static final String MT = "MT";
	public static final String VAN = "VAN";
	public static final String DD = "DD";
	public static final String NB = "NB";
	public static final String STOCK = "STOCK";
	public static final String BUD_ADMIN = "BUDADMIN";
	public static final String NIS = "NISSIN";
	public static final String NIS_PENS = "NISSINPENS";
	public static final String NIS_VIEW = "NISSINVIEW";
	
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
		setRoleAccess(convertToString(rst.getString("ROLE")).trim());
		setRoleAll(Utils.isNull(rst.getString("ROLE_ALL")));
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
		setMoneyToPens(Utils.isNull(rst.getString("money_to_pens")));
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
		try{
			List<References> ref = new ArrayList<References>();
			// customer type, sales group, order type
			if ( !getType().equalsIgnoreCase(TT) && !getType().equalsIgnoreCase(VAN)
			) {
				// No Role Info
			} else if (getType().equalsIgnoreCase(TT)) {
				ref = InitialReferences.getReferenes().get(InitialReferences.ROLE_TT);
			} else if (getType().equalsIgnoreCase(VAN)) {
				ref = InitialReferences.getReferenes().get(InitialReferences.ROLE_VAN);
			} 
			if(ref != null){
				for (References r : ref) {
					if (r.getName().equalsIgnoreCase(InitialReferences.CUSTOMER_TYPE)) {
						setCustomerType(r);
					} else if (r.getName().equalsIgnoreCase(InitialReferences.SALES_GROUP)) {
						setSalesGroup(r);
					} else if (r.getName().equalsIgnoreCase(InitialReferences.ORDER_TYPE)) {
						setOrderType(r);
					} 
				}
			}
			//System.out.println("activeRoleInfo:type{"+getType()+"}ref{"+ref+"}orderType{"+getOrderType()+"}");
			if ( getType().equalsIgnoreCase(TT) || getType().equalsIgnoreCase(VAN)) {
				for (References r : InitialReferences.getReferenes().get(InitialReferences.ROLE)) {
					if (r.getKey().equalsIgnoreCase(getType())) {
						setRole(r);
						break;
					}
				}
			}else{
				if(!Utils.isNull(getRoleAll()).equals("")){
				   setRole( new References(Utils.isNull(getRoleAll()), Utils.isNull(getRoleAll())));
				}else{
				   setRole( new References("admin", "admin"));
				}
			}
		}catch(Exception e){
			e.printStackTrace();
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
	private String roleAccess;
	private String roleAll;
	/** VAN NO */
	private String vanNo;

	/** USER NAME */
	private String userName;

	/** TERRITORY */
	private String territory;
	
	/** PD_PAID for VAN Sales **/
	private String pdPaid;
    /** moneyToPens */
    private String moneyToPens;
    
    private boolean mobile;
    private String module;
    
	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getRoleAll() {
		return roleAll;
	}

	public void setRoleAll(String roleAll) {
		this.roleAll = roleAll;
	}

	public boolean isMobile() {
		return mobile;
	}

	public void setMobile(boolean mobile) {
		this.mobile = mobile;
	}

	public String getRoleAccess() {
		return roleAccess;
	}

	public void setRoleAccess(String roleAccess) {
		this.roleAccess = roleAccess;
	}

	public String getMoneyToPens() {
		return moneyToPens;
	}

	public void setMoneyToPens(String moneyToPens) {
		this.moneyToPens = moneyToPens;
	}

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
