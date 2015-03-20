package com.isecinc.pens.bean;

import java.util.ArrayList;
import java.util.List;

public class ReceiptGenerateSummary {

		List<Receipt> successRptL;
		List<Receipt> failCancelRptL;
		List<Order> failOrderL;
		List<String> errorMsg;

		public ReceiptGenerateSummary() {
			successRptL = new ArrayList<Receipt>();
			failCancelRptL = new ArrayList<Receipt>();
			failOrderL = new ArrayList<Order>();
			errorMsg = new ArrayList<String>();
		}

		public void addSucessReceipt(Receipt receipt) {
			successRptL.add(receipt);
		}

		public void addFailOrder(Order order, String errMsg) {
			failOrderL.add(order);
			errorMsg.add(errMsg);
		}
		public void addFailCancelReceipt(Receipt receipt, String errMsg) {
			failCancelRptL.add(receipt);
			errorMsg.add(errMsg);
		}

		public List<Receipt> getSuccessReceiptList() {
			return this.successRptL;
		}
		
		public List<Receipt> getFailCancelReceiptList() {
			return this.failCancelRptL;
		}

		public List<Order> getFailOrderList() {
			return this.failOrderL;
		}

		public int getReceiptSize() {
			return this.successRptL.size();
		}
		
		public int getFailCancelReceiptSize() {
			return this.failCancelRptL.size();
		}
		
		public int getOrderSize() {
			return this.failOrderL.size();
		}

		public String getErrorMessage(int idx) {
			return errorMsg.get(idx);
		}
	

}
