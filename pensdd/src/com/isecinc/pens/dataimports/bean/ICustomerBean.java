package com.isecinc.pens.dataimports.bean;

import java.sql.ResultSet;

import com.isecinc.pens.bean.Customer;

/**
 * Customer
 * 
 * @author Aneak.t
 * @version $Id: Customer.java,v 1.0 07/10/2010 15:52:00 aneak.t Exp $
 * 
 *          atiz.b : code prefix transient
 * 
 */
public class ICustomerBean extends Customer {

	private static final long serialVersionUID = -8675517484785667619L;

	/**
	 * Default Constructor
	 */
	public ICustomerBean() {
		//
	}

	/**
	 * Constructor with rst
	 * 
	 * @param rst
	 * @throws Exception
	 */
	public ICustomerBean(ResultSet rst) throws Exception {
		// Mandatory
		setId(rst.getInt("ICUSTOMER_ID"));
		setImported(rst.getString("IMPORTED"));

		/*setCode(rst.getString("CODE").trim());
		setName(rst.getString("NAME").trim());
		setName2(rst.getString("NAME2").trim());
		setTerritory(rst.getString("TERRITORY").trim());
		setBusinessType(ConvertNullUtil.convertToString(rst.getString("BUSINESS_TYPE")).trim());
		setIsActive(rst.getString("ISACTIVE").trim());
		//setUser(new MUser().find(String.valueOf(rst.getInt("USER_ID"))));
		setPersonIDNo(rst.getString("PERSON_ID_NO").trim());
		setEmail(rst.getString("EMAIL").trim());
		setPaymentTerm(rst.getString("PAYMENT_TERM").trim());
		setVatCode(rst.getString("VAT_CODE").trim());
		//setExpiredDate("");
//		if (rst.getTimestamp("EXPIRED_DATE") != null) {
//			setExpiredDate(DateToolsUtil.convertToString(rst.getTimestamp("EXPIRED_DATE")));
//		}
		setBirthDay("");
		if (rst.getTimestamp("BIRTHDAY") != null) {
			setBirthDay(DateToolsUtil.convertToString(rst.getTimestamp("BIRTHDAY")));
		}
		setOccupation(ConvertNullUtil.convertToString(rst.getString("OCCUPATION")).trim());
		setCustomerType(rst.getString("CUSTOMER_TYPE").trim());
		setShippingMethod(ConvertNullUtil.convertToString(rst.getString("SHIPPING_METHOD")).trim());
//		setShippingDate(rst.getString("SHIPPING_DATE").trim());
//		setShippingTime(ConvertNullUtil.convertToString(rst.getString("SHIPPING_TIME")).trim());
//		setShippingTimeTo(ConvertNullUtil.convertToString(rst.getString("SHIPPING_TIME_TO")).trim());
		setMonthlyIncome(rst.getDouble("MONHTLY_INCOME"));
		setChrolesterol(rst.getDouble("CHOLESTEROL"));
		setMemberLevel(rst.getString("MEMBER_LEVEL").trim());
//		setRecommendedBy(rst.getString("RECOMMENDED_BY").trim());
//		setRegisterDate("");
//		if (rst.getTimestamp("REGISTER_DATE") != null) {
//			setRegisterDate(DateToolsUtil.convertToString(rst.getTimestamp("REGISTER_DATE")));
//		}
		setMemberType(rst.getString("MEMBER_TYPE").trim());
//		setCreated(rst.getTimestamp("CREATED"));
//		setCreatedBy(new MUser().find(String.valueOf(rst.getInt("CREATED_BY"))));
//		setUpdated(rst.getTimestamp("UPDATED"));
//		setUpdatedBy(new MUser().find(String.valueOf(rst.getInt("UPDATED_BY"))));
//		setOrderAmountPeriod(rst.getInt("ORDER_AMOUNT_PERIOD"));
		setPaymentMethod(rst.getString("PAYMENT_METHOD").trim());
//		setParentCustomerId(rst.getInt("PARENT_CUSTOMER_ID"));
//		setProductCategory(new MProductCategory().find(String.valueOf(rst.getInt("PRODUCT_CATEGORY_ID"))));
//		setRoundTrip(rst.getString("ROUND_TRIP").trim());
//
//		setRecommendedType(ConvertNullUtil.convertToString(rst.getString("RECOMMENDED_TYPE")).trim());
//		if (rst.getString("RECOMMENDED_ID") != null && rst.getInt("RECOMMENDED_ID") != 0) {
//			setRecommendedId(rst.getInt("RECOMMENDED_ID"));
//			setRecommendedCode(new MMember().find(String.valueOf(getRecommendedId())).getCode());
//		} else {
//			setRecommendedCode("");
//		}
//		setAgeMonth(rst.getInt("AGE_MONTH"));
//		setCancelReason(ConvertNullUtil.convertToString(rst.getString("CANCEL_REASON")).trim());
//		setDaysBeforeExpire(String.valueOf(DateToolsUtil.calDiffDate(DateToolsUtil.DAY, DateToolsUtil
//				.getCurrentDateTime("dd/MM/yyyy"), getExpiredDate())));
//		if (getDaysBeforeExpire() != null && !getDaysBeforeExpire().equals("")) {
//			if (Integer.parseInt(getDaysBeforeExpire()) < 0) {
//				setExpired(true);
//				setDaysBeforeExpire(SystemProperties.getCaption("MemberExpired", null));
//			}
//		}
//		setInterfaces(rst.getString("INTERFACES").trim());
//		setPartyType(ConvertNullUtil.convertToString(rst.getString("PARTY_TYPE")).trim());
//		setDeliveryGroup(ConvertNullUtil.convertToString(rst.getString("DELIVERY_GROUP")));
//		setExported(rst.getString("EXPORTED"));
//		setIsvip(rst.getString("ISVIP"));
//		setCreditcardExpired(ConvertNullUtil.convertToString(rst.getString("CREDITCARD_EXPIRED")).trim());

		// set display label
//		setDisplayLabel();*/
	}

	private String email;
	private String personIDNo;
	private String memberType;
	private String occupation;
	private double monthlyIncome;
	private double chrolesterol;
	private String memberLevel;
	private String imported;
	private String registerDate;
	private String expiredDate;
	private int ageMonth;
	private String shippingDate;
	private String shippingTime;
	private String shippingTimeTo;
	private String deliveryGroup;
	private String roundTrip;
	private int orderAmountPeriod;
	private String isVip;
	private String userId;
	private String importedDetail;
	private String creditcardExpired;
	private String cancelReason;
	private String recommendedBy;
	private int recommendedId;
	private String recommendedType;
	private int orderLineRemain;
	private String startNextYear;
	private double prepaidNextYear;
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPersonIDNo() {
		return personIDNo;
	}
	public void setPersonIDNo(String personIDNo) {
		this.personIDNo = personIDNo;
	}
	public String getMemberType() {
		return memberType;
	}
	public void setMemberType(String memberType) {
		this.memberType = memberType;
	}
	public String getOccupation() {
		return occupation;
	}
	public void setOccupation(String occupation) {
		this.occupation = occupation;
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
	public String getImported() {
		return imported;
	}
	public void setImported(String imported) {
		this.imported = imported;
	}
	public String getRegisterDate() {
		return registerDate;
	}
	public void setRegisterDate(String registerDate) {
		this.registerDate = registerDate;
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
	public String getShippingDate() {
		return shippingDate;
	}
	public void setShippingDate(String shippingDate) {
		this.shippingDate = shippingDate;
	}
	public String getShippingTime() {
		return shippingTime;
	}
	public void setShippingTime(String shippingTime) {
		this.shippingTime = shippingTime;
	}
	public String getDeliveryGroup() {
		return deliveryGroup;
	}
	public void setDeliveryGroup(String deliveryGroup) {
		this.deliveryGroup = deliveryGroup;
	}
	public String getRoundTrip() {
		return roundTrip;
	}
	public void setRoundTrip(String roundTrip) {
		this.roundTrip = roundTrip;
	}
	public int getOrderAmountPeriod() {
		return orderAmountPeriod;
	}
	public void setOrderAmountPeriod(int orderAmountPeriod) {
		this.orderAmountPeriod = orderAmountPeriod;
	}
	public String getIsVip() {
		return isVip;
	}
	public void setIsVip(String isVip) {
		this.isVip = isVip;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getImportedDetail() {
		return importedDetail;
	}
	public void setImportedDetail(String importedDetail) {
		this.importedDetail = importedDetail;
	}
	public String getCreditcardExpired() {
		return creditcardExpired;
	}
	public void setCreditcardExpired(String creditcardExpired) {
		this.creditcardExpired = creditcardExpired;
	}
	public String getShippingTimeTo() {
		return shippingTimeTo;
	}
	public void setShippingTimeTo(String shippingTimeTo) {
		this.shippingTimeTo = shippingTimeTo;
	}
	public String getCancelReason() {
		return cancelReason;
	}
	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
	}
	public String getRecommendedBy() {
		return recommendedBy;
	}
	public void setRecommendedBy(String recommendedBy) {
		this.recommendedBy = recommendedBy;
	}
	public int getRecommendedId() {
		return recommendedId;
	}
	public void setRecommendedId(int recommendedId) {
		this.recommendedId = recommendedId;
	}
	public String getRecommendedType() {
		return recommendedType;
	}
	public void setRecommendedType(String recommendedType) {
		this.recommendedType = recommendedType;
	}
	public int getOrderLineRemain() {
		return orderLineRemain;
	}
	public void setOrderLineRemain(int orderLineRemain) {
		this.orderLineRemain = orderLineRemain;
	}
	public String getStartNextYear() {
		return startNextYear;
	}
	public void setStartNextYear(String startNextYear) {
		this.startNextYear = startNextYear;
	}
	public double getPrepaidNextYear() {
		return prepaidNextYear;
	}
	public void setPrepaidNextYear(double prepaidNextYear) {
		this.prepaidNextYear = prepaidNextYear;
	}

}
