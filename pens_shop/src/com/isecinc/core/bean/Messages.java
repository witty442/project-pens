package com.isecinc.core.bean;

import java.io.Serializable;
import java.sql.ResultSet;

/**
 * Messages
 * 
 * @author Atiz.b
 * @version $Id: Messages.java,v 1.0 21/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class Messages implements Serializable {

	private static final long serialVersionUID = -4307192124977084694L;

	public static String SAVE_SUCCESS = "I0001";
	public static String RECORD_NOT_FOUND = "I0002";
	public static String NO_ACCESS_PRIVILEGE = "I0003";
	public static String SAVE_FAIL = "F0001";
	public static String FETAL_ERROR = "F0002";
	public static String DUPLICATE = "F0003";
	public static String NO_PRICELIST = "F0004";
	
	public static String CHEQUE_DUPLICATE = "E0001";

	/**
	 * Default Constructor
	 */
	public Messages() {}

	/**
	 * Default Constructor with rst
	 * 
	 * @param rst
	 * @throws Exception
	 */
	public Messages(ResultSet rst) throws Exception {
		setMessageCode(rst.getString("MESSAGE_CODE"));
		setMessageDescTH(rst.getString("DESCRIPTION_TH"));
		setMessageDescEN(rst.getString("DESCRIPTION_EN"));
	}

	/** MESSAGE_CODE */
	private String messageCode;

	/** DESCRIPTION_TH */
	private String messageDescTH;

	/** DESCRIPTION_EN */
	private String messageDescEN;

	/**
	 * Get Description
	 * 
	 * @return
	 */
	public String getDesc() {
		return getMessageDescTH();
		// Locale loc = Locale.getDefault();
		// if (loc.getLanguage().equalsIgnoreCase("TH")) return getMessageDescTH();
		// else return getMessageDescEN();
	}

	/**
	 * To String
	 */
	public String toString() {
		return String.format("Message[%s]-[%s] [%s]", getMessageCode(), getMessageDescTH(), getMessageDescEN());
	}

	public String getMessageCode() {
		return messageCode;
	}

	public void setMessageCode(String messageCode) {
		this.messageCode = messageCode;
	}

	public String getMessageDescTH() {
		return messageDescTH;
	}

	public void setMessageDescTH(String messageDescTH) {
		this.messageDescTH = messageDescTH;
	}

	public String getMessageDescEN() {
		return messageDescEN;
	}

	public void setMessageDescEN(String messageDescEN) {
		this.messageDescEN = messageDescEN;
	}

}
