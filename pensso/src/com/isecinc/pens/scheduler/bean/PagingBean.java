package com.isecinc.pens.scheduler.bean;

import java.util.List;

public class PagingBean {
	private List itemList;
	private int totalPage;
	private int currentPage;
	private int recPerPage;

	public List getItemList() {
		return itemList;
	}
	public void setItemList(List itemList) {
		this.itemList = itemList;
	}
	public int getTotalPage() {
		return totalPage;
	}
	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	
	public int getInitialPage(){
		totalPage = itemList.size()/recPerPage;
		
		return totalPage;
	}
	public PagingBean(List itemList,int recPerPage){
		
		this.itemList = itemList;
		this.recPerPage = recPerPage;
		
	}
}
