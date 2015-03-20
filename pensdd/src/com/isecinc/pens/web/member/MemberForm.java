package com.isecinc.pens.web.member;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.Factory;
import org.apache.commons.collections.list.LazyList;

import com.isecinc.core.web.I_Form;
import com.isecinc.pens.bean.Address;
import com.isecinc.pens.bean.Contact;
import com.isecinc.pens.bean.Member;
import com.isecinc.pens.bean.MemberProduct;

/**
 * Member Form
 * 
 * @author Aneak.t
 * @version $Id: MemberForm.java,v 1.0 11/10/2010 00:00:00 aneak.t Exp $
 * 
 */

public class MemberForm extends I_Form{

	private static final long serialVersionUID = 4404731038669305057L;
	
	private MemberCriteria criteria = new MemberCriteria();
	
	private List<Member> results = null;
	
	List<Address> addresses = null;

	List<Contact> contacts = null;
	
	List<MemberProduct> memberProducts = null;

	@SuppressWarnings("unchecked")
	public MemberForm() {
		Factory factory = new Factory() {
			public Object create() {
				return new Address();
			}
		};
		addresses = LazyList.decorate(new ArrayList<Address>(), factory);

		Factory factory2 = new Factory() {
			public Object create() {
				return new Contact();
			}
		};
		contacts = LazyList.decorate(new ArrayList<Contact>(), factory2);
		
		Factory factory3 = new Factory() {
			public Object create() {
				return new MemberProduct();
			}
		};
		memberProducts = LazyList.decorate(new ArrayList<MemberProduct>(), factory3);
	}
	
	
	public List<MemberProduct> getMemberProducts() {
		return memberProducts;
	}


	public void setMemberProducts(List<MemberProduct> memberProducts) {
		this.memberProducts = memberProducts;
	}


	public MemberCriteria getCriteria() {
		return criteria;
	}

	public void setCriteria(MemberCriteria criteria) {
		this.criteria = criteria;
	}
	public List<Member> getResults() {
		return results;
	}
	
	public void setResults(List<Member> results) {
		this.results = results;
	}


	public Member getMember() {
		return criteria.getMember();
	}

	public void setMember(Member member) {
		criteria.setMember(member);
	}

	public List<Address> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<Address> addresses) {
		this.addresses = addresses;
	}

	public List<Contact> getContacts() {
		return contacts;
	}

	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}
	

}
