package com.isecinc.pens.process;

public class OrderKeyBean {

	private String orderNo;
	private String barOnBox;
	
	public OrderKeyBean(String orderNo,String barOnBox){
		this.orderNo=orderNo;
		this.barOnBox=barOnBox;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getBarOnBox() {
		return barOnBox;
	}
	public void setBarOnBox(String barOnBox) {
		this.barOnBox = barOnBox;
	}
	
	

}
