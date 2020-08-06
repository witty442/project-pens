package com.isecinc.pens.web.batchtask;

import java.io.Serializable;

public class BatchTaskDispBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3744574017784744770L;
	
	private boolean dispDetail;//Display Result 
	private boolean dispRecordFailHead;
	private boolean dispRecordFailDetail;
	private boolean dispRecordSuccessHead;
	private boolean dispRecordSuccessDetail;
	public boolean isDispDetail() {
		return dispDetail;
	}
	public void setDispDetail(boolean dispDetail) {
		this.dispDetail = dispDetail;
	}
	public boolean isDispRecordFailHead() {
		return dispRecordFailHead;
	}
	public void setDispRecordFailHead(boolean dispRecordFailHead) {
		this.dispRecordFailHead = dispRecordFailHead;
	}
	public boolean isDispRecordFailDetail() {
		return dispRecordFailDetail;
	}
	public void setDispRecordFailDetail(boolean dispRecordFailDetail) {
		this.dispRecordFailDetail = dispRecordFailDetail;
	}
	public boolean isDispRecordSuccessHead() {
		return dispRecordSuccessHead;
	}
	public void setDispRecordSuccessHead(boolean dispRecordSuccessHead) {
		this.dispRecordSuccessHead = dispRecordSuccessHead;
	}
	public boolean isDispRecordSuccessDetail() {
		return dispRecordSuccessDetail;
	}
	public void setDispRecordSuccessDetail(boolean dispRecordSuccessDetail) {
		this.dispRecordSuccessDetail = dispRecordSuccessDetail;
	}
	
	
}
