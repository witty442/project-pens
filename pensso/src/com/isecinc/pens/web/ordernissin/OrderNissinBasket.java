package com.isecinc.pens.web.ordernissin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.isecinc.pens.web.sales.bean.ProductCatalog;

import net.sf.json.JSONArray;

public class OrderNissinBasket {

	// List<ProductCatalog> items = null;
	HashMap<String, ProductCatalog> items = null;

	public OrderNissinBasket() {
		this.items = new HashMap<String, ProductCatalog>();
	}

	public OrderNissinBasket(List<ProductCatalog> items) {
		this();
		setItems(items);
	}

	public List<ProductCatalog> getItems() {
		List<ProductCatalog> ret_items = new ArrayList<ProductCatalog>();
		ret_items.addAll(this.items.values());
		Collections.sort(ret_items);
		return ret_items;
	}

	public void setItems(List<ProductCatalog> items) {
		for (ProductCatalog item : items) {
			this.items.put(item.getProductId() + item.getUom2(), item);
		}
	}

	/*
	 * return int 0 mean no action 1 mean add/remove or increase/decrease
	 * product qty from the basket
	 */
	public int adjustItem(ProductCatalog item) {
		int ret = 0;

		ProductCatalog basketItem = getBasketItem(item);

		if (basketItem == null) {
			if (item.getQty1() <= 0 && item.getQty2() <=0)
				return ret;

			adjustBasketItem(item);
		} else {
			int x = isQtyChange(basketItem, item);

			if (x == 0)
				return ret;
			else {
				if (item.getQty1() <= 0 && item.getQty2() <=0)
					takeOffBasketItem(item);
				else
					adjustBasketItem(item);

				ret = 1;
			}
		}

		return ret;
	}

	private void adjustBasketItem(ProductCatalog item) {
		// TODO Auto-generated method stub
		this.items.put(item.getProductId()+"", item);
	}

	private void takeOffBasketItem(ProductCatalog item) {
		// TODO Auto-generated method stub
		this.items.remove(item.getProductId()+"");
	}

	public ProductCatalog getBasketItem(ProductCatalog item) {
		return this.items.get(item.getProductId() );
	}

	private int isQtyChange(ProductCatalog basketItem, ProductCatalog item) {
		if (basketItem.getQty1() != item.getQty1()
				|| basketItem.getQty2() != item.getQty2()) {
			return 1;
		}

		return 0;
	}

	public JSONArray getJSON() {
		return JSONArray.fromObject(this.getItems());
	}
	
	public boolean hasItemBrandInBasket(String brandCode){
		for(ProductCatalog item : this.items.values()){
			if(item.getProductCode().indexOf(brandCode) >= 0) 
				return true;
		}
		
		return false;
	}
}
