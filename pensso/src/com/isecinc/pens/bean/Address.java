package com.isecinc.pens.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;

import com.isecinc.core.bean.References;
import com.isecinc.core.model.I_PO;
import com.isecinc.pens.init.InitialReferences;
import com.isecinc.pens.model.MDistrict;
import com.isecinc.pens.model.MProvince;
import com.pens.util.ConvertNullUtil;
import com.pens.util.Utils;

/**
 * Address
 * 
 * @author Aneak.t
 * @version $Id: Address.java,v 1.0 07/10/2010 15:52:00 aneak.t Exp $
 * 
 */
public class Address extends I_PO implements Serializable {

	private static final long serialVersionUID = -2634020238501083540L;

	/**
	 * Default Constructor
	 */
	public Address() {}

	/**
	 * Default Constructor with rst
	 * 
	 * @throws Exception
	 */
	public Address(ResultSet rst) throws Exception {
		setId(rst.getInt("ADDRESS_ID"));
		setCustomerId(rst.getLong("CUSTOMER_ID"));
		setLine1(ConvertNullUtil.convertToString(rst.getString("Line1")).trim());
		setLine2(ConvertNullUtil.convertToString(rst.getString("Line2")).trim());
		setLine3(ConvertNullUtil.convertToString(rst.getString("Line3")).trim());
		setLine4(ConvertNullUtil.convertToString(rst.getString("Line4")).trim());
		setDistrict(new MDistrict().find(rst.getString("DISTRICT_ID")));
		setProvince(new MProvince().find(rst.getString("PROVINCE_ID")));
		setPostalCode(rst.getString("POSTAL_CODE").trim());
		setPurpose(rst.getString("PURPOSE").trim());
		setIsActive(rst.getString("ISACTIVE").trim());
		setAlternateName(Utils.isNull(rst.getString("alternate_name")));
		/** Display */
		setDisplayLabel();
		
		// Add Oracle Reference ID
		setSiteUseId(rst.getInt("SITE_USE_ID"));
		setReferenceId(rst.getInt("REFERENCE_ID"));
	}

	/**
	 * Set Display Label
	 */
	protected void setDisplayLabel() throws Exception {
		for (References r : InitialReferences.getReferenes().get(InitialReferences.ACTIVE)) {
			if (r.getKey().equalsIgnoreCase(getIsActive())) {
				setActiveLabel(r.getName());
				break;
			}
		}

		if (getPurpose().equalsIgnoreCase("B")) {
			setPurposeLabel("Bill To");
		} else {
			setPurposeLabel("Ship To");
		}

	}

	/**
	 * Get Line String
	 * 
	 * @return
	 */
	public String getLineString() {
		String lineString;
		lineString = "";
		if( !Utils.isNull(getAlternateName()).equals("")){
		   lineString = "("+Utils.isNull(getAlternateName())+") ";
		}
		lineString += getLine1() + " ";
		lineString += getLine2() + " ";
		if ("กรุงเทพฯ".equalsIgnoreCase(getProvince().getName())
				|| "กรุงเทพมหานคร".equalsIgnoreCase(getProvince().getName())) {
			lineString += "แขวง";
			lineString += (getLine3()) + " ";
			lineString += "เขต";
			lineString += (getDistrict().getName()) + " ";
			lineString += "";
		} else {
			lineString += "ตำบล";
			lineString += (getLine3()) + " ";
			lineString += "อำเภอ";
			lineString += (getDistrict().getName()) + " ";
			lineString += "จังหวัด";
		}
		lineString += (getProvince().getName()) + " ";
		lineString += (getPostalCode());
		return lineString;
	}

	/** ID */
	private int id;

	/** LINE1 */
	private String line1;

	/** LINE2 */
	private String line2;

	/** LINE3 */
	private String line3;

	/** LINE4 */
	private String line4;

	/** DISTRICT */
	private District district = new District();

	/** PROVINCE */
	private Province province = new Province();

	/** POSTAL CODE */
	private String postalCode;

	/** COUNTRY */
	private String country;

	/** PURPOSE */
	private String purpose;

	/** PURPOSE Label */
	private String purposeLabel;

	/** ACTIVE */
	private String isActive;

	/** CUSTOMER ID */
	private long customerId;
	private long siteUseId;
	
	/** Reference ID (Oracle Address ID)*/
	private int referenceId;
    private String alternateName;
    
    
	public long getSiteUseId() {
		return siteUseId;
	}

	public void setSiteUseId(long siteUseId) {
		this.siteUseId = siteUseId;
	}

	public String getAlternateName() {
		return alternateName;
	}

	public void setAlternateName(String alternateName) {
		this.alternateName = alternateName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLine1() {
		return line1;
	}

	public void setLine1(String line1) {
		this.line1 = line1;
	}

	public String getLine2() {
		return line2;
	}

	public void setLine2(String line2) {
		this.line2 = line2;
	}

	public String getLine3() {
		return line3;
	}

	public void setLine3(String line3) {
		this.line3 = line3;
	}

	public String getLine4() {
		return line4;
	}

	public void setLine4(String line4) {
		this.line4 = line4;
	}

	public District getDistrict() {
		return district;
	}

	public void setDistrict(District district) {
		this.district = district;
	}

	public Province getProvince() {
		return province;
	}

	public void setProvince(Province province) {
		this.province = province;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getPurposeLabel() {
		return purposeLabel;
	}

	public void setPurposeLabel(String purposeLabel) {
		this.purposeLabel = purposeLabel;
	}

	public long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}

	public int getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(int referenceId) {
		this.referenceId = referenceId;
	}
}
