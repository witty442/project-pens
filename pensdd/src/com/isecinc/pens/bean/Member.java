package com.isecinc.pens.bean;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import util.ConvertNullUtil;
import util.DateToolsUtil;

import com.isecinc.core.bean.References;
import com.isecinc.core.model.I_PO;
import com.isecinc.pens.SystemProperties;
import com.isecinc.pens.init.InitialReferences;
import com.isecinc.pens.model.MMember;
import com.isecinc.pens.model.MProductCategory;
import com.isecinc.pens.model.MUser;

/**
 * Member
 * 
 * @author Aneak.t
 * @version $Id: Member.java,v 1.0 12/10/2010 15:52:00 aneak.t Exp $
 * 
 */

public class Member extends I_PO implements Serializable {

	private static final long serialVersionUID = -8675517484785667619L;
	
	
	public static final String ROUND_TRIP_7 = "7";
	public static final String ROUND_TRIP_15 = "15";
	public static final String ROUND_TRIP_30 = "30";

	/**
	 * Default Constructor
	 */
	public Member() {
		setRecommendedBy("");
		setRecommendedCode("");
	}

	/**
	 * Constructor with rst
	 * 
	 * @param rst
	 * @throws Exception
	 */
	public Member(ResultSet rst) throws Exception {
		// Mandatory
		setId(rst.getInt("CUSTOMER_ID"));
		setCode(rst.getString("CODE").trim());
		setName(rst.getString("NAME").trim());
		setName2(ConvertNullUtil.convertToString(rst.getString("NAME2")).trim());
		setTerritory(ConvertNullUtil.convertToString(rst.getString("TERRITORY")).trim());
		setBusinessType(ConvertNullUtil.convertToString(rst.getString("BUSINESS_TYPE")).trim());
		setIsActive(rst.getString("ISACTIVE").trim());
		setUser(new MUser().find(String.valueOf(rst.getInt("USER_ID"))));
		setPersonIDNo(ConvertNullUtil.convertToString(rst.getString("PERSON_ID_NO")).trim());
		setEmail(ConvertNullUtil.convertToString(rst.getString("EMAIL")).trim());
		setPaymentTerm(ConvertNullUtil.convertToString(rst.getString("PAYMENT_TERM")).trim());
		setVatCode(ConvertNullUtil.convertToString(rst.getString("VAT_CODE")).trim());
		setExpiredDate("");
		if (rst.getTimestamp("EXPIRED_DATE") != null) {
			setExpiredDate(DateToolsUtil.convertToString(rst.getTimestamp("EXPIRED_DATE")));
		}
		setBirthDay("");
		if (rst.getTimestamp("BIRTHDAY") != null) {
			setBirthDay(DateToolsUtil.convertToString(rst.getTimestamp("BIRTHDAY")));
		}
		setOccupation(ConvertNullUtil.convertToString(rst.getString("OCCUPATION")).trim());
		setCustomerType(rst.getString("CUSTOMER_TYPE").trim());
		setShippingMethod(ConvertNullUtil.convertToString(rst.getString("SHIPPING_METHOD")).trim());
		setShippingDate(ConvertNullUtil.convertToString(rst.getString("SHIPPING_DATE")).trim());
		setShippingTime(ConvertNullUtil.convertToString(rst.getString("SHIPPING_TIME")).trim());
		setShippingTimeTo(ConvertNullUtil.convertToString(rst.getString("SHIPPING_TIME_TO")).trim());
		setMonthlyIncome(rst.getDouble("MONHTLY_INCOME"));
		setChrolesterol(rst.getDouble("CHOLESTEROL"));
		setMemberLevel(ConvertNullUtil.convertToString(rst.getString("MEMBER_LEVEL")).trim());
		setRecommendedBy(ConvertNullUtil.convertToString(rst.getString("RECOMMENDED_BY")).trim());
		setRegisterDate("");
		//System.out.println("rst.getTimestamp-->"+rst.getTimestamp("REGISTER_DATE"));
		if (rst.getTimestamp("REGISTER_DATE") != null) {
			setRegisterDate(DateToolsUtil.convertToString(rst.getTimestamp("REGISTER_DATE")));
			//setRegisterDate(DateToolsUtil.convertToStringEng(rst.getTimestamp("REGISTER_DATE")));
			//setRegisterDate(DateToolsUtil.convertToStringDefaut(rst.getTimestamp("REGISTER_DATE")));
			
			
		}
		setFirstDeliverlyDate("");
		if (rst.getTimestamp("FIRST_DELIVERLY_DATE") != null) {
			setFirstDeliverlyDate(DateToolsUtil.convertToString(rst.getTimestamp("FIRST_DELIVERLY_DATE")));
		}
		
		setMemberType(ConvertNullUtil.convertToString(rst.getString("MEMBER_TYPE")).trim());
		setCreated(rst.getTimestamp("CREATED"));
		setCreatedBy(new MUser().find(String.valueOf(rst.getInt("CREATED_BY"))));
		setUpdated(rst.getTimestamp("UPDATED"));
		setUpdatedBy(new MUser().find(String.valueOf(rst.getInt("UPDATED_BY"))));
		setOrderAmountPeriod(rst.getInt("ORDER_AMOUNT_PERIOD"));
		setPaymentMethod(ConvertNullUtil.convertToString(rst.getString("PAYMENT_METHOD")).trim());
		setParentCustomerId(rst.getInt("PARENT_CUSTOMER_ID"));
		setProductCategory(new MProductCategory().find(String.valueOf(rst.getInt("PRODUCT_CATEGORY_ID"))));

		String rt = ConvertNullUtil.convertToString(rst.getString("ROUND_TRIP")).trim();
		setRoundTrip(rt.length() > 0 ? String.valueOf(new Double(rt).intValue()) : "0");

		setRecommendedType(ConvertNullUtil.convertToString(rst.getString("RECOMMENDED_TYPE")).trim());
		if (rst.getString("RECOMMENDED_ID") != null && rst.getInt("RECOMMENDED_ID") != 0) {
			setRecommendedId(rst.getInt("RECOMMENDED_ID"));
			// setRecommendedCode(new MMember().find(String.valueOf(getRecommendedId())).getCode());
			Member member = new MMember().find(String.valueOf(getRecommendedId()));
			setRecommendedCode("");
			if (member != null) {
				setRecommendedCode(member.getCode());
			}
		} else {
			setRecommendedCode("");
		}
		setAgeMonth(rst.getInt("AGE_MONTH"));
		setCancelReason(ConvertNullUtil.convertToString(rst.getString("CANCEL_REASON")).trim());

		if (getExpiredDate() != null && !getExpiredDate().equals("")) {
			setDaysBeforeExpire(String.valueOf(DateToolsUtil.calDiffDate(DateToolsUtil.DAY, DateToolsUtil
					.getCurrentDateTime("dd/MM/yyyy"), getExpiredDate())));
		}

		if (getDaysBeforeExpire() != null && !getDaysBeforeExpire().equals("")) {
			if (Integer.parseInt(getDaysBeforeExpire()) < 0) {
				setExpired(true);
				setDaysBeforeExpire(SystemProperties.getCaption("MemberExpired", null));
			}
		}
		setInterfaces(rst.getString("INTERFACES").trim());
		setPartyType(ConvertNullUtil.convertToString(rst.getString("PARTY_TYPE")).trim());
		setDeliveryGroup(ConvertNullUtil.convertToString(rst.getString("DELIVERY_GROUP")));
		setExported(rst.getString("EXPORTED"));
		setIsvip(rst.getString("ISVIP"));
		setCreditcardExpired(ConvertNullUtil.convertToString(rst.getString("CREDITCARD_EXPIRED")).trim());
		// set display label
		setDisplayLabel();
		setIsFreeOfChart(rst.getString("FREE_OF_CHART"));
		setPaymentType(rst.getInt("PAYMENT_TYPE"));
		
		setCreditCardNo(rst.getString("CARD_NO"));
		setCreditCardBank(rst.getString("CARD_BANK"));
		setCardName(rst.getString("CARD_NAME"));
		
		//Edit 10/12/2557
		setOldPriceFlag(rst.getString("OLD_PRICE_FLAG"));
		
		setTotalOrderQty(rst.getInt("total_order_qty"));
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

		for (References r : InitialReferences.getReferenes().get(InitialReferences.MEMBER_TYPE)) {
			if (r.getKey().equalsIgnoreCase(getMemberType())) {
				setMemberTypeLabel(r.getName());
				break;
			}
		}
		for (References r : InitialReferences.getReferenes().get(InitialReferences.MEMBER_STATUS)) {
			if (r.getKey().equalsIgnoreCase(getMemberLevel())) {
				setMemberLevelLabel(r.getName());
				break;
			}
		}
	}

	private int id;
	private String code;
	private String name;
	private String name2;
	private String territory;
	private String businessType;
	private String customerType;
	private String isActive;
	private User user = new User();
	private String personIDNo;
	private String email;
	private String paymentTerm;
	private String vatCode;
	private String birthDay;
	private String occupation;
	private String shippingMethod;
	private String shippingDate;
	private String shippingTime;
	private double monthlyIncome;
	private double chrolesterol;
	private String memberLevel;
	private String recommendedType;
	private String recommendedBy;
	private int recommendedId;
	private String memberType;
	private String registerDate;
	private String firstDeliverlyDate;
	

	private Timestamp created;
	private User createdBy = new User();
	private Timestamp updated;
	private User updatedBy = new User();
	private int orderAmountPeriod;
	private String paymentMethod;
	private int parentCustomerId;
	private ProductCategory productCategory = new ProductCategory();
	private String age;
	private List<MemberProduct> memberProducts = new ArrayList<MemberProduct>();
	private String roundTrip;
	private String expiredDate;
	private int ageMonth;
	private String cancelReason;
	private boolean expired = false;
	private String interfaces;
	
	private String isFreeOfChart;
	private int paymentType;
	private String oldPriceFlag;
    private int totalOrderQty;
    
    private String memberCodeCopy;
    
    
	public String getMemberCodeCopy() {
		return memberCodeCopy;
	}

	public void setMemberCodeCopy(String memberCodeCopy) {
		this.memberCodeCopy = memberCodeCopy;
	}

	public int getTotalOrderQty() {
		return totalOrderQty;
	}

	public void setTotalOrderQty(int totalOrderQty) {
		this.totalOrderQty = totalOrderQty;
	}

	public String getOldPriceFlag() {
		return oldPriceFlag;
	}

	public void setOldPriceFlag(String oldPriceFlag) {
		this.oldPriceFlag = oldPriceFlag;
	}

	/** DELIVERY_GROUP */
	private String deliveryGroup;

	/** Transient */
	private String recommendedCode;

	/** Member Type Label **/
	private String memberTypeLabel;

	/** Member Level Label **/
	private String memberLevelLabel;

	private String daysBeforeExpire;

	/** PARTY_TYPE */
	private String partyType;

	/** Exported */
	private String exported;

	/** ISVIP */
	private String isvip;

	/** SHIPPING_TIME_TO */
	private String shippingTimeTo;

	/** CREDITCARD_EXPIRED */
	private String creditcardExpired;
	
	
	/** Credit Card No & CreditCardBank **/
	private String creditCardNo;
	private String creditCardBank;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRoundTrip() {
		return roundTrip;
	}

	public void setRoundTrip(String roundTrip) {
		this.roundTrip = roundTrip;
	}

	public String getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(String registerDate) {
		this.registerDate = registerDate;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCustomerType() {
		return customerType;
	}

	public void setCustomerType(String customerType) {
		this.customerType = customerType;
	}

	public String getName2() {
		return name2;
	}

	public void setName2(String name2) {
		this.name2 = name2;
	}

	public String getTerritory() {
		return territory;
	}

	public void setTerritory(String territory) {
		this.territory = territory;
	}

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	public String getShippingTime() {
		return shippingTime;
	}

	public void setShippingTime(String shippingTime) {
		this.shippingTime = shippingTime;
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

	public String getPersonIDNo() {
		return personIDNo;
	}

	public void setPersonIDNo(String personIDNo) {
		this.personIDNo = personIDNo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPaymentTerm() {
		return paymentTerm;
	}

	public void setPaymentTerm(String paymentTerm) {
		this.paymentTerm = paymentTerm;
	}

	public String getVatCode() {
		return vatCode;
	}

	public void setVatCode(String vatCode) {
		this.vatCode = vatCode;
	}

	public String getBirthDay() {
		return birthDay;
	}

	public void setBirthDay(String birthDay) {
		this.birthDay = birthDay;
	}

	public String getOccupation() {
		return occupation;
	}

	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}

	public String getShippingMethod() {
		return shippingMethod;
	}

	public void setShippingMethod(String shippingMethod) {
		this.shippingMethod = shippingMethod;
	}

	public String getShippingDate() {
		return shippingDate;
	}

	public void setShippingDate(String shippingDate) {
		this.shippingDate = shippingDate;
	}

	public double getMonthlyIncome() {
		return monthlyIncome;
	}

	public void setMonthlyIncome(double monthlyIncome) {
		this.monthlyIncome = monthlyIncome;
	}

	public double getChrolesterol() {
		return chrolesterol;
	}

	public void setChrolesterol(double chrolesterol) {
		this.chrolesterol = chrolesterol;
	}

	public String getMemberLevel() {
		return memberLevel;
	}

	public void setMemberLevel(String memberLevel) {
		this.memberLevel = memberLevel;
	}

	public String getRecommendedBy() {
		return recommendedBy;
	}

	public void setRecommendedBy(String recommendedBy) {
		this.recommendedBy = recommendedBy;
	}

	public String getMemberType() {
		return memberType;
	}

	public void setMemberType(String memberType) {
		this.memberType = memberType;
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

	public int getOrderAmountPeriod() {
		return orderAmountPeriod;
	}

	public void setOrderAmountPeriod(int orderAmountPeriod) {
		this.orderAmountPeriod = orderAmountPeriod;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public int getParentCustomerId() {
		return parentCustomerId;
	}

	public void setParentCustomerId(int parentCustomerId) {
		this.parentCustomerId = parentCustomerId;
	}

	public ProductCategory getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(ProductCategory productCategory) {
		this.productCategory = productCategory;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public List<MemberProduct> getMemberProducts() {
		return memberProducts;
	}

	public void setMemberProducts(List<MemberProduct> memberProducts) {
		this.memberProducts = memberProducts;
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

	public String getRecommendedType() {
		return recommendedType;
	}

	public void setRecommendedType(String recommendedType) {
		this.recommendedType = recommendedType;
	}

	public int getRecommendedId() {
		return recommendedId;
	}

	public void setRecommendedId(int recommendedId) {
		this.recommendedId = recommendedId;
	}

	public String getRecommendedCode() {
		return recommendedCode;
	}

	public void setRecommendedCode(String recommendedCode) {
		this.recommendedCode = recommendedCode;
	}

	public String getExpiredDate() {
		return expiredDate;
	}

	public void setExpiredDate(String expiredDate) {
		this.expiredDate = expiredDate;
	}

	public int getAgeMonth() {
		return ageMonth;
	}

	public void setAgeMonth(int ageMonth) {
		this.ageMonth = ageMonth;
	}

	public String getCancelReason() {
		return cancelReason;
	}

	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
	}

	public String getDaysBeforeExpire() {
		return daysBeforeExpire;
	}

	public void setDaysBeforeExpire(String daysBeforeExpire) {
		this.daysBeforeExpire = daysBeforeExpire;
	}

	public String getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(String interfaces) {
		this.interfaces = interfaces;
	}

	public boolean isExpired() {
		return expired;
	}

	public void setExpired(boolean expired) {
		this.expired = expired;
	}

	public String getPartyType() {
		return partyType;
	}

	public void setPartyType(String partyType) {
		this.partyType = partyType;
	}

	public String getDeliveryGroup() {
		return deliveryGroup;
	}

	public void setDeliveryGroup(String deliveryGroup) {
		this.deliveryGroup = deliveryGroup;
	}

	public String getExported() {
		return exported;
	}

	public void setExported(String exported) {
		this.exported = exported;
	}

	public String getIsvip() {
		return isvip;
	}

	public void setIsvip(String isvip) {
		this.isvip = isvip;
	}

	public String getShippingTimeTo() {
		return shippingTimeTo;
	}

	public void setShippingTimeTo(String shippingTimeTo) {
		this.shippingTimeTo = shippingTimeTo;
	}

	public String getCreditcardExpired() {
		return creditcardExpired;
	}

	public void setCreditcardExpired(String creditcardExpired) {
		this.creditcardExpired = creditcardExpired;
	}
	
	public String getFirstDeliverlyDate() {
		return firstDeliverlyDate;
	}

	public void setFirstDeliverlyDate(String firstDeliverlyDate) {
		this.firstDeliverlyDate = firstDeliverlyDate;
	}


	public int getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(int paymentType) {
		this.paymentType = paymentType;
	}

	public String getIsFreeOfChart() {
		return isFreeOfChart;
	}

	public void setIsFreeOfChart(String isFreeOfChart) {
		this.isFreeOfChart = isFreeOfChart;
	}

	public String getCreditCardNo() {
		return creditCardNo;
	}

	public void setCreditCardNo(String creditCardNo) {
		this.creditCardNo = creditCardNo;
	}

	public String getCreditCardBank() {
		return creditCardBank;
	}

	public void setCreditCardBank(String creditCardBank) {
		this.creditCardBank = creditCardBank;
	}
	
	private String cardName ;

	public String getCardName() {
		return cardName;
	}

	public void setCardName(String cardName) {
		this.cardName = cardName;
	}
	
}
