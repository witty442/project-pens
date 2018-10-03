package com.isecinc.pens.web.reqPromotion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import net.sf.json.JSONArray;

public class RequestPromotionBasket {

	// List<ProductCatalog> items = null;
	HashMap<String, RequestPromotionProductCatalog> items = null;

	public RequestPromotionBasket() {
		this.items = new HashMap<String, RequestPromotionProductCatalog>();
	}

	public RequestPromotionBasket(List<RequestPromotionProductCatalog> items) {
		this();
		setItems(items);
	}

	public List<RequestPromotionProductCatalog> getItems() {
		List<RequestPromotionProductCatalog> ret_items = new ArrayList<RequestPromotionProductCatalog>();
		ret_items.addAll(this.items.values());
		Collections.sort(ret_items);
		return ret_items;
	}

	public void setItems(List<RequestPromotionProductCatalog> items) {
		for (RequestPromotionProductCatalog item : items) {
			this.items.put(item.getProductId() + item.getUom2(), item);
		}
	}

	/*
	 * return int 0 mean no action 1 mean add/remove or increase/decrease
	 * product qty from the basket
	 */
	public int adjustItem(RequestPromotionProductCatalog item) {
		int ret = 0;

		RequestPromotionProductCatalog basketItem = getBasketItem(item);

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

	private void adjustBasketItem(RequestPromotionProductCatalog item) {
		// TODO Auto-generated method stub
		this.items.put(item.getProductId() + item.getUom2(), item);
	}

	private void takeOffBasketItem(RequestPromotionProductCatalog item) {
		// TODO Auto-generated method stub
		this.items.remove(item.getProductId() + item.getUom2());
	}

	public RequestPromotionProductCatalog getBasketItem(RequestPromotionProductCatalog item) {
		return this.items.get(item.getProductId() + item.getUom2());
	}

	private int isQtyChange(RequestPromotionProductCatalog basketItem, RequestPromotionProductCatalog item) {
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
		for(RequestPromotionProductCatalog item : this.items.values()){
			if(item.getProductCode().indexOf(brandCode) >= 0) 
				return true;
		}
		
		return false;
	}
}
