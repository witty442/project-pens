package com.isecinc.pens.web.buds;

import java.io.Serializable;

import com.isecinc.pens.bean.ConfPickingBean;
import com.isecinc.pens.bean.OrderEDIBean;
public class BudsAllBean implements  Serializable{

/**
 * 
 */
private static final long serialVersionUID = 8389174957413702703L;

private ConfPickingBean confPickingBean;
private OrderEDIBean orderEDIBean;


public ConfPickingBean getConfPickingBean() {
	return confPickingBean;
}

public void setConfPickingBean(ConfPickingBean confPickingBean) {
	this.confPickingBean = confPickingBean;
}

public OrderEDIBean getOrderEDIBean() {
	return orderEDIBean;
}

public void setOrderEDIBean(OrderEDIBean orderEDIBean) {
	this.orderEDIBean = orderEDIBean;
}
 
  
}
