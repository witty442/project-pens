package com.isecinc.core.web;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;

/**
 * I_Form Class
 * 
 * @author Atiz.b
 * @version $Id: I_Form.java,v 1.0 13/06/2010 15:52:00 atiz.b Exp $
 * 
 */
public abstract class I_Form extends ActionForm {

	private static final long serialVersionUID = -5128814193636175092L;

	/** Logger */
	public static Logger logger = Logger.getLogger("PENS");

	/** ID with Multiplicity */
	private String[] ids;

	/** ID for Find */
	private String id;

	/** Status for Change */
	private String status;
	
   //Wit Edit 19/09/2017 pageing
	private int currPage;
	private int totalPage;
	private int totalRecord;
	private int startRec;
	private int endRec;
	private int pageSize;
	
	
	public int getCurrPage() {
		return currPage;
	}

	public void setCurrPage(int currPage) {
		this.currPage = currPage;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getTotalRecord() {
		return totalRecord;
	}

	public void setTotalRecord(int totalRecord) {
		this.totalRecord = totalRecord;
	}

	public int getStartRec() {
		return startRec;
	}

	public void setStartRec(int startRec) {
		this.startRec = startRec;
	}

	public int getEndRec() {
		return endRec;
	}

	public void setEndRec(int endRec) {
		this.endRec = endRec;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public String[] getIds() {
		return ids;
	}

	public void setIds(String[] ids) {
		this.ids = ids;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	// start paginator
	private String resultScreen;
	private String pageObjKey;
	private String currentPage;
	private String orderBy;
	private String txtDorA;
	private String focusFieldId;

	public String getResultScreen() {
		return resultScreen;
	}
	public void setResultScreen(String resultScreen) {
		this.resultScreen = resultScreen;
	}
	public String getPageObjKey() {
		return pageObjKey;
	}
	public void setPageObjKey(String pageObjKey) {
		this.pageObjKey = pageObjKey;
	}
	public String getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(String currentPage) {
		this.currentPage = currentPage;
	}
	public String getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	public String getTxtDorA() {
		return txtDorA;
	}
	public void setTxtDorA(String txtDorA) {
		this.txtDorA = txtDorA;
	}
	public String getFocusFieldId() {
		return focusFieldId;
	}
	public void setFocusFieldId(String focusFieldId) {
		this.focusFieldId = focusFieldId;
	}
	// end paginator
}
