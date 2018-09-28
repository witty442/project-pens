package com.isecinc.pens.web.moveorder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.isecinc.pens.bean.User;

public class MoveOrderBean implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = -4104083172800233911L;

	private String periodType;
	private String period;
	private String periodDesc;
	private String startDate;
	private String endDate;
	private String salesChannelNo;
	private String salesChannelName;
	private String salesrepCode;
	private String salesrepName;
	private String requestNumber;
	private String requestDate;
	private String docType;
	private String docStatus;
	private String pdCode;
	private String pdName;
	private String custCatNo;
	private String ctnQty;
	private String pcsQty;
	private String amount;
	private String createUser;
	private String updateUser;
	private String statusDate;
	private String remark;
	private String reason;
	private String realCtnQty;
	private String realPcsQty;
	private int moveDay;
	private String dispHaveReason;
	private String dispCheckMoveDay;
	private List<MoveOrderBean> items;
	
	
	public String getDispCheckMoveDay() {
		return dispCheckMoveDay;
	}
	public void setDispCheckMoveDay(String dispCheckMoveDay) {
		this.dispCheckMoveDay = dispCheckMoveDay;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getRealCtnQty() {
		return realCtnQty;
	}
	public void setRealCtnQty(String realCtnQty) {
		this.realCtnQty = realCtnQty;
	}
	public String getRealPcsQty() {
		return realPcsQty;
	}
	public void setRealPcsQty(String realPcsQty) {
		this.realPcsQty = realPcsQty;
	}
	public String getDispHaveReason() {
		return dispHaveReason;
	}
	public void setDispHaveReason(String disphaveReason) {
		this.dispHaveReason = disphaveReason;
	}
	public int getMoveDay() {
		return moveDay;
	}
	public void setMoveDay(int moveDay) {
		this.moveDay = moveDay;
	}
	public String getPeriodType() {
		return periodType;
	}
	public void setPeriodType(String periodType) {
		this.periodType = periodType;
	}
	public String getPeriod() {
		return period;
	}
	public void setPeriod(String period) {
		this.period = period;
	}
	public String getPeriodDesc() {
		return periodDesc;
	}
	public void setPeriodDesc(String periodDesc) {
		this.periodDesc = periodDesc;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	public String getSalesChannelNo() {
		return salesChannelNo;
	}
	public void setSalesChannelNo(String salesChannelNo) {
		this.salesChannelNo = salesChannelNo;
	}
	public String getSalesChannelName() {
		return salesChannelName;
	}
	public void setSalesChannelName(String salesChannelName) {
		this.salesChannelName = salesChannelName;
	}
	public String getSalesrepCode() {
		return salesrepCode;
	}
	public void setSalesrepCode(String salesrepCode) {
		this.salesrepCode = salesrepCode;
	}
	public String getSalesrepName() {
		return salesrepName;
	}
	public void setSalesrepName(String salesrepName) {
		this.salesrepName = salesrepName;
	}
	public String getRequestNumber() {
		return requestNumber;
	}
	public void setRequestNumber(String requestNumber) {
		this.requestNumber = requestNumber;
	}
	public String getRequestDate() {
		return requestDate;
	}
	public void setRequestDate(String requestDate) {
		this.requestDate = requestDate;
	}
	public String getDocType() {
		return docType;
	}
	public void setDocType(String docType) {
		this.docType = docType;
	}
	public String getDocStatus() {
		return docStatus;
	}
	public void setDocStatus(String docStatus) {
		this.docStatus = docStatus;
	}
	public String getPdCode() {
		return pdCode;
	}
	public void setPdCode(String pdCode) {
		this.pdCode = pdCode;
	}
	public String getPdName() {
		return pdName;
	}
	public void setPdName(String pdName) {
		this.pdName = pdName;
	}
	public String getCustCatNo() {
		return custCatNo;
	}
	public void setCustCatNo(String custCatNo) {
		this.custCatNo = custCatNo;
	}
	public String getCtnQty() {
		return ctnQty;
	}
	public void setCtnQty(String ctnQty) {
		this.ctnQty = ctnQty;
	}
	public String getPcsQty() {
		return pcsQty;
	}
	public void setPcsQty(String pcsQty) {
		this.pcsQty = pcsQty;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public String getUpdateUser() {
		return updateUser;
	}
	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}
	public String getStatusDate() {
		return statusDate;
	}
	public void setStatusDate(String statusDate) {
		this.statusDate = statusDate;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public List<MoveOrderBean> getItems() {
		return items;
	}
	public void setItems(List<MoveOrderBean> items) {
		this.items = items;
	}

		
}
