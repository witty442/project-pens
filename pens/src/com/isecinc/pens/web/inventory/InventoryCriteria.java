package com.isecinc.pens.web.inventory;

import com.isecinc.core.web.I_Criteria;
import com.isecinc.pens.bean.Inventory;

/**
 * Product Criteria
 * 
 * @author Aneak.t
 * @version $Id: ProductCriteria.java,v 1.0 06/10/2010 00:00:00 aneak.t Exp $
 * 
 */

public class InventoryCriteria extends I_Criteria{

	private static final long serialVersionUID = -7484955808713858060L;
	
	private Inventory inventory = new Inventory();

	public Inventory getInventory() {
		return inventory;
	}

	public void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}
	
}
