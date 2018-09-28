package com.isecinc.pens.web.stock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONArray;

public class StockBasket {

	// List<ProductCatalog> items = null;
	HashMap<String, StockProductCatalog> items = null;

	public StockBasket() {
		this.items = new HashMap<String, StockProductCatalog>();
	}

	public StockBasket(List<StockProductCatalog> items) {
		this();
		setItems(items);
	}

	public List<StockProductCatalog> getItems() {
		List<StockProductCatalog> ret_items = new ArrayList<StockProductCatalog>();
		ret_items.addAll(this.items.values());
		Collections.sort(ret_items);
		return ret_items;
	}

	public void setItems(List<StockProductCatalog> items) {
		for (StockProductCatalog item : items) {
			this.items.put(item.getProductId() + item.getUom2(), item);
		}
	}

	/*
	 * return int 0 mean no action 1 mean add/remove or increase/decrease
	 * product qty from the basket
	 */
	public int adjustItem(StockProductCatalog item) {
		int ret = 0;

		StockProductCatalog basketItem = getBasketItem(item);

		if (basketItem == null) {
			if (item.getLineNetAmt() <= 0d)
				return ret;

			adjustBasketItem(item);
		} else {
			int x = isQtyChange(basketItem, item);

			if (x == 0)
				return ret;
			else {
				if (item.getLineNetAmt() <= 0d)
					takeOffBasketItem(item);
				else
					adjustBasketItem(item);

				ret = 1;
			}
		}

		return ret;
	}

	private void adjustBasketItem(StockProductCatalog item) {
		// TODO Auto-generated method stub
		this.items.put(item.getProductId() + item.getUom2(), item);
	}

	private void takeOffBasketItem(StockProductCatalog item) {
		// TODO Auto-generated method stub
		this.items.remove(item.getProductId() + item.getUom2());
	}

	public StockProductCatalog getBasketItem(StockProductCatalog item) {
		return this.items.get(item.getProductId() + item.getUom2());
	}

	private int isQtyChange(StockProductCatalog basketItem, StockProductCatalog item) {
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
		for(StockProductCatalog item : this.items.values()){
			if(item.getProductCode().indexOf(brandCode) >= 0) 
				return true;
		}
		
		return false;
	}
}
