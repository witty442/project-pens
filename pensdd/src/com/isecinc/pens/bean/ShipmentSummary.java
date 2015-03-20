package com.isecinc.pens.bean;

import java.util.ArrayList;
import java.util.List;

public class ShipmentSummary {

		List<OrderTransaction> successL;
		List<OrderTransaction> failL;
		List<String> errorMsg;
		List<OrderTransaction> shipDateL;
		List<OrderTransaction> rescheduleShipDateL;
		
		int no_of_confirm = 0;
		int no_of_updated = 0;
		int no_of_updatedReqDate = 0;
		int no_of_reScheduleRecord = 0;
		
		public ShipmentSummary() {
			successL = new ArrayList<OrderTransaction>();
			failL = new ArrayList<OrderTransaction>();
			errorMsg = new ArrayList<String>();
			shipDateL = new ArrayList<OrderTransaction>();
			rescheduleShipDateL = new ArrayList<OrderTransaction>();
		}

		public void addShipDate(OrderTransaction m ) {
			shipDateL.add(m);
		}
		public void addRescheduleShipDate(OrderTransaction m ) {
			rescheduleShipDateL.add(m);
		}
		public void addSucess(OrderTransaction m ) {
			successL.add(m);
		}
		public void addFailOrder(OrderTransaction m, String errMsg) {
			failL.add(m);
			errorMsg.add(errMsg);
		}
		public List<OrderTransaction> getShipDateList() {
			return this.shipDateL;
		}
		public List<OrderTransaction> getRescheduleShipDateList() {
			return this.rescheduleShipDateL;
		}
		public List<OrderTransaction> getSuccessList() {
			return this.successL;
		}

		public List<OrderTransaction> getFailOrderList() {
			return this.failL;
		}

		public int getShipDateSize() {
			return this.shipDateL.size();
		}
		public int getRescheduleShipDateSize() {
			return this.rescheduleShipDateL.size();
		}
		public int getSuccessSize() {
			return this.successL.size();
		}

		public int getFailSize() {
			return this.failL.size();
		}

		public String getErrorMessage(int idx) {
			return errorMsg.get(idx);
		}

		public int getNo_of_confirm() {
			return no_of_confirm;
		}

		public void setNo_of_confirm(int no_of_confirm) {
			this.no_of_confirm = no_of_confirm;
		}

		public int getNo_of_updated() {
			return no_of_updated;
		}

		public void setNo_of_updated(int no_of_updated) {
			this.no_of_updated = no_of_updated;
		}

		public int getNo_of_updatedReqDate() {
			return no_of_updatedReqDate;
		}

		public void setNo_of_updatedReqDate(int no_of_updatedReqDate) {
			this.no_of_updatedReqDate = no_of_updatedReqDate;
		}

		public int getNo_of_reScheduleRecord() {
			return no_of_reScheduleRecord;
		}

		public void setNo_of_reScheduleRecord(int no_of_reScheduleRecord) {
			this.no_of_reScheduleRecord = no_of_reScheduleRecord;
		}
	
		

}
