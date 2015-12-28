package com.isecinc.pens.bean;

import java.io.Serializable;

public class SaveInvoiceBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1011918568953216669L;

	private String ACTIVITY_CODE         ;   
	private String BILL_10               ;        
	private String CUST_ID               ;        
	private String SHIP_NO               ;        
	private String BUS_CODE              ;        
	private String DEPT_CODE             ;        
	private String PRODUCT_CODE          ;        
	private String SPD_CODE              ;        
	private String PRODUCT_TNAME         ;        
	private String BILL_DATE             ;        
	private String SHIP_TO_ADDRESS       ;        
	private String SHIP_NAME             ;        
	private String TSC_ID                ;        
	private String TSC_NAME              ;        
	private String SITE_ID               ;        
	private String NET_AMOUNT            ;        
	private String TDH_GEN_NO            ;        
	private String PRODUCT_BARCODE       ;        
	private String BKK_UPC_FLAG          ;        
	private String BILL_NO               ;        
	private String VIA_TO_ADDRESS        ;        
	private String VIA_NAME              ;        
	private String CORNER_ID             ;        
	private String CORNER_NAME           ;        
	private String DIS_SALE_PASS         ;        
	private String DISC1_PERCENT         ;        
	private String DISC2_PERCENT         ;        
	private String DISC_BAHT             ;        
	private String ZIPCODE               ;        
	private String SHIP_PHONE            ;        
	private String VIA_PHONE             ;        
	private String CASH_FLAG             ;        
	private String DELIVERY_DATE         ;        
	private String PO_NO                 ;        
	private String FROM_SYSTEM           ;        
	private String GROUP_NO_BILL_REPLACE ;        
	private String SORTER_ROUND          ;        
	private String SORTER_BATCH          ;        
	private String SORTER_CHUTE          ;        

	private String ORACLE_INVOICE_NO;
	private String createUser;
	private String updateUser;
	private String custCode;
	private String custDesc;
	
	
	public String getCustCode() {
		return custCode;
	}
	public void setCustCode(String custCode) {
		this.custCode = custCode;
	}
	public String getCustDesc() {
		return custDesc;
	}
	public void setCustDesc(String custDesc) {
		this.custDesc = custDesc;
	}
	public String getACTIVITY_CODE() {
		return ACTIVITY_CODE;
	}
	public void setACTIVITY_CODE(String aCTIVITY_CODE) {
		ACTIVITY_CODE = aCTIVITY_CODE;
	}
	public String getBILL_10() {
		return BILL_10;
	}
	public void setBILL_10(String bILL_10) {
		BILL_10 = bILL_10;
	}
	public String getCUST_ID() {
		return CUST_ID;
	}
	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}
	public String getSHIP_NO() {
		return SHIP_NO;
	}
	public void setSHIP_NO(String sHIP_NO) {
		SHIP_NO = sHIP_NO;
	}
	public String getBUS_CODE() {
		return BUS_CODE;
	}
	public void setBUS_CODE(String bUS_CODE) {
		BUS_CODE = bUS_CODE;
	}
	public String getDEPT_CODE() {
		return DEPT_CODE;
	}
	public void setDEPT_CODE(String dEPT_CODE) {
		DEPT_CODE = dEPT_CODE;
	}
	public String getPRODUCT_CODE() {
		return PRODUCT_CODE;
	}
	public void setPRODUCT_CODE(String pRODUCT_CODE) {
		PRODUCT_CODE = pRODUCT_CODE;
	}
	public String getSPD_CODE() {
		return SPD_CODE;
	}
	public void setSPD_CODE(String sPD_CODE) {
		SPD_CODE = sPD_CODE;
	}
	public String getPRODUCT_TNAME() {
		return PRODUCT_TNAME;
	}
	public void setPRODUCT_TNAME(String pRODUCT_TNAME) {
		PRODUCT_TNAME = pRODUCT_TNAME;
	}
	public String getBILL_DATE() {
		return BILL_DATE;
	}
	public void setBILL_DATE(String bILL_DATE) {
		BILL_DATE = bILL_DATE;
	}
	public String getSHIP_TO_ADDRESS() {
		return SHIP_TO_ADDRESS;
	}
	public void setSHIP_TO_ADDRESS(String sHIP_TO_ADDRESS) {
		SHIP_TO_ADDRESS = sHIP_TO_ADDRESS;
	}
	public String getSHIP_NAME() {
		return SHIP_NAME;
	}
	public void setSHIP_NAME(String sHIP_NAME) {
		SHIP_NAME = sHIP_NAME;
	}
	public String getTSC_ID() {
		return TSC_ID;
	}
	public void setTSC_ID(String tSC_ID) {
		TSC_ID = tSC_ID;
	}
	public String getTSC_NAME() {
		return TSC_NAME;
	}
	public void setTSC_NAME(String tSC_NAME) {
		TSC_NAME = tSC_NAME;
	}
	public String getSITE_ID() {
		return SITE_ID;
	}
	public void setSITE_ID(String sITE_ID) {
		SITE_ID = sITE_ID;
	}
	public String getNET_AMOUNT() {
		return NET_AMOUNT;
	}
	public void setNET_AMOUNT(String nET_AMOUNT) {
		NET_AMOUNT = nET_AMOUNT;
	}
	public String getTDH_GEN_NO() {
		return TDH_GEN_NO;
	}
	public void setTDH_GEN_NO(String tDH_GEN_NO) {
		TDH_GEN_NO = tDH_GEN_NO;
	}
	public String getPRODUCT_BARCODE() {
		return PRODUCT_BARCODE;
	}
	public void setPRODUCT_BARCODE(String pRODUCT_BARCODE) {
		PRODUCT_BARCODE = pRODUCT_BARCODE;
	}
	public String getBKK_UPC_FLAG() {
		return BKK_UPC_FLAG;
	}
	public void setBKK_UPC_FLAG(String bKK_UPC_FLAG) {
		BKK_UPC_FLAG = bKK_UPC_FLAG;
	}
	public String getBILL_NO() {
		return BILL_NO;
	}
	public void setBILL_NO(String bILL_NO) {
		BILL_NO = bILL_NO;
	}
	public String getVIA_TO_ADDRESS() {
		return VIA_TO_ADDRESS;
	}
	public void setVIA_TO_ADDRESS(String vIA_TO_ADDRESS) {
		VIA_TO_ADDRESS = vIA_TO_ADDRESS;
	}
	public String getVIA_NAME() {
		return VIA_NAME;
	}
	public void setVIA_NAME(String vIA_NAME) {
		VIA_NAME = vIA_NAME;
	}
	public String getCORNER_ID() {
		return CORNER_ID;
	}
	public void setCORNER_ID(String cORNER_ID) {
		CORNER_ID = cORNER_ID;
	}
	public String getCORNER_NAME() {
		return CORNER_NAME;
	}
	public void setCORNER_NAME(String cORNER_NAME) {
		CORNER_NAME = cORNER_NAME;
	}
	public String getDIS_SALE_PASS() {
		return DIS_SALE_PASS;
	}
	public void setDIS_SALE_PASS(String dIS_SALE_PASS) {
		DIS_SALE_PASS = dIS_SALE_PASS;
	}
	public String getDISC1_PERCENT() {
		return DISC1_PERCENT;
	}
	public void setDISC1_PERCENT(String dISC1_PERCENT) {
		DISC1_PERCENT = dISC1_PERCENT;
	}
	public String getDISC2_PERCENT() {
		return DISC2_PERCENT;
	}
	public void setDISC2_PERCENT(String dISC2_PERCENT) {
		DISC2_PERCENT = dISC2_PERCENT;
	}
	public String getDISC_BAHT() {
		return DISC_BAHT;
	}
	public void setDISC_BAHT(String dISC_BAHT) {
		DISC_BAHT = dISC_BAHT;
	}
	public String getZIPCODE() {
		return ZIPCODE;
	}
	public void setZIPCODE(String zIPCODE) {
		ZIPCODE = zIPCODE;
	}
	public String getSHIP_PHONE() {
		return SHIP_PHONE;
	}
	public void setSHIP_PHONE(String sHIP_PHONE) {
		SHIP_PHONE = sHIP_PHONE;
	}
	public String getVIA_PHONE() {
		return VIA_PHONE;
	}
	public void setVIA_PHONE(String vIA_PHONE) {
		VIA_PHONE = vIA_PHONE;
	}
	public String getCASH_FLAG() {
		return CASH_FLAG;
	}
	public void setCASH_FLAG(String cASH_FLAG) {
		CASH_FLAG = cASH_FLAG;
	}
	public String getDELIVERY_DATE() {
		return DELIVERY_DATE;
	}
	public void setDELIVERY_DATE(String dELIVERY_DATE) {
		DELIVERY_DATE = dELIVERY_DATE;
	}
	public String getPO_NO() {
		return PO_NO;
	}
	public void setPO_NO(String pO_NO) {
		PO_NO = pO_NO;
	}
	public String getFROM_SYSTEM() {
		return FROM_SYSTEM;
	}
	public void setFROM_SYSTEM(String fROM_SYSTEM) {
		FROM_SYSTEM = fROM_SYSTEM;
	}
	public String getGROUP_NO_BILL_REPLACE() {
		return GROUP_NO_BILL_REPLACE;
	}
	public void setGROUP_NO_BILL_REPLACE(String gROUP_NO_BILL_REPLACE) {
		GROUP_NO_BILL_REPLACE = gROUP_NO_BILL_REPLACE;
	}
	public String getSORTER_ROUND() {
		return SORTER_ROUND;
	}
	public void setSORTER_ROUND(String sORTER_ROUND) {
		SORTER_ROUND = sORTER_ROUND;
	}
	public String getSORTER_BATCH() {
		return SORTER_BATCH;
	}
	public void setSORTER_BATCH(String sORTER_BATCH) {
		SORTER_BATCH = sORTER_BATCH;
	}
	public String getSORTER_CHUTE() {
		return SORTER_CHUTE;
	}
	public void setSORTER_CHUTE(String sORTER_CHUTE) {
		SORTER_CHUTE = sORTER_CHUTE;
	}
	public String getORACLE_INVOICE_NO() {
		return ORACLE_INVOICE_NO;
	}
	public void setORACLE_INVOICE_NO(String oRACLE_INVOICE_NO) {
		ORACLE_INVOICE_NO = oRACLE_INVOICE_NO;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public String getUpdateUser() {
		return updateUser;
	}
	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}
	
	
}
