package com.isecinc.core.model;

import java.io.Serializable;

/**
 * I_PO Class
 * 
 * @author Atiz.b
 * @version $Id: I_PO.java,v 1.0 06/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public abstract class I_PO implements Serializable {

	private static final long serialVersionUID = -842028756977462875L;

	//OLD STATUS
	public static String DOC_SAVE = "SV";//OPEN
	
	//STEP ORDER
	public static String STATUS_CANCEL = "CANCEL";//Cancel
	public static String STATUS_RESERVE = "RESERVE";//Reserve
	public static String STATUS_UNAVAILABLE = "UNAVAILABLE";//UNAVAILABLE
	
	//STEP PICKING ,LOADING
	public static String STATUS_PICKING = "PICKING";//PICKING (print pick)
	public static String STATUS_REJECT = "REJECT";// REJECT
	public static String STATUS_LOADING = "LOADING";//LOADING create Order Oracle 
	

	private String activeLabel;

	//protected abstract void setDisplayLabel() throws Exception;

	public String getActiveLabel() {
		return activeLabel;
	}

	public void setActiveLabel(String activeLabel) {
		this.activeLabel = activeLabel;
	}

	// start paginator
	private String txtDorA = "ASC";
	private String orderBy;

	public String getTxtDorA() {
		return this.txtDorA;
	}
	public void setTxtDorA(String txtDorA) {
		this.txtDorA = txtDorA;
	}
	public String getOrderBy() {
		return this.orderBy;
	}
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	// end paginator
}
