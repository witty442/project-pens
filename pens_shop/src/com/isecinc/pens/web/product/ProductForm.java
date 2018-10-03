package com.isecinc.pens.web.product;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.Product;

/**
 * Product Form
 * 
 * @author Aneak.t
 * @version $Id: ProductForm.java,v 1.0 06/10/2010 00:00:00 aneak.t Exp $
 * 
 */

public class ProductForm extends I_Form{

	private static final long serialVersionUID = 7620409792910183595L;

	private ProductCriteria criteria = new ProductCriteria();
	
	private Product[] results = null;

	public Product getProduct() {
		return criteria.getProduct();
	}

	public void setProduct(Product product) {
		criteria.setProduct(product);
	}

	public ProductCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(ProductCriteria criteria) {
		this.criteria = criteria;
	}

	public Product[] getResults() {
		return results;
	}

	public void setResults(Product[] results) {
		this.results = results;
	}
	
	
}
