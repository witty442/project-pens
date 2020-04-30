package com.isecinc.pens.web.boxno;

import java.io.Serializable;
import java.util.List;

public class BoxNoBean implements Serializable{
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    /**Report **/
	private String salesChannelNo;
	private String salesZone;
	private String docNo;
	/** properties**/
	private String period;
	private String periodDesc;
	private String pdCode ;
	private String pdCodeKey ;
	private String pdDesc ;
	private String totalBox ;
	private String noBox ;
	private String salesrepCode ;
	private String salesrepName;
	private String salesrepId;
	private String createUser; 
	private String updateUser; 
	private String updateDate; 
	private String createDate; 
	private String printDate;
	private int printCount ;
	private List<BoxNoBean> itemsList;
	private StringBuffer dataStrBuffer;
	private boolean canEdit;
	
	private String reportName;
	
	
	public String getDocNo() {
		return docNo;
	}
	public void setDocNo(String docNo) {
		this.docNo = docNo;
	}
	public String getPdCodeKey() {
		return pdCodeKey;
	}
	public void setPdCodeKey(String pdCodeKey) {
		this.pdCodeKey = pdCodeKey;
	}
	public String getSalesChannelNo() {
		return salesChannelNo;
	}
	public void setSalesChannelNo(String salesChannelNo) {
		this.salesChannelNo = salesChannelNo;
	}
	public String getSalesZone() {
		return salesZone;
	}
	public void setSalesZone(String salesZone) {
		this.salesZone = salesZone;
	}
	public String getReportName() {
		return reportName;
	}
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}
	public boolean isCanEdit() {
		return canEdit;
	}
	public void setCanEdit(boolean canEdit) {
		this.canEdit = canEdit;
	}
	public StringBuffer getDataStrBuffer() {
		return dataStrBuffer;
	}
	public void setDataStrBuffer(StringBuffer dataStrBuffer) {
		this.dataStrBuffer = dataStrBuffer;
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
	public String getPdCode() {
		return pdCode;
	}
	public void setPdCode(String pdCode) {
		this.pdCode = pdCode;
	}
	public String getPdDesc() {
		return pdDesc;
	}
	public void setPdDesc(String pdDesc) {
		this.pdDesc = pdDesc;
	}
	public String getTotalBox() {
		return totalBox;
	}
	public void setTotalBox(String totalBox) {
		this.totalBox = totalBox;
	}
	public String getNoBox() {
		return noBox;
	}
	public void setNoBox(String noBox) {
		this.noBox = noBox;
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
	public String getSalesrepId() {
		return salesrepId;
	}
	public void setSalesrepId(String salesrepId) {
		this.salesrepId = salesrepId;
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
	public String getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getPrintDate() {
		return printDate;
	}
	public void setPrintDate(String printDate) {
		this.printDate = printDate;
	}
	public int getPrintCount() {
		return printCount;
	}
	public void setPrintCount(int printCount) {
		this.printCount = printCount;
	}
	public List<BoxNoBean> getItemsList() {
		return itemsList;
	}
	public void setItemsList(List<BoxNoBean> itemsList) {
		this.itemsList = itemsList;
	}

	
	
}
