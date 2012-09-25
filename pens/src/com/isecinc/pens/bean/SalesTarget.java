package com.isecinc.pens.bean;

import java.io.Serializable;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.Locale;

import com.isecinc.core.bean.References;
import com.isecinc.core.model.I_PO;
import com.isecinc.pens.SystemElements;
import com.isecinc.pens.init.InitialReferences;
import com.isecinc.pens.model.MUser;

/**
 * SalesTarget
 * 
 * @author Aneak.t
 * @version $Id: SalesTarget.java,v 1.0 07/10/2010 15:52:00 aneak.t Exp $
 * 
 */

public class SalesTarget extends I_PO implements Serializable {

	private static final long serialVersionUID = -919128368293295221L;

	/**
	 * Default Constructor
	 */
	public SalesTarget() {

	}

	/**
	 * Constructor with rst
	 * 
	 * @param rst
	 * @throws Exception
	 */
	public SalesTarget(ResultSet rst) throws Exception {
		// Mandatory
		setId(rst.getInt("SALES_TARGET_ID"));
		setMonth(rst.getString("MONTH").trim());
		setTargetAmount(rst.getDouble("TARGET_AMOUNT"));
		setUser(new MUser().find(String.valueOf(rst.getInt("USER_ID"))));
		setIsActive(rst.getString("ISACTIVE").trim());
		setYear(rst.getInt("YEAR"));

		// set display label
		setDisplayLabel();

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
		setTargetAmountLabel(new DecimalFormat("#,##0.00").format(getTargetAmount()));
	}

	private int id;
	private String month;
	private String monthLabel;
	private double targetAmount;
	private User user = new User();
	private String isActive;
	private int year;

	/** Target Amount Label */
	private String targetAmountLabel;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public double getTargetAmount() {
		return targetAmount;
	}

	public void setTargetAmount(double targetAmount) {
		this.targetAmount = targetAmount;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getIsActive() {
		return isActive;
	}

	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getMonthLabel() {
		if (month.equals(SystemElements.JAN)) {
			monthLabel = SystemElements.getCaption(SystemElements.JAN, new Locale("th, TH"));
		} else if (month.equals(SystemElements.FEB)) {
			monthLabel = SystemElements.getCaption(SystemElements.FEB, new Locale("th, TH"));
		} else if (month.equals(SystemElements.MAR)) {
			monthLabel = SystemElements.getCaption(SystemElements.MAR, new Locale("th, TH"));
		} else if (month.equals(SystemElements.APR)) {
			monthLabel = SystemElements.getCaption(SystemElements.APR, new Locale("th, TH"));
		} else if (month.equals(SystemElements.MAY)) {
			monthLabel = SystemElements.getCaption(SystemElements.MAY, new Locale("th, TH"));
		} else if (month.equals(SystemElements.JUN)) {
			monthLabel = SystemElements.getCaption(SystemElements.JUN, new Locale("th, TH"));
		} else if (month.equals(SystemElements.JUL)) {
			monthLabel = SystemElements.getCaption(SystemElements.JUL, new Locale("th, TH"));
		} else if (month.equals(SystemElements.AUG)) {
			monthLabel = SystemElements.getCaption(SystemElements.AUG, new Locale("th, TH"));
		} else if (month.equals(SystemElements.SEP)) {
			monthLabel = SystemElements.getCaption(SystemElements.SEP, new Locale("th, TH"));
		} else if (month.equals(SystemElements.OCT)) {
			monthLabel = SystemElements.getCaption(SystemElements.OCT, new Locale("th, TH"));
		} else if (month.equals(SystemElements.NOV)) {
			monthLabel = SystemElements.getCaption(SystemElements.NOV, new Locale("th, TH"));
		} else if (month.equals(SystemElements.DEC)) {
			monthLabel = SystemElements.getCaption(SystemElements.DEC, new Locale("th, TH"));
		}

		return monthLabel;
	}

	public String getTargetAmountLabel() {
		return targetAmountLabel;
	}

	public void setTargetAmountLabel(String targetAmountLabel) {
		this.targetAmountLabel = targetAmountLabel;
	}

}
