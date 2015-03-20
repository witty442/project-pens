package com.isecinc.pens.web.shipment;

import com.isecinc.core.web.I_Criteria;
import com.isecinc.pens.bean.OrderLine;
import com.isecinc.pens.bean.ShipmentConfirm;

/**
 * Order Criteria
 * 
 * @author atiz.b
 * @version $Id: OrderCriteria.java,v 1.0 14/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class ShipmentCriteria extends I_Criteria {

	private static final long serialVersionUID = 6220881218635143101L;

	private OrderLine orderLine = new OrderLine();
	
	private ShipmentConfirm shipment = new ShipmentConfirm();

	public OrderLine getOrderLine() {
		return orderLine;
	}

	public void setOrderLine(OrderLine orderLine) {
		this.orderLine = orderLine;
	}

	public ShipmentConfirm getShipment() {
		return shipment;
	}

	public void setShipment(ShipmentConfirm shipment) {
		this.shipment = shipment;
	}
}
