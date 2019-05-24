package com.isecinc.pens.bean;

import static util.ConvertNullUtil.convertToString;

import java.io.Serializable;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import util.Utils;

import com.isecinc.core.bean.References;
import com.isecinc.core.model.I_PO;
import com.isecinc.pens.model.MGroupRole;


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
	public static final String MKT = "MKT";
	public static final String MT_SALES = "MTSALES";
	public static final String DD_SALES = "DDSALES";
	public static final String MTMGR = "MTMGR";
	public static final String STOCKCR = "STOCKCR";
	public static final String SPIDER = "SPIDER";
	public static final String PRODSHOW = "PRODSHOW";
	public static final String VANDOC = "VANDOC";
	public static final String STOCKVAN = "STOCKVAN";
	public static final String MC_ENTRY = "MC_ENTRY";
	public static final String MT_QUERY = "MT_QUERY"; 
	public static final String SA = "SA"; //salesAnalysis
	
	public static final String TTSUPER = "TTSUPER";//Supper
	public static final String TTMGR = "TTMGR";//ManagerSale
	
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
		setCode(convertToString(rst.getString("CODE")));
		setName(convertToString(rst.getString("NAME")));

		// oracle fields
		setCategory(convertToString(rst.getString("CATEGORY")));
		setOrganization(convertToString(rst.getString("ORGANIZATION")));
		setSourceName(convertToString(rst.getString("SOURCE_NAME")));
		setIdCardNo(convertToString(rst.getString("ID_CARD_NO")));
	
		//String dateStr = Utils.stringValue(new Date(rst.getDate("START_DATE",Calendar.getInstance(Locale.US)).getTime()),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th);
		if( !Utils.isNull(rst.getString("START_DATE")).equals("")){
		  setStartDate(Utils.stringValue(new Date(rst.getDate("START_DATE",Calendar.getInstance(Locale.US)).getTime()),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
		}
		if( !Utils.isNull(rst.getString("END_DATE")).equals("")){
		  setEndDate(Utils.stringValue(new Date(rst.getDate("END_DATE",Calendar.getInstance(Locale.US)).getTime()),Utils.DD_MM_YYYY_WITH_SLASH,Utils.local_th));
		}
		
		setTerritory(convertToString(rst.getString("TERRITORY")));

		// sales online fields
		setUserName(convertToString(rst.getString("USER_NAME")));
		setPassword(convertToString(rst.getString("PASSWORD")));
		setConfirmPassword(convertToString(rst.getString("PASSWORD")));
         
		setUserGroupId(rst.getInt("USER_GROUP_ID"));
		setUserGroupName(MGroupRole.getUserGroupName(getUserGroupId()));
		//set Role Sales Target
		setRoleSalesTarget(convertToString(rst.getString("ROLE_SALESTARGET")));
		//set Role Cr stock
		setRoleCRStock(convertToString(rst.getString("ROLE_CR_STOCK")));
		
		//set Role Cr stock
		setRoleSpider(convertToString(rst.getString("ROLE_SPIDER")));
		
		setRoleProdShow(convertToString(rst.getString("ROLE_PRODSHOW")));
		setRoleVanDoc(convertToString(rst.getString("ROLE_VANDOC")));
		setRoleStockVan(convertToString(rst.getString("ROLE_STOCK_VAN")));
		setRoleMC(convertToString(rst.getString("ROLE_MC")));
		setRoleSA(convertToString(rst.getString("ROLE_SA")));
		
		System.out.println("setRoleMC:"+getRoleMC()+";"+convertToString(rst.getString("ROLE_MC")));
		// set display label
		setDisplayLabel();
  
		// active role information
		// customer type, sales group, order type
		activeRoleInfo();
       
		setActiveLabel(Utils.isStatusActiveByDate(getStartDate(),getEndDate())?"Active":"InActive");
	}
  
	
	
	/**
	 * Set Display Label
	 */
	protected void setDisplayLabel() {
		
	}

	/**
	 * Active Role Info
	 */
	public void activeRoleInfo() {
		
		
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

	/** USER_GROUP  **/
	private int userGroupId;
	
	private String userGroupName;
	
	private String active;
    
	private String changeUserGroup;
	
    private String newPassword;
    private String reNewPassword;
    private String roleSalesTarget;
    private String roleCRStock;
    private String roleSpider;
    private String roleProdShow;
    private String roleVanDoc;
    private String roleStockVan;
    private String roleMC;
    private String roleSA;
    
    
	public String getRoleSA() {
		return roleSA;
	}

	public void setRoleSA(String roleSA) {
		this.roleSA = roleSA;
	}

	public String getRoleMC() {
		return roleMC;
	}

	public void setRoleMC(String roleMC) {
		this.roleMC = roleMC;
	}

	public String getRoleStockVan() {
		return roleStockVan;
	}

	public void setRoleStockVan(String roleStockVan) {
		this.roleStockVan = roleStockVan;
	}

	public String getRoleProdShow() {
		return roleProdShow;
	}

	public void setRoleProdShow(String roleProdShow) {
		this.roleProdShow = roleProdShow;
	}

	public String getRoleVanDoc() {
		return roleVanDoc;
	}

	public void setRoleVanDoc(String roleVanDoc) {
		this.roleVanDoc = roleVanDoc;
	}

	public String getRoleSpider() {
		return roleSpider;
	}

	public void setRoleSpider(String roleSpider) {
		this.roleSpider = roleSpider;
	}

	public String getRoleCRStock() {
		return roleCRStock;
	}

	public void setRoleCRStock(String roleCRStock) {
		this.roleCRStock = roleCRStock;
	}

	public String getRoleSalesTarget() {
		return roleSalesTarget;
	}

	public void setRoleSalesTarget(String roleSalesTarget) {
		this.roleSalesTarget = roleSalesTarget;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getReNewPassword() {
		return reNewPassword;
	}

	public void setReNewPassword(String reNewPassword) {
		this.reNewPassword = reNewPassword;
	}

	public String getUserGroupName() {
		return userGroupName;
	}

	public void setUserGroupName(String userGroupName) {
		this.userGroupName = userGroupName;
	}

	public String getChangeUserGroup() {
		return changeUserGroup;
	}

	public void setChangeUserGroup(String changeUserGroup) {
		this.changeUserGroup = changeUserGroup;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}
    

	public int getUserGroupId() {
		return userGroupId;
	}

	public void setUserGroupId(int userGroupId) {
		this.userGroupId = userGroupId;
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

}
