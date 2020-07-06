package com.isecinc.pens.web.report.controlall;

import java.io.Serializable;
import java.util.List;

import util.ConvertNullUtil;

/**
 * Detailed Sales Report
 * 
 * @author Aneak.t
 * @version $Id: DetailedSalesReport.java,v 1.0 20/01/2011 15:52:00 aneak.t Exp $
 * 
 */

public class ControlAllReport implements Serializable{

	private static final long serialVersionUID = 9117495907654379254L;

	private String startDate;
	private String endDate;
	private String no;
	private int id;
	private String period;
	private String recordType;
	private String date;
	private String docNo;
	
	private List<ControlAllReport> items;

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getRecordType() {
		return recordType;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDocNo() {
		return docNo;
	}

	public void setDocNo(String docNo) {
		this.docNo = docNo;
	}

	public List<ControlAllReport> getItems() {
		return items;
	}

	public void setItems(List<ControlAllReport> items) {
		this.items = items;
	}
	
	
	
}
