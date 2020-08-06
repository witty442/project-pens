package com.isecinc.pens.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;

import com.isecinc.core.bean.References;
import com.isecinc.core.model.I_PO;
import com.isecinc.pens.init.InitialReferences;
import com.isecinc.pens.model.MDocType;

/**
 * DocSeq Class
 * 
 * @author Atiz.b
 * @version $Id: DocSequence.java,v 1.0 08/10/2010 00:00:00 atiz.b Exp $
 * 
 *          atiz.b : visit const variable
 * 
 */
public class DocSequence extends I_PO implements Serializable {

	public static final String CUSTOMER_NUMBER = "CustomerNo";
	public static final String MEMBER_NUMBER = "MemberNo";
	public static final String ORDER_NUMNER = "OrderNo";
	public static final String RECEIPT_NUMNER = "ReceiptNo";
	public static final String VISIT_NUMBER = "VisitNo";

	private static final long serialVersionUID = 4513797097679382461L;

	/**
	 * Default Constructor
	 */
	public DocSequence() {}

	/**
	 * Default Constructor
	 * 
	 * @param rst
	 * @throws Exception
	 */
	public DocSequence(ResultSet rst) throws Exception {
		setId(rst.getLong("DOCTYPE_SEQUENCE_ID"));
		setDoctypeID(rst.getInt("DOCTYPE_ID"));
		setSalesCode(rst.getString("SALES_CODE").trim());
		setOrderType(rst.getString("ORDER_TYPE").trim());
		setStartNo(rst.getInt("START_NO"));
		setCurrentNext(rst.getInt("CURRENT_NEXT"));
		setCurrentYear(rst.getString("CURRENT_YEAR").trim());
		setCurrentMonth(rst.getString("CURRENT_MONTH").trim());
		setActive(rst.getString("ISACTIVE").trim());

		// setDispayLabel
		//setDisplayLabel();
	}

	/**
	 * Set Display Label
	 * 
	 * @throws Exception
	 */
	protected void setDisplayLabel() throws Exception {
		/*for (References r : InitialReferences.getReferenes().get(InitialReferences.DOC_RUN)) {
			if (r.getKey().equalsIgnoreCase(getOrderType())) {
				setOrderLabel(r.getName());
				break;
			}
		}
		setDoctypeLabel((new MDocType().find(String.valueOf(getDoctypeID()))).getDescription());*/
	}

	/** ID */
	private long id;

	/** DOCTYPE_ID */
	private int doctypeID;

	/** SALES CODE */
	private String salesCode;

	/** ORDER TYPE(COUNT BY) */
	private String orderType;

	/** START NO */
	private int startNo;

	/** CURRENT NEXT */
	private int currentNext;

	/** CURRENT YEAR */
	private String currentYear;

	/** CURRENT MONTH */
	private String currentMonth;

	/** ISACTIVE */
	private String active;

	/** ORDER LABEL */
	private String orderLabel;

	/** DOCTYPE LABEL */
	private String doctypeLabel;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getDoctypeID() {
		return doctypeID;
	}

	public void setDoctypeID(int doctypeID) {
		this.doctypeID = doctypeID;
	}

	public String getSalesCode() {
		return salesCode;
	}

	public void setSalesCode(String salesCode) {
		this.salesCode = salesCode;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public int getStartNo() {
		return startNo;
	}

	public void setStartNo(int startNo) {
		this.startNo = startNo;
	}

	public int getCurrentNext() {
		return currentNext;
	}

	public void setCurrentNext(int currentNext) {
		this.currentNext = currentNext;
	}

	public String getCurrentYear() {
		return currentYear;
	}

	public void setCurrentYear(String currentYear) {
		this.currentYear = currentYear;
	}

	public String getCurrentMonth() {
		return currentMonth;
	}

	public void setCurrentMonth(String currentMonth) {
		this.currentMonth = currentMonth;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getOrderLabel() {
		return orderLabel;
	}

	public void setOrderLabel(String orderLabel) {
		this.orderLabel = orderLabel;
	}

	public String getDoctypeLabel() {
		return doctypeLabel;
	}

	public void setDoctypeLabel(String doctypeLabel) {
		this.doctypeLabel = doctypeLabel;
	}

}
