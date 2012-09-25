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

	public static String DOC_SAVE = "SV";
	public static String DOC_VOID = "VO";

	private String activeLabel;

	protected abstract void setDisplayLabel() throws Exception;

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
