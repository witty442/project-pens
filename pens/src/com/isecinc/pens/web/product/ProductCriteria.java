package com.isecinc.pens.web.product;

import com.isecinc.core.web.I_Criteria;
import com.isecinc.pens.bean.Product;

/**
 * Product Criteria
 * 
 * @author Aneak.t
 * @version $Id: ProductCriteria.java,v 1.0 06/10/2010 00:00:00 aneak.t Exp $
 * 
 */

public class ProductCriteria extends I_Criteria{

	private static final long serialVersionUID = 1866638989475543234L;
	
	private Product product = new Product();

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}
	
}
