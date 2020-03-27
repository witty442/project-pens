package com.isecinc.pens.bean;

import java.io.Serializable;

public class PageingBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int totalPage;
	private int startRec;
	private int endRec;
	private int no ;
	private int currPage;
	
	private StringBuffer pageingHtml;
	
	
	public int getCurrPage() {
		return currPage;
	}
	public void setCurrPage(int currPage) {
		this.currPage = currPage;
	}
	public StringBuffer getPageingHtml() {
		return pageingHtml;
	}
	public void setPageingHtml(StringBuffer pageingHtml) {
		this.pageingHtml = pageingHtml;
	}
	public int getTotalPage() {
		return totalPage;
	}
	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
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
	public int getNo() {
		return no;
	}
	public void setNo(int no) {
		this.no = no;
	}
	
	
}
