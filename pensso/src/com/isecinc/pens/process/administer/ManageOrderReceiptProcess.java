package com.isecinc.pens.process.administer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.isecinc.core.model.I_PO;
import com.isecinc.pens.bean.Order;
import com.isecinc.pens.bean.Receipt;
import com.isecinc.pens.bean.User;
import com.isecinc.pens.model.MOrder;
import com.isecinc.pens.model.MReceipt;
import com.isecinc.pens.web.admin.ManageOrderReceiptForm;
import com.pens.util.DateUtil;
import com.pens.util.Utils;

public class ManageOrderReceiptProcess {
	private Logger logger = Logger.getLogger("PENS");
	/**
	 * Get Order
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<Order> getOrders(String documentDate) throws Exception {
		List<Order> orders = new ArrayList<Order>();
		String whereCause = "";
		whereCause = "\n and doc_status IN('"+I_PO.STATUS_UNAVAILABLE+"','"+I_PO.STATUS_RESERVE+"','"+I_PO.STATUS_REJECT+"') and EXPORTED = 'N' ";
		whereCause += "\n and order_date = to_date('" + DateUtil.convBuddhistToChristDate(documentDate, DateUtil.DD_MM_YYYY_WITH_SLASH) + "','dd/mm/yyyy') ";
		
		logger.debug("sql whereCause:"+whereCause);
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
	public List<Receipt> getReceipt(ManageOrderReceiptForm aForm,User user) throws Exception {
		List<Receipt> receipts = new ArrayList<Receipt>();
		String whereCause = "";
		whereCause = "\n  and doc_status = 'SV' and EXPORTED = 'N' ";
		
		whereCause += "\n  and receipt_date >= to_date('"+ DateUtil.convBuddhistToChristDate(aForm.getDocumentDateFrom(), DateUtil.DD_MM_YYYY_WITH_SLASH) + "','dd/mm/yyyy') ";
		whereCause += "\n  and receipt_date <= to_date('"+ DateUtil.convBuddhistToChristDate(aForm.getDocumentDateTo(), DateUtil.DD_MM_YYYY_WITH_SLASH) + "','dd/mm/yyyy') ";
		if( !user.getUserName().equalsIgnoreCase("admin")){
			whereCause += "\n  and user_id = "+user.getId();
		}
		whereCause += "\n and customer_id in( ";
		whereCause += "\n   select customer_id from pensso.m_customer c where 1=1";
		if( !user.getUserName().equalsIgnoreCase("admin")){
			whereCause += "\n  and c.user_id = "+user.getId();
		}
		if( !Utils.isNull(aForm.getCustomerCode()).equals("")){
			whereCause += "\n  and c.code = '"+Utils.isNull(aForm.getCustomerCode())+"'";
		}
		if( !Utils.isNull(aForm.getCustomerName()).equals("")){
			whereCause += "\n  and c.name LIKE '%"+Utils.isNull(aForm.getCustomerName())+"%'";
		}
		whereCause += "\n )";
		
		logger.debug("whereCause :\n"+whereCause);
		
		Receipt[] rs = new MReceipt().search(whereCause);
		if (rs != null) receipts = Arrays.asList(rs);
		return receipts;
	}
}
