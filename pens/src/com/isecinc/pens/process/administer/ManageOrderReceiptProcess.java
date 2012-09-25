package com.isecinc.pens.process.administer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import util.DateToolsUtil;

import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.Receipt;
import com.isecinc.pens.model.MOrder;
import com.isecinc.pens.model.MReceipt;

public class ManageOrderReceiptProcess {

	/**
	 * Get Order
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<Order> getOrders(String documentDate) throws Exception {
		List<Order> orders = new ArrayList<Order>();
		String whereCause = "";
		whereCause = " and doc_status = 'SV' and EXPORTED = 'N' ";
		whereCause += "  and order_date = '" + DateToolsUtil.convertToTimeStamp(documentDate.trim()) + "' ";
		Order[] rs = new MOrder().search(whereCause);
		if (rs != null) orders = Arrays.asList(rs);
		return orders;
	}

	/**
	 * Get Receipt
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<Receipt> getReceipt(String documentDate) throws Exception {
		List<Receipt> receipts = new ArrayList<Receipt>();
		String whereCause = "";
		whereCause = "  and doc_status = 'SV' and EXPORTED = 'N' ";
		whereCause += "  and receipt_date = '" + DateToolsUtil.convertToTimeStamp(documentDate.trim()) + "' ";
		Receipt[] rs = new MReceipt().search(whereCause);
		if (rs != null) receipts = Arrays.asList(rs);
		return receipts;
	}
}
