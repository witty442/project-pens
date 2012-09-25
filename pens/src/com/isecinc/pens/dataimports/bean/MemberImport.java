package com.isecinc.pens.dataimports.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.isecinc.pens.bean.Member;
import com.isecinc.pens.bean.Order;

/**
 * Member Import Bean
 * 
 * @author atiz.b
 * @version $Id: MemberImport.java,v 1.0 17/01/2011 00:00:00 atiz.b Exp $
 * 
 */
public class MemberImport implements Serializable {

	private static final long serialVersionUID = -6570070241003051692L;

	public MemberImport() {

	}

	public MemberImport(Member member) {
		this.member = member;
	}

	private Member member = new Member();

	private List<Order> orderId = new ArrayList<Order>();

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public List<Order> getOrderId() {
		return orderId;
	}

	public void setOrderId(List<Order> orderId) {
		this.orderId = orderId;
	}

}
