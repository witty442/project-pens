package com.isecinc.pens.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.isecinc.core.bean.References;
import com.isecinc.core.model.I_PO;
import com.isecinc.pens.inf.helper.Utils;
import com.isecinc.pens.init.InitialReferences;
import com.isecinc.pens.model.MCustomer;
import com.isecinc.pens.model.MUser;
import com.isecinc.pens.model.MOrgRule;
import com.pens.util.ConvertNullUtil;
import com.pens.util.DateToolsUtil;
import com.pens.util.DateUtil;
import com.pens.util.NumberUtil;
import com.sun.org.apache.bcel.internal.generic.GETSTATIC;

/**
 * Order
 * 
 * @author atiz.b
 * @version $Id: Order.java,v 1.0 14/10/2010 00:00:00 atiz.b Exp $
 * 
 */
public class OrderNissin extends I_PO implements Serializable {

	private static final long serialVersionUID = -136106985704627134L;

	/**
	 * Default Constructor
	 */
	public OrderNissin() {}

	private String id;
	private long invoiceId;
	private String orderNo;
	private String orderDate;
	private String orderTime;
	private String orderType;
	private long customerId;
	private String addressSummary;
	private String interfaces;
	private User user = new User();
	private String docStatus;
	private String pendingStatus;
	private String orderDateFrom;
	private String orderDateTo;
	private String docStatusLabel;
	private String created;
	private String pricelistLabel;
	private String exported;
	private String remark;
	private String reason;
	private List<OrderNissin> itemsList;
	private List<OrderNissinLine> linesList;
	private boolean canEdit;
	private CustomerNissin customerNis = new CustomerNissin();
	private String salesrepCode;
	private List<SalesrepBean> salesrepCodeList = new ArrayList<SalesrepBean>();
	private String salesrepId;
	private String oraCustomerId;
	private String oraCustomerCode;
	private String oraCustomerName;
	private String invoiceNo;
	private String invoiceDate;
	private String priceListId;

	private String nisCreateDate;
	private String nisCreateUser;
	private String nisUpdateDate;
	private String nisUpdateUser;
	private String pensActionDate;
	private String pensActionUser;
	private String salesActionDate;
	private String salesActionUser;
	private String completeActionDate;
	private String completeActionUser;
	private boolean readonlyText;
	private String readonlyStyle;
	private boolean readonlyOrderNoText;
	private String readonlyOrderNoStyle;
    private String pensNote;
	
    
	public String getPensNote() {
		return pensNote;
	}
	public void setPensNote(String pensNote) {
		this.pensNote = pensNote;
	}
	public String getCompleteActionDate() {
		return completeActionDate;
	}
	public void setCompleteActionDate(String completeActionDate) {
		this.completeActionDate = completeActionDate;
	}
	public boolean getReadonlyText() {
		return readonlyText;
	}
	public void setReadonlyText(boolean readonlyText) {
		this.readonlyText = readonlyText;
	}
	public String getReadonlyStyle() {
		return readonlyStyle;
	}
	public void setReadonlyStyle(String readonlyStyle) {
		this.readonlyStyle = readonlyStyle;
	}
	public boolean getReadonlyOrderNoText() {
		return readonlyOrderNoText;
	}
	public void setReadonlyOrderNoText(boolean readonlyOrderNoText) {
		this.readonlyOrderNoText = readonlyOrderNoText;
	}
	public String getReadonlyOrderNoStyle() {
		return readonlyOrderNoStyle;
	}
	public void setReadonlyOrderNoStyle(String readonlyOrderNoStyle) {
		this.readonlyOrderNoStyle = readonlyOrderNoStyle;
	}
	public List<SalesrepBean> getSalesrepCodeList() {
		return salesrepCodeList;
	}
	public void setSalesrepCodeList(List<SalesrepBean> salesrepCodeList) {
		this.salesrepCodeList = salesrepCodeList;
	}
	public String getNisCreateDate() {
		return nisCreateDate;
	}
	public void setNisCreateDate(String nisCreateDate) {
		this.nisCreateDate = nisCreateDate;
	}
	public String getNisCreateUser() {
		return nisCreateUser;
	}
	public void setNisCreateUser(String nisCreateUser) {
		this.nisCreateUser = nisCreateUser;
	}
	public String getNisUpdateDate() {
		return nisUpdateDate;
	}
	public void setNisUpdateDate(String nisUpdateDate) {
		this.nisUpdateDate = nisUpdateDate;
	}
	public String getNisUpdateUser() {
		return nisUpdateUser;
	}
	public void setNisUpdateUser(String nisUpdateUser) {
		this.nisUpdateUser = nisUpdateUser;
	}
	public String getPensActionDate() {
		return pensActionDate;
	}
	public void setPensActionDate(String pensActionDate) {
		this.pensActionDate = pensActionDate;
	}
	public String getPensActionUser() {
		return pensActionUser;
	}
	public void setPensActionUser(String pensActionUser) {
		this.pensActionUser = pensActionUser;
	}
	public String getSalesActionDate() {
		return salesActionDate;
	}
	public void setSalesActionDate(String salesActionDate) {
		this.salesActionDate = salesActionDate;
	}
	public String getSalesActionUser() {
		return salesActionUser;
	}
	public void setSalesActionUser(String salesActionUser) {
		this.salesActionUser = salesActionUser;
	}
	
	public String getCompleteActionUser() {
		return completeActionUser;
	}
	public void setCompleteActionUser(String completeActionUser) {
		this.completeActionUser = completeActionUser;
	}
	public String getPendingStatus() {
		return pendingStatus;
	}
	public void setPendingStatus(String pendingStatus) {
		this.pendingStatus = pendingStatus;
	}
	public String getPriceListId() {
		return priceListId;
	}
	public void setPriceListId(String priceListId) {
		this.priceListId = priceListId;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public long getInvoiceId() {
		return invoiceId;
	}
	public void setInvoiceId(long invoiceId) {
		this.invoiceId = invoiceId;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public String getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
	}
	public String getOrderTime() {
		return orderTime;
	}
	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	public long getCustomerId() {
		return customerId;
	}
	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}
	
	public String getAddressSummary() {
		return addressSummary;
	}
	public void setAddressSummary(String addressSummary) {
		this.addressSummary = addressSummary;
	}
	public String getInterfaces() {
		return interfaces;
	}
	public void setInterfaces(String interfaces) {
		this.interfaces = interfaces;
	}
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getDocStatus() {
		return docStatus;
	}
	public void setDocStatus(String docStatus) {
		this.docStatus = docStatus;
	}
	public String getOrderDateFrom() {
		return orderDateFrom;
	}
	public void setOrderDateFrom(String orderDateFrom) {
		this.orderDateFrom = orderDateFrom;
	}
	public String getOrderDateTo() {
		return orderDateTo;
	}
	public void setOrderDateTo(String orderDateTo) {
		this.orderDateTo = orderDateTo;
	}
	public String getDocStatusLabel() {
		return docStatusLabel;
	}
	public void setDocStatusLabel(String docStatusLabel) {
		this.docStatusLabel = docStatusLabel;
	}
	public String getCreated() {
		return created;
	}
	public void setCreated(String created) {
		this.created = created;
	}
	public String getPricelistLabel() {
		return pricelistLabel;
	}
	public void setPricelistLabel(String pricelistLabel) {
		this.pricelistLabel = pricelistLabel;
	}
	public String getExported() {
		return exported;
	}
	public void setExported(String exported) {
		this.exported = exported;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	
	public List<OrderNissin> getItemsList() {
		return itemsList;
	}
	public void setItemsList(List<OrderNissin> itemsList) {
		this.itemsList = itemsList;
	}
	public List<OrderNissinLine> getLinesList() {
		return linesList;
	}
	public void setLinesList(List<OrderNissinLine> linesList) {
		this.linesList = linesList;
	}
	public boolean isCanEdit() {
		return canEdit;
	}
	public void setCanEdit(boolean canEdit) {
		this.canEdit = canEdit;
	}
	public CustomerNissin getCustomerNis() {
		return customerNis;
	}
	public void setCustomerNis(CustomerNissin customerNis) {
		this.customerNis = customerNis;
	}
	public String getSalesrepCode() {
		return salesrepCode;
	}
	public void setSalesrepCode(String salesrepCode) {
		this.salesrepCode = salesrepCode;
	}
	public String getSalesrepId() {
		return salesrepId;
	}
	public void setSalesrepId(String salesrepId) {
		this.salesrepId = salesrepId;
	}
	public String getOraCustomerId() {
		return oraCustomerId;
	}
	public void setOraCustomerId(String oraCustomerId) {
		this.oraCustomerId = oraCustomerId;
	}
	public String getOraCustomerCode() {
		return oraCustomerCode;
	}
	public void setOraCustomerCode(String oraCustomerCode) {
		this.oraCustomerCode = oraCustomerCode;
	}
	public String getOraCustomerName() {
		return oraCustomerName;
	}
	public void setOraCustomerName(String oraCustomerName) {
		this.oraCustomerName = oraCustomerName;
	}
	public String getInvoiceNo() {
		return invoiceNo;
	}
	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}
	public String getInvoiceDate() {
		return invoiceDate;
	}
	public void setInvoiceDate(String invoiceDate) {
		this.invoiceDate = invoiceDate;
	}
	
	

}
