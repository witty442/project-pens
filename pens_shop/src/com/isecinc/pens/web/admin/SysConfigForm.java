package com.isecinc.pens.web.admin;

import com.isecinc.core.web.I_Form;

/**
 * SysConfig Form
 * 
 * @author Atiz.b
 * @version $Id: SysConfigForm.java,v 1.0 06/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class SysConfigForm extends I_Form {

	private static final long serialVersionUID = -7921440992157646422L;

	/** ALERT PERIOD */
	private int alertPeriod;

	/** QTY DELIVER */
	private String qtyDeliver;

	/** ROUND DELIVER */
	private String roundDeliver;

	/** MEMBER TYPE */
	private String memberType;

	public int getAlertPeriod() {
		return alertPeriod;
	}

	public void setAlertPeriod(int alertPeriod) {
		this.alertPeriod = alertPeriod;
	}

	public String getQtyDeliver() {
		return qtyDeliver;
	}

	public void setQtyDeliver(String qtyDeliver) {
		this.qtyDeliver = qtyDeliver;
	}

	public String getRoundDeliver() {
		return roundDeliver;
	}

	public void setRoundDeliver(String roundDeliver) {
		this.roundDeliver = roundDeliver;
	}

	public String getMemberType() {
		return memberType;
	}

	public void setMemberType(String memberType) {
		this.memberType = memberType;
	}

}
