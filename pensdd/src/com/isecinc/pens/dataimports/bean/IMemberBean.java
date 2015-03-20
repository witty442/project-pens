package com.isecinc.pens.dataimports.bean;

import java.util.ArrayList;
import java.util.List;

import com.isecinc.pens.bean.Member;
import com.isecinc.pens.dataimports.process.MemberImportProcess;

public class IMemberBean extends Member {

	private static final long serialVersionUID = 3896273002370242609L;

	private ICustomerBean customer;
	private IAddressBean address;
	private IContactBean contact;
	private ArrayList<IMemberProductBean> productList;
	private String errorDesc;

	public ICustomerBean getCustomer() {
		return customer;
	}
	public void setCustomer(ICustomerBean customer) {
		this.customer = customer;
	}
	public IAddressBean getAddress() {
		return address;
	}
	public void setAddress(IAddressBean address) {
		this.address = address;
	}
	public IContactBean getContact() {
		return contact;
	}
	public void setContact(IContactBean contact) {
		this.contact = contact;
	}
	public ArrayList<IMemberProductBean> getProductList() {
		return productList;
	}
	public void setProductList(ArrayList<IMemberProductBean> productList) {
		this.productList = productList;
	}
	public String getErrorDesc() {
		return errorDesc;
	}
	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}

	// start paginator
	public static final String GET_PAGING_MEMBER_LIST = "getPagingMemberList";
	public static final String GET_PAGING_MEMBER_COUNT = "getPagingMemberCount";

	private static List<IMemberBean> memberList;
	private static Integer memberCount;

	public static List<IMemberBean> getMemberList() {
		return memberList;
	}

	public static void setMemberList(List<IMemberBean> memberList) {
		IMemberBean.memberList = memberList;
	}

	public static Integer getMemberCount() {
		return memberCount;
	}

	public static void setMemberCount(Integer memberCount) {
		IMemberBean.memberCount = memberCount;
	}

	public static List getPagingMemberList(IMemberBean dto, int skip, int max) {
		return getMemberList();
	}

	public static Integer getPagingMemberCount(IMemberBean dto) {
		return getMemberCount();
	}
	// end paginator
}
